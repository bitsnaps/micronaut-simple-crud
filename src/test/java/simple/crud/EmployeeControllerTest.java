package simple.crud;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class EmployeeControllerTest {

    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeClass
    public static void setupServer(){
        server = ApplicationContext.build().run(EmbeddedServer.class);
        client = server.getApplicationContext().createBean(HttpClient.class, server.getURL());
    }

    @AfterClass
    public static void stopServer(){
        if (server != null){
            server.stop();
        }
        if (client != null){
            client.stop();
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void supplyAnInvalidOrderTriggersValidationFailure() {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(hasProperty("response", hasProperty("status", is(HttpStatus.BAD_REQUEST))));
        client.toBlocking().exchange(HttpRequest.GET("/employee/list"));
    }

    @Test
    public void testFindNonExistingGenreReturns404() {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(hasProperty("response", hasProperty("status", is(HttpStatus.NOT_FOUND))));
        HttpResponse response = client.toBlocking().exchange(HttpRequest.GET("/employee/99"));
    }

    @Test
    public void testEmployeeCrudOperations() {

        List<Long> employeeIds = new ArrayList<>();

        Employee employee = new Employee("Said");

        // create
        HttpRequest request = HttpRequest.POST("/employee", employee);
        HttpResponse response = client.toBlocking().exchange(request, Employee.class);
//        response is JSON Object

        employeeIds.add( ((Employee) response.body()).getId());
        Long id = employeeIds.get(0);

        assertEquals(HttpStatus.CREATED, response.getStatus());

        assertEquals(employeeIds.size(), 1);

        // update
        Employee employee1 = new Employee("Karim");
        employee1.setId(id);
        request = HttpRequest.PUT("/employee", employee1);
        response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        // select
        request = HttpRequest.GET("/employee/"+id);
        employee1 = client.toBlocking().retrieve(request, Employee.class);

        assertEquals("Karim", employee1.getName());

        // list
        request = HttpRequest.GET("/employee");
        List<Employee> employees = client.toBlocking().retrieve(request, Argument.of(List.class, Employee.class));

        assertEquals(1, employees.size());

        // delete
        for (Long employeeId : employeeIds) {
            request = HttpRequest.DELETE("/employee/"+employeeId);
            response = client.toBlocking().exchange(request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }

        request = HttpRequest.GET("/employee");
        employees = client.toBlocking().retrieve(request, Argument.of(List.class, Employee.class));
        assertEquals(0, employees.size());

    }

}

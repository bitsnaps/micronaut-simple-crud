package simple.crud;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller("/employee")
public class EmployeeController {
    /*

    list:
        curl localhost:8080/employee

    findOne:
        curl localhost:8080/employee/1

    create:
        curl -d '{"name":"Ali"}' -H "Content-Type: application/json" localhost:8080/employee
        curl -X POST -d '{"name":"Amine"}' -H "Content-Type: application/json" localhost:8080/employee

    update:
        curl -X PUT -d '{"id":1, "name":"Kamel"}' -H "Content-Type: application/json" localhost:8080/employee

    delete:
        curl -X DELETE localhost:8080/employee/1

     */

    protected final EmployeeService employeeService;

    // constructor injection
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/{id}")
    public Employee show(Long id) {
        return employeeService.findById(id).orElse(null);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Get("/")
    public List<Employee> list() {
        return employeeService.findAll();
    }

    @Put("/")
    public HttpResponse update(@Body @Valid Employee employee) {
        int nbrOfUpdated = employeeService.update(employee.getId(), employee.getName());
        return HttpResponse
                .noContent()
                .header(HttpHeaders.LOCATION, toUri(employee).getPath());
    }

    @Post("/")
    public HttpResponse<Employee> save(@Body @Valid Employee employee){
        employeeService.save(employee);
        return HttpResponse
                .created(employee)
                .headers(headers -> headers.location(toUri(employee)));
    }

    @Delete("/{id}")
    public HttpResponse delete( Long id) {
        employeeService.deleteById(id);
        return HttpResponse.noContent();
    }

    private URI toUri(Employee employee) {
        return URI.create("/employee/"+employee.getId());
    }


}
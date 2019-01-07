package simple.crud;

import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.spring.tx.annotation.Transactional;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


@Singleton
public class EmployeeServiceImpl implements EmployeeService {

    @PersistenceContext
    private EntityManager entityManager;
    private final ApplicationConfiguration applicationConfiguration;

    public EmployeeServiceImpl(@CurrentSession EntityManager entityManager,
                               ApplicationConfiguration applicationConfiguration){
        this.entityManager = entityManager;
        this.applicationConfiguration = applicationConfiguration;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findById(@NotNull Long id) {
        return Optional.ofNullable(entityManager.find(Employee.class, id));
    }

    @Override
    @Transactional
    public Employee save(@NotNull Employee employee) {
        //Employee employee = new Employee(name);
        entityManager.persist(employee);
        return employee;
    }

    @Override
    @Transactional
    public void deleteById(@NotNull Long id) {
        findById(id).ifPresent(employee -> entityManager.remove(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        String qlString = "SELECT e FROM Employee as e";
        TypedQuery<Employee> query = entityManager.createQuery(qlString, Employee.class);
        query.setMaxResults(50);
        return query.getResultList();
    }

    @Override
    @Transactional
    public int update(@NotNull Long id, @NotBlank String name) {
        return entityManager.createQuery("UPDATE Employee e SET name = :name where id = :id")
                .setParameter("name", name)
                .setParameter("id", id)
                .executeUpdate();
    }
}
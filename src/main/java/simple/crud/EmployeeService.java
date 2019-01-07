package simple.crud;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

interface EmployeeService {

    Optional<Employee> findById(@NotNull Long id);

    Employee save(@NotNull Employee employee);

    void deleteById(Long id);

    List<Employee> findAll();

    int update(@NotNull Long id, @NotBlank String name);


}

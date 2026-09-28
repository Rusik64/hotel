package com.example.hotel.repository;

import com.example.hotel.repository.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e ORDER BY e.id")
    List<Employee> findAllEmployees();

    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Optional<Employee> findEmployeeById(@Param("id") Long id);

    @Query("SELECT e FROM Employee e WHERE e.username = :username")
    Optional<Employee> findEmployeeByUsername(@Param("username") String username);

    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.username = :username")
    boolean existsEmployeeByUsername(@Param("username") String username);

    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.id = :id")
    boolean existsEmployeeById(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO employees (username, password, first_name, last_name, middle_name, role, active, created_at, updated_at) " +
            "VALUES (:username, :password, :firstName, :lastName, :middleName, :role, :active, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            nativeQuery = true)
    void insertEmployee(
            @Param("username") String username,
            @Param("password") String password,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("role") String role,
            @Param("active") Boolean active
    );

    @Transactional
    @Modifying
    @Query(value = "UPDATE employees SET " +
            "username = :username, " +
            "password = :password, " +
            "first_name = :firstName, " +
            "last_name = :lastName, " +
            "middle_name = :middleName, " +
            "role = :role, " +
            "active = :active, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = :id",
            nativeQuery = true)
    void updateEmployee(
            @Param("id") Long id,
            @Param("username") String username,
            @Param("password") String password,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("role") String role,
            @Param("active") Boolean active
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM Employee e WHERE e.id = :id")
    void deleteEmployeeById(@Param("id") Long id);
}
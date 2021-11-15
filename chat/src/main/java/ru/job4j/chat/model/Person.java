package ru.job4j.chat.model;

import ru.job4j.chat.controller.Operation;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 1, message = "Id must be more than 0", groups = {Operation.OnUpdate.class})
    private int id;

    @NotBlank(message = "Login must be not blank", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    @Size(min = 3, message = "Login must be at least 3 characters", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    private String login;

    @NotBlank(message = "Password must be not blank", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    @Size(min = 6, message = "Password must be at least 6 characters", groups = {Operation.OnCreate.class, Operation.OnUpdate.class})
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

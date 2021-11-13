package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Role;
import ru.job4j.chat.repository.PersonRepository;
import ru.job4j.chat.repository.RoleRepository;
import ru.job4j.chat.service.ObjectPatcher;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    public PersonController(PersonRepository personRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ObjectMapper objectMapper) {
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        return new ResponseEntity<>(
                StreamSupport.stream(personRepository.findAll().spliterator(), false).collect(Collectors.toList()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = personRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found. Please check the data")
        );
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        if (person.getLogin().length() < 2) {
            throw new IllegalArgumentException("Invalid login. Login length must be more than 1 character");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 character");
        }
        Role role = roleRepository.findByRole("ROLE_USER");
        person.setRole(role);
        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        return new ResponseEntity<>(
                personRepository.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        if (personRepository.findById(person.getId()).isEmpty()) {
            throw new IllegalArgumentException("Person with this id is not found");
        }
        if (person.getLogin().length() < 2) {
            throw new IllegalArgumentException("Invalid login. Login length must be more than 1 character");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 character");
        }
        personRepository.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        personRepository.delete(person);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Person> patch(@RequestBody Person person) throws InvocationTargetException, IllegalAccessException {
        Person patchablePerson = personRepository.findById(person.getId()).orElseThrow(
                () -> new IllegalArgumentException("Person with this id is not found")
        );
        ObjectPatcher.patch(patchablePerson, person);
        return new ResponseEntity<>(
                personRepository.save(patchablePerson),
                HttpStatus.OK
        );
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
    }
}

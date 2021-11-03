package ru.job4.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4.chat.model.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {
}

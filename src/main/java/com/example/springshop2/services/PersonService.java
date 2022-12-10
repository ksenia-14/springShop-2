package com.example.springshop2.services;

import com.example.springshop2.models.Person;
import com.example.springshop2.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Person findByLogin(String personLogin) {
        Optional<Person> person_db = personRepository.findByLogin(personLogin);
        return person_db.orElse(null);
    }

    public Person findById(int id) {
        Optional<Person> person_db = personRepository.findById(id);
        return person_db.orElse(null);
    }

    //Данный метод позволяет удалить пользователя по id
    @Transactional
    public void deleteById(int id) {
        personRepository.deleteById(id);
    }

    @Transactional
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }

    //Данный метод позволяет обновить данные
    @Transactional
    public void updatePerson(int id, Person person) {
        person.setId(id);
        personRepository.save(person);
    }
}

package com.example.springshop2.repositories;

import com.example.springshop2.models.Order;
import com.example.springshop2.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPerson(Person person);
    List<Order> findOrderByNumber(String number);
}

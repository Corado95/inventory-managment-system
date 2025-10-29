package com.example.inventory.repository;

import com.example.inventory.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {


    List<Item> findByNameContainingIgnoreCase(String name);

    List<Item> findByCategoryContainingIgnoreCase(String category);

}


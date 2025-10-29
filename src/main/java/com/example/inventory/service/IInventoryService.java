package com.example.inventory.service;

import com.example.inventory.model.Item;
import java.util.List;
import java.util.Optional;

public interface IInventoryService {
    List<Item> getAllItems();
    Optional<Item> getItemById(Long id);
    Item addItem(Item item);
    Item updateItem(Long id, Item updatedItem);
    boolean deleteItem(Long id);
    Item increaseStock(Long id, int amount);
    Item decreaseStock(Long id, int amount);
    List<Item> searchItems(String name, String category);
}

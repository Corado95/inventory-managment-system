package com.example.inventory.service;

import com.example.inventory.model.Item;
import java.util.List;
import java.util.Optional;

public interface IInventoryService {
    List<Item> getAllItems();
    Optional<Item> getItemById(Long id);
    Item addItem(Item item);
    Item updateItem(Long id, Item updatedItem);
    void deleteItem(Long id);
    Item increaseStock(Long id);
    Item decreaseStock(Long id);
    List<Item> searchItems(String name, String category);
    List<Item> getLowStockItems(int threshold);
    void checkAndSendLowStock(Item item);
}

package com.example.inventory.service;

import com.example.inventory.model.Item;
import com.example.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService implements IInventoryService  {

    private final ItemRepository itemRepository;
    private final EmailService emailService; //
    private final int LOW_STOCK_THRESHOLD = 5;

    public InventoryService(ItemRepository itemRepository, EmailService emailService) {
        this.itemRepository = itemRepository;
        this.emailService = emailService;
    }


    public void checkAndSendLowStock(Item item) {
        if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
            String subject = "Low Stock Alert: " + item.getName();
            String text = "Item '" + item.getName() + "' is low on stock.\n" +
                    "Current quantity: " + item.getQuantity();
            emailService.sendSimpleMessage("coradolauricella@gmail.com", subject, text);
        }
    }


    @Override
    public List<Item> getAllItems() {
        return List.of();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return Optional.empty();
    }

    @Override
    public Item addItem(Item item) {
        return null;
    }

    @Override
    public Item updateItem(Long id, Item updatedItem) {
        return null;
    }

    @Override
    public void deleteItem(Long id) {

    }

    @Override
    public Item increaseStock(Long id) {
        return null;
    }

    @Override
    public Item decreaseStock(Long id) {
        return null;
    }

    @Override
    public List<Item> searchItems(String name, String category) {
        return List.of();
    }

    public List<Item> getLowStockItems(int threshold) {
        return itemRepository.findAll().stream()
                .filter(i -> i.getQuantity() < threshold)
                .toList();
    }
}

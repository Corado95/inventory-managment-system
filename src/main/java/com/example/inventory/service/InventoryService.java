package com.example.inventory.service;

import com.example.inventory.model.Item;
import com.example.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class InventoryService implements IInventoryService {

    private final ItemRepository itemRepository;
    private final EmailService emailService;
    private final int LOW_STOCK_THRESHOLD = 5;

    public InventoryService(ItemRepository itemRepository, EmailService emailService) {
        this.itemRepository = itemRepository;
        this.emailService = emailService;
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Item addItem(Item item) {
        Item saved = itemRepository.save(item);
        checkAndSendLowStock(saved);
        return saved;
    }

    @Override
    public Item updateItem(Long id, Item updatedItem) {
        return itemRepository.findById(id).map(existing -> {
            existing.setName(updatedItem.getName());
            existing.setQuantity(updatedItem.getQuantity());
            existing.setPrice(updatedItem.getPrice());
            existing.setCategory(updatedItem.getCategory());
            Item saved = itemRepository.save(existing);
            checkAndSendLowStock(saved);
            return saved;
        }).orElse(null);
    }

    @Override
    public boolean deleteItem(Long id) {
        if (!itemRepository.existsById(id)) return false;
        itemRepository.deleteById(id);
        return true;
    }

    @Override
    public Item increaseStock(Long id, int amount) {
        return itemRepository.findById(id).map(item -> {
            item.setQuantity(item.getQuantity() + amount);
            Item saved = itemRepository.save(item);
            // no low-stock check necessary after increase, but harmless
            return saved;
        }).orElse(null);
    }

    @Override
    public Item decreaseStock(Long id, int amount) {
        return itemRepository.findById(id).map(item -> {
            if (item.getQuantity() < amount) {
                throw new IllegalArgumentException("Not enough stock available. Current stock: " + item.getQuantity());
            }
            item.setQuantity(item.getQuantity() - amount);
            Item saved = itemRepository.save(item);
            checkAndSendLowStock(saved);
            return saved;
        }).orElse(null);
    }

    @Override
    public List<Item> searchItems(String name, String category) {
        if (name != null && !name.isBlank() && category != null && !category.isBlank()) {
            // if repository has a combined query you can use it; otherwise filter in memory
            return itemRepository.findByNameContainingIgnoreCase(name).stream()
                    .filter(i -> i.getCategory() != null && i.getCategory().toLowerCase().contains(category.toLowerCase()))
                    .toList();
        }
        if (name != null && !name.isBlank()) return itemRepository.findByNameContainingIgnoreCase(name);
        if (category != null && !category.isBlank()) return itemRepository.findByCategoryContainingIgnoreCase(category);
        return itemRepository.findAll();
    }

    public void checkAndSendLowStock(Item item) {
        if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
            String subject = "Low Stock Alert: " + item.getName();
            String text = "Item '" + item.getName() + "' is low on stock.\nCurrent quantity: " + item.getQuantity();
            emailService.sendSimpleMessage("coradolauricella@gmail.com", subject, text);
        }
    }

    public List<Item> getLowStockItems(int threshold) {
        return itemRepository.findAll().stream()
                .filter(i -> i.getQuantity() < threshold)
                .toList();
    }
}
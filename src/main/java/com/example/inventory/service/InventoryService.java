package com.example.inventory.service;

import com.example.inventory.model.Item;
import com.example.inventory.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

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



    public List<Item> getLowStockItems(int threshold) {
        return itemRepository.findAll().stream()
                .filter(i -> i.getQuantity() < threshold)
                .toList();
    }
}

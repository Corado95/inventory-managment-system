package com.example.inventory.scheduler;

import com.example.inventory.service.EmailService;
import com.example.inventory.service.InventoryService;
import com.example.inventory.model.Item;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LowStockScheduler {

    private final InventoryService inventoryService;
    private final EmailService emailService;

    public LowStockScheduler(InventoryService inventoryService, EmailService emailService) {
        this.inventoryService = inventoryService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendLowStockAlerts() {
        List<Item> lowStockItems = inventoryService.getLowStockItems(5);

        if (!lowStockItems.isEmpty()) {
            StringBuilder sb = new StringBuilder("The following items are low in stock:\n\n");
            for (Item item : lowStockItems) {
                sb.append(item.getName())
                        .append(" (Quantity: ")
                        .append(item.getQuantity())
                        .append(")\n");
            }

            emailService.sendSimpleMessage(
                    "coradolauricella@gmail.com",
                    "Low Stock Alert",
                    sb.toString()
            );
        }
    }
}

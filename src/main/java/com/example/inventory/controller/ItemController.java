package com.example.inventory.controller;

import com.example.inventory.model.Item;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.inventory.service.IInventoryService;

import java.util.List;


@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final IInventoryService inventoryService;

    public ItemController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Item> getAllItems() {
        return inventoryService.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return inventoryService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Item> addItem(@RequestBody Item item) {
        Item saved = inventoryService.addItem(item);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        Item updated = inventoryService.updateItem(id, updatedItem);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        boolean existed = inventoryService.deleteItem(id);
        if (!existed) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // Search endpoints
    @GetMapping("/search/name")
    public List<Item> searchByName(@RequestParam String name) {
        return inventoryService.searchItems(name, null);
    }

    @GetMapping("/search/category")
    public List<Item> searchByCategory(@RequestParam String category) {
        return inventoryService.searchItems(null, category);
    }

    // Increase / decrease with amount
    @PatchMapping("/{id}/increase")
    public ResponseEntity<Item> increaseStock(@PathVariable Long id, @RequestParam int amount) {
        Item item = inventoryService.increaseStock(id, amount);
        if (item == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(item);
    }

    @PatchMapping("/{id}/decrease")
    public ResponseEntity<?> decreaseStock(@PathVariable Long id, @RequestParam int amount) {
        try {
            Item updated = inventoryService.decreaseStock(id, amount);
            if (updated == null) return ResponseEntity.status(404).body("Item with id " + id + " not found");
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


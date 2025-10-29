package com.example.inventory.controller;

import com.example.inventory.model.Item;
import com.example.inventory.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.inventory.service.IInventoryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")


public class ItemController {

    private final ItemRepository itemRepository;
    private final IInventoryService inventoryService;
    ;

    public ItemController(ItemRepository itemRepository, IInventoryService inventoryService) {
        this.itemRepository = itemRepository;
        this.inventoryService = inventoryService;
    }



    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }


    @PostMapping
    public Item addItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        Optional<Item> optionalItem = itemRepository.findById(id);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Item existingItem = optionalItem.get();
        existingItem.setName(updatedItem.getName());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setPrice(updatedItem.getPrice());

        itemRepository.save(existingItem);

        return ResponseEntity.ok(existingItem);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 🔍 Search by name
    @GetMapping("/search/name")
    public List<Item> searchByName(@RequestParam String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    // 🔍 Search by category
    @GetMapping("/search/category")
    public List<Item> searchByCategory(@RequestParam String category) {
        return itemRepository.findByCategoryContainingIgnoreCase(category);
    }


    // 🟩 Increase stock
    @PatchMapping("/{id}/increase")
    public ResponseEntity<Item> increaseStock(@PathVariable Long id, @RequestParam int amount) {
        return itemRepository.findById(id)
                .map(item -> {
                    item.setQuantity(item.getQuantity() + amount);
                    return ResponseEntity.ok(itemRepository.save(item));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/decrease")
    public ResponseEntity<?> decreaseStock(@PathVariable Long id, @RequestParam int amount) {
        return itemRepository.findById(id)
                .map(item -> {
                    if (item.getQuantity() < amount) {
                        return ResponseEntity
                                .badRequest()
                                .body("Not enough stock available. Current stock: " + item.getQuantity());
                    }

                    item.setQuantity(item.getQuantity() - amount);
                    Item updatedItem = itemRepository.save(item);

                    // 🔔 Check low stock immediately after decrease
                    inventoryService.checkAndSendLowStock(updatedItem);

                    return ResponseEntity.ok(updatedItem);
                })
                .orElseGet(() -> ResponseEntity
                        .status(404)
                        .body("Item with id " + id + " not found"));
    }




}

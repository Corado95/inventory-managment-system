package com.example.inventory.service;

import com.example.inventory.model.Item;
import com.example.inventory.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

class InventoryServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIncreaseStock() {
        Item item = new Item();
        item.setId(1L);
        item.setQuantity(5);

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(item);

        Item updated = inventoryService.increaseStock(1L);

        Assertions.assertEquals(6, updated.getQuantity());
        Mockito.verify(itemRepository).save(item);
    }

    @Test
    void testDecreaseStock() {
        Item item = new Item();
        item.setId(1L);
        item.setQuantity(5);

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(item);

        Item updated = inventoryService.decreaseStock(1L);

        Assertions.assertEquals(4, updated.getQuantity());
        Mockito.verify(itemRepository).save(item);
    }
}

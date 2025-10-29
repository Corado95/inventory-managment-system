package com.example.inventory;

import com.example.inventory.model.Item;
import com.example.inventory.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") //

public class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // for JSON serialization

    @MockBean
    private EmailService emailService; // mock so no real emails are sent


    @Test
    public void whenAddItem_thenGetAllReturnsIt() throws Exception {
        Item newItem = new Item();
        newItem.setName("Test Keyboard");
        newItem.setCategory("Electronics");
        newItem.setPrice(49.99);
        newItem.setQuantity(10);


        String json = objectMapper.writeValueAsString(newItem);
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Keyboard"))
                .andExpect(jsonPath("$.quantity").value(10));


        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Keyboard"));
    }


    @Test
    public void whenDecreaseStock_belowThreshold_thenEmailSent() throws Exception {

        Item newItem = new Item();
        newItem.setName("Test Mouse");
        newItem.setCategory("Electronics");
        newItem.setPrice(19.99);
        newItem.setQuantity(2);

        String json = objectMapper.writeValueAsString(newItem);

        String response = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Item saved = objectMapper.readValue(response, Item.class);
        Long id = saved.getId();


        mockMvc.perform(patch("/api/items/" + id + "/decrease")
                        .param("amount", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(1));

        verify(emailService, times(1)).sendSimpleMessage(anyString(), anyString(), anyString());
    }
}



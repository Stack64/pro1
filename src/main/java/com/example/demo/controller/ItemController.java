package com.example.demo.controller;

import com.example.demo.dto.ItemRequest;
import com.example.demo.entity.Item;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    // In-memory store for demo (no database)
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @GetMapping
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        return item;
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody ItemRequest request) {
        Item item = new Item();
        item.setId(idSequence.getAndIncrement());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setCreatedAt(LocalDateTime.now());

        items.put(item.getId(), item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
    @PutMapping("/{id}")
    public Item updateItem(@PathVariable Long id,
                       @Valid @RequestBody ItemRequest request) {
        Item item = items.get(id);

        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        return item;
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
       if (items.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
       }
       return ResponseEntity.noContent().build();
    }
}

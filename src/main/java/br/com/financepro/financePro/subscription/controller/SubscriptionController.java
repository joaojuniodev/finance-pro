package br.com.financepro.financePro.subscription.controller;

import br.com.financepro.financePro.subscription.dto.request.SubscriptionRequestDTO;
import br.com.financepro.financePro.subscription.dto.response.SubscriptionResponseDTO;
import br.com.financepro.financePro.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions/v1")
public class SubscriptionController {

    @Autowired
    private SubscriptionService service;

    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getAll() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponseDTO> create(@RequestBody SubscriptionRequestDTO subscription) {
        return ResponseEntity.ok().body(service.create(subscription));
    }

    @PutMapping
    public ResponseEntity<SubscriptionResponseDTO> update(@RequestBody SubscriptionRequestDTO subscription) {
        return ResponseEntity.ok().body(service.update(subscription));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
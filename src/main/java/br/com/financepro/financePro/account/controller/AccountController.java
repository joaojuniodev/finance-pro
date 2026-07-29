package br.com.financepro.financePro.account.controller;

import br.com.financepro.financePro.account.controller.doc.AccountControllerDocs;
import br.com.financepro.financePro.account.dto.request.AccountRequestDTO;
import br.com.financepro.financePro.account.dto.response.AccountResponseDTO;
import br.com.financepro.financePro.account.dto.response.DashboardOverviewResponseDTO;
import br.com.financepro.financePro.account.service.AccountService;
import br.com.financepro.financePro.account.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Account")
@RestController
@RequestMapping("/api/accounts/v1")
public class AccountController implements AccountControllerDocs {

    @Autowired
    private AccountService service;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    @Override
    public ResponseEntity<List<AccountResponseDTO>> getAll() {
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/dashboard/overview/{accountId}")
    @Override
    public ResponseEntity<DashboardOverviewResponseDTO> getDashboardOverview(@PathVariable UUID accountId) {
        return ResponseEntity.ok().body(dashboardService.getDashboardOverview(accountId));
    }

    @GetMapping("/by-username/{username}")
    @Override
    public ResponseEntity<AccountResponseDTO> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok().body(service.getByUsername(username));
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<AccountResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(service.getById(id));
    }

    @PostMapping("/start/{username}")
    @Override
    public ResponseEntity<AccountResponseDTO> start(@PathVariable String username) {
        return ResponseEntity.ok().body(service.startAccount(username));
    }

    @PostMapping
    @Override
    public ResponseEntity<AccountResponseDTO> create(@RequestBody AccountRequestDTO account) {
        return ResponseEntity.ok().body(service.create(account));
    }

    @PutMapping
    @Override
    public ResponseEntity<AccountResponseDTO> update(@RequestBody AccountRequestDTO account) {
        return ResponseEntity.ok().body(service.update(account));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
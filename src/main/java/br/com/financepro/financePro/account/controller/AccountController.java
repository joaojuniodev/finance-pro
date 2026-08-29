package br.com.financepro.financePro.account.controller;

import br.com.financepro.financePro.account.controller.doc.AccountControllerDocs;
import br.com.financepro.financePro.account.dto.request.AccountRequestDTO;
import br.com.financepro.financePro.account.dto.response.AccountResponseDTO;
import br.com.financepro.financePro.account.dto.response.ActivitiesResponseDTO;
import br.com.financepro.financePro.account.dto.response.AnalyticsResponseDTO;
import br.com.financepro.financePro.account.dto.response.DashboardResponseDTO;
import br.com.financepro.financePro.account.service.AccountService;
import br.com.financepro.financePro.account.service.activities.ActivitiesService;
import br.com.financepro.financePro.account.service.analytics.AnalyticsService;
import br.com.financepro.financePro.account.service.dashboard.DashboardService;
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

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    @Override
    public ResponseEntity<List<AccountResponseDTO>> getAll() {
        return ResponseEntity.ok().body(service.getAll()); 
    }

    @GetMapping("/dashboard/{accountId}")
    @Override
    public ResponseEntity<DashboardResponseDTO> getDashboard(
        @PathVariable UUID accountId,
        @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok().body(dashboardService.getDashboard(accountId, limit));
    }

    @GetMapping("/activities/{accountId}")
    @Override
    public ResponseEntity<ActivitiesResponseDTO> getActivities(
        @PathVariable UUID accountId,
        @RequestParam(name = "month", required = false) Integer month,
        @RequestParam(name = "year", required = false) Integer year
    ) {
        return ResponseEntity.ok().body(activitiesService.getActivities(accountId, month, year));
    }

    @GetMapping("/analytics/{accountId}")
    public ResponseEntity<AnalyticsResponseDTO> getAnalytics(
        @PathVariable UUID accountId,
        @RequestParam(defaultValue = "6") int limit
    ) {
        return ResponseEntity.ok().body(analyticsService.getAnalytics(accountId, limit));
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
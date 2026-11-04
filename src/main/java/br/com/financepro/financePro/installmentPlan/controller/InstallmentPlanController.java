package br.com.financepro.financePro.installmentPlan.controller;

import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanRequestDTO;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanResponseDTO;
import br.com.financepro.financePro.installmentPlan.service.InstallmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.security.Principal;

@RestController
@RequestMapping("/api/installmentPlans/v1")
public class InstallmentPlanController {

    @Autowired
    private InstallmentPlanService service;

    @PostMapping
    public ResponseEntity<InstallmentPlanResponseDTO> create(@RequestBody InstallmentPlanRequestDTO installment, Principal principal) {
        return ResponseEntity.ok().body(service.create(installment, principal.getName()));
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<InstallmentPlanResponseDTO>> getAllByWallet(@PathVariable UUID walletId, Principal principal) {
        return ResponseEntity.ok(service.getAllByWallet(walletId, principal.getName()));
    }
}

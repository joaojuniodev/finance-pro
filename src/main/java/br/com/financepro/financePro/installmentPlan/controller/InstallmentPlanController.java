package br.com.financepro.financePro.installmentPlan.controller;

import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanRequestDTO;
import br.com.financepro.financePro.installmentPlan.dto.InstallmentPlanResponseDTO;
import br.com.financepro.financePro.installmentPlan.service.InstallmentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/installmentPlans/v1")
public class InstallmentPlanController {

    @Autowired
    private InstallmentPlanService service;

    @PostMapping
    public ResponseEntity<InstallmentPlanResponseDTO> create(@RequestBody InstallmentPlanRequestDTO installment) {
        return ResponseEntity.ok().body(service.create(installment));
    }
}
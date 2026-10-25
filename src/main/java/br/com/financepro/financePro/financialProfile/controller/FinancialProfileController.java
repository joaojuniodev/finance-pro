package br.com.financepro.financePro.financialProfile.controller;

import br.com.financepro.financePro.financialProfile.dto.FinancialProfileDetailsResponse;
import br.com.financepro.financePro.financialProfile.service.FinancialProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@RestController
@RequestMapping("/api/financial-profiles/v1")
@Tag(name = "Financial Profile")
public class FinancialProfileController {

    private final FinancialProfileService service;

    public FinancialProfileController(FinancialProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar o perfil financeiro do usuário autenticado")
    public ResponseEntity<FinancialProfileDetailsResponse> getMine(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.getByUsername(principal.getName()));
    }
}

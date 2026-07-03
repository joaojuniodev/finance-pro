package br.com.financepro.financePro.security.controller;

import br.com.financepro.financePro.security.controller.doc.AuthControllerDocs;
import br.com.financepro.financePro.security.dto.AccountCredentialsDTO;
import br.com.financepro.financePro.security.dto.UserResponseDTO;
import br.com.financepro.financePro.security.service.AuthService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    @Autowired
    private AuthService service;

    @PostMapping("/sign")
    @Override
    public ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials) {
        if (credentialsIsInvalid(credentials)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        var token = service.signIn(credentials);

        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<UserResponseDTO> register(@RequestBody AccountCredentialsDTO credentials) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(credentials));
    }

    private boolean credentialsIsInvalid(AccountCredentialsDTO credentials) {
        return credentials == null ||
            StringUtils.isBlank(credentials.getUsername()) ||
            StringUtils.isBlank(credentials.getPassword());
    }
}
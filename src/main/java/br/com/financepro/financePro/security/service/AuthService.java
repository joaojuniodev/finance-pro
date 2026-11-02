package br.com.financepro.financePro.security.service;

import br.com.financepro.financePro.security.dto.AccountCredentialsDTO;
import br.com.financepro.financePro.security.dto.TokenDTO;
import br.com.financepro.financePro.security.dto.UserResponseDTO;
import br.com.financepro.financePro.security.jwt.JwtTokenProvider;
import br.com.financepro.financePro.security.model.Role;
import br.com.financepro.financePro.security.model.User;
import br.com.financepro.financePro.account.repository.AccountRepository;
import br.com.financepro.financePro.security.repository.UserRepository;
import br.com.financepro.financePro.wallet.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final Logger log = LoggerFactory.getLogger(AuthService.class.getName());

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TokenDTO signIn(AccountCredentialsDTO credentials) {
        log.info("Performing the login");

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword()
            )
        );

        var user = repository.findByUsernameIgnoreCaseOrEmailIgnoreCase(credentials.getUsername(), credentials.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("Username " + credentials.getUsername() + " not found!");
        }

        var roles = user.getRoles()
            .stream()
            .map(Role::getAuthority)
            .toList();

        String fullName = user.getFullName();

        return tokenProvider.createAccessToken(
            user.getUsername(),
            fullName,
            roles
        );
    }

    public UserResponseDTO register(AccountCredentialsDTO credentials) {
        log.info("Registering new User");

        if (credentials.getUsername() == null || !credentials.getUsername().trim().matches("[A-Za-z0-9_.-]{3,50}")
            || credentials.getEmail() == null || !credentials.getEmail().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
            || credentials.getPassword() == null || credentials.getPassword().length() < 8
            || credentials.getFullName() == null || credentials.getFullName().trim().length() < 3) {
            throw new br.com.financepro.financePro.security.exception.RegistrationException("Confira os dados informados para o cadastro.");
        }
        credentials.setUsername(credentials.getUsername().trim());
        credentials.setEmail(credentials.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        if (repository.existsByUsernameIgnoreCase(credentials.getUsername())) {
            throw new br.com.financepro.financePro.security.exception.RegistrationException("Este nome de usuário já está em uso.");
        }
        if (repository.existsByEmailIgnoreCase(credentials.getEmail()) || repository.existsByUsernameIgnoreCase(credentials.getEmail())) {
            throw new br.com.financepro.financePro.security.exception.RegistrationException("Este e-mail já está em uso.");
        }

        var user = buildUser(credentials);
        user.setRoles(List.of());

        try {
            User savedUser = repository.saveAndFlush(user);
            return toResponse(savedUser);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new br.com.financepro.financePro.security.exception.RegistrationException("Nome de usuário ou e-mail já cadastrado.");
        }
    }

    private User buildUser(AccountCredentialsDTO credentials) {
        User user = new User();
        user.setUsername(credentials.getUsername());
        user.setEmail(credentials.getEmail());
        user.setRecoveryEmail(credentials.getRecoveryEmail());
        user.setPassword(generatedPassword(credentials.getPassword()));
        user.setFullName(credentials.getFullName());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        return user;
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), null, user.getEnabled());
    }

    private String generatedPassword(String passwordString) {
        return passwordEncoder.encode(passwordString);
    }
}

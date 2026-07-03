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

        var user = repository.findByUserName(credentials.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("Username " + credentials.getUsername() + " not found!");
        }

        var roles = user.getRoles()
            .stream()
            .map(Role::getAuthority)
            .toList();

        String fullName = repository.findFullNameByUser(credentials.getUsername());

        return tokenProvider.createAccessToken(
            credentials.getUsername(),
            fullName,
            roles
        );
    }

    public UserResponseDTO register(AccountCredentialsDTO credentials) {
        log.info("Registering new User");

        var user = buildUser(credentials);
        user.setRoles(List.of());

        User savedUser = repository.save(user);
        return toResponse(savedUser);
    }

    private User buildUser(AccountCredentialsDTO credentials) {
        User user = new User();
        user.setUsername(credentials.getUsername());
        user.setPassword(generatedPassword(credentials.getPassword()));
        user.setFullName(credentials.getFullName());
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        return user;
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEnabled());
    }

    private String generatedPassword(String passwordString) {
        return passwordEncoder.encode(passwordString);
    }
}
package br.com.financepro.financePro.security.dto;

import java.util.Objects;

public class AccountCredentialsDTO {

    private String username;
    private String email;
    private String recoveryEmail;
    private String fullName;
    private String password;

    public AccountCredentialsDTO() {}

    public AccountCredentialsDTO(String username, String email, String recoveryEmail, String fullName, String password) {
        this.username = username;
        this.email = email;
        this.recoveryEmail = recoveryEmail;
        this.fullName = fullName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AccountCredentialsDTO that = (AccountCredentialsDTO) o;
        return Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getFullName(), that.getFullName()) && Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getUsername());
        result = 31 * result + Objects.hashCode(getFullName());
        result = 31 * result + Objects.hashCode(getPassword());
        return result;
    }
}
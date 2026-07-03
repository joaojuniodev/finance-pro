package br.com.financepro.financePro.security.dto;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TokenDTO {

    private String username;
    private String fullName;
    private List<String> roles;
    private Boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;

    public TokenDTO() {}

    public TokenDTO(String username, String fullName, List<String> roles, Boolean authenticated, Date created, Date expiration, String accessToken, String refreshToken) {
        this.username = username;
        this.fullName = fullName;
        this.roles = roles;
        this.authenticated = authenticated;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        TokenDTO tokenDTO = (TokenDTO) o;
        return Objects.equals(getUsername(), tokenDTO.getUsername()) && Objects.equals(getFullName(), tokenDTO.getFullName()) && Objects.equals(getRoles(), tokenDTO.getRoles()) && Objects.equals(getAuthenticated(), tokenDTO.getAuthenticated()) && Objects.equals(getCreated(), tokenDTO.getCreated()) && Objects.equals(getExpiration(), tokenDTO.getExpiration()) && Objects.equals(getAccessToken(), tokenDTO.getAccessToken()) && Objects.equals(getRefreshToken(), tokenDTO.getRefreshToken());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getUsername());
        result = 31 * result + Objects.hashCode(getFullName());
        result = 31 * result + Objects.hashCode(getRoles());
        result = 31 * result + Objects.hashCode(getAuthenticated());
        result = 31 * result + Objects.hashCode(getCreated());
        result = 31 * result + Objects.hashCode(getExpiration());
        result = 31 * result + Objects.hashCode(getAccessToken());
        result = 31 * result + Objects.hashCode(getRefreshToken());
        return result;
    }
}
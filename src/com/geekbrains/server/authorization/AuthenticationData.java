package com.geekbrains.server.authorization;

public class AuthenticationData {

    private String username;
    private boolean authenticated;

    public String getUsername() {
        return username;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}

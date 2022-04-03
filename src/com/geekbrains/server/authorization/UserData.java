package com.geekbrains.server.authorization;

public class UserData {
    private final int id;
    private final String nickName;
    private final String login;
    private final String password;

    public UserData(int id, String nickName, String login, String password) {
        this.id = id;
        this.nickName = nickName;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void info() {
        System.out.printf("Идентификатор: %s%nПользователь: %s%nЛогин: %s", id, nickName, login);
    }
}

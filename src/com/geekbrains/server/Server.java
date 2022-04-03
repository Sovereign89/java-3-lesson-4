package com.geekbrains.server;

import com.geekbrains.CommonConstants;
import com.geekbrains.server.authorization.AuthService;
import com.geekbrains.server.authorization.InMemoryAuthServiceImpl;
import com.geekbrains.server.authorization.UserData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final AuthService authService;
    private final FileUtils fileUtils;

    private List<ClientHandler> connectedUsers;
    Connection connection = null;

    public Server() {
        this.fileUtils = new FileUtils();
        this.authService = new InMemoryAuthServiceImpl();
        try (ServerSocket server = new ServerSocket(CommonConstants.SERVER_PORT)) {
            authService.start();
            connect();
            findAll();
            connectedUsers = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException | SQLException exception) {
            System.out.println("Ошибка в работе сервера");
            exception.printStackTrace();
        } finally {
            if (authService != null) {
                authService.end();
            }
        }
    }

    private void findAll() {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT * FROM USERS;")) {
                while (rs.next()) {
                    UserData user = new UserData(rs.getInt("id"), rs.getString("username"), rs.getString("login"), rs.getString("password"));
                    authService.addUser(rs.getString("login"),user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeNickname(int userId, String nickname) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("UPDATE USERS SET USERNAME = \"%s\" WHERE ID = %d;", nickname, userId));
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserIdByLogin(String login) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(String.format("SELECT ID FROM USERS WHERE LOGIN = \"%s\";", login))) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickNameBusy(String nickName) {
        for (ClientHandler handler : connectedUsers) {
            if (handler.getNickName().equals(nickName)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler handler : connectedUsers) {
            handler.sendMessage(message);
            if (fileUtils.writeToFile(message, handler.getLogin())) {
                System.out.println("Message stored to file");
            }
        }
    }

    public synchronized void privateMessage(String username, String message) {
        for(ClientHandler handler: connectedUsers) {
            if(handler.getNickName().equals(username)){
                handler.sendMessage("{" + username + "}: " + message);
                if (fileUtils.writeToFile(message, handler.getLogin())) {
                    System.out.println("Message stored to file");
                }
            }
        }
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
        System.out.println("Connected to DB successfully");
    }

    private void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public synchronized void addConnectedUser(ClientHandler handler) {
        connectedUsers.add(handler);
    }

    public synchronized void disconnectUser(ClientHandler handler) {
        connectedUsers.remove(handler);
    }

    public String getClients() {
        StringBuilder builder = new StringBuilder("/clients ");
        for (ClientHandler user : connectedUsers) {
            builder.append(user.getNickName()).append("\n");
        }

        return builder.toString();
    }
}

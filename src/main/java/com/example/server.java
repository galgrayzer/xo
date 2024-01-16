package com.example;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class server {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket();
            server.bind(new java.net.InetSocketAddress(sockProtocol.getHost(), sockProtocol.getPort()));
            System.out.println("Server started on port 1234");
            ArrayList<GAME> games = new ArrayList<GAME>();
            while (true) {
                Socket client = server.accept();
                sockProtocol sock = new sockProtocol(client);
                System.out.println("Client connected from " + client.getInetAddress());
                new Thread(() -> {
                    Boolean flag = true;
                    try {
                        String msg = sock.res();
                        String name = msg.split(" ")[0];
                        String size = msg.split(" ")[1];
                        GAME currentgame = null;
                        for (GAME game : games) {
                            if (game.getGridSize() == Integer.parseInt(size) && !game.isFull()) {
                                sock.send("2");
                                game.setSocket(client);
                                game.setPlayer2(name);
                                currentgame = game;
                                games.remove(game);
                                break;
                            }
                        }
                        if (currentgame == null) {
                            sock.send("1");
                            currentgame = new GAME(Integer.parseInt(size), name, client);
                        }
                        games.add(currentgame);
                        System.out.println("Game created with size " + size);
                        while (flag && !client.isClosed()) {
                            msg = sock.res();
                            String[] msgArr = msg.split(" ");
                            if (msgArr[0].equals("isFull")) {
                                sock.send("isFull " + String.valueOf(currentgame.isFull()));
                            } else if (msgArr[0].equals("getGridSize")) {
                                sock.send(String.valueOf(currentgame.getGridSize()));
                            } else if (msgArr[0].equals("changeGrid")) {
                                currentgame.changeGrid(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]),
                                        Integer.parseInt(msgArr[3]));
                            } else if (msgArr[0].equals("getGrid")) {
                                sock.send(String.valueOf(
                                        currentgame.getGrid(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]))));
                            } else if (msgArr[0].equals("getPlayers")) {
                                String[] players = currentgame.getPlayers();
                                sock.send("getPlayers " + players[0] + " " + players[1]);
                            } else if (msgArr[0].equals("close")) {
                                currentgame.close();
                                games.remove(currentgame);
                                sock.close();
                                flag = false;
                                System.out.println("Client disconnected from " + client.getInetAddress());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                        flag = false;
                        try {
                            sock.close();
                            System.out.println("Client disconnected from " + client.getInetAddress());
                        } catch (Exception e2) {
                            System.out.println("Error: " + e2);
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}

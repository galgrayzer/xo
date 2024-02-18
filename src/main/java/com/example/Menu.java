package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import com.example.views.PlayersDB;
import com.example.views.PlayerStatsDB;

import com.example.models.*;

import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;

public class Menu {

    private PlayersDB playersDB;
    private PlayerStatsDB playerStatsDB;

    @FXML
    private TextField gridSize;
    @FXML
    private TextField playerName;
    @FXML
    private Label error;

    @FXML
    public void Init() {
        try {
            this.playerStatsDB = new PlayerStatsDB();
            this.playersDB = new PlayersDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                int gridSize = Integer.parseInt(this.gridSize.getText());
                Platform.runLater(() -> {
                    try {
                        processGridSize(gridSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (NumberFormatException e) {
                Platform.runLater(() -> error.setText("Error: Grid size must be an integer"));
            }
        }).start();
    }

    private void processGridSize(int gridSize) throws IOException {
        try {
            if (gridSize < 3) {
                error.setText("Error: Grid size must be at least 3");
            } else if (gridSize > 7) {
                error.setText("Error: Grid size must be at most 7");
            } else {
                GUI gui = new GUI(1, gridSize);
                Socket gameSocket = new Socket(sockProtocol.getHost(), sockProtocol.getPort());
                sockProtocol sock = new sockProtocol(gameSocket);
                gui.setGame(gameSocket, sock);
                sock.send(playerName.getText() + " " + gridSize);
                try {
                    Player player = new Player(playerName.getText());
                    ArrayList<Player> players = playersDB.selectAll();
                    boolean exists = false;
                    for (Player p : players) {
                        if (p.getUsername().equals(playerName.getText())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        playersDB.insert(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
                loader.setController(gui);
                App.getPrimaryStage().setScene(new Scene(loader.load(), 640, 480));
                App.getPrimaryStage().setOnCloseRequest(e -> {
                    sock.send("close");
                });
                new Thread(() -> {
                    String msg;
                    try {
                        while (true) {
                            try {
                                msg = sock.res();
                            } catch (Exception e) {
                                break;
                            }
                            String[] msgArr = msg.split(" ");
                            Platform.runLater(() -> {
                                // UI update code
                                if (msgArr[0].equals("1") || msgArr[0].equals("2")) {
                                    gui.setValue(Integer.parseInt(msgArr[0]));
                                } else if (msgArr[0].equals("refreshGrid")) {
                                    gui.updateGrid(Integer.parseInt(msgArr[1]), Integer.parseInt(msgArr[2]),
                                            Integer.parseInt(msgArr[3]));
                                } else if (msgArr[0].equals("enableGrid")) {
                                    gui.enableGrid();
                                } else if (msgArr[0].equals("disableGrid")) {
                                    gui.disableGrid();
                                } else if (msgArr[0].equals("win")) {
                                    try {
                                        Player player = playersDB.selectByUsername(playerName.getText());
                                        Stats stats = playerStatsDB.selectByPlayerId(player.getId());
                                        stats.setWins(stats.getWins() + 1);
                                        playerStatsDB.update(stats);
                                        gui.win(stats, playerName.getText());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (msgArr[0].equals("lose")) {
                                    try {
                                        Player player = playersDB.selectByUsername(playerName.getText());
                                        Stats stats = playerStatsDB.selectByPlayerId(player.getId());
                                        stats.setLosses(stats.getLosses() + 1);
                                        playerStatsDB.update(stats);
                                        gui.lose(stats, playerName.getText());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (msgArr[0].equals("draw")) {
                                    try {
                                        Player player = playersDB.selectByUsername(playerName.getText());
                                        Stats stats = playerStatsDB.selectByPlayerId(player.getId());
                                        stats.setDraws(stats.getDraws() + 1);
                                        playerStatsDB.update(stats);
                                        gui.draw(stats, playerName.getText());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (msgArr[0].equals("refresh")) {
                                    gui.refresh();
                                } else if (msgArr[0].equals("createGrid")) {
                                    gui.createGrid();
                                } else if (msgArr[0].equals("getPlayers")) {
                                    gui.setPlayer1(msgArr[1]);
                                    gui.setPlayer2(msgArr[2]);
                                    gui.updatePlayerNames();
                                } else if (msgArr[0].equals("isFull")) {
                                    gui.setIsFull(msgArr[1]);
                                    gui.playersJoined();
                                } else if (msgArr[0].equals("close")) {
                                    sock.close();
                                    try {
                                        this.playerStatsDB.close();
                                        this.playersDB.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

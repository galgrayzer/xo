package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import java.sql.ResultSet;

import java.net.Socket;

import java.io.IOException;

public class Menu {
    private mysql sql;

    @FXML
    private TextField gridSize;
    @FXML
    private TextField playerName;
    @FXML
    private Label error;

    @FXML
    public void Init() {
        try {
            this.sql = new mysql();
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

    private volatile boolean isRunning = true;

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
                    sql.query(
                            "INSERT INTO players (username)"
                                    + " SELECT '" + playerName.getText() + "'"
                                    + " WHERE NOT EXISTS (SELECT 1 FROM players WHERE username='" + playerName.getText()
                                    + "');",
                            true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource("GameWindow.fxml"));
                loader.setController(gui);
                App.getPrimaryStage().setScene(new Scene(loader.load(), 640, 480));
                App.getPrimaryStage().setOnCloseRequest(e -> {
                    sock.send("close");
                    isRunning = false;
                });
                new Thread(() -> {
                    try {
                        while (isRunning) {
                            String msg = sock.res();
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
                                        sql.query(
                                                "UPDATE playerStats SET wins = wins + 1 WHERE playerID = (SELECT playerID FROM players WHERE username='"
                                                        + playerName.getText() + "');",
                                                true);
                                        gui.win(sql, playerName.getText());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (msgArr[0].equals("lose")) {
                                    try {
                                        sql.query(
                                                "UPDATE playerStats SET losses = losses + 1 WHERE playerID = (SELECT playerID FROM players WHERE username='"
                                                        + playerName.getText() + "');",
                                                true);
                                        gui.lose(sql, playerName.getText());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (msgArr[0].equals("draw")) {
                                    try {
                                        sql.query(
                                                "UPDATE playerStats SET draws = draws + 1 WHERE playerID = (SELECT playerID FROM players WHERE username='"
                                                        + playerName.getText() + "');",
                                                true);
                                        gui.draw(sql, playerName.getText());
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
                                    isRunning = false;
                                    try {
                                        sql.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                        isRunning = false;
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

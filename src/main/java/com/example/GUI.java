package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;

import java.net.Socket;

public class GUI implements Initializable, gameInterface {
    private Socket gameSocket;
    private sockProtocol sock;
    private int value;
    private int gridSize;
    private String isFull;
    private int[][] grid;
    private String player1, player2;

    @FXML
    private Label playerName;

    @FXML
    private Label player2Name;

    @FXML
    private Label centerLabel;

    @FXML
    private GridPane gridPane;

    public GUI(int value, int gridSize) {
        this.value = value;
        this.gridSize = gridSize;
        this.grid = new int[gridSize][gridSize];
    }

    void setIsFull(String isFull) {
        this.isFull = isFull;
    }

    void setPlayer1(String player1) {
        this.player1 = player1;
    }

    void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public void setGame(Socket game, sockProtocol sock) {
        this.gameSocket = game;
        this.sock = sock;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public void updateGrid(int i, int j, int value) {
        this.grid[i][j] = value;
        refreshGrid();
    }

    public void refresh() {
        sock.send("getPlayers");
        sock.send("isFull");
    }

    public void createGrid() {
        try {
            if (isFull == "false") {
                return;
            }
            gridPane.getChildren().clear();
            for (int i = 0; i < gridSize; i++) {
                gridPane.addRow(i);
                for (int j = 0; j < gridSize; j++) {
                    gridPane.addColumn(j);
                    Button button = new Button();
                    button.setOnAction((ActionEvent event) -> {
                        int x = GridPane.getColumnIndex(button);
                        int y = GridPane.getRowIndex(button);
                        try {
                            sock.send(String.format("changeGrid %d %d %d", x, y, value));
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                        }
                    });
                    button.setStyle("-fx-font-size: 30px; -fx-min-width: 100px; -fx-min-height: 100px;");
                    if (value == 2) {
                        button.setDisable(true);
                    }
                    gridPane.add(button, i, j);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void disableGrid() {
        try {
            for (int i = 0; i < gridSize; i++) {
                gridPane.addRow(i);
                for (int j = 0; j < gridSize; j++) {
                    gridPane.addColumn(j);
                    Button button = (Button) gridPane.getChildren().get(i * gridSize + j);
                    button.setDisable(true);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void enableGrid() {
        try {
            int i, j;
            for (i = 0; i < gridSize; i++) {
                gridPane.addRow(i);
                for (j = 0; j < gridSize; j++) {
                    gridPane.addColumn(j);
                    Button button = (Button) gridPane.getChildren().get(i * gridSize + j);
                    if (grid[i][j] != 0) {
                        button.setDisable(true);
                        continue;
                    }
                    button.setDisable(false);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void win() {
        centerLabel.setText("You win!");
        gridPane.visibleProperty().set(false);
    }

    public void draw() {
        centerLabel.setText("Draw!");
        gridPane.visibleProperty().set(false);
    }

    public void lose() {
        centerLabel.setText("You lose!");
        gridPane.visibleProperty().set(false);
    }

    public void refreshGrid() {
        try {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    Button button = (Button) gridPane.getChildren().get(i * gridSize + j);
                    if (grid[i][j] == 1) {
                        button.setText("X");
                        button.setDisable(true);
                    } else if (grid[i][j] == 2) {
                        button.setText("O");
                        button.setDisable(true);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);

        }
    }

    public void playersJoined() {
        try {
            if (isFull.equals("true")) {
                centerLabel.setText("");

            } else {
                centerLabel.setText("Waiting for players...");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void updatePlayerNames() {
        playerName.setText(player1);
        player2Name.setText(player2);
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        refresh();
    }
}

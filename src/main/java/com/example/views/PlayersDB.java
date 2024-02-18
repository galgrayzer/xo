package com.example.views;

import java.util.ArrayList;
import com.example.models.Player;

public class PlayersDB extends Mysql {

    public PlayersDB() throws Exception {
        super("jdbc:mysql://localhost:3306/javaxo", "root", "1234");
    }

    private void createPlayer(Player player) {
        try {
            player.setId(this.result.getInt("playerId"));
            player.setUsername(this.result.getString("username"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Player> selectAll() {
        ArrayList<Player> players = new ArrayList<Player>();
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM players");
            this.select();
            while (result.next()) {
                Player player = new Player();
                createPlayer(player);
                players.add(player);
            }
            return players;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player selectById(int id) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM players WHERE playerId = ?");
            this.statement.setInt(1, id);
            this.select();
            if (result.next()) {
                Player player = new Player();
                createPlayer(player);
                return player;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player selectByUsername(String username) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM players WHERE username = ?");
            this.statement.setString(1, username);
            this.select();
            if (result.next()) {
                Player player = new Player();
                createPlayer(player);
                return player;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(Player player) {
        try {
            this.statement = this.connection.prepareStatement("INSERT INTO players (username) VALUES (?)");
            this.statement.setString(1, player.getUsername());
            this.changeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Player player) {
        try {
            this.statement = this.connection.prepareStatement("UPDATE players SET username = ? WHERE playerId = ?");
            this.statement.setString(1, player.getUsername());
            this.statement.setInt(2, player.getId());
            this.changeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            this.statement = this.connection.prepareStatement("DELETE FROM players WHERE playerId = ?");
            this.statement.setInt(1, id);
            this.changeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

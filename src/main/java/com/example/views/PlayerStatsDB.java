package com.example.views;

import java.util.ArrayList;
import com.example.models.Stats;

public class PlayerStatsDB extends Mysql {

    public PlayerStatsDB() throws Exception {
        super("jdbc:mysql://localhost:3306/javaxo", "root", "1234");
    }

    private void createStats(Stats stats) {
        try {
            stats.setId(this.result.getInt("playerId"));
            stats.setWins(this.result.getInt("wins"));
            stats.setLosses(this.result.getInt("losses"));
            stats.setDraws(this.result.getInt("draws"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Stats> selectAll() {
        ArrayList<Stats> allStats = new ArrayList<Stats>();
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM playerStats");
            this.select();
            while (result.next()) {
                Stats stats = new Stats();
                createStats(stats);
                allStats.add(stats);
            }
            return allStats;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Stats selectByPlayerId(int id) {
        try {
            this.statement = this.connection.prepareStatement("SELECT * FROM playerStats WHERE playerId = ?");
            this.statement.setInt(1, id);
            this.select();
            if (result.next()) {
                Stats stats = new Stats();
                createStats(stats);
                return stats;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(Stats stats) {
        try {
            this.statement = this.connection
                    .prepareStatement("INSERT INTO playerStats (playerId, wins, losses, draws) VALUES (?, ?, ?, ?)");
            this.statement.setInt(1, stats.getId());
            this.statement.setInt(2, stats.getWins());
            this.statement.setInt(3, stats.getLosses());
            this.statement.setInt(4, stats.getDraws());
            this.changeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Stats stats) {
        try {
            this.statement = this.connection
                    .prepareStatement("UPDATE playerStats SET wins = ?, losses = ?, draws = ? WHERE playerId = ?");
            this.statement.setInt(1, stats.getWins());
            this.statement.setInt(2, stats.getLosses());
            this.statement.setInt(3, stats.getDraws());
            this.statement.setInt(4, stats.getId());
            this.changeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Stats stats) {
        try {
            this.statement = this.connection.prepareStatement("DELETE FROM playerStats WHERE playerId = ?");
            this.statement.setInt(1, stats.getId());
            this.changeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

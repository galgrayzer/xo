package com.example;

public interface gameInterface {
    public void refreshGrid();

    public void disableGrid();

    public void enableGrid();

    public void win(mysql sql, String username);

    public void lose(mysql sql, String username);

    public void draw(mysql sql, String username);

    public void refresh();
}

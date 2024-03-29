package com.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class sockProtocol {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private static int PORT = 1234;
    private static String HOST = "localhost";

    public sockProtocol(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public String res() throws Exception {
        try {
            return this.in.readUTF();
        } catch (Exception e) {
            throw e;
        }
    }

    public void send(String res) {
        try {
            this.out.writeUTF(res);
            this.out.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public boolean isClosed() {
        return this.socket.isClosed();
    }

    public static String getHost() {
        return HOST;
    }

    public static int getPort() {
        return PORT;
    }
}

package org.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SendMessage {

    String host;
    ServerSocket server;
    int port;

    private PrintWriter output;

    public SendMessage(String host, int port, ServerSocket server) {
        this.host = host;
        this.server = server;
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void execute(String message) throws IOException {
        try (Socket conn = new Socket(host, port)) {

            this.output = new PrintWriter(conn.getOutputStream(), true);
            this.output.println(message);

        } catch (Exception e) {
            this.output.print("Server fechou");
            server.close();
        }
    }

    public void executeAndWait(String message) throws IOException {
        try (Socket conn = new Socket(host, port)) {
            var input = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            this.output = new PrintWriter(conn.getOutputStream(), true);
            this.output.println(message);

            input.readLine();
            System.out.println("Retorno da seção crítica");

        } catch (Exception e) {
            this.output.print("Server fechou");
            server.close();
        }
    }
}

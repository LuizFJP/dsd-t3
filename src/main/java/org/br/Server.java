package org.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server extends Thread {
    ServerSocket server = new ServerSocket(175);
    String requestReceived;
    String[] requestToServer;

    BufferedReader input;
    PrintWriter output;

    private List<String> ipList = new ArrayList<>();

    public Server() throws IOException {
    }

    @Override
    public void run() {
        try {
            initServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initServer() throws IOException {

        server.setReuseAddress(true);

        System.out.println("Server iniciado");

        while (true) {
            try {
                Socket connection = server.accept();

                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                output = new PrintWriter(connection.getOutputStream(), true);

                System.out.println("Recebendo mensagem");

                requestReceived = input.readLine();

                ipList.add(requestReceived);
                sendMessage();

                requestToServer = requestReceived.split(";");

                connection.close();
                if (ipList.size() == 5) {
                    sendMessage();
                }
            } catch (Exception e) {
                output.print("Server fechou");
                server.close();
            }
        }
    }

    private void sendMessage() {
        for (String ip : ipList) {
            try (Socket conn = new Socket("127.0.0.1", 176)) {

                System.out.println("Conex�o estabelecida.");
                PrintWriter out = new PrintWriter(conn.getOutputStream(), true);

//                out.println(ipList);
                out.println(Arrays.asList("chegou", "mensagem"));
            } catch (UnknownHostException e) {
                System.out.println("host n�o encontrado");
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

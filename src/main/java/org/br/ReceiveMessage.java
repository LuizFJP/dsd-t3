package org.br;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ReceiveMessage extends Thread {
    ServerSocket server;
    BufferedReader input;
    MessageCallback callBack;

    public ReceiveMessage(ServerSocket server, MessageCallback callBack) {
        this.server = server;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() throws IOException {
        while (true) {
            try {
                Socket connection = server.accept();

                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                System.out.println("Recebendo mensagem");

                var requestReceived = input.readLine();
                var requestToServer = requestReceived.split(";");
                callBack.onMessageReceived(List.of(requestToServer));

                connection.close();
            } catch (Exception e) {
                server.close();
            }
        }
    }


    public interface MessageCallback {
        void onMessageReceived(List<String> message) throws IOException;
    }
}

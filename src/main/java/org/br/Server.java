package org.br;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.br.models.Machine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server extends Thread {
    ServerSocket server;
    private final int selfPort;
    String requestReceived;
    int counter = 0;
    ObjectMapper mapper = new ObjectMapper();

    BufferedReader input;
    PrintWriter output;

    private List<Machine> machineList = new ArrayList<>();

    public Server(int selfPort) throws IOException {
        this.selfPort = selfPort;
        this.server = new ServerSocket(selfPort);
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
        machineList.add(new Machine(selfPort, server.getInetAddress().getHostAddress()));
        server.setReuseAddress(true);

        System.out.println("Server iniciado");

        while (true) {
            try {
                Socket connection = server.accept();

                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                output = new PrintWriter(connection.getOutputStream(), true);

                System.out.println("Recebendo mensagem");

                requestReceived = input.readLine();
                var requestToServer = requestReceived.split(";");
                System.out.println("mensagem recebida pelo servidor: " + requestToServer[0]);
                if (requestReceived.contains("Machine")) {
                    var machine = Machine.fromString(requestReceived);
                    machineList.add(machine);
                } else {
                    counter += Integer.parseInt(List.of(requestToServer).get(0));
                    System.out.println(List.of(requestToServer).get(1));
                }

                connection.close();
                System.out.println(machineList.size());
                if (machineList.size() == 6) {
                    sendMessage();
                }
            } catch (Exception e) {
                output.print("Server fechou");
                server.close();
            }
        }
    }

    private void sendMessage() {
        for (int i = 1; i < machineList.size(); i++) {
            try (Socket conn = new Socket(machineList.get(i).getHost(), machineList.get(i).getPort())) {

                System.out.println("retornando lista de ips e hosts para: ." + machineList.get(i).getHost() + ":" + machineList.get(i).getPort());
                PrintWriter out = new PrintWriter(conn.getOutputStream(), true);

                String listAsString = machineList.stream()
                        .map(Machine::toString) // Assuming Machine has a meaningful toString() implementation
                        .collect(Collectors.joining(";"));

                out.println(listAsString);
            } catch (UnknownHostException e) {
                System.out.println("host não encontrado");
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

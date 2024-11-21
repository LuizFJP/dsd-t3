package org.br;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.br.models.Machine;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Host extends Thread {
    ServerSocket server;
    int okCount = 0;
    LamportClock clock;
    private List<Machine> machineList = new ArrayList<>();
    private List<HostMetaData> hostMetadataList = new ArrayList<>();
    private Afonso state = Afonso.RELEASED;
    private String selfIp;
    private final int selfPort;
    ObjectMapper mapper = new ObjectMapper();

    public Host(int selfPort) throws IOException {
        clock = new LamportClock();
        this.selfPort = selfPort;
        this.server = new ServerSocket(selfPort);
        server.setReuseAddress(true);
    }

    @Override
    public void run() {
        connectToServer();
        var activateWanted = new ActivateWanted((port) -> {
            try {
                requestAccess(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            activateWanted.start();
            activateWanted.join();
            readMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException ex) {
            Logger.getLogger(Host.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readMessage() throws IOException, InterruptedException {
        var receiveMessage = new ReceiveMessage(this.server, this::handleMessage);
        receiveMessage.start();
        receiveMessage.join();
    }

    public void handleMessage(List<String> message) throws IOException {
        System.out.println("host:" + this.selfPort +" message: " + message );
        System.out.println("Tamanho da mensagem: " + message.size());
        if (message.size() == 6) {
            System.out.println("Processando lista de máquinas recebida...");
            machineList = message.stream().map(Machine::fromString)
                    .collect(Collectors.toCollection(ArrayList::new));
        } else if (message.size() > 0 && message.get(0).equals("OK")) {
            System.out.println("Mensagem OK identificada.");
            okCount++;
            System.out.println("Contador de OKs: " + okCount);

            System.out.println(machineList.size() - 1);
            System.out.println(okCount == machineList.size() - 2);
//            System.out.println(machineList);

            if (okCount == machineList.size() - 2){
                System.out.println("Entrando na região crítica...");
                System.out.println(hostMetadataList);
                System.out.println(message.get(0));
                this.state = Afonso.HELD;
                var sendMessage = new SendMessage(
                        machineList.get(0).getHost(),
                        machineList.get(0).getPort(),
                        this.server);

                System.out.println("Criado SendMessage");
                sendMessage.executeAndWait("1" + ";" + selfIp);

                System.out.println("Passou do execute");
                okCount = 0;
                System.out.println("Saindo da região crítica...");

                System.out.println(clock.getClock());
                this.state = Afonso.RELEASED;
                System.out.println(this.state);
                for (int i = 0; !hostMetadataList.isEmpty(); i++) {
                    clock.increment();
                    sendMessage.setHost(hostMetadataList.get(i).ip);
                    sendMessage.setPort(hostMetadataList.get(i).port);
                    hostMetadataList.remove(i);
                }
            }
        } else {
            System.out.println("Mensagem recebida.");
            var hostMetaData = HostMetaData.fromString(message.get(0));
            System.out.println(hostMetaData);

            System.out.println(hostMetaData.requestType.equals(RequestType.REQUEST));
            System.out.println(RequestType.REQUEST);
            System.out.println(hostMetaData.requestType);

            robson(hostMetaData);

        }
    }

    public void connectToServer() {
        try (Socket conn = new Socket("0.0.0.0", 175)) {
            System.out.println("Conexão estabelecida." + conn);
            PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
            selfIp = this.server.getInetAddress().getHostAddress();
            var thisMachine = new Machine(selfPort, selfIp);
            out.println(thisMachine);
        } catch (UnknownHostException e) {
            System.out.println("host não encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void robson(HostMetaData hostMetaData) throws IOException {
        var sendMessage = new SendMessage(hostMetaData.ip, hostMetaData.port, this.server);
        if (state == Afonso.RELEASED) {
            clock.increment();
            sendMessage.execute(RequestType.OK.toString());
        }
        if (state == Afonso.WANTED) {
            if (hostMetaData.timestamp < clock.getClock()) {
                clock.update(hostMetaData.timestamp);
                sendMessage.execute(RequestType.OK.toString());
            }
        }
        clock.update(hostMetaData.timestamp);
        hostMetadataList.add(hostMetaData);

    }

    public void requestAccess(String ip) throws IOException {
        System.out.println("Lista de máquinas: " + this.machineList);
        if (ip.equals(Integer.toString(selfPort))) {
            this.state = Afonso.WANTED;
            var thisHostMetadata = new HostMetaData(this.selfIp, this.selfPort, this.clock.getClock(), RequestType.REQUEST);
            for (int i = 1; i < machineList.size(); i++) {
                if (machineList.get(i).getPort() != selfPort) {
                    var sendMessage = new SendMessage(
                            machineList.get(i).getHost(),
                            machineList.get(i).getPort(),
                            this.server);

                    sendMessage.execute(thisHostMetadata.toString());
                }
            }
        }
    }

    public static class HostMetaData {
        String ip;
        int port;
        int timestamp;
        RequestType requestType;

        public HostMetaData() {}

        public HostMetaData(String ip, int port, int timestamp, RequestType requestType) {
            this.ip = ip;
            this.port = port;
            this.timestamp = timestamp;
            this.requestType = requestType;
        }

        @Override
        public String toString() {
            return "HostMetaData{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", timestamp=" + timestamp +
                    ", requestType=" + requestType +
                    '}';
        }

        public static HostMetaData fromString(String str) {
            // Remove the "HostMetaData{" prefix and the trailing '}'
            str = str.replace("HostMetaData{", "").replace("}", "");

            // Split the string by comma to separate fields
            String[] fields = str.split(", ");

            // Create a new HostMetaData instance
            HostMetaData metaData = new HostMetaData();

            // Parse each field and set the corresponding value
            for (String field : fields) {
                String[] keyValue = field.split("=");
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                switch (key) {
                    case "ip":
                        metaData.ip = value.replace("'", ""); // Remove single quotes
                        break;
                    case "port":
                        metaData.port = Integer.parseInt(value);
                        break;
                    case "timestamp":
                        metaData.timestamp = Integer.parseInt(value);
                        break;
                    case "requestType":
                        metaData.requestType = RequestType.valueOf(value); // Adjust parsing if it's a custom type
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown field: " + key);
                }
            }

            return metaData;
        }
    }
}

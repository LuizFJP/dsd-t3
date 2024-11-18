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
import java.util.Collections;
import java.util.List;

public class Host extends Thread {
//   conectar o servidor e mandar o ip

    //   receber os ips
    ServerSocket server = new ServerSocket(176);
    String requestReceived;
    String[] requestToServer;

    BufferedReader input;
    PrintWriter output;
    private List<String> ipList = new ArrayList<>();
    private Afonso state = Afonso.RELEASED;

    public Host() throws IOException {

    }

    @Override
    public void run() {
        connectToServer();
        notifyIps();
    }

    public void notifyIps() {
        while (true) {
            try {
                Socket connection = server.accept();

                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                output = new PrintWriter(connection.getOutputStream(), true);

                System.out.println("Recebendo mensagem");

                requestReceived = input.readLine();

                ipList = Collections.singletonList(requestReceived);

                connection.close();

            } catch (Exception e) {
                output.print("Server fechou");
                try {
                    server.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
// linha que ele recebe os valores do servidor
//        linha que ele adiciona a list de ips
//        chama o metodo start()
    }

    public void connectToServer() {
            try (Socket conn = new Socket("localhost", 175)) {

                System.out.println("Conex�o estabelecida.");
                PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
var a = conn.getLocalAddress();
                out.println(conn.getLocalAddress());
            } catch (UnknownHostException e) {
                System.out.println("host n�o encontrado");
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    public void robson() {
        for (String ip : ipList) {
            try (Socket conn = new Socket(ip, 176)) {

                System.out.println("Conex�o estabelecida.");
                PrintWriter out = new PrintWriter(conn.getOutputStream(), true);
//                out.println(ipList);
                out.println(Arrays.asList("request"));
                input.readLine();
            } catch (UnknownHostException e) {
                System.out.println("host n�o encontrado");
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

        // aqui é o algoritmo do trabalho

        // requisicao pra saber se tá ok CRIAR CONEXão

//    mandar a requisição com o host e o timestamp
//    executar a seção crítica
// o proprio sender atualiza o timestamp
    // armazenar em um hashmap de host e timestamp

    // COMO RECEBER VARIAS REQUISIÇOES SEM A FECHAR A CONEXAO
    // COMO EU MANDO UMA REQUISICAO E RECEBO UMA REQUISICAO DE OUTRO R
}

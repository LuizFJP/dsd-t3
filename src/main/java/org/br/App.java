package org.br;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        new Server(175).start();
        Thread.sleep(1000);
        new Host(176).start();
        Thread.sleep(1000);
        new Host(177).start();
        Thread.sleep(1000);
        new Host(178).start();
        Thread.sleep(1000);
        new Host(179).start();
        Thread.sleep(1000);
        new Host(180).start();
    }
}

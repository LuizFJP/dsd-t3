package org.br;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        new Server().start();
        new Host().start();


    }
}

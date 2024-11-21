package org.br;

import java.io.IOException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author luizportela
 */
public class ServerApp {
    public static void main(String[] args) throws IOException {
        new Server(175).start();
    }
}

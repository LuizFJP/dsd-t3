package org.br;

import java.util.Scanner;

public class ActivateWanted extends Thread {
    private static ActivateWanted instance;
    private final StateCallback stateCallback;

    @Override
    public void run() {
        init();
    }

    public ActivateWanted(StateCallback stateCallback) {
        this.stateCallback = stateCallback;
    }

    public void init() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            var input = scanner.next();

            System.out.println("host com a porta: " + input + " pediu para entrar em wanted");
            stateCallback.changeStateToWanted(input);
        }
    }



    public interface StateCallback {
        void changeStateToWanted(String input);
    }
}

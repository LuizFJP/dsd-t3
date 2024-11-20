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

    public static synchronized ActivateWanted getInstance(StateCallback stateCallback) {
        if (instance == null) {
            instance = new ActivateWanted(stateCallback);
            instance.start();
        }
        return instance;
    }

    public void init() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            var input = scanner.next();
            System.out.println("host com a porta: " + input + " pediu para entrar em wanted");
            stateCallback.changeStateToWanted(input);

        }
    }


    public interface StateCallback {
        void changeStateToWanted(String input);
    }
}

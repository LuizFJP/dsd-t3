package org.br;

public class LamportClock {
    private int clock;

    public LamportClock() {
        this.clock = 0;
    }

    public void increment() {
        clock++;
    }

    public int getClock() {
        return clock;
    }

    public void update(int receivedTimestamp) {
        clock = Math.max(clock, receivedTimestamp) + 1;
    }

    public int send() {
        increment();
        return clock;
    }

    public void displayClock() {
        System.out.println("Current Lamport Clock Value: " + clock);
    }
}


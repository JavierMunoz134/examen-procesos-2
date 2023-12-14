package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeteoStationsPool {
    public static void main(String[] args) {
        int maxThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        for (int i = 0; i < maxThreads; i++){
            executor.execute(new MeteoStations());
        }
    }
}
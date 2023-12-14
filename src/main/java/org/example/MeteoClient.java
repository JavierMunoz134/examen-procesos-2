package org.example;

import org.example.constants.Constantes;
import redis.clients.jedis.Jedis;

import java.util.Scanner;
import java.util.Set;

public class MeteoClient {
    public static void main(String[] args) {
        String command = "";
        Scanner sc = new Scanner(System.in);
        try (Jedis jedis = new Jedis(Constantes.REDIS_SERVER_URI, Constantes.REDIS_SERVER_PORT)) {
            while (!command.equals("exit")) {
                System.out.print("Enter the command: ");
                String rawCommand = sc.nextLine() + " ";
                String[] commandSplit = rawCommand.split(" ", 2);
                command = commandSplit[0];
                switch (command) {
                    case "LASTID" -> {
                        String stationId = commandSplit[1];
                        String jedisHash = String.format(Constantes.HASH_KEY_LAST_TEMPERATURE_REDIS, Constantes.INICIALES_ALUMNO, stationId);
                        String temperature = jedis.hget(jedisHash, "temperature");
                        System.out.printf("Temperature for station %s: %s\n", stationId, temperature); // Agrega esta línea
                        System.out.printf("This is the last temperature recorded by station %s: %s\n", stationId, temperature);
                    }
                    case "MAXTEMP" -> {
                        if (commandSplit.length > 1) {
                            if (commandSplit[1].equals("ALL")) {
                                printMaxTemperatureForAllStations(jedis);
                            } else {
                                printMaxTemperatureForStation(jedis, commandSplit[1]);
                            }
                        } else {
                            System.out.println("Formato incorrecto. Uso: MAXTEMP ID o MAXTEMP ALL");
                        }
                    }
                    case "ALERTS" -> {
                        Set<String> keys = jedis.keys(Constantes.ALERTS_KEY_REDIS);
                        if (keys != null) {
                            for (String key : keys) {
                                String alert = jedis.get(key);
                                System.out.println(alert);
                                jedis.del(key);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void printMaxTemperatureForAllStations(Jedis jedis) {
        Set<String> stationKeys = jedis.keys(String.format("%s:LASTMEASUREMENT:*", Constantes.INICIALES_ALUMNO));
        if (stationKeys != null && !stationKeys.isEmpty()) {
            for (String stationKey : stationKeys) {
                String stationId = stationKey.split(":")[2];
                Float maxTemperature = getMaxTemperatureForStation(jedis, stationId);
                System.out.printf("Temperatura máxima para la estación %s: %.2f\n", stationId, maxTemperature);
            }
        } else {
            System.out.println("No hay estaciones registradas.");
        }
    }

    private static void printMaxTemperatureForStation(Jedis jedis, String stationId) {
        Float maxTemperature = getMaxTemperatureForStation(jedis, stationId);
        if (maxTemperature != null) {
            System.out.printf("Temperatura máxima para la estación %s: %.2f\n", stationId, maxTemperature);
        } else {
            System.out.printf("No hay datos para la estación %s.\n", stationId);
        }
    }

    private static Float getMaxTemperatureForStation(Jedis jedis, String stationId) {
        String jedisHash = String.format(Constantes.HASH_KEY_LAST_TEMPERATURE_REDIS, Constantes.INICIALES_ALUMNO, stationId);
        String temperature = jedis.hget(jedisHash, "temperature");
        if (temperature != null) {
            return Float.parseFloat(temperature);
        }
        return null;
    }
}

package org.example;

import org.example.constants.Constantes;
import org.eclipse.paho.client.mqttv3.*;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class MeteoServer {
    public static void main(String[] args) {
        String publisherId = UUID.randomUUID().toString();
        Jedis jedis = new Jedis(Constantes.REDIS_SERVER_URI, Constantes.REDIS_SERVER_PORT);
        MqttClient client = null;

        try {
            client = new MqttClient(Constantes.MQTT_SERVER_URI, publisherId);
            Constantes.connectMqttClient(client);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection to Solace broker lost! " + throwable.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) {
                    try {
                        handleMqttMessage(topic, new String(mqttMessage.getPayload()), jedis);
                    } catch (Exception e) {
                        e.printStackTrace(); // Manejo de errores apropiado
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    // Este método se llama cuando un mensaje se ha entregado completamente al servidor
                }
            });

            client.subscribe(Constantes.subscribeAllStationsTopics, 0);

            // Mantén el programa en ejecución
            while (true) {
                // Puedes hacer cualquier otra operación necesaria aquí
                // Por ejemplo, puedes agregar un retraso para evitar un bucle infinito sin pausas.
                Thread.sleep(500);
            }

        } catch (MqttException | InterruptedException e) {
            e.printStackTrace(); // Manejo de errores apropiado
        } finally {
            // Cierra Jedis
            if (jedis != null) {
                jedis.close();
            }

            // Cierra MqttClient
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace(); // Manejo de errores apropiado
                }
            }
        }
    }

    private static void handleMqttMessage(String topic, String payload, Jedis jedis) {
        String[] topicSplit = topic.substring(1).split("/");
        String jedisHash = String.format(Constantes.HASH_KEY_LAST_TEMPERATURE_REDIS, Constantes.INICIALES_ALUMNO, topicSplit[2]);
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        // Obtener la temperatura de la cadena
        String[] payloadParts = payload.split("#");
        if (payloadParts.length < 3) {
            // Manejar el error, la cadena no tiene el formato esperado
            System.err.println("Error: El payload no tiene el formato esperado");
            return;
        }

        // Modificamos esta línea para reemplazar la coma por el punto en la cadena de temperatura
        String temperatureString = payloadParts[2].replace(',', '.');

        try {
            float degrees = Float.parseFloat(temperatureString);

            jedis.hset(jedisHash, "datetime", dateTime);
            jedis.hset(jedisHash, "temperature", temperatureString);

            if (degrees > 30f || degrees < 0f) {
                // Actualiza la cadena de formato para incluir la fecha y la hora
                String alertMessage = String.format(Constantes.FORMAT_ALERT_STRING, LocalDate.now(), LocalTime.now(), topicSplit[2]);
                jedis.set(Constantes.ALERTS_KEY_REDIS, alertMessage);
            }
        } catch (NumberFormatException e) {
            // Manejar el error de conversión a flotante
            System.err.println("Error al convertir la temperatura a flotante: " + e.getMessage());
        }

    }







}

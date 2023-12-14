package org.example;

import org.example.constants.Constantes;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class MeteoStations extends Thread {
    public String uniqueID = "ID";
    private static int uniqueNumber = 0;

    public MeteoStations() {
        uniqueID += uniqueNumber;
        uniqueNumber++;
        setName(uniqueID);
    }
    @Override
    public void run() {
        String publisherId = UUID.randomUUID().toString();
        MqttClient client = null;

        try {
            client = new MqttClient(Constantes.MQTT_SERVER_URI, publisherId);
            Constantes.connectMqttClient(client);

            while (true) {
                MqttMessage message = new MqttMessage(createMqttMsg());
                client.publish(createMqttTopic(), message);
                sleep(5000);
            }
        } catch (MqttException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private float generateTemperature() {
        float min = -10.0f;
        float max = 40.0f;
        return new Random().nextFloat() * (max - min) + min;
    }

    public byte[] createMqttMsg() {
        float temperature = generateTemperature();
        String msg = String.format("%s#%s#%.2f", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), temperature);

        // Reemplaza la coma por el punto en la cadena de temperatura
        msg = msg.replace(',', '.');

        return msg.getBytes();
    }


    private String createMqttTopic() {
        return String.format("/%s/METEO/%s/MEASUREMENTS", Constantes.INICIALES_ALUMNO, uniqueID);
    }

    // Agrega este método para obtener el ID único
    public String getUniqueID() {
        return uniqueID;
    }
}

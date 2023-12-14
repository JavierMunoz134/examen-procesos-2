package org.example.constants;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Constantes {
    public static final String MQTT_SERVER_URI = "tcp://10.2.1.91:1883";
    public static final String REDIS_SERVER_URI = "184.73.34.167";
    public static final int REDIS_SERVER_PORT = 6379;
    public static final String INICIALES_ALUMNO = "JMS";
    public static final String subscribeAllStationsTopics = "/JMS/METEO/#";
    public static final String ALERTS_KEY_REDIS = "JMS:ALERTS";
    public static final String FORMAT_ALERT_STRING = "Alerta por temperaturas extremas el %s a las %s en la estación %s";
    public static final String HASH_KEY_LAST_TEMPERATURE_REDIS = "%s:LASTMEASUREMENT:%s";

    public static void connectMqttClient(MqttClient client) throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        client.connect(options);
    }
}


Meteo Monitoring System
Este es un sistema de monitoreo meteorológico (Meteo Monitoring System) implementado en Java, que consta de tres componentes principales:

MeteoStations:

MeteoStations es una clase que simula estaciones meteorológicas. Cada instancia de esta clase representa una estación con un identificador único (uniqueID), que se genera automáticamente y se utiliza para construir el tema MQTT y la clave Redis asociados con esa estación.
La estación publica regularmente mediciones de temperatura en un tema MQTT específico y guarda la última temperatura en una base de datos Redis.
Las temperaturas se generan aleatoriamente entre -10.0 y 40.0 grados Celsius.
MeteoServer:

MeteoServer es una aplicación que actúa como servidor MQTT. Utiliza la biblioteca Eclipse Paho para conectarse a un servidor MQTT y subscribirse a todos los temas de las estaciones meteorológicas (subscribeAllStationsTopics).
Cuando recibe un mensaje MQTT, maneja la información, actualiza la base de datos Redis con la última temperatura y emite alertas si la temperatura es extremadamente alta o baja.
MeteoClient:

MeteoClient es una interfaz de línea de comandos que permite a los usuarios interactuar con el sistema. Permite comandos como consultar la última temperatura de una estación específica (LASTID), obtener la temperatura máxima de una estación o de todas las estaciones (MAXTEMP), y verificar alertas de temperaturas extremas (ALERTS).
MeteoStationsPool:

MeteoStationsPool es una aplicación que crea un conjunto de hilos (MeteoStations) para simular múltiples estaciones meteorológicas que generan mediciones de temperatura de manera concurrente.
Requisitos
Java 8 o superior
Servidor MQTT
Servidor Redis
Configuración
Asegúrate de tener configurados los siguientes parámetros en la clase Constantes:

MQTT_SERVER_URI: URI del servidor MQTT.
REDIS_SERVER_URI: URI del servidor Redis.
REDIS_SERVER_PORT: Puerto del servidor Redis.
INICIALES_ALUMNO: Iniciales del alumno para construir temas y claves únicas.
Uso
Compila todas las clases.
Inicia MeteoServer para establecer la conexión MQTT y Redis.
Inicia MeteoStationsPool para simular múltiples estaciones.
Inicia MeteoClient para interactuar con el sistema mediante comandos.
Comandos de MeteoClient
LASTID <ID>: Muestra la última temperatura registrada por la estación con el ID especificado.
MAXTEMP <ID | ALL>: Muestra la temperatura máxima para una estación específica o para todas las estaciones.
ALERTS: Muestra y limpia las alertas por temperaturas extremas.
Para salir, ingresa el comando exit.
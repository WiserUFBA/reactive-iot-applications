/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.soft_iot.publisher.util;

import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cleberlira
 */
public class MqttClientUtil {
   public static final String BROKER_HOST = "localhost";
   public static final int BROKER_PORT = 1883;
   private static MqttClient mqttClient;

    public static MqttClient getMqttClientUtil() {
        if (mqttClient == null) {
            CountDownLatch latch = new CountDownLatch(1);
            mqttClient = MqttClient.create(Vertx.vertx()).connect(BROKER_PORT, BROKER_HOST, res -> {
                if (res.succeeded()) System.out.println("connected");
                latch.countDown();
            });

            try { latch.await(10000, TimeUnit.MILLISECONDS); }
            catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        return mqttClient;
    }
}

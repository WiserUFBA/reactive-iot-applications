package br.ufba.dcc.wiser.soft_iot.publisher;

import java.util.List;

//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
//import org.eclipse.paho.client.mqttv3.MqttSecurityException;
//import org.osgi.service.blueprint.container.ServiceUnavailableException;
import br.ufba.dcc.wiser.soft_iot.model.Device;
import br.ufba.dcc.wiser.soft_iot.model.Sensor;
import br.ufba.dcc.wiser.soft_iot.publisher.util.MqttClientUtil;
import br.ufba.dcc.wiser.soft_iot.publisher.util.tatu.TATUWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.mqtt.MqttQoS;
//import br.ufba.dcc.wiser.soft_iot.mapping_devices.Controller;
//import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.MqttConnectionException;
import io.vertx.mqtt.MqttException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import javax.naming.ServiceUnavailableException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class MqttPublisherController extends AbstractVerticle {

    private String brokerUrl;
    private String brokerPort;
    private String serverId;
    private String username;
    private String password;
    private int defaultCollectionTime;
    private int defaultPublishingTime;
    //  private MqttClient publisher;
    private boolean debugModeValue;
    private List<Device> listDevices;
    private String enderecoBarramento;

    private MqttClient publisher;
    private MqttClientOptions mqttOptions;

    public MqttPublisherController() {
    }

    private void deserializedMessage(String message) {
        Gson gson = new Gson();

        Type devicesListType = new TypeToken<ArrayList<Device>>() {
        }.getType();
        listDevices = gson.fromJson(message, devicesListType);

      //  System.out.println("Sending FLOW messages:");
        sendFlowRequestBySensorDevice();

    }

    @Override
    public void start() {

        this.mqttOptions = new MqttClientOptions();

        if (!this.username.isEmpty()) {
            mqttOptions.setUsername(this.username);
        }
        if (!this.password.isEmpty()) {
            mqttOptions.setPassword(this.password);
        }

        this.publisher = MqttClientUtil.getMqttClientUtil();

        try {

            BundleContext context = FrameworkUtil.getBundle(MqttPublisherController.class).getBundleContext();

            final ServiceReference eventBusRef = context.getServiceReference("io.vertx.core.eventbus.EventBus");

            EventBus eb = (EventBus) context.getService(eventBusRef);

            MessageConsumer<String> consumer = eb.consumer(enderecoBarramento);

            consumer.handler(message -> {

                deserializedMessage(message.body());

            });
        } catch (Exception d) {
            d.printStackTrace();

        }

    }

    public List<Device> getListDevices() {
        return listDevices;
    }

    public void sendFlowRequestBySensorDevice() {
        try {
            for (Device device : listDevices) {

                List<Sensor> sensors = device.getSensors();
                for (Sensor sensor : sensors) {
                    String flowRequest;
                    if (sensor.getCollection_time() <= 0) {
                        flowRequest = TATUWrapper.getTATUFlowValue(sensor.getId(), 2000, 3000);
                    } else {
                        flowRequest = TATUWrapper.getTATUFlowValue(sensor.getId(), sensor.getCollection_time(), sensor.getPublishing_time());
                    }
                 //   System.out.println("[topic: " + device.getId() + "] " + flowRequest);
                    publishTATUMessage(flowRequest, device.getId());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishTATUMessage(String msg, String topicName) {

        Buffer buffer = Buffer.buffer(msg.getBytes());

        String topic = TATUWrapper.topicBase + topicName;

        try {
         //   this.publisher = MqttClientUtil.getMqttClientUtil();

            publisher.publish(topic, buffer, MqttQoS.AT_MOST_ONCE, false, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //   private void publishTATUMessage(String msg, String topicName) {
//        MqttMessage mqttMsg = new MqttMessage();
//        mqttMsg.setPayload(msg.getBytes());
//        String topic = TATUWrapper.topicBase + topicName;
//        try {
//            publisher.publish(topic, mqttMsg);
//        } catch (MqttPersistenceException e) {
//            e.printStackTrace();
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//
//    }
    public void destroy() {
        //   this.publisher.disconnect(); // TODO Auto-generated catch block
    }

    private void printlnDebug(String str) {
        if (debugModeValue) {
            System.out.println(str);
        }
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public void setBrokerPort(String brokerPort) {
        this.brokerPort = brokerPort;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
//
//	public Controller getFotDevices() {
//		return fotDevices;
//	}
//
//	public void setFotDevices(Controller fotDevices) {
//		this.fotDevices = fotDevices;
//	}

    public void setDefaultCollectionTime(int defaultCollectionTime) {
        this.defaultCollectionTime = defaultCollectionTime;
    }

    public void setDefaultPublishingTime(int defaultPublishingTime) {
        this.defaultPublishingTime = defaultPublishingTime;
    }

    public void setDebugModeValue(boolean debugModeValue) {
        this.debugModeValue = debugModeValue;
    }

    /**
     * @param listDevices the listDevices to set
     */
    public void setListDevices(List<Device> listDevices) {
        this.listDevices = listDevices;
    }

    /**
     * @return the enderecoBarramento
     */
    public String getEnderecoBarramento() {
        return enderecoBarramento;
    }

    /**
     * @param enderecoBarramento the enderecoBarramento to set
     */
    public void setEnderecoBarramento(String enderecoBarramento) {
        this.enderecoBarramento = enderecoBarramento;
    }

}

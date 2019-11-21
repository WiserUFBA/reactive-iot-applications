package br.ufba.dcc.wiser.iot_reactive.reactive.local.storage;

import br.ufba.dcc.wiser.iot_reactive.localstorage.util.MqttClientUtil;
import br.ufba.dcc.wiser.iot_reactive.model.Device;
import br.ufba.dcc.wiser.iot_reactive.model.Sensor;
import br.ufba.dcc.wiser.iot_reactive.model.SensorData;
import br.ufba.dcc.wiser.iot_reactive.storage.util.tatu.TATUWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.MqttException;
import io.vertx.mqtt.messages.MqttMessage;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.naming.ServiceUnavailableException;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class MqttH2StorageController extends AbstractVerticle {

    private String brokerUrl;
    private String brokerPort;
    private String serverId;
    private String username;
    private String password;
    private String fusekiURI;
    private String baseURI;
    private MqttClient subscriber;
    private DataSource dataSource;
    private String numOfHoursDataStored;
    private MqttClientOptions mqttOptions;

    private String enderecoBarramento;

    private List<Device> listDevices;
    //private Controller fotDevices;
    private int defaultCollectionTime;
    private int defaultPublishingTime;
    private boolean debugModeValue;

    private void deserializedMessage(String message) {
        Gson gson = new Gson();

        Type devicesListType = new TypeToken<ArrayList<Device>>() {
        }.getType();

        listDevices = gson.fromJson(message, devicesListType);

        System.out.println("subscribing in topics:");
        subscribeDevicesTopics(listDevices);

        this.subscriber.publishHandler(hndlr -> {
            System.out.println("There are new message in topic: " + hndlr.topicName());
            System.out.println("Content(as string) of the message: " + hndlr.payload().toString());
            System.out.println("QoS: " + hndlr.qosLevel());

        }).subscribe("/dev/sc01", 1);

    }

    @Override
    public void start() {

        this.mqttOptions = new MqttClientOptions();

        try {
            if (!this.username.isEmpty()) {
                mqttOptions.setUsername(this.username);
            }
            if (!this.password.isEmpty()) {
                mqttOptions.setPassword(this.password);
            }

            this.subscriber = MqttClientUtil.getMqttClientUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            BundleContext context = FrameworkUtil.getBundle(MqttH2StorageController.class).getBundleContext();

            final ServiceReference eventBusRef = context.getServiceReference("io.vertx.core.eventbus.EventBus");

            EventBus eb = (EventBus) context.getService(eventBusRef);

            MessageConsumer<String> consumer = eb.consumer(enderecoBarramento);

            consumer.handler(message -> {

                deserializedMessage(message.body());

            });
        } catch (Exception d) {
            d.printStackTrace();

        }

      
        // TODO Auto-generated catch block
//		try {
//			Connection dbConnection = this.dataSource.getConnection();
//			Statement stmt = dbConnection.createStatement();
//			//stmt.execute("drop table sensor_data");
//			//stmt.execute("drop table semantic_registered_last_time_sensors");
//			//stmt.execute("drop table aggregation_registered_last_time_sensors");
//			DatabaseMetaData dbMeta = dbConnection.getMetaData();
//			printlnDebug("Using datasource "
//					+ dbMeta.getDatabaseProductName() + ", URL "
//					+ dbMeta.getURL());
//			stmt.execute("CREATE TABLE IF NOT EXISTS sensor_data(ID BIGINT AUTO_INCREMENT PRIMARY KEY, sensor_id VARCHAR(255),"
//					+ " device_id VARCHAR(255), data_value VARCHAR(255), start_datetime TIMESTAMP, end_datetime TIMESTAMP, aggregation_status INT DEFAULT 0)");
//			
//			stmt.execute("CREATE TABLE IF NOT EXISTS semantic_registered_last_time_sensors(sensor_id VARCHAR(255),"
//					+ " device_id VARCHAR(255), last_time TIMESTAMP)");
//			
//			stmt.execute("CREATE TABLE IF NOT EXISTS aggregation_registered_last_time_sensors(sensor_id VARCHAR(255),"
//					+ " device_id VARCHAR(255), last_time TIMESTAMP)");
//			
        /*ResultSet rs = stmt.executeQuery("SELECT * FROM sensor_data");
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                writeResult(rs, meta.getColumnCount());
            }*/
 /*
			rs = stmt.executeQuery("CALL DISK_SPACE_USED('sensor_data')");
			meta = rs.getMetaData();
            while (rs.next()) {
                writeResult(rs, meta.getColumnCount());
            }*/
//            dbConnection.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }

    private void subscribeDevicesTopics(List<Device> devices) {

        System.out.println("aqui " + devices.size());
        //this.subscriber.subscribe("CONNECTED", 1);
        for (Device device : devices) {
            System.out.println(TATUWrapper.topicBase + device.getId());
            this.subscriber.subscribe(TATUWrapper.topicBase + device.getId(), 1);
        }
    }

    private void writeResult(ResultSet rs, int columnCount) throws SQLException {
        for (int c = 1; c <= columnCount; c++) {
            System.out.print(rs.getString(c) + ", ");
        }
        System.out.println();
    }

    public void disconnect() {
        this.subscriber.disconnect(); // TODO Auto-generated catch block
    }

//	public void connectionLost(Throwable arg0) {
//		printlnDebug("connectionLost...trying to reconnect...");
//		MqttConnectOptions connOpt = new MqttConnectOptions();
//		try {
//			if (!this.username.isEmpty())
//				connOpt.setUserName(this.username);
//			if (!this.password.isEmpty())
//				connOpt.setPassword(this.password.toCharArray());
//			long unixTime = System.currentTimeMillis() / 1000L;
//			this.subscriber = new MqttClient(this.brokerUrl + ":"
//					+ this.brokerPort, this.serverId + unixTime);
//			this.subscriber.setCallback(this);
//			this.subscriber.connect(connOpt);
//		//	subscribeDevicesTopics(fotDevices.getListDevices());
//
//		} catch (MqttException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.exit(-1);
//		}catch (ServiceUnavailableException e) {
//			e.printStackTrace();
//		}
//
//	}
//	public synchronized void messageArrived(final String topic,
//			final MqttMessage message) throws Exception {
//		new Thread(new Runnable() {
//			public void run() {
//				
//				String messageContent = new String(message.getPayload());
//				printlnDebug("topic: " + topic + "message: " + messageContent);
//				if(TATUWrapper.isValidTATUAnswer(messageContent)){
//					try{	
//						String deviceId = TATUWrapper.getDeviceIdByTATUAnswer(messageContent);
//					Device device = getDeviceById(deviceId);
//						
//						String sensorId = TATUWrapper.getSensorIdByTATUAnswer(messageContent);
//						Sensor sensor = device.getSensorbySensorId(sensorId);
//						Date date = new Date();
//						List<SensorData> listSensorData = TATUWrapper.parseTATUAnswerToListSensorData(messageContent,device,sensor,date);
//						printlnDebug("answer received: device: " + deviceId +  " - sensor: " + sensor.getId() + " - number of data sensor: " + listSensorData.size());
//						storeSensorData(listSensorData, device);
//					}
//					catch (Exception e) {
//						e.printStackTrace();
//					}
//				}else if(topic.contentEquals("CONNECTED")){
//					printlnDebug("Resending FLOW request for device: " + messageContent);
//					try {
//						Thread.sleep(2000);
//						Device device = getDeviceById(messageContent);
//						if(device != null)
//							sendFlowRequest(device);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (ServiceUnavailableException e) {
//						e.printStackTrace();
//					}
//					
//				}
//			}
//		}).start();
//	}
    public Device getDeviceById(String deviceId) {
        for (Device device : getListDevices()) {
            if (device.getId().contentEquals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    private void sendFlowRequest(Device device) {
        try {
            if (device != null) {
                List<Sensor> sensors = device.getSensors();
                for (Sensor sensor : sensors) {
                    String flowRequest;
                    if (sensor.getCollection_time() <= 0) {
                        flowRequest = TATUWrapper.getTATUFlowValue(sensor.getId(), defaultCollectionTime, defaultPublishingTime);
                    } else {
                        flowRequest = TATUWrapper.getTATUFlowValue(sensor.getId(), sensor.getCollection_time(), sensor.getPublishing_time());
                    }
                    printlnDebug("[topic: " + device.getId() + "] " + flowRequest);
                    //publishTATUMessage(flowRequest, device.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//	private void publishTATUMessage(String msg, String topicName){
//		 Buffer buffer = Buffer.buffer(msg.getBytes());
//
//                String topic = TATUWrapper.topicBase + topicName;
//		try {
//			subscriber.publish(topic, mqttMsg);
//		} catch (Exception e) {
//			e.printStackTrace();
//                }
//		
//	}
//	
//	private void storeSensorData(List<SensorData> listSensorData, Device device){
//		try {
//			Connection dbConn = this.dataSource.getConnection();
//			Statement stmt = dbConn.createStatement();
//			for(SensorData sensorData : listSensorData){
//				String sensorId = sensorData.getSensor().getId();
//				Timestamp startDateTime = new Timestamp(sensorData.getStartTime().getTime());
//				Timestamp endDateTime = new Timestamp(sensorData.getEndTime().getTime());
//				boolean result = stmt.execute("INSERT INTO sensor_data (sensor_id, device_id, data_value, start_datetime, end_datetime) values "
//						+ "('"+ sensorId + "', '" + device.getId() +"', '" + sensorData.getValue() + "' ,'" + startDateTime
//						+ "', '" + endDateTime + "')");
//				if(result){
//					printlnDebug("cannot insert data:" + "('"+ sensorId + "', '" + device.getId() +"', '" + sensorData.getValue() + "' ,'" + startDateTime
//							+ "', '" + endDateTime + "')");
//				}
//			}
//			dbConn.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		
//	}
    public void cleanOldData() {
        printlnDebug("clean old data...");
        Connection dbConn;
        try {
            dbConn = this.dataSource.getConnection();
            Statement stmt = dbConn.createStatement();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, (-1) * Integer.parseInt(this.numOfHoursDataStored));
            Date date = cal.getTime();
            String strDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            stmt.execute("DELETE FROM sensor_data WHERE end_datetime <= '" + strDate + "' AND aggregation_status = 0");
            dbConn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public void setFusekiURI(String fusekiURI) {
        this.fusekiURI = fusekiURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public String getFusekiURI() {
        return fusekiURI;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setnumOfHoursDataStored(String numOfHoursDataStored) {
        this.numOfHoursDataStored = numOfHoursDataStored;
    }

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
     * @return the listDevices
     */
    public List<Device> getListDevices() {
        return listDevices;
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
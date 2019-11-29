package br.ufba.dcc.wiser.iot_reactive.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;



public class Device {
	private String id;
	private double latitude;
	private double longitude;
	@JsonIgnoreProperties("device")
	private List<Sensor> sensors;
	
	public Device(){
		
	}
	
	public Sensor getSensorbySensorId(String sensorId){
            
            System.out.println("tamanho " + sensors.size());
		for(Sensor sensor : sensors){
                    
                                System.out.println("sensor.getId() " + sensor.getId());
                                System.out.println("sensorId " + sensorId);
   
			if(sensor.getId().contentEquals(sensorId))
				return sensor;
		}
		return null;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	
}

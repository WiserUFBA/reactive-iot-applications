/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufba.dcc.wiser.iot_reactive.model;


import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author cleberlira
 */

public class SensorData {
    private String value;
    private Device device;
    private Sensor sensor;
    private LocalDateTime localDateTime;
    private Date startTime;
    private Date endTime;
    private long delay;

    
    public SensorData(String value, LocalDateTime localDateTime, 
            Sensor sensor, Device device, long delay){
        this.value = value;
        this.localDateTime = localDateTime;
        this.sensor = sensor;
        this.device = device;
        this.delay = delay;
    }
    
    
//     public SensorData(String value, LocalDateTime localDateTime, 
//            Sensor sensor, Device device, long delay){
//        this.value = value;
//        this.localDateTime = localDateTime;
//        this.sensor = sensor;
//        this.device = device;
//        this.delay = delay;
//    }
    
     //utilizado para o sensor físico
    public SensorData( Device device,  Sensor sensor, String value, Date startTime,
			Date endTime){
        this.value = value;
      //  this.localDateTime = localDateTime;
        this.sensor = sensor;
        this.device = device;
        this.startTime = startTime;
        this.endTime = endTime;
        
    }
    
    public SensorData(String value, Sensor sensor, Device device){
        this.value = value;
        this.sensor = sensor;
        this.device = device;
    }
    
    
    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    /**
     * @param device the device to set
     */
    public void setDevice(Device device) {
        this.device = device;
    }

    /**
     * @param sensor the sensor to set
     */
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
    
    public Sensor getSensor() {
		return sensor;
	}

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    
    
    
	
}
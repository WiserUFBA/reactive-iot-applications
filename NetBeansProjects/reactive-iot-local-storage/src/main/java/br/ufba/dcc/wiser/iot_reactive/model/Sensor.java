package br.ufba.dcc.wiser.iot_reactive.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class Sensor {
	private String id;
	private String type;
	private int collection_time;
	private int publishing_time;
	
	
	public Sensor(){
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public int getCollection_time() {
		return collection_time;
	}

	public void setCollection_time(int collection_time) {
		this.collection_time = collection_time;
	}

	public int getPublishing_time() {
		return publishing_time;
	}

	public void setPublishing_time(int publishing_time) {
		this.publishing_time = publishing_time;
	}
	
	
	
	
}

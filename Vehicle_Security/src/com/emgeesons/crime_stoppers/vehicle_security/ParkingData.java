package com.emgeesons.crime_stoppers.vehicle_security;

public class ParkingData {
	
	private String vehicle_id,vehicle_model, lat, lon, comm, check, type;

	public ParkingData(String vehicle_id, String vehicle_model, String lat,
			String lon, String comm, String check, String type) {
		this.vehicle_id = vehicle_id;
		this.vehicle_model = vehicle_model;
		this.lat = lat;
		this.lon = lon;
		this.comm = comm;
		this.check = check;
		this.type = type;

	}

	public String getvehicle_id() {
		return this.vehicle_id;
	}

	public void setvehicle_id(String vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public String getvehicle_model() {
		return this.vehicle_model;
	}

	public void setvehicle_model(String vehicle_model) {
		this.vehicle_model = vehicle_model;
	}

	public String getlat() {
		return this.lat;
	}

	public void setlat(String lat) {
		this.lat = lat;
	}

	public String getlon() {
		return this.lon;
	}

	public void setlon(String lon) {
		this.lon = lon;
	}

	public String getcomm() {
		return this.comm;
	}

	public void setcomm(String comm) {
		this.comm = comm;
	}

	public String getcheck() {
		return this.check;
	}

	public void setcheck(String check) {
		this.check = check;
	}

	public String gettype() {
		return this.type;
	}

	public void settype(String type) {
		this.type = type;
	}
}

package com.emgeesons.crime_stoppers.vehicle_security;

public class VehicleData {
	int vehicle_id;
	private String vehicle_make, vehicle_type, vehicle_body, vehicle_model,
			vehicle_eng, vehicle_ch, vehicle_colour, vehicle_acc, vehicle_reg,
			vehicle_insname, vehicle_insno, vehicle_insexp, status, expmi,
			inumber, state;

	public VehicleData(int vehicle_id, String vehicle_type,
			String vehicle_make, String vehicle_model, String vehicle_body,
			String vehicle_eng, String vehicle_ch, String vehicle_colour,
			String vehicle_acc, String vehicle_reg, String vehicle_insname,
			String vehicle_insno, String vehicle_insexp, String status,
			String expmi, String inumber, String state) {
		this.vehicle_id = vehicle_id;
		this.vehicle_type = vehicle_type;
		this.vehicle_make = vehicle_make;
		this.vehicle_model = vehicle_model;
		this.vehicle_body = vehicle_body;
		this.vehicle_eng = vehicle_eng;
		this.vehicle_ch = vehicle_ch;
		this.vehicle_colour = vehicle_colour;
		this.vehicle_acc = vehicle_acc;
		this.vehicle_reg = vehicle_reg;
		this.vehicle_insname = vehicle_insname;
		this.vehicle_insno = vehicle_insno;
		this.vehicle_insexp = vehicle_insexp;
		this.status = status;
		this.expmi = expmi;
		this.inumber = inumber;
		this.state = state;

	}

	public String getstate() {
		return this.state;
	}

	public String getinumber() {
		return this.inumber;
	}

	public int getvehicle_id() {
		return this.vehicle_id;
	}

	public void setuserid(int vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public String getvehicle_type() {
		return this.vehicle_type;
	}

	public void setvehicle_type(String vehicle_type) {
		this.vehicle_type = vehicle_type;
	}

	public String getvehicle_make() {
		return this.vehicle_make;
	}

	public void setvehicle_make(String vehicle_make) {
		this.vehicle_make = vehicle_make;
	}

	public String getvehicle_model() {
		return this.vehicle_model;
	}

	public void setvehicle_model(String vehicle_model) {
		this.vehicle_model = vehicle_model;
	}

	public String getvehicle_body() {
		return this.vehicle_body;
	}

	public void setvehicle_body(String vehicle_body) {
		this.vehicle_body = vehicle_body;
	}

	public String getvehicle_eng() {
		return this.vehicle_eng;
	}

	public void setvehicle_eng(String vehicle_eng) {
		this.vehicle_eng = vehicle_eng;
	}

	public String getvehicle_ch() {
		return this.vehicle_ch;
	}

	public void setlName(String vehicle_ch) {
		this.vehicle_ch = vehicle_ch;
	}

	public String getvehicle_colour() {
		return this.vehicle_colour;
	}

	public void setemail(String vehicle_colour) {
		this.vehicle_colour = vehicle_colour;
	}

	public String getvehicle_acc() {
		return this.vehicle_acc;
	}

	public void setvehicle_acc(String vehicle_acc) {
		this.vehicle_acc = vehicle_acc;
	}

	public String getvehicle_reg() {
		return this.vehicle_reg;
	}

	public void setvehicle_reg(String vehicle_reg) {
		this.vehicle_reg = vehicle_reg;
	}

	public String getvehicle_insname() {
		return this.vehicle_insname;
	}

	public void setgender(String vehicle_insname) {
		this.vehicle_insname = vehicle_insname;
	}

	public String getvehicle_insno() {
		return this.vehicle_insno;
	}

	public void setlicenseNo(String vehicle_insno) {
		this.vehicle_insno = vehicle_insno;
	}

	public String getvehicle_insexp() {
		return this.vehicle_insexp;
	}

	public void setstreet(String vehicle_insexp) {
		this.vehicle_insexp = vehicle_insexp;
	}

	public String getstatus() {
		return this.status;
	}

	public void setstatus(String status) {
		this.status = status;
	}

	public String getexpmil() {
		return this.expmi;
	}

	public void setexpmi(String expmi) {
		this.expmi = expmi;
	}
}

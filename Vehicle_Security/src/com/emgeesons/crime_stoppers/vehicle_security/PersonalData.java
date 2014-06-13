package com.emgeesons.crime_stoppers.vehicle_security;

public class PersonalData {
	private String user_id, fName, lName, email, mobileNumber, dob, gender,
			licenseNo, street, address, postcode, dtModified, fbId, fbToken,
			cname, cnumber, pin, squs, sans, spoints;

	public PersonalData(String user_id, String fName, String lName,
			String email, String mobileNumber, String dob, String gender,
			String licenseNo, String street, String address, String postcode,
			String dtModified, String fbId, String fbToken, String cname,
			String cnumber, String pin, String squs, String sans, String spoints) {
		this.user_id = user_id;
		this.fName = fName;
		this.lName = lName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.dob = dob;
		this.gender = gender;
		this.licenseNo = licenseNo;
		this.street = street;
		this.address = address;
		this.postcode = postcode;
		this.dtModified = dtModified;
		this.fbId = fbId;
		this.fbToken = fbToken;
		this.cname = cname;
		this.cnumber = cnumber;
		this.pin = pin;
		this.squs = squs;
		this.sans = sans;
		this.spoints = spoints;

	}

	public String getuserid() {
		return this.user_id;
	}

	public void setuserid(String name) {
		this.user_id = name;
	}

	public String getfName() {
		return this.fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return this.lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getemail() {
		return this.email;
	}

	public void setemail(String email) {
		this.email = email;
	}

	public String getmobileNumber() {
		return this.mobileNumber;
	}

	public void setmobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getdob() {
		return this.dob;
	}

	public void setdob(String dob) {
		this.dob = dob;
	}

	public String getgender() {
		return this.gender;
	}

	public void setgender(String gender) {
		this.gender = gender;
	}

	public String getlicenseNo() {
		return this.licenseNo;
	}

	public void setlicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getstreet() {
		return this.street;
	}

	public void setstreet(String street) {
		this.street = street;
	}

	public String getaddress() {
		return this.address;
	}

	public void address(String address) {
		this.address = address;
	}

	public String getpostcode() {
		return this.postcode;
	}

	public void setpostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getdtModified() {
		return this.dtModified;
	}

	public void setdtModified(String dtModified) {
		this.dtModified = dtModified;
	}

	public String getfbId() {
		return this.fbId;
	}

	public void setfbId(String fbId) {
		this.fbId = fbId;
	}

	public String getfbToken() {
		return this.fbToken;
	}

	public void setfbToken(String fbToken) {
		this.fbToken = fbToken;
	}

	public String getcname() {
		return this.cname;
	}

	public void setcname(String cname) {
		this.cname = cname;
	}

	public String getcnumber() {
		return this.cnumber;
	}

	public void setcnumber(String cnumber) {
		this.cnumber = cnumber;
	}

	public String getpin() {
		return this.pin;
	}

	public void setpin(String pin) {
		this.pin = pin;
	}

	public String getsqus() {
		return this.squs;
	}

	public void setqus(String squs) {
		this.squs = squs;
	}

	public String getsans() {
		return this.sans;
	}

	public void setsans(String sans) {
		this.sans = sans;
	}

	public String getspoints() {
		return this.spoints;
	}

	public void setspoints(String spoints) {
		this.spoints = spoints;
	}
}

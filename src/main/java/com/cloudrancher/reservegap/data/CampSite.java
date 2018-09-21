package com.cloudrancher.reservegap.data;

public class CampSite {

	public int id;
	public String name;
	
	public CampSite() {
		
	}
	public CampSite(int id, String name) {
		this.id = id;
		this.name =name;
	}
	
	@Override
	public String toString() {
		return "CampSite { id: "+id+", name: "+name+" }";
	}
}

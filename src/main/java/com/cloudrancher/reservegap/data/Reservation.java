package com.cloudrancher.reservegap.data;

import java.time.LocalDate;

public class Reservation {
	public int campsiteId;
	public LocalDate startDate;
	public LocalDate endDate;
	
	public Reservation() {}
	
	public Reservation(int campsiteId, LocalDate startDate, LocalDate endDate) {
		this.campsiteId = campsiteId;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	@Override
	public String toString() {
		return "Reservation { campsiteId: "+campsiteId+", startDate: "+startDate+", endDate: "+endDate+" } ";
	}
}

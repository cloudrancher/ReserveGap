package com.cloudrancher.reservegap;

import java.util.List;

import com.cloudrancher.reservegap.data.Reservation;

public interface ReservationProvider {

	public List<Reservation> getAllReservations();
}

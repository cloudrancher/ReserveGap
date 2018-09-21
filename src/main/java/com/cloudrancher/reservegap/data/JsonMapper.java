package com.cloudrancher.reservegap.data;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonMapper {
	private static final Logger log = Logger.getLogger(JsonMapper.class.getName());
	
	public static List<CampSite> parseCampSites(String campSiteJson) {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(campSiteJson,new TypeReference<List<CampSite>>(){});
		} catch (IOException e) {
			log.log(Level.FINE, "Error parsing camp site list", e);
			return null;
		}
	}

	public static List<Reservation> parseReservations(String resListJson) {
		try {
			ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
			return om.readValue(resListJson,new TypeReference<List<Reservation>>(){});
		} catch (IOException e) {
			log.log(Level.FINE, "Error parsing reservation list", e);
			return null;
		}
	}
}

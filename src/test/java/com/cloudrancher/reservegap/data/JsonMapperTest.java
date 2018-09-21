package com.cloudrancher.reservegap.data;

import static org.junit.Assert.*;

import java.util.List;
import java.time.LocalDate;

import org.junit.Test;

public class JsonMapperTest {

	/**
	 * Test that we can parse a properly formed list of camp sites
	 */
	@Test
	public void testParseCampSites() {
		String campSiteListJson = "[{\"id\":1,\"name\":\"Cozy Cabin\"}"
				+ ",{\"id\": 2,\"name\": \"Comfy Cabin\"}]";
		
		List<CampSite> siteList = JsonMapper.parseCampSites(campSiteListJson);
		
		assertNotNull(siteList);
		assertEquals(2,siteList.size());
		siteList.stream().forEach(campSite -> {
			switch(campSite.id) {
			case 1:	
				assertEquals("Cozy Cabin", campSite.name);
				break;
			case 2:
				assertEquals("Comfy Cabin",campSite.name);
				break;
			default:
				fail("Unknown campsite exists");
			}
		});
	}
	
	/**
	 * Test that we get null and not exceptions when we give the campsite parser garbage
	 */
	@Test
	public void testBogusCampSites() {
		assertNull(JsonMapper.parseCampSites("not a valid Json String"));
		
		assertNull(JsonMapper.parseCampSites("[{\"value\": \"Valid JSON but not a campsite\"}]"));
		
		assertNull(JsonMapper.parseCampSites("[{\"id\": 1, \"name\": \"Valid site\"}, "
				+ "{\"id\": 2, \"bogusfield\": \"invalid site\"}]"));
	}
	
	/**
	 * Test that we can parse a properly formed list of Reservations
	 */
	@Test 
	public void testParseReservations() {
		String resListJson = "[{\"campsiteId\": 1, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-03\"},"
				+ "{\"campsiteId\": 2, \"startDate\": \"2018-06-08\", \"endDate\": \"2018-06-10\"}]";
		
		List<Reservation> resList = JsonMapper.parseReservations(resListJson);
		
		assertNotNull(resList);
		assertEquals(2,resList.size());
		resList.stream().forEach(res -> {
			switch(res.campsiteId) {
			case 1:
				assertEquals(LocalDate.parse("2018-06-01"), res.startDate);
				assertEquals(LocalDate.parse("2018-06-03"), res.endDate);
				break;
			case 2:
				assertEquals(LocalDate.parse("2018-06-08"), res.startDate);
				assertEquals(LocalDate.parse("2018-06-10"), res.endDate);
				break;
			default:
				fail("Unknown reservation exists");
			}
		});
	}
	
	/**
	 * Test that we get null and not exceptions when we give the reservation parser garbage
	 */
	@Test
	public void testBogusReservations() {
		assertNull(JsonMapper.parseReservations("not a valid reservation list"));
		
		assertNull(JsonMapper.parseReservations("[{\"value\":\"Valid JSON but not a reservation\"}]"));
		
		assertNull(JsonMapper.parseReservations("[{\"campsiteId\": 1, \"startDate\": \"2018-06-01\", \"endDate\": \"2018-06-03\"},"
				+ "{\"campsiteId\": 1, \"bogusfield\": \"invalid reservation\"}]"));
	}

}

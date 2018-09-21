package com.cloudrancher.reservegap;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.cloudrancher.reservegap.data.CampSite;
import com.cloudrancher.reservegap.data.Reservation;

public class GapCheckerTest {

	/**
	 * Test the correctness of the overlap computation
	 */
	@Test
	public void dateRangeOverlapTest() {
		//Identical Ranges overlap
		assertTrue(GapChecker.dateRangesOverlap(
				LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-03"),
				LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-03")));

		//One day overlap 
		assertTrue(GapChecker.dateRangesOverlap(
				LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-03"),
				LocalDate.parse("2018-01-03"),LocalDate.parse("2018-01-04")));

		//One Day range 
		assertTrue(GapChecker.dateRangesOverlap(
				LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-01"),
				LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-04")));

		//Adjacent, non-overlapping 
		assertFalse(GapChecker.dateRangesOverlap(
				LocalDate.parse("2018-01-01"),LocalDate.parse("2018-01-03"),
				LocalDate.parse("2018-01-04"),LocalDate.parse("2018-01-05")));
	}
	
	@Test
	public void testNoSpace() {
		List<Reservation> resList = Arrays.asList(
				new Reservation(1,LocalDate.parse("2018-06-01"), LocalDate.parse("2018-06-03")),
				new Reservation(1,LocalDate.parse("2018-06-04"), LocalDate.parse("2018-06-10")),
				new Reservation(1,LocalDate.parse("2018-06-11"), LocalDate.parse("2018-06-15")));

		GapChecker gapChecker = new GapChecker(() -> resList, () -> Arrays.asList(new CampSite(1,"CampSite1")));

		//Start Date is open, but end date is not
		{
			LocalDate start = LocalDate.parse("2018-05-20");
			LocalDate end = LocalDate.parse("2018-06-01");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(1,overlapResList.size());
		}

		//Start Date is filled, but end date is open
		{
			LocalDate start = LocalDate.parse("2018-06-15");
			LocalDate end = LocalDate.parse("2018-06-20");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(1,overlapResList.size());
		}

		//Both date are filled (overlapping 2 different res)
		{
			LocalDate start = LocalDate.parse("2018-06-04");
			LocalDate end = LocalDate.parse("2018-06-11");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(2,overlapResList.size());
		}
	}

	@Test
	public void testNoSpaceEdges() {
		List<Reservation> resList = Arrays.asList(
				new Reservation(1,LocalDate.parse("2018-06-01"), LocalDate.parse("2018-06-03")),
				new Reservation(1,LocalDate.parse("2018-06-04"), LocalDate.parse("2018-06-10")),
				new Reservation(1,LocalDate.parse("2018-06-11"), LocalDate.parse("2018-06-15")));

		GapChecker gapChecker = new GapChecker(() -> resList, () -> Arrays.asList(new CampSite(1,"CampSite1")));

		//Dates just before
		{
			LocalDate start = LocalDate.parse("2018-05-20");
			LocalDate end = LocalDate.parse("2018-05-31");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(0,overlapResList.size());
		}
		
		//Date just after
		{
			LocalDate start = LocalDate.parse("2018-06-16");
			LocalDate end = LocalDate.parse("2018-06-20");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(0,overlapResList.size());
		}
	}
	
	@Test
	public void testInnerSpace() {
		List<Reservation> resList = Arrays.asList(
				new Reservation(1,LocalDate.parse("2018-06-01"), LocalDate.parse("2018-06-03")),
				new Reservation(1,LocalDate.parse("2018-06-08"), LocalDate.parse("2018-06-10")),
				new Reservation(1,LocalDate.parse("2018-06-11"), LocalDate.parse("2018-06-15")));

		GapChecker gapChecker = new GapChecker(() -> resList, () -> Arrays.asList(new CampSite(1,"CampSite1")));

		//Dates in between
		{
			LocalDate start = LocalDate.parse("2018-06-04");
			LocalDate end = LocalDate.parse("2018-06-07");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(0,overlapResList.size());
		}
		
		//Date middle overlap start
		{
			LocalDate start = LocalDate.parse("2018-06-03");
			LocalDate end = LocalDate.parse("2018-06-04");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(1,overlapResList.size());
		}

		//Date middle overlap end
		{
			LocalDate start = LocalDate.parse("2018-06-04");
			LocalDate end = LocalDate.parse("2018-06-08");
			List<Reservation> overlapResList = gapChecker.findOverlappingReservations(start,end);
			assertNotNull(overlapResList);
			assertEquals(1,overlapResList.size());
		}
	}
	
	@Test
	public void testGapCalc() {
		List<Reservation> resList = Arrays.asList(
				new Reservation(1,LocalDate.parse("2018-06-01"), LocalDate.parse("2018-06-03")), //gap=0
				new Reservation(1,LocalDate.parse("2018-06-08"), LocalDate.parse("2018-06-10")), //gap=1
				new Reservation(2,LocalDate.parse("2018-06-01"), LocalDate.parse("2018-06-01")), //gap=2
				new Reservation(2,LocalDate.parse("2018-06-02"), LocalDate.parse("2018-06-03")), //gap=0
				new Reservation(2,LocalDate.parse("2018-06-07"), LocalDate.parse("2018-06-09")), //gap=0
				new Reservation(3,LocalDate.parse("2018-06-01"), LocalDate.parse("2018-06-02")), //gap=1
				new Reservation(3,LocalDate.parse("2018-06-08"), LocalDate.parse("2018-06-09")), //gap=1
				new Reservation(4,LocalDate.parse("2018-06-07"), LocalDate.parse("2018-06-10"))); //gap=0

		List<CampSite> siteList = Arrays.asList(
				new CampSite(1,"Cozy Cabin"),
				new CampSite(2,"Comfy Cabin"),
				new CampSite(3,"Rustic Cabin"),
				new CampSite(4,"Rickety Cabin"),
				new CampSite(5,"Cabin in the Woods"));
		
		GapChecker gapChecker = new GapChecker(() -> resList, () -> siteList);
		LocalDate start = LocalDate.parse("2018-06-04");
		LocalDate end = LocalDate.parse("2018-06-06");
		
		// Find gap=1
		{
			List<Reservation> gapResList = gapChecker.findReservationsWithGap(start, end, 1);
			assertNotNull(gapResList);
			assertEquals(3, gapResList.size());
		}

		// Find gap <= 2 
		{
			List<Reservation> gapResList = gapChecker.findReservationsWithGap(start, end, 2);
			assertNotNull(gapResList);
			assertEquals(4, gapResList.size());
		}
	}
}

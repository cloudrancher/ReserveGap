package com.cloudrancher.reservegap;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.cloudrancher.reservegap.data.CampSite;
import com.cloudrancher.reservegap.data.Reservation;

/**
 * Performs checking of overlapping and gapped reservation comparisons.
 * 
 * @author egeorge
 *
 */
public class GapChecker {
	private ReservationProvider resProv;
	private CampSiteProvider siteProv;
	
	public GapChecker(ReservationProvider resProv, CampSiteProvider siteProv) {
		this.resProv = resProv;
		this.siteProv = siteProv;
	}

	/**
	 * 
	 * @param range1Start
	 * @param range1End
	 * @param range2Start
	 * @param range2End
	 * @return true if the two ranges overlap (inclusive)
	 */
	public static boolean dateRangesOverlap(LocalDate range1Start, LocalDate range1End, LocalDate range2Start, LocalDate range2End) {
		return (range1Start.isBefore(range2End) || range1Start.isEqual(range2End)) 
				&& (range1End.isAfter(range2Start) || range1End.isEqual(range2Start));
	}
	
	/**
	 * Returns all reservations that have dates which overlap with the provided range 
	 * 
	 * @param startDate
	 * @param endDate
	 * @return All reservations from the reservation provider, or an empty list if none overlap
	 */
	public List<Reservation> findOverlappingReservations(LocalDate startDate, LocalDate endDate) {
		//TODO: We should be able to consolidate the overlapping and gap calculation
		List<Reservation> allCampSiteResList = resProv.getAllReservations();
		
		// Find all reservations that overlap
		return allCampSiteResList.stream()
			.filter(res -> dateRangesOverlap(res.startDate,res.endDate,startDate,endDate))
			.collect(Collectors.toList());
	}
	
	/**
	 * Returns all reservations that are <gap> or fewer days away from the provided range.
	 * 
	 * Does *not* return adjacent reservations, even if gap is provided as 0. 
	 * 
	 * @param startDate
	 * @param endDate
	 * @param gap
	 * @return
	 */
	public List<Reservation> findReservationsWithGap(LocalDate startDate, LocalDate endDate, int gap) {
		List<Reservation> allCampSiteResList = resProv.getAllReservations();

		return allCampSiteResList.stream()
			.filter(res -> !dateRangesOverlap(res.startDate,res.endDate,startDate,endDate)) //take out overlaps
			.filter(res -> {
				long startGap = ChronoUnit.DAYS.between(res.endDate, startDate);
				long endGap = ChronoUnit.DAYS.between(endDate, res.startDate);
				// Gaps of 1 are adjacent, otherwise any gap smaller than specified is a blocking reservation
				return (startGap > 1 && startGap <= gap+1)	
					   || (endGap > 1 && endGap <= gap+1);
			}) // Get only ones with gap
			.collect(Collectors.toList());
	}
	
	/**
	 * Returns sites that have neither overlapping reservations nor reservations <gap> or fewer days away
	 * from the provided range.  
	 * 
	 * @param startDate
	 * @param endDate
	 * @param gap
	 * @return
	 */
	public List<CampSite> findAvailableCampSites(LocalDate startDate, LocalDate endDate, int gap) {
		//Any site with an actually overlapping reservation should be excluded
		Set<Integer> overLappingSites = findOverlappingReservations(startDate, endDate).stream()
			.map(res -> res.campsiteId)
			.collect(Collectors.toSet());
		
		//Any site with a reservation with an impermissible gap should be excluded
		Set<Integer> sitesWithGaps = findReservationsWithGap(startDate, endDate, gap).stream()
			.map(res -> res.campsiteId)
			.collect(Collectors.toSet());
		
		return siteProv.getAllCampSites().stream()
				.filter(s -> !overLappingSites.contains(s.id) &&
							 !sitesWithGaps.contains(s.id))
				.collect(Collectors.toList());
	}
}

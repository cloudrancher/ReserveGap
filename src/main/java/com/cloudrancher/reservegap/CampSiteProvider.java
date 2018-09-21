package com.cloudrancher.reservegap;

import java.util.List;

import com.cloudrancher.reservegap.data.CampSite;

public interface CampSiteProvider {

	public List<CampSite> getAllCampSites();
}

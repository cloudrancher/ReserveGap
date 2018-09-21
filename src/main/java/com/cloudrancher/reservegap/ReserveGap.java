package com.cloudrancher.reservegap;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.cloudrancher.reservegap.data.CampSite;
import com.cloudrancher.reservegap.data.JsonMapper;
import com.cloudrancher.reservegap.data.Reservation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReserveGap{

	private static Logger log = Logger.getLogger(ReserveGap.class.getName());
	
	private static Options cliOpt = new Options();
	private static void usage(String errorMsg) {
		HelpFormatter formatter = new HelpFormatter();
		
		formatter.printHelp((errorMsg==null?"":errorMsg+"\n")+"ReserveGap [options] fileName", cliOpt );
		System.exit(1);
	}
	
	public static void main(String[] args) {
		cliOpt.addOption(Option.builder("g")
				.hasArg()
				.argName("gap")
				.type(Integer.class)
				.desc("Maximum gap in days to consider blocking (default 1)").build());
		cliOpt.addOption("h", "This Help");
		
		try {
			// Capture all the inputs
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse( cliOpt, args);
			if(cmd.hasOption("h"))
				usage(null);
			
			List<String> argList = cmd.getArgList();
			
			if(argList == null || argList.size() < 1)
				usage("**** FileName Required");
			
			File inFile = new File(argList.get(0));
			if(!inFile.exists()) 
				usage("**** InputFile "+inFile+" does not exit");
			
			int gap = Integer.parseInt(cmd.getOptionValue("g", "1"));
			
			// load the json
			ObjectMapper om = new ObjectMapper();
			JsonNode rootNode = om.readTree(inFile);

			// Extract the campsite data 
			JsonNode campSiteListNode = rootNode.get("campsites");
			log.finer("Parsing CampSites: "+campSiteListNode);
			List<CampSite> siteList = JsonMapper.parseCampSites(campSiteListNode.toString());
			log.finer("parsedSiteList: "+siteList.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
			
			// Extract the reservation data
			JsonNode resListNode = rootNode.get("reservations");
			List<Reservation> resList = JsonMapper.parseReservations(resListNode.toString());
			log.finer("parsedResList: "+resList.stream().map(r -> r.toString()).collect(Collectors.joining(",")));

			// Extract the search data
			JsonNode searchNode = rootNode.get("search");
			LocalDate startDate = LocalDate.parse(searchNode.get("startDate").asText());
			LocalDate endDate = LocalDate.parse(searchNode.get("endDate").asText());

			// ********************************
			// Do the computation
			GapChecker gapChecker = new GapChecker(() -> resList, () -> siteList);
			List<CampSite> availSites = gapChecker.findAvailableCampSites(startDate,endDate,gap);
			
			System.out.println("Availble Camp Sites: \n\t"+availSites.stream().map(s -> s.name).collect(Collectors.joining("\n\t")));
		} catch (ParseException | IOException e) {
			usage("**** "+e.getMessage());
		}
	}
	
}

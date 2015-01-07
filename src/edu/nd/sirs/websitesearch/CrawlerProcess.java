package edu.nd.sirs.websitesearch;


import java.io.File;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * A simple API class to access the functionality of Crawler4J
 * @author Richard McCreadie
 *
 */
public class CrawlerProcess implements CrawlerAPI{

	String host;
	String tmpFolder;
	String crawlFolder;
	public CrawlerProcess(String tmpFolder, String crawlFolder) {		
		this.tmpFolder = tmpFolder;
		this.crawlFolder = crawlFolder;
	}
	
	public int crawl(int linkdepth, String url) {
		host = url;
		String crawlStorageFolder = tmpFolder;
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(linkdepth);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		} catch (Exception e) {
			e.printStackTrace();
		}

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(host);

        /*
         * Set the custom data for the crawler 
         */
        CustomData dataPacket = new CustomData(host, new File(crawlFolder));
        controller.setCustomData(dataPacket);
        
        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(CrawlStrategy.class, numberOfCrawlers);
        
        
        return 0;
		
	}
	
	public static void main(String[] args){
		CrawlerProcess sc = new CrawlerProcess("./data/crawl/tmp", "./data/crawl");
		sc.crawl(2,"http://cse.nd.edu/");
	}

	
	
}

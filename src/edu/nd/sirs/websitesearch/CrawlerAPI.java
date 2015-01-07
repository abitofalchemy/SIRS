package edu.nd.sirs.websitesearch;

public interface CrawlerAPI {

	/**
	 * Crawls a website and adds the documents to the specified index
	 * @param linkdepth - number of links to follow
	 * @param url - hostname
	 * @return 0
	 */
	public int crawl(int linkdepth, String url);
	
}

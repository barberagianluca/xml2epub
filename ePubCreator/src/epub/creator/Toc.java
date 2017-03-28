/* XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.creator;

import epub.translator.Unicode2HTML;

/** ePub Toc Structure - All functions are self-explanatory <p> They add informations into the Toc */
public class Toc {
	//Variables
	private static String header;
	private static String footer;
	
	private String head=" ";
	private String docTitle=" ";
	private String navMap=" ";
	
	/** Creates a http://www.daisy.org/z3986/2005/ncx/ ebook index*/
	public Toc() {
		/*0-Tier*/
		//Set header
		header="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>"+
		"\n<ncx xmlns=\"http://www.daisy.org/z3986/2005/ncx/\" version=\"2005-1\" xml:lang=\"en\">\r\n";
		//Set footer
		footer="</ncx>";
	}
	
	/** dtb:depth = numero capitoli, sottocapitoli - profondità 2 */
	public void writeHead(String isbn) {
		head = "\t<head>\r\n" + 
				"\t\t<meta content=\""+isbn+"\" name=\"dtb:uid\"/>\r\n" + 
				"\t\t<meta content=\"2\" name=\"dtb:depth\"/>\r\n" + 
				"\t\t<meta content=\"0\" name=\"dtb:totalPageCount\"/>\r\n" + 
				"\t\t<meta content=\"0\" name=\"dtb:maxPageNumber\"/>\r\n" + 
				"\t</head>\r\n";
	}
	
	public void writeDocTitle(String title) {
		docTitle = "\t<docTitle>\r\n" + 
				"\t\t<text>"+title+"</text>\r\n" + 
				"\t</docTitle>";
	}
	
	/**
	 * Title [CopyrightPage+Toc default]
	 * Capitoli nome e id!!
	 * */
	public void writeNavMap(String title, String[] nomeCapitoli, String[] idCapitoli) throws java.io.IOException {
		//Minimum error handler
		if(nomeCapitoli.length!=idCapitoli.length) {
			throw (new java.io.IOException("Error in writeNavMap - Parameters lenghts doesn't match..."));
		}
		
		String header = "\n\t<navMap>\r\n" +
		"\t\t<navPoint class=\"chapter\" id=\"title\" playOrder=\"1\">\r\n" + 
		"\t\t\t<navLabel>\r\n" + 
		"\t\t\t<text>"+title+"</text>\r\n" + 
		"\t\t\t</navLabel>\r\n" + 
		"\t\t\t<content src=\"Text/title.xhtml\"/>\r\n" + 
		"\t\t</navPoint>\r\n" + 
		"\r\n" + 
		"\t\t<navPoint class=\"chapter\" id=\"toc\" playOrder=\"2\">\r\n" + 
		"\t\t\t<navLabel>\r\n" + 
		"\t\t\t\t<text>Table of Contents</text>\r\n" + 
		"\t\t\t</navLabel>\r\n" + 
		"\t\t\t<content src=\"Text/toc.xhtml\"/>\r\n" + 
		"\t\t</navPoint>\n";
		
		String navPoints = "";
		int i;
		for(i=0; i<nomeCapitoli.length; i++) {
			navPoints+="\n\t\t<navPoint class=\"chapter\" id=\""+idCapitoli[i]+"\" playOrder=\""+(i+3)+"\">\r\n" + 
					"\t\t\t<navLabel>\r\n" + 
					"\t\t\t\t<text>"+Unicode2HTML.encode(nomeCapitoli[i])+"</text>\r\n" + 
					"\t\t\t</navLabel>\r\n" + 
					"\t\t\t<content src=\"Text/"+idCapitoli[i]+".xhtml\"/>\r\n" + 
					"\t\t</navPoint>\r\n";
		}
		
		
		String footer = "\t\t<navPoint class=\"chapter\" id=\"backcover\" playOrder=\""+(i+3)+"\">\r\n" + 
				"\t\t\t<navLabel>\r\n" + 
				"\t\t\t\t<text>Backcover</text>\r\n" + 
				"\t\t\t</navLabel>\r\n" + 
				"\t\t\t<content src=\"Text/backcover.xhtml\"/>\r\n" + 
				"\t\t</navPoint>\r\n" + "\t</navMap>\r\n";
		
		navMap = header+navPoints+footer;
	}
	
	public String toString() {
		StringBuilder allText = new StringBuilder();
		allText.append(header);
		allText.append(this.head);
		allText.append(this.docTitle);
		allText.append(this.navMap);
		allText.append(footer);
		
		return allText.toString();
	}
	
	
}

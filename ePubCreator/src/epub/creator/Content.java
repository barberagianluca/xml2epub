/*
 * XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 * 
 * In epub.creator you can find the java structure of the .epub 
 * Content of the .epub
 */ 

package epub.creator;

/** ePub Content Structure */
public class Content {
	//Variables
	/** {@link Content} file initial values string */
	private static String header;
	/** {@link Content} file final values string */
	private static String footer;
	
	/** metaData strings */
	private String metaData=" ";
	/** Manifest of the e-book */
	private String manifest=" ";
	private String spine=" ";
	private String guide=" " ;
	
	
	//Constructors
	/** Creates an empty Content structure with header and footer initialized (version idpf.org/2007) */
	public Content() {
		/*0-Tier*/
		//Set header
		header="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>"
				+ "\n<package xmlns=\"http://www.idpf.org/2007/opf\" unique-identifier=\"uuid_id\" version=\"2.0\">"; //edit for 3.0
		//Set footer
		footer="</package>";
	}

	
	//1-Tier Methods
	/** Writes Meta Data
	 * @param bookInfo
	 * [0] = Title, [1] = Language Code, [2] = ISBN or ID,
	 * [3] = Creator/Author, [4] = Publisher, [5] = Date (yyyy-mm-dd) */
	public void writeMetadata(String[] bookInfo) throws java.io.IOException  {
		//Minimum error handler
		if(bookInfo.length!=6) {
			throw (new java.io.IOException("Error in writeMetadata - Incorrect number of parameters"));
		}
		
		//Static data
		String metadataHeader = "\n\t<metadata xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:opf=\"http://www.idpf.org/2007/opf\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> \n";
		String metadataFooter = "\t</metadata> \n";
		
		String metadataTitle = "\t\t<dc:title>"+bookInfo[0]+"</dc:title> \n";
		String metadataLang = "\t\t<dc:language>"+bookInfo[1]+"</dc:language> \n";
		String metadataIdentifier = "\t\t<dc:identifier id=\"uuid_id\" opf:scheme=\"uuid\">"+bookInfo[2]+"</dc:identifier> \n"; //edit for 3.0
		//String metadataIdentifier = "\t\t<dc:identifier id=\"uuid_id\">"+bookInfo[2]+"</dc:identifier> \n";
		String metadataCreator = "\t\t<dc:creator>"+bookInfo[3]+"</dc:creator> \n";
		String metadataPublisher = "\t\t<dc:publisher>"+bookInfo[4]+"</dc:publisher> \n";
		String metadataDate = "\t\t<dc:date>"+bookInfo[5]+"</dc:date> \n";
		
		String metadataCover = "\n\t\t<meta name=\"cover\" content=\"my-cover-image\"/>\n";
		
		//String dcmodified = "<dcterms:modified>"+bookInfo[5]+"</dcterms:modified>"; //edit for 3.0
		
		//Set metaData
		metaData = metadataHeader+metadataTitle+metadataLang+metadataIdentifier+metadataCreator+metadataPublisher+metadataDate+/*dcmodified+*/metadataCover+metadataFooter;
	}	
	
	/** Writes the Manifest of the epub
	 * @param chaptersNames Array of names of chapters
	 * */
	public void writeManifest(String[] chaptersNames) {	
		String manifestHeader = "\n\t<manifest>\r\n" + 
				"\t\t<item href=\"../OEBPS/Text/bookcover.xhtml\" id=\"bookcover\" media-type=\"application/xhtml+xml\"/>\r\n" +
				"\t\t<item href=\"../OEBPS/Text/backcover.xhtml\" id=\"backcover\" media-type=\"application/xhtml+xml\"/>\r\n" +
				"\t\t<item href=\"../OEBPS/Text/title.xhtml\" id=\"title\" media-type=\"application/xhtml+xml\"/>\r\n" +  
				"\t\t<item href=\"../OEBPS/Text/toc.xhtml\" id=\"toc\" media-type=\"application/xhtml+xml\"/>\n";
		String manifestFooter = "\t\t<item href=\"../OEBPS/Images/cover.jpg\" id=\"my-cover-image\" media-type=\"image/jpeg\"/>\r\n" + 
				"\t\t<item href=\"../OEBPS/Images/backcover.jpg\" id=\"my-backcover-image\" media-type=\"image/jpeg\"/>\r\n" + 
				"\t\t<item href=\"../OEBPS/Styles/stylesheet.css\" id=\"cascadingstylesheet\" media-type=\"text/css\"/>\r\n" + 
				"\t\t<item href=\"../OEBPS/toc.ncx\" id=\"tableofcontents\" media-type=\"application/x-dtbncx+xml\"/>\r\n" + 
				"\r\n" + 
				"\t</manifest> \n";
		
		//Ciclo for
		String chaptersItems = "";
		if(chaptersNames!=null) { //Evita che esploda tutto
		for(int i=0; i<(chaptersNames.length); i++) {
			chaptersItems += "\t\t<item href=\"../OEBPS/Text/"+chaptersNames[i]+".xhtml\" id=\""+chaptersNames[i]+"\" media-type=\"application/xhtml+xml\"/>\n";
		}
		}
		
		//Write manifest
		manifest = manifestHeader+chaptersItems+manifestFooter;
	}
	
	/**
	 * Like write manifest... (Array nome capitoli...)
	 * Niente colophon
	 * */
	public void writeSpine(String[] chaptersNames) {	
		String spineHeader = "\n\t<spine toc=\"tableofcontents\">\r\n" + 
				"\t\t<itemref idref=\"bookcover\" linear=\"no\"/>\r\n" + 
				"\t\t<itemref idref=\"title\"/>\r\n" + 
				"\t\t<itemref idref=\"toc\"/>\r\n";
		String spineFooter = "\t\t<itemref idref=\"backcover\"/>\r\n" + 
				"\t</spine>\n";
		
		//Ciclo for
		String itemrefs = "";
		if(chaptersNames!=null) { //Evita che esploda tutto
		for(int i=0; i<(chaptersNames.length); i++) {
			itemrefs += "\t\t<itemref idref=\""+chaptersNames[i]+"\"/>\n";
		}
		}
		
		spine = spineHeader + itemrefs + spineFooter;
	}
	
	/** Writes a default Guide to the {@link Content}.<p>
	 * DEFAULT PAGES = bookcover.xhtml e toc.xhtml
	 */
	public void writeGuide() {	
		guide = "\n\t<guide>\r\n" + 
				"\t\t<reference href=\"Text/bookcover.xhtml\" title=\"Cover Image\" type=\"cover\"/>\r\n" + 
				"\t\t<reference href=\"Text/toc.xhtml\" title=\"Table Of Contents\" type=\"toc\"/>\r\n" + 
				"\t</guide>\n\n";
	}
		
	//To-String
	/** Return the representation of the {@link Content} aka the XHTML representation.*/ 
	public String toString() {
		StringBuilder allText = new StringBuilder();
		allText.append(header);
		allText.append(this.metaData);
		allText.append(this.manifest);
		allText.append(this.spine);
		allText.append(this.guide);
		allText.append(footer);
			
		return allText.toString();
	}

}

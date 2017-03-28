/*
 * XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.creator;

/** Utility Class for eBook version 2.0 */
public final class E_Book_2_0 {
	public final static String containerXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + 
			"<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\r\n" + 
			"  <rootfiles>\r\n" + 
			"\t<rootfile full-path=\"OEBPS/content.opf\" media-type=\"application/oebps-package+xml\"/>\r\n" + 
			"  </rootfiles>\r\n" + 
			"</container>";
	public final static String mimetypeLine = "application/epub+zip".trim();
	
	/** Safe convert illegal UTF-8 (HTML Entities) character that may cause illegal filename in filesystem */
	public static String convertHTMLEntitytoSimilarASCI(String text) {
		/* VOCALS */
		//a
		text = new String(text.replace("&#192;", "A"));
		text = new String(text.replace("&#193;", "A"));
		text = new String(text.replace("&#194;", "A"));
		text = new String(text.replace("&#195;", "A"));
		text = new String(text.replace("&#196;", "A"));
		
		text = new String(text.replace("&#224;", "a"));
		text = new String(text.replace("&#225;", "a"));
		text = new String(text.replace("&#226;", "a"));
		text = new String(text.replace("&#227;", "a"));
		text = new String(text.replace("&#228;", "a"));
		
		//e
		text = new String(text.replace("&#200;", "E"));
		text = new String(text.replace("&#201;", "E"));
		text = new String(text.replace("&#202;", "E"));
		text = new String(text.replace("&#203;", "E"));
		
		text = new String(text.replace("&#232;", "e"));
		text = new String(text.replace("&#233;", "e"));
		text = new String(text.replace("&#234;", "e"));
		text = new String(text.replace("&#235;", "e"));
		
		//i
		text = new String(text.replace("&#204;", "I"));
		text = new String(text.replace("&#205;", "I"));
		text = new String(text.replace("&#206;", "I"));
		text = new String(text.replace("&#209;", "N"));
		text = new String(text.replace("&#207;", "I"));
		
		text = new String(text.replace("&#236;", "i"));
		text = new String(text.replace("&#237;", "i"));
		text = new String(text.replace("&#238;", "i"));
		text = new String(text.replace("&#241;", "n"));
		text = new String(text.replace("&#239;", "i"));
		
		//o
		text = new String(text.replace("&#210;", "O"));
		text = new String(text.replace("&#211;", "O"));
		text = new String(text.replace("&#212;", "O"));
		text = new String(text.replace("&#213;", "O"));
		text = new String(text.replace("&#214;", "O"));
		
		text = new String(text.replace("&#242;", "o"));
		text = new String(text.replace("&#243;", "o"));
		text = new String(text.replace("&#244;", "o"));
		text = new String(text.replace("&#245;", "o"));
		text = new String(text.replace("&#246;", "o"));
		
		//u
		text = new String(text.replace("&#217;", "U"));
		text = new String(text.replace("&#218;", "U"));
		text = new String(text.replace("&#219;", "U"));
		text = new String(text.replace("&#220;", "U"));
		
		text = new String(text.replace("&#249;", "u"));
		text = new String(text.replace("&#250;", "u"));
		text = new String(text.replace("&#251;", "u"));
		text = new String(text.replace("&#252;", "u"));
		
		/* SPECIAL CHAR */
		text = new String(text.replace("&#221;", "Y"));
		text = new String(text.replace("&#159;", "Y"));
		
		text = new String(text.replace("&#253;", "y"));
		text = new String(text.replace("&#255;", "y"));
		
		return text;
		
	}

}

/* XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.translator;

import epub.creator.BookPrinter;

/** Converts Unicodes to HTML Entities*/
public final class Unicode2HTML {
	public static String encode(String unicodedString) {
		//Ciao come va
		if(!unicodedString.contains("\\u")) return unicodedString;
		
		// /u0000 fgfgmkmgfkkgmfdf
		//
		//0000 ...
		
		// dfdfdfdf /u0000 dfdfdfd
			//dfdfdfdf
			//0000 ffgjfg
		
		// dfdofkdofkdofkod /u0000
		
		String[] splittedStr = unicodedString.split("\\u005c\\u0075"); // \ u
		if(BookPrinter.verboseCreatorEnabled) System.out.println("Sto splittando stringhe con \\u005c\\u0075 ");
		
		char[] tempBufferEncoder = new char[4];
		int decimalHexRepresentation = 0;
		String htmlEntity;
		String tempSubString;
		
		String toRetString=splittedStr[0];
		
		for(int i=1; i<splittedStr.length; i++) { //i=1 sempre
			tempBufferEncoder[0] = splittedStr[i].charAt(0);
			tempBufferEncoder[1] = splittedStr[i].charAt(1);
			tempBufferEncoder[2] = splittedStr[i].charAt(2);
			tempBufferEncoder[3] = splittedStr[i].charAt(3);
			
			decimalHexRepresentation = (int) (Long.parseLong(""+tempBufferEncoder[0]+tempBufferEncoder[1]+tempBufferEncoder[2]+tempBufferEncoder[3], 16));
			htmlEntity="&#"+decimalHexRepresentation+";";
			
			tempSubString = htmlEntity + splittedStr[i].substring(4, (splittedStr[i].length()));
			splittedStr[i] = tempSubString;
			toRetString += splittedStr[i];
			
		}
		
		return toRetString;
	}

}

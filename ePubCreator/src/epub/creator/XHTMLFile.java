/* XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.creator;

import org.apache.commons.lang3.StringEscapeUtils;

import epub.translator.Capitolo;

/** ePub XHTMLFile Structure - All functions are self-explanatory <p> They add informations into the XHTMLFile */
public class XHTMLFile {
	//Variables
	private static String header;
	private static String footer;
	
	private String xhtmlPageTitle;
	private String body=" ";
	
	//Costruttore -> Parametro Titolo pagina
	public XHTMLFile(String pageTitle) {
		//pageTitle=(pageTitle);	//entities...
		//Set header and footer
		header="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\r\n" + 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" +
				"<head>\r\n" + 
				"\t<title>"+pageTitle+"</title>\r\n" + 
				"\t<meta content=\"http://www.w3.org/1999/xhtml; charset=utf-8\" http-equiv=\"Content-Type \"/>\r\n" + 
				"\t<meta http-equiv=\"default-style\" content=\"text/html; charset=utf-8\"/>\r\n"+
				"\t<link href=\"../Styles/stylesheet.css\" rel=\"stylesheet\" type=\"text/css\"/>\r\n" + 
				"</head>\r\n";
		footer="</html>";
		xhtmlPageTitle=pageTitle;
	}
	
	//Getter/Adder - Setter
	/*private void addParagraph(String paragraph) {
		this.paragraphs+=paragraph;
	}*/
	
	/** New method */
	public void writeChapter(Capitolo c) {
		body="<body class=\"mainbody\">\r\n" + 
				 "  <div class=\"paragraphtext\">\r\n";
		body+="\t\n<h1>"+xhtmlPageTitle+"</h1>\r\n";
		
		body+=c.toString();
		
		body+="  </div>\r\n"
				+ "</body>\r\n";
	}
	
	//OLD METHODS - deprecated
	public void writeBody(String textNoHTML) {
		body="<body class=\"mainbody\">\r\n" + 
			 "  <div class=\"paragraphtext\">\r\n";
				
		body+="\t\n<h1>"+xhtmlPageTitle+"</h1>\r\n";
		
		//textNoHTML=StringEscapeUtils.escapeHtml4(textNoHTML);
		
		body+="<span>"+textNoHTML+"</span>\r\n";
		
		body+="  </div>\r\n"
			+ "</body>\r\n";
	}
	
	/** For Title.. align:center pages */
	public void writeBodyCenter(String textNoHTML) {
		body="<body class=\"mainbody\">\r\n" + 
				 "  <div class=\"centeraligntext\">\r\n";
		body+="\t\n<h1>"+xhtmlPageTitle+"</h1>\r\n";
		
		textNoHTML=StringEscapeUtils.escapeHtml4(textNoHTML);
		
		//body+="<span>"+textNoHTML+"</span>\r\n";
		body+=textNoHTML;
		
		body+= "</div></body>\r\n";
	}
	
	public String toString() {
		return header+body+footer;
	}
	
	public void writeBodyTocXhtml(String title, String author, String[] nomeCapitoli, String[] idCapitoli) {
		body="<body class=\"mainbody\">\r\n" + 
				 "  <div class=\"centeraligntext\">\r\n";
		body+="\t\n<h1>"+xhtmlPageTitle+"</h1>\r\n\t<br/>\n";
		
		body+="\n\t\t<h2>"+title+"</h2>\r\n" + 
			 "\n\t\t<h3>"+author+"</h3>\r\n\t<br/>\n";
		
		for(int i=0; i<nomeCapitoli.length; i++) {
			body+="\n\t<br/><a href=\"../Text/"+idCapitoli[i]+".xhtml\">"+nomeCapitoli[i]+"</a>\n";
		}
		
		body+="\n\t<br/><a href=\"../Text/backcover.xhtml\">Back Cover</a>";
		
		body+="  </div>\r\n"
				+ "</body>\r\n";
	}
	
	public void writeTitleBody(String[] info) {
		body="<body class=\"mainbody\">\r\n" + 
				 "  <div class=\"centeraligntext\">\r\n";
		body+="\t\n<h1><br/><br/><br/>"+xhtmlPageTitle+"<br/><br/><br/></h1>\r\n\t<br/>\n";
		
		//[0] Title - [1] Language - [2] ISBN - [3] Author - [4] Publishing House - [5] Publication Date (yyyy-mm-dd)
		body+="\n\r\t<h3>"+info[3]+"</h3><br/>";
		body+="\n\r\t<h4>"+info[4]+"</h4><br/><br/><br/>";
		
		body+="\n\r\t<span>"+info[2]+"</span><br/>";
		body+="\n\r\t<span>Publication Date: "+info[5]+"</span><br/>";
		
		
		
		body+="  </div>\r\n"
				+ "</body>\r\n";

	}
	
}

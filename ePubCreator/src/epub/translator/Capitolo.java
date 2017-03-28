/* XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.translator;

import java.util.List;

import com.google.zxing.WriterException;

import epub.creator.BookPrinter;

import java.io.IOException;
import java.util.LinkedList;

/** ePub Chapter virtual structure - All functions are self-explanatory <p> They handle data for the 'physical' chapters */
public class Capitolo {
	private String title;
	private List<sottoCapitolo> sottoCapitoli;
	private int numSottoCapitoli;
	
	public Capitolo(String chapterTitle) {
		title = chapterTitle;
		sottoCapitoli = new LinkedList<sottoCapitolo> ();
		numSottoCapitoli = 0;
	}
	
	protected class sottoCapitolo {
		protected String resData;
		protected String resType;
		
		public sottoCapitolo() {
			resData = null;
			resType = null;
		}
		
		public String getType() {
			return this.resType;
		}
		
		public String getData() {
			return this.resData;
		}
	}
	
	public void aggiungiSottoCapitolo(String resourceType, String resourceData) {
		sottoCapitolo sc = new sottoCapitolo();
		sc.resData = resourceData;
		sc.resType = resourceType;
		this.sottoCapitoli.add(sc);
		numSottoCapitoli++;
	}
	
	public void ordinaSottoCapitoli() {
		//copia lista
		List<sottoCapitolo> tempSCList = new LinkedList<sottoCapitolo> ();
		List<sottoCapitolo> subChaptersOrderedList = new LinkedList<sottoCapitolo> ();
		for(int i=0; i<this.sottoCapitoli.size(); i++) {
			tempSCList.add(this.sottoCapitoli.get(i));
		}
		//if(BookPrinter.verboseCreatorEnabled) System.out.println("Da ordinare " + tempSCList.size() + " capitoli");
		int elementsNumber = 0;
		while(!tempSCList.isEmpty()) {
			for(int i=0; i<tempSCList.size(); i++) { //ciclo per la dimensione del vettore temporaneo
				//se il tipo del capitolo è
				if( (tempSCList.get(i).resType).equals(ConceptMapper.chapterElements[elementsNumber]) ) {
					//if(BookPrinter.verboseCreatorEnabled) System.out.println("Trovato " + ConceptMapper.chapterElements[elementsNumber] + " in posizione " + i);
					subChaptersOrderedList.add(tempSCList.remove(i));	//aggiungi sottocapitolo e quindi rimuovilo dalla lista temporanea
					i=-1; //appena ne trovo uno devo riniziare
				} else {
					//if(BookPrinter.verboseCreatorEnabled) System.out.println("Nessun elemento di tipo " + ConceptMapper.chapterElements[elementsNumber] + " ("+tempSCList.get(i).resType+")");
				}
			}
			
			//Non dovrebbe mai verifcarsi
			if(elementsNumber < ((ConceptMapper.chapterElements[elementsNumber].length())-1)) {
				elementsNumber++; //passo al prossimo elemento
			}
				
		}
		
		//cambio puntatore
		this.sottoCapitoli = subChaptersOrderedList;
	}
	
	public String getTitle() {
		return title;
	}
	
	public List<sottoCapitolo> getSubChapters() {
		List<sottoCapitolo> tempSCList = new LinkedList<sottoCapitolo> ();
		for(int i=0; i<this.sottoCapitoli.size(); i++) {
			tempSCList.add(this.sottoCapitoli.get(i));
		}
		return tempSCList;
	}
	
	public String toString() {
		if(BookPrinter.verboseCreatorEnabled) System.out.println("Ci sono " + numSottoCapitoli + " sotto capitoli da stampare..." + sottoCapitoli.size());
		String stampaDelCapitolo = "";
		if(numSottoCapitoli==0)	return "Capitolo Vuoto";
		
		for(int i=0; i<sottoCapitoli.size(); i++) {
			//stampaDelCapitolo+="Tipo <" + sottoCapitoli.get(i).resType + "> \n\t" + sottoCapitoli.get(i).resData + "\n";
			
			String testoCapitolo =  Unicode2HTML.encode((sottoCapitoli.get(i).resData));
			testoCapitolo = testoCapitolo.replace("\n","<br/>");
			testoCapitolo = testoCapitolo.replace("&lt;img src=&#34;data","<img src=\"data");
			testoCapitolo = testoCapitolo.replace("&#34;/>","\"/>");
			testoCapitolo = testoCapitolo.replace("&#34;>","\"/>"); //correzione delle immagini Base64 errate...
			
			//BAD BASE64 TEST
			String[] testDelCapitolo = testoCapitolo.split(" ");
			if(testDelCapitolo.length == 1) {
				if(BookPrinter.verboseCreatorEnabled) System.out.println("Capitolo senza spazi -> Una sola parola oppure codifica errata di qualcosa...");
				if(testDelCapitolo[0].contains("base64")) {
					if(BookPrinter.verboseCreatorEnabled) System.out.println("Immagine base 64 senza tag...");
					testDelCapitolo[0] = testDelCapitolo[0].replace("\n", "");
					testDelCapitolo[0] = testDelCapitolo[0].replace("<br/>", "");
					testoCapitolo = "<img src=\"" + testDelCapitolo[0] + "\"/>";
				}
			}
			
			
			
			//cerco presenza di URI e di .jpg / .png
			testoCapitolo = preProcessURI(testoCapitolo);
			
			//prendo immagini e video quindi faccio escape
			testoCapitolo = processImageAndVideo(testoCapitolo);
			
			//UnEscape
			testoCapitolo = theGoodTheBadAndTheUglyTagCleaner(testoCapitolo);
			
			//faccio escape del testo rimasto...
			stampaDelCapitolo+="\n<div class=\"" + sottoCapitoli.get(i).resType + "\"><span>"
					+ "\n<b>" + sottoCapitoli.get(i).resType + "</b><br />"
					+ testoCapitolo + "</span></div><br />\n";			
			
		}
		
        return stampaDelCapitolo;
	}
	
	/** Test QR Code with http://blog.qr4.nl/Online-QR-Code_Decoder.aspx  */
	private String processImageAndVideo(String textWithLinks) {
		//image substring
		String imageLinkStart = "&lt;img src=&#34;http";
		String imageLinkStop = "/>";
		
		//video substring
		String videoFrameLinkStart = "&lt;iframe";
		String videoFrameLinkStop = "&lt;/iframe>";
		
		while(	textWithLinks.toLowerCase().contains(imageLinkStart.toLowerCase()) ||
				textWithLinks.toLowerCase().contains(videoFrameLinkStart.toLowerCase())
		){
			if(textWithLinks.toLowerCase().contains(imageLinkStart.toLowerCase())) {
				{
				System.err.println("La stringa contiene un immagine");
				
				//System.out.println("Inizia al carattere #" + textWithLinks.indexOf(videoFrameLinkStart)); // prints "4"
				//System.out.println("Termina al carattre #" + textWithLinks.lastIndexOf(videoFrameLinkStop)); // prints "22"
				char charToExtract;
				String imageStr="";
				String originalImageStr="";
				for(int i=textWithLinks.indexOf(imageLinkStart); i<textWithLinks.indexOf(imageLinkStop, textWithLinks.indexOf(imageLinkStart)); i++) {
					charToExtract = textWithLinks.charAt(i);
					imageStr += ""+charToExtract;
					originalImageStr+= ""+charToExtract;
				}
				originalImageStr+=imageLinkStop;
				
				//System.out.println("Stringa video = " + videoStr);
				imageStr = imageStr.replace("&#34;","\""); //ripristina tag video originale
				//System.out.println("Stringa processata = " + videoStr);
				
				//FIND URL PATTERN
				String[] splittedVideoStr =  imageStr.split("\"");
				String imageURL="";
				for(int i=0; i<splittedVideoStr.length; i++) {
					if(splittedVideoStr[i].contains("http")) {
						imageURL = splittedVideoStr[i];
					}
				}
				int maxBytesImageSize = 500000;
				if(BookPrinter.verboseCreatorEnabled) System.out.println("Trovata una URL immagine: '"+imageURL+"' Limite KB attuale: " + (maxBytesImageSize/1024));
				
				//String imageNewPath = "<img height=\"125\" width=\"125\" src=\"";
				String imageNewPath = "<img src=\"";
				try {
					imageNewPath += "../Images"+(ImageAndVideoHandler.saveImage(imageURL, maxBytesImageSize)).substring((ImageAndVideoHandler.saveImage(imageURL, maxBytesImageSize)).lastIndexOf("/"));
				} catch (IOException | WriterException e) {
					e.printStackTrace();
				}
				imageNewPath += "\" alt=\"QR-Code\" />";
				
				if(BookPrinter.verboseCreatorEnabled) System.out.println("Nuovo link da inserire: " + imageNewPath);
				
				//replace old Str with new
				if(BookPrinter.verboseCreatorEnabled) System.out.println("Sostituisco " + originalImageStr);
				textWithLinks = new String(textWithLinks.replace(originalImageStr, imageNewPath));
				
				//System.out.println(textWithLinks);
				}
				continue;
			}
				
			else if (textWithLinks.toLowerCase().contains(videoFrameLinkStart.toLowerCase())) {
				{
					System.err.println("La stringa contiene video");
					//System.out.println("Inizia al carattere #" + textWithLinks.indexOf(videoFrameLinkStart)); // prints "4"
					//System.out.println("Termina al carattre #" + textWithLinks.lastIndexOf(videoFrameLinkStop)); // prints "22"
					char charToExtract;
					String videoStr="";
					String originalVideoStr="";
					for(int i=textWithLinks.indexOf(videoFrameLinkStart); i<textWithLinks.indexOf(videoFrameLinkStop, textWithLinks.indexOf(videoFrameLinkStart)); i++) {
						charToExtract = textWithLinks.charAt(i);
						videoStr += ""+charToExtract;
						originalVideoStr+= ""+charToExtract;
					}
					originalVideoStr+=videoFrameLinkStop;
					
					//System.out.println("Stringa video = " + videoStr);
					videoStr = videoStr.replace("&#34;","\""); //ripristina tag video originale
					//System.out.println("Stringa processata = " + videoStr);
					
					//FIND URL PATTERN
					String[] splittedVideoStr =  videoStr.split("\"");
					String videoURL="";
					for(int i=0; i<splittedVideoStr.length; i++) {
						if(splittedVideoStr[i].contains("http")) {
							videoURL = splittedVideoStr[i];
						}
					}
					
					if(BookPrinter.verboseCreatorEnabled) System.out.println("Trovata una URL video: '"+videoURL+"'");
					
					//String imageNewPath = "<img height=\"125\" width=\"125\" src=\"";
					String imageNewPath = "<img src=\"";
					try {
						imageNewPath += "../Images"+(ImageAndVideoHandler.saveVideo(videoURL)).substring((ImageAndVideoHandler.saveVideo(videoURL)).lastIndexOf("/"));
					} catch (IOException | WriterException e) {
						e.printStackTrace();
					}
					imageNewPath += "\" alt=\"QR-Code\" />";
					
					if(BookPrinter.verboseCreatorEnabled) System.out.println("Nuovo link da inserire: " + imageNewPath);
					
					//replace old Str with new
					if(BookPrinter.verboseCreatorEnabled) System.out.println("Sostituisco " + originalVideoStr);
					textWithLinks = new String(textWithLinks.replace(originalVideoStr, imageNewPath));
					
					//System.out.println(textWithLinks);
				}	
				continue;
				
			} else  { //La stringa non contiene immagini o video...
				//System.out.println("La stringa non contiene immagini o video...");
				break;
			}
		}
		//cercare all'interno del testo la presenza di tag del tipo <img src="linkimmagine"/>
		
		//prendere link immagine/video chiamare
		
		//ImageAndVideoHandler.saveImage(imageUrl, maxSize);
		//ImageAndVideoHandler.saveVideo(videoUrl);
		
		//Sostituire al posto del link, il link all'immagine...
		
		return textWithLinks;
	}
	
	/** Cleans */
	private String theGoodTheBadAndTheUglyTagCleaner(String htmlText) {
		//The Good
		htmlText = htmlText.replaceAll("&lt;strong>", "<strong>");
		htmlText = htmlText.replaceAll("&lt;/strong>", "</strong>");
		
		//The Bad
		htmlText = htmlText.replaceAll("&lt;/p>", "");
		htmlText = htmlText.replaceAll("&lt;p>", "");
		
		//The Ugly
		htmlText = htmlText.replaceAll("        ", "");
		htmlText = htmlText.replaceAll("\t\t", "");
		
		return htmlText;
	}
	
	/** URL Handler
	 * @param text A string of the ebook data content
	 * @return The input string with tagged URL
	 * */
	private String preProcessURI(String text) {
		String jpgUrlEnd = ".jpg";
		//String pngUrlEnd = ".png";
		
		if(!text.contains("http")) return text;
		
		
		int uriStartPosition = 0;
		int uriStopPosition = 0;
		
		int uriSpacePosition = 0;
		int uriTabPosition = 0;
		int uriNewLinePosition = 0;
		int uriLessThenPosition = 0;
		int uriEOTPosition = 0;//\u0004
		int uriEOTBPosition = 0;//\u0017
		
		for(int i=0; i<text.length(); i++) {
			if( (i+3)<text.length() &&
				text.charAt(i) == 'h' && text.charAt(i+1) == 't' && text.charAt(i+2) == 't'  && text.charAt(i+3) == 'p') {
				//System.out.println("Trovato http ");
				if(i>=3) {
					if(text.charAt(i-1) != ';' && text.charAt(i-2) != '4'  && text.charAt(i-3) != '3') {
						//System.out.print(" appartenente ad una url fuori da un tag");
						//System.out.println("Trovata URL in mezzo al capitolo");
					}
					else {
						i=i+4;
						//System.out.print(" dentro ad un tag");
						//questi non li faccio qui...
						continue;
					}	
				} else {
					//System.out.println("Trovata URL a inizio capitolo");
				}
				
				//trovata URL senza tag
				
				uriStartPosition = i;
				uriStopPosition = text.indexOf(jpgUrlEnd, uriStartPosition);
				
				
				//devo controllare se c'è uno spazio
				uriSpacePosition = text.indexOf(" ", uriStartPosition);
				uriTabPosition = text.indexOf("\t", uriStartPosition);
				uriNewLinePosition = text.indexOf("\n", uriStartPosition);
				uriLessThenPosition = text.indexOf("<", uriStartPosition);
				uriEOTPosition = text.indexOf("\u0004", uriStartPosition);
				uriEOTBPosition = text.indexOf("\u0017", uriStartPosition);
				
				//System.out.println("Uri Start: " + uriStartPosition + " - Uri Stop: " + uriStopPosition + " - Uri space: " + uriSpacePosition);
				
				int minSpace = nonMinusOneMin(uriSpacePosition, uriTabPosition, uriNewLinePosition, uriLessThenPosition, uriEOTPosition, uriEOTBPosition);
				
				if(BookPrinter.verboseCreatorEnabled) System.out.println("Uri Start: " + uriStartPosition + " - Uri Stop: " + uriStopPosition + " - Uri space: " + minSpace);
				
				if( minSpace>0 && minSpace>uriStartPosition && minSpace<uriStopPosition) {
					if(!BookPrinter.optionGenericLinkQRCoded) {
						i=minSpace;
						continue;
					}
					
					//allora c'è una url semplice e poi una url con immagine...
					//Es: Benvenuti su http://ricette.giallozafferano.it dove potrai trovare ricette come questa http://ricette.giallozafferano.it/torta.jpg
					char charToExtract = ' ';
					String uriStr = "", originalUriStr="";
					
					for(int k=uriStartPosition; k<minSpace; k++) {
						charToExtract = text.charAt(k);
						uriStr += ""+charToExtract;
						originalUriStr += ""+charToExtract;
					}				
					if(uriStr.equals("") || uriStr.equals(" ")) { if(BookPrinter.verboseCreatorEnabled) System.out.println("i = " + i); i=minSpace; if (i>=text.length()) break; }
					if(BookPrinter.verboseCreatorEnabled) System.out.println("Trovata URL SEMPLICE: " + uriStr);
					try {
						uriStr = ImageAndVideoHandler.saveVideo(uriStr);
					} catch (IOException | WriterException e) {
						e.printStackTrace();
					}
					uriStr = "../Images"+uriStr.substring(uriStr.lastIndexOf("/"));
					uriStr = Unicode2HTML.encode(uriStr);
					uriStr = new String("<img src=\"" + uriStr + "\" alt=\"Immagine\" />");
					
					text = new String( text.replace(originalUriStr, uriStr) );
					
					//Una uri va da uriStartPosition fino a uriStartPosition
					//poi da li devo controllare la prossima posizione di http...
					
					//oppure metto i=uriSpacePosition e faccio continue per continuare il processo da li...
					i=minSpace;
					continue;
				} 
				
				if(uriStopPosition!=-1 && uriStopPosition!=uriStartPosition ) {
					char charToExtract;
					String uriStr = "", originalUriStr="";
					
					for(int k=uriStartPosition; k<uriStopPosition+4; k++) {
						charToExtract = text.charAt(k);
						uriStr += ""+charToExtract;
						originalUriStr += ""+charToExtract;
					}				
					if(uriStr.equals("") || uriStr.equals(" ")) { if(BookPrinter.verboseCreatorEnabled) System.out.println("i = " + i); i=uriStopPosition+17; if (i>=text.length()) break; }
					if(BookPrinter.verboseCreatorEnabled) System.out.println("Trovata URL: " + uriStr);
					uriStr = Unicode2HTML.encode(uriStr);
					uriStr = new String("&lt;img src=&#34;" + uriStr + "&#34; alt=&#34;Immagine&#34; />");
					
					text = new String( text.replace(originalUriStr, uriStr) );
					i=uriStopPosition+17;
					continue;
					
				} else {
					if(BookPrinter.optionGenericLinkQRCoded) {
					//uri non di immagine jpg
					char charToExtract = ' ';
					String uriStr = "", originalUriStr="";
					if(minSpace==Integer.MAX_VALUE) minSpace = (text.length()); //caso in cui ho solo la url isolata dal mondo
					
					for(int k=uriStartPosition; k<minSpace; k++) {
						charToExtract = text.charAt(k);
						uriStr += ""+charToExtract;
						originalUriStr += ""+charToExtract;
					}				
					if(uriStr.equals("") || uriStr.equals(" ")) { if(BookPrinter.verboseCreatorEnabled) System.out.println("i = " + i); i=minSpace; if (i>=text.length()) break; }
					if(BookPrinter.verboseCreatorEnabled) System.out.println("Trovata URL SEMPLICE: " + uriStr);
					try {
						uriStr = ImageAndVideoHandler.saveVideo(uriStr);
					} catch (IOException | WriterException e) {
						e.printStackTrace();
					}
					
					uriStr = "../Images"+uriStr.substring(uriStr.lastIndexOf("/"));
					uriStr = Unicode2HTML.encode(uriStr);
					uriStr = new String("<img src=\"" + uriStr + "\" alt=\"Immagine\" />");
					
					text = new String( text.replace(originalUriStr, uriStr) );
					}
				}
			}	
			
		}
		
		return text;
	}
	
	/** Utility Methods
	 * @param numbers An array of Integer
	 * @return The minimum number !=-1
	 * */
	private int nonMinusOneMin(Integer... numbers){
		if(numbers.length==1) return numbers[0];
		
		int minValue = Integer.MAX_VALUE;
		
		for(int i = 0; i < numbers.length; i++){
	        if(numbers[i]<=minValue && numbers[i]>-1)
	        	minValue = numbers[i];
	    }
		
		return minValue;
	}
	
	
}

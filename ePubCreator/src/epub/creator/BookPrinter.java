/*
 * XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 * 
 * This is the MAIN Class - BookPrinter. Use
 * 	java BookPrinter.class "XTM_FILE_FULL_PATH" "LANGUAGE_2_CHARS_ID" [ "eBookName" "book_isbn" "author" "publishing house" "yyyy-mmm-dd_pubblication_date" ] 
 */ 

package epub.creator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringEscapeUtils;

import epub.translator.Capitolo;
import epub.translator.ConceptMapper;
import epub.translator.Unicode2HTML;
import javacc.parsexml.*;
import javacc.structure.*;

/**  This is the MAIN Class of the project! Please read carefully the javadoc of this class!
 * <p>
 * You should use only this class to create an ePub from the XTM file.
 * <p>
 * <ul>
 * <li>"fullPathFileName"
 * <li>"bookLanguageCode"
 * </ul>
 * are required parameters
 * <p>
 * <ul>
 * <li>"eBookName"
 * <li>"book_isbn"
 * <li>"author"
 * <li>"publishing house"
 * <li>"yyyy-mmm-dd_pubblication_date"
 * </ul>
 * are optional parameters 
 * <p>
 * EXAMPLE<p> Run in Eclipse with parameters:<p> "C:\Desktop\LPAstronomia.xtm" "it" "Precessione degli Equinozi" "isbn-000-1-123-12345-6" "Ipparco di Micea" "Rhodos Press"
 * <p>It will generate an epub named 'Precessione degli Equinozi_it' in the working directory.<p>Please read carefully the javadoc of this class!
 * */
public abstract class BookPrinter {
	//INPUT FOLDER
	/** Default immutable files Input Folder. PLEASE MODIFY THIS VALUE TO USE DIFFERENT CSS OR BOOK COVERS!*/
	public static final String path = "src/zip/creator/Input/";
	/** Default generated images temporary Input Folder*/
	public static final String imagesTempPath = "src/zip/creator/Input/Images/";
	/** Critical Operations Timer (expressed in seconds) - Can be set to 0 for developing purpose but should be set to a value 10 or more for application */
	public static final int TIMEOUT = 10;
	/** e-book informations - [0] Title - [1] Language - [2] ISBN - [3] Author - [4] Publishing House - [5] Publication Date (yyyy-mm-dd)*/
	private static String[] infoLibro = new String[6];
	/** Enables/disables parser console log - Developer options */
	protected static boolean verboseParserEnabled = false; // Set to true to show parser console logs - [DEVELOPER ONLY]
	/** Enables/disables e-book creator console log - Developer options */
	public static boolean verboseCreatorEnabled = false; // Set to true to show bookPrinter console logs - [DEVELOPER ONLY]
	/** Enables/disables conversion of all links in the book - Developer options */
	public static boolean optionGenericLinkQRCoded = false; // Set to true to enable creation of qr code of all links! - [DEVELOPER ONLY]
	
	/** The main
	 * @param args args[0]=fullPathFileName - args[1]=bookLanguage
	 * others params are [ "eBookName" "book_isbn" "author" "publishing house" "yyyy-mmm-dd_pubblication_date" ]
	 *  */
	public static void main(String[] args) {
		//Variabili e-book
		LinkedList<Capitolo> listaCapitoli = null;
		XTM xtm = null;
		String ebookName = "noName";
		
		if(args.length>1) {	//almeno .xtm e lingua libro...
			String fileName = args[0];	//Primo argomento = Nome file (indirizzo assoluto) da parsare
			String language = args[1];	//Secondo argomento = Codice lingua (2 caratteri) del libro
			
			try {
				xtm = XTMParser.parse(args[0], verboseParserEnabled); //Parso
			} catch (Exception e1) {
				e1.printStackTrace();			//Purtoppo il parser può lanciare eccezioni di qualsiasi tipo
				System.exit(-1); 				//Ovviamente nel caso non avrebbe senso proseguire
			}
			
			xtm.print(fileName+"-LOG.txt");		//Stampo log nello stesso punto del file - [DEVELOPER ONLY]
			
			ConceptMapper cm = new ConceptMapper(xtm); 		//Creo concept mapper
			
			LinkedList<String> orderedChaptersList = knowledgeIndexer(xtm, cm);	//algoritmo d'indicizzazione della conoscenza (ordino id capitoli) 
			
			/*for(int i=0; i<orderedChaptersList.size(); i++) //Stampiamo lista "capitoli" - [DEVELOPER ONLY] - [DEBUG ONLY]
				System.out.println("Capitolo " + (i+1) + " rappresentato dal topic con ID " + orderedChaptersList.get(i));*/
			
			//Per ogni capitolo creo la pagina da inserire nell'ebook;
			Topic selectedChapter = null;
			LinkedList<TopicOccurrence> selectedChapterContentList = null;
			LinkedList<String> selectedChapterContentLangList = null;
			listaCapitoli = new LinkedList<Capitolo> ();
			Capitolo capitoloTemp = null;			
			for(int i=0; i<orderedChaptersList.size(); i++) {
				selectedChapter = xtm.getTopicMap().getTopic(orderedChaptersList.get(i)); 	//prendo topic capitolo
				String title = selectedChapter.getTopicName().getTopicNameValue();			//prendo titolo
				selectedChapterContentList = (LinkedList<TopicOccurrence>) selectedChapter.getTopicOccurrenceList();	//prendo lista "sottocapitoli"
				capitoloTemp = new Capitolo(title);		//preparo struttura Capitolo
				
				for(int j=0; j<selectedChapterContentList.size(); j++) {	//ciclo per la lista "sotto capitoli"
					selectedChapterContentLangList = (LinkedList<String>) selectedChapterContentList.get(j).getTopicOccurenceScope();	//prendo lingue del sotocapitolo
					String elementType = cm.conceptOfTopicID(selectedChapterContentList.get(j).getTopicOccurenceType());				//prendo tipo elemento
					String resourceData = selectedChapterContentList.get(j).getTopicOccurenceResourceData();							//prendo la resource data (contenuto)
					
					for(int k=0; k<selectedChapterContentLangList.size(); k++) {	//controllo lingua...
						if( (selectedChapterContentLangList.get(k)).equals(cm.idTopicOfConcept(language))
						||  (selectedChapterContentLangList.get(k)).equals(cm.idTopicOfConcept("Language_independent"))) {
							capitoloTemp.aggiungiSottoCapitolo(elementType, resourceData);	//se lingua ok aggiungo sottocapitolo
						}
					}
				}
				capitoloTemp.ordinaSottoCapitoli();	//ordina capitolo
				listaCapitoli.add(capitoloTemp);	//quindi aggiungilo
			}
			
			//Fetching dei paramentri di input "facoltativi"
			infoLibro[1] = args[1]; //la lingua del libro è già passata come secondo parametro...
			
			if(args.length>2) infoLibro[0] = args[2]; else { System.out.println("Nessun titolo specificato (terzo argomento vuoto)... Userò il default..."); infoLibro[0] = "Titolo Sconosciuto";}
			ebookName = infoLibro[0]+"_"+infoLibro[1];
			infoLibro[0] = Unicode2HTML.encode(StringEscapeUtils.escapeJava(infoLibro[0]));
			if(args.length>3) infoLibro[2] = args[3]; else { System.out.println("Nessun codice isbn specificato (quarto argomento vuoto)... Userò il default..."); infoLibro[2] = "isbn-000-0-000-00000-0";}
			if(args.length>4) infoLibro[3] = args[4]; else { System.out.println("Nessun autore specificato (quinto argomento vuoto)... Userò il default..."); infoLibro[3] = "Autore Ignoto";}
			if(args.length>5) infoLibro[4] = args[5]; else { System.out.println("Nessuna casa editrice specificata (sesto argomento vuoto)... Userò il default..."); infoLibro[4] = "Nessuna Casa Editrice";}
			int year = Calendar.getInstance().get(Calendar.YEAR); int month = (Calendar.getInstance().get(Calendar.MONTH))+1; int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			String monthStr; if(month<10) monthStr="0"+month; else monthStr=""+month;
			String dayStr; if(day<10) dayStr="0"+day; else dayStr=""+day;
			if(args.length>6) infoLibro[5] = args[6]; else { System.out.println("Nessuna data di pubblicazione specificata (settimo argomento vuoto)... Userò data di oggi..."); infoLibro[5] = year+"-"+monthStr+"-"+dayStr;}
			
			try {
				System.out.println("Se le seguenti informazioni non sono corrette interrompre l'elaborazione entro " + TIMEOUT + " secondi.");
				System.out.println("  - Titolo del libro: " + infoLibro[0]);
				System.out.println("  - Lingua del libro: " + infoLibro[1]);
				System.out.println("  - Codice isbn del libro: " + infoLibro[2]);
				System.out.println("  - Autore del libro: " + infoLibro[3]);
				System.out.println("  - Casa editrice: " + infoLibro[4]);
				System.out.println("  - Data di pubblicazione (yyyy-mm-dd): " + infoLibro[5]);
				Thread.sleep(TIMEOUT*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} else {
			System.err.println("ERROR THE FOLLOWING PARAMETERS ARE REQUIRED:");
			System.err.println("  - Absolute Path of the .xtm file to convert into an e-book");
			System.err.println("  - Language code of the e-book. i.e. \"en\" or \"it\" ...");
			System.exit(-2);
		}
		
		
		//File stream
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			/** INIT FILES STREAM */
			fos = new FileOutputStream(ebookName+".zip");
			zos = new ZipOutputStream(fos);
			zos.setLevel(ZipOutputStream.STORED);	//DO NOT COMPRESS
			/** LISTA FILE DA GENERARE*/
			// /mimetype [NOTA BENE -> VA SEMPRE INSERITO PER PRIMO NELLO ZIP]
			String mimetypeFileName = "mimetype";
			// /META-INF/container.xml
			String containerFileName = "container.xml";
			// /OEBPS/content.opf
			String contentFileName = "content.opf";
			// /OEBPS/tox.ncx
			String tocFileName = "toc.ncx";
			// /OEBPS/Text/bookcover.xhmtl
			String bookcoverFileName = "bookcover.xhtml";
			// /OEBPS/Text/backcover.xhmtl
			String backcoverFileName="backcover.xhtml";
			
			/** LISTA FILE DA PRELEVARE DALLA CARTELLA Input */
			// /OEBPS/Images/cover.jpg
			String coverFile = "cover.jpg";
			// /OEBPS/Images/backcover.jpg
			String backcoverFile = "backcover.jpg";
			// /OEBPS/Styles/stylesheet.css
			String stylesheetFile = "stylesheet.css";
			
			
			/** Inizio scritture files fissi e della cartella Input */
			//SCRIVI /mimetype
			writeMimetype(mimetypeFileName, zos);
			//SCRIVI /META-INF/container.xml
			writeContainer(containerFileName, zos);
			//SCRIVI /OEBPS/Images/cover.jpg			
			addToZipFile(path+coverFile, zos, "OEBPS/Images/"+coverFile);
			//SCRIVI /OEBPS/Images/backcover.jpg
			addToZipFile(path+backcoverFile, zos, "OEBPS/Images/"+backcoverFile);
			//SCRIVI /OEBPS/Styles/stylesheet.css
			addToZipFile(path+stylesheetFile, zos, "OEBPS/Styles/"+stylesheetFile);
			//SCRIVI /OEBPS/Text/bookcover.xhtml
			writeBookCover(bookcoverFileName, zos);
			//SCRIVI /OEBPS/Text/backcover.xhtml
			writeBookBackCover(backcoverFileName, zos);
			
			/** Inizio parsing... Dati parsati dal file xml */
		
			
			/** Inizio gestione immagini e video trovati...*/
			// Per ogni link di video devo generare QR CODE al link e aggiunta alla cartella OEBPS/Images/QRCodes/
			// Per ogni immagine sopra la soglia devo generare QR del link e aggiunta alla cartella OEBPS/Images/QRCodes/
			// Ogni immagine invece che rientra nella soglia va scaricata e aggiunta alla cartella OEBPS/Images/
			
			//In tutti i casi il parser si è già occupato di sostutire link web con link all'immagine corrispondente della rispettiva cartella
			//quindi da qualche parte mi deve passare
				//-Lista immagini da scaricare
				//-Lista immagini e video da creare qr code
				//-Mappa <nome immagine da scaricare, nome da dare> per assegnare nomi corretti
			
			/** Inizio Scrittura dati parsati...*/
			//Si suppone che io sia qui se ho già tutti i dati
			
			//CREATE /OEBPS/Text/title.xhtml
			XHTMLFile xhtmlTitle = new XHTMLFile(infoLibro[0]);	//scrivo titolo
			xhtmlTitle.writeTitleBody(infoLibro);
			writeToZip("title.xhtml", zos, "OEBPS/Text/", xhtmlTitle.toString());
			//CREATE /OEBPS/Text/chapXY.xhtml
			//.......CAPITOLI QUI........
			XHTMLFile xhtmlChap = null;
	
			String[] capitoliNome = new String[listaCapitoli.size()];
			String[] capitoliId = new String[listaCapitoli.size()];
			
			if(!(listaCapitoli.size()==0)) {
				for(int i=0; i<listaCapitoli.size(); i++){
					xhtmlChap = new XHTMLFile(Unicode2HTML.encode(listaCapitoli.get(i).getTitle()));
					capitoliNome[i] = listaCapitoli.get(i).getTitle();
					capitoliNome[i] = new String(Unicode2HTML.encode(capitoliNome[i]));
					capitoliId[i] = ((listaCapitoli.get(i).getTitle().replace(" ", "_").toLowerCase()).replace("/", "-").replace("&amp;", "and")); //Characters valdation...
					capitoliId[i] =  E_Book_2_0.convertHTMLEntitytoSimilarASCI(Unicode2HTML.encode(capitoliId[i]));
					xhtmlChap.writeChapter(listaCapitoli.get(i));
					writeToZip(capitoliId[i]+".xhtml", zos, "OEBPS/Text/", xhtmlChap.toString());
				}
			} else {
				System.out.println("ERRORE NON SONO RIUSCITO A TROVARE I CAPITOLI!!!!!!");
				System.exit(500000);
			}
			
			/*
			//EPILOGO
			xhtmlChap = new XHTMLFile(capitoliNomeTest[50]); 
			xhtmlChap.writeBody("Epilogo");
			writeToZip(capitoliIdTest[50]+".xhtml", zos, "OEBPS/Text/", xhtmlChap.toString());
			//BIOGRAFIA
			xhtmlChap = new XHTMLFile(capitoliNomeTest[51]); 
			xhtmlChap.writeBody("Biografia");
			writeToZip(capitoliIdTest[51]+".xhtml", zos, "OEBPS/Text/", xhtmlChap.toString());
			//COLOFONE
			xhtmlChap = new XHTMLFile(capitoliNomeTest[52]); 
			xhtmlChap.writeBody("Colofone");
			writeToZip(capitoliIdTest[52]+".xhtml", zos, "OEBPS/Text/", xhtmlChap.toString());
			*/
			
			
			//CREATE /OEBPS/Text/toc.xhtml
			XHTMLFile xhtmlToc = new XHTMLFile("Index"); 
			xhtmlToc.writeBodyTocXhtml(infoLibro[0], infoLibro[3], capitoliNome, capitoliId);
			writeToZip("toc.xhtml", zos, "OEBPS/Text/", xhtmlToc.toString());
			
			//CREATE /OEBPS/content.opf
			Content c = new Content();
			c.writeMetadata(infoLibro);			
			c.writeManifest(capitoliId);
			c.writeSpine(capitoliId);
			c.writeGuide();
			writeToZip(contentFileName, zos, "OEBPS/", c.toString());
			
			//CREATE /OEBPS/toc.ncx
			Toc toc = new Toc();
			toc.writeHead(infoLibro[2]);
			toc.writeDocTitle(infoLibro[0]);
			toc.writeNavMap(infoLibro[0], capitoliNome, capitoliId);	
			writeToZip(tocFileName, zos, "OEBPS/", toc.toString());
			
			//carica le immagini nello zip e cancellare le immagini dalla cartella
			loadTheImages(zos);
			
			//Chiudi Stream
			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		renameToEpub(ebookName);	//Rinomina file .zip in .epub
		
		//Pulizia file nella cartella...
		cleanTheFolder(ebookName);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////METODI ELABORAZIONE DELLA CONOSCENZA//////////////////////////////////////
	/** This function creates the ordered list of chapters in the book
	 * @param xtm Specifies the XTM structure
	 * @param cm Specifies the {@link ConceptMapper}
	 * @return Ordered list of chapters
	 * */
	private static LinkedList<String> knowledgeIndexer(XTM xtm, ConceptMapper cm) {
		LinkedList<String> ki = new LinkedList<String> ();
		
		LinkedList<Association> associationList = (LinkedList<Association>) xtm.getTopicMap().getAssociationList();
		
		Association tempAssociation = null;
		LinkedList<AssociationRole> tempAssociationRole = null;
		
		LinkedList<String> defaultRole1List = new LinkedList<String> ();
		LinkedList<String> defaultRole2List = new LinkedList<String> ();
		int totalChaptersNum = 0;
		
		for(int i=0; i<associationList.size(); i++) {
			tempAssociation = associationList.get(i);
			if((tempAssociation.getAssociationType()).equals(cm.idTopicOfConcept("Precedence"))) {
				totalChaptersNum++;
				tempAssociationRole = (LinkedList<AssociationRole>) tempAssociation.getAssociationRoleList();
				for(int j=0; j<tempAssociationRole.size(); j++) {
					if( (tempAssociationRole.get(j).getAssociationRoleType()).equals(cm.idTopicOfConcept("Default_Role_1")) ) {
						defaultRole1List.add(tempAssociationRole.get(j).getAssociationRoleTopicRef());
					} else if ( (tempAssociationRole.get(j).getAssociationRoleType()).equals(cm.idTopicOfConcept("Default_Role_2")) ) {
						defaultRole2List.add(tempAssociationRole.get(j).getAssociationRoleTopicRef());
					}
				}
			}
		}
		
		//qui ho 2 liste, una dei default role 1 , l'altra dei default role 2
		String idTopic = null;
		String firstTopic = null;
		
		int k=0;
		//PASSO b dell'algoritmo 1
		for(int i=0; i<defaultRole1List.size(); i++) {
			k=0;
			idTopic = defaultRole1List.get(i);
			for(int j=0; j<defaultRole2List.size(); j++, k++) {
				if( (defaultRole2List.get(j)).equals(idTopic) ) {
					break; //non è il primo capitolo
				}
			}
			if(k>=defaultRole2List.size()) { //li ho fatti tutti ma non l'ho trovato
				//ergo ho identificato il primo capitolo
				firstTopic = idTopic;
				break;	//evito calcoli inutili, ottimizzo caso medio O(n^2/2)
			}
		}
		
		if(verboseCreatorEnabled) System.out.println("Il primo capitolo è espresso dal topicID " + firstTopic);
		ki.add(firstTopic);
		
		//Partendo da firstTopic devo trovare il secondo capitolo prendendo il default role 2 della association
		//ove compare come default role 1 il firstTopic. quindi iterare con il secondo capitolo, terzo, ...  
		
		int chaptersCounter = 0;
		String currentRole1Topic = firstTopic;
		
		if(verboseCreatorEnabled) System.out.println("Ci sono " + totalChaptersNum + " capitoli da ordinare...");
		exit:
		while(true) {
			loop:
			for(int i=0; i<associationList.size(); i++) {
				if(chaptersCounter >= totalChaptersNum) {
					break exit;
				}
				
				tempAssociation = associationList.get(i);
				if((tempAssociation.getAssociationType()).equals(cm.idTopicOfConcept("Precedence"))) {
					//System.out.println("Relazione Precedence trovata al passo " + i);
					tempAssociationRole = (LinkedList<AssociationRole>) tempAssociation.getAssociationRoleList();
					for(int j=0; j<tempAssociationRole.size(); j++) {
						if( (tempAssociationRole.get(j).getAssociationRoleType()).equals(cm.idTopicOfConcept("Default_Role_1")) ) {
							//System.out.println("Trovato Default Role 1 al passo " + i + "." + j);
							//trovo il default role 1
							//se qui compare currentRole1Topic
							//devo prendere nella stessa association il default role 2
							if( (tempAssociationRole.get(j).getAssociationRoleTopicRef()).equals(currentRole1Topic)) {
								for(int w=0; w<tempAssociationRole.size(); w++) {
									if ( (tempAssociationRole.get(w).getAssociationRoleType()).equals(cm.idTopicOfConcept("Default_Role_2")) ) {
										//devo prendere il topicID che ha default role 2 e inserirlo in lista
										ki.addLast(tempAssociationRole.get(w).getAssociationRoleTopicRef());
										//quindi aggiornare il current topic
										currentRole1Topic = tempAssociationRole.get(w).getAssociationRoleTopicRef();
										chaptersCounter++;
										//System.out.println("Altro giro, altro regalo");
										break loop;
									}
									
								}
							} else {
								//System.out.println("CurrentRole1 = " + currentRole1Topic + " - Attuale = " + tempAssociationRole.get(j).getAssociationRoleTopicRef());
							}
						}
					}
				}
			}
		}
		
		return ki;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////METODI SCRITTURA FILES//////////////////////////////////////////////////////////////////////
	//FISSI
	/** Simply writes the mimeType String in the file.
	 * This operation must be the first of the epub(.zip) file!
	 * @param mimetypeFileName Name of mimetype file
	 * @param zos Specifies the {@link ZipOutputStream}
	 * */
	private static void writeMimetype(String mimetypeFileName, ZipOutputStream zos) 
			throws FileNotFoundException, IOException {
		//Scrivi e aggiungi mymetype
		FileWriter mimetype = new FileWriter(mimetypeFileName);
		mimetype.write(E_Book_2_0.mimetypeLine);
		mimetype.close();
		addToZipFile(mimetypeFileName, zos, mimetypeFileName);
	}
	
	/** Simply puts the containerXML String into the container.xml file.
	 * @param containerFileName Name of container file
	 * @param zos Specifies the {@link ZipOutputStream}
	 * */
	private static void writeContainer(String containerFileName, ZipOutputStream zos) 
			throws FileNotFoundException, IOException {
		//Scrivi e aggiungi container
		String folder = "META-INF/";
		FileWriter container = new FileWriter(containerFileName);
		container.write(E_Book_2_0.containerXML);
		container.close();
		addToZipFile(containerFileName, zos, folder+containerFileName);
	}
	
	/** Writes the BookCover.
	 * @param bookcoverFileName Name of bookcover file
	 * @param zos Specifies the {@link ZipOutputStream}
	 * */
	private static void writeBookCover(String bookcoverFileName, ZipOutputStream zos)
			throws IOException {
		
		String bookCoverXHTML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + 
				"<head>\r\n" + 
				"<title>Book Cover</title>\r\n" + 
				"<meta content=\"http://www.w3.org/1999/xhtml; charset=utf-8\" http-equiv=\"Content-Type\"/>\r\n" + 
				"\t<style title=\"override_css\" type=\"text/css\">\r\n" + 
				"\t\t@page {padding: 0pt; margin:0pt;}\r\n" + 
				"\t\tbody {text-align: center; padding:0pt; margin: 0pt;}\r\n" + 
				"\t</style>\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"\t<div>\r\n" + 
				"\t\t<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"100%\" version=\"1.1\" viewBox=\"0 0 548 800\" width=\"100%\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><image height=\"800\" width=\"548\" xlink:href=\"../Images/cover.jpg\"/></svg>\r\n" + 
				"\t</div>\r\n" + 
				"</body>\r\n" + 
				"</html>";
		
		String folder = "OEBPS/Text/";
		FileWriter container = new FileWriter(bookcoverFileName);
		container.write(bookCoverXHTML);
		container.close();
		addToZipFile(bookcoverFileName, zos, folder+bookcoverFileName);
	}
	
	/** Writes the BackBookCover. Absolutely is not a standard!
	 * @param backcoverFileName Name of backcover file
	 * @param zos Specifies the {@link ZipOutputStream}
	 * */
	private static void writeBookBackCover(String backcoverFileName, ZipOutputStream zos)
			throws IOException {
		
		String backCoverXHTML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + 
				"<head>\r\n" + 
				"<title>Book Backcover</title>\r\n" + 
				"<meta content=\"http://www.w3.org/1999/xhtml; charset=utf-8\" http-equiv=\"Content-Type\"/>\r\n" + 
				"\t<style title=\"override_css\" type=\"text/css\">\r\n" + 
				"\t\t@page {padding: 0pt; margin:0pt;}\r\n" + 
				"\t\tbody {text-align: center; padding:0pt; margin: 0pt;}\r\n" + 
				"\t</style>\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"\t<div>\r\n" + 
				"\t\t<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"100%\" version=\"1.1\" viewBox=\"0 0 548 800\" width=\"100%\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><image height=\"800\" width=\"548\" xlink:href=\"../Images/backcover.jpg\"/></svg>\r\n" + 
				"\t</div>\r\n" + 
				"</body>\r\n" + 
				"</html>";
		
		String folder = "OEBPS/Text/";
		FileWriter container = new FileWriter(backcoverFileName);
		container.write(backCoverXHTML);
		container.close();
		addToZipFile(backcoverFileName, zos, folder+backcoverFileName);
	}
	
	/** Adds a file to the zip ({@link ZipOutputStream})
	 * @param fileName Name of the file to add
	 * @param zos Specifies the {@link ZipOutputStream}
	 * @param folder Specify the folder in the epub(.zip) file
	 * @param stringToWrite Value to add in the Container
	 * */
	private static void writeToZip(String fileName, ZipOutputStream zos, String folder, String stringToWrite) 
			throws FileNotFoundException, IOException {
		FileWriter container = new FileWriter(fileName);
		container.write(stringToWrite);
		container.close();
		addToZipFile(fileName, zos, folder+fileName);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	////METODI GESTIONE ZIP E EPUB//////////////////////////////////////////////////////////////////
	/** Adds a file to a zip ({@link ZipOutputStream})
	 * @param fileName Name of the file to write
	 * @param zos Specifies the {@link ZipOutputStream}
	 * @param zipFileName The FullPathFileName in the zip
	 * */
	private static void addToZipFile( String fileName, ZipOutputStream zos, String zipFileName) throws FileNotFoundException, IOException {

		if(verboseCreatorEnabled) System.out.println("Writing '" + fileName + "' to zip file");
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(zipFileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}
	
	/** Rename the .zip file to .epub<p>
	 * WARNING: if the .epub file already exists, prints a message and waits for epub.creator.BookPrinter.TIMEOUT seconds
	 * @param fileName The .zip file name to rename 
	 */
	private static void renameToEpub(String fileName) {
		/*Rename File in .epub*/
		// File (or directory) with old name
		File oldFileName = new File(fileName+".zip");

		// File (or directory) with new name
		File newFileName = new File(fileName+".epub");

		//CANCELLO SE ESISTE GIA'
		if (newFileName.exists()) {
		   System.err.println("Attenzione, questa operazione porterà alla cancellazione del precedente epub generato,\n hai " + TIMEOUT + " secondi per interrompere l'operazione (Ctrl+c o terminare la console)");
		   
		   boolean cancellazioneRiuscita=false;
		   try {
			   Thread.sleep(TIMEOUT*1000);
			   cancellazioneRiuscita = Files.deleteIfExists(newFileName.toPath());
		   } catch (IOException ioe) {
			   System.err.println("Non sono riuscito a cancellare il file esistente (IOException)");
			   ioe.printStackTrace();
			   System.exit(1);
		   } catch (SecurityException se) {
			   System.err.println("Operazione non permessa (SecurityException)");
			   se.printStackTrace();
			   System.exit(1);
		   } catch (InterruptedException ie) {
			   ie.printStackTrace();
		   }
		   if(!cancellazioneRiuscita){
			   System.err.println("Non sono riuscito a cancellare il file esistente, file non trovato...");
			   System.exit(1);
		   }
		  
		}

		// Rename file (or directory)
		boolean success = oldFileName.renameTo(newFileName);

		if (!success) {
		   // File was not successfully renamed
			System.err.println("Errore, non sono riuscito a rinominare il file");
			return;
		} else {
			System.out.println("Operazione completata: e-book creato.");
		}
		
	}
	
	/** Remove temporary files generated during execution time...<p> 
	 * cleanTheFolder prints cTF) warnings if encounters errors<p>
	 * WARNING: check exclusion lists in the function!!
	 * @param eBookName The ebook name aka args[2]
	 * */
	private static void cleanTheFolder(String eBookName) {
		File dir = new File(".");
		  String[] children = dir.list();
		  if (children == null) {
		      // Either dir does not exist or is not a directory
		  } else {
		      for (int i=0; i < children.length; i++) {
		          // Get filename of file or directory
		          String filename = children[i];
		          //Escludi cartelle e file da tenere...
		          if(
		        	  !(filename.equals(".settings")) &&
		        	  !(filename.equals("bin")) &&
		        	  !(filename.equals("libs")) &&
		        	  !(filename.equals("src")) &&
		        	  !(filename.equals("doc")) &&
		        	  !(filename.equals(".classpath")) &&
		        	  !(filename.equals(".project")) &&
		        	  !(filename.equals(eBookName+".zip")) &&
		        	  !(filename.equals(eBookName)) &&
		        	  !(filename.equals(eBookName+".epub"))
		        	) {
		        	  if(verboseCreatorEnabled) System.out.println("cTF) Pulizia Output [" + filename + "]");
		        	  
		        	  boolean cancellazioneRiuscita=false;
			   		   try {
			   			   File file2Delete = new File(filename);
			   			   cancellazioneRiuscita = Files.deleteIfExists(file2Delete.toPath());
			   		   } catch (java.nio.file.DirectoryNotEmptyException dnee) {
			   			System.err.println("cTF) Non riesco a cancellare la cartella " + filename + " poichè non è vuota...");
		          	   }catch (IOException ioe) {
			   			   System.err.println("Non sono riuscito a cancellare il file esistente (IOException)");
			   			   ioe.printStackTrace();
			   			   //System.exit(1);
			   		   } catch (SecurityException se) {
			   			   System.err.println("Operazione non permessa (SecurityException)");
			   			   se.printStackTrace();
			   			   //System.exit(1);
			   		   }
			   		   if(!cancellazioneRiuscita){
			   			   System.err.println("Non sono riuscito a cancellare il file esistente, file non trovato...");
			   			   //System.exit(1);
			   		   }
		          }
		      }
		  }
		 
	}

	/** Upload and clean temporary images
	 * @param zos The {@link ZipOutputStream}
	 *  */
	private static void loadTheImages(ZipOutputStream zos) {
		File dir = new File(BookPrinter.imagesTempPath);
		String[] children = dir.list();
		if (children == null) { // Either dir does not exist or is not a directory
			
		} else {
			for (int i=0; i < children.length; i++) { // Get filename of file or directory
				String filename = children[i];
				//Escludi cartelle e file da tenere...
				if(verboseCreatorEnabled) System.out.println("lTI) Inserisco l'immagine [" + filename + "]");
				boolean cancellazioneRiuscita=false;
				File file2AddAndDelete = null;
				Path filePath=null;
				try {
					file2AddAndDelete = new File(filename);
					addToZipFile(BookPrinter.imagesTempPath+file2AddAndDelete, zos, "OEBPS/Images/"+file2AddAndDelete);
					filePath = Paths.get(BookPrinter.imagesTempPath+filename);
					cancellazioneRiuscita = Files.deleteIfExists(filePath);
				} catch (IOException ioe) {
		   			   System.err.println("Non sono riuscito a cancellare il file esistente (IOException)");
		   			   ioe.printStackTrace();
		   			   //System.exit(1);
		   		} catch (SecurityException se) {
		   			   System.err.println("Operazione non permessa (SecurityException)");
		   			   se.printStackTrace();
		   			   //System.exit(1);
		   		} finally {
		   		
		   		}
				
				if(!cancellazioneRiuscita){
		   			   System.err.println("Non sono riuscito a cancellare il file esistente, file non trovato..." + filePath.toString());
		   			   //System.exit(1);
		   		}
			}
		}
	}
	

}

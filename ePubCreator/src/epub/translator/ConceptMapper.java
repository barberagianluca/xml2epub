/* XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.translator;

import java.util.*;

import epub.creator.BookPrinter;
import javacc.structure.*;

/** Associates Concept to TopicID and TopicID to Concept <p> Requires the entire {@link javacc.structure.XTM} structure */
public class ConceptMapper {
	//struttura che mappa da ID Topic a Concetto
	private Map<String, String> topicID2Concept;
	//struttura che mappa da Concetto a ID Topic
	private Map<String, String> concept2TopicID;
	
	/** Defines the sub-chapters and their order */
	public static String[] chapterElements = {"Prerequisite", "Learning_Outcome", "Description", "Awareness", "Innovation", "Understanding"};
	
	public ConceptMapper(XTM xtm) {
		topicID2Concept = new HashMap<String, String> ();	// key = ID Topic
		concept2TopicID = new HashMap<String, String> ();	// key = Concept
		
		
		TopicMap tm = xtm.getTopicMap();
		LinkedList<String> topicList = (LinkedList<String>) tm.getAllTopicsID();
		
		Topic currentTopic = null;
		LinkedList<String> topicSubjectIDList = null;
		String conceptURL = null;
		String concept = null;
		for(int i=0; i<topicList.size(); i++) {
			currentTopic = tm.getTopic(topicList.get(i));
			topicSubjectIDList = (LinkedList<String>) currentTopic.getTopicSubjectIdentifierList();
			
			
			for(int j=0; j<topicSubjectIDList.size(); j++) {
				conceptURL = topicSubjectIDList.get(j);
				concept = conceptFinder(conceptURL);
				if(concept.equals("NOT-FOUND")) continue;
				topicID2Concept.put(currentTopic.getTopicID(), concept);
				concept2TopicID.put(concept, currentTopic.getTopicID());
				if(BookPrinter.verboseCreatorEnabled) System.out.println("Trovato concetto -> " + concept + " nel topic " + currentTopic.getTopicID());
			}
		}
	}
	
	/** Strict Concept Finder - URI MATCH */
	private String conceptFinder(String conceptURL) {
		//Language recognition
		if(conceptURL.toLowerCase().contains("http://www.topicmaps.org/xtm/1.0/language.xtm#")) {
			String[] parts = conceptURL.split("#");
			//String part1 = parts[0]; 	//http://www.topicmaps.org/xtm/1.0/language.xtm
			String part2 = parts[1]; 	//Language Identifier
			return part2;	// part2 = language
		}
		
		// Noioso ma necessario switch case di tutte le possibili URI riconoscibili
		switch(conceptURL) {
			case "http://www.eiffe-l.org/encode/si/core/Learning_Outcome": return "Learning_Outcome";
			case "http://www.eiffe-l.org/encode/si/core/Innovation": return "Innovation";
			case "http://www.eiffe-l.org/encode/si/core/Awareness": return "Awareness";
			case "http://www.eiffe-l.org/encode/si/core/Understanding": return "Understanding";
			case "http://www.eiffe-l.org/encode/si/core/lesson-plan/precedence": return "Precedence";
			case "http://www.eiffe-l.org/encode/si/core/Description": return "Description";
			case "http://www.eiffe-l.org/encode/si/core/prerequisite": return "Prerequisite";
			case "http://www.eiffe-l.org/encode/si/core/primary_notion": return "Primary_Notion";
			case "http://www.eiffe-l.org/encode/si/core/secondary_notion": return "Secondary_Notion";
			case "http://wandora.org/si/core/default-role-2": return "Default_Role_2";
			case "http://wandora.org/si/core/default-role-1": return "Default_Role_1";
			case "http://www.wandora.org/core/wandoraclass" : 
			case "http://wandora.org/si/core/wandora-class" : return "Encode_class";
			case "www.eiffe-l.org/encode/si/core/schema_ECM": return "Educational_Concept_Map";
			case "http://www.wandora.org/core/langindependent":
			case "http://wandora.org/si/core/lang-independent": return "Language_independent";
			case "http://www.wandora.org/core/occurrencetype": 
			case "http://wandora.org/si/core/occurrence-type":	return "Occurrence_type";
			case "http://wandora.org/si/core/hide-level":
			case "http://wandora.org/si/core/hidelevel": return "Hide_level";
			case "http://wandora.org/si/core/language":
			case "http://www.topicmaps.org/xtm/1.0/language.xtm": 
			case "http://www.wandora.org/core/language": return "Encode_language";
			case "http://www.wandora.org/core/variant-name-version":
			case "http://wandora.org/si/core/variant-name-version": return "Encode_variant_name_version";
			case "http://www.topicmaps.org/xtm/1.0/core.xtm#display": return "Scope_Display";
			case "http://www.wandora.org/core/role":
			case "http://wandora.org/si/core/role": return "Role";
			case "http://www.wandora.org/core/associationtype":
			case "http://wandora.org/si/core/association-type": return "Association_Type";
			case "http://www.wandora.org/core/roleclass":
			case "http://wandora.org/si/core/role-class": return "Role_class";
			case "http://www.wandora.org/core/schema-type":
			case "http://wandora.org/si/core/schema-type": return "Schema_type";
			case "http://www.wandora.org/core/contenttype":
			case "http://wandora.org/si/core/content-type": return "Content_type";
			
			default:
				if(BookPrinter.verboseCreatorEnabled) System.out.println("Attenzione nessun concetto deducibile dalla URI specificata ["+conceptURL+"]");
				return "NOT-FOUND";
		}
	}

	/** YOU MUST CHECK IF IT IS NULL === NOT FOUND! */
	public String conceptOfTopicID(String topicID) {
		return topicID2Concept.get(topicID);
	}
	
	/** YOU MUST CHECK IF IT IS NULL === NOT FOUND! */
	public String idTopicOfConcept(String Concept) {
		return concept2TopicID.get(Concept);
	}
}

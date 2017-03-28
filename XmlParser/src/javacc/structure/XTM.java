/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi
 */

package javacc.structure;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/** 
* XTM is a specialization of the XML language, one of its derivates specifically
* designed to format information with an associative structure:
* XTM is the XML format variant specific for concept maps, semantic networks,
* knowledge bases, and in general, for logical and cognitive structures. 
*/
public class XTM {
	/** Represent the XML version of the file (in this parser is fixed to 1.0) */
	protected String xml_version;
	/** Represent the XML encoding char set (in this parser is fixed to UTF-8) */
	protected String xml_encoding;
	/** The TopicMap of the XTM file, should be accessed via {@link XTM#getTopicMap()} and {@link XTM#setTopicMap(TopicMap, boolean)} methods */
	protected TopicMap topicMap;
	
	// XTM Constructors
	/** Creates an empty XTM structure */
	public XTM() {
		xml_version = "1.0";
		xml_encoding = "UTF-8";
		topicMap = null;
	}
	
	/** Creates an XTM structure specifying the {@link XTM#topicMap}
	 * @param tm Specify the {@link XTM#topicMap}
	 */
	public XTM(TopicMap tm) {
		xml_version = "1.0";
		xml_encoding = "UTF-8";
		topicMap = tm;
	}
	
	/** Set the TopicMap of the current XTM object
	 * @param theTopicMap Specify the {@link XTM#topicMap} for this structure
	 * @param overwrite If true enables overwriting, set it to false to false to disable overwriting
	 * @return False is returned only if @param overwrite was set to true and a {@link XTM#topicMap} already exists for this structure
	 */
	public boolean setTopicMap(TopicMap theTopicMap, boolean overwrite) {
		if(topicMap == null) {
			topicMap = theTopicMap;
		}
		else {
			if(overwrite) {
				topicMap = theTopicMap;
			} else {
				return false;
			}
		}
		return true;
	}
	
	/** 
	 * Get the {@link TopicMap} of the current XTM object.
	 * @return The {@link XTM#topicMap}.
	 */
	public TopicMap getTopicMap() {
		return topicMap;
	}
	
	/**
	 * Print the entire structure into a LOG file. 
	 * @param fileName Should be the full path of the file name. Es: "C:\Desktop\XTM_FILE_TO_PARSE.xtm_LOG.txt"
	 */
	public void print(String fileName) {
		PrintStream write = null;
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			write = new PrintStream(out);
			write.println("XTM-XML Version: " + this.xml_version + " - XTM Encoding: " + this.xml_encoding);
			
			LinkedList<String> topicsID = (LinkedList<String>) this.getTopicMap().getAllTopicsID();
			LinkedList<Association> associationList = (LinkedList<Association>) this.getTopicMap().getAssociationList();
			
			int topicNum = topicsID.size();
			write.println("Parsati " + topicNum + " topic.");
			
			int associationNum = associationList.size();
			write.println("Parsate " + associationNum + " association.");
			
			//TEMP OBJECTS - TOPIC
			String currentTopicID;
			Topic currentTopic = null;
			List<String> currentTopicSubLocList = null;
			
			List<String> currentTopicSubIdList = null;
			List<String> currentTopicIstOfList = null;
			TopicName currentTopic_TopicName = null;
			List<TopicNameVariant> currentTopicNameVariantList = null;
			TopicNameVariant currentTopicNameVariant = null;
			List<String> currentTopicNameVariantScopeList = null;
			String currentTopicNameVariantScope = null;
			List<TopicOccurrence> currentTopicOccurrenceList = null;
			TopicOccurrence currentTopicOccurrence = null;
			List<String> currentTopicOccurrenceScopeList = null;
			String currentTopicOccurrenceScope = null;
			
			//FETCH INFO - TOPIC
			for(int i=0; i<topicNum; i++) {
				write.println("");
				currentTopicID = topicsID.get(i);
				currentTopic = this.getTopicMap().getTopic(currentTopicID);
				
				write.println("\t ID = " + currentTopic.getTopicID());
				
				currentTopicSubLocList = currentTopic.getTopicSubjectLocatorList();
				if(currentTopicSubLocList.size() > 0) { 
					write.println("\t SubjectLocator:");
					for(int j=0; j<currentTopicSubLocList.size(); j++) {
						write.println("\t\t - " + currentTopicSubLocList.get(j));
					}
				} else {
					write.println("\t SubjectLocator non trovato!");
				}
				
				
				currentTopicSubIdList = currentTopic.getTopicSubjectIdentifierList();
				if(currentTopicSubIdList.size() > 0) { 
					write.println("\t SubjectIdentifier:");
					for(int j=0; j<currentTopicSubIdList.size(); j++) {
						write.println("\t\t - " + currentTopicSubIdList.get(j));
					}
				} else {
					write.println("\t SubjectIdentifier non trovato!");
				}
				
				currentTopicIstOfList = currentTopic.getTopicInstanceOfList();
				if(currentTopicIstOfList.size() > 0) {
					write.println("\t InstanceOf:");
					for(int j=0; j<currentTopicIstOfList.size(); j++) {
						write.println("\t\t - " + currentTopicIstOfList.get(j));
					}
				} else {
					write.println("\t IstanceOf non trovato!");
				}
				
				currentTopic_TopicName = currentTopic.getTopicName();
				if(currentTopic_TopicName != null) {
					write.println("\t Name:");
					write.println("\t\t NameValue = " + currentTopic_TopicName.getTopicNameValue());
					currentTopicNameVariantList = currentTopic_TopicName.getTopicNameVariantList();
					for(int j=0; j<currentTopicNameVariantList.size(); j++) {
						currentTopicNameVariant = currentTopicNameVariantList.get(j);
						write.println("\t\t\t - ResourceData: '" + currentTopicNameVariant.getTopicNameVariantResourceData() + "'");
						currentTopicNameVariantScopeList = currentTopicNameVariant.getTopicNameVariantScope();
						write.println("\t\t\t - Scope:");
						for(int k=0; k<currentTopicNameVariantScopeList.size(); k++) {
							currentTopicNameVariantScope = currentTopicNameVariantScopeList.get(k);
							write.println("\t\t\t\t - " + currentTopicNameVariantScope);
						}
					}
					
				} else {
					write.println("\t Name non trovato!");
				}
				
				currentTopicOccurrenceList = currentTopic.getTopicOccurrenceList();
				if(currentTopicOccurrenceList != null) {
					if (currentTopicOccurrenceList.size() > 0 ) {
						write.println("\t Occurrence List:");
						for(int j=0; j<currentTopicOccurrenceList.size(); j++) {
							currentTopicOccurrence = currentTopicOccurrenceList.get(j);
							write.println("\t\t Type: " + currentTopicOccurrence.getTopicOccurenceType());
							write.println("\t\t ResourceData: '" + currentTopicOccurrence.getTopicOccurenceResourceData() + "'");
							currentTopicOccurrenceScopeList = currentTopicOccurrence.getTopicOccurenceScope();
							write.println("\t\t Scope:");
							for(int k=0; k<currentTopicOccurrenceScopeList.size(); k++) {
								currentTopicOccurrenceScope = currentTopicOccurrenceScopeList.get(k);
								write.println("\t\t\t -" + currentTopicOccurrenceScope);
							}
						}
					} else {
						write.println("\t Occurrence List non trovato!");
					}
				}
				
				
			}
			
			//FETCH INFO - ASSOCIATION
			//TEMP OBJECTS - ASSOCIATION
			Association currentAssociation = null;
			String currentAssociationType = null;
			AssociationRole currentAssociationRole = null;
			//associationList
			write.println("");
			for(int i = 0; i< associationNum; i++) {
				currentAssociation = associationList.get(i);
				
				currentAssociationType = currentAssociation.getAssociationType();
				write.println("\t Association");
				write.println("\t\t - Type: " + currentAssociationType);
				
				write.println("\t\t Association Role:");
				write.println("\t\t\t -----------------");
				for(int j=0; j < Association.associationRole_MAX_SIZE; j++) {
					currentAssociationRole = currentAssociation.getAssociationRoleList().get(j);
					write.println("\t\t\t - Topic Ref: " + currentAssociationRole.getAssociationRoleTopicRef());
					write.println("\t\t\t - Type: " + currentAssociationRole.getAssociationRoleType());
					write.println("\t\t\t -----------------");
				}
				
				write.println("");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			write.close();
		}
	}
	
}

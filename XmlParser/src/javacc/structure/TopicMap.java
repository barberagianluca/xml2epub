/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi
 */

package javacc.structure;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;

/** One object TopicMap is contained in every {@link XTM} */
public class TopicMap {
	/** Represent the TopicMap definition URI */
	public static String topicMap_xmlns = "http://www.topicmaps.org/xtm/";
	/** Represent the TopicMap version that can be parsed */
	public static String topicMap_version = "2.0";
	/** Topics ID List */
	private List<String> topicsID; //String = ID topic
	/** Map the Topic ID with the Object */
	private Map<String, Topic> topicsMap;
	/** List of the Associations, Associations do not have any ID */
	private List<Association> associationsList;	
	
	// Constructor
	/** Creates an empty TopicMap structure */
	public TopicMap() {		
		// Initialization structures
		topicsMap = new HashMap <String, Topic> ();
		associationsList = new LinkedList<Association> ();
		topicsID = new LinkedList<String> ();
	}
	
	// Topic Methods
	/** Add a Topic into the current TopicMap
	 * @param topic The Topic to insert
	 * @return TRUE if success, FALSE if that Topic already exist (check if ID already exists) 
	 */
	public boolean putTopic (Topic topic) {
		String topicID = topic.getTopicID();
		
		if(topicsMap.containsKey(topicID)) { // you should not invoke this method twice for the same topic..
			return false;
		} else {	
			topicsMap.put(topicID, topic);
			topicsID.add(topicID);
		}
	
		return true;
	}
	
	/** Try to return a Topic
	 * @param idTopic The Topic ID of the topic
	 * @return NULL only if the topic does not exist 
	 */
	public Topic getTopic(String idTopic) { // O(1) cost
		return topicsMap.get(idTopic);
	}
	
	/** Return the Topics List
	 * @return List of topics ID 
	 */
	public List<String> getAllTopicsID() {
		return topicsID;
	}
	
	//Assosiation Methods
	/** Add an Association to the current Topic Map. Warning: cannot check duplicates 
	 * @param association The Association to add
	 * @return Always true, but duplicated cannot be eliminated! 
	 */
	public boolean putAssociation (Association association) {
		associationsList.add(association);
		return true;
	}

	/** Return the List od Associations of the current TopicMap
	 * @return The Associations List 
	 */
	public List<Association> getAssociationList() {
		return associationsList;
	}
	
}

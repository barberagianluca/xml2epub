/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi
 */

package javacc.structure;

import java.util.List;
import java.util.LinkedList;

/** 1+ Topic must be contained in every {@link TopicMap} structure  */
public class Topic {
	/** Topic ID - Must be 1*/
	private String id;
	/** SubjectIdentifier List - Subject Identifiers must be 1+ */
	private List<String> subjectIdentifier;
	/** instanceOf List - InstanceOf must be 1+  - String = topicID ES: #topic223 */
	private List<String> instanceOf;
	/** TopicName object - Must be 1 */
	private TopicName topicName;
	/** SubjectLocator List - Can be 0+ */
	private List<String> subjectLocator;
	/** TopicOccurence object List - Can be 0+ */
	private List<TopicOccurrence> occurenceList; //*
	
	// Constructors
	/** Creates an empty Topic structure */
	public Topic() {
		id = "";
		topicName = null;
		subjectIdentifier = new LinkedList<String>();
		instanceOf = new LinkedList<String>(); 
		subjectLocator = new LinkedList<String>();
		occurenceList = new LinkedList<TopicOccurrence>();
	}
	
	/** Creates an empty Topic structure specifying its ID
	 * @param idTopic the Topic ID
	 */
	public Topic(String idTopic) {
		id = idTopic;
		topicName = null;
		subjectIdentifier = new LinkedList<String>();
		instanceOf = new LinkedList<String>(); 
		subjectLocator = new LinkedList<String>();
		occurenceList = new LinkedList<TopicOccurrence>();
	}
	
	// Setter & Adder
	/** Set the ID of the Topic, may print a message if overriding the old ID
	 * @param idTopic the Topic ID
	 */
	public void setTopicID(String idTopic) {
		if(!(this.id).equals("")) System.out.println("Warning Topic ID overriding  - Old TopicID: " + this.id);
		this.id = idTopic;
	}
	
	/** Set the TopicName of the Topic, may print a message if overriding the old ID
	 * @param tn the TopicName
	 */
	public void setTopicName(TopicName tn) {
		if(this.topicName != null) System.out.println("Warning TopicName overriding  - Old TopicID hashCode: " + this.topicName.hashCode());
		this.topicName = tn;
	}
	
	/** Add a TopicSubjectIdentifier to the Topic
	 * @param tsi the TopicSubjectIdentifier
	 */
	public void addTopicSubjectIdentifier(String tsi) {
		subjectIdentifier.add(tsi); // warning may return false in different implementations...
	}
	
	/** Add a TopicInstanceOf to the Topic
	 * @param tio the TopicInstanceOf
	 */
	public void addTopicInstanceOf(String tio) {
		instanceOf.add(tio);
	}
	
	/** Add a TopicSubjectLocator to the Topic
	 * @param tsl the TopicSubjectLocator
	 */
	public void addTopicSubjectLocator(String tsl) {
		subjectLocator.add(tsl);
	}
	
	/** Add a TopicOccurrence to Occurrence List to the Topic
	 * @param tol the TopicOccurrence object
	 */
	public void addTopicOccurence(TopicOccurrence tol) {
		occurenceList.add(tol);
	}
	
	// Getter
	/** Get the Topic ID 
	 * @return The Topic ID
	 */
	public String getTopicID() {
		return this.id;
	}
	
	/** Get the TopicName 
	 * @return The TopicName
	 */
	public TopicName getTopicName(){
		return this.topicName;
	}
	
	/** Get the TopicSubjectIdentifierList 
	 * @return The List of Topic Subject Identifiers
	 */
	public List<String> getTopicSubjectIdentifierList() {
		return this.subjectIdentifier;
	}
	
	/** Get the TopicSubjectIdentifierList 
	 * @return The List of Topic Subject Identifiers
	 */
	public List<String> getTopicInstanceOfList() {
		return this.instanceOf;
	}
	
	/** Get the TopicSubjectLocatorList 
	 * @return The List of Topic Subject Locators
	 */
	public List<String> getTopicSubjectLocatorList() {
		return this.subjectLocator;
	}
	
	/** Get the TopicOccurenceList 
	 * @return The List of Topic Occurrence
	 */
	public List<TopicOccurrence> getTopicOccurrenceList() {
		return this.occurenceList;
	}
	
	//Creazione oggetti
	/** Create a new Topic Name and add to Topic
	 * @return TopicName already generated 
	 */
	public TopicName getNewTopicName() {
		TopicName tn = new TopicName();
		setTopicName(tn);
		return tn;
	}
	
	/** Create a new Occurrence and add to Topic
	 * @return TopicOccurrence already generated 
	 */
	public TopicOccurrence getNewTopicOccurence() {
		TopicOccurrence to = new TopicOccurrence();
		addTopicOccurence(to);
		return to;
	}
	
	/** Create a new Name Variant and add to Topic
	 * @return TopicNameVariant already generated 
	 */
	public TopicNameVariant getNewTopicNameVariant() {
		TopicNameVariant tnv = new TopicNameVariant();
		getTopicName().addTopicNameVariant(tnv);
		return tnv;
	}
}

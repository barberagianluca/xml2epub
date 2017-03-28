/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi
 */

package javacc.structure;

import java.util.List;
import java.util.LinkedList;

/** 1 TopicName must be contained in every {@link Topic} */
public class TopicName {
	/** TopicName value - Must be 1 */
	String value;
	/** TopicNameVariant List - Must be 1+ */
	List<TopicNameVariant> tnvList;
	
	//Constructor
	/** Creates an empty TopicName structure */
	public TopicName() {
		value = "";
		tnvList = new LinkedList<TopicNameVariant> ();
	}
	
	//Setter & Adder
	/** Set the Topic Name Value
	 * @param tnVal TopicName Value
	 */
	public void setTopicNameValue(String tnVal) {
		this.value = tnVal;
	}
	
	/** Add a Topic Name Variant
	 * @param tnv TopicNameVariant object
	 */
	public void addTopicNameVariant(TopicNameVariant tnv) {
		tnvList.add(tnv);
	}
	
	//Getter
	/**
	 * Get the Topic Name Value which often represent the title of the Topic
	 * @return The string which represent the TopicNameValue  
	 */
	public String getTopicNameValue() {
		return value;
	}
	
	/**
	 * Get the Topic Name Variant List
	 * @return The TopicName Variant List 
	 */
	public List<TopicNameVariant> getTopicNameVariantList() {
		return tnvList;
	}
	
	
	/**
	 * Get the last Topic Name Variant inserted in the List
	 * @return The last TopicName Variant 
	 */
	public TopicNameVariant getLastTopicNameVariant() {
		return this.getTopicNameVariantList().get((this.getTopicNameVariantList().size()-1));
	}
	
}

/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi
 */

package javacc.structure;

import java.util.LinkedList;
import java.util.List;

/** 1+ TopicNameVariant can be contained in every {@link TopicName#tnvList} */
public class TopicNameVariant {
	/** Every scope is an id which refers to another Topic */
	private List<String> scope; 	//String = topicID
	/** Resource data, must be 1, represent the content of the Topic */
	private String resourceData;
	
	
	/** Creates an empty TopicNameVariant structure */
	public TopicNameVariant() {
		scope = new LinkedList<String> ();
		resourceData = "";
	}
	
	//Setter & Adder
	/** Set the ResourceData of the TopicNameVariant
	 * @param tnvrd Topic Name Variant Resource Data
	 */
	public void setTopicNameVariantResourceData(String tnvrd) {
		this.resourceData = tnvrd;
	}
	
	/** Add a Scope to the TopicNameVariant
	 * @param tnvs Topic Name Variant Scope (a Topic ID)
	 */
	public void addTopicNameVariantScope(String tnvs) {
		scope.add(tnvs);
	}
	
	//Getter
	/** Get the Resource Data from the current Topic Name Variant
	 * @return Resource Data which may contains UTF-8 char and represent the title. Resource should be expressed in ASCII + XHTML Entities to
	 * be full compatible to any e-book readers.
	 */
	public String getTopicNameVariantResourceData() {
		return resourceData;
	}
	
	/** Get the List of Scope from the current Topic Name Variant
	 * @return TopicNameVariantScope List (List of Topic IDs)
	 */
	public List<String> getTopicNameVariantScope() {
		return scope;
	} 
}

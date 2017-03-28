/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi
 */

package javacc.structure;

import java.util.List;
import java.util.LinkedList;

/** 0+ TopicOccurence can be contained in every {@link Topic} */
public class TopicOccurrence {
	/** Topic Occurrence Type - a Topic ID */
	String type; //String = topicID - MUST ONE
	/** Topic Occurrence Scope List - List of Topic IDs */
	List<String> scope; //String = topicID - *
	/** Topic Occurrence Resource Data - E-Book Text */
	String resourceData;	//Must ONE - TESTO EBOOK
	
	// Constructor
	/** Creates an empty TopicOccurrence structure */
	public TopicOccurrence() {
		type = "";
		scope = new LinkedList<String> ();
		resourceData = "";
	}
	
	// Setter & Adder
	/** Set the Topic Occurrence Type - Topic ID
	 * @param tot Topic Occurrence Type
	 */
	public void setTopicOccurenceType(String tot) {
		this.type = tot;
	}
	
	/** Add the Topic Occurrence Scope - Topic ID
	 * @param tos Topic Occurrence Scope
	 */
	public void addTopicOccurenceScope(String tos) {
		scope.add(tos);
	}
	
	/** Set the Topic Occurrence Resource Data - UTF-8 Char should be replaced with ASCII + XHTML Entities
	 * @param tord Topic Occurrence Resource Data
	 */
	public void setTopicOccurenceResourceData(String tord) {
		this.resourceData = tord;
	}
	
	// Getter
	/** Get the Topic Occurrence Type - Topic ID
	 * @return Topic Occurrence Type
	 */
	public String getTopicOccurenceType() {
		return type;
	}
	
	/** Get the Topic Occurrence Scope List - List of Topic IDs
	 * @return Topic Occurrence Scope List
	 */
	public List<String> getTopicOccurenceScope() {
		return scope;
	}
	
	/** Get the Topic Occurrence Resource Data - UTF-8 Char should be replaced with ASCII + XHTML Entities
	 * @return Topic Occurrence Resource Data
	 */
	public String getTopicOccurenceResourceData() {
		return resourceData;
	}
}

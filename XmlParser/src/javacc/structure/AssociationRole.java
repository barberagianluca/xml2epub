/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi. 
 */ 

package javacc.structure;

/** AssociationRoles are usually {@link Association#associationRole_MAX_SIZE} elements. */
public class AssociationRole {
	/** Association Role Type - a Topic ID */
	private String type;
	/** Association Topic Reference - a Topic ID */
	private String topicRef;
	
	/** Creates an empty AssociationRole structure */
	public AssociationRole() {
		type = null;
		topicRef = null;
	}
	
	//Setter
	/** Set the Type of the current AssociationRole
	 * @param t The AssociationRole Type
	 */
	public void setAssociationRoleType(String t) {
		this.type = t;
	}
	
	/** Set the Topic Reference of the current AssociationRole
	 * @param tr The AssociationRole TopicRef
	 */
	public void setAssociationRoleTopicRef(String tr) {
		this.topicRef = tr;
	}
	
	//Getter
	/** Get the Type of the current AssociationRole
	 * @return The AssociationRole Type
	 */
	public String getAssociationRoleType() {
		return this.type;
	}
	
	/** Get the Topic Reference of the current AssociationRole
	 * @return The AssociationRole TopicRef
	 */
	public String getAssociationRoleTopicRef() {
		return this.topicRef;
	}
}

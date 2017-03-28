/*
 * XTM Parser created with JavaCC 1.5.33, compatible with JavaCC 1.5.0+ Eclipse Plugins
 * Parser created by Gianluca Barbera and Marco Placidi
 */ 

package javacc.structure;

import java.util.LinkedList;
import java.util.List;

/** Associations do not have ID, 1+ association must be contained in every {@link TopicMap} */
public class Association {
	/** Association Type - Topic ID */
	private String type;
	/** Association Role List - Size should be of {@link Association#associationRole_MAX_SIZE}.*/
	private List<AssociationRole> associationRoleList;
	/** Association Role List Max Size - Constant (2).*/
	public static final int associationRole_MAX_SIZE = 2;
	
	/** Creates an empty Association structure */
	public Association() {
		associationRoleList = new LinkedList<AssociationRole>();
		type = null;
	}
	
	// Setter & Adder
	/** Set the Type of the Association
	 * @param associationType the Association Type (Topic ID)
	 */
	public void setAssociationType(String associationType) {
		this.type = associationType;
	}
	
	/** Add a Role to the current Association, may produce an error if the list size is already {@link Association#associationRole_MAX_SIZE}
	 * @param associationRole the Association Role (Topic ID)
	 */
	public void addAssociationRole(AssociationRole associationRole) {
		if(this.associationRoleList.size() >= associationRole_MAX_SIZE) {
			System.err.println("Error 0x1d5f151 - Max 2 AssociationRole permitted");
			return;
		}
		this.associationRoleList.add(associationRole); 
	}
	
	//Getter
	/** Get Association Type 
	 * @return The Association Type
	 */
	public String getAssociationType() {
		return this.type;
	}
	
	/** Get Association Role List 
	 * @return The Association List of {@link Association#associationRole_MAX_SIZE} elements
	 */
	public List<AssociationRole> getAssociationRoleList() {
		return this.associationRoleList; 
	}
	
}
 
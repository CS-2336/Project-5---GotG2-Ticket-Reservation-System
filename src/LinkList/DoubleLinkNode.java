/*
 	Name: Naman Gangwani
 	NetID: nkg160030
 */

package LinkList;

public class DoubleLinkNode extends Node {
	
	DoubleLinkNode previous, next; // Stores the previous and next DoubleLinkNode seat pointers
	
	/** Overloaded constructor with row and seat keys **/
	public DoubleLinkNode(int row, int seat) {
		super(row, seat);
	}
	
	@Override
	public DoubleLinkNode clone()
	{
		DoubleLinkNode cloned = new DoubleLinkNode(super.row, super.seat);
		cloned.setTypeSeat(super.getTypeSeat());
		cloned.setAuditorium(super.getAuditorium());
		return cloned;
	}
	
	/** Getter method for previous **/
	public DoubleLinkNode getPrev()
	{ return previous; }
	
	/** Getter method for next **/
	public DoubleLinkNode getNext()
	{ return next; }
	
	/** Setter method for previous **/
	public void setPrev(DoubleLinkNode previous)
	{ this.previous = previous; }
	
	/** Getter method for next **/
	public void setNext(DoubleLinkNode next)
	{ this.next = next; }
}
/*
 	Name: Naman Gangwani
 	NetID: nkg160030
 */

package LinkList;

public class LinkedList {
	
	public DoubleLinkNode head, tail; // Stores the head and tail of the LinkedList
	
	/** Default Constructor **/
	public LinkedList()
	{
		head = null;
		tail = null;
	}
	
	/** Overloaded Constructor **/
	public LinkedList(DoubleLinkNode head)
	{
		this.head = head; // Sets head to given parameter
		// Increments through head's `next` pointers until none can be found
		DoubleLinkNode curr = head;
		while (curr.getNext() != null)
			curr = curr.getNext();
		this.tail = curr; // Sets tail to last found DoubleLinkNode
	}
	
	/** Getter method for head **/
	public DoubleLinkNode getHead()
	{ return head; }
	
	/** Getter method for tail **/
	public DoubleLinkNode getTail()
	{ return tail; }
	
	/** Setter method for head **/
	public void setHead(DoubleLinkNode head)
	{ this.head = head; }
	
	/** Setter method for tail **/
	public void setTail(DoubleLinkNode tail)
	{ this.tail = tail; }
		
	/** Adds a DoubleLinkNode sequentially in the list based on row and seat number **/
	public void addNode(DoubleLinkNode n)
	{
		if (head == null) // If there is no head
			setHead(n); // The passed in node is the head
		else if ((n.row == head.row && n.seat < head.seat) || n.row < head.row) // If the node comes before the head
		{
			// Sets the node to be the new head
			n.next = head;
			n.previous = null;
			head.previous = n;
			head = n;
		} else // If it comes after the head
		{
			// Increment through the items in the LinkedList until the parameter node is greater than one of the nodes
			DoubleLinkNode curr = head;
			while (curr.next != null && (n.row != curr.next.row || (n.row == curr.next.row && n.seat > curr.next.seat)))
				curr = curr.next;
			// Adds the node accordingly in the list
			n.next = curr.next;
			n.previous = curr;
			curr.next = n;
			curr.previous = n.previous.previous;
			if (n.next == null) // If the newly added node is pointing to null
				setTail(n); // It is the new tail
		}
	}
	
	/** Deletes a DoubleLinkNode in the LinkedList based on the contents of the passed in DoubleLinkNode **/
	public DoubleLinkNode deleteNode(DoubleLinkNode n)
	{
		if (head == null) // If there is no head
			return head; // Nothing can be deleted
		else if (head.row == n.row && head.seat == n.seat) // If the head is the one to be deleted
		{
			// Delete the head and set its 'next` pointer to be the new head
			DoubleLinkNode hold = head;
			head = head.next;
			if (head != null)
				head.previous = null;
			hold.next = null;
			return hold;
		} else
		{
			DoubleLinkNode toDelete = find(n);
            if (toDelete != null) // If the parameter node is found in the LinkedList
            {
            	// Delete the node and create new pointers for its previous and next DoubleLinkNodes
            	DoubleLinkNode before = toDelete.previous, after = toDelete.next;
            	if (before != null)
            		before.next = after;
    			if (after != null)
    				after.previous = before;
    			toDelete.previous = null;
    			toDelete.next = null;
    			return toDelete;
            } else
            	return null;
		}
	}
	
	/** Returns a DoubleLinkNode if passed in DoubleLinkNode is found in the list; returns null otherwise **/
	public DoubleLinkNode find(DoubleLinkNode n)
	{
		DoubleLinkNode curr = head;
		while (curr != null) // Increments through the list
		{
			if (curr.row == n.row && curr.seat == n.seat) // If it is found in the list
				return curr; // Return it
			curr = curr.next;
		}
		return null; //Return null if it is not found
	}
	
	/** Returns the size of LinkedList **/
	public int getSize()
	{
		int size = 0;
		DoubleLinkNode curr = head;
		while (curr != null) // Increments through the LinkedList
		{
			size++; // Adds on to the size
			curr = curr.next;
		}
		return size; // Returns total found size
	}
}
/*
 	Name: Naman Gangwani
 	NetID: nkg160030
 */

package LinkList;

public abstract class Node {
	int row, seat; // Stores the seat's row and seat values
	int auditorium;
	private String typeSeat; // Stores what type the seat is (i.e Adult, Senior, Child)
	
	/** Overloaded constructor with row and seat keys **/
	public Node(int row, int seat)
	{
		this.row = row;
		this.seat = seat;
	}
	
	/** Getter method for row **/
	public int getRow()
	{ return row; }
	
	/** Getter method for seat **/
	public int getSeat()
	{ return seat; }
	
	/** Getter method for type of seat **/
	public String getTypeSeat()
	{ return typeSeat; }
	
	/** Getter method for auditorium **/
	public int getAuditorium()
	{ return auditorium; }
	
	/** Setter method for row **/
	public void setRow(int row)
	{ this.row = row; }
	
	/** Setter method for seat **/
	public void setSeat(int seat)
	{ this.seat = seat; }
	
	/** Setter method for seat **/
	public void setTypeSeat(String typeSeat)
	{ this.typeSeat = typeSeat; }
	
	/** Getter method for auditorium **/
	public void setAuditorium(int auditorium)
	{ this.auditorium = auditorium; }
}
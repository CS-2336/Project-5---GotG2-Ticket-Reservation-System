/*
 	Name: Naman Gangwani
 	NetID: nkg160030
 */

import LinkList.*;
import LinkList.LinkedList;
import java.util.*;
import java.io.*;

public class Main {
	
	public static LinkedList[][] auditoriums = new LinkedList[3][2]; // Keeps track of empty and reserved seats for all auditoriums
	public static int[][] numRowsAndSeats = new int[3][2]; // Keeps track of the number of rows and seats in each auditoriums
	public static HashMap<String, String> database = new HashMap<String, String>(); // Stores all userdata
	public static double[][] ticketSales = new double[3][3]; // Keeps track of adult, senior, and children ticket sales for each auditorium
	
	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner input = new Scanner(System.in);
		
		// Initializes all LinkedLists and reads in all the files
		for (int i = 0; i < auditoriums.length; i++)
		{
			for (int j = 0; j < auditoriums[0].length; j++)
				auditoriums[i][j] = new LinkedList();
			readAuditorium(new Scanner(new File("A"+(i+1)+".txt")), i);
		}
				
		/** Fills in HashMap from the userdb.dat file **/
		Scanner userdb = new Scanner(new File("userdb.dat"));
		while (userdb.hasNextLine())
		{
			String username = userdb.next(), password = userdb.next(); // Reads in username/password on that line
			database.put(username, password); // Adds username as a key and password as a value into the database HashMap
		}
		userdb.close();
				
		boolean adminExits = false;
		while (!adminExits) // While the admin hasn't chosen to exit
		{
			/** Username and password authenticator **/
			boolean invalid = true;
			String username = "";
			while (invalid) // While the user keeps entering invalid passwords
			{
				System.out.print("Username: ");
				username = input.nextLine(); // Takes in username
				int numAttempts = 0;
				do
				{
					System.out.print("Password: ");
					String password = input.nextLine(); // Takes in password
					numAttempts++;
					if(database.get(username).equals(password)) // If the password matches the one in the database
						invalid = false; // It is valid
					else
					{
						if (numAttempts < 3)
							System.out.println("\n** Invalid password. Please try again.");
						else
							System.out.println("\n** Too many invalid password attempts. Returning to starting point.\n");
					}
				} while (numAttempts < 3 && invalid); // While the user still has attempts and hasn't inputted the correct password
			}

			if (!username.equals("admin"))
			{
				int chosenOption = 0; // User choice for auditorium
				while (chosenOption != 5) // While the user does not choose to log out
				{
					System.out.println("\n1. Reserve Seats \n2. View Orders \n3. Update Order \n4. Display Receipt \n5. Log Out\n"); // Displays options
					do
					{
						chosenOption = Integer.parseInt(validateInput("Enter a menu option number (1-5): ", int.class, input));
						if (chosenOption < 1 || chosenOption > 5)
							System.out.println("\n** Please only choose options 1-5");
					} while (chosenOption < 1 || chosenOption > 5);
					
					switch(chosenOption)
					{
						case 1:
							reservationProcess(input, username, "", 0); // Takes user through reservation process
							break;
						case 2:
							displayOrders(username); // Displays orders in the account
							break;
						case 3:
							displayOrders(username); // Displays orders in the account
							int orderNumber = 0;
							while (orders.get(username+""+(orderNumber + 1)) != null)
								orderNumber++;
							if (orderNumber > 0) // If there's at least one order
							{
								System.out.println();
								int chosenOrder = validate("Select an order: ", "\n** Please only select a valid order.", 1, orderNumber, input);
								System.out.println("\n1. Add tickets to order \n2. Delete tickets from order \n3. Cancel order\n");
								int updateOption = validate("Choose an option: ", "\n** Please only choose from options 1-3.", 1, 3, input);
								switch(updateOption)
								{
									case 1:
										// Takes user through reservation process for that order only
										reservationProcess(input, username, username+""+chosenOrder, orders.get(username+""+chosenOrder).getHead().getAuditorium()); 
										break;
									case 2:
										int seatToDelete = 0;
										int increment = 0;
										ArrayList<DoubleLinkNode> deleteOptions = new ArrayList<DoubleLinkNode>();;
										do
										{
											System.out.println();
											increment = 1;
											DoubleLinkNode cur = orders.get(username+""+chosenOrder).getHead();
											while (cur != null) // Loops through all the seats and prints them out for the user to choose
											{
												System.out.println(increment+". Auditorium #"+cur.getAuditorium()+"; Row "+cur.getRow()+", Seat "+cur.getSeat()+"; "+cur.getTypeSeat());
												increment++;
												if (!deleteOptions.contains(cur))
													deleteOptions.add(cur);
												cur = cur.getNext();
											}
											System.out.println(increment+". Exit\n");
											
											seatToDelete = validate("Select a ticket to delete: ", "\n** Please only select a valid ticket.", 1, increment, input);
											if (seatToDelete != increment) // If the user didn't choose to exit
											{
												unreserveSeat(username+""+chosenOrder, deleteOptions.get(seatToDelete - 1), deleteOptions.get(seatToDelete - 1).getAuditorium() - 1);
												deleteOptions.remove(seatToDelete - 1);
												System.out.println("\nTicket removed.");
												if (deleteOptions.size() == 0)
												{
													orders.remove(username+""+chosenOrder); // Removes the order if all tickets have been deleted
													int j = chosenOrder;
													while(orders.get(username+""+(j+1)) != null)
													{
														orders.put(username+""+j, orders.get(username+""+(j+1))); // Increments all orders after it down one
														j++;
													}
													orders.remove(username+""+j);
													System.out.println("Order removed.");
												}
											}
										} while (seatToDelete != increment && deleteOptions.size() > 0); // Until the user exits or deletes all the seats
										break;
									case 3:
										LinkedList orderToRemove = orders.get(username+""+chosenOrder);
										DoubleLinkNode cur = orderToRemove.getHead();
										while(cur != null) // Removes all the seats in that order and makes them open again
										{
											DoubleLinkNode hold = cur;
											cur = cur.getNext();
											unreserveSeat(username+""+chosenOrder, hold, hold.getAuditorium() - 1);
										}
										orders.remove(username+""+chosenOrder); // Removes the order
										int j = chosenOrder;
										while(orders.get(username+""+(j+1)) != null) // Moves all orders after it down one
										{
											orders.put(username+""+j, orders.get(username+""+(j+1)));
											j++;
										}
										orders.remove(username+""+j);
										System.out.println("\nOrder canceled and removed.");
										break;
								}
							} else
								System.out.println("You do not have any orders."); // Notifies the user that he/she has no orders
							break;
						case 4:
							printReceipt(username); // Prints receipt
							break;
						default:
							System.out.println("\nLogging out...\n"); // Logs the user out
							break;
					}
				}
				
			} else
			{
				int chosenOption = 0; // User choice for auditorium
				while (chosenOption != 3) // While the user does not choose to log out
				{
					System.out.println("\n1. View auditorium \n2. Print Report \n3. Exit\n"); // Displays options
					chosenOption = validate("Enter a menu option number (1-3): ", "\n** Please only choose options 1-3", 1, 3, input);
					switch(chosenOption)
					{
						case 1:
							System.out.println("\n1. Auditorium 1 \n2. Auditorium 2 \n3. Auditorium 3\n");
							int chosenAuditorium = 0;
							do
							{
								chosenAuditorium = Integer.parseInt(validateInput("Choose an auditorium (1-3): ", int.class, input));
								if (chosenAuditorium < 1 || chosenAuditorium > 3)
									System.out.println("\n** Please only choose options 1-3");
							} while(chosenAuditorium < 1 || chosenAuditorium > 3);
							displayAuditorium(chosenAuditorium - 1); // Displays the auditorium
							break;
						case 2:
							System.out.println("\nAUDITORIUM\tOPEN SEATS\tRESERVED SEATS\t ADULT SEATS\tSENIOR SEATS\tCHILD SEATS\tTICKET SALES");
							double[] total = new double[6];
							for (int i = 1; i <= 3; i++) // For each auditorium
							{
								// Prints the individual auditorium's statistics
								System.out.printf("    "+i+"\t\t    "+auditoriums[i - 1][0].getSize()+"\t\t     "+auditoriums[i - 1][1].getSize()
										+"\t\t     "+(int)(ticketSales[i - 1][0]/10) + "\t\t     "+(int)(ticketSales[i - 1][1]/7.5)+ "\t\t    "+(int)(ticketSales[i - 1][2]/5.25) 
										+ "\t\t    $%.2f\n", (ticketSales[i - 1][0] + ticketSales[i - 1][1] + ticketSales[i - 1][2]));
								total[0]+= auditoriums[i - 1][0].getSize();
								total[1]+= auditoriums[i - 1][1].getSize();
								total[2]+=(ticketSales[i - 1][0]/10);
								total[3]+=(ticketSales[i - 1][1]/7.5);
								total[4]+=(ticketSales[i - 1][2]/5.25);
								total[5]+=(ticketSales[i - 1][0] + ticketSales[i - 1][1] + ticketSales[i - 1][2]);
							}
							System.out.printf("\n  TOTAL\t\t    "+(int)(total[0])+"\t\t    "+(int)(total[1])+"\t\t     "+(int)(total[2])+"\t\t     "+(int)(total[3])+"\t\t    "
									+(int)(total[4])+"\t\t    $%.2f\n", total[5]); // Prints the total of everything
							break;
						default:
							break;
					}
				}
				
				for (int i = 1; i <= 3; i++) // Loops through all the files
				{
					PrintWriter auditorium = new PrintWriter("A"+i+".txt");
					auditorium.print(""); // Clears the current file
					auditorium.flush();
					updateFile(auditorium, new DoubleLinkNode(1, 1), i - 1); // Updates the file starting from row 1, seat 1
					auditorium.close();
				}
				adminExits = true; // The admin has exited
			}
		}
		
		System.out.println("\n** Program terminated **");
		input.close();
	}
	
	/** Method that takes the user through the entire reservation process **/
	public static void reservationProcess(Scanner input, String username, String orderName, int chosenAuditorium)
	{
		if (orderName.equals("")) // If it's not adding on to an existing order
		{
			System.out.println("\n1. Auditorium 1 \n2. Auditorium 2 \n3. Auditorium 3\n");
			do
			{
				chosenAuditorium = Integer.parseInt(validateInput("Choose an auditorium (1-3): ", int.class, input)); // Ask for the auditorium
				if (chosenAuditorium < 1 || chosenAuditorium > 3)
					System.out.println("\n** Please only choose options 1-3");
			} while(chosenAuditorium < 1 || chosenAuditorium > 3);
		}
		displayAuditorium(chosenAuditorium - 1);
		int adultSeats = Integer.parseInt(validateInput("Enter the number of adult seats: ", int.class, input));
		int seniorSeats = Integer.parseInt(validateInput("Enter the number of senior seats: ", int.class, input));
		int childrenSeats = Integer.parseInt(validateInput("Enter the number of children seats: ", int.class, input));
		
		ArrayList<DoubleLinkNode> temporaryOrders = new ArrayList<DoubleLinkNode>();
		// Prompts user to enter row and seat number for each adult seat
		for (int i = 0; i < adultSeats; i++)
		{
			System.out.println("\n[Reservation for adult seat #"+(i + 1)+"]");
			int chosenRow = validate("Enter a row number: ", "\n** Please only choose a valid row number.", 1, numRowsAndSeats[chosenAuditorium - 1][0], input);
			int chosenColumn = validate("Enter a seat number: ", "\n** Please only choose a valid starting seat number.", 1, numRowsAndSeats[chosenAuditorium - 1][1], input);
			DoubleLinkNode adultSeat = new DoubleLinkNode(chosenRow, chosenColumn);
			adultSeat.setTypeSeat("Adult");
			adultSeat.setAuditorium(chosenAuditorium);
			temporaryOrders.add(adultSeat);
		}
		
		// Prompts user to enter row and seat number for each senior seat
		for (int i = 0; i < seniorSeats; i++)
		{
			System.out.println("\n[Reservation for senior seat #"+(i + 1)+"]");
			int chosenRow = validate("Enter a row number: ", "\n** Please only choose a valid row number.", 1, numRowsAndSeats[chosenAuditorium - 1][0], input);
			int chosenColumn = validate("Enter a seat number: ", "\n** Please only choose a valid starting seat number.", 1, numRowsAndSeats[chosenAuditorium - 1][1], input);
			DoubleLinkNode seniorSeat = new DoubleLinkNode(chosenRow, chosenColumn);
			seniorSeat.setTypeSeat("Senior");
			seniorSeat.setAuditorium(chosenAuditorium);
			temporaryOrders.add(seniorSeat);
		}
		
		// Prompts user to enter row and seat number for each child seat
		for (int i = 0; i < childrenSeats; i++)
		{
			System.out.println("\n[Reservation for child seat #"+(i + 1)+"]");
			int chosenRow = validate("Enter a row number: ", "\n** Please only choose a valid row number.", 1, numRowsAndSeats[chosenAuditorium - 1][0], input);
			int chosenColumn = validate("Enter a seat number: ", "\n** Please only choose a valid starting seat number.", 1, numRowsAndSeats[chosenAuditorium - 1][1], input);
			DoubleLinkNode childSeat = new DoubleLinkNode(chosenRow, chosenColumn);
			childSeat.setTypeSeat("Child");
			childSeat.setAuditorium(chosenAuditorium);
			temporaryOrders.add(childSeat);
		}
		
		/** Checks to see if every chosen seat isn't already reserved **/
		boolean areSeatsValid = true;
		for (DoubleLinkNode i: temporaryOrders)
			if (auditoriums[chosenAuditorium - 1][0].find(i) == null)
			{
				areSeatsValid = false;
				break;
			}
		
		// If the seats are valid and the user didn't enter 0 for all of them
		if (areSeatsValid && (adultSeats > 0 || seniorSeats > 0 || childrenSeats > 0))
		{
			if (orderName.equals("")) // If it's a brand new order
			{
				int orderNumber = 1;
				while (orders.get(username+""+orderNumber) != null)
					orderNumber++;
				orderName = username+""+orderNumber;
				orders.put(orderName, new LinkedList()); // Create a new order
			}
			for(DoubleLinkNode i: temporaryOrders)
			{
				auditoriums[chosenAuditorium - 1][1].addNode(auditoriums[chosenAuditorium - 1][0].deleteNode(i)); // Deletes seat from empty seats and adds it to reserved seats
				if (i.getTypeSeat().equals("Adult"))
					ticketSales[chosenAuditorium - 1][0] += 10;
				else if (i.getTypeSeat().equals("Senior"))
					ticketSales[chosenAuditorium - 1][1] += 7.5;
				else if (i.getTypeSeat().equals("Child"))
					ticketSales[chosenAuditorium - 1][2] += 5.25;
				orders.get(orderName).addNode(i.clone()); // Adds a cloned copy of it to customer's orders to prevent head/tail intertwining between LinkedList
			}
			System.out.println("\nSeat(s) successfully reserved!");
		} else
		{
			int numTickets = adultSeats + seniorSeats + childrenSeats;
			int[] seats = getBestAvailable(numTickets, chosenAuditorium - 1);
			if (seats != null)
			{
				// Prompts user to accept the offer for best available seats
				if (numTickets > 1)
					System.out.println("\nBest available seats: Row "+seats[0]+"; Seats "+seats[1]+"-"+(seats[1] + numTickets - 1));
				else
					System.out.println("\nBest available seats: Row "+seats[0]+"; Seat "+seats[1]);
				System.out.print("Would you like to reserve these seats? (Y/N): ");
				String answer = input.nextLine();
				if (answer.toUpperCase().equals("Y"))
				{
					if (orderName.equals("")) // If it's a brand new order
					{
						int orderNumber = 1;
						while (orders.get(username+""+orderNumber) != null)
							orderNumber++;
						orderName = username+""+orderNumber;
						orders.put(orderName, new LinkedList()); // Create a new order
					}
					
					reserveSeats(orderName, seats[0], seats[1], adultSeats, seniorSeats, childrenSeats, chosenAuditorium - 1); // Reserves the seats if the user answers Y
					System.out.println("\nSeat(s) successfully reserved!");
				} else
					System.out.println("\nSeat(s) not reserved.");
			} else
				System.out.println("\nCould not find best available seats.");
		}
	}
	
	/** Method to display all the orders under the account of a specific username **/
	static HashMap<String, LinkedList> orders= new HashMap<String, LinkedList>(); // Stores orders for each username in a LinkedList
	public static void displayOrders(String username)
	{
		System.out.println();
		int orderNumber = 1;
		while(orders.get(username+""+orderNumber) != null) // Loops through all the orders of a specific username
		{
			LinkedList currentOrder = orders.get(username+""+orderNumber);
			System.out.println(orderNumber+". Order #"+orderNumber);
			if (currentOrder != null && currentOrder.getSize() > 0) // If the order isn't empty
			{
				System.out.println("\tAuditorum #"+currentOrder.getHead().getAuditorium()); // Auditorium number
				int numAdults = 0, numSeniors = 0, numChildren = 0;
				DoubleLinkNode cur = currentOrder.getHead();
				while (cur != null)
				{
					System.out.println("\t\tRow "+cur.getRow()+", Seat "+cur.getSeat()); // Prints details of each seat
					if (cur.getTypeSeat().equals("Adult"))
						numAdults++;
					else if (cur.getTypeSeat().equals("Senior"))
						numSeniors++;
					else if (cur.getTypeSeat().equals("Child"))
						numChildren++;
					cur = cur.getNext();
				}
				// Prints total amount of adults, seniors, and children
				if (numChildren != 1)
					System.out.println("\tTOTAL: "+numAdults+" Adult(s), "+numSeniors+" Senior(s), "+numChildren+" Children\n");
				else
					System.out.println("\tTOTAL: "+numAdults+" Adult(s), "+numSeniors+" Senior(s), "+numChildren+" Child\n");
			}
			orderNumber++;
		}
	}
	
	/** Method for printing the receipt based on a given username **/
	public static void printReceipt(String username)
	{
		System.out.println();
		int orderNumber = 1;
		double totalPrice = 0.0;
		while(orders.get(username+""+orderNumber) != null) // Loops through all the orders of a specific username
		{
			LinkedList currentOrder = orders.get(username+""+orderNumber);
			System.out.println("Order #"+orderNumber+":");
			if (currentOrder != null && currentOrder.getSize() > 0) // If the order isn't empty
			{
				System.out.println("\tAuditorum #"+currentOrder.getHead().getAuditorium()); // Auditorium number
				int numAdults = 0, numSeniors = 0, numChildren = 0;
				DoubleLinkNode cur = currentOrder.getHead();
				while (cur != null) // Loops through all the seats in the order
				{
					System.out.println("\t\tRow "+cur.getRow()+", Seat "+cur.getSeat()); // Prints detail of current seat
					if (cur.getTypeSeat().equals("Adult"))
						numAdults++;
					else if (cur.getTypeSeat().equals("Senior"))
						numSeniors++;
					else if (cur.getTypeSeat().equals("Child"))
						numChildren++;
					cur = cur.getNext();
				}
				double price = 10 * numAdults + 7.5 * numSeniors + 5.25 * numChildren; // Calculates the price
				totalPrice+=price; // Adds to the total price
				if (numChildren != 1)
				{
					System.out.println("\tTotal: "+numAdults+" Adult(s), "+numSeniors+" Senior(s), "+numChildren+" Children");
					System.out.printf("\t$10 x "+numAdults+" Adult(s) + $7.50 x "+numSeniors+" Senior(s) + $5.25 x "+numChildren+" Children = $%.2f\n", price);
				} else
				{
					System.out.println("\tTotal: "+numAdults+" Adult(s), "+numSeniors+" Senior(s), "+numChildren+" Child");
					System.out.printf("\t$10 x "+numAdults+" Adult(s) + $7.50 x "+numSeniors+" Senior(s) + $5.25 x 1 Child = $%.2f\n", price);
				}
			}
			orderNumber++;
		}
		System.out.printf("\nTOTAL AMOUNT: $%.2f\n", totalPrice);
	}
	
	/** Method for validating input when prompting the user with min and max values**/
	public static int validate(String message, String error, int min, int max, Scanner input)
	{
		int toReturn = 0;
		do
		{
			toReturn = Integer.parseInt(validateInput(message, int.class, input));
			// Checks to see if it's outside the bounds of the auditorium
			if (toReturn < min || toReturn > max)
				System.out.println(error);
		} while (toReturn < min || toReturn > max); // Prompts user until it is valid
		
		return toReturn;
	}
	
	/** Method for validating input **/
	public static String validateInput(String message, Class<?> cls, Scanner input)
	{
		while (true) // Infinitely asks for input until valid input is given
		{
			try
			{
				System.out.print(message);
				String in = input.nextLine();
				if (cls == int.class) // If it's looking for an int
				{
					int checkIfThrowsException = Integer.parseInt(in); // Tests with a case that could possibly throw an exception
					return String.valueOf(checkIfThrowsException);
				} else if (cls == double.class) // If it's looking for a double
				{
					double checkIfThrowsException = Double.parseDouble(in); // Tests with a case that could possibly throw an exception
					return String.valueOf(checkIfThrowsException);
				}
			} catch (Exception e)
			{
				// Notifies user to input only of the valid type
				System.out.println("\n** Please only enter a value of type "+cls.getName()+"."); 
			}
		}
	}
	
	/** Method for reading in a given auditorium file into its proper LinkedLists **/
	public static void readAuditorium(Scanner auditorium, int chosen)
	{
		int numRows = 0;
		int numSeats = 0;
		while (auditorium.hasNextLine()) // While there are lines to read
		{
			String line = auditorium.nextLine();
			if (!line.equals(""))
			{
				numRows++;
				numSeats = line.length();
				// Loops through all the characters in the line and adds to appropriate LinkedList
				for (int i = 0; i < numSeats; i++)
				{
					DoubleLinkNode seatToAdd = new DoubleLinkNode(numRows, i + 1);
					seatToAdd.setAuditorium(chosen + 1);
					if (line.charAt(i) == '#')
						auditoriums[chosen][0].addNode(seatToAdd);
					else
						auditoriums[chosen][1].addNode(seatToAdd);
				}
			}
		}
		// Updates the maximum values of its row and seats
		numRowsAndSeats[chosen][0] = numRows;
		numRowsAndSeats[chosen][1] = numSeats;
	}
	
	/** Method for printing out the given auditorium to the console **/
	public static void displayAuditorium(int chosen)
	{
		System.out.print("\n  ");
		// Prints top row of numbers
		int columnNumber = 0;
		for (int i = 0; i < numRowsAndSeats[chosen][1]; i++) 
		{
			columnNumber+=1;
			if (columnNumber == 10)
				columnNumber = 0; // Resets back to 0 when it reaches 10 to prevent double digits in labeling
			System.out.print(columnNumber);
		}
		
		System.out.println();
		for (int i = 0; i < numRowsAndSeats[chosen][0]; i++) // For each row
		{
			System.out.print((i + 1)+" "); // Prints row number
			for (int j = 0; j < numRowsAndSeats[chosen][1]; j++) // For each seat in the row
				if (auditoriums[chosen][0].find(new DoubleLinkNode(i + 1, j + 1)) != null) // If the seat can be found in the emtpy seats LinkedList
					System.out.print("#"); // Print # if empty seat
				else
					System.out.print("."); // Print . if reserved seat
			System.out.println();
		}
		System.out.println();
	}
	
	/** Method for getting the best available seats in a given auditorium based on the number of tickets **/
	public static int[] getBestAvailable(int numTickets, int chosen)
	{
		int[] reservation = null;
		DoubleLinkNode curr = auditoriums[chosen][0].getHead();
		int centerSeat = (int) Math.ceil((double) numRowsAndSeats[chosen][1]/2); // Finds the center seat
		int centerRow = (int) Math.ceil((double) numRowsAndSeats[chosen][0]/2); // Finds the center row
		double distanceFromCenterSeat = 100000000;
		while (curr != null)
		{
			DoubleLinkNode curr2 = curr;
			int numSeats = 1;
			while (curr2.getNext() != null && curr2.getNext().getRow() == curr2.getRow() && curr2.getNext().getSeat() == curr2.getSeat() + 1 && numSeats < numTickets)
			{
				numSeats++;
				curr2 = curr2.getNext();
			}
			
			// If current seat matches given criteria and is closest to the center of the auditorium (seat-wise and row-wise)
			if (numSeats == numTickets)
			{
				DoubleLinkNode centerInGroup = curr;
				//System.out.print(centerInGroup.getRow() +" "+centerInGroup.getSeat() + " ");
				int j = 0;
				if (numTickets%2 == 0)
					j = 1;
				for (int i = j; i < numTickets/2; i++)
					centerInGroup = centerInGroup.getNext(); // Gets the center seat in the group of seats
				//System.out.println(centerInGroup.getSeat());
				if (Math.sqrt(Math.pow(centerSeat - centerInGroup.getSeat(), 2) + Math.pow(centerRow - centerInGroup.getRow(), 2)) <= distanceFromCenterSeat)
				{
					distanceFromCenterSeat = Math.sqrt(Math.pow(centerSeat - centerInGroup.getSeat(), 2) + Math.pow(centerRow - centerInGroup.getRow(), 2)); // Updates distance from center seat to that of the current best seat
					// Updates reservation to the current best seats
					reservation = new int[2];
					reservation[0] = curr.getRow();
					reservation[1] = curr.getSeat();
				}
			}
			curr = curr.getNext();
		}
		return reservation; // Null is returned if no seats were found in availability based on given criteria
	}
	
	/** Method for reserving seats based on given starting seat and number of tickets in a given auditorium **/
	public static void reserveSeats(String orderName, int row, int seat, int numAdults, int numSeniors, int numChildren, int chosen)
	{
		int adultSeatsUsed = 0, seniorSeatsUsed = 0;
		for (int i = 0; i < (numAdults + numSeniors + numChildren); i++) // Loops through all the seats in the section
		{
			DoubleLinkNode seatToAdd = auditoriums[chosen][0].deleteNode(new DoubleLinkNode(row, seat + i));
			if (adultSeatsUsed < numAdults) // If there are still adult seats left
			{
				adultSeatsUsed++;
				seatToAdd.setTypeSeat("Adult"); // Set it to be an adult seat
				ticketSales[chosen][0] += 10; // Add to the total price for adult seats
			}
			else if (seniorSeatsUsed < numSeniors)
			{
				seniorSeatsUsed++;
				seatToAdd.setTypeSeat("Senior"); // Set it to be a senior seat
				ticketSales[chosen][1] += 7.5; // Add to the total price for senior seats
			}
			else
			{
				seatToAdd.setTypeSeat("Child"); // Set it to be a child seat
				ticketSales[chosen][2] += 5.25; // Add to the total price for children seats
			}
			auditoriums[chosen][1].addNode(seatToAdd); // Deletes seat from empty seats and adds it to reserved seats
			orders.get(orderName).addNode(seatToAdd.clone());
		}
	}
	
	/** Method for unreserving a seat from a specific order and auditorium **/
	public static void unreserveSeat(String orderName, DoubleLinkNode seat, int chosen)
	{
		auditoriums[chosen][0].addNode(auditoriums[chosen][1].deleteNode(seat)); // Removes from auditorium
		orders.get(orderName).deleteNode(seat); // Removes from user's order
		if (seat.getTypeSeat().equals("Adult"))
			ticketSales[chosen][0] -= 10;
		else if (seat.getTypeSeat().equals("Senior"))
			ticketSales[chosen][1] -= 7.5;
		else if (seat.getTypeSeat().equals("Child"))
			ticketSales[chosen][2] -= 5.25;
	}
	
	/** Method to recursively update the given auditorium file **/
	public static void updateFile(PrintWriter auditorium, DoubleLinkNode n, int chosen)
	{
		if (auditoriums[chosen][0].find(n) != null) // If current seat is in the empty seats LinkedList
			auditorium.print("#");
		else // Otherwise, if current seat is in the reserved seats LinkedList
			auditorium.print(".");
		
		int row = n.getRow(), seat = n.getSeat() + 1; // Increments to the next seat
		// Moves to next row if all seats in the current row have been printed
		if (seat > numRowsAndSeats[chosen][1])
		{
			row++;
			seat = 1;
			if (row <= numRowsAndSeats[chosen][0])
				auditorium.println();
		}
		
		if (row <= numRowsAndSeats[chosen][0]) // If it hasn't already printed the last seat
			updateFile(auditorium, new DoubleLinkNode(row, seat), chosen); // Print the next seat
	}
}

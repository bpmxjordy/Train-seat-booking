package trainBooking;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TrainBooking {

	static Scanner console = new Scanner(System.in);

	public static void main(String[] args) throws IOException {

		String input = "";
		boolean retry = false;

		while (retry == false) {
			System.out.println("Welcome to Train Booking!");

			System.out.println("1 | Reserve a seat	2 | Cancel a booking"); 						// Get the user to select a menu
			input = console.nextLine();
			if (input.equals("1")) { 																// Running the function for their selection
				SeatBooking();
			} else if (input.equals("2")) {
				CancelBooking();
			} else {
				System.out.println("Please try again!");
				input = "";
				main(null);
			}
		}

	}

	private static void SeatBooking() throws IOException { 											// The function that finds the seat that the user is looking for
		
		ArrayList<String> compareInput = new ArrayList<String>();
		ArrayList<String> compareFile = new ArrayList<String>();
		Path path = Paths.get("seats.txt"); 														// getting the path to the file
		long lines = Files.lines(path).count(); 													// Finding out how long the file is
		ArrayList<String> linesToChange = new ArrayList<String>();
		String email;
		boolean match = false;
		compareInput = UserInput(compareInput); 													// This is the function that gets the users input
		for (int i = 0; i < lines; i++) { 															// making the program run through each line in the text file until it finds a
																									// matching seat.
			compareFile = Compare(i); 																// splitting the line of text into an ArrayList so we can compare it with the
																									// other ArrayList.
			if (compareFile.get(1).equals(compareInput.get(0)) && compareFile.get(2).equals(compareInput.get(1))
					&& compareFile.get(3).equals(compareInput.get(2)) && compareFile.get(4).equals(compareInput.get(3))
					&& (Double.parseDouble(compareInput.get(4))) > Double.parseDouble(compareFile.get(5))
					&& compareFile.get(6).equals("free")) {
				match = true;
				linesToChange.add(Integer.toString(i));												// Putting lines of the seats that match the request into
																									// another ArrayList.
			}
		}

		if (match == true) {
			int choice = Choice(linesToChange); 													// Getting the line that they want to book
			choice--;
			System.out.println("Please enter your email address: ");
			email = console.next();

			Reserve(email, choice, linesToChange); 													// Running the function that replaces the original line with the new
																									// line that has their email on

			System.out.println("Thankyou for booking with us.");
			System.out.println("");
			main(null);
		} else {
			System.out.println("Sorry we could not find a suitable seat.");
		}

	}

	private static void Reserve(String email, int choice, ArrayList<String> linesToChange) throws IOException { // This is the function that replaces the line with the new line

		String line;
		String[] lineText;
		int lineNumber = Integer.parseInt(linesToChange.get(choice)); 								// finding the text file line they want to book
		line = Files.readAllLines(Paths.get("seats.txt")).get(lineNumber); 							// putting the line into a string
		lineText = line.split(" "); 																// Splitting the string into an array

		lineText[6] = email; 																		// Replacing "free" with their email address
		String fileLine = "";
		for (int i = 0; i < lineText.length; i++) { 												// running through the array and placing it into one string
			fileLine += lineText[i];
			fileLine += " ";
		}

		Path path = Paths.get("seats.txt"); 														// getting the path of the text file
		List<String> lines = Files.readAllLines(path); 												// Placing the contents of the text file into an array
		lines.set(lineNumber, fileLine); 															// Replacing the line we want to book with the booked line
		Files.write(path, lines); 																	// writing the new information to the text file

	}

	private static void CancelBooking() throws IOException { 										// The function for cancelling a booking
		
		String email;
		System.out.println("Please enter your email address: "); 									// Used to find the bookings for the email address
		email = console.next();

		ArrayList<String> linesToChange = new ArrayList<String>();
		ArrayList<String> compareFile = new ArrayList<String>();
		Path path = Paths.get("seats.txt");
		long lines = Files.lines(path).count();

		boolean match = false;

		for (int i = 0; i < lines; i++) {
			compareFile = Compare(i);
			if (compareFile.get(6).toUpperCase().equals(email.toUpperCase())) { 					// If one of the lines of text ends with their email address then this will 
				linesToChange.add(Integer.toString(i));												// add the line number to an ArrayList
				match = true;	
			} else if (Compare(i).get(5) == "false") {
				match = false;
			}
		}

		if (match == true) {
			String line;
			int input = 0;

			for (int i = 0; i < linesToChange.size(); i++) {
				line = Files.readAllLines(Paths.get("seats.txt")).get(Integer.parseInt(linesToChange.get(i)));
				System.out.printf("%s | %s", i + 1, line); 											// Formatting the response
				System.out.println(" ");
			}

			System.out.println(" ");
			System.out.print("Please select one of the above options: ");
			input = console.nextInt();

			Cancel(input, linesToChange); 															// Calling the function that will replace their email with "free"
		} else {
			System.out.println("Sorry there are no bookings for this email address."); 				// saying there are no more bookings for their email address if there isn't a match
		}

	}

	private static void Cancel(int choice, ArrayList<String> linesToChange) throws IOException {	// This is the function that cancels the booking they have previously made

		String line;
		String[] lineText;
		int lineNumber = Integer.parseInt(linesToChange.get(choice - 1));
		line = Files.readAllLines(Paths.get("seats.txt")).get(lineNumber);
		lineText = line.split(" ");

		lineText[6] = "free"; 																		// replacing their email with "free"
		String fileLine = "";
		for (int i = 0; i < lineText.length; i++) {
			fileLine += lineText[i];
			fileLine += " ";
		}

		Path path = Paths.get("seats.txt");
		List<String> lines = Files.readAllLines(path);
		lines.set(lineNumber, fileLine);
		Files.write(path, lines); 																	// placing the updated text into the file

	}

	public static ArrayList<String> UserInput(ArrayList<String> compareInput) throws IOException { // This is the function that gets the users input
		
		String input = "";
		compareInput.clear();
		while (input != "t") {
			System.out.println("What type of seat would you like?");
			System.out.println("1 | first    2 | Standard");

			input = InputVal(input = console.next(), compareInput);

			if (input == "1") {
				compareInput.add("1ST");
				input = "";
			} else if (input == "2") {
				compareInput.add("STD");
				input = "";
			}

			System.out.println("1 | Window    2 | No Window");
			input = InputVal(input = console.next(), compareInput);

			if (input == "1") {
				compareInput.add("true");
				input = "";
			} else if (input == "2") {
				compareInput.add("false");
				input = "";
			}

			System.out.println("1 | Aisle    2 | No Aisle");
			input = InputVal(input = console.next(), compareInput);

			if (input == "1") {
				compareInput.add("true");
				input = "";
			} else if (input == "2") {
				compareInput.add("false");
				input = "";
			}

			System.out.println("1 | Table    2 | No Table");
			input = InputVal(input = console.next(), compareInput);

			if (input == "1") {
				compareInput.add("true");
				input = "t";
			} else if (input == "2") {
				compareInput.add("false");
				input = "t";
			}

			System.out.println("What is your max price");
			System.out.println("1 = £25 | 2 = £30 | 3 = £50");
			input = InputVal3(input = console.next(), compareInput);

			if (input == "1") {
				compareInput.add("25");
				input = "t";
			} else if (input == "2") {
				compareInput.add("30");
				input = "t";
			} else if (input == "3") {
				compareInput.add("50");
				input = "t";
			}

			System.out.println(" ");

		}
		return compareInput;

	}

	private static String InputVal(String string, ArrayList<String> compareInput) throws IOException { // used to validate the users input
		
		if (string.equals("1")) {
			return "1";
		} else if (string.equals("2")) {
			return "2";
		} else {
			System.out.println("Please try again.");
			SeatBooking();
		}
		return string;
	}

	private static String InputVal3(String string, ArrayList<String> compareInput) throws IOException { // used to validate the users input
		
		if (string.equals("3")) {
			return "3";
		} else if (string.equals("2")) {
			return "2";
		} else if (string.equals("1")) {
			return "1";
		} else {
			System.out.println("Please try again.");
			SeatBooking();

		}
		return string;
	}

	public static ArrayList<String> Compare(int x) throws IOException { 								// Getting the line from the text file and placing it into an Array
		
		ArrayList<String> compareFile = new ArrayList<String>();
		String line;
		String[] lineText;
		line = Files.readAllLines(Paths.get("seats.txt")).get(x);
		lineText = line.split(" ");

		for (int i = 0; i < 7; i++) {
			compareFile.add(lineText[i]); 																// putting the array into an ArrayList
		}
		return compareFile;
	}

	public static int Choice(ArrayList<String> linesToChange) throws IOException { 						// This is the function that Formats the un-booked seats and displays them to the user and allows the
																										// user to choose one.
		String line;
		int input = 0;

		for (int i = 0; i < linesToChange.size(); i++) {
			line = Files.readAllLines(Paths.get("seats.txt")).get(Integer.parseInt(linesToChange.get(i))); // finds the line that the user has chosen
			System.out.printf("%s | %s", i + 1, line);
			System.out.println(" ");
		}

		System.out.println(" ");
		System.out.print("Please select one of the above options: ");
		input = console.nextInt();

		return input;
	}
}

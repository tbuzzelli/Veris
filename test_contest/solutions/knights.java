import java.util.Scanner;


public class knights {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scanner scan = new Scanner(System.in);

		int loops = scan.nextInt(); //scan in the number of loops

		for(int i=0;i<loops;i++){ //loop over each input

			String input = scan.next(); //scan in the next input
			String output = input; //create the output string

			//make sure that you check the flags and build the
			//output string in the correct order.

			if(input.contains("1")){ //knights are only represented by 1
				output = output+" knights";
			}
			if(input.contains("9") || input.contains("8") || 
			input.contains("7") || input.contains("6") || 
			input.contains("5")){ //pirates are anything that doubles into ten or more
			//meaning the digits 5-9
				output = output+" pirates";
			}
			if(input.contains("0")){ //ninjas
				output = output+" ninjas";
			}
			
			
			//print the output string
			System.out.println(output);

		}

	}

}

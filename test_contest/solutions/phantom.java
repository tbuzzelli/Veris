import java.util.Scanner;

public class phantom 
{
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		int n = scan.nextInt(); // Number of test cases
		for (int i = 0; i < n; i++)
		{
			String command = scan.next(); // Either BANG or RING
			int t = scan.nextInt(); // Number of times
			String message;
			if (command.equals("RING"))
				message = "Hey, Superman!";
			else
				message = "Are you jelly?";
			for (int j = 0; j < t; j++)
				System.out.println(message); // Output the shout t times
		}
	}
}

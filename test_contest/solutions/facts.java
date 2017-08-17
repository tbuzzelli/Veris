import java.util.Scanner;

public class facts 
{
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		int n = scan.nextInt(); // Number of test cases
		for (int i = 0; i < n; i++)
			System.out.println(-1 * scan.nextInt()); // Print opposite of each number
	}
}

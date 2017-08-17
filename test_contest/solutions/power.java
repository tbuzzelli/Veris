
import java.util.Scanner;

public class power {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		
		int cases = scan.nextInt();
		for(int i = 0;i<cases;i++){
			int pow = scan.nextInt();
			System.out.println((int)Math.pow(2, pow) - 1);
		}

	}

}

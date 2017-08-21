import java.util.Arrays;
import java.util.Scanner;
// Author: Derek Goodwin

public class shoes {
	// maximum shoe size difference that two seahorses sharing one pair of
	// shoes are allowed to have
	static final int MAX_DIFF = 2;
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		// named after a variable described in the problem
		int T = in.nextInt();
		for(int t = 1; t <= T; t++){
			
			// named after a variable described in the problem
			int N = in.nextInt();
			
			int[] shoeSizes = new int[N];
			for(int n = 0; n < N; n++){
				shoeSizes[n] = in.nextInt();
			}
			Arrays.sort(shoeSizes);
			
			// now that the shoeSizes array is sorted,
			// each seahorse is going to be okay with sharing a pair of shoes
			// with the next highest shoe size, if with any larger shoe size at
			// all. likewise, each seahorse is going to be okay with sharing a
			// pair of shoes with the next lowest shoe size, if with any
			// smaller shoe size at all.
			// So it never makes more sense to try to pair non-adjacent shoe
			// sizes than it makes sense to pair adjacent shoe sizes.
			int amtPairsNeeded = 0;
			for(int n = 0; n < N; n++){
				
				// when we pair two seahorses to one shoe, we need to make sure
				// n increases by two for the next pass of the loop.
				if(n + 1 < N && shoeSizes[n] + MAX_DIFF >= shoeSizes[n+1]){
					n++;
				}
				
				// one iteration of this loop represents one pairing
				amtPairsNeeded++;
			}
			
			// that's just about it.
			System.out.println("Litter #" + t + ": " + amtPairsNeeded);
		}
	}
}

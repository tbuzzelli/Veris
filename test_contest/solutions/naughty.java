//Author: Gabriel Pita
import java.util.*;

public class naughty {
    
    public static void main(String[] args) {
        
        Scanner in = new Scanner(System.in);
        
        int y = in.nextInt();
        
        for(int year = 0; year < y; year++){
            
            int h = in.nextInt();
            int d = in.nextInt();
            
            //saves the number of times each house is visited. Houses will need to adjusted to be 0-indexed
            int[] visitsPerHouse = new int[h];
            
        /*
             
            It is too slow to process the days individually. In the worst case with h= 10^5
            and d = 10^5 and with every house being pranked almost everyday there could be on the order
            of 5*10^9 operations (h*d divided by the minimum k of 2). Anything with 10^9 operations or 
            above will usually be too slow in a programming contest, so a faster approach is needed.   
            Since there are only about 10 possible values for k, if we made a solution that ran in
            around k*d operations (at worst 10^6), it will almost definitely run in time.
            
            To make a solution that runs in around k*d operations we can do some book keeping 
            as we read in the information about each day. For each day we will save the houses Santa
            starts at and ends at (derived with math), and categorize those by k. All of this information
            can be saved in a k by h 2D array of integers (startsAndEnds). Later we will use this information to
            obtain the number of times each house is visited, which will be saved in visitsPerHouse.
             
         */
            //startsAndEnds[0] and startsAndEnds[1] are not used.
            int[][] startsAndEnds = new int[11][h]; 
            
            for(int day =0; day<d; day++){
                int s = in.nextInt()-1; //0-indexing
                int k = in.nextInt();
                int p = in.nextInt();
                
                //This will be the first house in the sequence NOT to be visited (the actual is s+k*(p-1)
                //This index can be way over h if p is large
                int endHouse = s+k*p;
                
                /*
                 These increments are a way of saving ranges. Under a particular k, and house i
                 startsAndEnds[k][i] will store the number of ranges that start at house i with this k,
                 or if startsAndEnds[k][i] is negative the absolute value will be the number of ranges
                 that end at house i. In this setup starts and ends cancel each other out.
                 
                 The reason endHouse is not the actual last house in the sequence is due to the
                 ways ranges are set up here. Since incrementing at an index includes a house
                 for a range, decrementing at an index excludes an house. Since I want the last house of the
                 sequence to be included in the range, I want to decrement instead at what would have been the
                 next house in the sequence.
                 */
                                
                startsAndEnds[k][s]++;
                if(endHouse < h)
                	startsAndEnds[k][endHouse]--;
            }
            
            for(int k = 2; k <= 10; k++){
            	
            	//For each k we partition the houses based of their value mod k.
            	//This is because as Santa is pranking housing, he will always 
            	//prank houses with the same indicies mod k in the same day.
            	for(int modk =0; modk < k; modk++){
            		
            		// Notice that house is incremented by k.
            		// These loops will cover every house for any value of k.
            		
            		int runningVisitCount = 0;
            		for(int house = modk; house < h; house+= k){
            			

            			//runningVisitCount will tell us how many ranges have started before or at this index
            			//that have not yet ended. A range in this context is a sequence of houses that Santa
            			//visits in a single day. runningVisitCount at this time is exactly how many times
            			//index house is visited on days with the current k in the outerloop.
            			
            			runningVisitCount += startsAndEnds[k][house];
            			visitsPerHouse[house] += runningVisitCount;
            		}
            	}
            	
            	
            }
            
            //looping every all houses, and saving the maximum
            int maxhouse = 0;
            int maxcount = Integer.MIN_VALUE;
            for(int house = 0; house < h; house++){
            	
            	if(visitsPerHouse[house] > maxcount){
            		maxcount = visitsPerHouse[house];
            		maxhouse = house;
            	}
            }
            
            System.out.println("House " +(maxhouse+1)+" should get the biggest and best gift next Christmas.");
            
        }
        
        in.close();
    }

}

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class yertle {

	public static void main(String[] args) {
		/*
		 * For this problem we will iterate over all the positions
		 * in the pyramid, compute the minimum strength, required
		 * for each position, and place the weakest turtle that
		 * has that strength or greater in that spot.
		 * If we are ever unable to fill a position, we're done. 
		 */
		Scanner scan=new Scanner(System.in);

		//scan in number of ponds to process
		int p=scan.nextInt();

		//loop through ponds
		for(int i=0;i<p;i++){

			//scan in number of turtles and their weight
			int n=scan.nextInt();
			int w=scan.nextInt();

			//"strengths" will hold the strengths of each turtle
			ArrayList<Integer> strengths=new ArrayList<Integer>();
			for(int j=0;j<n-1;j++){
				strengths.add(scan.nextInt());
			}
			Collections.sort(strengths);

			//strenghtRequired[j][k] will be the minimum strength a turtle needs to be put in position (j,k)
			double[][] strenghtRequired=new double[141][141];

			//"levels" keeps track of how many levels of the pyramid we have completed
			int levels=1;

			loop:for(int j=1;j<141;j++){
				for(int k=0;k<=j;k++){

					//the strength required for each position is half of the sum of the strengths required for
					//the two positions above it plus half of the weights of the turtles in those positions
					if(k==0){
						strenghtRequired[j][0]=(strenghtRequired[j-1][0]+w)*.5;
					}else if(k==j){
						strenghtRequired[j][k]=(strenghtRequired[j-1][k-1]+w)*.5;
					}else{
						strenghtRequired[j][k]=(strenghtRequired[j-1][k-1]+strenghtRequired[j-1][k])*.5+w;
					}

					//see if there is a turtle who has enough strength to fit this position
					//if there is, remove that turtle from the list
					boolean found=false;
					for(int l=0;l<strengths.size();l++){
						if(strengths.get(l)>=strenghtRequired[j][k]){
							strengths.remove(l);
							found=true;
							break;
						}
					}
					//if the position can't be filled, we're done
					if(!found){
						break loop;
					}
				}
				//after we've filled all the spots in this level, add one to our counter 
				levels++;
			}

			//print out the answer
			if(levels==1){
				System.out.println("Pond #"+(i+1)+": Poor Yertle.");
			}else{
				System.out.println("Pond #"+(i+1)+": The pyramid is "+levels+" turtles high!");
			}

		}
	}

}

/*
	Solution to HSPT 2017 - Every Day I'm Shuffling
	Solution Idea - A permutation of numbers from 1 to N can be 
		represented as a set of disjoint cycles. For example, the
		permutation {2,3,1,4} contains two cycles: 1->2->3->1, and
		4->4. Since each card at the beginning of this process starts
		in one of these cycles, it's final position depends only on the
		length of the cycle. So find the length of each cycle, and the
		final position for a card in a cycle will be the remainder after K 
		(the number of days they have been shuffling) is divided by the 
		cycle length steps forward from it's initial position in the cycle. 
 */
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class shuffling {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int T = scan.nextInt();
		while(T-->0){
			int N = scan.nextInt(), K = scan.nextInt();
			
			int[] perm = new int[N], sol = new int[N];
			boolean[] seen = new boolean[N];
			for(int i=0;i<N;i++)perm[i] = scan.nextInt()-1;
			for(int i=0;i<N;i++){
				if(seen[i])continue;
				int idx = i;
				int count = 0;
				ArrayList<Integer> temp = new ArrayList<>();
				while(!seen[idx]){
					seen[idx] = true;
					count++;
					temp.add(idx);
					idx = perm[idx];
				}
				idx = 0;
				for(int x : temp)
					sol[x] = temp.get(((idx++)+K)%count)+1;
			}
			
			for(int i : sol)System.out.print(i+" ");
			System.out.println();
		}
	}
}

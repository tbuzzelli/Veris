import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

// O(n) solution to wheel

public class wheel {
    
    @SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int T = in.nextInt();

		for (int t = 1; t <= T; t++) {
			int n = in.nextInt();
			in.nextLine(); // consume the line break after n
			
			// Build adjacency list of 3*n, one node for each phrase, and potentially one node for the first and last word of each phrase
			ArrayList<Integer>[] adj = new ArrayList[3 * n];
			for(int i=0;i<3*n;i++)
				adj[i] = new ArrayList<>();
			
			HashMap<String, Integer> map = new HashMap<>();
			// Start off ID's for words at n because the first n nodes will be the phrases (the next 2*n will be words)
			int ID = n;
			for (int i = 0; i < n; i++) {
				String s = in.nextLine();
				String[] split = s.split(" ");
				String firstWord = split[0], lastWord = split[split.length-1];
				
				// Assign ID's to the first/last word if they don't have them yet
				if(!map.containsKey(firstWord))
					map.put(firstWord, ID++);
				if(!map.containsKey(lastWord))
					map.put(lastWord, ID++);
				
				// add an edge from the start word to the phrase, and from the phrase to the end word
				int first = map.get(firstWord), last = map.get(lastWord);
				adj[first].add(i);
				adj[i].add(last);
			}
			
			// then toposort to find the longest path in this graph
			int[] indeg = new int[3*n];
			for(int i=0;i<3*n;i++) {
				for(int e : adj[i]) {
					indeg[e]++;
				}
			}
			ArrayDeque<Integer> q = new ArrayDeque<>();
			for(int i=0;i<3*n;i++) {
				if(indeg[i] == 0) {
					q.offer(i);
				}
			}
			int[] maxLen = new int[3*n];
			Arrays.fill(maxLen, 1); // Default everything to length 1 (just itself)
			int max = 1;
			
			while(!q.isEmpty()) {
				int v = q.poll();
				max = Math.max(max, maxLen[v]);
				for(int e : adj[v]) {
					// Try extending our chain that ends at node v to node e, this would have length maxLen[v]+1
					maxLen[e] = Math.max(maxLen[e], maxLen[v]+1);
					indeg[e]--;
					if(indeg[e] == 0) {
						q.offer(e);
					}
				}
			}
			// Every phrase in the path will have one meta-node after it (the node for it's last word), and the first
			// phrase will have one before it, so our length is 2*answer+1, so subtract one and divide by 2
			System.out.printf("Puzzle #%d: %d\n", t, (max-1)/2);
		}
	}
}

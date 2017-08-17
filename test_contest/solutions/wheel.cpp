#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <unordered_map>
#include <vector>
#include <queue>
#include <algorithm>
using namespace std;
// AUTHOR: Derek Goodwin
// Recommended tab length: 4

// no phrase in this problem is allowed to not have spaces. so, searching for
// the first/last spaces is a good way to get the first and last words for each
// phrase
string getFirstWord(string str){
	int i;
	for(i = 0; i < str.length(); i++){
		if(str[i] == ' ') break;
	}
	return str.substr(0, i);
}
string getLastWord(string str){
	int i;
	for(i = str.length() - 1; i >= 0; i--){
		if(str[i] == ' ') break;
	}
	return str.substr(i+1, str.length());
}

int main(){
	// STYLE NOTE:
	// T, N, numStrs are all the "bounds" versions of the variables t, n, and
	// ns respectively
	int T, t, N, n, numStrs, ns, ans;
	string nothing, line, first, last;
	cin >> T;
	for(t = 1; t <= T; t++){
		// extra getline added to prevent problems
		cin >> N; getline(cin, nothing);

		vector<vector<int>> nexts(2*N);

		// data stuff we use later
		vector<int> prevs(2*N);
		vector<int> dist(2*N);
		for(n = 0; n < 2*N; n++){
			prevs.push_back(0);
			dist.push_back(0);
		}

		queue<int> q;

		// hash map will be used to map strings to integers
		unordered_map<string, int> hashmap(4*N);
		numStrs = 0;
		for(n = 0; n < N; n++){
			getline(cin, line);
			first = getFirstWord(line);
			last = getLastWord(line);
			// if these words aren't in the hashmap yet, add them
			if(hashmap[first] == 0){
				numStrs++;
				hashmap[first] = numStrs;
			}
			if(hashmap[last] == 0){
				numStrs++;
				hashmap[last] = numStrs;
			}
			nexts[hashmap[first]-1].push_back(hashmap[last]-1);
			// add one to the prevs of the destination string. this is for the
			// topsort that will happen later.
			prevs[hashmap[last]-1]++;
		}

		// if prevs[ns] is zero, it's a starting node in the topsort
		for(ns = 0; ns < numStrs; ns++){
			if(prevs[ns] == 0){
				q.push(ns);
			}
		}

		// topsort time!
		ans = 0;
		while(q.size() > 0){
			ns = q.front();
			for(n = 0; n < nexts[ns].size(); n++){
				prevs[nexts[ns][n]]--;
				dist[nexts[ns][n]] = max(dist[nexts[ns][n]], dist[ns] + 1);
				ans = max(ans, dist[nexts[ns][n]]);
				if(prevs[nexts[ns][n]] == 0){
					q.push(nexts[ns][n]);
				}
			}
			q.pop();
		}

		stringstream outs;
		outs << "Puzzle #" << t << ": " << ans << "\n";
		cout << outs.str();
	}
}

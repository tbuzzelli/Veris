package com.verisjudge.checker.custom;

import java.util.ArrayList;
import java.util.HashSet;

import com.verisjudge.Verdict;
import com.verisjudge.checker.Checker;
import com.verisjudge.utils.FastScanner;

public class Nwerc2014H extends Checker {

	final int MIN_FREQ = 0;
	final int MAX_FREQ = 1000000000 - 1;
	int N, from, to;
	int[][][] channel;
	HashSet<Integer>[] usedChannels;
	ArrayList<Integer>[] adj;
		
	@SuppressWarnings("unchecked")
	void init() {
		channel = new int[2][10005][2];
		usedChannels = new HashSet[2];
		usedChannels[0] = new HashSet<>();
		usedChannels[1] = new HashSet<>();
		adj = new ArrayList[10005];
		for (int i = 0; i < adj.length; i++)
			adj[i] = new ArrayList<>();
	}
    @Override
    public Verdict check(FastScanner input, FastScanner pScanner, FastScanner ansScanner) {
        init();
        try {
	        N = input.nextInt();
	        for (int i = 0; i < N - 1; i++) {
	        	from = input.nextInt();
	        	to = input.nextInt();
	        	from--;
	        	to--;
	        	adj[from].add(to);
	        	adj[to].add(from);
	        }
	        
	        try {
		        for (int i = 0; i < N; i++) {
		        	for (int j = 0; j < 2; j++) {
		        		channel[1][i][j] = ansScanner.nextInt();
		        		if (channel[1][i][j] < MIN_FREQ || channel[1][i][j] > MAX_FREQ)
		        			return Verdict.INTERNAL_ERROR;
		        	}
		        }
	        } catch (Exception e2) {
	        	return Verdict.INTERNAL_ERROR;
	        }
	        
	        for (int i = 0; i < N; i++) {
	        	for (int j = 0; j < 2; j++) {
	        		channel[0][i][j] = pScanner.nextInt();
	        		if (channel[0][i][j] < MIN_FREQ || channel[0][i][j] > MAX_FREQ)
	        			return Verdict.WRONG_ANSWER;
	        	}
	        }
	        Verdict v = check();
	        
	        if (pScanner.hasNext()) {
	        	v = Verdict.WRONG_ANSWER;
	        }
	        
	        return v;
        } catch (Exception e) {
        	return Verdict.WRONG_ANSWER;
        }
    }

    @SuppressWarnings("unchecked")
	public Verdict check() {
    	for (int node = 0; node < N; node++) {
    		HashSet<Integer>[] here = new HashSet[2];
    		here[0] = new HashSet<>();
    		here[1] = new HashSet<>();
    		for (int j = 0; j < 2; j++) {
    			for (int k = 0; k < 2; k++) {
    				here[k].add(channel[k][node][j]);
    			}
    		}
    		for (int next : adj[node]) {
    			boolean[] connected = new boolean[2];
    			for (int j = 0; j < 2; j++) {
    				for (int k = 0; k < 2; k++) {
    					if (here[k].contains(channel[k][next][j])) {
    						usedChannels[k].add(channel[k][next][j]);
    						connected[k] = true;
    					}
    				}
    			}
    			if (!connected[0]) return Verdict.WRONG_ANSWER;
    			if (!connected[1]) return Verdict.WRONG_ANSWER;
    		}
    	}
    	
    	if (usedChannels[0].size() != usedChannels[1].size())
    		return Verdict.WRONG_ANSWER;
    	return Verdict.CORRECT;
    }
     

}
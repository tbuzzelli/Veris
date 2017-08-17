"""
	Solution to HSPT 2017 - Naughty or Nice
	Solution idea - On a particular day, we can easily determine which
		house is the first and the last house to be pranked using simple
		math in constant time. Unfortunately, we can not update all the houses
		in the range because with the large bounds of the problem, this will take
		far too long to compute and keep count. However, something very important
		to note is that the maximum skip size is 10, so we can abuse this fact.
		
		Our final runtime will be O(n), but Python is somewhat slow when it comes to
		these types of operations so the same solution in Java/C will be much faster.
"""

T = int(input())
while T > 0:
	h, d = map(int, input().split(" "))
	best = 0
	# Sol stores the final answer, add and dele are used to compute final answer
	sol = [0]*h
	add = [0]*11
	dele = [0]*11
	
	# Initialize add and dele as 2D arrays of size [11][h], since 1 - 10 are valid skip sizes
	for i in range(11):
		add[i] = [0]*h
		dele[i] = [0]*h
	for i in range(d):
		s,k,p=map(int, input().split(" "))
		s-=1
		# +1 since we know a range of updates starts at index s
		add[k][s]+=1
		
		# +1 to denote that we need to stop 1 range at index (p-1)*k+s
		if (p-1)*k + s < h:
			dele[k][(p-1)*k+s]+=1

	# Sweep through each house in order
	for i in range(h):
		# Sweep through all possible skip sizes
		for j in range(1,11):
			# If add[j][i] is non-zero we know santa hit this house with a skip size of j, add[j][k] times
			sol[i]+=add[j][i]
			
			# dele[j][i] ranges ended at this position, so get rid of them
			add[j][i]-=dele[j][i]
			
			# Santa moves j positions forward, so we need to update the add array to account for this
			if(i+j<h):
				add[j][i+j]+=add[j][i]
		if sol[best] < sol[i]:
			best = i
	
	print("House "+str(best+1)+" should get the biggest and best gift next Christmas.")
	T-=1
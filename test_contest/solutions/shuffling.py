#This solution applies the idea of fast exponentiation to permutations. Fast exponentiation is a way of quickly 
#calculating a^b (a raised to the power b) where b can be very large (usually this will done under some modulus 
#since the result can be very large). Instead of calculating a^b by multiply a together b times, first a^(b/2) is 
#computed recursively and then squared to result in a^b. If b is odd then the result must be multiplied by a to 
#reach the correct result (b/2 is computed with integer division).

#Here is an example with integers (not actually in this solution)
def fastexpoInteger(base, power):
	if power == 0:
		return 1
	half = fastexpoInteger(base,power//2)
	half *= half
	if power % 2 == 1:
		half *= base;
	return half

#We can apply fastexpo to permutations because we can group them up in anyway and still get the same result. 
#Say for some some permutation p the card that started at index i is at index j after 500 iterations and the card
#that was at index j is at index k after 500 iterations. That means that after 1000 iterations the card at index i will 
#end up at index k. 
def fastexpo(n, base, power):
	if power == 0:
		identity = [0]*n
		for i in range(n):
			identity[i] = i
		return identity
	
	half = fastexpo(n,base,power//2)
	result = [0]*n;
	
	#In context with the input, the array p tells us that after 1 iteration p[i] is in position i. Similarly this 
	#half array tells us that after power/2 iterations half[i] is what is in position i. half[half[i]] will tell us what is in 
	#position i after power iterations by applying the same permutation twice (think back to the i to j, j to k example)
	for i in range(n):
		result[i] = half[half[i]]
		
	if power % 2 == 1:
		for i in range(n):
			#base[result[i]] will apply the base permuation a single time
			result[i] = base[result[i]]
	return result


t = int(input())
for caseNumber in range(t):
	n, k = map(int, input().split())
	p = list(map(int, input().split()))
	
	#shifting the input so it is 0-indexed
	for i in range(n):
		p[i] -= 1;
	
	#calculating the answer with the function above
	ans = fastexpo(n,p,k)
	
	#converting back to 1-indexed
	for i in range(n):
		ans[i]+= 1
	
	print(' '.join(map(str, ans)))

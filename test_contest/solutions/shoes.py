def solve(sizes):
 #if there's just one seahorse, give them a shoe and remove them from our list of seahorses waiting for shoes
 if(len(sizes)==1):
  sizes.pop()
  return 1
 #otherwise, just take the last seahorse from our list of seahorses waiting (the one with the largest shoe size)
 last = sizes.pop()
 #if the next largest seahorse can also fit in this shoe size, or the shoe size between them (you can squeeze in either direction), then we can just use the other shoe from this pair of shoes for free
 if(last-2<=sizes[-1]):
  sizes.pop()
 return 1

#read in number of test cases
t = int(input())
for litter in range(1,t+1):
 #for every test case, read in input and then sort it
 n = int(input())
 sizes = sorted(list(map(int,input().split())))
 count = 0
 #while we still have seahorses left, assign shoes to them
 while(len(sizes)>0):
  count+=solve(sizes)
 #output number of shoes
 print("Litter #"+str(litter)+": "+str(count))


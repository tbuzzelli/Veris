# Scan in the number of ponds.
numPonds = int(input())
for i in range(numPonds) :
	# Scan in the first line of input for this pond and split it at the space.
	nums  = input().split()
	# Parse the number of turtles in the pond and how much weight they can support.
	numTurtles = int(nums[0])
	turtleWeight = int(nums[1])

	# Scan in the line containing the weight each turtle can support, split it, 
	# parse the numbers, and put them in a list.
	canSupport = []
	nums = input().split()
	for j in range(numTurtles-1) :
		canSupport.append(int(nums[j]))

	# Sort the list of the weights the turtles can support, in ascending order.
	canSupport = sorted(canSupport)

	# Create the pyramid and put Yertle at the top.
	pyramid = []
	pyramid.append([])
	pyramid[0] = [0]
	
	maxLevelReached = 0
	levelWidth = 2
	while True :
		completedLevel = True

		# Add a new level to the pyramid.
		pyramid.append([])
		for j in range(levelWidth) :

			# Calculate the weight the current turtle needs to support.
			weightToSupport = 0
			# Calculate the weight of the turtle above and to the left.
			if j != 0 :
				weightToSupport += turtleWeight / 2
				weightToSupport += pyramid[maxLevelReached][j-1] / 2
			# Calculate the weight of the turtle above and to the right.
			if j != levelWidth-1 :
				weightToSupport += turtleWeight / 2
				weightToSupport += pyramid[maxLevelReached][j] / 2

			# Greedily search through the turtles list for the smallest turtle 
			# that can support that much weight. If we find one, remove it 
			# from the list.
			foundTurtle = False
			for j in range(len(canSupport)) :
				if canSupport[j] >= weightToSupport :
					del canSupport[j]
					foundTurtle = True
					break

			
			if foundTurtle :
				# We found a working turtle, so add it to the pyramid.
				pyramid[maxLevelReached+1].append(weightToSupport)
			else :
				# We couldn't find a turtle that worked, so we couldn't complete the level.
				completedLevel = False
				break

		if completedLevel :
			# We completed the level, so increment our level counter and how wide 
			# our level is.
			maxLevelReached += 1
			levelWidth += 1
		else :
			# We didn't complete the new level, so break.
			break

	# Print the appropriate output.
	if maxLevelReached == 0 :
		print("Pond #", i+1, ": Poor Yertle.", sep="")
	else : 
		print("Pond #", i+1, ": The pyramid is ", maxLevelReached+1, " turtles high!", sep="")

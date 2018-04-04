# Define pi according to the problem statement.
PI = 3.141592653589793

# Scan in the number of burritos to check.
numBurritos = int(input())
for i in range(numBurritos) :
	# Scan in the first line of input for this burrito and split it at the space.
	nums  = input().split()
	# Parse the burrito's volume and radius.
	burritoVolume = int(nums[0])
	burritoRadius = int(nums[1])
	# Scan in the second line of input for this burrito and split it at the space.
	nums  = input().split()
	# Parse the foil's length and width.
	foilWidth = int(nums[0])
	foilLength = int(nums[1])

	# Calculate the burrito's length and circumference, given the volume and radius.
	burritoLength = burritoVolume / (PI * burritoRadius * burritoRadius)
	burritoCircum = PI * 2 * burritoRadius

	itFits = False
	if (burritoLength + 2*burritoRadius <= foilLength and burritoCircum <= foilWidth) :
		# Check if it fits in the original orientation.
		itFits = True
	elif (burritoLength + 2*burritoRadius <= foilWidth and burritoCircum <= foilLength) :
		# Check if it fits when the foil is rotated 90 degrees.
		itFits = True

	# Output the appropriate sentence.
	if (itFits) :
		print("Burrito #", i+1, ": Don't worry, the burrito fits!", sep="")
	else :
		print("Burrito #", i+1, ": Looks like a cold burrito today.", sep="")
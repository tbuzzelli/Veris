# Solution written by Will Cromar
# Python 3.6

import re

# The number of test cases
t = int(input())

# Run each test case
for _ in range(t):
    # We won't bother parsing an int from the input string
    num = input().strip()

    # List of strings to be printed at the end
    output = [num]

    # num reminds Ankit of knights if it contains a 1. Charge on!
    if re.search("1", num):
        output.append("knights")

    # num reminds Ankit of pirates if there is a digit that carrys
    # when doubled. (ie 5, 6, 7, 8, and 9)
    if re.search("[5-9]", num):
        output.append("pirates")

    # num reminds Ankit of ninjas if it contains a 0
    if re.search("0", num):
        output.append("ninjas")

    # Print all output tokens, separated by spaces
    print(" ".join(output))

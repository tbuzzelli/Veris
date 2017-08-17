# Declare a set of all shifted punctuation
shiftPunct = {"~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "{", "}", "|", ":", "\"", "<", ">", "?"}

# Read in number of cases to consider
cases = int(input())

# Read in each case
for i in range(0, cases):
    paragraph = input()

    # A counter for the number of times the shift key was pressed
    shifts = 0
    # A flag if the shift key is currently pressed
    last = False

    # Check each character in the string
    for char in paragraph:
        # If the character is a capital letter, or is in the set of shifted symbols, the shift key is pressed
        if "A" <= char <= "Z" or char in shiftPunct:
            # If shift key is not pressed, increment number of times pressed
            if not last:
                shifts += 1
            # Press the shift key
            last = True
        # If the shift key is not needed, stop pressing it
        else:
            last = False

    print("The shift key was pressed {} times.".format(shifts))

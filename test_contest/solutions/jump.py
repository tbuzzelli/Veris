def jumpLeft(leftX: int, rightX: int, forwardY: int, backY: int) -> (int, int, int, int):
    # If at the wall bounce back
    if leftX == 0:
        if rightX == 0:
            return leftX, rightX, forwardY, backY
        return leftX, rightX-1, forwardY, backY
    return leftX-1, rightX-1, forwardY, backY


def jumpRight(xVal: int, leftX: int, rightX: int, forwardY: int, backY: int) -> (int, int, int, int):
    # If at the wall bounce back
    if rightX == xVal-1:
        if leftX == xVal-1:
            return leftX, rightX, forwardY, backY
        return leftX+1, rightX, forwardY, backY
    return leftX+1, rightX+1, forwardY, backY


def jumpForward(leftX: int, rightX: int, forwardY: int, backY: int) -> (int, int, int, int):
    # If at the wall bounce back
    if forwardY == 0:
        if backY == 0:
            return leftX, rightX, forwardY, backY
        return leftX, rightX, forwardY, backY-1
    return leftX, rightX, forwardY-1, backY-1


def jumpBack(yVal: int, leftX: int, rightX: int, forwardY: int, backY: int) -> (int, int, int, int):
    # If at the wall bounce back
    if backY == yVal-1:
        if forwardY == yVal-1:
            return leftX, rightX, forwardY, backY
        return leftX, rightX, forwardY+1, backY
    return leftX, rightX, forwardY+1, backY+1

# Read in number of cases to consider
cases = int(input())

# Read in each case, and perform operation, then print
for i in range(0, cases):
    x, y, jumps = input().split()

    # convert values to ints
    x = int(x)
    y = int(y)
    jumps = int(jumps)

    # Since jumping will always form a rectangle, hold the coordinates of the rectangle
    lX = 0
    rX = x-1
    fY = 0
    bY = y-1

    # Print header
    print("Trip #{}:".format(i+1))

    # Simulate each sudden movement
    for j in range(0, jumps):
        move = input()
        if move == "L":
            lX, rX, fY, bY = jumpLeft(lX, rX, fY, bY)
        if move == "R":
            lX, rX, fY, bY = jumpRight(x, lX, rX, fY, bY)
        if move == "F":
            lX, rX, fY, bY = jumpForward(lX, rX, fY, bY)
        if move == "B":
            lX, rX, fY, bY = jumpBack(y, lX, rX, fY, bY)
        area = (rX-lX + 1) * (bY-fY + 1)
        print(area)
    print()

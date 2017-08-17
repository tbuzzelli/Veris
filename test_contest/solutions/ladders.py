# Written by Will Cromar
# Python 3.6

from collections import deque

# DX/DY array. In order of precedence, we'll move
# left, right, up, and down
DX = [-1, 1, 0, 0]
DY = [0, 0, -1, 1]

# Constants for types of spaces
EMPTY = '.'
LADDER = '#'
CHUTE = '*'
START = 'S'
EXIT = 'E'


# Primary business functions. Includes several helpers
def solve(grid, l, w, h):
    # Generator that gives all adjacent spots to the given one
    def adjacent(x, y, z):
        # print("call to adj w/", (x, y, z))
        for dx, dy in zip(DX, DY):
            # print(x + dx, y + dy, z)
            yield x + dx, y + dy, z

    # True if the result is in bounds, false otherwise
    def inBounds(x, y, z):
        return x >= 0 and y >= 0 and z >= 0 and x < w and y < l and z < h

    # Gives all adjacent nodes that are in bounds
    def moves(x, y, z):
        # Get all adjacent nodes and then filter them
        return filter(lambda triplet: inBounds(*triplet), adjacent(x, y, z))

    # Follow a chute to the bottom
    def chuteBottom(x, y, z):
        # While the spot below us is still a chute, keep moving down levels
        while z > 0 and grid[z - 1][y][x] == CHUTE:
            z -= 1

        return x, y, z

    # Gives vertically adjacent spots that have ladders
    def ladderSpots(x, y, z):
        # If there's a ladder above us, yield it
        if z < h - 1 and grid[z + 1][y][x] == LADDER:
            yield x, y, z + 1

        # Likewise, yield ladders below us
        if z > 0 and grid[z - 1][y][x] == LADDER:
            yield x, y, z - 1

    # Searches for the start symbol and returns an ordered triplet
    # or None
    def findStart():
        for z, level in enumerate(grid):
            for y, row in enumerate(level):
                if START in row:
                    return row.find(START), y, z

    # Finally, we can move on to our breadth-first search
    # This will store which spots we've seen before
    seen = set()

    # Will store an ordered triplet and a number of steps
    q = deque()

    # Find the starting position and add it to the queue w/ 0 steps
    start = findStart()
    seen.add(start)
    q.append(start)

    # Run BFS until the queue empties
    while len(q) > 0:
        # Unpack the data from the queue
        triplet = q.popleft()
        x, y, z = triplet

        # If we've found the exit, we're done!
        if grid[z][y][x] == EXIT:
            return True

        # If we stepped on a chute, fall
        if grid[z][y][x] == CHUTE:
            next = chuteBottom(x, y, z)
            if next not in seen:
                seen.add(next)
                q.appendleft(next)

            # If we're not at the bottom of the chute, then we
            # can't move anywhere else
            if next != triplet:
                continue

        # If we stepped on a ladder, see where we can go
        if grid[z][y][x] == LADDER:
            for next in ladderSpots(x, y, z):
                if next not in seen:
                    seen.add(next)
                    q.append(next)

        # Otherwise, explore our "slice" of the map
        for next in moves(x, y, z):
            if next not in seen:
                seen.add(next)
                q.append(next)

    return False

# Number of test cases
n = int(input())

# Process each test case
for case in range(n):
    # Get the dimensions of the  grid
    l, w, h = map(int, input().strip().split())

    # Read it in
    grid = []
    for _ in range(h):
        slice = []
        for _ in range(l):
            slice.append(input().strip())

        grid.append(slice)

    # I reverse the list to make z = 0 correspond to the lowest
    # slice on the map
    grid.reverse()

    # Determine if there is a path from start to end
    ans = solve(grid, l, w, h)
    # If there is, print "Yes", otherwise, "No"
    print("Map #%d: %s" % (case + 1, "Yes" if ans else "No"))

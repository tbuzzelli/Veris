# Solution to diamond from 2017 UCF HSPT

T = int(input())
for t in range(1, T+1):
    # Read in the grid
    n, m = map(int, input().split())
    g = []
    for i in range(0, n):
        g.append(list(input()))
    # marked[i][j] will be true if grid[i][j] is part of a diamond and false otherwise
    marked = []
    # Initialize marked to all false
    for i in range(0, n):
        cur = []
        for j in range(0, m):
            cur.append(False)
        marked.append(cur);
    # Check for diamonds of each possible size
    for size in range(1, n):
        # Try each possible point for the diamond to start where it is possible for it to fit in bounds
        for i in range(0, n - 2*size + 1):
            for j in range(size, m - size + 1):
                good = True;
                # Check that there is a diamond there
                for k in range(0, size):
                    good &= g[i+k][j+k] == '\\'
                    good &= g[i+size+k][j+size-1-k] == '/'
                    good &= g[i+2*size-1-k][j-1-k] == '\\'
                    good &= g[i+size-1-k][j-size+k] == '/'
                # If a diamond was found, mark all characters that were part of a diamond
                if good:
                    for k in range(0, size):
                        marked[i+k][j+k] = True
                        marked[i+size+k][j+size-1-k] = True
                        marked[i+2*size-1-k][j-1-k] = True
                        marked[i+size-1-k][j-size+k] = True
    # Print the updated grid
    print("Slab #" + str(t) + ":")
    for i in range(0, n):
        for j in range(0, m):
            if marked[i][j]:
                print(g[i][j], end='')
            else:
                print('.', end='')
        print('')
    
        


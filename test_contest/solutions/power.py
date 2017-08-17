# Solution written by Will Cromar
# Written for Python 3.6

c = int(input())

# Run c test cases
for _ in range(c):
    n = int(input())

    # Equivalent to 2^n - 1
    print(2 ** n - 1)

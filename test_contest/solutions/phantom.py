
n = int(input())

for testCase in range(n):

    # Print the appropriate response the appropriate number of times.
    cmd, t = input().split()
    t = int(t)

    result = {'RING':(t*['Hey, Superman!']), 
              'BANG':(t*['Are you jelly?'])}

    # Print the appropriate response t times based on input
    for reply in result[cmd]:
        print(reply)


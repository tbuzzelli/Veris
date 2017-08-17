from collections import Counter
from array import array

# Run each case
t = int(input())
for case in range(t):
    # Read in all of the phrases
    n = int(input())
    phrases = [input().strip().split(" ") for _ in range(n)]

    # Create the set of first and last words -- they're all
    # we care about
    wordSet = set()
    for phrase in phrases:
        wordSet.add(phrase[0])
        wordSet.add(phrase[-1])

    # Map words to index
    k = len(wordSet)
    wordIdx = {word: i for i, word in enumerate(wordSet)}
    idxword = {i: word for i, word in enumerate(wordSet)}

    # Connect words that are at the beginning and end of phrases,
    # respectively. Also count in-degrees
    adj = [list() for _ in range(k)]
    inDegree = Counter()

    for phrase in phrases:
        u = wordIdx[phrase[0]]
        v = wordIdx[phrase[-1]]
        adj[u].append(v)
        inDegree[v] += 1

    # Topsort the nodes
    ts = []

    # Find the roots of the DAG and add them to the topsort
    for u in range(k):
        if inDegree[u] == 0:
            ts.append(u)

    # Sort the rest of the nodes
    for u in ts:
        # Decrement the in-degree of adjacent nodes
        edges = adj[u]
        inDegree.subtract(edges)

        # Filter for edges that now have in-degree 0 and
        # add them to the topsort
        for v in filter(lambda i: inDegree[i] == 0, edges):
            ts.append(v)

    # Find the longest path through the topsorted DAG
    dp = [0] * k
    for u in ts:
        edges = adj[u]
        for v in edges:
            dp[v] = max(dp[v], dp[u] + 1)

    # Print the answer
    print("Puzzle #%d:" % (case + 1), max(dp))

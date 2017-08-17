/**
 * The Power of Two is a Curious Thing
 * Author: Tyler Woodhull
 */

// Include the necessary header
#include <stdio.h>

int main() {
	// Create variables needed to solve the problem
	int i, caseCount, n;

	// Scan the number of test cases
	scanf("%d", &caseCount);

	// Iterate through all test cases
	for (i = 0; i < caseCount; i++) {
		// Scan the value for n
		scanf("%d", &n);
		// Print (2 ^ n) - 1 using a bitshift
		printf("%d\n", (1 << n) - 1);
	}

	return 0;
}

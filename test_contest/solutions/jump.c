#include <stdio.h>

// Given two numbers, return the minimum value.
int min(int a, int b) {
  if(a < b) {
    return a;
  }
  return b;
}

// Given two numbers, return the maximum value.
int max(int a, int b) {
  if(a > b) {
    return a;
  }
  return b;
}

int main() {
  // Read in the number of test cases.
  int numCases;
  scanf("%d\n", &numCases);

  // Loop through each test case.
  for(int trip = 1; trip <= numCases; ++trip) {

    // Read in the number tanks in both directions
    // as well as the number of truck movements.
    int x, y, totalMoves;
    scanf("%d %d %d\n", &x, &y, &totalMoves);

    // We do not need to simulate all fish moving between each tank,
    // but instead just keep track of the size of the rectangle of
    // tanks containing fish. Each move from the truck moves the
    // rectangle of fish. If the rectangle hits a wall, we just
    // shrink the size of the rectangle by one in that direction.

    // Set our bounds for our rectangle still containing fish. This
    // starts off as all tanks.
    int left = 1;
    int right = x;
    int top = 1;
    int bottom = y;

    printf("Trip #%d:\n", trip);

    // Go through each move.
    for(int move = 0; move < totalMoves; ++move) {

      // Read in the current direction.
      char direction;
      scanf("%c\n", &direction);

      if(direction == 'F') {
        // If the direction is forwards, then move the top and bottom
        // edges forward. If either of them go below our bound of 1,
        // set them to 1.

        --top;
        --bottom;
        top = max(top, 1);
        bottom = max(bottom, 1);

      } else if(direction == 'B') {
        // If the direction is backwards, then move the top and bottom
        // edges backwards. If either of them go above our bound of y,
        // set them to y.

        ++top;
        ++bottom;
        top = min(top, y);
        bottom = min(bottom, y);

      } else if(direction == 'L') {
        // If the direction is left, then move the left and right edges
        // left. If either of them go past our bound of 1, set them to 1.

        --left;
        --right;
        left = max(left, 1);
        right = max(right, 1);

      } else if(direction == 'R') {
        // If the direction is right, then move the left and right edges
        // right. If either of them go past our bound of x, set them to x.

        ++left;
        ++right;
        left = min(left, x);
        right = min(right, x);
      }

      // Calculate the area of the current rectangle containing fish.
      int dx = right - left + 1;
      int dy = bottom - top + 1;
      int area = dx*dy;
      printf("%d\n", area);
    }
    printf("\n");
  }

  return 0;
}

#include <stdio.h>
#include <stdlib.h>

/*
 * Every tank initially has at least one fish, and the group of tanks
 * with fish in them will always be a rectangle. Therefore, we can
 * just store the x, y, width, and height values of the rectangle and
 * update them after each movement
 */

int main() {
    int i, j, runs, height, width, numMovements;

    scanf("%d", &runs);
    for (i = 0; i < runs; i++) {
        scanf("%d %d %d\n", &width, &height, &numMovements);
        int rectX = 0, rectY = 0, rectWidth = width, rectHeight = height;

        printf("Trip #%d:\n", i+1);
        for (j = 0; j < numMovements; j++) {
            char c;
            scanf("%c\n", &c);
            if (c == 'F') {
				if (rectY > 0) rectY--;
				else if (rectHeight > 1) rectHeight--;
			} else if (c == 'L') {
				if (rectX > 0) rectX--;
				else if (rectWidth > 1) rectWidth--;
			} else if (c == 'B') {
				if (rectY + rectHeight < height) rectY++;
				else if (rectY < height-1) {
					rectY++;
					rectHeight--;
				}
			} else {
				if (rectX + rectWidth < width) rectX++;
				else if (rectX < width-1) {
					rectX++;
					rectWidth--;
				}
			}

            printf("%d\n", rectWidth * rectHeight);
        }
        printf("\n");
    }
}

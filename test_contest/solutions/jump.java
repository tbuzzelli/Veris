/* The area of tanks that contain fish will always be a rectangle,
 * so we will keep track of the endpoints of the rectangle: startX & Y, endX & Y
 *         Front
 *         
 * *---*---*---*---*---*
 * |   |   |   |   |   |        (0)
 * *---*---*---*---*---*
 * |   | F | F | F | F | startY (1)
 * *---*---*---*---*---*
 * |   | F | F | F | F | endY   (2)
 * *---*---*---*---*---*
 * |   |   |   |   |   |        (3)
 * *---*---*---*---*---*
 *     startX       endX
 *  (0) (1) (2) (3) (4)
 * 
 *          Back
 * 
 */

import java.util.Scanner;

public class jump
{
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        
        // The number of test cases
        int t = scan.nextInt();
        for (int i = 0; i < t; i++)
        {
            System.out.printf("Trip #%d:\n", i+1);
            
            // Width and length, number of sudden movements
            int x = scan.nextInt();
            int y = scan.nextInt();
            int n = scan.nextInt();
            
            // All tanks are initially full, so we will initialize the endpoints as follows:
            int startX = 0;
            int endX = x-1;
            int startY = 0;
            int endY = y-1;
            
            for (int j = 0; j < n; j++)
            {
                char direction = scan.next().charAt(0); // The direction will always be one letter
                if (direction == 'F')
                {
                    startY = Math.max(0, startY-1); // The starting y-coordinate will decrease by one unless there are already fish in the front
                    endY = Math.max(0, endY-1); // The ending y-coordinate will also decrease by one unless it is already 0
                    // Note that the x-coordinates will stay the same
                }
                else if (direction == 'B')
                {
                    startY = Math.min(y-1, startY+1); // Using the same logic, the y-coordinates will increase unless there are already fish in the back
                    endY = Math.min(y-1, endY+1);
                }
                else if (direction == 'L')
                {
                    startX = Math.max(0, startX-1);
                    endX = Math.max(0, endX-1);
                }
                else if (direction == 'R')
                {
                    startX = Math.min(x-1, startX+1);
                    endX = Math.min(x-1, endX+1);
                }
                
                // We can now calculate the area of the the rectangle
                // The width is the difference of our x-coordinates plus 1
                // The length is the difference of our y-coordinates plus 1
                System.out.println((endX-startX+1) * (endY-startY+1));
            }
            
            // Blank line after each case
            System.out.println();
        }
        scan.close();
    }
}

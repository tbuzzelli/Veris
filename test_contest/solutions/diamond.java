
/*
   Solution to HSPT 2017 - Diamonds in the Rough
   Solution Idea - Pick every '/' character as a potential
        starting diamond, then walk down the left half of the diamond
        assuming that you can always make a bigger diamond by going down
        and to the left, or finish a diamond by going straight down seeing
        a '\' character. Be sure to check that the appropriate character
        exists for the right half of the diamond at each step, and when you
        get to the bottom of a candidate diamond, mark all the characters you
        checked as a valid character.
*/

import java.util.Scanner;

public class diamond {
   static int R, C;
   static char[][] map;
   static boolean[][] good;

   public static void main(String[] args) {
      // Open input
      Scanner scan = new Scanner(System.in);

      // Read in number of test cases
      int T = scan.nextInt();
      for (int t = 1; t <= T; t++) {
         // Read in number of rows and columns
         R = scan.nextInt();
         C = scan.nextInt();

         // Read in the map
         map = new char[R][C];
         good = new boolean[R][C];
         for (int i = 0; i < R; i++)
            map[i] = scan.next().toCharArray();

         // Loop over the map and try all starting points of diamonds
         for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
               if (map[i][j] == '/')
                  check(i, j, 1, true);
            }
         }

         // Output the new slab
         System.out.println("Slab #" + t + ":");
         for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
               // If good, print the character, otherwise print a '.'
               System.out.print(good[i][j] ? map[i][j] : ".");
            }
            System.out.println();
         }
      }

      // Close input
      scan.close();
   }



   /*
       (a,b) is coordinate within the map matrix we are checking
       dist is the distance between the two characters we are checking
       top is a boolean representing whether we are checking for the top
           of a diamond
   */
   private static boolean check(int a, int b, int dist, boolean top) {
      // The only time dist < 0 will be when we've finished checking
      // a valid diamond
      if (dist < 0)
         return true;

      // This checks to see if the character we are checking is out of bounds
      if (a < 0 || a >= R || b < 0 || b >= C || b + dist >= C)
         return false;
      
      boolean valid = false;
      if (top && map[a][b] == '/' && map[a][b + dist] == '\\') {
         // Check if we can make a bigger diamond top of a diamond
         valid = check(a + 1, b - 1, dist + 2, true);

         // Check if we can form the bottom half of a diamond from here
         valid |= check(a + 1, b, dist, false);

         // If this is a good diamond, mark it
         if (valid)
            good[a][b] = good[a][b + dist] = true;
      }
      if (!top && map[a][b] == '\\' && map[a][b + dist] == '/') {
         if (valid = check(a + 1, b + 1, dist - 2, false))
            good[a][b] = good[a][b + dist] = true;
      }
      return valid;
   }
}

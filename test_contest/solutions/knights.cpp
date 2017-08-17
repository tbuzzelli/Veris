/**
 * Knights, Pirates, Ninjas
 * Author: Tyler Woodhull
 */

// Include the required headers
#include <stdio.h>
#include <string.h>

int main() {
   // Create a character array to store our number
   // We will need 10 characters for the number, plus an extra for \0
   char number[11];
   // Create variables to be used in the solution
   int n, i, j, numberLen;
   // Create flags that will store the answer of the case
   bool knights, pirates, ninjas;

   // Scan the number of test cases
   scanf("%d", &n);

   // Iterate through all of the test cases
   for (i = 0; i < n; i++) {
      // Initialize all answers
      knights = false;
      pirates = false;
      ninjas = false;

      // Scan in the number that will be evaluated
      scanf("%s", number);

      // Get the number of digits in the number
      numberLen = strlen(number);

      // Iterate through each digit
      for (j = 0; j < numberLen; j++) {
         // If the current digit is 0, then Ankit will think of ninjas
         if (number[j] == '0') {
            ninjas = true;
         }
         // If the current digit is 1, then Ankit will think of knights
         if (number[j] == '1') {
            knights = true;
         }
         // If the current digit is going to carry over when multiplied by 2
         // then Ankit will think of pirates
         if (number[j] >= '5') {
            pirates = true;
         }
      }

      // Print out the answers
      printf("%s", number);
      if (knights) printf(" knights");
      if (pirates) printf(" pirates");
      if (ninjas) printf(" ninjas");
      printf("\n");
   }

   return 0;
}

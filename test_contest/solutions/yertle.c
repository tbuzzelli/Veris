#include <stdlib.h>
#include <stdio.h>

#define MAX_N 10000

// The comparator used for our sort
int double_cmp(const void *a, const void *b) {
    const double *ia = (const double *) a;
    const double *ib = (const double *) b;
    if(*ia == *ib) return 0;
    return *ia > *ib ? 1 : -1;
}

int main(void) {
    
    // declare our variables
    int p;
    
    // supports holds how much each turtle can support
    double supports[MAX_N + 1];
    
    // pyramid and nextRow will hold what a single row of the pyramid looks like
    double pyramid[1000], nextRow[1000];
    
    // read in the number of ponds
    scanf("%d", &p);
    
    // loop through each pond
    for(int pondNumber = 1; pondNumber <= p; pondNumber++) {
        int n;
        double w;
        
        // read in the number of turtles and the weight of one turtle
        scanf("%d %lf", &n, &w);
        
        // read in the amount of weight each turtle can support
        for(int i = 0; i < n - 1; i++) {
            scanf("%lf", &supports[i]);
        }
        
        // sort the array of how much weight each turtle can support
        qsort(supports, n - 1, sizeof(double), double_cmp);
        
        // the pyramid starts with Yertle at the top
        // he has a weight w so that is the total weight which will need to be supported
        pyramid[0] = w;
        
        // our pyramid size starts at one
        int size = 1;
        while(1) {
            // increment the size of our pyramid
            size++;
            
            // set our index to 0
            // this index will be used when we start assigning turtles to our pyramid
            // we will greedily assign the weakest possible turtle to support each section
            int idx = 0;
            
            // so far we think we will be able to create a pyramid of this height
            int good = 1;
            
            // we will loop through all of the turtle spots.
            // we only go half way because of the symetry and we want to process the pairs
            // of smaller amounts at the same time so we can do our sweep through the array
            for(int i = 0; i < (size + 1) / 2; i++) {
                // calculate how much this turtle will need to support
                // if he isn't on the end, he is underneath 2 turtles
                // so we add the turtle to the left as well
                double support = pyramid[i] / 2.0;
                if(i > 0)
                    support += pyramid[i - 1] / 2.0;
                
                // here we calculate how many turtles in this row will bear this amount of weight
                // if we have an odd number of turtles in this row and we are in the middle,
                // we are the only turtle supporting this amount. Otherwise, there is a turtle
                // on the otherside which will also support this amount of weight
                int count = i == size - i - 1 ? 1 : 2;
                
                // while we still need to assign a turtle to a spot (count > 0)
                // and while we still have available turtles (idx < n - 1) we will see if we can
                // choose the turtle at index idx
                while(count > 0 && idx < n - 1) {
                    // if this turtle is too weak, advance to the next one and continue
                    if(supports[idx] < support) {
                        idx++;
                        continue;
                    }
                    
                    // if this turtle is strong enough, set it to 0 so we won't reuse it
                    // and then we advance our index and decrease our count of turtles we still need
                    supports[idx] = 0;
                    idx++;
                    count--;
                }
                
                // if count is more than 0, we know we didn't assign all the turtles we need to
                // thus, we can't do this row so we set good to 0 (false)
                if(count > 0) {
                    good = 0;
                    break;
                }
                
                // set the values in the next row of our pyramid. We add w to the amount we
                // support because any turtles under us have to carry our weight as well
                nextRow[size - i - 1] = nextRow[i] = support + w;
            }
            
            // if we couldn't do this row, decrease the size and break out
            if(!good) {
                size--;
                break;
            }
            
            // copy the values from next row into the pyramid array
            for(int i = 0; i < size; i++) {
                pyramid[i] = nextRow[i];
            }
        }
        
        // if the pyramid can only be 1 tall, we print Poor Yerle.
        // otherwise, we print how tall the pyramid is
        if(size == 1) {
            printf("Pond #%d: Poor Yertle.\n", pondNumber);
        } else {
            printf("Pond #%d: The pyramid is %d turtles high!\n", pondNumber, size);
        }
        
    }
    
    return 0;
}

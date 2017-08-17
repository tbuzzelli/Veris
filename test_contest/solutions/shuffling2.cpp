// Solution to shuffling from 2017 UCF HSPT
// The idea behind this solution is to calculate what happens when the deck is shuffled a number of times that is equal to each power of two.
// For example, if we know where each card ends up after two shuffles, we can apply this result twice to figure out where each card
// will be after 4 shuffles.  Continuing this pattern, we can get the answer for anytime k is a power of two.  Then, using the binary representation of k,
// k can be written as the sum of at most 30 powers of two, and each of these groups of shuffles can be applied sequentially.

#include <iostream>

using namespace std;

int powers[31][100001]; // powers[i][j] is where card j will be after 2^i shuffles.

int main()
{
    int T;
    cin >> T;
    for(int t = 1; t<=T; t++)
    {
        int n, k;
        cin >> n >> k;
        // Read in input and get answers for a single shuffle.
        for(int i = 0; i<n; i++)
        {
            cin >> powers[0][i];
            powers[0][i]--;
        }
        // Calculate the answers for all other powers of two.
        for(int j = 0; j<30; j++)
            for(int i = 0; i<n; i++)
                powers[j+1][i] = powers[j][powers[j][i]];
        int res[n];
        for(int i = 0; i<n; i++) res[i] = i;
        // Figure out which powers of two are 1 bits in the binary representation of k, and apply those shuffles.
        for(int i = 0; i<31; i++)
        {
            if((k & (1<<i)) > 0)
            {
                int nres[n];
                for(int j = 0; j<n; j++)
                {
                    nres[j] = powers[i][res[j]];
                }
                for(int j = 0; j<n; j++) res[j] = nres[j];
            }
        }
        // Print total result
        cout << (res[0] + 1);
        for(int i = 1; i<n; i++) cout << " " << (res[i] + 1);
        cout << endl;
    }
    return 0;
}


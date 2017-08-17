// Solution to jump from 2017 UCF HSPT
// The idea is that after each movement, the set of tanks with fish in them will remain a contiguous rectangle.
// Because of this, it is sufficient to keep track of the front and rear rows that have tanks with fish in them,
// as well as the leftmost and rightmost columns.  Then, the number of tanks with fish is the number of rows times
// the number of columns, which can be calculated easily after each movement.

#include <iostream>

using namespace std;

int main()
{
    int T;
    cin >> T;
    for(int t = 1; t<=T; t++)
    {
        int n, m, moves;
        cin >> m >> n >> moves;
        int left = 0, right = m - 1; // Initially all columns from 0 to m-1 contain fish in them.
        int top = 0, bottom = n - 1; // Similarly, all rows from 0 to n-1 contain fish.
        cout << "Trip #" << t << ":" << endl;
        for(int i = 0; i<moves; i++)
        {
            char c;
            cin >> c;
            if(c == 'L')
            {
                // If jumping left, the column boundaries shift to the left.
                if(left > 0) left--;
                if(right > 0) right--;
            }
            else if(c == 'R')
            {
                // If jumping right, the column boundaries shift to the right.
                if(left < m - 1) left++;
                if(right < m - 1) right++;
            }
            else if(c == 'F')
            {
                // If jumping forward, the row boundaries shift up one.
                if(top > 0) top--;
                if(bottom > 0) bottom--;
            }
            else if(c == 'B')
            {
                // If jumping backwards, the row boundaries shift down one.
                if(top < n - 1) top++;
                if(bottom < n - 1) bottom++;
            }
            cout << (bottom - top + 1) * (right - left + 1) << endl;
        }
        cout << endl;
    }
    return 0;
}

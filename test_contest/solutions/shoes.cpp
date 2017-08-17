#include <bits/stdc++.h>

using namespace std;

int main()
{
    int t; cin >> t;
    for(int c = 1; c <= t; c++)
    {
        int n; cin >> n;

        vector<int> sizes(n);
        for(int i = 0; i < n; i++) cin >> sizes[i];

        //sort the sizes so that you can sweep over them in order
        sort(sizes.begin(), sizes.end());

        int answer = 0;
        for(int i = 0; i < n; i++)
        {
            //if the current shoe size is less than two away from the next one then buy one pair for both and skip checking the next one
            if(i < n-1 and abs(sizes[i] - sizes[i+1]) <= 2)
            {
                i++;
            }
            answer++;
        }

        cout << "Litter #" << c << ": " << answer << endl;

    }
    return 0;
}

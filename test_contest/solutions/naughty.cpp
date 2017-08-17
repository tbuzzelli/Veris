#include <bits/stdc++.h>

using namespace std;

int main()
{
    vector<vector<int> > pranks;
    int y; cin >> y;
    while(y--)
    {
        //using scanf instead of cin because of large input size
        int h, d; scanf("%d%d", &h, &d);

        //delta array will be used to keep track of ranges in which houses are pranked so instead of doing
        //d sweeps, we will only need to do one for every possible k
        int delta[10][10];
        memset(delta, 0, sizeof delta);
        pranks.clear();
        pranks.resize(10);
        for(int i = 0; i < 10; i++)
            pranks[i].assign(h, 0);


        for(int i = 0; i < d; i++)
        {
            int s, k, p; scanf("%d%d%d", &s, &k, &p);
            s--;k--;
            //adding one to the house number which santa will start pranking
            pranks[k][s]++;
            //subtracting one to the house number which santa will stop pranking if it is within bounds
            if(s+ ((k+1)*p) < h)pranks[k][s + ((k+1)*p)]--;

        }

        vector<int> prankFreq(h, 0);

        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < 10; j++)//updating delta array
                delta[j][i%(j+1)] += pranks[j][i];
            for(int j = 0; j < 10; j++)//applying delta array
                prankFreq[i] += delta[j][i%(j+1)];

        }

        int MAX = -1;
        int index = -1;

        //sweeping to find house which was pranked the most
        for(int i = 0; i < h; i++)
        {
            if(prankFreq[i] > MAX)
            {
                MAX = prankFreq[i];
                index = i;
            }
        }

        cout << "House "<<index+1<<" should get the biggest and best gift next Christmas.\n";
    }

    return 0;
}

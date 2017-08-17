#include <bits/stdc++.h>

using namespace std;
char land[50][50];
bool keep[50][50];
int h, l;

void checkDiamond(int x, int y1, int y2)
{
    //check the /\ and \/ are in the right order and that the distance between them is odd
    if(y1 > y2) return;
    if(y2 - y1 % 2 == 0) return;

    int half = (y2-y1)/2;

    //check that a diamond fits using these coordinates
    if(x - half < 0)return;
    if(x+1+half >= l)return;

    //this will store all points that belong to this diamond if it turns out to be a diamond
    vector<pair<int, int> > posPoints;

    //store /\ and \/ points
    posPoints.push_back(make_pair(y1,x));
    posPoints.push_back(make_pair(y1,x+1));
    posPoints.push_back(make_pair(y2,x));
    posPoints.push_back(make_pair(y2,x+1));

    for(int i = 1; i <= half; i++)
    {
        //this checks that the points in between /\ and \/ are the correct ones
        if(land[y2-i][x-i] != '\\' or
           land[y1+i][x-i] != '/' or
           land[y2-i][x+i+1] != '/' or
           land[y1+i][x+i+1] != '\\') return;
        else
        {
            //stores points in between /\ and \/
            posPoints.push_back(make_pair(y2-i,x-i));
            posPoints.push_back(make_pair(y1+i,x-i));
            posPoints.push_back(make_pair(y2-i,x+i+1));
            posPoints.push_back(make_pair(y1+i,x+i+1));
        }
    }

    for(int i = 0; i < (int)posPoints.size(); i++)
    {
        //goes through all the points in the found diamond and marks them to not be changed
        keep[posPoints[i].first][posPoints[i].second] = true;
    }
}

int main()
{
    int s; cin >> s;
    for(int t = 1; t <= s; t++)
    {
        cin >> h >> l;

        for(int i = 0; i < h; i++)
            for(int j = 0; j < l; j++)
                cin >> land[i][j];

        memset(keep, false, sizeof keep);

        vector<vector<int> > downV(l), upV(l);

        for(int i = 0; i < l-1; i++)//store all /\ found indexed by left
            for(int j = 0; j < h; j++)
                if(land[j][i] == '/' and land[j][i+1] == '\\')
                    downV[i].push_back(j);

        for(int i = 0; i < l-1; i++)//store all \/ found indexed by left
            for(int j = 0; j < h; j++)
                if(land[j][i] == '\\' and land[j][i+1] == '/')
                    upV[i].push_back(j);


        //check all pairs of \/ and /\ which are lined up
        for(int i = 0; i < l-1; i++)
        {
            for(int j = 0; j < (int)downV[i].size(); j++)
            {
                for(int k = 0; k < (int)upV[i].size(); k++)
                {
                    checkDiamond(i, downV[i][j], upV[i][k]);
                }
            }
        }

        //remove / and \ which are not part of any diamond
        for(int i = 0; i < h; i++)
            for(int j = 0; j < l; j++)
                if(!keep[i][j]) land[i][j] = '.';


        cout << "Slab #" << t << ":"<<endl;
        for(int i = 0; i < h; i++)
        {
            for(int j = 0; j < l; j++)
            {
                cout << land[i][j];
            }
            cout << endl;
        }


    }
    return 0;
}


// Solution to ladders from 2017 UCF HSPT
// Uses a breath first search to mark every square as visited or not visited
#include <iostream>
#include <string>
#include <queue>

using namespace std;

int main()
{
    int T;
    cin >> T;
    for(int t = 1; t<=T; t++)
    {
        int n, m, d;
        cin >> n >> m >> d;
        // Read in the board and store the starting and ending squares in si/sj/sk and ei/ej/ek.
        char board[d][n][m];
        int si = -1, sj = -1, sk = -1;
        int ei = -1, ej = -1, ek = -1;
        int vis[d][n][m];
        for(int i = 0; i<d; i++)
        {
            for(int j = 0; j<n; j++)
            {
                string s;
                cin >> s;
                for(int k = 0; k<m; k++)
                {
                    vis[i][j][k] = 0;
                    board[i][j][k] = s[k];
                    if(board[i][j][k] == 'S')
                    {
                        si = i; sj = j; sk = k;
                    }
                    else if(board[i][j][k] == 'E')
                    {
                        ei = i; ej = j; ek = k;
                    }

                }
            }
        }
        // When moving around on a level, the options for movements are (0, +1), (0, -1), (+1, 0), and (-1, 0)
        int dj[] = {0, 0, 1, -1};
        int dk[] = {1, -1, 0, 0};
        
        // Perform a breadth-first search of all locations that can be visited.
        // This involves putting each location into a queue, and when it is removed from the queue,
        // figuring out which squares can be reached from there in a single move
        std::queue<int> qi, qj, qk;
        qi.push(si);
        qj.push(sj);
        qk.push(sk);
        vis[si][sj][sk] = 1;
        while(qi.size())
        {
            // Get a location from the queue to process
            int ati = qi.front();
            int atj = qj.front();
            int atk = qk.front();
            qi.pop(); qj.pop(); qk.pop();
            // Case 1: Chute - try to move down to the bottom of the chute
            if(board[ati][atj][atk] == '*')
            {
                int ni = ati, nj = atj, nk = atk;
                while(ni < d - 1 && board[ni+1][atj][nk] == '*') ni++;
                if(ati != ni)
                {
                    if(!vis[ni][nj][nk])
                    {
                        vis[ni][nj][nk] = 1;
                        qi.push(ni);
                        qj.push(nj);
                        qk.push(nk);
                    }
                    continue; // We were at a chute but not the bottom so the only choice was down
                }
            }
            // Case 2: Ladder - try to move up or down by 1
            if(board[ati][atj][atk] == '#')
            {
                int ni = ati - 1, nj = atj, nk = atk;
                if(ni >= 0 && board[ni][nj][nk] == '#' && !vis[ni][nj][nk])
                {
                    vis[ni][nj][nk] = 1;
                    qi.push(ni);
                    qj.push(nj);
                    qk.push(nk);
                }
                ni = ati + 1; nj = atj; nk = atk;
                if(ni < d && board[ni][nj][nk] == '#' && !vis[ni][nj][nk])
                {
                    vis[ni][nj][nk] = 1;
                    qi.push(ni);
                    qj.push(nj);
                    qk.push(nk);
                }
            }
            // Case 3: Move around on the same level
            for(int move = 0; move < 4; move++)
            {
                int nj = atj + dj[move], nk = atk + dk[move], ni = ati;
                if(nk >= 0 && nk < m && nj >= 0 && nj < n && !vis[ni][nj][nk])
                {
                    vis[ni][nj][nk] = 1;
                    qi.push(ni);
                    qj.push(nj);
                    qk.push(nk);
                }
            }
            
        }
        string ans = "No";
        if(vis[ei][ej][ek]) ans = "Yes";
        cout << "Map #" << t << ": " << ans << endl;
    }
    return 0;
}


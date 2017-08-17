#include <iostream>
#include <deque>
using namespace std;

//In this problem, we will simply do a floodfill style bfs to see if we can navigate from the start to the finish.
int main(){
    int T;
    cin >> T;
    int t;
    for(t = 1; t<= T; t++){
        int l, w, h;
        cin >> l >> w >> h;
        char arr[l][w][h];
        bool visited[l][w][h];
        int x,y,z;
        int sx, sy, sz;
        //Read in input
        for(z = h-1; z >=0; z--){
            for(y = 0; y < w; y++){
                for(x = 0; x < l; x++){
                    visited[x][y][z]=false;
                    cin>> arr[x][y][z];
                    if(arr[x][y][z]=='S'){
                        sx=x;
                        sy=y;
                        sz=z;
                        arr[x][y][z]='.';
                    }
                }
            }
        }
        //We will use a deque (double-ended queue (allowing inserting to the end and removing from the front)) in order to hold our current move
        deque<int> dq;
        //A dx dy array makes navigating around easier
        int dy[] = {-1,0,0,1};
        int dx[] = {0,-1,1,0};
        //Start at the start
        dq.push_back(sx);
        dq.push_back(sy);
        dq.push_back(sz);
        int i;
        bool hit = false;
        while(!dq.empty()){
            //Get our current x,y,z location
            x = dq.front();
            dq.pop_front();
            y = dq.front();
            dq.pop_front();
            z = dq.front();
            dq.pop_front();
            //If we let ourselves revisit locations, we will loop forever.
            if(visited[x][y][z])continue;

            //On a normal square, go in all four directions (as long as it isn't out of bounds)
            if(arr[x][y][z]=='.'){
                for(i = 0; i < 4; i++){
                    int xx = dx[i]+x;
                    int yy = dy[i]+y;
                    if(yy>=0 && yy<w && xx>=0 && xx<l){
                        dq.push_back(xx);
                        dq.push_back(yy);
                        dq.push_back(z);
                    }
                }
            }
            //Ladders let us move up or down (as long as there is a ladder where we are going), and in all four directions
            if(arr[x][y][z]=='#'){
                if(z+1<h && arr[x][y][z+1]=='#'){
                    dq.push_back(x);
                    dq.push_back(y);
                    dq.push_back(z+1);
                }
                if(z-1>=0 && arr[x][y][z-1]=='#'){
                    dq.push_back(x);
                    dq.push_back(y);
                    dq.push_back(z-1);
                }
                for(i = 0; i < 4; i++){
                    int xx = dx[i]+x;
                    int yy = dy[i]+y;
                    if(yy>=0 && yy<w && xx>=0 && xx<l){
                        dq.push_back(xx);
                        dq.push_back(yy);
                        dq.push_back(z);
                    }
                }
            }
            //If we can move down into another chute, we have too. Otherwise we can move in all four directions.
            if(arr[x][y][z]=='*'){
                if(z-1>=0 && arr[x][y][z-1]=='*'){
                    dq.push_back(x);
                    dq.push_back(y);
                    dq.push_back(z-1);
                }else{
                    for(i = 0; i < 4; i++){
                    int xx = dx[i]+x;
                    int yy = dy[i]+y;
                    if(yy>=0 && yy<w && xx>=0 && xx<l){
                        dq.push_back(xx);
                        dq.push_back(yy);
                        dq.push_back(z);
                    }
                }
                }
            }
            if(arr[x][y][z]=='E')hit = true;
            visited[x][y][z]=true;
        }
        cout<<"Map #"<<t<<": ";
        if(hit){
            cout<<"Yes";
        }else{
            cout<<"No";
        }
        cout<<"\n";
    }
}
#include <iostream>
#include <vector>
using namespace std;

//This solution makes use of a technique reminiscent of Fast Matrix Exponentiation or Fast Exponentiation. In both of these techniques, something is being raised to a (potentailly large) power.
//The observation that can be made that speeds this up is that x^n is equal to (x^(n/2))^2. So if we can quickly calculate x^(n/2), calculating x^n is trivial. In order to find x^(n/2), we use recursion!

vector<int> init;

//The basic shuffling procedure described in the problem. Init is the starting deck, and perm is the order they are resorted.
vector<int> permute(vector<int> init, vector<int> perm) {
    vector<int> ans(perm.size());
    for (int i = 0; i < perm.size(); i++) {
        ans[i] = init[perm[i] - 1];
    }
    return ans;
}

//This is that exponentation technique, this permutes init by perm, exp times
vector<int> expo(vector<int> perm, int exp) {
    //In the event that we are permuting zero times, it is just the inital vector
    if (exp == 0) {
        return init;
    }
    //Otherwise, we say that permuting x times is the same as permuting the permutation of x/2 times with itself.
    if (exp % 2 == 0) {
        vector<int> half = expo(perm, exp / 2);
        return permute(half, half);
    }
    else {
        //Of course, this only works if it was even, so in the event that it was odd we can simply permute one additional time. (for instance, x^(n+1) = x^n * x)
        return permute(perm, expo(perm, exp - 1));
    }
}

int main() {
    int t;
    cin >> t;
    int testcase;
    for (testcase = 1; testcase <= t; testcase++) {
        int n, k;
        cin >> n;
        cin >> k;
        vector<int> temp(n);
        init = temp;
        vector<int> perm(n);
        int i;
        for (i = 0; i < n; i++) {
            init[i]=i+1;
        }
        for (i = 0; i < n; i++) {
            cin >> perm[i];
        }
        vector<int> ans = permute(init, expo(perm, k));
        for (i = 0; i < n; i++) {
            cout << ans[i];
            if(i<n-1)cout<<" ";
        }
        cout<<"\n";
    }
}



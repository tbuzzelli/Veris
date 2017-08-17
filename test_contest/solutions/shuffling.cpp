#include <bits/stdc++.h>

using namespace std;

vector<int> joinPermutations(vector<int> &perm1, vector<int> &perm2)
{

    vector<int> ans(perm1.size());
    for(int i = 0; i < perm1.size();i++)
    {
        //applying the permutation to itself
        ans[i] = perm2[perm1[i]-1];
    }

    return ans;
}

vector<int> findPerm(vector<int> &p, int e) // making sure to pass by value and not reference
{

    //base case
    if(e == 1) return p;

    //recursive step
    vector<int> halfExpoPerm = findPerm(p, e/2);

    //apply the new permutation to itself
    vector<int> fullExpoPerm = joinPermutations(halfExpoPerm, halfExpoPerm);

    if(e%2 == 0)
    {
        //if the exponent can be divided by two
        return fullExpoPerm;
    }
    else
    {
        //otherwise if an addition application is required
        return joinPermutations(fullExpoPerm, p);
    }


}

int main()
{
    int t;scanf("%d", &t);

    while(t--)
    {

        //using scanf and printf instead of cin and cout because of large input/output
        int n, k;scanf("%d%d",&n, &k);
        vector<int> perm(n);

        for(int i = 0; i < n; i++) scanf("%d",&perm[i]);

        //using a fast matrix expo to solve this problem by applying the permutation
        //to itself

        //if k = 0, then the permutation was never applied so just print 1-n numbers in order
        if(k == 0)
        {
            for(int i = 0; i < n; i++)
            {
                if(i)printf(" ");
                printf("%d",i+1);
            }
            printf("\n");
            continue;
        }

        vector<int> answer = findPerm(perm, k);

        for(int i = 0; i < n; i++)
        {
            if(i)printf(" ");
            printf("%d",answer[i]);
        }
        printf("\n");
    }


    return 0;
}

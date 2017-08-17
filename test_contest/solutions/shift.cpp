#include <iostream>

using namespace std;

int main()
{
    int i, j, n, ans, isRun, isShifted;
    string str;
    string shiftLetters = "~!@#$%^&*()_+{}|:\"<>?ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // list of letters to press shift key for

    cin >> n;
    getline(cin, str);
    while(n --> 0) {
        // read in a whole line with the spaces still
        getline(cin, str);
        isRun = 0;
        ans = 0;
        for(i=0;i<str.length();i++) {
            // check if this letter is shifted by going through the list of shifted letters
            // could do this faster with a hashset, but this will work fine for these bounds
            isShifted = 0;
            for(j=0;j<shiftLetters.length();j++) {
                if(str[i] == shiftLetters[j]) {
                    isShifted = 1;
                    break;
                }
            }
            // if so, increment the answer if it starts a run (press shift key), and set the run flag to true
            // otherwise, set the run flag to false (let go of shift key)
            if(isShifted) {
                if(!isRun) {
                    ans++;
                }
                isRun = 1;
            } else {
                isRun = 0;
            }
        }
        cout << "The shift key was pressed " << ans << " times." << endl;
    }
    return 0;
}

#include <iostream>
#include <string.h>

using namespace std;

int main()
{
	int T;
	cin >> T;
	for(int t = 1; t<=T; t++)
	{
		string s;
		int times;
		cin >> s >> times;
		int isBang = strcmp((const char*)s.c_str(), "RING");
		for(int i = 0; i<times; i++)
		{
			cout << (isBang ? "Are you jelly?" : "Hey, Superman!") << endl;
		}
	}
}

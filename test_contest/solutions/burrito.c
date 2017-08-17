#include <stdio.h>
int main(){
 /* define pi, and read in how many cases there are */
 double pi = 3.141592653589793;
 int cases;
 scanf("%d",&cases);
 int i;
 for(i = 1; i <= cases; i++){
  /* for every case, we need to read in the volume and radius of the burrito*/
  double volume;
  double radius;
  scanf("%lf",&volume);
  scanf("%lf",&radius);
  /* derive length from the given formula (volume = length * pi * r^2), and then we need an additional 2r (one r on either side) */
  double length = volume/(pi*radius*radius)+2*radius;
  /* the other dimension must be at least circumference = 2 * pi * r */
  double circ = pi*2*radius;
  /* read in the length and width of the foil*/
  double fl, fw;
  scanf("%lf",&fl);
  scanf("%lf",&fw);
  printf("Burrito #%d: ",i);
  /* now we just need to verify that in one of the two possible orientations, the burrito fits in the foil*/
  if((fl>=circ&&fw>=length)||(fl>=length&&fw>=circ)){
   printf("Don't worry, the burrito fits!\n");
  }else{
   printf("Looks like a cold burrito today.\n");
  }
 }
return 0;
}

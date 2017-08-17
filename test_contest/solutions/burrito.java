
import java.util.*;

public class burrito {
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        // read in the number of burritos to check
        int n = in.nextInt();
        
        // loop through all n burritos
        for(int burritoNumber = 1; burritoNumber <= n; burritoNumber++) {
            // read in the volume and radius of the burrito
            int volume = in.nextInt();
            int radius = in.nextInt();
            
            // read in the width and length of the foil
            int width = in.nextInt();
            int length = in.nextInt();
            
            // calculate the burrito's length and circumference
            double circumference = Math.PI * radius * 2.0;
            // we case to double here so we don't do integer division
            double burritoLength = 
                ((double) volume) / radius / radius / Math.PI;
            
            boolean canFit = false;
            
            // check if the burrito will fit with the normal orientation
            // then check if it will fit if we rotate the foil 90 degrees
            // the burrito will fit if one dimension is more than or equal
            // to the circumference and the other dimension is more than or
            // equal to the burrito's length + 2 * radius
            if(width >= burritoLength + radius + radius && 
               length >= circumference) {
                canFit = true;
            }
            else if(length >= burritoLength + radius + radius && 
                    width >= circumference) {
                canFit = true;
            }
            
            // print our answer
            System.out.printf("Burrito #%d: ", burritoNumber);
            if(canFit) {
                System.out.printf("Don't worry, the burrito fits!%n");
            } else {
                System.out.printf("Looks like a cold burrito today.%n");
            }
        }
    }
    
}


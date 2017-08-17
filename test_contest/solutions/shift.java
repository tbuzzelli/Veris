import java.util.Scanner;

public class shift {

	public static void main(String[] args) {
		
		Scanner scan=new Scanner(System.in);
		
		//scan in the number of paragraphs
		int p=scan.nextInt();
		
		scan.nextLine();
		
		//string of all the characters that require shift to be pressed
		String usesShift="ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+{}|:\"<>?";
		
		//loop through the paragraphs
		for(int i=0;i<p;i++){
			
			//scan in the paragraph
			String paragraph=scan.nextLine();
			
			//keeps track of whether shift is currently pressed or not
			boolean shiftPressed=false;
			
			//keeps track of how many times we've pressed shift
			int presses=0;
			
			//Loop over the paragraph and any time the current character requires us
			//to use shift and it isn't already pressed, increment presses by one.
			//If the current character doesn't use shift, then set shiftPressed to false
			for(int j=0;j<paragraph.length();j++){
				if(usesShift.contains(""+paragraph.charAt(j))){
					if(!shiftPressed){
						presses++;
						shiftPressed=true;
					}
				}else{
					shiftPressed=false;
				}
			}
			
			//print out the answer
			System.out.println("The shift key was pressed "+presses+" times.");
			
		}
		
	}

}

// Joshua Morgan Jones
//Assignment 2 question 3 code
import cern.jet.random.engine.*;

public class alignSequences{

	private static MersenneTwister m;
	//Given two similar sequences, generated from previously running mutateChildren file,
	//tries to align them together based on an algorithm in the align method
	public static void main(String[] args){
		m = new MersenneTwister();
		String bString = "";
		String cString = "";
		//bString = "TAGCTTCAGCGTTATTGACTGTAAGTTACAGGGAA";
		//cString = "TAGCGGCTTTAAGGTACAGGGAACCCGACAAATTA";
		bString = "TCCCCACAAGCCAGCAGCACAGTCTAATACCCTCA";
		cString = "TCCAGAGTCTCGTATCCTTGGGATGGTCCCATAGC";
		int penalty = -3;

		int[] b = toArray(bString);
		int[] c = toArray(cString);

		int[][] score = makeScoreM(2, -2);
		System.out.println("B: " + bString);
		System.out.println("C: " + cString);
		System.out.println("Alignment with d = " + penalty + ":");
		align(b, c, score, penalty);
		penalty = -4;
		System.out.println("Alignment with d = " + penalty + ":");
		align(b, c, score, penalty);
		penalty = -2;
		System.out.println("Alignment with d = " + penalty + ":");
		align(b, c, score, penalty);
		penalty = -1;
		System.out.println("Alignment with d = " + penalty + ":");
		align(b, c, score, penalty);
	}
	// algorithm to try and align two DNA sequences to see where they match up
	//helps to find a possible common ancestor
	public static void align(int[] b, int[] c, int[][] scoreM, int d){
		int[][] f = new int[b.length+1][c.length+1];
		for(int i=0; i<=b.length; i++){
			f[i][0] = d*i;
		}
		for(int j=0; j<=c.length; j++){
			f[0][j] = d*j;
		}
		int match = 0, delete = 0, insert = 0;
		for(int i=1; i<=b.length; i++){
			for(int j=1; j<=c.length; j++){
				match = f[i-1][j-1] + scoreM[b[i-1]][c[j-1]];
				delete = f[i-1][j] + d;
				insert = f[i][j-1] + d;
				f[i][j] = Math.max(match, Math.max(insert, delete));
			}
		}

		String alignmentB = "";
		String alignmentC = "";
		int i = b.length;
		int j = c.length;
		while( i>0 || j>0){
			if( i>0 && j>0 && (f[i][j] == (f[i-1][j-1] + scoreM[b[i-1]][c[j-1]]))){
				alignmentB = arrayChar(b, i-1) + alignmentB;
				alignmentC = arrayChar(c, j-1) + alignmentC;
				i--;
				j--;
			}else if( i>0 && f[i][j] == (f[i-1][j] + d)){
				alignmentB = arrayChar(b, i-1) + alignmentB;
				alignmentC = "-" + alignmentC;
				i--;
			}else {//if( j>0 && f[i][j] == (f[i][j-1] + d)){
				alignmentB = "-" + alignmentB;
				alignmentC = arrayChar(c, j-1) + alignmentC;
				j--;
			} 
		}
		System.out.println("B: " + alignmentB);
		System.out.println("C: " + alignmentC);

	}
	//Gets the character at a specific 
	public static String arrayChar(int[] a, int pos){
		if(a[pos] == 0){
			return "A";
		}else if(a[pos] == 1){
			return "C";
		}else if(a[pos] == 2){
			return "G";
		}else{
			return "T";
		}
	}
	//makes a score matrix to use when scoring the differences between sequences
	public static int[][] makeScoreM(int ifSame, int notSame){
		int[][] score = new int[4][4];
		for(int i =0; i<4; i++){
			for(int j=0; j<4; j++){
				if(i == j){
					score[i][j] = ifSame;
				}else{
					score[i][j] = notSame;
				}
			}
		}
		return score;
	}
	//Turns the string of a sequence into an array to be worked on
	public static int[] toArray(String s){
		int[] toReturn = new int[s.length()];
		for(int i=0; i<toReturn.length; i++){
			if(s.charAt(i) == 'A'){
				toReturn[i] = 0;
			}else if(s.charAt(i) == 'C'){
				toReturn[i] = 1;
			}else if(s.charAt(i) == 'G'){
				toReturn[i] = 2;
			}else{
				toReturn[i] = 3;
			}
		}
		return toReturn;
	}
	//Turns a sequnce into a string
	public static String toString(int[] s){
		String sequence = "";
		for(int i=0; i<s.length; i++){
			char toAdd;
			if(s[i] == 0){
				toAdd = 'A';
			}else if(s[i]==1){
				toAdd = 'C';
			}else if(s[i]==2){
				toAdd = 'G';
			}else if(s[i]==3){
				toAdd = 'T';
			}else{
				toAdd = '-';
			}
			sequence = sequence + toAdd; 
		}
		return sequence;
	}
}
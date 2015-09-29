// Joshua Morgan Jones
//Assignment 2 Question 1
import cern.jet.random.engine.*;

public class mutatingSequences{

	private static MersenneTwister m;

	public static void main(String[] args){
		//1a part
		m = new MersenneTwister(1);
		int length = 50;
		double mu = 0.01;
		double rate = mu*3/4;
		int t = 10;
		System.out.println("PartA:");
		String[] toPrint;
		toPrint = simulate(length, rate, t);
		System.out.println("A: " + toPrint[0]);
		System.out.println("B: " + toPrint[1]);
		System.out.println("C: " + toPrint[2]);
		System.out.println("Places different from B to A = " + toPrint[3]);
		System.out.println("Places different from C to A = " + toPrint[4]);
		System.out.println("Places different from B to C = " + toPrint[5]);
		
		//1b
		System.out.println("");
		System.out.println("PartB:");
		length = 1000;
		t = 25;
		String[] toCount;
		int totalCount = 0;
		double[] eachCount = new double[1000];
		//simulate 1000 times and count the differences at each site from B to C
		//then calculates the mean and variance of these
		for(int i=0; i<1000; i++){
			toCount = simulate(length, rate, t);
			totalCount += Integer.parseInt(toCount[5]);
			eachCount[i] = (Integer.parseInt(toCount[5]))*1.0;
		}
		double mean = totalCount*1.0/1000.0;
		double var = 0.0;
		for(int i=0; i<1000; i++){
			var += Math.pow((eachCount[i] - mean), 2);
		}
		var = var/1000.0;
		System.out.println("Total: " + totalCount);
		System.out.println("Mean: " + mean);
		System.out.println("variance: " + var);

		//partc
		//System.out.println("");
		//System.out.println("PartC:");
		length = 10000;
		t = 10;
		rate = 0.03*3/4;
		toCount = simulate(length, rate, t);
		
	}
	//method to create possion distributed random vairables
	public static int poisProcess(double rate, int time){
		int amount = 0;
		double t = randomExp(rate);
		while(t<=time){
			amount++;
			t += randomExp(rate);
		}
		return amount;
	}
	//method to create exponentially distributed random variables
	public static double randomExp(double rate){
		double rand = m.nextDouble();
		return -Math.log(rand)/rate;
	}
	//Simulates a randomly generated DNA sequence, mutates it and then compares the two
	public static String[] simulate(int length, double rate, int time){
		//sets up sequences
		int[] ancestor = genSequence(length);
		int[] B = ancestor.clone();
		int[] C = ancestor.clone();
		//mutates the two sibling sequences
		String[] toReturn = new String[6];
		toReturn[0] = toString(ancestor);
		mutate(B, rate, time);
		mutate(C, rate, time);
		if(length == 10000){
			pAB(B, C);

		}
		
		toReturn[1] = toString(B);
		toReturn[2] = toString(C);
		toReturn[3] = "" + compare(ancestor, B);
		toReturn[4] = "" + compare(ancestor, C);
		toReturn[5] = "" + compare(B, C);
		return toReturn;
	}

	public static void pAB(int[] b, int[] c){
		double[][] prob = new double[4][4];
		for(int i=0; i<b.length; i++){
			if(b[i] == c[i]){
				prob[b[i]][c[i]] += 1;
			}else{
				prob[b[i]][c[i]] += 0.5;
				prob[c[i]][b[i]] += 0.5;
			}
		}
		double probANotB = 0.0;
		for(int i=0; i<4; i++){
			double total = 0.0;
			for(int j=0; j<4; j++){
				total += prob[i][j];
			}
			for(int j=0; j<4; j++){
				prob[i][j] = prob[i][j]/total;
			}
		}
	}
	//Mutates a previously generated DNA sequence
	public static void mutate(int[] toMutate, double rate, int time){
		double prob;
		int toAdd = 4, random = 4, num = 0;
		boolean same = true;
		for(int i=0; i<toMutate.length; i++){
			//put in generation of rv
			num = poisProcess(rate, time);
			if(num > 0){
				for(int j=0; j<num; j++){
					random = toMutate[i];
					while(!(random != toMutate[i] && random!=4)){
						random = (int)((m.nextDouble())*4);
					}
					toMutate[i] = random;
				}
			}
		}
	}
	//Gives the number of places where two sequences differ
	public static int compare(int[] a, int[] b){
		int count = 0;
		for(int i=0; i<a.length; i++){
			if(a[i] != b[i]){
				count++;
			}
		}
		return count;
	}
	//Truns the array of a sequence into a readable string
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
			}else{
				toAdd = 'T';
			}
			sequence = sequence + toAdd; 
		}
		return sequence;
	}
	//Generates the random sequence
	public static int[] genSequence(int length){
		int[] sequence = new int[length];
		for(int i=0; i<length; i++){
			int toAdd = (int)((m.nextDouble())*4);
			if(toAdd == 4){
				toAdd--;
			}
			sequence[i] = toAdd;
		}
		return sequence;
	}
}
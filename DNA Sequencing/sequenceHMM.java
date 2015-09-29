/*
Joshua Morgan Jones, 6453502, jjon942
to run just compile and run, shouldn't need anything else
Assignment 3 qeustion 1
*/
import cern.jet.random.engine.*;
import java.util.*;

public class sequenceHMM{

	//I declare the random number generator here so that it can easily be used
	//throughout the entire file as well as the two probabilty matrices
	private static MersenneTwister m;
	private static double[][] eProbs = {{0.2, 0.3, 0.5}, {0.3, 0.55, 0.15}, {0.5, 0.1, 0.4}};
	private static double[][] sTransitions = {{15.0/16, (1 - 15.0/16)*0.3, (1 - 15.0/16)*0.7}, {(1 - 5.0/6)*0.4, 5.0/6, (1 - 5.0/6)*0.6}, {(1 - 3.0/4) *0.5, (1 - 3.0/4)*0.5, 3.0/4}};


	//This Program uses a HMM(hidden markov model) to generate sequences of a type of DNA
	//and also different type of emissions that can happen from particular states in the
	//sequence. Then also figures out what the probability of this occuring is relative to
	//what else can happen.
	public static void main(String[] args){

		m = new MersenneTwister(1);
		int totalLength = 200;
		int[] stateSequence = genSequence(totalLength);
		int[] symbolSequence = genSymbols(stateSequence);
		System.out.println("1B:");
		System.out.println(toString(stateSequence, true));
		System.out.println(toString(symbolSequence, false));

		//does 1c
		System.out.println("1C:");
		String givenPis = "HHHHHTTEEEHHHHHHEEEEEE";
		String givenXs = "NINBNIIBNIBBINBIINBBNB";
		int[] givenPi = toArray(givenPis, true);
		int[] givenX = toArray(givenXs, false);

		double givenProb = jointP(givenX, givenPi);
		System.out.println("Given probability = " + givenProb);
		double simProb = jointP(symbolSequence, stateSequence);
		System.out.println("Simulated probability = " + simProb);

		//part d now
		System.out.println("1D:");
		double givenXProb = probX(givenX);
		System.out.println("Given P(x) = " + givenXProb);
		double simXProb = probX(symbolSequence);
		System.out.println("Simulated P(x) = " + simXProb);
		
	}

	
	//calculates the P(x) for a given sequence x
	//which is essentiallyy forming a matrix containing probabilities from which we can
	//make a path (sequence of states) and figure out the probability of that path
	public static double probX(int[] x){
		double prob = 0.0;
		double[][] eMatrix = new double[3][x.length];
		eMatrix[0][0] = log2(1.0/3) + log2(eProbs[0][x[0]]);
		eMatrix[1][0] = log2(1.0/3) + log2(eProbs[1][x[0]]);
		eMatrix[2][0] = log2(1.0/3) + log2(eProbs[2][x[0]]);

		double[] current = new double[3];
		for(int i=1; i<x.length; i++){
			for(int j=0; j<3; j++){
				current[0] = eMatrix[0][i-1] + log2(sTransitions[0][j]);
				current[1] = eMatrix[1][i-1] + log2(sTransitions[1][j]);
				current[2] = eMatrix[2][i-1] + log2(sTransitions[2][j]);
				eMatrix[j][i] = logSum(current) + log2(eProbs[j][x[i]]);
			}
		}
		current[0] = eMatrix[0][x.length-1];
		current[1] = eMatrix[1][x.length-1];
		current[2] = eMatrix[2][x.length-1];
		prob = logSum(current);
		return prob;
	}

	
	//calculates the logsum of probabilities
	public static double logSum(double[] x){
		double z = 0;
		for(int i=1; i<x.length; i++){
			z += Math.pow(2, x[i] - x[0]);
		}
		return x[0] + log2(z);
	}

	
	//calculates the log base 2 of a double
	public static double log2(double n){
		double log = Math.log(n) / Math.log(2.0);
		return log;
	}

	
	//calculates the joint probability of seeing a random secondary Sequence and
	//emission sequence together.
	public static double jointP(int[] x, int[] pi){
		double totalP = log2(1.0/3);
		double currentP = 0.0;
		currentP = log2(eProbs[pi[0]][x[0]]);
		totalP += currentP;
		for(int i=1; i<x.length; i++){
			totalP += log2(sTransitions[pi[i-1]][pi[i]]);
			totalP += log2(eProbs[pi[i]][x[i]]);
		}
		return totalP;
	}

	
	//generates the emissions from a given set of states(secondary sequence) according to thir emission probabilites
	public static int[] genSymbols(int[] states){
		int[] symbols = new int[states.length];
		//generates the emission for each state in the states array
		double check = 0.0;
		for(int i = 0; i<states.length; i++){
			check = m.nextDouble();
			if(check < eProbs[states[i]][0]){
				symbols[i] = 0;
			}else if(check < (eProbs[states[i]][0]) + eProbs[states[i]][1]){
				symbols[i] = 1;
			}else{
				symbols[i] = 2;
			}
		}
		return symbols;
	}

	//generates a random sequence uniformly from a given length
	public static int[] genSequence(int length){
		//assuming that each of the states has equal chance of appearing first
		int[] sequence = new int[length];
		int toAdd = (int)((m.nextDouble())*3);
		if(toAdd == 3){
			toAdd--;
		}
		sequence[0] = toAdd;
		//now get the remaining states
		int previous = toAdd;
		double check = 0.0;
		for(int i=1; i<length; i++){
			previous = sequence[i-1];
			check = m.nextDouble();
			if(check < sTransitions[previous][0]){
				sequence[i] = 0;
			}else if(check < (sTransitions[previous][0]) +sTransitions[previous][1]){
				sequence[i] = 1;
			}else{
				sequence[i] = 2;
			}
		}
		//done creating sequence

		return sequence;
	}
	//turns a sequence string into array form
	public static int[] toArray(String input, boolean state){
		int[] output = new int[input.length()];
		if(state){
			for(int i=0; i<output.length; i++){
				if(input.charAt(i) == 'H'){
					output[i] = 0;
				}else if(input.charAt(i) == 'E'){
					output[i] = 1;
				}else{
					output[i] = 2;
				}
			}
		}else{
			for(int i=0; i<output.length; i++){
				if(input.charAt(i) == 'N'){
					output[i] = 0;
				}else if(input.charAt(i) == 'B'){
					output[i] = 1;
				}else{
					output[i] = 2;
				}
			}
		}
		return output;
	}
	//creates the string of a sequence
	public static String toString(int[] s, boolean state){

		String sequence = "";
		if(state){
			for(int i=0; i<s.length; i++){
			char toAdd;
			if(s[i] == 0){
				toAdd = 'H';
			}else if(s[i]==1){
				toAdd = 'E';
			}else {
				toAdd = 'T';
			}
			sequence = sequence + toAdd; 
		}
		}else{
			for(int i=0; i<s.length; i++){
				char toAdd;
				if(s[i] == 0){
					toAdd = 'N';
				}else if(s[i]==1){
					toAdd = 'B';
				}else {
					toAdd = 'I';
				}
				sequence = sequence + toAdd; 
			}
		}
		return sequence;
	}
}
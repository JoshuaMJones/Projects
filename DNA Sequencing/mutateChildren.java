// Joshua Morgan Jones
//Assignment 2 question 2 code

import cern.jet.random.engine.*;

public class mutateChildren{

	private static MersenneTwister m;
	
	public static void main(String[] args){
		//2c part
		m = new MersenneTwister(7);
		int t =20, length = 50;
		double rate = 0.01;
		String[] s = simulate(length, rate, t);
	}
	//simulates a DNA sequence and then generates two new sequences which have mutated
	//from the original sequence.
	public static String[] simulate(int length, double mu, int time){
		double rate = mu*3/4;
		//sets up sequences
		int[] ancestor = genSequence(length);
		int[] B = ancestor.clone();
		int[] C = ancestor.clone();
		//loops once for each time period
		String[] toReturn = new String[6];
		toReturn[0] = toString(ancestor);

		mutate(B, rate, time);
		mutate(C, rate, time);

		toReturn[1] = toString(B);
		toReturn[2] = toString(C);
		toReturn[3] = "" + compare(ancestor, B);
		toReturn[4] = "" + compare(ancestor, C);
		toReturn[5] = "" + compare(B, C);
		//sequences have been mutated
		//now work on insertion and deletion
		int bInsert = 0, bDelete = 0, cInsert = 0, cDelete = 0;
		int sum = 0;
		rate = length*time*mu/10;
		//generates until at least one amount is not 0
		while(sum == 0){
			bInsert = poisProcess(rate, 1);
			bDelete = poisProcess(rate, 1);
			cInsert = poisProcess(rate, 1);
			cDelete = poisProcess(rate, 1);
			sum = bInsert + bDelete + cInsert + cDelete;
		}
		//creates sequeneces to be inserted/deleted from
		int[] bChanged = B.clone();
		int[] cChanged = C.clone();
		int[] bIIndices = null, bDIndices = null, cIIndices = null, cDIndices = null;
		String bCompare = toString(bChanged);
		String cCompare = toString(cChanged);
		//does insertions/deletions and stores the indices at which they occured
		if(bInsert>0){
			bIIndices = new int[bInsert];
			for(int i=0; i<bInsert; i++){
				int index = generateIndex(bChanged.length);	
				bChanged = insert(bChanged, index);
				bIIndices[i] = index;
			}
		}if(cInsert>0){
			cIIndices = new int[cInsert];
			for(int i=0; i<cInsert; i++){
				int index = generateIndex(cChanged.length);	
				cChanged = insert(cChanged, index);
				cIIndices[i] = index;
			}
		}
		if(bDelete>0){
			bDIndices = new int[bDelete];
			for(int i=0; i<bDelete; i++){
				int index = generateIndex(bChanged.length);	
				bChanged = delete(bChanged, index);
				bDIndices[i] = index;
			}
		}if(cDelete>0){
			cDIndices = new int[cDelete];
			for(int i=0; i<cDelete; i++){
				int index = generateIndex(cChanged.length);
				cChanged = delete(cChanged, index);
				cDIndices[i] = index;
			}
		}
		System.out.println("Ancestor:");
		System.out.println(toString(ancestor));
		System.out.println("B after mutations:");
		System.out.println(toString(B));
		System.out.println("B after insertions and deletions:");
		System.out.println(toString(bChanged));
		System.out.println("C after mutations:");
		System.out.println(toString(C));
		System.out.println("C after insertions and deletions:");
		System.out.println(toString(cChanged));
		// System.out.println(bInsert);
		// System.out.println(bDelete);
		// System.out.println(cInsert);
		// System.out.println(cDelete);

		return toReturn;
	}
	//generates a random index, used to insert/delete parts of a DNA sequence
	public static int generateIndex(int max){
		int index = (int)((m.nextDouble())*(max));
			if(index == max){
				index--;
			}
		return index;
	}
	//deletes a part of a sequence from a given index
	public static int[] delete(int[] toChange, int index){
		int toRemove = toChange.length - index;
		int[] changed = null;
		if(toRemove <= 3){
			changed = new int[toChange.length - toRemove];
			System.arraycopy(toChange, 0, changed, 0, toChange.length - toRemove);
		}else{
			changed = new int[toChange.length - 3];
			if(index == 0){
				System.arraycopy(toChange, 3, changed, 0, changed.length);
			}
			System.arraycopy(toChange, 0, changed, 0, index);
			System.arraycopy(toChange, index +3, changed, index, toChange.length - index - 3);
		}
		return changed;
	}
	//inserts random bases into a given part of a sequence
	public static int[] insert(int[] toChange, int index){
		int[] changed = new int[toChange.length + 3];
		System.arraycopy(toChange, 0, changed, 0, index+1);
		for(int i=1; i<4; i++){
			int toAdd = (int)((m.nextDouble())*4);
			if(toAdd == 4){
				toAdd--;
			}
			changed[index + i] = toAdd;
		}
		if((index + 4)<changed.length){
			System.arraycopy(toChange, index+1, changed, index+4, (changed.length)-(index+4));
		}
		return changed;
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
	// adds random mutations to a sequence
	public static void mutate(int[] toMutate, double rate, int time){
		double prob;
		int toAdd = 4, random = 4, num = 0;
		boolean same = true;
		for(int i=0; i<toMutate.length; i++){
			num = poisProcess(rate, time);
			if(num > 0){
				random = toMutate[i];
				while(!(random != toMutate[i] && random!=4)){
					random = (int)((m.nextDouble())*4);
				}
				toMutate[i] = random;
			}
		}
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
	//Generates a random Sequence
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
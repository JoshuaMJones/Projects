import Jama.*;
//needs the jama package in the same location
public class SVDCompression{
	//File makes compressed approximations of an image file based on SVD (singular value decomposition) matrix compression
	public static void main(String[] args){

		//setting up the matrix A
		String picName = "baboon.pgm";//this string is for the name of the Original image
		int[] id = new int[1];
		int[] row = new int[120];
		int[] col = new int[100];
		int m = 120;
		int n = 100;
		PGM_PPM_Handler imageConverter = new PGM_PPM_Handler();
		char[] input = imageConverter.readFilePGM_PPM(picName,id,row,col);
		double[][] original = new double[m][n];
		int count = 0;
		for(int i=0; i< m; i++){
			for(int j=0; j<n; j++){

				original[i][j] = input[count];
				count++;
			}
		}
		Matrix A = new Matrix(original);
		
		//creating the svd of A
		SingularValueDecomposition svdA = new SingularValueDecomposition(A);
		Matrix U = svdA.getU();//mxn matrix
		Matrix D = svdA.getS();//nxn matrix
		Matrix V = svdA.getV();//nxn matrix
		Matrix VT = V.transpose();//still nxn

		//making images for the svd matrices
		visualizeMatrix(U, m, n, imageConverter, "U.pgm");
		visualizeMatrix(V, n, n, imageConverter, "V.pgm");
		visualizeMatrix(D, n, n, imageConverter, "D.pgm");

		//making images for the p approximation matrices
		//also printing out erros values etc for each one
		double[] singularValues = svdA.getSingularValues();
		Matrix P, Ap;
		double[] errors;
		double compression = 0.0;
		double p = 1.0;
		for(int i=1; i<50; i+=5){
			P = makeP(singularValues, i, n, n);
			Ap = U.times(P.times(VT));
 			errors = visualizeApproximation(Ap, A, m, n, imageConverter, "p" + i + ".pgm");
 			compression = 100*(1-((100*p+120*p+p)/12000));
 			System.out.println("p = " + i);
 			System.out.println("Max Error = " + errors[0]);
 			System.out.println("Mean Error = " + errors[1]);
 			System.out.println("Compression = " + compression);
 			System.out.println("-----");
		}
	}
	//this method turns the approximation of A into an Image file
	public static double[] visualizeApproximation(Matrix current, Matrix original, int row, int col,
		PGM_PPM_Handler imageConverter, String imageName){
		double [] errors = new double[2];
		char[] currentImage = new char[row*col];
		double maxError = 0.0;
		double meanError = 0.0;
		//it first cycles through to find the min and the max values
		//in the matrix. and also add's up all the errors
		int count = 0;
		double min = current.get(0,0);
		double max = current.get(0,0);
		double currentError = 0.0;
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				if(min > current.get(i,j)){
					min = current.get(i,j);
				}
				if(max < current.get(i,j)){
					max = current.get(i,j);
				}
				currentError = Math.abs(current.get(i,j) - original.get(i,j));
				if(currentError> maxError){
					maxError = currentError;
				}
				meanError = meanError + currentError;
			}
		}
		//then it maps them to values between 0 and 255
		//and then turns it into an image
		double mapped = 0.0;
		double remainder = 0.0;
		int toAdd = 0;
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				mapped = ((current.get(i,j)-min)/(max-min))*255;
				remainder = (10*mapped)%10;
				toAdd = (int)mapped;
				if(remainder>=5){
					toAdd++;
				}
				currentImage[count] = (char)toAdd;
				count++;
			}
		}
		// sets the max and mean errors to be passed back to the main method
		errors[0] = maxError;
		errors[1] = (meanError/12000);
		imageConverter.saveFilePGM_PPM(imageName, currentImage, 1 , row, col);
		return errors;
	}
	//this rounds a double to the nearest natural number
	public static int round(double toRound){
		double remainder = 0.0;
		remainder = (10*toRound)%10;
		int i = (int)toRound;
		if(remainder>=5){
			i++;
		}
		return i;
	}
	// makes the matrix P
	public static Matrix makeP(double[] values, int p, int row, int col){
		Matrix P = new Matrix(row, col);
		for(int i=0; i<p; i++){
			P.set(i, i, values[i]);
		}
		return P;
	}
	//this method turns a matrix into an Image
	public static void visualizeMatrix(Matrix current, int row, int col,
		PGM_PPM_Handler imageConverter, String imageName){
		char[] currentImage = new char[row*col];
		//it first cycles through to find the min and the max values
		//in the matrix
		int count = 0;
		double min = current.get(0,0);
		double max = current.get(0,0);
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				if(min > current.get(i,j)){
					min = current.get(i,j);
				}
				if(max < current.get(i,j)){
					max = current.get(i,j);
				}
			}
		}
		//then it maps them to values between 0 and 255
		//and then turns it into an image
		double mapped = 0.0;
		double remainder = 0.0;
		int toAdd = 0;
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				mapped = ((current.get(i,j)-min)/(max-min))*255;
				remainder = (10*mapped)%10;
				toAdd = (int)mapped;
				if(remainder>=5){
					toAdd++;
				}
				currentImage[count] = (char)toAdd;
				count++;
			}
		}
		imageConverter.saveFilePGM_PPM(imageName, currentImage, 1 , row, col);
	}

}
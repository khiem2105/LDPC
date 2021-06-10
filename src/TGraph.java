
public class TGraph {
	private int n_r, w_r, n_c, w_c;
	private int[][] left = null, right = null;
	
	//Constructeur	
	public TGraph(Matrix H, int wc, int wr) {
		n_r = H.getRows();
		n_c = H.getCols();
		w_r = wr;
		w_c = wc;
		left = new int[n_r][w_r+1];
		right = new int[n_c][w_c+1];
		
		//Set valeur for elements in left
		for(int i = 0; i < n_r; i++) {
			for(int j = 0, k = 0; j <= w_r && k < n_c; j++) {
				if(j == 0) {
					left[i][j] = 0;
				}
				else {
					while(H.getElem(i, k) == 0) {
						k++;
					}
					left[i][j] = k;
					k++;
				}
			}
		}
		//Set valeur for elements in right
		for(int i = 0; i < n_c; i++) {
			for(int j = 0, k = 0; j <= w_c && k < n_r; j++) {
				if(j == 0) {
					right[i][j] = 0;
				}
				else {
					while(H.getElem(k, i) == 0) {
						k++;
					}
					right[i][j] = k;
					k++;
				}
			}
		}
	}
	
	public void display() {
		//Display left
		System.out.println("Left:");
		for(int i = 0; i < n_r; i++) {
			for(int j = 0; j <= w_r; j++) {
				String sL = String.format("%3d", left[i][j]);
				System.out.print(sL);
				if(j == 0) 
					System.out.print("|");
				System.out.print(" ");
			}
			System.out.println("");
		}
		//Display right
		System.out.println("Right:");
		for(int i = 0; i < n_c; i++) {
			for(int j = 0; j <= w_c; j++) {
				String s = String.format("%3d", right[i][j]);
				System.out.print(s);
				if(j == 0) 
					System.out.print("|");
				System.out.print(" ");
			}
			System.out.println("");
		}
	}
	
	public Matrix decode(Matrix code, int rounds) {
		Matrix decodeWord = new Matrix(1, code.getCols());
		for(int i = 0; i < decodeWord.getCols(); i++) {
			decodeWord.setElem(0, i, (byte)(-1));;
		}
		
		//Assign the valeur of code to the first column of right
		for(int i = 0; i < code.getCols(); i++) {
			right[i][0] = code.getElem(0, i);
		}
		//Repeat rounds time
		for(int times = 0; times < rounds; times++) {
			//Calculate the parity
			for(int i = 0; i < n_r; i++) {
				left[i][0] = 0;
				for(int j = 1; j <= w_r; j++) {
					left[i][0] = (left[i][0] + right[left[i][j]][0]) % 2; 
				}
			}
			//Verification
			int[] countFalseParity = new int[n_c];
			//Initialize all false parity to zero
			for(int i = 0; i < n_c; i++) {
				countFalseParity[i] = 0;
			}
			int nbFalseParity = 0;
			for(int i = 0; i < n_r; i++) {
				if(left[i][0] == 1) {
					nbFalseParity += 1;
					for(int j = 1; j <= w_r; j++) {
						countFalseParity[left[i][j]] += 1;
					}
				}
			}
			//If every bit is 0, assigned the valeur of the first column of right 
			//to the word decoded
			if(nbFalseParity == 0) {
				for(int i = 0; i < decodeWord.getCols(); i++) {
//					System.out.println(right[i][0]);
					decodeWord.setElem(0, i, (byte)(right[i][0]));
				}
				return decodeWord;
			}
			else {
				//Find maximum number of false parity that each bit in
				//the code word evolve in
				int maxNbFalseParity = 0;
				for(int i = 0; i < n_c; i++) {
					if(countFalseParity[i] > maxNbFalseParity) {
						maxNbFalseParity = countFalseParity[i];
					}
				}
				//Reverse bits that evolved in the most false parity equation
				for(int i = 0; i < n_c; i++) {
					if(countFalseParity[i] == maxNbFalseParity) {
						right[i][0] = 1 - right[i][0];
					}
				}
			}
		}
		
		return decodeWord;
	}
}

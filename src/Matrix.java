//import java.util.*;
//import java.io.*;
import java.util.Random;

public class Matrix {
    private byte[][] data = null;
    private int rows = 0, cols = 0;
    
    public Matrix(int r, int c) {
        data = new byte[r][c];
        rows = r;
        cols = c;
    }
    
    public Matrix(byte[][] tab) {
        rows = tab.length;
        cols = tab[0].length;
        data = new byte[rows][cols];
        for (int i = 0 ; i < rows ; i ++)
            for (int j = 0 ; j < cols ; j ++) 
                data[i][j] = tab[i][j];
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public byte getElem(int i, int j) {
        return data[i][j];
    }
    
    public void setElem(int i, int j, byte b) {
        data[i][j] = b;
    }
    
    public boolean isEqualTo(Matrix m){
        if ((rows != m.rows) || (cols != m.cols))
            return false;
        for (int i = 0; i < rows; i++) 
            for (int j = 0; j < cols; j++) 
                if (data[i][j] != m.data[i][j])
                    return false;
                return true;
    }
    
    public void shiftRow(int a, int b){
        byte tmp = 0;
        for (int i = 0; i < cols; i++){
            tmp = data[a][i];
            data[a][i] = data[b][i];
            data[b][i] = tmp;
        }
    }
    
    public void shiftCol(int a, int b){
        byte tmp = 0;
        for (int i = 0; i < rows; i++){
            tmp = data[i][a];
            data[i][a] = data[i][b];
            data[i][b] = tmp;
        }
    }
     
    public void display() {
        System.out.print("[");
        for (int i = 0; i < rows; i++) {
            if (i != 0) {
                System.out.print(" ");
            }
            
            System.out.print("[");
            
            for (int j = 0; j < cols; j++) {
                System.out.printf("%d", data[i][j]);
                
                if (j != cols - 1) {
                    System.out.print(" ");
                }
            }
            
            System.out.print("]");
            
            if (i == rows - 1) {
                System.out.print("]");
            }
            
            System.out.println();
        }
        System.out.println();
    }
    
    public Matrix transpose() {
        Matrix result = new Matrix(cols, rows);
        
        for (int i = 0; i < rows; i++) 
            for (int j = 0; j < cols; j++) 
                result.data[j][i] = data[i][j];
    
        return result;
    }
    
    public Matrix add(Matrix m){
        Matrix r = new Matrix(rows,m.cols);
        
        if ((m.rows != rows) || (m.cols != cols))
            System.out.printf("Erreur d'addition\n");
        
        for (int i = 0; i < rows; i++) 
            for (int j = 0; j < cols; j++) 
                r.data[i][j] = (byte) ((data[i][j] + m.data[i][j]) % 2);
        return r;
    }
    
    public Matrix multiply(Matrix m){
        Matrix r = new Matrix(rows,m.cols);
        
        if (m.rows != cols)
            System.out.printf("Erreur de multiplication\n");
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                r.data[i][j] = 0;
                for (int k = 0; k < cols; k++){
                    r.data[i][j] =  (byte) ((r.data[i][j] + data[i][k] * m.data[k][j]) % 2);
                }
            }
        }
        
        return r;
    }
    
    public void addRow(int a, int b) {
    	for(int i = 0; i < cols; i++) {
    		data[b][i] = (byte) ((data[b][i] + data[a][i]) % 2);
    	}
    }
    
    public void addCol(int a, int b) {
    	for(int i = 0; i < rows; i++) {
    		data[i][b] = (byte) ((data[i][b] + data[i][a]) % 2);
    	}
    }
    
    public Matrix sysTransform() {
    	Matrix sysControl = new Matrix(data);
    	Matrix R = new Matrix(rows, rows);
    	
    	//Initialisation matrix R inversible    	
    	for(int i = 0; i < R.rows; i++) {
    		for(int j = 0; j < R.cols; j++) {
    			R.data[i][j] = data[i][j+cols-rows];
    		}
    	}
    	//Transform matrix R to indetical matrix
    	for(int i = 0; i < R.rows; i++) {
    		if(R.data[i][i] == 0) {
	    		for(int j = i+1; j < R.rows; j++) {
	    			if(R.data[j][i] == 1) {
	    				R.shiftRow(i, j);
	    				sysControl.shiftRow(i, j);
	    				break;
	    			}
	    		}
    		}
    		for(int j = i+1; j < R.rows; j++) {
    			if(R.data[j][i] == 1) {
    				R.addRow(i, j);
    				sysControl.addRow(i, j);
    			}
    		}
    	}
    	for(int i = R.rows-1; i >= 0; i--) {
    		for(int j = i-1; j >= 0; j--) {
    			if(R.data[j][i] == 1) {
    				R.addRow(i, j);
    				sysControl.addRow(i, j);
    			}
    		}
    	}
//    	R.display();
    	//Finish, the systematique control matrix is now sysControl    	
    	return sysControl;
    }
    
    public Matrix genG() {
    	Matrix g = new Matrix(cols-rows, cols);
//    	Matrix sysControl = this.sysTransform();
    	Matrix M = new Matrix(rows, cols-rows);
    	
    	//Initialize M    	
    	for(int i = 0; i < rows; i++) {
    		for(int j = 0; j < cols-rows; j++) {
    			M.data[i][j] = data[i][j];
    		}
    	}
    	//Calculate M tranpose
    	Matrix M_t = M.transpose(); 
    	//Initialize the first part (identical matrix)    	
    	for(int i = 0; i < g.rows; i++) {
    		g.data[i][i] = 1;
    	}
    	//The second part
    	for(int i = 0; i < g.rows; i++) {
    		for(int j = g.rows; j < g.cols; j++) {
    			g.data[i][j] = M_t.data[i][j-g.rows];
    		}
    	}
    	//Finish, return g    	
    	return g;
    }
    
    public Matrix errGen(int w) {
    	Random r = new Random();
    	Matrix wordGenerated = new Matrix(data);
    	
    	//Use une boucle de w iteration for assign value to u
    	int i = 0;
    	while(i < w) {
    		int indice = r.nextInt(cols);
    		if(wordGenerated.getElem(0, indice) == 0) {
    			wordGenerated.setElem(0, indice, (byte) 1);
    			i++;
    		}	
    	}
    	
    	return wordGenerated;
    }
}


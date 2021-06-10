//import java.util.*;
import java.io.*;

public class Main {
    
    public static Matrix loadMatrix(String file, int r, int c) {
        byte[] tmp =  new byte[r * c];
        byte[][] data = new byte[r][c];
        try {
            FileInputStream fos = new FileInputStream(file);
            fos.read(tmp);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < r; i++)
            for (int j = 0; j< c; j++)
                data[i][j] = tmp[i * c + j];
            return new Matrix(data);
    }
    
    public static void main(String[] arg) {
        //H 
        Matrix hbase = loadMatrix("src/data/matrix-15-20-3-4", 15, 20);
        System.out.println("Matrice de controle H:");
        hbase.display();
        //Form systématique de H
        Matrix hsys = hbase.sysTransform();
        System.out.println("Form systématique de H:");
        hsys.display();
        //Matrice génératrice
        Matrix sysgen = hsys.genG();
        System.out.println("Matrice génératrice G:");
        sysgen.display();
        //Mot binaire u
        byte[][] data_u = {{1, 0, 1, 0, 1}};
        Matrix u = new Matrix(data_u);
        System.out.println("Mot binaire u:");
        u.display();
        //Encodage de u:     
        Matrix x = u.multiply(sysgen);
        System.out.println("Encodage de u (x=u.G):");
        x.display();
        //Calculate syndrôme de x:
        System.out.println("Syndrome de x (s=H.x^t)");
        hsys.multiply(x.transpose()).display();
        //hbase.multiply(x.transpose()).display();
    	
        //Graph de Tanner
    	TGraph g = new TGraph(hbase, 3, 4);
    	g.display();
    	
    	//Tester algorithme de décodage
    	for(int i = 1; i <= 4; i++) {
    		Matrix e = new Matrix(1, 20);
    		if(i == 1) {
    			 byte[][] data_e = {{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
    			 e = new Matrix(data_e);
    		}
    		else if(i == 2) {
    			byte[][] data_e = {{0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
    			e = new Matrix(data_e);
    		}
    		else if(i == 3) {
    			byte[][] data_e = {{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}};
    			e = new Matrix(data_e);
    		}
    		else if(i == 4) {
    			byte[][] data_e = {{0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0}};
    			e = new Matrix(data_e);
    		}
    		//Afficher mot de code
    		System.out.println("Mot de code x:");
    		x.display();
    		//Aficher vecteur d'erreur
    		System.out.println("Vecteur d'erreur e" + i + ":");
    		e.display();
    		//Afficher mot de code bruté
    		System.out.println("Mot de code bruité y" + i + " = " + "e" + i + " + x:");
    		Matrix y = x.add(e);
    		y.display();
    		//Syndrome de y
    		System.out.println("Syndrome de y" + i + ":");
    		hbase.multiply(y.transpose()).transpose().display();
    		//Correction de y:
    		System.out.println("Correction x" + i + " de y" + i + ":");
    		Matrix yCorrect = g.decode(y, 100);
    		yCorrect.display();
    		//Resultat
    		System.out.println("x" + i + " = x: " + yCorrect.isEqualTo(x));
    		System.out.println("---------------------------------------------------------");
      	}
//    	evaluate();
    }
    
    //Evaluation
    
    public static void evaluate() {
    	Matrix bigH = loadMatrix("src/data/Matrix-2048-6144-5-15", 2048, 6144);
    	Matrix bigHSys = bigH.sysTransform();
    	Matrix bigG = bigHSys.genG();
    	//Mot u
    	byte[][] data = new byte[1][4096];
    	for(int i = 0; i < 4096; i++) {
    		if(i % 2 == 0)
    			data[0][i] = 1;
    		else
    			data[0][i] = 0;
    	}
    	Matrix u = new Matrix(data);
//    	System.out.println("Mot u obtenu:");
//    	u.display();
    	Matrix uEncoded = u.multiply(bigG);
//    	System.out.println("Mot de code x = u.G");
//    	uEncoded.display();
    	
    	//Graph de Tanner:
    	TGraph g = new TGraph(bigH, 5, 15);
//    	System.out.println("Graph de Tanner:");
//    	g.display();
    	
    	//Evaluation
    	int nbSuccess = 0, nbWrong = 0, nbFail = 0, w = 0, rounds = 200, loops = 10000;
    	byte[][] failData = new byte[1][6144];
    	for(int i = 0; i < 6144; i++) {
    		failData[0][i] = (byte) -1;
    	}
    	Matrix failWord = new Matrix(failData);
    	for(int j = 0; j < 4; j++) {
    		if(j == 0) w = 124;
    		else if(j == 1) w = 134;
    		else if(j == 2) w = 144;
    		else w = 154;
    		nbWrong = 0; nbSuccess = 0; nbFail = 0;
    		System.out.println("Nombre d'erreurs: " + w);
    		for(int i = 0; i < loops; i++) {
        		Matrix erreur = new Matrix(1, 6144).errGen(w);
        		Matrix y = uEncoded.add(erreur);
        		Matrix yCorrect = g.decode(y, rounds);
        		
        		if(yCorrect.isEqualTo(uEncoded)) {
        			nbSuccess++;
        		}
        		else {
        			if(yCorrect.isEqualTo(failWord))
        				nbFail++;
        			else
        				nbWrong++;
        		}
        	}
//        	System.out.println(nbSuccess);
        	
        	System.out.println("Taux de success: " + ((float)nbSuccess/(float)loops)*100 + "%");
        	System.out.println("Taux échoué: " + ((float)nbFail/(float)loops)*100 + "%");
        	System.out.println("Taux de erroné: " + ((float)nbWrong/(float)loops)*100 + "%");
        	System.out.println("-------------------------------------------------");

        }
    }
}

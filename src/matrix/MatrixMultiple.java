/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrix;


import java.util.Random;
public class MatrixMultiple {
	public static Random rn;
        public static void displayMatrix(int[][] mat) {
		int n = mat.length; 
		int m = mat[0].length; 
		StringBuffer buffer=new StringBuffer();
		if (n <= 660) {
			int digit = (int) Math.log10(n)*2+3;
			for (int row = 0; row < n; row++) {
				for (int col = 0; col < m; col++) {
					String numStr = String.format("%"+digit+"d ", mat[row][col]);
					buffer.append(numStr);
				}
				buffer.append("\n");
			}
		} else {
			System.out.println("The matrix is too big to display on screen.");
		}
		System.out.println(buffer.toString());
	}
        public static int[][] createUnitMatrix(int n) {
		int[][] matrix = new int[n][n];
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				matrix[row][col] = 0;
			}
			matrix[row][row] = 1; 
		}
		return matrix; 
	}

        public static int[][] createRandomMatrix(int n) {
		int[][] matrix = new int[n][n];
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				matrix[row][col] = (int)(Math.random()*1000);
			}
		}
		return matrix; 
	}

	public static void displayLeftMatrix(int[][] a, int n)
	{
		for(int i=0;i<n;i++)
		{
			System.out.println();
			int left=(i+1)%n;
			for(int j=0;j<n;j++)
			{		
				System.out.print(a[i][left]+" ");
				left=(left+1)%n;
			}
			
		}
		System.out.println();
	}
	public static void displayUpMatrix(int[][] a, int n)
	{
		for(int i=0;i<n;i++)
		{
			System.out.println();
			int up=(i+1)%n;
			for(int j=0;j<n;j++)
			{
				
				System.out.print(a[up][j]+" ");
				up=(up+1)%n;
			}
			
		}
		System.out.println();
	}
	public static int[][] createDisplayMatrix(int dim)
	{
		int[][] kq=new int[dim][dim];
		if(rn==null)rn=new Random();
		for(int i=0;i<dim;i++)
		{
			for(int j=0;j<dim;j++)
			{
				
				kq[i][j]=rn.nextInt(10)+1;
			}
		}
		return kq;
	}

    
}
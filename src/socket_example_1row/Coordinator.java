package socket_example_1row;

import java.io.*; 
import java.util.Arrays;
import matrix.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;
public class Coordinator implements Runnable{ 
	
	Connection conn; 
	int n; 
	public int[][] a; 
	public int[][] b; 
	public int[][] c; 
	int numNodes; 
        int m;
	DataInputStream[][] disWorkers;
	DataOutputStream[][] dosWorkers;
        int workers;
        int port;
        
	
	public Coordinator(int n, int numNodes, int port) { 
		this.n = n; 
		//a = new int[n][n]; 
		//b = new int[n][n]; 
		//c = new int[n][n]; 
                a = MatrixMultiple.createRandomMatrix(this.n); 
		b = MatrixMultiple.createRandomMatrix(this.n);
                c = new int[n][n];
		this.numNodes = numNodes;
                double workers=(int)Math.sqrt(numNodes);
                double m=(int)n/workers;
                this.m=(int)Math.round(m);
		this.workers=(int)Math.round(workers);
                this.port=port;
	}
	
 	void configurate(int portNum) { 
		try { 
			System.out.println("Matrix A");
			MatrixMultiple.displayMatrix(a);
			
			//do initial shifting on a and b
			System.out.println("Matrix B");
			MatrixMultiple.displayMatrix(b);
			
//perform initial shift
			
			for(int i=0;i<n;i++)
			{
				int temp[]=Arrays.copyOf(a[i], a[i].length);
				for(int j=0;j<n;j++)
				{
					
					a[i][(j+n-(i+1))%n]=temp[j];
				}
			}
			System.out.println("Shifted Matrix A");
			MatrixMultiple.displayMatrix(a);
			//do initial shifting on a and b
			
			for(int i=0;i<n;i++)
			{
				int temp[]=new int [n];
				for(int j=0;j<n;j++)
				{
					temp[j]=b[j][i];
					
				}
				for(int j=0;j<n;j++)
				{
					b[(j+n-(i+1))%n][i]=temp[j];
					
				}
			}
					
			System.out.println("Shifted Matrix B");
			MatrixMultiple.displayMatrix(b);
		
                    
                        conn = new Connection(portNum); 
			disWorkers = new DataInputStream[workers][workers]; 
			dosWorkers = new DataOutputStream[workers][workers];
			String[][] ips = new String[workers][workers]; 
			int[][] portsX = new int[workers][workers]; 
                        int[][] portsY = new int[workers][workers]; 
			for (int i=0; i<numNodes; i++ ) { 
				DataIO dio = conn.acceptConnect(); 
				DataInputStream dis = dio.getDis(); 
				int indexA = dis.readInt();
				int indexB = dis.readInt(); 			//get worker ID
				ips[indexA][indexB] = dis.readUTF(); 			//get worker ip
				portsX[indexA][indexB] = dis.readInt();  		//get worker port #
				portsY[indexA][indexB] = dis.readInt();
                                disWorkers[indexA][indexB] = dis; 
				dosWorkers[indexA][indexB] = dio.getDos(); 	//the stream to worker ID
				dosWorkers[indexA][indexB].writeInt(n); 		//assign matrix dimension (height)
				//int width = (indexA<numNodes-1) ? n/numNodes : n/numNodes+n%numNodes;
				dosWorkers[indexA][indexB].writeInt(m); 	//assign matrix width 
			}
			for (int w=0; w<workers; w++) {
				for(int v=0;v<workers;v++)
				{
				
						
                                                int myLeft=(v+workers-1)%workers;
                                                int myRight=(v+1)%workers;
                                                int myTop=(w+workers-1)%workers;
                                                int myBottom=(w+1)%workers;
                                                dosWorkers[w][v].writeUTF(ips[w][myLeft]);
                                                dosWorkers[w][v].writeInt(portsX[w][myLeft]);
					
                                                dosWorkers[w][v].writeUTF(ips[w][myRight]);
                                                dosWorkers[w][v].writeInt(portsX[w][myRight]);
					
                                                dosWorkers[w][v].writeUTF(ips[myTop][v]);
                                                dosWorkers[w][v].writeInt(portsY[myTop][v]);
					
                                                dosWorkers[w][v].writeUTF(ips[myBottom][v]);
                                                dosWorkers[w][v].writeInt(portsY[myBottom][v]);
					}  
				
			}
		} catch (IOException ioe) { 
			System.out.println("error: Coordinator assigning neighbor infor.");  
			ioe.printStackTrace(); 
		} 
	}
	
	void distribute() { 
		/*a = MatrixMultiple.createDisplayMatrix(n); 
		b = MatrixMultiple.createDisplayMatrix(n);
		System.out.println("The matrix a is:");
		MatrixMultiple.displayMatrix(a,n); 
		System.out.println("");
		System.out.println("The matrix b is:");
		MatrixMultiple.displayMatrix(b,n);
                        */
		for (int w = 0; w<workers; w++){                               
		
			for(int v=0;v<workers;v++)		
			{	 
			for(int k=0;k<m;k++){                            
                            for(int l=0;l<m;l++)
                            {
				try { 
					dosWorkers[w][v].writeInt(a[w*m+k][v*m+l]);
				} catch (IOException ioe) { 
					System.out.println("error in distribute: " + w + ", " + v);  
					ioe.printStackTrace(); 
				}
                            }
                        }
                        }
			 
			
                        }
                
                for (int w = 0; w<workers; w++){
                    
			for(int v=0;v<workers;v++)		
			{	 
			for(int k=0;k<m;k++){
                            for(int l=0;l<m;l++)
                            {
				try { 
					dosWorkers[w][v].writeInt(b[w*m+k][v*m+l]);  
				} catch (IOException ioe) { 
					System.out.println("error in distribute: " + w + ", " + v);  
					ioe.printStackTrace(); 
				}
                            }
                        }
			} 
			 
			
                        }
		System.out.println("The matrix C is:");
                int [][]result=new int [n][n];
		for (int w = 0; w < workers; w++)
		
			
			for(int v=0;v<workers;v++)		
			{	// send blocks 
				
                            for(int k=0;k<m;k++)
					for(int l=0;l<m;l++)
									
				try { 
					c[w*m+k][v*m+l]=disWorkers[w][v].readInt();  
				}
                                catch (IOException ioe) { 
					System.out.println("error in distribute: " + w + ", " + v);  
					ioe.printStackTrace(); 
				}
				 
			}  
			
		
		MatrixMultiple.displayMatrix(c);
        }


        

	
	public static void main(String[] args) { 
		if (args.length != 3) {
			System.out.println("usage: java Coordinator maxtrix-dim number-nodes coordinator-port-num"); 
		} 
                System.out.println("Enter size of matrix");
                Scanner l=new Scanner(System.in);
		int n = l.nextInt();
                
                System.out.println("Enter number of workers");
                Scanner k=new Scanner(System.in);
		int numNodes = k.nextInt();
                int port=8080;
                Coordinator coor = new Coordinator(n ,numNodes, port);
                coor.run();
		
	}

    @Override
    public void run() {
        this.configurate(8080); 
		this.distribute(); 
		//System.out.println("C");
                //MatrixMultiple.displayMatrix(c);
                System.out.println("Done");
    
    }
    
}

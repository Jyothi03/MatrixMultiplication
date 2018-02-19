package socket_example_1row;

import java.io.*;
import java.net.InetAddress;
import matrix.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

public class Worker implements Runnable {

	int addressA;
	int addressB;
	int localValueA;
	int localValueB;
	Connection conn1;
        Connection conn2;
        
        DataOutputStream[][] dosWorker;
        DataInputStream[][] disWorker;
        int n;
        int m;
        
	int dim; 
	int width; 
	int[][] a;
	int[][] b;
	int[][] c;
	DataInputStream disCoor;
	DataOutputStream dosCoor;
	
        DataOutputStream dosLeft;
	DataOutputStream dosTop;
        
        DataInputStream disRight;
	DataInputStream disBottom;
        String coordinatorIP;
	int coordinatorPort;
	// Stack parament;
	public Worker(int addressA,int addressB, int localValueA,int localValueB,String coordinatorIP,int coordinatorPort) {
		this.addressA= addressA;
		this.addressB = addressB;
                this.localValueA=localValueA;
	        this.localValueB=localValueB;
		this.coordinatorIP=coordinatorIP;
		this.coordinatorPort=coordinatorPort;
		//parament=new Stack();
	}

	void configurate(String coorIP, int coorPort) {
		try {
                    	System.out.printf("[%d,%d]Configuring Worker [%d,%d]\n",addressA,addressB,addressA,addressB);

			Connection conn1 = new Connection(localValueA);
                        Connection conn2 = new Connection(localValueB);
                        System.out.printf("[%d,%d]Attempting connection to coordinator\n",addressA,addressB,addressA,addressB);

			DataIO dio = conn1.connectIO(coorIP, coorPort); 
			dosCoor = dio.getDos();  
			dosCoor.writeInt(addressA);
			dosCoor.writeInt(addressB);
			dosCoor.writeUTF(InetAddress.getLocalHost().getHostAddress());
			dosCoor.writeInt(localValueA);
                        dosCoor.writeInt(localValueB);
			disCoor = dio.getDis();
			n = disCoor.readInt(); // read total matrix dimension
			m = disCoor.readInt(); // get my matrix  				
			//width = disCoor.readInt();
			a = new int[m][m];
			b = new int[m][m];
			c = new int[m][m];
                       
                        String ipLeft = disCoor.readUTF(); // left block connection info
			
                        int portLeft = disCoor.readInt();
			String ipRight = disCoor.readUTF(); // right block connection info
			
                        int portRight = disCoor.readInt();
			String ipTop = disCoor.readUTF();
			int portTop = disCoor.readInt();
			String ipBottom = disCoor.readUTF();
			int portBottom = disCoor.readInt();
                        if (addressB% 2 == 0) { // Even # worker connecting manner
				System.out.printf("[%d,%d]Attempting connection to left worker %d:%d \n", addressA,addressB,(addressA+(n/m)-1)%(n/m),
						addressB);
				dosLeft = conn1.connect2write(ipLeft, portLeft);
				System.out.printf("[%d,%d]Connected to left worker\n",addressA,addressB);
				System.out.printf("[%d,%d]Waiting for connection from right worker with id [%d,%d]\n",addressA,addressB,
								(addressA + 1) % (n / m), addressB);
                                disRight = conn1.accept2read();
				System.out.printf("[%d,%d]Connected to right worker\n",addressA,addressB);
			}
                        else {

				System.out.printf("[%d,%d]Waiting for connection from bottom worker with id [%d,%d]\n",addressA,addressB,
								(addressA), (addressB + 1) % (n / m));
                                disRight = conn1.accept2read();
				System.out.printf("[%d,%d]Connected to right worker\n",addressA,addressB);
				System.out.printf("[%d,%d]Attempting connection to left worker %d:%d \n", addressA,addressB,(addressA+(n/m)-1)%(n/m),
						addressB);
                                dosLeft = conn1.connect2write(ipLeft, portLeft);
				System.out.printf("[%d,%d]Connected to left worker\n",addressA,addressB);
			}

			// this worker is now connected to its left and right workers
			if (addressA % 2 == 0) {
				System.out.printf("[%d,%d]Attempting connection to top worker %d:%d \n",addressA,addressB,
						addressA, (addressB+(n/m)-1)%(n/m));
				dosTop = conn2.connect2write(ipTop, portTop);
				System.out.printf("[%d,%d]Connected to top worker\n",addressA,addressB);
				System.out.printf("[%d,%d]Waiting for connection from bottom worker with id [%d,%d]\n",addressA,addressB,
								(addressA), (addressB + 1) % (n / m));
                                disBottom = conn2.accept2read();
				System.out.printf("[%d,%d]Connected to bottom worker\n",addressA,addressB);
			} else {

				System.out.printf("[%d,%d]Waiting for connection from bottom worker with id [%d,%d]\n",addressA,addressB,
								(addressA), (addressB + 1) % (n / m));


				disBottom = conn2.accept2read();
				System.out.printf("[%d,%d]Connected to bottom worker\n",addressA,addressB);
				System.out.printf("[%d,%d]Attempting connection to top worker %d:%d \n",addressA,addressB,
						addressA, (addressB+(n/m)-1)%(n/m));
				dosTop = conn2.connect2write(ipTop, portTop);
				System.out.printf("[%d,%d]Connected to top worker\n",addressA,addressB);
			}
                        } catch (IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.printf("[%d,%d]Configuration done.\n",addressA,addressB);
	}

                  /*  DataOutputStream[][] dosWorker = new DataOutputStream[m][m];
                    DataInputStream[][] disWorker = new DataInputStream[m][m];
		   sWorker=new MyServer(localPort,parament);
                       
			for(int i=0;i<m;i++)
			{
				for(int j=0;j<m;j++)
				{
					String ipWorker = disCoor.readUTF();
					int portWorker = disCoor.readInt();
					System.out.println("Configuration for."+portWorker);
					dosWorker[i][j] = conn.connect2write(ipWorker, portWorker);	
					
				}
			}
					
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 
		System.out.println("Configuration done."); 
	}
	
	public void sendLeft(int i, int j, int n)
	{
		int left=(j+1)%dim;
		try
		{
			System.out.println("send left!"+i+" "+left);
			dosWorker[i][left].writeInt(n);
			dosWorker[i][left].flush();
			
		}
		catch(Exception ae){
		}
	}
	public void sendUp(int i,int j,int n)
	{
		int up=(i+1)%dim;
		try
		{
			System.out.println("send up!"+up+" "+j);
			dosWorker[up][j].writeInt(n);
			dosWorker[up][j].flush();
			
		}
		catch(Exception ae){
		}
	}
	public int receiveLeft(int i,int j)
	{
		try
		{
			int left=(j+1)%dim;
			return disWorker[i][left].readInt();
		}
		catch(Exception ae)
		{
		}
		return 0;
	}
	public int receiveUp(int i, int j)
	{
		try
		{
		int up=(i+1)%dim;
		return disWorker[up][j].readInt();
		}
		catch(Exception ae)
		{
		}
		return 0;
	}
       */
	void compute() {
		
		int dim = m;
		int width = m;
		
	        System.out.printf("[%d,%d]Waiting for initial matrix a from coordinator\n",addressA,addressB);
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < width; j++) {
				try {
					a[i][j] = disCoor.readInt();
				} catch (IOException ioe) {
					System.out.println("error: " + i + ", " + j);
					ioe.printStackTrace();
				}
			}
		}
		System.out.printf("[%d,%d]Recieved initial matrix a\n",addressA,addressB);
		MatrixMultiple.displayMatrix(a);
		System.out.printf("[%d,%d]Waiting for initial matrix b from coordinator\n",addressA,addressB);
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < width; j++) {
				try {
					b[i][j] = disCoor.readInt();
				} catch (IOException ioe) {
					System.out.println("error: " + i + ", " + j);
					ioe.printStackTrace();
				}
			}
		}
		System.out.printf("[%d,%d]Recieved initial matrix b\n",addressA,addressB);
		MatrixMultiple.displayMatrix(b);

		int iterations = n;
		while (iterations-- > 0) {
			System.out.printf("[%d,%d]Iterations left %d\n",addressA,addressB,iterations);
			// perform computation
			for (int i = 0; i < dim; i++)
				for (int j = 0; j < width; j++) {
					c[i][j] += a[i][j] * b[i][j];
				}
                        System.out.println("Matrix Cij");
                        MatrixMultiple.displayMatrix(c);

			// shift matrix a toward left
			int[] tempIn;
			int[] tempOut;
			
			if (addressB % 2 == 0) { // Even # worker shifting procedure
                            System.out.println("Matrix A before shifting");
                            MatrixMultiple.displayMatrix(a);
				System.out.printf("[%d,%d]sending row from a\n",addressA,addressB);
				for (int i = 0; i < dim; i++) {
					try {
						dosLeft.writeInt(a[i][0]);
					} catch (IOException ioe) {
						System.out
								.println("error in sending to left, row=" + i);
						ioe.printStackTrace();
					}
				}
				System.out.printf("[%d,%d]sent row from a\n",addressA,addressB);
				// local shift
				for (int i = 0; i < dim; i++) {
					for (int j = 1; j < width; j++) {
						a[i][j - 1] = a[i][j];
					}
				}
				System.out.printf("[%d,%d]attempting read row to a\n",addressA,addressB);
				// receive the rightmost column
				for (int i = 0; i < dim; i++) {
					try {
						a[i][width - 1] = disRight.readInt();
					} catch (IOException ioe) {
						System.out
								.println("error in receiving from right, row="
										+ i);
						ioe.printStackTrace();
					}
				}
				System.out.printf("[%d,%d]read row to a\n",addressA,addressB);
				System.out.printf("[%d,%d]Shifted matrix a\n",addressA,addressB);
				//MatrixMultiple.displayMatrix(a);
			System.out.println("Matrix A after shifting");
                            MatrixMultiple.displayMatrix(a);	
				
				 

			} else { // Odd # worker shifting procedure

				tempIn = new int[dim];
				tempOut = new int[dim];
				System.out.printf("[%d,%d]attempting read row to a\n",addressA,addressB);
                                System.out.println("Matrix A before shifting");
                                MatrixMultiple.displayMatrix(a);
				for (int i = 0; i < dim; i++) { // receive a column from right
					try {
						tempIn[i] = disRight.readInt();
					} catch (IOException ioe) {
						System.out
								.println("error in receiving from right, row="
										+ i);
						ioe.printStackTrace();
					}
				}
				
				for (int i = 0; i < dim; i++) { // local shift
					tempOut[i] = a[i][0];
				}
				for (int i = 0; i < dim; i++) {
					for (int j = 1; j < width; j++) {
						a[i][j - 1] = a[i][j];
					}
				}
				for (int i = 0; i < dim; i++) {
					a[i][width - 1] = tempIn[i];
				}
				System.out.printf("[%d,%d]read row to a\n",addressA,addressB);
				System.out.printf("[%d,%d]sending row from a\n",addressA,addressB);
				for (int i = 0; i < dim; i++) { // send leftmost column to left
												// node
					try {
						dosLeft.writeInt(tempOut[i]);
					} catch (IOException ioe) {
						System.out.println("error in sending left, row=" + i);
						ioe.printStackTrace();
					}
				}
				System.out.printf("[%d,%d]sent row from a\n",addressA,addressB);
				System.out.printf("[%d,%d]Shifted matrix a\n",addressA,addressB);
				//MatrixMultiple.displayMatrix(a);
				System.out.println("Matrix A after shifting");
                                MatrixMultiple.displayMatrix(a);
			}
			if(addressA%2==0)
			{

				// do the same for b now
				System.out.printf("[%d,%d]sending col from b\n",addressA,addressB);
				System.out.println("Matrix B before shifting");
                                MatrixMultiple.displayMatrix(b);
                                for (int i = 0; i < width; i++) {
					try {
						dosTop.writeInt(b[0][i]);
					} catch (IOException ioe) {
						System.out.println("error in sending to top, col=" + i);
						ioe.printStackTrace();
					}
				}
				System.out.printf("[%d,%d]sent col from b\n",addressA,addressB);
				// local shift
				for (int i = 1; i < dim; i++) {
					for (int j = 0; j < width; j++) {
						b[i - 1][j] = b[i][j];
					}
				}
				// receive the bottom most column
				System.out.printf("[%d,%d]attempting read col to b\n",addressA,addressB);
				for (int i = 0; i < width; i++) {
					try {
						b[dim - 1][i] = disBottom.readInt();
					} catch (IOException ioe) {
						System.out
								.println("error in receiving from bottom, col="
										+ i);
						ioe.printStackTrace();
					}
				}
				System.out.printf("[%d,%d]read col to b\n",addressA,addressB);
				System.out.printf("[%d,%d]Shifted matrices b\n",addressA,addressB);
				//MatrixMultiple.displayMatrix(b);
                                System.out.println("Matrix B after shifting");
                                MatrixMultiple.displayMatrix(b);
                                
			}
			else
			{
				// do the same for b now
				
				tempIn = new int[width];
				tempOut = new int[width];
				// receive the bottom most column
				System.out.printf("[%d,%d]Attempting read col to b \n",addressA,addressB);
				System.out.println("Matrix B before shifting");
                                MatrixMultiple.displayMatrix(b);
                                for (int i = 0; i < width; i++) {
					try {
						tempIn[i] = disBottom.readInt();
					} catch (IOException ioe) {
						System.out
								.println("error in receiving from bottom, col="
										+ i);
						ioe.printStackTrace();
					}
				}
				System.out.printf("[%d,%d]read col to b\n",addressA,addressB);
				for (int i = 0; i < width; i++) { // local shift
					tempOut[i] = b[0][i];
				}
				for (int i = 1; i < dim; i++) {
					for (int j = 0; j < width; j++) {
						b[i - 1][j] = b[i][j];
					}
				}
				for (int i = 0; i < width; i++) {
					b[dim - 1][i] = tempIn[i];
				}
				System.out.printf("[%d,%d]sending col from b\n",addressA,addressB);
				for (int i = 0; i < width; i++) { // send leftmost column to left
					// node
					try {
						dosTop.writeInt(tempOut[i]);
					} catch (IOException ioe) {
						System.out.println("error in sending top, col=" + i);
						ioe.printStackTrace();
					}
				}System.out.printf("[%d,%d]sent col from b\n",addressA,addressB);

				System.out.printf("[%d,%d]Shifted matrices b \n",addressA,addressB);
				//MatrixMultiple.displayMatrix(b);
                                System.out.println("Matrix B after shifting");
                                MatrixMultiple.displayMatrix(b);
			
			}

		}
		System.out.printf("[%d,%d]Completed computation, sending result\n",addressA,addressB);
		//now write output to coordinator
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < width; j++) {
				try {
					dosCoor.writeInt(c[i][j]);
				} catch (IOException e) {
					e.printStackTrace();
					throw new Error("Error sending output");
				}
			}
		}
		
		
		System.out.printf("[%d,%d]All done\n",addressA,addressB);

        }

	public static void main(String[] args) {
            System.out.println("Enter worker x id");
		Scanner s=new Scanner(System.in);
                int workeri =s.nextInt(); 
	System.out.println("Enter worker y id");
		Scanner d=new Scanner(System.in);
                int workerj =d.nextInt(); 
		System.out.println(workeri+" worker "+workerj);
                System.out.println("Enter worker x port number");
		Scanner f=new Scanner(System.in);               
		int portNum1 =f.nextInt(); 
                System.out.println("Enter worker y port number");
		Scanner g=new Scanner(System.in);               
		int portNum2 =g.nextInt(); 
                
                String coordinatorIP="localhost";
                int coordinatorPort=8080;
		Worker worker;
                worker = new Worker( workeri, workerj, portNum1,portNum2,coordinatorIP,coordinatorPort);
		//worker.configurate("127.0.0.1", 8080);
		//worker.compute();
		try {Thread.sleep(1200);} catch (Exception e) {e.printStackTrace();}
		System.out.println("Done.");
                worker.run();
	}

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                  this.configurate("localhost", 8080);
		  this.compute();
		  System.out.printf("[%d,%d]Done.\n",addressA,addressB);
    
    }
}

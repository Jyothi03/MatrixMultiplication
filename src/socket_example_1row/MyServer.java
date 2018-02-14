package socket_example_1row;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Stack;
public class MyServer implements Runnable{ 
	ServerSocket ss;
	Socket s; 
	Stack para;
	ArrayList al=new ArrayList();

	MyServer(int port,Stack stack)throws IOException{
		ss=new ServerSocket(port); // create server socket 
		para=stack;
	 		Thread th=new Thread(this);
	 		th.start();
} 
		
	public void run()
	{
		try
		{
			while(true){ 
			System.out.println("Server is listening");  
			s=ss.accept(); //accept the client socket
		al.add(s); // add the client socket in arraylist
		System.out.println("Client is Connected");  
		MyThread r=new MyThread(s,al,para);//new thread for receive and sending the messages 
		Thread t=new Thread(r);
		t.start();
		}
		}
		catch(IOException ae){
		}
		
	 
	}
	
	} 
//class is used to receive the message and send it to all clients 
class MyThread implements Runnable{ 
	Socket s; 
	static ArrayList al; 
	DataInputStream din;
	Stack para;
		
 DataOutputStream dout;
 MyThread(Socket s, ArrayList al,Stack stack){
	 this.s=s;
	 this.al=al; 
	 para=stack;
	 } 
 public void run(){ 
	 String str;
	 int i=1;
	 try{ 
		 din=new DataInputStream(s.getInputStream()); 
		 }catch(Exception e){}
	 while(i==1){
		 try{ 
			 int x=din.readInt(); //read the message
			 para.push(x);
			 }catch (IOException e){} 
		 }
	 } // send it to all clients 
 public void distribute(String str)throws IOException{ 
	 Iterator i=al.iterator();
 Socket st;
 while(i.hasNext()){ 
	 st=(Socket)i.next(); 
	 dout=new DataOutputStream(st.getOutputStream());
	 dout.writeUTF(str); dout.flush(); 
	 } 
 } 
 }
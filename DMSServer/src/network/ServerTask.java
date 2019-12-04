package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//클라이언트와 연결을 담당하는 쓰레드
//서버소켓을 생성하고, 서버소켓이 클라이언트와 연결을 accept 하면
//해당 클라이언트에게 새 ReceiveThread와 SendThread를 만들어 준다.

class ServerTask implements Runnable
{
	private int port;
	ServerSocket serverSocket;
	private static final int THREAD_CNT = 2;
	private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_CNT);
	
	private boolean stop = false;
	
	public ServerTask(int PORT)
	{
		this.port = PORT;
	}
	
	@Override
	public void run()
	{
		try
		{
			serverSocket = new ServerSocket(port);
			
			//NetworkHandler에서 종료요청이 오기전까지 계속 클라이언트의 요청을 받아들인다.
			while(!stop)
			{
				System.out.println("waiting for client...");
				Socket socket = serverSocket.accept();
				try
				{
					// 요청이 오면 스레드 풀의 스레드로 소켓을 넣어줍니다.
					// 이후는 스레드 내에서 처리합니다.
					threadPool.execute(new ClientTask(socket));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		System.out.println("클라이언트스레드풀 종료 시작");
		stop = true;
		try
		{
			//이부분에서 예외잡힘...
			//Socket socket = serverSocket.accept(); 이거랑 관련있는것 같은데, 해결법 찾아보는중...
			serverSocket.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		threadPool.shutdown();
		System.out.println("클라이언트스레드풀 종료됨");
	}
}
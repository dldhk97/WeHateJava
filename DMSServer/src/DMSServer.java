
import DB.DBHandler;
import utils.Logger;
import utils.SocketHelper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//기숙사 관리 시스템(Dormitory Management System)

//기능
//DB와 클라이언트 사이에서 중간다리 역할을 한다.
//클라이언트와 통신하며 클라이언트가 원하는 기능을 수행한다.
//클라이언트는 일반사용자(학생) 일 수도 있으며, 고급사용자(관리자/선생님)일 수도 있다.
//사용자 타입에 무관하게 서버는 클라이언트와 커넥션을 맺고있어야 한다.

//디자인
//패키지 각각에는 기본적으로 Handler가 주어지고, 이 Handler을 통해 패키지 내 다른 클래스들을
//컨트롤하는 방식으로 하고자 한다.

//서버 자체에서 상호작용할 수 있는 메뉴도 필요하다고 생각한다.
//메뉴가 없으면 클라이언트와 소켓 통신으로만 서버 관리가 가능한데 그렇게 하기 위한 프로토콜을 설계해야된다.
//개발 시간을 단축시키기 위해서 서버 종료, 디버깅모드, 서버 실행 등 간단한 메뉴는 서버 자체에서
//할 수 있는게 좋다고 판단하였다.

public class DMSServer {
    public static void main(String[] args){
        boolean running = true;
        ServerSocket sSocket = null;
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Logger.INSTANCE.print("서버 초기화 중...");

        try {
            sSocket = new ServerSocket(SocketHelper.port);
        } catch (IOException e) {
            Logger.INSTANCE.print("서버 소켓 생성 실패!! 서버를 종료합니다.");
            e.printStackTrace();
            return;
        }
        try {
            DBHandler.INSTANCE.init();
        } catch (ClassNotFoundException | SQLException e) {
            Logger.INSTANCE.print("DB 연결 실패!! 서버를 종료합니다.");
            e.printStackTrace();
            return;
        }

        Logger.INSTANCE.print("서버 초기화 완료");

        // 현재 running 을 멈추는 기능 없음.
        while (running) {
            Socket socket = null;
            try {
                socket = sSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SocketHelper socketHelper = new SocketHelper(socket);
            threadPool.execute(new ServerTask(socketHelper));
        }

        threadPool.shutdown();
    }
}
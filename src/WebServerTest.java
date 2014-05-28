import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServerTest {
    private static final String ROOT_PATH = "/Users/jun.her/workspace/path-to-documentroot/";

    ServerSocket ss;
    Socket socket;

    public WebServerTest(int port){
        try{
            //サーバーソケットのインスタンス生成
            ss = new ServerSocket(port);

            while(true){
                socket = ss.accept();
                System.out.println("Info : クライアントと接続されました。");
                //接続処理スレッド
                ConnectionThread ct = new ConnectionThread(socket);
                ct.start();
            }
        } catch (Exception ex) {
            System.out.println("Error : ServerSocket インスタンス生成失敗");
            ex.printStackTrace();
        }
    }

    static class ConnectionThread extends Thread {
        Socket socket;

        public ConnectionThread(Socket sc) {
            socket = sc;
        }
        //接続処理スレッド
        public void run(){
            try {
                PrintStream ps = new PrintStream(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inline = reader.readLine();
                while (reader.ready() && inline != null) {
                    System.out.println(inline);

                    inline = reader.readLine();
                }

                String filePath = ROOT_PATH + "test.html";
                File file = new File(filePath);

                ps.println("HTTP//1.0 200 OK");
                ps.println("MIME_version : 1.0");
                ps.println("Content_Type : text/html");
                ps.println("Content_length : "+ (int)file.length());
                ps.println("");

                sendfile(ps, file);
                ps.flush();
                ps.close();

            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            return;
        }

        void sendfile(PrintStream ps, File file){
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(file));

                int len = (int)file.length();
                byte buf[] = new byte[len];

                dis.readFully(buf);
                ps.write(buf, 0 , len);
                ps.flush();
                dis.close();
            } catch(Exception ex){
                System.out.println("ブラウザへ書き込み失敗");
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ
        if(args.length != 1){
            System.out.println("ポートを指定してください。");
            System.out.println("ex) java WebServerTest 50000");
            return;
        }

        try {
            new WebServerTest(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            // TODO 自動生成された catch ブロック
            System.out.println("ポートは数値を指定してください。");
            return;
        }
    }

}


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.ws.Response;


public class WebServer03 {
    private static final String ROOT_PATH = "/Users/jun.her/workspace/WebServerTest/src/";

    ServerSocket ss;
    Socket socket;

    public WebServer03(int port){
        try{
            //サーバーソケットのインスタンス生成
            ss = new ServerSocket(port);
//            Log.debug("INFO", "サーバーソケットのインスタンス生成成功");
            while(true){
                //クライアントからの接続を待つ
                socket = ss.accept();
//                Log.debug("INFO", "クライアントと接続されました。");
//                System.out.println("Info : クライアントと接続されました。");
                //接続処理スレッド
                ConnectionThread ct = new ConnectionThread(socket, port);
                ct.start();
            }
        } catch (Exception ex) {
            System.out.println("Error : ServerSocket インスタンス生成失敗");
            ex.printStackTrace();
        }
    }

    static class ConnectionThread extends Thread {
        Socket socket;
        int portNo;

        public ConnectionThread(Socket sc, int port) {
            socket = sc;
            portNo = port;
        }
        //接続処理スレッド
        public void run(){
            try {
                //サーバーへデータを送るために出力ストリームを作成
                PrintStream ps = new PrintStream(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inline = reader.readLine();
//                Log.debug("INFO", "*****HTTPリクエストを読み込みを開始ます。*****");
                Log.debug("", "HTTP Request");
                while (reader.ready() && inline != null) {
                    Log.debug("HTTPリクエスト", inline);
                    System.out.println(inline);

                    inline = reader.readLine();
                }
//                Log.debug("INFO", "*****HTTPリクエストを読み込みが終了しました。*****");

                String filePath = ROOT_PATH + "test.html";
                File file = new File(filePath);

                ps.println("HTTP//1.0 302 Found");
                ps.println("MIME_version : 1.0");
                ps.println("Content_Type : text/html");
                ps.println("location : " + "http://www.net-marketing.co.jp/");
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
                Log.debug("INFO", "*****ブラウザへの書き込みを開始します。*****");
                DataInputStream dis = new DataInputStream(new FileInputStream(file));

                int len = (int)file.length();
                byte buf[] = new byte[len];

                dis.readFully(buf);
                ps.write(buf, 0 , len);

                Log.debug("", "HTTP Response");
                Log.debug("", new String(buf,"UTF-8"));
                ps.flush();
                dis.close();
                Log.debug("INFO", "*****ブラウザへの書き込みが終了しました。*****");
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
            Log.debug("エラー","ポートを指定してください。¥¥n ex) java WebServerTest 50000");
//            System.out.println("ex) java WebServerTest 50000");
            return;
        }

        try {
            new WebServer03(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            // TODO 自動生成された catch ブロック
            Log.debug("エラー","ポートは数値を指定してください。");
            System.out.println("ポートは数値を指定してください。");
            return;
        }
    }

}

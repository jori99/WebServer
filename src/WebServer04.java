
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.xml.ws.Response;


public class WebServer04 {
    private static final String ROOT_PATH = "/Users/jun.her/workspace/WebServerTest/src/";

    ServerSocket ss;
    Socket socket;

    public WebServer04(int port){
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
                boolean cookieFlg = false;
                String strCookie = "Set-Cookie:";
                String strCookie2 = "Set-Cookie:";
                Log.debug("INFO", "*****HTTPリクエストを読み込みを開始ます。*****");
                Log.debug("", "HTTP Request");
                while (reader.ready() && inline != null) {
                    Log.debug("HTTPリクエスト", inline);

                    if("/login".equals(inline.split(" ")[1])){
                        cookieFlg = true;
                        Calendar cal = Calendar.getInstance();
                        strCookie = strCookie + "TEST01=test;expires=" + new Date(cal.getTimeInMillis() + (60*5*1000)-32400000);
                        strCookie2 = strCookie2 + "TEST02=" + randomStr(10) + ";expires="+new Date(cal.getTimeInMillis() + (60*60*24*1000)-32400000);
                    }else if("/logout".equals(inline.split(" ")[1])){
                        cookieFlg = true;
                        Calendar cal = Calendar.getInstance();
                        strCookie = strCookie + "TEST01=test;expires=" + new Date(cal.getTimeInMillis() -32400000);
                        strCookie2 = strCookie2 + "TEST02=" + randomStr(10) + ";expires="+new Date(cal.getTimeInMillis() -32400000);
                    }

                    System.out.println(inline);
                    inline = reader.readLine();
                }
//                Log.debug("INFO", "*****HTTPリクエストを読み込みが終了しました。*****");

                String filePath = ROOT_PATH + "test.html";
                File file = new File(filePath);

                ps.println("HTTP/1.1 200 OK");
                ps.println("Content_Type : text/html");
                ps.println("Content_length : "+ (int)file.length());
                if(cookieFlg){
                    ps.println(strCookie);
                    ps.println(strCookie2);
                }
                ps.println("");

                sendfile(ps, file);
                ps.close();
            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            return;
        }

        void sendfile(PrintStream ps, File file){
            try {
//                Log.debug("INFO", "*****ブラウザへの書き込みを開始します。*****");
                DataInputStream dis = new DataInputStream(new FileInputStream(file));

                int len = (int)file.length();
                byte buf[] = new byte[len];

                dis.readFully(buf);
                ps.write(buf, 0 , len);

                Log.debug("", "HTTP Response");
                Log.debug("", new String(buf,"UTF-8"));
                ps.flush();
                dis.close();
//                Log.debug("INFO", "*****ブラウザへの書き込みが終了しました。*****");
            } catch(Exception ex){
                System.out.println("ブラウザへ書き込み失敗");
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    static String randomStr(int cnt){
        String alNum = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String result = "";
        Random ran = new Random();
        for(int i=0;i<cnt;i++){
            int temp = ran.nextInt(alNum.length());
            result += alNum.substring(temp, temp+1);
        }
        return result;
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
            new WebServer04(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            // TODO 自動生成された catch ブロック
            Log.debug("エラー","ポートは数値を指定してください。");
            System.out.println("ポートは数値を指定してください。");
            return;
        }
    }
}

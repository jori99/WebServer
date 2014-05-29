import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Log {
    private static final String LOG_PATH = "/Users/jun.her/workspace/WebServerTest/log/";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static FileWriter fw = null;

    public static void start() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String filePath = LOG_PATH + "log_" +  sdf.format(new Date()) + ".log";
            fw = new FileWriter(filePath, true);
        } catch (IOException e){}
    }

    public static void close() {
        try {
            fw.close();
        } catch (IOException e){}
    }

    public static void debug(String logKind, String msg) {
        start();
        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            fw.write(sdf.format(new Date())+ " : ");
//            fw.write("【" + logKind + "】");
            fw.write(msg + LINE_SEPARATOR);
            fw.flush();
        } catch (IOException e) {
            System.err.println("ログファイル書き込みで例外発生!!");
        } finally {
            close();
        }
    }
}

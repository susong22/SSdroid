package co.kr.ssdroidlib.comm;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created By hhsong 2020.05.12
 */
public class SUtils {
    public static long gNewID = 0;
    public static long NewID()
    {
        if(gNewID == 0) {
            gNewID = (new Date()).getTime();
            return gNewID;
        }
        gNewID++;
        return gNewID;
    }

    /**
     * Get SDCard Path
     * @return
     */
    public static String getExternalSdCardPath() {
        String path = null;

        File sdCardFile = null;
        List<String> sdCardPossiblePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard");

        for (String sdPath : sdCardPossiblePath) {
            File file = new File("/mnt/", sdPath);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                }
                else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
        }
        else {
            sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        return sdCardFile.getAbsolutePath();
    }
}

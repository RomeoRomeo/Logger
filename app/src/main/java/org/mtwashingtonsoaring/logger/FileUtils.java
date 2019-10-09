package org.mtwashingtonsoaring.logger;

import android.os.Environment;
import java.io.File;


public class FileUtils {
    public static String getAppDir(){
        return MyApp.getContext().getExternalFilesDir(null) + "/" + MyApp.getContext().getString(R.string.app_name);
    }

    public static File createDirIfNotExist(String path){
        File dir = new File(path);
       // File testdir = new File("/storage/emulated/0/Android/data/org.mtwashintonsoaring.logger/files/Logger");
        boolean parrentExists = dir.exists();
        boolean doesExist = dir.exists();
        if( !doesExist ){
            dir.mkdirs();
        }
        doesExist = dir.exists();
        return dir;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
}

package org.mtwashingtonsoaring.logger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.*;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.widget.*;


public class TestSDCardWrite extends AppCompatActivity {

    EditText txtData;
    Button btnWriteSDFile;
    Button btnReadSDFile;
    Button btnClearScreen;
    Button btnClose;

    public static void start(Context context) {
        Intent intent = new Intent(context, TestSDCardWrite.class);
      //  intent.putExtra(EXTRA_TABLE_NAME_TAG, tableName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sdcard_write);


        verifyStoragePermissions(this);


        // bind GUI elements with local controls
        txtData = (EditText) findViewById(R.id.txtData);
        txtData.setHint("Enter some lines of data here...");

        btnWriteSDFile = (Button) findViewById(R.id.btnWriteSDFile);
        btnWriteSDFile.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // write on SD card file data in the text box
                try {
                    File myFile = new File("/mnt/external_sd/mysdfile.txt");
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter =
                            new OutputStreamWriter(fOut);
                    myOutWriter.append(txtData.getText());
                    myOutWriter.close();
                    fOut.close();
                    Toast.makeText(getBaseContext(),
                            "Done writing SD 'mysdfile.txt'",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }// onClick
        }); // btnWriteSDFile

        btnReadSDFile = (Button) findViewById(R.id.btnReadSDFile);
        btnReadSDFile.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // write on SD card file data in the text box
                try {
                    File myFile = new File("/mnt/external_sd/mysdfile.txt");
                    FileInputStream fIn = new FileInputStream(myFile);
                    BufferedReader myReader = new BufferedReader(
                            new InputStreamReader(fIn));
                    String aDataRow = "";
                    String aBuffer = "";
                    while ((aDataRow = myReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";
                    }
                    txtData.setText(aBuffer);
                    myReader.close();
                    Toast.makeText(getBaseContext(),
                            "Done reading SD 'mysdfile.txt'",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

            }// onClick
        }); // btnReadSDFile

        btnClearScreen = (Button) findViewById(R.id.btnClearScreen);
        btnClearScreen.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // clear text box
                txtData.setText("");
//                String state = Environment.getExternalStorageState();
//                Toast.makeText(getBaseContext(), state,
//                        Toast.LENGTH_SHORT).show();
//                File mnt = new File("/storage");
//                if (!mnt.exists())
//                    mnt = new File("/mnt");
//
//                File[] roots = mnt.listFiles(new FileFilter() {
//
//                    @Override
//                    public boolean accept(File pathname) {
//                        boolean isSymLink = true;
//                        try{
//                             isSymLink = isSymlink(pathname);
//                        } catch (IOException e){
//                            Log.e("RRR TestSDCardWrite", "io exception " + e.getMessage());
//                        }
//                        return pathname.isDirectory() && pathname.exists()
//                                && pathname.canWrite() && !pathname.isHidden()
//                                && !isSymLink;
//                    }
//                });
                String extsdPath = System.getenv("SECONDARY_STORAGE");
                File extStore = Environment.getExternalStorageDirectory();
                Toast.makeText(getBaseContext(), extsdPath,
                        Toast.LENGTH_LONG).show();
                Log.e("RRR testsd","secondary storage = " + extsdPath);
                Log.e("RRR testsd","ExternalStorageDirectory = " + extStore.toString());
                File extFile = getExternalFilesDir(null);
                Log.e("RRR testsd","getExternalFilesDir = " + extFile.toString());
//                Log.e ("RRR", "roots length " + roots.length);
//                for (int i = 0; i < roots.length;i++){
//                    Log.e ("RRR", "roots returned " + roots [i].toString());
//                }

            }
        }); // btnClearScreen

        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // clear text box
                finish();
            }
        }); // btnClose

    }// onCreate

    public static boolean isSymlink(File file) throws IOException {
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}





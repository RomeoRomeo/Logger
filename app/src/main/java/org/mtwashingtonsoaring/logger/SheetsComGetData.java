package org.mtwashingtonsoaring.logger;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SheetsComGetData extends Activity
        implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private TextView titleText;
    ProgressDialog mProgress;
    private static final String EXTRA_GUID = "org.mtwashingtonsoaring.logger.SheetsComGUID";
    public static final String READ_SHEET_DATA = "ReadData";
    public static final String WRITE_SHEET_DATA = "WriteData";
    private String readWriteMode;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String TITLE_TEXT = "Retrieving Data";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    public static void start(Context context, String extraGUID) {
        Intent intent = new Intent(context, SheetsComGetData.class);
        intent.putExtra(EXTRA_GUID, extraGUID);
        context.startActivity(intent);
        context = context;
    }

    /**
     * Create the activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        readWriteMode = intent.getStringExtra(EXTRA_GUID);

        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        titleText = new TextView(this);
        titleText.setText(TITLE_TEXT);
        activityLayout.addView(titleText);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());

        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();
    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String spreadsheetId = sharedPreferences.getString("prefs_sheets_url"," ");

        Log.e("RRR","got to get results 3");
        Log.e ("RRR","spreadsheetId = " + spreadsheetId );
        if (! isGooglePlayServicesAvailable()) {
            Log.e("RRR","got to acquireGooglePlayServices()");
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            Log.e("RRR","got to chooseAccount()");
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Log.e("RRR", "device off line");
            mOutputText.setText("No network connection available.");
        } else if (spreadsheetId == " "){
            Log.e("RRR", "No Spreadsheet URL");
            mOutputText.setText ("No Spreadsheet URL");
        } else {
            Log.e("RRR","got to get make request");
            new MakeRequestTask(mCredential).execute();
        }
        Log.e("RRR","should work");
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                Log.e("RRR","got to start activity for account picker");
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            Log.e("RRR","asking for permissions");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                SheetsComGetData.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;


        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Logger")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                if (readWriteMode.equals(READ_SHEET_DATA)) {
                    return getDataFromApi();
                }
                if (readWriteMode.equals(WRITE_SHEET_DATA)) {
                    return PutDataToApi();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return new ArrayList<String>(); // this would be a error from no mode set
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * @return List of names and majors
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {

            List<String> results = new ArrayList<String>();


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String spreadsheetId = sharedPreferences.getString("prefs_sheets_url","");                                                             //"1fFLur7_QlrY8Mvtd1HHfE8Fe7EXKez4ZYtZaLzL9lsc";
            String range;

            range= DBHelper.PILOT_SOURCE_SHEET;
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            results.addAll (transferDataToDB (values, DBHelper.PILOT_TABLE_NAME));

            range = DBHelper.GLIDER_SOURCE_SHEET;
            response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            values = response.getValues();
            results.addAll (transferDataToDB(values, DBHelper.GLIDER_TABLE_NAME));

            range = DBHelper.TUG_SOURCE_SHEET;
            response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            values = response.getValues();
            results.addAll (transferDataToDB(values, DBHelper.TUG_TABLE_NAME));

            range = DBHelper.TYPE_SOURCE_SHEET;
            response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            values = response.getValues();
            results.addAll (transferDataToDB(values, DBHelper.TYPE_TABLE_NAME));

            range = DBHelper.FLIGHT_SOURCE_SHEET;
            response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .setDateTimeRenderOption("FORMATTED_STRING")
                    .execute();
            values = response.getValues();
            results.addAll (transferDataToDB (values, DBHelper.FLIGHT_TABLE_NAME));

            return results;
        }

        List<String> transferDataToDB (List<List<Object>> values, String tableName){

            Object[] buffer = new Object [20];
            List<String> results = new ArrayList<String>();
            ContentValues cv = new ContentValues();
       //     String sqlBuffer = new String("");


            if (values != null && values.size() > 1) {  // dont want to use just the header row
                List headers = values.get(0);

                Log.e("RRR","headers = " + headers);
                List row;
                int headerSize = headers.size();            // find the number of data items from the size of the header row
                results.add("Pilot Names");
                for (int v = 1; v < values.size();v++){
                    row = values.get(v);
                    for (int i=0;i<headerSize;i++) {
                        buffer[i] = "";
                        if (i < row.size()) {
                            if(tableName.equals(DBHelper.FLIGHT_TABLE_NAME) && i == 1){
                                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                                try {
                                    buffer[i] = df.parse((String)row.get(i));
                                }
                                catch (java.text.ParseException e){
                                    Log.e ("RRR"," bad parse of date data in transer data to DB");
                                }
                            } else {
                                buffer[i] = row.get(i);
                            }
                        }
                        cv.put((String) headers.get(i),buffer[i].toString());
                    }
                    MyApp.db.insert(tableName,null,cv);


                    results.add((String)row.get(0));
                    row.clear();
                }

            }
            return results;
        }

        private List<String> PutDataToApi() throws IOException {

            List<String> results = new ArrayList<String>();



            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String spreadsheetId = sharedPreferences.getString("prefs_sheets_url","");
            String range;

            ValueRange writeValues = transferDataToSheets(DBHelper.FLIGHT_TABLE_NAME);
            UpdateValuesResponse response = this.mService.spreadsheets().values()
                    .update(spreadsheetId,DBHelper.FLIGHT_WRITE_RANGE,writeValues)
                    .setValueInputOption("USER_ENTERED")
                    .execute();


            range = "FlightsBackup!A:A"; // just the _id column

            ValueRange readVals = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .setDateTimeRenderOption("FORMATTED_STRING")
                    .execute();
            List<List<Object>> testValues = readVals.getValues();
            int appendSize = testValues.size();

                Log.e("RRR putDataToAPI","append size = " + appendSize);

            String appendRange = DBHelper.FLIGHT_BACKUP_TABLE_NAME + "!A" + (appendSize + 1) + ":N";
            writeValues = transferDataToSheets(DBHelper.FLIGHT_TABLE_NAME);
            response = this.mService.spreadsheets().values()
                    .update(spreadsheetId,appendRange,writeValues)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

            return results;
        }


        ValueRange transferDataToSheets ( String tableName){


            Cursor c = MyApp.db.query(tableName,null,null,null,null,null,null);


            ValueRange valueRange = new ValueRange();
            if (c.getCount() != 0) {

                c.moveToFirst();
                List<List<Object>> allValues = new ArrayList<List<Object>>();

                do {
                    List<Object> row = new ArrayList<Object>();
                    for (int col = 0; col < c.getColumnCount(); col++) {
                        if(tableName.equals(DBHelper.FLIGHT_TABLE_NAME) && col == 1 ){  // special case for date in flight table
                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                            row.add(df.format(c.getLong(col)));
                        } else {
                            row.add(c.getString(col));
                        }
                    }
                    allValues.add(row);
                } while (c.moveToNext());
                valueRange.setValues(allValues);
            }
            return valueRange;
        }

        List<List<Object>>  PrepDataForExport ( String tableName){

            Cursor c = MyApp.db.query(tableName,null,null,null,null,null,null);
            List<List<Object>> allValues = new ArrayList<List<Object>>();

            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    List<Object> row = new ArrayList<Object>();
                    for (int col = 0; col < c.getColumnCount(); col++) {
                        if(tableName.equals(DBHelper.FLIGHT_TABLE_NAME) && col == 1 ){  // special case for date in flight table
                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                            row.add(df.format(c.getLong(col)));
                        } else {
                            row.add(c.getString(col));
                        }
                    }
                    allValues.add(row);
                } while (c.moveToNext());
            }
            return allValues;
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() <= 0) {   //  first row is just the header
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText.setText(TextUtils.join("\n", output));


            }
            finish();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            SheetsComGetData.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                    Log.e ("RRR", "API Error" + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
}
package com.example.sid.phonetracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.app.LoaderManager;

import java.util.ArrayList;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks < Cursor > , LocationListener {


    private SimpleCursorAdapter dataAdapter;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String lat;
    String provider;
    public String latitude, longitude;
    public ArrayList < String > valid_no = new ArrayList < > ();

    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }
    private void requestSmsPermission1() {
        String permission = Manifest.permission.READ_PHONE_STATE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 4);
        }
    }
    private void requestSmsPermission2() {
        String permission = Manifest.permission.SEND_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 3);
        }
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    protected boolean gps_enabled, network_enabled;
    public boolean Exists(String no) {
        StringBuilder num = new StringBuilder(no);
        no = num.substring(3, num.length());


        if (valid_no.contains(no)) {
            return true;
        } else {
            return false;
        }

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayListView();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 1); // 1 is a integer which will return the result in onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_PHONE_STATE
                    },
                    2);
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_SMS
                    },
                    3);
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.SEND_SMS
                    },
                    4);

            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        requestSmsPermission();
        requestSmsPermission1();
        requestSmsPermission2();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);





        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String sender) {


                if (Exists(sender)) {
                    String phoneNumber = sender;
                    String smsBody = "latitude:" + latitude + "longitude:" + longitude;
                    // Get the default instance of SmsManager
                    SmsManager smsManager = SmsManager.getDefault();
                    // Send a text based SMS
                    smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
                    // If your OTP is a number that can have maximum 8 digits.
                }
                /* Pattern pattern = Pattern.compile(OTP_REGEX);
                 Matcher matcher = pattern.matcher(messageText);
                 String otp = "No OTP Present";

                 while (matcher.find())
                 {
                     otp = matcher.group();
                 }

                 otpTextView.setText(otp);*/
            }
        });


        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // starts a new Intent to add a Country
                Intent personEdit = new Intent(getBaseContext(), PersonEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "add");
                personEdit.putExtras(bundle);
                startActivity(personEdit);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);


    }

    private void displayListView() {

        // The desired columns to be bound
        String[] columns = new String[] {

                Person.COL_FIRSTNAME,
                Person.COL_NO


        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.name,
                R.id.number,
        };

        // create an adapter from the SimpleCursorAdapter
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.person_info,
                null,
                columns,
                to,
                0);

        // get reference to the ListView
        ListView listView = (ListView) findViewById(R.id.personList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        //Ensures a loader is initialized and active.
        getLoaderManager().initLoader(0, null, this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // display the selected country
                String col_id =
                        cursor.getString(cursor.getColumnIndexOrThrow(Person.COL_ID));
                Toast.makeText(getApplicationContext(),
                        col_id, Toast.LENGTH_SHORT).show();

                // starts a new Intent to update/delete a Country
                // pass in row Id to create the Content URI for a single row
                Intent personEdit = new Intent(getBaseContext(), PersonEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "update");
                bundle.putString("rowId", col_id);
                personEdit.putExtras(bundle);
                startActivity(personEdit);

            }
        });

    }

    @Override
    public Loader < Cursor > onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Person.COL_ID,
                Person.COL_FIRSTNAME,
                Person.COL_NO
        };
        CursorLoader cursorLoader = new CursorLoader(this,
                MyContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader < Cursor > loader, Cursor cursor) {


        dataAdapter.swapCursor(cursor);
        valid_no = new ArrayList < > ();
        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {
            valid_no.add(cursor.getString(2));
            cursor.moveToNext();

        }


    }

    @Override
    public void onLoaderReset(Loader < Cursor > loader) {

        dataAdapter.swapCursor(null);

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude() + "";
        longitude = location.getLongitude() + "";
        Log.d("latitude", latitude + "");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude", "disable");

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude", "enable");

    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude", "status");

    }
}
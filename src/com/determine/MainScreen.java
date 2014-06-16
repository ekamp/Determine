package com.determine;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class  MainScreen extends Activity implements LocationListener, SensorEventListener{
	private double sum;
	private int count;
	
	private SensorManager sensor;
	
	private boolean driving;
	TextView currently;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);

		currently = (TextView) findViewById(R.id.currently);
		sum=0;
		
		final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
	        }else{
	            showGPSDisabledAlertToUser();
	        }
		
		
		
		//Will get location updates every 5 minutes or every mile 1600 meters (12,000 milliseconds)
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,120000,1600, this);

		final LocationListener listen = this;
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		sensor = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
	}
	
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
        .setCancelable(false)
        .setPositiveButton("Goto Settings Page To Enable GPS",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
	
	

	@Override
	public void onLocationChanged(Location location) {

		System.out.println(location.getLatitude());
		System.out.println(location.getLongitude());
		System.out.println("Speed :" + location.getSpeed());
		
		//Approx 25 mph
		if(location.getSpeed() >= 11.176)
		{
			currently.setText("Currently Driving");
			driving = true;
			//save battery
			sensor.unregisterListener(this);
		}
		else
		{
			driving  = false;
			sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	
	
	@Override
	public void onPause() {
	    super.onPause();  //Unreg the listener
	    sensor.unregisterListener(this);
	}

	@Override
	public void onResume() { //rereg the listener
	    super.onPause();  // Always call the superclass method first
	    sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
	}
	

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){	
			
			if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
	            return;
	        }
			
			double accel = Math.sqrt( Math.pow(event.values[0],2) + Math.pow(event.values[1],2) + Math.pow(event.values[2],2));
			sum+=accel;
			count++;
			
			//Reset the count every 300 seconds
			if(count == 300)
			{
				count = 1;
				sum = accel;
			}
			
			System.out.println("SUM " + (sum+1)/count);
			
			//Based on my study the threshold averaged at 10.32, might need to average a few to determine if walking
			if( ((( sum + 1 ) / count ) >= 10.32 ) && !driving)
			{
	            currently.setText("Currently Walking");
			}
			else if(!driving)
			{
				currently.setText("Currently your phone is on the table");
			}
			
		}
	}
}
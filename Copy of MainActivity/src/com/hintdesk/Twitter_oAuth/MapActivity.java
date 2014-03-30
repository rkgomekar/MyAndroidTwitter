package com.hintdesk.Twitter_oAuth;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements LocationListener{
	
	 GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		
		 LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		   lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

		   map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		map.clear();

		   MarkerOptions mp = new MarkerOptions();

		   mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

		   mp.title("my position");

		   map.addMarker(mp);

		   map.animateCamera(CameraUpdateFactory.newLatLngZoom(
		    new LatLng(location.getLatitude(), location.getLongitude()), 16));
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}

package com.augmented_reality.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.augmented_reality.R;
import com.augmented_reality.activity.AugmentedView;
import com.augmented_reality.data.ARData;
import com.augmented_reality.data.GooglePlacesDataSource;
import com.augmented_reality.data.LocalDataSource;
import com.augmented_reality.data.NetworkDataSource;
import com.augmented_reality.data.WikipediaDataSource;
import com.augmented_reality.ui.Marker;
import com.augmented_reality.ui.Radar;
import com.augmented_reality.ui.objects.PaintableBoxedText;
import com.augmented_reality.widget.VerticalTextView;


/**
 * This class extends the AugmentedReality and is designed to be an example on
 * how to extends the AugmentedReality class to show multiple data sources.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Demo extends AugmentedReality implements ActionBar.OnNavigationListener,GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener {
	private ActionBar actionBar;
	
    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;
    ; 
	
    // Navigation adapter
    private TitleNavigationAdapter adapter;
    private static final String TAG = "Demo";
    private static final String locale = Locale.getDefault().getLanguage();
    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);
    private static final Map<String, NetworkDataSource> sources = new ConcurrentHashMap<String, NetworkDataSource>();
    private LocationClient locationclient=null;
    private static Toast myToast = null;
    private static VerticalTextView text = null;
    private static boolean flag=false;
    public static AugmentedReality augmentedreal = null;
    public static AugmentedView real=null ;
    /**
     * {@inheritDoc}
     */
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(locationclient!=null)
			   locationclient.disconnect();
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
     //  super.flag = getIntent().getExtras().getBoolean("iconflag");
      // super.flag=true;
      // Log.e("setting demo flag",Boolean.toString(super.flag));
        // Create toast
        myToast = new Toast(getApplicationContext());
        myToast.setGravity(Gravity.CENTER, 0, 0);
        // Creating our custom text view, and setting text/rotation
        text = new VerticalTextView(getApplicationContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(params);
        text.setBackgroundResource(android.R.drawable.toast_frame);
        text.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Small);
        text.setShadowLayer(2.75f, 0f, 0f, Color.parseColor("#BB000000"));
        myToast.setView(text);
        // Setting duration and displaying the toast
        myToast.setDuration(Toast.LENGTH_SHORT);
        augmentedreal = new AugmentedReality();
        Context context=this;
     // real = new AugmentedView(context);
      Marker.setContext(getApplicationContext());
     Radar.setContext(getApplicationContext());
     PaintableBoxedText.setContext(getApplicationContext());
     
        // Local
      //  LocalDataSource localData = new LocalDataSource(this.getResources());
        //ARData.addMarkers(localData.getMarkers());

      //  NetworkDataSource wikipedia = new WikipediaDataSource(this.getResources());
       // sources.put("wiki", wikipedia);
        NetworkDataSource googlePlaces_all = new GooglePlacesDataSource(this.getResources(),"airport|amusement_park|aquarium|art_gallery|bus_station|campground|car_rental|city_hall|embassy|establishment|hindu_temple|local_governemnt_office|mosque|museum|night_club|park|place_of_worship|police|post_office|stadium|spa|subway_station|synagogue|taxi_stand|train_station|travel_agency|University|zoo");
        sources.put("googlePlaces", googlePlaces_all);
        //actionBar = getActionBar();
        
        // Hide the action bar title
      //  actionBar.setDisplayShowTitleEnabled(false);
 
        // Enabling Spinner dropdown navigation
       // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
         
        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("All", R.drawable.ic_launcher));
        navSpinner.add(new SpinnerNavItem("Airport", R.drawable.ic_launcher));
        navSpinner.add(new SpinnerNavItem("Banks", R.drawable.ic_launcher));
        navSpinner.add(new SpinnerNavItem("Restaurants", R.drawable.ic_launcher));     
        navSpinner.add(new SpinnerNavItem("Places of Worship", R.drawable.ic_launcher)); 
        navSpinner.add(new SpinnerNavItem("Educational Institutes", R.drawable.ic_launcher)); 
        // title drop down adapter
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);
 
        // assigning the spinner navigation     
       // actionBar.setListNavigationCallbacks(adapter, this);
        int resp =GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
  	   
	      if(resp == ConnectionResult.SUCCESS){

	       locationclient = new LocationClient(this,this,this);
	       locationclient.connect();
	      }

	      else{
	  
	       Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
	
	      }
	      imgbtn1.setOnClickListener(new View.OnClickListener(){

	 			@Override
	 			public void onClick(View v) {
	 				// TODO Auto-generated method stub
	 				
	 				NetworkDataSource googlePlaces_air = new GooglePlacesDataSource(v.getResources(),"airport");
			        sources.put("airport", googlePlaces_air);
			        flag=true;
			        //ARData.delMarkers(markers);
			        updateDataOnZoom();
			        Toast.makeText(getApplicationContext(), "Airports",
	  						   Toast.LENGTH_LONG).show();
			        //flag=false;
	 			}}) ;

	         imgbtn2.setOnClickListener(new View.OnClickListener(){

	  			@Override
	  			public void onClick(View v) {
	  				// TODO Auto-generated method stub
	  				NetworkDataSource googlePlaces_banks = new GooglePlacesDataSource(v.getResources(),"bank");
			        sources.put("bank", googlePlaces_banks);
			        flag=true;
			        updateDataOnZoom();
	  				Toast.makeText(getApplicationContext(), "Banks",
	  						   Toast.LENGTH_LONG).show();
	  			}}) ;
	        
	        
	         imgbtn3.setOnClickListener(new View.OnClickListener(){

	  			@Override
	  			public void onClick(View v) {
	  				// TODO Auto-generated method stub
	  				NetworkDataSource googlePlaces_res = new GooglePlacesDataSource(v.getResources(),"restaurant");
			        sources.put("rest", googlePlaces_res);
			        flag=true;
			        updateDataOnZoom();
			        //flag=false;
	  				Toast.makeText(getApplicationContext(), "Restaurants",
	  						   Toast.LENGTH_LONG).show();
	  			}}) ;
	         imgbtn4.setOnClickListener(new View.OnClickListener(){

	  			@Override
	  			public void onClick(View v) {
	  				// TODO Auto-generated method stub
	  				NetworkDataSource googlePlaces_worship = new GooglePlacesDataSource(v.getResources(),"hindu_temple|mosque|place_of_worship|church|synagogue");
			        sources.put("worship", googlePlaces_worship);
			        flag=true;
			        updateDataOnZoom();
	  				Toast.makeText(getApplicationContext(), "Places of Worship",
	  						   Toast.LENGTH_LONG).show();
	  			}}) ;
	        
	         imgbtn5.setOnClickListener(new View.OnClickListener(){

	  			@Override
	  			public void onClick(View v) {
	  				// TODO Auto-generated method stub
	  				NetworkDataSource googlePlaces_edu = new GooglePlacesDataSource(v.getResources(),"university|school");
			        sources.put("educational", googlePlaces_edu);
			        flag=true;
			        updateDataOnZoom();
	  				Toast.makeText(getApplicationContext(), "Eduactional Institutions",
	  						   Toast.LENGTH_LONG).show();
	  			}}) ;
	        
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

       // Location last = ARData.getCurrentLocation();
    	Location loc=new Location("bla");
    	 LocationManager locationManager = (LocationManager) 
                 getSystemService(LOCATION_SERVICE);
         locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
         loc = locationManager
                 .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	try{
    		
    		if (loc != null ) {
               
                ARData.setCurrentLocation(loc);
                updateData(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
    		}
    		
    		else if(locationclient!=null && locationclient.isConnected())
    		{	loc = locationclient.getLastLocation();
    		ARData.setCurrentLocation(loc);
            updateData(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());}
    		else{
    			 // Toast.makeText(this, "Please turn on your location in Location Settings " , Toast.LENGTH_LONG).show();
    		}
    	}
    	catch(Exception ex2){
    		/*loc = ARData.getCurrentLocation();
    		ARData.setCurrentLocation(loc);
            updateData(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
            //Toast.makeText(this, "Using hardfix values " , Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Augmented Reality App");
            builder.setMessage("Your Location should be enabled for this to work");
         // Add the buttons
            builder.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
            	
                       public void onClick(DialogInterface dialog, int id) {
                           // User clicked OK button
                    	// Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                    	 startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                    	
                   }});
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                    //	 Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                    	   finish();
                       }
                   }
            );
            builder.create().show();
    	
    	*/
    		Toast.makeText(this, "For the app to work properly please turn on your Location in Location Settings " , Toast.LENGTH_LONG).show();
    	
    	
    	
    	
    	
    	
    	}
    	
        
      //  String lat;String lon;
      //  lat=Double.toString(loc.getLatitude());
      //  lon=Double.toString(loc.getLongitude());
     //   Toast.makeText(this, "latitiude :"+lat+"  longitude :"+lon, Toast.LENGTH_LONG).show();
		//Toast.makeText(this, "demo connected",
        //        Toast.LENGTH_SHORT).show();
    	 //Log.v(TAG, "location error");
    }
    public void draw(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected() item=" + item);
        switch (item.getItemId()) {
            case R.id.showRadar:
                showRadar = !showRadar;
                item.setTitle(((showRadar) ? "Hide" : "Show") + " Radar");
                break;
            case R.id.showZoomBar:
                showZoomBar = !showZoomBar;
                item.setTitle(((showZoomBar) ? "Hide" : "Show") + " Zoom Bar");
                zoomLayout.setVisibility((showZoomBar) ? LinearLayout.VISIBLE : LinearLayout.GONE);
                break;
            case R.id.exit:
                finish();
                break;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        updateData(location.getLatitude(), location.getLongitude(), location.getAltitude());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void markerTouched(Marker marker,int ans) {
    	if(marker!=null){
    //real.setmarker(marker);
    		}
    	if(ans==1){
    	augmentedreal.useCollisionDetection=true;
    	Log.e("ans=1","yes");
    	}
    	
    	
    	//Uri uri = Uri.parse("https://www.google.com.pk");
		 //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		 //startActivity(intent);
       // text.setText(marker.getName()+" - "+marker.getvic()+" - "+marker.getplace());
    	//marker.draw(canvas);
    	else if(ans==2){
    		//String finalURL = "https://www.google.com/search#q=+site:plus.google.com+intitle:about+intitle:"+marker.getName()+"+"+marker.getvic()+"&btnI=I"; 
    	//	String finalURL="http://www.google.com/search#q=site:plus.google.com+intitle:about+intitle:"+marker.getName()+"+"+marker.getvic()+"&btnI=I'm+Feeling+Lucky";
    	//	Log.e("finalurl",finalURL);
    		//AlertDialog.Builder alert = new AlertDialog.Builder(this); 
    		//alert.setTitle("Title here");

    		/*WebView wv = new WebView(this);
    		wv.getSettings().setJavaScriptEnabled(true);
    		wv.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
    		//wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    		//wv.loadUrl("http://www.google.com/search?q=azure+ray+site:en.wikipedia.org&btnI=I%27m+Feeling+Lucky");
    		wv.loadUrl(finalURL );
    		wv.setWebChromeClient(new WebChromeClient());
    		wv.setWebViewClient(new WebViewClient() {
    		    @Override
    		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		        view.loadUrl(url);

    		        return false;
    		    }
    		}
    		);

    		alert.setView(wv);
    		alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
    		    @Override
    		    public void onClick(DialogInterface dialog, int id) {
    		        dialog.dismiss();
    		    }
    		});
    		alert.show();*/
     /*String finalURL = "https://www.google.com/search#q=+site:plus.google.com+intitle:about+intitle:"+marker.getName()+"+"+marker.getvic()+"&btnI=I"; 
     Uri uri = Uri.parse(finalURL);
		 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		 startActivity(intent);*/
     //  myToast.show();
       // augmentedView.
    		 Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
             String reference = marker.getplace();
             intent.putExtra("reference", reference);

             // Starting the Place Details Activity
             startActivity(intent);
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateDataOnZoom() {
        super.updateDataOnZoom();
        Location last = ARData.getCurrentLocation();
        updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());
    }

    private void updateData(final double lat, final double lon, final double alt) {
        try {
            exeService.execute(new Runnable() {
                @Override
                public void run() {
                    for (NetworkDataSource source : sources.values())
                        download(source, lat, lon, alt);
                }
            });
        } catch (RejectedExecutionException rej) {
            Log.w(TAG, "Not running new download Runnable, queue is full.");
        } catch (Exception e) {
            Log.e(TAG, "Exception running download Runnable.", e);
        }
    }

    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
        if (source == null) return false;
        String url = null;
        try {
            url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);
        } catch (NullPointerException e) {
            return false;
        }

        List<Marker> markers = null;
        try {
            markers = source.parse(url);
        } catch (NullPointerException e) {
            return false;
        }
        if(flag){
        ARData.delMarkers(markers);
        Log.w(TAG, "Douche123 here deleting markers just for the heck of it.");
        flag=false;
        }
        ARData.addMarkers(markers);
      
        return true;
    }

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		  switch(itemPosition) {
		    case 0:
		    	NetworkDataSource googlePlaces_all = new GooglePlacesDataSource(this.getResources(),"airport|amusement_park|aquarium|art_gallery|bus_station|campground|car_rental|city_hall|embassy|establishment|hindu_temple|local_governemnt_office|mosque|museum|night_club|park|place_of_worship|police|post_office|stadium|spa|subway_station|synagogue|taxi_stand|train_station|travel_agency|university|zoo");
		        sources.put("googlePlaces", googlePlaces_all);
		    	flag=true;
		    	 updateDataOnZoom();
		        break;
		    case 1:
		    	
		    	NetworkDataSource googlePlaces_air = new GooglePlacesDataSource(this.getResources(),"airport");
		        sources.put("airport", googlePlaces_air);
		        flag=true;
		        //ARData.delMarkers(markers);
		        updateDataOnZoom();
		        //flag=false;
		        break;
		    case 2:
		    	NetworkDataSource googlePlaces_banks = new GooglePlacesDataSource(this.getResources(),"bank");
		        sources.put("bank", googlePlaces_banks);
		        flag=true;
		        updateDataOnZoom();
		        //flag=false;
		    	break;
		    case 3:
		    	//restaurant
		    	NetworkDataSource googlePlaces_res = new GooglePlacesDataSource(this.getResources(),"restaurant");
		        sources.put("rest", googlePlaces_res);
		        flag=true;
		        updateDataOnZoom();
		        //flag=false;
		    	break;
		    case 4:
		    	//worship
		    	NetworkDataSource googlePlaces_worship = new GooglePlacesDataSource(this.getResources(),"hindu_temple|mosque|place_of_worship|church|synagogue");
		        sources.put("worship", googlePlaces_worship);
		        flag=true;
		        updateDataOnZoom();
		        //flag=false;
		    	break;
		    case 5:
		    	//education
		    	NetworkDataSource googlePlaces_edu = new GooglePlacesDataSource(this.getResources(),"university|school");
		        sources.put("educational", googlePlaces_edu);
		        flag=true;
		        updateDataOnZoom();
		        //flag=false;
		    	break;
		    }
		   
		return false;
	}
}

package com.augmented_reality.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.augmented_reality.R;
import com.augmented_reality.ui.IconMarker;
import com.augmented_reality.ui.Marker;

/**
 * This class extends DataSource to fetch data from Google Places.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class GooglePlacesDataSource extends NetworkDataSource {

	private static final String URL = "https://maps.googleapis.com/maps/api/place/search/json?";
	private static String TYPES = "airport|amusement_park|aquarium|art_gallery|bus_station|campground|car_rental|city_hall|embassy|establishment|hindu_temple|local_governemnt_office|mosque|museum|night_club|park|place_of_worship|police|post_office|stadium|spa|subway_station|synagogue|taxi_stand|train_station|travel_agency|University|zoo";
	private String user=null;
	private static String key = null;
	private static Bitmap icon = null;
private Resources r;
	public GooglePlacesDataSource(Resources res,String TYPE) { //added filter TYPE
		if (res == null) throw new NullPointerException();

		key = res.getString(R.string.google_places_api_key);
	createIcon(res);
		TYPES=TYPE;//MINE
	}

	protected void createIcon(Resources res) {
		if (res == null) throw new NullPointerException();

		icon = BitmapFactory.decodeResource(res, R.drawable.f);
		r=res;
	
	}
private void changeicon(String type){
	if (r == null) Log.d("places","error");
	
	int resID = r.getIdentifier(type, "drawable", "com.augmented_reality");
	if(resID==0){
		icon = BitmapFactory.decodeResource(r, R.drawable.f);
	}
	else
		icon = BitmapFactory.decodeResource(r, resID);
	
}
	@Override
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale) {
		try {
			Log.e("url",URL + "location="+lat+","+lon+"&radius="+(radius*1000.0f)+"&types="+TYPES+"&sensor=true&key="+key);
			return URL + "location="+lat+","+lon+"&radius="+(radius*1000.0f)+"&types="+TYPES+"&sensor=true&key="+key;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public List<Marker> parse(String URL) {
		if (URL == null) throw new NullPointerException();

		InputStream stream = null;
		stream = getHttpGETInputStream(URL);
		if (stream == null) throw new NullPointerException();

		String string = null;
		string = getHttpInputString(stream);
		if (string == null) throw new NullPointerException();

		JSONObject json = null;
		try {
			json = new JSONObject(string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (json == null) throw new NullPointerException();

		return parse(json);
	}

	@Override
	public List<Marker> parse(JSONObject root) {
		if (root == null) throw new NullPointerException();

		JSONObject jo = null;
		JSONArray dataArray = null;
		List<Marker> markers = new ArrayList<Marker>();

		try {
			if (root.has("results")) dataArray = root.getJSONArray("results");
			if (dataArray == null) return markers;
			int top = Math.min(MAX, dataArray.length());
			top=20;
			 //Log.w("TOP VALUE", String.valueOf(top));
			for (int i = 0; i < top; i++) {
				jo = dataArray.getJSONObject(i);
				Marker ma = processJSONObject(jo);
				if (ma != null) markers.add(ma);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return markers;
	}

	private Marker processJSONObject(JSONObject jo) {
		if (jo == null) throw new NullPointerException();

		if (!jo.has("geometry")) throw new NullPointerException();

		Marker ma = null;
		try {
			Double lat = null, lon = null;

			if (!jo.isNull("geometry")) {
				JSONObject geo = jo.getJSONObject("geometry");
				JSONObject coordinates = geo.getJSONObject("location");
				lat = Double.parseDouble(coordinates.getString("lat"));
				lon = Double.parseDouble(coordinates.getString("lng"));
			}
			if (lat != null) {
				 user = jo.getString("name");
				String type_string=jo.getString("types");
				String vic=jo.getString("vicinity");
				String place=jo.getString("place_id");
				String[] str_array = type_string.split(",|\\]");
				String type = str_array[0]; 
				
				type=type.substring(1);
				type = type.replace("\"", "");
				vic = vic.replace("\"", "");
				place = place.replace("\"", "");
				Log.d("types",type);
				//removed string
				//+ ": " + jo.getString("name")
				
				changeicon(type);
				ma = new IconMarker(user , lat, lon, 0, Color.RED, icon,vic,place);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ma;
	}
	public String getname(){
		return this.user;
		
	}
}
package com.augmented_reality.activity;

import java.util.ArrayList;

import com.augmented_reality.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class main extends Activity{
boolean iconflag;
boolean fromsettings=false;
boolean isFirstTime = true;
SharedPreferences appPref;
AlertDialog dialog; 
final CharSequence[] items = {" Icons+Panel on the bottom "," Icons+markers on screen "};
final ArrayList seletedItems=new ArrayList(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		appPref = getSharedPreferences("isFirstTime", 0);       
        isFirstTime = appPref.getBoolean("isFirstTime", true);
        iconflag = appPref.getBoolean("iconflag", false);
              if (isFirstTime)
        
            {Toast.makeText(getApplicationContext(), "This dialog will only appear once.Use settings to access the dialog again",
            		   Toast.LENGTH_LONG).show();
            	  AlertDialog.Builder builder = new AlertDialog.Builder(this);

                  builder.setTitle("Select The Desired Layout");
                  builder.setMultiChoiceItems(items, null,
                  new DialogInterface.OnMultiChoiceClickListener() {
                   // indexSelected contains the index of item (of which check box checked)
                   @Override
                   public void onClick(DialogInterface dialog, int indexSelected,
                           boolean isChecked) {
                	   final AlertDialog alertDialog = (AlertDialog) dialog;
                 	  final ListView alertDialogList = ((AlertDialog) dialog).getListView();
                       if (isChecked) {
                           // If the user checked the item, add it to the selected items
                           // write your code when user checked the check box	                    	   
                    	   if(indexSelected==0){
                    		  // Canvas c=real.can;
                    		  
                    		   //final Marker m=new Marker(true);
                    		   iconflag=true;
                    		//   alertDialogList.getChildAt(1).setEnabled(false);
                    		
                    		
                    	   }
                    	   else if(indexSelected==1){
                    		 //  final Marker m=new Marker(true);
                    		   iconflag=false;
                    		//   alertDialogList.getChildAt(0).setEnabled(false);
                    	   }
                           seletedItems.add(indexSelected);
                       } else if (seletedItems.contains(indexSelected)) {
                           // Else, if the item is already in the array, remove it 
                           // write your code when user checked the check box 
                           seletedItems.remove(Integer.valueOf(indexSelected));
                       }
                     
                   }
               })
                // Set the action buttons
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       //  Your code when user clicked on OK
                       //  You can write the code  to save the selected item here
                	   if(fromsettings)
                	   {//Toast.makeText(getApplicationContext(), "Restart the app for the changes to take effect",
                    		//   Toast.LENGTH_LONG).show();
                		   }
                      
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                      //  Your code when user clicked on Cancel
                     
                   }
               });
         
                  dialog = builder.create();//AlertDialog dialog; create like this outside onClick
                  dialog.show();
            }
              SharedPreferences.Editor editor = appPref.edit();
              editor.putBoolean("isFirstTime", false);
              editor.putBoolean("iconflag", iconflag);
              editor.commit();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor = appPref.edit();
        editor.putBoolean("isFirstTime", false);
        editor.putBoolean("iconflag", iconflag);
        editor.commit();
		super.onStop();
	}
	public boolean getflag(){
		 return appPref.getBoolean("iconflag", true);
		
	}
public void opendemo(View v){
	Intent intent = new Intent(this, Demo.class);
	intent.putExtra("iconflag", iconflag);
	startActivity(intent);	
	
}
public void open_settings(View v){
	  AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setTitle("Select The Desired Layout");
      builder.setMultiChoiceItems(items, null,
      new DialogInterface.OnMultiChoiceClickListener() {
       // indexSelected contains the index of item (of which check box checked)
       @Override
       
       public void onClick(DialogInterface dialog, int indexSelected,
               boolean isChecked) {
    	   final AlertDialog alertDialog = (AlertDialog) dialog;
     	  final ListView alertDialogList = ((AlertDialog) dialog).getListView();
           if (isChecked) {
               // If the user checked the item, add it to the selected items
               // write your code when user checked the check box	       
        	
        	   if(indexSelected==0){
        		  // Canvas c=real.can;
        		  
        		   //final Marker m=new Marker(true);
        		   iconflag=true;
        		  //
        		
        		
        	   }
        	   else if(indexSelected==1){
        		 //  final Marker m=new Marker(true);
        		   iconflag=false;
        		//   alertDialogList.getChildAt(0).setEnabled(false);
        	   }
               seletedItems.add(indexSelected);
           } else if (seletedItems.contains(indexSelected)) {
               // Else, if the item is already in the array, remove it 
               // write your code when user checked the check box 
               seletedItems.remove(Integer.valueOf(indexSelected));
           }
         
       }
   })
    // Set the action buttons
   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
       @Override
       public void onClick(DialogInterface dialog, int id) {
           //  Your code when user clicked on OK
           //  You can write the code  to save the selected item here
    	   if(fromsettings)
    	   {//Toast.makeText(getApplicationContext(), "Restart the app for the changes to take effect",
        		   //Toast.LENGTH_LONG).show();
    		   }
          
       }
   })
   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
       @Override
       public void onClick(DialogInterface dialog, int id) {
          //  Your code when user clicked on Cancel
         
       }
   });

      dialog = builder.create();//AlertDialog dialog; create like this outside onClick
      dialog.show();
	fromsettings=true;
}
@Override
public void onBackPressed() {
	 new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("AR compass")
	        .setMessage("Are you sure you want to exit?")
	        .setPositiveButton("No", null)
	      .setNegativeButton("Yes", new DialogInterface.OnClickListener()
	       {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		          finish();    
		        }

		        })
	      
	      
	      
	      .create().show();
	}

}

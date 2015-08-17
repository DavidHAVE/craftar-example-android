package com.craftar.craftarexamples;

import org.json.JSONObject;

import android.util.Log;

import com.craftar.CraftARItemAR;
import com.craftar.CraftARSDKException;

public class MyARItem extends CraftARItemAR {
		
	public MyARItem(JSONObject object) throws CraftARSDKException {
		super(object);
	}

	
	protected void trackingStarted() {
		super.trackingStarted();
		Log.d("Catchoom Example", "Tracking started for item: " + this.getItemName());
	}
	
	protected void trackingLost() {
		super.trackingLost();
		Log.d("Catchoom Example", "Tracking lost for item: " + this.getItemName());
		
		// Adjust depth depending on the aspect of the video you want to fit to the screen
		this.setItemTranslation(new float[] {0.0f, 0.0f,420.0f});

		// Set rotation
		// Note: you can do this accordingly to the screen orientation and change depending on how you want the app to handle rotations.
		this.setItemRotation(new float[] {
				0.0f, -1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f
		});
		
	}
	
}

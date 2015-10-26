// com.craftar.craftarexamples is free software. You may use it under the MIT license, which is copied
// below and available at http://opensource.org/licenses/MIT
//
// Copyright (c) 2014 Catchoom Technologies S.L.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
// FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.catchoom.craftarsdkexamples;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.craftar.CLog;
import com.craftar.CraftARActivity;
import com.craftar.CraftARContent;
import com.craftar.CraftARContentImageButton;
import com.craftar.CraftARError;
import com.craftar.CraftARItem;
import com.craftar.CraftARItemAR;
import com.craftar.CraftAROnDeviceCollection;
import com.craftar.CraftAROnDeviceCollectionManager;
import com.craftar.CraftARSDK;
import com.craftar.CraftARSDKException;
import com.craftar.CraftARTouchEventInterface;
import com.craftar.CraftARTouchEventInterface.OnContentClickListener;
import com.craftar.CraftARTouchEventInterface.OnTouchEventListener;
import com.craftar.CraftARTracking;

import java.io.File;


public class OnDeviceARActivity extends CraftARActivity implements CraftAROnDeviceCollectionManager.AddCollectionListener, CraftARTouchEventInterface.OnContentClickListener, CraftARTouchEventInterface.OnTouchEventListener {

	private final String TAG = "OnDeviceARActivity";
	private final static String COLLECTION_TOKEN="d523d576df0c491e";


	CraftARTracking mTracking;
	CraftARSDK mCraftARSDK;
    CraftAROnDeviceCollectionManager mCollectionManager;
    CraftAROnDeviceCollection mCollection;
    private Toast mToast;
    private View mScanningLayout;

    @Override
	public void onCreate(Bundle savedInstanceState) {
    	CLog.showDebugLogs = true;
    	CLog.showVerboseLogs = true;
		super.onCreate(savedInstanceState);
	}
		
	@Override
	public void onPostCreate() {
		
		View mainLayout= getLayoutInflater().inflate(R.layout.activity_ar_programmatically_ar_from_craftar, null);
		setContentView(mainLayout);

        mScanningLayout = findViewById(R.id.layout_scanning);
        mScanningLayout.setVisibility(View.GONE);

        /**
         * Get the CraftAR SDK instance and start capturing
         */
		mCraftARSDK = CraftARSDK.Instance();
		mCraftARSDK.startCapture(this);
		mCraftARSDK.setOnContentClickListener(new OnContentClickListener() {
			
			@Override
			public void onClick(CraftARContent content) {
				Log.d(TAG,"onClick in "+content);				
			}
		});

		mCraftARSDK.setOnContentTouchListener(new OnTouchEventListener() {
			
			@Override
			public void onTouchUp(CraftARContent content) {
				Log.d(TAG,"OnTouchUp in "+content);				
			}
			
			@Override
			public void onTouchOut(CraftARContent content) {
				Log.d(TAG,"OnTouchOut in "+content);				
			}
			
			@Override
			public void onTouchIn(CraftARContent content) {
				Log.d(TAG,"OnTouchIn in "+content);				
			}
			
			@Override
			public void onTouchDown(CraftARContent content) {
				Log.d(TAG,"OnTouchDown in "+content);				
			}
		}); 

        /**
         * Get the instance of the On-device Collection Manager.
         * This class manages on-device AR collections allowing to add them to the
         * device from a bundle zip file.
         */
        mCollectionManager = CraftAROnDeviceCollectionManager.init(this);

        /**
         * Get the instance of the Tracking
         */
		mTracking = CraftARTracking.Instance(this);
		
	}


    @Override
    public void onPreviewStarted(int width,int height){
        Log.d(TAG, "OnPreviewStarted in MainActivity");

        /**
         * The on-device collection may already be added to the device (we just add it once)
         * we can use the token to retrieve it.
         */
        mCollection =  mCollectionManager.get(COLLECTION_TOKEN);

        if(mCollection != null){
            /**
             * If the on-device collection is already in the device, we will add the collection items
             * to the tracking and start the AR experience.
             */
            loadCollection();
        }else{
            /**
             * If not, we get the path for the bundle and add the collection to the device first.
             * The addCollection  method receives an AddCollectionListener instance that will receive
             * the callbacks when the collection is ready.
             */
            String bundlePath = getApplicationContext().getExternalFilesDir(null) + "/arbundle.zip";
            File bundleFile = new File(bundlePath);
            mCollectionManager.addCollection(bundleFile, this);
        }
    }

    @Override
    public void collectionAdded(CraftAROnDeviceCollection collection) {
        showToast("Collection " + collection.getName() + " added!", Toast.LENGTH_SHORT);

        /**
         * The collection is on the device and ready to use!
         * Keep a reference to the collection and add its items to the
         * tracking and start the AR experience.
         */
        mCollection = collection;
        loadCollection();
    }

    @Override
    public void addCollectionFailed(CraftARError craftARError) {
        Log.e(TAG, "Error adding collection: " + craftARError.getErrorMessage());

    }

    @Override
    public void addCollectionProgress(float v) {
        /**
         * For large on-device collections, the add process can take some time. Here we provide an
         * estimate of the percentage of completeness of this operation.
         */
    }

    private void loadCollection() {
        // Get all item UUIDs in the collection
        for(String itemUUID: mCollection.listItems()){
            // Get the item and check that it is an AR item
            CraftARItem item = mCollection.getItem(itemUUID);
            if(item.isAR()){
                CraftARItemAR itemAR = (CraftARItemAR)item;
                try {

                    /**
                     * We can receive callbacks from touch events on AR contents.
                     */
                    for(CraftARContent content:itemAR.getContents()){
                        boolean isButton = content instanceof CraftARContentImageButton;
                        if(!isButton){
                            // for clicks
                            content.setContentClickListener(this);
                            // and touch (up, down, in, out) events
                            content.setContentTouchEventListener(this);
                        }
                    }

                    // Add the item to the tracking
                    Log.d(TAG, "Adding item "+item.getItemName()+" for tracking");
                    mTracking.addItem((CraftARItemAR)item);
                } catch (CraftARSDKException e) {
                    showToast(e.getMessage(),Toast.LENGTH_SHORT);
                    e.printStackTrace();
                }
            }
        }
        // Start tracking this collection.
        mTracking.startTracking();
    }

    @Override
    public void onClick(CraftARContent craftARContent) {
        showToast("Content clicked!", Toast.LENGTH_SHORT);
    }

    @Override
    public void onTouchIn(CraftARContent craftARContent) {
        showToast("Content onTouchIn!", Toast.LENGTH_SHORT);
    }

    @Override
    public void onTouchOut(CraftARContent craftARContent) {
        showToast("Content onTouchOut!", Toast.LENGTH_SHORT);
    }

    @Override
    public void onTouchDown(CraftARContent craftARContent) {
        showToast("Content onTouchDown!", Toast.LENGTH_SHORT);
    }

    @Override
    public void onTouchUp(CraftARContent craftARContent) {
        showToast("Content onTouchUp!", Toast.LENGTH_SHORT);
    }

    private void showToast(String toastText, int toastDuration) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), toastText,	toastDuration);
        mToast.show();

    }

    @Override
    public void finish() {
        /**
         * Stop Tracking and clean the AR scene
         */
        mTracking.stopTracking();
        mTracking.removeAllItems();
        super.finish();
    }
}

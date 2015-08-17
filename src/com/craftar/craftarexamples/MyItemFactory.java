package com.craftar.craftarexamples;

import org.json.JSONObject;

import com.craftar.CraftARItem;
import com.craftar.CraftARItemFactory;
import com.craftar.CraftARSDKException;

public class MyItemFactory extends CraftARItemFactory {
		
	public MyItemFactory() {
	}

	public CraftARItem itemFromJSONObject(JSONObject object) throws CraftARSDKException {
		int itemType = CraftARItem.getItemTypeFromJSONObject(object);
		switch (itemType) {
		case CraftARItem.ITEM_TYPE_RECOGNITION_ONLY:
			return new CraftARItem(object);
		case CraftARItem.ITEM_TYPE_AR:
			return new MyARItem(object);
		default:
			return null;
		}
	}

}
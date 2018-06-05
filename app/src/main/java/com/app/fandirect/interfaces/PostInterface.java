package com.app.fandirect.interfaces;

import com.app.fandirect.entities.GetServicesEnt;

/**
 * Created by saeedhyder on 5/11/2018.
 */

public interface PostInterface {

    void getCategoryData(GetServicesEnt categoryName);
    void getSelectedPlaceData(String placeName, String placeAddress, String placeLat, String placeLng);
}

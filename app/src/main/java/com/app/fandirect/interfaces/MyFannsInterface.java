package com.app.fandirect.interfaces;

import com.app.fandirect.entities.FannListEnt;
import com.app.fandirect.entities.GetMyFannsEnt;

/**
 * Created by saeedhyder on 5/15/2018.
 */

public interface MyFannsInterface {

    void getMyFanns(FannListEnt entity, int position);
}

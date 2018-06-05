package com.app.fandirect.interfaces;

import com.app.fandirect.entities.AllRequestEnt;

/**
 * Created by saeedhyder on 5/15/2018.
 */

public interface FannRequestInterface {

    void confirmRequest(AllRequestEnt entity, int position);
    void deleteRequest(AllRequestEnt entity, int position);
}

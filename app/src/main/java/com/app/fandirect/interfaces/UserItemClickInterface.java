package com.app.fandirect.interfaces;

import com.app.fandirect.entities.GetMyFannsEnt;

/**
 * Created by saeedhyder on 5/16/2018.
 */

public interface UserItemClickInterface {

    void addFriend(GetMyFannsEnt entity, int position,String key,String id);
    void profileClick(GetMyFannsEnt entity, int position, String id);
}

package com.app.fandirect.interfaces;

import com.app.fandirect.entities.Post;

/**
 * Created by saeedhyder on 6/21/2018.
 */

public interface ReportPostIntetface {

    void reportPost(Post entity, int position);
    void reportUser(Post entity, int position);
    void DeletePost(Post entity, int position);
}

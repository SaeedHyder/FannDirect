package com.app.fandirect.interfaces;

import com.app.fandirect.entities.Post;

/**
 * Created by saeedhyder on 5/15/2018.
 */

public interface PostClicksInterface {

    void like(Post entity, int position);
    void favorite(Post entity, int position);
    void share(Post entity, int position);
    void comment(Post entity, int position);
}

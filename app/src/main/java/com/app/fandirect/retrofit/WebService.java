package com.app.fandirect.retrofit;


import com.app.fandirect.entities.AllRequestEnt;
import com.app.fandirect.entities.CmsEnt;
import com.app.fandirect.entities.FanStatus;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.entities.GetProfileEnt;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.MessageThreadsEnt;
import com.app.fandirect.entities.NotificationEnt;
import com.app.fandirect.entities.Post;
import com.app.fandirect.entities.ResponseWrapper;
import com.app.fandirect.entities.ServiceHistoryEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.entities.commentEnt;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface WebService {

    @FormUrlEncoded
    @POST("login")
    Call<ResponseWrapper<UserEnt>> loginUser(@Field("email") String email,
                                             @Field("password") String password,
                                             @Field("role_id") String role_id,
                                             @Field("device_type") String device_type,
                                             @Field("device_token") String device_token
    );

    @FormUrlEncoded
    @POST("register")
    Call<ResponseWrapper<UserEnt>> registerUser(@Field("user_name") String user_name,
                                                @Field("email") String email,
                                                @Field("latitude") String latitude,
                                                @Field("longitude") String longitude,
                                                @Field("location") String location,
                                                @Field("password") String password,
                                                @Field("device_type") String device_type,
                                                @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("registersp")
    Call<ResponseWrapper<UserEnt>> registerSp(@Field("user_name") String user_name,
                                              @Field("email") String email,
                                              @Field("phone") String phone,
                                              @Field("password") String password,
                                              @Field("city") String city,
                                              @Field("state") String state,
                                              @Field("latitude") String latitude,
                                              @Field("longitude") String longitude,
                                              @Field("location") String location,
                                              @Field("service_ids") String service_ids);


    @FormUrlEncoded
    @POST("codeVerification")
    Call<ResponseWrapper<UserEnt>> VerifyCode(@Field("user_id") String user_id,
                                              @Field("verification_code") String verification_code);

    @FormUrlEncoded
    @POST("forgotPassword")
    Call<ResponseWrapper<UserEnt>> forgotPassword(@Field("email") String email);


    @Multipart
    @POST("updateProfile")
    Call<ResponseWrapper<UserEnt>> updateUserProfile(@Part("user_name") RequestBody user_name,
                                                     @Part("email") RequestBody email,
                                                     @Part("phone") RequestBody phone,
                                                     @Part("gender") RequestBody gender,
                                                     @Part("location") RequestBody location,
                                                     @Part("latitude") RequestBody latitude,
                                                     @Part("longitude") RequestBody longitude,
                                                     @Part("dob") RequestBody dob,
                                                     @Part("profession") RequestBody profession,
                                                     @Part("work_at") RequestBody work_at,
                                                     @Part("hobbies") RequestBody hobbies,
                                                     @Part("about") RequestBody about,
                                                     @Part MultipartBody.Part avatar

    );

    @Multipart
    @POST("updateSpProfile")
    Call<ResponseWrapper<UserEnt>> updateSpProfile(@Part("user_name") RequestBody user_name,
                                                   @Part("email") RequestBody email,
                                                   @Part("phone") RequestBody phone,
                                                   @Part("gender") RequestBody gender,
                                                   @Part("location") RequestBody location,
                                                   @Part("latitude") RequestBody latitude,
                                                   @Part("longitude") RequestBody longitude,
                                                   @Part("dob") RequestBody dob,
                                                   @Part("service_ids") RequestBody service_ids,
                                                   @Part("certificate_license") RequestBody certificate_license,
                                                   @Part("license_type") RequestBody license_type,
                                                   @Part("company_name") RequestBody company_name,
                                                   @Part("company_category") RequestBody company_category,
                                                   @Part("insurance_available") RequestBody insurance_available,
                                                   @Part("insurance_information") RequestBody insurance_information,
                                                   @Part("about") RequestBody about,
                                                   @Part MultipartBody.Part avatar

    );


    @FormUrlEncoded
    @POST("user/logout")
    Call<ResponseWrapper> logoutUser();


    @FormUrlEncoded
    @POST("changePassword")
    Call<ResponseWrapper<UserEnt>> ChangePassword(@Field("old_password") String old_password,
                                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("updateDeviceToken")
    Call<ResponseWrapper> updateToken(
            @Field("device_token") String device_token,
            @Field("device_type") String device_type
    );

    @FormUrlEncoded
    @POST("resendCode")
    Call<ResponseWrapper<UserEnt>> resendCode(@Field("email") String email);


    @GET("getNotifications")
    Call<ResponseWrapper<ArrayList<NotificationEnt>>> getNotification();

    @FormUrlEncoded
    @POST("pushOnOff")
    Call<ResponseWrapper> pushOnOff(@Field("notify_status") int notify_status);

    @GET("getUserProfile")
    Call<ResponseWrapper<GetProfileEnt>> getProfile(@Query("profiler_id") String profiler_id);

    @FormUrlEncoded
    @POST("resetPassword")
    Call<ResponseWrapper<UserEnt>> resetForgotPassword(
            @Field("password") String password,
            @Field("user_id") String user_id);

    @GET("getServices")
    Call<ResponseWrapper<ArrayList<GetServicesEnt>>> getServices();

    @GET("homeSearch")
    Call<ResponseWrapper<ArrayList<UserEnt>>> SearchPeople(@Query("role_id") String role_id,
                                                           @Query("search_title") String search_title);

    @FormUrlEncoded
    @POST("sendFanRequest")
    Call<ResponseWrapper<FanStatus>> addFann(
            @Field("fan_id") String fan_id);

    @GET("getAllFanRequest")
    Call<ResponseWrapper<ArrayList<AllRequestEnt>>> getAllFannRequests();

    @FormUrlEncoded
    @POST("markFanRequestStatus")
    Call<ResponseWrapper> markFannRequset(
            @Field("request_id") String request_id,
            @Field("status") String status);

    @FormUrlEncoded
    @POST("createPost")
    Call<ResponseWrapper> createFeedPost(
            @Field("description") String description,
            @Field("feed_type") String feed_type,
            @Field("location") String location,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("createPromotion")
    Call<ResponseWrapper> createPromotionPost(
            @Field("description") String description,
            @Field("feed_type") String feed_type,
            @Field("location") String location,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("service_ids") String service_ids);


    @Multipart
    @POST("createPost")
    Call<ResponseWrapper> createFeedPost(@Part("description") RequestBody description,
                                         @Part("feed_type") RequestBody feed_type,
                                         @Part("location") RequestBody location,
                                         @Part("latitude") RequestBody latitude,
                                         @Part("longitude") RequestBody longitude,
                                         @Part MultipartBody.Part image
    );

    @Multipart
    @POST("createPromotion")
    Call<ResponseWrapper> createPromotionPost(@Part("description") RequestBody description,
                                              @Part("feed_type") RequestBody feed_type,
                                              @Part("location") RequestBody location,
                                              @Part("latitude") RequestBody latitude,
                                              @Part("longitude") RequestBody longitude,
                                              @Part("service_ids") RequestBody service_ids,
                                              @Part MultipartBody.Part image
    );


    @GET("getAllPosts")
    Call<ResponseWrapper<ArrayList<Post>>> getAllPosts();

    @FormUrlEncoded
    @POST("postFavouriteUnfavourite")
    Call<ResponseWrapper> favoritePost(
            @Field("post_id") String post_id
    );

    @FormUrlEncoded
    @POST("postLikeUnLike")
    Call<ResponseWrapper> likePost(
            @Field("post_id") String post_id
    );

    @GET("myFavouritePosts")
    Call<ResponseWrapper<ArrayList<Post>>> getFavoritePosts();

    @GET("getMyPromotion")
    Call<ResponseWrapper<ArrayList<Post>>> getMyPromotionsPosts();


    @GET("getMyFans")
    Call<ResponseWrapper<ArrayList<GetMyFannsEnt>>> getMyFanns();

    @GET("getMyPosts")
    Call<ResponseWrapper<ArrayList<Post>>> getMyPosts();

    @FormUrlEncoded
    @POST("sendMessage")
    Call<ResponseWrapper<MessageThreadsEnt>> sendMessage(
            @Field("receiver_id") String receiver_id,
            @Field("message") String message
    );

    @GET("getMyThreads")
    Call<ResponseWrapper<ArrayList<MessageThreadsEnt>>> getMyThreadMsges();

    @GET("getThreadMessages")
    Call<ResponseWrapper<ArrayList<MessageThreadsEnt>>> getConversation(@Query("thread_id") String thread_id);

    @GET("getMessages")
    Call<ResponseWrapper<ArrayList<MessageThreadsEnt>>> getConversationFromMsg(@Query("receiver_id") String receiver_id);

    @FormUrlEncoded
    @POST("postComment")
    Call<ResponseWrapper<commentEnt>> postComment(
            @Field("comment") String comment,
            @Field("post_id") String post_id
    );

    @GET("getComments")
    Call<ResponseWrapper<ArrayList<commentEnt>>> getComments(@Query("post_id") String post_id);

    @GET("getAllPromotion")
    Call<ResponseWrapper<ArrayList<Post>>> getAllPromotions(@Query("service_id") String service_id);

    @GET("getMyServices")
    Call<ResponseWrapper<ArrayList<GetServicesEnt>>> getMyServices(@Query("sp_id") String sp_id);

    @GET("getServiceProvider")
    Call<ResponseWrapper<ArrayList<UserEnt>>> getServiceProvider(@Query("service_id") String service_id);

    @FormUrlEncoded
    @POST("serviceRequest")
    Call<ResponseWrapper> serviceRequest(
            @Field("receiver_id") String receiver_id,
            @Field("service_id") String service_id,
            @Field("date") String date,
            @Field("time") String time,
            @Field("location") String location,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("description") String description
    );

    @GET("spServiceRequestHistory")
    Call<ResponseWrapper<ArrayList<ServiceHistoryEnt>>> getServiceRequestHistory(@Query("from_date") String from_date,
                                                                                 @Query("to_date") String to_date);

    @GET("userServiceRequestHistory")
    Call<ResponseWrapper<ArrayList<ServiceHistoryEnt>>> getUserServiceRequestHistory(@Query("from_date") String from_date,
                                                                                 @Query("to_date") String to_date);


    @FormUrlEncoded
    @POST("markRequestStatus")
    Call<ResponseWrapper> markRequestService(
            @Field("receiver_id") String receiver_id,
            @Field("request_id") String request_id,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("requestFeedback")
    Call<ResponseWrapper> feedback(
            @Field("receiver_id") String receiver_id,
            @Field("request_id") String request_id,
            @Field("rating") String rating,
            @Field("feedback") String feedback
    );


    @GET("logout")
    Call<ResponseWrapper> logout(
            @Query("device_token") String device_token
    );

    @GET("getCms")
    Call<ResponseWrapper<CmsEnt>> cms(
            @Query("type") String type
    );

    @GET("changePrivacySetting")
    Call<ResponseWrapper> changePrivacySetting(
            @Query("type") String type,
            @Query("status") int status
    );








}
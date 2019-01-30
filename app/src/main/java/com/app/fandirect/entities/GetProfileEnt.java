package com.app.fandirect.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saeedhyder on 5/14/2018.
 */

public class GetProfileEnt {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("role_id")
    @Expose
    private String roleId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("reviews_count")
    @Expose
    private String reviewsCount;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("socialmedia_type")
    @Expose
    private String socialmediaType;
    @SerializedName("socialmedia_id")
    @Expose
    private String socialmediaId;
    @SerializedName("is_notify")
    @Expose
    private String isNotify;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("verification_code")
    @Expose
    private String verificationCode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_verified")
    @Expose
    private String isVerified;
    @SerializedName("profession")
    @Expose
    private String profession;
    @SerializedName("work_at")
    @Expose
    private String workAt;
    @SerializedName("hobbies")
    @Expose
    private String hobbies;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("dob")
    @Expose
    private String dob;

    @SerializedName("certificate_license")
    @Expose
    private String certificateLicense;
    @SerializedName("license_type")
    @Expose
    private String licenseType;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("company_category")
    @Expose
    private String companyCategory;
    @SerializedName("insurance_available")
    @Expose
    private String insuranceAvailable;
    @SerializedName("insurance_information")
    @Expose
    private String insuranceInformation;
    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("education")
    @Expose
    private String Education;

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        Education = education;
    }

    public String getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(String reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    ArrayList<GetServicesEnt> myServices=new ArrayList<>();

    public ArrayList<GetServicesEnt> getMyServices() {
        return myServices;
    }

    public void setMyServices(ArrayList<GetServicesEnt> myServices) {
        this.myServices = myServices;
    }

    public String getCertificateLicense() {
        return certificateLicense;
    }

    public void setCertificateLicense(String certificateLicense) {
        this.certificateLicense = certificateLicense;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCategory() {
        return companyCategory;
    }

    public void setCompanyCategory(String companyCategory) {
        this.companyCategory = companyCategory;
    }

    public String getInsuranceAvailable() {
        return insuranceAvailable;
    }

    public void setInsuranceAvailable(String insuranceAvailable) {
        this.insuranceAvailable = insuranceAvailable;
    }

    public String getInsuranceInformation() {
        return insuranceInformation;
    }

    public void setInsuranceInformation(String insuranceInformation) {
        this.insuranceInformation = insuranceInformation;
    }

    @SerializedName("Posts")
    @Expose
    private ArrayList<Post> posts = new ArrayList<>();
    @SerializedName("Pictures")
    @Expose
    private ArrayList<Picture> pictures = new ArrayList<>();
    @SerializedName("Fans")
    @Expose
    private ArrayList<GetMyFannsEnt> fans = new ArrayList<>();
    @SerializedName("MutualFans")
    @Expose
    private ArrayList<GetMyFannsEnt> mutualFanns = new ArrayList<>();
    @SerializedName("Reviews")
    @Expose
    private ArrayList<ReviewsEnt> reviews =new ArrayList<>();

    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("fan_status")
    @Expose
    private FanStatus fanStatus;
    @SerializedName("profile_status")
    @Expose
    private String profileStaus;
    @SerializedName("post_status")
    @Expose
    private String postStatus;

    public ArrayList<ReviewsEnt> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<ReviewsEnt> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<GetMyFannsEnt> getMutualFanns() {
        return mutualFanns;
    }

    public void setMutualFanns(ArrayList<GetMyFannsEnt> mutualFanns) {
        this.mutualFanns = mutualFanns;
    }

    public String getProfileStaus() {
        return profileStaus;
    }

    public void setProfileStaus(String profileStaus) {
        this.profileStaus = profileStaus;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public FanStatus getFanStatus() {
        return fanStatus;
    }

    public void setFanStatus(FanStatus fanStatus) {
        this.fanStatus = fanStatus;
    }




    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSocialmediaType() {
        return socialmediaType;
    }

    public void setSocialmediaType(String socialmediaType) {
        this.socialmediaType = socialmediaType;
    }

    public String getSocialmediaId() {
        return socialmediaId;
    }

    public void setSocialmediaId(String socialmediaId) {
        this.socialmediaId = socialmediaId;
    }

    public String getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(String isNotify) {
        this.isNotify = isNotify;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getWorkAt() {
        return workAt;
    }

    public void setWorkAt(String workAt) {
        this.workAt = workAt;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public ArrayList<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<Picture> pictures) {
        this.pictures = pictures;
    }

    public ArrayList<GetMyFannsEnt> getFans() {
        return fans;
    }

    public void setFans(ArrayList<GetMyFannsEnt> fans) {
        this.fans = fans;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}

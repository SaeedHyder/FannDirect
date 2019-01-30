package com.app.fandirect.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by saeedhyder on 5/5/2018.
 */

public class UserEnt {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("role_id")
    @Expose
    private String roleId;
    @SerializedName("first_name")
    @Expose
    private Object firstName;
    @SerializedName("last_name")
    @Expose
    private Object lastName;
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
    private Object address;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("state")
    @Expose
    private Object state;
    @SerializedName("country")
    @Expose
    private Object country;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("socialmedia_type")
    @Expose
    private Object socialmediaType;
    @SerializedName("socialmedia_id")
    @Expose
    private Object socialmediaId;
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
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("rating")
    @Expose
    private Float rating;

    @SerializedName("fan_status")
    @Expose
    private FanStatus fanStatus;

    @SerializedName("profile_status")
    @Expose
    private String profileStaus;
    @SerializedName("post_status")
    @Expose
    private String postStatus;
    @SerializedName("distance")
    @Expose
    private String distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
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

    public Object getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = firstName;
    }

    public Object getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
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

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Object getCountry() {
        return country;
    }

    public void setCountry(Object country) {
        this.country = country;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Object getSocialmediaType() {
        return socialmediaType;
    }

    public void setSocialmediaType(Object socialmediaType) {
        this.socialmediaType = socialmediaType;
    }

    public Object getSocialmediaId() {
        return socialmediaId;
    }

    public void setSocialmediaId(Object socialmediaId) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}

package com.movieweb.model;
public class Users 
{
	private int user_id;
    private String username;
    private String full_name;
    private String password;
    private String email;
    private String phone;
    private String avt_path;
    private String banner_path;
    private String role;
    private boolean isActive;
    private String created_at;
    private String deleted_at;
    //
    public Users() {
    }
    public int getUserId() {
        return user_id;
    }
    public void setUserId(int userId) {
        this.user_id = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getFullName() {
        return full_name;
    }
    public void setFullName(String fullName) {
        this.full_name = fullName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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
    public String getAvtPath() {
        return avt_path;
    }
    public void setAvtPath(String avtPath) {
        this.avt_path = avtPath;
    }
    public String getBannerPath() {
        return banner_path;
    }
    public void setBannerPath(String bannerPath) {
        this.banner_path = bannerPath;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
    public String getCreatedAt() {
        return created_at;
    }
    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }
    public String getDeletedAt() {
        return deleted_at;
    }
    public void setDeletedAt(String deletedAt) {
        this.deleted_at = deletedAt;
    }
}

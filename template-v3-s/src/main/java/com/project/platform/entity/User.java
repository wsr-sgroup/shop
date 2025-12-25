package com.project.platform.entity;

import java.time.LocalDateTime;

public class User {
    /**
     * 		主键
     */
    private Integer id;
    /**
     * 	用户名
     */
    private String username;
    /**
     * 		密码
     */
    private String password;
    /**
     * 		名称
     */
    private String nickname;
    /**
     * 		头像
     */
    private String avatarUrl;
    /**
     * 		电话
     */
    private String tel;
    /**
     * 		邮件
     */
    private String email;
    /**
     * 		状态
     */
    private String status;
    /**
     * 创建时间
     */
    private LocalDateTime create_time;
    //alt+insert

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDateTime create_time) {
        this.create_time = create_time;
    }
}

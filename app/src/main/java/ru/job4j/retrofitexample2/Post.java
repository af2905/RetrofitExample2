package ru.job4j.retrofitexample2;

import com.google.gson.annotations.SerializedName;

class Post {
    private Integer id;
    private int userId;
    private String title;

    @SerializedName("body")
    private String text;

    Post(int userId, String title, String text) {
        this.userId = userId;
        this.title = title;
        this.text = text;
    }

    Post(Integer id, int userId, String title, String text) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.text = text;
    }

    int getUserId() {
        return userId;
    }

    Integer getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

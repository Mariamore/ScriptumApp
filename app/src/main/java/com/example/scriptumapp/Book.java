package com.example.scriptumapp;

public class Book {

    private String title, author, status, photo;

    public Book() {
    }

    public Book(String title, String author, String status, String photo) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

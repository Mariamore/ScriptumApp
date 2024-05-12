package com.example.scriptumapp;

public class Book {

    String author, comments, editorial, photo, status, title, user, year;

    public Book() {
    }

    public Book(String author, String photo, String status, String title) {
        this.author = author;
        this.photo = photo;
        this.status = status;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

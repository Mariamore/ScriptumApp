package com.example.scriptumapp;

public class Book {
    private String title, author, editorial, year, status, comments, user, photo;

    public Book(String title, String author, String editorial, String year, String status, String comments, String user, String photo) {
        this.title = title;
        this.author = author;
        this.editorial = editorial;
        this.year = year;
        this.status = status;
        this.comments = comments;
        this.user = user;
        this.photo = photo;
    }

    public Book() {
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

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", editorial='" + editorial + '\'' +
                ", year='" + year + '\'' +
                ", status='" + status + '\'' +
                ", comments='" + comments + '\'' +
                ", user='" + user + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }


}

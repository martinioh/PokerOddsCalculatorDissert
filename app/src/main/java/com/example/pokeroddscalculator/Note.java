package com.example.pokeroddscalculator;
//basic object for a note, used when logging notes into database
public class Note {
    public String uid, title, note, date;

    public Note (){
    }

    public Note(String uid, String title, String note, String date) {
        this.uid = uid;
        this.title = title;
        this.note = note;
        this.date = date;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}

package com.example.todo;

public class Tasks {



    String id="";
    String tittle="";
    long date=0;
    boolean done=false;

    public Tasks( ) {
    }


    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }



    public Tasks(String id, String tittle, long date,boolean state) {
         this.id=id;
         this.tittle=tittle;
         this.date=date;
         this.done=state;
    }

}

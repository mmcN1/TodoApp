package com.mehmettemiz.todoapp;

import java.io.Serializable;

public class TodoItem implements Serializable {
    String name;
    String id;
    String note;

    public TodoItem(String name, String id, String note) {
        this.name = name;
        this.id = id;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getNote() {
        return note;
    }
}

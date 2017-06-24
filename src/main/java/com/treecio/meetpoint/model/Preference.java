package com.treecio.meetpoint.model;

import java.util.HashMap;

public enum Preference {

    TEMPERATURE(1, "Do you prefer higher temperatures over low?");

    static HashMap<Integer, Preference> map = new HashMap<>();

    static {
        for (Preference p : Preference.values()) {
            map.put(p.id, p);
        }
    }

    int id;
    String question;

    Preference(int id, String question) {
        this.id = id;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public static Preference fromId(int id) {
        return map.get(id);
    }

}

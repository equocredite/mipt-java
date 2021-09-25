package com.hometask1;

public class Holding {
    private final String name;
    private final Long inn;
    private final Long[] subsidiaries;

    public Holding(String name, Long inn, Long[] subsidiaries) {
        this.name = name;
        this.inn = inn;
        this.subsidiaries = subsidiaries;
    }
}

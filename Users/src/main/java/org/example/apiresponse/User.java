package org.example.apiresponse;

import java.util.UUID;

public class User {
    private String name;
    private int age;
    private String uuid;

    public User() {
        this.name = this.generateRandomName();
        this.age = this.getRandomAge();
        this.uuid = UUID.randomUUID().toString();
    }

    public String generateRandomName() {
        String[] names = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
        int index = (int) (Math.random() * names.length);
        return names[index];
    }

    public int getRandomAge() {
        return (int) (Math.random() * 100);
    }
}

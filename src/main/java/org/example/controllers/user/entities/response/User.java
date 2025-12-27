package org.example.controllers.user.entities.response;

import java.util.UUID;

public class User {
    private final String name;
    private final int age;
    private final String uuid;

    public User() {
        this.name = this.generateRandomName();
        this.age = this.generateRandomAge();
        this.uuid = UUID.randomUUID().toString();
    }

    public String generateRandomName() {
        String[] names = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
        int index = (int) (Math.random() * names.length);
        return names[index];
    }

    public int generateRandomAge() {
        return (int) (Math.random() * 100);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getUuid() {
        return uuid;
    }
}

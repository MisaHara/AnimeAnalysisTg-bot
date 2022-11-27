package com.example.botSpring.model;

import org.glassfish.grizzly.http.util.TimeStamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "usersDataTable")
public class User {

    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String userName;
    private TimeStamp registeredAt;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    private String age;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public TimeStamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(TimeStamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}

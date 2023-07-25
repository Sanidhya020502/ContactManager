package com.sanidhya.contactManager.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CONTACT")
public class Contact {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int cid;
    private String name;
    private String secondName;
    private String work;
    private String email;
    private String phone;
    private String image;
    @Column(length = 1000)
    private String decscription; 


    @ManyToOne
    private User user;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public int getCid() {
        return this.cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return this.work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDecscription() {
        return this.decscription;
    }

    public void setDecscription(String decscription) {
        this.decscription = decscription;
    }


}

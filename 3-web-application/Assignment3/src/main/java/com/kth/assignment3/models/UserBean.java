/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kth.assignment3.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;

/**
 *
 * @author axel
 */
@Entity
public class UserBean implements Serializable {
    private String email;
    private String password;
    private String username;
    
    /**
     * 
     * Getters
     * 
     */
    @Id
    @Column(name = "email")
    public String getEmail() {
        return this.email;
    }
        
    @Column(name = "password")
    public String getPassword() {
        return this.password;
    }
    
    @Column(name = "username")
    public String getUsername() {
        return this.username;
    }
    
    /**
    * 
    * Setters
    * 
    */
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
   
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (email != null ? email.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.kth.assignment3.models.User[ email=" + email + " ]";
    }
    
}

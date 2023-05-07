package com.covoiturage.user.query.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity
@Table(name = "accountuser")
public class Account {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;
    @Column(name = "date_de_naissance")
    private Date DateOfBirth;
    @Column(name = "Addresse")
    private String Adress;
    @Column(name = "Telephone")
    private String Telephone;


}

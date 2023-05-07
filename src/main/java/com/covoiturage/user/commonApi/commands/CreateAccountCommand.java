package com.covoiturage.user.commonApi.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class CreateAccountCommand extends baseCommand<String> {
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String firstname;
    @Getter
    @Setter
    private String lastname;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private String Adress;
    @Getter @Setter private Date DateOfBirth;
    @Getter @Setter private String Telephone;





    public CreateAccountCommand (String id, String username,String firstname, String lastname, String email ,String password,Date DateOfBirth,String Adress,String Telephone)

    {
        super(id);
        this.username= username;
        this.firstname=firstname;
        this.lastname=lastname;
        this.email=email;
        this.password=password;
        this.DateOfBirth=DateOfBirth;
        this.Adress=Adress;
        this.Telephone=Telephone;

    }



}

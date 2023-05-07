package com.covoiturage.user.commonApi.commands;

import lombok.Getter;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.sql.Time;
import java.util.Date;

public class UpdateAccountCommand extends baseCommand<String> {
    @TargetAggregateIdentifier
    @Getter
    private String username;
    @Getter
    private String firstname;
    @Getter
    private String lastname;
    @Getter private String email;
    @Getter private String password;
    @Getter
    private String Adress;
    @Getter private Date DateOfBirth;
    @Getter private String Telephone;
    public UpdateAccountCommand(String id, String username,String firstname, String lastname, String email ,String password,Date DateOfBirth,String Adress,String Telephone)

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
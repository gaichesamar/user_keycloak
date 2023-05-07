package com.covoiturage.user.commonApi.commands;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import lombok.Getter;

public class LoginCommand {


    @Getter
    private String username;

    @Getter
    private final String password;

    public LoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }


}

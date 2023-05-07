package com.covoiturage.user.commonApi.commands;

import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class DeleteAccountCommand {
    @Getter
    private String id;

    public DeleteAccountCommand(String id) {
        this.id = id;
    }

}

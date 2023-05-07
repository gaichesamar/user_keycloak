package com.covoiturage.user.commonApi.commands;


import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public abstract class baseCommand<T> {
    @TargetAggregateIdentifier
    @Getter
    private T id;
    public baseCommand(T id)
    {
        this.id= id;
    }
}

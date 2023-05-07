package com.covoiturage.user.commands.aggregates;

import com.covoiturage.user.KeycloakProvider;
import com.covoiturage.user.commonApi.commands.CreateAccountCommand;
import com.covoiturage.user.commonApi.commands.UpdateAccountCommand;
import com.covoiturage.user.query.controllers.AccountQueryController;
import com.covoiturage.user.query.entities.Account;
import com.covoiturage.user.query.repositories.AccountRepository;
import org.apache.catalina.User;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service

public class UserService {
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.ressource}")
    public String CLIENT_ID;
    @Value("${keycloak.secret}")
    public String SECRET;

    private final KeycloakProvider kcProvider;
    private  final AccountRepository accountRepository;
    public UserService(KeycloakProvider kcProvider,  AccountRepository accountRepository) {
        this.kcProvider = kcProvider;
        this.accountRepository = accountRepository;
    }

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AccountQueryController.class);

    @Transactional
    public void handle(CreateAccountCommand createUserCommand) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(createUserCommand.getPassword());
        credentialRepresentation.setTemporary(false);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(createUserCommand.getUsername());
        userRepresentation.setFirstName(createUserCommand.getFirstname());
        userRepresentation.setLastName(createUserCommand.getLastname());
        userRepresentation.setEmail(createUserCommand.getEmail());

        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Arrays.asList(credentialRepresentation));
        Map<String, List<String>> userAttributes = new HashMap<>();
        userAttributes.put("attribute_name", Arrays.asList("attribute_value"));
        userRepresentation.setAttributes(userAttributes);
        usersResource.create(userRepresentation);

        Account account = new Account();
        account.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE));
        account.setUsername(userRepresentation.getUsername());
        account.setPassword(createUserCommand.getPassword());
        account.setEmail(createUserCommand.getEmail());
        account.setFirstname(createUserCommand.getFirstname());
        account.setLastname(createUserCommand.getLastname());
        account.setDateOfBirth(createUserCommand.getDateOfBirth());
        account.setAdress(createUserCommand.getAdress());
        account.setTelephone(createUserCommand.getTelephone());

        accountRepository.save(account);
    }

    public void handle(String id, UpdateAccountCommand command) {
        UserResource userResource = kcProvider.getInstance().realm(realm).users().get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setUsername(command.getUsername());
        userRepresentation.setFirstName(command.getFirstname());
        userRepresentation.setLastName(command.getLastname());
        userRepresentation.setEmail(command.getEmail());
        userResource.update(userRepresentation);
       }

   public void deleteUser(String id) {
        UserResource userResource = kcProvider.getInstance().realm(realm).users().get(id);
        userResource.remove();
    }






}
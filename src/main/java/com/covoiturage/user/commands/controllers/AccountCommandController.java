package com.covoiturage.user.commands.controllers;
import com.covoiturage.user.KeycloakProvider;
import com.covoiturage.user.commands.aggregates.UserService;
import com.covoiturage.user.commonApi.commands.CreateAccountCommand;
import com.covoiturage.user.commonApi.commands.DeleteAccountCommand;
import com.covoiturage.user.commonApi.commands.UpdateAccountCommand;
import com.covoiturage.user.query.controllers.AccountQueryController;
import com.covoiturage.user.query.entities.UserDTO;
import com.covoiturage.user.query.services.KeycloakAdminClientService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
;import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
@RequestMapping(path = "/commands/account")
public class AccountCommandController {
    private CommandGateway commandGateway;
    private EventStore eventStore;
    private final QueryGateway queryGateway;
    private final UserService userService;
    private final KeycloakAdminClientService kcAdminClient;
    private final KeycloakProvider kcProvider;
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger( AccountQueryController.class);

    public AccountCommandController(CommandGateway commandGateway, EventStore eventStore, QueryGateway queryGateway, UserService userService, KeycloakAdminClientService kcAdminClient, KeycloakProvider kcProvider) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
        this.queryGateway = queryGateway;
        this.userService = userService;
        this.kcAdminClient = kcAdminClient;
        this.kcProvider = kcProvider;
    }

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody CreateAccountCommand createUserCommand) {
        userService.handle(createUserCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    @PostMapping("/login")

    public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody UserDTO userDTO) {
        Keycloak keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(userDTO).build();

        AccessTokenResponse accessTokenResponse = null;
        try {
            accessTokenResponse = keycloak.tokenManager().getAccessToken();
            return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse);
        } catch (BadRequestException ex) {
            LOG.warn("invalid account. User probably hasn't verified email.", ex);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/photo")
    public ResponseEntity<Void> saveProfilePhoto(@RequestParam("photo") MultipartFile photo) {
        // Handle saving the photo to the user's profile
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/getUsername")
    public ResponseEntity<Map<String, String>> getUsername(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) keycloakAuthenticationToken.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        String username = keycloakSecurityContext.getToken().getPreferredUsername();
        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/")
    public ResponseEntity<Void> updateUser(Principal principal, @RequestBody UpdateAccountCommand command) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) keycloakAuthenticationToken.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        AccessToken tokenInfo = keycloakSecurityContext.getToken();
        String userId = tokenInfo.getSubject();

        userService.handle(userId, command);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAccount(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) keycloakAuthenticationToken.getPrincipal();
        KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
        AccessToken tokenInfo = keycloakSecurityContext.getToken();
        String userId = tokenInfo.getSubject();

        DeleteAccountCommand command = new DeleteAccountCommand(userId);
        commandGateway.sendAndWait(command);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody UpdateAccountCommand command) {
        userService.handle(id, command);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }




}
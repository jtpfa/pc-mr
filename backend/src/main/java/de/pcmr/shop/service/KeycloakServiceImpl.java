package de.pcmr.shop.service;

import de.pcmr.shop.domain.CustomerEntity;
import de.pcmr.shop.domain.CustomerRoleEnum;
import de.pcmr.shop.exception.keycloak.KeycloakEndpointNotFoundException;
import de.pcmr.shop.exception.keycloak.KeycloakUnknownErrorException;
import de.pcmr.shop.exception.keycloak.KeycloakUserAlreadyExistsException;
import de.pcmr.shop.exception.keycloak.KeycloakUserIsNotAuthorizedException;
import de.pcmr.shop.service.model.KeycloakUserRepresentation;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KeycloakServiceImpl implements KeycloakServiceI {

    @Value("${PCMR_AUTH_SERVER_URL}")
    private String authServerUrl;
    @Value("${PCMR_KEYCLOAK_REALM}")
    private String keycloakRealm;
    @Value("${PCMR_RESOURCE}")
    private String keycloakClient;
    @Value("${PCMR_KEYCLOAK_REGISTRATION_USER}")
    private String keycloakRegistrationUser;
    @Value("${PCMR_KEYCLOAK_REGISTRATION_PASSWORD}")
    private String keycloakRegistrationPassword;

    private Keycloak keycloak;

    @PostConstruct
    public void initKeycloak() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(keycloakRealm)
                .clientId(keycloakClient)
                .username(keycloakRegistrationUser)
                .password(keycloakRegistrationPassword)
                .build();
    }

    @Override
    public void createKeycloakUser(CustomerEntity customerEntity) throws KeycloakUserIsNotAuthorizedException, KeycloakUserAlreadyExistsException, KeycloakEndpointNotFoundException, KeycloakUnknownErrorException {
        try {
            UserRepresentation user = createKeycloakUserFromCustomer(customerEntity);

            RealmResource realmResource = keycloak.realm(keycloakRealm);
            UsersResource usersResource = realmResource.users();

            Response response = usersResource.create(user);
            String userId = CreatedResponseUtil.getCreatedId(response);

            CredentialRepresentation passwordCredentials = createCredentialsFromCustomer(customerEntity);

            UserResource userResource = usersResource.get(userId);
            userResource.resetPassword(passwordCredentials);
        } catch (WebApplicationException ex) {
            handleWebApplicationException(ex);
        }
    }

    @Override
    public void updateKeycloakUser(CustomerEntity customerEntity, String currentUsername) throws KeycloakEndpointNotFoundException, KeycloakUnknownErrorException, KeycloakUserAlreadyExistsException, KeycloakUserIsNotAuthorizedException {
        try {
            RealmResource realmResource = keycloak.realm(keycloakRealm);
            UsersResource usersResource = realmResource.users();

            UserRepresentation userRepresentation = findUserByEmail(currentUsername, usersResource);
            userRepresentation.setUsername(customerEntity.getEmail());
            userRepresentation.setEmail(customerEntity.getEmail());
            userRepresentation.setFirstName(customerEntity.getFirstName());
            userRepresentation.setLastName(customerEntity.getLastName());

            UserResource userResource = usersResource.get(userRepresentation.getId());
            userResource.update(userRepresentation);
        } catch (ClientErrorException ex) {
            handleWebApplicationException(ex);
        }
    }

    @Override
    public List<UserRepresentation> findAllKeycloakUsersWithRole(CustomerRoleEnum roleEnum) throws KeycloakEndpointNotFoundException, KeycloakUnknownErrorException, KeycloakUserAlreadyExistsException, KeycloakUserIsNotAuthorizedException {
        try {
            RealmResource realmResource = keycloak.realm(keycloakRealm);
            return getUserListBasedOnRole(roleEnum, realmResource);
        } catch (ClientErrorException ex) {
            handleWebApplicationException(ex);
        } return null;
    }

    @Override
    public CustomerRoleEnum getRoleOfCustomer(CustomerEntity customerEntity) throws KeycloakEndpointNotFoundException, KeycloakUnknownErrorException, KeycloakUserAlreadyExistsException, KeycloakUserIsNotAuthorizedException {
        try {
            RealmResource realmResource = keycloak.realm(keycloakRealm);
            UsersResource usersResource = realmResource.users();

            UserRepresentation userRepresentation = findUserByEmail(customerEntity.getEmail(), usersResource);
            UserResource userResource = usersResource.get(userRepresentation.getId());
            List<RoleRepresentation> userRoles = userResource.roles().realmLevel().listEffective();

            List<CustomerRoleEnum> customerRoles = CustomerRoleEnum.getFromStringList(userRoles.stream().map(RoleRepresentation::getName).collect(Collectors.toList()));

            if (customerRoles.contains(CustomerRoleEnum.ADMIN)) {
                return CustomerRoleEnum.ADMIN;
            } else if (customerRoles.contains(CustomerRoleEnum.EMPLOYEE)) {
                return CustomerRoleEnum.EMPLOYEE;
            } else {
                return CustomerRoleEnum.CUSTOMER;
            }

        } catch (ClientErrorException ex) {
            handleWebApplicationException(ex);
        } return null;
    }

    private UserRepresentation findUserByEmail(String email, UsersResource usersResource) {
        List<UserRepresentation> keycloakUserList = usersResource.search(email);
        if (keycloakUserList.size() != 1) {
            throw new IllegalStateException();
        }
        return keycloakUserList.get(0);
    }

    private List<UserRepresentation> getUserListBasedOnRole(CustomerRoleEnum roleEnum, RealmResource realmResource) {
        switch (roleEnum) {
            case CUSTOMER: {
                Set<KeycloakUserRepresentation> customerSet = realmResource.roles().get(roleEnum.toString()).getRoleUserMembers()
                        .parallelStream().map(KeycloakUserRepresentation::new).collect(Collectors.toSet());
                Set<KeycloakUserRepresentation> employeeSet = realmResource.roles().get(CustomerRoleEnum.EMPLOYEE.toString()).getRoleUserMembers()
                        .parallelStream().map(KeycloakUserRepresentation::new).collect(Collectors.toSet());
                customerSet.removeAll(employeeSet);
                return new ArrayList<>(customerSet);
            }
            case EMPLOYEE: {
                Set<KeycloakUserRepresentation> employeeSet = realmResource.roles().get(roleEnum.toString()).getRoleUserMembers()
                        .parallelStream().map(KeycloakUserRepresentation::new).collect(Collectors.toSet());
                Set<KeycloakUserRepresentation> adminSet = realmResource.roles().get(CustomerRoleEnum.ADMIN.toString()).getRoleUserMembers()
                        .parallelStream().map(KeycloakUserRepresentation::new).collect(Collectors.toSet());
                employeeSet.removeAll(adminSet);
                return new ArrayList<>(employeeSet);
            }
            default:
                Set<KeycloakUserRepresentation> adminSet = realmResource.roles().get(roleEnum.toString()).getRoleUserMembers()
                        .parallelStream().map(KeycloakUserRepresentation::new).collect(Collectors.toSet());
                adminSet.removeIf(e -> e.getEmail() == null);
                return new ArrayList<>(adminSet);
        }
    }

    private void handleWebApplicationException(WebApplicationException ex) throws KeycloakUserIsNotAuthorizedException, KeycloakUserAlreadyExistsException, KeycloakEndpointNotFoundException, KeycloakUnknownErrorException {
        switch (ex.getResponse().getStatus()) {
            case 401:
                throw new KeycloakUserIsNotAuthorizedException(ex);
            case 404:
                throw new KeycloakEndpointNotFoundException(ex);
            case 409:
                throw new KeycloakUserAlreadyExistsException(ex);
            case 400:
                throw new KeycloakUnknownErrorException();
            default:
                throw ex;
        }
    }

    private UserRepresentation createKeycloakUserFromCustomer(CustomerEntity customerEntity) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(customerEntity.getEmail());
        user.setFirstName(customerEntity.getFirstName());
        user.setLastName(customerEntity.getLastName());
        user.setUsername(customerEntity.getEmail());
        user.setAttributes(Collections.singletonMap("origin", Collections.singletonList("pcmr_application")));
        user.setEnabled(true);

        return user;
    }

    private CredentialRepresentation createCredentialsFromCustomer(CustomerEntity customerEntity) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(customerEntity.getPassword());

        return passwordCredentials;
    }
}

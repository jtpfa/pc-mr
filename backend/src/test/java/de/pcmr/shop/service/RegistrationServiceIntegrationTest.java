package de.pcmr.shop.service;

import de.pcmr.shop.AbstractIntegrationTest;
import de.pcmr.shop.builder.CustomerEntityBuilder;
import de.pcmr.shop.domain.CustomerEntity;
import de.pcmr.shop.exception.CustomerAlreadyExistsException;
import de.pcmr.shop.exception.keycloak.KeycloakEndpointNotFoundException;
import de.pcmr.shop.exception.keycloak.KeycloakUnknownErrorException;
import de.pcmr.shop.exception.keycloak.KeycloakUserAlreadyExistsException;
import de.pcmr.shop.exception.keycloak.KeycloakUserIsNotAuthorizedException;
import de.pcmr.shop.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Partial integration test for the registration service.
 *
 * @author Fynn Lohse
 */

class RegistrationServiceIntegrationTest extends AbstractIntegrationTest {
    private final RegistrationServiceI registrationService;
    private final CustomerRepository customerRepository;

    private final static String CUSTOMER_EMAIL = "test@test.com";
    private final static String CUSTOMER_FIRSTNAME = "John";
    private final static String CUSTOMER_LASTNAME = "Smith";
    private final static String CUSTOMER_PASSWORD = "MyS3cr3tP@ssw0rd";

    private final static String CUSTOMER_INVALID_EMAIL = "kaputteEmail";
    private final static String CUSTOMER_INVALID_PASSWORD = "1234";
    private final static String CUSTOMER_INVALID_FIRSTNAME = "";
    private final static String CUSTOMER_INVALID_LASTNAME = "";

    private CustomerEntity customerEntity;

    private final Given given = new Given();
    private final When when = new When();
    private final Then then = new Then();

    private UsersResource usersResource;

    @Autowired
    RegistrationServiceIntegrationTest(RegistrationServiceI registrationService, CustomerRepository customerRepository) {
        super();
        this.registrationService = registrationService;
        this.customerRepository = customerRepository;

    }

    @PostConstruct
    void initUserResource() {
        this.usersResource = super.getUsersResource();
    }

    @Test
    void testSuccessfulRegisterNewCustomer() throws KeycloakUserAlreadyExistsException, CustomerAlreadyExistsException, KeycloakEndpointNotFoundException, KeycloakUserIsNotAuthorizedException, KeycloakUnknownErrorException {
        given.aCustomerEntityWith(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME, CUSTOMER_PASSWORD);

        when.aCustomerIsRegistered();

        then.numberOfCustomersInTheDatabaseAre(1);
        then.technicalAttributesShouldNotBeNull();
        then.passwordShouldBeNull();
        then.theAttributesOfTheCustomerAre(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME);

        then.numberOfKeycloakUsersAre(2);
        then.theAttributesOfTheKeycloakUserAre(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME);
        then.theKeycloakUserIsEnabled(CUSTOMER_EMAIL);
    }

    @Test
    void testRegisterNewCustomerWithInvalidEmail() {
        given.aCustomerEntityWith(CUSTOMER_INVALID_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME, CUSTOMER_PASSWORD);

        assertThrows(ConstraintViolationException.class, when::aCustomerIsRegistered);

        then.numberOfCustomersInTheDatabaseAre(0);
        then.numberOfKeycloakUsersAre(1);
    }

    @Test
    void testRegisterNewCustomerWithInvalidPassword() {
        given.aCustomerEntityWith(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME, CUSTOMER_INVALID_PASSWORD);

        assertThrows(ConstraintViolationException.class, when::aCustomerIsRegistered);

        then.numberOfCustomersInTheDatabaseAre(0);
        then.numberOfKeycloakUsersAre(1);
    }

    @Test
    void testRegisterNewCustomerThatAlreadyExists() throws KeycloakUnknownErrorException, KeycloakUserAlreadyExistsException, CustomerAlreadyExistsException, KeycloakEndpointNotFoundException, KeycloakUserIsNotAuthorizedException {
        given.aCustomerEntityWith(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME, CUSTOMER_PASSWORD);

        when.aCustomerIsRegistered();
        assertThrows(KeycloakUserAlreadyExistsException.class, when::aCustomerIsRegistered);

        then.numberOfCustomersInTheDatabaseAre(1);
        then.technicalAttributesShouldNotBeNull();
        then.passwordShouldBeNull();
        then.theAttributesOfTheCustomerAre(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME);

        then.numberOfKeycloakUsersAre(2);
        then.theAttributesOfTheKeycloakUserAre(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_LASTNAME);
        then.theKeycloakUserIsEnabled(CUSTOMER_EMAIL);
    }

    @Test
    void testRegisterNewCustomerWithInvalidFirstname() {
        given.aCustomerEntityWith(CUSTOMER_EMAIL, CUSTOMER_INVALID_FIRSTNAME, CUSTOMER_LASTNAME, CUSTOMER_PASSWORD);

        assertThrows(ConstraintViolationException.class, when::aCustomerIsRegistered);

        then.numberOfCustomersInTheDatabaseAre(0);
        then.numberOfKeycloakUsersAre(1);
    }

    @Test
    void testRegisterNewCustomerWithInvalidLastname() {
        given.aCustomerEntityWith(CUSTOMER_EMAIL, CUSTOMER_FIRSTNAME, CUSTOMER_INVALID_LASTNAME, CUSTOMER_PASSWORD);

        assertThrows(ConstraintViolationException.class, when::aCustomerIsRegistered);

        then.numberOfCustomersInTheDatabaseAre(0);
        then.numberOfKeycloakUsersAre(1);
    }

    class Given {
        void aCustomerEntityWith(String email, String firstname, String lastname, String password) {
            customerEntity = CustomerEntityBuilder.aCustomerEntity()
                    .withEmail(email)
                    .withFirstName(firstname)
                    .withLastName(lastname)
                    .withPassword(password)
                    .build();
        }
    }

    class When {
        void aCustomerIsRegistered() throws KeycloakEndpointNotFoundException, KeycloakUserAlreadyExistsException, KeycloakUserIsNotAuthorizedException, CustomerAlreadyExistsException, KeycloakUnknownErrorException {
            registrationService.registerCustomer(customerEntity);
        }
    }

    class Then {
        void numberOfCustomersInTheDatabaseAre(int numberOfCustomers) {
            assertEquals(numberOfCustomers, customerRepository.findAll().size());
        }

        void theAttributesOfTheCustomerAre(String email, String firstname, String lastname) {
            CustomerEntity customer = customerRepository.findAll().get(0);
            assertEquals(email, customer.getEmail());
            assertEquals(firstname, customer.getFirstName());
            assertEquals(lastname, customer.getLastName());
        }

        void passwordShouldBeNull() {
            CustomerEntity customer = customerRepository.findAll().get(0);
            assertNull(customer.getPassword());
        }

        void technicalAttributesShouldNotBeNull() {
            CustomerEntity customer = customerRepository.findAll().get(0);
            assertNotNull(customer.getCreated());
            //assertNotNull(customer.getCreatedBy());
            //assertNotNull(customer.getLastModifiedBy());
            assertNotNull(customer.getUpdated());
        }

        void numberOfKeycloakUsersAre(int numberOfKeycloakUsers) {
            assertEquals(numberOfKeycloakUsers, usersResource.list().size());
        }

        void theAttributesOfTheKeycloakUserAre(String email, String firstname, String lastname) {
            UserRepresentation userRepresentation = usersResource.search(email).get(0);
            assertEquals(email, userRepresentation.getEmail());
            assertEquals(email, userRepresentation.getUsername());
            assertEquals(firstname, userRepresentation.getFirstName());
            assertEquals(lastname, userRepresentation.getLastName());

        }

        void theKeycloakUserIsEnabled(String email) {
            UserRepresentation userRepresentation = usersResource.search(email).get(0);
            assertTrue(userRepresentation.isEnabled());
        }
    }
}

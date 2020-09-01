package de.pcmr.shop.api.model;

import de.pcmr.shop.domain.CustomerRoleEnum;

public class CustomerDetailsRoleDTO extends CustomerDetailsDTO {
    private CustomerRoleEnum customerRole;

    public CustomerRoleEnum getCustomerRole() {
        return customerRole;
    }

    public void setCustomerRole(CustomerRoleEnum customerRole) {
        this.customerRole = customerRole;
    }
}

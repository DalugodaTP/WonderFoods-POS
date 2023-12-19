package model;

import dto.CustomerDto;

import java.util.List;

public interface CustomerModel {
    public boolean saveCustomer(CustomerDto dto);
    public boolean updateCustomer(CustomerDto dto);
    public boolean deleteCustomer(String id);

    List<CustomerDto> allCustomers();

}

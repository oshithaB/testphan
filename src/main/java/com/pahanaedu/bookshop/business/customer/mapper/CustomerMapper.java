package com.pahanaedu.bookshop.business.customer.mapper;

import com.pahanaedu.bookshop.business.customer.dto.CustomerDTO;
import com.pahanaedu.bookshop.business.customer.model.Customer;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for Customer entity and DTO conversion
 */
public class CustomerMapper {
    
    /**
     * Convert Customer entity to CustomerDTO
     * @param customer Customer entity
     * @return CustomerDTO
     */
    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setAccountNumber(customer.getAccountNumber());
        dto.setName(customer.getName());
        dto.setAddress(customer.getAddress());
        dto.setTelephone(customer.getTelephone());
        dto.setEmail(customer.getEmail());
        dto.setActive(customer.isActive());
        
        if (customer.getCreatedAt() != null) {
            dto.setCreatedAt(customer.getCreatedAt().toString());
        }
        if (customer.getUpdatedAt() != null) {
            dto.setUpdatedAt(customer.getUpdatedAt().toString());
        }
        
        return dto;
    }
    
    /**
     * Convert CustomerDTO to Customer entity
     * @param dto CustomerDTO
     * @return Customer entity
     */
    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setAccountNumber(dto.getAccountNumber());
        customer.setName(dto.getName());
        customer.setAddress(dto.getAddress());
        customer.setTelephone(dto.getTelephone());
        customer.setEmail(dto.getEmail());
        customer.setActive(dto.isActive());
        
        return customer;
    }
    
    /**
     * Convert list of Customer entities to list of CustomerDTOs
     * @param customers List of Customer entities
     * @return List of CustomerDTOs
     */
    public static List<CustomerDTO> toDTOList(List<Customer> customers) {
        if (customers == null) {
            return null;
        }
        
        List<CustomerDTO> dtoList = new ArrayList<>();
        for (Customer customer : customers) {
            dtoList.add(toDTO(customer));
        }
        return dtoList;
    }
    
    /**
     * Convert list of CustomerDTOs to list of Customer entities
     * @param dtos List of CustomerDTOs
     * @return List of Customer entities
     */
    public static List<Customer> toEntityList(List<CustomerDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        
        List<Customer> entityList = new ArrayList<>();
        for (CustomerDTO dto : dtos) {
            entityList.add(toEntity(dto));
        }
        return entityList;
    }
}
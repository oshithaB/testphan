package com.pahanaedu.bookshop.persistence.resource.resource.factory.impl;

import com.pahanaedu.bookshop.business.bill.bill.model.Bill;
import com.pahanaedu.bookshop.business.bill.bill.model.BillItem;
import com.pahanaedu.bookshop.persistence.resource.resource.factory.ProductFactory;
import com.pahanaedu.bookshop.persistence.resource.resource.model.Product;
import java.math.BigDecimal;


public class BillFactoryImpl extends ProductFactory {
    
    public static final String BILL_TYPE = "bill";
    public static final String BILL_ITEM_TYPE = "bill_item";
    
    @Override
    public Product createProduct(String type) {
        return createProduct(type, (Object[]) null);
    }
    
    @Override
    public Product createProduct(String type, Object... params) {
        switch (type.toLowerCase()) {
            case BILL_TYPE:
                return createBill(params);
            case BILL_ITEM_TYPE:
                return createBillItem(params);
            default:
                throw new IllegalArgumentException("Unsupported product type: " + type);
        }
    }
    
    @Override
    public String[] getSupportedTypes() {
        return new String[]{BILL_TYPE, BILL_ITEM_TYPE};
    }
    
    private Bill createBill(Object... params) {
        Bill bill = new Bill();
        
        if (params != null && params.length >= 3) {
            bill.setBillNumber((String) params[0]);
            bill.setCustomerId((Integer) params[1]);
            bill.setCashierId((Integer) params[2]);
        }
        
        return bill;
    }
    
    private BillItem createBillItem(Object... params) {
        BillItem item = new BillItem();
        
        if (params != null && params.length >= 6) {
            item.setBillId((Integer) params[0]);
            item.setBookId((Integer) params[1]);
            item.setQuantity((Integer) params[2]);
            item.setUnitPrice((BigDecimal) params[3]);
            item.setTaxRate((BigDecimal) params[4]);
            item.setDiscountRate((BigDecimal) params[5]);
            item.calculateAmounts();
        }
        
        return item;
    }
    
    /**
     * Create a bill with specific parameters
     * @param billNumber bill number
     * @param customerId customer ID
     * @param cashierId cashier ID
     * @return Bill instance
     */
    public Bill createBill(String billNumber, int customerId, int cashierId) {
        return (Bill) createProduct(BILL_TYPE, billNumber, customerId, cashierId);
    }
    
    /**
     * Create a bill item with specific parameters
     * @param billId bill ID
     * @param bookId book ID
     * @param quantity quantity
     * @param unitPrice unit price
     * @param taxRate tax rate
     * @param discountRate discount rate
     * @return BillItem instance
     */
    public BillItem createBillItem(int billId, int bookId, int quantity, BigDecimal unitPrice, BigDecimal taxRate, BigDecimal discountRate) {
        return (BillItem) createProduct(BILL_ITEM_TYPE, billId, bookId, quantity, unitPrice, taxRate, discountRate);
    }
}
package org.axis2.service.OrderProcessing;

import java.util.HashMap;

public class Inventory {

    private HashMap<Product, Integer> productsInStock;

    private static Inventory instance = new Inventory();

    public Inventory() {
        System.out.println("Inventory initialized");
        Product prod1 = new Product("MacC_1", "Mac Cheese", 34.89);
        Product prod2 = new Product("PasReg_4", "Pasta Regular", 126.00);
        Product prod3 = new Product("PeaB_31", "Peanut Butter", 54.50);
        Product prod4 = new Product("AppSa_62", "Apple Sauce", 78.45);

        HashMap<Product, Integer> tempStock = new HashMap<>();

        tempStock.put(prod1, 25);
        tempStock.put(prod2, 12);
        tempStock.put(prod3, 14);
        tempStock.put(prod4, 11);

        setProductsInStock(tempStock);

        System.out.println("Finished creating inventory instance");

    }

    public static Inventory getInstance(){
        return instance;
    }

    public HashMap<Product, Integer> getProductsInStock() {
        return productsInStock;
    }

    public void setProductsInStock(HashMap<Product, Integer> productsInStock) {
        this.productsInStock = productsInStock;
    }

    public boolean updateInventory(Integer requestedQuantity, String id) {
        System.out.println("Trying to update inventory");
        for(Product prod : productsInStock.keySet()) {
            Integer availableQuant = productsInStock.get(prod);
            if(prod.getProductId().equals(id)) {
                if(availableQuant >= requestedQuantity) {
                    System.out.println("Items can be supplied");
                    productsInStock.put(prod, (availableQuant - requestedQuantity));
                    return true;
                } else return false; //product not in stock
            }
        }
        return false; //no such product
    }

    public void printCurrentStockStatus () {
        for(Product prod : productsInStock.keySet()) {
            System.out.println(prod.getProductId() + " --- " + productsInStock.get(prod));
        }
    }
}

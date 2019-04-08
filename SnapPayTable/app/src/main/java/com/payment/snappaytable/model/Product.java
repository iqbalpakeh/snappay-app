package com.payment.snappaytable.model;

public class Product {

    /**
     * List of product name
     */
    public static final String PRODUCT_GOLDEN_ROLL = "Golden Roll";
    public static final String PRODUCT_PHOENIX_ROLL = "Phoenix Roll";
    public static final String PRODUCT_LOBSTER_SALAD_ROLL = "Lobster Salad Roll";
    public static final String PRODUCT_SALMON_ROLL = "Salmon Roll";
    public static final String PRODUCT_UNAGI_ROLL = "Unagi Roll";
    public static final String PRODUCT_KANI_MENTAI_MAYO_ROLL = "Kani Mentai Mayo Roll";
    public static final String PRODUCT_MOMOJI = "Momoji (10 pcs)";
    public static final String PRODUCT_SUMIRE = "Sumire (10 pcs)";

    /**
     * Name of the product
     */
    private String name;

    /**
     * Price of the product
     */
    private float price;

    /**
     * resource file used by the product
     */
    private int resource;

    /**
     * Private constructor to make sure that the object can only be build with build function
     *
     * @param name     of product
     * @param price    of product
     * @param resource image used by product
     */
    private Product(String name, float price, int resource) {
        this.name = name;
        this.price = price;
        this.resource = resource;
    }

    /**
     * Build product object
     *
     * @param name     of product
     * @param price    of product
     * @param resource image used by product
     * @return Product object
     */
    public static Product build(String name, float price, int resource) {
        return new Product(name, price, resource);
    }

    /**
     * Retrieve product name
     *
     * @return product name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve product price
     *
     * @return product price
     */
    public float getPrice() {
        return price;
    }

    /**
     * Retrieve image resource
     *
     * @return image resource
     */
    public int getResource() {
        return resource;
    }
}

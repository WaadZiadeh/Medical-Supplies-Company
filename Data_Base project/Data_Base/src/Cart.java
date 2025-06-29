public class Cart {

    private int productID;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;

    public Cart(int productID, String productName, double price, int quantity, double totalPrice) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice=getTotalPrice();
    }

    public int getProductID() {
        return productID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        this.totalPrice=this.price*this.quantity;
        return totalPrice; 
    }   
}

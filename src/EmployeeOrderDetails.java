public class EmployeeOrderDetails {
    private int orderID;
    private String productName;
    private int quantity;

    public EmployeeOrderDetails(int orderID, String productName, int quantity) {
        this.orderID = orderID;
        this.productName = productName;
        this.quantity = quantity;
    }
    
    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    
}

import java.sql.Date;

public class EmployeeOrders {
    private int orderID;
    private String customerName;
    private String contactNumber;
    private String email;
    private double totalAmount;
    private Date orderDate;
    private String status;

    public EmployeeOrders(int orderID, String customerName, String contactNumber, String email, double totalAmount,
            Date orderDate, String status) {
        this.orderID = orderID;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }

    public EmployeeOrders(int orderID, double totalAmount, String status) {
        this.orderID = orderID;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    
}

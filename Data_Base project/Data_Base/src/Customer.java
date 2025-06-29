public class Customer {
    private int customerID;
    private String name;
    private String email;
    private String contact;
    private double totalAmount;

    public Customer(String name, String email, String contact) {
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public Customer(int customerID, String name, String email, String contact) {
        this.customerID = customerID;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    

    public Customer(String name, double totalAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    

    
}

public class Employee {
    private String name;
    private String userName;
    private String password;
    private String phoneNumber;
    private String email;
    private int ID;
    public Employee(String name, String userName, String phoneNumber) {
        this.name = name;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }
    

    public Employee(String name, String userName, String phoneNumber, String email, int iD) {
        this.name = name;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        ID = iD;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public int getID() {
        return ID;
    }


    public void setID(int iD) {
        ID = iD;
    }
    
    
}

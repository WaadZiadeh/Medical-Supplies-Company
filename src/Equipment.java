public class Equipment {
    private int ProductID;
    private String ProductName;
    private double Price;
    private int StockQuantity;
    private String ProductionCompany;
    private String WarrantyPeriod;
    private String PowerSource;
    private String Certification;
    private String ExpectedLifespanl;
    public int length;

    //quipment(int, String, double, int, String, int, String, String, int)
    public Equipment() {
    }
    
    public Equipment(int productID, String productName, double price, int stockQuantity, String productionCompany,
            String warrantyPeriod, String powerSource, String certification, String expectedLifespanl) {
        ProductID = productID;
        ProductName = productName;
        Price = price;
        StockQuantity = stockQuantity;
        ProductionCompany = productionCompany;
        WarrantyPeriod = warrantyPeriod;
        PowerSource = powerSource;
        Certification = certification;
        ExpectedLifespanl = expectedLifespanl;
        length=9;
    }

    

    public Equipment(String warrantyPeriod, String powerSource, String certification, String expectedLifespanl) {
        WarrantyPeriod = warrantyPeriod;
        PowerSource = powerSource;
        Certification = certification;
        ExpectedLifespanl = expectedLifespanl;
        length=4;
    }

    public String getWarrantyPeriod() {
        return WarrantyPeriod;
    }
    public void setWarrantyPeriod(String warrantyPeriod) {
        WarrantyPeriod = warrantyPeriod;
    }
    public String getPowerSource() {
        return PowerSource;
    }
    public void setPowerSource(String powerSource) {
        PowerSource = powerSource;
    }
    public String getCertification() {
        return Certification;
    }
    public void setCertification(String certification) {
        Certification = certification;
    }
    public String getExpectedLifespanl() {
        return ExpectedLifespanl;
    }
    public void setExpectedLifespanl(String expectedLifespanl) {
        ExpectedLifespanl = expectedLifespanl;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getStockQuantity() {
        return StockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        StockQuantity = stockQuantity;
    }

    public String getProductionCompany() {
        return ProductionCompany;
    }

    public void setProductionCompany(String productionCompany) {
        ProductionCompany = productionCompany;
    }

    
}

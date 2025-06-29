import java.sql.Date;

public class Supply {
    private int ProductID;
    private String ProductName;
    private double Price;
    private int StockQuantity;
    private String ProductionCompany;
    private Date ExpirationDate;
    private boolean Recyclability;
    private String UsageInstructions;
    private String StorageRequirements;
    public int length;
       // Equipment(int, String, double, double, String, Date, String, String, String)


    public Supply() {
    }
    
    public Supply(int productID, String productName, double price, int stockQuantity, String productionCompany,
            Date expirationDate, boolean recyclability, String usageInstructions, String storageRequirements) {
        ProductID = productID;
        ProductName = productName;
        Price = price;
        StockQuantity = stockQuantity;
        ProductionCompany = productionCompany;
        ExpirationDate = expirationDate;
        Recyclability = recyclability;
        UsageInstructions = usageInstructions;
        StorageRequirements = storageRequirements;
        length=9;
    }

    

    public Supply(Date expirationDate, boolean recyclability, String usageInstructions, String storageRequirements) {
        ExpirationDate = expirationDate;
        Recyclability = recyclability;
        UsageInstructions = usageInstructions;
        StorageRequirements = storageRequirements;
        length=4;
    }

    public Date getExpirationDate() {
        return ExpirationDate;
    }
    public void setExpirationDate(Date expirationDate) {
        ExpirationDate = expirationDate;
    }
    public boolean isRecyclability() {
        return Recyclability;
    }
    public void setRecyclability(boolean recyclability) {
        Recyclability = recyclability;
    }
    public String getUsageInstructions() {
        return UsageInstructions;
    }
    public void setUsageInstructions(String usageInstructions) {
        UsageInstructions = usageInstructions;
    }
    public String getStorageRequirements() {
        return StorageRequirements;
    }
    public void setStorageRequirements(String storageRequirements) {
        StorageRequirements = storageRequirements;
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

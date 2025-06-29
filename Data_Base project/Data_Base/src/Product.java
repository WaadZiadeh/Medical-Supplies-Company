public class Product {
    private String productName;
    private double price;
    private int soldCount;
    private double totalSales;
    public Product(String productName, double price, int soldCount, double totalSales) {
        this.productName = productName;
        this.price = price;
        this.soldCount = soldCount;
        this.totalSales = totalSales;
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
    public int getSoldCount() {
        return soldCount;
    }
    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }
    public double getTotalSales() {
        return totalSales;
    }
    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    
}

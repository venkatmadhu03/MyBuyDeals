package appsnova.com.mybuydeals.models;

public class HomeProductsModel {

    private String productId;
    private String productName;
    private String price;
    private String regularPrice;
    private String imageUrl;
    private String vendorName;
    private String vendorDescription;
    private String rating;
    private String productDesc;

    public HomeProductsModel(String productId, String productName, String price,
                             String regularPrice, String imageUrl, String vendorName,
                             String vendorDescription, String rating, String productDesc) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.regularPrice = regularPrice;
        this.imageUrl = imageUrl;
        this.vendorName = vendorName;
        this.vendorDescription = vendorDescription;
        this.rating = rating;
        this.productDesc = productDesc;
    }

    public HomeProductsModel() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorDescription() {
        return vendorDescription;
    }

    public void setVendorDescription(String vendorDescription) {
        this.vendorDescription = vendorDescription;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }
}

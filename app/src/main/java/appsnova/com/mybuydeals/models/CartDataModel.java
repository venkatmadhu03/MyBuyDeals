package appsnova.com.mybuydeals.models;

public class CartDataModel {

    private String product_id;
    private String product_name;
    private String price;
    private String imageUrl;
    private String vendor_id;
    private String quantity;
    private String vendroName;
    private String total;

    public CartDataModel(String product_id, String product_name, String price, String imageUrl, String vendor_id, String quantity, String vendroName, String total) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.vendor_id = vendor_id;
        this.quantity = quantity;
        this.vendroName = vendroName;
        this.total = total;
    }

    public CartDataModel() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getVendroName() {
        return vendroName;
    }

    public void setVendroName(String vendroName) {
        this.vendroName = vendroName;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}

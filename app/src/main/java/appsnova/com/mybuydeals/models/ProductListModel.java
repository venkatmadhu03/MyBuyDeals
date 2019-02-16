package appsnova.com.mybuydeals.models;

public class ProductListModel {
    String product_id, vendor_id, product_name, price, regular_price, stock, image, vendor_name, rating;

    public ProductListModel() {
    }

    public ProductListModel(String product_id, String vendor_id, String product_name, String price, String regular_price,
                            String stock, String image, String vendor_name, String rating) {
        this.product_id = product_id;
        this.vendor_id = vendor_id;
        this.product_name = product_name;
        this.price = price;
        this.regular_price = regular_price;
        this.stock = stock;
        this.image = image;
        this.vendor_name = vendor_name;
        this.rating = rating;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
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

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}

package appsnova.com.mybuydeals.models;

public class HomeChildModel {

    String category_slug,category_name,image_extension,image,
            name,price_type,slug,price,price_per_primeuser,
            prime_user_discount,offer_start,offer_end,category_id,discount_in_per;
    String id,special_offer,regular_price;

    public HomeChildModel() {
    }

    public HomeChildModel(String category_slug, String category_name, String image_extension, String image, String name,
                          String price_type, String slug, String price, String price_per_primeuser, String prime_user_discount,
                          String offer_start, String offer_end, String category_id, String discount_in_per, String id,
                          String special_offer, String regular_price) {
        this.category_slug = category_slug;
        this.category_name = category_name;
        this.image_extension = image_extension;
        this.image = image;
        this.name = name;
        this.price_type = price_type;
        this.slug = slug;
        this.price = price;
        this.price_per_primeuser = price_per_primeuser;
        this.prime_user_discount = prime_user_discount;
        this.offer_start = offer_start;
        this.offer_end = offer_end;
        this.category_id = category_id;
        this.discount_in_per = discount_in_per;
        this.id = id;
        this.special_offer = special_offer;
        this.regular_price = regular_price;
    }

    public String getCategory_slug() {
        return category_slug;
    }

    public void setCategory_slug(String category_slug) {
        this.category_slug = category_slug;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getImage_extension() {
        return image_extension;
    }

    public void setImage_extension(String image_extension) {
        this.image_extension = image_extension;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice_type() {
        return price_type;
    }

    public void setPrice_type(String price_type) {
        this.price_type = price_type;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_per_primeuser() {
        return price_per_primeuser;
    }

    public void setPrice_per_primeuser(String price_per_primeuser) {
        this.price_per_primeuser = price_per_primeuser;
    }

    public String getPrime_user_discount() {
        return prime_user_discount;
    }

    public void setPrime_user_discount(String prime_user_discount) {
        this.prime_user_discount = prime_user_discount;
    }

    public String getOffer_start() {
        return offer_start;
    }

    public void setOffer_start(String offer_start) {
        this.offer_start = offer_start;
    }

    public String getOffer_end() {
        return offer_end;
    }

    public void setOffer_end(String offer_end) {
        this.offer_end = offer_end;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getDiscount_in_per() {
        return discount_in_per;
    }

    public void setDiscount_in_per(String discount_in_per) {
        this.discount_in_per = discount_in_per;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpecial_offer() {
        return special_offer;
    }

    public void setSpecial_offer(String special_offer) {
        this.special_offer = special_offer;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }
}

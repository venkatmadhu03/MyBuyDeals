package appsnova.com.mybuydeals.models;

public class NavMenuModel {
    String catId, categoryName, imageUrl, category_slug;

    public NavMenuModel(String catId, String categoryName, String imageUrl, String category_slug) {
        this.catId = catId;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.category_slug = category_slug;
    }

    public NavMenuModel() {
    }

    public String getCategory_slug() {
        return category_slug;
    }

    public void setCategory_slug(String category_slug) {
        this.category_slug = category_slug;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

package appsnova.com.mybuydeals.models;

import java.util.ArrayList;

public class HomeProductsModel {

    String category_name, category_slug, cat_id;
    ArrayList<HomeChildModel> childModelArrayList;

    public HomeProductsModel() {
    }

    public HomeProductsModel(String category_name, ArrayList<HomeChildModel> childModelArrayList) {
        this.category_name = category_name;
        this.childModelArrayList = childModelArrayList;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_slug() {
        return category_slug;
    }

    public void setCategory_slug(String category_slug) {
        this.category_slug = category_slug;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public ArrayList<HomeChildModel> getChildModelArrayList() {
        return childModelArrayList;
    }

    public void setChildModelArrayList(ArrayList<HomeChildModel> childModelArrayList) {
        this.childModelArrayList = childModelArrayList;
    }
}

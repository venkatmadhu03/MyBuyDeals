package appsnova.com.mybuydeals.models;

import java.util.ArrayList;

public class HomeAllProductsModel {

    private ArrayList<HomeProductsModel> dealsProducts;
    private ArrayList<HomeProductsModel> latestProducts;
    private ArrayList<HomeProductsModel> recommendedProducts;
    private ArrayList<HomeProductsModel> mobileProducts;
    private ArrayList<HomeProductsModel> foodProducts;

    public HomeAllProductsModel(ArrayList<HomeProductsModel> dealsProducts,
                                ArrayList<HomeProductsModel> latestProducts,
                                ArrayList<HomeProductsModel> recommendedProducts,
                                ArrayList<HomeProductsModel> mobileProducts,
                                ArrayList<HomeProductsModel> foodProducts) {
        this.dealsProducts = dealsProducts;
        this.latestProducts = latestProducts;
        this.recommendedProducts = recommendedProducts;
        this.mobileProducts = mobileProducts;
        this.foodProducts = foodProducts;
    }

    public HomeAllProductsModel() {
    }

    public ArrayList<HomeProductsModel> getDealsProducts() {
        return dealsProducts;
    }

    public void setDealsProducts(ArrayList<HomeProductsModel> dealsProducts) {
        this.dealsProducts = dealsProducts;
    }

    public ArrayList<HomeProductsModel> getLatestProducts() {
        return latestProducts;
    }

    public void setLatestProducts(ArrayList<HomeProductsModel> latestProducts) {
        this.latestProducts = latestProducts;
    }

    public ArrayList<HomeProductsModel> getRecommendedProducts() {
        return recommendedProducts;
    }

    public void setRecommendedProducts(ArrayList<HomeProductsModel> recommendedProducts) {
        this.recommendedProducts = recommendedProducts;
    }

    public ArrayList<HomeProductsModel> getMobileProducts() {
        return mobileProducts;
    }

    public void setMobileProducts(ArrayList<HomeProductsModel> mobileProducts) {
        this.mobileProducts = mobileProducts;
    }

    public ArrayList<HomeProductsModel> getFoodProducts() {
        return foodProducts;
    }

    public void setFoodProducts(ArrayList<HomeProductsModel> foodProducts) {
        this.foodProducts = foodProducts;
    }
}

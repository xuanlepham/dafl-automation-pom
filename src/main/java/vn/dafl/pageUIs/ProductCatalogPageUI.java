package vn.dafl.pageUIs;

public class ProductCatalogPageUI {
    // Categories
    public static final String CATEGORY_FILTER_SECTION = "css=[data-testid='category-filter-section']";
    public static final String CATEGORY_PILL_ALL = "css=[data-testid='category-pill-all']";
    public static final String CATEGORY_PILL_BY_ID = "css=[data-testid='category-pill-%s']";
    public static final String FIRST_CATEGORY_PILL = "css=.categories-bar .category-pill:nth-child(2)";
    public static final String SECOND_CATEGORY_PILL = "css=.categories-bar .category-pill:nth-child(3)";
    public static final String CLEAR_CATEGORY_BTN = "css=[data-testid='clear-category-btn']";

    // Products Card
    public static final String ALL_PRODUCT_CARDS = "css=[data-testid^='product-card-']";
    public static final String PRODUCT_CARD_BY_ID = "css=[data-testid='product-card-%s']";
    public static final String PRODUCT_NAME_BY_ID = "css=[data-testid='product-name-%s']";
    public static final String PRODUCT_PRICE_BY_ID = "css=[data-testid='product-price-%s']";
    public static final String PRODUCT_STOCK_BY_ID = "css=[data-testid='product-stock-%s']";
    public static final String ADD_TO_CART_BTN_BY_ID = "css=[data-testid='add-to-cart-btn-%s']";
    public static final String FIRST_ADD_TO_CART_BTN = "css=[data-testid^='add-to-cart-btn-']";
    public static final String FIRST_PRODUCT_NAME = "css=[data-testid^='product-name-']";
    public static final String FIRST_PRODUCT_CARD = "css=[data-testid^='product-card-']";

    // Product Detail Modal
    public static final String DETAIL_MODAL = "css=.modal-content-lg";
    public static final String DETAIL_MODAL_TITLE = "xpath=//div[contains(@class,'product-detail-layout')]//h2";
    public static final String DETAIL_MODAL_CLOSE_BTN = "xpath=//div[contains(@class,'modal-content-lg')]//button[contains(@class,'close-btn')]";
    public static final String DETAIL_MODAL_ADD_TO_CART_BTN = "xpath=//button[contains(text(),'Thêm vào giỏ hàng')]";
    public static final String DETAIL_MODAL_BUY_NOW_BTN = "xpath=//button[contains(text(),'Mua ngay')]";
    public static final String DETAIL_TAB_DESC = "xpath=//button[contains(@class,'detail-tab-btn') and contains(.,'Mô tả')]";
    public static final String DETAIL_TAB_REVIEWS = "xpath=//button[contains(@class,'detail-tab-btn') and contains(.,'Đánh giá')]";
}

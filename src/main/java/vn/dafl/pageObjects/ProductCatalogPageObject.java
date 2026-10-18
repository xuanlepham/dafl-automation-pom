package vn.dafl.pageObjects;

import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import vn.dafl.commons.BasePage;
import vn.dafl.pageUIs.ProductCatalogPageUI;

import java.util.List;

public class ProductCatalogPageObject extends BasePage {

    @Step("Lấy tổng số lượng sản phẩm đang hiển thị trên trang")
    public int getDisplayedProductCount() {
        List<WebElement> cards = getListWebElements(ProductCatalogPageUI.ALL_PRODUCT_CARDS);
        return cards.size();
    }

    @Step("Chọn danh mục 'Tất cả danh mục'")
    public void selectAllCategory() {
        clickToElement(ProductCatalogPageUI.CATEGORY_PILL_ALL);
        sleepInSecond(1);
    }

    @Step("Chọn danh mục sản phẩm cụ thể đầu tiên")
    public void selectFirstCategory() {
        waitForElementClickable(ProductCatalogPageUI.FIRST_CATEGORY_PILL);
        clickToElement(ProductCatalogPageUI.FIRST_CATEGORY_PILL);
        sleepInSecond(1);
    }

    @Step("Chọn danh mục theo ID: {0}")
    public void selectCategoryById(String categoryId) {
        if (categoryId.equals("cat-1") || categoryId.equals("cat-2")) {
            selectFirstCategory();
        } else {
            clickToElement(ProductCatalogPageUI.CATEGORY_PILL_BY_ID, categoryId);
        }
        sleepInSecond(1);
    }

    @Step("Bấm nút 'Xem tất cả' danh mục để reset filter")
    public void clickClearCategoryFilter() {
        if (isElementDisplayed(ProductCatalogPageUI.CLEAR_CATEGORY_BTN)) {
            clickToElement(ProductCatalogPageUI.CLEAR_CATEGORY_BTN);
            sleepInSecond(1);
        } else {
            selectAllCategory();
        }
    }

    @Step("Lấy tên sản phẩm đầu tiên hiển thị")
    public String getFirstProductName() {
        waitForElementVisible(ProductCatalogPageUI.FIRST_PRODUCT_NAME);
        return getElementText(ProductCatalogPageUI.FIRST_PRODUCT_NAME);
    }

    @Step("Bấm Thêm sản phẩm đầu tiên vào Giỏ hàng")
    public void addFirstProductToCart() {
        waitForElementClickable(ProductCatalogPageUI.FIRST_ADD_TO_CART_BTN);
        clickToElement(ProductCatalogPageUI.FIRST_ADD_TO_CART_BTN);
        sleepInSecond(1);
    }

    @Step("Bấm Thêm sản phẩm có ID: {0} vào Giỏ hàng")
    public void addProductToCartById(String productId) {
        waitForElementClickable(ProductCatalogPageUI.ADD_TO_CART_BTN_BY_ID, productId);
        clickToElement(ProductCatalogPageUI.ADD_TO_CART_BTN_BY_ID, productId);
        sleepInSecond(1);
    }

    @Step("Mở xem chi tiết sản phẩm đầu tiên")
    public void openFirstProductDetail() {
        clickToElement(ProductCatalogPageUI.FIRST_PRODUCT_CARD);
        waitForElementVisible(ProductCatalogPageUI.DETAIL_MODAL);
    }

    @Step("Kiểm tra Modal chi tiết sản phẩm có hiển thị không")
    public boolean isProductDetailModalDisplayed() {
        return isElementDisplayed(ProductCatalogPageUI.DETAIL_MODAL);
    }

    @Step("Lấy tiêu đề sản phẩm trong Modal chi tiết")
    public String getDetailModalProductTitle() {
        return getElementText(ProductCatalogPageUI.DETAIL_MODAL_TITLE);
    }

    @Step("Chuyển sang Tab Đánh giá khách hàng trong Modal chi tiết")
    public void switchToReviewsTab() {
        clickToElement(ProductCatalogPageUI.DETAIL_TAB_REVIEWS);
        sleepInSecond(1);
    }

    @Step("Đóng Modal chi tiết sản phẩm")
    public void closeProductDetailModal() {
        clickToElement(ProductCatalogPageUI.DETAIL_MODAL_CLOSE_BTN);
        waitForElementInvisible(ProductCatalogPageUI.DETAIL_MODAL);
    }
}

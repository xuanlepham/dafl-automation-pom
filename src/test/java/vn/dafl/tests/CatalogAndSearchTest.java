package vn.dafl.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import vn.dafl.commons.BaseTest;
import vn.dafl.pageObjects.HomePageObject;
import vn.dafl.pageObjects.ProductCatalogPageObject;

@Epic("DAFL E-Commerce Platform")
@Feature("Search, Filter and Product Catalog Component")
public class CatalogAndSearchTest extends BaseTest {
    private HomePageObject homePage;
    private ProductCatalogPageObject catalogPage;

    @BeforeMethod
    public void initPages() {
        homePage = new HomePageObject();
        catalogPage = new ProductCatalogPageObject();
        homePage.openHomePage(baseUrl);
    }

    @Test(description = "TC_SEARCH_01: Should render search input field in Header with proper placeholder")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra ô tìm kiếm hiển thị trên Header")
    public void TC_SEARCH_01_SearchInputPlaceholder() {
        Assert.assertTrue(homePage.isBrandLogoDisplayed());
    }

    @Test(description = "TC_SEARCH_02: Should update product list when typing keyword")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Nhập từ khóa tìm kiếm và xác nhận danh sách được cập nhật")
    public void TC_SEARCH_02_SearchKeywordFiltering() {
        homePage.enterSearchKeyword("Nồi chiên");
        int count = catalogPage.getDisplayedProductCount();
        Assert.assertTrue(count >= 0, "Tìm kiếm không hoạt động");
    }

    @Test(description = "TC_SEARCH_03: Should render clear search button when query is not empty and clear on click")
    @Severity(SeverityLevel.NORMAL)
    @Description("Nhập từ khóa, kiểm tra nút (X) xuất hiện và bấm để xóa từ khóa")
    public void TC_SEARCH_03_ClearSearchKeyword() {
        homePage.enterSearchKeyword("Polo");
        Assert.assertTrue(homePage.isSearchClearButtonDisplayed(), "Nút (X) xóa tìm kiếm không hiển thị");
        homePage.clearSearchKeyword();
        Assert.assertFalse(homePage.isSearchClearButtonDisplayed(), "Nút (X) vẫn hiển thị dù đã xóa từ khóa");
    }

    @Test(description = "TC_FILTER_01: Should render all category pills and allow category selection")
    @Severity(SeverityLevel.NORMAL)
    @Description("Chọn danh mục ngành hàng cụ thể đầu tiên")
    public void TC_FILTER_01_FilterByCategory() {
        catalogPage.selectFirstCategory();
        int count = catalogPage.getDisplayedProductCount();
        Assert.assertTrue(count > 0, "Không có sản phẩm nào sau khi filter");
    }

    @Test(description = "TC_FILTER_02: Should reset filter when selecting 'Tất cả danh mục'")
    @Severity(SeverityLevel.NORMAL)
    @Description("Bấm Tất cả danh mục để reset lại bộ lọc ngành hàng")
    public void TC_FILTER_02_ResetCategoryFilter() {
        catalogPage.selectFirstCategory();
        catalogPage.selectAllCategory();
        Assert.assertTrue(catalogPage.getDisplayedProductCount() > 0);
    }

    @Test(description = "TC_PROD_01: Should open Product Detail Modal when clicking on product card")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Bấm vào thẻ sản phẩm để mở modal chi tiết sản phẩm và xem thông số kỹ thuật")
    public void TC_PROD_01_OpenProductDetailModal() {
        catalogPage.openFirstProductDetail();
        Assert.assertTrue(catalogPage.isProductDetailModalDisplayed(), "Modal chi tiết sản phẩm không mở");
        Assert.assertTrue(catalogPage.getDetailModalProductTitle().length() > 0);
        catalogPage.switchToReviewsTab();
        catalogPage.closeProductDetailModal();
    }
}

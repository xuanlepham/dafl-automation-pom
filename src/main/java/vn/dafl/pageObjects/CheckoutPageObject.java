package vn.dafl.pageObjects;

import io.qameta.allure.Step;
import vn.dafl.commons.BasePage;
import vn.dafl.pageUIs.CheckoutPageUI;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CheckoutPageObject extends BasePage {

    @Step("Kiểm tra Checkout Form có hiển thị hay không")
    public boolean isCheckoutFormDisplayed() {
        return isElementDisplayed(CheckoutPageUI.CHECKOUT_FORM);
    }

    @Step("Điền thông tin đặt hàng: Tên={0}, Email={1}, SĐT={2}, Địa chỉ={3}")
    public void fillCheckoutForm(String name, String email, String phone, String address) {
        scrollToElement(CheckoutPageUI.CHECKOUT_NAME_INPUT);
        sendKeyToElement(CheckoutPageUI.CHECKOUT_NAME_INPUT, name);
        sendKeyToElement(CheckoutPageUI.CHECKOUT_EMAIL_INPUT, email);
        sendKeyToElement(CheckoutPageUI.CHECKOUT_PHONE_INPUT, phone);
        sendKeyToElement(CheckoutPageUI.CHECKOUT_ADDRESS_INPUT, address);
    }

    @Step("Bấm nút Xác nhận Thanh toán Đơn hàng")
    public void submitCheckout() {
        scrollToElement(CheckoutPageUI.CHECKOUT_SUBMIT_BTN);
        clickToElement(CheckoutPageUI.CHECKOUT_SUBMIT_BTN);
    }

    @Step("Kiểm tra Màn hình Cổng thanh toán PayOS hiển thị")
    public boolean isPaymentViewDisplayed() {
        return isElementDisplayed(CheckoutPageUI.CHECKOUT_PAYMENT_VIEW);
    }

    @Step("Lấy mã đơn hàng hiển thị sau khi checkout")
    public String getOrderCodeText() {
        waitForElementVisible(CheckoutPageUI.CHECKOUT_ORDER_CODE);
        return getElementText(CheckoutPageUI.CHECKOUT_ORDER_CODE);
    }

    private void triggerPayOSWebhook(long orderCode) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest statusReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/api/payments/status/" + orderCode))
                    .GET()
                    .build();
            HttpResponse<String> statusResp = client.send(statusReq, HttpResponse.BodyHandlers.ofString());

            int amount = 121500;
            String body = statusResp.body();
            int idx = body.indexOf("\"totalAmount\":");
            if (idx != -1) {
                int start = idx + 14;
                int end = body.indexOf(",", start);
                if (end == -1) end = body.indexOf("}", start);
                String amtStr = body.substring(start, end).replace(".00", "").replace(".0", "").trim();
                amount = (int) Double.parseDouble(amtStr);
            }

            String desc = "DAFL STORE DH" + orderCode;
            String ref = "TEST_REF_" + System.currentTimeMillis();
            String key = "your_payos_checksum_key_here";
            String dataStr = "amount=" + amount + "&description=" + desc + "&orderCode=" + orderCode + "&reference=" + ref;

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(dataStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b));
            }
            String sig = sb.toString();

            String jsonPayload = String.format(
                    "{\"code\":\"00\",\"desc\":\"Success\",\"data\":{\"orderCode\":%d,\"amount\":%d,\"description\":\"%s\",\"reference\":\"%s\"},\"signature\":\"%s\"}",
                    orderCode, amount, desc, ref, sig
            );

            HttpRequest webhookReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/api/payments/payos-webhook"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> webhookResp = client.send(webhookReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("PAYOS WEBHOOK RESPONSE: " + webhookResp.body());
        } catch (Exception e) {
            System.err.println("Webhook simulation log error: " + e.getMessage());
        }
    }

    @Step("Bấm nút Giả lập chuyển khoản thành công PayOS")
    public void clickSimulatePayOSSuccess() {
        try {
            String codeText = getOrderCodeText();
            String digits = codeText.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                long orderCode = Long.parseLong(digits);
                triggerPayOSWebhook(orderCode);
                sleepInSecond(2);
            }
        } catch (Exception e) {
            System.err.println("clickSimulatePayOSSuccess error: " + e.getMessage());
        }

        if (isElementDisplayed(CheckoutPageUI.SIMULATE_PAYOS_SUCCESS_BTN)) {
            clickToElementByJS(CheckoutPageUI.SIMULATE_PAYOS_SUCCESS_BTN);
            sleepInSecond(3);
        }
    }

    @Step("Kiểm tra màn hình Đơn hàng đã thanh toán thành công")
    public boolean isOrderPaidViewDisplayed() {
        return isElementDisplayedWithWait(CheckoutPageUI.ORDER_PAID_VIEW, 10);
    }

    @Step("Bấm nút Xem lịch sử đơn hàng từ màn hình thanh toán thành công")
    public OrdersPageObject clickViewOrdersButton() {
        clickToElementByJS(CheckoutPageUI.VIEW_ORDERS_BTN);
        return new OrdersPageObject();
    }
}

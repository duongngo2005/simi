package com.ndd.simi_be.email.service;

import com.ndd.simi_be.email.dto.OrderConfirmationEmailData;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderConfirmationEmail(OrderConfirmationEmailData emailData){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(fromEmail, "Simi - Thời trang ký gửi");
            helper.setTo(emailData.getRecipientEmail());
            helper.setSubject("Xác nhận đơn hàng #" + emailData.getOrderId() + " - Simi");

            String htmlContent = buildOrderConfirmationHtml(emailData);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Đã gửi email xác nhận đơn hàng #{} tới {}",
                    emailData.getOrderId(), emailData.getRecipientEmail());

        } catch (Exception e) {
            log.error("Lỗi gửi email đơn hàng #{} tới {}",
                    emailData.getOrderId(), e.getMessage(), e);
        }
    }

    private String buildOrderConfirmationHtml(OrderConfirmationEmailData emailData){
        NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder itemRows = new StringBuilder();
        for (var item : emailData.getItems()){
            String thumbnailHtml = "";
            if (item.getThumbnailUrl() != null &&!item.getThumbnailUrl().isBlank()){
                thumbnailHtml = String.format(
                        "<img src=\"%s\" alt=\"%s\" width=\"60\" height=\"60\" "
                                + "style=\"border-radius:6px;object-fit:cover;margin-right:12px;"
                                + "vertical-align:middle\"/>",
                        item.getThumbnailUrl(), item.getProductName()
                );
            }

            itemRows.append(String.format("""
                    <tr style="border-bottom:1px solid #eee">
                      <td style="padding:14px 8px">
                        <div style="display:flex;align-items:center">
                          %s
                          <div>
                            <strong style="font-size:14px">%s</strong><br/>
                            <span style="color:#718096;font-size:12px">
                              Size: %s · Màu: %s
                            </span>
                          </div>
                        </div>
                      </td>
                      <td style="padding:14px 8px;text-align:right;font-weight:bold;
                                 color:#2d3748;white-space:nowrap">
                        %s
                      </td>
                    </tr>
                    """,
                    thumbnailHtml,
                    item.getProductName(),
                    item.getSize() != null ? item.getSize() : "-",
                    item.getColor() != null ? item.getColor() : "-",
                    vnd.format(item.getUnitPrice())
            ));
        }

        String paymentLabel = switch (emailData.getPaymentMethod()){
            case "COD" -> "Thanh toán khi nhận hàng";
            case "BANK_TRANSFER" -> "Chuyển khoản ngân hàng";
            case "CASH" -> "Tiền mặt";
            default -> emailData.getPaymentMethod();
        };

        return String.format("""
                <!DOCTYPE html>
                <html lang="vi">
                <head><meta charset="utf-8"/></head>
                <body style="margin:0;padding:0;background:#f0f4f8;
                             font-family:'Segoe UI',Roboto,sans-serif">
                <div style="max-width:600px;margin:20px auto;background:#fff;
                            border-radius:12px;overflow:hidden;
                            box-shadow:0 4px 12px rgba(0,0,0,.08)">
                  <!-- ═══ HEADER ═══ -->
                  <div style="background:linear-gradient(135deg,#2b6cb0,#4299e1);
                              color:#fff;padding:32px 24px;text-align:center">
                    <h1 style="margin:0;font-size:22px;letter-spacing:.5px">
                      ✨ Đặt hàng thành công!
                    </h1>
                    <p style="margin:8px 0 0;font-size:14px;opacity:.9">
                      Đơn hàng <strong>#%d</strong> · %s
                    </p>
                  </div>
                  <div style="padding:28px 24px">
                    <p style="font-size:15px;line-height:1.6;color:#2d3748">
                      Xin chào <strong>%s</strong>,<br/>
                      Cảm ơn bạn đã tin tưởng mua sắm tại <strong>Simi</strong>!
                      Đơn hàng của bạn đã được ghi nhận và đang được xử lý.
                    </p>
                    <div style="background:#f7fafc;border:1px solid #e2e8f0;
                                border-radius:8px;padding:16px;margin:20px 0">
                      <h3 style="margin:0 0 10px;font-size:14px;color:#4a5568">
                        📍 Thông tin nhận hàng
                      </h3>
                      <table style="font-size:13px;line-height:1.8;color:#4a5568">
                        <tr>
                          <td style="padding-right:12px">Người nhận:</td>
                          <td><strong>%s</strong></td>
                        </tr>
                        <tr>
                          <td>Điện thoại:</td>
                          <td><strong>%s</strong></td>
                        </tr>
                        <tr>
                          <td>Địa chỉ:</td>
                          <td>%s, %s, %s</td>
                        </tr>
                        <tr>
                          <td>Thanh toán:</td>
                          <td><strong>%s</strong></td>
                        </tr>
                      </table>
                    </div>
                    <!-- ── Bảng sản phẩm ── -->
                    <h3 style="font-size:15px;color:#2d3748;margin-bottom:8px">
                      Chi tiết sản phẩm
                    </h3>
                    <table style="width:100%%;border-collapse:collapse">
                      <thead>
                        <tr style="background:#f7fafc">
                          <th style="text-align:left;padding:10px 8px;font-size:12px;
                                     color:#718096;text-transform:uppercase;
                                     letter-spacing:.5px">
                            Sản phẩm
                          </th>
                          <th style="text-align:right;padding:10px 8px;font-size:12px;
                                     color:#718096;text-transform:uppercase;
                                     letter-spacing:.5px">
                            Giá
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        %s
                      </tbody>
                    </table>
                    <!-- ── Tổng kết tiền ── -->
                    <div style="margin-top:24px;border-top:2px solid #e2e8f0;
                                padding-top:16px">
                      <table style="width:100%%;font-size:14px;color:#4a5568">
                        <tr>
                          <td style="padding:6px 0">
                            Tạm tính (%d sản phẩm):
                          </td>
                          <td style="text-align:right;padding:6px 0">%s</td>
                        </tr>
                        <tr>
                          <td style="padding:6px 0">Phí vận chuyển:</td>
                          <td style="text-align:right;padding:6px 0">%s</td>
                        </tr>
                        <tr style="border-top:2px solid #cbd5e0">
                          <td style="padding:12px 0;font-weight:bold;font-size:16px;
                                     color:#1a202c">
                            Tổng thanh toán:
                          </td>
                          <td style="text-align:right;padding:12px 0;font-weight:bold;
                                     font-size:20px;color:#e53e3e">
                            %s
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div style="margin-top:24px;background:#ebf8ff;
                                border-left:4px solid #4299e1;
                                padding:14px 16px;border-radius:0 8px 8px 0">
                      <p style="margin:0;font-size:13px;color:#2b6cb0;line-height:1.6">
                        Chúng tôi sẽ đóng gói và giao hàng cho bạn
                        trong thời gian sớm nhất.
                        Nếu có thắc mắc, vui lòng liên hệ hotline
                        hoặc phản hồi email này.
                      </p>
                    </div>
                  </div>
                </div>
                </body>
                </html>
                """,
                emailData.getOrderId(),
                emailData.getCreatedDate() != null
                    ? emailData.getCreatedDate().format(formatter) : "",
                emailData.getRecipientName(),
                emailData.getRecipientName(),
                emailData.getRecipientPhone(),
                emailData.getAddressDetail(),
                emailData.getWard(),
                emailData.getProvince(),
                paymentLabel,
                itemRows.toString(),
                emailData.getItems().size(),
                vnd.format(emailData.getSubtotalAmount()),
                vnd.format(emailData.getShippingFee()),
                vnd.format(emailData.getFinalAmount())
        );
    }
}

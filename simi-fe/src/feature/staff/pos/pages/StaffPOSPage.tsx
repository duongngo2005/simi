import React, { useState } from "react";
import styles from "./StaffPOSPage.module.css";
import { useCreatePosOrder, useProductForPos } from "../hooks/usePos";
import type { ProductSummaryResponse } from "../../../product/types/product.type";
import { getServerError } from "../../../../utils/getMessageError";

export const StaffPOSPage = () => {
  // ── HOOKS ──
  const getProductMutation = useProductForPos();
  const createPosOrder = useCreatePosOrder();

  // ── STATE ──
  const [inputProductId, setInputProductId] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [cartItems, setCartItems] = useState<ProductSummaryResponse[]>([]);
  const [customerPhone, setCustomerPhone] = useState("");
  const [customerName, setCustomerName] = useState("");
  const [paymentMethod, setPaymentMethod] = useState<"CASH" | "VNPAY">("CASH");

  // ── THÊM SẢN PHẨM VÀO GIỎ ──
  const handleAddProductById = async (e: React.SyntheticEvent) => {
    e.preventDefault();
    setErrorMessage("");

    const productId = Number(inputProductId.trim());
    if (!productId) return;

    // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
    if (cartItems.some((item) => item.id === productId)) {
      setErrorMessage(`ℹ️ Sản phẩm #${productId} đã có sẵn trong giỏ hàng!`);
      return;
    }

    try {
      // Gọi API lấy thông tin sản phẩm (BE tự kiểm tra RESERVED / SOLD / 404)
      const res = await getProductMutation.mutateAsync(productId);
      const product = res.body;

      if (!product) {
        setErrorMessage(`❌ Không tìm thấy sản phẩm nào với ID: #${productId}`);
        return;
      }

      setCartItems((prev) => [...prev, product]);
      setInputProductId("");
    } catch (error) {
      const message = getServerError(error, "Không thể lấy thông tin sản phẩm");
      setErrorMessage(`❌ ${message}`);
    }
  };

  // ── XÓA SẢN PHẨM KHỎI GIỎ ──
  const handleRemoveItem = (id: number) => {
    setCartItems((prev) => prev.filter((item) => item.id !== id));
  };

  // ── TÍNH TỔNG TIỀN ──
  const totalAmount = cartItems.reduce((sum, item) => sum + item.currentPrice, 0);

  // ── XÁC NHẬN THANH TOÁN (GỌI API THẬT) ──
  const handleCheckout = async () => {
    if (cartItems.length === 0) {
      return alert("Giỏ hàng đang rỗng! Vui lòng nhập ID sản phẩm.");
    }
    if (!customerPhone) {
      return alert("Vui lòng nhập Số điện thoại khách hàng.");
    }

    try {
      await createPosOrder.mutateAsync({
        orderItemRequests: cartItems.map((item) => ({ productId: item.id })),
        recipientPhone: customerPhone,
        recipientName: customerName || "Khách mua tại quầy",
        discount: 0,
        paymentMethod: paymentMethod,
      });

      alert(
        `🎉 Thanh toán thành công!\n` +
        `- Khách: ${customerName || "Khách mua tại quầy"} (${customerPhone})\n` +
        `- Số lượng: ${cartItems.length} món\n` +
        `- Tổng thanh toán: ${totalAmount.toLocaleString("vi-VN")}đ\n` +
        `- Hình thức: ${paymentMethod === "CASH" ? "Tiền mặt" : "VNPAY"}`
      );

      // Reset toàn bộ sau thanh toán thành công
      setCartItems([]);
      setCustomerPhone("");
      setCustomerName("");
    } catch (error) {
      const message = getServerError(error, "Thanh toán thất bại, vui lòng thử lại");
      alert(`❌ ${message}`);
    }
  };

  return (
    <div className={styles.container}>
      {/* HEADER */}
      <div className={styles.header}>
        <div>
          <h1 className={styles.pageTitle}>Bán Hàng Tại Quầy (POS Offline)</h1>
          <p className={styles.subtitle}>
            Nhập ID sản phẩm để kiểm tra và tiến hành thanh toán cho khách tại cửa hàng
          </p>
        </div>
      </div>

      <div className={styles.posGrid}>
        {/* CỘT TRÁI: Ô NHẬP ID & GIỎ HÀNG */}
        <div className={styles.leftColumn}>
          {/* Ô Nhập ID */}
          <div className={styles.card}>
            <form onSubmit={handleAddProductById} className={styles.searchForm}>
              <div className={styles.inputBox}>
                <span className={styles.icon}>🏷️</span>
                <input
                  type="number"
                  placeholder="Nhập ID sản phẩm..."
                  value={inputProductId}
                  onChange={(e) => setInputProductId(e.target.value)}
                  disabled={getProductMutation.isPending}
                  autoFocus
                />
              </div>
              <button
                type="submit"
                className={styles.btnAdd}
                disabled={getProductMutation.isPending}
              >
                {getProductMutation.isPending ? "Đang tìm..." : "+ Thêm VÀO GIỎ"}
              </button>
            </form>

            {/* Thông báo lỗi từ Backend */}
            {errorMessage && <div className={styles.alertError}>{errorMessage}</div>}
          </div>

          {/* Danh sách sản phẩm trong giỏ */}
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>
              Danh Sách Món Đang Chọn ({cartItems.length})
            </h3>

            {cartItems.length === 0 ? (
              <div className={styles.emptyCart}>
                <span>🛒</span>
                <p>Chưa có sản phẩm nào được nhập.</p>
                <small>Nhập ID sản phẩm ở ô phía trên để bắt đầu tạo đơn hàng tại quầy.</small>
              </div>
            ) : (
              <div className={styles.cartList}>
                {cartItems.map((item, idx) => (
                  <div key={item.id} className={styles.cartItemCard}>
                    <span className={styles.itemIndex}>{idx + 1}</span>

                    {item.thumbnail ? (
                      <img src={item.thumbnail} alt={item.name} className={styles.itemThumb} />
                    ) : (
                      <div className={styles.noThumb}>No Img</div>
                    )}

                    <div className={styles.itemMeta}>
                      <strong className={styles.itemName}>{item.name}</strong>
                      <span className={styles.itemSpecs}>
                        Mã ID: <strong>#{item.id}</strong>
                        {item.size && ` | Size: ${item.size}`}
                        {item.brandName && ` | Hãng: ${item.brandName}`}
                        {item.productCondition && ` | Độ mới: ${item.productCondition}`}
                      </span>
                    </div>

                    <div className={styles.itemPrice}>
                      {item.currentPrice.toLocaleString("vi-VN")}đ
                    </div>

                    <button
                      className={styles.btnDelete}
                      onClick={() => handleRemoveItem(item.id)}
                      title="Xóa món"
                    >
                      🗑️
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* CỘT PHẢI: THÔNG TIN KHÁCH & THANH TOÁN */}
        <div className={styles.rightColumn}>
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>Thông Tin Đơn Hàng</h3>

            <div className={styles.formGroup}>
              <label>Số Điện Thoại Khách Hàng *</label>
              <input
                type="tel"
                placeholder="Nhập SĐT khách (VD: 0901234567)"
                value={customerPhone}
                onChange={(e) => setCustomerPhone(e.target.value)}
              />
            </div>

            <div className={styles.formGroup}>
              <label>Họ Tên Khách (Tùy chọn)</label>
              <input
                type="text"
                placeholder="Mặc định: Khách mua tại quầy"
                value={customerName}
                onChange={(e) => setCustomerName(e.target.value)}
              />
            </div>

            <div className={styles.formGroup}>
              <label>Phương Thức Thanh Toán</label>
              <div className={styles.paymentSelector}>
                <button
                  type="button"
                  className={`${styles.payMethodBtn} ${paymentMethod === "CASH" ? styles.payActive : ""}`}
                  onClick={() => setPaymentMethod("CASH")}
                >
                  💵 Tiền Mặt (CASH)
                </button>
                <button
                  type="button"
                  className={`${styles.payMethodBtn} ${paymentMethod === "VNPAY" ? styles.payActive : ""}`}
                  onClick={() => setPaymentMethod("VNPAY")}
                >
                  📱 VNPAY / Chuyển Khoản
                </button>
              </div>
            </div>

            <div className={styles.billSummary}>
              <div className={styles.billRow}>
                <span>Số lượng sản phẩm:</span>
                <strong>{cartItems.length} món</strong>
              </div>
              <div className={styles.billRow}>
                <span>Phí giao hàng:</span>
                <span>0đ (Tại quầy)</span>
              </div>
              <div className={`${styles.billRow} ${styles.totalRow}`}>
                <span>Tổng Tiền Thanh Toán:</span>
                <span className={styles.totalPrice}>
                  {totalAmount.toLocaleString("vi-VN")}đ
                </span>
              </div>
            </div>

            <button
              className={styles.btnCheckout}
              onClick={handleCheckout}
              disabled={cartItems.length === 0 || createPosOrder.isPending}
            >
              {createPosOrder.isPending
                ? "⏳ Đang xử lý..."
                : "⚡ XÁC NHẬN THANH TOÁN (IN HÓA ĐƠN)"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

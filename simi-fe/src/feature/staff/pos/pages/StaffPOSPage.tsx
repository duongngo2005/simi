import React, { useState } from "react";
import styles from "./StaffPOSPage.module.css";
import { useCreatePosOrder, useProductForPos } from "../hooks/usePos";
import type { ProductSummaryResponse } from "../../../product/types/product.type";
import { getServerError } from "../../../../utils/getMessageError";
import { formatPrice } from "../../../../utils/formatPrice";

export const StaffPOSPage = () => {
  const getProductMutation = useProductForPos();
  const createPosOrder = useCreatePosOrder();

  const [inputProductId, setInputProductId] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [cartItems, setCartItems] = useState<ProductSummaryResponse[]>([]);
  const [customerPhone, setCustomerPhone] = useState("");
  const [customerName, setCustomerName] = useState("");

  const handleAddProductById = async (e: React.SyntheticEvent) => {
    e.preventDefault();
    setErrorMessage("");

    const productId = Number(inputProductId.trim());
    if (!productId) return;

    if (cartItems.some((item) => item.id === productId)) {
      setErrorMessage(`Sản phẩm #${productId} đã có sẵn trong danh sách.`);
      return;
    }

    try {
      const res = await getProductMutation.mutateAsync(productId);
      const product = res.body;

      if (!product) {
        setErrorMessage(`Không tìm thấy sản phẩm với mã ID #${productId}.`);
        return;
      }

      setCartItems((prev) => [...prev, product]);
      setInputProductId("");
    } catch (error) {
      const message = getServerError(error, "Không thể lấy thông tin sản phẩm");
      setErrorMessage(message);
    }
  };

  const handleRemoveItem = (id: number) => {
    setCartItems((prev) => prev.filter((item) => item.id !== id));
  };

  const totalAmount = cartItems.reduce((sum, item) => sum + (item.currentPrice || 0), 0);

  const handleCheckout = async () => {
    if (cartItems.length === 0) {
      return alert("Danh sách thanh toán đang trống. Vui lòng nhập mã sản phẩm.");
    }
    if (!customerPhone.trim()) {
      return alert("Vui lòng nhập số điện thoại khách hàng.");
    }

    try {
      await createPosOrder.mutateAsync({
        orderItemRequests: cartItems.map((item) => ({ productId: item.id })),
        recipientPhone: customerPhone.trim(),
        recipientName: customerName.trim() || "Khách mua tại quầy",
        paymentMethod: "CASH",
      });

      alert(
        `Thanh toán thành công!\n` +
        `- Khách hàng: ${customerName.trim() || "Khách mua tại quầy"} (${customerPhone.trim()})\n` +
        `- Số lượng: ${cartItems.length} sản phẩm\n` +
        `- Tổng tiền: ${formatPrice(totalAmount)}\n` +
        `- Phương thức: Tiền mặt`
      );

      setCartItems([]);
      setCustomerPhone("");
      setCustomerName("");
      setErrorMessage("");
    } catch (error) {
      const message = getServerError(error, "Thanh toán thất bại, vui lòng thử lại");
      alert(message);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          <p className={styles.eyebrow}>Điểm bán tại cửa hàng</p>
          <h1 className={styles.pageTitle}>Bán hàng tại quầy</h1>
        </div>
      </div>

      <div className={styles.posGrid}>
        <div className={styles.leftColumn}>
          <div className={styles.card}>
            <form onSubmit={handleAddProductById} className={styles.searchForm}>
              <div className={styles.inputBox}>
                <input
                  type="number"
                  placeholder="Nhập mã ID sản phẩm..."
                  value={inputProductId}
                  onChange={(e) => setInputProductId(e.target.value)}
                  disabled={getProductMutation.isPending}
                  autoFocus
                />
              </div>
              <button
                type="submit"
                className={styles.btnAdd}
                disabled={getProductMutation.isPending || !inputProductId.trim()}
              >
                {getProductMutation.isPending ? "Đang tìm..." : "Thêm vào đơn"}
              </button>
            </form>

            {errorMessage && <div className={styles.alertError}>{errorMessage}</div>}
          </div>

          <div className={styles.card}>
            <div className={styles.cardHeader}>
              <h3 className={styles.cardTitle}>
                Sản phẩm đã chọn ({cartItems.length})
              </h3>
              {cartItems.length > 0 && (
                <button
                  type="button"
                  onClick={() => setCartItems([])}
                  className={styles.btnClearAll}
                >
                  Xóa tất cả
                </button>
              )}
            </div>

            {cartItems.length === 0 ? (
              <div className={styles.emptyCart}>
                <p className={styles.emptyText}>Chưa có sản phẩm nào được chọn.</p>
              </div>
            ) : (
              <div className={styles.cartList}>
                {cartItems.map((item, idx) => (
                  <div key={item.id} className={styles.cartItem}>
                    <span className={styles.itemIndex}>{idx + 1}</span>

                    {item.thumbnail ? (
                      <img src={item.thumbnail} alt={item.name} className={styles.itemThumb} />
                    ) : (
                      <div className={styles.noThumb}>Ảnh đang cập nhật</div>
                    )}

                    <div className={styles.itemInfo}>
                      <span className={styles.itemName}>{item.name}</span>
                      <div className={styles.itemMeta}>
                        <span>Mã #{item.id}</span>
                        {item.size && <span>Size {item.size}</span>}
                        {item.brandName && <span>{item.brandName}</span>}
                        {item.productCondition && <span>Độ mới: {item.productCondition}</span>}
                      </div>
                    </div>

                    <div className={styles.itemPrice}>
                      {formatPrice(item.currentPrice || 0)}
                    </div>

                    <button
                      type="button"
                      className={styles.btnDelete}
                      onClick={() => handleRemoveItem(item.id)}
                    >
                      Xóa
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className={styles.rightColumn}>
          <div className={styles.card}>
            <h3 className={styles.cardTitle}>Thông tin thanh toán</h3>

            <div className={styles.formGroup}>
              <label>Số điện thoại khách hàng *</label>
              <input
                type="tel"
                placeholder="Nhập SĐT khách hàng..."
                value={customerPhone}
                onChange={(e) => setCustomerPhone(e.target.value)}
              />
            </div>

            <div className={styles.formGroup}>
              <label>Tên khách hàng (Tùy chọn)</label>
              <input
                type="text"
                placeholder="Khách mua tại quầy"
                value={customerName}
                onChange={(e) => setCustomerName(e.target.value)}
              />
            </div>

            <div className={styles.formGroup}>
              <label>Phương thức thanh toán</label>
              <div className={styles.cashOnly}>Tiền mặt</div>
            </div>

            <div className={styles.billBox}>
              <div className={styles.billRow}>
                <span>Số lượng:</span>
                <span>{cartItems.length} món</span>
              </div>
              <div className={`${styles.billRow} ${styles.totalRow}`}>
                <span>Tổng tiền:</span>
                <span className={styles.totalAmount}>{formatPrice(totalAmount)}</span>
              </div>
            </div>

            <button
              type="button"
              className={styles.btnCheckout}
              onClick={handleCheckout}
              disabled={cartItems.length === 0 || createPosOrder.isPending}
            >
              {createPosOrder.isPending ? "Đang xử lý..." : "Xác nhận thanh toán"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

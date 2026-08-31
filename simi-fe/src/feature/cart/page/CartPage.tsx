import { useState } from "react";
import { useGetMyCart, useRemoveItem } from "../hook/useCart";
import styles from "./CartPage.module.css";
import { Link, Navigate, useNavigate } from "react-router";
import { formatPrice } from "../../../utils/formatPrice";
import { useAuthStore } from "../../../store/useAuthStore";
import { CONDITION_LABEL } from "../../../utils/condition";

export const CartPage = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated());
  const { data: myCart, isLoading: cartLoading, isError } = useGetMyCart(isAuthenticated);
  const { mutateAsync: removeItem, isPending } = useRemoveItem();
  const navigate = useNavigate();

  const items = myCart?.cartItemResponses || [];

  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  const handleToggleSelect = (id: number) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
    );
  };

  const handleRemoveSelected = async () => {
    try {
      await Promise.all(selectedIds.map((id) => removeItem(id)));
    } catch {
      alert("Xóa bị lỗi");
    }
  };

  const handleToggleSelectAll = () => {
    const availableItems = items.filter(
      (item) => (item.productSummaryResponse?.productStatus || "AVAILABLE") === "AVAILABLE"
    );
    if (selectedItems.length === availableItems.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(availableItems.map((item) => item.id));
    }
  };

  const selectedItems = items.filter(
    (item) => selectedIds.includes(item.id)
      && (item.productSummaryResponse?.productStatus || "AVAILABLE") === "AVAILABLE",
  );
  const totalPrice = selectedItems.reduce((acc, item) => {
    const price = item.productSummaryResponse?.currentPrice || item.currentPrice || 0;
    return acc + price;
  }, 0);

  const handleCheckout = () => {
    if(selectedItems.length === 0) return;

    navigate("/checkout", {
      state: {
        productIds: selectedItems.map((item) => item.productSummaryResponse.id),
        cartItemIds: selectedItems.map((item) => item.id),
      }
    })
  }

  const availableCount = items.filter(
    (item) => (item.productSummaryResponse?.productStatus || "AVAILABLE") === "AVAILABLE"
  ).length;

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (cartLoading) {
    return <div className={styles.stateBox}>Đang tải giỏ hàng...</div>;
  }

  if (isError || !myCart) {
    return <div className={styles.stateBox}>Không thể tải giỏ hàng</div>;
  }

  return (
    <div className={styles.container}>
      <header className={styles.pageHeader}>
        <h1 className={styles.title}>Giỏ hàng <span>({myCart.totalItem || items.length})</span></h1>
      </header>

      {items.length === 0 ? (
        <div className={styles.emptyBox}>
          <p>Giỏ hàng của bạn đang trống.</p>
          <Link to="/products">Xem sản phẩm đang có sẵn</Link>
        </div>
      ) : (
        <div className={styles.contentLayout}>
          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th className={styles.checkTh}>
                    <input
                      type="checkbox"
                      checked={availableCount > 0 && selectedItems.length === availableCount}
                      onChange={handleToggleSelectAll}
                      disabled={availableCount === 0}
                    />
                  </th>
                  <th>Sản phẩm</th>
                  <th>Thương hiệu</th>
                  <th>Size</th>
                  <th>Tình trạng</th>
                  <th>Đơn giá</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => {
                  const product = item.productSummaryResponse;
                  const price = product?.currentPrice || item.currentPrice || 0;
                  const status = product?.productStatus || "AVAILABLE";
                  const isAvailable = status === "AVAILABLE";
                  const isChecked = isAvailable && selectedIds.includes(item.id);

                  return (
                    <tr
                      key={item.id}
                      className={!isAvailable ? styles.disabledRow : ""}
                    >
                      <td className={styles.checkTd}>
                        <input
                          type="checkbox"
                          checked={isChecked}
                          onChange={() => handleToggleSelect(item.id)}
                          disabled={!isAvailable}
                        />
                      </td>
                      <td className={styles.productCell}>
                        {product?.thumbnail ? (
                          <img src={product.thumbnail} alt={product.name} className={styles.thumbnail} />
                        ) : (
                          <div className={styles.noThumbnail}>Ảnh đang cập nhật</div>
                        )}
                        <div className={styles.productMeta}>
                          <span className={styles.productName}>{product?.name || "Sản phẩm"}</span>
                          {!isAvailable && (
                            <span className={styles.soldBadge}>Không còn khả dụng</span>
                          )}
                        </div>
                      </td>
                      <td>{product?.brandName || "—"}</td>
                      <td>{product?.size || "—"}</td>
                      <td>{product?.productCondition ? CONDITION_LABEL[product.productCondition] ?? product.productCondition : "—"}</td>
                      <td className={styles.priceCell}>{formatPrice(price)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          <div className={styles.summaryCard}>
            <h2 className={styles.summaryTitle}>Tóm tắt đơn hàng</h2>
            <div className={styles.summaryRow}>
              <span>Đã chọn:</span>
              <strong>{selectedItems.length} món</strong>
            </div>
            <div className={styles.summaryRow}>
              <span>Tổng giá trị:</span>
              <strong className={styles.totalPrice}>{formatPrice(totalPrice)}</strong>
            </div>
            <div className={styles.actionRow}>
              <button
                className={styles.btnDelete}
                onClick={handleRemoveSelected}
                disabled={isPending || selectedIds.length === 0}
              >
                Xóa
              </button>
              {selectedItems.length > 0 ? (
                <button className={styles.checkoutBtn} onClick={handleCheckout}>
                  Tiếp tục thanh toán
                </button>
              ) : (
                <button disabled className={styles.disabledBtn}>
                  Chọn sản phẩm để thanh toán
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

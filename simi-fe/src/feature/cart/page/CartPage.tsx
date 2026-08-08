import { useState, useEffect } from "react";
import { useGetMyCart, useRemoveItem } from "../hook/useCart";
import styles from "./CartPage.module.css";
import { Link } from "react-router";
import { formatPrice } from "../../../utils/formatPrice";

export const CartPage = () => {
  const { data: myCart, isLoading: cartLoading, isError } = useGetMyCart();
  const { mutateAsync: removeItem, isPending } = useRemoveItem();

  const items = myCart?.cartItemResponses || [];

  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  useEffect(() => {
    if (items.length > 0) {
      const availableIds = items
        .filter((item) => (item.productSummaryResponse?.productStatus || "AVAILABLE") === "AVAILABLE")
        .map((item) => item.id);
      setSelectedIds(availableIds);
    }
  }, [myCart]);

  const handleToggleSelect = (id: number) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
    );
  };

  const handleRemoveSelected = async () => {
    try {
      await Promise.all(selectedIds.map((id) => removeItem(id)));
    } catch (error) {
      alert("Xóa bị lỗi");
    }
  };

  const handleToggleSelectAll = () => {
    const availableItems = items.filter(
      (item) => (item.productSummaryResponse?.productStatus || "AVAILABLE") === "AVAILABLE"
    );
    if (selectedIds.length === availableItems.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(availableItems.map((item) => item.id));
    }
  };

  const selectedItems = items.filter((item) => selectedIds.includes(item.id));
  const totalPrice = selectedItems.reduce((acc, item) => {
    const price = item.productSummaryResponse?.currentPrice || item.currentPrice || 0;
    return acc + price;
  }, 0);

  const availableCount = items.filter(
    (item) => (item.productSummaryResponse?.productStatus || "AVAILABLE") === "AVAILABLE"
  ).length;

  if (cartLoading) {
    return <div className={styles.stateBox}>Đang tải giỏ hàng...</div>;
  }

  if (isError || !myCart) {
    return <div className={styles.stateBox}>Không thể tải giỏ hàng</div>;
  }

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Giỏ hàng của tôi ({myCart.totalItem || items.length})</h1>

      {items.length === 0 ? (
        <div className={styles.emptyBox}>Giỏ hàng của bạn đang trống</div>
      ) : (
        <div className={styles.contentLayout}>
          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th className={styles.checkTh}>
                    <input
                      type="checkbox"
                      checked={availableCount > 0 && selectedIds.length === availableCount}
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
                  const isChecked = selectedIds.includes(item.id);

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
                          <div className={styles.noThumbnail}>No Img</div>
                        )}
                        <div className={styles.productMeta}>
                          <span className={styles.productName}>{product?.name || "Sản phẩm"}</span>
                          {!isAvailable && (
                            <span className={styles.soldBadge}>Đã được bán</span>
                          )}
                        </div>
                      </td>
                      <td>{product?.brandName || "—"}</td>
                      <td>{product?.size || "—"}</td>
                      <td>{product?.productCondition || "—"}</td>
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
                <Link to="/checkout" state={{ selectedCartItemIds: selectedIds }} className={styles.checkoutBtn}>
                  Thanh toán
                </Link>
              ) : (
                <button disabled className={styles.disabledBtn}>
                  Thanh toán
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

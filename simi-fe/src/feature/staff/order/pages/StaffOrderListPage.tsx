import React, { useState } from "react";
import styles from "./StaffOrderListPage.module.css";
import { useChangeOrderStatus, useStaffOrder } from "../hooks/useStaffOrders";
import { formatPrice } from "../../../../utils/formatPrice";
import type { OrderFilterRequest } from "../../../order/types/order.type";
import { getServerError } from "../../../../utils/getMessageError";
import { queryClient } from "../../../../app/queryClient";

export const StaffOrderListPage = () => {

  const [filters, setFilters] = useState<OrderFilterRequest>({
    orderStatus: "",
    orderChannel: "",
    keyword: "",
    fromDate: "",
    toDate: "",
    page: 0,
    size: 10,
    sortBy: "createdDate",
    sortDir: "desc",
  });

  const { data: pageData, isLoading, isError, error } = useStaffOrder(filters);

  const updateStatus = useChangeOrderStatus();

  const handleStatusChange = (orderId: number, status: string) => {
    updateStatus.mutate(
      { orderId, status },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: ['orders', 'staff'] });
          alert(`Đã cập nhật trạng thái đơn #${orderId} thành công!`);
        },
        onError: (err) => {
          const message = getServerError(err, "Cập nhật trạng thái thất bại");
          alert(message);
        },
      }
    );
  };

  const orders = pageData?.content || [];
  const totalPages = pageData?.totalPages || 1;

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
      page: 0,
    }));
  };

  const handleToggleSortDir = () => {
    setFilters((prev) => ({
      ...prev,
      sortDir: prev.sortDir === "desc" ? "asc" : "desc",
      page: 0,
    }));
  };

  return (
    <div className={styles.container}>
      <h1 className={styles.pageTitle}>Quản lý Đơn hàng (Staff)</h1>

      <div className={styles.filterSection}>
        <div className={styles.filterGrid}>
          <div className={`${styles.inputGroup} ${styles.colSpan2}`}>
            <label>Tìm kiếm</label>
            <input
              type="text"
              name="keyword"
              placeholder="Mã đơn, Tên khách, SĐT..."
              value={filters.keyword}
              onChange={handleInputChange}
            />
          </div>

          <div className={styles.inputGroup}>
            <label>Kênh bán</label>
            <select
              name="orderChannel"
              value={filters.orderChannel}
              onChange={handleInputChange}
            >
              <option value="">Tất cả kênh</option>
              <option value="ONLINE">Online</option>
              <option value="IN_STORE">Mua tại quầy</option>
            </select>
          </div>

          <div className={styles.inputGroup}>
            <label>Trạng thái</label>
            <select
              name="orderStatus"
              value={filters.orderStatus}
              onChange={handleInputChange}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="PENDING">Chờ xử lý</option>
              <option value="PACKING">Đang đóng gói</option>
              <option value="SHIPPING">Đang giao hàng</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="CANCELLED">Đã huỷ</option>
            </select>
          </div>

          <div className={styles.inputGroup}>
            <label>Từ ngày</label>
            <input
              type="date"
              name="fromDate"
              value={filters.fromDate}
              onChange={handleInputChange}
            />
          </div>

          <div className={styles.inputGroup}>
            <label>Đến ngày</label>
            <input
              type="date"
              name="toDate"
              value={filters.toDate}
              onChange={handleInputChange}
            />
          </div>

          <div className={styles.inputGroup}>
            <label>Sắp xếp theo</label>
            <select
              name="sortBy"
              value={filters.sortBy}
              onChange={handleInputChange}
            >
              <option value="createdDate">Ngày tạo đơn</option>
              <option value="finalAmount">Tổng giá trị</option>
              <option value="id">ID đơn hàng</option>
            </select>
          </div>

          <div className={styles.inputGroup}>
            <label>Thứ tự</label>
            <button
              type="button"
              className={styles.sortDirBtn}
              onClick={handleToggleSortDir}
              title={filters.sortDir === "desc" ? "Giảm dần" : "Tăng dần"}
            >
              {filters.sortDir === "desc" ? (
                <>
                  <span className={styles.arrowIcon}>↓</span> Giảm dần
                </>
              ) : (
                <>
                  <span className={styles.arrowIcon}>↑</span> Tăng dần
                </>
              )}
            </button>
          </div>
        </div>
      </div>

      <div className={styles.tableWrapper}>
        {isLoading ? (
          <div className={styles.loading}>Đang tải dữ liệu từ máy chủ...</div>
        ) : isError ? (
          <div className={styles.error}>
            Đã xảy ra lỗi khi tải đơn hàng: {(error as Error)?.message || "Lỗi không xác định"}
          </div>
        ) : orders.length === 0 ? (
          <div className={styles.empty}>Không tìm thấy đơn hàng nào phù hợp.</div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Mã đơn</th>
                <th>Ngày tạo</th>
                <th>Sản phẩm đầu</th>
                <th>Tổng thanh toán</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td className={styles.orderId}>#{order.id}</td>
                  <td className={styles.dateCell}>
                    {new Date(order.createdDate).toLocaleDateString("vi-VN", {
                      hour: "2-digit",
                      minute: "2-digit",
                      day: "2-digit",
                      month: "2-digit",
                      year: "numeric",
                    })}
                  </td>
                  <td className={styles.productCell}>
                    {order.firstItemThumbnail ? (
                      <img
                        src={order.firstItemThumbnail}
                        alt={order.firstItemName}
                        className={styles.thumbnailImg}
                      />
                    ) : (
                      <div className={styles.noThumbnail}>No Img</div>
                    )}
                    <div className={styles.productInfo}>
                      <span className={styles.productName}>
                        {order.firstItemName || "Chưa có tên sản phẩm"}
                      </span>
                      {order.totalItem > 1 && (
                        <span className={styles.totalItemsCount}>
                          +{order.totalItem - 1} sản phẩm khác
                        </span>
                      )}
                    </div>
                  </td>
                  <td className={styles.amount}>
                    {formatPrice(order.finalAmount)}
                  </td>
                  <td>
                    <select
                      className={`${styles.statusSelect} ${
                        styles[`status_${order.orderStatus}`]
                      }`}
                      value={order.orderStatus}
                      onChange={(e) =>
                        handleStatusChange(order.id, e.target.value)
                      }
                      disabled={updateStatus.isPending}
                    >
                      <option value="PENDING">Chờ xử lý</option>
                      <option value="PACKING">Đang đóng gói</option>
                      <option value="SHIPPING">Đang giao hàng</option>
                      <option value="COMPLETED">Hoàn thành</option>
                      <option value="CANCELLED">Đã huỷ</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className={styles.pagination}>
        <button
          disabled={filters.page === 0 || isLoading}
          onClick={() =>
            setFilters((prev) => ({ ...prev, page: prev.page - 1 }))
          }
          className={styles.pageBtn}
        >
          Trước
        </button>

        <span className={styles.pageInfo}>
          Trang {filters.page + 1} / {totalPages}
        </span>

        <button
          disabled={pageData?.last || filters.page + 1 >= totalPages || isLoading}
          onClick={() =>
            setFilters((prev) => ({ ...prev, page: prev.page + 1 }))
          }
          className={styles.pageBtn}
        >
          Sau
        </button>
      </div>
    </div>
  );
};

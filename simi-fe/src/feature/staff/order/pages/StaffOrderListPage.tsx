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
          queryClient.invalidateQueries({ queryKey: ["orders", "staff"] });
          alert(`Đã cập nhật trạng thái đơn #${orderId} thành công.`);
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

  const handleResetFilters = () => {
    setFilters({
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
  };

  return (
    <div className={styles.container}>
      {/* TIÊU ĐỀ TRANG */}
      <div className={styles.topBar}>
        <div>
          <h1 className={styles.pageTitle}>Quản lý đơn hàng</h1>
          <p className={styles.subtitle}>
            Theo dõi, xử lý và cập nhật tiến độ giao nhận đơn hàng
          </p>
        </div>
      </div>

      {/* BỘ LỌC TÌM KIẾM */}
      <div className={styles.filterCard}>
        <div className={styles.filterHeader}>
          <span className={styles.filterTitle}>Bộ lọc tìm kiếm</span>
          <button
            type="button"
            className={styles.btnReset}
            onClick={handleResetFilters}
          >
            Đặt lại bộ lọc
          </button>
        </div>

        <div className={styles.filterGrid}>
          {/* Ô Tìm kiếm từ khóa (chiếm 2 cột) */}
          <div className={`${styles.inputGroup} ${styles.colSpan2}`}>
            <label>Từ khóa</label>
            <input
              type="text"
              name="keyword"
              placeholder="Nhập mã đơn, tên khách, số điện thoại..."
              value={filters.keyword}
              onChange={handleInputChange}
            />
          </div>

          {/* Lọc Kênh bán (1 cột) */}
          <div className={styles.inputGroup}>
            <label>Kênh bán</label>
            <select
              name="orderChannel"
              value={filters.orderChannel}
              onChange={handleInputChange}
            >
              <option value="">Tất cả kênh</option>
              <option value="ONLINE">Online</option>
              <option value="IN_STORE">Tại quầy</option>
            </select>
          </div>

          {/* Lọc Trạng thái (Đầy đủ 7 trạng thái) */}
          <div className={styles.inputGroup}>
            <label>Trạng thái</label>
            <select
              name="orderStatus"
              value={filters.orderStatus}
              onChange={handleInputChange}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="PENDING">Chờ xác nhận</option>
              <option value="PENDING_PAYMENT">Chờ thanh toán</option>
              <option value="PACKING">Đang đóng gói</option>
              <option value="SHIPPING">Đang giao hàng</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="CANCELLED">Đã hủy</option>
              <option value="EXPIRED">Hết hạn thanh toán</option>
            </select>
          </div>

          {/* Từ ngày (1 cột) */}
          <div className={styles.inputGroup}>
            <label>Từ ngày</label>
            <input
              type="date"
              name="fromDate"
              value={filters.fromDate}
              onChange={handleInputChange}
            />
          </div>

          {/* Đến ngày (1 cột) */}
          <div className={styles.inputGroup}>
            <label>Đến ngày</label>
            <input
              type="date"
              name="toDate"
              value={filters.toDate}
              onChange={handleInputChange}
            />
          </div>

          {/* Sắp xếp theo (1 cột) */}
          <div className={styles.inputGroup}>
            <label>Sắp xếp</label>
            <select
              name="sortBy"
              value={filters.sortBy}
              onChange={handleInputChange}
            >
              <option value="createdDate">Ngày tạo đơn</option>
              <option value="finalAmount">Tổng tiền</option>
              <option value="id">Mã đơn hàng</option>
            </select>
          </div>

          {/* Thứ tự hiển thị (1 cột) */}
          <div className={styles.inputGroup}>
            <label>Thứ tự</label>
            <button
              type="button"
              className={styles.btnSort}
              onClick={handleToggleSortDir}
            >
              {filters.sortDir === "desc" ? "Mới nhất trước" : "Cũ nhất trước"}
            </button>
          </div>
        </div>
      </div>

      {/* BẢNG DỮ LIỆU ĐƠN HÀNG */}
      <div className={styles.tableCard}>
        {isLoading ? (
          <div className={styles.centerText}>Đang tải dữ liệu đơn hàng...</div>
        ) : isError ? (
          <div className={styles.errorText}>
            Lỗi khi tải dữ liệu: {(error as Error)?.message || "Không xác định"}
          </div>
        ) : orders.length === 0 ? (
          <div className={styles.centerText}>Không tìm thấy đơn hàng nào phù hợp.</div>
        ) : (
          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th style={{ width: "90px" }}>Mã đơn</th>
                  <th style={{ width: "130px", textAlign: "center" }}>Ngày tạo</th>
                  <th>Sản phẩm</th>
                  <th style={{ width: "130px", textAlign: "right" }}>Tổng tiền</th>
                  <th style={{ width: "170px", textAlign: "center" }}>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.id}>
                    {/* Mã đơn */}
                    <td>
                      <span className={styles.orderId}>#{order.id}</span>
                    </td>

                    {/* Ngày tạo */}
                    <td className={styles.dateCell}>
                      {new Date(order.createdDate).toLocaleDateString("vi-VN", {
                        hour: "2-digit",
                        minute: "2-digit",
                        day: "2-digit",
                        month: "2-digit",
                        year: "numeric",
                      })}
                    </td>

                    {/* Thông tin sản phẩm */}
                    <td>
                      <div className={styles.productCell}>
                        {order.firstItemThumbnail ? (
                          <img
                            src={order.firstItemThumbnail}
                            alt={order.firstItemName}
                            className={styles.thumbnail}
                          />
                        ) : (
                          <div className={styles.noThumbnail}>Chưa có ảnh</div>
                        )}
                        <div className={styles.productMeta}>
                          <span className={styles.productName}>
                            {order.firstItemName || "Sản phẩm không có tên"}
                          </span>
                          {order.totalItem > 1 && (
                            <span className={styles.moreCount}>
                              +{order.totalItem - 1} món khác
                            </span>
                          )}
                        </div>
                      </div>
                    </td>

                    {/* Tổng tiền */}
                    <td className={styles.amountCell}>
                      {formatPrice(order.finalAmount || 0)}
                    </td>

                    {/* Dropdown chỉnh sửa trạng thái trực tiếp */}
                    <td style={{ textAlign: "center" }}>
                      <select
                        className={`${styles.statusSelect} ${styles[`status_${order.orderStatus}`]}`}
                        value={order.orderStatus}
                        onChange={(e) => handleStatusChange(order.id, e.target.value)}
                        disabled={
                          updateStatus.isPending ||
                          order.orderStatus === "COMPLETED" ||
                          order.orderStatus === "CANCELLED" ||
                          order.orderStatus === "EXPIRED"
                        }
                      >
                        <option value="PENDING">Chờ xác nhận </option>
                        <option value="PENDING_PAYMENT">Chờ thanh toán</option>
                        <option value="PACKING">Đang đóng gói</option>
                        <option value="SHIPPING">Đang giao hàng</option>
                        <option value="COMPLETED">Hoàn thành</option>
                        <option value="CANCELLED">Đã hủy</option>
                        <option value="EXPIRED">Hết hạn</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* PHÂN TRANG */}
        <div className={styles.pagination}>
          <button
            type="button"
            disabled={filters.page === 0 || isLoading}
            onClick={() => setFilters((prev) => ({ ...prev, page: prev.page - 1 }))}
            className={styles.pageBtn}
          >
            Trang trước
          </button>

          <span className={styles.pageInfo}>
            Trang {filters.page + 1} / {totalPages}
          </span>

          <button
            type="button"
            disabled={pageData?.last || filters.page + 1 >= totalPages || isLoading}
            onClick={() => setFilters((prev) => ({ ...prev, page: prev.page + 1 }))}
            className={styles.pageBtn}
          >
            Trang sau
          </button>
        </div>
      </div>
    </div>
  );
};
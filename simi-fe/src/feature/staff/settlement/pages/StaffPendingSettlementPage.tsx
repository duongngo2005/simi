import { useState } from "react";
import { useNavigate } from "react-router";
import styles from "./StaffPendingSettlementPage.module.css";
import { useStaffConsignments } from "../../consignment/hooks/useConsignments";
import type { ConsignmentFilterRequest } from "../../consignment/types/staffConsignment.type";

export const StaffPendingSettlementPage = () => {
  const [filters, setFilters] = useState<ConsignmentFilterRequest>({
    keyword: "",
    startDateFrom: "",
    startDateTo: "",
    expiryDateFrom: "",
    expiryDateTo: "",
    isExpiringSoon: false,
    consignmentStatus: "PENDING_SETTLEMENT",
    page: 0,
    size: 10,
    sortBy: "createdDate",
    sortDir: "desc",
  });

  const { data: pageData, isLoading } = useStaffConsignments(filters);
  const nav = useNavigate();

  const consignments = pageData?.content || [];
  const totalPages = pageData?.totalPages || 1;

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Lô hàng chờ quyết toán</h1>
      </div>

      <div className={styles.tableWrapper}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Mã lô hàng</th>
              <th>Khách gửi</th>
              <th>Nhân viên nhận</th>
              <th>Ngày hết hạn</th>
              <th>Tổng sản phẩm</th>
              <th>Đã bán</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? (
              <tr>
                <td colSpan={8} className={styles.emptyCell}>
                  Đang tải dữ liệu...
                </td>
              </tr>
            ) : consignments.length === 0 ? (
              <tr>
                <td colSpan={8} className={styles.emptyCell}>
                  Không có lô hàng nào chờ quyết toán
                </td>
              </tr>
            ) : (
              consignments.map((item) => (
                <tr key={item.id}>
                  <td className={styles.codeCell}>#{item.id}</td>
                  <td>{item.consignorName}</td>
                  <td>{item.receivedName}</td>
                  <td>
                    {item.expiryDate
                      ? new Date(item.expiryDate).toLocaleDateString("vi-VN")
                      : "—"}
                  </td>
                  <td>{item.totalItem}</td>
                  <td>{item.soldItem}</td>
                  <td>
                    <span
                      className={`${styles.statusBadge} ${
                        styles[`status_${item.status}`]
                      }`}
                    >
                      Chờ quyết toán
                    </span>
                  </td>
                  <td>
                    <button
                      onClick={() => nav(`/staff/consignments/${item.id}`)}
                      className={styles.btnDetail}
                    >
                      Quyết toán
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className={styles.pagination}>
          <button
            disabled={filters.page === 0}
            onClick={() =>
              setFilters((prev) => ({ ...prev, page: prev.page - 1 }))
            }
            className={styles.pageBtn}
          >
            Trang trước
          </button>
          <span className={styles.pageInfo}>
            Trang {filters.page + 1} / {totalPages}
          </span>
          <button
            disabled={filters.page >= totalPages - 1}
            onClick={() =>
              setFilters((prev) => ({ ...prev, page: prev.page + 1 }))
            }
            className={styles.pageBtn}
          >
            Trang sau
          </button>
        </div>
      )}
    </div>
  );
};

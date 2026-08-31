import { useState } from "react";
import { useCreateConsignment, useStaffConsignments } from "../hooks/useConsignments";
import styles from "./StaffConsignmentPage.module.css";
import type { ConsignmentFilterRequest } from "../types/staffConsignment.type";
import { CreateConsignmentModal } from "../components/CreateConsignmentModal";
import { useNavigate } from "react-router";

const STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang ký gửi",
  PENDING_SETTLEMENT: "Chờ quyết toán",
  SETTLED: "Đã quyết toán",
  CLOSED: "Đã đóng",
};

export const StaffConsignmentPage = () => {

  const [filters, setFilters] = useState<ConsignmentFilterRequest>({
    keyword: "",
    startDateFrom: "",
    startDateTo: "",
    expiryDateFrom: "",
    expiryDateTo: "",
    isExpiringSoon: false,
    consignmentStatus: "",
    page: 0,
    size: 10,
    sortBy: "createdDate",
    sortDir: "desc",
  });

  const { data: pageData, isLoading } = useStaffConsignments(filters);
  const [isOpenModal, setIsOpenModal] = useState(false);
  const nav = useNavigate()
  const createConsignment = useCreateConsignment()

  const consignments = pageData?.content || [];
  const totalPages = pageData?.totalPages || 1;

  const handleCreate = async (data: {consignorPhone: string, note: string}) => {
    const response = await createConsignment.mutateAsync(data)
    nav(`/staff/consignments/${response.body.id}`)
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          <p className={styles.eyebrow}>Vận hành ký gửi</p>
          <h1 className={styles.title}>Quản lý ký gửi</h1>
        </div>
        <button onClick={() => setIsOpenModal(true)} className={styles.btnCreate}>Tạo lô ký gửi</button>
      </div>

      <div className={styles.tableWrapper}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Mã lô hàng</th>
              <th>Khách gửi</th>
              <th>Nhân viên nhận</th>
              <th>Ngày bắt đầu</th>
              <th>Ngày hết hạn</th>
              <th>Tổng sản phẩm</th>
              <th>Đã bán</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {consignments.length === 0 ? (
              <tr>
                <td colSpan={9} className={styles.emptyCell}>
                  Không có lô ký gửi nào
                </td>
              </tr>
            ) : (
              consignments.map((item) => (
                <tr key={item.id}>
                  <td className={styles.codeCell}>#{item.id}</td>
                  <td>{item.consignorName}</td>
                  <td>{item.receivedName}</td>
                  <td>
                    {item.startDate
                      ? new Date(item.startDate).toLocaleDateString("vi-VN")
                      : "—"}
                  </td>
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
                      {STATUS_LABEL[item.status] ?? item.status}
                    </span>
                  </td>
                  <td>
                    <button onClick={() => nav(`/staff/consignments/${item.id}`)} className={styles.btnDetail}>Chi tiết</button>
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
            className={styles.pageBtn}
            disabled={filters.page === 0 || isLoading}
            onClick={() =>
              setFilters((prev) => ({ ...prev, page: prev.page - 1 }))
            }
          >
            Trước
          </button>
          <span className={styles.pageInfo}>
            Trang {filters.page + 1} / {totalPages}
          </span>
          <button
            className={styles.pageBtn}
            disabled={pageData?.last || isLoading}
            onClick={() =>
              setFilters((prev) => ({ ...prev, page: prev.page + 1 }))
            }
          >
            Sau
          </button>
        </div>
      )}

      <CreateConsignmentModal
        isOpen={isOpenModal}
        onClose={() => setIsOpenModal(false)}
        onSubmit={handleCreate}
      />
    </div>
  );
};

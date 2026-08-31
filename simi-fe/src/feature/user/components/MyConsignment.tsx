import { useState } from "react";
import { useNavigate } from "react-router";
import styles from "./MyConsignment.module.css";
import { useGetMyConsignments } from "../../consignment/hooks/useConsignment";

const STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang ký gửi",
  PENDING_SETTLEMENT: "Chờ kết toán",
  SETTLED: "Đã kết toán",
  CLOSED: "Đã hoàn tất",
};

const TABS = [
  { key: "", label: "Tất cả" },
  { key: "ACTIVE", label: "Đang ký gửi" },
  { key: "PENDING_SETTLEMENT", label: "Chờ kết toán" },
  { key: "SETTLED", label: "Đã kết toán" },
  { key: "CLOSED", label: "Đã hoàn tất" },
];

export const MyConsignment = () => {
  const [selectedStatus, setSelectedStatus] = useState<string>("");
  const navigate = useNavigate();

  const { data: consignments = [], isLoading, isError } = useGetMyConsignments();

  const filteredConsignments = consignments.filter((item) => {
    if (!selectedStatus) return true;
    return item.status === selectedStatus;
  });

  return (
    <div className={styles.container}>
      <div className={styles.sectionHeader}>
        <h2 className={styles.title}>Ký gửi của tôi</h2>
        <p className={styles.count}>{filteredConsignments.length} lô phù hợp</p>
      </div>

      <div className={styles.filterTabs}>
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={`${styles.tabBtn} ${selectedStatus === tab.key ? styles.tabActive : ""}`}
            onClick={() => setSelectedStatus(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className={styles.stateBox}>Đang tải danh sách ký gửi...</div>
      ) : isError ? (
        <div className={styles.stateBox}>Không thể tải danh sách ký gửi</div>
      ) : filteredConsignments.length === 0 ? (
        <div className={styles.emptyBox}>Không có lô ký gửi nào</div>
      ) : (
        <div className={styles.consignmentList}>
          {filteredConsignments.map((item) => (
            <div
              key={item.id}
              className={styles.consignmentCard}
              onClick={() => navigate(`/my-consignments/${item.id}`)}
            >
              <div className={styles.cardHeader}>
                <div className={styles.headerLeft}>
                  <span className={styles.batchId}>LÔ #{String(item.id).padStart(4, "0")}</span>
                  <span className={styles.dateInfo}>
                    Tiếp nhận {new Date(item.startDate).toLocaleDateString("vi-VN")}
                  </span>
                </div>
                <span className={`${styles.statusBadge} ${styles[`status${item.status}`]}`}>
                  {STATUS_LABEL[item.status] || item.status}
                </span>
              </div>

              <div className={styles.cardBody}>
                <div className={styles.metaRow}>
                  <span>Tổng số <strong>{item.totalItem} món</strong></span>
                  <span>Đã bán <strong>{item.soldItem} món</strong></span>
                </div>
                {item.note && <div className={styles.noteText}>Ghi chú: {item.note}</div>}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

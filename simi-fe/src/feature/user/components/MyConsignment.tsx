import { useState } from "react";
import styles from "./MyConsignment.module.css";
import { useGetMyConsignments } from "../../consignment/hooks/useConsignment";
import { ConsignmentDetailModal } from "./ConsignmentDetailModal";

const STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang ký gửi",
  PENDING_SETTLEMENT: "Chờ thanh toán",
  SETTLED: "Đã thanh toán",
  CLOSED: "Đã hoàn tất",
};

const TABS = [
  { key: "", label: "Tất cả" },
  { key: "ACTIVE", label: "Đang ký gửi" },
  { key: "PENDING_SETTLEMENT", label: "Chờ thanh toán" },
  { key: "SETTLED", label: "Đã thanh toán" },
  { key: "CLOSED", label: "Đã hoàn tất" },
];

export const MyConsignment = () => {
  const [selectedStatus, setSelectedStatus] = useState<string>("");
  const [selectedId, setSelectedId] = useState<number | null>(null);

  const { data: consignments = [], isLoading, isError } = useGetMyConsignments();

  const filteredConsignments = consignments.filter((item) => {
    if (!selectedStatus) return true;
    return item.status === selectedStatus;
  });

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Ký gửi của tôi ({filteredConsignments.length})</h2>

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
              onClick={() => setSelectedId(item.id)}
            >
              <div className={styles.cardHeader}>
                <div className={styles.headerLeft}>
                  <span className={styles.batchId}>Lô ký gửi #{item.id}</span>
                  <span className={styles.dateInfo}>
                    Ngày gửi: {new Date(item.startDate).toLocaleDateString("vi-VN")}
                  </span>
                </div>
                <span className={`${styles.statusBadge} ${styles[`status${item.status}`]}`}>
                  {STATUS_LABEL[item.status] || item.status}
                </span>
              </div>

              <div className={styles.cardBody}>
                <div className={styles.metaRow}>
                  <span>Tổng sản phẩm: <strong>{item.totalItem} món</strong></span>
                  <span>Đã bán: <strong>{item.soldItem} món</strong></span>
                </div>
                {item.note && <div className={styles.noteText}>Ghi chú: {item.note}</div>}
              </div>
            </div>
          ))}
        </div>
      )}

      <ConsignmentDetailModal id={selectedId} onClose={() => setSelectedId(null)} />
    </div>
  );
};

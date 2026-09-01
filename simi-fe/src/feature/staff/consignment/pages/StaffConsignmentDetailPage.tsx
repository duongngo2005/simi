import { useParams, useNavigate } from "react-router";
import { useActiveConsignment, useConsignmentFullDetail } from "../hooks/useConsignments";
import { ConsignmentItemForm } from "../components/ConsignmentItemForm";
import { ConsignmentItemList } from "../components/ConsignmentItemList";
import { ConsignmentHeaderInfo } from "../components/ConsignmentHeaderInfo";
import styles from "./StaffConsignmentDetailPage.module.css";
import { useState } from "react";
import type { ConsignmentItemResponse } from "../types/staffConsignment.type";
import { ActiveConsignmentItemList } from "../components/ActiveConsignmentItemList";
import { PendingSettlementConsignmentView } from "../components/PendingSettlementConsignmentView";
import { SettledConsignmentView } from "../components/SettledConsignmentView";

const STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang ký gửi",
  PENDING_SETTLEMENT: "Chờ quyết toán",
  SETTLED: "Đã quyết toán",
  CLOSED: "Đã đóng",
};

export const StaffConsignmentDetailPage = () => {
  const { id } = useParams();
  const nav = useNavigate();
  const {
    data: consignmentFullDetail,
    isLoading,
    isError,
  } = useConsignmentFullDetail(Number(id));

  const consignment = consignmentFullDetail?.consignmentResponse;
  const items = consignmentFullDetail?.consignmentItemResponses || [];

  const { mutateAsync: activeConsignment } = useActiveConsignment();

  const [editingItem, setEditingItem] = useState<ConsignmentItemResponse | null>(null);

  if (isLoading) {
    return <div className={styles.stateBox}>Đang tải lô ký gửi…</div>;
  }

  if (isError || !consignment) {
    return <div className={styles.stateBox}>Đã có lỗi xảy ra</div>;
  }

  const handleActive = async (consignmentId: number) => {
    try {
      await activeConsignment(consignmentId);
      alert("Kích hoạt lô hàng thành công");
    } catch {
      alert("Lỗi kích hoạt lô hàng");
    }
  };

  return (
    <div className={styles.container}>
      <header className={styles.pageHeader}>
        <button
          type="button"
          className={styles.backLink}
          onClick={() => nav("/staff/consignments")}
        >
          <span aria-hidden="true">←</span>
          Quay lại danh sách ký gửi
        </button>

        <div className={styles.titleRow}>
          <div>
            <h1 className={styles.title}>
              Lô ký gửi <span className={styles.lotId}>#{String(consignment.id).padStart(4, "0")}</span>
            </h1>
          </div>
          <span className={`${styles.statusBadge} ${styles[`status_${consignment.status}`]}`}>
            {STATUS_LABEL[consignment.status] ?? consignment.status}
          </span>
        </div>
      </header>

      {consignment.status === "DRAFT" ? (
        <div className={styles.contentGrid}>
          <ConsignmentItemForm
            key={editingItem?.id ?? "new-item"}
            consignmentId={consignment.id}
            editingItem={editingItem}
            onCancelEdit={() => setEditingItem(null)}
          />
          <ConsignmentItemList
            consignmentId={consignment.id}
            status={consignment.status}
            items={items}
            onActivate={() => handleActive(consignment.id)}
            onEditItem={(item) => setEditingItem(item)}
          />
        </div>
      ) : consignment.status === "ACTIVE" ? (
        <div>
          <ConsignmentHeaderInfo consignment={consignment} />
          <ActiveConsignmentItemList items={items} />
        </div>
      ) : consignment.status === "PENDING_SETTLEMENT" ? (
        <PendingSettlementConsignmentView consignmentId={consignment.id} />
      ) : consignment.status === "SETTLED" ? (
        <SettledConsignmentView consignmentId={consignment.id} />
      ) : (
        <div className={styles.stateBox}>Trạng thái: {consignment.status}</div>
      )}
    </div>
  );
};

import { useParams } from "react-router";
import { useActiveConsignment, useConsignmentFullDetail } from "../hooks/useConsignments";
import { ConsignmentItemForm } from "../components/ConsignmentItemForm";
import { ConsignmentItemList } from "../components/ConsignmentItemList";
import styles from "./StaffConsignmentDetailPage.module.css";
import { useState } from "react";
import type { ConsignmentItemResponse } from "../types/staffConsignment.type";

export const StaffConsignmentDetailPage = () => {
  const { id } = useParams();
  const {
    data: consignmentFullDetail,
    isLoading,
    isError,
  } = useConsignmentFullDetail(Number(id));

  const consignment = consignmentFullDetail?.consignmentResponse;
  const items = consignmentFullDetail?.consignmentItemResponses || [];

  const {mutateAsync: activeConsignment} = useActiveConsignment()

  const [editingItem, setEditingItem] = useState<ConsignmentItemResponse | null >(null);

  if (isLoading) {
    return <div className={styles.stateBox}>Loading...</div>;
  }

  if (isError || !consignment) {
    return <div className={styles.stateBox}>Đã có lỗi xảy ra</div>;
  }

  const handleActive = async (consignmentId: number) => {
    try{
      const response = await activeConsignment(consignmentId);
      alert("Kích hoạt lô hàng thành công")
    }catch(error){
      alert("Lỗi kích hoạt lô hàng")
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.topBar}>
        <button className={styles.btnBack}>Quay lại</button>
        <h1 className={styles.title}>Lô ký gửi #{id}</h1>
        <span className={styles.statusBadge}>{consignment.status}</span>
      </div>
      {consignment.status === "DRAFT" ? (
        <div className={styles.contentGrid}>
          <ConsignmentItemForm 
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
      ) : (
        <div className={styles.stateBox}>NOT DRAFT</div>
      )}
    </div>
  );
};

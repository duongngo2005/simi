import { useState } from "react";
import { useDeleteConsignmentItem } from "../hooks/useConsignments";
import type { ConsignmentItemResponse } from "../types/staffConsignment.type";
import styles from "./ConsignmentItemList.module.css";

interface Props {
  consignmentId: number,
  items: ConsignmentItemResponse[];
  status: string;
  onActivate: () => void;
  onEditItem: (item: ConsignmentItemResponse) => void;
}

export const ConsignmentItemList = ({
  consignmentId,
  items,
  status,
  onActivate,
  onEditItem
}: Props) => {

  const getOriginPrice = (item: ConsignmentItemResponse) => {
    const schedule = item.priceScheduleResponses?.find(
      (s) => s.effectiveAfterDays === 0
    );
    return schedule ? schedule.price.toLocaleString("vi-VN") + "đ" : "-";
  };

  const {mutateAsync: deleteConsignmentItem} = useDeleteConsignmentItem();

  const [deletingId, setDeletingId] = useState<number | null>(null);

  const handleDeleteConsignmentItem = async (consignmentItemId: number) => {
    try{
      setDeletingId(consignmentItemId)
      await deleteConsignmentItem({consignmentItemId, consignmentId});
      alert("Xóa thành công")
    }catch(error){
      alert("Xóa thất bại");
      setDeletingId(null)
    }finally{
      setDeletingId(null)
    }
  }

  return (
    <div className={styles.card}>
      <div className={styles.cardHeader}>
        <h3 className={styles.cardTitle}>Danh sách sản phẩm</h3>
        {status === "DRAFT" && (
          <button className={styles.btnActivate} onClick={onActivate}>
            Kích hoạt lô hàng
          </button>
        )}
      </div>
      {items.length === 0 ? (
        <div className={styles.emptyText}>Chưa có sản phẩm</div>
      ) : (
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Ảnh</th>
                <th>Tên</th>
                <th>Tình trạng</th>
                <th>Giá gốc</th>
                <th>Hoa hồng</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr 
                key={item.id}
                className={deletingId === item.id ? styles.deletingRow : ""}
                >
                  <td>
                    <img
                      src={
                        item.productDetailResponse?.thumbnail
                      }
                      alt={item.productDetailResponse?.name || ""}
                      className={styles.thumbnail}
                    />
                  </td>
                  <td>
                    <div className={styles.productName}>
                      {item.productDetailResponse?.name}
                    </div>
                    {(item.productDetailResponse?.size ||
                      item.productDetailResponse?.color) && (
                      <small className={styles.productMeta}>
                        {[
                          item.productDetailResponse?.size,
                          item.productDetailResponse?.color,
                        ]
                          .filter(Boolean)
                          .join(" - ")}
                      </small>
                    )}
                  </td>
                  <td>{item.productDetailResponse?.productCondition}</td>
                  <td className={styles.priceCell}>{getOriginPrice(item)}</td>
                  <td>{item.commissionRate}</td>
                  <td>
                    <span className={styles.statusBadge}>{item.status}</span>
                  </td>
                  {status === "DRAFT" && (
                    <td>
                      <div className={styles.actionButtons}>
                        <button onClick={() => onEditItem(item)} type="button" className={styles.btnEdit}>
                          Chỉnh sửa
                        </button>
                        <button
                          type="button"
                          className={styles.btnDelete}
                          onClick={() => handleDeleteConsignmentItem(item.id)}
                          disabled={deletingId == item.id}
                        >
                          Xóa
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

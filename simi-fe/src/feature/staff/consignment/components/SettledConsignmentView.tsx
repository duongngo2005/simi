import styles from "./SettledConsignmentView.module.css";
import type { ConsignmentItemResponse } from "../types/staffConsignment.type";
import { useGetSettlement } from "../../settlement/hook/useStaffSettlement";

interface Props {
  consignmentId: number;
}

export const SettledConsignmentView = ({ consignmentId }: Props) => {
  const { data: settlement, isLoading, isError } = useGetSettlement(consignmentId);

  if (isLoading) {
    return <div className={styles.stateBox}>Đang tải thông tin quyết toán...</div>;
  }

  if (isError || !settlement) {
    return <div className={styles.stateBox}>Không tìm thấy thông tin phiếu quyết toán</div>;
  }

  const items: ConsignmentItemResponse[] = settlement.settlementItemResponses || [];

  return (
    <div className={styles.container}>
      <div className={styles.topStatusBanner}>
        <span className={styles.bannerTitle}>Phiếu quyết toán #{settlement.id}</span>
        <span className={styles.bannerDate}>
          Ngày hoàn tất: {settlement.settledAt ? new Date(settlement.settledAt).toLocaleString("vi-VN") : "—"}
        </span>
      </div>

      <div className={styles.gridTwoCols}>
        <div className={styles.card}>
          <div className={styles.cardHeader}>Thông tin chuyển khoản</div>
          <div className={styles.infoGrid}>
            <div className={styles.infoGroup}>
              <span className={styles.infoLabel}>Mã khách hàng:</span>
              <span className={styles.infoValue}>#{settlement.consignorId}</span>
            </div>
            <div className={styles.infoGroup}>
              <span className={styles.infoLabel}>Phương thức:</span>
              <span className={styles.infoValue}>{settlement.paymentMethod || "BANK_TRANSFER"}</span>
            </div>
            <div className={styles.infoGroup}>
              <span className={styles.infoLabel}>Ngân hàng:</span>
              <span className={styles.infoValue}>{settlement.bankName || "—"}</span>
            </div>
            <div className={styles.infoGroup}>
              <span className={styles.infoLabel}>Số tài khoản:</span>
              <span className={styles.infoValue}>{settlement.accountNumber || "—"}</span>
            </div>
            <div className={styles.infoGroup}>
              <span className={styles.infoLabel}>Chủ tài khoản:</span>
              <span className={styles.infoValue}>{settlement.accountHolder || "—"}</span>
            </div>
            <div className={styles.infoGroup}>
              <span className={styles.infoLabel}>Nhân viên duyệt:</span>
              <span className={styles.infoValue}>#{settlement.processedById}</span>
            </div>
          </div>
        </div>

        <div className={styles.card}>
          <div className={styles.cardHeader}>Biên lai chuyển khoản</div>
          <div className={styles.proofImageWrapper}>
            {settlement.proofImageUrl ? (
              <a href={settlement.proofImageUrl} target="_blank" rel="noreferrer">
                <img
                  src={settlement.proofImageUrl}
                  alt="Biên lai chuyển khoản"
                  className={styles.proofImage}
                />
              </a>
            ) : (
              <span className={styles.noImageText}>Không có ảnh biên lai</span>
            )}
          </div>
        </div>
      </div>

      <div className={styles.card}>
        <div className={styles.cardHeader}>Tổng kết tài chính đã thanh toán</div>
        <div className={styles.summaryGrid}>
          <div className={styles.summaryBox}>
            <span className={styles.summaryLabel}>Tổng doanh số sản phẩm</span>
            <span className={styles.summaryValue}>
              {settlement.totalSoldAmount?.toLocaleString("vi-VN")} đ
            </span>
          </div>
          <div className={styles.summaryBox}>
            <span className={styles.summaryLabel}>Phí hoa hồng cửa hàng</span>
            <span className={styles.summaryValue}>
              {settlement.totalCommissionAmount?.toLocaleString("vi-VN")} đ
            </span>
          </div>
          <div className={`${styles.summaryBox} ${styles.netBox}`}>
            <span className={styles.summaryLabel}>Số tiền đã thực nhận</span>
            <span className={styles.netValue}>
              {settlement.netAmount?.toLocaleString("vi-VN")} đ
            </span>
          </div>
        </div>
      </div>

      <div className={styles.tableSection}>
        <h3 className={styles.sectionTitle}>Sản phẩm đã bán & quyết toán ({items.length})</h3>
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Mã món</th>
                <th>Tên sản phẩm</th>
                <th>Giá bán</th>
                <th>Tỉ lệ hoa hồng</th>
              </tr>
            </thead>
            <tbody>
              {items.length === 0 ? (
                <tr>
                  <td colSpan={4} className={styles.emptyCell}>
                    Không có danh sách sản phẩm chi tiết
                  </td>
                </tr>
              ) : (
                items.map((item) => (
                  <tr key={item.id}>
                    <td className={styles.codeCell}>#{item.id}</td>
                    <td>{item.productDetailResponse?.name}</td>
                    <td>
                      {item.productDetailResponse?.currentPrice?.toLocaleString("vi-VN")} đ
                    </td>
                    <td>{item.commissionRate * 100}%</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

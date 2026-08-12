import { useRef, useState } from "react";
import { useCreateSettlement, usePreviewSettlement } from "../../settlement/hook/useStaffSettlement";
import styles from "./PendingSettlementConsignmentView.module.css";
import type { ConsignmentItemResponse } from "../types/staffConsignment.type";

interface Props {
  consignmentId: number;
}

export const PendingSettlementConsignmentView = ({ consignmentId }: Props) => {
  const { data: preview, isLoading, isError } = usePreviewSettlement(consignmentId);
  const { mutateAsync: createSettlement, isPending: submitting } = useCreateSettlement();

  const [showModal, setShowModal] = useState(false);
  const [proofFile, setProofFile] = useState<File | null>(null);
  const [proofPreviewUrl, setProofPreviewUrl] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setProofFile(file);
    if (file) {
      setProofPreviewUrl(URL.createObjectURL(file));
    } else {
      setProofPreviewUrl(null);
    }
  };

  const handleOpenModal = () => {
    setProofFile(null);
    setProofPreviewUrl(null);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    if (submitting) return;
    setShowModal(false);
    setProofFile(null);
    setProofPreviewUrl(null);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleSubmitSettlement = async () => {
    if (!proofFile) return;
    try {
      await createSettlement({ consignmentId, file: proofFile });
      alert("Quyết toán lô hàng thành công");
      setShowModal(false);
    } catch (err: any) {
      alert(err.response?.data?.message || "Lỗi khi thực hiện quyết toán");
    }
  };

  if (isLoading) {
    return <div className={styles.stateBox}>Đang tính toán thông tin quyết toán...</div>;
  }

  if (isError || !preview) {
    return <div className={styles.stateBox}>Đã có lỗi xảy ra khi tải xem trước quyết toán</div>;
  }

  const soldItems: ConsignmentItemResponse[] = preview.soldItems || [];
  const returnItems: ConsignmentItemResponse[] = preview.returnItems || [];
  const reserveItems: ConsignmentItemResponse[] = preview.reserveItems || [];
  const hasBankInfo = preview.bankName && preview.accountNumber && preview.accountHolder;

  return (
    <div className={styles.container}>
      {!preview.canSettle && (
        <div className={styles.warningBanner}>
          {!hasBankInfo
            ? "Khách hàng chưa cập nhật đủ thông tin tài khoản ngân hàng. Vui lòng yêu cầu khách hàng cập nhật trước khi quyết toán."
            : reserveItems.length > 0
            ? `Lô hàng còn ${reserveItems.length} sản phẩm đang trong đơn hàng chưa hoàn tất. Chờ đơn hàng kết thúc rồi mới có thể quyết toán.`
            : "Lô hàng chưa đủ điều kiện quyết toán."}
        </div>
      )}

      {/* --- Thông tin tài khoản --- */}
      <div className={styles.card}>
        <div className={styles.cardHeader}>Thông tin tài khoản nhận tiền</div>
        <div className={styles.bankGrid}>
          <div className={styles.infoGroup}>
            <span className={styles.infoLabel}>Mã khách hàng:</span>
            <span className={styles.infoValue}>#{preview.consignorId}</span>
          </div>
          <div className={styles.infoGroup}>
            <span className={styles.infoLabel}>Tên khách hàng:</span>
            <span className={styles.infoValue}>{preview.consignorName}</span>
          </div>
          <div className={styles.infoGroup}>
            <span className={styles.infoLabel}>Ngân hàng:</span>
            <span className={hasBankInfo ? styles.infoValue : styles.infoValueMissing}>
              {preview.bankName || "Chưa cập nhật"}
            </span>
          </div>
          <div className={styles.infoGroup}>
            <span className={styles.infoLabel}>Số tài khoản:</span>
            <span className={hasBankInfo ? styles.infoValue : styles.infoValueMissing}>
              {preview.accountNumber || "Chưa cập nhật"}
            </span>
          </div>
          <div className={styles.infoGroup}>
            <span className={styles.infoLabel}>Chủ tài khoản:</span>
            <span className={hasBankInfo ? styles.infoValue : styles.infoValueMissing}>
              {preview.accountHolder || "Chưa cập nhật"}
            </span>
          </div>
        </div>
      </div>

      {/* --- Tổng kết tài chính --- */}
      <div className={styles.card}>
        <div className={styles.cardHeader}>Tổng kết tài chính</div>
        <div className={styles.summaryGrid}>
          <div className={styles.summaryBox}>
            <span className={styles.summaryLabel}>Tổng doanh số bán được</span>
            <span className={styles.summaryValue}>
              {preview.totalSoldAmount?.toLocaleString("vi-VN")} đ
            </span>
          </div>
          <div className={styles.summaryBox}>
            <span className={styles.summaryLabel}>Phí hoa hồng cửa hàng</span>
            <span className={styles.summaryValue}>
              {preview.totalCommissionAmount?.toLocaleString("vi-VN")} đ
            </span>
          </div>
          <div className={`${styles.summaryBox} ${styles.netBox}`}>
            <span className={styles.summaryLabel}>Số tiền cần chuyển khoản</span>
            <span className={styles.netValue}>
              {preview.netAmount?.toLocaleString("vi-VN")} đ
            </span>
          </div>
        </div>
      </div>

      {/* --- Bảng sản phẩm đã bán --- */}
      <div className={styles.tableSection}>
        <h3 className={styles.sectionTitle}>
          Sản phẩm đã bán ({preview.soldItemCount ?? soldItems.length})
        </h3>
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
              {soldItems.length === 0 ? (
                <tr>
                  <td colSpan={4} className={styles.emptyCell}>Không có món nào bán được</td>
                </tr>
              ) : (
                soldItems.map((item) => (
                  <tr key={item.id}>
                    <td className={styles.codeCell}>#{item.id}</td>
                    <td>{item.productDetailResponse?.name}</td>
                    <td>{item.productDetailResponse?.currentPrice?.toLocaleString("vi-VN")} đ</td>
                    <td>{(item.commissionRate * 100).toFixed(0)}%</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* --- Bảng sản phẩm đang trong đơn (chỉ hiện khi có) --- */}
      {reserveItems.length > 0 && (
        <div className={styles.tableSection}>
          <h3 className={styles.sectionTitleWarn}>
            Đang trong đơn hàng — chưa hoàn tất ({reserveItems.length})
          </h3>
          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Mã món</th>
                  <th>Tên sản phẩm</th>
                  <th>Giá niêm yết</th>
                </tr>
              </thead>
              <tbody>
                {reserveItems.map((item) => (
                  <tr key={item.id}>
                    <td className={styles.codeCell}>#{item.id}</td>
                    <td>{item.productDetailResponse?.name}</td>
                    <td>{item.productDetailResponse?.currentPrice?.toLocaleString("vi-VN")} đ</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* --- Bảng sản phẩm trả lại --- */}
      <div className={styles.tableSection}>
        <h3 className={styles.sectionTitle}>
          Sản phẩm trả lại cho khách ({returnItems.length})
        </h3>
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Mã món</th>
                <th>Tên sản phẩm</th>
                <th>Giá niêm yết</th>
              </tr>
            </thead>
            <tbody>
              {returnItems.length === 0 ? (
                <tr>
                  <td colSpan={3} className={styles.emptyCell}>Không có món nào hoàn trả</td>
                </tr>
              ) : (
                returnItems.map((item) => (
                  <tr key={item.id}>
                    <td className={styles.codeCell}>#{item.id}</td>
                    <td>{item.productDetailResponse?.name}</td>
                    <td>{item.productDetailResponse?.currentPrice?.toLocaleString("vi-VN")} đ</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* --- Nút mở modal --- */}
      <div className={styles.footerAction}>
        <button
          className={styles.btnSettle}
          disabled={!preview.canSettle}
          onClick={handleOpenModal}
        >
          Tiến hành quyết toán
        </button>
      </div>

      {/* ════════ MODAL ════════ */}
      {showModal && (
        <div className={styles.modalOverlay} onClick={handleCloseModal}>
          <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
            <div className={styles.modalHeader}>
              <span>Xác nhận chuyển khoản quyết toán</span>
              <button className={styles.modalClose} onClick={handleCloseModal} disabled={submitting}>✕</button>
            </div>

            <div className={styles.modalBody}>
              {/* Thông tin chuyển khoản */}
              <div className={styles.transferInfo}>
                <div className={styles.transferRow}>
                  <span className={styles.transferLabel}>Ngân hàng</span>
                  <span className={styles.transferValue}>{preview.bankName}</span>
                </div>
                <div className={styles.transferRow}>
                  <span className={styles.transferLabel}>Số tài khoản</span>
                  <span className={styles.transferValue}>{preview.accountNumber}</span>
                </div>
                <div className={styles.transferRow}>
                  <span className={styles.transferLabel}>Chủ tài khoản</span>
                  <span className={styles.transferValue}>{preview.accountHolder}</span>
                </div>
                <div className={`${styles.transferRow} ${styles.transferAmountRow}`}>
                  <span className={styles.transferLabel}>Số tiền chuyển</span>
                  <span className={styles.transferAmount}>
                    {preview.netAmount?.toLocaleString("vi-VN")} đ
                  </span>
                </div>
              </div>

              {/* Upload biên lai */}
              <div className={styles.uploadSection}>
                <label className={styles.uploadLabel}>
                  Ảnh biên lai chuyển khoản <span className={styles.required}>*</span>
                </label>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  className={styles.fileInput}
                  onChange={handleFileChange}
                />
                {proofPreviewUrl && (
                  <img
                    src={proofPreviewUrl}
                    alt="Xem trước biên lai"
                    className={styles.proofPreview}
                  />
                )}
              </div>
            </div>

            <div className={styles.modalFooter}>
              <button
                className={styles.btnCancel}
                onClick={handleCloseModal}
                disabled={submitting}
              >
                Hủy
              </button>
              <button
                className={styles.btnConfirm}
                disabled={!proofFile || submitting}
                onClick={handleSubmitSettlement}
              >
                {submitting ? "Đang xử lý..." : "Quyết toán"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

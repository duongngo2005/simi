import { useParams, useNavigate } from "react-router";
import {
  useGetConsignmentDetail,
  useGetMySettlement,
  useGetMyDispositions,
} from "../hooks/useConsignment";
import { formatPrice } from "../../../utils/formatPrice";
import styles from "./MyConsignmentDetailPage.module.css";

const STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang ký gửi",
  PENDING_SETTLEMENT: "Chờ kết toán",
  SETTLED: "Đã kết toán",
  CLOSED: "Đã hoàn tất",
};

const ITEM_STATUS_LABEL: Record<string, string> = {
  DRAFT: "Nháp",
  ACTIVE: "Đang bán",
  RESERVED: "Đang giữ chỗ",
  EXPIRED: "Hết hạn",
  SOLD: "Đã bán",
  CANCELLED: "Đã hủy",
  RETURNED: "Đã trả lại",
  DONATED: "Đã quyên góp",
};

const DISPOSITION_TYPE_LABEL: Record<string, string> = {
  RETURN: "Trả hàng",
  DONATE: "Quyên góp",
};

const DISPOSITION_STATUS_LABEL: Record<string, string> = {
  PENDING: "Chờ nhận lại",
  CONFIRM: "Chờ xác nhận quyên góp",
  COMPLETED: "Đã xử lý",
  CANCELLED: "Đã hủy",
};

export const MyConsignmentDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const consignmentId = Number(id) || 0;

  const { data: detail, isLoading, isError } = useGetConsignmentDetail(consignmentId);
  const { data: settlement } = useGetMySettlement(consignmentId);
  const { data: dispositions = [] } = useGetMyDispositions(consignmentId);

  const consignment = detail?.consignmentResponse;
  const items = detail?.consignmentItemResponses ?? [];

  const soldCount = items.filter((i) => i.status === "SOLD").length;
  const activeCount = items.filter((i) => i.status === "ACTIVE").length;
  const expiredCount = items.filter((i) => i.status === "EXPIRED").length;
  const reservedCount = items.filter((i) => i.status === "RESERVED").length;

  if (isLoading) {
    return (
      <div className={styles.page}>
        <div className={styles.stateBox}>Đang tải chi tiết lô ký gửi...</div>
      </div>
    );
  }

  if (isError || !detail || !consignment) {
    return (
      <div className={styles.page}>
        <div className={styles.stateBox}>Không thể tải chi tiết lô ký gửi.</div>
      </div>
    );
  }

  return (
    <div className={styles.page}>
      <div className={styles.topBar}>
        <button className={styles.btnBack} onClick={() => navigate(-1)}>
          ← Quay lại danh sách
        </button>
        <div className={styles.titleGroup}>
          <div>
            <p className={styles.eyebrow}>Hộ chiếu ký gửi</p>
            <h1 className={styles.pageTitle}>Lô #{String(consignment.id).padStart(4, "0")}</h1>
          </div>
          <span className={`${styles.statusBadge} ${styles[`status${consignment.status}`]}`}>
            {STATUS_LABEL[consignment.status] || consignment.status}
          </span>
        </div>
      </div>

      <div className={styles.infoCard}>
        <h2 className={styles.sectionTitle}>Thông tin lô ký gửi</h2>
        <div className={styles.infoGrid}>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Ngày gửi</span>
            <span className={styles.infoValue}>
              {consignment.startDate
                ? new Date(consignment.startDate).toLocaleDateString("vi-VN")
                : "-"}
            </span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Hạn ký gửi</span>
            <span className={styles.infoValue}>
              {consignment.expiryDate
                ? new Date(consignment.expiryDate).toLocaleDateString("vi-VN")
                : "-"}
            </span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Nhân viên tiếp nhận</span>
            <span className={styles.infoValue}>{consignment.receivedName || "-"}</span>
          </div>
          {consignment.note && (
            <div className={styles.infoItemFull}>
              <span className={styles.infoLabel}>Ghi chú</span>
              <span className={styles.infoValue}>{consignment.note}</span>
            </div>
          )}
        </div>
      </div>

      <div className={styles.summaryBar}>
        <div className={styles.summaryItem}>
          <span className={styles.summaryNumber}>{items.length}</span>
          <span className={styles.summaryLabel}>Tổng số</span>
        </div>
        <div className={styles.summaryItem}>
          <span className={styles.summaryNumber}>{activeCount}</span>
          <span className={styles.summaryLabel}>Đang bán</span>
        </div>
        <div className={styles.summaryItem}>
          <span className={styles.summaryNumber}>{reservedCount}</span>
          <span className={styles.summaryLabel}>Đang giữ</span>
        </div>
        <div className={styles.summaryItem}>
          <span className={styles.summaryNumber}>{soldCount}</span>
          <span className={styles.summaryLabel}>Đã bán</span>
        </div>
        <div className={styles.summaryItem}>
          <span className={styles.summaryNumber}>{expiredCount}</span>
          <span className={styles.summaryLabel}>Hết hạn</span>
        </div>
      </div>

      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>Danh sách sản phẩm ký gửi</h2>
        <div className={styles.itemList}>
          {items.map((item) => {
            const product = item.productDetailResponse;
            const price = product?.currentPrice || 0;
            const thumb =
              product?.thumbnail || product?.productImageResponses?.[0]?.imageUrl;

            return (
              <div key={item.id} className={styles.itemCard}>
                {thumb ? (
                  <img src={thumb} alt={product?.name} className={styles.itemThumb} />
                ) : (
                  <div className={styles.itemNoThumb}>Ảnh đang cập nhật</div>
                )}

                <div className={styles.itemInfo}>
                  <span className={styles.itemName}>{product?.name || "Sản phẩm"}</span>
                  <div className={styles.itemMeta}>
                    {product?.size && <span>Size: {product.size}</span>}
                    {product?.color && <span>Màu: {product.color}</span>}
                  </div>
                </div>

                <div className={styles.itemRight}>
                  <span className={`${styles.itemStatusBadge} ${styles[`itemStatus${item.status}`]}`}>
                    {ITEM_STATUS_LABEL[item.status] || item.status}
                  </span>
                  <span className={styles.itemPrice}>{formatPrice(price)}</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {settlement && (
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Thông tin kết toán</h2>
          <div className={styles.settlementCard}>
            <div className={styles.settlementRow}>
              <span>Tổng tiền bán được</span>
              <span className={styles.settlementAmount}>
                {formatPrice(settlement.totalSoldAmount)}
              </span>
            </div>
            <div className={styles.settlementRow}>
              <span>Phí hoa hồng Simi</span>
              <span className={styles.settlementDeduct}>
                -{formatPrice(settlement.totalCommissionAmount)}
              </span>
            </div>
            <div className={styles.settlementDivider} />
            <div className={styles.settlementRowTotal}>
              <span>Số tiền thực nhận</span>
              <span className={styles.settlementTotal}>
                {formatPrice(settlement.netAmount)}
              </span>
            </div>

            <div className={styles.settlementDivider} />

            <div className={styles.settlementDetails}>
              <div className={styles.settlementDetail}>
                <span className={styles.infoLabel}>Ngày kết toán</span>
                <span className={styles.infoValue}>
                  {new Date(settlement.settledAt).toLocaleDateString("vi-VN")}
                </span>
              </div>
              <div className={styles.settlementDetail}>
                <span className={styles.infoLabel}>Hình thức</span>
                <span className={styles.infoValue}>
                  {settlement.paymentMethod === "ONLINE" ? "Chuyển khoản" : settlement.paymentMethod}
                </span>
              </div>
              <div className={styles.settlementDetail}>
                <span className={styles.infoLabel}>Chủ tài khoản</span>
                <span className={styles.infoValue}>{settlement.accountHolder}</span>
              </div>
              <div className={styles.settlementDetail}>
                <span className={styles.infoLabel}>Số tài khoản</span>
                <span className={styles.infoValue}>{settlement.accountNumber}</span>
              </div>
              <div className={styles.settlementDetail}>
                <span className={styles.infoLabel}>Ngân hàng</span>
                <span className={styles.infoValue}>{settlement.bankName}</span>
              </div>
            </div>

            {settlement.proofImageUrl && (
              <div className={styles.proofSection}>
                <span className={styles.infoLabel}>Ảnh xác nhận chuyển khoản</span>
                <img
                  src={settlement.proofImageUrl}
                  alt="Xác nhận chuyển khoản"
                  className={styles.proofImage}
                />
              </div>
            )}
          </div>
        </div>
      )}

      {dispositions.length > 0 && (
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Hàng chờ xử lý</h2>
          <div className={styles.dispositionList}>
            {dispositions.map((d) => (
              <div key={d.id} className={styles.dispositionCard}>
                <div className={styles.dispositionHeader}>
                  <span className={styles.dispositionName}>{d.productName || "Sản phẩm"}</span>
                  <span
                    className={`${styles.dispositionTypeBadge} ${
                      d.type === "RETURN" ? styles.typeReturn : styles.typeDonate
                    }`}
                  >
                    {DISPOSITION_TYPE_LABEL[d.type] || d.type}
                  </span>
                </div>
                <div className={styles.dispositionBody}>
                  <span className={styles.dispositionStatus}>
                    {DISPOSITION_STATUS_LABEL[d.status] || d.status}
                  </span>
                  {d.type === "RETURN" && d.pickupDeadline && (
                    <span className={styles.dispositionDeadline}>
                      Hạn nhận lại: {new Date(d.pickupDeadline).toLocaleDateString("vi-VN")}
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

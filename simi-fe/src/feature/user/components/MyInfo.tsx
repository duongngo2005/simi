import { useState, type FormEvent } from "react";
import { useAuthStore } from "../../../store/useAuthStore";
import { userApi } from "../api/userApi";
import styles from "./MyInfo.module.css";

const ROLE_LABEL: Record<string, string> = {
  ADMIN: "Quản trị viên",
  STAFF: "Nhân viên",
  CUSTOMER: "Khách hàng",
};

const STATUS_LABEL: Record<string, string> = {
  ACTIVE: "Đang hoạt động",
  LOCKED: "Đã bị khóa",
};

export const MyInfo = () => {
  const { user, setUser } = useAuthStore();
  const [bankName, setBankName] = useState(user?.bankName ?? "");
  const [accountNumber, setAccountNumber] = useState(user?.accountNumber ?? "");
  const [accountHolder, setAccountHolder] = useState(user?.accountHolder ?? "");
  const [isSaving, setIsSaving] = useState(false);
  const [feedback, setFeedback] = useState<{ type: "success" | "error"; message: string } | null>(null);

  if (!user) return null;

  const handleBankInformationSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFeedback(null);
    setIsSaving(true);

    try {
      const response = await userApi.updateBankInformation({
        bankName,
        accountNumber,
        accountHolder,
      });
      setUser(response.body);
      setFeedback({ type: "success", message: "Đã cập nhật thông tin nhận tiền." });
    } catch {
      setFeedback({ type: "error", message: "Không thể cập nhật. Vui lòng kiểm tra lại thông tin." });
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.card}>
        <div className={styles.cardHeader}>
          <p className={styles.eyebrow}>Thông tin đã đăng ký</p>
          <h2 className={styles.cardTitle}>Thông tin cá nhân</h2>
        </div>

        <div className={styles.infoGrid}>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Họ và tên</span>
            <span className={styles.infoValue}>{user.fullName}</span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Email</span>
            <span className={styles.infoValue}>{user.email}</span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Số điện thoại</span>
            <span className={styles.infoValue}>
              {user.phoneNumber ?? <span className={styles.empty}>Chưa cập nhật</span>}
            </span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Địa chỉ</span>
            <span className={styles.infoValue}>
              {user.address ?? <span className={styles.empty}>Chưa cập nhật</span>}
            </span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Vai trò</span>
            <span className={styles.infoValue}>{ROLE_LABEL[user.role] ?? user.role}</span>
          </div>

          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Trạng thái tài khoản</span>
            <span
              className={`${styles.statusBadge} ${
                user.status === "ACTIVE" ? styles.statusActive : styles.statusLocked
              }`}
            >
              {STATUS_LABEL[user.status] ?? user.status}
            </span>
          </div>
        </div>
      </div>

      {user.role === "CUSTOMER" && (
        <form className={styles.card} onSubmit={handleBankInformationSubmit}>
          <div className={styles.cardHeader}>
            <p className={styles.eyebrow}>Dùng khi quyết toán</p>
            <h2 className={styles.cardTitle}>Thông tin nhận tiền ký gửi</h2>
            <p className={styles.cardDescription}>
              Thông tin này được dùng khi Simi quyết toán tiền bán sản phẩm ký gửi.
            </p>
          </div>

          <div className={styles.infoGrid}>
            <label className={styles.infoItem}>
              <span className={styles.infoLabel}>Ngân hàng</span>
              <input
                className={styles.input}
                value={bankName}
                onChange={(event) => setBankName(event.target.value)}
                maxLength={100}
                required
              />
            </label>

            <label className={styles.infoItem}>
              <span className={styles.infoLabel}>Số tài khoản</span>
              <input
                className={styles.input}
                value={accountNumber}
                onChange={(event) => setAccountNumber(event.target.value)}
                maxLength={50}
                required
              />
            </label>

            <label className={styles.infoItem}>
              <span className={styles.infoLabel}>Chủ tài khoản</span>
              <input
                className={styles.input}
                value={accountHolder}
                onChange={(event) => setAccountHolder(event.target.value)}
                maxLength={100}
                required
              />
            </label>
          </div>

          {feedback && (
            <p className={feedback.type === "success" ? styles.feedbackSuccess : styles.feedbackError}>
              {feedback.message}
            </p>
          )}

          <div>
            <button className={styles.btnPrimary} type="submit" disabled={isSaving}>
              {isSaving ? "Đang lưu..." : "Lưu thông tin nhận tiền"}
            </button>
          </div>
        </form>
      )}
    </div>
  );
};

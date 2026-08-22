import { useSearchParams, Link } from "react-router";
import styles from "./PaymentResultPage.module.css";

export const PaymentResultPage = () => {
    const [searchParams] = useSearchParams();

    const responseCode = searchParams.get("vnp_ResponseCode");
    const txnRef = searchParams.get("vnp_TxnRef");
    const amount = searchParams.get("vnp_Amount");
    const bankCode = searchParams.get("vnp_BankCode");
    const transactionNo = searchParams.get("vnp_TransactionNo");

    const isSuccess = responseCode === "00";
    const formattedAmount = amount
        ? new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" })
              .format(Number(amount) / 100)
        : "";

    return (
        <div className={styles.page}>
            <div className={styles.card}>
                {/* Icon */}
                <div className={styles.iconWrapper}>
                    <div className={`${styles.iconCircle} ${isSuccess ? styles.success : styles.failed}`}>
                        {isSuccess ? (
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"
                                 className={styles.icon}>
                                <polyline points="20 6 9 17 4 12" />
                            </svg>
                        ) : (
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"
                                 className={styles.icon}>
                                <line x1="18" y1="6" x2="6" y2="18" />
                                <line x1="6" y1="6" x2="18" y2="18" />
                            </svg>
                        )}
                    </div>
                </div>

                <h1 className={styles.title}>
                    {isSuccess ? "Thanh toán thành công!" : "Thanh toán thất bại"}
                </h1>
                <p className={styles.subtitle}>
                    {isSuccess
                        ? "Cảm ơn bạn đã mua sắm tại Simi. Đơn hàng đang được xử lý."
                        : "Giao dịch không thành công. Bạn có thể thử lại từ trang đơn hàng."
                    }
                </p>

                <div className={styles.details}>
                    {txnRef && (
                        <div className={styles.row}>
                            <span>Mã giao dịch</span>
                            <span>{txnRef}</span>
                        </div>
                    )}
                    {formattedAmount && (
                        <div className={styles.row}>
                            <span>Số tiền</span>
                            <strong>{formattedAmount}</strong>
                        </div>
                    )}
                    {bankCode && (
                        <div className={styles.row}>
                            <span>Ngân hàng</span>
                            <span>{bankCode}</span>
                        </div>
                    )}
                    {transactionNo && (
                        <div className={styles.row}>
                            <span>Mã VNPay</span>
                            <span>{transactionNo}</span>
                        </div>
                    )}
                </div>

                {/* Nút điều hướng */}
                <div className={styles.actions}>
                    <Link to="/" className={styles.btnPrimary}>
                        Tiếp tục mua sắm
                    </Link>
                    <Link to="/my-orders" className={styles.btnSecondary}>
                        Xem đơn hàng của tôi
                    </Link>
                </div>
            </div>
        </div>
    );
};
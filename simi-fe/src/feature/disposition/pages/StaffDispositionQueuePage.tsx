import { useSearchParams } from "react-router";
import { useQuery } from "@tanstack/react-query";
import styles from "./StaffDispositionQueuePage.module.css";
import type { ApiResponse, PageResponse } from "../../../types/common";
import api from "../../../lib/http/apiClient";

interface ItemDisposition {
  id: number;
  type: "RETURN" | "DONATE";
  status: "PENDING" | "CONFIRM" | "COMPLETED" | "CANCELLED";
  productName: string;
  consignmentId: number;
  pickupDeadline: string | null;
}

const STATUS_LABEL: Record<ItemDisposition["status"], string> = {
  PENDING: "Chờ xử lý",
  CONFIRM: "Chờ xác nhận quyên góp",
  COMPLETED: "Hoàn thành",
  CANCELLED: "Đã hủy",
};

export function StaffDispositionQueuePage() {
  const [params] = useSearchParams();

  const type = params.get("type") ?? "";
  const status = params.get("status") ?? "";

  const { data, isLoading } = useQuery({
    queryKey: ["staff-dispositions", type, status],
    queryFn: async () => {
      const response = await api.get<
        ApiResponse<PageResponse<ItemDisposition>>
      >("/item-dispositions", {
        params: {
          type,
          status,
          page: 0,
          size: 30,
          sortBy: "createdDate",
          sortDir: "asc",
        },
      });

      return response.data.body;
    },
  });

  return (
    <section className={styles.page}>
      <header className={styles.header}>
        <h1 className={styles.title}>Hàng đợi bàn giao</h1>

        <p className={styles.description}>
          {type === "DONATE"
            ? "Món cần xác nhận đã được bàn giao quyên góp."
            : "Món đang chờ trả lại cho chủ hàng."}
        </p>
      </header>

      <div className={styles.tableContainer}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Item</th>
              <th>Lô ký gửi</th>
              <th>Hạn nhận</th>
              <th>Trạng thái</th>
            </tr>
          </thead>

          <tbody>
            {isLoading && (
              <tr>
                <td colSpan={4} className={styles.messageCell}>
                  Đang tải…
                </td>
              </tr>
            )}

            {!isLoading && data?.content.length === 0 && (
              <tr>
                <td colSpan={4} className={styles.messageCell}>
                  Không có item trong hàng đợi.
                </td>
              </tr>
            )}

            {data?.content.map((item) => (
              <tr key={item.id}>
                <td>
                  #{item.id} · {item.productName}
                </td>

                <td>#{item.consignmentId}</td>

                <td>
                  {item.pickupDeadline
                    ? new Date(item.pickupDeadline).toLocaleDateString("vi-VN")
                    : "—"}
                </td>

                <td>{STATUS_LABEL[item.status]}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
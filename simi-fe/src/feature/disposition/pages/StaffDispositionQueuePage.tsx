import { useState, useMemo } from "react";
import { useSearchParams } from "react-router";
import {
  useConfirmDonation,
  useConfirmReturn,
  useItemDispositions,
} from "../hooks/useDispositions";
import type { ItemDisposition } from "../types/disposition.type";
import styles from "./StaffDispositionQueuePage.module.css";

type TabId = "RETURN_PENDING" | "DONATE_CONFIRM" | "COMPLETED";

interface TabItem {
  id: TabId;
  label: string;
  type: string;
  status: string;
}

const TABS: TabItem[] = [
  { id: "RETURN_PENDING", label: "Chờ trả khách", type: "RETURN", status: "PENDING" },
  { id: "DONATE_CONFIRM", label: "Chờ quyên góp", type: "DONATE", status: "CONFIRM" },
  { id: "COMPLETED", label: "Lịch sử hoàn tất", type: "", status: "COMPLETED" },
];

export function StaffDispositionQueuePage() {
  const [searchParams, setSearchParams] = useSearchParams();

  const currentTab: TabId = useMemo(() => {
    const type = searchParams.get("type");
    const status = searchParams.get("status");
    if (type === "DONATE" && status === "CONFIRM") return "DONATE_CONFIRM";
    if (status === "COMPLETED") return "COMPLETED";
    return "RETURN_PENDING";
  }, [searchParams]);

  const activeTabConfig = TABS.find((t) => t.id === currentTab) ?? TABS[0];

  const [keyword, setKeyword] = useState("");
  const [debouncedKeyword, setDebouncedKeyword] = useState("");
  const [page, setPage] = useState(0);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const filter = useMemo(
    () => ({
      keyword: debouncedKeyword,
      type: activeTabConfig.type,
      status: activeTabConfig.status,
      page,
      size: 15,
      sortBy: "createdDate",
      sortDir: "desc",
    }),
    [debouncedKeyword, activeTabConfig, page],
  );

  const { data, isLoading, isFetching } = useItemDispositions(filter);
  const confirmReturnMutation = useConfirmReturn();
  const confirmDonationMutation = useConfirmDonation();

  const handleTabClick = (tab: TabItem) => {
    const nextParams = new URLSearchParams();
    if (tab.type) nextParams.set("type", tab.type);
    if (tab.status) nextParams.set("status", tab.status);
    setSearchParams(nextParams);
    setPage(0);
    setSelectedIds([]);
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setDebouncedKeyword(keyword);
    setPage(0);
    setSelectedIds([]);
  };

  const handleSelectAll = (items: ItemDisposition[]) => {
    if (currentTab === "COMPLETED") return;
    if (selectedIds.length === items.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(items.map((i) => i.id));
    }
  };

  const handleToggleItem = (id: number) => {
    if (currentTab === "COMPLETED") return;
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id],
    );
  };

  const handleExecuteBatchAction = async () => {
    if (selectedIds.length === 0) return;

    if (currentTab === "RETURN_PENDING") {
      await confirmReturnMutation.mutateAsync({ itemDispositionIds: selectedIds });
    } else if (currentTab === "DONATE_CONFIRM") {
      await confirmDonationMutation.mutateAsync({ itemDispositionIds: selectedIds });
    }

    setSelectedIds([]);
    setIsModalOpen(false);
  };

  const items = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const isMutating = confirmReturnMutation.isPending || confirmDonationMutation.isPending;

  const isReturnTab = currentTab === "RETURN_PENDING";
  const isDonateTab = currentTab === "DONATE_CONFIRM";
  const isCompletedTab = currentTab === "COMPLETED";

  const colSpanCount = isReturnTab ? 6 : isDonateTab ? 5 : 5;

  return (
    <div className={styles.container}>
      <header className={styles.topBar}>
        <div>
          <p className={styles.eyebrow}>Hàng sau ký gửi</p>
          <h1 className={styles.pageTitle}>Xử lý trả hàng & Quyên góp</h1>
        </div>
      </header>

      <div className={styles.tabsBar}>
        {TABS.map((tab) => {
          const isActive = currentTab === tab.id;
          return (
            <button
              key={tab.id}
              type="button"
              className={`${styles.tabBtn} ${isActive ? styles.tabBtnActive : ""}`}
              onClick={() => handleTabClick(tab)}
            >
              {tab.label}
            </button>
          );
        })}
      </div>

      <div className={styles.filterCard}>
        <form onSubmit={handleSearchSubmit} className={styles.searchForm}>
          <input
            type="text"
            className={styles.searchInput}
            placeholder="Tìm theo tên món đồ, mã lô, tên hoặc SĐT chủ hàng..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <button type="submit" className={styles.btnSearch}>
            Tìm kiếm
          </button>
          {debouncedKeyword && (
            <button
              type="button"
              className={styles.btnClear}
              onClick={() => {
                setKeyword("");
                setDebouncedKeyword("");
                setPage(0);
              }}
            >
              Xóa lọc
            </button>
          )}
        </form>
      </div>

      {!isCompletedTab && selectedIds.length > 0 && (
        <div className={styles.batchBar}>
          <span className={styles.batchSelectedText}>
            Đã chọn <strong>{selectedIds.length}</strong> món hàng
          </span>
          <div className={styles.batchActions}>
            {isReturnTab && (
              <button
                type="button"
                className={styles.btnConfirmReturn}
                onClick={() => setIsModalOpen(true)}
                disabled={isMutating}
              >
                Xác nhận đã trả khách ({selectedIds.length})
              </button>
            )}
            {isDonateTab && (
              <button
                type="button"
                className={styles.btnConfirmDonate}
                onClick={() => setIsModalOpen(true)}
                disabled={isMutating}
              >
                Xác nhận đã quyên góp ({selectedIds.length})
              </button>
            )}
            <button
              type="button"
              className={styles.btnCancelSelection}
              onClick={() => setSelectedIds([])}
            >
              Bỏ chọn
            </button>
          </div>
        </div>
      )}

      <div className={styles.tableCard}>
        <div className={styles.tableWrapper}>
          <table className={styles.table}>
            <thead>
              <tr>
                {!isCompletedTab && (
                  <th style={{ width: "40px", textAlign: "center" }}>
                    <input
                      type="checkbox"
                      checked={items.length > 0 && selectedIds.length === items.length}
                      onChange={() => handleSelectAll(items)}
                      disabled={isLoading || items.length === 0}
                    />
                  </th>
                )}
                <th>Món hàng</th>
                <th style={{ width: "90px", textAlign: "center" }}>Mã lô</th>
                <th>Chủ ký gửi</th>
                {isReturnTab && (
                  <th style={{ width: "130px", textAlign: "center" }}>Hạn nhận lại</th>
                )}
                {isCompletedTab && (
                  <th style={{ width: "120px", textAlign: "center" }}>Hình thức</th>
                )}
                {isCompletedTab && (
                  <th style={{ width: "110px", textAlign: "center" }}>Trạng thái</th>
                )}
                {!isCompletedTab && (
                  <th style={{ width: "110px", textAlign: "center" }}>Thao tác</th>
                )}
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                <tr>
                  <td colSpan={colSpanCount} className={styles.centerText}>
                    Đang tải danh sách hàng đợi...
                  </td>
                </tr>
              ) : items.length === 0 ? (
                <tr>
                  <td colSpan={colSpanCount} className={styles.centerText}>
                    Không có món hàng nào trong danh sách.
                  </td>
                </tr>
              ) : (
                items.map((item) => {
                  const isSelected = selectedIds.includes(item.id);
                  const isExpired =
                    item.pickupDeadline && new Date(item.pickupDeadline) < new Date();

                  return (
                    <tr key={item.id} className={isSelected ? styles.rowSelected : ""}>
                      {!isCompletedTab && (
                        <td style={{ textAlign: "center" }}>
                          <input
                            type="checkbox"
                            checked={isSelected}
                            onChange={() => handleToggleItem(item.id)}
                          />
                        </td>
                      )}

                      <td>
                        <span className={styles.productName}>{item.productName || "Sản phẩm"}</span>
                      </td>

                      <td style={{ textAlign: "center" }}>
                        <span className={styles.consignmentId}>#{item.consignmentId}</span>
                      </td>

                      <td>
                        <div className={styles.consignorInfo}>
                          <span className={styles.consignorName}>{item.consignorName || "Khách hàng"}</span>
                          <span className={styles.consignorPhone}>{item.consignorPhone || "-"}</span>
                        </div>
                      </td>

                      {isReturnTab && (
                        <td className={styles.dateCell}>
                          <span className={isExpired ? styles.deadlinePassed : ""}>
                            {item.pickupDeadline
                              ? new Date(item.pickupDeadline).toLocaleDateString("vi-VN")
                              : "-"}
                          </span>
                        </td>
                      )}

                      {isCompletedTab && (
                        <td style={{ textAlign: "center" }}>
                          <span
                            className={`${styles.typeBadge} ${
                              item.type === "RETURN" ? styles.typeReturn : styles.typeDonate
                            }`}
                          >
                            {item.type === "RETURN" ? "Đã trả khách" : "Đã quyên góp"}
                          </span>
                        </td>
                      )}

                      {isCompletedTab && (
                        <td style={{ textAlign: "center" }}>
                          <span className={styles.completedBadge}>Đã xong</span>
                        </td>
                      )}

                      {!isCompletedTab && (
                        <td style={{ textAlign: "center" }}>
                          {isReturnTab && (
                            <button
                              type="button"
                              className={styles.btnRowAction}
                              onClick={() => {
                                setSelectedIds([item.id]);
                                setIsModalOpen(true);
                              }}
                            >
                              Đã trả
                            </button>
                          )}
                          {isDonateTab && (
                            <button
                              type="button"
                              className={styles.btnRowAction}
                              onClick={() => {
                                setSelectedIds([item.id]);
                                setIsModalOpen(true);
                              }}
                            >
                              Đã tặng
                            </button>
                          )}
                        </td>
                      )}
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className={styles.pagination}>
            <span className={styles.pageInfo}>
              Trang {page + 1} / {totalPages}
            </span>
            <div className={styles.paginationBtns}>
              <button
                type="button"
                className={styles.btnPage}
                disabled={page === 0 || isFetching}
                onClick={() => setPage((p) => Math.max(p - 1, 0))}
              >
                Trang trước
              </button>
              <button
                type="button"
                className={styles.btnPage}
                disabled={page >= totalPages - 1 || isFetching}
                onClick={() => setPage((p) => p + 1)}
              >
                Trang sau
              </button>
            </div>
          </div>
        )}
      </div>

      {isModalOpen && (
        <div className={styles.backdrop} role="presentation">
          <div className={styles.modal}>
            <h3 className={styles.modalTitle}>
              {isReturnTab
                ? "Xác nhận đã trả hàng cho khách"
                : "Xác nhận đã bàn giao quyên góp"}
            </h3>
            <p className={styles.modalDesc}>
              Bạn có chắc chắn muốn xác nhận <strong>{selectedIds.length} món hàng</strong> đã được{" "}
              {isReturnTab ? "khách đến nhận lại" : "bàn giao đi từ thiện"} không?
            </p>
            <div className={styles.modalActions}>
              <button
                type="button"
                className={styles.btnModalCancel}
                onClick={() => setIsModalOpen(false)}
                disabled={isMutating}
              >
                Hủy bỏ
              </button>
              <button
                type="button"
                className={styles.btnModalSubmit}
                onClick={handleExecuteBatchAction}
                disabled={isMutating}
              >
                {isMutating ? "Đang xử lý…" : "Xác nhận"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

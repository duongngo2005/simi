import React, { useState } from "react";
import styles from "./StaffConsignmentPage.module.css";

interface ConsignmentSummary {
  id: number;
  consignorPhone: string;
  consignorName: string;
  receivedByName: string;
  startDate: string;
  expiryDate: string;
  totalItems: number;
  status: string;
  note: string;
}

// Kiểu dữ liệu mốc giá
interface PriceScheduleRow {
  effectiveAfterDays: number;
  price: string;
}

interface ConsignmentItemDetail {
  id: number;
  productName: string;
  category: string;
  size: string;
  color: string;
  condition: string;
  commissionRate: number;
  priceSchedules: PriceScheduleRow[];
}

const MOCK_CONSIGNMENTS: ConsignmentSummary[] = [
  {
    id: 101,
    consignorPhone: "0901234567",
    consignorName: "Nguyễn Văn A",
    receivedByName: "Staff Nguyễn",
    startDate: "2026-06-25T00:00:00Z",
    expiryDate: "2026-07-25T00:00:00Z",
    totalItems: 4,
    status: "ACTIVE",
    note: "Khách gửi đồ thu đông",
  },
  {
    id: 100,
    consignorPhone: "0912345678",
    consignorName: "Trần Thị B",
    receivedByName: "Staff Lê",
    startDate: "2026-05-10T00:00:00Z",
    expiryDate: "2026-06-10T00:00:00Z",
    totalItems: 2,
    status: "EXPIRED",
    note: "Đã hết hạn - Cần kết toán",
  },
];

export const StaffConsignmentPage = () => {
  const [viewMode, setViewMode] = useState<"LIST" | "DETAIL">("LIST");
  const [showInitModal, setShowInitModal] = useState(false);
  const [initPhone, setInitPhone] = useState("");
  const [initNote, setInitNote] = useState("");

  const [activeConsignment, setActiveConsignment] = useState<{
    id: number;
    phone: string;
    note: string;
  } | null>(null);

  const [draftItems, setDraftItems] = useState<ConsignmentItemDetail[]>([]);

  // State Form nhập món mới
  const [itemForm, setItemForm] = useState({
    name: "",
    category: "Áo sơ mi",
    size: "M",
    color: "Đen",
    condition: "LIKE_NEW",
    commissionRate: 30,
  });

  // 💡 STATE LỊCH GIẢM GIÁ ĐỘNG (Mặc định chỉ có 1 mốc: 0 ngày - Giá gốc)
  const [dynamicSchedules, setDynamicSchedules] = useState<PriceScheduleRow[]>([
    { effectiveAfterDays: 0, price: "" },
  ]);

  const [editingItemId, setEditingItemId] = useState<number | null>(null);

  // ── XỬ LÝ LỊCH GIÁ ĐỘNG ──
  // 1. Thêm mốc giảm giá mới
  const handleAddScheduleRow = () => {
    const lastDays = dynamicSchedules[dynamicSchedules.length - 1]?.effectiveAfterDays || 0;
    setDynamicSchedules((prev) => [
      ...prev,
      { effectiveAfterDays: lastDays + 15, price: "" }, // Mặc định tự cộng 15 ngày
    ]);
  };

  // 2. Thay đổi giá trị mốc
  const handleScheduleChange = (index: number, field: "effectiveAfterDays" | "price", value: string) => {
    setDynamicSchedules((prev) => {
      const updated = [...prev];
      if (field === "effectiveAfterDays") {
        updated[index].effectiveAfterDays = Number(value);
      } else {
        updated[index].price = value;
      }
      return updated;
    });
  };

  // 3. Xóa 1 mốc giảm giá (không cho xóa mốc 0 ngày)
  const handleRemoveScheduleRow = (index: number) => {
    if (index === 0) return alert("Không thể xóa mốc giá gốc (0 ngày)!");
    setDynamicSchedules((prev) => prev.filter((_, idx) => idx !== index));
  };

  // ── BẮT ĐẦU TẠO LÔ MỚI ──
  const handleConfirmInitModal = (e: React.FormEvent) => {
    e.preventDefault();
    if (!initPhone) return alert("Vui lòng nhập số điện thoại khách!");

    const newId = Math.floor(Math.random() * 900) + 102;
    setActiveConsignment({
      id: newId,
      phone: initPhone,
      note: initNote || "Không có ghi chú",
    });

    setShowInitModal(false);
    setInitPhone("");
    setInitNote("");
    setDraftItems([]);
    setDynamicSchedules([{ effectiveAfterDays: 0, price: "" }]);
    setViewMode("DETAIL");
  };

  // ── THÊM/SỬA MÓN ──
  const handleSaveItem = (e: React.FormEvent) => {
    e.preventDefault();
    if (!itemForm.name || !dynamicSchedules[0].price) {
      return alert("Vui lòng nhập tên và giá gốc (mốc 0 ngày)!");
    }

    if (editingItemId) {
      setDraftItems((prev) =>
        prev.map((item) =>
          item.id === editingItemId
            ? {
                ...item,
                productName: itemForm.name,
                category: itemForm.category,
                size: itemForm.size,
                color: itemForm.color,
                condition: itemForm.condition,
                commissionRate: itemForm.commissionRate,
                priceSchedules: [...dynamicSchedules],
              }
            : item
        )
      );
      setEditingItemId(null);
    } else {
      const newItem: ConsignmentItemDetail = {
        id: Date.now(),
        productName: itemForm.name,
        category: itemForm.category,
        size: itemForm.size,
        color: itemForm.color,
        condition: itemForm.condition,
        commissionRate: itemForm.commissionRate,
        priceSchedules: [...dynamicSchedules],
      };
      setDraftItems((prev) => [...prev, newItem]);
    }

    // Reset Form về mặc định 1 mốc
    setItemForm({
      name: "",
      category: "Áo sơ mi",
      size: "M",
      color: "Đen",
      condition: "LIKE_NEW",
      commissionRate: 30,
    });
    setDynamicSchedules([{ effectiveAfterDays: 0, price: "" }]);
  };

  // ── SỬA MÓN ──
  const handleEditClick = (item: ConsignmentItemDetail) => {
    setEditingItemId(item.id);
    setItemForm({
      name: item.productName,
      category: item.category,
      size: item.size,
      color: item.color,
      condition: item.condition,
      commissionRate: item.commissionRate,
    });
    setDynamicSchedules([...item.priceSchedules]);
  };

  // ── XÓA MÓN ──
  const handleDeleteItem = (itemId: number) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa món này khỏi đơn?")) {
      setDraftItems((prev) => prev.filter((i) => i.id !== itemId));
    }
  };

  // ── KÍCH HOẠT LÔ HÀNG ──
  const handleActivateConsignment = () => {
    if (draftItems.length === 0) {
      return alert("Vui lòng thêm ít nhất 1 sản phẩm trước khi kích hoạt!");
    }
    alert(`🚀 Kích hoạt lô hàng #${activeConsignment?.id} thành công!`);
    setViewMode("LIST");
  };

  return (
    <div className={styles.container}>
      {/* ── MÀN HÌNH 1: DANH SÁCH TỔNG QUAN ── */}
      {viewMode === "LIST" && (
        <>
          <div className={styles.header}>
            <div>
              <h1 className={styles.pageTitle}>Quản lý Ký Gửi (Consignment)</h1>
              <p className={styles.subtitle}>Theo dõi lô hàng, cảnh báo hết hạn và kết toán cho khách</p>
            </div>
            <button
              className={styles.btnCreate}
              onClick={() => setShowInitModal(true)}
            >
              ⚡ Tạo Lô Ký Gửi Mới
            </button>
          </div>

          <div className={styles.warningGrid}>
            <div className={`${styles.warningCard} ${styles.warningExpiring}`}>
              <div className={styles.warningIcon}>⚠️</div>
              <div>
                <span className={styles.warningCount}>1 Lô Hàng</span>
                <span className={styles.warningLabel}>Sắp hết hạn (trong 7 ngày)</span>
              </div>
            </div>
            <div className={`${styles.warningCard} ${styles.warningSettlement}`}>
              <div className={styles.warningIcon}>🚨</div>
              <div>
                <span className={styles.warningCount}>1 Lô Hàng</span>
                <span className={styles.warningLabel}>Đã hết hạn - Cần kết toán</span>
              </div>
            </div>
          </div>

          <div className={styles.tableWrapper}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Mã Lô</th>
                  <th>SĐT Khách ký gửi</th>
                  <th>Người tiếp nhận</th>
                  <th>Ngày bắt đầu</th>
                  <th>Ngày hết hạn</th>
                  <th>Số sản phẩm</th>
                  <th>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {MOCK_CONSIGNMENTS.map((item) => (
                  <tr key={item.id}>
                    <td className={styles.consignmentId}>#{item.id}</td>
                    <td><strong>{item.consignorPhone}</strong></td>
                    <td>{item.receivedByName}</td>
                    <td className={styles.dateCell}>
                      {new Date(item.startDate).toLocaleDateString("vi-VN")}
                    </td>
                    <td className={styles.dateCell}>
                      {new Date(item.expiryDate).toLocaleDateString("vi-VN")}
                    </td>
                    <td>{item.totalItems} món</td>
                    <td>
                      <span className={`${styles.statusBadge} ${styles[`status_${item.status}`]}`}>
                        {item.status === "ACTIVE" && "Đang bán (ACTIVE)"}
                        {item.status === "EXPIRED" && "Hết hạn (EXPIRED)"}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* ── MODAL NHỎ BAN ĐẦU ── */}
      {showInitModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalSmall}>
            <div className={styles.modalHeader}>
              <h3>⚡ Tạo Lô Ký Gửi Mới</h3>
              <button className={styles.btnClose} onClick={() => setShowInitModal(false)}>✕</button>
            </div>
            <form onSubmit={handleConfirmInitModal} className={styles.modalForm}>
              <div className={styles.inputGroup}>
                <label>Số điện thoại khách ký gửi *</label>
                <input
                  type="tel"
                  placeholder="Nhập số điện thoại khách (VD: 0901234567)"
                  value={initPhone}
                  onChange={(e) => setInitPhone(e.target.value)}
                  required
                />
              </div>

              <div className={styles.inputGroup}>
                <label>Ghi chú lô hàng</label>
                <input
                  type="text"
                  placeholder="Ví dụ: Đồ thu đông, gửi 5 món..."
                  value={initNote}
                  onChange={(e) => setInitNote(e.target.value)}
                />
              </div>

              <div className={styles.modalActions}>
                <button type="button" className={styles.btnCancel} onClick={() => setShowInitModal(false)}>
                  Hủy
                </button>
                <button type="submit" className={styles.btnPrimary}>
                  Bắt đầu nhập chi tiết →
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ── MÀN HÌNH 2: TRANG BIÊN TẬP DRAFT FULL ── */}
      {viewMode === "DETAIL" && activeConsignment && (
        <div className={styles.detailWorkspace}>
          <div className={styles.detailHeader}>
            <div>
              <button className={styles.btnBack} onClick={() => setViewMode("LIST")}>
                ← Quay lại danh sách
              </button>
              <h2 className={styles.detailTitle}>
                Chi Tiết Lô Ký Gửi #{activeConsignment.id}
                <span className={styles.badgeDraft}>TRẠNG THÁI: DRAFT</span>
              </h2>
              <p className={styles.detailMeta}>
                SĐT Khách: <strong>{activeConsignment.phone}</strong> | Ghi chú: {activeConsignment.note}
              </p>
            </div>

            <button className={styles.btnActivateMain} onClick={handleActivateConsignment}>
              🚀 XÁC NHẬN & KÍCH HOẠT LÔ HÀNG (ACTIVE)
            </button>
          </div>

          <div className={styles.detailGrid}>
            {/* CỘT TRÁI: FORM NHẬP SẢN PHẨM */}
            <div className={styles.formCard}>
              <h3 className={styles.cardSectionTitle}>
                {editingItemId ? "✏️ Chỉnh Sửa Sản Phẩm" : "➕ Thêm Sản Phẩm Mới"}
              </h3>

              <form onSubmit={handleSaveItem} className={styles.itemFormGrid}>
                <div className={`${styles.inputGroup} ${styles.colSpan2}`}>
                  <label>Tên sản phẩm *</label>
                  <input
                    type="text"
                    placeholder="VD: Áo sơ mi lụa tơ tằm Zara"
                    value={itemForm.name}
                    onChange={(e) => setItemForm({ ...itemForm, name: e.target.value })}
                    required
                  />
                </div>

                <div className={styles.inputGroup}>
                  <label>Loại sản phẩm</label>
                  <select
                    value={itemForm.category}
                    onChange={(e) => setItemForm({ ...itemForm, category: e.target.value })}
                  >
                    <option value="Áo sơ mi">Áo sơ mi</option>
                    <option value="Quần jean">Quần jean</option>
                    <option value="Đầm / Váy">Đầm / Váy</option>
                    <option value="Áo khoác">Áo khoác</option>
                  </select>
                </div>

                <div className={styles.inputGroup}>
                  <label>Size</label>
                  <input
                    type="text"
                    value={itemForm.size}
                    onChange={(e) => setItemForm({ ...itemForm, size: e.target.value })}
                  />
                </div>

                <div className={styles.inputGroup}>
                  <label>Màu sắc</label>
                  <input
                    type="text"
                    value={itemForm.color}
                    onChange={(e) => setItemForm({ ...itemForm, color: e.target.value })}
                  />
                </div>

                <div className={styles.inputGroup}>
                  <label>Tình trạng</label>
                  <select
                    value={itemForm.condition}
                    onChange={(e) => setItemForm({ ...itemForm, condition: e.target.value })}
                  >
                    <option value="NEW_TAG">Mới nguyên tag</option>
                    <option value="LIKE_NEW">Như mới (95%+)</option>
                    <option value="GOOD">Tốt (85-94%)</option>
                  </select>
                </div>

                <div className={`${styles.inputGroup} ${styles.colSpan2}`}>
                  <label>Tỷ lệ hoa hồng (%) *</label>
                  <input
                    type="number"
                    min="10"
                    max="60"
                    value={itemForm.commissionRate}
                    onChange={(e) => setItemForm({ ...itemForm, commissionRate: Number(e.target.value) })}
                  />
                </div>

                {/* 💡 CHỨC NĂNG LỊCH GIÁ ĐỘNG LINH HOẠT */}
                <div className={`${styles.inputGroup} ${styles.colSpan2}`}>
                  <div className={styles.scheduleHeader}>
                    <label>Lịch Giảm Giá Theo Thời Gian (Price Schedule)</label>
                    <button
                      type="button"
                      className={styles.btnAddScheduleRow}
                      onClick={handleAddScheduleRow}
                    >
                      + Thêm Mốc Giảm Giá
                    </button>
                  </div>

                  <div className={styles.priceScheduleContainer}>
                    {dynamicSchedules.map((schedule, idx) => (
                      <div key={idx} className={styles.scheduleRowDynamic}>
                        <div className={styles.dayInputGroup}>
                          <span>Sau</span>
                          <input
                            type="number"
                            min="0"
                            value={schedule.effectiveAfterDays}
                            onChange={(e) => handleScheduleChange(idx, "effectiveAfterDays", e.target.value)}
                            disabled={idx === 0} // Mốc 0 ngày cố định
                          />
                          <span>ngày:</span>
                        </div>

                        <input
                          type="number"
                          placeholder={idx === 0 ? "Giá gốc *" : "Giá giảm..."}
                          value={schedule.price}
                          onChange={(e) => handleScheduleChange(idx, "price", e.target.value)}
                          required={idx === 0}
                          className={styles.priceInput}
                        />

                        {idx > 0 && (
                          <button
                            type="button"
                            className={styles.btnRemoveSchedule}
                            onClick={() => handleRemoveScheduleRow(idx)}
                            title="Xóa mốc này"
                          >
                            🗑️
                          </button>
                        )}
                      </div>
                    ))}
                  </div>
                </div>

                {/* Ảnh */}
                <div className={`${styles.inputGroup} ${styles.colSpan2}`}>
                  <label>Ảnh Thumbnail sản phẩm</label>
                  <input type="file" accept="image/*" />
                </div>

                <div className={styles.formActions}>
                  {editingItemId && (
                    <button
                      type="button"
                      className={styles.btnCancelEdit}
                      onClick={() => {
                        setEditingItemId(null);
                        setDynamicSchedules([{ effectiveAfterDays: 0, price: "" }]);
                      }}
                    >
                      Hủy Sửa
                    </button>
                  )}
                  <button type="submit" className={styles.btnSaveItem}>
                    {editingItemId ? "💾 Cập Nhật Sản Phẩm" : "➕ Thêm Vào Lô DRAFT"}
                  </button>
                </div>
              </form>
            </div>

            {/* CỘT PHẢI: BẢNG DANH SÁCH MÓN */}
            <div className={styles.listCard}>
              <h3 className={styles.cardSectionTitle}>
                Danh Sách Sản Phẩm Trong Lô ({draftItems.length})
              </h3>

              {draftItems.length === 0 ? (
                <div className={styles.emptyItems}>Chưa có sản phẩm nào trong lô DRAFT này.</div>
              ) : (
                <div className={styles.itemTableWrapper}>
                  <table className={styles.miniTable}>
                    <thead>
                      <tr>
                        <th>Tên sản phẩm</th>
                        <th>Lịch giá thiết lập</th>
                        <th>Hoa hồng</th>
                        <th>Thao tác</th>
                      </tr>
                    </thead>
                    <tbody>
                      {draftItems.map((item) => (
                        <tr key={item.id}>
                          <td>
                            <strong>{item.productName}</strong>
                            <div className={styles.subMeta}>
                              {item.category} | Size {item.size} | {item.color}
                            </div>
                          </td>
                          <td>
                            <div className={styles.scheduleBadgesList}>
                              {item.priceSchedules.map((s, i) => (
                                <span key={i} className={styles.scheduleTag}>
                                  {s.effectiveAfterDays} ngày: {Number(s.price).toLocaleString()}đ
                                </span>
                              ))}
                            </div>
                          </td>
                          <td>{item.commissionRate}%</td>
                          <td>
                            <div className={styles.btnGroup}>
                              <button
                                className={styles.btnEdit}
                                onClick={() => handleEditClick(item)}
                              >
                                ✏️
                              </button>
                              <button
                                className={styles.btnDelete}
                                onClick={() => handleDeleteItem(item.id)}
                              >
                                🗑️
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

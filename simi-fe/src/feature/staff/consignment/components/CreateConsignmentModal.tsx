import React, { useState } from "react";
import styles from "./CreateConsignmentModal.module.css";

interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: { consignorPhone: string; note: string }) => void;
}

export const CreateConsignmentModal = ({
  isOpen,
  onClose,
  onSubmit,
}: Props) => {
  const [consignorPhone, setConsignorPhone] = useState("");
  const [note, setNote] = useState("");

  if (!isOpen) return null;

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    onSubmit({ consignorPhone, note });
    setConsignorPhone("");
    setNote("");
    onClose();
  };

  return (
    <div className={styles.overlay}>
      <div className={styles.modal}>
        <div className={styles.title}>Tạo lô hàng ký gửi</div>
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label htmlFor="sdt" className={styles.label}>
              Số điện thoại khách hàng
            </label>
            <input
              id="sdt"
              type="tel"
              required
              value={consignorPhone}
              onChange={(event) => setConsignorPhone(event.target.value)}
              className={styles.input}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="note" className={styles.label}>
              Ghi chú
            </label>
            <input
              id="note"
              type="text"
              value={note}
              onChange={(event) => setNote(event.target.value)}
              className={styles.input}
            />
          </div>
          <div className={styles.actions}>
            <button
              type="button"
              onClick={onClose}
              className={styles.btnCancel}
            >
              Hủy
            </button>
            <button type="submit" className={styles.btnSubmit}>
              Xác nhận
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

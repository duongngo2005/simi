import { useState, useEffect } from "react";
import { useLocation, useNavigate, Link } from "react-router";
import styles from "./CheckoutPage.module.css";
import type { ProductDetailResponse } from "../../product/types/product.type";
import type { OrderRequest } from "../types/order.type";
import { useProvinces, useWards } from "../hooks/useLocation";
import { formatPrice } from "../../../utils/formatPrice";
import { useThumbnail } from "../../product/hooks/useProducts";
import { useShippingFee, useCreateOrder } from "../hooks/useOrder";

const CONDITION_LABEL: Record<string, string> = {
  NEW_TAG: "Mới nguyên tag",
  LIKE_NEW: "Như mới (95%+)",
  GOOD: "Tốt (85-94%)",
  FAIR: "Khá (70-84%)",
};

export const CheckoutPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const product = location.state?.product as ProductDetailResponse | undefined;

const [formData, setFormData] = useState<{
  fullName: string;
  phone: string;
  email: string;
  provinceCode: string;
  provinceName: string;
  wardCode: string;
  wardName: string;
  addressDetail: string;
  note: string;
  paymentMethod: "COD" | "VNPAY";  
}>({
  fullName: "",
  phone: "",
  email: "",
  provinceCode: "",
  provinceName: "",
  wardCode: "",
  wardName: "",
  addressDetail: "",
  note: "",
  paymentMethod: "COD",
});

  // ── Tất cả hooks đặt TRƯỚC guard clause ──────────────
  const { data: provinces = [], isLoading: loadingProvinces } = useProvinces();
  const { data: wards = [], isLoading: loadingWards } = useWards(formData.provinceCode);
  const { data: thumbnailUrl } = useThumbnail(product?.id ?? 0);
  const { data: shippingFee = 30000 } = useShippingFee(
    formData.provinceCode,
    product?.currentPrice ?? 0
  );
  const { mutateAsync: createOrder, isPending } = useCreateOrder();

  useEffect(() => {
    if (!product) navigate("/");
  }, [product, navigate]);

  // Guard sau tất cả hooks
  if (!product) {
    return <div className={styles.page}><p>Đang chuyển hướng...</p></div>;
  }

  const total = product.currentPrice + shippingFee;

  // ── Handlers ─────────────────────────────────────────
  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleProvinceChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const code = e.target.value;
    const selected = provinces.find((p) => p.code === code);
    setFormData((prev) => ({
      ...prev,
      provinceCode: code,
      provinceName: selected?.fullName ?? "",
      wardCode: "",
      wardName: "",
    }));
  };

  const handleWardChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const code = e.target.value;
    const selected = wards.find((w) => w.code === code);
    setFormData((prev) => ({
      ...prev,
      wardCode: code,
      wardName: selected?.fullName ?? "",
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.provinceCode || !formData.wardCode) {
      alert("Vui lòng chọn đầy đủ Tỉnh/Thành phố và Xã/Phường.");
      return;
    }

    const payload: OrderRequest = {
      recipientName:  formData.fullName,
      recipientPhone: formData.phone,
      province:       formData.provinceCode,
      ward:           formData.wardCode,
      addressDetail:  formData.addressDetail,
      paymentMethod: formData.paymentMethod,
      discount:       0,
      orderItemRequests:     [{ productId: product.id }], 
    };

    try {
      const res = await createOrder(payload); // dùng useMutation thay vì gọi api thô
      navigate("/orders/success", {
        state: { orderId: res.body?.id },
      });
    } catch {
      alert("Đặt hàng thất bại. Vui lòng thử lại.");
    }
  };

  // ── Render ───────────────────────────────────────────
  return (
    <div className={styles.page}>
      <h1 className={styles.pageTitle}>Thanh toán đơn hàng</h1>

      <form onSubmit={handleSubmit} className={styles.container}>
        {/* CỘT TRÁI */}
        <div className={styles.leftCol}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Thông tin nhận hàng</h2>

            <div className={styles.inputGroup}>
              <label>Họ và tên</label>
              <input required type="text" name="fullName"
                placeholder="Nhập đầy đủ họ và tên"
                value={formData.fullName} onChange={handleInputChange}
              />
            </div>

            <div className={styles.inputGrid}>
              <div className={styles.inputGroup}>
                <label>Số điện thoại</label>
                <input required type="tel" name="phone"
                  placeholder="Số điện thoại nhận hàng"
                  value={formData.phone} onChange={handleInputChange}
                />
              </div>
              <div className={styles.inputGroup}>
                <label>Email (Nhận hóa đơn)</label>
                <input required type="email" name="email"
                  placeholder="email@example.com"
                  value={formData.email} onChange={handleInputChange}
                />
              </div>
            </div>

            <div className={styles.inputGrid}>
              <div className={styles.inputGroup}>
                <label>Tỉnh / Thành phố</label>
                <select required value={formData.provinceCode} onChange={handleProvinceChange}>
                  <option value="">
                    {loadingProvinces ? "Đang tải..." : "Chọn Tỉnh/Thành phố"}
                  </option>
                  {provinces.map((p) => (
                    <option key={p.code} value={p.code}>{p.fullName}</option>
                  ))}
                </select>
              </div>

              <div className={styles.inputGroup}>
                <label>Xã / Phường</label>
                <select required
                  disabled={!formData.provinceCode || loadingWards}
                  value={formData.wardCode} onChange={handleWardChange}
                >
                  <option value="">
                    {!formData.provinceCode ? "Chọn Tỉnh/Thành trước"
                      : loadingWards ? "Đang tải..." : "Chọn Xã/Phường"}
                  </option>
                  {wards.map((w) => (
                    <option key={w.code} value={w.code}>{w.fullName}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className={styles.inputGroup}>
              <label>Địa chỉ chi tiết (Số nhà, tên đường)</label>
              <input required type="text" name="addressDetail"
                placeholder="Ví dụ: 123 Đường Lê Lợi"
                value={formData.addressDetail} onChange={handleInputChange}
              />
            </div>

            <div className={styles.inputGroup}>
              <label>Ghi chú (Tùy chọn)</label>
              <textarea name="note" rows={3}
                placeholder="Ghi chú về thời gian giao hàng..."
                value={formData.note} onChange={handleInputChange}
              />
            </div>
          </div>

          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Phương thức thanh toán</h2>
            <div className={styles.paymentMethods}>
              {(["COD", "VNPAY"] as const).map((method) => (
                <label key={method}
                  className={`${styles.paymentLabel} ${formData.paymentMethod === method ? styles.paymentActive : ""}`}
                >
                  <input type="radio" name="paymentMethod" value={method}
                    checked={formData.paymentMethod === method}
                    onChange={handleInputChange}
                  />
                  <div className={styles.paymentText}>
                    {method === "COD" ? (
                      <>
                        <strong>Thanh toán khi nhận hàng (COD)</strong>
                        <span>Trả tiền mặt trực tiếp khi nhận hàng</span>
                      </>
                    ) : (
                      <>
                        <strong>Cổng thanh toán VNPAY</strong>
                        <span>Thanh toán online qua thẻ ATM / QR Code</span>
                      </>
                    )}
                  </div>
                </label>
              ))}
            </div>
          </div>
        </div>

        {/* CỘT PHẢI */}
        <div className={styles.rightCol}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Chi tiết đơn hàng</h2>

            <div className={styles.productRow}>
              <img src={thumbnailUrl} alt={product.name} className={styles.productImg} />
              <div className={styles.productInfo}>
                <span className={styles.productName}>{product.name}</span>
                {product.brandName && (
                  <span className={styles.productBrand}>{product.brandName}</span>
                )}
                <div className={styles.productMeta}>
                  {product.size && <span>Size {product.size}</span>}
                  {product.productCondition && (
                    <span>{CONDITION_LABEL[product.productCondition]}</span>
                  )}
                </div>
                <span className={styles.productPrice}>
                  {formatPrice(product.currentPrice)}
                </span>
              </div>
            </div>

            <hr className={styles.divider} />

            <div className={styles.summaryRows}>
              <div className={styles.summaryRow}>
                <span>Tạm tính</span>
                <span>{formatPrice(product.currentPrice)}</span>
              </div>
              <div className={styles.summaryRow}>
                <span>Phí vận chuyển</span>
                <span>{formatPrice(shippingFee)}</span>
              </div>
              <hr className={styles.divider} />
              <div className={`${styles.summaryRow} ${styles.totalRow}`}>
                <span>Tổng cộng</span>
                <span>{formatPrice(total)}</span>
              </div>
            </div>

            <button type="submit" className={styles.btnSubmit} disabled={isPending}>
              {isPending ? "Đang xử lý..." : "Đặt hàng ngay"}
            </button>

            <Link to={`/products/${product.id}`} className={styles.btnBack}>
              ← Quay lại chi tiết sản phẩm
            </Link>
          </div>
        </div>
      </form>
    </div>
  );
};

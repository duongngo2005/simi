import { useState, useEffect } from "react";
import { useLocation, useNavigate, Link, Navigate } from "react-router";
import styles from "./CheckoutPage.module.css";
import type { OrderRequest } from "../types/order.type";
import { useProvinces, useWards } from "../hooks/useLocation";
import { formatPrice } from "../../../utils/formatPrice";
import { useProductsByIds } from "../../product/hooks/useProducts";
import { useShippingFee, useCreateOrder } from "../hooks/useOrder";
import { CONDITION_LABEL } from "../../../utils/condition";
import { cartApi } from "../../cart/api/cartApi";
import { useQueryClient } from "@tanstack/react-query";
import { useAuthStore } from "../../../store/useAuthStore";
import { getServerError } from "../../../utils/getMessageError";

export const CheckoutPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated());

  const { productIds = [], cartItemIds } = (location.state as {
    productIds?: number[];
    cartItemIds?: number[];
  }) || {};

  const { data: productResponses = [], isLoading: loadingProducts } = useProductsByIds(productIds);
  const products = productResponses.map((res) => res.body).filter(Boolean);

  const subtotal = products.reduce((acc, p) => acc + (p?.currentPrice || 0), 0);

  const [formData, setFormData] = useState<{
    fullName: string;
    phone: string;
    provinceCode: string;
    provinceName: string;
    wardCode: string;
    wardName: string;
    addressDetail: string;
    paymentMethod: "COD" | "ONLINE";
  }>({
    fullName: "",
    phone: "",
    provinceCode: "",
    provinceName: "",
    wardCode: "",
    wardName: "",
    addressDetail: "",
    paymentMethod: "COD",
  });

  const { data: provinces = [], isLoading: loadingProvinces } = useProvinces();
  const { data: wards = [], isLoading: loadingWards } = useWards(formData.provinceCode);
  const { data: shippingFee = 30000 } = useShippingFee(formData.provinceCode, subtotal);
  const { mutateAsync: createOrder, isPending } = useCreateOrder();

  useEffect(() => {
    if (!productIds || productIds.length === 0) {
      navigate("/cart", { replace: true });
    }
  }, [productIds, navigate]);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (loadingProducts || productIds.length === 0) {
    return <div className={styles.stateWrapper}><p>Đang tải đơn hàng...</p></div>;
  }

  const total = subtotal + shippingFee;

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
      recipientName: formData.fullName,
      recipientPhone: formData.phone,
      province: formData.provinceCode,
      ward: formData.wardCode,
      addressDetail: formData.addressDetail,
      paymentMethod: formData.paymentMethod,
      discount: 0,
      orderItemRequests: productIds.map((id) => ({ productId: id })),
    };

    try {
      const res = await createOrder(payload);

      if (cartItemIds && cartItemIds.length > 0) {
        try {
          await Promise.all(cartItemIds.map((id) => cartApi.removeItem(id)));
          queryClient.invalidateQueries({ queryKey: ["my-cart"] });
        } catch {
          alert("Đơn hàng đã được tạo, nhưng giỏ hàng chưa cập nhật. Vui lòng tải lại trang giỏ hàng.");
        }
      }

      if (res.body?.paymentUrl){
        window.location.href = res.body.paymentUrl
      }else{
        navigate("/orders/success", {
          state: {orderId: res.body?.orderDetail?.id}
        })
      }
    } catch (error: unknown) {
        alert(getServerError(error, "Đặt hàng thất bại. Vui lòng thử lại."));
    }
  };

  return (
    <div className={styles.page}>
      <header className={styles.pageHeader}>
        <p className={styles.eyebrow}>Bước cuối cùng</p>
        <h1 className={styles.pageTitle}>Xác nhận đơn hàng <span>({products.length} sản phẩm)</span></h1>
        <p>Điền địa chỉ nhận hàng, sau đó kiểm tra lại tổng tiền trước khi đặt đơn.</p>
      </header>

      <form onSubmit={handleSubmit} className={styles.container}>
        <div className={styles.leftCol}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Thông tin nhận hàng</h2>

            <div className={styles.inputGroup}>
              <label htmlFor="recipientName">Họ và tên</label>
              <input
                required
                type="text"
                id="recipientName"
                name="fullName"
                placeholder="Nhập đầy đủ họ và tên"
                value={formData.fullName}
                onChange={handleInputChange}
              />
            </div>

            <div className={styles.inputGrid}>
              <div className={styles.inputGroup}>
                <label htmlFor="recipientPhone">Số điện thoại</label>
                <input
                  required
                  type="tel"
                  id="recipientPhone"
                  name="phone"
                  placeholder="Số điện thoại nhận hàng"
                  value={formData.phone}
                  onChange={handleInputChange}
                />
              </div>
            </div>

            <div className={styles.inputGrid}>
              <div className={styles.inputGroup}>
                <label htmlFor="province">Tỉnh / Thành phố</label>
                <select id="province" required value={formData.provinceCode} onChange={handleProvinceChange}>
                  <option value="">
                    {loadingProvinces ? "Đang tải..." : "Chọn Tỉnh/Thành phố"}
                  </option>
                  {provinces.map((p) => (
                    <option key={p.code} value={p.code}>{p.fullName}</option>
                  ))}
                </select>
              </div>

              <div className={styles.inputGroup}>
                <label htmlFor="ward">Xã / Phường</label>
                <select
                  id="ward"
                  required
                  disabled={!formData.provinceCode || loadingWards}
                  value={formData.wardCode}
                  onChange={handleWardChange}
                >
                  <option value="">
                    {!formData.provinceCode
                      ? "Chọn Tỉnh/Thành trước"
                      : loadingWards
                      ? "Đang tải..."
                      : "Chọn Xã/Phường"}
                  </option>
                  {wards.map((w) => (
                    <option key={w.code} value={w.code}>{w.fullName}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className={styles.inputGroup}>
              <label htmlFor="addressDetail">Địa chỉ chi tiết (Số nhà, tên đường)</label>
              <input
                required
                type="text"
                id="addressDetail"
                name="addressDetail"
                placeholder="Ví dụ: 123 Đường Lê Lợi"
                value={formData.addressDetail}
                onChange={handleInputChange}
              />
            </div>

          </div>

          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Phương thức thanh toán</h2>
            <div className={styles.paymentMethods}>
              {(["COD", "ONLINE"] as const).map((method) => (
                <label
                    key={method}
                    className={`${styles.paymentLabel} ${
                        formData.paymentMethod === method ? styles.paymentActive : ""
                    }`}
                >
                    <input
                        type="radio"
                        name="paymentMethod"
                        value={method}
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

        <div className={styles.rightCol}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Chi tiết đơn hàng</h2>

            <div className={styles.productList}>
              {products.map((product) => {
                if (!product) return null;
                const thumb = product.thumbnail || product.productImageResponses?.[0]?.imageUrl;

                return (
                  <div key={product.id} className={styles.productRow}>
                    {thumb ? (
                      <img src={thumb} alt={product.name} className={styles.productImg} />
                    ) : (
                      <div className={styles.noImg}>Ảnh đang cập nhật</div>
                    )}
                    <div className={styles.productInfo}>
                      <span className={styles.productName}>{product.name}</span>
                      <div className={styles.productMeta}>
                        {product.size && <span>Size: {product.size}</span>}
                        {product.productCondition && (
                          <span>Tình trạng: {CONDITION_LABEL[product.productCondition] || product.productCondition}</span>
                        )}
                      </div>
                      <span className={styles.productPrice}>{formatPrice(product.currentPrice)}</span>
                    </div>
                  </div>
                );
              })}
            </div>

            <hr className={styles.divider} />

            <div className={styles.summaryRows}>
              <div className={styles.summaryRow}>
                <span>Tạm tính</span>
                <span>{formatPrice(subtotal)}</span>
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

            <Link to="/cart" className={styles.btnBack}>
              Quay lại giỏ hàng
            </Link>
          </div>
        </div>
      </form>
    </div>
  );
};

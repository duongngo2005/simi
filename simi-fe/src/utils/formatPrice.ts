export const formatPrice = (price?: number) => {
  if (price == null) return "0đ";
  return price.toLocaleString("vi-VN") + "đ";
};
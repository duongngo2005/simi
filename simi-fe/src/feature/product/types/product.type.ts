export interface ProductImageResponse {
  imageUrl: string;
  thumbnail: boolean;
}

export interface ProductSummaryResponse {
  id: number;
  name: string;
  brandName?: string;
  currentPrice: number; // Tương đương BigDecimal ở BE
  size?: string;
  productCondition?: string;
  productImageResponses?: ProductImageResponse[];
  // Dành riêng cho FE giả lập giao diện Sale
  oldPrice?: number;
  discount?: string;
  isSale?: boolean;
}
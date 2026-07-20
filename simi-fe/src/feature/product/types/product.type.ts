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

export interface ProductDetailResponse {
  id: number;
  name: string;
  description: string;
  size: string;
  color: string;
  currentPrice: number;
  productCondition: "NEW_TAG" | "LIKE_NEW" | "GOOD" | "FAIR";
  productStatus: string;
  brandName: string;
  categoryName: string;
  tagNames: string[];
  productImageResponses: { imageUrl: string; thumbnail: boolean }[];
  createdDate: string;
}

export interface ProductImageResponse {
  imageUrl: string;
  imagePublicId: string;
  id: number;
} 
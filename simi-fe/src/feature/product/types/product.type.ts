export interface ProductImageResponse {
  imageUrl: string;
  thumbnail: boolean;
}

export interface ProductSummaryResponse {
  id: number;
  name: string;
  brandName?: string;
  currentPrice: number;
  size?: string;
  productCondition?: string;
  productStatus: string;
  thumbnail: string;
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
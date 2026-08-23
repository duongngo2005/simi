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
  brand: number | null;
  category: number;
  tagNames: string[];
  productImageResponses: { id: number, imageUrl: string; thumbnail: boolean }[];
  createdDate: string;
  thumbnail: string;
  gender?: "MEN" | "WOMEN" | "UNISEX";
  material?: string;
}

export interface ProductImageResponse {
  imageUrl: string;
  imagePublicId: string;
  id: number;
} 
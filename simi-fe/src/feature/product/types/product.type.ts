export interface ProductImageResponse {
  id: number;
  imageUrl: string;
  thumbnail: boolean;
}

export interface TagResponse {
  id: number;
  name: string;
  slug: string;
  active: boolean;
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
  brandName?: string | null;
  category: number;
  tagResponses: TagResponse[];
  productImageResponses: ProductImageResponse[];
  createdDate: string;
  thumbnail: string;
  gender?: "MEN" | "WOMEN" | "UNISEX";
  material?: string;
}

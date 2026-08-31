import { useMemo, type FormEvent } from "react";
import { useSearchParams } from "react-router";
import { ProductCard } from "../components/ProductCard";
import { useProductSearch } from "../hooks/useProducts";
import type { ProductSearchParams } from "../api/productApis";
import styles from "./ProductListPage.module.css";

const LEGACY_CATEGORY_SLUG: Record<string, string> = {
  accessories: "phu-kien",
  shoes: "giay-dep",
};

const LEGACY_CONDITION: Record<string, string> = {
  NEW: "NEW_TAG",
};

const SORT_OPTIONS = [
  { value: "createdDate:desc", label: "Mới đăng" },
  { value: "currentPrice:asc", label: "Giá tăng dần" },
  { value: "currentPrice:desc", label: "Giá giảm dần" },
  { value: "name:asc", label: "Tên A–Z" },
];

export const ProductListPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const filters = useMemo<ProductSearchParams>(() => {
    const rawPage = Number(searchParams.get("page") ?? "0");
    const legacySort = searchParams.get("sort") === "newest";
    const rawCondition = searchParams.get("productCondition")
      ?? LEGACY_CONDITION[searchParams.get("condition") ?? ""];
    const condition = ["NEW_TAG", "LIKE_NEW", "GOOD", "FAIR"].includes(rawCondition ?? "")
      ? rawCondition
      : undefined;
    const rawCategory = searchParams.get("categorySlug") ?? searchParams.get("category");
    const rawSortBy = searchParams.get("sortBy") ?? "createdDate";
    const sortBy = legacySort || !["createdDate", "currentPrice", "name"].includes(rawSortBy)
      ? "createdDate"
      : rawSortBy;
    const sortDir = legacySort ? "desc" : searchParams.get("sortDir") === "asc" ? "asc" : "desc";

    return {
      page: Number.isInteger(rawPage) && rawPage >= 0 ? rawPage : 0,
      size: 12,
      keyword: searchParams.get("keyword") || undefined,
      productCondition: condition || undefined,
      categorySlug: rawCategory ? LEGACY_CATEGORY_SLUG[rawCategory] ?? rawCategory : undefined,
      sortBy,
      sortDir,
    };
  }, [searchParams]);

  const { data: productPage, isLoading, isError } = useProductSearch(filters);

  const updateParams = (changes: Record<string, string | null>) => {
    const next = new URLSearchParams(searchParams);
    Object.entries(changes).forEach(([key, value]) => {
      if (value) {
        next.set(key, value);
      } else {
        next.delete(key);
      }
    });
    setSearchParams(next);
  };

  const handleSearch = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const keyword = String(formData.get("keyword") ?? "").trim();
    updateParams({ keyword: keyword || null, page: null });
  };

  const handleSortChange = (value: string) => {
    const [sortBy, sortDir] = value.split(":");
    updateParams({ sortBy, sortDir, sort: null, page: null });
  };

  const handleConditionChange = (value: string) => {
    updateParams({ productCondition: value || null, condition: null, page: null });
  };

  const clearFilters = () => {
    setSearchParams({});
  };

  const products = productPage?.content ?? [];
  const selectedSort = `${filters.sortBy ?? "createdDate"}:${filters.sortDir ?? "desc"}`;
  const totalPages = productPage?.totalPages ?? 0;

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <h1>Sản phẩm đang có sẵn</h1>
      </header>

      <section className={styles.filterBar} aria-label="Lọc sản phẩm">
        <form className={styles.searchForm} onSubmit={handleSearch}>
          <input
            key={searchParams.get("keyword") ?? ""}
            name="keyword"
            defaultValue={searchParams.get("keyword") ?? ""}
            placeholder="Tìm theo tên sản phẩm"
            aria-label="Tìm sản phẩm"
          />
          <button type="submit">Tìm</button>
        </form>

        <div className={styles.selectGroup}>
          <select
            value={filters.productCondition ?? ""}
            onChange={(event) => handleConditionChange(event.target.value)}
            aria-label="Lọc theo tình trạng"
          >
            <option value="">Mọi tình trạng</option>
            <option value="NEW_TAG">Mới nguyên tag</option>
            <option value="LIKE_NEW">Như mới</option>
            <option value="GOOD">Tốt</option>
            <option value="FAIR">Khá</option>
          </select>
          <select value={selectedSort} onChange={(event) => handleSortChange(event.target.value)} aria-label="Sắp xếp">
            {SORT_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>{option.label}</option>
            ))}
          </select>
          <button type="button" className={styles.resetButton} onClick={clearFilters}>Xóa lọc</button>
        </div>
      </section>

      {isLoading && <div className={styles.stateBox}>Đang tải sản phẩm...</div>}
      {isError && <div className={styles.stateBox}>Không thể tải danh sách sản phẩm. Vui lòng thử lại.</div>}
      {!isLoading && !isError && products.length === 0 && (
        <div className={styles.stateBox}>Không tìm thấy sản phẩm phù hợp.</div>
      )}

      {!isLoading && !isError && products.length > 0 && (
        <>
          <p className={styles.resultCount}>Tìm thấy {productPage?.totalElements ?? products.length} sản phẩm</p>
          <div className={styles.productGrid}>
            {products.map((product) => <ProductCard key={product.id} {...product} />)}
          </div>

          {totalPages > 1 && (
            <nav className={styles.pagination} aria-label="Phân trang sản phẩm">
              <button
                disabled={filters.page === 0}
                onClick={() => updateParams({ page: String((filters.page ?? 0) - 1) })}
              >
                Trước
              </button>
              <span>Trang {(filters.page ?? 0) + 1}/{totalPages}</span>
              <button
                disabled={productPage?.last}
                onClick={() => updateParams({ page: String((filters.page ?? 0) + 1) })}
              >
                Sau
              </button>
            </nav>
          )}
        </>
      )}
    </div>
  );
};

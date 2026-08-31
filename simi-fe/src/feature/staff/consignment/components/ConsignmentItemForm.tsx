import React, { useEffect, useState } from "react";
import api from "../../../../lib/http/apiClient";
import styles from "./ConsignmentItemForm.module.css";
import {
  useCreateConsignmentItem,
  useUpdateConsignmentItem,
} from "../hooks/useConsignments";
import type { ConsignmentItemResponse } from "../types/staffConsignment.type";

const PRODUCT_CONDITION = [
  { value: "NEW_TAG", label: "Nguyên tag (100%)" },
  { value: "LIKE_NEW", label: "Như mới (95%)" },
  { value: "GOOD", label: "Tốt (90-95%)" },
  { value: "FAIR", label: "Khá (80-90%)" },
];

const GENDER_OPTIONS = [
  { value: "UNISEX", label: "Unisex (Nam / Nữ)" },
  { value: "MEN", label: "Nam" },
  { value: "WOMEN", label: "Nữ" },
];

interface Props {
  consignmentId: number;
  editingItem?: ConsignmentItemResponse | null;
  onCancelEdit?: () => void;
}

export const ConsignmentItemForm = ({
  consignmentId,
  editingItem,
  onCancelEdit,
}: Props) => {
  const [categories, setCategories] = useState<{ id: number; name: string }[]>(
    []
  );
  const [brands, setBrands] = useState<{ id: number; name: string }[]>([]);

  const [itemForm, setItemForm] = useState({
    name: "",
    categoryId: "",
    brandId: "",
    size: "",
    color: "",
    productCondition: "",
    gender: "UNISEX",
    material: "",
    description: "",
    commissionRate: 0.3,
    tagInput: "",
  });

  const [priceSchedules, setPriceSchedules] = useState([
    { effectiveAfterDays: 0, price: "" },
  ]);

  const handleAddSchedule = () => {
    const lastSchedule = priceSchedules[priceSchedules.length - 1];
    const nextDays = lastSchedule ? lastSchedule.effectiveAfterDays + 15 : 15;

    setPriceSchedules([
      ...priceSchedules,
      { effectiveAfterDays: nextDays, price: "" },
    ]);
  };

  const handleScheduleChange = (
    index: number,
    field: "effectiveAfterDays" | "price",
    value: string
  ) => {
    const updated = [...priceSchedules];
    if (field === "effectiveAfterDays") {
      updated[index].effectiveAfterDays = Number(value);
    } else {
      updated[index].price = value;
    }
    setPriceSchedules(updated);
  };

  const handleRemoveSchedule = (index: number) => {
    if (index === 0) return;
    setPriceSchedules(priceSchedules.filter((_, i) => i !== index));
  };

  useEffect(() => {
    api
      .get("/categories")
      .then((response) => setCategories(response.data.body || []))
      .catch(() => {});
    api
      .get("/brands")
      .then((response) => setBrands(response.data.body || []))
      .catch(() => {});
  }, []);

  const handleChange = (
    event: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    setItemForm({ ...itemForm, [event.target.name]: event.target.value });
  };

  const [thumbnailFile, setThumbnailFile] = useState<File | null>(null);
  const [thumbnailPreview, setThumbnailPreview] = useState<string>("");
  const [imageFiles, setImageFiles] = useState<File[]>([]);
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);

  const handleThumbnailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setThumbnailFile(file);
      setThumbnailPreview(URL.createObjectURL(file));
    }
  };

  const handleDetailImagesChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const filesArray = Array.from(e.target.files);
      setImageFiles((prev) => [...prev, ...filesArray]);

      const newPreviews = filesArray.map((file) => URL.createObjectURL(file));
      setImagePreviews((prev) => [...prev, ...newPreviews]);
    }
  };

  const handleRemoveImage = (index: number) => {
    setImageFiles((prev) => prev.filter((_, i) => i !== index));
    setImagePreviews((prev) => prev.filter((_, i) => i !== index));
  };

  const {
    mutateAsync: createConsignmentItem,
    isPending: isCreatingConsignmentItem,
  } = useCreateConsignmentItem();
  const {
    mutateAsync: updateConsignmentItem,
    isPending: isUpdatingConsignmentItem,
  } = useUpdateConsignmentItem();

  const handleSubmitForm = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingItem && !thumbnailFile) {
      alert("Vui lòng chọn ảnh thumbnail");
      return;
    }

    if (!priceSchedules?.[0]) {
      alert("Vui lòng nhập giá gốc");
      return;
    }

    if (!itemForm.categoryId) {
      alert("Vui lòng chọn loại sản phẩm");
      return;
    }

    const consignmentItemRequest = {
      commissionRate: Number(itemForm.commissionRate),
      priceScheduleRequests: priceSchedules
        .filter((s) => s.price !== "")
        .map((s) => ({
          effectiveAfterDays: s.effectiveAfterDays,
          price: Number(s.price),
        })),
      productRequest: {
        name: itemForm.name,
        categoryId: Number(itemForm.categoryId),
        brandId: itemForm.brandId ? Number(itemForm.brandId) : undefined,
        size: itemForm.size || undefined,
        color: itemForm.color || undefined,
        description: itemForm.description || undefined,
        productCondition: itemForm.productCondition || undefined,
        gender: (itemForm.gender as "MEN" | "WOMEN" | "UNISEX") || "UNISEX",
        material: itemForm.material || undefined,
        tagNames: itemForm.tagInput
          ? itemForm.tagInput.split(",").map((t) => t.trim()).filter(Boolean)
          : [],
      },
    };

    try {
      if (editingItem) {
        await updateConsignmentItem({
          consignmentId,
          consignmentItemId: editingItem.id,
          thumbnail: thumbnailFile || undefined,
          images: imageFiles.length > 0 ? imageFiles : undefined,
          deleteImageIds,
          consignmentItemRequest,
        });
        alert("Cập nhật thành công");
        onCancelEdit?.();
      } else {
        await createConsignmentItem({
          consignmentId,
          thumbnail: thumbnailFile!,
          images: imageFiles.length > 0 ? imageFiles : undefined,
          consignmentItemRequest,
        });
        alert("Thêm sản phẩm thành công");
      }

      resetForm();
    } catch {
      alert("Thêm sản phẩm thất bại");
    }
  };

  const resetForm = () => {
    setItemForm({
      name: "",
      categoryId: "",
      brandId: "",
      size: "",
      color: "",
      productCondition: "",
      gender: "UNISEX",
      material: "",
      description: "",
      commissionRate: 0.3,
      tagInput: "",
    });
    setPriceSchedules([{ effectiveAfterDays: 0, price: "" }]);
    setThumbnailFile(null);
    setThumbnailPreview("");
    setImageFiles([]);
    setImagePreviews([]);
    setExistingImages([]);
    setDeleteImageIds([]);
  };

  const [existingImages, setExistingImages] = useState<
    { id: number; imageUrl: string }[]
  >([]);

  const [deleteImageIds, setDeleteImageIds] = useState<number[]>([]);

  useEffect(() => {
    if (!editingItem) return;

    const product = editingItem.productDetailResponse;

    setItemForm({
      name: product.name || "",
      categoryId: String(product.category || ""),
      brandId: product.brand ? String(product.brand) : "",
      size: product.size || "",
      color: product.color || "",
      productCondition: product.productCondition || "",
      gender: product.gender || "UNISEX",
      material: product.material || "",
      description: product.description || "",
      commissionRate: editingItem.commissionRate,
      tagInput: product.tagNames?.join(", ") || "",
    });

    setPriceSchedules(
      editingItem.priceScheduleResponses.map((s) => ({
        effectiveAfterDays: s.effectiveAfterDays,
        price: String(s.price),
      }))
    );

    setThumbnailPreview(product.thumbnail || "");
    setThumbnailFile(null);

    const otherImages =
      product.productImageResponses
        ?.filter((img) => !img.thumbnail)
        .map((img) => ({ id: img.id, imageUrl: img.imageUrl })) || [];
    setExistingImages(otherImages);

    setImageFiles([]);
    setImagePreviews([]);
    setDeleteImageIds([]);
  }, [editingItem]);

  const handleRemoveExistingImage = (imgId: number) => {
    setExistingImages((prev) => prev.filter((img) => img.id !== imgId));
    setDeleteImageIds((prev) => [...prev, imgId]);
  };

  return (
    <div className={styles.card}>
      <h3 className={styles.cardTitle}>
        {editingItem ? "Chỉnh sửa chi tiết" : "Thêm sản phẩm mới"}
      </h3>
      <form onSubmit={handleSubmitForm} className={styles.form}>
        <div className={styles.field}>
          <label htmlFor="name" className={styles.label}>
            Tên sản phẩm
          </label>
          <input
            type="text"
            value={itemForm.name}
            onChange={handleChange}
            name="name"
            className={styles.input}
          />
        </div>
        <div className={styles.row}>
          <div className={styles.field}>
            <label htmlFor="categoryId" className={styles.label}>
              Loại sản phẩm
            </label>
            <select
              value={itemForm.categoryId}
              name="categoryId"
              onChange={handleChange}
              className={styles.select}
            >
              <option value="">-- Chọn loại sản phẩm --</option>
              {categories.map((cate) => (
                <option value={cate.id} key={cate.id}>
                  {cate.name}
                </option>
              ))}
            </select>
          </div>
          <div className={styles.field}>
            <label htmlFor="brandId" className={styles.label}>
              Thương hiệu
            </label>
            <select
              name="brandId"
              value={itemForm.brandId}
              onChange={handleChange}
              className={styles.select}
            >
              <option value="">Không</option>
              {brands.map((brand) => (
                <option value={brand.id} key={brand.id}>
                  {brand.name}
                </option>
              ))}
            </select>
          </div>
        </div>
        <div className={styles.row}>
          <div className={styles.field}>
            <label htmlFor="size" className={styles.label}>
              Size
            </label>
            <input
              name="size"
              type="text"
              value={itemForm.size}
              onChange={handleChange}
              className={styles.input}
            />
          </div>
          <div className={styles.field}>
            <label htmlFor="color" className={styles.label}>
              Màu
            </label>
            <input
              name="color"
              type="text"
              value={itemForm.color}
              onChange={handleChange}
              className={styles.input}
            />
          </div>
        </div>

        <div className={styles.row}>
          <div className={styles.field}>
            <label htmlFor="gender" className={styles.label}>
              Giới tính
            </label>
            <select
              name="gender"
              value={itemForm.gender}
              onChange={handleChange}
              className={styles.select}
            >
              {GENDER_OPTIONS.map((g) => (
                <option value={g.value} key={g.value}>
                  {g.label}
                </option>
              ))}
            </select>
          </div>
          <div className={styles.field}>
            <label htmlFor="material" className={styles.label}>
              Chất liệu
            </label>
            <input
              name="material"
              type="text"
              placeholder="VD: 100% Cotton, Linen, Denim..."
              value={itemForm.material}
              onChange={handleChange}
              className={styles.input}
            />
          </div>
        </div>

        <div className={styles.row}>
          <div className={styles.field}>
            <label htmlFor="productCondition" className={styles.label}>
              Tình trạng
            </label>
            <select
              name="productCondition"
              value={itemForm.productCondition}
              onChange={handleChange}
              className={styles.select}
            >
              {PRODUCT_CONDITION.map((condition) => (
                <option value={condition.value} key={condition.value}>
                  {condition.label}
                </option>
              ))}
            </select>
          </div>
          <div className={styles.field}>
            <label htmlFor="commissionRate" className={styles.label}>
              Hoa hồng
            </label>
            <input
              name="commissionRate"
              type="number"
              value={itemForm.commissionRate}
              onChange={handleChange}
              className={styles.input}
            />
          </div>
        </div>

        <div className={styles.scheduleBox}>
          <div className={styles.scheduleHeader}>
            <label htmlFor="" className={styles.label}>
              Lịch giá
            </label>
            <button
              onClick={handleAddSchedule}
              type="button"
              className={styles.btnAddSchedule}
            >
              Thêm mốc
            </button>
          </div>
          {priceSchedules.map((s, index) => (
            <div key={index} className={styles.scheduleRow}>
              <span>Sau</span>
              <input
                type="number"
                min={0}
                value={s.effectiveAfterDays}
                disabled={index === 0}
                className={styles.dayInput}
                onChange={(event) =>
                  handleScheduleChange(
                    index,
                    "effectiveAfterDays",
                    event.target.value
                  )
                }
              />
              <span>ngày</span>
              <input
                type="number"
                placeholder={index === 0 ? "Giá gốc" : "Giá giảm"}
                value={s.price}
                required={index === 0}
                className={styles.priceInput}
                onChange={(event) =>
                  handleScheduleChange(index, "price", event.target.value)
                }
              />
              {index > 0 && (
                <button
                  onClick={() => handleRemoveSchedule(index)}
                  type="button"
                  className={styles.btnDeleteSchedule}
                >
                  Xóa
                </button>
              )}
            </div>
          ))}
        </div>

        <div className={styles.field}>
          <label htmlFor="tagInput" className={styles.label}>
            Thẻ tag
          </label>
          <input
            name="tagInput"
            type="text"
            placeholder="Ví dụ: form gọn, công sở, dễ phối đồ"
            value={itemForm.tagInput}
            onChange={handleChange}
            className={styles.input}
          />
        </div>
        <div className={styles.field}>
          <label htmlFor="description" className={styles.label}>
            Mô tả sản phẩm
          </label>
          <textarea
            rows={2}
            name="description"
            value={itemForm.description}
            onChange={handleChange}
            className={styles.textarea}
          />
        </div>
        <div className={styles.uploadSection}>
          <div className={styles.uploadBlock}>
            <label htmlFor="" className={styles.label}>
              Thumbnail
            </label>
            <div className={styles.uploadBox}>
              {thumbnailPreview ? (
                <div className={styles.previewBox}>
                  <img
                    src={thumbnailPreview}
                    alt=""
                    className={styles.previewImage}
                  />
                  <button
                    type="button"
                    className={styles.btnRemoveImage}
                    onClick={() => {
                      setThumbnailFile(null);
                      setThumbnailPreview("");
                    }}
                  >
                    X
                  </button>
                </div>
              ) : (
                <label htmlFor="thumbnailInput" className={styles.fileLabel}>
                  <input
                    id="thumbnailInput"
                    type="file"
                    accept="image/*"
                    onChange={handleThumbnailChange}
                    className={styles.fileInput}
                  />
                  Thêm ảnh
                </label>
              )}
            </div>
          </div>
          <div className={styles.uploadBlock}>
            <label htmlFor="" className={styles.label}>
              Các ảnh khác
            </label>

            <div className={styles.galleryGrid}>
              {existingImages.map((img) => (
                <div key={`existing-${img.id}`} className={styles.previewBox}>
                  <img src={img.imageUrl} alt="" className={styles.previewImage} />
                  <button
                    type="button"
                    className={styles.btnRemoveImage}
                    onClick={() => handleRemoveExistingImage(img.id)}
                  >
                    X
                  </button>
                </div>
              ))}

              {imagePreviews.map((url, index) => (
                <div key={`new-${index}`} className={styles.previewBox}>
                  <img src={url} alt="" className={styles.previewImage} />
                  <button
                    type="button"
                    className={styles.btnRemoveImage}
                    onClick={() => handleRemoveImage(index)}
                  >
                    X
                  </button>
                </div>
              ))}
              <label htmlFor="detailImagesInput" className={styles.fileLabel}>
                <input
                  id="detailImagesInput"
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={handleDetailImagesChange}
                  className={styles.fileInput}
                />
                <span>Thêm ảnh</span>
              </label>
            </div>
          </div>
        </div>

        <div className={styles.actionRow}>
          <button
            disabled={isCreatingConsignmentItem || isUpdatingConsignmentItem}
            type="submit"
            className={styles.btnSubmit}
          >
            {editingItem
              ? isUpdatingConsignmentItem
                ? "Đang cập nhật..."
                : "Cập nhật chi tiết"
              : isCreatingConsignmentItem
              ? "Đang xử lý..."
              : "Thêm vào lô hàng"}
          </button>
          {editingItem && (
            <button
              type="button"
              className={styles.btnCancel}
              onClick={() => {
                onCancelEdit?.();
                resetForm();
              }}
            >
              Hủy
            </button>
          )}
        </div>
      </form>
    </div>
  );
};

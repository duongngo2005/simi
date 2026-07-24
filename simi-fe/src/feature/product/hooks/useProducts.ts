import { useQuery } from "@tanstack/react-query"
import { getProductDetail, getProducts, getThumbnail } from "../api/productApis"

export const useNewestProducts = () => {
    return useQuery({
        queryKey: ['product', 'newest'],
        queryFn: () => 
            getProducts({
                page: 0,
                size: 20,
                sortBy: "createdDate",
                sortDir: "desc"
            }),
            select: (response) => response.body?.content || []
    })
} 

export const useNewTagProducts = () => {
    return useQuery({
        queryKey: ['product', 'newTag'],
        queryFn: () => getProducts({
            page: 0,
            size: 20,
            sortBy: "createdDate",
            sortDir: "desc",
            productCondition: "NEW_TAG"
        }),
        select: (response) => response.body?.content || []
    })
}

export const useAccessoriesSection = () => {
    return useQuery({
        queryKey: ['products', "accessory"],
        queryFn: () => getProducts({
            page: 0,
            size: 20,
            sortBy: "createdDate",
            sortDir: "desc",
            categorySlug: "phu-kien"
        }),
        select: (response) => response.body?.content || []
    })
}

export const useFootWearSection = () => {
    return useQuery({
        queryKey: ['product', 'footwear'],
        queryFn: () => getProducts({
            categorySlug: "giay-dep"
        }),
        select: (response) => response.body?.content || []
    })
}

export const useThumbnail = (id: number) => {
    return useQuery({
        queryKey: ['product', id, 'thumbnail'],
        queryFn: () => getThumbnail(id),
        enabled: !!id,
        select: (response) => response.body.imageUrl
    })
}

export const useProductDetail = (id: number) => {
    return useQuery({
        queryKey: ["product", id],
        queryFn: () => getProductDetail(id),
        select: (response) => response.body,
        enabled: !!id,
  });
}
  
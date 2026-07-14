import axios from "axios"
import type { ErrorResponse } from "../types/common"

export const getServerError = (error: unknown, fallback = "Đã có lỗi xảy ra"): string => {
    if(axios.isAxiosError<ErrorResponse>(error)){
        return error.response?.data?.message ?? fallback
    }
    return fallback;
}
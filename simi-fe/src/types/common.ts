export interface ApiResponse<T>{
    body: T;
    status: number;
    message: string
}

export interface ErrorResponse{
    status: number;
    message: string;
    path: string;
    timestamp: string;
    errors?: Record<string, string>
}

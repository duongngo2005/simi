import type { UserResponse } from "./user";

export interface AuthTokenResponse{
    accessToken: string;
    tokenType: string;
    userResponse: UserResponse;
}

export interface LoginRequest{
    email: string;
    password: string;
}

export interface RegisterRequest{
    email: string;
    fullName: string;
    password: string;
}
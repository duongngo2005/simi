export type Role = "ADMIN" | "STAFF" | "CUSTOMER";
export type UserStatus = "ACTIVE" | "LOCKED";

export interface UserResponse{
    id: number;
    email: string;
    fullName: string;
    role: Role;
    status: UserStatus;
    avatarUrl: string | null;
    address: string | null;
    phoneNumber: string | null;
}
export interface AiChatTurn {
    sender: "user" | "ai";
    text: string;
}

export interface AiChatRequest {
    message: string;
    history: AiChatTurn[];
}

export interface ProductSuggestion {
    id: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    brandName: string | null;
    size: string | null;
}

export interface AiChatResponse {
    reply: string;
    suggestedProducts: ProductSuggestion[];
}

export interface ChatMessage {
    sender: "user" | "ai";
    text: string;
    products?: ProductSuggestion[];
}
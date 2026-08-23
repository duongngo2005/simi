import api from "../../../lib/http/apiClient";
import type { ApiResponse } from "../../../types/common";
import type { AiChatRequest, AiChatResponse } from "../types/ai.type";

export const sendAiChatMessage = async (payload: AiChatRequest): Promise<AiChatResponse> => {
    const response = await api.post<ApiResponse<AiChatResponse>>("/ai/chat", payload);
    return response.data.body;
};
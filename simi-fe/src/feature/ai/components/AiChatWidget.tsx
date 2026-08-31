import { useState, useRef, useEffect } from "react";
import { Link } from "react-router";
import styles from "./AiChatWidget.module.css";
import { sendAiChatMessage } from "../api/aiApi";
import { formatPrice } from "../../../utils/formatPrice";
import type { AiChatTurn, ChatMessage, ProductSuggestion } from "../types/ai.type";

const INITIAL_MESSAGE: ChatMessage = {
    sender: "ai",
    text: "Xin chào! Mình là Simi Stylist 🌿 Mình có thể giúp bạn tìm đồ tại Simi — cửa hàng ký gửi thời trang chọn lọc. Bạn đang tìm mẫu gì hôm nay?",
};

export const AiChatWidget = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState<ChatMessage[]>([INITIAL_MESSAGE]);
    const [input, setInput] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const bottomRef = useRef<HTMLDivElement>(null);


    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, isLoading]);

    const handleSend = async (e: React.FormEvent) => {
        e.preventDefault();
        const trimmed = input.trim();
        if (!trimmed || isLoading) return;

        const historyPayload: AiChatTurn[] = messages.slice(-6).map((m) => ({
            sender: m.sender,
            text: m.text.substring(0, 500),
        }));

        setMessages((prev) => [...prev, { sender: "user", text: trimmed }]);
        setInput("");
        setIsLoading(true);

        try {
            const response = await sendAiChatMessage({
                message: trimmed,
                history: historyPayload, 
            });

            setMessages((prev) => [
                ...prev,
                {
                    sender: "ai",
                    text: response.reply,
                    products: response.suggestedProducts,
                },
            ]);
        } catch {
            setMessages((prev) => [
                ...prev,
                {
                    sender: "ai",
                    text: "Xin lỗi bạn, Simi đang gặp trục trặc kết nối. Bạn thử lại sau nhé! 🙏",
                },
            ]);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.widgetWrapper}>
            {!isOpen && (
                <button
                    className={styles.triggerBtn}
                    onClick={() => setIsOpen(true)}
                    aria-label="Mở Simi Stylist"
                >
                    <span className={styles.triggerIcon}>🌿</span>
                    <span className={styles.triggerLabel}>Simi Stylist</span>
                </button>
            )}

            {isOpen && (
                <div className={styles.chatBox}>
                    <div className={styles.chatHeader}>
                        <div className={styles.headerInfo}>
                            <div className={styles.avatar}>🌿</div>
                            <div>
                                <div className={styles.botName}>Simi Stylist</div>
                            </div>
                        </div>
                        <button
                            className={styles.closeBtn}
                            onClick={() => setIsOpen(false)}
                            aria-label="Đóng chat"
                        >
                            ✕
                        </button>
                    </div>

                    <div className={styles.chatBody}>
                        {messages.map((msg, idx) => (
                            <div
                                key={idx}
                                className={msg.sender === "user" ? styles.userRow : styles.aiRow}
                            >
                                {msg.sender === "ai" && (
                                    <div className={styles.aiAvatarSmall}>🌿</div>
                                )}
                                <div
                                    className={
                                        msg.sender === "user" ? styles.userBubble : styles.aiBubble
                                    }
                                >
                                    <p className={styles.bubbleText}>{msg.text}</p>

                                    {msg.products && msg.products.length > 0 && (
                                        <div className={styles.productList}>
                                            {msg.products.map((p) => (
                                                <ProductCard key={p.id} product={p} />
                                            ))}
                                        </div>
                                    )}
                                </div>
                            </div>
                        ))}

                        {isLoading && (
                            <div className={styles.aiRow}>
                                <div className={styles.aiAvatarSmall}>🌿</div>
                                <div className={styles.aiBubble}>
                                    <div className={styles.typingDots}>
                                        <span />
                                        <span />
                                        <span />
                                    </div>
                                    <span className={styles.typingText}>Simi Stylist đang tìm đồ cho bạn...</span>
                                </div>
                            </div>
                        )}

                        <div ref={bottomRef} />
                    </div>

                    <form onSubmit={handleSend} className={styles.inputBar}>
                        <input
                            type="text"
                            className={styles.inputField}
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            placeholder="VD: Tìm váy đi tiệc dưới 500k..."
                            disabled={isLoading}
                        />
                        <button
                            type="submit"
                            className={styles.sendBtn}
                            disabled={isLoading || !input.trim()}
                        >
                            Gửi
                        </button>
                    </form>
                </div>
            )}
        </div>
    );
};

const ProductCard = ({ product }: { product: ProductSuggestion }) => (
    <Link to={`/products/${product.id}`} className={styles.productCard}>
        <div className={styles.productThumb}>
            {product.thumbnailUrl ? (
                <img src={product.thumbnailUrl} alt={product.name} />
            ) : (
                <div className={styles.thumbPlaceholder}>📷</div>
            )}
        </div>
        <div className={styles.productInfo}>
            <div className={styles.productName}>{product.name}</div>
            {(product.brandName || product.size) && (
                <div className={styles.productMeta}>
                    {product.brandName && <span>{product.brandName}</span>}
                    {product.size && <span>Size {product.size}</span>}
                </div>
            )}
            <div className={styles.productPrice}>{formatPrice(product.price)}</div>
        </div>
        <div className={styles.productArrow}>›</div>
    </Link>
);

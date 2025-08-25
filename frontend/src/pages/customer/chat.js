import { useContext, useEffect, useState, useRef } from "react";
import { getTinNhanOfKhacHang } from "../../services/TinNhanService";
import { toast } from "react-toastify";
import { WebSocketContext } from "../../components/WebSocketContext";
import { Typing } from "../../components/Typing";
import { uploadImage } from "../../services/uploadImage";
import { RenderImage, renderImageToHTML } from "../../components/commons/renderImage";

function isElementInView(container, elementId) {
    document.getElementById('noidung').focus();
    const containerEl = container instanceof HTMLElement ? container : document.getElementById(container);
    const element = document.getElementById(elementId);

    if (!containerEl || !element) return false;

    const containerRect = containerEl.getBoundingClientRect();
    const elementRect = element.getBoundingClientRect();

    return (
        elementRect.top >= containerRect.top &&
        elementRect.bottom <= containerRect.bottom
    );
}

function isAtBottom(element) {
    return element.scrollTop + element.clientHeight >= element.scrollHeight - 10; // Sai số nhỏ để linh hoạt
}

function ChatCustomer({ setOpen }) {
    const [id, setId] = useState(0);
    const [danhSachTinNhan, setDanhSachTinNhan] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [likedMessages, setLikedMessages] = useState({});
    const chatContainerRef = useRef(null);
    const previousCustomerIdRef = useRef(null);
    const viTriCha = useRef("");
    const top = useRef(4000000000);
    const [viTriCuoi, setViTriCuoi] = useState("");
    const viTriCuoiRef = useRef("");
    let idd = localStorage.getItem("id");
    const [typ, setTyp] = useState(false);
    const fileInputRef = useRef(null);
    const [reply, setReply] = useState(null);

    const triggerFileInput = () => {
        fileInputRef.current.click();
    };

    const handleFileSelect = async (event) => {
        const files = event.target.files;

        if (!files || files.length === 0) {
            return;
        }

        try {
            const uploadPromises = Array.from(files).map((file) => uploadImage(file));
            const imageUrls = await Promise.all(uploadPromises);
            let t = document.getElementById("noidung").value;
            sendMessage({ type: "MESSAGE", noiDungTinNhan: t, khachHang: 0, listImage: imageUrls });
        } catch (error) {
            alert("Đã xảy ra lỗi khi upload ảnh");
        }
    };

    useEffect(() => {
        return () => clearTimeout(timeoutRef.current);
    }, []);

    const customerPickRef = useRef(idd);

    useEffect(() => {
        viTriCuoiRef.current = viTriCuoi;
    }, [viTriCuoi]);

    const { sendMessage, addMessageListener, removeMessageListener } = useContext(WebSocketContext);

    const dangGo = useRef(false);
    const timeoutRef = useRef(null);

    const handleChange = (e) => {
        if (!dangGo.current) {
            dangGo.current = true;
            sendMessage({
                type: "USERACTIVITY",
                activity: 2,
                userBiTacDong: customerPickRef.current?.id,
            });
        }
        clearTimeout(timeoutRef.current);
        timeoutRef.current = setTimeout(() => {
            sendMessage({
                type: "USERACTIVITY",
                activity: 3,
                userBiTacDong: customerPickRef.current?.id,
            });
            dangGo.current = false;
        }, 1000);
    };

    useEffect(() => {
        const handler = (msg) => {
            if (msg.type === "MESSAGE") {
                let imageHTML = "";
                if (msg?.listImage?.length > 0) {
                    imageHTML = renderImageToHTML(msg.listImage);
                }

                sendMessage({
                    tinNhan: [msg.idtinNhan],
                    typeView: 2,
                    type: "VIEWMESSAGE",
                });

                let replyHTML = "";
                if (msg?.re !== 0) {
                    const trichdan = (msg?.noiDungRe || "").slice(0, 7);
                    const dots = (msg?.noiDungRe || "").length > 7 ? "..." : "";
                    replyHTML = `
                        <div 
                            onclick="document.getElementById('msg-${msg.re}').scrollIntoView({ behavior: 'smooth' });"
                            class="relative cursor-pointer pl-6 pt-2 pb-2 bg-gray-50 rounded-md shadow-sm"
                        >
                            <p class="text-sm text-gray-500 flex items-center">
                                <img class="w-5 mr-2" src="https://cdn-icons-png.flaticon.com/128/591/591866.png" alt="reply icon" />
                                Trả lời ${customerPickRef?.current?.tenKhach || ""}
                            </p>
                            <p class="text-sm text-gray-500 ml-7">${trichdan}${dots}</p>
                        </div>
                    `;
                }

                let t = document.getElementById("khungchat");
                if (!t) return;

                const wasAtBottom = isAtBottom(t);

                let trangThai =
                    msg?.statusNhanVien === 0 ? "Đã gửi" :
                    msg?.statusNhanVien === 1 ? "Đã nhận" :
                    msg?.statusNhanVien === 2 ? "Đã xem" : "";

                let trangThaiHTML = msg?.nhanvien === 0
                    ? `
                        <div class="mt-1 text-xs text-white bg-gray-400 px-3 py-1 rounded-full flex items-center gap-1 justify-end">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                            </svg>
                            <span id="status-${msg.idtinNhan}">${trangThai}</span>
                        </div>`
                    : "";

                let noidung = `
                    <div
                        id="msg-${msg.idtinNhan}"
                        class="message-container mt-4 ${msg.nhanvien !== 0 ? "" : "message-sent flex-row-reverse"} flex items-start gap-2 new-message"
                    >
                        <div class="avatar w-8 h-8 rounded-full bg-gray-300 overflow-hidden">
                            <img
                                src="${msg.nhanvien !== 0 ? "https://tse1.mm.bing.net/th/id/OIP.7N_T9RrGbWdgT_eRzIkGFQHaHa?pid=Api&P=0&h=220" : "https://cdn-icons-png.flaticon.com/512/149/149071.png"}"
                                alt="Avatar"
                                class="w-full h-full object-cover"
                            />
                        </div>
                        <div class="flex flex-col ${msg.nhanvien !== 0 ? "items-start l-0" : "items-end"} max-w-[70%]">
                            ${imageHTML}
                            ${replyHTML}
                            <div class="relative mt-3 group" style="padding-right: 30px;">
                                <div class="shadow-sm p-3 rounded-xl ${msg.nhanvien !== 0 ? "bg-white text-black" : "bg-green-900 text-white"} pr-6">
                                    <p>${msg.noiDungTinNhan || "Tin nhắn không có nội dung"}</p>
                                    <p class="text-xs mt-1">${msg.ngayGui}</p>
                                </div>
                                <img
                                    src="https://cdn-icons-png.flaticon.com/128/591/591866.png"
                                    alt="Reply"
                                    class="w-5 h-5 absolute right-1 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 cursor-pointer"
                                    id="reply-${msg.idtinNhan}"
                                />
                                <div
                                    class="absolute -bottom-2 p-1 right-1 bg-white rounded-full shadow-md cursor-pointer ${msg.nhanvien === 0 ? "pointer-events-none" : ""}"
                                >
                                    <img
                                        id="heart-${msg.idtinNhan}"
                                        src="${msg.daTim ? "https://toppng.com/uploads/preview/like-yellow-icon-like-icon-yellow-11553392640ctcuya9tln.png" : "https://cdn-icons-png.flaticon.com/128/9758/9758347.png"}"
                                        alt="Like"
                                        class="w-4 h-4"
                                    />
                                </div>
                            </div>
                            ${trangThaiHTML}
                        </div>
                    </div>
                `;

                t.insertAdjacentHTML("beforeend", noidung);

                if (wasAtBottom) {
                    requestAnimationFrame(() => {
                        t.scrollTop = t.scrollHeight;
                    });
                }

                setTimeout(() => {
                    const messageElement = document.getElementById(`msg-${msg.idtinNhan}`);
                    if (messageElement) {
                        messageElement.classList.remove("new-message");
                    }
                }, 500);

                viTriCuoiRef.current = `msg-${msg.idtinNhan}`;

                const replyElement = document.getElementById(`reply-${msg.idtinNhan}`);
                replyElement.addEventListener("click", () => {
                    setReply(msg);
                });
                const heartElement = document.getElementById(`heart-${msg.idtinNhan}`);
                if (heartElement) {
                    heartElement.addEventListener("click", () => {
                        sendMessage({
                            type: "DROPHEART",
                            active: !msg.daTim,
                            tinNhanDuocTim: msg.idtinNhan,
                        });
                        heartElement.src = !msg.daTim
                            ? "https://toppng.com/uploads/preview/like-yellow-icon-like-icon-yellow-11553392640ctcuya9tln.png"
                            : "https://cdn-icons-png.flaticon.com/128/9758/9758347.png";
                        msg.daTim = !msg.daTim;
                    });
                }
            } else if (msg.type === "DROPHEART") {
                let m = document.getElementById("heart-" + msg.tinNhanDuocTim);
                if (m != null) {
                    m.src = msg.active
                        ? "https://toppng.com/uploads/preview/like-yellow-icon-like-icon-yellow-11553392640ctcuya9tln.png"
                        : "https://cdn-icons-png.flaticon.com/128/9758/9758347.png";
                }
            } else if (msg.type === "VIEWMESSGE") {
                const danhSachTinNhan = msg.tinNhan;
                danhSachTinNhan.forEach(id => {
                    const spanElement = document.getElementById("status-" + id);
                    if (spanElement) {
                        spanElement.innerText = "Đã xem";
                        spanElement.classList.add("da-xem");
                    }
                });
            } else if (msg.type === "USERACTIVITY") {
                if (msg.activity === 2) {
                    setTyp(true);
                } else {
                    setTyp(false);
                }
            }
        };

        addMessageListener(handler);
        return () => removeMessageListener(handler);
    }, []);

    useEffect(() => {
        const chatBox = document.getElementById("khungchat");
        if (!chatBox) return;

        const handleScroll = () => {
            if (chatBox.scrollTop === 0 && !isLoading) {
                setIsLoading(true);
                const previousHeight = chatBox.scrollHeight;
                const previousScrollTop = chatBox.scrollTop;

                getTinNhanOfKhacHang(0, top.current)
                    .then((d) => {
                        if (d.length === 0) {
                            setIsLoading(false);
                            return;
                        }

                        d.forEach((msg) => {
                            let imageHTML = "";
                            if (msg?.listImage?.length > 0) {
                                imageHTML = renderImageToHTML(msg.listImage);
                            }

                            let replyHTML = "";
                            if (msg?.re !== 0) {
                                const trichdan = (msg?.noiDungRe || "").slice(0, 7);
                                const dots = (msg?.noiDungRe || "").length > 7 ? "..." : "";
                                replyHTML = `
                                    <div 
                                        onclick="document.getElementById('msg-${msg.re}').scrollIntoView({ behavior: 'smooth' });"
                                        class="relative cursor-pointer pl-6 pt-2 pb-2 bg-gray-50 rounded-md shadow-sm"
                                    >
                                        <p class="text-sm text-gray-500 flex items-center">
                                            <img class="w-5 mr-2" src="https://cdn-icons-png.flaticon.com/128/591/591866.png" alt="reply icon" />
                                            Trả lời ${customerPickRef?.current?.tenKhach || ""}
                                        </p>
                                        <p class="text-sm text-gray-500 ml-7">${trichdan}${dots}</p>
                                    </div>
                                `;
                            }

                            let t = document.getElementById("khungchat");
                            if (!t) return;

                            let trangThai =
                                msg?.statusNhanVien === 0 ? "Đã gửi" :
                                msg?.statusNhanVien === 1 ? "Đã nhận" :
                                msg?.statusNhanVien === 2 ? "Đã xem" : "";

                            let trangThaiHTML = msg?.nhanvien === 0
                                ? `
                                    <div class="mt-1 text-xs text-white bg-gray-400 px-3 py-1 rounded-full flex items-center gap-1 justify-end">
                                        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                                        </svg>
                                        <span id="status-${msg.idtinNhan}">${trangThai}</span>
                                    </div>`
                                : "";

                            let noidung = `
                                <div
                                    id="msg-${msg.idtinNhan}"
                                    class="message-container mt-4 ${msg.nhanvien !== 0 ? "" : "message-sent flex-row-reverse"} flex items-start gap-2"
                                >
                                    <div class="avatar w-8 h-8 rounded-full bg-gray-300 overflow-hidden">
                                        <img
                                            src="${msg.nhanvien !== 0 ? "https://tse1.mm.bing.net/th/id/OIP.7N_T9RrGbWdgT_eRzIkGFQHaHa?pid=Api&P=0&h=220" : "https://cdn-icons-png.flaticon.com/512/149/149071.png"}"
                                            alt="Avatar"
                                            class="w-full h-full object-cover"
                                        />
                                    </div>
                                    <div class="flex flex-col ${msg.nhanvien !== 0 ? "items-start l-0" : "items-end"} max-w-[70%]">
                                        ${imageHTML}
                                        ${replyHTML}
                                        <div class="relative mt-3 group" style="padding-right: 30px;">
                                            <div class="shadow-sm p-3 rounded-xl ${msg.nhanvien !== 0 ? "bg-white text-black" : "bg-green-900 text-white"} pr-6">
                                                <p>${msg.noiDungTinNhan || "Tin nhắn không có nội dung"}</p>
                                                <p class="text-xs mt-1">${msg.ngayGui}</p>
                                            </div>
                                            <img
                                                src="https://cdn-icons-png.flaticon.com/128/591/591866.png"
                                                alt="Reply"
                                                class="w-5 h-5 absolute right-1 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 cursor-pointer"
                                                id="reply-${msg.idtinNhan}"
                                            />
                                            <div
                                                class="absolute -bottom-2 p-1 right-1 bg-white rounded-full shadow-md cursor-pointer ${msg.nhanvien === 0 ? "pointer-events-none" : ""}"
                                            >
                                                <img
                                                    id="heart-${msg.idtinNhan}"
                                                    src="${msg.daTim ? "https://toppng.com/uploads/preview/like-yellow-icon-like-icon-yellow-11553392640ctcuya9tln.png" : "https://cdn-icons-png.flaticon.com/128/9758/9758347.png"}"
                                                    alt="Like"
                                                    class="w-4 h-4"
                                                />
                                            </div>
                                        </div>
                                        ${trangThaiHTML}
                                    </div>
                                </div>
                            `;

                            t.insertAdjacentHTML("afterbegin", noidung);
                            const replyElement = document.getElementById(`reply-${msg.idtinNhan}`);
                            replyElement.addEventListener("click", () => {
                                setReply(msg);
                            });
                            const heartElement = document.getElementById(`heart-${msg.idtinNhan}`);
                            if (heartElement) {
                                heartElement.addEventListener("click", () => {
                                    sendMessage({
                                        type: "DROPHEART",
                                        active: !msg.daTim,
                                        tinNhanDuocTim: msg.idtinNhan,
                                    });
                                    heartElement.src = !msg.daTim
                                        ? "https://toppng.com/uploads/preview/like-yellow-icon-like-icon-yellow-11553392640ctcuya9tln.png"
                                        : "https://cdn-icons-png.flaticon.com/128/9758/9758347.png";
                                    msg.daTim = !msg.daTim;
                                });
                            }
                        });

                        const newHeight = chatBox.scrollHeight;
                        chatBox.scrollTop = previousScrollTop + (newHeight - previousHeight);
                        top.current = d?.[d.length - 1]?.idtinNhan || 0;
                        setIsLoading(false);
                    })
                    .catch((e) => {
                        setIsLoading(false);
                        toast.error("Lỗi khi tải tin nhắn cũ");
                    });
            }
        };

        chatBox.addEventListener("scroll", handleScroll);
        return () => {
            chatBox.removeEventListener("scroll", handleScroll);
        };
    }, [isLoading]);

    useEffect(() => {
        setIsLoading(true);
        sendMessage({
            tinNhan: [],
            typeView: 2,
            type: "VIEWMESSAGE",
        });
        getTinNhanOfKhacHang(0, 40000000)
            .then((d) => {
                setDanhSachTinNhan(d);
                top.current = d?.[d.length - 1]?.idtinNhan || 0;
                setIsLoading(false);
                if (d?.length !== 0) {
                    setViTriCuoi("msg-" + d?.[0]?.idtinNhan);
                    requestAnimationFrame(() => {
                        const chatBox = document.getElementById("khungchat");
                        if (chatBox) {
                            chatBox.scrollTop = chatBox.scrollHeight;
                        }
                    });
                }
            })
            .catch(() => {
                setIsLoading(false);
                toast.error("Lỗi khi tải tin nhắn");
            });
    }, []);

    const toggleLike = (messageId) => {
        setLikedMessages((prev) => ({
            ...prev,
            [messageId]: !prev[messageId],
        }));
    };

    const formatDate = (timestamp) => {
        const date = new Date(timestamp);
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);

        if (date.toDateString() === today.toDateString()) {
            return "Today";
        } else if (date.toDateString() === yesterday.toDateString()) {
            return "Yesterday";
        }
        return date.toLocaleDateString("vi-VN");
    };

    const groupMessagesByDate = (messages) => {
        const grouped = [];
        let currentDate = null;

        messages.forEach((msg) => {
            const messageDate = formatDate(msg.ngayGioNhan);
            if (messageDate !== currentDate) {
                grouped.push({ type: "date", date: messageDate });
                currentDate = messageDate;
            }
            grouped.push({ type: "message", data: msg });
        });

        return grouped.reverse();
    };

    const groupedMessages = groupMessagesByDate(danhSachTinNhan);

    return (
        <div className="sm:h-[584px] md:h-[555px] md:w-[40%] sm:w-[100%] bg-gray-100 fixed right-0 shadow-md z-[1000]">
            <style>
                {`
                    .chat-item {
                        transition: transform 0.5s ease-in-out, opacity 0.5s ease-in-out;
                    }
                    .chat-item.moving {
                        transform: translateY(-10px);
                        opacity: 0.7;
                    }
                    .loading {
                        text-align: center;
                        padding: 10px;
                        color: gray;
                    }
                    .like-button {
                        cursor: pointer;
                        transition: color 0.2s;
                    }
                    .like-button:hover {
                        color: #ff4d4f;
                    }
                    .liked {
                        color: #ff4d4f;
                        fill: #ff4d4f;
                    }
                    .message-container {
                        display: flex;
                        align-items: flex-start;
                        margin-bottom: 8px;
                    }
                    .message-sent {
                        flex-direction: row-reverse;
                    }
                    .avatar {
                        width: 32px;
                        height: 32px;
                        border-radius: 50%;
                        background-color: #d1d5db;
                        margin: 0 8px;
                    }
                    .message-bubble {
                        max-width: 60%;
                        padding: 10px;
                        border-radius: 12px;
                        position: relative;
                    }
                    .timestamp {
                        font-size: 0.75rem;
                        color: #6b7280;
                        margin-top: 4px;
                        text-align: left;
                    }
                    .new-message {
                        animation: slideIn 0.5s ease-in-out;
                    }
                    @keyframes slideIn {
                        from {
                            opacity: 0;
                            transform: translateY(20px);
                        }
                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }
                `}
            </style>
            <div className="flex h-full">
                <div className="w-[100%] flex flex-col">
                    <div className="p-4 bg-white border-b border-gray-200 flex items-center">
                        <div className="avatar w-8 h-8 rounded-full bg-gray-300 overflow-hidden">
                            <img
                                src="https://tse1.mm.bing.net/th/id/OIP.7N_T9RrGbWdgT_eRzIkGFQHaHa?pid=Api&P=0&h=220"
                                alt="Avatar"
                                className="w-full h-full object-cover"
                            />
                        </div>
                        <div>
                            <h2 className="font-semibold">SKINLY</h2>
                        </div>
                        <div className="ml-auto flex space-x-2">
                            <button
                                onClick={() => {
                                    setOpen(false);
                                }}
                                className="p-2 hover:bg-gray-100 rounded"
                            >
                                <img src="https://cdn-icons-png.flaticon.com/128/447/447047.png" className="w-5" />
                            </button>
                        </div>
                    </div>
                    <div
                        className="flex-1 p-4 overflow-y-auto bg-gray-50 relative"
                        ref={chatContainerRef}
                        id="khungchat"
                        style={{ display: "flex", flexDirection: "column" }}
                    >
                        {isLoading && <div className="loading">Đang tải tin nhắn...</div>}
                        {groupedMessages.map((item, index) =>
                            item.type === "date" ? (
                                <div key={`date-${index}`} className="mb-4">
                                    <p className="text-sm text-gray-500 text-center">{item.date}</p>
                                </div>
                            ) : (
                                <div
                                    key={`msg-${item.data.idtinNhan}`}
                                    id={`msg-${item.data.idtinNhan}`}
                                    className={`message-container flex ${item.data.nhanvien != 0 ? "" : "flex-row-reverse"} gap-2 mt-4`}
                                >
                                    <div className="avatar w-8 h-8 rounded-full bg-gray-300 overflow-hidden">
                                        <img
                                            src={
                                                item.data.nhanvien != 0
                                                    ? "https://tse1.mm.bing.net/th/id/OIP.7N_T9RrGbWdgT_eRzIkGFQHaHa?pid=Api&P=0&h=220"
                                                    : "https://cdn-icons-png.flaticon.com/512/149/149071.png"
                                            }
                                            alt="Avatar"
                                            className="w-full h-full object-cover"
                                        />
                                    </div>
                                    <div
                                        className={`flex flex-col max-w-[70%] ${item.data.nhanvien != 0 ? "items-start" : "items-end"}`}
                                    >
                                        {item.data?.listImage?.length > 0 && (
                                            <div className="shadow-md p-1">
                                                <RenderImage images={item.data?.listImage} />
                                            </div>
                                        )}
                                        <div className="relative mt-3 inline-block pr-[30px] group">
                                            {item?.data?.re != 0 && (
                                                <div
                                                    onClick={() => {
                                                        document
                                                            .getElementById("msg-" + item?.data?.re)
                                                            ?.scrollIntoView({ behavior: "smooth" });
                                                    }}
                                                    className="relative cursor-pointer pl-6 pt-2 pb-2 bg-gray-50 rounded-md shadow-sm mb-1"
                                                >
                                                    <p className="text-sm text-gray-500 flex items-center">
                                                        <img
                                                            className="w-5 mr-2"
                                                            src="https://cdn-icons-png.flaticon.com/128/591/591866.png"
                                                            alt="reply icon"
                                                        />
                                                        Trả lời {customerPickRef?.current?.tenKhach}
                                                    </p>
                                                    <p className="text-sm text-gray-500 ml-7">
                                                        {(item.data?.noiDungRe || "").slice(0, 7)}
                                                        {item.data?.noiDungRe?.length > 7 ? "..." : ""}
                                                    </p>
                                                </div>
                                            )}
                                            <div
                                                className={`shadow-sm p-3 rounded-xl pr-6 ${item.data.nhanvien != 0 ? "bg-white text-black" : "bg-green-900 text-white"}`}
                                            >
                                                <p>{item.data.noiDungTinNhan || "Tin nhắn không có nội dung"}</p>
                                                <p className="text-xs mt-1">{item.data.ngayGui}</p>
                                            </div>
                                            <img
                                                src="https://cdn-icons-png.flaticon.com/128/591/591866.png"
                                                alt="Reply"
                                                className="w-5 h-5 absolute right-1 top-1/2 -translate-y-1/2 opacity-0 group-hover:opacity-100 transition-opacity duration-200 cursor-pointer"
                                                onClick={() => {
                                                    setReply(item.data);
                                                }}
                                            />
                                            <div
                                                className={`absolute -bottom-2 p-1 right-1 bg-white rounded-full shadow-md cursor-pointer ${item.data.nhanvien == 0 ? "pointer-events-none" : ""}`}
                                            >
                                                <img
                                                    onClick={() =>
                                                        sendMessage({
                                                            type: "DROPHEART",
                                                            active: !item.data.daTim,
                                                            tinNhanDuocTim: item.data.idtinNhan,
                                                        })
                                                    }
                                                    id={"heart-" + item.data.idtinNhan}
                                                    src={
                                                        item.data.daTim
                                                            ? "https://toppng.com/uploads/preview/like-yellow-icon-like-icon-yellow-11553392640ctcuya9tln.png"
                                                            : "https://cdn-icons-png.flaticon.com/128/9758/9758347.png"
                                                    }
                                                    alt="Like"
                                                    className="w-4 h-4"
                                                />
                                            </div>
                                        </div>
                                        {item?.data?.nhanvien === 0 && (
                                            <div className="mt-1 text-xs text-white bg-gray-400 px-3 py-1 rounded-full flex items-center gap-1 justify-end">
                                                <svg
                                                    xmlns="http://www.w3.org/2000/svg"
                                                    className="h-4 w-4 text-white"
                                                    fill="none"
                                                    viewBox="0 0 24 24"
                                                    stroke="currentColor"
                                                >
                                                    <path
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                        strokeWidth="2"
                                                        d="M5 13l4 4L19 7"
                                                    />
                                                </svg>
                                                <span id={"status-" + item.data.idtinNhan} className="text-sm">
                                                    {item?.data?.statusNhanVien === 0 && "Đã gửi"}
                                                    {item?.data?.statusNhanVien === 1 && "Đã nhận"}
                                                    {item?.data?.statusNhanVien === 2 && "Đã xem"}
                                                </span>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            )
                        )}
                    </div>
                    {typ && <Typing ten={"SKINLY"} />}
                    {reply && (
                        <div className="relative pl-6 pr-8 pt-2 pb-2 bg-gray-50 rounded-md shadow-sm">
                            <button
                                onClick={() => setReply(null)}
                                className="absolute top-1 right-1 text-gray-500 text-sm"
                            >
                                ✕
                            </button>
                            <p className="text-sm text-gray-500 flex items-center">
                                <img
                                    className="w-5 mr-2"
                                    src="https://cdn-icons-png.flaticon.com/128/591/591866.png"
                                    alt="reply icon"
                                />
                                Trả lời tin nhắn của {customerPickRef?.current?.tenKhach}
                            </p>
                            <p className="text-sm text-gray-500 ml-7">{reply?.noiDungTinNhan}</p>
                        </div>
                    )}
                    <div className="p-4 bg-white border-t border-gray-200">
                        <div className="flex items-center">
                            <button
                                onClick={triggerFileInput}
                                className="mr-2 p-2 text-gray-800 rounded-lg"
                            >
                                <img
                                    className="w-8"
                                    src="https://cdn-icons-png.flaticon.com/128/8138/8138518.png"
                                />
                            </button>
                            <input
                                type="file"
                                ref={fileInputRef}
                                onChange={handleFileSelect}
                                accept="image/*"
                                multiple
                                className="hidden"
                            />
                            <input
                                type="text"
                                onChange={handleChange}
                                id="noidung"
                                placeholder="Type a message..."
                                className="flex-1 p-2 rounded-lg outline-none"
                            />
                            <button
                                onClick={() => {
                                    let t = document.getElementById("noidung").value;
                                    if (t === "") {
                                        toast.error("Vui lòng nhập nội dung tin nhắn!!!");
                                        return;
                                    }
                                    sendMessage({
                                        type: "MESSAGE",
                                        noiDungTinNhan: t,
                                        re: reply?.idtinNhan || 0,
                                    });
                                    document.getElementById("noidung").value = ""; // Clear input sau khi gửi
                                }}
                                className="ml-2 p-2 bg-green-900 text-white rounded-lg hover:bg-green-700"
                            >
                                Send
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export { ChatCustomer };
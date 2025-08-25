import { useContext, useEffect, useState, useRef } from "react";
import { getInitEmployee, getTinNhanOfKhacHang } from "../../services/TinNhanService";
import { toast } from "react-toastify";
import { WebSocketContext } from "../../components/WebSocketContext";
import { Typing } from "../../components/Typing";
import { uploadImage } from "../../services/uploadImage";
import { RenderImage, renderImageToHTML } from "../../components/commons/renderImage";
import notificationSound from '../../assets/notification.wav';
import { Alert } from "antd";

function isElementInView(container, elementId) {
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

function Chat() {
    const [imgPick, setImgPick] = useState("");
    const [openImg, setOpenImg] = useState(false);
    const [danhSachKhoiTao, setDanhSachKhoiTao] = useState([]);
    const [id, setId] = useState(0);
    const [customerPick, setCustomerPick] = useState(null);
    const [danhSachTinNhan, setDanhSachTinNhan] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [likedMessages, setLikedMessages] = useState({});
    const chatContainerRef = useRef(null);
    const previousCustomerIdRef = useRef(null);
    const viTriCha = useRef("");
    const top = useRef(4000000000);
    const [viTriCuoi, setViTriCuoi] = useState("");
    const viTriCuoiRef = useRef("");
    const [typing, setTyping] = useState(false);
    const lan = useRef(0);
    const fileInputRef = useRef(null);
    const [reply, setReply] = useState(null);
    const [search, setSearch] = useState("");
    const [danhSachMount, setDanhSachMount] = useState([]);
    const [isUploading, setIsUploading] = useState(false);

    const handleFileSelect = async (event) => {
        const files = event.target.files;

        if (!files || files.length === 0) {
            return;
        }

        setIsUploading(true);

        try {
            const uploadPromises = Array.from(files).map((file) => uploadImage(file));
            const imageUrls = await Promise.all(uploadPromises);
            let t = document.getElementById("noidung").value;
            sendMessage({ type: "MESSAGE", noiDungTinNhan: t, khachHang: customerPick?.id, listImage: imageUrls });
        } catch (error) {
            alert("Đã xảy ra lỗi khi upload ảnh");
        } finally {
            setIsUploading(false);
        }
    };

    const triggerFileInput = () => {
        fileInputRef.current.click();
    };

    useEffect(() => {
        return () => clearTimeout(timeoutRef.current);
    }, []);

    useEffect(() => {
        setId(localStorage.getItem("id"));
        getInitEmployee()
            .then((d) => {
                setDanhSachKhoiTao(d);
            })
            .catch(() => {
                toast("Lấy dữ liệu thất bại, vui lòng thử lại");
            });
    }, []);

    const customerPickRef = useRef(null);

    useEffect(() => {
        viTriCuoiRef.current = viTriCuoi;
    }, [viTriCuoi]);

    const { sendMessage, addMessageListener, removeMessageListener } = useContext(WebSocketContext);

    useEffect(() => {
        customerPickRef.current = customerPick;
    }, [customerPick]);

    const dangGo = useRef(false); // Đã gửi "đang gõ" hay chưa
    const timeoutRef = useRef(null);

    const handleChange = (e) => {
        if (!dangGo.current) {
            // Nếu chưa báo "đang gõ", thì báo và set flag
            dangGo.current = true;
            sendMessage({
                type: "USERACTIVITY",
                activity: 2,
                userBiTacDong: customerPickRef.current?.id,
            });
        }

        // Mỗi lần gõ, clear timeout cũ
        clearTimeout(timeoutRef.current);

        // Set lại timeout mới để báo "ngưng gõ" sau 2s
        timeoutRef.current = setTimeout(() => {
            sendMessage({
                type: "USERACTIVITY",
                activity: 3,
                userBiTacDong: customerPickRef.current?.id,
            });
            dangGo.current = false;
        }, 2000);
    };

    useEffect(() => {
        const handler = (msg) => {
            console.log(msg);

            if (msg.type === "MESSAGE") {
                const audio = document.getElementById("notification-sound");
                if (audio) {
                    audio.play();
                }

                // Update danhSachKhoiTao state
                setDanhSachKhoiTao((prev) => {
                    const index = prev.findIndex((item) => item.id === msg.khachHang);
                    if (index === -1) return prev;

                    const newList = [...prev];
                    const updatedItem = {
                        ...newList[index],
                        noiDungTinNhan: msg.noiDungTinNhan,
                        khach: msg.nhanvien === 0,
                        statusNhanVien: customerPickRef.current?.id === msg.khachHang ? 2 : 0,
                        thoiHanNhan: msg.thoiHanNhan,
                    };

                    newList.splice(index, 1);
                    return [updatedItem, ...newList];
                });

                // Render message if it belongs to the current customer
                if (msg.khachHang === customerPickRef.current?.id) {
                    // Kiểm tra xem có đang ở dưới đáy không trước khi thêm tin nhắn
                    let t = document.getElementById("khungchat");
                    const wasAtBottom = isAtBottom(t);

                    // Thêm tin nhắn vào state với flag isNew để áp dụng hiệu ứng
                    setDanhSachTinNhan((prev) => [...prev, { ...msg, isNew: true }]);

                    // Nếu đang ở dưới đáy, cuộn xuống
                    if (wasAtBottom) {
                        requestAnimationFrame(() => {
                            t.scrollTop = t.scrollHeight;
                        });
                    }

                    // Xóa flag isNew sau thời gian animation (0.5s)
                    setTimeout(() => {
                        setDanhSachTinNhan((prev) =>
                            prev.map((item) =>
                                item.idtinNhan === msg.idtinNhan
                                    ? { ...item, isNew: false }
                                    : item
                            )
                        );
                    }, 500);

                    setViTriCuoi(`msg-${msg.idtinNhan}`);
                }
            } else if (msg.type === "DROPHEART") {
                setDanhSachTinNhan((prev) =>
                    prev.map((item) =>
                        item.idtinNhan === msg.tinNhanDuocTim
                            ? { ...item, daTim: msg.active }
                            : item
                    )
                );
            } else if (msg.type === "VIEWMESSGE") {
                const danhSachTinNhanIds = msg.tinNhan;

                setDanhSachTinNhan((prev) =>
                    prev.map((item) =>
                        danhSachTinNhanIds.includes(item.idtinNhan)
                            ? { ...item, statusKhachHang: 2 }
                            : item
                    )
                );
            } else if (msg.type === "USERACTIVITY") {
                console.log("type :" + msg.activity);
                if (msg.activity === 0) {
                    setDanhSachKhoiTao((prev) =>
                        prev.map((item) =>
                            item.id === msg.idUser
                                ? { ...item, dangHoatDong: false }
                                : item
                        )
                    );
                }
                if (msg.activity === 1) {
                    console.log(msg);
                    sendMessage({ type: "MOUNTUSER", idUser: customerPickRef.current?.id, mount: true });
                    setDanhSachKhoiTao((prev) =>
                        prev.map((item) =>
                            item.id === msg.idUser
                                ? { ...item, dangHoatDong: true }
                                : item
                        )
                    );
                }
                if (msg.activity === 2) {
                    setTyping(true);
                }
                if (msg.activity === 3) {
                    setTyping(false);
                }
            } else if (msg.type === "MOUNTUSER") {
                const container = document.getElementById("danhSachDangChat");
                container.innerHTML = "";
                const currentUser = localStorage.getItem("name");

                const colors = [
                    { bg: "#FEF9C3", text: "#F59E0B" }, // yellow-100 / yellow-500
                    { bg: "#DBEAFE", text: "#3B82F6" }, // blue-100 / blue-500
                    { bg: "#DCFCE7", text: "#22C55E" }  // green-100 / green-500
                ];

                let index = 0;

                msg.danhSachDangChat.forEach((tenNguoiDung) => {
                    if (tenNguoiDung !== currentUser) {
                        const avatar = document.createElement("div");
                        const icon = document.createElement("i");

                        icon.className = "fa-regular fa-user";
                        icon.style.fontSize = "18px";

                        const color = colors[index % colors.length];
                        index++;

                        avatar.style.width = "40px";
                        avatar.style.height = "40px";
                        avatar.style.borderRadius = "50%";
                        avatar.style.backgroundColor = color.bg;
                        avatar.style.color = color.text;
                        avatar.style.display = "flex";
                        avatar.style.alignItems = "center";
                        avatar.style.justifyContent = "center";
                        avatar.style.margin = "5px";

                        avatar.title = tenNguoiDung;
                        avatar.appendChild(icon);
                        container.appendChild(avatar);
                    }
                });
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

                getTinNhanOfKhacHang(customerPickRef.current?.id, top.current)
                    .then((d) => {
                        if (d.length === 0) {
                            setIsLoading(false);
                            return;
                        }

                        // Thêm tin nhắn cũ vào đầu state
                        setDanhSachTinNhan((prev) => [...d, ...prev]);

                        // Adjust scroll position to maintain the view
                        requestAnimationFrame(() => {
                            const newHeight = chatBox.scrollHeight;
                            chatBox.scrollTop = newHeight - previousHeight;
                        });

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
        if (customerPick?.id) {
            setIsLoading(true);
            getTinNhanOfKhacHang(customerPick.id, 40000000)
                .then((d) => {
                    setDanhSachTinNhan(d);
                    top.current = d?.[d.length - 1]?.idtinNhan || 0;
                    setIsLoading(false);
                    if (d?.length !== 0) {
                        setViTriCuoi("msg-" + d?.[d.length - 1]?.idtinNhan);
                        // Cuộn xuống đáy khi chuyển khách hàng
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
        }
    }, [customerPick]);

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

        return grouped;
    };

    const groupedMessages = groupMessagesByDate(danhSachTinNhan);

    return (
        <div className="sm:h-[580px] md:h-[540px] bg-gray-100">
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
                <div className="w-[30%] bg-white border-r border-gray-200 overflow-y-auto">
                    <div className="p-4 bg-white border-b border-gray-200 h-[73px] flex items-center justify-between shadow-sm">
                        <div className="flex items-center">
                            <i className="fa-regular fa-comments text-lg p-2 bg-indigo-100 text-indigo-600 rounded-lg"></i>
                            <div className="ml-3">
                                <h2 className="text-xl font-bold text-gray-800">Skinly Chat</h2>
                            </div>
                        </div>
                        <div className="flex items-center bg-gray-100 rounded-full px-3 py-1.5 w-1/2">
                            <i className="fa-solid fa-magnifying-glass text-gray-500"></i>
                            <input
                                type="text"
                                value={search}
                                onChange={(e) => {
                                    setSearch(e.target.value);
                                }}
                                placeholder="Search messages..."
                                className="ml-2 bg-transparent w-full outline-none text-gray-700 placeholder-gray-400 text-sm"
                            />
                        </div>
                    </div>
                    <div className="p-2 w-full space-y-2">
                        {danhSachKhoiTao
                            ?.filter((d) => d?.tenKhach?.toLowerCase().includes(search?.toLowerCase()))
                            .map((d) => (
                                <div
                                    id={d?.id}
                                    key={d?.id}
                                    className="chat-item flex items-start p-2 hover:bg-gray-100 cursor-pointer rounded w-full"
                                    onClick={() => {
                                        const container = document.getElementById("danhSachDangChat");
                                        container.innerHTML = "";

                                        sendMessage({ type: "MOUNTUSER", idUser: d?.id || 0, mount: true });
                                        setTyping(false);
                                        setDanhSachKhoiTao((prev) =>
                                            prev.map((item) =>
                                                item.id === d?.id ? { ...item, statusNhanVien: 2 } : item
                                            )
                                        );
                                        sendMessage({ type: "MOUNTUSER", idUser: customerPickRef?.current?.id || 0, mount: false });
                                        setCustomerPick(d);
                                    }}
                                >
                                    <div className="relative basis-[30%] md:basis-[20%] max-w-[30%] md:max-w-[20%] mr-3 shrink-0">
                                        <img
                                            src="https://cdn-icons-png.flaticon.com/512/149/149071.png"
                                            alt="Avatar"
                                            className="w-10 h-10 object-cover rounded-full"
                                        />
                                        {d?.dangHoatDong && (
                                            <span className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-white rounded-full" />
                                        )}
                                    </div>
                                    <div className="flex-1 overflow-hidden">
                                        <div className="flex justify-between items-center w-full gap-2">
                                            <p className="font-medium text-left truncate">{d?.tenKhach}</p>
                                            <p className="hidden sm:block text-sm text-gray-400 text-right whitespace-nowrap truncate">
                                                {d?.thoiHanNhan}
                                            </p>
                                        </div>
                                        {d?.noiDungTinNhan && (
                                            <p
                                                className={`text-sm truncate ${
                                                    d?.statusNhanVien != 2 && d?.nhanVien == 0
                                                        ? "font-bold text-black"
                                                        : "text-gray-500"
                                                }`}
                                            >
                                                {d?.khach ? "" : "Skinly: "}
                                                {d.noiDungTinNhan}
                                            </p>
                                        )}
                                    </div>
                                </div>
                            ))}
                    </div>
                </div>
                <div className="w-[70%] flex flex-col">
                    <div className="p-4 bg-white border-b border-gray-200 flex items-center">
                        <img
                            src="https://cdn-icons-png.flaticon.com/512/149/149071.png"
                            alt="Avatar"
                            className="w-10 mr-2 object-cover"
                        />
                        <div className="ml-2">
                            <h2 className="font-semibold">{customerPick?.tenKhach}</h2>
                            <p
                                className={`text-sm pl-1 w-fit pr-1 ${
                                    customerPick?.dangHoatDong ? "text-green-500 bg-green-100" : "text-orange-500 bg-orange-100"
                                }`}
                            >
                                {customerPick?.dangHoatDong ? "Online" : "Offline"}
                            </p>
                        </div>
                        <div className="ml-auto flex space-x-2" id="danhSachDangChat"></div>
                    </div>
                    <div
                        className="flex-1 p-4 overflow-y-auto bg-gray-50 relative max-h-[90%]"
                        ref={chatContainerRef}
                        id="khungchat"
                        style={{ display: "flex", flexDirection: "column" }}
                    >
                        {isLoading && <div className="loading">Đang tải tin nhắn...</div>}
                        {groupedMessages.map((item, index) =>
                            item.type === "date" ? (
                                <div
                                    key={`date-${index}`}
                                    className="text-center text-sm text-gray-500 my-2"
                                >
                                    {item.date}
                                </div>
                            ) : (
                                <div key={`msg-${item.data.idtinNhan}`}>
                                    <div
                                        id={`msg-${item.data.idtinNhan}`}
                                        className={`message-container flex ${
                                            item.data.nhanvien == 0 ? "" : "flex-row-reverse"
                                        } gap-2 mt-4 ${item.data.isNew ? "new-message" : ""}`}
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
                                            className={`flex flex-col max-w-[70%] ${
                                                item.data.nhanvien == 0 ? "items-start" : "items-end"
                                            }`}
                                        >
                                            {item.data?.listImage?.length > 0 && (
                                                <div className="shadow-md p-1">
                                                    <RenderImage images={item.data?.listImage}></RenderImage>
                                                </div>
                                            )}
                                            <div
                                                className="relative mt-3 group"
                                                style={{ display: "inline-block", paddingRight: "30px" }}
                                            >
                                                {item?.data?.re != 0 && (
                                                    <div
                                                        onClick={() => {
                                                            document
                                                                .getElementById("msg-" + item?.data?.re)
                                                                .scrollIntoView({ behavior: "smooth" });
                                                        }}
                                                        className="relative cursor-pointer pl-6 pt-2 pb-2 bg-gray-50 rounded-md shadow-sm"
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
                                                    className={`shadow-sm p-3 rounded-xl pr-6 ${
                                                        item.data.nhanvien == 0
                                                            ? "bg-white text-black"
                                                            : "bg-green-900 text-white"
                                                    }`}
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
                                                    className={`absolute -bottom-2 p-1 right-1 bg-white rounded-full shadow-md cursor-pointer ${
                                                        item.data.nhanvien != 0 ? "pointer-events-none" : ""
                                                    }`}
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
                                            {item?.data?.nhanvien != 0 && (
                                                <div className="mt-1 text-xs text-white bg-gray-400 px-3 py-1 rounded-full">
                                                    <span id={"status-" + item.data.idtinNhan} className="text-sm">
                                                        {item?.data?.statusKhachHang == 0 && "Đã gửi"}
                                                        {item?.data?.statusKhachHang == 1 && "Đã nhận"}
                                                        {item?.data?.statusKhachHang == 2 && "Đã xem"}
                                                    </span>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            )
                        )}
                    </div>
                    {typing && <Typing ten={customerPickRef?.current?.tenKhach} />}
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
                                {isUploading ? (
                                    <i className="fa fa-spinner fa-spin w-8 h-8"></i>
                                ) : (
                                    <img
                                        className="w-8"
                                        src="https://cdn-icons-png.flaticon.com/128/8138/8138518.png"
                                    />
                                )}
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
                                className="flex-1 p-2 rounded-lg focus:outline-none"
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
                                        khachHang: customerPick?.id,
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
                <audio id="notification-sound" src={notificationSound} preload="auto"></audio>
            </div>
        </div>
    );
}

export { Chat };
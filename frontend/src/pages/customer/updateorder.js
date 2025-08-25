import { formatToVND } from "../../utils/Format";
import BackGroundTopLeft from "../../assets/background.png";
import BackGroundBotRight from "../../assets/backgoundleft.png";
import { useContext, useEffect, useState } from "react";
import { createOrder, getOrderForUpdate, getviewOrder, getviewOrderAlready, remove, update } from "../../services/OrderService";
import { toast } from "react-toastify";
import { useNavigate, useSearchParams } from "react-router-dom";
import Modal from "../../components/commons/modal";
import { Context } from "./HomeCustommer";
import { ModalAddress } from "../../components/commons/ModalAddress";
import { getTinh, getHuyen, getXa } from "../../services/address";
import { getInfo } from "../../services/userService";
import { tinhPhiGiaoHang } from "../../services/GHNService";

function UpdateOrder() {
    const [load, setLoad] = useState(false);
    const [data, setData] = useState({});
    const { cartUpdate, setCartUpdate, change, setChange } = useContext(Context);
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState("");
    const [open, setOpen] = useState(false);
    const [header, setHeader] = useState("");
    const [status, setStatus] = useState(0);
    const [reason, setReason] = useState("");
    const [thongTinNguoiDung, setThongTinNguoiDung] = useState({});
    const [chuoi, setChuoi] = useState("");
    const [code, setCode] = useState([]);
    const [openAddressModal, setOpenAddressModal] = useState(false);
    const navigate = useNavigate();
    const id = searchParams.get("id");
    const [loi, setLoi] = useState(true);
    const [tongTien, setTongTien] = useState(0);

    // Fetch user info and initialize address
    useEffect(() => {
        getInfo()
            .then((d) => {
                setThongTinNguoiDung(d);
            })
            .catch((err) => {
                toast.error("Không thể tải thông tin người dùng: " + err.message);
            });
    }, [data.diaChi, data.code]);

    // Fetch order data
    useEffect(() => {
        getviewOrderAlready(id)
            .then((data) => {
                setChuoi(data.diaChi || data.diaChi || "");
                setCode(data.code || data.code || []);
                setData(data);
                let bienThes = data.data.flatMap((sanPham) =>
                    Array.isArray(sanPham.danhSachCon)
                        ? sanPham.danhSachCon.map((bienThe) => ({
                            idBienThe: bienThe.idBienThe,
                            soLuong: bienThe.tongSoLuong,
                        }))
                        : []
                );
                if (cartUpdate?.data?.length === 0 && change === false) {
                    setCartUpdate({
                        id: id,
                        data: bienThes,
                    });
                }
            })
            .catch((e) => {
                console.error("Error fetching data:", e);
                navigate(-1);
            });
    }, []);

    // Update order data when cartUpdate changes
    useEffect(() => {
        if (cartUpdate?.data?.length !== 0 && change === true) {
            getOrderForUpdate(cartUpdate?.data, cartUpdate?.id)
                .then((data) => {
                    console.log("Data from getOrderForUpdate:", data);
                    setData(data);
                })
                .catch((e) => {
                    console.error("Error in getOrderForUpdate:", e);
                    toast.error(e?.response?.data?.message || "Tính toán cập nhật thất bại");
                });
        }
    }, [cartUpdate]);

    // Format address with prefixes and codes
    const formatAddressForOrder = async (address, codes) => {
        if (!address || !codes || codes.length !== 3) {
            return address;
        }

        try {
            const [provinceCode, districtCode, wardCode] = codes;
            const provinces = await getTinh();
            const province = provinces.find((p) => p.code === provinceCode);
            const provinceName = province ? `Tỉnh ${province.name}` : "";
            const districts = await getHuyen(provinceCode);
            const district = districts.find((d) => d.code === districtCode);
            const districtName = district ? `Huyện ${district.name}` : "";
            const wards = await getXa(districtCode);
            const ward = wards.find((w) => w.code === wardCode);
            const wardName = ward ? (ward.name.startsWith("Phường") ? ward.name : `Xã ${ward.name}`) : "";
            const detailedAddress = address.split(", ").slice(-1)[0] || "";
            return `${provinceName}, ${districtName}, ${wardName}, ${detailedAddress}.${provinceCode} ${districtCode} ${wardCode}`;
        } catch (err) {
            toast.error("Không thể định dạng địa chỉ: " + err.message);
            return address;
        }
    };

    // Handle order update
    const handleUpdateOrder = async () => {
        let cart = cartUpdate?.data || [];
        if (cart.length === 0) {
            toast.error("Giỏ hàng trống!");
            return;
        }

        if (!chuoi || code.length !== 3) {
            toast.error("Vui lòng cung cấp đầy đủ thông tin địa chỉ!");
            return;
        }

        try {
            const formattedAddress = await formatAddressForOrder(chuoi, code);
            cart = cart.map(item => ({
                ...item,
                diaChi: formattedAddress
            }));
            await update(cart, id);
            toast.success("Cập nhật đơn hàng thành công");
            setChange(false);
        } catch (e) {
            toast.error(e?.response?.data?.message || "Cập nhật đơn hàng thất bại");
        }
    };

    // Handle confirm cancellation or return
    const handleConfirm = () => {
        if (!reason.trim()) {
            toast.error(`Vui lòng nhập lý do ${status === 4 ? "hủy" : "hoàn"} đơn hàng`);
            return;
        }
        remove(id, status, reason)
            .then(() => {
                setOpen(false);
                navigate(-1);
            })
            .catch((e) => {
                setOpen(false);
                toast.error(e?.response?.data?.message || "Cập nhật thất bại");
            });
    };

    // Handle delete item
    const handleDeleteItem = (idBienThe) => {
        const totalItems = cartUpdate.data.length;
        if (totalItems === 1) {
            toast.error("Quý khách có thể hủy đơn hàng nếu không còn nhu cầu");
            return;
        }
        setCartUpdate((prev) => ({
            ...prev,
            data: prev.data.filter((item) => item.idBienThe !== idBienThe),
        }));
        setChange(true);
    };

    function taoDiaChi(chuoi, code) {
        const codeStr = code.join(" ");
        return `${chuoi}.${codeStr}`;
    }

    function chuyenDoiDanhSach(data) {
        return data.flatMap(sp =>
            sp.danhSachCon.map(con => ({
                idBienThe: con.idBienThe,
                soLuong: con.tongSoLuong
            }))
        );
    }

    useEffect(() => {
        if (chuoi?.length > 10) {
            tinhPhiGiaoHang(taoDiaChi(chuoi, code), chuyenDoiDanhSach(data?.data)).then((d) => {
                setLoi(false);
                setTongTien(d?.data?.total);
            }).catch((e) => {
                setLoi(true);
                if (loi && data?.data?.length > 0 && code?.length > 0) {
                    if (e?.response?.data?.message) {
                        toast.error(e?.response?.data?.message);
                    }
                }
            });
        }
    }, [code, data?.data]);

    return (
        <div className="w-full relative z-0">
            <link
                rel="stylesheet"
                href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"
            />
            <img src={BackGroundTopLeft} alt="bg" className="absolute top-0 right-0 w-48 z-30" />
            <img src={BackGroundBotRight} alt="bg" className="absolute bottom-0 w-48 z-30" />
            <div className="container mx-auto max-w-7xl relative bg-white p-4 rounded">
                <h2 className="text-2xl font-bold mb-4 text-gray-800">Cập nhật đơn hàng</h2>
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="flex-1 z-40">
                        <div className="max-h-[500px] overflow-y-auto scrollbar-thin scrollbar-thumb-green-900 scrollbar-track-gray-100">
                            <table className="w-full text-sm text-center text-gray-600 border">
                                <thead className="bg-green-900 text-green-100 text-xs uppercase sticky top-0 z-50">
                                    <tr>
                                        <th className="px-4 py-2 w-1/4">Sản phẩm</th>
                                        <th className="px-4 py-2">Phân loại</th>
                                        <th className="px-4 py-2">Giá tiền</th>
                                        <th className="px-4 py-2">Số lượng</th>
                                        <th className="px-4 py-2">Thành tiền</th>
                                        {data?.canUpdateCustomer && (
                                            <th className="px-4 py-2">Thao tác</th>
                                        )}
                                    </tr>
                                </thead>
                                <tbody>
                                    {data?.data?.map((product, pIdx) => {
                                        const productRowSpan = product.danhSachCon.reduce(
                                            (acc, variant) => acc + variant.cartItemLasts.length,
                                            0
                                        );

                                        return product.danhSachCon.flatMap((variant, vIdx) => {
                                            const variantRowSpan = variant.cartItemLasts.length;
                                            const totalQuantity = variant.cartItemLasts.reduce(
                                                (sum, ct) => sum + ct.soLuong,
                                                0
                                            );

                                            return variant.cartItemLasts.map((item, iIdx) => {
                                                const isFirstProductRow = vIdx === 0 && iIdx === 0;
                                                const isFirstVariantRow = iIdx === 0;
                                                const thanhTien = item.soLuong * item.giaGiam;

                                                return (
                                                    <tr
                                                        key={`${pIdx}-${vIdx}-${iIdx}`}
                                                        className="border-b bg-white hover:bg-gray-50"
                                                    >
                                                        {isFirstProductRow && (
                                                            <td className="px-4 py-2" rowSpan={productRowSpan}>
                                                                <div className="flex items-center justify-center gap-3">
                                                                    <img
                                                                        src={variant.anhGioiThieu || product.anhBia}
                                                                        alt={product.tenSanPham}
                                                                        className="w-16 h-16 object-cover rounded"
                                                                    />
                                                                    <p className="font-medium">{product.tenSanPham}</p>
                                                                </div>
                                                            </td>
                                                        )}

                                                        {isFirstVariantRow && (
                                                            <td
                                                                className="px-4 py-2 font-semibold"
                                                                rowSpan={variantRowSpan}
                                                            >
                                                                {variant.tenBienThe}
                                                            </td>
                                                        )}

                                                        <td className="px-4 py-2">
                                                            {item.giaGiam < variant.giaGoc ? (
                                                                <div>
                                                                    <div className="font-bold text-black">
                                                                        {item.giaGiam.toLocaleString("vi-VN")} đ
                                                                    </div>
                                                                    <div className="line-through text-gray-400">
                                                                        {variant.giaGoc.toLocaleString("vi-VN")} đ
                                                                    </div>
                                                                    <div>({item.soLuong})</div>
                                                                </div>
                                                            ) : (
                                                                <div className="font-bold text-black">
                                                                    {variant.giaGoc.toLocaleString("vi-VN")} đ
                                                                </div>
                                                            )}
                                                        </td>

                                                        {isFirstVariantRow && (
                                                            <td className="px-4 py-2" rowSpan={variantRowSpan}>
                                                                {data?.canUpdateCustomer ? (
                                                                    <div className="flex items-center gap-2 justify-center">
                                                                        <button
                                                                            onClick={() => {
                                                                                setCartUpdate((prev) => {
                                                                                    const foundItem = prev.data.find(
                                                                                        (item) => item.idBienThe === variant.idBienThe
                                                                                    );
                                                                                    const updatedData = prev.data.map((item) =>
                                                                                        item.idBienThe === variant.idBienThe
                                                                                            ? { ...item, soLuong: Math.max(1, item.soLuong - 1) }
                                                                                            : item
                                                                                    );
                                                                                    return {
                                                                                        ...prev,
                                                                                        data: updatedData,
                                                                                    };
                                                                                });
                                                                                setChange(true);
                                                                            }}
                                                                            className="w-8 h-8 bg-gray-200 rounded flex items-center justify-center"
                                                                            disabled={totalQuantity <= 1}
                                                                        >
                                                                            <i className="fas fa-minus"></i>
                                                                        </button>
                                                                        <input
                                                                            onChange={(e) => {
                                                                                const newQuantity = parseInt(e.target.value) || 1;
                                                                                setCartUpdate((prev) => {
                                                                                    const foundItem = prev.data.find(
                                                                                        (item) => item.idBienThe === variant.idBienThe
                                                                                    );
                                                                                    const updatedData = prev.data
                                                                                        .map((item) =>
                                                                                            item.idBienThe === variant.idBienThe
                                                                                                ? { ...item, soLuong: Math.max(1, newQuantity) }
                                                                                                : item
                                                                                        )
                                                                                        .filter((item) => item.soLuong > 0);
                                                                                    return {
                                                                                        ...prev,
                                                                                        data: updatedData,
                                                                                    };
                                                                                });
                                                                                setChange(true);
                                                                            }}
                                                                            type="number"
                                                                            value={totalQuantity}
                                                                            className="w-10 pl-2 outline-none border border-gray-400 text-center"
                                                                            min="1"
                                                                        />
                                                                        <button
                                                                            onClick={() => {
                                                                                setCartUpdate((prev) => {
                                                                                    const foundItem = prev.data.find(
                                                                                        (item) => item.idBienThe === variant.idBienThe
                                                                                    );
                                                                                    const updatedData = prev.data.map((item) =>
                                                                                        item.idBienThe === variant.idBienThe
                                                                                            ? { ...item, soLuong: item.soLuong + 1 }
                                                                                            : item
                                                                                    );
                                                                                    return {
                                                                                        ...prev,
                                                                                        data: updatedData,
                                                                                    };
                                                                                });
                                                                                setChange(true);
                                                                            }}
                                                                            className="w-8 h-8 bg-gray-200 rounded flex items-center justify-center"
                                                                        >
                                                                            <i className="fas fa-plus"></i>
                                                                        </button>
                                                                    </div>
                                                                ) : (
                                                                    <p className="font-medium">{totalQuantity}</p>
                                                                )}
                                                            </td>
                                                        )}

                                                        <td className="px-4 py-2 font-medium">
                                                            {thanhTien.toLocaleString("vi-VN")} đ
                                                        </td>

                                                        {data?.canUpdateCustomer && isFirstVariantRow && (
                                                            <td className="px-4 py-2" rowSpan={variantRowSpan}>
                                                                <button
                                                                    onClick={() => handleDeleteItem(variant.idBienThe)}
                                                                    className="text-red-500 hover:text-red-700"
                                                                >
                                                                    <i className="fas fa-trash"></i>
                                                                </button>
                                                            </td>
                                                        )}
                                                    </tr>
                                                );
                                            });
                                        });
                                    })}
                                </tbody>
                            </table>
                        </div>
                        <div className="z-10 bg-white mt-4 p-6 rounded-lg shadow-md border border-gray-200">
                            <h3 className="text-lg font-semibold mb-4 text-gray-800 flex items-center">
                                <i className="fas fa-user-circle mr-2 text-green-900"></i>
                                Thông tin nhận hàng
                            </h3>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div className="flex items-center">
                                    <i className="fas fa-user text-green-900 mr-3"></i>
                                    <div>
                                        <p className="text-sm text-gray-500">Tên khách hàng</p>
                                        <p className="font-medium text-gray-800">
                                            {thongTinNguoiDung.ten || "Chưa cung cấp"}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center">
                                    <i className="fas fa-phone text-green-900 mr-3"></i>
                                    <div>
                                        <p className="text-sm text-gray-500">Số điện thoại</p>
                                        <p className="font-medium text-gray-800">
                                            {thongTinNguoiDung.soDienThoai || "Chưa cung cấp"}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center">
                                    <i className="fas fa-map-marker-alt text-green-900 mr-3"></i>
                                    <div>
                                        <p className="text-sm text-gray-500">Địa chỉ</p>
                                        <p className="font-medium text-gray-800">{chuoi || "Chưa cung cấp"}</p>
                                    </div>
                                </div>
                                <div className="flex items-center">
                                    <i className="fas fa-envelope text-green-900 mr-3"></i>
                                    <div>
                                        <p className="text-sm text-gray-500">Email</p>
                                        <p className="font-medium text-gray-800">
                                            {thongTinNguoiDung.email || "Chưa cung cấp"}
                                        </p>
                                    </div>
                                </div>
                                {data?.canUpdateCustomer && (
                                    <div className="flex items-center">
                                        <button
                                            onClick={() => setOpenAddressModal(true)}
                                            className="font-bold cursor-pointer text-yellow-500 bg-yellow-100 px-3 py-1 rounded"
                                        >
                                            <i className="fa-solid fa-square-pen mr-2"></i>
                                            Chỉnh sửa địa chỉ
                                        </button>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="md:w-1/4 w-full p-4 relative border-t-4 border-b-2 border-green-900 shadow-inner rounded-md z-40">
                        <h3 className="text-lg font-semibold mb-2">Hóa đơn của bạn</h3>
                        <hr className="mb-2" />
                        <p className="mb-2">
                            Tạm tính: <span className="float-right">{formatToVND(data?.tongTien)}</span>
                        </p>
                        <p className="mb-2">
                            Tiết kiệm: <span className="float-right">{formatToVND(data?.giamGia)}</span>
                        </p>
                        <hr className="my-2" />
                        <p className="font-bold">
                            Tổng cộng: <span className="float-right">{formatToVND(data?.tongTien)}</span>
                        </p>
                        <p className="text-gray-500 text-sm mt-1">(Đã bao gồm VAT)</p>
                        <button
                            onClick={handleUpdateOrder}
                            className={`mt-4 w-full py-2 ${data?.canUpdateCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                        >
                            Cập nhật đơn hàng
                        </button>
                        {data?.huyDonHangCustomer ? (
                            <button
                                onClick={() => {
                                    setOpen(true);
                                    setHeader("Xác nhận hủy đơn hàng");
                                    setMessage("Bạn chắc chắn muốn hủy đơn hàng?");
                                    setStatus(4);
                                    setReason("");
                                }}
                                className={`mt-4 w-full py-2 ${data?.huyDonHangCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Hủy Đơn hàng
                            </button>
                        ) : null}
                        {data?.hoanHangCustomer ? (
                            <button
                                onClick={() => {
                                    setOpen(true);
                                    setMessage("Bạn chắc chắn muốn gửi yêu cầu hoàn đơn?");
                                    setHeader("Xác nhận gửi yêu cầu hoàn đơn");
                                    setStatus(6);
                                    setReason("");
                                }}
                                className={`mt-4 w-full py-2 ${data?.hoanHangCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Tạo yêu cầu hoàn đơn
                            </button>
                        ) : null}
                        <div className="mt-6 rounded-md">
                            {loi ? (
                                data?.data?.length > 0 && code?.length > 0 ? <p className="text-red-500 text-sm font-bold">Đơn vị vận chuyển không thể giao đến khu vực này</p> : null
                            ) : (
                                <div>
                                    <p className="text-gray-600 mb-2 font-bold">
                                        Phí vận chuyển dự kiến:
                                    </p>
                                    <div className="flex items-end gap-3 mb-3">
                                        <img
                                            src="data:image/png;base64,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"
                                            alt="Transport Icon"
                                            className=""
                                        />
                                        <p className="font-medium text-orange-600 font-bold">{formatToVND(tongTien)}</p>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
            {open ? (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-6">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">{header}</h2>
                        <p className="text-md text-gray-700 mb-3">{message}</p>
                        <div className="mb-4">
                            <p className="text-md text-gray-700 mb-3">
                                Lý do {status === 4 ? "hủy" : "hoàn"} đơn hàng:
                            </p>
                            <textarea
                                value={reason}
                                onChange={(e) => setReason(e.target.value)}
                                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-700"
                                rows="4"
                                placeholder="Vui lòng nhập lý do..."
                            />
                        </div>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                onClick={handleConfirm}
                            >
                                Đồng ý
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={() => setOpen(false)}
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </Modal>
            ) : null}
            {openAddressModal && (
                <Modal setOpen={setOpenAddressModal}>
                    <ModalAddress
                        address={chuoi}
                        setAddress={setChuoi}
                        addressIds={code}
                        setAddressIds={setCode}
                        isOpen={openAddressModal}
                        setOpen={setOpenAddressModal}
                        onClose={() => setOpenAddressModal(false)}
                    />
                </Modal>
            )}
        </div>
    );
}

export { UpdateOrder };
import { formatToVND } from "../../utils/Format";
import BackGroundTopLeft from "../../assets/background.png";
import BackGroundBotRight from "../../assets/backgoundleft.png";
import { useEffect, useState } from "react";
import { getviewOrderAlreadyE, getOrderForUpdateEmployee, update, removeEmployee, hoanDonOnline } from "../../services/OrderService";
import { toast } from "react-toastify";
import { useNavigate, useSearchParams } from "react-router-dom";
import Modal from "../../components/commons/modal";
import { getInfo } from "../../services/userService";

function ViewChiTietOnline() {
    const [load, setLoad] = useState(false);
    const [data, setData] = useState({});
    const [cartUpdate, setCartUpdate] = useState({
        id: 0,
        data: []
    });
    const [change, setChange] = useState(false);
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState("");
    const [open, setOpen] = useState(false);
    const [header, setHeader] = useState("");
    const [status, setStatus] = useState(0);
    const [thongTinNguoiDung, setThongTinNguoiDung] = useState({});
    const [openSuccessModal, setOpenSuccessModal] = useState(false);
    const [openFailureModal, setOpenFailureModal] = useState(false);
    const [failureReason, setFailureReason] = useState("");
    const navigate = useNavigate();
    const id = searchParams.get("id");

    const refreshOrderData = () => {
        getviewOrderAlreadyE(id)
            .then((newData) => {
                setData(newData);
                let bienThes = newData.data.flatMap((sanPham) =>
                    Array.isArray(sanPham.danhSachCon)
                        ? sanPham.danhSachCon.map((bienThe) => ({
                              idBienThe: bienThe.idBienThe,
                              soLuong: bienThe.tongSoLuong,
                          }))
                        : []
                );
                setCartUpdate({
                    id: id,
                    data: bienThes,
                });
                setChange(false);
            })
            .catch((e) => {
                console.error("Error refreshing data:", e);
                toast.error(e?.response?.data?.message || "Tải dữ liệu thất bại");
            });
    };

    useEffect(() => {
        getviewOrderAlreadyE(id)
            .then((data) => {
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
    }, [id, cartUpdate?.data?.length, change, navigate]);

    useEffect(() => {
        if (cartUpdate?.data?.length !== 0 && change === true) {
            getOrderForUpdateEmployee(cartUpdate?.data, cartUpdate?.id)
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

    useEffect(() => {
        getInfo()
            .then((d) => {
                setThongTinNguoiDung(d);
            })
            .catch((err) => {
                toast.error("Không thể tải thông tin người dùng: " + err.message);
            });
    }, []);

    const handleConfirmSuccess = (payNow) => {
        hoanDonOnline(id, payNow, "", true)
            .then(() => {
                refreshOrderData();
                setOpenSuccessModal(false);
                toast.success("Xác nhận hoàn thành công");
            })
            .catch((e) => {
                setOpenSuccessModal(false);
                toast.error(e?.response?.data?.message || "Xác nhận thất bại");
            });
    };

    const handleConfirmFailure = () => {
        if (!failureReason.trim()) {
            toast.error("Vui lòng nhập lý do thất bại");
            return;
        }
        hoanDonOnline(id, false, failureReason, false)
            .then(() => {
                refreshOrderData();
                setOpenFailureModal(false);
                setFailureReason("");
                toast.success("Xác nhận hoàn thất bại thành công");
            })
            .catch((e) => {
                setOpenFailureModal(false);
                toast.error(e?.response?.data?.message || "Xác nhận thất bại");
            });
    };

    console.log(cartUpdate.data);

    return (
        <div className="w-full relative z-0">
            <img
                src={BackGroundTopLeft}
                alt="bg"
                className="absolute top-0 right-0 w-48 z-30"
            />
            <img
                src={BackGroundBotRight}
                alt="bg"
                className="absolute bottom-0 w-48 z-30"
            />
            <div className="container mx-auto max-w-7xl relative bg-white p-4 rounded">
                <h2 className="text-2xl font-bold mb-4 text-gray-800">Giỏ hàng</h2>
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
                                            const totalQuantity = variant.cartItemLasts.reduce((sum, ct) => sum + ct.soLuong, 0);

                                            return variant.cartItemLasts.map((item, iIdx) => {
                                                const isFirstProductRow = vIdx === 0 && iIdx === 0;
                                                const isFirstVariantRow = iIdx === 0;
                                                const thanhTien = item.soLuong * item.giaGiam;

                                                return (
                                                    <tr key={`${pIdx}-${vIdx}-${iIdx}`} className="border-b bg-white hover:bg-gray-50">
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
                                                            <td className="px-4 py-2 font-semibold" rowSpan={variantRowSpan}>
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
                                                            <td className="px-4 py-2 justify-center" rowSpan={variantRowSpan}>
                                                                <div className="items-center  gap-2">
                                                                    {/* <input
                                                                        onChange={(e) => {
                                                                            const newQuantity = parseInt(e.target.value) || 0;
                                                                            setCartUpdate((prev) => {
                                                                                const updatedData = prev.data
                                                                                    .map((item) =>
                                                                                        item.idBienThe === variant.idBienThe
                                                                                            ? { ...item, soLuong: newQuantity }
                                                                                            : item
                                                                                    )
                                                                                    .filter((item) => item.soLuong > 0);

                                                                                return {
                                                                                    ...prev,
                                                                                    data: updatedData
                                                                                };
                                                                            });
                                                                            setChange(true);
                                                                        }}
                                                                        type="number"
                                                                        value={totalQuantity}
                                                                        className="w-10 pl-2 outline-none border border-gray-400"
                                                                    /> */}
                                                                    {totalQuantity}
                                                                </div>
                                                            </td>
                                                        )}

                                                        <td className="px-4 py-2 font-medium">
                                                            {thanhTien.toLocaleString("vi-VN")} đ
                                                        </td>
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
                                        <p className="font-medium text-gray-800">
                                            {data?.diaChi || "Chưa cung cấp"}
                                        </p>
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
                            </div>
                        </div>
                    </div>

                    <div className="md:w-1/4 w-full p-4 relative border-t-4 border-green-900 bg-gray-50 shadow-inner rounded-md z-40">
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
                        {/* <button
                            onClick={() => {
                                update(cartUpdate.data, id)
                                    .then(() => {
                                        refreshOrderData();
                                        toast.success("Cập nhật đơn hàng thành công");
                                    })
                                    .catch((e) => {
                                        toast.error(e?.response?.data?.message || "Cập nhật đơn hàng thất bại");
                                    });
                            }}
                            className={`mt-4 w-full py-2 ${data?.canUpdateCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                        >
                            Cập nhật đơn hàng
                        </button> */}
                        {data?.huyDonHangCustomer ? (
                            <button
                                onClick={() => {
                                    setOpen(true);
                                    setHeader("Xác nhận hủy đơn hàng");
                                    setMessage(
                                        data?.thanhToan
                                            ? "Bạn có muốn hoàn tiền cùng lúc không?"
                                            : "Bạn chắc chắn muốn hủy đơn hàng?"
                                    );
                                    setStatus(4);
                                }}
                                className={`mt-4 w-full py-2 ${data?.huyDonHangCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Hủy Đơn hàng
                            </button>
                        ) : null}
                        {data?.coTheXacNhanHoanHang ? (
                            <button
                                onClick={() => {
                                    setOpenSuccessModal(true);
                                }}
                                className={`mt-4 w-full py-2 ${data?.coTheXacNhanHoanHang ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Xác nhận hoàn thành công
                            </button>
                        ) : null}
                        {data?.coTheXacNhanHoanHang ? (
                            <button
                                onClick={() => {
                                    setOpenFailureModal(true);
                                }}
                                className={`mt-4 w-full py-2 ${data?.coTheXacNhanHoanHang ? "bg-orange-500 hover:bg-orange-600" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Xác nhận hoàn thất bại
                            </button>
                        ) : null}
                    </div>
                </div>
            </div>
            {open ? (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-6">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">
                            {header}
                        </h2>
                        <p className="text-md text-gray-700 mb-5">
                            {message}
                        </p>
                        <div className="flex justify-end gap-3">
                            {data?.thanhToan ? (
                                <>
                                    <button
                                        className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                        onClick={() => {
                                            removeEmployee(id, status, true)
                                                .then(() => {
                                                    refreshOrderData();
                                                    setOpen(false);
                                                    toast.success("Hủy đơn hàng thành công");
                                                })
                                                .catch((e) => {
                                                    setOpen(false);
                                                    toast.error(e?.response?.data?.message || "Cập nhật thất bại");
                                                });
                                        }}
                                    >
                                        Đồng ý hoàn tiền
                                    </button>
                                    <button
                                        className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                        onClick={() => {
                                            removeEmployee(id, status, false)
                                                .then(() => {
                                                    refreshOrderData();
                                                    setOpen(false);
                                                    toast.success("Hủy đơn hàng thành công");
                                                })
                                                .catch((e) => {
                                                    setOpen(false);
                                                    toast.error(e?.response?.data?.message || "Cập nhật thất bại");
                                                });
                                        }}
                                    >
                                        Không hoàn tiền
                                    </button>
                                </>
                            ) : (
                                <>
                                    <button
                                        className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                        onClick={() => {
                                            removeEmployee(id, status, false)
                                                .then(() => {
                                                    refreshOrderData();
                                                    setOpen(false);
                                                    toast.success("Hủy đơn hàng thành công");
                                                })
                                                .catch((e) => {
                                                    setOpen(false);
                                                    toast.error(e?.response?.data?.message || "Cập nhật thất bại");
                                                });
                                        }}
                                    >
                                        Đồng ý
                                    </button>
                                    <button
                                        className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                        onClick={() => setOpen(false)}
                                    >
                                        Đóng
                                    </button>
                                </>
                            )}
                        </div>
                    </div>
                </Modal>
            ) : null}
            {openSuccessModal ? (
                <Modal setOpen={setOpenSuccessModal}>
                    <div className="z-50 w-96 bg-white rounded-xl p-6">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">
                            Xác nhận hoàn thành công
                        </h2>
                        <p className="text-md text-gray-700 mb-5">
                            Bạn có muốn chi trả ngay bây giờ không?
                        </p>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                onClick={() => handleConfirmSuccess(true)}
                            >
                                Chi trả ngay
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={() => handleConfirmSuccess(false)}
                            >
                                Không chi trả
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={() => setOpenSuccessModal(false)}
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </Modal>
            ) : null}
            {openFailureModal ? (
                <Modal setOpen={setOpenFailureModal}>
                    <div className="z-50 w-96 bg-white rounded-xl p-6">
                        <h2 className="text-lg font-semibold text-orange-500 mb-2">
                            Xác nhận hoàn thất bại
                        </h2>
                        <p className="text-md text-gray-700 mb-2">
                            Vui lòng nhập lý do thất bại:
                        </p>
                        <textarea
                            value={failureReason}
                            onChange={(e) => setFailureReason(e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded-md mb-4"
                            placeholder="Nhập lý do..."
                            rows="4"
                        />
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition"
                                onClick={handleConfirmFailure}
                            >
                                Xác nhận
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={() => {
                                    setOpenFailureModal(false);
                                    setFailureReason("");
                                }}
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </Modal>
            ) : null}
        </div>
    );
}

export { ViewChiTietOnline };
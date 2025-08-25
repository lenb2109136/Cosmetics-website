import { formatToVND } from "../../utils/Format";
import BackGroundTopLeft from "../../assets/background.png";
import BackGroundBotRight from "../../assets/backgoundleft.png";
import { useContext, useEffect, useState } from "react";
import { createOrder, getOrderForUpdate, getviewOrder, getviewOrderAlready, getviewOrderAlreadyTaiQuay, remove, update } from "../../services/OrderService";
import { toast } from "react-toastify";
import { useNavigate, useSearchParams } from "react-router-dom";
import Modal from "../../components/commons/modal";
import { PrevPayment } from "./PrePayment";
import { Refund } from "./RefundPayment";

// New Payment Confirmation Modal Component
const PaymentConfirmationModal = ({ isOpen, setIsOpen, onConfirm, header, message }) => {
    if (!isOpen) return null;

    return (
        <Modal setOpen={setIsOpen}>
            <div className="z-50 w-96 bg-white rounded-xl p-6">
                <h2 className="text-lg font-semibold text-green-700 mb-2">{header}</h2>
                <p className="text-md text-gray-700 mb-5">{message}</p>
                <div className="flex justify-end gap-3">
                    <button
                        className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                        onClick={onConfirm}
                    >
                        Đồng ý
                    </button>
                    <button
                        className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                        onClick={() => setIsOpen(false)}
                    >
                        Đóng
                    </button>
                </div>
            </div>
        </Modal>
    );
};

function ViewChiTietTaiQuay() {
    
    const [load, setLoad] = useState(false);
    const [data, setData] = useState({});
    const [searchParams] = useSearchParams();
    const [message, setMessage] = useState("");
    const [open, setOpen] = useState(false); // For existing modal (cancel/return)
    const [paymentModalOpen, setPaymentModalOpen] = useState(false); // For payment confirmation modal
    const [prevPaymentOpen, setPrevPaymentOpen] = useState(false); // For PrevPayment modal
    const [header, setHeader] = useState("");
    const [status, setStatus] = useState(0);
    const [cartUpdate, setCartUpdate] = useState({ id: null, data: [] });
    const [change, setChange] = useState(false);
    const navigate = useNavigate();
    const id = searchParams.get("id");
    const [openPayment22, setOpenPayMen22] = useState(false);

    useEffect(() => {
        getviewOrderAlreadyTaiQuay(id)
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
    }, [id]);

    const refreshOrderData = () => {
        getviewOrderAlreadyTaiQuay(id)
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
                className="fixed bottom-0 w-48 z-30"
            />
            <div className="container mx-auto max-w-7xl relative bg-white p-4 rounded">
                <h2 className="text-2xl font-bold mb-4 text-gray-800">Giỏ hàng</h2>
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="flex-1 z-40">
                        <div className="max-h-[500px] overflow-y-auto scrollbar-thin scrollbar-thumb-green-900 scrollbar-track-gray-100 ">
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
                                                            <td className="px-4 py-2" rowSpan={variantRowSpan}>
                                                                <div className="flex items-center justify-center gap-2">
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
                        {data?.canUpdateCustomer ? (
                            <button
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
                                className={`mt-4 w-full py-2 ${data?.canUpdateCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200"} text-white rounded`}
                            >
                                Cập nhật đơn hàng
                            </button>
                        ) : null}

                        {data?.huyDonHangCustomer ? (
                            <button
                                onClick={() => {
                                    setOpen(true);
                                    setHeader("Xác nhận hủy đơn hàng");
                                    setMessage("Bạn chắc chắn muốn hủy đơn hàng?");
                                    setStatus(4);
                                }}
                                className={`mt-4 w-full py-2 ${data?.huyDonHangCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Hủy Đơn hàng
                            </button>
                        ) : null}

                        {data?.hoanHangCustomer ? (
                            <button
                                onClick={() => {
                                    setOpenPayMen22(true)
                                }}
                                className={`mt-4 w-full py-2 ${data?.hoanHangCustomer ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Hoàn đơn
                            </button>
                        ) : null}

                        {data?.canThanhToan ? (
                            <button
                                onClick={() => {
                                    setPaymentModalOpen(true); // Open payment confirmation modal
                                }}
                                className={`mt-4 w-full py-2 ${data?.canThanhToan ? "bg-green-900 hover:bg-green-800" : "bg-gray-200 pointer-events-none"} text-white rounded`}
                            >
                                Thanh toán đơn hàng
                            </button>
                        ) : null}
                    </div>
                </div>
            </div>

            {/* Existing Modal for Cancel/Return */}
            {open ? (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-6">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">
                            {header}
                        </h2>
                        <p className="text-md text-gray-700 mb-5">{message}</p>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                onClick={() => {
                                    remove(id, status)
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
                        </div>
                    </div>
                </Modal>
            ) : null}

            {/* Payment Confirmation Modal */}
            <PaymentConfirmationModal
                isOpen={paymentModalOpen}
                setIsOpen={setPaymentModalOpen}
                onConfirm={() => {
                    setPaymentModalOpen(false); // Close confirmation modal
                    setPrevPaymentOpen(true); // Open PrevPayment modal
                }}
                header="Xác nhận thanh toán"
                message="Bạn có chắc muốn thực hiện thanh toán lại không?"
            />

            {/* PrevPayment Modal */}
            {prevPaymentOpen ? (
                <PrevPayment id={id} setOpenp={setPrevPaymentOpen}/>
            ) : null}
            {openPayment22 ? (
                <Refund id={id} setOpen={(value) => {
                    setOpenPayMen22(value);
                    if (!value) refreshOrderData(); // Refresh data after refund modal closes
                }}/>
            ) : null}
        </div>
    );
}

export { ViewChiTietTaiQuay };
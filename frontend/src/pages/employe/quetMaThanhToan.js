import { useState, useEffect } from "react";
import Modal from "../../components/commons/modal";
import { QRCodeCanvas as QRCode } from "qrcode.react";
import { xacNhanThanhToan, getTrangThai, chuyenDoiHinhThuc, huyDonTaiQuay } from "../../services/OrderService";
import { toast } from "react-toastify";

function QuetMaThanhToan({ da, setOpen }) {
    const [openThanhCong, setOpenThanhCong] = useState(false);
    const [openThatBai, setOpenThatBai] = useState(false);
    const [openHuyThanhCong, setOpenHuyThanhCong] = useState(false); 
    const [shouldClose, setShouldClose] = useState(false);
    const [orderData, setOrderData] = useState(da);
    const [retryCount, setRetryCount] = useState(0); // Thêm state để đếm số lần gọi

    const handleChuyenDoiHinhThuc = async () => {
        try {
            const response = await chuyenDoiHinhThuc(orderData?.idCall);
            setOrderData(response); 
            toast.success("Chuyển đổi hình thức thanh toán thành công");
        } catch (e) {
            toast.error(e?.response?.data?.message || "Chuyển đổi hình thức thất bại");
        }
    };

    // Xử lý hủy đơn
    const handleHuyDon = async () => {
        try {
            await huyDonTaiQuay(orderData?.idCall); // Gọi API hủy đơn
            setOpenHuyThanhCong(true); // Hiển thị modal hủy thành công
        } catch (e) {
            toast.error(e?.response?.data?.message || "Hủy đơn thất bại"); // Chỉ hiển thị toast lỗi
        }
    };

    // Kiểm tra trạng thái thanh toán khi ở chế độ online
    useEffect(() => {
        let timer;
        if (!orderData?.needCheck && !openThanhCong && !openThatBai && !openHuyThanhCong) {
            timer = setInterval(() => {
                setRetryCount(prev => prev + 1); // Tăng số lần gọi
                getTrangThai(orderData?.idCall, retryCount + 1).then((response) => {
                    if (response === 1) {
                        setOpenThanhCong(true);
                    } else if (response === 11) {
                        setOpenThatBai(true);
                    }
                });
            }, 1500);
        }

        return () => {
            if (timer) {
                clearInterval(timer); // Hủy interval khi component unmount hoặc khi chuyển sang thanh toán thường
            }
        };
    }, [orderData?.idCall, orderData?.needCheck, openThanhCong, openThatBai, openHuyThanhCong, retryCount]);

    useEffect(() => {
        if (shouldClose) {
            setOpen(false);
        }
    }, [shouldClose, setOpen]);

    // Cập nhật orderData khi prop da thay đổi
    useEffect(() => {
        setOrderData(da);
    }, [da]);

    return (
        <>
            {openThanhCong ? (
                <Modal b={false}>
                    <div className="bg-green-900 text-green-200 p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
                        <h2 className="text-xl font-semibold">Giao dịch hoàn tất</h2>
                        <p className="text-sm">Đơn đã thành công</p>
                        <button
                            onClick={() => {
                                setOpenThanhCong(false);
                                setShouldClose(true);
                            }}
                            className="w-full bg-green-700 hover:bg-green-600 text-green-100 font-medium py-2 rounded-md transition-colors duration-200"
                        >
                            OK
                        </button>
                    </div>
                </Modal>
            ) : openHuyThanhCong ? (
                <Modal b={false}>
                    <div className="bg-green-900 text-green-200 p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
                        <h2 className="text-xl font-semibold">Hủy đơn thành công</h2>
                        <p className="text-sm">Đã hủy đơn thành công</p>
                        <button
                            onClick={() => {
                                setOpenHuyThanhCong(false);
                                setShouldClose(true);
                            }}
                            className="w-full bg-green-700 hover:bg-green-600 text-green-100 font-medium py-2 rounded-md transition-colors duration-200"
                        >
                            OK
                        </button>
                    </div>
                </Modal>
            ) : openThatBai ? (
                <Modal b={false}>
                    <div className="bg-red-900 text-red-200 p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
                        <h2 className="text-xl font-semibold">Giao dịch thất bại</h2>
                        <p className="text-sm">Đơn hàng đã bị hủy, vui lòng tạo lại đơn</p>
                        <button
                            onClick={() => {
                                setOpenThatBai(false);
                                setShouldClose(true);
                            }}
                            className="w-full bg-red-700 hover:bg-red-600 text-red-100 font-medium py-2 rounded-md transition-colors duration-200"
                        >
                            OK
                        </button>
                    </div>
                </Modal>
            ) : orderData?.needCheck ? (
                <Modal b={false}>
                    <div className="bg-green-900 text-green-200 p-6 rounded-lg shadow-lg max-w-sm w-full">
                        <h2 className="text-xl font-semibold mb-2 text-center">Xác nhận thanh toán</h2>
                        <p className="text-sm mb-4 text-center">
                            Bạn phải thực hiện xác nhận thanh toán trước khi khách hàng rời đi
                        </p>
                        <div className="flex">
                            <button
                                onClick={() => {
                                    xacNhanThanhToan(orderData?.idCall).then(() => {
                                        setOpenThanhCong(true);
                                    });
                                }}
                                className="w-full mr-2 bg-green-700 hover:bg-green-600 text-green-100 font-medium py-2 rounded-md transition-colors duration-200"
                            >
                                Đã thu tiền
                            </button>
                            <button
                                onClick={handleChuyenDoiHinhThuc}
                                className="w-full mr-2 bg-green-700 hover:bg-green-600 text-green-100 font-medium py-2 rounded-md transition-colors duration-200"
                            >
                                Chuyển đổi hình thức
                            </button>
                            <button
                                onClick={handleHuyDon}
                                className="w-full bg-green-700 hover:bg-green-600 text-green-100 font-medium py-2 rounded-md transition-colors duration-200"
                            >
                                Hủy đơn
                            </button>
                        </div>
                    </div>
                </Modal>
            ) : (
                <Modal b={false}>
                    <div className="bg-green-900 text-green-200 p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
                        <h2 className="text-xl font-semibold">Chờ khách hàng thanh toán</h2>
                        <p className="text-sm">Mã thanh toán</p>
                        <div className="bg-white p-2 rounded-md">
                            <QRCode value={orderData?.qrCodeUrl} size={200} />
                        </div>
                        <div className="flex">
                            <button
                                onClick={handleChuyenDoiHinhThuc}
                                className="w-full mr-2 bg-green-700 hover:bg-green-600 text-green-100 font-medium py-2 rounded-md transition-colors duration-200"
                            >
                                Chuyển đổi hình thức
                            </button>
                            <button
                                onClick={handleHuyDon}
                                className="w-full bg-green-700 hover:bg-green-600 text-green-100 font-medium py-2 rounded-md transition-colors duration-200"
                            >
                                Hủy đơn
                            </button>
                        </div>
                    </div>
                </Modal>
            )}
        </>
    );
}

export { QuetMaThanhToan };
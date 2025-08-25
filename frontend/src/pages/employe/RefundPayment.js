import { useEffect, useState } from "react";
import { getHinhThuc, hoanDon } from "../../services/OrderService";
import { toast } from "react-toastify";
import Modal from "../../components/commons/modal";

function Refund({ id, setOpen }) {
    const [d, setD] = useState(id);
    const [showSuccess, setShowSuccess] = useState(false);
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [lan, setLan] = useState(0);
    const [isLoading, setIsLoading] = useState(false); // Thêm trạng thái loading

    useEffect(() => {
        getHinhThuc(id)
            .then((data) => {
                setD(data);
            })
            .catch(() => {
                toast("Lấy dữ liệu thất bại");
                setOpen(false);
            });
    }, [id, setOpen]);

    const handleRefund = (isCash = false) => {
        setIsLoading(true); // Bật loading
        hoanDon(id, 0, isCash)
            .then(() => {
                setIsLoading(false); // Tắt loading
                setShowSuccess(true);
                setLan(0); // Reset attempts on success
                // Giữ modal thành công trong 1 giây
                setTimeout(() => {
                    setOpen(false);
                }, 1000);
            })
            .catch((e) => {
                setIsLoading(false); // Tắt loading
                if (d === 1 && e?.response?.status === 500) {
                    let newLan = lan;
                    if (lan < 2) {
                        newLan = lan + 1;
                    }
                    setLan(newLan);
                    setErrorMessage(
                        newLan >= 2
                            ? "Không thể hoàn tiền online, vui lòng thử lại hoặc chuyển sang hoàn tiền mặt"
                            : "Hoàn hàng thất bại vui lòng thử lại"
                    );
                    setShowError(true);
                } else {
                    setErrorMessage(e?.response?.data?.message || "Hoàn hàng thất bại vui lòng thử lại");
                    setShowError(true);
                }
            });
    };

    return (
        <>
            {isLoading ? (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-4 flex justify-center items-center">
                        <svg
                            className="animate-spin h-8 w-8 text-green-700"
                            xmlns="http://www.w3.org/2000/svg"
                            fill="none"
                            viewBox="0 0 24 24"
                        >
                            <circle
                                className="opacity-25"
                                cx="12"
                                cy="12"
                                r="10"
                                stroke="currentColor"
                                strokeWidth="4"
                            ></circle>
                            <path
                                className="opacity-75"
                                fill="currentColor"
                                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                            ></path>
                        </svg>
                        <p className="ml-3 text-md text-gray-700">Đang xử lý...</p>
                    </div>
                </Modal>
            ) : showSuccess ? (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-4">
                        <h2 className="text-lg font-semibold text-green-900 mb-2">Hoàn hàng thành công</h2>
                        <p className="text-md text-gray-700 mb-5">Đơn hàng đã được hoàn trả thành công.</p>
                        <div className="flex justify-end">
                            <button
                                onClick={() => setOpen(false)}
                                className="px-4 py-2 bg-green-900 text-white rounded-lg hover:bg-green-950 transition"
                            >
                                OK
                            </button>
                        </div>
                    </div>
                </Modal>
            ) : showError ? (
                <Modal setOpen={() => setShowError(false)}>
                    <div className="z-50 w-96 bg-red-100 rounded-xl p-4">
                        <h2 className="text-lg font-semibold text-red-600 mb-2">Lỗi hoàn hàng</h2>
                        <p className="text-md text-gray-700 mb-5">{errorMessage}</p>
                        <div className="flex justify-end gap-3">
                            <button
                                onClick={() => setShowError(false)}
                                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
                            >
                                OK
                            </button>
                        </div>
                    </div>
                </Modal>
            ) : d === 2 ? (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-4">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">Xác nhận hoàn hàng (Trả tiền mặt)</h2>
                        <div className="flex">
                            <img
                                className="h-5 mt-2"
                                src="https://cdn-icons-png.flaticon.com/128/1828/1828877.png"
                                alt="check icon"
                            />
                            <p className="ml-3 text-md text-gray-700 mb-5">
                                Sản phẩm chỉ được trả khi đáp ứng đủ điều kiện hoàn trả
                            </p>
                        </div>
                        <div className="flex">
                            <img
                                className="h-5 mt-2"
                                src="https://cdn-icons-png.flaticon.com/128/1828/1828877.png"
                                alt="check icon"
                            />
                            <p className="ml-3 text-md text-gray-700 mb-5">
                                Kiểm tra đầy đủ thông tin hóa đơn đảm bảo chính xác
                            </p>
                        </div>
                        <div className="flex">
                            <img
                                className="h-5 mt-2"
                                src="https://cdn-icons-png.flaticon.com/128/1828/1828877.png"
                                alt="check icon"
                            />
                            <p className="ml-3 text-md text-gray-700 mb-5">
                                Đảm bảo nhận lại đầy đủ sản phẩm, và hoàn trả tiền đầy đủ cho khách hàng
                            </p>
                        </div>
                        <div className="flex justify-end gap-3">
                            <button
                                onClick={() => handleRefund(true)}
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                            >
                                Đồng ý
                            </button>
                            <button
                                onClick={() => setOpen(false)}
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </Modal>
            ) : (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-4">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">Xác nhận hoàn hàng (Hoàn hàng online)</h2>
                        <div className="flex">
                            <img
                                className="h-5 mt-2"
                                src="https://cdn-icons-png.flaticon.com/128/1828/1828877.png"
                                alt="check icon"
                            />
                            <p className="ml-3 text-md text-gray-700 mb-5">
                                Nếu hoàn trả liên tục hai lần không thành công hãy chọn hoàn tiền trực tiếp.
                            </p>
                        </div>
                        {d === 1 && lan === 2 ? (
                            <div className="flex">
                                <img
                                    className="h-5 mt-2"
                                    src="https://cdn-icons-png.flaticon.com/128/1828/1828877.png"
                                    alt="check icon"
                                />
                                <p className="ml-3 text-md text-gray-700 mb-5">
                                    Thực hiện đầy đủ các bước nếu hoàn trả bằng tiền mặt.
                                </p>
                            </div>
                        ) : null}
                        <div className="flex justify-end gap-3">
                            <button
                                onClick={() => handleRefund(false)}
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                            >
                                Đồng ý
                            </button>
                            {d === 1 && lan === 2 ? (
                                <button
                                    onClick={() => handleRefund(true)}
                                    className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                >
                                    Hoàn tiền mặt
                                </button>
                            ) : null}
                            <button
                                onClick={() => setOpen(false)}
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </Modal>
            )}
        </>
    );
}

export { Refund };
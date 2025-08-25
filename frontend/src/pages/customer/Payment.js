import { useState, useEffect } from "react";
import Modal from "../../components/commons/modal";
import { QRCodeCanvas as QRCode } from "qrcode.react";
import { getTrangThaiOnline, getThonTinThanhToanOnline } from "../../services/OrderService";
import { toast } from "react-toastify";
import { formatToVND } from "../../utils/Format";

function QuetMaThanhToan({ da, setOpen }) {
  const [openThanhCong, setOpenThanhCong] = useState(false);
  const [openThatBai, setOpenThatBai] = useState(false);
  const [shouldClose, setShouldClose] = useState(false);
  const [orderData, setOrderData] = useState(null);
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    getThonTinThanhToanOnline(da)
      .then((d) => setOrderData(d))
      .catch(() => {
        setOpenThatBai(true);
      });
  }, []);

  useEffect(() => {
    let timer;
    if (!openThanhCong && !openThatBai && orderData?.idCall) {
      timer = setInterval(() => {
        setRetryCount(prev => prev + 1);
        getTrangThaiOnline(orderData.idCall, retryCount + 1)
          .then((response) => {
            console.log(response);
            if (response === true) {
              setOpenThanhCong(true);
            } else if (response === 11) {
              setOpenThatBai(true);
            }
          })
          .catch((e) => {
            setOpenThatBai(true);
            toast.error(e?.response?.data?.message || "Lấy trạng thái thanh toán thất bại");
          });
      }, 1500);
    }
    return () => clearInterval(timer);
  }, [orderData?.idCall, openThanhCong, openThatBai, retryCount]);

  useEffect(() => {
    if (shouldClose) setOpen(false);
  }, [shouldClose, setOpen]);

  return (
    <>
      {openThanhCong ? (
        <Modal b={false}>
          <div className="bg-green-900 text-green-200 p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
            <h2 className="text-xl font-semibold">Giao dịch hoàn tất</h2>
            <p className="text-sm">Đơn hàng đã được thanh toán thành công</p>
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
      ) : openThatBai ? (
        <Modal b={false}>
          <div className="bg-red-900 text-red-200 p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
            <h2 className="text-xl font-semibold">Giao dịch thất bại</h2>
            <p className="text-sm">Lấy thông tin thanh toán thất bại</p>
            <div className="flex gap-2">
              <button
                onClick={() => {
                  setOpenThatBai(false);
                  setShouldClose(true);
                }}
                className="w-full bg-red-700 hover:bg-red-600 text-red-100 font-medium py-2 rounded-md transition-colors duration-200"
              >
                Đóng
              </button>
              <button
                onClick={() => {
                  setOpenThatBai(false);
                  getThonTinThanhToanOnline(da)
                    .then((d) => setOrderData(d))
                    .catch(() => setOpenThatBai(true));
                }}
                className="w-full bg-red-700 hover:bg-red-600 text-red-100 font-medium py-2 rounded-md transition-colors duration-200"
              >
                Thử lại
              </button>
            </div>
          </div>
        </Modal>
      ) : (
        <Modal setOpen={setOpen}>
          <div className="bg-white text-gray-800 p-6 rounded-lg shadow-lg max-w-lg w-full flex flex-row items-center gap-6">
            <div className="flex-1 border-r border-gray-200 pr-6">
              <h2 className="text-xl font-semibold  mb-6">Thông tin đơn hàng</h2>

              <div className="space-y-4">
                <div>
                  <p className="text-sm text-gray-500">Khách hàng</p>
                  <p className="text-sm font-medium text-gray-800">{orderData?.tenNguoiDung}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-500">Số điện thoại</p>
                  <p className="text-sm font-medium text-gray-800">{orderData?.soDienThoai}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-500">Mã đơn hàng</p>
                  <p className="text-sm font-medium text-gray-800">{orderData?.idCall}</p>
                </div>

                <div>
                  <p className="text-sm text-gray-500">Mô tả</p>
                  <p className="text-sm font-medium text-gray-800">Thanh toán đơn hàng</p>
                </div>

                <div>
                  <p className="text-sm text-gray-500">Số tiền</p>
                  <p className="text-sm font-semibold text-green-600">{formatToVND(orderData?.tongTien)}</p>
                </div>
              </div>
            </div>

            <div className="flex-1 bg-green-900 text-white p-4 rounded-lg flex flex-col items-center gap-2">
              <h2 className="text-xl font-semibold text-center">Quét mã QR để thanh toán</h2>
              <div className="bg-white p-2 rounded-md">
                <QRCode value={orderData?.qrCodeUrl || ""} size={200} />
              </div>
              <p className="text-xs text-center">Sử dụng App MoMo để dùng camera để quét mã QR</p>
            </div>
          </div>
        </Modal>
      )}
    </>
  );
}

export { QuetMaThanhToan };
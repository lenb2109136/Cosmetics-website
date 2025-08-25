import { useEffect, useState, useRef, useCallback } from "react";
import { getByMaVach, getByMaVachCorect, getByMaVachForCreateHoaDon } from "../../services/sanPhamService";
import { toast } from "react-toastify";
import Quagga from "quagga";
import { Order } from "./Order";
import "../../styles/quetMa.css"
function AddHoaDon() {
    const [danhSachNhap, setDanhSachNhap] = useState([]); // Lưu dạng [{idBienThe, soLuong}]
    const [maVach, setMaVach] = useState("");
    const [danhSachGoiY, setDanhSachGoiY] = useState([]);
    const [open, setOpen] = useState(false);
    const [openScanModal, setOpenScanModal] = useState(false);
    const [openProductConfirmModal, setOpenProductConfirmModal] = useState(false);
    const [openErrorModal, setOpenErrorModal] = useState(false);
    const [openDuplicateModal, setOpenDuplicateModal] = useState(false);
    const [scannedProduct, setScannedProduct] = useState(null);
    const [scannedSoLuong, setScannedSoLuong] = useState(1);
    const [scannedDonGia, setScannedDonGia] = useState(0);
    const scannerRef = useRef(null);
    const canvasRef = useRef(null);
    const isScanning = useRef(false);
    const [hinhThucThanhToan, setHinhThucThanhToan] = useState(0);
    const [re, setRe] = useState(0);
    const [nguoiDung, setNguoiDung] = useState({
        id: 0,
        ten: ""
    });
    const [taiXuong, setTaiXuong] = useState(false)
    const [luuTru, setLuuTru] = useState(false)
    const [refresh, setRefresh] = useState(false);

    // useEffect cho quét mã vạch
    useEffect(() => {
        if (openScanModal && !isScanning.current) {
            Quagga.init(
                {
                    inputStream: {
                        name: "Live",
                        type: "LiveStream",
                        target: scannerRef.current,
                        constraints: {
                            width: 480,
                            height: 360,
                            facingMode: "environment",
                        },
                    },
                    locator: {
                        patchSize: "medium",
                        halfSample: true,
                    },
                    decoder: {
                        readers: ["code_128_reader", "ean_reader", "ean_8_reader", "upc_reader"],
                    },
                    locate: true,
                },
                (err) => {
                    if (err) {
                        console.error("QuaggaJS init error:", err);
                        toast.error("Không thể khởi động camera!");
                        setOpenScanModal(false);
                        return;
                    }
                    Quagga.start();
                    isScanning.current = true;
                }
            );

            Quagga.onProcessed((result) => {
                const drawingCanvas = canvasRef.current;
                if (drawingCanvas && result) {
                    const ctx = drawingCanvas.getContext("2d");
                    ctx.clearRect(0, 0, drawingCanvas.width, drawingCanvas.height);

                    if (result.boxes) {
                        ctx.beginPath();
                        ctx.lineWidth = 2;
                        ctx.strokeStyle = "green";
                        result.boxes.forEach((box) => {
                            ctx.rect(
                                box[0].x,
                                box[0].y,
                                box[2].x - box[0].x,
                                box[2].y - box[0].y
                            );
                        });
                        ctx.stroke();
                    }
                }
            });

            Quagga.onDetected(async (data) => {
                if (!isScanning.current) return;
                isScanning.current = false;
                Quagga.pause();

                const scannedCode = data.codeResult.code;
                try {
                    const products = await getByMaVachCorect(scannedCode);
                    if (products && products.length > 0) {
                        const product = products[0];
                        const existingProduct = danhSachNhap.find((p) => p.idBienThe === product.id);
                        if (existingProduct) {
                            setScannedProduct(product);
                            setScannedSoLuong(existingProduct.soLuong);
                            setScannedDonGia(product.donGia || 0);
                            setOpenDuplicateModal(true);
                        } else {
                            setScannedProduct(product);
                            setScannedSoLuong(1);
                            setScannedDonGia(product.donGia || 0);
                            setOpenProductConfirmModal(true);
                        }
                    } else {
                        setOpenErrorModal(true);
                    }
                } catch (error) {
                    console.error("Error fetching product:", error);
                    setOpenErrorModal(true);
                }
            });

            return () => {
                if (isScanning.current) {
                    try {
                        Quagga.stop();
                        isScanning.current = false;
                    } catch (error) {
                        console.error("Error stopping Quagga:", error);
                    }
                }
            };
        }
    }, [openScanModal, danhSachNhap]);

    // Tìm kiếm sản phẩm theo mã vạch
    useEffect(() => {
        if (maVach.trim()) {
            getByMaVachForCreateHoaDon(maVach, true)
                .then((d) => {
                    setDanhSachGoiY(d.slice(0, 4));
                })
                .catch(() => {
                    setDanhSachGoiY([]);
                });
        } else {
            setDanhSachGoiY([]);
        }
    }, [maVach]);

    // Thêm hoặc cập nhật sản phẩm vào danhSachNhap
    const handleAddItem = useCallback((item, soLuong) => {
        const newItem = { idBienThe: item.id, soLuong }; // Chuẩn hóa định dạng
        if (!danhSachNhap.some((product) => product.idBienThe === item.id)) {
            setDanhSachNhap([...danhSachNhap, newItem]);
            toast.success("Thêm sản phẩm thành công!");
        } else {
            setDanhSachNhap(
                danhSachNhap.map((product) =>
                    product.idBienThe === item.id ? { ...product, soLuong } : product
                )
            );
            toast.success("Cập nhật sản phẩm thành công!");
        }
        setMaVach("");
        setOpen(false);
        setOpenProductConfirmModal(false);
        setOpenDuplicateModal(false);
        if (openScanModal) {
            isScanning.current = true;
            Quagga.start();
        }
    }, [danhSachNhap, openScanModal]);

    // Hủy xác nhận sản phẩm
    const handleCancelProduct = useCallback(() => {
        setOpenProductConfirmModal(false);
        setOpenDuplicateModal(false);
        setScannedProduct(null);
        if (openScanModal) {
            isScanning.current = true;
            Quagga.start();
        }
    }, [openScanModal]);

    // Đóng modal lỗi
    const handleCloseErrorModal = useCallback(() => {
        setOpenErrorModal(false);
        if (openScanModal) {
            isScanning.current = true;
            Quagga.start();
        }
    }, [openScanModal]);

    // Đóng modal quét mã vạch
    const handleCloseScanModal = useCallback(() => {
        setOpenScanModal(false);
        if (isScanning.current) {
            try {
                Quagga.stop();
                isScanning.current = false;
                console.log("Quagga stopped successfully");
            } catch (error) {
                console.error("Error stopping Quagga:", error);
                toast.error("Lỗi khi dừng quét mã vạch!");
            }
        }
    }, []);

    // Lấy thông tin sản phẩm để hiển thị trong bảng
    const [productDetails, setProductDetails] = useState({});
    useEffect(() => {
        const fetchProductDetails = async () => {
            const ids = danhSachNhap.map((item) => item.idBienThe);
            if (ids.length > 0) {
                try {
                    const products = await Promise.all(
                        ids.map((id) => getByMaVachCorect(id))
                    );
                    const details = products.reduce((acc, productList) => {
                        if (productList && productList[0]) {
                            acc[productList[0].id] = productList[0];
                        }
                        return acc;
                    }, {});
                    setProductDetails(details);
                } catch (error) {
                    console.error("Error fetching product details:", error);
                    toast.error("Lỗi khi lấy thông tin sản phẩm!");
                }
            } else {
                setProductDetails({});
            }
        };
        fetchProductDetails();
    }, [danhSachNhap]);

    return (
        <div className="relative p-3">
            <style>
                {`
                    .custom-scrollbar::-webkit-scrollbar {
                        width: 8px;
                    }
                    .custom-scrollbar::-webkit-scrollbar-track {
                        background: #f1f1f1;
                        border-radius: 4px;
                    }
                    .custom-scrollbar::-webkit-scrollbar-thumb {
                        background: #4a9b6b
                        border-radius: 4px;
                    }
                    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
                        background: #3d8a5a;
                    }
                    .modal-compact { padding: 1.5rem; max-width: 400px; border-radius: 0.75rem; border: 1px solid #d1d5db; }
                    .modal-scan { padding: 1.5rem; max-width: 450px; border-radius: 0.75rem; border: 1px solid #d1d5db; }
                `}
            </style>
            <div className="w-full space-y-4">

                {/* Hàng tiêu đề và tìm kiếm */}
                <div className="flex flex-wrap items-center gap-4">
                    <p className="font-bold text-xl  whitespace-nowrap p-2 text-green-900 rounded-md border shadow-md ml-3 w-fit">
                        <i class="fa-solid fa-cart-shopping "></i> Thêm hóa đơn
                    </p>

                    <div className="relative flex-1 min-w-[250px]">
                        <input
                            className="outline-none border border-gray-400 w-full rounded-md pl-4 py-1"
                            placeholder="Nhập tên hoặc mã hàng hóa"
                            value={maVach}
                            onFocus={() => setOpen(true)}
                            onBlur={() => setTimeout(() => setOpen(false), 200)}
                            onChange={(e) => setMaVach(e.target.value)}
                        />
                        {open && (
                            <div className="absolute bg-white border border-green-900 rounded-md shadow-md w-full mt-1 z-10 top-full">
                                <div className="absolute -top-2 left-3 w-0 h-0 border-l-8 border-r-8 border-b-8 border-l-transparent border-r-transparent border-b-green-900"></div>
                                <ul className="text-sm p-2 max-h-60 overflow-y-auto custom-scrollbar">
                                    {danhSachGoiY.length > 0 ? (
                                        danhSachGoiY.map((item) => (
                                            <li
                                                key={item.id}
                                                onClick={() => handleAddItem(item, 1)}
                                                className={`py-1 px-2 hover:bg-green-900 hover:text-white cursor-pointer 
                    ${danhSachNhap.some((product) => product.idBienThe === item.id) ? "opacity-50 cursor-not-allowed" : ""}`}
                                            >
                                                <p>{item?.tenSanPham} - {item?.ten}</p>
                                                <p className="font-bold">({item?.maVach})</p>
                                            </li>
                                        ))
                                    ) : (
                                        <li className="py-1 px-2 text-gray-500 cursor-not-allowed">
                                            Không có sản phẩm
                                        </li>
                                    )}
                                </ul>
                            </div>
                        )}
                    </div>

                    <button
                        onClick={() => setOpenScanModal(true)}
                        className="  py-2 px-4 rounded-md text-green-900 shadow-md border transition whitespace-nowrap"
                    >
                        Quét mã vạch
                    </button>
                </div>

                {/* Hàng các nút chức năng */}
                <div className="flex flex-wrap gap-4 justify-between pb-3">
                     <p className="ml-3 text-xl font-bold mb-4 text-gray-800 p-2 shadow-md border w-fit rounded-md"><i class="fa-solid fa-bars-staggered text-green-900 rounded-md "></i> Giỏ hàng</p >
                
                  <div className="basis-[80%] flex justify-end">
                      <button
                        onClick={() => document.getElementById("down").click()}
                        className={`mr-5  py-2 px-4 rounded-md  shadow-md border transition whitespace-nowrap ${re==0?"pointer-events-none text-gray-400":"text-green-900"}`}
                    >
                        <i class="fa-solid fa-file-export mr-1"></i>
                        Xuất hóa đơn
                    </button>
                    <a id="down" download href={"http://localhost:8080/api/hoadontaiquay/getbill/" + re}></a>

                    <button
                        onClick={() => {
                            setDanhSachNhap([])
                            setRe(0)
                        }}
                        className="mr-5  py-2 px-4 rounded-md text-green-900 shadow-md border transition whitespace-nowrap"
                    >
                        <i class="fa-solid fa-arrows-rotate mr-1"></i>
                        Làm mới
                    </button>

                    <button
                        onClick={() => setLuuTru(true)}
                        className="mr-5 flex items-center   py-2 px-4 rounded-md text-green-900 shadow-md border transition whitespace-nowrap"
                    >
                        <img
                            src="https://cdn-icons-gif.flaticon.com/15309/15309697.gif"
                            className="w-8 h-8 rounded-full mr-2"
                            alt="Lưu tạm"
                        />
                        Lưu trữ
                    </button>

                    <button
                        onClick={() => setTaiXuong(true)}
                        className=" flex items-center text-green-900 shadow-md border py-2 px-4 rounded-md  transition whitespace-nowrap"
                    >
                        <img
                            src="https://cdn-icons-gif.flaticon.com/17905/17905441.gif"
                            className="w-8 h-8 rounded-full mr-2"
                            alt="Tải xuống"
                        />
                        Tải xuống
                    </button>
                  </div>
                </div>
            </div>


            {/* Modal quét mã vạch */}
            {openScanModal && (
                <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 relative overflow-hidden transform transition-all duration-300">
                        {/* Close Button - Top Right */}
                        <button
                            className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 transition-colors duration-200"
                            onClick={handleCloseScanModal}
                            aria-label="Close"
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>

                        {/* Title */}
                        <h2 className="text-xl font-bold text-green-900 mb-6 text-center">Quét Mã sản phẩm</h2>

                        {/* Scanner Area */}
                        <div className="flex justify-center relative bg-gray-100 rounded-lg overflow-hidden">
                            <div
                                ref={scannerRef}
                                className="w-full max-w-[360px] h-[240px] bg-gray-200 relative"
                            />
                            <canvas
                                ref={canvasRef}
                                className="absolute top-0 left-0 w-full h-full max-w-[360px] max-h-[240px] pointer-events-none"
                            />
                            {/* Overlay Scanner Animation */}
                            <div className="absolute inset-0 flex items-center">
                                <div className="w-4/5 h-1 bg-green-500 opacity-50 animate-scanner-line" />
                            </div>
                        </div>

                        {/* Optional Instruction Text */}
                        <p className="text-center text-gray-500 text-sm mt-4">Đặt mã vạch vào khung để quét</p>
                    </div>
                </div>
            )}

            {/* Modal xác nhận sản phẩm quét được */}
            {openProductConfirmModal && scannedProduct && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white modal-compact">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">
                            Xác nhận sản phẩm
                        </h2>
                        <p className="text-md text-gray-700 mb-2">
                            Sản phẩm: {scannedProduct.tenSanPham} - {scannedProduct.ten}
                        </p>
                        <p className="text-md text-gray-700 mb-4">
                            Mã vạch: {scannedProduct.maVach}
                        </p>
                        <div className="mb-4">
                            <label className="block text-sm font-bold text-gray-700">Số lượng:</label>
                            <input
                                type="number"
                                className="w-full outline-none border border-gray-200 rounded-md p-2 mt-1"
                                value={scannedSoLuong}
                                onChange={(e) => setScannedSoLuong(parseInt(e.target.value) || 1)}
                                min={1}
                            />
                        </div>
                        <div className="flex justify-end gap-3 mt-4">
                            <button
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                onClick={() => handleAddItem(scannedProduct, scannedSoLuong)}
                            >
                                Thêm
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={handleCancelProduct}
                            >
                                Hủy
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal sản phẩm trùng */}
            {openDuplicateModal && scannedProduct && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white modal-compact">
                        <h2 className="text-lg font-semibold text-yellow-700 mb-2">
                            Sản phẩm đã tồn tại
                        </h2>
                        <p className="text-md text-gray-700 mb-2">
                            Sản phẩm: {scannedProduct.tenSanPham} - {scannedProduct.ten}
                        </p>
                        <p className="text-md text-gray-700 mb-4">
                            Mã vạch: {scannedProduct.maVach}
                        </p>
                        <div className="mb-4">
                            <label className="block text-sm font-bold text-gray-700">Số lượng:</label>
                            <input
                                type="number"
                                className="w-full outline-none border border-gray-200 rounded-md p-2 mt-1"
                                value={scannedSoLuong}
                                onChange={(e) => setScannedSoLuong(parseInt(e.target.value) || 1)}
                                min={1}
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-sm font-bold text-gray-700">Đơn giá:</label>
                            <input
                                type="number"
                                className="w-full outline-none border border-gray-200 rounded-md p-2 mt-1"
                                value={scannedDonGia}
                                onChange={(e) => setScannedDonGia(parseInt(e.target.value) || 0)}
                                min={0}
                            />
                        </div>
                        <div className="flex justify-end gap-3 mt-4">
                            <button
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                onClick={() => handleAddItem(scannedProduct, scannedSoLuong)}
                            >
                                Cập nhật
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={handleCancelProduct}
                            >
                                Hủy
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal lỗi khi không tìm thấy sản phẩm */}
            {openErrorModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white modal-compact">
                        <h2 className="text-lg font-semibold text-red-700 mb-2">
                            Lỗi quét mã vạch
                        </h2>
                        <p className="text-md text-gray-700 mb-5">
                            Không tìm thấy sản phẩm với mã vạch này.
                        </p>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={handleCloseErrorModal}
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Bảng hiển thị danhSachNhap */}
            <Order
                refresh={refresh}
                setRe={setRe}
                nguoiDung={nguoiDung}
                setNguoiDung={setNguoiDung}
                dsMatHang={danhSachNhap}
                hinhThucThanhToan={hinhThucThanhToan}
                setHinhThucThanhToan={setHinhThucThanhToan}
                setDanhSachMatHang={setDanhSachNhap}
                luuTru={luuTru}
                setLuuTru={setLuuTru}
                taiXuong={taiXuong}
                setTaiXuong={setTaiXuong}
            />
        </div>
    );
}

export { AddHoaDon };
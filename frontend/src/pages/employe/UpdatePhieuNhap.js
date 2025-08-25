import { useEffect, useState, useRef } from "react";
import { getByMaVach, getByMaVachCorect, getByMaVachCorectv2, getByMaVachForPhieuNhap } from "../../services/sanPhamService";
import { formatToVND } from "../../utils/Format";
import { getAll } from "../../services/SupplierService";
import { deletePhieuNhap, getUpdate, save, savev2, update } from "../../services/PhieuNhapService";
import { toast } from "react-toastify";
import Quagga from "quagga";
import { OCRScanner } from "./OCRScanner";
import React from "react";
import { useNavigate, useParams } from "react-router-dom";

function chuyenDoiDanhSach(danhSach) {
    if (!Array.isArray(danhSach)) return [];
    const nhomTheoId = {};

    danhSach.forEach((item, index) => {
        const { id, tenSanPham, ten } = item;
        if (!nhomTheoId[id]) {
            nhomTheoId[id] = {
                id,
                tenSanPham,
                ten,
                cacQuyCach: []
            };
        }
        nhomTheoId[id].cacQuyCach.push({
            viTri: index,
            quyCachDongGoi: item.quyCachDongGoi,
            maVach: item.maVach,
            soLuongTronQuyCach: item.soLuongTronQuyCach,
            duongDanIcon: item.duongDanIcon,
            macDinh: item.macDinh,
            soLuong: item.soLuong,
            donGia: item.donGia
        });
    });
    return Object.values(nhomTheoId);
}

function UpdatePhieuNhap() {
    const { id } = useParams();
    const [danhSachNhap, setDanhSachNhap] = useState([]);
    const [coTheCapNhat, setCoTheCapNhat] = useState(false);
    const [maVach, setMaVach] = useState("");
    const [danhSachGoiY, setDanhSachGoiY] = useState([]);
    const [open, setOpen] = useState(false);
    const [load, setLoad] = useState(false);
    const [donViCungCap, setDonViCungCap] = useState([]);
    const [supplierSearch, setSupplierSearch] = useState("");
    const [openSupplier, setOpenSupplier] = useState(false);
    const [selectedSupplier, setSelectedSupplier] = useState(null);
    const [vat, setVat] = useState(0);
    const [openConfirmModal, setOpenConfirmModal] = useState(false);
    const [openCancelConfirmModal, setOpenCancelConfirmModal] = useState(false);
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
    const nhanVien = localStorage.getItem("name") || "Chưa xác định";
    const navigate = useNavigate();

    useEffect(() => {
        getUpdate(id)
            .then((response) => {
                const { data, tenConTy } = response;
                if (data) {
                    setCoTheCapNhat(response?.canUpdate);
                    setDanhSachNhap(data.sanPham || []);
                    setVat(data.thueVAT || 0);
                    if (data.idDonViCungCap) {
                        const supplier = donViCungCap.find(s => s.id === data.idDonViCungCap) || {
                            id: data.idDonViCungCap,
                            ten: tenConTy || data.tenCongTy || "Unknown Supplier",
                            maSoThue: data.maSoThueCongTy || "",
                            diaChi: data.diaChiCongTy || "",
                            soDienThoai: data.soDienThoaiCongTy || ""
                        };
                        setSelectedSupplier(supplier);
                        setSupplierSearch(supplier.ten);
                    }
                }
            })
            .catch((error) => {
                console.error("Error fetching update data:", error);
                toast.error("Không thể tải dữ liệu phiếu nhập!");
            });
    }, [id, donViCungCap]);

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
                    const products = await getByMaVachCorectv2(scannedCode);
                    if (products && products.length > 0) {
                        const product = products[0];
                        const existingProduct = danhSachNhap.find((p) => p.id === product.id && p.maVach === product.maVach);
                        if (existingProduct) {
                            setScannedProduct(existingProduct);
                            setScannedSoLuong(existingProduct.soLuong);
                            setScannedDonGia(existingProduct.donGia || 0);
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
                    setOpenErrorModal(true);
                }
            });

            return () => {
                if (isScanning.current) {
                    Quagga.stop();
                    isScanning.current = false;
                }
            };
        }
    }, [openScanModal, danhSachNhap]);

    useEffect(() => {
        if (maVach.trim()) {
            getByMaVachForPhieuNhap(maVach)
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

    useEffect(() => {
        getAll()
            .then((d) => {
                setDonViCungCap(d);
            })
            .catch(() => {});
    }, []);

    const handleAddItem = (item, soLuong, donGia) => {
        if (!danhSachNhap.some((product) => product.id === item.id && product.maVach === item.maVach)) {
            setDanhSachNhap([...danhSachNhap, { ...item, soLuong, donGia }]);
        } else {
            setDanhSachNhap(
                danhSachNhap.map((product) =>
                    product.id === item.id && product.maVach === item.maVach
                        ? { ...product, soLuong, donGia }
                        : product
                )
            );
        }
        setMaVach("");
        setOpen(false);
        setOpenProductConfirmModal(false);
        setOpenDuplicateModal(false);
        if (openScanModal) {
            isScanning.current = true;
            Quagga.start();
        }
    };

    const handleUpdateItem = (viTri, soLuong, donGia) => {
        setDanhSachNhap(
            danhSachNhap.map((product, index) =>
                index === viTri ? { ...product, soLuong, donGia } : product
            )
        );
        setLoad(!load);
    };

    const handleRemoveItem = (viTri) => {
        setDanhSachNhap(danhSachNhap.filter((_, index) => index !== viTri));
        setLoad(!load);
    };

    const handleCancelProduct = () => {
        setOpenProductConfirmModal(false);
        setOpenDuplicateModal(false);
        setScannedProduct(null);
        if (openScanModal) {
            isScanning.current = true;
            Quagga.start();
        }
    };

    const handleCloseErrorModal = () => {
        setOpenErrorModal(false);
        if (openScanModal) {
            isScanning.current = true;
            Quagga.start();
        }
    };

    const handleCloseScanModal = () => {
        if (isScanning.current) {
            Quagga.stop();
            isScanning.current = false;
        }
        setOpenScanModal(false);
    };

    const handleSelectSupplier = (supplier) => {
        setSelectedSupplier(supplier);
        setSupplierSearch(supplier.ten);
        setOpenSupplier(false);
    };

    const filteredSuppliers = supplierSearch
        ? donViCungCap.filter(
            (s) =>
                s.ten.toLowerCase().includes(supplierSearch.toLowerCase()) ||
                s.maSoThue.includes(supplierSearch)
        ).slice(0, 7)
        : donViCungCap.slice(0, 7);

    const totalAmount = danhSachNhap.reduce(
        (sum, item) => sum + item.soLuong * (item.donGia || 0),
        0
    );
    const totalPayable = totalAmount + parseFloat(vat || 0);

    const handleSave = () => {
        update(danhSachNhap, selectedSupplier, vat, id)
            .then((d) => {
                toast.success("Cập nhật phiếu thành công");
                navigate("/employee/nhaphang/update/" + d.data.data);
            })
            .catch((e) => {
                toast.error(e?.response?.data?.message || "Lưu thông tin thất bại");
                setOpenConfirmModal(false);
            });
    };

    const handleCancelInvoice = () => {
        deletePhieuNhap(id).then(()=>{
            toast.success("Xóa phiếu nhập thành công")
            navigate("/employee/nhaphang")
        }).catch(()=>{
            toast.error("Xóa phiếu nhập thất bại")
        })
    };

    const danhSachNhom = chuyenDoiDanhSach(danhSachNhap);

    return (
        <div className="relative">
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
                        background: #4a9b6b;
                        border-radius: 4px;
                    }
                    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
                        background: #3d8a5a;
                    }
                    .no-wrap { white-space: nowrap; }
                    .modal-compact { padding: 1.5rem; max-width: 400px; border-radius: 0.75rem; border: 1px solid #d1d5db; }
                    .modal-scan { padding: 1.5rem; max-width: 450px; border-radius: 0.75rem; border: 1px solid #d1d5db; }
                    .sub-row { background-color: #f9f9f9; }
                `}
            </style>
            <div className="flex justify-between items-center p-3">
                <p className="font-bold text-lg">
                    <i className="fa-solid fa-file-import text-green-900 p-2 rounded-md mr-1 shadow-md border"></i> Nhập hàng
                </p>
                <div className="flex gap-2">
                    <button
                        onClick={() => setOpenScanModal(true)}
                        className="flex items-center gap-2 text-green-900 py-2 px-4 rounded-md border shadow-md transition duration-200"
                    >
                        <i className="fa-solid fa-barcode text-green-900 text-lg"></i>
                        <span className="text-sm font-medium">Quét mã vạch</span>
                    </button>
                    {coTheCapNhat && (
                        <button
                            onClick={() => setOpenConfirmModal(true)}
                            className="text-green-900 py-2 px-4 rounded-md shadow-md border transition"
                        >
                            Cập nhật
                        </button>
                    )}
                    {coTheCapNhat && (
                        <button
                            onClick={() => setOpenCancelConfirmModal(true)}
                            className="text-red-900 py-2 px-4 rounded-md shadow-md border transition"
                        >
                            Hủy phiếu
                        </button>
                    )}
                </div>
            </div>
            <div className="flex p-3 items-end relative">
                <div className="relative w-[30%]">
                    <input
                        className="outline-none border-1 border w-full rounded-md pl-4 py-1"
                        placeholder="Nhập tên hoặc mã hàng hóa"
                        value={maVach}
                        onFocus={() => setOpen(true)}
                        onBlur={() => setTimeout(() => setOpen(false), 200)}
                        onChange={(e) => setMaVach(e.target.value)}
                    />
                    {open && (
                        <div className="absolute bg-white border border-green-900 rounded-md shadow-md w-full mt-1 z-10 mt-2">
                            <div className="absolute -top-2 left-3 w-0 h-0 border-l-8 border-r-8 border-b-8 border-l-transparent border-r-transparent border-b-green-900"></div>
                            <ul className="text-sm p-2 max-h-60 overflow-y-auto custom-scrollbar">
                                {danhSachGoiY.length > 0 ? (
                                    danhSachGoiY.map((item) => (
                                        <li
                                            key={`${item.id}-${item.maVach}`}
                                            onClick={() => handleAddItem(item, 1, item.donGia || 0)}
                                            className={`py-1 px-2 hover:bg-green-900 hover:text-white cursor-pointer 
                                                ${danhSachNhap.some((product) => product.id === item.id && product.maVach === item.maVach) ? "opacity-50 cursor-not-allowed" : ""}`}
                                        >
                                            <p>
                                                {item?.tenSanPham} - {item?.ten}
                                            </p>
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
            </div>
            <div className="flex p-3">
                <table className="basis-[75%] border-collapse w-full shadow-md rounded-md overflow-hidden">
                    <thead>
                        <tr className="text-gray-500 bg-gray-100 rounded-md text-center">
                            <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">STT</th>
                            <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Tên sản phẩm</th>
                            <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Số lượng</th>
                            <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Đơn giá</th>
                            <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Thành tiền</th>
                            <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Quy cách đóng gói</th>
                            <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {danhSachNhom.length > 0 ? (
                            danhSachNhom.map((item, index) => (
                                <React.Fragment key={item.id}>
                                    {item.cacQuyCach.map((quyCach, subIndex) => (
                                        <tr
                                            key={`${item.id}-${quyCach.maVach}`}
                                            className={`text-center transition`}
                                        >
                                            {subIndex === 0 && (
                                                <>
                                                    <td
                                                        className="border p-2"
                                                        rowSpan={item.cacQuyCach.length}
                                                    >
                                                        {index + 1}
                                                    </td>
                                                    <td
                                                        className="border p-2"
                                                        rowSpan={item.cacQuyCach.length}
                                                    >
                                                        <p className="font-bold">{item.tenSanPham}</p>
                                                        <span className="pl-1 pr-1 rounded-sm">
                                                            <span className="text-green-900 border pl-1 pr-1 rounded-sm shadow-sm">
                                                                <i className="fa-solid fa-layer-group"></i>
                                                            </span>
                                                        </span>{" "}
                                                        <span>{item.ten}</span>
                                                    </td>
                                                </>
                                            )}
                                            <td className="border p-2">
                                                <input
                                                    type="number"
                                                    className="outline-none pl-2 border border-gray-200 w-16 rounded-md"
                                                    onChange={(e) => {
                                                        const newSoLuong = parseInt(e.target.value) || 0;
                                                        if (newSoLuong <= 0) {
                                                            handleRemoveItem(quyCach.viTri);
                                                        } else {
                                                            handleUpdateItem(quyCach.viTri, newSoLuong, quyCach.donGia);
                                                        }
                                                    }}
                                                    min={0}
                                                    value={quyCach.soLuong}
                                                />
                                            </td>
                                            <td className="border p-2">
                                                <input
                                                    type="number"
                                                    className="outline-none pl-2 border border-gray-200 w-24 rounded-md"
                                                    onChange={(e) => {
                                                        const newDonGia = parseInt(e.target.value) || 0;
                                                        handleUpdateItem(quyCach.viTri, quyCach.soLuong, newDonGia);
                                                    }}
                                                    min={0}
                                                    value={quyCach.donGia}
                                                />
                                            </td>
                                            <td className="border p-2">
                                                {formatToVND(quyCach.soLuong * (quyCach.donGia || 0))}
                                            </td>
                                            <td className="border p-2">
                                                <div className="flex items-center gap-2 justify-center">
                                                    <span className="text-sm text-gray-700 p-2 shadow-md rounded-md text-green-900 border">
                                                        {quyCach?.quyCachDongGoi} [ {quyCach?.soLuongTronQuyCach} đơn vị / {quyCach?.quyCachDongGoi}]
                                                    </span>
                                                </div>
                                            </td>
                                            {subIndex === 0 && (
                                                <td
                                                    className="border p-2 justify-center"
                                                    rowSpan={item.cacQuyCach.length}
                                                >
                                                    <p
                                                        className="w-fit p-2 cursor-pointer "
                                                        onClick={() => {
                                                                const updatedDanhSachNhap = danhSachNhap.filter(
                                                                    (product) => product.id !== item.id
                                                                );
                                                                setDanhSachNhap(updatedDanhSachNhap);
                                                            
                                                        }}
                                                    >
                                                        Xóa
                                                    </p>
                                                </td>
                                            )}
                                        </tr>
                                    ))}
                                </React.Fragment>
                            ))
                        ) : (
                            <tr className="text-center">
                                <td colSpan={7}>
                                    <div className="w-full flex justify-center">
                                        <img
                                            className="w-44"
                                            src="https://media.istockphoto.com/id/861576608/vi/vec-to/bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-empty-shopping-bag-m%E1%BA%ABu-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-vector-online-business.jpg?s=612x612&w=0&k=20&c=-6p0qVyb_p8VeHVu1vJVaDHa6Wd_mCjMkrFBTlQdehI="
                                            alt="Giỏ hàng trống"
                                        />
                                    </div>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
                <div className="w-full basis-[25%] border-l-2 border ml-2 shadow-md p-4 rounded-md">
                    <h4 className="text-center text-green-900 font-bold mb-4">Thông tin thanh toán</h4>
                    <div className="mb-6 flex items-center max-w-md">
                        <div className="flex items-center justify-center h-16">
                            <i className="fa-solid fa-boxes-packing rounded-md shadow-md text-lg shadow-md text-green-900 p-2 border rounded-md p-2"></i>
                        </div>
                        <div className="ml-3 w-full relative">
                            <label className="text-sm text-gray-800 font-semibold mb-1 block">Nhà cung cấp:</label>
                            <input
                                type="text"
                                placeholder="Nhập tên hoặc mã số thuế"
                                className="w-full border border-gray-300 rounded-md px-3 py-2 text-sm outline-none"
                                value={supplierSearch}
                                onChange={(e) => setSupplierSearch(e.target.value)}
                                onFocus={() => setOpenSupplier(true)}
                                onBlur={() => setTimeout(() => setOpenSupplier(false), 200)}
                            />
                            {openSupplier && (
                                <div className="absolute left-0 right-0 bg-white border border-green-900 rounded-md shadow-md mt-1 z-10">
                                    <div className="absolute -top-2 left-5 w-0 h-0 border-l-8 border-r-8 border-b-8 border-l-transparent border-r-transparent border-b-green-900"></div>
                                    <ul className="text-sm p-2 max-h-60 overflow-y-auto custom-scrollbar">
                                        {filteredSuppliers.length > 0 ? (
                                            filteredSuppliers.map((supplier) => (
                                                <li
                                                    key={supplier.id}
                                                    onClick={() => handleSelectSupplier(supplier)}
                                                    className="py-1 px-2 hover:bg-green-900 hover:text-white cursor-pointer"
                                                >
                                                    <p>{supplier.ten}</p>
                                                    <p className="font-bold">({supplier.maSoThue})</p>
                                                </li>
                                            ))
                                        ) : (
                                            <li className="py-1 px-2 text-gray-500 cursor-not-allowed">
                                                Không tìm thấy nhà cung cấp
                                            </li>
                                        )}
                                    </ul>
                                </div>
                            )}
                        </div>
                    </div>
                    <div className="flex justify-between">
                        <div className="mb-6 flex items-center rounded-md">
                            <i className="fa-solid fa-money-check-dollar shadow-md text-green-900 p-2 border rounded-md text-md"></i>
                            <div className="rounded-lg ml-1 pl-1 pr-1">
                                <p className="text-gray-800 text-sm">Tổng tiền:</p>
                                <p className="text-gray-900 text-sm">{formatToVND(totalAmount)}</p>
                            </div>
                        </div>
                        <div className="mb-6 flex items-center rounded-md">
                            <i className="fa-solid fa-money-check-dollar shadow-md text-green-900 p-2 border rounded-md text-md"></i>
                            <div className="rounded-lg ml-1 pl-1 pr-1">
                                <p className="text-gray-800 text-sm">Nhân viên Lập:</p>
                                <p className="text-black text-sm">{nhanVien}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            {/* Modal xác nhận cập nhật phiếu nhập */}
            {openConfirmModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white modal-compact">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">
                            Xác nhận cập nhật phiếu nhập
                        </h2>
                        <p className="text-md text-gray-700 mb-5">
                            Bạn chắc chắn muốn cập nhật phiếu nhập này?
                        </p>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                onClick={handleSave}
                            >
                                Đồng ý
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={() => setOpenConfirmModal(false)}
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {/* Modal xác nhận hủy phiếu */}
            {openCancelConfirmModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white modal-compact">
                        <h2 className="text-lg font-semibold text-red-700 mb-2">
                            Xác nhận hủy phiếu nhập
                        </h2>
                        <p className="text-md text-gray-700 mb-5">
                            Bạn chắc chắn muốn hủy phiếu nhập này?
                        </p>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-red-700 text-white rounded-lg hover:bg-red-800 transition"
                                onClick={handleCancelInvoice}
                            >
                                Đồng ý
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={() => setOpenCancelConfirmModal(false)}
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {/* Modal quét mã vạch */}
            {openScanModal && (
                <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-6 relative overflow-hidden transform transition-all duration-300">
                        <button
                            className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 transition-colors duration-200"
                            onClick={handleCloseScanModal}
                            aria-label="Close"
                        >
                            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                        <h2 className="text-2xl font-bold text-green-600 mb-6 text-center">Quét Mã Sản Phẩm</h2>
                        <div className="flex justify-center relative bg-gray-100 rounded-lg overflow-hidden">
                            <div
                                ref={scannerRef}
                                className="w-full max-w-[360px] h-64 bg-gray-200 relative"
                            />
                            <canvas
                                ref={canvasRef}
                                className="absolute top-0 left-0 w-full h-full max-w-[360px] max-h-64 pointer-events-none"
                            />
                            <div className="absolute inset-0 flex items-center">
                                <div className="w-full h-1 bg-green-500 opacity-50 animate-scanner-line" />
                            </div>
                        </div>
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
                                onClick={() => handleAddItem(scannedProduct, scannedSoLuong, scannedDonGia)}
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
                                onClick={() => handleAddItem(scannedProduct, scannedSoLuong, scannedDonGia)}
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
        </div>
    );
}

export { UpdatePhieuNhap };
import { useEffect, useRef, useState } from "react";
import { PickProduct } from "../../../components/admin/PickProduct";
import Modal from "../../../components/commons/modal";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../../utils/Format";
import { Tooltip } from "../../../components/commons/ToolTip";
import { validateDuLieu } from "../../../utils/Vadilators/FlashSaleCreateVadilator";
import { getFlash, saveFlashSale } from "../../../services/FlashSaleService";
import { useNavigate, useSearchParams } from "react-router-dom";
import gsap from "gsap";

function AddFlash() {
    const [openPickProduct, setOpenPickProduct] = useState(false);
    const [danhSachChon, setDanhSachChon] = useState([]);
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const id = searchParams.get("id");
    const [idchon, setidchon] = useState(0);

    const [flash, setFlash] = useState([]);
    const [result, setResult] = useState({
        ngayBatDau: "",
        ngayKetThuc: "",
        thoiGianBatDau: "",
        thoiGianKetThuc: "",
        data: []
    });
    const [dealtrung, setdealtrung] = useState({ data: [], dataPhu: [],flash:[] });
    const [showWarningDiv, setShowWarningDiv] = useState(false);

    const headerRef = useRef(null);
    const dateTimeRef = useRef(null);
    const productTableRef = useRef(null);
    const warningDivRef = useRef(null);

    const toggleWarningDiv = (d) => {
        setFlash(d);
        setShowWarningDiv((prev) => !prev);
    };

    useEffect(() => {
        if (id != null && id > 0) {
            getFlash(id).then((data) => {
                setResult(data);
                setDanhSachChon(data.data);
            }).catch(() => {
                toast.error("Flash sale không tồn tại");
                navigate(-1);
            });
        }
    }, [id, navigate]);

    useEffect(() => {
        const ctx = gsap.context(() => {
            // Header animation
            gsap.from(headerRef.current, {
                opacity: 0,
                y: -30,
                duration: 0.6,
                ease: "power2.out",
            });

            // Date & Time inputs animation
            gsap.from(dateTimeRef.current.children, {
                opacity: 0,
                y: 20,
                duration: 0.7,
                delay: 0.2,
                ease: "power2.out",
                stagger: 0.1,
            });

            // Product table animation
            gsap.from(productTableRef.current, {
                opacity: 0,
                scale: 0.95,
                duration: 0.8,
                delay: 0.4,
                ease: "power2.out",
            });

            // Warning div animation
            if (showWarningDiv) {
                gsap.fromTo(
                    warningDivRef.current,
                    { x: "100%" },
                    { x: 0, duration: 0.5, ease: "power2.out" }
                );
            }
        });

        return () => ctx.revert();
    }, [showWarningDiv]);

    return (
        <div className="rounded-xl space-y-6 relative">
            {/* Header */}
            <div
                ref={headerRef}
                className="relative mt-3 bg-white p-3 rounded-md flex justify-between items-center"
            >
                <div>
                    <i className="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
                    <strong className="text-lg">Tạo thông tin flashsale</strong>
                    <p className="text-sm text-gray-600 mt-1">
                        Cung cấp các thông tin cơ bản để thêm một chương trình mới
                    </p>
                </div>
                <button
                    className="px-2 py-2 border font-semibold rounded-md shadow transition duration-200"
                    onClick={() => {
                        result.data = danhSachChon;
                        saveFlashSale(result)
                            .then(() => toast.success("Lưu thông tin thành công"))
                            .catch((e) =>
                                toast.error(e?.response?.data?.message || "Lưu thông tin thất bại")
                            );
                    }}
                >
                    {id != null ? "+ Cập nhật" : "+ Thêm"}
                </button>
            </div>

            {/* Ngày & Giờ */}
            <div ref={dateTimeRef} className="grid grid-cols-2 md:grid-cols-4 gap-4 bg-white p-3">
                <div>
                    <label className="flex items-center text-sm text-gray-600 mb-1 font-medium text-gray-900">
                        <i className="fas fa-calendar-alt text-blue-500 mr-2 bg-blue-100 p-2 rounded-sm"></i> Ngày bắt đầu
                    </label>
                    <div className="border mt-2 rounded p-1">
                        <input
                            value={result.ngayBatDau}
                            onChange={(e) =>
                                setResult((prev) => ({
                                    ...prev,
                                    ngayBatDau: e.target.value,
                                }))
                            }
                            type="datetime-local"
                            className={`input-style outline-none w-full px-2 py-1 rounded cursor-pointer ${
                                id != null ? "pointer-events-none blur-[1px]" : ""
                            }`}
                        />
                    </div>
                </div>
                <div>
                    <label className="flex items-center text-sm text-gray-600 mb-1 font-medium text-gray-900">
                        <i className="fas fa-calendar-check text-green-500 mr-2 bg-green-100 p-2 rounded-sm"></i> Ngày kết thúc
                    </label>
                    <div className="border mt-2 rounded p-1">
                        <input
                            value={result.ngayKetThuc}
                            onChange={(e) =>
                                setResult((prev) => ({
                                    ...prev,
                                    ngayKetThuc: e.target.value,
                                }))
                            }
                            type="datetime-local"
                            className="input-style outline-none w-full px-2 py-1 rounded cursor-pointer"
                        />
                    </div>
                </div>
                <div>
                    <label className="flex items-center text-sm text-gray-600 mb-1 font-medium text-gray-900">
                        <i className="fas fa-clock text-violet-500 mr-2 bg-violet-100 p-2 rounded-sm"></i> Giờ bắt đầu
                    </label>
                    <div className="border mt-2 rounded p-1">
                        <input
                            value={result.thoiGianBatDau}
                            onChange={(e) =>
                                setResult((prev) => ({
                                    ...prev,
                                    thoiGianBatDau: e.target.value,
                                }))
                            }
                            type="time"
                            className="input-style outline-none w-full px-2 py-1 rounded cursor-pointer"
                        />
                    </div>
                </div>
                <div>
                    <label className="flex items-center text-sm text-gray-600 mb-1 font-medium text-gray-900">
                        <i className="fas fa-clock text-yellow-500 mr-2 p-2 bg-yellow-100 rounded-sm"></i> Giờ kết thúc
                    </label>
                    <div className="border mt-2 rounded p-1">
                        <input
                            value={result.thoiGianKetThuc}
                            onChange={(e) =>
                                setResult((prev) => ({
                                    ...prev,
                                    thoiGianKetThuc: e.target.value,
                                }))
                            }
                            type="time"
                            className="input-style outline-none w-full px-2 py-1 rounded cursor-pointer"
                        />
                    </div>
                </div>
            </div>

            {/* Sản phẩm */}
            <div ref={productTableRef} className="bg-white p-4 rounded-lg">
                <div className="flex justify-between items-center mb-4">
                    <strong className="text-lg">
                        <i className="fa-solid fa-boxes-stacked mr-1 text-pink-500 bg-pink-100 p-1 mr-2"></i> Sản phẩm tham gia flashsale
                    </strong>
                    <button
                        onClick={() => setOpenPickProduct(true)}
                        className="px-2 py-2 border font-semibold rounded-md shadow transition duration-200"
                    >
                        Thêm sản phẩm
                    </button>
                </div>

                <div className="overflow-x-auto max-h-[50vh]">
                    <table className="w-full table-auto border border-gray-300 text-sm rounded-md">
                        <thead className="bg-gray-200 text-gray-700">
                            <tr>
                                <th className="px-2 text-center py-2 text-left min-w-[200px]">Phân loại hàng</th>
                                <th className="px-2 text-center py-2 text-center min-w-[60px]">Giá gốc</th>
                                <th className="px-2 text-center py-2 text-center min-w-[60px]">Giá đã giảm</th>
                                <th className="px-2 text-center py-2 text-center min-w-[70px]">Khuyến mãi (%)</th>
                                <th className="px-2 text-center py-2 text-center min-w-[80px]">Số lượng khuyến mãi</th>
                                {id != null ? (
                                    <th className="px-2 py-2 text-center min-w-[140px] font-semibold">
                                        Số lượng đã dùng
                                    </th>
                                ) : null}
                                <th className="px-2 py-2 text-center min-w-[60px]">Kho hàng</th>
                            </tr>
                        </thead>
                        <tbody>
                            {danhSachChon?.map((dk, index) => (
                                <>
                                    <tr key={`sp-${index}`} className="bg-white shadow-sm">
                                        <td colSpan={id == null ? 5 : 6} className="px-4 py-3">
                                            <div className="flex items-center gap-3">
                                                <img
                                                    src={dk.hinhAnh}
                                                    className="w-[50px] h-[50px] object-cover rounded"
                                                    alt="ảnh bìa"
                                                />
                                                <p className="font-medium text-gray-800">{dk?.ten}</p>
                                            </div>
                                        </td>
                                        <td className="px-4 py-3">
                                            <div className="flex justify-end items-center">
                                                <button
                                                    onClick={() => {
                                                        let a = danhSachChon.filter((data) => data.id !== dk.id);
                                                        setDanhSachChon(a);
                                                    }}
                                                    className="p-2 rounded transition-colors "
                                                    aria-label="Xóa sản phẩm"
                                                    title="Xóa"
                                                >
                                                    <i className="fa-solid fa-eraser p-2 "></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                    {dk?.bienThe?.map((bt, btIndex) => (
                                        <tr
                                            key={`bt-${index}-${btIndex}`}
                                            className="hover:bg-gray-50 border-t bg-white"
                                        >
                                            <td className="px-3 py-2">
                                                <div className="pl-5">
                                                    <div className="flex flex-row items-center pl-5">
                                                        <img
                                                            src={bt.hinhAnh}
                                                            className="w-[50px] h-[50px] object-cover rounded"
                                                            alt="ảnh bìa"
                                                        />
                                                        {((bt?.danhSachFlasSale != null && bt?.danhSachFlasSale?.length != 0)|| bt?.dealPhu?.length !== 0 || bt.dealChinh?.length !== 0) ? (
                                                            <svg
                                                                className="rounded-md ml-2 pt-3 duration-500 group-hover:rotate-[360deg] group-hover:scale-110 w-[8%] cursor-pointer"
                                                                viewBox="0 0 24 24"
                                                                fill="red"
                                                                xmlns="http://www.w3.org/2000/svg"
                                                                onClick={() => {
                                                                    setidchon(dk.id);
                                                                    toggleWarningDiv(bt.danhSachFlasSale);
                                                                     setdealtrung({
                                                                        data: bt.dealChinh,
                                                                        dataPhu: bt.dealPhu,
                                                                        flash:bt.danhSachFlasSale
                                                                    });
                                                                }}
                                                            >
                                                                <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z"></path>
                                                            </svg>
                                                        ) : null}
                                                    </div>
                                                    <p className="pl-5">{bt?.ten}</p>
                                                </div>
                                            </td>
                                            <td className="px-3 py-2 text-center">
                                                <p className="p-2  rounded-md">
                                                    {formatToVND(bt.gia)}
                                                </p>
                                            </td>
                                            <td className="px-3 py-2 text-center">
                                                <div className="rounded p-2  rounded-md">
                                                    {formatToVND(
                                                        Math.ceil(
                                                            bt.gia * (1 - (isNaN(parseFloat(bt.giaGiam)) ? 0 : parseFloat(bt.giaGiam)) / 100)
                                                        )
                                                    )}
                                                </div>
                                            </td>
                                            <td className="px-3 py-2 text-center">
                                                <div className="border border-gray-200 rounded p-1">
                                                    <input
                                                        type="number"
                                                        className={`input-style text-center outline-none w-auto rounded-sm ${
                                                            !bt?.giaGiam || bt.giaGiam < 0 || bt.giaGiam > 100
                                                                ? "border-red-300"
                                                                : "border-gray-200"
                                                        }`}
                                                        defaultValue={bt?.giaGiam || 0}
                                                        onChange={(e) => {
                                                            const newGiaGiam = parseFloat(e.target.value);
                                                            if (newGiaGiam <= 99) {
                                                                let a = [...danhSachChon];
                                                                a[index] = { ...a[index] };
                                                                a[index].bienThe = [...a[index].bienThe];
                                                                a[index].bienThe[btIndex] = {
                                                                    ...a[index].bienThe[btIndex],
                                                                    giaGiam: newGiaGiam,
                                                                };
                                                                setDanhSachChon(a);
                                                            }
                                                        }}
                                                    />
                                                </div>
                                            </td>
                                            <td className="px-3 py-2 text-center">
                                                <div className="border border-gray-200 rounded p-1">
                                                    <input
                                                        type="number"
                                                        className={`input-style text-center outline-none w-auto rounded-sm ${
                                                            !bt?.soLuongKhuyenMai || bt.soLuongKhuyenMai === 0
                                                                ? "border-red-300"
                                                                : "border-gray-200"
                                                        }`}
                                                        onChange={(e) => {
                                                            const newSoLuongKhuyenMai = parseFloat(e.target.value);
                                                            if (newSoLuongKhuyenMai <= 99) {
                                                                let a = [...danhSachChon];
                                                                a[index] = { ...a[index] };
                                                                a[index].bienThe = [...a[index].bienThe];
                                                                a[index].bienThe[btIndex] = {
                                                                    ...a[index].bienThe[btIndex],
                                                                    soLuongKhuyenMai: newSoLuongKhuyenMai,
                                                                };
                                                                setDanhSachChon(a);
                                                            }
                                                        }}
                                                        defaultValue={bt?.soLuongKhuyenMai || 0}
                                                    />
                                                </div>
                                            </td>
                                            {id != null ? (
                                                <td className="px-3 py-2 text-center">
                                                    {bt?.soLuongKhuyenMai - bt?.soLuongConLai}
                                                </td>
                                            ) : null}
                                            <td className="px-3 py-2 text-center">
                                                <div className="rounded p-1">{bt?.soLuongKho || 0} sản phẩm</div>
                                            </td>
                                        </tr>
                                    ))}
                                </>
                            ))}
                            {danhSachChon?.length === 0 ? (
                                <tr className="text-center">
                                    <td colSpan={6}>
                                        <div className="w-full flex justify-center">
                                            <img
                                                className="w-44"
                                                src="https://media.istockphoto.com/id/861576608/vi/vec-to/bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-empty-shopping-bag-m%E1%BA%ABu-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-vector-online-business.jpg?s=612x612&w=0&k=20&c=-6p0qVyb_p8VeHVu1vJVaDHa6Wd_mCjMkrFBTlQdehI="
                                                alt="Giỏ hàng trống"
                                            />
                                        </div>
                                    </td>
                                </tr>
                            ) : null}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Modal chọn sản phẩm */}
            {openPickProduct && (
                <Modal setOpen={setOpenPickProduct}>
                    <PickProduct
                        danhSachChon={danhSachChon}
                        filter={[]}
                        setDanhSachChon={setDanhSachChon}
                        task="flash"
                    />
                </Modal>
            )}

            {/* Warning Div */}
            {showWarningDiv && (
                <div
                    ref={warningDivRef}
                    className="fixed top-0 right-0 h-full w-[40%] bg-white shadow-xl rounded-l-lg p-6 z-50 transform transition-transform duration-500 ease-in-out"
                >
                    <div className="flex justify-between items-center mb-4">
                        <h3 className="text-xl font-semibold text-gray-800">Lưu ý</h3>
                        <button
                            onClick={toggleWarningDiv}
                            className="text-gray-500 hover:text-gray-700 text-2xl font-bold transition-colors duration-200"
                        >
                            ×
                        </button>
                    </div>
                    <p className="text-gray-600 text-sm leading-relaxed mb-4">
                        Sản phẩm này đã được thêm vào một Flash Sale khác. Vui lòng kiểm tra lại thông tin Flash Sale hiện tại trước khi tiếp tục.
                    </p>
                    <div className="overflow-auto max-h-[60%]">
                        <table className="min-w-full divide-y divide-gray-200 text-sm text-left">
                            <thead className="bg-gray-100 text-gray-700 font-semibold">
                                <tr>
                                    <th className="px-4 py-2">Ngày bắt đầu</th>
                                    <th className="px-4 py-2">Ngày kết thúc</th>
                                    <th className="px-4 py-2">Giờ bắt đầu</th>
                                    <th className="px-4 py-2">Giờ kết thúc</th>
                                    <th className="px-4 py-2">Tỉ lệ giảm</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {flash?.map((data, index) => (
                                    <tr
                                        key={index}
                                        className="group relative hover:bg-gray-50 cursor-pointer transition"
                                    >
                                        <td className="px-4 py-2">{dinhDangNgay(data.thoiGianChay)}</td>
                                        <td className="px-4 py-2">{dinhDangNgay(data.thoiGianNgung)}</td>
                                        <td className="px-4 py-2">{data.thoiDiemBatDau}</td>
                                        <td className="px-4 py-2">{data.thoiDiemKetThuc}</td>
                                        <td className="px-4 py-2">{data.giaTriGiam} %</td>
                                        <td
                                            className="absolute top-0 left-0 w-full h-full z-10 flex items-center justify-center 
                                            bg-white/70 backdrop-blur-[1px] opacity-0 group-hover:opacity-100 
                                            transition-opacity duration-300 ease-in-out"
                                        >
                                            <div
                                                onClick={() => {
                                                    navigate(`/admin/marketing/flash/update?id=${data.id}`);
                                                }}
                                                className="bg-white border border-blue-700 rounded shadow-lg px-4 py-2 text-sm"
                                            >
                                                Xem chi tiết
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <section className="mb-8">
                            <h4 className="text-lg font-semibold mb-3 text-violet-700">Vai trò deal chính</h4>
                            <div className="overflow-x-auto">
                                <table className="w-full text-sm text-left text-gray-800 border border-collapse">
                                    <thead className="bg-gray-200 font-semibold">
                                        <tr>
                                            <th className="p-2 border">STT</th>
                                            <th className="p-2 border">Thời gian chạy deal</th>
                                            <th className="p-2 border">Thời gian ngưng deal</th>
                                            <th className="p-2 border">Số lượt giới hạn</th>
                                            <th className="p-2 border">Số lượt đã dùng</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {dealtrung?.data?.length === 0 ? (
                                            <tr>
                                                <td colSpan={5} className="text-center italic text-gray-500 py-4">
                                                    Không có dữ liệu
                                                </td>
                                            </tr>
                                        ) : (
                                            dealtrung?.data?.map((data, index) => (
                                                <tr key={index} className="hover:bg-gray-50">
                                                    <td className="p-2 border">{index + 1}</td>
                                                    <td className="p-2 border">{dinhDangNgay(data.thoiGianChay)}</td>
                                                    <td className="p-2 border">{dinhDangNgay(data.thoiGianNgung)}</td>
                                                    <td className="p-2 border">{data.soLuotGioiHan}</td>
                                                    <td className="p-2 border">{data.soLuongDaDung}</td>
                                                </tr>
                                            ))
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                        <section>
                            <h4 className="text-lg font-semibold mb-3 text-violet-700">Vai trò deal phụ (giảm giá)</h4>
                            <div className="overflow-x-auto">
                                <table className="w-full text-sm text-left text-gray-800 border border-collapse">
                                    <thead className="bg-gray-200 font-semibold">
                                        <tr>
                                            <th className="p-2 border">STT</th>
                                            <th className="p-2 border">Thời gian chạy deal</th>
                                            <th className="p-2 border">Thời gian ngưng deal</th>
                                            <th className="p-2 border">Số lượt giới hạn</th>
                                            <th className="p-2 border">Số lượt đã dùng</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {dealtrung?.dataPhu?.length === 0 ? (
                                            <tr>
                                                <td colSpan={5} className="text-center italic text-gray-500 py-4">
                                                    Không có dữ liệu
                                                </td>
                                            </tr>
                                        ) : (
                                            dealtrung?.dataPhu?.map((data, index) => (
                                                <tr key={index} className="hover:bg-gray-50">
                                                    <td className="p-2 border">{index + 1}</td>
                                                    <td className="p-2 border">{dinhDangNgay(data.thoiGianChay)}</td>
                                                    <td className="p-2 border">{dinhDangNgay(data.thoiGianNgung)}</td>
                                                    <td className="p-2 border">{data.soLuotGioiHan}</td>
                                                    <td className="p-2 border">{data.soLuongDaDung}</td>
                                                </tr>
                                            ))
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </section>
                    </div>
                </div>
            )}
        </div>
    );
}

export { AddFlash };
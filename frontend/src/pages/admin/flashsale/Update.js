import { useEffect, useRef, useState } from "react";
import { PickProduct } from "../../../components/admin/PickProduct";
import Modal from "../../../components/commons/modal";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../../utils/Format";
import { Tooltip } from "../../../components/commons/ToolTip";
import { validateDuLieu } from "../../../utils/Vadilators/FlashSaleCreateVadilator";
import { getFlash, getIDBienThe, saveFlashSale, updateFlash } from "../../../services/FlashSaleService";
import { useNavigate, useSearchParams } from "react-router-dom";
import { update } from "../../../services/ThongSoService";
import gsap from "gsap";

function UpdateFlash() {
    const [openPickProduct, setOpenPickProduct] = useState(false);
    const [danhSachChon, setDanhSachChon] = useState([]);
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const id = searchParams.get("id");
    const [flashOld, setFlashOld] = useState([]);
    const [flash, setFlash] = useState([]);
    const [load, setLoad] = useState(false);
    const [idBienThe, setIdBienThe] = useState([]);
    const [result, setResult] = useState({
        ngayBatDau: "",
        ngayKetThuc: "",
        thoiGianBatDau: "",
        thoiGianKetThuc: "",
        data: []
    });
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
        getIDBienThe(id).then(data => setIdBienThe(data));
        if (id != null && id > 0) {
            getFlash(id).then((data) => {
                setResult(data);
                setDanhSachChon(data.data);
                setFlashOld(data.data);
            }).catch(() => {
                toast.error("Flash sale không tồn tại");
                navigate(-1);
            });
        }
    }, []);

    // Hàm kiểm tra thời gian
    function isPastTime(timeString) {
        const targetTime = new Date(timeString);
        const now = new Date();
        return now > targetTime;
    }

    // Hàm kiểm tra xem Flash Sale đã bắt đầu chưa
    function hasStarted(startTime) {
        const start = new Date(startTime);
        const now = new Date();
        return now >= start;
    }

    // Kiểm tra trạng thái của Flash Sale
    const isFlashSaleEnded = isPastTime(result.ngayKetThuc);
    const isFlashSaleStarted = hasStarted(result.ngayBatDau);

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
        <div
            className={`rounded-xl space-y-6 relative ${result.canUpdate==false ? "pointer-events-none" : ""}`}
        >
            {/* Header */}
            <div
                ref={headerRef}
                className="relative mt-3 bg-white p-3 rounded-md flex justify-between items-center"
            >
                <div>
                    <i className="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
                    <strong className="text-lg">Cập nhật thông tin FlashSale</strong>
                    <p className="text-sm text-gray-600 mt-1">
                        Cung cấp các thông tin cơ bản để cập nhật chương trình Flash Sale
                    </p>
                </div>
                <button
                    onClick={() => {
                        result.data = danhSachChon;
                        updateFlash(result, id)
                            .then(() => toast.success("Lưu thông tin thành công"))
                            .catch((e) => toast.error(e?.response?.data?.message || "Lưu thông tin thất bại"));
                    }}
                    className={`${result.canUpdate==false ? "bg-gray-300" : ""} px-2 border py-2 font-semibold rounded-md shadow transition duration-200`}
                >
                    Cập nhật
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
                            onChange={(e) => {
                                setResult((prev) => ({
                                    ...prev,
                                    ngayBatDau: e.target.value
                                }));
                            }}
                            type="datetime-local"
                            className={`input-style outline-none w-full px-2 py-1 rounded cursor-pointer `}
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
                            onChange={(e) => {
                                setResult((prev) => ({
                                    ...prev,
                                    ngayKetThuc: e.target.value
                                }));
                            }}
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
                            onChange={(e) => {
                                setResult((prev) => ({
                                    ...prev,
                                    thoiGianBatDau: e.target.value
                                }));
                            }}
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
                            onChange={(e) => {
                                setResult((prev) => ({
                                    ...prev,
                                    thoiGianKetThuc: e.target.value
                                }));
                            }}
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
                        <i className="fa-solid fa-boxes-stacked mr-1 text-pink-500 bg-pink-100 p-1 mr-2"></i> Sản phẩm tham gia Flash Sale
                    </strong>
                    <button
                        onClick={() => setOpenPickProduct(true)}
                        className={`${result.canUpdate==false ? "bg-gray-300" : ""} px-2 py-2 border font-semibold rounded-md shadow transition duration-200`}
                    >
                        Thêm sản phẩm
                    </button>
                </div>

                <div className="overflow-x-auto max-h-[50vh]">
                    <table className="w-full table-auto border border-gray-300 text-sm rounded-md">
                        <thead className="bg-gray-200 text-gray-700">
                            <tr>
                                <th className="px-2 py-2 text-left min-w-[200px]">Phân loại hàng</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Giá gốc</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Giá đã giảm</th>
                                <th className="px-2 py-2 text-center min-w-[70px]">Khuyến mãi (%)</th>
                                <th className="px-2 py-2 text-center min-w-[80px]">Số lượng khuyến mãi</th>
                                <th className="px-2 py-2 text-center min-w-[140px]">Số lượng đã dùng</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Kho hàng</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Hoạt Động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {danhSachChon?.map((dk, index) => (
                                <>
                                    <tr key={`sp-${index}`} className="bg-white shadow-sm">
                                        <td colSpan={7} className="px-4 py-3">
                                            {dk.notUpdate ? (
                                                <div className="absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[2px]">
                                                    <div className="bg-white bg-opacity-80 border-2 border-red-500 text-red-500 font-semibold px-4 py-2 rounded">
                                                        Không còn sử dụng
                                                    </div>
                                                </div>
                                            ) : null}
                                            <div className="flex items-center gap-3">
                                                <img src={dk.hinhAnh} className="w-[50px] h-[50px] object-cover rounded" alt="ảnh bìa" />
                                                <p className="font-medium text-gray-800">{dk?.ten}</p>
                                            </div>
                                        </td>
                                        <td className="flex items-center justify-start px-4 py-3">
                                            {!flashOld.some(item => item.id === dk.id) ? (
                                                <button
                                                    onClick={() => {
                                                        let a = danhSachChon.filter(data => data.id !== dk.id);
                                                        setDanhSachChon(a);
                                                    }}
                                                    className="p-2 rounded transition-colors text-violet-500 bg-violet-100"
                                                    aria-label="Xóa sản phẩm"
                                                    title="Xóa"
                                                >
                                                    <i className="fa-solid fa-eraser p-2"></i>
                                                </button>
                                            ) : null}
                                        </td>
                                    </tr>
                                    {dk?.bienThe?.map((bt, btIndex) => (
                                        <tr key={`bt-${index}-${btIndex}`} className="hover:bg-gray-50 border-t bg-white">
                                            <td className="px-3 py-2">
                                                <div className="pl-5">
                                                    <div className="flex flex-row items-center pl-5">
                                                        <img src={bt.hinhAnh} className="w-[50px] h-[50px] object-cover rounded" alt="ảnh bìa" />
                                                        {bt?.danhSachFlasSale != null && bt?.danhSachFlasSale?.length != 0 ? (
                                                            <svg
                                                                className="ml-2 pt-3 duration-500 group-hover:rotate-[360deg] group-hover:scale-110 w-[8%] cursor-pointer"
                                                                viewBox="0 0 24 24"
                                                                fill="red"
                                                                xmlns="http://www.w3.org/2000/svg"
                                                                onClick={() => {
                                                                    toggleWarningDiv(bt.danhSachFlasSale);
                                                                }}
                                                            >
                                                                <path
                                                                    d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z"
                                                                ></path>
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
                                                    {formatToVND(bt.gia * (1 - ((isNaN(parseFloat(bt.giaGiam)) ? 0 : parseFloat(bt.giaGiam)) / 100)))}
                                                </div>
                                            </td>
                                            <td className="px-3 py-2 text-center">
                                                <div className="border border-gray-200 rounded p-1">
                                                    <input
                                                        min={0}
                                                        max={100}
                                                        type="number"
                                                        className={`input-style text-center outline-none w-auto rounded-sm ${
                                                            bt?.soLuongKho > 0
                                                                ? (!bt?.giaGiam || bt.soLuongKhuyenMai === 0 ? "border-red-300" : "border-gray-200")
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
                                                            bt?.soLuongKho > 0
                                                                ? (!bt?.giaGiam || bt.soLuongKhuyenMai === 0 ? "border-red-300" : "border-gray-200")
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
                                                        min={bt?.soLuongConLai}
                                                        max={1000}
                                                        defaultValue={bt?.soLuongKhuyenMai || 0}
                                                    />
                                                </div>
                                            </td>
                                            <td className="px-3 py-2 text-center">{bt?.soLuongConLai}</td>
                                            <td className="px-3 py-2 text-center">
                                                <div className="rounded p-1">{bt?.soLuongKho || 0} sản phẩm</div>
                                            </td>
                                            <td className="text-white text-md">
                                                {idBienThe.some(item => item == bt?.id) ? (
                                                    <button
                                                        onClick={() => {
                                                            bt.conSuDung = !bt.conSuDung;
                                                            setLoad(!load);
                                                        }}
                                                        className={`${bt.conSuDung ? "bg-green-100 text-green-500" : "bg-gray-300"} pl-2 pr-2 pt-1 pb-1 rounded-sm ${
                                                            bt.giaGiam > 0 && bt.soLuongKhuyenMai > 0 ? "" : "blur-[1px] pointer-events-none"
                                                        }`}
                                                    >
                                                        <strong>Hoạt động</strong>
                                                    </button>
                                                ) : null}
                                            </td>
                                        </tr>
                                    ))}
                                </>
                            ))}
                            {danhSachChon?.length == 0 ? (
                                <tr className="text-center">
                                    <td colSpan="8">
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
                    <PickProduct danhSachChon={danhSachChon} filter={flashOld} setDanhSachChon={setDanhSachChon} />
                </Modal>
            )}

            {/* Warning Div */}
            {showWarningDiv && (
                <div
                    ref={warningDivRef}
                    className="fixed top-0 right-0 h-full w-[40%] bg-white shadow-xl rounded-l-lg p-6 z-50 transform transition-transform duration-500 ease-in-out"
                    style={{ transform: showWarningDiv ? "translateX(0)" : "translateX(100%)" }}
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
                                    <tr key={index} className="hover:bg-gray-50">
                                        <td className="px-4 py-2">{dinhDangNgay(data.thoiGianChay)}</td>
                                        <td className="px-4 py-2">{dinhDangNgay(data.thoiGianNgung)}</td>
                                        <td className="px-4 py-2">{data.thoiDiemBatDau}</td>
                                        <td className="px-4 py-2">{data.thoiDiemKetThuc}</td>
                                        <td className="px-4 py-2">{data.giaTriGiam} %</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}

export { UpdateFlash };
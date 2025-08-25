import { useEffect, useRef, useState } from "react";
import { PickProduct } from "../../../components/admin/PickProduct";
import Modal from "../../../components/commons/modal";
import { dinhDangNgay, formatToVND } from "../../../utils/Format";
import { getDeal, saveDeal, updateDeal } from "../../../services/DealService";
import { toast } from "react-toastify";
import { useSearchParams } from "react-router-dom";
import gsap from "gsap";

function isPastTime(timeString) {
    const targetTime = new Date(timeString);
    const now = new Date();
    return now > targetTime;
}

function UpdateDeal() {
    const [danhSachChon, setDanhSachChon] = useState([]);
    const [danhSachChonPhu, setDanhSachChonPhu] = useState([]);
    const [open, setOpen] = useState(false);
    const [openDealPhu, setOpenDealPhu] = useState(false);
    const [opentrung, setopentrung] = useState(false);
    const [searchParams] = useSearchParams();
    const [load, setLoad] = useState(false);
    const [dealtrung, setdealtrung] = useState({ data: [], dataPhu: [] });
    const [result, setResult] = useState({
        tenChuongTrinh: "",
        ngayBatDau: "",
        ngayKetThuc: "",
        thoiGianBatDau: "",
        thoiGianKetThuc: "",
        data: [],
        soLuongGioiHan: 0,
        soLuongDaDung: 0
    });
    const [expandedMain, setExpandedMain] = useState({});
    const [expandedSecondary, setExpandedSecondary] = useState({});

    const headerRef = useRef(null);
    const inputRef = useRef(null);
    const productTableRef = useRef(null);
    const warningDivRef = useRef(null);

    const id = searchParams.get("id");

    useEffect(() => {
        getDeal(id).then((d) => {
            setResult(d);
            setDanhSachChon(d.data);
            setDanhSachChonPhu(d.dataPhu);
        }).catch((e) => {
            toast.error(e?.response?.data?.message || "Không tìm thấy deal");
        });
    }, []);

    useEffect(() => {
        const ctx = gsap.context(() => {
            gsap.from(headerRef.current, {
                opacity: 0,
                y: -30,
                duration: 0.6,
                ease: "power2.out",
            });

            gsap.from(inputRef.current.children, {
                opacity: 0,
                y: 20,
                duration: 0.7,
                delay: 0.2,
                ease: "power2.out",
                stagger: 0.1,
            });

            gsap.from(productTableRef.current, {
                opacity: 0,
                scale: 0.95,
                duration: 0.8,
                delay: 0.4,
                ease: "power2.out",
            });

            if (opentrung) {
                gsap.fromTo(
                    warningDivRef.current,
                    { x: "100%" },
                    { x: 0, duration: 0.5, ease: "power2.out" }
                );
            }
        });

        return () => ctx.revert();
    }, [opentrung]);

    const toggleMainProduct = (index) => {
        setExpandedMain((prev) => ({
            ...prev,
            [index]: !prev[index]
        }));
    };

    const toggleSecondaryProduct = (index) => {
        setExpandedSecondary((prev) => ({
            ...prev,
            [index]: !prev[index]
        }));
    };

    return (
        <div className={`rounded-xl space-y-6 relative ${result.canUpdate==false ? "pointer-events-none" : ""}`}>
            <div
                ref={headerRef}
                className="relative mt-3 bg-white p-3 rounded-md flex justify-between items-center"
            >
                <div>
                    <i className="fa-solid fa-gift bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
                    <strong className="text-lg  text-violet-500">Cập nhật thông tin Deal</strong>
                    <p className="text-sm text-gray-600 mt-1">
                        Cung cấp các thông tin cơ bản để cập nhật chương trình Deal
                    </p>
                </div>
                <button
                    onClick={() => {
                        result.data = danhSachChon;
                        result.dataPhu = danhSachChonPhu;
                        updateDeal(result, id)
                            .then(() => toast.success("Cập nhật thông tin deal thành công"))
                            .catch((e) => {
                                toast.error(e?.response?.data?.message || "Cập nhật deal thất bại");
                            });
                    }}
                    className={`${result.canUpdate==false ? "bg-gray-300" : "border "} px-3 py-2 font-semibold rounded-md shadow transition duration-200`}
                >
                    Cập nhật
                </button>
            </div>

            <div ref={inputRef} className="grid grid-cols-1 md:grid-cols-3 gap-4 bg-white p-3 rounded-md">
                <div>
                    <label className="flex items-center text-sm text-gray-600 mb-1 font-medium text-gray-900">
                        <i className="fas fa-ticket-alt text-blue-500 mr-2 bg-blue-100 p-2 rounded-sm"></i> Số lượt giới hạn (Đã dùng: {result?.soLuongDaDung})
                    </label>
                    <div className="border mt-2 rounded-md p-1">
                        <input
                            min={result?.soLuongDaDung}
                            value={result.soLuongGioiHan}
                            onChange={(e) => {
                                setResult((prev) => ({
                                    ...prev,
                                    soLuongGioiHan: e.target.value
                                }));
                            }}
                            type="number"
                            className="input-style outline-none w-full px-2 py-1 rounded-md cursor-pointer"
                        />
                    </div>
                </div>
                <div>
                    <label className="flex items-center text-sm text-gray-600 mb-1 font-medium text-gray-900">
                        <i className="fas fa-calendar-alt text-blue-500 mr-2 bg-blue-100 p-2 rounded-sm"></i> Thời gian bắt đầu
                    </label>
                    <div className="border mt-2 rounded-md p-1">
                        <input
                            value={result.ngayBatDau}
                            onChange={(e) => {
                                const selectedDate = new Date(e.target.value);
                                const now = new Date();
                                if (!isPastTime(result.ngayBatDau) && selectedDate >= now) {
                                    setResult((prev) => ({
                                        ...prev,
                                        ngayBatDau: e.target.value,
                                    }));
                                }
                            }}
                            type="datetime-local"
                            min={new Date().toISOString().slice(0, 16)}
                            className={`input-style outline-none w-full px-2 py-1 rounded-md cursor-pointer`}
                        />
                    </div>
                </div>
                <div>
                    <label className="flex items-center text-sm text-gray-600 mb-1 font-medium text-gray-900">
                        <i className="fas fa-calendar-check text-blue-500 mr-2 bg-blue-100 p-2 rounded-sm"></i> Thời gian kết thúc
                    </label>
                    <div className="border mt-2 rounded-md p-1">
                        <input
                            value={result.ngayKetThuc}
                            onChange={(e) => {
                                setResult((prev) => ({
                                    ...prev,
                                    ngayKetThuc: e.target.value
                                }));
                            }}
                            type="datetime-local"
                            className="input-style outline-none w-full px-2 py-1 rounded-md cursor-pointer"
                        />
                    </div>
                </div>
            </div>

            <div ref={productTableRef} className="bg-white p-4 rounded-lg">
                <div className="flex justify-between items-center mb-4">
                    <strong className="text-lg">
                        <i className="fa-solid fa-boxes-stacked mr-1 text-violet-500 bg-violet-100 p-1 mr-2 rounded-sm"></i> Sản phẩm chính
                    </strong>
                    <button
                        onClick={() => setOpen(true)}
                        className={`${isPastTime(result.ngayKetThuc) ? "bg-gray-300" : "bg-violet-100 text-violet-500 hover:bg-violet-200"} px-3 py-2 font-semibold rounded-md shadow transition duration-200`}
                    >
                        Thêm sản phẩm
                    </button>
                </div>
                <div className="overflow-x-auto max-h-[50vh]">
                    <table className="w-full table-auto border border-gray-300 text-sm rounded-md">
                        <thead className="bg-gray-50 text-gray-700">
                            <tr>
                                <th className="px-2 py-2 text-left min-w-[200px]">Sản phẩm</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Giá</th>
                                <th className="px-2 py-2 text-center min-w-[120px]">Số lượng tối thiểu</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Số lượng kho</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Hoạt động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {danhSachChon?.length === 0 ? (
                                <tr>
                                    <td colSpan={5} className="text-center italic text-gray-500 py-4">
                                        Chưa thêm dữ liệu sản phẩm chính
                                    </td>
                                </tr>
                            ) : (
                                danhSachChon.map((d, index) => (
                                    <>
                                        <tr key={`sp-${index}`} className="bg-white shadow-sm">
                                            <td colSpan={5} className="px-4 py-3">
                                                <div className="flex items-center justify-between">
                                                    <div className="flex items-center">
                                                        <button
                                                            onClick={() => toggleMainProduct(index)}
                                                            className="mr-2 focus:outline-none"
                                                            aria-label={expandedMain[index] ? "Thu gọn" : "Mở rộng"}
                                                        >
                                                            <i className={`fa-solid fa-chevron-down transform transition-transform duration-300 ${expandedMain[index] ? "rotate-180" : ""}`}></i>
                                                        </button>
                                                        <img src={d.hinhAnh} className="w-[50px] h-[50px] object-cover rounded-md" alt="ảnh bìa" />
                                                        <p className="ml-2 font-medium text-gray-800">{d.ten}</p>
                                                    </div>
                                                    {d?.ready ? null : (
                                                        <button
                                                            onClick={() => {
                                                                danhSachChon.splice(index, 1);
                                                                setLoad(!load);
                                                            }}
                                                            className="p-2 rounded-md transition-colors "
                                                            aria-label="Xóa sản phẩm"
                                                            title="Xóa"
                                                        >
                                                            <i className="fa-solid fa-eraser p-2"></i>
                                                        </button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                        {expandedMain[index] && d.bienThe?.map((dd, btIndex) => (
                                            <tr key={`bt-${index}-${btIndex}`} className="hover:bg-gray-50 border-t bg-white">
                                                <td className="px-3 py-2">
                                                    <div className="pl-5 flex items-center">
                                                        <img src={dd.hinhAnh} className="w-[50px] h-[50px] object-cover rounded-md" alt="ảnh bìa" />
                                                        <div>
                                                            <p className="pl-3">{dd.ten}</p>
                                                            {dd?.dealPhu?.length !== 0 || dd.dealChinh?.length !== 0 ? (
                                                                <svg
                                                                    className="ml-2 pt-3 duration-500 group-hover:rotate-[360deg] group-hover:scale-110 w-[5%] cursor-pointer"
                                                                    viewBox="0 0 24 24"
                                                                    fill="red"
                                                                    xmlns="http://www.w3.org/2000/svg"
                                                                    onClick={() => {
                                                                        setdealtrung({
                                                                            data: dd.dealChinh,
                                                                            dataPhu: dd.dealPhu
                                                                        });
                                                                        setopentrung(true);
                                                                    }}
                                                                >
                                                                    <path
                                                                        d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z"
                                                                    ></path>
                                                                </svg>
                                                            ) : null}
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className="px-3 py-2 text-center">
                                                    <p className="p-2 rounded-md">
                                                        {formatToVND(dd.gia)}
                                                    </p>
                                                </td>
                                                <td className="px-3 py-2 text-center">
                                                    {(result?.soLuongDaDung <= 0 && dd?.ready === true) || dd?.ready === false || d?.ready === undefined ? (
                                                        <input
                                                            onChange={(e) => {
                                                                const newSoLuongKhuyenMai = parseFloat(e.target.value);
                                                                if (newSoLuongKhuyenMai <= 99) {
                                                                    let a = [...danhSachChon];
                                                                    a[index] = { ...a[index] };
                                                                    a[index].bienThe = [...a[index].bienThe];
                                                                    a[index].bienThe[btIndex] = {
                                                                        ...a[index].bienThe[btIndex],
                                                                        soLuongKhuyenMai: newSoLuongKhuyenMai,
                                                                        conSuDung: e.target.value == 0 ? false : true
                                                                    };
                                                                    setDanhSachChon(a);
                                                                }
                                                            }}
                                                            defaultValue={dd?.soLuongKhuyenMai || 0}
                                                            placeholder="Số lượng tối đa"
                                                            min={0}
                                                            className="outline-none border border-gray-200 pl-2 text-center rounded-md focus:ring-2 focus:ring-violet-400"
                                                            type="number"
                                                        />
                                                    ) : (
                                                        dd?.soLuongKhuyenMai
                                                    )}
                                                </td>
                                                <td className="px-3 py-2 text-center">{dd.soLuongKho}</td>
                                                <td className="px-3 py-2 text-center">
                                                    {dd?.ready ? (
                                                        <button
                                                            onClick={() => {
                                                                dd.conSuDung = !dd.conSuDung;
                                                                setLoad(!load);
                                                            }}
                                                            className={`${dd.conSuDung ? "bg-green-100 text-green-500" : "bg-gray-300"} pl-2 pr-2 pt-1 pb-1 rounded-md ${dd.soLuongKhuyenMai > 0 ? "" : "blur-[1px] pointer-events-none"}`}
                                                        >
                                                            <strong>Hoạt động</strong>
                                                        </button>
                                                    ) : null}
                                                </td>
                                            </tr>
                                        ))}
                                    </>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            <div className="bg-white p-4 rounded-lg">
                <div className="flex justify-between items-center mb-4">
                    <strong className="text-lg">
                        <i className="fa-solid fa-tags mr-1 text-violet-500 bg-violet-100 p-1 mr-2 rounded-sm"></i> Sản phẩm phụ (giảm giá)
                    </strong>
                    <button
                        onClick={() => setOpenDealPhu(true)}
                        className={`${isPastTime(result.ngayKetThuc) ? "bg-gray-300" : "bg-violet-100 text-violet-500 hover:bg-violet-200"} px-3 py-2 font-semibold rounded-md shadow transition duration-200`}
                    >
                        Thêm sản phẩm
                    </button>
                </div>
                <div className="overflow-x-auto max-h-[50vh]">
                    <table className="w-full table-auto border border-gray-300 text-sm rounded-md">
                        <thead className="bg-gray-50 text-gray-700">
                            <tr>
                                <th className="px-2 py-2 text-left min-w-[200px]">Sản phẩm</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Giá bán hiện tại</th>
                                <th className="px-2 py-2 text-center min-w-[70px]">Tỉ lệ giảm (%)</th>
                                <th className="px-2 py-2 text-center min-w-[120px]">Giảm tối đa trên đơn</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Số lượng kho</th>
                                <th className="px-2 py-2 text-center min-w-[60px]">Hoạt động</th>
                            </tr>
                        </thead>
                        <tbody>
                            {danhSachChonPhu?.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="text-center italic text-gray-500 py-4">
                                        Chưa thêm dữ liệu sản phẩm đi kèm
                                    </td>
                                </tr>
                            ) : (
                                danhSachChonPhu.map((d, index) => (
                                    <>
                                        <tr key={`sp-phu-${index}`} className="bg-white shadow-sm">
                                            <td colSpan={6} className="px-4 py-3">
                                                <div className="flex items-center justify-between">
                                                    <div className="flex items-center">
                                                        <button
                                                            onClick={() => toggleSecondaryProduct(index)}
                                                            className="mr-2 focus:outline-none"
                                                            aria-label={expandedSecondary[index] ? "Thu gọn" : "Mở rộng"}
                                                        >
                                                            <i className={`fa-solid fa-chevron-down transform transition-transform duration-300 ${expandedSecondary[index] ? "rotate-180" : ""}`}></i>
                                                        </button>
                                                        <img src={d.hinhAnh} className="w-[50px] h-[50px] object-cover rounded-md" alt="ảnh bìa" />
                                                        <p className="ml-2 font-medium text-gray-800">{d.ten}</p>
                                                    </div>
                                                    {d?.ready ? null : (
                                                        <button
                                                            onClick={() => {
                                                                danhSachChonPhu.splice(index, 1);
                                                                setLoad(!load);
                                                            }}
                                                            className="p-2 rounded-md transition-colors text-yellow-500 bg-yellow-100 hover:bg-yellow-200"
                                                            aria-label="Xóa sản phẩm"
                                                            title="Xóa"
                                                        >
                                                            <i className="fa-solid fa-eraser p-2"></i>
                                                        </button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                        {expandedSecondary[index] && d.bienThe?.map((dd, btIndex) => (
                                            <tr key={`bt-phu-${index}-${btIndex}`} className="hover:bg-gray-50 border-t bg-white">
                                                <td className="px-3 py-2">
                                                    {dd.notUpdate ? (
                                                        <div className="absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[2px]">
                                                            <div className="bg-white bg-opacity-80 border-2 border-red-500 text-red-500 font-semibold px-4 py-2 rounded-md">
                                                                Không còn sử dụng
                                                            </div>
                                                        </div>
                                                    ) : null}
                                                    <div className="pl-5 flex items-center">
                                                        <img src={dd.hinhAnh} className="w-[50px] h-[50px] object-cover rounded-md" alt="ảnh bìa" />
                                                        <div>
                                                            <p className="pl-3">{dd.ten}</p>
                                                            {dd?.dealPhu?.length !== 0 || dd.dealChinh?.length !== 0 ? (
                                                                <svg
                                                                    className="ml-2 pt-3 duration-500 group-hover:rotate-[360deg] group-hover:scale-110 w-[5%] cursor-pointer"
                                                                    viewBox="0 0 24 24"
                                                                    fill="red"
                                                                    xmlns="http://www.w3.org/2000/svg"
                                                                    onClick={() => {
                                                                        setdealtrung({
                                                                            data: dd.dealChinh,
                                                                            dataPhu: dd.dealPhu
                                                                        });
                                                                        setopentrung(true);
                                                                    }}
                                                                >
                                                                    <path
                                                                        d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z"
                                                                    ></path>
                                                                </svg>
                                                            ) : null}
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className="px-3 py-2 text-center">
                                                    <p className="p-2 rounded-md">
                                                        {formatToVND(dd.gia)}
                                                    </p>
                                                </td>
                                                <td className="px-3 py-2 text-center">
                                                    {(result?.soLuongDaDung <= 0 && dd?.ready === true) || dd?.ready === false || d?.ready === undefined ? (
                                                        <input
                                                            min={0}
                                                            max={100}
                                                            defaultValue={dd?.giaGiam || 0}
                                                            onChange={(e) => {
                                                                const newGiaGiam = parseFloat(e.target.value);
                                                                if (newGiaGiam <= 99) {
                                                                    let a = [...danhSachChonPhu];
                                                                    a[index] = { ...a[index] };
                                                                    a[index].bienThe = [...a[index].bienThe];
                                                                    a[index].bienThe[btIndex] = {
                                                                        ...a[index].bienThe[btIndex],
                                                                        giaGiam: newGiaGiam,
                                                                        conSuDung: (newGiaGiam == 0 || dd.soLuongKhuyenMai == 0) ? false : true
                                                                    };
                                                                    setDanhSachChonPhu(a);
                                                                }
                                                            }}
                                                            placeholder="Tỉ lệ giảm"
                                                            className="outline-none border border-gray-200 pl-2 text-center rounded-md focus:ring-2 focus:ring-violet-400"
                                                            type="number"
                                                        />
                                                    ) : (
                                                        dd?.giaGiam
                                                    )}
                                                </td>
                                                <td className="px-3 py-2 text-center">
                                                    {(result?.soLuongDaDung <= 0 && dd?.ready === true) || dd?.ready === false || d?.ready === undefined ? (
                                                        <input
                                                            min={0}
                                                            onChange={(e) => {
                                                                const newSoLuongKhuyenMai = parseFloat(e.target.value);
                                                                if (newSoLuongKhuyenMai <= 99) {
                                                                    let a = [...danhSachChonPhu];
                                                                    a[index] = { ...a[index] };
                                                                    a[index].bienThe = [...a[index].bienThe];
                                                                    a[index].bienThe[btIndex] = {
                                                                        ...a[index].bienThe[btIndex],
                                                                        soLuongKhuyenMai: newSoLuongKhuyenMai,
                                                                        conSuDung: (newSoLuongKhuyenMai == 0 || dd.giaGiam == 0) ? false : true
                                                                    };
                                                                    setDanhSachChonPhu(a);
                                                                }
                                                            }}
                                                            defaultValue={dd?.soLuongKhuyenMai || 0}
                                                            placeholder="Số lượng tối đa"
                                                            className="outline-none border border-gray-200 pl-2 text-center rounded-md focus:ring-2 focus:ring-violet-400"
                                                            type="number"
                                                        />
                                                    ) : (
                                                        dd?.soLuongKhuyenMai
                                                    )}
                                                </td>
                                                <td className="px-3 py-2 text-center">{dd.soLuongKho}</td>
                                                <td className="px-3 py-2 text-center">
                                                    {dd?.ready ? (
                                                        <button
                                                            onClick={() => {
                                                                dd.conSuDung = !dd.conSuDung;
                                                                setLoad(!load);
                                                            }}
                                                            className={`${dd.conSuDung ? "bg-green-100 text-green-500" : "bg-gray-300"} pl-2 pr-2 pt-1 pb-1 rounded-md ${(dd.giaGiam > 0 && dd.soLuongKhuyenMai > 0) ? "" : "blur-[1px] pointer-events-none"}`}
                                                        >
                                                            <strong>Hoạt động</strong>
                                                        </button>
                                                    ) : null}
                                                </td>
                                            </tr>
                                        ))}
                                    </>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {open && (
                <Modal setOpen={setOpen}>
                    <PickProduct danhSachChon={danhSachChon} filter={danhSachChonPhu || []} setDanhSachChon={setDanhSachChon} task={"deal"} />
                </Modal>
            )}

            {openDealPhu && (
                <Modal setOpen={setOpenDealPhu}>
                    <PickProduct danhSachChon={danhSachChonPhu} filter={danhSachChon} setDanhSachChon={setDanhSachChonPhu} task={"deal"} />
                </Modal>
            )}

            {opentrung && (
                <div
                    ref={warningDivRef}
                    className="fixed top-0 right-0 h-full w-[40%] bg-white shadow-xl rounded-l-lg p-6 z-50 transform transition-transform duration-500 ease-in-out"
                >
                    <div className="flex justify-between items-center mb-4">
                        <h3 className="text-xl font-semibold text-gray-800">Lưu ý</h3>
                        <button
                            onClick={() => setopentrung(false)}
                            className="text-gray-500 hover:text-gray-700 text-2xl font-bold transition-colors duration-200"
                        >
                            ×
                        </button>
                    </div>
                    <p className="text-gray-600 text-sm leading-relaxed mb-4">
                        Sản phẩm này đã được thêm vào một Deal khác. Vui lòng kiểm tra lại thông tin Deal hiện tại trước khi tiếp tục.
                    </p>
                    <div className="overflow-auto max-h-[60%]">
                        <section className="mb-8">
                            <p className="text-lg font-semibold mb-3 text-violet-700">Vai trò deal chính</p>
                            <table className="min-w-full text-sm text-left text-gray-800 border">
                                <thead className="bg-violet-100 font-semibold">
                                    <tr>
                                        <th className="py-2 px-3 border">STT</th>
                                        <th className="py-2 px-3 border">Thời gian chạy deal</th>
                                        <th className="py-2 px-3 border">Thời gian ngưng deal</th>
                                        <th className="py-2 px-3 border">Số lượt giới hạn</th>
                                        <th className="py-2 px-3 border">Số lượt đã dùng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {dealtrung?.data?.length === 0 ? (
                                        <tr>
                                            <td colSpan={5} className="text-center italic text-gray-500 py-4">Không có dữ liệu</td>
                                        </tr>
                                    ) : (
                                        dealtrung?.data?.map((data, index) => (
                                            <tr key={index} className="hover:bg-gray-50">
                                                <td className="py-2 px-3 border">{index + 1}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianChay)}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianNgung)}</td>
                                                <td className="py-2 px-3 border">{data.soLuotGioiHan}</td>
                                                <td className="py-2 px-3 border">{data.soLuongDaDung}</td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </section>
                        <section>
                            <p className="text-lg font-semibold mb-3 text-violet-700">Vai trò deal phụ (giảm giá)</p>
                            <table className="min-w-full text-sm text-left text-gray-800 border">
                                <thead className="bg-violet-100 font-semibold">
                                    <tr>
                                        <th className="py-2 px-3 border">STT</th>
                                        <th className="py-2 px-3 border">Thời gian chạy deal</th>
                                        <th className="py-2 px-3 border">Thời gian ngưng deal</th>
                                        <th className="py-2 px-3 border">Số lượt giới hạn</th>
                                        <th className="py-2 px-3 border">Số lượt đã dùng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {dealtrung?.dataPhu?.length === 0 ? (
                                        <tr>
                                            <td colSpan={5} className="text-center italic text-gray-500 py-4">Không có dữ liệu</td>
                                        </tr>
                                    ) : (
                                        dealtrung?.dataPhu?.map((data, index) => (
                                            <tr key={index} className="hover:bg-gray-50">
                                                <td className="py-2 px-3 border">{index + 1}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianChay)}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianNgung)}</td>
                                                <td className="py-2 px-3 border">{data.soLuotGioiHan}</td>
                                                <td className="py-2 px-3 border">{data.soLuongDaDung}</td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </section>
                    </div>
                </div>
            )}
        </div>
    );
}

export { UpdateDeal };
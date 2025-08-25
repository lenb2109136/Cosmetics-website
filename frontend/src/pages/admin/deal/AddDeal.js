import { useEffect, useRef, useState } from "react";
import { CustomCalendar } from "../../../components/commons/PickDateTime";
import Modal from "../../../components/commons/modal";
import { PickProduct } from "../../../components/admin/PickProduct";
import { dinhDangNgay, formatToVND } from "../../../utils/Format";
import { saveDeal } from "../../../services/DealService";
import { toast } from "react-toastify";
import gsap from "gsap";

function AddDeal() {
    const [bd, setbd] = useState("");
    const [danhSachChon, setDanhSachChon] = useState([]);
    const [danhSachChonPhu, setDanhSachChonPhu] = useState([]);
    const [open, setOpen] = useState(false);
    const [openDealPhu, setOpenDealPhu] = useState(false);
    const [opentrung, setopentrung] = useState(false);
    const [dealtrung, setdealtrung] = useState({ data: [], dataPhu: [],flash:[] });
    const [result, setResult] = useState({
        tenChuongTrinh: "sdsd",
        ngayBatDau: "",
        ngayKetThuc: "",
        thoiGianBatDau: "",
        thoiGianKetThuc: "",
        data: [],
        soLuongGioiHan: 0
    });

    const headerRef = useRef(null);
    const inputSectionRef = useRef(null);
    const productTableRef = useRef(null);
    const productPhuTableRef = useRef(null);
    const warningDivRef = useRef(null);
    const [flash,setFlash]=useState([])
    useEffect(() => {
        const ctx = gsap.context(() => {
            // Header animation
            gsap.from(headerRef.current, {
                opacity: 0,
                y: -30,
                duration: 0.6,
                ease: "power2.out",
            });

            // Input section animation
            gsap.from(inputSectionRef.current.children, {
                opacity: 0,
                y: 20,
                duration: 0.7,
                delay: 0.2,
                ease: "power2.out",
                stagger: 0.1,
            });

            // Product tables animation
            gsap.from([productTableRef.current, productPhuTableRef.current], {
                opacity: 0,
                scale: 0.95,
                duration: 0.8,
                delay: 0.4,
                ease: "power2.out",
                stagger: 0.2,
            });

            // Warning div animation
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

    return (
        <div className="p-4 space-y-6">
            {/* Header */}
            <div
                ref={headerRef}
                className="bg-white p-3 rounded-md flex justify-between items-center"
            >
                <div>
                    <i className="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
                    <strong className="text-lg text-violet-700">Thêm deal mới</strong>
                    <p className="text-sm text-gray-600 mt-1">
                        Cung cấp các thông tin cơ bản để thêm một chương trình deal mới
                    </p>
                </div>
                <button
                    onClick={() => {
                        result.data = danhSachChon;
                        result.dataPhu = danhSachChonPhu;
                        saveDeal(result)
                            .then(() => toast.success("Tạo Deal thành công"))
                            .catch((e) => toast.error(e?.response?.data?.message || "Tạo deal thất bại"));
                    }}
                    className="px-2 py-2 border font-semibold rounded-md shadow transition duration-200 hover:opacity-90"
                >
                    + Thêm deal
                </button>
            </div>

            {/* Input Section */}
            <div ref={inputSectionRef} className="flex flex-row gap-4 bg-white p-3 rounded-md">
    <div className="flex-1">
        <label className="flex items-center text-sm text-gray-600 mb-1 font-medium">
            <i className="fa-solid fa-ticket bg-blue-100 text-blue-500 p-2 rounded-sm mr-2"></i>
            Số lượt giới hạn
        </label>
        <input
            value={result.soLuongGioiHan}
            onChange={(e) => {
                setResult((prev) => ({
                    ...prev,
                    soLuongGioiHan: e.target.value
                }));
            }}
            type="number"
            className="w-full mt-2 border border-gray-300 rounded-md px-3 py-2 outline-none focus:ring-2 focus:ring-violet-400"
        />
    </div>
    <div className="flex-1">
        <label className="flex items-center text-sm text-gray-600 mb-1 font-medium">
            <i className="fa-solid fa-calendar-alt bg-blue-100 text-blue-500 p-2 rounded-sm mr-2"></i>
            Ngày bắt đầu
        </label>
        <input
            value={result.ngayBatDau}
            onChange={(e) => {
                setResult((prev) => ({
                    ...prev,
                    ngayBatDau: e.target.value
                }));
            }}
            type="datetime-local"
            className="w-full mt-2 border border-gray-300 rounded-md px-3 py-2 outline-none focus:ring-2 focus:ring-violet-400"
        />
    </div>
    <div className="flex-1">
        <label className="flex items-center text-sm text-gray-600 mb-1 font-medium">
            <i className="fa-solid fa-calendar-check bg-blue-100 text-blue-500 p-2 rounded-sm mr-2"></i>
            Ngày kết thúc
        </label>
        <input
            value={result.ngayKetThuc}
            onChange={(e) => {
                setResult((prev) => ({
                    ...prev,
                    ngayKetThuc: e.target.value
                }));
            }}
            type="datetime-local"
            className="w-full border mt-2 border-gray-300 rounded-md px-3 py-2 outline-none focus:ring-2 focus:ring-violet-400"
        />
    </div>
</div>

            {/* Sản phẩm chính */}
            <div ref={productTableRef} className="bg-white p-4 rounded-lg">
                <div className="flex justify-between items-center mb-4">
                    <strong className="text-lg">
                        <i className="fa-solid fa-boxes-stacked bg-pink-100 text-pink-500 p-2 rounded-sm mr-2"></i>
                        Sản phẩm chính
                    </strong>
                    <button
                        onClick={() => setOpen(true)}
                        className="px-2 py-2 border font-semibold rounded-md shadow transition duration-200 hover:opacity-90"
                    >
                        + Thêm sản phẩm
                    </button>
                </div>
                <div className="overflow-x-auto max-h-[50vh]">
                    <table className="w-full border-collapse text-sm">
                        <thead className="bg-gray-200 text-gray-700">
                            <tr>
                                <th className="p-3 border min-w-[200px] text-left">Sản phẩm</th>
                                <th className="p-3 border text-center min-w-[60px]">Giá</th>
                                <th className="p-3 border text-center min-w-[100px]">Số lượng tối thiểu đơn hàng</th>
                                <th className="p-3 border text-center min-w-[60px]">Số lượng kho</th>
                            </tr>
                        </thead>
                        <tbody>
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
                            ) : (
                                danhSachChon.map((d, index) => (
                                    <>
                                        <tr key={index} className="bg-gray-100">
                                            <td colSpan={5} className="p-3 border">
                                               <div className="flex justify-between">
                                                 <div className="flex items-center">
                                                    <img src={d.hinhAnh} className="m-2 w-[50px] h-[50px] object-cover rounded" alt={d.ten} />
                                                    <span className="pl-3">{d.ten}</span>
                                                </div>
                                                 <button
                                                        onClick={() => {
                                                            let a = danhSachChon.filter((data) => data.id !== d.id);
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
                                        {d.bienThe?.map((dd, btIndex) => (
                                            <tr key={btIndex} className="text-center hover:bg-gray-50 transition-all duration-200">
                                                <td className="p-3 border">
                                                    <div className="flex items-center pl-5">
                                                        <img src={dd.hinhAnh} className="m-2 w-[50px] h-[50px] object-cover rounded" alt={dd.ten} />
                                                        <span className="pl-3">{dd.ten}</span>
                                                        {(dd?.danhSachFlasSale?.length !== 0|| dd?.dealPhu?.length !== 0 || dd.dealChinh?.length !== 0) && (
                                                            <svg
                                                                className="ml-2 w-5 h-5 cursor-pointer text-red-500 hover:rotate-[360deg] hover:scale-110 transition-all duration-500"
                                                                viewBox="0 0 24 24"
                                                                fill="currentColor"
                                                                onClick={() => {
                                                                    setdealtrung({
                                                                        data: dd.dealChinh,
                                                                        dataPhu: dd.dealPhu,
                                                                        flash:dd.danhSachFlasSale
                                                                    });
                                                                    setopentrung(true);
                                                                }}
                                                            >
                                                                <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z" />
                                                            </svg>
                                                        )}
                                                    </div>
                                                </td>
                                                <td className="p-3 border">
                                                    <p className="p-2  rounded-md">{formatToVND(dd.gia)}</p>
                                                </td>
                                                <td className="p-3 border">
                                                    <input
                                                        onChange={(e) => {
                                                            const newSoLuongKhuyenMai = parseInt(e.target.value);
                                                            if (newSoLuongKhuyenMai <= 99) {
                                                                let a = [...danhSachChon];
                                                                a[index] = { ...a[index] };
                                                                a[index].bienThe = [...a[index].bienThe];
                                                                a[index].bienThe[btIndex] = {
                                                                    ...a[index].bienThe[btIndex],
                                                                    soLuongKhuyenMai: newSoLuongKhuyenMai
                                                                };
                                                                setDanhSachChon(a);
                                                            }
                                                        }}
                                                        defaultValue={dd?.soLuongKhuyenMai || 0}
                                                        placeholder="Số lượng giảm"
                                                        min={0}
                                                        type="number"
                                                        className="w-24 border border-gray-200 rounded px-2 py-1 outline-none focus:ring-2 focus:ring-violet-400"
                                                    />
                                                </td>
                                                <td className="p-3 border">
                                                    <p className="p-2 rounded">{dd.soLuongKho}</p>
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

            {/* Sản phẩm phụ */}
            <div ref={productPhuTableRef} className="bg-white p-4 rounded-lg">
                <div className="flex justify-between items-center mb-4">
                    <strong className="text-lg">
                        <i className="fa-solid fa-boxes-stacked bg-pink-100 text-pink-500 p-2 rounded-sm mr-2"></i>
                        Sản phẩm phụ (sản phẩm giảm giá)
                    </strong>
                    <button
                        onClick={() => setOpenDealPhu(true)}
                        className="px-2 py-2 border font-semibold rounded-md shadow transition duration-200 hover:opacity-90"
                    >
                        + Thêm sản phẩm
                    </button>
                </div>
                <div className="overflow-x-auto max-h-[50vh]">
                    <table className="w-full border-collapse text-sm">
                        <thead className="bg-gray-200 text-gray-700">
                            <tr>
                                <th className="p-2 border min-w-[200px] text-left">Sản phẩm</th>
                                <th className="p-2 border text-center min-w-[100px]">Giá bán hiện tại</th>
                                <th className="p-2 border text-center min-w-[80px]">Tỉ lệ giảm</th>
                                <th className="p-2 border text-center min-w-[100px]">Giảm tối đa trên đơn vị</th>
                                <th className="p-2 border text-center min-w-[60px]">Số lượng kho</th>
                            </tr>
                        </thead>
                        <tbody>
                            {danhSachChonPhu?.length === 0 ? (
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
                            ) : (
                                danhSachChonPhu.map((d, index) => (
                                    <>
                                        <tr key={index} className="bg-gray-100">
                                            <td colSpan={6} className="p-2 border">
                                                <div className="flex items-center justify-between">
                                                    <div className="flex items-center">
                                                        <img src={d.hinhAnh} className="m-2 w-[50px] h-[50px] object-cover rounded" alt={d.ten} />
                                                        <span className="pl-3">{d.ten} </span>
                                                    </div>
                                                    <span> <button
                                                        onClick={() => {
                                                            let a = danhSachChonPhu.filter((data) => data.id !== d.id);
                                                            setDanhSachChonPhu(a);
                                                        }}
                                                        className="p-2 rounded transition-colors "
                                                        aria-label="Xóa sản phẩm"
                                                        title="Xóa"
                                                    >
                                                        <i className="fa-solid fa-eraser p-2 "></i>
                                                    </button></span>
                                                </div>
                                            </td>
                                        </tr>
                                        {d.bienThe?.map((dd, btIndex) => (
                                            <tr key={btIndex} className="text-center hover:bg-gray-50 transition-all duration-200">
                                                <td className="p-2 border">
                                                    <div className="flex items-center pl-5">
                                                        <img src={dd.hinhAnh} className="m-2 w-[50px] h-[50px] object-cover rounded" alt={dd.ten} />
                                                        <span className="pl-3">{dd.ten}</span>
                                                        {( dd?.danhSachFlasSale?.length !== 0 ||  dd?.dealPhu?.length !== 0 || dd.dealChinh?.length !== 0) && (
                                                            <svg
                                                                className="ml-2 w-5 h-5 cursor-pointer text-red-500 hover:rotate-[360deg] hover:scale-110 transition-all duration-500"
                                                                viewBox="0 0 24 24"
                                                                fill="currentColor"
                                                                onClick={() => {
                                                                    setdealtrung({
                                                                        data: dd.dealChinh,
                                                                        dataPhu: dd.dealPhu,
                                                                        falsh:dd.danhSachFlasSale
                                                                    });
                                                                    setopentrung(true);
                                                                }}
                                                            >
                                                                <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z" />
                                                            </svg>
                                                        )}
                                                    </div>
                                                </td>
                                                <td className="p-2 border">
                                                    <p className="p-2  rounded-md">{formatToVND(dd.gia)}</p>
                                                </td>
                                                <td className="p-2 border">
                                                    <input
                                                        min={1}
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
                                                                    giaGiam: newGiaGiam
                                                                };
                                                                setDanhSachChonPhu(a);
                                                            }
                                                        }}
                                                        placeholder="Tỉ lệ giảm"
                                                        type="number"
                                                        className="w-24 border border-gray-200 rounded px-2 py-1 outline-none focus:ring-2 focus:ring-violet-400"
                                                    />
                                                </td>
                                                <td className="p-2 border">
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
                                                                    soLuongKhuyenMai: newSoLuongKhuyenMai
                                                                };
                                                                setDanhSachChonPhu(a);
                                                            }
                                                        }}
                                                        defaultValue={dd?.soLuongKhuyenMai || 0}
                                                        placeholder="Số lượng giảm"
                                                        type="number"
                                                        className="w-24 border border-gray-200 rounded px-2 py-1 outline-none focus:ring-2 focus:ring-violet-400"
                                                    />
                                                </td>
                                                <td className="p-2 border">
                                                    <p className="p-2 rounded">{dd.soLuongKho}</p>
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

            {/* Modal chọn sản phẩm chính */}
            {open && (
                <Modal setOpen={setOpen}>
                    <PickProduct
                        danhSachChon={danhSachChon}
                        filter={danhSachChonPhu || []}
                        setDanhSachChon={setDanhSachChon}
                        task={"deal"}
                    />
                </Modal>
            )}

            {/* Modal chọn sản phẩm phụ */}
            {openDealPhu && (
                <Modal setOpen={setOpenDealPhu}>
                    <PickProduct
                        danhSachChon={danhSachChonPhu}
                        filter={danhSachChon}
                        setDanhSachChon={setDanhSachChonPhu}
                        task={"deal"}
                    />
                </Modal>
            )}

            {/* Warning Modal */}
            {opentrung && (
                <div
                    ref={warningDivRef}
                    className="fixed top-0 right-0 h-full w-[40%] bg-white shadow-xl rounded-l-lg p-6 z-50 transform transition-transform duration-500 ease-in-out"
                >
                    <div className="flex justify-between items-center mb-4">
                        <h3 className="text-xl font-semibold text-violet-700">Thông tin Lưu ý</h3>
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
                        <section>
                              <h4 className="text-lg font-semibold mb-3 text-violet-700">Flashsale đang áp dụng</h4>
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
                                    {dealtrung?.falsh?.map((data, index) => (
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
                                                        // navigate(`/admin/marketing/flash/update?id=${data.id}`);
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
                        </section>
                    </div>
                </div>
            )}
        </div>
    );
}

export { AddDeal };
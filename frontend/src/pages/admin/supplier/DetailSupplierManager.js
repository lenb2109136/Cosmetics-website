import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getAllBase } from "../../../services/PhieuNhapService";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../../utils/Format";
import Modal from "../../../components/commons/modal";
import { ChiTietPhieuNhap } from "../../../components/admin/DetailimportSlip";
import { DetailProduct } from "../../../components/admin/DetailProduct";
import gsap from "gsap";
import { Pagination } from "../../../components/commons/Pagination";

function DetailSupllierManager() {
    const navigate = useNavigate();
    const { id } = useParams();

    const formatDate = (date) => date.toISOString().slice(0, 10);
    const today = new Date();
    const threeMonthsAgo = new Date(today.getFullYear(), today.getMonth() - 3, 1);
    const [bd, setbd] = useState(formatDate(threeMonthsAgo));
    const [kt, setkt] = useState(formatDate(today));
    const [pick, setPick] = useState(true);


    const [danhSachPhieuNhap, setDanhSachPhieuNhap] = useState([]);
    const [trang, setTrang] = useState(0);
    const [tong, setTong] = useState(0)
    const [tongTrang, setTongTrang] = useState(0)
    const [open, setOpen] = useState(false);
    const [idPhieu, setIdPhieu] = useState(-1);

    const titleRef = useRef(null);
    const statsRef = useRef(null);
    const filterRef = useRef(null);
    const filterRef2 = useRef(null);
    const tableRef = useRef(null);
    const [soLuongSanPham, setsoLuongSanPham] = useState(0)
    useEffect(() => {
        if (id == null || id < 1) {
            navigate(-1);
        } else {
            getAllBase(bd, kt, trang, id)
                .then((data) => {
                    setDanhSachPhieuNhap(data.content)
                    setTongTrang(data.totalPages)
                    setTong(data.totalElements)
                }
                )
                .catch((err) =>
                    toast.error(err?.response?.data?.message || "Lấy dữ liệu thất bại")
                );
        }
    }, [bd, kt, trang, id]);

    useEffect(() => {
        const ctx = gsap.context(() => {
            gsap.from(titleRef.current, {
                opacity: 0,
                y: -30,
                duration: 0.6,
                ease: "power2.out",
            });

            gsap.from(statsRef.current, {
                opacity: 0,
                y: 20,
                duration: 0.7,
                delay: 0.2,
                ease: "power2.out",
                stagger: 0.1,
            });

            gsap.from(filterRef.current, {
                opacity: 0,
                y: 30,
                duration: 0.8,
                delay: 0.3,
                ease: "power2.out",
            });
            gsap.from(filterRef2.current, {
                opacity: 0,
                y: 30,
                duration: 0.8,
                delay: 0.4,
                ease: "power2.out",
            });

            gsap.from(tableRef.current, {
                opacity: 0,
                scale: 0.95,
                duration: 0.8,
                delay: 0.5,
                ease: "power2.out",
            });
        });

        return () => ctx.revert();
    }, []);

    const tongTien = danhSachPhieuNhap.reduce((acc, cur) => acc + cur.tongTien, 0);
    const soLanNhap = danhSachPhieuNhap.length;

    return (
        <div className="p-4 md:p-8">
            <p className="text-left   mt-4 mb-3 bg-white p-3 rounded-md">
                <i class=" fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
                <strong className="text-lg">Thông tin chi tiết</strong>
                <div className="flex w-full justify-between">
                    <p className=" mt-1">Quản lý thông tin nhà cung cấp</p>
                    <p onClick={()=>{
                        navigate("/admin/supplier/addsupplier/"+id)
                    }} className="font-bold cursor-pointer"><i class="fa-solid fa-square-pen p-2 rounded-sm text-yellow-500 bg-yellow-100"></i> Điều chỉnh</p>
                </div>
            </p>
            <div ref={statsRef} className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div className="border-l-4 border-l-yellow-400 rounded-xl p-4 shadow-md bg-white">
                    <p className="text-sm text-gray-500"><i class="fa-solid fa-sack-dollar p-2 rounded-sm text-yellow-500 bg-yellow-100"></i> Tổng giá trị giao dịch</p>
                    <p className="text-xl font-bold  mt-1">{formatToVND(tongTien)}</p>
                </div>
                <div className="border-l-4 border-pink-4 border-l-pink-400  rounded-xl p-4 shadow-md bg-white">
                    <p className="text-sm text-gray-500"><i class="fa-solid fa-truck-ramp-box p-2 rounded-sm text-pink-500 bg-pink-100"></i> Tổng số lần nhập kho</p>
                    <p className="text-xl font-bold  mt-1">{soLanNhap} lần nhập</p>
                </div>
                <div className="border-l-4 border-green-4 border-l-green-400 rounded-xl p-4 shadow-md bg-white">
                    <p className="text-sm text-gray-500"><i class="p-2 rounded-sm text-green-500 bg-green-100 fa-solid fa-boxes-stacked "></i> Tổng sản phẩm cung cấp</p>
                    <p className="text-xl font-bold  mt-1">{danhSachPhieuNhap?.[0]?.soluong} Sản phẩm</p>
                </div>
            </div>

            <div ref={filterRef} className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6 bg-white p-3 pb-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1"><i class="p-2 rounded-sm text-blue-500 bg-blue-100 fa-solid fa-clock"></i> Thời điểm bắt đầu</label>
                    <input
                        type="date"
                        value={bd}
                        onChange={(e) => setbd(e.target.value)}
                        className="w-full mt-2 p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1"><i class=" p-2 rounded-sm text-blue-500 bg-blue-100 fa-solid fa-clock"></i> Thời điểm kết thúc</label>
                    <input
                        type="date"
                        value={kt}
                        onChange={(e) => setkt(e.target.value)}
                        className="w-full mt-2 p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
                    />
                </div>
            </div>
            <div ref={filterRef2} className="flex-row justify-start bg-white p-3">
                <div className="flex justify-start">
                    <p
                        onClick={() => setPick(true)}
                        className={`pr-4 cursor-pointer flex items-center ${pick === true ? "text-blue-400" : null}`}
                    >
                        <i className="fas fa-truck mr-2"></i> Nhập hàng
                    </p>
                    <p
                        onClick={() => setPick(false)}
                        className={`pr-4 cursor-pointer flex items-center ${pick === false ? "text-blue-400" : null}`}
                    >
                        <i className="fas fa-box mr-2"></i> Sản phẩm cung cấp
                    </p>
                </div>
            </div>

            {
                pick ? <div ref={tableRef} className="overflow-x-auto bg-white p-3">
                    <p className="mb-2 font-bold"><i class="fa-solid fa-list p-2 rounded-sm text-green-500 bg-green-100 mr-2"></i> Danh sách Lần nhập hàng</p>
                    <table className="min-w-full divide-y divide-gray-200 border">
                        <thead className="bg-gray-100">
                            <tr>
                                <th className="px-4 py-2 text-left text-sm font-semibold text-gray-700">STT</th>
                                <th className="px-4 py-2 text-left text-sm font-semibold text-gray-700">Ngày lập phiếu</th>
                                <th className="px-4 py-2 text-left text-sm font-semibold text-gray-700">Nhân viên lập phiếu</th>
                                {/* <th className="px-4 py-2 text-left text-sm font-semibold text-gray-700">Thuế VAT</th> */}
                                <th className="px-4 py-2 text-left text-sm font-semibold text-gray-700">Tổng tiền thanh toán</th>
                                <th className="px-4 py-2 text-left text-sm font-semibold text-gray-700">Xem chi tiết</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {danhSachPhieuNhap?.length > 0 ? (
                                danhSachPhieuNhap.map((data, index) => (
                                    <tr key={index} className="hover:bg-gray-50 transition">
                                        <td className="px-4 py-2">{index + 1}</td>
                                        <td className="px-4 py-2">{dinhDangNgay(data.ngayNhap)}</td>
                                        <td className="px-4 py-2">{data.nhanVien}</td>
                                        {/* <td className="px-4 py-2">{formatToVND(data.thueVAT)}</td> */}
                                        <td className="px-4 py-2">{formatToVND(data.tongTien)}</td>
                                        <td className="px-4 py-2">
                                            <button
                                                onClick={() => {
                                                    setOpen(true);
                                                    setIdPhieu(data.id);
                                                }}
                                                className="pl-2 pr-2 pb-1 pt-1 text-green-500 bg-green-100"
                                            >
                                                Xem chi tiết
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6" className="text-center py-4 text-gray-500">
                                        Không có dữ liệu phiếu nhập trong khoảng thời gian này.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                    <div>
                        {tongTrang > 0 ? <Pagination
                            trangHienTai={trang}
                            setTrangHienTai={setTrang}
                            soLuongTrang={tongTrang}
                        /> : null}
                    </div>
                </div> : <DetailProduct sl={setsoLuongSanPham} bd={bd} kt={kt} id={id} setOpen={setOpen} setIdPhieu={setIdPhieu} />
            }
            {open && (
                <Modal setOpen={setOpen}>
                    <ChiTietPhieuNhap id={idPhieu} />
                </Modal>
            )}


        </div>
    );
}

export { DetailSupllierManager };

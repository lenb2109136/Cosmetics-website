import { useEffect, useRef, useState } from "react";
import gsap from "gsap";
import { getbaseSupplierManager } from "../../../services/SupplierService";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { Pagination } from "../../../components/commons/Pagination";

function ManagerSupplier() {
    // State dữ liệu
    const [trang, settrang] = useState(0);
    const [bd, setbd] = useState("");
    const [kt, setkt] = useState("");
    const [chuoi, setchuoi] = useState("");
    const [data, setdata] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [tongPhanTu,setTongPhanTu]=useState(0)
    const navigate = useNavigate()
    // Ref hiệu ứng
    const containerRef = useRef(null);

    // Set mặc định ngày bắt đầu và kết thúc
    useEffect(() => {
        const today = new Date();
        const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

        const formatDate = (d) => d.toISOString().slice(0, 10);

        setbd(formatDate(startOfMonth));
        setkt(formatDate(today));
    }, []);

    // Hiệu ứng gsap
    useEffect(() => {
        const ctx = gsap.context(() => {
            gsap.from(containerRef.current, {
                y: 40,
                opacity: 0,
                duration: 0.8,
                ease: "power2.out",
            });
        });

        return () => { ctx.revert() };
    }, []);

    useEffect(() => {
        if (!bd || !kt) return;

        getbaseSupplierManager(bd, kt, trang, chuoi)
            .then((d) => {
                setdata(d.content || []);
                setTotalPages(d.totalPages || 1);
                setTongPhanTu(d.totalElements)
            })
            .catch((e) => {
                toast.error(e.response?.data?.message || "Lỗi khi tải dữ liệu");
            });
    }, [chuoi, bd, kt, trang]);

    return (
        <div ref={containerRef} className="p-3   rounded-xl  mx-auto ">
            <p className="text-left   mt-4 mb-3 bg-white p-3 rounded-md">
                <i class="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
                <strong className="text-lg">Quản lý nhà cung cấp</strong>
                <p className=" mt-1">Quản lý danh sách đơn vị cung cấp tại cửa hàng</p>
            </p>


            {/* Bộ lọc */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8 bg-white rounded-md p-3">
                <div id="ten">
                    <p className="block mb-1 text-md font-medium text-gray-700">
                        <i className="fas fa-user mr-2 text-yellow-500 bg-yellow-100 p-2 mb-2"></i> Tên nhà cung cấp:
                    </p>
                    <input
                        value={chuoi}
                        onChange={(e) => setchuoi(e.target.value)}
                        type="text"
                        placeholder="Nhập tên nhà cung cấp"
                        className="outline-none w-full p-2 cursor-pointer border rounded-lg focus:ring-2 focus:ring-blue-400"
                    />
                </div>
                <div id="bt">
                    <p className="block mb-1 text-md font-medium text-gray-700">
                        <i className="fas fa-calendar-alt mr-2 text-yellow-500 bg-yellow-100 p-2 mb-2"></i> Thời điểm bắt đầu:
                    </p>
                    <input
                        value={bd}
                        onChange={(e) => setbd(e.target.value)}
                        type="date"
                        className="outline-none w-full p-2 cursor-pointer border rounded-lg focus:ring-2 focus:ring-blue-400"
                    />
                </div>
                <div id="kt">
                    <p className="block mb-1 text-md font-medium text-gray-700">
                        <i className="fas fa-calendar-check mr-2 bg-yellow-100 text-yellow-500 p-2 mb-2 rounded-md"></i> Thời điểm kết thúc:
                    </p>
                    <input
                        value={kt}
                        onChange={(e) => setkt(e.target.value)}
                        type="date"
                        className="outline-none w-full p-2 cursor-pointer border rounded-lg focus:ring-2 focus:ring-blue-400"
                    />
                </div>
            </div>

            {/* Bảng dữ liệu */}
            <div id="bang" className="overflow-x-auto bg-white p-3">
                <div className="flex items-center mb-2">
                    <i class="fa-solid fa-list mt-[4px] text-green-500 bg-green-100 p-2 rounded-md mr-2"></i>
                    <p>Tổng số đơn vị: {tongPhanTu}</p>
                </div>
                <table className="min-w-full table-auto border border-gray-300 rounded-lg">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="px-4 py-2 border">STT</th>
                            <th className="px-4 py-2 border">Tên nhà cung cấp</th>
                            <th className="px-4 py-2 border">Tổng sản phẩm đã cung cấp</th>
                            <th className="px-4 py-2 border">Tổng lần nhập kho</th>
                            <th className="px-4 py-2 border">Chi tiết</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.length > 0 ? (
                            data.map((d, index) => (
                                <tr key={d.id} className="hover:bg-gray-50">
                                    <td className="px-4 py-2 border text-center">{index + 1 + trang * 10}</td>
                                    <td className="px-4 py-2 border">{d.ten}</td>
                                    <td className="px-4 py-2 border text-center">{d.tongSanPham}</td>
                                    <td className="px-4 py-2 border text-center">{d.tongPhieuNhap}</td>
                                    <td className="px-4 py-2 border text-center">
                                        <button onClick={() => {
                                            navigate(`detail/${d.id}`)
                                        }} className="text-green-500 bg-green-100 pl-2 pr-2 rounded-md ">Xem</button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="5" className="text-center py-4 text-gray-500">
                                    Không có dữ liệu
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
                <Pagination  setTrangHienTai={settrang} trangHienTai={trang} soLuongTrang={totalPages} ></Pagination>
            </div>

            
        </div>
    );
}

export { ManagerSupplier };

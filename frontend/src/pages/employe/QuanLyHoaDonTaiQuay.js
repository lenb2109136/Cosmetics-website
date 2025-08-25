import { useState, useRef, useEffect } from "react";
import { getByStatusTaiQuay } from "../../services/OrderService";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import { Pagination } from "../../components/commons/Pagination";
import { useNavigate } from "react-router-dom";

function QuanLyHoaDonTaiQuay() {
    const [status, setStatus] = useState([
        { id: 0, name: "Tất cả" },
        { id: 2, name: "Chưa thanh toán" },
        { id: 1, name: "Hoàn tất" },
        { id: 8, name: "Hoàn trả" },
        { id: 11, name: "Đơn hủy" },
    ]);
    const navigate=useNavigate()
    const [trang, setTrang] = useState(0)
    const [data, setData] = useState([])
    const [tongTrang, setTongTrang] = useState(0)
    const [tongSoPhanTu, setTongSoPhanTu] = useState(0)
    const [activeId, setActiveId] = useState(0);
    const [filter, setFilter] = useState({
        maHoaDon: "",
        tenNguoiDung: "",
        ngayLap: "",
        sort: 1,
    });
    useEffect(() => {
       let a= {
            maHoaDon:filter.maHoaDon,
            khachHang:filter.tenNguoiDung,
            trangThai:activeId,
            ngayLap:filter.ngayLap,
            sort:filter.sort,
            trang:trang
        }
        getByStatusTaiQuay(a).then(d=>{
            setData(d?.danhSach)
            setTongSoPhanTu(d?.tongSoPhanTu)
            setTongTrang(d?.tongTrang)
        })
    }, [filter,trang,activeId])
    const [openSortDropdown, setOpenSortDropdown] = useState(false);
    const sortRef = useRef(null);

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilter((prev) => ({
            ...prev,
            [name]: name === "sort" ? parseInt(value) : value,
        }));
    };

    const handleSortSelect = (value) => {
        setFilter((prev) => ({ ...prev, sort: value }));
        setOpenSortDropdown(false);
    };

    return (
        <div className="p-4">
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
                        background: #14532d;
                        border-radius: 4px;
                    }
                    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
                        background: #0f3d22;
                    }
                `}
            </style>
            {/* Bộ lọc nằm ngang */}
            <div className="flex flex-row gap-4 mb-4">
                <div className="flex flex-col flex-1">
                      <div className="mb-2">
                        <i class="fa-solid fa-barcode mr-1 text-green-900  border shadow-md rounded-md p-2"></i>
                        <span >Mã hóa đơn:</span>
                    </div>
                    <input
                        name="maHoaDon"
                        value={filter.maHoaDon}
                        onChange={handleFilterChange}
                        className="border border-green-900 rounded-md px-3 py-2 text-sm"
                        placeholder="Nhập mã hóa đơn"
                    />
                </div>
                <div className="flex flex-col flex-1">
                    <div className="mb-2">
                        <i class="fa-regular fa-user mr-1 text-green-900  border shadow-md rounded-md p-2"></i>
                        <span >Tên khách hàng:</span>
                    </div>
                    <input
                        name="tenNguoiDung"
                        value={filter.tenNguoiDung}
                        onChange={handleFilterChange}
                        className="border border-green-900 rounded-md px-3 py-2 text-sm"
                        placeholder="Nhập thông tin khách hàng"
                    />
                </div>
                <div className="flex flex-col flex-1">
                   <div className="mb-2">
                        <i class="fa-regular fa-clock mr-1 text-green-900  border shadow-md rounded-md p-2"></i>
                        <span >Ngày lập:</span>
                    </div>
                    <input
                        name="ngayLap"
                        type="date"
                        value={filter.ngayLap}
                        onChange={handleFilterChange}
                        className="border border-green-900 rounded-md px-3 py-2 text-sm"
                    />
                </div>
                <div className="flex flex-col flex-1 relative z-50">
                    <div className="mb-2">
                        <i class="fa-solid fa-arrow-up-short-wide mr-1 text-green-900  border shadow-md rounded-md p-2"></i>
                        <span >Sắp xếp theo:</span>
                    </div>
                    <input
                        ref={sortRef}
                        value={
                            filter.sort === 1
                                ? "Mới nhất"
                                : filter.sort === 2
                                    ? "Cũ nhất"
                                    : filter.sort === 3
                                        ? "Giá trị tăng"
                                        : "Giá trị giảm"
                        }
                        onFocus={() => setOpenSortDropdown(true)}
                        onBlur={() => setTimeout(() => setOpenSortDropdown(false), 200)}
                        className="border border-green-900 rounded-md px-3 py-2 text-sm cursor-pointer"
                        placeholder="Chọn cách sắp xếp"
                        readOnly
                    />
                    {openSortDropdown && (
                        <div className="absolute bg-white border border-green-900 rounded-md shadow-md w-full mt-1 z-10 top-full">
                            <div className="absolute -top-2 left-3 w-0 h-0 border-l-8 border-r-8 border-b-8 border-l-transparent border-r-transparent border-b-green-900"></div>
                            <ul className="text-sm p-2 max-h-60 overflow-y-auto custom-scrollbar">
                                {[
                                    { value: 1, label: "Mới nhất" },
                                    { value: 2, label: "Cũ nhất" },
                                    { value: 3, label: "Giá trị tăng" },
                                    { value: 4, label: "Giá trị giảm" },
                                ].map((option) => (
                                    <li
                                        key={option.value}
                                        onClick={() => handleSortSelect(option.value)}
                                        className={`py-1 px-2 hover:bg-green-900 hover:text-white cursor-pointer
                                            ${filter.sort === option.value ? "bg-green-900 text-white" : ""}`}
                                    >
                                        {option.label}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            </div>
            {/* Tab trạng thái */}
            <div className="flex gap-4 border-b-2 border-gray-200">
                {status.map((state) => (
                    <button
                        key={state.id}
                        onClick={() => setActiveId(state.id)}
                        className={`pb-2 mb-2 mr-4 pr-2 flex transition-colors justify-between items-center duration-300 text-sm font-medium text-green-900  border mr-4 shadow-md rounded-md p-1
                            ${activeId === state.id ? "text-green-900 border-b-2 border-green-900" : "text-gray-500 hover:text-green-900"}
                        `}
                    >
                    {state.id==0?<i class="fa-solid fa-border-all pl-2 mr-4"></i>:null}
                        {state.id==1?<i class="fa-solid fa-check pl-2 mr-4"></i>:null}
                        {state.id==8?<i class="fa-solid fa-clock-rotate-left pl-2 mr-2"></i>:null}
                        {state.id==9?<i class="fa-solid fa-shop-slash pl-2 mr-2"></i>:null}
                        {state.id==2?<i class="fa-solid fa-money-bill-wave pl-2 mr-2"></i>:null}
                        {state.id==11?<i class="fa-solid fa-shop-slash pl-2 mr-2"></i>:null}
                        <p>{state.name}</p>
                    </button>
                ))}
            </div>
            <p className="mt-2 text-green-900 ml-2">Tổng số: {tongSoPhanTu} đơn hàng</p>
            <table className="w-full mt-2 border-collapse">
                            <thead>
                                <tr className="bg-green-900 text-green-200 text-center">
                                    <th className="px-4 py-2">STT</th>
                                    <th className="px-4 py-2">Ngày đặt hàng</th>
                                    <th className="px-4 py-2">Khách hàng</th>
                                    <th className="px-4 py-2">Tổng Tiền thanh toán</th>
                                    <th className="px-4 py-2">Tổng số mặt hàng</th>
                                    <th className="px-4 py-2">Tổng số lượng hàng hóa</th>
                                    {activeId==8? <th className="px-4 py-2">Đã trả tiền khách</th>:null}
                                    <th className="px-4 py-2">Xem chi tiết</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data?.map((item, index) => (
                                    <tr onClick={() => {
                                        navigate(`/employee/detailtaiquay?id=${item.id}`)
                                    }} key={item.id} className="text-center even:bg-gray-100 cursor-pointer hover:bg-green-50 transition-all duration-400">
                                        <td className="px-4 py-2">{index + 1}</td>
                                        <td className="px-4 py-2">{dinhDangNgay(item?.ngayLap)}</td>
                                        <td className="px-4 py-2">{item?.tenKhachHang}</td>
                                        <td className="px-4 py-2">{formatToVND(item.tongTien)}</td>
                                        <td className="px-4 py-2">{item.tongSoMatHang}</td>
                                        <td className="px-4 py-2">{item.tongSoLuongHang}</td>
                                        {activeId==8? <td className="px-4 py-2">{item.daHoanHang}</td>:null}
                                        <td className="  text-green-900 hover:font-bold  ">
                                            <p className="p-2 py-1 border shadow-md rounded-md mt-1 mb-1 mr-2" >Xem chi tiết</p>
                                        </td>
                                    </tr>
                                ))}
                                 {
                                        !data || data?.length === 0 ? (
                                            <tr className="text-center border border-green-900">
                                                <td colSpan={activeId==8?7:6}>
                                                    <div className="w-full flex justify-center">
                                                        <img
                                                            className="w-44"
                                                            src="https://media.istockphoto.com/id/861576608/vi/vec-to/bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-empty-shopping-bag-m%E1%BA%ABu-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-vector-online-business.jpg?s=612x612&w=0&k=20&c=-6p0qVyb_p8VeHVu1vJVaDHa6Wd_mCjMkrFBTlQdehI="
                                                        />
                                                    </div>
                                                </td>
                                            </tr>
                                        ) : null
                                    }
                            </tbody>
                        </table>
                        <Pagination color="bg-green-900" setTrangHienTai={setTrang} trangHienTai={trang} soLuongTrang={tongTrang}></Pagination>
        </div>
    );
}

export { QuanLyHoaDonTaiQuay };
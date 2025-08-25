import { useEffect, useState } from "react";
import { getKhauHaoForEmpl } from "../../services/PhieuNhapService";
import { dinhDangNgay } from "../../utils/Format";
import { Pagination } from "../../components/commons/Pagination";
import { useNavigate } from "react-router-dom";

function HomePhieuKiemHang() {
    const [tongSoTrang, setTongSoTrang] = useState(0);
    const [trangHienTai, setTrangHienTai] = useState(0);
    const [tongSoPhieu, setTongSoPhieu] = useState(0);
    const [ten, setTen] = useState("");
    const [maVach, setMaVach] = useState(""); // New state for Tên/Mã vạch
    const [tinhTrang, setTinhTrang] = useState(0); // 0: Tất cả, 1: Đã xác nhận, -1: Chưa xác nhận
    const [bd, setBd] = useState(() => {
        const now = new Date();
        return new Date(now.getFullYear(), now.getMonth(), 1).toISOString().slice(0, 16);
    });
    const [kt, setKt] = useState(() => new Date().toISOString().slice(0, 16));
    const [data, setData] = useState([]);
    const [isTinhTrangOpen, setIsTinhTrangOpen] = useState(false);

    useEffect(() => {
        getKhauHaoForEmpl(ten, bd, kt, trangHienTai, tinhTrang, maVach)
            .then((d) => {
                setData(d.data.data.content || []);
                setTongSoTrang(d.data.data.totalPages || 0);
                setTongSoPhieu(d.data.data.totalElements || 0);
            })
            .catch(() => {});
    }, [ten, bd, kt, trangHienTai, tinhTrang, maVach]);

    const navigate = useNavigate();

    const tinhTrangOptions = [
        { value: 0, label: "Tất cả" },
        { value: 1, label: "Đã xác nhận" },
        { value: -1, label: "Chưa xác nhận" },
    ];

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between">
                <strong>
                    <p className="text-lg font-bold text-center text-gray-700 uppercase">
                        <i className="fa-solid fa-file-invoice mr-2 p-2 text-green-900 border shadow-md rounded-md"></i>
                        Quản lý phiếu hao hụt hàng hóa
                    </p>
                </strong>
                <div className="flex justify-between items-center">
                    <button
                        onClick={() => navigate("add")}
                        className="p-2 text-green-800 shadow-md rounded-md border"
                    >
                        + Thêm phiếu nhập mới
                    </button>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-600 mb-2">
                        <i className="fas fa-user mr-2 p-2 text-green-900 border shadow-md rounded-md"></i> Tên nhân viên
                    </label>
                    <input
                        type="text"
                        value={ten}
                        onChange={(e) => setTen(e.target.value)}
                        className="mt-1 block w-full border border-gray-300 rounded-lg p-2"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-600 mb-2">
                        <i className="fas fa-barcode mr-2 p-2 text-green-900 border shadow-md rounded-md"></i> Tên/Mã vạch
                    </label>
                    <input
                        type="text"
                        value={maVach}
                        onChange={(e) => setMaVach(e.target.value)}
                        className="mt-1 block w-full border border-gray-300 rounded-lg p-2"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-600 mb-2">
                        <i className="fas fa-calendar-alt mr-2 text-green-900 border p-2 shadow-md rounded-md"></i> Thời gian bắt đầu
                    </label>
                    <div className="relative mt-1">
                        <input
                            type="datetime-local"
                            value={bd}
                            onChange={(e) => setBd(e.target.value)}
                            className="block w-full border border-gray-300 rounded-lg p-2 pl-10"
                        />
                        <i className="fas fa-calendar-alt absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
                    </div>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-600 mb-2">
                        <i className="fas fa-calendar-check mr-2 text-green-900 border p-2 shadow-md rounded-md"></i> Thời gian kết thúc
                    </label>
                    <div className="relative mt-1">
                        <input
                            type="datetime-local"
                            value={kt}
                            onChange={(e) => setKt(e.target.value)}
                            className="block w-full border border-gray-300 rounded-lg p-2 pl-10"
                        />
                        <i className="fas fa-calendar-check absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
                    </div>
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-600 mb-2">
                        <i className="fas fa-check-circle mr-2 text-green-900 border p-2 shadow-md rounded-md"></i> Tình trạng
                    </label>
                    <div className="relative mt-1 border border-gray-300 rounded-lg h-[43px]">
                        <button
                            type="button"
                            aria-expanded={isTinhTrangOpen}
                            aria-haspopup="listbox"
                            aria-labelledby="tinh-trang-label"
                            className="grid w-full cursor-default grid-cols-1 rounded-md bg-white py-1.5 pr-2 pl-3 text-left text-gray-900 outline-1 -outline-offset-1 outline-gray-300 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm"
                            onClick={() => setIsTinhTrangOpen(!isTinhTrangOpen)}
                        >
                            <span className="col-start-1 row-start-1 flex items-center gap-3 pr-6">
                                <span className="block truncate">
                                    {tinhTrangOptions.find((option) => option.value === tinhTrang)?.label || "Tất cả"}
                                </span>
                            </span>
                            <svg
                                viewBox="0 0 16 16"
                                fill="currentColor"
                                aria-hidden="true"
                                className="col-start-1 row-start-1 size-5 self-center justify-self-end text-gray-500 sm:size-4"
                            >
                                <path
                                    d="M5.22 10.22a.75.75 0 0 1 1.06 0L8 11.94l1.72-1.72a.75.75 0 1 1 1.06 1.06l-2.25 2.25a.75.75 0 0 1-1.06 0l-2.25-2.25a.75.75 0 0 1 0-1.06ZM10.78 5.78a.75.75 0 0 1-1.06 0L8 4.06 6.28 5.78a.75.75 0 0 1-1.06-1.06l2.25-2.25a.75.75 0 0 1 1.06 0l2.25 2.25a.75.75 0 0 1 0 1.06Z"
                                    clipRule="evenodd"
                                    fillRule="evenodd"
                                />
                            </svg>
                        </button>
                        {isTinhTrangOpen && (
                            <ul
                                role="listbox"
                                tabIndex="-1"
                                aria-labelledby="tinh-trang-label"
                                className="absolute z-10 mt-4 max-h-56 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black/5 focus:outline-none sm:text-sm"
                            >
                                {tinhTrangOptions.map((option) => (
                                    <li
                                        key={option.value}
                                        role="option"
                                        className="relative cursor-default py-2 pr-9 pl-3 text-gray-900 select-none hover:bg-indigo-600 hover:text-white"
                                        onClick={() => {
                                            setTinhTrang(option.value);
                                            setIsTinhTrangOpen(false);
                                        }}
                                    >
                                        <div className="flex items-center">
                                            <span className="ml-3 block truncate font-normal">
                                                {option.label}
                                            </span>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                </div>
            </div>
            <div className="flex justify-between items-center h-fit">
                <p className="text-sm text-gray-600 w-fit rounded-md border shadow-md p-2">
                    <i className="fas fa-list mr-1 text-green-900"></i> Tổng số phiếu: <strong>{tongSoPhieu}</strong>
                </p>
                <div>
                    <Pagination
                        color="bg-green-900"
                        setTrangHienTai={setTrangHienTai}
                        soLuongTrang={tongSoTrang}
                        trangHienTai={trangHienTai}
                    />
                </div>
            </div>

            <div className="overflow-x-auto">
                <table className="min-w-full border border-gray-300 text-sm text-center">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="border p-2">STT</th>
                            <th className="border p-2"><i className="fas fa-calendar-day mr-1"></i> Ngày lập</th>
                            <th className="border p-2"><i className="fas fa-user-tie mr-1"></i> Nhân viên lập</th>
                            <th className="border p-2"><i className="fas fa-boxes mr-1"></i> Số lượng hàng hao hụt</th>
                            <th className="border p-2"><i className="fas fa-box-open mr-1"></i> Số lượng hao hụt</th>
                            <th className="border p-2"><i className="fas fa-check-circle mr-1"></i> Tình trạng</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data?.length > 0 ? (
                            data.map((d, index) => (
                                <tr key={index} className="hover:bg-gray-50">
                                    <td onClick={() => navigate(`${d?.id}`)} className="border p-2 cursor-pointer">{index + 1}</td>
                                    <td onClick={() => navigate(`${d?.id}`)} className="border p-2 cursor-pointer">{dinhDangNgay(d?.ngayLap)}</td>
                                    <td onClick={() => navigate(`${d?.id}`)} className="border p-2 cursor-pointer">{d?.nhanVienLap}</td>
                                    <td onClick={() => navigate(`${d?.id}`)} className="border p-2 cursor-pointer">{d?.soLuongHangHoaHut}</td>
                                    <td onClick={() => navigate(`${d?.id}`)} className="border p-2 cursor-pointer">{d?.soLuongHangHaoHut}</td>
                                    <td className="border p-2 flex justify-center">
                                        {d?.daXacNhan ? (
                                            <p className="bg-green-900 text-white p-2 rounded-md font-semibold w-fit">Đã xác nhận</p>
                                        ) : (
                                            <p className="text-white my-1 font-semibold bg-orange-500 p-2 rounded-md mt-2 w-fit">Chờ xác nhận</p>
                                        )}
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="6" className="p-4 text-gray-500 italic">
                                    Không có dữ liệu
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export { HomePhieuKiemHang };
import { useEffect, useState } from "react";
import { duyetPhieukiem, getInfoHaoHut, getKhauHaoForEmpl, getThongKePhieuKiem } from "../../services/PhieuNhapService";
import { formatToVND, dinhDangNgay } from "../../utils/Format";
import { Pagination } from "../../components/commons/Pagination";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

function HomeHaoHut() {
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
    const [tongHopHao, setTongHopHao] = useState({});
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedPhieuId, setSelectedPhieuId] = useState(null);
    const [trangHienTaiThongTinHaoHut, setTrangHienTaiThongTinHaoHut] = useState(0);
    const [tongtrang2, setTongTrang2] = useState(0)
    const [tongphantu2, settongphantu2] = useState(0)
    const [dataHaoHut, setDataHaoHut] = useState([])
    useEffect(() => {
        getThongKePhieuKiem(ten, bd, kt, tinhTrang, maVach).then(d => {
            setTongHopHao(d);
        }).catch(() => { });
    }, [ten, bd, kt, tinhTrang, maVach]);
    useEffect(() => {
        getInfoHaoHut(ten, bd, kt, trangHienTaiThongTinHaoHut, tinhTrang, maVach).then((d) => {
            setDataHaoHut(d.tongGiaTriHaoHut)
            settongphantu2(d?.tongSoPhanTu)
            setTongTrang2(d.tongTrang)
        })
    }, [ten, bd, kt, trangHienTaiThongTinHaoHut, tinhTrang, maVach])

    useEffect(() => {
        getKhauHaoForEmpl(ten, bd, kt, trangHienTai, tinhTrang,maVach)
            .then((d) => {
                setData(d.data.data.content || []);
                setTongSoTrang(d.data.data.totalPages || 0);
                setTongSoPhieu(d.data.data.totalElements || 0);
            })
            .catch(() => { });
    }, [ten, bd, kt, trangHienTai, tinhTrang,maVach]);

    const navigate = useNavigate();

    const tinhTrangOptions = [
        { value: 0, label: "Tất cả" },
        { value: 1, label: "Đã xác nhận" },
        { value: -1, label: "Chưa xác nhận" },
    ];

    const handleConfirmPhieu = (id) => {
        setSelectedPhieuId(id);
        setIsModalOpen(true);
    };

    const handleConfirm = () => {
        duyetPhieukiem(selectedPhieuId).then(() => {
            toast.success("duyệt phiếu thành công")
            setIsModalOpen(false);
            setSelectedPhieuId(null);
            
            getKhauHaoForEmpl(ten, bd, kt, trangHienTai, tinhTrang, maVach)
                .then((d) => {
                    setData(d.data.data.content || []);
                    setTongSoTrang(d.data.data.totalPages || 0);
                    setTongSoPhieu(d.data.data.totalElements || 0);
                })
                .catch(() => { });
            getKhauHaoForEmpl(ten, bd, kt, trangHienTai, tinhTrang, maVach)
                .then((d) => {
                    setData(d.data.data.content || []);
                    setTongSoTrang(d.data.data.totalPages || 0);
                    setTongSoPhieu(d.data.data.totalElements || 0);

                })
                .catch(() => { });
        }).catch(e => {
            toast.error(e?.response?.data?.message || "Duyệt thất bại, vui lòng thử lại")
        })
    };

    const handleCancel = () => {
        setIsModalOpen(false);
        setSelectedPhieuId(null);
    };

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between">
                <strong>
                    <p className="text-lg font-bold text-center text-gray-700 uppercase">
                        <i className="fa-solid fa-file-invoice mr-2 p-2 text-green-900 border shadow-md rounded-md"></i>
                        Quản lý phiếu hao hụt hàng hóa
                    </p>
                </strong>
                <div className="flex justify-between items-center"></div>
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
            <div className="flex flex-wrap justify-left gap-5 max-w-6xl mx-auto p-5">
                <div className="bg-white rounded-lg shadow-md p-5 flex items-center gap-4 hover:-translate-y-1 transition-transform flex-1 min-w-[250px] max-w-[350px]">
                    <i className="fa fa-money-bill-wave text-green-900 bg-green-100 border p-2 shadow-md rounded-md"></i>
                    <div>
                        <p className="text-base font-bold text-gray-800">Tổng Tiền hao hụt</p>
                        <p className="text-lg text-green-900 font-semibold">{formatToVND(tongHopHao.tongGiaTriHaoHut)}</p>
                    </div>
                </div>
                <div className="bg-white rounded-lg shadow-md p-5 flex items-center gap-4 hover:-translate-y-1 transition-transform flex-1 min-w-[250px] max-w-[350px]">
                    <i className="fa fa-box-open text-blue-500 bg-blue-100 border p-2 shadow-md rounded-md"></i>
                    <div>
                        <p className="text-base font-bold text-gray-800">Tổng số lượng hao hụt</p>
                        <p className="text-lg text-green-900 font-semibold">{tongHopHao.tongSoLuongHaoHut}</p>
                    </div>
                </div>
                <div className="bg-white rounded-lg shadow-md p-5 flex items-center gap-4 hover:-translate-y-1 transition-transform flex-1 min-w-[250px] max-w-[350px]">
                    <i className="fa fa-file-invoice text-yellow-400 bg-yellow-100 border p-2 shadow-md rounded-md"></i>
                    <div>
                        <p className="text-base font-bold text-gray-800">Tổng hóa đơn dự kiến hủy</p>
                        <p className="text-lg text-green-900 font-semibold">{tongHopHao.tongSoHoaDonDuKienHuy}</p>
                    </div>
                </div>
                <div className="bg-white rounded-lg shadow-md p-5 flex items-center gap-4 hover:-translate-y-1 transition-transform flex-1 min-w-[250px] max-w-[350px]">
                    <i className="fa fa-file-invoice-dollar text-pink-500 bg-pink-100 border p-2 shadow-md rounded-md"></i>
                    <div>
                        <p className="text-base font-bold text-gray-800">Tổng hóa đơn đã hủy</p>
                        <p className="text-lg text-green-900 font-semibold">{tongHopHao.tongSoHoaDonHuy}</p>
                    </div>
                </div>
                <div className="bg-white rounded-lg shadow-md p-5 flex items-center gap-4 hover:-translate-y-1 transition-transform flex-1 min-w-[250px] max-w-[350px]">
                    <i className="fa fa-exclamation-triangle text-violet-500 bg-violet-100 border p-2 shadow-md rounded-md"></i>
                    <div>
                        <p className="text-base font-bold text-gray-800">Tổng số lần hao hụt</p>
                        <p className="text-lg text-green-900 font-semibold">{tongHopHao.tongSoLanHaoHut}</p>
                    </div>
                </div>
                <div className="bg-white rounded-lg shadow-md p-5 flex items-center gap-4 hover:-translate-y-1 transition-transform flex-1 min-w-[250px] max-w-[350px]">
                    <i className="fa fa-boxes text-orange-500 bg-orange-100 border p-2 shadow-md rounded-md"></i>
                    <div>
                        <p className="text-base font-bold text-gray-800">Tổng sản phẩm hao hụt</p>
                        <p className="text-lg text-green-900 font-semibold">{tongHopHao.tongSanPhamHaoHut}</p>
                    </div>
                </div>
            </div>
            <div>
                <div className="flex justify-between items-center h-fit">
                    <p className="text-sm text-gray-600 w-fit rounded-md border shadow-md p-2 mb-6">
                        <i className="fas fa-list mr-1 text-green-900"></i> Chi tiết hao hụt: <strong>{tongphantu2} </strong>
                    </p>
                    <div className="mb-5">
                        <Pagination
                            color="bg-green-900"
                            setTrangHienTai={setTrangHienTaiThongTinHaoHut}
                            soLuongTrang={tongtrang2}
                            trangHienTai={trangHienTaiThongTinHaoHut}
                        />
                    </div>
                </div>

                <table className="min-w-full border border-gray-300 text-sm text-center">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="border p-2">STT</th>
                            <th className="border p-2"><i className="fas fa-tag mr-1"></i> Tên phân loại</th>
                            <th className="border p-2"><i className="fas fa-image mr-1"></i> Ảnh phân loại</th>
                            <th className="border p-2"><i className="fas fa-box-open mr-1"></i> Tổng số lượng hao</th>
                            <th className="border p-2"><i className="fas fa-money-bill-wave mr-1"></i> Tổng giá trị hao</th>
                        </tr>
                    </thead>
                    <tbody>
                        {dataHaoHut?.length > 0 ? (
                            dataHaoHut.map((item, index) => (
                                <tr key={index} className="hover:bg-gray-50">
                                    <td className="border p-2">{index + 1}</td>
                                    <td className="border p-2">{item.tenBienThe}</td>
                                    <td className="border p-2">
                                        <img
                                            src={item.anhBienThe}
                                            alt={item.tenBienThe}
                                            className="w-[50px] h-auto rounded-md border shadow-md mx-auto"
                                            onError={(e) => { e.target.src = 'https://via.placeholder.com/48'; }}
                                        />
                                    </td>
                                    <td className="border p-2">{item.tongSoLuongHao}</td>
                                    <td className="border p-2">{formatToVND(item.tongGiaTriHao)}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="5" className="p-4 text-gray-500 italic">
                                    Không có dữ liệu
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
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
                                            <button
                                                onClick={() => handleConfirmPhieu(d?.id)}
                                                className="text-white my-1 font-semibold bg-orange-500 p-2 rounded-md w-fit hover:bg-orange-600 transition-colors"
                                            >
                                                Chờ xác nhận
                                            </button>
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

            {isModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md">
                        <h2 className="text-lg font-bold text-gray-800 mb-4">Xác nhận duyệt phiếu</h2>
                        <p className="text-gray-600 mb-6">Bạn có chắc chắn duyệt phiếu nhập không?</p>
                        <div className="flex justify-end gap-4">
                            <button
                                onClick={handleCancel}
                                className="px-4 py-2 bg-gray-300 text-gray-800 rounded-md hover:bg-gray-400 transition-colors"
                            >
                                Hủy
                            </button>
                            <button
                                onClick={handleConfirm}
                                className="px-4 py-2 bg-green-900 text-white rounded-md hover:bg-green-800 transition-colors"
                            >
                                Xác nhận
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export { HomeHaoHut };
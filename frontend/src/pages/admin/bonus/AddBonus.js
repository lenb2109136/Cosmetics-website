import { useState } from "react";
import Modal from "../../../components/commons/modal";
import { PickProduct } from "../../../components/admin/PickProduct";
import { dinhDangNgay, formatToVND } from "../../../utils/Format";
import { saveBonus } from "../../../services/BonusService";
import { toast } from "react-toastify";

function AddBonus() {
    const [danhSachChinh, setDanhSachChinh] = useState([]);
    const [danhSachTangKem, setDanhSachTangKem] = useState([]);
    const [result, setResult] = useState({
        ngayBatDau: "",
        ngayKetThuc: "",
        soLuongGioiHan: "",
        data: [],
        dataPhu: [],
    });
    const [danhSachTrung, setDanhSachTrung] = useState({
        data: [],
        dataPhu: []
    })
    const [openTrung, setOpenTrung] = useState(false)
    const [openPickMain, setOpenPickMain] = useState(false);
    const [openPickBonus, setOpenPickBonus] = useState(false);

    const handleSaveBonus = () => {
        setResult((prev) => ({
            ...prev,
            data: danhSachChinh,
            dataPhu: danhSachTangKem,
        }));
        saveBonus(result)
            .then(() => toast.success("Tạo khuyến mãi thành công"))
            .catch((e) => toast.error(e?.response?.data?.message || "Tạo khuyến mãi thất bại"));
    };

    return (
        <div className="p-6 max-w-7xl mx-auto bg-white rounded-lg shadow-md">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">Thêm Khuyến Mãi</h1>

            {/* Form Section */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Ngày bắt đầu</label>
                    <input
                        type="datetime-local"
                        value={result.ngayBatDau}
                        onChange={(e) => setResult((prev) => ({ ...prev, ngayBatDau: e.target.value }))}
                        className="w-full border border-gray-300 rounded-md px-3 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Ngày kết thúc</label>
                    <input
                        type="datetime-local"
                        value={result.ngayKetThuc}
                        onChange={(e) => setResult((prev) => ({ ...prev, ngayKetThuc: e.target.value }))}
                        className="w-full border border-gray-300 rounded-md px-3 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Số lượt giới hạn</label>
                    <input
                        type="number"
                        value={result.soLuongGioiHan}
                        onChange={(e) => setResult((prev) => ({ ...prev, soLuongGioiHan: e.target.value }))}
                        className="w-full border border-gray-300 rounded-md px-3 py-2 focus:ring-2 focus:ring-blue-500 outline-none"
                        min="0"
                    />
                </div>
            </div>
            <button
                onClick={handleSaveBonus}
                className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition mb-6"
            >
                Thêm Khuyến Mãi
            </button>

            {/* Main Products Section */}
            <div className="mb-8">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold text-gray-700">Sản phẩm chính</h2>
                    <button
                        onClick={() => setOpenPickMain(true)}
                        className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 transition"
                    >
                        Thêm sản phẩm chính
                    </button>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full border-collapse bg-white shadow-sm rounded-lg">
                        <thead>
                            <tr className="bg-blue-100 text-gray-700">
                                <th className="p-3 text-left">Sản phẩm</th>
                                <th className="p-3 text-center">Giá bán</th>
                                <th className="p-3 text-center">Số lượng từ</th>
                                <th className="p-3 text-center">Số lượng trong kho</th>
                            </tr>
                        </thead>
                        <tbody>
                            {danhSachChinh.length === 0 ? (
                                <tr>
                                    <td colSpan={4} className="text-center p-4 italic text-gray-500">
                                        Chưa thêm dữ liệu sản phẩm chính
                                    </td>
                                </tr>
                            ) : (
                                danhSachChinh.map((d, index) => (
                                    <>
                                        <tr key={`main-${index}`} className="bg-gray-50">
                                            <td colSpan={4} className="p-3">
                                                <div className="flex items-center">
                                                    <img
                                                        src={d.hinhAnh}
                                                        alt={d.ten}
                                                        className="w-16 h-16 object-cover rounded-md mr-3"
                                                    />
                                                    <span className="font-medium">{d.ten}</span>
                                                </div>
                                            </td>
                                        </tr>
                                        {d.bienThe?.map((dd, btIndex) => (
                                            <tr
                                                key={`main-variant-${index}-${btIndex}`}
                                                className="border-t hover:bg-gray-100 transition"
                                            >
                                                <td className="p-3">
                                                    <div className="flex items-center">
                                                        <img
                                                            src={dd.hinhAnh}
                                                            alt={dd.ten}
                                                            className="w-12 h-12 object-cover rounded-md mr-3"
                                                        />
                                                        <div className="flex items-center">
                                                            <span>{dd.ten}</span>
                                                            {(dd.tangKemChinh?.length > 0 || dd.tangKemPhu?.length > 0) && (
                                                                <svg
                                                                    className="ml-2 w-5 h-5 text-red-500 cursor-pointer hover:scale-110 transition"
                                                                    viewBox="0 0 24 24"
                                                                    fill="currentColor"
                                                                    xmlns="http://www.w3.org/2000/svg"
                                                                    onClick={() => {
                                                                        setDanhSachTrung({
                                                                            data: dd.tangKemChinh,
                                                                            dataPhu: dd.tangKemPhu
                                                                        })
                                                                        setOpenTrung(true)
                                                                    }}
                                                                >
                                                                    <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z" />
                                                                </svg>
                                                            )}
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className="p-3 text-center">{formatToVND(dd.gia)}</td>
                                                <td className="p-3 text-center">
                                                    <input
                                                        type="number"
                                                        defaultValue={dd?.soLuongKhuyenMai || 0}
                                                        onChange={(e) => {
                                                            const newSoLuongKhuyenMai = parseInt(e.target.value);
                                                            if (newSoLuongKhuyenMai <= 99) {
                                                                const updatedList = [...danhSachChinh];
                                                                updatedList[index] = { ...updatedList[index] };
                                                                updatedList[index].bienThe = [...updatedList[index].bienThe];
                                                                updatedList[index].bienThe[btIndex] = {
                                                                    ...updatedList[index].bienThe[btIndex],
                                                                    soLuongKhuyenMai: newSoLuongKhuyenMai,
                                                                };
                                                                setDanhSachChinh(updatedList);
                                                            }
                                                        }}
                                                        className="w-20 border border-gray-300 rounded-md px-2 py-1 text-center focus:ring-2 focus:ring-blue-500 outline-none"
                                                        min="0"
                                                        placeholder="Số lượng"
                                                    />
                                                </td>
                                                <td className="p-3 text-center">{dd.soLuongKho}</td>
                                            </tr>
                                        ))}
                                    </>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Bonus Products Section */}
            <div>
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold text-gray-700">Sản phẩm tặng kèm</h2>
                    <button
                        onClick={() => setOpenPickBonus(true)}
                        className="bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 transition"
                    >
                        Thêm sản phẩm tặng kèm
                    </button>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full border-collapse bg-white shadow-sm rounded-lg">
                        <thead>
                            <tr className="bg-blue-100 text-gray-700">
                                <th className="p-3 text-left">Sản phẩm</th>
                                <th className="p-3 text-center">Giá bán</th>
                                <th className="p-3 text-center">Số lượng tặng kèm</th>
                                <th className="p-3 text-center">Số lượng trong kho</th>
                            </tr>
                        </thead>
                        <tbody>
                            {danhSachTangKem.length === 0 ? (
                                <tr>
                                    <td colSpan={4} className="text-center p-4 italic text-gray-500">
                                        Chưa thêm dữ liệu sản phẩm tặng kèm
                                    </td>
                                </tr>
                            ) : (
                                danhSachTangKem.map((d, index) => (
                                    <>
                                        <tr key={`bonus-${index}`} className="bg-gray-50">
                                            <td colSpan={4} className="p-3">
                                                <div className="flex items-center">
                                                    <img
                                                        src={d.hinhAnh}
                                                        alt={d.ten}
                                                        className="w-16 h-16 object-cover rounded-md mr-3"
                                                    />
                                                    <span className="font-medium">{d.ten}</span>
                                                </div>
                                            </td>
                                        </tr>
                                        {d.bienThe?.map((dd, btIndex) => (
                                            <tr
                                                key={`bonus-variant-${index}-${btIndex}`}
                                                className="border-t hover:bg-gray-100 transition"
                                            >
                                                <td className="p-3">
                                                    <div className="flex items-center">
                                                        <img
                                                            src={dd.hinhAnh}
                                                            alt={dd.ten}
                                                            className="w-12 h-12 object-cover rounded-md mr-3"
                                                        />
                                                        <div className="flex items-center">
                                                            <span>{dd.ten}</span>
                                                            {(dd.tangKemChinh?.length > 0 || dd.tangKemPhu?.length > 0) && (
                                                                <svg
                                                                    className="ml-2 w-5 h-5 text-red-500 cursor-pointer hover:scale-110 transition"
                                                                    viewBox="0 0 24 24"
                                                                    fill="currentColor"
                                                                    xmlns="http://www.w3.org/2000/svg"
                                                                    onClick={() => {
                                                                        setDanhSachTrung({
                                                                            data: dd.tangKemChinh,
                                                                            dataPhu: dd.tangKemPhu
                                                                        })
                                                                        setOpenTrung(true)
                                                                    }}
                                                                >
                                                                    <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z" />
                                                                </svg>
                                                            )}
                                                        </div>
                                                    </div>
                                                </td>
                                                <td className="p-3 text-center">{formatToVND(dd.gia)}</td>
                                                <td className="p-3 text-center">
                                                    <input
                                                        type="number"
                                                        defaultValue={dd?.soLuongKhuyenMai || 0}
                                                        onChange={(e) => {
                                                            const newSoLuongKhuyenMai = parseInt(e.target.value);
                                                            if (newSoLuongKhuyenMai <= 99) {
                                                                const updatedList = [...danhSachTangKem];
                                                                updatedList[index] = { ...updatedList[index] };
                                                                updatedList[index].bienThe = [...updatedList[index].bienThe];
                                                                updatedList[index].bienThe[btIndex] = {
                                                                    ...updatedList[index].bienThe[btIndex],
                                                                    soLuongKhuyenMai: newSoLuongKhuyenMai,
                                                                };
                                                                setDanhSachTangKem(updatedList);
                                                            }
                                                        }}
                                                        className="w-20 border border-gray-300 rounded-md px-2 py-1 text-center focus:ring-2 focus:ring-blue-500 outline-none"
                                                        min="0"
                                                        placeholder="Số lượng"
                                                    />
                                                </td>
                                                <td className="p-3 text-center">{dd.soLuongKho}</td>
                                            </tr>
                                        ))}
                                    </>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Modals */}
            {openPickMain && (
                <Modal setOpen={setOpenPickMain}>
                    <PickProduct
                        danhSachChon={danhSachChinh}
                        filter={danhSachTangKem}
                        setDanhSachChon={setDanhSachChinh}
                        task="bonus"
                    />
                </Modal>
            )}
            {openPickBonus && (
                <Modal setOpen={setOpenPickBonus}>
                    <PickProduct
                        danhSachChon={danhSachTangKem}
                        filter={danhSachChinh}
                        setDanhSachChon={setDanhSachTangKem}
                        task="bonus"
                    />
                </Modal>
            )}
            {
                        openTrung ? <Modal setOpen={setOpenTrung}>
                             <div className="w-[800px] rounded-sm">
                                <strong><p className="text-xl text-blue-700 text-center">Thông tin Lưu ý</p></strong>
                                    <section className="mb-8 w-full">
                                        <p className="text-lg font-semibold mb-3 text-blue-700">Vai trò deal chính</p>
                                        <div className="overflow-x-auto">
                                            <table className="min-w-full text-sm text-left text-gray-800 border">
                                                <thead className="bg-gray-200 font-semibold">
                                                    <tr>
                                                        <th className="py-2 px-3 border">STT</th>
                                                        <th className="py-2 px-3 border">Thời gian chạy deal</th>
                                                        <th className="py-2 px-3 border">Thời gian ngưng deal</th>
                                                        <th className="py-2 px-3 border">Số lượt giới hạn</th>
                                                        <th className="py-2 px-3 border">Số lượt đã dùng</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {danhSachTrung?.data?.length === 0 ? (
                                                        <tr>
                                                            <td colSpan={5} className="text-center italic text-gray-500 py-4">Không có dữ liệu</td>
                                                        </tr>
                                                    ) : (
                                                        danhSachTrung?.data?.map((data, index) => (
                                                            <tr key={index} className="hover:bg-gray-100">
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
                                        </div>
                                    </section>
                                    <section>
                                        <p className="text-lg font-semibold mb-3 text-blue-700">Vai trò deal phụ ( giảm giá )</p>
                                        <div className="overflow-x-auto">
                                            <table className="min-w-full text-sm text-left text-gray-800 border">
                                                <thead className="bg-gray-200 font-semibold">
                                                    <tr>
                                                        <th className="py-2 px-3 border">STT</th>
                                                        <th className="py-2 px-3 border">Thời gian chạy deal</th>
                                                        <th className="py-2 px-3 border">Thời gian ngưng deal</th>
                                                        <th className="py-2 px-3 border">Số lượt giới hạn</th>
                                                        <th className="py-2 px-3 border">Số lượt đã dùng</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {danhSachTrung?.dataPhu?.length === 0 ? (
                                                        <tr>
                                                            <td colSpan={5} className="text-center italic text-gray-500 py-4">Không có dữ liệu</td>
                                                        </tr>
                                                    ) : (
                                                        danhSachTrung?.dataPhu?.map((data, index) => (
                                                            <tr key={index} className="hover:bg-gray-100">
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
                                        </div>
                                    </section>
                                </div>
                        </Modal> : null
                    }
        </div>
    );
}

export { AddBonus };
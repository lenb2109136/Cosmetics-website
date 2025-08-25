import { useEffect, useState } from "react"
import Modal from "../commons/modal"
import PickCategory from "../commons/PickCategory"
import { formatToVND } from "../../utils/Format"
import { getCreateFlashSale } from "../../services/sanPhamService"
import { toast } from "react-toastify"

function isIdInAnyList(id, listA) {
    return Array.isArray(listA) && listA.some(item => item?.id === id);
}
function reverse(data) {
    return {
        ten: data.ten,
        hinhAnh: data.anhSanPham,
        id: data.id,
        bienThe: data.bienThe
    }
}

function PickProduct({ danhSachChon, setDanhSachChon, filter, task }) {
    const [danhSach, setDanhSach] = useState([])
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(1)
    const [openPickCategory, setOpenPickCategory] = useState(false)
    const [category, setcategoryPick] = useState([])
    const [chuoi, setChuoi] = useState("")
    const [ten, setTen] = useState("")

    const fetchData = (pageNumber = 0) => {
        const id = category?.[category.length - 1]?.id || 0;
        getCreateFlashSale(id, ten, pageNumber, task)
            .then(res => {
                const allItems = res?.content || [];
                const filterIds = new Set(filter.map(item => item.id));
                const filteredItems = allItems.filter(item => !filterIds.has(item.id));
                setDanhSach(filteredItems);
                setTotalPages(res?.totalPages || 1);
                setPage(res?.number || 0);
            })
            .catch(() => {
                toast.error("Lấy dữ liệu khởi tạo thất bại");
            });
    };


    useEffect(() => {
        fetchData()
    }, [])

    return (
        <div className="p-4 max-h-[80vh] overflow-auto min-w-[60vw]">
            <p className="text-lg font-semibold mb-4">Chọn sản phẩm</p>

            {/* Bộ lọc */}
            <div className="flex flex-row gap-4 mb-4">
                <div>
                    <div className="font-medium mb-1">Chọn ngành hàng:</div>
                    <input
                        readOnly
                        className="outline-none border px-2 py-1 rounded cursor-pointer"
                        onClick={() => setOpenPickCategory(true)}
                        value={chuoi || "Tất cả ngành hàng"}
                    />
                </div>
                <div>
                    <div className="font-medium mb-1">Tìm tên sản phẩm:</div>
                    <input
                        value={ten}
                        onChange={(e) => setTen(e.target.value)}
                        className="outline-none border px-2 py-1 rounded"
                        placeholder="Nhập tên sản phẩm..."
                    />
                </div>
                <div className="flex items-end">
                    <button
                        className="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600"
                        onClick={() => fetchData(0)}
                    >
                        Xác nhận
                    </button>
                </div>
            </div>

            {/* Bảng sản phẩm */}
            <div className="overflow-x-auto border rounded-md">
                <table className="w-full text-sm">
                    <thead className="bg-gray-100 text-left">
                        <tr >
                            <th className="p-3">
                                <div className="flex items-center gap-2">
                                    <input className="cursor-pointer" type="checkbox" />
                                    <span>Sản Phẩm</span>
                                </div>
                            </th>
                            <th className="p-3">Giá</th>
                            <th className="p-3">Kho hàng</th>
                        </tr>
                    </thead>
                    <tbody>
                        {danhSach.map((d, index) => (
                            <tr key={index} className={`hover:bg-gray-50 border-t bg-white 
                                `}>
                                <td className="p-3">
                                    <div className="flex items-center gap-3">
                                        <input
                                            checked={isIdInAnyList(d.id, danhSachChon)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setDanhSachChon([...(danhSachChon || []), reverse(d)]);
                                                } else {
                                                    setDanhSachChon(prev => prev.filter(item => item.id !== d.id))
                                                }
                                            }}
                                            className="cursor-pointer"
                                            type="checkbox"
                                        />
                                        <img
                                            src={d?.anhSanPham}
                                            alt="Ảnh sản phẩm"
                                            className="w-10 h-10 object-cover border rounded"
                                        />
                                        <div>
                                            <div className={`font-medium ${d.disabled ? 'text-gray-400' : ''}`}>{d.ten}</div>
                                            <div className="text-xs text-gray-500">Mã: {d.maSp}</div>
                                        </div>
                                    </div>
                                </td>
                                <td className={`p-3 ${d.disabled ? 'text-gray-400' : ''}`}>
                                    {d.giaMin == d.GiaMax
                                        ? formatToVND(d.giaMin)
                                        : `${formatToVND(d.giaMin)} - ${formatToVND(d.GiaMax)}`
                                    }
                                </td>
                                <td className={`p-3 ${d.disabled ? 'text-gray-400' : ''}`}>
                                    {d.Tongkho}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Phân trang */}
            <div className="flex justify-between mt-4">
                <button
                    disabled={page === 0}
                    onClick={() => fetchData(page - 1)}
                    className="px-3 py-1 border rounded disabled:opacity-50"
                >
                    Trang trước
                </button>
                <span>Trang {page + 1} / {totalPages}</span>
                <button
                    disabled={page + 1 >= totalPages}
                    onClick={() => fetchData(page + 1)}
                    className="px-3 py-1 border rounded disabled:opacity-50"
                >
                    Trang sau
                </button>
            </div>

            {/* Modal chọn ngành hàng */}
            {openPickCategory && (
                <Modal setOpen={setOpenPickCategory}>
                    <PickCategory
                        categoryPick={category}
                        setcategoryPick={setcategoryPick}
                        setChuoi={setChuoi}
                        setOpen={setOpenPickCategory}
                    />
                </Modal>
            )}
        </div>
    )
}

export { PickProduct }

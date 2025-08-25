import { useEffect, useState } from "react";
import Modal from "../../components/commons/modal";
import PickCategory from "../../components/commons/PickCategory";
import { getManager } from "../../services/sanPhamService";
import { Pagination } from "../../components/commons/Pagination";

import { useNavigate } from "react-router-dom";
import { formatToVND } from "../../utils/Format";

function QuanLySanPham() {
    const [openPickCategory, setOpenPickCategory] = useState(false);
    const [category, setcategoryPick] = useState([]);
    const [chuoi, setChuoi] = useState("");
    const [ten, setTen] = useState("");
    const [conSuDung, setConSuDung] = useState(true);
    const [hetHang, setHetHang] = useState(0);
    const [data, setData] = useState([]);
    const [trang, setTrang] = useState(0);
    const [tong, setTong] = useState(0)
    const [tongTrang, setTongTrang] = useState(0)
    useEffect(() => {
        getManager(category?.[category?.length - 1]?.id || 0, trang, ten, conSuDung, hetHang).then(data => {
            setData(data.data)
            setTong(data.totalPages)
            setTongTrang(data.totalPages)
        });
    }, [hetHang, category, ten, trang, conSuDung]);
    const navigate = useNavigate()

    return (
        <div>
            <p className="text-center text-2xl text-blue-700 mt-4 mb-3">
                <strong>Quản lý sản phẩm</strong>
            </p>
            <hr className="w-[20%] mx-auto pt-3 text-blue-400"></hr>
            <div className="flex flex-wrap justify-between gap-4 mb-4">
                <div className="flex-1 min-w-[200px]">
                    <div className="font-medium mb-1">Chọn ngành hàng:</div>
                    <input
                        readOnly
                        className="w-full outline-none border px-2 py-1 rounded cursor-pointer"
                        onClick={() => setOpenPickCategory(true)}
                        value={chuoi || "Tất cả ngành hàng"}
                    />
                </div>

                <div className="flex-1 min-w-[200px]">
                    <div className="font-medium mb-1">Tìm tên sản phẩm:</div>
                    <input
                        value={ten}
                        onChange={(e) => setTen(e.target.value)}
                        className="w-full outline-none border px-2 py-1 rounded"
                        placeholder="Nhập tên sản phẩm..."
                    />
                </div>

                <div className="flex-1 min-w-[180px]">
                    <div className="font-medium mb-1">Trạng thái sản phẩm:</div>
                    <p
                        onClick={() => setConSuDung(!conSuDung)}
                        className={`p-1 ${conSuDung ? "bg-green-500" : "bg-gray-300"} text-white rounded-md text-center cursor-pointer`}
                    >
                        <strong>Hoạt động</strong>
                    </p>
                </div>

                <div className="flex-1 min-w-[180px]">
                    <div className="font-medium mb-1">Tình trạng hàng:</div>
                    <select
                        value={hetHang}
                        onChange={(e) => setHetHang(parseInt(e.target.value))}
                        className="w-full outline-none border px-2 py-1 rounded bg-white cursor-pointer appearance-none focus:ring-2 focus:ring-blue-500 transition duration-200"
                        style={{
                            paddingRight: "2rem",
                            backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%234B5563' stroke-width='2'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' d='M19 9l-7 7-7-7'/%3E%3C/svg%3E")`,
                            backgroundRepeat: "no-repeat",
                            backgroundPosition: "right 0.5rem center",
                            backgroundSize: "1.5em",
                        }}
                    >
                        <option value={0}>Tất cả</option>
                        <option value={2}>Hết hàng</option>
                        <option value={1}>Sắp hết hàng</option>
                    </select>
                </div>

                <div className="flex items-end">
                    <button className="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600">
                        Xác nhận
                    </button>
                </div>
            </div>

            <div>
                <strong><p className="p-2">Tổng số: {tong}  sản phẩm</p></strong>
                <table className="w-full border-collapse table-auto">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="p-2 border">Sản Phẩm</th>
                            <th className="p-2 border">Phân loại</th>
                            <th className="p-2 border">Giá</th>
                            <th className="p-2 border">Số lượng kho</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data?.map((d, i) => {
                            const first = d?.bienThe?.[0];
                            const rest = d?.bienThe?.slice(1) || [];

                            return (
                                <>
                                    <tr key={`sp-${i}-0`} className="border">
                                        <td className="p-2 border" rowSpan={d?.bienThe?.length}>
                                            <div className="flex items-center gap-2">
                                                <img onClick={() => navigate("update?id=" + d.id)} className="w-[40px] h-[40px] object-cover cursor-pointer rounded" src={d.anhGioiThieu} alt="" />
                                                <p className="font-medium hover:text-blue-300 cursor-pointer" onClick={() => navigate("thongke?id=" + d.id)}>{d.ten}</p>
                                            </div>
                                        </td>
                                        <td className="p-2 border">
                                            <div className="relative">
                                                {first.notUpdate ? <div className="border border-red-500 absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[1px] bg-white bg-opacity-60 rounded">
                                                    <span className="text-red-500 font-semibold">Không còn sử dụng</span>
                                                </div> : null}
                                                <div className="flex items-center gap-2">
                                                    <img className="w-[40px] h-[40px] object-cover rounded" src={first.anhGioiThieu} alt="" />
                                                    <p>{first.ten}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="p-2 border">{formatToVND(first.gia)}</td>
                                        <td className="p-2 border">
                                            <div className="flex flex-row">
                                                <span className="basis-1/6">{first.soLuongKho}</span>
                                                {first.soLuongKho <= 25 && first.soLuongKho > 0 ? <span className="bg-orange-500 p-1 rounded-sm text-white"><strong>Sắp hết hàng</strong></span> : null}
                                                {first.soLuongKho <= 0 ? <span className="bg-red-400 p-1 rounded-sm text-white"><strong>Hết hàng</strong></span> : null}
                                                {first.soLuongKho > 25 ? <span className="bg-green-600 p-1 rounded-sm text-white"><strong>Còn hàng</strong></span> : null}
                                            </div>
                                        </td>
                                    </tr>

                                    {rest.map((f, index) => (
                                        <tr key={`sp-${i}-${index + 1}`} className="border">
                                            <td className="p-2 border">
                                                <div className="relative">
                                                    {f.notUpdate ? <div className="border border-red-500 absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[1px] bg-white bg-opacity-60 rounded">
                                                        <span className="text-red-500 font-semibold">Không còn sử dụng</span>
                                                    </div> : null}
                                                    <div className="flex items-center gap-2">
                                                        <img className="w-[40px] h-[40px] object-cover rounded" src={f.anhGioiThieu} alt="" />
                                                        <p>{f.ten}</p>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="p-2 border">{formatToVND(f.gia)}</td>
                                            <td className="p-2">
                                                <div className="flex flex-row">
                                                    <span className="basis-1/6">{f.soLuongKho}</span>
                                                    {f.soLuongKho <= 25 && f.soLuongKho > 0 ? <span className="bg-orange-500 p-1 rounded-sm text-white"><strong>Sắp hết hàng</strong></span> : null}
                                                    {f.soLuongKho <= 0 ? <span className="bg-red-400 p-1 rounded-sm text-white"><strong>Hết hàng</strong></span> : null}
                                                    {f.soLuongKho > 25 ? <span className="bg-green-600 p-1 rounded-sm text-white"><strong>Còn hàng</strong></span> : null}
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </>
                            );
                        })}
                    </tbody>
                </table>
                <div className="flex justify-between items-center mt-6">
                    <div></div>
                    <Pagination
                        trangHienTai={trang}
                        setTrangHienTai={setTrang}
                        soLuongTrang={tong}
                    />
                </div>
            </div>
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
    );
}

export { QuanLySanPham };
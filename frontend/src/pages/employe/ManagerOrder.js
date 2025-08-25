import { useEffect, useState } from "react";
import { getOrderByStatus } from "../../services/OrderService";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import { useNavigate } from "react-router-dom";
import { AddHoaDon } from "./AddHoaDon";
function ManagerOrder() {
    const navigate = useNavigate();
    const [status, setStatus] = useState([
        { id: 3, name: "Chờ xác nhận" },
        { id: 2, name: "Đã xác nhận" },
        { id: 1, name: "Thành công" },
        { id: 6, name: "Yêu cầu hoàn hàng" },
        { id: 7, name: "Đã xác nhận hoàn hàng" },
        { id: 8, name: "Hoàn thành công" },

        { id: 10, name: "Hoàn thất bại" },
        { id: 4, name: "Đơn hủy" },
        { id: 9, name: "Đơn hủy bởi cửa hàng" },
        { id: 11, name: "Thất bại" },
    ]);
    const [activeId, setActiveId] = useState(1);
    const [data, setData] = useState([]);

    // useEffect(() => {
    //     getOrderByStatus(activeId)
    //         .then((dat) => setData(dat))
    //         .catch(() => toast.error("Lấy dữ liệu thất bại"));
    // }, [activeId]);


    return (
        <div className="p-4">
            {/* <h2 className="text-2xl font-bold">Quản lý hóa đơn</h2>
            <div className="flex gap-4 mt-4 border-b-2 justify-around">
                {status.map((state) => (
                    <button
                        key={state.id}
                        onClick={() => setActiveId(state.id)}
                        className={`pb-2 transition-colors duration-300 text-sm font-medium 
                            ${activeId === state.id ? "text-green-600 border-b-2 border-green-600" : "text-gray-500 hover:text-green-600"}
                        `}
                    >
                        {state.name}
                    </button>
                ))}
            </div>
            <table className="w-full mt-4 border-collapse">
    <thead>
        <tr className="bg-green-900 text-green-200 text-center">
            <th className="px-4 py-2">STT</th>
            <th className="px-4 py-2">Ngày đặt hàng</th>
            <th className="px-4 py-2">Tổng Tiền thanh toán</th>
            <th className="px-4 py-2">Tổng số mặt hàng</th>
            <th className="px-4 py-2">Tổng số lượng hàng hóa</th>
        </tr>
    </thead>
    <tbody>
        {data?.map((item, index) => (
            <tr onClick={()=>{
                navigate(`/customer/updateorder?id=${item.id}`)
            }} key={item.id} className="text-center even:bg-gray-100 cursor-pointer hover:bg-green-50 transition-all duration-400">
                <td className="px-4 py-2">{index + 1}</td>
                <td className="px-4 py-2">{dinhDangNgay(item?.ngayLap)}</td>
                <td className="px-4 py-2">{formatToVND(item.tongTien)}</td>
                <td className="px-4 py-2">{item.tongSoMatHang}</td>
                <td className="px-4 py-2">{item.tongSoLuongHang}</td>
            </tr>
        ))}
    </tbody>
</table> */}
<AddHoaDon></AddHoaDon>
        </div>
    );
}
export { ManagerOrder }
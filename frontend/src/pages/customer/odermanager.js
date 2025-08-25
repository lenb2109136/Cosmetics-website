

import { useEffect, useRef, useState } from "react";
import { getOrderByStatus } from "../../services/OrderService";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import { useNavigate } from "react-router-dom";
import Modal from "../../components/commons/modal";
import { QuetMaThanhToan } from "./Payment";
import { Pagination } from "../../components/commons/Pagination";

// Modal xác nhận
function ConfirmModal({ id, setOpen, onConfirm }) {
  return (
    <Modal>
      <div className="bg-white p-4 rounded">
        <h3 className="text-md font-bold mb-4 text-green-900">Xác nhận lập hóa đơn</h3>
        <p>Thực hiện xác nhận với hóa đơn ?</p>
        <div className="flex justify-end mt-4">
          <button
            className="px-4 py-2 bg-green-600 text-white rounded mr-2"
            onClick={onConfirm}
          >
            Đồng ý
          </button>
          <button
            className="px-4 py-2 bg-gray-300 text-black rounded"
            onClick={() => setOpen(false)}
          >
            Đóng
          </button>
        </div>
      </div>
    </Modal>
  );
}

function OrderManager() {
  const navigate = useNavigate();
  const [status, setStatus] = useState([
    { id: 3, name: "Chờ xác nhận" },
    { id: 2, name: "Đã xác nhận" },
    { id: 1000, name: "Đang vận chuyển" },
    { id: 1, name: "Thành công" },
    // { id: 6, name: "Yêu cầu hoàn hàng" },
    // { id: 7, name: "Đã xác nhận hoàn hàng" },
    // { id: 8, name: "Hoàn thành công" },
    // { id: 10, name: "Hoàn thất bại" },
    { id: 4, name: "Đơn hủy" },
    { id: 9, name: "Đơn hủy bởi cửa hàng" },
    // { id: 11, name: "Thất bại" },
     { id: 16, name: "Đang hoàn đơn" },
      { id: 14, name: "Hoàn đơn chi trả" },
      { id: 15, name: "Hoàn đơn không chi trả" },

  ]);
  const [activeId, setActiveId] = useState(1);
  const [data, setData] = useState([]);
  const [openConfirm, setOpenConfirm] = useState(false);
  const [openQR, setOpenQR] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
   const sortRef = useRef(null);
    const handleSortSelect = (value) => {
    setFilter((prev) => ({ ...prev, sort: value }));
    setOpenSortDropdown(false);
  };
  const [tongSoTrang,setTongSoTrang]=useState(0)
  const [trang,setTrang]=useState(0)
  const [openSortDropdown, setOpenSortDropdown] = useState(false);
  const [filter, setFilter] = useState({
    maHoaDon: "",
    tenNguoiDung: "",
    ngayLap: "",
    sort: 1,
  });
  
  
  useEffect(() => {
    getOrderByStatus(activeId, filter,trang)
      .then((dat) => {
        setData(dat.content)
        setTongSoTrang(dat.totalPages)
      })
      .catch(() => toast.error("Lấy dữ liệu thất bại"));
  }, [activeId,filter,trang]);
  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">Quản lý hóa đơn</h2>
      <div className="flex flex-row gap-4 mb-4">
        <div className="flex flex-col flex-1">
          <div className="mb-2">
            <i className="fa-solid fa-barcode mr-1 text-green-900 border shadow-md rounded-md p-2 mb-2"></i>
            <span>Tên sản phẩm:</span>
          </div>
          <input
            name="maHoaDon"
            value={filter.maHoaDon}
              onChange={(e)=>{
                setFilter({
                  ...filter,
                  maHoaDon:e.target.value
                })
            }}
            className="border border-green-900 rounded-md px-3 py-2 text-sm outline-none "
            placeholder="Nhập mã hóa đơn"
          />
        </div>
         <div className="flex flex-col flex-1">
          <div className="mb-2">
            <i className="fa-regular fa-clock mr-1 text-green-900 border shadow-md rounded-md p-2 mb-2"></i>
            <span>Ngày lập:</span>
          </div>
          <input
            name="ngayLap"
            type="date"
            value={filter.ngayLap}
            onChange={(e)=>{
                setFilter({
                  ...filter,
                  ngayLap:e.target.value
                })
            }}
            className="border border-green-900 rounded-md px-3 py-2 text-sm outline-none"
          />
        </div>
        {/* <div className="flex flex-col flex-1 relative z-50">
          <div className="mb-2">
            <i className="fa-solid fa-arrow-up-short-wide mr-1 text-green-900 border shadow-md rounded-md p-2"></i>
            <span>Sắp xếp theo:</span>
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
                    className={`py-1 px-2 hover:bg-green-900 hover:text-white cursor-pointer ${filter.sort === option.value ? "bg-green-900 text-white" : ""
                      }`}
                  >
                    {option.label}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div> */}
      </div>
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
      <div>
         <Pagination trangHienTai={trang} setTrangHienTai={setTrang} soLuongTrang={tongSoTrang} color="bg-green-900"></Pagination>
      </div>
      <div className="mt-4 flex flex-wrap gap-4">
       
        {data?.map((item, index) => (
          <div
            key={item.id}
            className="p-4 w-[calc(50%-0.5rem)] bg-white rounded-lg shadow-md border border-gray-300 cursor-pointer hover:shadow-lg transition-shadow duration-300"
          // 
          >
            <div className="flex justify-between items-center border-b pb-2 border-gray-200">
              <div>
                <p onClick={() => navigate(`/customer/updateorder?id=${item.id}`)} className="font-bold text-lg cursor-pointer text-gray-800">skinlyI{item.id}</p>
                <div className="flex items-center text-sm text-gray-600 space-x-2 mt-1">
                  <span>Khách hàng</span>
                  <span className="w-1 h-1 bg-black rounded-full inline-block"></span>
                  <span>{dinhDangNgay(item.ngayLap)}</span>
                </div>
              </div>
              <div className="flex space-x-6 text-right">
                <div>
                  <p className="text-sm text-gray-500">Sản phẩm</p>
                  <p className="text-md font-semibold text-gray-800">{item.tongSoMatHang}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Tổng tiền</p>
                  <p className="text-md font-semibold text-green-600">{formatToVND(item.tongTien)}</p>
                </div>
              </div>
            </div>
            <div>
              <span className="text-sm text-gray-500 block mb-2">Sản phẩm</span>
              <div className="flex gap-4 flex-wrap">
                {item.danhSachMatHang.slice(0, 2).map((product, idx) => (
                  <div
                    key={idx}
                    className="border border-gray-200 rounded-md p-3 min-w-[220px] bg-white shadow-sm"
                  >
                    <p className="text-sm font-medium text-gray-800 truncate" title={product.tenSanPham}>
                      {product.tenSanPham.length > 30 ? product.tenSanPham.substring(0, 27) + "..." : product.tenSanPham}
                    </p>
                    <div className="text-xs text-gray-600 flex flex-wrap gap-2 mt-1">
                      <span>SL: {product.soLuong}</span>
                      <span>Phân loại: {product.tenPhanLoai.length > 20 ? product.tenPhanLoai.substring(0, 17) + "..." : product.tenPhanLoai}</span>
                      <span className="text-right font-semibold text-gray-800 ml-auto">{formatToVND(product.donGia)}</span>
                    </div>
                  </div>
                ))}
                {item.danhSachMatHang.length > 2 && (
                  <div className="flex items-center text-sm text-gray-500">
                    ...
                  </div>
                )}
              </div>
            </div>
            <div className="flex justify-between items-center pt-2">
              <div>
                {
                  activeId == 1000 ? <p className="text-sm text-gray-600">
                    Phí giao hàng: <span className="text-orange-500 font-semibold">{formatToVND(item.tongPhiGiaoHang)} </span>
                    | Dự kiến giao <span className="text-orange-500 font-semibold">{dinhDangNgay(item.thoiGianDuKienGiao)}</span>
                  </p> : null
                }
              </div>
              <div className="flex items-center">
                {activeId === 2 && !item.isThanhToan ? (
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      setSelectedId(item.id);
                      setOpenConfirm(true);
                    }}
                    className={`px-2 py-1  rounded ${item?.isChange ? " bg-green-600 text-white " :"bg-gray-300 text-gray-500"}`} 
                  >
                    Thanh toán
                  </button>
                ) : activeId === 2 && item.isThanhToan ? (
                  <div className="flex bg-green-100 p-1 rounded-md pointer-events-none">
                    <img className="w-5" src="https://cdn-icons-png.flaticon.com/128/7518/7518748.png" alt="Paid" />
                  </div>
                ) : (
                  <div className="px-2 py-1 bg-green-50 text-green-800 text-sm rounded-lg">
                    <p className="text-green-700">{item.isThanhToan ? "Đã thanh toán" : <p >Chưa thanh toán</p>}</p>
                    { }
                  </div>
                )}
                
                {
                  (item?.daHoanTien == false && activeId == 9) ? <div className="px-2 ml-2 py-1 bg-yellow-50 text-green-800 text-sm rounded-lg">
                    <p className="text-yellow-400 "> <p >{item?.daHoanTien == false ? "Chưa hoàn trả" : null}</p></p>
                  </div> : null
                }
                
              </div>
            </div>
          </div>
        ))}
      </div>
      {openConfirm && (
        <ConfirmModal
          id={selectedId}
          setOpen={setOpenConfirm}
          onConfirm={() => {
            setOpenConfirm(false);
            setOpenQR(true);
          }}
        />
      )}
      {openQR && selectedId != null ? <QuetMaThanhToan da={selectedId} setOpen={setOpenQR} /> : null}
    </div>
  );
}

export { OrderManager };
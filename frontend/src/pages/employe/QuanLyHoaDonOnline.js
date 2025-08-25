import { useState, useRef, useEffect } from "react";
import { duyet, getByStatusOnline, getURLprintOrder, hoanDonAfter, xacNhanHoangHang, changeDongHop, hoanDonV2, reCreateGHN } from "../../services/OrderService";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import { Pagination } from "../../components/commons/Pagination";
import { useNavigate, useSearchParams } from "react-router-dom";
import { toast } from "react-toastify";
import { getAllDongHop } from "../../services/trangthaiservice";

function QuanLyHoaDonOnline() {
  const [status, setStatus] = useState([
    { id: 3, name: "Chờ xác nhận" },
    { id: 2, name: "Đã xác nhận" },
    { id: 13, name: "Chờ lấy" },
    { id: 12, name: "Đang giao" },
    { id: 1, name: "Hoàn tất" },
  ]);
  const [load,setLoad]=useState(false)

  const [statusGroups, setStatusGroups] = useState([
    {
      id: "cancelled",
      name: "Đơn hủy",
      statuses: [
        { id: 9, name: "Hủy bởi cửa hàng" },
        { id: 4, name: "Hủy bởi khách hàng" },
      ],
      defaultStatus: 9,
    },
    {
      id: "returning",
      name: "Hoàn đơn",
      statuses: [
        { id: 16, name: "Đang hoàn đơn" },
        { id: 17, name: "Đã hoàn đơn" },
      ],
      defaultStatus: 16,
    },
    {
      id: "return_processed",
      name: "Hoàn xử lý",
      statuses: [
        { id: 14, name: "Hoàn chi trả đơn" },
        { id: 15, name: "Hoàn không chi trả" },
      ],
      defaultStatus: 14,
    },
  ]);

  const [danhSachChon, setDanhSachChon] = useState([]);
  const navigate = useNavigate();
  const [trang, setTrang] = useState(0);
  const [data, setData] = useState([]);
  const [tongTrang, setTongTrang] = useState(0);
  const [tongSoPhanTu, setTongSoPhanTu] = useState(0);
  const [activeId, setActiveId] = useState(0);
  const [activeGroup, setActiveGroup] = useState(null);
  const [filter, setFilter] = useState({
    maHoaDon: "",
    tenNguoiDung: "",
    ngayLap: "",
    sort: 1,
  });
  const [searchParams, setSearchParams] = useSearchParams();
  const [dsDongHop, setDanhSachDongHop] = useState([]);

  // Modal state for adjusting shipping info
  const [openModalDieuChinh, setOpenModalDieuChinh] = useState(false);
  const [selectedOrderId, setSelectedOrderId] = useState(null);
  const [khoiLuong, setKhoiLuong] = useState(0);
  const [selectedDongGoiId, setSelectedDongGoiId] = useState(null);
  const [openDongGoiDropdown, setOpenDongGoiDropdown] = useState(false);
  const dongGoiRef = useRef(null);

  // Loading state for API calls
  const [isLoading, setIsLoading] = useState(false);

  const [openModalThanhCong, setOpenModalThanhCong] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [openModalXacNhanHoanTien, setOpenModalXacNhanHoanTien] = useState(false);
  const [donIdToHoanTien, setDonIdToHoanTien] = useState(null);
  const [openModalLyDo, setOpenModalLyDo] = useState(false);
  const [selectedReason, setSelectedReason] = useState("");
  const [openModalXacNhanHoan, setOpenModalXacNhanHoan] = useState(false);
  const [donIdToXacNhan, setDonIdToXacNhan] = useState(null);
  // State for radio buttons in confirmation modal
  const [hoanChiTra, setHoanChiTra] = useState(null); // 0 for Hoàn chi trả, 1 for Hoàn không chi trả
  const [traTien, setTraTien] = useState(null); // 0 for Trả luôn, 1 for Trả sau

  useEffect(() => {
    const type = searchParams.get("active");
    const group = searchParams.get("group");

    if (group) {
      setActiveGroup(group);
      if (type !== null) {
        const id = parseInt(type);
        if (!isNaN(id)) {
          setActiveId(id);
        } else {
          const defaultId = statusGroups.find((g) => g.id === group)?.defaultStatus;
          setActiveId(defaultId);
          setSearchParams({ group, active: defaultId.toString() });
        }
      } else {
        const defaultId = statusGroups.find((g) => g.id === group)?.defaultStatus;
        setActiveId(defaultId);
        setSearchParams({ group, active: defaultId.toString() });
      }
    } else {
      if (type !== null) {
        const id = parseInt(type);
        if (!isNaN(id)) {
          setActiveId(id);
        } else {
          setActiveId(0);
          setSearchParams({ active: "0" });
        }
      } else {
        setActiveId(0);
        setSearchParams({ active: "0" });
      }
      setActiveGroup(null);
    }
    getAllDongHop()
      .then((d) => {
        setDanhSachDongHop(d);
      })
      .catch(() => {
        toast.error("Không thể tải danh sách dạng đóng hộp");
      });
  }, []);

  useEffect(() => {
    let a = {
      maHoaDon: filter.maHoaDon,
      khachHang: filter.tenNguoiDung,
      trangThai: activeId,
      ngayLap: filter.ngayLap,
      sort: filter.sort,
      trang: trang,
    };
    getByStatusOnline(a).then((d) => {
      setData(d?.danhSach);
      setTongSoPhanTu(d?.tongSoPhanTu);
      setTongTrang(d?.tongTrang);
      setDanhSachChon([]);
    });
  }, [filter, trang, activeId]);

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

  const handleCheckboxChange = (id) => {
    setDanhSachChon((prev) => {
      if (prev.includes(id)) {
        return prev.filter((itemId) => itemId !== id);
      } else {
        return [...prev, id];
      }
    });
  };

  const handleSelectAll = () => {
    const approvedItems = data
      .filter((item) => item.duocDuyet === true)
      .map((item) => item.id);

    if (danhSachChon.length === approvedItems.length) {
      setDanhSachChon([]);
    } else {
      setDanhSachChon(approvedItems);
    }
  };


  const handleDuyetDon = () => {
    if (danhSachChon.length === 0) {
      toast.error("Vui lòng chọn ít nhất một đơn hàng để duyệt!");
      return;
    }
    duyet(danhSachChon)
      .then(() => {
        setSuccessMessage("Xác nhận duyệt đơn thành công");
        setOpenModalThanhCong(true);
        setDanhSachChon([]);
        let a = {
          maHoaDon: filter.maHoaDon,
          khachHang: filter.tenNguoiDung,
          trangThai: activeId,
          ngayLap: filter.ngayLap,
          sort: filter.sort,
          trang: trang,
        };
        getByStatusOnline(a).then((d) => {
          setData(d?.danhSach);
          setTongSoPhanTu(d?.tongSoPhanTu);
          setTongTrang(d?.tongTrang);
        });
      })
      .catch((e) => {
        toast.error(e?.response?.data?.message || "Duyệt đơn thất bại");
      });
  };

  const handleHoanTien = () => {
    if (donIdToHoanTien) {
      hoanDonAfter(donIdToHoanTien)
        .then(() => {
          setSuccessMessage("Đơn hàng đã được chi trả thành công");
          setOpenModalThanhCong(true);
          setOpenModalXacNhanHoanTien(false);
          let a = {
            maHoaDon: filter.maHoaDon,
            khachHang: filter.tenNguoiDung,
            trangThai: activeId,
            ngayLap: filter.ngayLap,
            sort: filter.sort,
            trang: trang,
          };
          getByStatusOnline(a).then((d) => {
            setData(d?.danhSach);
            setTongSoPhanTu(d?.tongSoPhanTu);
            setTongTrang(d?.tongTrang);
          });
        })
        .catch((e) => {
          toast.error(e?.response?.data?.message || "Hoàn tiền thất bại");
          setOpenModalXacNhanHoanTien(false);
        });
    }
  };

  const handleXemLyDo = (item) => {
    setSelectedReason(item.lyDo || "Không có lý do được cung cấp");
    setOpenModalLyDo(true);
  };

  const handleXacNhanHoanHang = () => {
    if (donIdToXacNhan) {
      if (hoanChiTra === null) {
        toast.error("Vui lòng chọn loại hoàn đơn!");
        return;
      }
      if (hoanChiTra === 0 && traTien === null) {
        toast.error("Vui lòng chọn hình thức trả tiền!");
        return;
      }
      setIsLoading(true);

      const params = hoanChiTra === 0
        ? [donIdToXacNhan, hoanChiTra, traTien]
        : [donIdToXacNhan, hoanChiTra, 0];
      let t = {
        donIdToXacNhan: params[0],
        hoanChiTra: params[1],
        traTien: params[2]
      }
      hoanDonV2(t)
        .then((response) => {
          setIsLoading(false);
          if (response.status === 200) {
            toast.success("Xác nhận thành công!");
          } else if (response.status === 500) {
            toast.error("Hóa đơn đã được hoàn nhưng chưa thể tri trả, vui lòng chuyển trạng thái để kiểm tra!");
          } else {
            toast.error("Xác nhận thất bại!");
          }
        })
        .catch((error) => {
          setIsLoading(false);
          if (error.response && error.response.status === 500) {
            toast.error("Hóa đơn đã được hoàn nhưng chưa thể tri trả, vui lòng chuyển trạng thái để kiểm tra!");
          } else {
            toast.error("Xác nhận thất bại!");
          }
        });
    }
  };

  const handleOpenDieuChinhModal = (item) => {
    setSelectedOrderId(item.id);
    setKhoiLuong(item.khoiLuong);
    setSelectedDongGoiId(item.dongGoiId);
    setOpenModalDieuChinh(true);
  };

  const handleLuuDieuChinh = () => {
    if (khoiLuong <= 0) {
      toast.error("Khối lượng phải lớn hơn 0!");
      return;
    }
    if (!selectedDongGoiId) {
      toast.error("Vui lòng chọn dạng đóng hộp!");
      return;
    }
    CHANGE(khoiLuong, selectedDongGoiId);
    setOpenModalDieuChinh(false);
  };

  const CHANGE = (khoiLuong, dongGoiId) => {
    setIsLoading(true);
    changeDongHop(selectedOrderId, dongGoiId, khoiLuong)
      .then((response) => {
        if (response.status === 200) {
          const selectedDongGoi = dsDongHop.find((d) => d.id === dongGoiId);
          const tenDongGoi = selectedDongGoi ? selectedDongGoi.ten : "";
          const chieuDai = selectedDongGoi ? selectedDongGoi.chieuDai : 0;
          const chieuRong = selectedDongGoi ? selectedDongGoi.chieuRong : 0;
          const chieuCao = selectedDongGoi ? selectedDongGoi.chieuCao : 0;

          setData((prevData) =>
            prevData.map((item) =>
              item.id === selectedOrderId
                ? {
                  ...item,
                  khoiLuong: khoiLuong,
                  dongGoiId: dongGoiId,
                  tenDongGoi: tenDongGoi,
                  chieuDai: chieuDai,
                  chieuRong: chieuRong,
                  chieuCao: chieuCao,
                }
                : item
            )
          );

          toast.success("Cập nhật thông tin đóng hộp thành công");
        } else {
          toast.error("Cập nhật thông tin đóng hộp thất bại");
        }
      })
      .catch((error) => {
        toast.error(error?.response?.data?.message || "Cập nhật thông tin đóng hộp thất bại");
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleDongGoiSelect = (dongGoiId) => {
    setSelectedDongGoiId(dongGoiId);
    setOpenDongGoiDropdown(false);
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
          .spinner {
            border: 4px solid rgba(0, 0, 0, 0.1);
            border-left-color: #14532d;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
          }
          @keyframes spin {
            to {
              transform: rotate(360deg);
            }
          }
        `}
      </style>
      <div className="flex flex-row gap-4 mb-4">
        <div className="flex flex-col flex-1">
          <div className="mb-2">
            <i className="fa-solid fa-barcode mr-1 text-green-900 border shadow-md rounded-md p-2"></i>
            <span>Mã hóa đơn:</span>
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
            <i className="fa-regular fa-user mr-1 text-green-900 border shadow-md rounded-md p-2"></i>
            <span>Tên khách hàng:</span>
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
            <i className="fa-regular fa-clock mr-1 text-green-900 border shadow-md rounded-md p-2"></i>
            <span>Ngày lập:</span>
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
        </div>
      </div>
      <div className="flex gap-4 border-gray-200">
        {status.map((state) => (
          <button
            key={state.id}
            onClick={() => {
              setActiveId(state.id);
              setActiveGroup(null);
              setSearchParams({ active: state.id.toString() });
            }}
            className={`pb-2 mb-2 flex transition-colors items-center duration-300 text-sm font-medium text-green-900 border shadow-md rounded-md p-1
              ${activeId === state.id && !activeGroup ? "text-green-900 border-b-2 border-green-900" : "text-gray-500 hover:text-green-900"}`}
          >
            {state.id === 3 ? <i className="fa-regular fa-hourglass-half pl-2 ml-2"></i> : null}
            {state.id === 2 ? <i className="fa-solid fa-clipboard-check pl-2"></i> : null}
            {state.id === 12 ? <i className="fa-solid fa-truck pl-2"></i> : null}
            {state.id === 1 ? <i className="fa-solid fa-check pl-2"></i> : null}
            {state.id === 13 ? <i className="fa-solid fa-hourglass-half pl-2"></i> : null}
            <span className="ml-2">{state.name}</span>
          </button>
        ))}
        {statusGroups.map((group) => (
          <button
            key={group.id}
            onClick={() => {
              setActiveGroup(group.id);
              setActiveId(group.defaultStatus);
              setSearchParams({ group: group.id, active: group.defaultStatus.toString() });
            }}
            className={`pb-2 mb-2 flex transition-colors items-center duration-300 text-sm font-medium text-green-900 border shadow-md rounded-md p-1
              ${activeGroup === group.id ? "text-green-900 border-b-2 border-green-900" : "text-gray-500 hover:text-green-900"}`}
          >
            {group.name}
          </button>
        ))}
      </div>
      {activeGroup && (
        <div className="flex gap-4 border-gray-200">
          {statusGroups
            .find((group) => group.id === activeGroup)
            ?.statuses.map((state) => (
              <button
                key={state.id}
                onClick={() => {
                  setActiveId(state.id);
                  setSearchParams({ group: activeGroup, active: state.id.toString() });
                }}
                className={`pb-2 mb-2 flex transition-colors items-center duration-300 text-sm font-medium text-green-900 border shadow-md rounded-md p-1
                  ${activeId === state.id ? "text-green-900 border-b-2 border-green-900" : "text-gray-500 hover:text-green-900"}`}
              >
                {state.id === 9 ? <i className="fa-solid fa-shop-slash pl-2"></i> : null}
                {state.id === 4 ? <i className="fa-solid fa-trash pl-2"></i> : null}
                {state.id === 16 ? <i className="fa-solid fa-hand pl-2"></i> : null}
                {state.id === 17 ? <i className="fa-solid fa-check pl-2"></i> : null}
                {state.id === 14 ? <i className="fa-solid fa-circle-exclamation pl-2"></i> : null}
                {state.id === 15 ? <i className="fa-solid fa-check pl-2"></i> : null}
                {state.name}
              </button>
            ))}
        </div>
      )}
      <div className="flex justify-between items-center w-full mt-2">
        <p className="text-green-900 ml-2">Tổng số: {tongSoPhanTu} đơn hàng</p>
        {activeId === 3 && (
          <div className="flex items-center space-x-4">
            <label className="inline-flex items-center">
              <input
                type="checkbox"
                checked={
                  data.length > 0 &&
                  danhSachChon.length === data.filter(item => item.duocDuyet === true).length
                }
                onChange={handleSelectAll}
                className="peer hidden"
              />

              <span className="w-5 h-5 inline-block rounded border border-gray-400 cursor-pointer peer-checked:bg-green-900 peer-checked:[&::after]:content-['✔'] peer-checked:[&::after]:text-white peer-checked:[&::after]:text-sm peer-checked:[&::after]:block peer-checked:[&::after]:text-center peer-checked:[&::after]:leading-5"></span>
              <span className="ml-2 text-green-900">Chọn tất cả</span>
            </label>
            <p className="text-green-900">Đã chọn: {danhSachChon?.length}</p>
            <button
              onClick={handleDuyetDon}
              className="bg-green-700 text-white px-4 py-2 rounded-md hover:bg-green-600"
            >
              Duyệt đơn
            </button>
          </div>
        )}
      </div>

      <div className="mt-4 flex flex-wrap gap-4 justify-between">
        {data?.length > 0 ? (
          data.map((item, index) => (
            <div
              key={item.id}
              className="p-4 w-[calc(50%-0.5rem)] md:w-[calc(50%-0.5rem)] sm:w-full bg-white rounded-lg shadow-md border border-gray-300 cursor-pointer hover:shadow-lg transition-shadow duration-300"
            >
              <div className="flex justify-between items-center border-b pb-2 border-gray-200">
                <div>
                  <p
                    onClick={() => navigate(`/employee/detailonline?id=${item.id}`)}
                    className="font-bold text-lg cursor-pointer text-gray-800"
                  >
                    skinlyI{item.id}
                  </p>
                  <div className="flex items-center text-sm text-gray-600 space-x-2 mt-1">
                    <span>{item.tenKhachHang}</span>
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
                  {item.danhSachMatHang?.slice(0, 2).map((product, idx) => (
                    <div
                      key={idx}
                      className="border border-gray-200 rounded-md p-3 min-w-[220px] bg-white shadow-sm"
                    >
                      <p
                        className="text-sm font-medium text-gray-800 truncate"
                        title={product.tenSanPham}
                      >
                        {product.tenSanPham.length > 30
                          ? product.tenSanPham.substring(0, 27) + "..."
                          : product.tenSanPham}
                      </p>
                      <div className="text-xs text-gray-600 flex flex-wrap gap-2 mt-1">
                        <span>SL: {product.soLuong}</span>
                        <span>
                          Phân loại:{" "}
                          {product.tenPhanLoai.length > 20
                            ? product.tenPhanLoai.substring(0, 17) + "..."
                            : product.tenPhanLoai}
                        </span>
                        <span className="text-right font-semibold text-gray-800 ml-auto">
                          {formatToVND(product.donGia)}
                        </span>
                      </div>
                    </div>
                  ))}
                  {item.danhSachMatHang?.length > 2 && (
                    <div className="flex items-center text-sm text-gray-500">...</div>
                  )}
                </div>
              </div>
              <div className="flex justify-between items-center pt-2">
                <div>
                  {activeId === 3 && (
                    <label className={`inline-flex items-center ${item.duocDuyet == true ? "" : "pointer-events-none bg-gray-300"}`}>
                      <input
                        type="checkbox"
                        checked={danhSachChon.includes(item.id)}
                        onChange={() => handleCheckboxChange(item.id)}
                        className={`peer hidden ${item.duocDuyet == true ? "" : "pointer-events-none bg-gray-300"}`}
                      />
                      <span className="w-5 h-5 inline-block rounded border border-gray-400 cursor-pointer peer-checked:bg-green-900 peer-checked:[&::after]:content-['✔'] peer-checked:[&::after]:text-white peer-checked:[&::after]:text-sm peer-checked:[&::after]:block peer-checked:[&::after]:text-center peer-checked:[&::after]:leading-5"></span>
                    </label>
                  )}
                </div>
                <div className="flex items-center space-x-2">
                  {activeId === 13 && (
                    <>
                      <div
                        onClick={() => handleOpenDieuChinhModal(item)}
                        className="cursor-pointer hover:bg-gray-200 p-1 rounded"
                      >
                        <p className="text-sm text-gray-500 bg-gray-100 p-1">Khối lượng: {item?.khoiLuong}</p>
                      </div>
                      <div
                        onClick={() => handleOpenDieuChinhModal(item)}
                        className="cursor-pointer hover:bg-gray-200 p-1 rounded"
                      >
                        <p className="text-sm text-gray-500 bg-gray-100 p-1">Kích thước: {item?.chieuDai} x {item?.chieuRong} x {item?.chieuCao}</p>
                      </div>
                      <div>
                        <p className="text-sm text-orange-500">{item?.tenDongGoi}</p>
                      </div>
                    </>
                  )}
                  {activeId === 13 && (
                    <button
                      onClick={() => {
                        getURLprintOrder(item.id)
                          .then((url) => {
                            if (url) {
                              window.open(url, "_blank");
                            } else {
                              toast.error("Không nhận được link in vận đơn");
                            }
                          })
                          .catch(() => {
                            toast.error("In vận đơn thất bại, vui lòng thử lại");
                          });
                      }}
                      className="px-2 py-1 bg-green-600 text-white rounded"
                    >
                      In vận đơn
                    </button>
                  )}
                  {activeId === 17 && (
                    <button
                      onClick={() => {
                        setDonIdToXacNhan(item.id);
                        setOpenModalXacNhanHoan(true);
                        setHoanChiTra(null);
                        setTraTien(null);
                      }}
                      className="px-2 py-1 bg-green-600 text-white rounded"
                    >
                      Xác nhận đơn
                    </button>
                  )}
                  {(activeId === 9 || activeId === 15) && item.daHoanHang && (
                    <div className="px-2 py-1 bg-green-50 text-green-800 text-sm rounded-lg">
                      <p className="text-green-700">Đã hoàn tất</p>
                    </div>
                  )}
                  {
                    item?.lenDon == true ? <button onClick={() => {
                      reCreateGHN(item?.id).then(() => {
                        toast.success("Lên đơn thành công")
                      }).catch((e) => {
                        toast.error(e?.response?.data?.message || "Lên đơn thất bại")
                      })
                    }} className="text-white bg-orange-500 p-2 rounded-md">Lên đơn giao hàng</button> : null
                  }
                  {(activeId === 9 || activeId === 15 || activeId === 14) && item.daHoanHang == false && item?.daThanhToan == true && (
                    <button
                      onClick={() => {
                        setDonIdToHoanTien(item.id);
                        setOpenModalXacNhanHoanTien(true);
                      }}
                      className="px-2 py-1 bg-orange-600 text-white rounded"
                    >
                      Chi trả
                    </button>
                  )}



                  {/* {activeId === 14 && (
                    <button
                      onClick={() => handleXemLyDo(item)}
                      className="px-2 py-1 bg-blue-600 text-white rounded"
                    >
                      Xem lý do
                    </button>
                  )}
                  {activeId === 14 && (
                    <button
                      onClick={() => {
                        setDonIdToXacNhan(item.id);
                        setOpenModalXacNhanHoan(true);
                      }}
                      className="px-2 py-1 bg-green-600 text-white rounded"
                    >
                      Xác nhận
                    </button>
                  )} */}
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="w-full flex justify-center">
            <img
              className="w-44"
              src="https://media.istockphoto.com/id/861576608/vi/vec-to/bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-empty-shopping-bag-m%E1%BA%ABu-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-vector-online-business.jpg?s=612x612&w=0&k=20&c=-6p0qVyb_p8VeHVu1vJVaDHa6Wd_mCjMkrFBTlQdehI="
              alt="No orders"
            />
          </div>
        )}
      </div>
      <Pagination color="bg-green-900" setTrangHienTai={setTrang} trangHienTai={trang} soLuongTrang={tongTrang} />

      {/* Loading Modal */}
      {isLoading && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
            <div className="spinner"></div>
            <p className="text-lg font-semibold text-green-900">Đang cập nhật...</p>
          </div>
        </div>
      )}

      {/* Modal điều chỉnh thông tin gửi hàng */}
      {openModalDieuChinh && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
            <h2 className="text-xl font-semibold text-green-900">Điều chỉnh thông tin gửi hàng</h2>
            <div className="w-full">
              <label className="text-sm text-gray-700">Khối lượng (g)</label>
              <input
                type="number"
                value={khoiLuong}
                onChange={(e) => setKhoiLuong(parseInt(e.target.value) || 0)}
                className="w-full border border-green-900 rounded-md px-3 py-2 text-sm mt-1"
                placeholder="Nhập khối lượng"
              />
            </div>
            <div className="w-full relative">
              <label className="text-sm text-gray-700">Dạng đóng hộp</label>
              <input
                ref={dongGoiRef}
                value={dsDongHop.find((d) => d.id === selectedDongGoiId)?.ten || ""}
                onFocus={() => setOpenDongGoiDropdown(true)}
                onBlur={() => setTimeout(() => setOpenDongGoiDropdown(false), 200)}
                className="w-full border border-green-900 rounded-md px-3 py-2 text-sm mt-1 cursor-pointer"
                placeholder="Chọn dạng đóng hộp"
                readOnly
              />
              {openDongGoiDropdown && (
                <div className="absolute bg-white border border-green-900 rounded-md shadow-md w-full mt-1 z-10 top-full">
                  <div className="absolute -top-2 left-3 w-0 h-0 border-l-8 border-r-8 border-b-8 border-l-transparent border-r-transparent border-b-green-900"></div>
                  <ul className="text-sm p-2 max-h-60 overflow-y-auto custom-scrollbar">
                    {dsDongHop.map((dongGoi) => (
                      <li
                        key={dongGoi.id}
                        onClick={() => handleDongGoiSelect(dongGoi.id)}
                        className={`py-1 px-2 hover:bg-green-900 hover:text-white cursor-pointer ${selectedDongGoiId === dongGoi.id ? "bg-green-900 text-white" : ""
                          }`}
                      >
                        {dongGoi.ten}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
            <div className="flex gap-4">
              <button
                onClick={handleLuuDieuChinh}
                className="bg-green-700 text-white px-4 py-2 rounded-md hover:bg-green-600"
              >
                Lưu
              </button>
              <button
                onClick={() => setOpenModalDieuChinh(false)}
                className="bg-gray-300 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-400"
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal thành công */}
      {openModalThanhCong && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
            <h2 className="text-xl font-semibold text-green-900">{successMessage}</h2>
            <p className="text-sm text-green-700">
              {successMessage.includes("chi trả") ? "Đơn hàng đã được chi trả thành công" : "Đơn hàng đã được xử lý thành công"}
            </p>
            <div className="flex gap-4">
              <button
                onClick={() => {
                  setOpenModalThanhCong(false)
                  setLoad(!load)
                }}
                className="bg-green-700 text-white px-4 py-2 rounded-md hover:bg-green-600"
              >
                Đồng ý
              </button>
              <button
                onClick={() => setOpenModalThanhCong(false)}
                className="bg-gray-300 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-400"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal xác nhận hoàn tiền */}
      {openModalXacNhanHoanTien && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
            <h2 className="text-xl font-semibold text-green-900">Xác nhận hoàn tiền</h2>
            <p className="text-sm text-gray-700">Bạn có chắc chắn muốn hoàn tiền cho đơn hàng này?</p>
            <div className="flex gap-4">
              <button
                onClick={handleHoanTien}
                className="bg-green-700 text-white px-4 py-2 rounded-md hover:bg-green-600"
              >
                Đồng ý
              </button>
              <button
                onClick={() => setOpenModalXacNhanHoanTien(false)}
                className="bg-gray-300 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-400"
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal lý do hoàn hàng */}
      {openModalLyDo && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full flex flex-col items-center gap-4">
            <h2 className="text-xl font-semibold text-green-900">Lý do hoàn hàng</h2>
            <p className="text-sm text-gray-700">{selectedReason}</p>
            <div className="flex gap-4">
              <button
                onClick={() => setOpenModalLyDo(false)}
                className="bg-green-700 text-white px-4 py-2 rounded-md hover:bg-green-600"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal xác nhận đơn hoàn */}
      {openModalXacNhanHoan && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md flex flex-col items-center gap-4">
            <h2 className="text-xl font-semibold text-green-900 text-center">Xác nhận đơn hoàn</h2>

            <div className="w-full flex flex-row justify-start gap-8">

              <div className="flex-1 flex flex-col items-start space-y-4">
                <p className="text-sm text-gray-700 text-center">Vui lòng chọn loại hoàn đơn:</p>
                <div className="flex items-center space-x-3">
                  <input
                    type="radio"
                    name="hoanChiTra"
                    value={0}
                    checked={hoanChiTra === 0}
                    onChange={() => setHoanChiTra(0)}
                    className="form-radio h-5 w-5 text-green-600 accent-green-600 border-green-600 focus:ring-green-600"
                  />
                  <span className="text-sm text-gray-700">Hoàn chi trả</span>
                </div>
                <div className="flex items-center space-x-3">
                  <input
                    type="radio"
                    name="hoanChiTra"
                    value={1}
                    checked={hoanChiTra === 1}
                    onChange={() => {
                      setHoanChiTra(1);
                      setTraTien(null);
                    }}
                    className="form-radio h-5 w-5 text-green-600 accent-green-600 border-green-600 focus:ring-green-600"
                  />
                  <span className="text-sm text-gray-700">Hoàn không chi trả</span>
                </div>
              </div>
              {hoanChiTra === 0 && (
                <div className="flex-1 flex flex-col items-start space-y-4">
                  <p className="text-sm text-gray-700">Trả tiền khách hàng:</p>
                  <div className="flex items-center space-x-3">
                    <input
                      type="radio"
                      name="traTien"
                      value={0}
                      checked={traTien === 0}
                      onChange={() => setTraTien(0)}
                      className="form-radio h-5 w-5 text-green-600 cursor-pointer  accent-green-600 border-green-600 focus:ring-green-600"
                    />
                    <span className="text-sm text-gray-700">Trả luôn</span>
                  </div>
                  <div className="flex items-center space-x-3">
                    <input
                      type="radio"
                      name="traTien"
                      value={1}
                      checked={traTien === 1}
                      onChange={() => setTraTien(1)}
                      className="form-radio h-5 w-5 text-green-600 cursor-pointer  accent-green-600 border-green-600 focus:ring-green-600"
                    />
                    <span className="text-sm text-gray-700">Trả sau</span>
                  </div>
                </div>
              )}
            </div>
            <div className="flex gap-4 mt-6">
              <button
                onClick={handleXacNhanHoanHang}
                className="bg-green-600 text-white px-6 py-2 cursor-pointer rounded-md hover:bg-green-700 transition-colors duration-200"
              >
                Đồng ý
              </button>
              <button
                onClick={() => {
                  setOpenModalXacNhanHoan(false);
                  setHoanChiTra(null);
                  setTraTien(null);
                }}
                className="bg-gray-300 text-gray-700 cursor-pointer  px-6 py-2 rounded-md hover:bg-gray-400 transition-colors duration-200"
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export { QuanLyHoaDonOnline };
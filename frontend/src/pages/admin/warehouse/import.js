import { useEffect, useState } from "react";
import { duyet, getAll } from "../../../services/PhieuNhapService";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../../utils/Format";
import Modal from "../../../components/commons/modal";
import { ChiTietPhieuNhap } from "../../../components/admin/DetailimportSlip";
import { Pagination } from "../../../components/commons/Pagination";
import { DetailProduct } from "../../../components/admin/DetailProduct";
import { DetailProduct2, ProductTable } from "./DetailImport";
import IconExcel from "../../../assets/IconExcel.png";
import { XuatTonKho } from "./xuatTonKho";
import { XuatLoiNhuan } from "./XuatLoiNhuan";

function NhapKho() {
  const formatDate = (date) => date.toISOString().slice(0, 10);
  const [bd, setbd] = useState("");
  const [kt, setkt] = useState("");
  const [tenSanPham, setTenSanPham] = useState("");
  const [chuoi, setchuoi] = useState("");
  const [chuoi22, setchuoi22] = useState("");
  const [pick, setpick] = useState(0);
  const [danhSachPhieuNhap, setDanhSachPhieuNhap] = useState([]);
  const [trang, setTrang] = useState(0);
  const [open, setOpen] = useState(false);
  const [idPhieu, setIdPhieu] = useState(-1);
  const [tong, setTong] = useState(0);
  const [tongTrang, setTongTrang] = useState(0);
  const [confirmOpen, setConfirmOpen] = useState(false); // State for confirmation modal
  const [phieuToDuyet, setPhieuToDuyet] = useState(null); // Store ID of slip to approve
  const [status, setStatus] = useState([
    { id: 3, name: "Chờ xác nhận" },
    { id: 2, name: "Đã xác nhận" },
    { id: 13, name: "Chờ giao" },
    { id: 12, name: "Đang giao" },
    { id: 1, name: "Hoàn tất" },
    { id: 6, name: "Yêu cầu hoàn hàng" },
  ]);
  const [selectedStatus, setSelectedStatus] = useState(status.map(s => s.id));

  useEffect(() => {
    const today = new Date();
    const threeMonthsAgo = new Date(today.getFullYear(), today.getMonth() - 3, 1);
    setbd(formatDate(threeMonthsAgo));
    setkt(formatDate(today));
    setchuoi22(status.map(s => s.id).join(" "));
  }, []);

  useEffect(() => {
    if (bd !== "" && kt !== "") {
      getAll(bd, kt, chuoi, tenSanPham, trang)
        .then((data) => {
          setTongTrang(data.totalPages);
          setTong(data.totalElements);
          setDanhSachPhieuNhap(data.content);
        })
        .catch((err) =>
          toast.error(err?.response?.data?.message || "Lấy dữ liệu thất bại")
        );
    }
  }, [bd, kt, trang, chuoi, tenSanPham]);

  const handleStatusChange = (id) => {
    let updatedSelected;
    if (selectedStatus.includes(id)) {
      updatedSelected = selectedStatus.filter(statusId => statusId !== id);
    } else {
      updatedSelected = [...selectedStatus, id];
    }
    setSelectedStatus(updatedSelected);
    setchuoi22(updatedSelected.join(" "));
  };

  const handleDuyet = (id) => {
    setPhieuToDuyet(id); // Store the ID of the slip to approve
    setConfirmOpen(true); // Open confirmation modal
  };

  const confirmDuyet = () => {
    if (phieuToDuyet) {
      duyet(phieuToDuyet)
        .then(() => {
          // Update the daDuyet field for the specific slip
          setDanhSachPhieuNhap((prev) =>
            prev.map((phieu) =>
              phieu.id === phieuToDuyet ? { ...phieu, daDuyet: true } : phieu
            )
          );
          toast.success("Duyệt phiếu nhập thành công!");
          setConfirmOpen(false); // Close confirmation modal
          setPhieuToDuyet(null); // Reset
        })
        .catch((err) => {
          toast.error(err?.response?.data?.message || "Duyệt phiếu nhập thất bại");
          setConfirmOpen(false); // Close confirmation modal
          setPhieuToDuyet(null); // Reset
        });
    }
  };

  const tongTien = danhSachPhieuNhap.reduce((acc, cur) => acc + cur.tongTien, 0);
  const soLanNhap = danhSachPhieuNhap.length;

  return (
    <div className="bg-gray-50 min-h-screen">
      <p className="text-left mt-4 mb-3 bg-white p-3 rounded-md">
        <i className="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
        <strong className="text-lg">Quản lý nhập kho</strong>
        <p className="mt-1">Quản lý tình hình nhập kho tại cửa hàng</p>
      </p>
      {/* Filter Inputs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8 bg-white rounded-lg p-4 shadow-sm">
        <div>
          <label htmlFor="tenNhaCungCap" className="block mb-1 text-sm font-medium text-gray-700 flex items-center mb-2">
            <i className="fas fa-user mr-2 text-yellow-500 p-2 rounded-sm bg-yellow-100"></i> Tên nhà cung cấp
          </label>
          <input
            id="tenNhaCungCap"
            value={chuoi}
            onChange={(e) => setchuoi(e.target.value)}
            type="text"
            placeholder="Nhập tên nhà cung cấp"
            className="outline-none w-full p-2.5 text-sm border border-gray-300 rounded-lg outline-none transition bg-gray-50"
          />
        </div>
        <div>
          <label htmlFor="tenSanPham" className="block mb-1 text-sm font-medium text-gray-700 flex items-center mb-2">
            <i className="fas fa-box mr-2 text-blue-600 text-yellow-500 p-2 rounded-sm bg-yellow-100"></i> Tên sản phẩm
          </label>
          <input
            id="tenSanPham"
            value={tenSanPham}
            onChange={(e) => setTenSanPham(e.target.value)}
            type="text"
            placeholder="Nhập tên sản phẩm"
            className="outline-none w-full p-2.5 text-sm border border-gray-300 rounded-lg outline-none transition bg-gray-50"
          />
        </div>
        <div>
          <label htmlFor="thoiDiemBatDau" className="block mb-1 text-sm font-medium text-gray-700 flex items-center mb-2">
            <i className="fas fa-calendar-alt mr-2 text-yellow-500 p-2 rounded-sm bg-yellow-100"></i> Thời điểm bắt đầu
          </label>
          <input
            id="thoiDiemBatDau"
            value={bd}
            onChange={(e) => setbd(e.target.value)}
            type="date"
            className="w-full p-2.5 text-sm border border-gray-300 rounded-lg outline-none transition bg-gray-50"
          />
        </div>
        <div>
          <label htmlFor="thoiDiemKetThuc" className="block mb-1 text-sm font-medium text-gray-700 flex items-center mb-2">
            <i className="fas fa-calendar-check mr-2 text-yellow-500 p-2 rounded-sm bg-yellow-100"></i> Thời điểm kết thúc
          </label>
          <input
            id="thoiDiemKetThuc"
            value={kt}
            onChange={(e) => setkt(e.target.value)}
            type="date"
            className="w-full p-2.5 text-sm border border-gray-300 rounded-lg outline-none transition bg-gray-50"
          />
        </div>
      </div>

      <div className="flex justify-start mb-6">
        <div className="flex space-x-4 overflow-x-auto whitespace-nowrap px-4">
          <button
            onClick={() => setpick(0)}
            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg border border-gray-300 shadow-md transition-colors ${
              pick === 0
                ? "text-green-700 border-b-2 border-green-700"
                : "text-gray-700 hover:text-green-700 hover:bg-gray-50"
            }`}
            role="tab"
            aria-selected={pick === 0}
          >
            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-green-200 rounded-full">
              <i className="fas fa-file-import text-green-700"></i>
            </span>
            Lần nhập
          </button>
          <button
            onClick={() => setpick(1)}
            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg border border-gray-300 shadow-md transition-colors ${
              pick === 1
                ? "text-violet-700 border-b-2 border-violet-700"
                : "text-gray-700 hover:text-violet-700 hover:bg-gray-50"
            }`}
            role="tab"
            aria-selected={pick === 1}
          >
            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-violet-200 rounded-full">
              <i className="fas fa-boxes text-violet-700"></i>
            </span>
            Sản phẩm nhập
          </button>
          <button
            onClick={() => setpick(2)}
            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg border border-gray-300 shadow-md transition-colors ${
              pick === 2
                ? "text-yellow-700 border-b-2 border-yellow-700"
                : "text-gray-700 hover:text-yellow-700 hover:bg-gray-50"
            }`}
            role="tab"
            aria-selected={pick === 2}
          >
            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-yellow-200 rounded-full">
              <i className="fas fa-list text-yellow-700"></i>
            </span>
            Danh sách tổng quát
          </button>
          <button
            onClick={() => setpick(3)}
            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg border border-gray-300 shadow-md transition-colors ${
              pick === 3
                ? "text-blue-700 border-b-2 border-blue-700"
                : "text-gray-700 hover:text-blue-700 hover:bg-gray-50"
            }`}
            role="tab"
            aria-selected={pick === 3}
          >
            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-blue-200 rounded-full">
              <i className="fas fa-warehouse text-blue-700"></i>
            </span>
            Tồn kho
          </button>
          <button
            onClick={() => setpick(4)}
            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg border border-gray-300 shadow-md transition-colors ${
              pick === 4
                ? "text-red-700 border-b-2 border-red-700"
                : "text-gray-700 hover:text-red-700 hover:bg-gray-50"
            }`}
            role="tab"
            aria-selected={pick === 4}
          >
            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-red-200 rounded-full">
              <i className="fas fa-chart-line text-red-700"></i>
            </span>
            Xuất hàng - lợi nhuận
          </button>
        </div>
      </div>

      {(pick === 3 || pick === 4) && (
        <div className="flex justify-start mb-6 px-4 space-x-4 overflow-x-auto whitespace-nowrap">
          {status.map((s) => (
            <label
              key={s.id}
              className={`flex items-center px-3 py-2 rounded-lg border cursor-pointer transition-colors ${
                selectedStatus.includes(s.id)
                  ? "bg-blue-100 border-blue-500 text-blue-700"
                  : "bg-gray-50 border-gray-300 text-gray-700 hover:bg-blue-50"
              }`}
            >
              <input
                type="checkbox"
                checked={selectedStatus.includes(s.id)}
                onChange={() => handleStatusChange(s.id)}
                className="mr-2 h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <i className="fas fa-check-circle mr-2 text-blue-500"></i>
              <span className="text-sm font-medium">{s.name}</span>
            </label>
          ))}
        </div>
      )}

      {pick === 0 && (
        <div className="bg-white rounded-lg shadow-sm overflow-hidden p-3">
          <p className="mb-2 font-bold">
            <i className="fa-solid fa-list p-2 rounded-sm text-green-500 bg-green-100 mr-2"></i> Danh sách lần nhập kho
          </p>

          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    STT
                  </th>
                  <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Ngày lập phiếu
                  </th>
                  <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Nhân viên lập phiếu
                  </th>
                  {/* <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Thuế VAT
                  </th> */}
                  <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Tổng tiền thanh toán
                  </th>
                  <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Xem chi tiết
                  </th>
                  <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                    Trạng thái
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200 bg-white">
                {danhSachPhieuNhap?.length > 0 ? (
                  danhSachPhieuNhap.map((data, index) => (
                    <tr
                      key={index}
                      className="hover:bg-gray-50 transition-colors"
                    >
                      <td className="px-4 py-2.5 text-sm text-gray-600">
                        {index + 1}
                      </td>
                      <td className="px-4 py-2.5 text-sm text-gray-600">
                        {dinhDangNgay(data.ngayNhap)}
                      </td>
                      <td className="px-4 py-2.5 text-sm text-gray-600">
                        {data.nhanVien}
                      </td>
                      {/* <td className="px-4 py-2.5 text-sm text-gray-600">
                        {formatToVND(data.thueVAT)}
                      </td> */}
                      <td className="px-4 py-2.5 text-sm text-gray-600">
                        {formatToVND(data.tongTien)}
                      </td>
                      <td className="px-4 py-2.5">
                        <button
                          onClick={() => {
                            setOpen(true);
                            setIdPhieu(data.id);
                          }}
                          className="text-blue-600 hover:text-blue-800 text-sm font-medium transition-colors"
                        >
                          Xem chi tiết
                        </button>
                      </td>
                      <td className="px-4 py-2.5">
                        {data.daDuyet ? (
                          <p className="bg-green-800 w-fit text-white rounded-md font-bold p-2">Đã duyệt</p>
                        ) : (
                          <button
                            onClick={() => handleDuyet(data.id)}
                            className="bg-orange-500 text-white rounded-md font-bold p-2"
                          >
                            Chưa duyệt
                          </button>
                        )}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan="7"
                      className="text-center py-6 text-sm text-gray-500"
                    >
                      Không có dữ liệu phiếu nhập trong khoảng thời gian này.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {tongTrang > 1 && (
            <div className="p-4">
              <Pagination
                trangHienTai={trang}
                setTrangHienTai={setTrang}
                soLuongTrang={tongTrang}
              />
            </div>
          )}

          {open && (
            <Modal setOpen={setOpen}>
              <ChiTietPhieuNhap id={idPhieu} />
            </Modal>
          )}

          {confirmOpen && (
            <Modal setOpen={setConfirmOpen}>
              <div className="p-6 bg-white rounded-lg shadow-lg">
                <h2 className="text-lg font-semibold text-gray-800 mb-4">
                  Xác nhận duyệt phiếu nhập
                </h2>
                <p className="text-sm text-gray-600 mb-6">
                  Bạn có chắc chắn muốn duyệt phiếu nhập này không?
                </p>
                <div className="flex justify-end space-x-4">
                  <button
                    onClick={() => setConfirmOpen(false)}
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300 transition-colors"
                  >
                    Hủy
                  </button>
                  <button
                    onClick={confirmDuyet}
                    className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
                  >
                    Xác nhận
                  </button>
                </div>
              </div>
            </Modal>
          )}
        </div>
      )}

      {pick === 1 && (
        <DetailProduct
          bd={bd}
          kt={kt}
          dvcc={chuoi}
          sp={tenSanPham}
          bienDoi={true}
          setOpen={setOpen}
        />
      )}
      {pick === 4 && (
        <XuatLoiNhuan
          bd={bd}
          kt={kt}
          dvcc={chuoi}
          sp={tenSanPham}
          bienDoi={true}
          setOpen={setOpen}
          status={chuoi22}
        />
      )}

      {pick === 2 && (
        <ProductTable
          bd={bd}
          kt={kt}
          dvcc={chuoi}
          sp={tenSanPham}
          bienDoi={true}
          setOpen={setOpen}
          status={chuoi22}
        />
      )}
      {pick === 3 && (
        <XuatTonKho
          bd={bd}
          kt={kt}
          dvcc={chuoi}
          sp={tenSanPham}
          bienDoi={true}
          setOpen={setOpen}
          status={chuoi22}
        />
      )}
    </div>
  );
}

export { NhapKho };
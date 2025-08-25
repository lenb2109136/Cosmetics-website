import { useEffect, useState } from "react";
import { duyet, getAll } from "../../services/PhieuNhapService";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import Modal from "../../components/commons/modal";
import { ChiTietPhieuNhap } from "../../components/admin/DetailimportSlip";
import { Pagination } from "../../components/commons/Pagination";
import { useNavigate } from "react-router-dom";

function NhapKhoE() {
  const formatDate = (date) => date.toISOString().slice(0, 10);
  const [bd, setbd] = useState("");
  const [kt, setkt] = useState("");
  const [tenSanPham, setTenSanPham] = useState("");
  const [chuoi, setchuoi] = useState("");
  const [danhSachPhieuNhap, setDanhSachPhieuNhap] = useState([]);
  const [trang, setTrang] = useState(0);
  const [open, setOpen] = useState(false);
  const [idPhieu, setIdPhieu] = useState(-1);
  const [tong, setTong] = useState(0);
  const [tongTrang, setTongTrang] = useState(0);
  const [confirmOpen, setConfirmOpen] = useState(false); // State for confirmation modal
  const [phieuToDuyet, setPhieuToDuyet] = useState(null); // Store ID of slip to approve
const navigate=useNavigate()
  useEffect(() => {
    const today = new Date();
    const threeMonthsAgo = new Date(today.getFullYear(), today.getMonth() - 3, 1);
    setbd(formatDate(threeMonthsAgo));
    setkt(formatDate(today));
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

  return (
    <div className="bg-gray-50 min-h-screen">
      <p className="text-left mt-4 mb-3 bg-white p-3 rounded-md">
         <p className="font-bold text-lg">
                    <i className="fa-solid fa-file-import  text-green-900 p-2 rounded-md mr-1 shadow-md border"></i> Danh sách nhập kho
                </p>
        <div className="flex justify-between items-center">
          <p className="mt-1">Lưu ý chỉ bạn chỉ có thể điều chỉnh những phiếu chưa được xác nhận</p>
          <button onClick={()=>{navigate("add")}} className="p-2 text-green-800 shadow-md rounded-md border">Thêm phiếu nhập mới</button>
        </div>
      </p>
      {/* Filter Inputs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8 bg-white rounded-lg p-4 shadow-sm">
        <div>
          <label htmlFor="tenNhaCungCap" className="block mb-1 text-sm font-medium text-gray-700 flex items-center mb-2">
            <i className="fas fa-user mr-2 text-green-900 p-2 rounded-md  shadow-md border"></i> Tên nhà cung cấp
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
            <i className="fas fa-box mr-2 text-green-900 p-2 rounded-md  shadow-md border"></i> Tên sản phẩm
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
            <i className="fas fa-calendar-alt mr-2 text-green-900 p-2 rounded-md  shadow-md border"></i> Thời điểm bắt đầu
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
            <i className="fas fa-calendar-check mr-2 text-green-900 p-2 rounded-md  shadow-md border"></i> Thời điểm kết thúc
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

      {/* Danh sách phiếu nhập */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden p-3">
        <p className="mb-2 font-bold">
          <i className="fa-solid fa-list p-2 rounded-sm bg-green-900  p-2 rounded-md text-white mr-2 shadow-md"></i> Danh sách lần nhập kho
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
                    
                    <td className="px-4 py-2.5 text-sm text-gray-600">
                      {formatToVND(data.tongTien)}
                    </td>
                    <td className="px-4 py-2.5">
                      <button
                        onClick={() => {
                          navigate("/employee/nhaphang/update/"+data.id)
                        }}
                        className="text-green-900 border shadow-md p-1 rounded-md text-sm font-medium transition-colors pl-2 pr-2"
                      >
                        Xem chi tiết
                      </button>
                    </td>
                    <td className="px-4 py-2.5 pointer-events-none">
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

        {/* Pagination */}
        {tongTrang > 1 && (
          <div className="p-4">
            <Pagination
            color="bg-green-900"
              trangHienTai={trang}
              setTrangHienTai={setTrang}
              soLuongTrang={tongTrang}
            />
          </div>
        )}

        {/* Modal Chi tiết */}
        {open && (
          <Modal setOpen={setOpen}>
            <ChiTietPhieuNhap id={idPhieu} />
          </Modal>
        )}

        {/* Modal Xác nhận duyệt */}
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
    </div>
  );
}

export { NhapKhoE };
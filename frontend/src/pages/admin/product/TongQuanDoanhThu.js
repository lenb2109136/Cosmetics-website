import growicon from "../../../assets/Growth.png";
import muiTen1 from "../../../assets/muiTen1.png";
import muiTen2 from "../../../assets/muiTen2.png";
import muiTen3 from "../../../assets/muiTen3.png";
import downicon from "../../../assets/down.png";
import { formatDateToString, formatToVND } from "../../../utils/Format";
import { useEffect, useState } from "react";
import { getThongKeCoBan } from "../../../services/sanPhamService";
import { toast } from "react-toastify";

function tinhNgay(ngayBatDau, ngayKetThuc) {
  const batDau = new Date(ngayBatDau.getFullYear(), ngayBatDau.getMonth(), ngayBatDau.getDate());
  const ketThuc = new Date(ngayKetThuc.getFullYear(), ngayKetThuc.getMonth(), ngayKetThuc.getDate());
  const millisecMotNgay = 24 * 60 * 60 * 1000;
  const soNgay = Math.round((ketThuc - batDau) / millisecMotNgay);
  return soNgay;
}

function TongQuanDoanhThu({ batDau, ketThuc, id, setcb }) {
  const [data, setData] = useState({});
  const soNgay = tinhNgay(batDau, ketThuc);

  useEffect(() => {
    getThongKeCoBan(id, formatDateToString(batDau), formatDateToString(ketThuc))
      .then((response) => {
        setData(response);
        setcb(response.thongTinCoBan);
      })
      .catch(() => {
        toast.error("Lấy dữ liệu thống kê thất bại");
      });
  }, [batDau, ketThuc, id, setcb]);

  // Calculate conversion rates with validation
  const calculateConversionRate = (numerator, denominator) => {
    if (!numerator || !denominator || denominator === 0) return "0.00";
    return Math.min((numerator / denominator) * 100, 100).toFixed(2);
  };

  const visitsToOrders = calculateConversionRate(data?.base?.tongDonDat, data?.truyCap?.soLuong);
  const ordersToSuccess = calculateConversionRate(data?.base?.tongThanhCong, data?.base?.tongDonDat);
  const visitsToSuccess = calculateConversionRate(data?.base?.tongThanhCong, data?.truyCap?.soLuong);

  return (
    <div className="w-full mx-auto flex flex-col lg:flex-row gap-6 mt-6 px-4">
      {/* Main Funnel Sections */}
      <div className="flex-1 space-y-4">
        {/* Section 1: Lượt truy cập */}
        <div className="flex bg-white shadow-lg rounded-xl overflow-hidden">
          <div className="flex-1 p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-lg font-semibold text-gray-800"> <i class="fa-regular fa-keyboard text-green-500"></i> Số lượt truy cập</p>
                <p className="text-2xl font-bold text-gray-900">{data?.truyCap?.soLuong ?? 0}</p>
                <p className="text-sm text-gray-500">So với {soNgay} ngày trước: {data?.truyCap?.soLuongTruocDo} lượt</p>
              </div>
              <div className="flex flex-col items-center">
                <img
                  className="w-6 h-7"
                  src={data?.truyCap?.tiLe >= 0 ? growicon : downicon}
                  alt="trend icon"
                />
                <p className="text-sm font-medium text-blue-600">
                  {/* {(data?.truyCap?.tiLe != null ? Number(data.truyCap.tiLe).toFixed(2) : "0.00")}% */}
                </p>
              </div>
            </div>
          </div>
          <div
            className="w-32 bg-gradient-to-b from-blue-600 to-blue-400 flex items-center justify-center text-white font-semibold text-sm text-center"
            style={{ clipPath: "polygon(0 0, 100% 0, 85% 100%, 15% 100%)" }}
          >
            Lượt truy cập
          </div>
        </div>

        {/* Section 2: Đơn đặt */}
        <div className="flex bg-white shadow-lg rounded-xl overflow-hidden">
          <div className="flex-1 p-6 flex space-x-6">
            <div className="flex-1 flex items-center justify-between">
              <div>
                <p className="text-lg font-semibold text-gray-800"><i class="fa-regular fa-user text-violet-500"></i> Người mua</p>
                <p className="text-2xl font-bold text-gray-900">{data?.nguoiMuaDonDat?.soLuong ?? 0}</p>
                <p className="text-sm text-gray-500">So với {soNgay} ngày trước: {data?.nguoiMuaDonDat?.soLuongTruocDo} lượt</p>
              </div>
              <div className="flex flex-col items-center">
                <img
                  className="w-6 h-7"
                  src={data?.nguoiMuaDonDat?.tiLe >= 0 ? growicon : downicon}
                  alt="trend icon"
                />
                <p className="text-sm font-medium text-blue-600">
                  {/* {(data?.nguoiMuaDonDat?.tiLe != null ? Number(data.nguoiMuaDonDat.tiLe).toFixed(2) : "0.00")}% */}
                </p>
              </div>
            </div>
            <div className="w-px bg-gray-200"></div>
            <div className="flex-1 flex items-center justify-between">
              <div>
                <p className="text-lg font-semibold text-gray-800"> <i class="fa-solid fa-sack-dollar text-yellow-400"></i> Doanh thu</p>
                <p className="text-2xl font-bold text-gray-900">
                  {data?.doanhSoDonDat?.soLuong != null ? formatToVND(data?.doanhSoDonDat?.soLuong) : "0"}
                </p>
                <p className="text-sm text-gray-500">So với {soNgay} ngày trước: {formatToVND(data?.doanhSoDonDat?.soLuongTruocDo)}</p>
              </div>
              <div className="flex flex-col items-center">
                <img
                  className="w-6 h-7"
                  src={data?.doanhSoDonDat?.tiLe >= 0 ? growicon : downicon}
                  alt="trend icon"
                />
                <p className="text-sm font-medium text-blue-600">
                  {/* {(data?.doanhSoDonDat?.tiLe != null ? Number(data.doanhSoDonDat.tiLe).toFixed(2) : "0.00")}% */}
                </p>
              </div>
            </div>
          </div>
          <div
            className="w-32 bg-gradient-to-b from-blue-600 to-blue-400 flex items-center justify-center text-white font-semibold text-sm text-center"
            style={{ clipPath: "polygon(15% 0, 85% 0, 100% 100%, 0% 100%)" }}
          >
            Đơn đặt
          </div>
        </div>

        {/* Section 3: Đơn thành công */}
        <div className="flex bg-white shadow-lg rounded-xl overflow-hidden">
          <div className="flex-1 p-6 flex space-x-6">
            <div className="flex-1 flex items-center justify-between">
              <div>
                <p className="text-lg font-semibold text-gray-800"><i class="fa-regular fa-user text-violet-500"></i> Người mua</p>
                <p className="text-2xl font-bold text-gray-900">{data?.nguoiMuaThanhCong?.soLuong ?? 0}</p>
                <p className="text-sm text-gray-500">So với {soNgay} ngày trước: {data?.nguoiMuaThanhCong?.soLuongTruocDo} lượt</p>
              </div>
              <div className="flex flex-col items-center">
                <img
                  className="w-8 h-7"
                  src={data?.nguoiMuaThanhCong?.tiLe >= 0 ? growicon : downicon}
                  alt="trend icon"
                />
                <p className="text-sm font-medium text-blue-600">
                  {/* {(data?.nguoiMuaThanhCong?.tiLe != null ? Number(data.nguoiMuaThanhCong.tiLe).toFixed(2) : "0.00")}% */}
                </p>
              </div>
            </div>
            <div className="w-px bg-gray-200"></div>
            <div className="flex-1 flex items-center justify-between">
              <div>
                <p className="text-lg font-semibold text-gray-800"><i class="fa-solid fa-sack-dollar text-yellow-400"></i> Doanh thu</p>
                <p className="text-2xl font-bold text-gray-900">
                  {data?.doanhSoThanhCong?.soLuong != null ? formatToVND(data?.doanhSoThanhCong?.soLuong) : "0"}
                </p>
                <p className="text-sm text-gray-500">So với {soNgay} ngày trước: {formatToVND(data?.doanhSoThanhCong?.soLuongTruocDo)}</p>
              </div>
              <div className="flex flex-col items-center">
                <img
                  className="w-8 h-7"
                  src={data?.doanhSoThanhCong?.tiLe >= 0 ? growicon : downicon}
                  alt="trend icon"
                />
                <p className="text-sm font-medium text-blue-600">
                  {/* {(data?.doanhSoThanhCong?.tiLe != null ? Number(data.doanhSoThanhCong.tiLe).toFixed(2) : "0.00")}% */}
                </p>
              </div>
            </div>
            <div className="w-px bg-gray-200"></div>
            <div className="flex-1 flex items-center justify-between">
              <div>
                <p className="text-lg font-semibold text-gray-800 truncate max-w-[180px]">
                  <i class="fa-solid fa-dollar-sign text-blue-500"></i> Doanh thu trung bình
                </p>
                <p className="text-2xl font-bold text-gray-900">
                  {data?.doanhSoTrungBinh?.doanhSo != null ? formatToVND(data?.doanhSoTrungBinh.doanhSo) : "0"}
                </p>
                <p className="text-sm text-gray-500">So với {soNgay} ngày trước : {formatToVND(data?.doanhSoTrungBinh?.doanhSoTruocDo)}</p>
              </div>
              <div className="flex flex-col items-center">
                <img
                  className="w-8 h-7"
                  src={data?.doanhSoTrungBinh?.tiLe >= 0 ? growicon : downicon}
                  alt="trend icon"
                />
                <p className="text-sm font-medium text-blue-600">
                  {/* {(data?.doanhSoTrungBinh?.tiLe != null ? Number(data.doanhSoTrungBinh.tiLe).toFixed(2) : "0.00")}% */}
                </p>
              </div>
            </div>
          </div>
          <div
            className="w-32 bg-gradient-to-b from-orange-600 to-orange-400 flex items-center justify-center text-white font-semibold text-sm text-center"
            style={{ clipPath: "polygon(15% 0, 85% 0, 100% 100%, 0% 100%)" }}
          >
            Đơn thành công
          </div>
        </div>
      </div>

      {/* Conversion Rates Section with Arrows */}
     <div className="basis-1/6 flex flex-col justify-between items-start h-64 py-4">
  <div className='mt-[10%] flex flex-row'>
    <img src={muiTen1} />
    <p className='ml-1 mt-[20%]'>
      Tỷ lệ (Lượt truy cập / Đơn được đặt) 
      <strong>
        <span className='text-blue-300 ml-1'>
          {
            data?.truyCap?.soLuong > 0
              ? ((data?.base?.tongDonDat || 0) / data?.truyCap.soLuong * 100).toFixed(2)
              : "0.00"
          }%
        </span>
      </strong>
    </p>
  </div>

  <div className='mt-[15%] flex flex-row'>
    <img src={muiTen2} />
    <p className='ml-1 mt-[20%]'>
      Tỷ lệ chuyển đổi (Từ đơn được đặt thành Đơn thành công) 
      <strong>
        <span className='text-blue-300 ml-1'>
          {
            data?.base?.tongDonDat > 0
              ? ((data?.base?.tongThanhCong || 0) / data?.base?.tongDonDat * 100).toFixed(2)
              : "0.00"
          }%
        </span>
      </strong>
    </p>
  </div>
</div>



      <div className="basis-1/6 mt-[7%] relative">
        <img className="h-[88%]" src={muiTen3} />
        <p className="absolute left-1 top-1/3 -translate-y-1/2 max-w-[80%]">
          Tỷ lệ  (Lượt truy cập / Đơn thành công)
          <strong>
            <span className='text-blue-300 ml-1'>
              {
                data?.truyCap?.soLuong > 0
                  ? ((data?.base?.tongThanhCong || 0) / data?.truyCap?.soLuong * 100).toFixed(2)
                  : "0.00"
              }%
            </span>
          </strong>
        </p>

      </div>
    </div>
  );
}

export { TongQuanDoanhThu };
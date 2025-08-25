import { useEffect, useState, useRef } from "react";
import { LineChart, CartesianGrid, XAxis, YAxis, Line, Tooltip, ResponsiveContainer } from "recharts";
import { getThongKe } from "../../../services/DealService";
import { useSearchParams } from "react-router-dom";
import { toast } from "react-toastify";
import { formatToVND } from "../../../utils/Format";
import React from "react";
import gsap from "gsap";

const COLORS = ["#FFD700", "#1E90FF", "#9C27B0", "#FF9800"];

function CustomLineTooltip({ active, payload, label }) {
  if (active && payload?.length) {
    return (
      <div className="bg-white border border-gray-200 rounded shadow-sm p-2">
        <p className="bg-gray-100 p-1 rounded font-semibold">{`Ngày: ${label}`}</p>
        {payload.map((entry, index) => (
          <div className="flex items-center mt-1" key={index}>
            <span className="w-3 h-3 inline-block mr-1" style={{ backgroundColor: entry.color }}></span>
            <span className="text-xs">{entry.name}: {entry.value}</span>
          </div>
        ))}
      </div>
    );
  }
  return null;
}

function ThongKeDeal() {
  const [data, setData] = useState({});
  const [searchParams] = useSearchParams();
  const [currentPage, setCurrentPage] = useState(0);
  const chartRef = useRef(null);
  const headerRef = useRef(null);
  const statsRef = useRef(null);
  const tableRef = useRef(null);
  const id = searchParams.get("id");

  useEffect(() => {
    getThongKe(id)
      .then(setData)
      .catch(() => toast.error("Lấy dữ liệu thất bại"));
  }, [id]);

  // Xử lý kéo chuột
  useEffect(() => {
    const chart = chartRef.current;
    if (!chart) return;

    let startX = 0;
    const handleMouseDown = (e) => {
      startX = e.clientX;
      chart.style.cursor = "grabbing";
    };
    const handleMouseUp = (e) => {
      chart.style.cursor = "grab";
      const deltaX = e.clientX - startX;
      if (Math.abs(deltaX) > 50) {
        setCurrentPage((prev) =>
          deltaX > 0 && prev > 0
            ? prev - 1
            : deltaX < 0 && prev < totalPages - 1
            ? prev + 1
            : prev
        );
      }
    };

    chart.addEventListener("mousedown", handleMouseDown);
    chart.addEventListener("mouseup", handleMouseUp);
    return () => {
      chart.removeEventListener("mousedown", handleMouseDown);
      chart.removeEventListener("mouseup", handleMouseUp);
    };
  }, [currentPage]);

  // Tạo dữ liệu cho biểu đồ
  const generateDateRange = (start, end) => {
    const dates = [];
    let currentDate = new Date(start);
    const endDate = new Date(end);
    while (currentDate <= endDate) {
      dates.push(currentDate.toISOString().split("T")[0]);
      currentDate.setDate(currentDate.getDate() + 1);
    }
    return dates;
  };

  const dateRange = generateDateRange(data?.batdau, data?.kt);
  const dataChart = dateRange.map((date) => ({
    date,
    soHoaDon: data?.danhSachHoaDon?.filter(
      (item) => item.ngayLap && new Date(item.ngayLap).toISOString().split("T")[0] === date
    ).length || 0,
  }));

  const ITEMS_PER_PAGE = 15;
  const totalPages = Math.ceil(dataChart.length / ITEMS_PER_PAGE);
  const startIndex = currentPage * ITEMS_PER_PAGE;
  const displayedData = dataChart.slice(startIndex, startIndex + ITEMS_PER_PAGE);

  const formatXAxis = (date, index) => {
    const current = new Date(date);
    const prev = index > 0 ? new Date(dateRange[index + startIndex - 1]) : null;
    const currentMonthYear = `${current.getMonth() + 1}/${current.getFullYear()}`;
    const prevMonthYear = prev ? `${prev.getMonth() + 1}/${prev.getFullYear()}` : null;
    return index === 0 || currentMonthYear !== prevMonthYear
      ? `${current.getDate()}/${current.getMonth() + 1}/${current.getFullYear()}`
      : current.getDate().toString();
  };

  const handleScroll = (direction) => {
    setCurrentPage((prev) =>
      direction === "left" && prev > 0
        ? prev - 1
        : direction === "right" && prev < totalPages - 1
        ? prev + 1
        : prev
    );
  };

  // GSAP animations
  useEffect(() => {
    const ctx = gsap.context(() => {
      // Header animation
      gsap.from(headerRef.current, {
        opacity: 0,
        y: -30,
        duration: 0.6,
        ease: "power2.out",
      });

      // Stats cards animation
      gsap.from(statsRef.current.children, {
        opacity: 0,
        y: 20,
        duration: 0.7,
        delay: 0.2,
        ease: "power2.out",
        stagger: 0.1,
      });

      // Chart animation
      gsap.from(chartRef.current, {
        opacity: 0,
        scale: 0.95,
        duration: 0.8,
        delay: 0.4,
        ease: "power2.out",
      });

      // Table animation
      gsap.from(tableRef.current, {
        opacity: 0,
        y: 20,
        duration: 0.8,
        delay: 0.6,
        ease: "power2.out",
      });
    });

    return () => ctx.revert();
  }, []);

  const maxSoHoaDon = Math.max(...dataChart.map((item) => item.soHoaDon), 10);
  const yAxisInterval = Math.ceil(maxSoHoaDon / 5);

  return (
    <div className="p-6 min-h-screen">
      <div ref={headerRef} className="bg-white mb-3 p-3 rounded-md shadow-sm flex items-center">
        <i className="fa-solid fa-chart-line bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
        <div>
          <strong className="text-lg text-blue-600">Thống kê hiệu quả Deal</strong>
          <p className="text-sm text-gray-600 mt-1">
            Thống kê các thông số cơ bản hiệu quả hoạt động của Deal
          </p>
        </div>
      </div>

      <div ref={statsRef} className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4 mb-6">
        <div className="stat-card bg-white p-4 rounded-lg shadow-sm flex items-center">
          <i className="fa-solid fa-receipt bg-blue-100 text-blue-500 p-3 rounded-md mr-3"></i>
          <div>
            <h4 className="text-base font-semibold text-gray-700">Hóa đơn áp dụng</h4>
            <p className="text-2xl font-bold text-blue-600 mt-1">{data?.soLuongHoaDonApDungNOW || 0}</p>
            <p className="text-sm text-gray-500">Cùng kỳ: {data?.soLuongHoaDonApDungCungKy || 0}</p>
            <p className="text-sm text-gray-500">Kỳ trước: {data?.soLuongHoaDonApDungTruocDo || 0}</p>
          </div>
        </div>

        {/* <div className="stat-card bg-white p-4 rounded-lg shadow-sm flex items-center">
          <i className="fa-solid fa-cart-shopping bg-green-100 text-green-600 p-3 rounded-md mr-3"></i>
          <div>
            <h4 className="text-base font-semibold text-gray-700">Số lượng sản phẩm chính/đơn</h4>
            <p className="text-2xl font-bold text-blue-600 mt-1">{data.trungBinhSanPhamChinhTrenHoaDonNOW || 0}</p>
            <p className="text-sm text-gray-500">Cùng kỳ: {data?.trungBinhSanPhamChinhTrenHoaDonCungKy || 0}</p>
            <p className="text-sm text-gray-500">Kỳ trước: {data?.trungBinhSanPhamChinhTrenHoaDonTruocDo || 0}</p>
          </div>
        </div> */}

        {/* <div className="stat-card bg-white p-4 rounded-lg shadow-sm flex items-center">
          <i className="fa-solid fa-tags bg-purple-100 text-purple-600 p-3 rounded-md mr-3"></i>
          <div>
            <h4 className="text-base font-semibold text-gray-700">Số lượng sản phẩm phụ/đơn</h4>
            <p className="text-2xl font-bold text-blue-600 mt-1">{data.trungBinhSanPhamPhuTrenHoaDonNOW || 0}</p>
            <p className="text-sm text-gray-500">Cùng kỳ: {data?.trungBinhSanPhamPhuTrenHoaDonCungKy || 0}</p>
            <p className="text-sm text-gray-500">Kỳ trước: {data?.trungBinhSanPhamPhuTrenHoaDonTruocDo || 0}</p>
          </div>
        </div> */}

        <div className="stat-card bg-white p-4 rounded-lg shadow-sm flex items-center">
          <i className="fa-solid fa-money-bill-wave bg-yellow-100 text-yellow-600 p-3 rounded-md mr-3"></i>
          <div>
            <h4 className="text-base font-semibold text-gray-700">Vốn bỏ ra</h4>
            <p className="text-2xl font-bold text-blue-600 mt-1">{formatToVND(data?.tongGiaVonNow || 0)}</p>
            <p className="text-sm text-gray-500">Cùng kỳ: {formatToVND(data?.tongGiaVonCungKy || 0)}</p>
            <p className="text-sm text-gray-500">Kỳ trước: {formatToVND(data?.tongGiaVonTruocDo || 0)}</p>
          </div>
        </div>

        <div className="stat-card bg-white p-4 rounded-lg shadow-sm flex items-center">
          <i className="fa-solid fa-coins bg-teal-100 text-teal-600 p-3 rounded-md mr-3"></i>
          <div>
            <h4 className="text-base font-semibold text-gray-700">Doanh thu</h4>
            <p className="text-2xl font-bold text-blue-600 mt-1">{formatToVND(data?.tongDoanhSoNow || 0)}</p>
            <p className="text-sm text-gray-500">Cùng kỳ: {formatToVND(data?.tongDoanhSoCungKy || 0)}</p>
            <p className="text-sm text-gray-500">Kỳ trước: {formatToVND(data?.tongDoanhSoTruocDo || 0)}</p>
          </div>
        </div>

        <div className="stat-card bg-white p-4 rounded-lg shadow-sm flex items-center">
          <i className="fa-solid fa-chart-line bg-pink-100 text-pink-600 p-3 rounded-md mr-3"></i>
          <div>
            <h4 className="text-base font-semibold text-gray-700">Lợi nhuận</h4>
            <p className="text-2xl font-bold text-blue-600 mt-1">
              {formatToVND((data?.tongDoanhSoNow || 0) - (data?.tongGiaVonNow || 0))}
            </p>
            <p className="text-sm text-gray-500">
              Cùng kỳ: {formatToVND((data?.tongDoanhSoCungKy || 0) - (data?.tongGiaVonCungKy || 0))}
            </p>
            <p className="text-sm text-gray-500">
              Kỳ trước: {formatToVND((data?.tongDoanhSoTruocDo || 0) - (data?.tongGiaVonTruocDo || 0))}
            </p>
          </div>
        </div>

        <div className="stat-card bg-white p-4 rounded-lg shadow-sm flex items-center">
          <i className="fa-solid fa-boxes-stacked bg-indigo-100 text-indigo-600 p-3 rounded-md mr-3"></i>
          <div>
            <h4 className="text-base font-semibold text-gray-700">Tổng lượng bán ra</h4>
            <p className="text-2xl font-bold text-blue-600 mt-1">{data.tongSoLuongBanNow || 0}</p>
            {/* <p className="text-sm text-gray-500">Cùng kỳ: {data?.tongSoLuongBanCungKy || 0}</p> */}
            <p className="text-sm text-gray-500">Kỳ trước: {data?.tongSoLuongBanTruocDo || 0}</p>
          </div>
        </div>

       
      </div>

      {/* <div className="bg-white p-4 rounded-lg shadow-sm">
        <div className="flex items-center mb-3">
          <i className="fa-solid fa-chart-line bg-blue-100 text-blue-500 p-2 rounded-sm mr-2"></i>
          <h3 className="text-lg font-semibold text-gray-800">Số lượng hóa đơn theo ngày</h3>
        </div>
        <div ref={chartRef} className="chart-container relative group" style={{ height: "400px" }}>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={displayedData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e5e7eb" />
              <XAxis dataKey="date" tickFormatter={formatXAxis} tick={{ fontSize: 12 }} interval={0} />
              <YAxis
                domain={[0, maxSoHoaDon]}
                tickCount={6}
                interval={yAxisInterval}
                tick={{ fontSize: 12 }}
              />
              <Tooltip content={<CustomLineTooltip />} />
              <Line
                type="monotone"
                dataKey="soHoaDon"
                stroke={COLORS[1]}
                strokeWidth={3}
                name="Số Hóa Đơn"
                dot={displayedData.length > 20 ? false : true}
                animationDuration={500}
              />
            </LineChart>
          </ResponsiveContainer>
          <div className="flex justify-center mt-2">
            <div className="flex items-center">
              <div className="w-4 h-4 rounded-full" style={{ backgroundColor: COLORS[1] }}></div>
              <span className="text-xs font-medium ml-2">Số Hóa Đơn</span>
            </div>
          </div>
          <button
            className={`absolute left-2 top-1/2 -translate-y-1/2 bg-gradient-to-r from-blue-500 to-blue-700 text-white rounded-full p-2 opacity-0 group-hover:opacity-70 hover:bg-blue-800 transition-all duration-300 ${
              currentPage === 0 ? "invisible" : ""
            }`}
            onClick={() => handleScroll("left")}
            disabled={currentPage === 0}
          >
            <i className="fa-solid fa-chevron-left"></i>
          </button>
          <button
            className={`absolute right-2 top-1/2 -translate-y-1/2 bg-gradient-to-r from-blue-500 to-blue-700 text-white rounded-full p-2 opacity-0 group-hover:opacity-70 hover:bg-blue-800 transition-all duration-300 ${
              currentPage >= totalPages - 1 ? "invisible" : ""
            }`}
            onClick={() => handleScroll("right")}
            disabled={currentPage >= totalPages - 1}
          >
            <i className="fa-solid fa-chevron-right"></i>
          </button>
        </div>
      </div> */}

      <div ref={tableRef} className="p-6 bg-white rounded-lg shadow-md mt-6">
        <div className="flex items-center mb-3">
          <i className="fa-solid fa-table bg-blue-100 text-blue-500 p-2 rounded-sm mr-2"></i>
          <h2 className="text-xl font-semibold text-gray-800">Chi tiết thông tin</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full border-collapse text-sm text-gray-700">
            <thead>
              <tr className="bg-gray-100 text-left text-gray-600 font-medium">
                <th className="p-3 border-b">Sản phẩm</th>
                <th className="p-3 border-b">Phân Loại</th>
                <th className="p-3 border-b">Tổng giá gốc bán ra</th>
                <th className="p-3 border-b">Tổng doanh thu</th>
                <th className="p-3 border-b">Tổng lợi nhuận thu được</th>
                <th className="p-3 border-b">Tổng lượt bán</th>
                <th className="p-3 border-b">Tổng số lượng bán ra</th>
              </tr>
            </thead>
            <tbody>
              {data?.thonTinPhanLoai?.length > 0 ? (
                data.thonTinPhanLoai.map((d, index) => (
                  <React.Fragment key={index}>
                    <tr className="hover:bg-gray-50 transition-colors duration-200 text-center justify-center">
                      <td rowSpan={d?.thongTinBienThe?.length || 1} className="p-3 border-b align-middle">
                        <div className="flex items-center space-x-3">
                          <img
                            src={d.anhBia || 'https://via.placeholder.com/48'}
                            alt={d.ten || 'Unknown'}
                            className="w-12 h-12 object-cover rounded"
                          />
                          <p className="font-medium text-gray-800">{d.ten || 'N/A'}</p>
                        </div>
                      </td>
                      <td className="p-3 border-b">
                        <div className="flex items-center space-x-3">
                          <img
                            src={d.thongTinBienThe?.[0]?.anhBienThe || 'https://via.placeholder.com/48'}
                            alt={d.thongTinBienThe?.[0]?.tenBienThe || 'Unknown'}
                            className="w-12 h-12 object-cover rounded"
                          />
                          <p className="text-gray-700">{d.thongTinBienThe?.[0]?.tenBienThe || 'N/A'}</p>
                        </div>
                      </td>
                      <td className="p-3 border-b">{formatToVND(d?.thongTinBienThe?.[0]?.tongGiaGoc ?? 0)}</td>
                      <td className="p-3 border-b">{formatToVND(d?.thongTinBienThe?.[0]?.tongDoanhSo ?? 0)}</td>
                      <td className="p-3 border-b">
                        {formatToVND((d?.thongTinBienThe?.[0]?.tongDoanhSo ?? 0) - (d?.thongTinBienThe?.[0]?.tongGiaGoc ?? 0))}
                      </td>
                      <td className="p-3 border-b">{d?.thongTinBienThe?.[0]?.tongSoLuotBan ?? 0}</td>
                      <td className="p-3 border-b">{d?.thongTinBienThe?.[0]?.tongSoLuongBan ?? 0}</td>
                    </tr>
                    {d?.thongTinBienThe?.slice(1).map((dg, i) => (
                      <tr key={`${index}-${i + 1}`} className="hover:bg-gray-50 transition-colors duration-200 text-center justify-center">
                        <td className="p-3 border-b">
                          <div className="flex items-center space-x-3">
                            <img
                              src={dg?.anhBienThe || 'https://via.placeholder.com/48'}
                              alt={dg?.tenBienThe || 'Unknown'}
                              className="w-12 h-12 object-cover rounded"
                            />
                            <p className="text-gray-700">{dg?.tenBienThe || 'N/A'}</p>
                          </div>
                        </td>
                        <td className="p-3 border-b">{formatToVND(dg?.tongGiaGoc ?? 0)}</td>
                        <td className="p-3 border-b">{formatToVND(dg?.tongDoanhSo ?? 0)}</td>
                        <td className="p-3 border-b">
                          {formatToVND((dg?.tongDoanhSo ?? 0) - (dg?.tongGiaGoc ?? 0))}
                        </td>
                        <td className="p-3 border-b">{dg?.tongSoLuotBan ?? 0}</td>
                        <td className="p-3 border-b">{dg?.tongSoLuongBan ?? 0}</td>
                      </tr>
                    ))}
                  </React.Fragment>
                ))
              ) : (
                <tr>
                  <td colSpan="7" className="p-3 text-center text-gray-500">
                    Không có dữ liệu
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      <style jsx>{`
        .shadow-sm {
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .stat-card {
          transition: transform 0.3s ease;
        }
        .stat-card:hover {
          transform: translateY(-5px);
        }
        .chart-container {
          border: 1px solid #e5e7eb;
          border-radius: 8px;
          padding: 10px;
          position: relative;
          cursor: grab;
        }
        .chart-container:hover button {
          opacity: 0.7;
        }
        button:hover {
          transform: scale(1.1);
          transition: transform 0.2s ease-in-out;
        }
        button:disabled {
          background: #ccc !important;
          cursor: not-allowed;
        }
      `}</style>
    </div>
  );
}

export default ThongKeDeal;
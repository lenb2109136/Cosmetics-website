import React, { useEffect, useState, useMemo, useRef } from 'react';
import { formatToVND } from '../../../utils/Format';
import { getThongKe } from '../../../services/FlashSaleService';
import { useSearchParams } from 'react-router-dom';
import { gsap } from 'gsap';
import { LineChart, CartesianGrid, XAxis, YAxis, Line, ResponsiveContainer, Tooltip, Legend } from 'recharts';
import { Pagination } from '../../../components/commons/Pagination';

const COLORS = ['#1E90FF', '#FFA500', '#FF4500', '#32CD32'];

function CustomLineTooltip({ active, payload, label }) {
  if (active && payload?.length) {
    return (
      <div className="bg-white border border-gray-200 rounded-md shadow-sm p-2">
        <p className="font-medium bg-gray-50 p-1 rounded">{`Ngày: ${label}`}</p>
        {payload.map((entry, index) => (
          <div key={index} className="flex justify-between text-sm py-1">
            <div className="flex items-center">
              <span className="w-2 h-2 rounded-full mr-2" style={{ backgroundColor: entry.color }} />
              <span>{entry.name}:</span>
            </div>
            <span>{entry.value}</span>
          </div>
        ))}
      </div>
    );
  }
  return null;
}

const ThongKeFlashSale = () => {
  const [dataNew, setDataNew] = useState({});
  const [dataOld, setDataOld] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchParams] = useSearchParams();
  const id = searchParams.get('id');
  const [currentPage, setCurrentPage] = useState(0);

  // Refs for GSAP animations (kept for stats and table, removed for charts)
  const statsContainerRef = useRef(null);
  const tableRef = useRef(null);
  const paginationRef = useRef(null);
  const [dd,setdd]=useState()

  useEffect(() => {
    setIsLoading(true);
    setError(null);
    getThongKe(id)
      .then((data) => {
        setdd(data)
        setDataNew(data.dataNew || {});
        setDataOld(data.dataOld || {});
      })
      .catch((error) => {
        console.error('Error fetching data:', error);
        setError('Không thể tải dữ liệu. Vui lòng thử lại.');
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, [id]);

  // GSAP Animations (only for stats, table, and pagination)
  useEffect(() => {
    if (!isLoading && !error) {
      const statCards = statsContainerRef.current.querySelectorAll('.stat-card');
      gsap.fromTo(
        statCards,
        { opacity: 0, y: 20 },
        {
          opacity: 1,
          y: 0,
          duration: 0.6,
          stagger: 0.1,
          ease: 'power2.out',
        }
      );

      const tableRows = tableRef.current.querySelectorAll('tbody tr');
      gsap.fromTo(
        tableRows,
        { opacity: 0, x: -20 },
        {
          opacity: 1,
          x: 0,
          duration: 0.8,
          stagger: 0.1,
          ease: 'power2.out',
          delay: 0.3,
        }
      );

      if (totalPages > 1 && paginationRef.current) {
        gsap.fromTo(
          paginationRef.current,
          { opacity: 0, y: 10 },
          {
            opacity: 1,
            y: 0,
            duration: 0.5,
            ease: 'power2.out',
            delay: 0.6,
          }
        );
      }
    }
  }, [isLoading, error]);

  const calculateChange = (newValue, oldValue) => {
    newValue = newValue ?? 0;
    oldValue = oldValue ?? 0;
    if (oldValue === 0 && newValue > 0) {
      return "100.00";
    } else if (newValue === 0 && oldValue > 0) {
      return "-100.00";
    } else if (newValue === 0 && oldValue === 0) {
      return "0.00";
    }
    const change = ((newValue - oldValue) / oldValue * 100).toFixed(2);
    return change;
  };

  const stats = [
    { label: 'Tổng số hóa đơn bán ra', key: 'tongSoLuongHoaDon', format: (v) => v },
    {
      label: 'Khách mua lại',
      key: 'tongSoLuongKhachMuaLai',
      format: (v) => v,
      calculate: (dataNew, dataOld) => {
        const newTotalSales = dataNew?.tonSoLuotBan ?? 0;
        const newTotalNewPurchases = dataNew?.tongSoLuongMuaMoi ?? 0;
        const newTotalReturnPurchases = Math.max(0, newTotalSales - newTotalNewPurchases);
        const oldTotalSales = dataOld?.tonSoLuotBan ?? 0;
        const oldTotalNewPurchases = dataOld?.tongSoLuongMuaMoi ?? 0;
        const oldTotalReturnPurchases = Math.max(0, oldTotalSales - oldTotalNewPurchases);
        return { newValue: newTotalReturnPurchases, oldValue: oldTotalReturnPurchases };
      },
    },
    { label: 'Số lượng mua khách hàng mới', key: 'tongSoLuongMuaMoi', format: (v) => v },
    {
      label: 'Tổng số lượng bán',
      key: 'tongSoLuongBanRa',
      format: (v) => v,
      calculate: (dataNew, dataOld) => {
        const newTotalSold = dataNew?.thonTinPhanLoai?.reduce((sum, product) =>
          sum + (product.thongTinBienThe?.reduce((q, variant) => q + (variant.tongSoLuongBan || 0), 0) || 0), 0) || 0;
        const oldTotalSold = dataOld?.thonTinPhanLoai?.reduce((sum, product) =>
          sum + (product.thongTinBienThe?.reduce((q, variant) => q + (variant.tongSoLuongBan || 0), 0) || 0), 0) || 0;
        return { newValue: newTotalSold, oldValue: oldTotalSold };
      },
    },
    { label: 'Tổng giá vốn', key: 'tongGiaVon', format: (v) => formatToVND(v) },
    { label: 'Tổng giá trị đơn', key: 'tongGiaTriTatCaHoaDon', format: (v) => formatToVND(v) },
    {
      label: 'Tổng lợi nhuận',
      key: 'tongLoiNhuan',
      format: (v) => formatToVND(v),
      calculate: (dataNew, dataOld) => {
        const newProfit = (dataNew.tongGiaTriTatCaHoaDon ?? 0) - (dataNew.tongGiaVon ?? 0);
        const oldProfit = (dataOld.tongGiaTriTatCaHoaDon ?? 0) - (dataOld.tongGiaVon ?? 0);
        return { newValue: newProfit, oldValue: oldProfit };
      },
    },
    { label: 'Giá trị TB/đơn', key: 'giaTriTrungBinhTrenMoiHoaDon', format: (v) => formatToVND(v) },
    { label: 'Tổng lượt truy cập', key: 'tongLuotTruyCap', format: (v) => v },
    { label: 'Lượt truy cập mới', key: 'tongLuotTruyCapMoi', format: (v) => v },
  ];

  const generateDateRange = (start, end, accessData, purchaseData) => {
    const dates = [];
    let currentDate = new Date(start);
    const endDate = new Date(end);
    const lastAccessDate = accessData.length > 0
      ? new Date(Math.max(...accessData.map(item => new Date(item.ngayGioTruyCap))))
      : endDate;
    const lastPurchaseDate = purchaseData.length > 0
      ? new Date(Math.max(...purchaseData.map(item => new Date(item.ngayBan))))
      : endDate;
    const effectiveEndDate = new Date(Math.min(endDate, lastAccessDate > lastPurchaseDate ? lastAccessDate : lastPurchaseDate));
    while (currentDate <= effectiveEndDate) {
      dates.push(currentDate.toISOString().split('T')[0]);
      currentDate.setDate(currentDate.getDate() + 1);
    }
    return dates;
  };

  const products = useMemo(() =>
    dataNew?.thonTinPhanLoai?.map(product => ({
      tenSanPham: product.ten,
      dataTruyCap: product.luotTruyCap?.dataTruyCap || [],
      thongTinMuaHang: product.thongTinMuaHang || []
    })) || [],
    [dataNew]
  );

  const variants = useMemo(() =>
    dataNew?.thonTinPhanLoai?.flatMap(product =>
      product.thongTinMuaHang.map(variant => ({
        tenBienThe: variant.ten,
        lanBan: variant.lanBan || []
      }))
    ) || [],
    [dataNew]
  );

  const allDataTruyCap = products.flatMap(p => p.dataTruyCap);
  const allDataMuaHang = variants.flatMap(v => v.lanBan);
  const dateRange = useMemo(() =>
    generateDateRange(dataNew.ngayBatDau || new Date(), dataNew.ngayKetThuc || new Date(), allDataTruyCap, allDataMuaHang),
    [dataNew.ngayBatDau, dataNew.ngayKetThuc, allDataTruyCap, allDataMuaHang]
  );

  const dataChartAccess = useMemo(() =>
    dateRange.map(date => {
      const result = { date };
      products.forEach((product) => {
        result[product.tenSanPham] = product.dataTruyCap.filter((item) => {
          if (!item.ngayGioTruyCap) return false;
          return new Date(item.ngayGioTruyCap).toISOString().split('T')[0] === date;
        }).length;
      });
      return result;
    }),
    [dateRange, products]
  );

  const dataChartPurchase = useMemo(() =>
    dateRange.map(date => {
      const result = { date };
      variants.forEach((variant) => {
        result[variant.tenBienThe] = variant.lanBan
          .filter((item) => {
            if (!item.ngayBan) return false;
            return new Date(item.ngayBan).toISOString().split('T')[0] === date;
          })
          .reduce((sum, item) => sum + (item.danhSachPhanNho?.reduce((q, part) => q + (part.soLuong || 0), 0) || 0), 0);
      });
      return result;
    }),
    [dateRange, variants]
  );

  const ITEMS_PER_PAGE = 15;
  const totalPages = Math.ceil(dateRange.length / ITEMS_PER_PAGE);
  const startIndex = currentPage * ITEMS_PER_PAGE;
  const displayedAccessData = dataChartAccess.slice(startIndex, Math.min(startIndex + ITEMS_PER_PAGE, dateRange.length));
  const displayedPurchaseData = dataChartPurchase.slice(startIndex, Math.min(startIndex + ITEMS_PER_PAGE, dateRange.length));

  const formatXAxis = (date, index) => {
    const current = new Date(date);
    const prev = index > 0 ? new Date(dateRange[index + startIndex - 1]) : null;
    const currentMonthYear = `${current.getMonth() + 1}/${current.getFullYear()}`;
    const prevMonthYear = prev ? `${prev.getMonth() + 1}/${prev.getFullYear()}` : null;
    return index === 0 || currentMonthYear !== prevMonthYear
      ? `${current.getDate()}/${current.getMonth() + 1}/${current.getFullYear()}`
      : current.getDate().toString();
  };

  if (isLoading) {
    return <div className="p-6 text-center">Đang tải...</div>;
  }

  if (error) {
    return <div className="p-6 text-center text-red-500">{error}</div>;
  }

  return (
    <div className="p-6 min-h-screen">
      <div className="bg-white mb-3 p-3 rounded-md">
        <i className="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
        <strong className="text-lg">Thống kê hiệu quả flashsale</strong>
        <p className="text-sm text-gray-600 mt-1">
          Thống kê các thông số cơ bản hiệu quả hoạt động của flashsale
        </p>
      </div>
      <div ref={statsContainerRef} className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
       {stats.map((stat, index) => (
  <div key={index} className="stat-card bg-white p-3 rounded-lg shadow-md">
    <div className="flex items-center">
      <i
        className={`fa-solid ${
          stat.label.includes('hóa đơn') ? 'fa-receipt' :
          stat.label.includes('Khách mua lại') ? 'fa-users' :
          stat.label.includes('khách hàng mới') ? 'fa-user-plus' :
          stat.label.includes('lượt bán') || stat.label.includes('bán') ? 'fa-cart-shopping' :
          stat.label.includes('giá vốn') ? 'fa-money-bill-wave' :
          stat.label.includes('lợi nhuận') ? 'fa-chart-line' :
          stat.label.includes('TB/đơn') ? 'fa-coins' :
          stat.label.includes('truy cập mới') ? 'fa-eye-slash' :
          stat.label.includes('truy cập') ? 'fa-eye' :
          'fa-circle-info'
        } ${
          stat.label.includes('hóa đơn') ? 'bg-indigo-100 text-indigo-600' :
          stat.label.includes('Khách mua lại') ? 'bg-blue-100 text-blue-600' :
          stat.label.includes('khách hàng mới') ? 'bg-purple-100 text-purple-600' :
          stat.label.includes('lượt bán') || stat.label.includes('bán') ? 'bg-green-100 text-green-600' :
          stat.label.includes('giá vốn') ? 'bg-yellow-100 text-yellow-600' :
          stat.label.includes('lợi nhuận') ? 'bg-pink-100 text-pink-600' :
          stat.label.includes('TB/đơn') ? 'bg-teal-100 text-teal-600' :
          stat.label.includes('truy cập mới') ? 'bg-red-100 text-red-600' :
          stat.label.includes('truy cập') ? 'bg-green-100 text-green-600' :
          'bg-blue-100 text-blue-500'
        } p-3 text-lg rounded-md mr-3`}
      ></i>
      <div>
        <p className="font-medium text-gray-700">{stat.label}</p>
        <p className="text-2xl font-bold text-blue-600">
          {stat.calculate
            ? stat.format(stat.calculate(dataNew, dataOld).newValue)
            : stat.format(dataNew[stat.key] ?? 0)}
        </p>
      </div>
    </div>
    <p className="text-sm text-gray-500 leading-tight">
      So với trước:{' '}
      {stat.calculate
        ? stat.format(stat.calculate(dataNew, dataOld).oldValue)
        : stat.format(dataOld[stat.key] ?? 0)}
    </p>
  </div>
))}

      </div>
      {/* Access Trend Chart */}
      {/* <div className="bg-white p-4 rounded-lg shadow-md mt-6">
        <p className="text-xl font-semibold text-gray-800 mb-2">Xu hướng lượt truy cập theo ngày</p>
        {dataChartAccess.length > 0 ? (
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={displayedAccessData} margin={{ top: 10, right: 20, left: 0, bottom: 10 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e5e7eb" />
              <XAxis dataKey="date" tickFormatter={formatXAxis} tick={{ fontSize: 12 }} interval={0} />
              <YAxis domain={[0, Math.max(...dataChartAccess.flatMap(item => products.map(p => item[p.tenSanPham] || 0)), 10)]} tick={{ fontSize: 12 }} />
              <Tooltip content={<CustomLineTooltip />} />
              {products.map((product, index) => (
                <Line
                  key={product.tenSanPham}
                  type="monotone"
                  dataKey={product.tenSanPham}
                  name={product.tenSanPham}
                  stroke={COLORS[index % COLORS.length]}
                  strokeWidth={2}
                  dot={false}
                />
              ))}
              <Legend wrapperStyle={{ paddingTop: '10px', fontSize: '12px' }} />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div className="p-4 text-center text-gray-500">Không có dữ liệu truy cập</div>
        )}
      </div> */}
      {/* Purchase Trend Chart */}
      {/* <div className="bg-white p-4 rounded-lg shadow-md mt-6">
        <p className="text-xl font-semibold text-gray-800 mb-2">Xu hướng số lượng mua hàng theo ngày</p>
        {dataChartPurchase.length > 0 && variants.some(v => v.lanBan.length > 0) ? (
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={displayedPurchaseData} margin={{ top: 10, right: 20, left: 0, bottom: 10 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e5e7eb" />
              <XAxis dataKey="date" tickFormatter={formatXAxis} tick={{ fontSize: 12 }} interval={0} />
              <YAxis domain={[0, Math.max(...dataChartPurchase.flatMap(item => variants.map(v => item[v.tenBienThe] || 0)), 10)]} tick={{ fontSize: 12 }} />
              <Tooltip content={<CustomLineTooltip />} />
              {variants.map((variant, index) => (
                <Line
                  key={variant.tenBienThe}
                  type="monotone"
                  dataKey={variant.tenBienThe}
                  name={variant.tenBienThe}
                  stroke={COLORS[index % COLORS.length]}
                  strokeWidth={2}
                  dot={false}
                />
              ))}
              <Legend wrapperStyle={{ paddingTop: '10px', fontSize: '12px' }} />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div className="p-4 text-center text-gray-500">Không có dữ liệu mua hàng</div>
        )}
      </div> */}
      {/* <div ref={paginationRef}>
        {totalPages > 1 && (
          <Pagination
            trangHienTai={currentPage}
            setTrangHienTai={setCurrentPage}
            soLuongTrang={totalPages}
          />
        )}
      </div> */}
      {/* Detailed Information Table */}
      <div ref={tableRef} className="p-6 bg-white rounded-lg shadow-md mt-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Chi tiết thông tin</h2>
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
              {dataNew?.thonTinPhanLoai?.length > 0 ? (
                dataNew.thonTinPhanLoai.map((d, index) => (
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
                            alt={d.thongTinBienThe?.[0]?.tenBienThe==="DEFAULT_SYSTEM_NAME_CLASSIFY"? "Mặc định" :d.thongTinBienThe?.[0]?.tenBienThe }
                            className="w-12 h-12 object-cover rounded"
                          />
                          <p className="text-gray-700">{d.thongTinBienThe?.[0]?.tenBienThe==="DEFAULT_SYSTEM_NAME_CLASSIFY"? "Mặc định" :d.thongTinBienThe?.[0]?.tenBienThe}</p>
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
                            <p className="text-gray-700">{dg?.tenBienThe ==="DEFAULT_SYSTEM_NAME_CLASSIFY"? "Mặc định" :dg?.tenBienThe}</p>
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
        .shadow-md {
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.08);
        }
      `}</style>
    </div>
  );
};

export { ThongKeFlashSale };
import { useEffect, useState } from "react";
import { getChiTietDoanhThu } from "../../../services/sanPhamService";
import { dinhDangNgay, formatDateToString, formatToVND } from "../../../utils/Format";
import { toast } from "react-toastify";
import { PieChart, Pie, Cell, Tooltip, Legend, LineChart, CartesianGrid, XAxis, YAxis, Line, ResponsiveContainer } from "recharts";
import { Pagination } from "../../../components/commons/Pagination";
import { XuHuongTruyCap } from "./XuHuongTruyCap";

const COLORS = [
    "#1e3a8a", // blue-900
    "#1d4ed8", // blue-700
    "#2563eb", // blue-600
    "#3b82f6", // blue-500
    "#60a5fa", // blue-400
    "#93c5fd", // blue-300
    "#bfdbfe", // blue-200
    "#dbeafe", // blue-100
];

function tinhThongKeDoanhThu(data) {
    const tongSoLuongBan = data.soLuongBanRa;
    const ngayMua = data.data?.map(item => item.thoiDiemMua.split('T')[0]);
    const ngayKhacNhau = [...new Set(ngayMua)];
    const soNgay = ngayKhacNhau.length;
    const tongDonBan = data.tongDonBan;
    const soDonTrungBinhTrenNgay = soNgay > 0 ? tongDonBan / soNgay : 0;
    return {
        tonSoLuongBan: tongSoLuongBan,
        soDonTrungBinhTrenNgay: soDonTrungBinhTrenNgay
    };
}

function CustomToolTipRounde({ active, payload }) {
    if (active && payload && payload.length) {
        const total = payload.reduce((sum, entry) => sum + entry.value, 0);
        return (
            <div className="bg-white p-3 border rounded shadow-sm">
                {payload.map((entry, index) => {
                    const percentage = ((entry.value / total) * 100).toFixed(2) + "%";
                    const truncatedName = entry.payload.ten.length > 15 ? entry.payload.ten.slice(0, 15) + "..." : entry.payload.ten;
                    return (
                        <div
                            key={`legend-${index}`}
                            className="flex items-center space-x-2 p-2 bg-gray-50 rounded-md shadow-sm"
                        >
                            <span
                                className="w-4 h-4 inline-block"
                                style={{ backgroundColor: entry.fill }}
                            ></span>
                            <span className="text-sm font-medium">{truncatedName} ({entry.value} - {percentage})</span>
                        </div>
                    );
                })}
            </div>
        );
    }
    return null;
}

function CustomPieChart({ data, dataKey, title, conten }) {
    const [legendPage, setLegendPage] = useState(0);
    const LEGEND_ITEMS_PER_PAGE = 6;

    const renderCustomLegend = ({ payload }) => {
        if (!payload || payload.length === 0) {
            return <div className="text-sm text-gray-500"></div>;
        }

        const total = data.reduce((sum, entry) => sum + entry[dataKey], 0);
        const totalLegendPages = Math.ceil(payload.length / LEGEND_ITEMS_PER_PAGE);
        const startIndex = legendPage * LEGEND_ITEMS_PER_PAGE;
        const endIndex = startIndex + LEGEND_ITEMS_PER_PAGE;
        const displayedLegend = payload.slice(startIndex, endIndex);

        return (
            <div className="flex flex-col items-start mt-6 w-full max-w-xs ml-6">
                <div className="flex flex-col gap-2 w-full">
                    {displayedLegend.map((entry, index) => {
                        const value = entry.payload[dataKey];
                        const percentage = ((value / total) * 100).toFixed(2) + "%";
                        const truncatedName =
                            entry.payload.ten.length > 15
                                ? entry.payload.ten.slice(0, 15) + "..."
                                : entry.payload.ten;
                        return (
                            <div className="w-full" key={`legend-${index}`}>
                                <div className="flex items-center justify-between w-full gap-2 text-xs h-6">
                                    <div className="flex items-center space-x-2 truncate max-w-[150px]">
                                        <span
                                            className="w-3 h-3 inline-block shrink-0"
                                            style={{
                                                backgroundColor:
                                                    COLORS[(index + startIndex) % COLORS.length],
                                            }}
                                        ></span>
                                        <span className="font-medium truncate whitespace-nowrap">
                                            {truncatedName}
                                        </span>
                                    </div>
                                    <span className="ml-2 text-right min-w-[90px] whitespace-nowrap">
                                        {value} -{" "}
                                        <span className="text-green-500 bg-green-100 px-1 py-0.5 rounded">
                                            {percentage}
                                        </span>
                                    </span>
                                </div>
                                <hr className="border-t border-gray-200 my-1 w-11/12 mx-auto" />
                            </div>
                        );
                    })}
                </div>
                {totalLegendPages > 1 && (
                    <div className="flex space-x-2 mt-2">
                        <button
                            onClick={() => setLegendPage((prev) => Math.max(prev - 1, 0))}
                            disabled={legendPage === 0}
                            className="px-2 py-1 bg-blue-500 text-white rounded disabled:bg-gray-300"
                        >
                            Prev
                        </button>
                        <span>{`Page ${legendPage + 1} of ${totalLegendPages}`}</span>
                        <button
                            onClick={() =>
                                setLegendPage((prev) =>
                                    Math.min(prev + 1, totalLegendPages - 1)
                                )
                            }
                            disabled={legendPage === totalLegendPages - 1}
                            className="px-2 py-1 bg-blue-500 text-white rounded disabled:bg-gray-300"
                        >
                            Next
                        </button>
                    </div>
                )}
            </div>
        );
    };

    return (
        <div className="mb-6 animate-fade-in w-full overflow-hidden">
            <div className="bg-white p-4 rounded-lg shadow-md relative">
                <div className="w-[200px] h-[200px] bg-blue-100 absolute rounded-full z-0 opacity-30 top-[-14px] left-[-60px]"></div>
                <div className="w-[200px] h-[200px] bg-blue-100 absolute rounded-full z-0 opacity-30 top-[-60px] left-[-14px]"></div>
                <div className="w-[200px] h-[200px] bg-blue-100 absolute rounded-full z-0 opacity-30 bottom-[-14px] right-[-60px]"></div>
                <div className="w-[200px] h-[200px] bg-blue-100 absolute rounded-full z-0 opacity-30 bottom-[-60px] right-[-14px]"></div>
                <div className="relative z-20">
                    <h3 className="text-left font-bold mb-2 text-gray-900">{title}</h3>
                </div>
                <p className="z-10 relative">{conten}</p>
                <div className="flex flex-row">
                    <div className="w-2/3">
                        <ResponsiveContainer width="100%" height={500}>
                            <PieChart>
                                <defs>
                                    {data &&
                                        data.map((_, index) => (
                                            <radialGradient
                                                id={`gradient-${index}`}
                                                key={`gradient-${index}`}
                                                cx="50%"
                                                cy="50%"
                                                r="50%"
                                            >
                                                <stop
                                                    offset="0%"
                                                    stopColor={COLORS[index % COLORS.length]}
                                                />
                                                <stop
                                                    offset="100%"
                                                    stopColor={`${COLORS[index % COLORS.length]}80`}
                                                />
                                            </radialGradient>
                                        ))}
                                </defs>
                                <Pie
                                    data={data || []}
                                    dataKey={dataKey}
                                    nameKey="ten"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={80}
                                    innerRadius={60}
                                    labelLine={false}
                                    label={false}
                                >
                                    {data &&
                                        data.map((entry, index) => (
                                            <Cell
                                                key={`cell-${index}`}
                                                fill={`url(#gradient-${index})`}
                                            />
                                        ))}
                                </Pie>
                                <Tooltip content={<CustomToolTipRounde />} />
                                <Legend content={renderCustomLegend} />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                    <div className="w-1/3 pl-6">
                        <Legend content={renderCustomLegend} />
                    </div>
                </div>
            </div>
        </div>
    );
}

function ChiTietDoanhThu({ batDau, ketThuc, id, bienThe, setcb }) {
    const [data, setData] = useState({});
    const [isLoaded, setIsLoaded] = useState(false);
    const [filter, setFilter] = useState(0);
    const [phanLoaiChon, setPhanLoaiChon] = useState(0);
    const [trang, setTrang] = useState(0);
    const [tong, setTong] = useState(0);

    const ITEMS_PER_PAGE = 7; // Set to 7 items per page for the table

    useEffect(() => {
        getChiTietDoanhThu(id, formatDateToString(batDau), formatDateToString(ketThuc), filter, phanLoaiChon)
            .then(data => {
                setData(data || {});
                setIsLoaded(true);
                setTong(Math.ceil(data?.data?.length / ITEMS_PER_PAGE)); // Calculate total pages based on 7 items
                setTrang(0);
            })
            .catch(error => {
                console.error("Error fetching data:", error);
                toast.error("Lấy dữ liệu thất bại");
                setIsLoaded(true);
            });
    }, [id, batDau, ketThuc, filter, phanLoaiChon]);

    // Paginate the table data
    const paginatedData = data?.data?.slice(trang * ITEMS_PER_PAGE, (trang + 1) * ITEMS_PER_PAGE) || [];

    const generateDateRange = (start, end) => {
        const dates = [];
        let currentDate = new Date(start);
        const endDate = new Date(end);
        while (currentDate <= endDate) {
            dates.push(currentDate.toISOString().split('T')[0]);
            currentDate.setDate(currentDate.getDate() + 1);
        }
        return dates;
    };

    const dateRange = generateDateRange(batDau, ketThuc);
    const dataChart = dateRange.map(date => {
        const items = (data?.data || []).filter(item => {
            if (!item.thoiDiemMua) return false;
            const parsedDate = new Date(item.thoiDiemMua);
            if (isNaN(parsedDate.getTime())) return false;
            const itemDate = parsedDate.toISOString().split('T')[0];
            return itemDate === date;
        });

        const aggregated = items.reduce(
            (acc, item) => ({
                date: date,
                tongSoLuong: acc.tongSoLuong + (item.tongSoLuong || 0),
                donGiaBan: acc.donGiaBan + (item.donGiaBan || 0),
                tongTien: acc.tongTien + (item.tongTien || 0),
                count: acc.count + 1,
            }),
            { tongSoLuong: 0, donGiaBan: 0, tongTien: 0, count: 0 }
        );

        return {
            date,
            tongSoLuong: aggregated.tongSoLuong,
            donGiaBan: aggregated.count > 0 ? aggregated.donGiaBan / aggregated.count : 0,
            tongTien: aggregated.tongTien,
        };
    });

    const formatXAxis = (date, index) => {
        const current = new Date(date);
        const prev = index > 0 ? new Date(dateRange[index - 1]) : null;
        const currentMonthYear = `${current.getMonth() + 1}/${current.getFullYear()}`;
        const prevMonthYear = prev ? `${prev.getMonth() + 1}/${prev.getFullYear()}` : null;
        if (index === 0 || currentMonthYear !== prevMonthYear) {
            return `${current.getDate()}/${current.getMonth() + 1}/${current.getFullYear()}`;
        }
        return current.getDate().toString();
    };

    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="bg-white border border-gray-300 rounded-[1px] shadow-lg w-[350px]">
                    <p className="bg-gray-100 p-2">{`Ngày: ${label}`}</p>
                    {payload.map((entry, index) => (
                        <div className="flex flex-row ml-2 mt-2 mb-2" key={index}>
                            <div className="basis-2/4 flex flex-row">
                                <div style={{ color: entry.color }}>
                                    <span style={{ marginRight: '10px' }}>●</span>
                                </div>
                                <span>{entry.name}:</span>
                            </div>
                            <span className="basis-2/4">
                                {entry.color === "#F6BD16" || entry.color === "#367FE3" ? formatToVND(entry.value) : entry.value}
                            </span>
                        </div>
                    ))}
                </div>
            );
        }
        return null;
    };

    const maxTongSoLuong = Math.max(...dataChart.map(item => item.tongSoLuong), 100);

    return (
        <div className="overflow-x-hidden p-4">
            <div className="flex space-x-4 mb-4 relative bg-gray-100 p-2 rounded-xl shadow-sm w-max">
                {["Tất cả", "Bán trực tiếp", "Bán online"].map((label, idx) => (
                    <div
                        key={idx}
                        onClick={() => setFilter(idx)}
                        className={`
                            px-5 py-2 rounded-lg cursor-pointer transition-all duration-300
                            ${filter === idx
                                ? "bg-white text-blue-600 shadow font-semibold"
                                : "text-gray-600 hover:bg-white hover:text-blue-500"}
                        `}
                    >
                        {label}
                    </div>
                ))}
            </div>

            <div className="flex justify-between gap-4 mb-3">
                <div className="flex-1 border-l-4 border-l-yellow-400 p-4 bg-white">
                    <div className="flex items-center">
                        <i className="fa-solid fa-sack-dollar text-yellow-400 bg-yellow-100 ml-2 mr-2 p-2"></i>
                        <div>
                            <p className="text-sm">Tổng doanh thu</p>
                            <p className="text-lg font-bold">{formatToVND(data?.tongDoanhThu)}</p>
                        </div>
                    </div>
                </div>

                <div className="flex-1 border-l-4 border-l-green-500 p-4 bg-white">
                    <div className="flex items-center">
                        <i className="fa-solid fa-keyboard text-green-500 bg-green-100 ml-2 mr-2 p-2"></i>
                        <div>
                            <p className="text-sm">Tổng đơn bán ra</p>
                            <p className="text-lg font-bold">{data?.tongDonBan}</p>
                        </div>
                    </div>
                </div>

                <div className="flex-1 border-l-4 border-l-pink-400 p-4 bg-white">
                    <div className="flex items-center">
                        <i className="fa-solid fa-basket-shopping text-pink-400 bg-pink-100 ml-2 mr-2 p-2"></i>
                        <div>
                            <p className="text-sm">Trung bình đơn trên ngày</p>
                            <p className="text-lg font-bold">{tinhThongKeDoanhThu(data)?.soDonTrungBinhTrenNgay?.toFixed(1)}</p>
                        </div>
                    </div>
                </div>

                <div className="flex-1 border-l-4 border-l-blue-400 p-4 bg-white">
                    <div className="flex items-center">
                        <i className="fa-solid fa-sack-dollar text-blue-400 bg-blue-100 ml-2 mr-2 p-2"></i>
                        <div>
                            <p className="text-sm">Tổng số lượng bán ra</p>
                            <p className="text-lg font-bold">{tinhThongKeDoanhThu(data)?.tonSoLuongBan}</p>
                        </div>
                    </div>
                </div>
            </div>

            <div className="flex flex-col gap-4">
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="flex-1">
                        <CustomPieChart data={data?.dulieuPhanLoai || []} dataKey="soLuongBanRa" content="Tổng số lượng sản phẩm được bán ra" title="Số lượng bán ra" />
                    </div>
                    <div className="flex-1">
                        <CustomPieChart data={data?.dulieuPhanLoai || []} dataKey="tongDoanhThu" content="Tổng giá trị hàng hóa bán ra" title="Tổng doanh thu" />
                    </div>
                    <div className="flex-1">
                        <CustomPieChart data={data?.dulieuPhanLoai || []} dataKey="tongDonBan" title="Tổng đơn bán" content="Tổng số lượng đơn bán ra các phân loại" />
                    </div>
                </div>
                <div className="flex-1 p-4">
                    <div className="flex mb-4 space-x-4 text-sm">
                        <div
                            onClick={() => setPhanLoaiChon(0)}
                            className={`flex items-center space-x-2 cursor-pointer px-3 py-2 rounded-md transition-all duration-300 
                                ${phanLoaiChon === 0
                                    ? "bg-blue-100 text-blue-600 font-semibold shadow-sm"
                                    : "text-gray-600 hover:text-blue-500 hover:bg-gray-100"}`}
                        >
                            <i className="fa-solid fa-layer-group"></i>
                            <span>Tất cả</span>
                        </div>

                        <div
                            onClick={() => setPhanLoaiChon(bienThe?.[0]?.id)}
                            className={`flex items-center space-x-2 cursor-pointer px-3 py-2 rounded-md transition-all duration-300 
                                ${phanLoaiChon !== 0
                                    ? "bg-blue-100 text-blue-600 font-semibold shadow-sm"
                                    : "text-gray-600 hover:text-blue-500 hover:bg-gray-100"}`}
                        >
                            <i className="fa-solid fa-tags"></i>
                            <span>Nhóm theo phân loại</span>
                        </div>
                    </div>

                    <div
                        className={`flex mb-3 overflow-x-auto transition-all duration-300 gap-3 ${phanLoaiChon !== 0 ? "max-h-[500px]" : "max-h-0"} `}
                    >
                        {bienThe?.map((d) => (
                            <div
                                key={d.id}
                                onClick={() => setPhanLoaiChon(d.id)}
                                className={`
                                    flex items-center gap-2 px-3 py-2 min-w-[140px] cursor-pointer rounded-lg transition-all duration-300
                                    border ${phanLoaiChon === d.id ? "bg-blue-100 border-blue-300 text-blue-700 shadow-sm" : "border-gray-200 text-gray-700 hover:bg-gray-50 hover:border-gray-300"}
                                `}
                            >
                                <img
                                    src={d.anhBia}
                                    alt={d.ten}
                                    className="w-8 h-8 rounded object-cover"
                                />
                                <p className="text-sm font-medium truncate">{d.ten}</p>
                            </div>
                        ))}
                    </div>

                    <div className="overflow-x-auto">
                        <table className="min-w-full table-auto border-collapse border shadow-md rounded-lg">
                            <thead>
                                <tr className="bg-gray-100 text-left">
                                    <th className="py-2 px-4 border">Thời điểm bán ra</th>
                                    <th className="py-2 px-4 border">Tên biến thể</th>
                                    <th className="py-2 px-4 border">Số lượng</th>
                                    <th className="py-2 px-4 border">Đơn giá bán</th>
                                    <th className="py-2 px-4 border">Giảm giá</th>
                                    <th className="py-2 px-4 border">Tổng SL</th>
                                    <th className="py-2 px-4 border">Tổng tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                {(!paginatedData || paginatedData.length === 0) ? (
                                    <tr>
                                        <td colSpan={7} className="py-4 px-4 text-center text-gray-500">
                                            Không có dữ liệu
                                        </td>
                                    </tr>
                                ) : (
                                    paginatedData.map((item, index) => {
                                        const thoiDiemMua = dinhDangNgay(item.thoiDiemMua);
                                        const tongSoLuong = item.tongSoLuong;
                                        const tongTien = item.tongTien || 0;
                                        const donGiaBanRa = item.donGiaBanRa || [];

                                        const totalRowSpanForLanBan = donGiaBanRa.reduce(
                                            (sum, bienThe) => sum + (bienThe.cartItemLasts?.length || 0),
                                            0
                                        );

                                        let lanBanRowRendered = false;

                                        return donGiaBanRa?.map((bienThe, bienTheIndex) => {
                                            const cartItemLasts = bienThe.cartItemLasts || [];
                                            const rowSpanBienThe = cartItemLasts.length;
                                            let bienTheRowRendered = false;

                                            return cartItemLasts?.map((cartItem, cartIndex) => {
                                                const donGiaBan = cartItem.giaGiam || 0;
                                                const soLuong = cartItem.soLuong || 0;
                                                const phanTramDealGiam = cartItem.phanTramDealGiam || 0;
                                                const phanTramFlashsale = cartItem.phanTramFlashsale || 0;

                                                const giamGia = [];
                                                if (phanTramDealGiam > 0) {
                                                    giamGia.push(
                                                        <span key="deal" className="flex items-center gap-1">
                                                            <img
                                                                className="w-5"
                                                                src="https://cdn-icons-png.flaticon.com/128/4772/4772843.png"
                                                                alt="deal icon"
                                                            />
                                                            {phanTramDealGiam}% (Deal)
                                                        </span>
                                                    );
                                                }
                                                if (phanTramFlashsale > 0) {
                                                    giamGia.push(
                                                        <span key="flashsale" className="flex items-center gap-2">
                                                            <img
                                                                className="w-5"
                                                                src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAilBMVEX////3lZXzfHz39vfu7O33kpL3lJT3kJDydXX3+frzenr1iIj3j4/2oqLzfX3+9vb3mpr36+z4n5/ycnL3z8/97u7u8PH38fL3pqb/+vr83d33rKz96Oj3r6/0hIT3x8f3v7/31db3t7f34OHxz9D3u7z3zMz3w8Pu3t/u5ubv1tfxsLH6zc36vr6UdT+4AAAO0UlEQVR4nN2d62KqvBKGi6wkBFERrVqVKrpa26+r9397mzMBJhCScHDPv7a25nFmkncmCX15eSqjpvknM9M0xx6OZjP3f+r2/0Np7gC8xHZ07MFpMJOLl3py7AEqWhvfszOK8D01I5x64HfHHqqUVR1osrMKrU4/TzjllNYHcGEoQz5dpIoNnvXzbsDRaTBh57CMA41Ni3UJvt0zInabQOjzIXZOrWdDlJgeze6/MqJJjfaZEKncWHPEnoal0WS南北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"
                                                                alt="flashsale icon"
                                                            />
                                                            {phanTramFlashsale}% (Flash)
                                                        </span>
                                                    );
                                                }

                                                return (
                                                    <tr key={`${index}-${bienTheIndex}-${cartIndex}`} className="bg-white hover:bg-blue-50">
                                                        {!lanBanRowRendered && (
                                                            <td className="py-2 px-4 border" rowSpan={totalRowSpanForLanBan}>
                                                                {thoiDiemMua}
                                                            </td>
                                                        )}
                                                        {!bienTheRowRendered && (
                                                            <td className="py-2 px-4 border" rowSpan={rowSpanBienThe}>
                                                                {bienThe.tenBienThe}
                                                            </td>
                                                        )}
                                                        <td className="py-2 px-4 border">{soLuong}</td>
                                                        <td className="py-2 px-4 border">{formatToVND(donGiaBan)}</td>
                                                        <td className="py-2 px-4 border">
                                                            {giamGia.length > 0 ? (
                                                                <div className="flex flex-col gap-1">{giamGia}</div>
                                                            ) : (
                                                                ""
                                                            )}
                                                        </td>
                                                        {!lanBanRowRendered && (
                                                            <td className="py-2 px-4 border" rowSpan={totalRowSpanForLanBan}>
                                                                {tongSoLuong}
                                                            </td>
                                                        )}
                                                        {!lanBanRowRendered && (
                                                            <td className="py-2 px-4 border" rowSpan={totalRowSpanForLanBan}>
                                                                {formatToVND(tongTien)}
                                                            </td>
                                                        )}

                                                        {(() => {
                                                            lanBanRowRendered = true;
                                                            bienTheRowRendered = true;
                                                            return null;
                                                        })()}
                                                    </tr>
                                                );
                                            });
                                        });
                                    })
                                )}
                            </tbody>
                        </table>
                    </div>

                    {data?.data?.length > ITEMS_PER_PAGE && (
                        <Pagination
                            trangHienTai={trang}
                            setTrangHienTai={setTrang}
                            soLuongTrang={tong}
                        />
                    )}
                </div>
                <div className="bg-white p-4 rounded-lg">
                    <p className="text-lg font-bold mb-2">Xu hướng dữ liệu:</p>
                    <p className="mb-4 text-gray-600">Xu hướng dữ liệu mua hàng, giá bán của sản phẩm</p>

                    <div className="w-full relative group">
                        <div className="relative">
                            <ResponsiveContainer width="100%" height={250}>
                                <LineChart
                                    data={dataChart}
                                    margin={{ top: 10, right: 20, left: 10, bottom: 20 }}
                                >
                                    <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" vertical={false} />
                                    <XAxis
                                        dataKey="date"
                                        tickFormatter={formatXAxis}
                                        height={40}
                                        interval={0}
                                        stroke="#666"
                                        tick={{ fontSize: 12 }}
                                    />
                                    <YAxis yAxisId="left" stroke="#666" tick={{ fontSize: 12 }} />
                                    <YAxis
                                        yAxisId="right"
                                        orientation="right"
                                        domain={[0, maxTongSoLuong]}
                                        stroke="#666"
                                        tick={{ fontSize: 12 }}
                                    />
                                    <Tooltip content={<CustomTooltip />} />

                                    <Line
                                        yAxisId="right"
                                        type="monotone"
                                        dataKey="tongSoLuong"
                                        stroke="#FB734B"
                                        strokeWidth={3}
                                        name="Tổng số lượng"
                                        dot={false}
                                        animationDuration={500}
                                    />
                                    <Line
                                        yAxisId="left"
                                        type="monotone"
                                        dataKey="donGiaBan"
                                        stroke="#F6BD16"
                                        strokeWidth={3}
                                        name="Đơn giá bán"
                                        dot={false}
                                        animationDuration={500}
                                    />
                                    <Line
                                        yAxisId="left"
                                        type="monotone"
                                        dataKey="tongTien"
                                        stroke="#367FE3"
                                        strokeWidth={3}
                                        name="Tổng doanh thu"
                                        dot={false}
                                        animationDuration={500}
                                    />
                                </LineChart>
                            </ResponsiveContainer>

                            <div className="flex justify-center mt-2 space-x-6">
                                <div className="flex items-center">
                                    <div className="w-4 h-4 rounded-full bg-[#367FE3] mr-2"></div>
                                    <span className="text-sm text-gray-700">Tổng doanh thu</span>
                                </div>
                                <div className="flex items-center">
                                    <div className="w-4 h-4 rounded-full bg-[#F6BD16] mr-2"></div>
                                    <span className="text-sm text-gray-700">Đơn giá bán</span>
                                </div>
                                <div className="flex items-center">
                                    <div className="w-4 h-4 rounded-full bg-[#FB734B] mr-2"></div>
                                    <span className="text-sm text-gray-700">Tổng số lượng bán ra</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <style jsx>{`
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }
                .animate-fade-in {
                    animation: fadeIn 0.5s ease-in-out;
                }
                .chart-container {
                    padding: 10px;
                    margin-bottom: 20px;
                    position: relative;
                    height: 250px;
                }
                .chart-container:hover button {
                    opacity: 0.7;
                }
                button:hover {
                    transform: scale(1.1);
                    transition: transform 0.2s ease-in-out;
                }
            `}</style>
            <XuHuongTruyCap setcb={setcb} batDau={batDau} ketThuc={ketThuc} id={id}></XuHuongTruyCap>
        </div>
    );
}

export { ChiTietDoanhThu };
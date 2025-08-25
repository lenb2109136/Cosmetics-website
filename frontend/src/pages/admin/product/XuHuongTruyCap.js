import { useEffect, useState } from "react";
import { getLuLuongTruyCap } from "../../../services/sanPhamService";
import { formatDateToString } from "../../../utils/Format";
import { toast } from "react-toastify";
import { PieChart, Pie, Cell, Tooltip, Legend, LineChart, CartesianGrid, XAxis, YAxis, Line, ResponsiveContainer, Area, AreaChart } from "recharts";
import { Pagination } from "../../../components/commons/Pagination";

const COLORS = [
    "#1E90FF",
    "#FFA500",
];

function CustomTooltip({ active, payload }) {
    if (active && payload && payload.length) {
        return (
            <div className="bg-white p-3 border rounded shadow-sm">
                {payload.map((entry, index) => (
                    <div
                        key={`tooltip-${index}`}
                        className="flex items-center space-x-2 p-2 bg-gray-50 rounded-md shadow-sm"
                    >
                        <span
                            className="w-4 h-4 inline-block"
                            style={{ backgroundColor: entry.fill }}
                        ></span>
                        <span className="text-sm font-medium">{entry.payload.ten}: {entry.value}</span>
                    </div>
                ))}
            </div>
        );
    }
    return null;
}

function CustomPieChart({ data, dataKey, title }) {
    const renderCustomLegend = ({ payload }) => {
        return (
            <div className="flex flex-wrap justify-center gap-4 mt-2">
                {payload.map((entry, index) => (
                    <div
                        key={`legend-${index}`}
                        className="flex items-center space-x-2 p-2 bg-gray-50 rounded-md shadow-sm"
                    >
                        <span
                            className="w-4 h-4 inline-block"
                            style={{ backgroundColor: COLORS[index % COLORS.length] }}
                        ></span>
                        <span className="text-sm font-medium">{entry.payload.ten}</span>
                    </div>
                ))}
            </div>
        );
    };

    return (
        <div className="mb-6 animate-fade-in shadow-lg rounded-lg p-4 bg-white">
            <h3 className="text-center font-semibold mb-2 text-lg" style={{ marginLeft: "auto", marginRight: "auto", width: "fit-content" }}>{title}</h3>
            <PieChart width={200} height={250}>
                <defs>
                    {data.map((_, index) => (
                        <radialGradient
                            id={`gradient-${index}`}
                            key={`gradient-${index}`}
                            cx="50%"
                            cy="50%"
                            r="50%"
                        >
                            <stop offset="0%" stopColor={COLORS[index % COLORS.length]} />
                            <stop
                                offset="100%"
                                stopColor={`${COLORS[index % COLORS.length]}80`}
                            />
                        </radialGradient>
                    ))}
                </defs>
                <Pie
                    data={data}
                    dataKey={dataKey}
                    nameKey="ten"
                    cx="50%"
                    cy="50%"
                    outerRadius={70}
                    innerRadius={40}
                    label
                >
                    {data.map((entry, index) => (
                        <Cell
                            key={`cell-${index}`}
                            fill={`url(#gradient-${index})`}
                        />
                    ))}
                </Pie>
                <Tooltip content={<CustomTooltip />} />
                <Legend content={renderCustomLegend} />
            </PieChart>
        </div>
    );
}

function XuHuongTruyCap({ batDau, ketThuc, id }) {
    const [data, setData] = useState({});
    const [timeDurate, setTimeDurate] = useState(10);
    const [currentPage, setCurrentPage] = useState(0);
    const [tong, setTong] = useState(0);
    const [trang, setrang] = useState(0);
    const [filter, setFilter] = useState(0);

    useEffect(() => {
        getLuLuongTruyCap(id, formatDateToString(batDau), formatDateToString(ketThuc), timeDurate, filter)
            .then(data => {
                setData(data);
                setTong(Math.ceil(data?.nguoiDungTruyCap?.length / 5));
                setrang(0);
            })
            .catch(() => {
                toast.error("Lấy dữ liệu thất bại");
            });
    }, [id, batDau, ketThuc, timeDurate, filter]);

    const accessData = [
        { ten: "Lượt truy cập cũ", value: data.tongLuongTruyCapCu || 0 },
        { ten: "Lượt truy cập mới", value: data.tonLuongTruyCapMoi || 0 },
    ];

    const conversionData = [
        { ten: "Chuyển đổi thành công", value: data.chuyenDoiThanhCong || 0 },
        { ten: "Chuyển đổi thất bại", value: data.chuyeDoiThatBai || 0 },
    ];

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
        const accessCount = (data?.dataTruyCap || []).filter(item => {
            if (!item.ngayGioTruyCap) return false;
            const itemDate = new Date(item.ngayGioTruyCap).toISOString().split('T')[0];
            return itemDate === date;
        }).length;

        return {
            date,
            luotTruyCap: accessCount,
        };
    });

    const maxLuotTruyCap = Math.max(...dataChart.map(item => item.luotTruyCap), 10);

    return (
        <>
            <div>
                <div className="flex flex-row mt-2 justify-around">
                    
                    <div>
                      
                    </div>
                </div>
                <div className="">
                   
                    <div className=" bg-gray-50 ">
                        <div className="bg-white p-4 rounded-lg shadow-md">
                            <h3 className="text-xl font-bold text-black mb-2">Lưu lượng truy cập</h3>
                            <p className="text-sm text-gray-600 mb-4">Lưu lượng truy cập sản phẩm (được tính bằng số lượt vào xem sản phẩm)</p>
                            <div className="chart-container relative group">
                                <ResponsiveContainer width="100%" height={250}>
                                    <AreaChart
                                        data={dataChart}
                                        margin={{ top: 10, right: 20, left: 10, bottom: 0 }}
                                    >
                                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                        <XAxis dataKey="date" hide />
                                        <YAxis hide />
                                        <Tooltip />
                                        <Area
                                            type="monotone"
                                            dataKey="luotTruyCap"
                                            stroke="#1E90FF"
                                            fill="#E6F0FA"
                                            fillOpacity={0.5}
                                            strokeWidth={2}
                                            dot={false}
                                        />
                                    </AreaChart>
                                </ResponsiveContainer>
                            </div>
                        </div>
                        
                        {/* {tong > 1 ? <Pagination
                            trangHienTai={trang}
                            setTrangHienTai={setrang}
                            soLuongTrang={tong}
                        /> : null} */}
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
                .shadow-lg {
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.08);
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
        </>
    );
}

export { XuHuongTruyCap };
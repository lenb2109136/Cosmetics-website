import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getByStatus, setActive } from "../../../services/FlashSaleService";
import { dinhDangNgay } from "../../../utils/Format";
import { toast } from "react-toastify";
import { getByStatusBonus, setActiveBonus } from "../../../services/BonusService";

const toISOStringWithoutMillis = (date) => {
  return date.toISOString().split(".")[0];
};

function HomeBonus() {
  const [activeTab, setActiveTab] = useState("Tất cả");
  const tabs = ["Tất cả", "Đang diễn ra", "Sắp diễn ra", "Đã kết thúc"];
  const statusMap = {
    "Tất cả": 0,
    "Đang diễn ra": 1,
    "Sắp diễn ra": 2,
    "Đã kết thúc": 3,
  };
  const [trang, setTrang] = useState(0);
  const [status, setStatus] = useState(statusMap[activeTab]);
  const [load, setLoad] = useState(false);
  const [bd, setBd] = useState(() => {
    const now = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(now.getMonth() - 1);
    return toISOStringWithoutMillis(oneMonthAgo);
  });
  const [kt, setKt] = useState(() => {
    const now = new Date();
    return toISOStringWithoutMillis(now);
  });
  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();

  useEffect(() => {
    console.log("Calling getByStatus with:", { trang, bd, kt, status });
    getByStatusBonus(trang, bd, kt, status)
      .then((response) => {
          setData(response.content);
          setTotalPages(response.totalPages || 1);
      })
      .catch((error) => {
        toast.error("Không thể tải dữ liệu Flash Sale");
      });
  }, [bd, kt, trang, status, load]);

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setStatus(statusMap[tab]);
    setTrang(0);
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setTrang(newPage);
    }
  };
  function isPastTime(timeString) {
    const targetTime = new Date(timeString);
    const now = new Date();
    return now > targetTime;
}


  return (
    <div className="p-4 space-y-6">
      <div className="bg-white shadow-md rounded-xl p-4">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">Tổng quan khuyến mãi tặng kèm của Shop</h2>
        </div>
        <div className="grid grid-cols-4 gap-4 text-center">
          {[
            { label: "Doanh Số", value: "₫ 0" },
            { label: "Đơn hàng", value: "0" },
            { label: "Người mua", value: "0" },
            { label: "Tỷ lệ truy cập", value: "0.00 %" },
          ].map((item, index) => (
            <div key={index}>
              <div className="text-gray-500 text-sm">{item.label}</div>
              <div className="text-lg font-semibold">{item.value}</div>
              <div className="text-xs text-gray-400">so với 7 ngày trước 0.00%</div>
            </div>
          ))}
        </div>
      </div>

      <div className="bg-white shadow-md rounded-xl p-4">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">Danh sách chương trình</h2>
          <button
            onClick={() => navigate("add")}
            className="bg-gradient-to-r from-blue-500 to-blue-700 text-white px-4 py-1 rounded hover:opacity-90 transition-all duration-300"
          >
            + Tạo
          </button>
        </div>
        <div className="flex space-x-4 border-b pb-2 mb-4">
          {tabs.map((tab, idx) => (
            <button
              key={idx}
              onClick={() => handleTabChange(tab)}
              className={`text-sm pb-1 transition-all duration-300 ${
                activeTab === tab
                  ? "text-blue-600 border-b-2 border-blue-600 font-medium"
                  : "text-gray-600 hover:text-blue-500"
              }`}
            >
              {tab}
            </button>
          ))}
        </div>

        <div className="mb-4 flex flex-row">
          <div className="flex flex-col">
            <strong>
              <p>Thời gian bắt đầu:</p>
            </strong>
            <input
              value={bd}
              onChange={(e) => setBd(e.target.value)}
              type="datetime-local"
              className="border px-3 py-1 rounded w-full outline-none border-gray-200 cursor-pointer mt-2"
            />
          </div>
          <div className="ml-3">
            <strong>
              <p>Thời gian kết thúc:</p>
            </strong>
            <input
              value={kt}
              onChange={(e) => setKt(e.target.value)}
              type="datetime-local"
              className="border px-3 py-1 rounded w-full outline-none border-gray-200 cursor-pointer mt-2"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left border">
            <thead className="bg-gray-100 text-gray-700">
              <tr>
                <th className="p-2 border">Thời gian áp dụng</th>
                <th className="p-2 border">Số lượng sản phẩm chính</th>
                <th className="p-2 border">Số lượng sản phẩm tặng kèm</th>
                <th className="p-2 border">Tổng lượt sử dụng</th>
                <th className="p-2 border">Trạng thái</th>
                <th className="p-2 border">Hoạt động</th>
                <th className="p-2 border">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {data?.length > 0 ? (
                data.map((d) => (
                  <tr >
                    <td  className={`p-2 border text-center ${isPastTime(d.thoiGianNgung)?"pointer-events-none bg-gray-200":null}`}>
                      {dinhDangNgay(d.thoiGianApDung)} - {dinhDangNgay(d.thoiGianNgung)}
                    </td>
                    <td className={`p-2 border text-center ${isPastTime(d.thoiGianNgung)?"pointer-events-none bg-gray-200":null}`}>
                      {d.sanPhamChinh}
                    </td>
                    <td className={`p-2 border text-center ${isPastTime(d.thoiGianNgung)?"pointer-events-none bg-gray-200":null}`}>{d.sanPhamPhu}</td>
                    <td className={`p-2 border text-center ${isPastTime(d.thoiGianNgung)?"pointer-events-none bg-gray-200":null}`}>
                        
                        {d.tongLuotToiDa} (Đã dùng  {d.soLuongDaDung} )</td>
                    <td className={`p-2 border text-center  ${isPastTime(d.thoiGianNgung)?"pointer-events-none bg-gray-200":null}`}>
                      <span className="bg-gray-200 text-gray-700 text-xs px-2 py-1 rounded">
                        {activeTab}
                      </span>
                    </td>
                    <td className={`p-2 border ${isPastTime(d.thoiGianNgung)?"pointer-events-none bg-gray-200":null}`}>
                      <button
                        onClick={() => {
                          setActiveBonus(d.id, !d.conSuDung)
                            .then(() => {
                              if (!d.conSuDung) {
                                toast.success("Kích hoạt khuyến mãi thành công");
                              } else {
                                toast.success("Vô hiệu khuyến mãi thành công");
                              }
                              d.conSuDung = !d.conSuDung;
                              setLoad(!load);
                            })
                            .catch((r) => {
                              toast.error(r?.response.data.message||"Kích hoạt thất bại");
                            });
                        }}
                        className={`${
                          d.conSuDung ? "bg-green-500" : "bg-gray-300"
                        } text-white p-1 rounded-sm`}
                      >
                        <strong>Hoạt động</strong>
                      </button>
                    </td>
                    <td className="p-2 border text-blue-500 space-y-1">
                      <div>
                        <a
                          className="cursor-pointer"
                          onClick={() => navigate(`update?id=${d.id}`)}
                        >
                          Chi tiết
                        </a>
                      </div>
                      <div>
                        <a href="#">Xem tổng hợp</a>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="7" className="p-2 text-center text-gray-500">
                    Không có dữ liệu
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="flex justify-between items-center mt-4">
          <div className="text-sm text-gray-600">
            Trang {trang + 1} / {totalPages}
          </div>
          <div className="flex space-x-2">
            
            <button
              onClick={() => handlePageChange(trang - 1)}
              disabled={trang === 0}
              className={`px-3 py-1 rounded ${
                trang === 0
                  ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                  : "bg-blue-500 text-white hover:bg-blue-600"
              }`}
            >
              Trước
            </button>
            {Array.from({ length: totalPages }, (_, index) => {
              const pageCount = 5;
              const startPage = Math.max(0, trang - Math.floor(pageCount / 2));
              const endPage = Math.min(totalPages, startPage + pageCount);
              if (index >= startPage && index < endPage) {
                return (
                  <button
                    key={index}
                    onClick={() => handlePageChange(index)}
                    className={`px-3 py-1 rounded ${
                      trang === index
                        ? "bg-blue-600 text-white"
                        : "bg-gray-200 text-gray-700 hover:bg-blue-500 hover:text-white"
                    }`}
                  >
                    {index + 1}
                  </button>
                );
              }
              return null;
            })}
            <button
              onClick={() => handlePageChange(trang + 1)}
              disabled={trang === totalPages - 1}
              className={`px-3 py-1 rounded ${
                trang === totalPages - 1
                  ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                  : "bg-blue-500 text-white hover:bg-blue-600"
              }`}
            >
              Sau
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export { HomeBonus };
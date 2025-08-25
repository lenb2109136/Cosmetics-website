import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { QuanLyHoaDonTaiQuay } from "./QuanLyHoaDonTaiQuay";
import { QuanLyHoaDonOnline } from "./QuanLyHoaDonOnline";
import BackGroundTopLeft from "../../assets/background.png";

function QuanLyHoaDon() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [activeTab, setActiveTab] = useState("taiQuay");

  useEffect(() => {
    const type = searchParams.get("type");
    if (type === "online" || type === "taiQuay") {
      setActiveTab(type);
    } else {
      setActiveTab("taiQuay");
      setSearchParams({ type: "taiQuay" });
    }
  }, []);

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setSearchParams((prev) => {
      const params = new URLSearchParams(prev);
      params.set("type", tab);
      return params;
    });
  };

  return (
    <div className="w-full relative z-0">
      <img
        src={BackGroundTopLeft}
        alt="bg"
        className="absolute top-0 right-0 w-24 z-30"
      />
      <div className="p-4">
        <h1 className="text-xl font-bold text-green-900 mb-4 border shadow-md rounded-md p-2 w-fit"> <i class="fa-solid fa-cart-shopping ml-1"></i> Quản lý đơn</h1>
        <div className="flex border-b border-green-900">
          <button
            className={`px-4 py-2 font-medium text-sm transition-colors duration-200 ${
              activeTab === "taiQuay"
                ? "border-b-2 border-green-900 text-green-900"
                : "text-gray-500 hover:text-green-900"
            }`}
            onClick={() => handleTabChange("taiQuay")}
          >
             <div className="mb-1">
              <i class="fa-solid fa-globe mr-1 text-green-900  border shadow-md rounded-md p-2"></i>
              <span >Hóa Đơn Tại Quầy:</span>
            </div>
          </button>
          <button
            className={`px-4 py-2 font-medium text-sm transition-colors duration-200 ${
              activeTab === "online"
                ? "border-b-2 border-green-900 text-green-900"
                : "text-gray-500 hover:text-green-900"
            }`}
            onClick={() => handleTabChange("online")}
          >

            <div className="mb-1">
                        <i class="fa-solid fa-blog mr-1 text-green-900  border shadow-md rounded-md p-2"></i>
                        <span >Hóa Đơn Online:</span>
                    </div>
            
          </button>
        </div>
        <div className="mt-4">
          {activeTab === "taiQuay" ? (
            <QuanLyHoaDonTaiQuay />
          ) : (
            <QuanLyHoaDonOnline />
          )}
        </div>
      </div>
    </div>
  );
}

export { QuanLyHoaDon };

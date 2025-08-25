import { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";

function GeneralManager() {
    const [tabPick, setTabPick] = useState(1);
    const navigate = useNavigate();

    const handleTabClick = (tabIndex, path) => {
        setTabPick(tabIndex);
        if (path) navigate(path);
    };

    return (
        <div>
            <p className="text-left mt-4 mb-3 bg-white p-3 rounded-md">
                <i className="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
                <strong className="text-lg">Kênh quản lý tổng hợp</strong>
                <p className="mt-1">Quản lý các thành phần cơ bản tại cửa hàng</p>
            </p>
            <div className="flex justify-start mb-6">
                <div className="w-full bg-white p-4">
                    <div className="flex space-x-4 overflow-x-auto whitespace-nowrap">
                        <button
                            onClick={() => handleTabClick(1, "category")}
                            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg transition-transform ${
                                tabPick === 1 ? "transform -translate-y-1 bg-white shadow-lg z-10" : "bg-gray-100"
                            }`}
                            style={{ minWidth: "fit-content" }}
                            role="tab"
                            aria-selected={tabPick === 1}
                        >
                            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-white border shadow-sm rounded-md">
                                <i className="fa-solid fa-list text-green-900"></i>
                            </span>
                            Danh mục
                        </button>
                        <button
                            onClick={() => handleTabClick(2, "parameter")}
                            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg transition-transform ${
                                tabPick === 2 ? "transform -translate-y-1 bg-white shadow-lg z-10" : "bg-gray-100"
                            }`}
                            style={{ minWidth: "fit-content" }}
                            role="tab"
                            aria-selected={tabPick === 2}
                        >
                            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-white border shadow-sm rounded-md">
                                <i className="fa-solid fa-layer-group text-green-900"></i>
                            </span>
                            Thông số
                        </button>
                        <button
                            onClick={() => handleTabClick(3)}
                            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg transition-transform ${
                                tabPick === 3 ? "transform -translate-y-1 bg-white shadow-lg z-10" : "bg-gray-100"
                            }`}
                            style={{ minWidth: "fit-content" }}
                            role="tab"
                            aria-selected={tabPick === 3}
                        >
                            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-white border shadow-sm rounded-md">
                                <i className="fa-solid fa-users-line text-green-900"></i>
                            </span>
                            Nhân viên
                        </button>
                        <button
                            onClick={() => handleTabClick(4, "brand")}
                            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg transition-transform ${
                                tabPick === 4 ? "transform -translate-y-1 bg-white shadow-lg z-10" : "bg-gray-100"
                            }`}
                            style={{ minWidth: "fit-content" }}
                            role="tab"
                            aria-selected={tabPick === 4}
                        >
                            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-white border shadow-sm rounded-md">
                                <i className="fa-solid fa-registered text-green-900"></i>
                            </span>
                            Thương hiệu
                        </button>
                        <button
                            onClick={() => handleTabClick(5, "quycachdonggoi")}
                            className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg transition-transform ${
                                tabPick === 5 ? "transform -translate-y-1 bg-white shadow-lg z-10" : "bg-gray-100"
                            }`}
                            style={{ minWidth: "fit-content" }}
                            role="tab"
                            aria-selected={tabPick === 5}
                        >
                            <span className="flex items-center justify-center w-6 h-6 mr-2 bg-white border shadow-sm rounded-md">
                                <i className="fa-solid fa-right-to-bracket text-green-900"></i>
                            </span>
                            Quy cách nhập hàng
                        </button>
                    </div>
                </div>
            </div>
            <Outlet />
        </div>
    );
}

export { GeneralManager };
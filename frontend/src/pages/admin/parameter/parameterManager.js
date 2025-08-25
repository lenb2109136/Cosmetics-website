import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { deleteDanhMuc, getDanhMucOfThongSo, getManagerThongSo, getThongSoCuTheOfThongSo } from "../../../services/ThongSoService";
import { toast } from "react-toastify";
import { removeThongSoCuThe } from "../../../services/ThongSoCuTheService";
import gsap from "gsap";
import Modal from "../../../components/commons/modal";
import { AddParameter } from "./AddParameter";

function ThongSo() {
    const navigate = useNavigate();
    const [duLieu, setDuLieu] = useState([]);
    const [tongTrang, setTongTrang] = useState(0);
    const [tongSoPhanTu, setTongSoPhanTu] = useState(0);
    const [tenThongSo, setTenThongSo] = useState("");
    const [trangHienTai, setTrangHienTai] = useState(0);
    const [selectedThongSo, setSelectedThongSo] = useState(null);
    const [danhsachDanhMuc, setDanhSachDanhMuc] = useState([])
    const [danhSachThongSo, setDanhSachThongSo] = useState([])
    const [pick, setPick] = useState(0)
    const [open, setOpen] = useState(false)
    const containerRef = useRef(null);
    const fetchData = () => {
        getManagerThongSo(trangHienTai, tenThongSo)
            .then((data) => {
                setDuLieu(data.data.data.content);
                setTongTrang(data.data.data.totalPages);
                setTongSoPhanTu(data.data.data.totalElements);
            })
            .catch((e) => {
                toast.error(e.response?.data || "Lỗi khi tải dữ liệu");
            });
    };
    useEffect(() => {
        const ctx = gsap.context(() => {
            gsap.from(containerRef.current, {
                y: 40,
                opacity: 0,
                duration: 0.8,
                scale: 1.2,
                ease: "power2.out",
            });
        });

        return () => { ctx.revert() };
    }, []);
    useEffect(() => {
        if (selectedThongSo) {
            getDanhMucOfThongSo(selectedThongSo?.id).then((data) => {
                setDanhSachDanhMuc(data)
            }).catch((err) => {
                toast.error(err.response.data.message)
            })
            getThongSoCuTheOfThongSo(selectedThongSo?.id).then((data) => {
                setDanhSachThongSo(data)
            }).catch((err) => {
                toast.error(err.response.data.message)
            })
        }
    }, [selectedThongSo])
    useEffect(() => {
        const timeout = setTimeout(() => {
            setTrangHienTai(0);
            fetchData();
        }, 300);
        return () => clearTimeout(timeout);
    }, [tenThongSo]);

    useEffect(() => {
        fetchData();
    }, [trangHienTai]);

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < tongTrang) {
            setTrangHienTai(newPage);
        }
    };

    const handleViewDetails = (thongSo) => {
        setSelectedThongSo(thongSo);
    };

    const handleCloseDetails = () => {
        setSelectedThongSo(null);
    };

    const renderPagination = () => {
        const pages = [];
        if (tongTrang <= 5) {
            for (let i = 0; i < tongTrang; i++) {
                pages.push(i);
            }
        } else {
            pages.push(0);

            if (trangHienTai > 2) {
                pages.push("...");
            }

            const start = Math.max(1, trangHienTai - 1);
            const end = Math.min(tongTrang - 2, trangHienTai + 1);

            for (let i = start; i <= end; i++) {
                pages.push(i);
            }

            if (trangHienTai < tongTrang - 3) {
                pages.push("...");
            }

            pages.push(tongTrang - 1);
        }

        return pages.map((page, idx) => {
            if (page === "...") {
                return (
                    <span key={`dots-${idx}`} className="px-2 py-1 text-gray-500">
                        ...
                    </span>
                );
            }

            return (
                <button
                    key={page}
                    onClick={() => handlePageChange(page)}
                    className={`px-3 py-1 border rounded hover:bg-gray-100 ${trangHienTai === page ? "bg-blue-600 text-white" : ""}`}
                >
                    {page + 1}
                </button>
            );
        });
    };

    return (
        <>
            <div ref={containerRef} className="flex h-screen overflow-hidden mt-2 bg-white  shadow-lg h-fit">
                {/* Left panel (2/5) */}
                <div
                    className={`p-3  transition-all duration-300 ease-in-out ${selectedThongSo ? "w-3/5" : "w-full"
                        } overflow-y-auto`}
                >
                    <div className="flex justify-between items-center my-4">
                        <div className="flex items-center space-x-3">
                            <span className="flex items-center justify-center w-10 h-10  text-green-900 border shadow-md rounded-md">
                                <i className="fas fa-search text-gray-700"></i>
                            </span>
                            <input
                                id="it"
                                type="text"
                                placeholder="Nhập tên thông số..."
                                className="w-full outline-none px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg shadow-md focus:outline-none  transition duration-300"
                                value={tenThongSo}
                                onChange={(e) => setTenThongSo(e.target.value)}
                                aria-label="Nhập tên thông số để tìm kiếm"
                            />
                        </div>
                        <button
                            onClick={() => {
                                setOpen(true);
                                setPick(null);
                            }}
                            className="flex items-center px-4 py-2 text-sm font-medium   rounded-lg shadow-md  transition-colors text-green-900 border shadow-md rounded-md"
                            aria-label="Thêm thông số mới"
                        >
                            <span className="flex items-center justify-center w-6 h-6 mr-2  rounded-full">
                                <i className="fas fa-plus text-green-900  rounded-md"></i>
                            </span>
                            Thêm thông số
                        </button>
                    </div>

                    <div className="flex items-center mb-4 bg-gray-50 p-3 rounded-lg shadow-sm">
                        <span className="flex items-center justify-center w-8 h-8 mr-3  bg-green-900 border shadow-md rounded-md">
                            <i className="fas fa-list-ul text-white"></i>
                        </span>
                        <p className="text-gray-700">
                            Tổng thông số: <span className="font-semibold text-green-700">{tongSoPhanTu}</span> | Trang hiện tại:{" "}
                            <span className="font-semibold text-green-700">{trangHienTai + 1}</span> / <span className="font-semibold text-green-700">{tongTrang}</span>
                        </p>
                    </div>
                    <div className="overflow-x-auto text-md">
                        <table className="w-full mt-4 border border-gray-200 rounded-lg overflow-hidden shadow-sm">
                            <thead className="bg- bg-gray-100 text-left">
                                <tr>
                                    <th className="py-3 px-4">Tên thông số</th>
                                    <th className="py-3 px-4 text-center">Tổng danh mục sử dụng</th>
                                    <th className="py-3 px-4 text-center">Tổng thông số cụ thể</th>
                                    <th className="py-3 px-4 text-center">Tổng sản phẩm sử dụng</th>
                                    <th className="py-3 px-4 text-center">Chi tiết</th> {/* Cột mới */}
                                </tr>
                            </thead>
                            <tbody>
                                {duLieu?.map((data) => (
                                    <tr key={data.id} className="border-t hover:bg-gray-50">
                                        <td className="py-3 px-4">{data.ten}</td>
                                        <td className="py-3 px-4 text-center">{data.tongSoDanhMuc}</td>
                                        <td className="py-3 px-4 text-center">{data.tongSoThongSoCuThe}</td>
                                        <td className="py-3 px-4 text-center">{data.tongSoSanPhamApDung}</td>
                                        <td className="py-3 px-4 text-center">
                                            <div className="relative group cursor-pointer inline-block">
                                                <div onClick={() => {
                                                    setOpen(true)
                                                    setPick(data.id)
                                                }} className="transition-all duration-300  text-green-900 pl-2 pr-2 rounded-md border shadow-md">
                                                    Chi tiết
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                    </div>

                    
                </div>


            </div>
            {open ? <Modal setOpen={setOpen} b={true}>
                <AddParameter id={pick}></AddParameter>
            </Modal> : null
            }
            <>
            </>
        </>
    );
}

export { ThongSo };
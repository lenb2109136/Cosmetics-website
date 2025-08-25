import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { deleteDanhMuc, getDanhMucOfThongSo, getManagerThongSo, getThongSoCuTheOfThongSo } from "../../../services/ThongSoService";
import { toast } from "react-toastify";
import { removeThongSoCuThe } from "../../../services/ThongSoCuTheService";
import { getManagerDanhMuc, getSanPhamOfDanhMuc, getThongSoCuTheOfDanhMuc } from "../../../services/CategoryService";
import gsap from "gsap";
import { Pagination } from "../../../components/commons/Pagination";

function DanhMucManger() {
  const navigate = useNavigate();
  const [duLieu, setDuLieu] = useState([]);
  const [tongTrang, setTongTrang] = useState(0);
  const [tongSoPhanTu, setTongSoPhanTu] = useState(0);
  const [tenThongSo, setTenThongSo] = useState("");
  const [trangHienTai, setTrangHienTai] = useState(0);
  const [selectedThongSo, setSelectedThongSo] = useState(null);
  const [danhsachDanhMuc, setDanhSachDanhMuc] = useState([])
  const [danhSachThongSo, setDanhSachThongSo] = useState([])
  const containerRef = useRef(null);
  const fetchData = () => {
    getManagerDanhMuc(trangHienTai, tenThongSo)
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
        x: 40,
        opacity: 0,
        duration: 0.8,
        ease: "power2.out",
      });
    });

    return () => { ctx.revert() };
  }, []);
  useEffect(() => {
    if (selectedThongSo) {
      getSanPhamOfDanhMuc(selectedThongSo?.id).then((data) => {
        setDanhSachDanhMuc(data)
      }).catch((err) => {
        toast.error(err.response.data.message)
      })
      getThongSoCuTheOfDanhMuc(selectedThongSo?.id).then((data) => {
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
    setSelectedThongSo(null); // Close the detail panel
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
    <div ref={containerRef} className="flex h-screen overflow-hidden ">
      {/* Left panel (2/5) */}
      <div
        className={` transition-all duration-300 p-3 bg-white ease-in-out ${selectedThongSo ? "w-3/5" : "w-full"
          } overflow-y-auto`}
      >
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center basis-1/2 space-x-3">
            <span className="flex items-center justify-center w-9 h-9 bg-white rounded-md shadow-md border ">
              <i className="fas fa-search text-green-900"></i>
            </span>
            <input
              type="text"
              placeholder="Nhập tên danh mục..."
              className="w-full px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg  focus:outline-none  transition duration-300"
              value={tenThongSo}
              onChange={(e) => setTenThongSo(e.target.value)}
              aria-label="Nhập tên danh mục để tìm kiếm"
            />
          </div>
          <button
            onClick={() => {
              navigate('/admin/general/addcategory');
            }}
            className="flex items-center px-4 py-2 text-sm font-medium  rounded-md border shadow-md  transition-colors mb-2"
            aria-label="Thêm danh mục mới"
          >

            <label htmlFor="tenNhaCungCap" className="block  text-sm font-medium text-gray-700 flex items-center ">
              <i className="fas fa-plus mr-2 text-green-900 p-2 rounded-md  shadow-md border"></i> Thêm danh mục
            </label>

          </button>
        </div>

       
        <div className="flex items-center space-x-2 justify-between">
             <p className="text-sm text-gray-600 w-fit rounded-md border shadow-md p-2">
                    <i className="fas fa-list mr-1 text-green-900"></i> Tổng số danh mục: <strong>{tongSoPhanTu}</strong>
                </p>
          <Pagination color="bg-green-900" setTrangHienTai={setTrangHienTai} trangHienTai={trangHienTai} soLuongTrang={tongTrang}></Pagination>
        </div>

        <div className="overflow-x-auto text-md">
          <table className="w-full mt-4 border border-gray-200 rounded-lg overflow-hidden shadow-sm">
            <thead className="bg-gray-50 text-left">
              <tr>
                <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Tên Danh mục</th>
                <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Tổng số danh mục con</th>
                <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Tổng thông số sử dụng</th>
                <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Tổng sản phẩm thuộc danh mục</th>
                <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Chi tiết</th> {/* Cột mới */}
              </tr>
            </thead>
            <tbody>
              {duLieu?.map((data) => (
                <tr key={data.id} className="border-t hover:bg-gray-50">
                  <td className="py-3 px-4">{data.ten}</td>
                  <td className="py-3 px-4 text-center">{data.tongDanhMucCon}</td>
                  <td className="py-3 px-4 text-center">{data.tongThongSo}</td>
                  <td className="py-3 px-4 text-center">{data.tongSanPham}</td>
                  <td className="py-3 px-4 text-center">
                    <div
                      className="relative group cursor-pointer inline-block"
                      onClick={() => {
                        navigate(`/admin/general/update/${data.id}`);
                      }}
                    >
                      <div className="text-green-900 border shadow-md p-1 rounded-md text-sm font-medium transition-colors pl-2 pr-2">
                        Xem chi tiết
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
  );
}

export { DanhMucManger };
import { useState, useEffect, useRef } from "react";
import { getTinh, getHuyen, getXa } from "../../../services/address";
import { toast } from "react-toastify";
import { getUpdate, saveSupplier } from "../../../services/SupplierService";
import gsap from "gsap";
import { useParams } from "react-router-dom";

function AddSupplier() {
  const { id } = useParams();

  const [nhaCungCap, setNhaCungCap] = useState({
    code: 0,
    name: "",
    diaChi: "",
    soDienThoai: "",
    maSoThue: "",
    soTaiKhoan: "",
    tinh: { code: 0, name: "" },
    huyen: { code: 0, name: "" },
    xa: { code: 0, name: "" },
  });
  const [listTinh, setListTinh] = useState([]);
  const [listHuyen, setListHuyen] = useState([]);
  const [listXa, setListXa] = useState([]);
  const [isLoadingTinh, setIsLoadingTinh] = useState(false);
  const [isLoadingHuyen, setIsLoadingHuyen] = useState(false);
  const [isLoadingXa, setIsLoadingXa] = useState(false);

  const formRef = useRef(null);

  // Tải danh sách tỉnh
  useEffect(() => {
    setIsLoadingTinh(true);
    getTinh()
      .then((data) => {
        console.log("List Tinh:", data); // Debug dữ liệu tỉnh
        setListTinh(data);
        setIsLoadingTinh(false);
      })
      .catch(() => {
        toast.error("Lấy dữ liệu tỉnh thất bại");
        setIsLoadingTinh(false);
      });
  }, []);

  // Tải dữ liệu nhà cung cấp
  useEffect(() => {
    if (id && id !== "0") {
      getUpdate(id)
        .then((data) => {
          console.log("NhaCungCap Data:", data); // Debug dữ liệu nhà cung cấp
          setNhaCungCap(data);
        })
        .catch(() => toast.error("Lấy dữ liệu nhà cung cấp thất bại"));
    }
  }, [id]);

  // Tải danh sách huyện khi tỉnh thay đổi
  useEffect(() => {
    if (nhaCungCap.tinh.code) {
      setIsLoadingHuyen(true);
      getHuyen(nhaCungCap.tinh.code)
        .then((data) => {
          console.log("List Huyen:", data); // Debug dữ liệu huyện
          setListHuyen(data);
          setIsLoadingHuyen(false);
        })
        .catch(() => {
          toast.error("Lấy dữ liệu huyện thất bại");
          setIsLoadingHuyen(false);
        });
    } else {
      setListHuyen([]);
      setIsLoadingHuyen(false);
    }
  }, [nhaCungCap.tinh.code]);

  // Tải danh sách xã khi huyện thay đổi
  useEffect(() => {
    if (nhaCungCap.huyen.code) {
      setIsLoadingXa(true);
      getXa(nhaCungCap.huyen.code)
        .then((data) => {
          console.log("List Xa:", data); // Debug dữ liệu xã
          setListXa(data);
          setIsLoadingXa(false);
        })
        .catch(() => {
          toast.error("Lấy dữ liệu xã thất bại");
          setIsLoadingXa(false);
        });
    } else {
      setListXa([]);
      setIsLoadingXa(false);
    }
  }, [nhaCungCap.huyen.code]);

  // Animation
  useEffect(() => {
    gsap.fromTo(
      formRef.current,
      { opacity: 0, y: 50 },
      { opacity: 1, y: 0, duration: 1, ease: "power2.out" }
    );
  }, []);

  const handleTinhChange = (e) => {
    const tinhCode = parseInt(e.target.value);
    const selectedTinh = listTinh.find((t) => t.code === tinhCode);
    console.log("Selected Tinh:", selectedTinh); // Debug tỉnh được chọn
    setNhaCungCap((prev) => ({
      ...prev,
      tinh: { code: tinhCode, name: selectedTinh?.name || "" },
      huyen: { code: 0, name: "" },
      xa: { code: 0, name: "" },
    }));
    setListHuyen([]);
    setListXa([]);
  };

  const handleHuyenChange = (e) => {
    const huyenCode = parseInt(e.target.value);
    const selectedHuyen = listHuyen.find((h) => h.code === huyenCode);
    console.log("Selected Huyen:", selectedHuyen); // Debug huyện được chọn
    setNhaCungCap((prev) => ({
      ...prev,
      huyen: { code: huyenCode, name: selectedHuyen?.name || "" },
      xa: { code: 0, name: "" },
    }));
    setListXa([]);
  };

  const handleXaChange = (e) => {
    const xaCode = parseInt(e.target.value);
    const selectedXa = listXa.find((x) => x.code === xaCode);
    console.log("Selected Xa:", selectedXa); // Debug xã được chọn
    setNhaCungCap((prev) => ({
      ...prev,
      xa: { code: xaCode, name: selectedXa?.name || "" },
    }));
  };

  const handleSave = () => {
    console.log("Saving NhaCungCap:", nhaCungCap); // Debug dữ liệu trước khi lưu
    saveSupplier(nhaCungCap)
      .then(() => toast.success("Cập nhật thông tin thành công"))
      .catch((err) =>
        toast.error(err.response?.data?.message || "Lỗi không xác định")
      );
  };

  return (
    <div ref={formRef} className="max-w-7xl mx-auto p-6 space-y-6">
      <div className="relative bg-white p-3 rounded-md flex justify-between items-center">
        <div>
          <i className="fa-solid fa-house bg-violet-100 text-violet-500 p-2 rounded-sm mr-2"></i>
          <strong className="text-lg">Thêm nhà cung cấp</strong>
          <p className="text-sm text-gray-600 mt-1">Thêm mới thông tin nhà cung cấp</p>
        </div>
        <button
          onClick={handleSave}
          className="px-2 py-2 text-violet-500 bg-violet-100 font-semibold rounded-md shadow transition duration-200"
          disabled={isLoadingTinh || isLoadingHuyen || isLoadingXa}
        >
          {(id!=null && id!=0 ?"Cập nhật thông tin" :"Cập nhật thông tin")}
        </button>
      </div>

      <section className="bg-white p-6 rounded-xl shadow-md space-y-6">
        <strong className="text-lg">
          <i className="fa-solid fa-house bg-yellow-100 text-yellow-500 p-2 rounded-sm mr-2"></i>Thông tin cơ bản
        </strong>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              <i className="fa-solid fa-boxes-stacked bg-yellow-100 text-yellow-500 p-2 rounded-sm mr-2"></i> Tên Nhà Cung Cấp:
            </label>
            <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
              <span className="px-3 text-gray-500"></span>
              <input
                type="text"
                value={nhaCungCap.name}
                onChange={(e) => setNhaCungCap({ ...nhaCungCap, name: e.target.value })}
                placeholder="Nhập tên nhà cung cấp..."
                className="w-full p-2.5 text-gray-900 outline-none"
              />
            </div>
          </div>
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              <i className="fa-solid fa-phone-volume bg-yellow-100 text-yellow-500 p-2 rounded-sm mr-2"></i> Số Điện Thoại:
            </label>
            <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
              <span className="px-3 text-gray-500"></span>
              <input
                type="text"
                value={nhaCungCap.soDienThoai}
                onChange={(e) => setNhaCungCap({ ...nhaCungCap, soDienThoai: e.target.value })}
                placeholder="Nhập số điện thoại..."
                className="w-full p-2.5 text-gray-900 outline-none"
              />
            </div>
          </div>
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              <i className="fa-solid fa-landmark bg-yellow-100 text-yellow-500 p-2 rounded-sm mr-2"></i> Mã Số Thuế:
            </label>
            <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
              <span className="px-3 text-gray-500"></span>
              <input
                type="text"
                value={nhaCungCap.maSoThue}
                onChange={(e) => setNhaCungCap({ ...nhaCungCap, maSoThue: e.target.value })}
                placeholder="Nhập mã số thuế..."
                className="w-full p-2.5 text-gray-900 outline-none"
              />
            </div>
          </div>
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              <i className="fa-regular fa-credit-card bg-yellow-100 text-yellow-500 p-2 rounded-sm mr-2"></i> Số Tài Khoản:
            </label>
            <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
              <span className="px-3 text-gray-500"></span>
              <input
                type="text"
                value={nhaCungCap.soTaiKhoan}
                onChange={(e) => setNhaCungCap({ ...nhaCungCap, soTaiKhoan: e.target.value })}
                placeholder="Nhập số tài khoản..."
                className="w-full p-2.5 text-gray-900 outline-none"
              />
            </div>
          </div>
        </div>
      </section>

      <section className="bg-white p-6 rounded-xl shadow-md space-y-6">
        <strong className="text-lg">
          <i className="fa-solid fa-house bg-green-100 text-green-500 p-2 rounded-sm mr-2"></i>Thông tin địa chỉ
        </strong>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              <i className="fa-solid fa-city bg-green-100 text-green-500 p-2 rounded-sm mr-2"></i> Tỉnh/Thành Phố:
            </label>
            <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
              <span className="px-3 text-gray-500"></span>
              <select
                className="w-full p-2.5 text-gray-900 outline-none bg-transparent cursor-pointer"
                value={nhaCungCap.tinh.code || ""}
                onChange={handleTinhChange}
                disabled={isLoadingTinh || !listTinh.length}
              >
                <option value="" disabled>
                  {isLoadingTinh ? "Đang tải..." : "-- Chọn tỉnh --"}
                </option>
                {listTinh.map((tinh) => (
                  <option key={tinh.code} value={tinh.code}>
                    {tinh.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              <i className="fa-solid fa-building bg-green-100 text-green-500 p-2 rounded-sm mr-2"></i> Quận/Huyện:
            </label>
            <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
              <span className="px-3 text-gray-500"></span>
              <select
                className="w-full p-2.5 text-gray-900 outline-none bg-transparent cursor-pointer"
                value={nhaCungCap.huyen.code || ""}
                onChange={handleHuyenChange}
                disabled={isLoadingHuyen || !listHuyen.length || !nhaCungCap.tinh.code}
              >
                <option value="" disabled>
                  {isLoadingHuyen ? "Đang tải..." : "-- Chọn huyện --"}
                </option>
                {listHuyen.map((huyen) => (
                  <option key={huyen.code} value={huyen.code}>
                    {huyen.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              <i className="fa-solid fa-map-location bg-green-100 text-green-500 p-2 rounded-sm mr-2"></i> Xã/Phường:
            </label>
            <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
              <span className="px-3 text-gray-500"></span>
              <select
                className="w-full p-2.5 text-gray-900 outline-none bg-transparent cursor-pointer"
                value={nhaCungCap.xa.code || ""}
                onChange={handleXaChange}
                disabled={isLoadingXa || !listXa.length || !nhaCungCap.huyen.code}
              >
                <option value="" disabled>
                  {isLoadingXa ? "Đang tải..." : "-- Chọn xã --"}
                </option>
                {listXa.map((xa) => (
                  <option key={xa.code} value={xa.code}>
                    {xa.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
        <div className="relative">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            <i className="fa-solid fa-location-dot bg-green-100 text-green-500 p-2 rounded-sm mr-2"></i> Địa Chỉ Chi Tiết:
          </label>
          <div className="flex items-center border border-gray-300 rounded-lg overflow-hidden mt-2">
            <span className="px-3 text-gray-500"></span>
            <input
              type="text"
              value={nhaCungCap.diaChi}
              onChange={(e) => setNhaCungCap({ ...nhaCungCap, diaChi: e.target.value })}
              placeholder="Nhập địa chỉ chi tiết..."
              className="w-full p-2.5 text-gray-900 outline-none"
            />
          </div>
        </div>
      </section>
    </div>
  );
}

export { AddSupplier };
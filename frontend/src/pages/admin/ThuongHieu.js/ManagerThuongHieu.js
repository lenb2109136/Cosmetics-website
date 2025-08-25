import { useEffect, useState } from "react";
import { getManager, update, save, saveT } from "../../../services/ThuongHieuService";
import { Pagination } from "../../../components/commons/Pagination";
import Modal from "../../../components/commons/modal";
import { toast } from "react-toastify";

function ManagerThuongHieu() {
    const [danhSachThuongHieu, setDanhSachThuongHieu] = useState([]);
    const [tongTrang, setTongTrang] = useState(0);
    const [tongSoPhanTu, setTongSoPhanTu] = useState(0);
    const [tenThongSo, setTenThongSo] = useState("");
    const [trangHienTai, setTrangHienTai] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [isSaveModalOpen, setIsSaveModalOpen] = useState(false);
    const [selectedImage, setSelectedImage] = useState(null);
    const [selectedBrand, setSelectedBrand] = useState(null);
    const [editData, setEditData] = useState({ ten: "", anhBia: null, anhDaiDien: null, anhNen: null });
    const [saveData, setSaveData] = useState({ ten: "", anhBia: null, anhDaiDien: null, anhNen: null });
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        getManager(tenThongSo, trangHienTai)
            .then((d) => {
                console.log("API response:", d);
                setDanhSachThuongHieu(d.content || []);
                setTongSoPhanTu(d.totalElements || 0);
                setTongTrang(d.totalPages || 0);
            })
            .catch((err) => {
                console.error("API error:", err);
                setDanhSachThuongHieu([]);
                setTongSoPhanTu(0);
                setTongTrang(0);
            });
    }, [tenThongSo, trangHienTai]);

    const openModal = (imageUrl) => {
        setSelectedImage(imageUrl || null);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedImage(null);
    };

    const openDetailModal = (brand) => {
        setSelectedBrand(brand);
        setEditData({
            ten: brand.ten,
            anhBia: null,
            anhDaiDien: null,
            anhNen: null
        });
        setIsDetailModalOpen(true);
    };

    const closeDetailModal = () => {
        setIsDetailModalOpen(false);
        setSelectedBrand(null);
        setEditData({ ten: "", anhBia: null, anhDaiDien: null, anhNen: null });
    };

    const openSaveModal = () => {
        setIsSaveModalOpen(true);
    };

    const closeSaveModal = () => {
        setIsSaveModalOpen(false);
        setSaveData({ ten: "", anhBia: null, anhDaiDien: null, anhNen: null });
    };

    const handleInputChange = (e) => {
        const { name, value, files } = e.target;
        if (files) {
            setEditData({ ...editData, [name]: files[0] });
        } else {
            setEditData({ ...editData, [name]: value });
        }
    };

    const handleSaveInputChange = (e) => {
        const { name, value, files } = e.target;
        if (files) {
            setSaveData({ ...saveData, [name]: files[0] });
        } else {
            setSaveData({ ...saveData, [name]: value });
        }
    };

    const handleUpdate = () => {
        setIsLoading(true);
        const formData = new FormData();
        if (editData.ten && editData.ten !== selectedBrand.ten) {
            formData.append("ten", editData.ten);
        }
        if (editData.anhBia) {
            formData.append("anhBia", editData.anhBia);
        }
        if (editData.anhDaiDien) {
            formData.append("anhDaiDien", editData.anhDaiDien);
        }
        if (editData.anhNen) {
            formData.append("anhNen", editData.anhNen);
        }
        formData.append("id", selectedBrand.id);

        update(formData)
            .then(() => {
                toast.success("Cập nhật thành công!");
                return getManager(tenThongSo, trangHienTai);
            })
            .then((d) => {
                setDanhSachThuongHieu(d.content || []);
                setTongSoPhanTu(d.totalElements || 0);
                setTongTrang(d.totalPages || 0);
                closeDetailModal();
            })
            .catch((e) => {
                toast.error(e?.response?.data?.message || "Cập nhật thất bại");
            })
            .finally(() => {
                setIsLoading(false);
            });
    };

    const handleSave = () => {
        if (!saveData.ten || !saveData.anhBia || !saveData.anhDaiDien || !saveData.anhNen) {
            toast.error("Vui lòng cung cấp đầy đủ tên và các hình ảnh");
            return;
        }

        setIsLoading(true);
        const formData = new FormData();
        formData.append("ten", saveData.ten);
        formData.append("anhBia", saveData.anhBia);
        formData.append("anhDaiDien", saveData.anhDaiDien);
        formData.append("anhNen", saveData.anhNen);

        saveT(formData)
            .then(() => {
                toast.success("Thêm thương hiệu thành công!");
                return getManager(tenThongSo, trangHienTai);
            })
            .then((d) => {
                setDanhSachThuongHieu(d.content || []);
                setTongSoPhanTu(d.totalElements || 0);
                setTongTrang(d.totalPages || 0);
                closeSaveModal();
            })
            .catch((e) => {
                toast.error(e?.response?.data?.message || "Thêm thương hiệu thất bại");
            })
            .finally(() => {
                setIsLoading(false);
            });
    };

    return (
        <div className="mt-2 p-3 bg-white">
            <div className="flex justify-between items-center my-4 bg-white">
                <div className="flex items-center basis-1/2 space-x-3">
                    <span className="flex items-center justify-center w-8 h-8 bg-gray-200 rounded-full">
                        <i className="fas fa-search text-gray-700"></i>
                    </span>
                    <input
                        type="text"
                        placeholder="Nhập tên thương hiệu..."
                        className="w-full px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg shadow-md focus:outline-none  transition duration-300"
                        value={tenThongSo}
                        onChange={(e) => setTenThongSo(e.target.value)}
                        aria-label="Nhập tên thương hiệu để tìm kiếm"
                    />
                </div>
                <button
                    onClick={openSaveModal}
                    className="flex items-center px-4 py-2 text-sm font-medium text-green-900  rounded-lg shadow-md border transition-colors"
                    aria-label="Thêm thương hiệu mới"
                >
                    <span className="flex items-center justify-center w-6 h-6 mr-2  rounded-full">
                        <i className="fas fa-plus text-green-900"></i>
                    </span>
                    Thêm thương hiệu
                </button>
            </div>
            <div>
                <div className=" flex justify-between">
                    <div className="flex items-center w-fit gap-2 p-3  rounded-lg  mb-4 pr-4">
                        <i className="fa-solid fa-list text-gray-600 text-md bg-green-900 text-white p-2 rounded-md"></i>
                        <p className="text-gray-800 font-medium text-md">
                            Tổng số thương hiệu: <span className="font-bold">{tongSoPhanTu}</span>
                        </p>
                    </div>
                    <Pagination
                        setTrangHienTai={setTrangHienTai}
                        soLuongTrang={tongTrang}
                        trangHienTai={trangHienTai}
                        color="bg-green-900"
                    />
                </div>
                <table className="w-full text-center justify-center">
                    <thead>
                        <tr className="font-bold pt-1 pb-1 text-center bg-gray-100">
                            <th className="py-3 px-4">STT</th>
                            <th className="py-3 px-4">Tên thương hiệu</th>
                            <th className="py-3 px-4">Số sản phẩm</th>
                            <th className="py-3 px-4">Ảnh bìa</th>
                            <th className="py-3 px-4">Ảnh đại diện</th>
                            <th className="py-3 px-4">Ảnh nền</th>
                            <th className="py-3 px-4">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Array.isArray(danhSachThuongHieu) && danhSachThuongHieu.length > 0 ? (
                            danhSachThuongHieu.map((d, index) => (
                                <tr className="pt-1 pb-1 text-center" key={d.id}>
                                    <td>{index + 1}</td>
                                    <td>{d.ten}</td>
                                    <td>{d.tongSanPha}</td>
                                    <td className="text-center">
                                        <img
                                            src={d?.anhBia}
                                            className="max-w-14 cursor-pointer"
                                            alt="Ảnh bìa"
                                            onClick={() => openModal(d?.anhBia)}
                                        />
                                    </td>
                                    <td>
                                        <img
                                            src={d?.anhDaiDien}
                                            className="max-w-14 cursor-pointer"
                                            alt="Ảnh đại diện"
                                            onClick={() => openModal(d?.anhDaiDien)}
                                        />
                                    </td>
                                    <td>
                                        <img
                                            src={d?.anhNen}
                                            className="max-w-14 cursor-pointer"
                                            alt="Ảnh nền"
                                            onClick={() => openModal(d?.anhNen)}
                                        />
                                    </td>
                                    <td>
                                        <button
                                            onClick={() => openDetailModal(d)}
                                            className="px-3 py-1  text-green-900 rounded border shadow-md "
                                        >
                                            Xem chi tiết
                                        </button>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="7" className="text-center">
                                    Không có dữ liệu
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            {isModalOpen && selectedImage && (
                <Modal isOpen={isModalOpen} setOpen={closeModal}>
                    <div className="flex justify-center items-center">
                        <img
                            src={selectedImage}
                            alt="Ảnh lớn"
                            className="max-w-[80vw] max-h-[80vh] object-contain"
                        />
                    </div>
                </Modal>
            )}
            {isDetailModalOpen && selectedBrand && (
                <Modal isOpen={isDetailModalOpen} setOpen={closeDetailModal}>
                    <div className="p-6 bg-white rounded-lg shadow-lg max-w-2xl w-full">
                        <div className="flex items-center mb-4">
                            <span className="flex items-center justify-center w-8 h-8 mr-3 bg-green-200 rounded-full">
                                <i className="fas fa-registered text-green-700"></i>
                            </span>
                            <h2 className="text-lg font-semibold text-gray-900">Chi tiết thương hiệu</h2>
                        </div>
                        <div className="mb-4">
                            <label className="block mb-2 text-sm font-medium text-gray-700">Tên thương hiệu:</label>
                            <div className="flex items-center space-x-3">
                                <span className="flex items-center justify-center w-6 h-6 bg-gray-200 rounded-full">
                                    <i className="fas fa-tag text-gray-700"></i>
                                </span>
                                <input
                                    type="text"
                                    name="ten"
                                    value={editData.ten}
                                    onChange={handleInputChange}
                                    className="w-full px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg shadow-md focus:outline-none focus:ring-2 focus:ring-green-500 transition duration-300"
                                    placeholder="Nhập tên thương hiệu"
                                    aria-label="Nhập tên thương hiệu"
                                />
                            </div>
                        </div>
                        <div className="mb-6">
                            <label className="block mb-2 text-sm font-medium text-gray-700">Ảnh thương hiệu:</label>
                            <div className="flex items-center justify-between gap-6">
                                <div className="relative flex-1">
                                    <label className="block mb-1 text-xs font-medium text-gray-600 flex items-center gap-2">
                                        <i className="fas fa-image text-green-700 bg-green-100 p-2 rounded-md mb-2"></i> Ảnh bìa
                                    </label>
                                    <input
                                        type="file"
                                        name="anhBia"
                                        onChange={handleInputChange}
                                        className="hidden"
                                        id="anhBia"
                                        accept="image/*"
                                    />
                                    <label htmlFor="anhBia" className="cursor-pointer flex flex-col items-center">
                                        {editData.anhBia ? (
                                            <img
                                                src={URL.createObjectURL(editData.anhBia)}
                                                alt="Ảnh bìa thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : selectedBrand.anhBia ? (
                                            <img
                                                src={selectedBrand.anhBia}
                                                alt="Ảnh bìa thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : (
                                            <div className="w-32 h-32 bg-gray-200 rounded-md flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-500 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Thay đổi ảnh</span>
                                    </label>
                                </div>
                                <div className="relative flex-1">
                                    <label className="block mb-1 text-xs font-medium text-gray-600 flex items-center gap-2">
                                        <i className="fas fa-user-circle text-green-700 bg-green-100 p-2 rounded-md mb-2"></i> Ảnh đại diện
                                    </label>
                                    <input
                                        type="file"
                                        name="anhDaiDien"
                                        onChange={handleInputChange}
                                        className="hidden"
                                        id="anhDaiDien"
                                        accept="image/*"
                                    />
                                    <label htmlFor="anhDaiDien" className="cursor-pointer flex flex-col items-center">
                                        {editData.anhDaiDien ? (
                                            <img
                                                src={URL.createObjectURL(editData.anhDaiDien)}
                                                alt="Ảnh đại diện thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : selectedBrand.anhDaiDien ? (
                                            <img
                                                src={selectedBrand.anhDaiDien}
                                                alt="Ảnh đại diện thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : (
                                            <div className="w-32 h-32 bg-gray-200 rounded-md flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-500 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Thay đổi ảnh</span>
                                    </label>
                                </div>
                                <div className="relative flex-1">
                                    <label className="block mb-1 text-xs font-medium text-gray-600 flex items-center gap-2">
                                        <i className="fas fa-layer-group text-green-700 bg-green-100 p-2 rounded-md mb-2"></i> Ảnh nền
                                    </label>
                                    <input
                                        type="file"
                                        name="anhNen"
                                        onChange={handleInputChange}
                                        className="hidden"
                                        id="anhNen"
                                        accept="image/*"
                                    />
                                    <label htmlFor="anhNen" className="cursor-pointer flex flex-col items-center">
                                        {editData.anhNen ? (
                                            <img
                                                src={URL.createObjectURL(editData.anhNen)}
                                                alt="Ảnh nền thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : selectedBrand.anhNen ? (
                                            <img
                                                src={selectedBrand.anhNen}
                                                alt="Ảnh nền thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : (
                                            <div className="w-32 h-32 bg-gray-200 rounded-md flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-500 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Thay đổi ảnh</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div className="flex justify-end gap-3">
                            <button
                                onClick={closeDetailModal}
                                className="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg shadow-md hover:bg-gray-200 transition-colors"
                                aria-label="Hủy bỏ"
                            >
                                <span className="flex items-center justify-center w-6 h-6 mr-2 bg-gray-200 rounded-full">
                                    <i className="fas fa-times text-gray-700"></i>
                                </span>
                                Hủy
                            </button>
                            <button
                                onClick={handleUpdate}
                                className="flex items-center px-4 py-2 text-sm font-medium text-green-700 bg-green-100 rounded-lg shadow-md hover:bg-green-200 transition-colors"
                                disabled={isLoading}
                                aria-label="Cập nhật thương hiệu"
                            >
                                <span className="flex items-center justify-center w-6 h-6 mr-2 bg-green-200 rounded-full">
                                    {isLoading ? (
                                        <svg
                                            className="animate-spin h-5 w-5 text-green-700"
                                            xmlns="http://www.w3.org/2000/svg"
                                            fill="none"
                                            viewBox="0 0 24 24"
                                        >
                                            <circle
                                                className="opacity-25"
                                                cx="12"
                                                cy="12"
                                                r="10"
                                                stroke="currentColor"
                                                strokeWidth="4"
                                            ></circle>
                                            <path
                                                className="opacity-75"
                                                fill="currentColor"
                                                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                                            ></path>
                                        </svg>
                                    ) : (
                                        <i className="fas fa-check text-green-700"></i>
                                    )}
                                </span>
                                {isLoading ? "Đang cập nhật..." : "Cập nhật"}
                            </button>
                        </div>
                    </div>
                </Modal>
            )}
            {isSaveModalOpen && (
                <Modal isOpen={isSaveModalOpen} setOpen={closeSaveModal}>
                    <div className="p-6 bg-white rounded-lg shadow-lg max-w-2xl w-full">
                        <div className="flex items-center mb-4">
                            <span className="flex items-center justify-center w-8 h-8 mr-3 bg-green-200 rounded-full">
                                <i className="fas fa-plus text-green-700"></i>
                            </span>
                            <h2 className="text-lg font-semibold text-gray-900">Thêm thương hiệu mới</h2>
                        </div>
                        <div className="mb-4">
                            <label className="block mb-2 text-sm font-medium text-gray-700">Tên thương hiệu:</label>
                            <div className="flex items-center space-x-3">
                                <span className="flex items-center justify-center w-6 h-6 bg-gray-200 rounded-full">
                                    <i className="fas fa-tag text-gray-700"></i>
                                </span>
                                <input
                                    type="text"
                                    name="ten"
                                    value={saveData.ten}
                                    onChange={handleSaveInputChange}
                                    className="w-full px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg shadow-md focus:outline-none  transition duration-300"
                                    placeholder="Nhập tên thương hiệu"
                                    aria-label="Nhập tên thương hiệu"
                                />
                            </div>
                        </div>
                        <div className="mb-6">
                            <label className="block mb-2 text-sm font-medium text-gray-700">Ảnh thương hiệu:</label>
                            <div className="flex items-center justify-between gap-6">
                                <div className="relative flex-1">
                                    <label className="block mb-1 text-xs font-medium text-gray-600 flex items-center gap-2">
                                        <i className="fas fa-image bg-orange-500 text-white rounded-md p-2"></i> Ảnh bìa
                                    </label>
                                    <input
                                        type="file"
                                        name="anhBia"
                                        onChange={handleSaveInputChange}
                                        className="hidden"
                                        id="saveAnhBia"
                                        accept="image/*"
                                    />
                                    <label htmlFor="saveAnhBia" className="cursor-pointer flex flex-col items-center">
                                        {saveData.anhBia ? (
                                            <img
                                                src={URL.createObjectURL(saveData.anhBia)}
                                                alt="Ảnh bìa thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : (
                                            <div className="w-32 h-32 bg-gray-200 rounded-md flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-500 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Thay đổi ảnh</span>
                                    </label>
                                </div>
                                <div className="relative flex-1">
                                    <label className="block mb-1 text-xs font-medium text-gray-600 flex items-center gap-2">
                                        <i className="fas fa-user-circle bg-orange-500 text-white rounded-md p-2"></i> Ảnh đại diện
                                    </label>
                                    <input
                                        type="file"
                                        name="anhDaiDien"
                                        onChange={handleSaveInputChange}
                                        className="hidden"
                                        id="saveAnhDaiDien"
                                        accept="image/*"
                                    />
                                    <label htmlFor="saveAnhDaiDien" className="cursor-pointer flex flex-col items-center">
                                        {saveData.anhDaiDien ? (
                                            <img
                                                src={URL.createObjectURL(saveData.anhDaiDien)}
                                                alt="Ảnh đại diện thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : (
                                            <div className="w-32 h-32 bg-gray-200 rounded-md flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-500 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Thay đổi ảnh</span>
                                    </label>
                                </div>
                                <div className="relative flex-1">
                                    <label className="block mb-1 text-xs font-medium text-gray-600 flex items-center gap-2">
                                        <i className="fas fa-layer-group bg-orange-500 text-white rounded-md p-2"></i> Ảnh nền
                                    </label>
                                    <input
                                        type="file"
                                        name="anhNen"
                                        onChange={handleSaveInputChange}
                                        className="hidden"
                                        id="saveAnhNen"
                                        accept="image/*"
                                    />
                                    <label htmlFor="saveAnhNen" className="cursor-pointer flex flex-col items-center">
                                        {saveData.anhNen ? (
                                            <img
                                                src={URL.createObjectURL(saveData.anhNen)}
                                                alt="Ảnh nền thương hiệu"
                                                className="w-32 h-32 object-cover rounded-md border border-gray-200 hover:opacity-80 transition duration-300"
                                            />
                                        ) : (
                                            <div className="w-32 h-32 bg-gray-200 rounded-md flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-500 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Thay đổi ảnh</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div className="flex justify-end gap-3 mt-6">
                            <button
                                onClick={closeSaveModal}
                                className="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg shadow-md hover:bg-gray-200 transition-colors"
                                aria-label="Hủy bỏ"
                            >
                                <span className="flex items-center justify-center w-6 h-6 mr-2 bg-gray-200 rounded-full">
                                    <i className="fas fa-times text-gray-700"></i>
                                </span>
                                Hủy
                            </button>
                            <button
                                onClick={handleSave}
                                className="flex items-center px-4 py-2 text-sm font-medium text-green-700 bg-green-100 rounded-lg shadow-md hover:bg-green-200 transition-colors"
                                disabled={isLoading}
                                aria-label="Lưu thương hiệu"
                            >
                                <span className="flex items-center justify-center w-6 h-6 mr-2 bg-green-200 rounded-full">
                                    {isLoading ? (
                                        <svg
                                            className="animate-spin h-5 w-5 text-green-700"
                                            xmlns="http://www.w3.org/2000/svg"
                                            fill="none"
                                            viewBox="0 0 24 24"
                                        >
                                            <circle
                                                className="opacity-25"
                                                cx="12"
                                                cy="12"
                                                r="10"
                                                stroke="currentColor"
                                                strokeWidth="4"
                                            ></circle>
                                            <path
                                                className="opacity-75"
                                                fill="currentColor"
                                                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                                            ></path>
                                        </svg>
                                    ) : (
                                        <i className="fas fa-check text-green-700"></i>
                                    )}
                                </span>
                                {isLoading ? "Đang lưu..." : "Lưu"}
                            </button>
                        </div>
                    </div>
                </Modal>
            )}

        </div>
    );
}

export { ManagerThuongHieu };
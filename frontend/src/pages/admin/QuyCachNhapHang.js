import { useEffect, useState } from "react";
import { getAll, them, put } from "../../services/QuyCachNhapHang";
import Modal from "../../components/commons/modal";
import { toast } from "react-toastify";

function QuyCachNhapHang() {
    const [ds, setDs] = useState([]);
    const [formData, setFormData] = useState({ ten: "", anhMoTa: null });
    const [editData, setEditData] = useState({ id: null, ten: "", anhMoTa: null });
    const [isLoading, setIsLoading] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [selectedItem, setSelectedItem] = useState(null);

    useEffect(() => {
        getAll()
            .then((data) => {
                setDs(data || []);
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }, []);

    const handleInputChange = (e) => {
        const { name, value, files } = e.target;
        if (name === "anhMoTa" && files && files[0]) {
            setFormData((prev) => ({ ...prev, [name]: files[0] }));
        } else {
            setFormData((prev) => ({ ...prev, [name]: value }));
        }
    };

    const handleEditInputChange = (e) => {
        const { name, value, files } = e.target;
        if (name === "anhMoTa" && files && files[0]) {
            setEditData((prev) => ({ ...prev, [name]: files[0] }));
        } else {
            setEditData((prev) => ({ ...prev, [name]: value }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            const formDataToSend = new FormData();
            formDataToSend.append("ten", formData.ten.trim());
            if (formData.anhMoTa) {
                formDataToSend.append("anhMoTa", formData.anhMoTa);
            }
            await them(formDataToSend);
            toast.success("Thêm quy cách thành công!");
            setFormData({ ten: "", anhMoTa: null });
            const updatedData = await getAll();
            setDs(updatedData || []);
            setIsModalOpen(false);
        } catch (error) {
            toast.error(error?.response?.data?.message || "Lỗi khi thêm quy cách!");
            console.error("Error creating item:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            const formDataToSend = new FormData();
            formDataToSend.append("ten", editData.ten.trim());
            if (editData.anhMoTa) {
                formDataToSend.append("anhMoTa", editData.anhMoTa);
            }
            formDataToSend.append("id", editData.id);
            await put(formDataToSend);
            toast.success("Cập nhật quy cách thành công!");
            setEditData({ id: null, ten: "", anhMoTa: null });
            const updatedData = await getAll();
            setDs(updatedData || []);
            setIsEditModalOpen(false);
            setSelectedItem(null);
        } catch (error) {
            toast.error(error?.response?.data?.message || "Lỗi khi cập nhật quy cách!");
            console.error("Error updating item:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const openModal = () => {
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setFormData({ ten: "", anhMoTa: null });
    };

    const openEditModal = (item) => {
        setSelectedItem(item);
        setEditData({ id: item.id, ten: item.tenQuyCach, anhMoTa: null });
        setIsEditModalOpen(true);
    };

    const closeEditModal = () => {
        setIsEditModalOpen(false);
        setEditData({ id: null, ten: "", anhMoTa: null });
        setSelectedItem(null);
    };

    return (
        <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-7xl mx-auto">
                <div className="bg-white p-6 rounded-xl shadow-lg">
                    <div className="flex items-center justify-between mb-6">
                        <div className="flex items-center space-x-3">
                            <span className="flex items-center justify-center w-10 h-10 border shadow-md rounded-md">
                                <i className="fas fa-list-ul text-green-900 text-lg"></i>
                            </span>
                            <h2 className="text-lg font-bold text-gray-900">Danh sách quy cách nhập</h2>
                        </div>
                        <button
                            onClick={openModal}
                            className="flex items-center px-5 py-2 bg-green-900 text-white rounded-lg  transition-colors duration-200 shadow-md"
                        >
                            <i className="fas fa-plus mr-2"></i>
                            Thêm quy cách
                        </button>
                    </div>
                    <div className="grid grid-cols-5 gap-6">
                        {Array.isArray(ds) && ds.length > 0 ? (
                            ds.map((d, index) => (
                                <div
                                    key={d?.id || index}
                                    className="bg-white p-4 rounded-lg cursor-pointer border border-gray-200 hover:shadow-xl transition-shadow duration-300 transform hover:-translate-y-2"
                                    onClick={() => openEditModal(d)}
                                >
                                    <div className="flex items-center space-x-4">
                                        <span className="text-gray-600 font-medium">{index + 1}</span>
                                        <div className="flex-1">
                                            <p className="text-gray-800 font-semibold truncate max-w-xs">
                                                {d?.tenQuyCach || "Chưa có tên"}
                                            </p>
                                        </div>
                                        {/* <div className="w-16 h-16 flex-shrink-0">
                                            {d?.duongDan ? (
                                                <img
                                                    className="w-full h-full object-cover rounded-full border-2 border-gray-100"
                                                    src={d?.duongDan}
                                                    alt={d?.tenQuyCach || "Hình ảnh quy cách"}
                                                />
                                            ) : (
                                                <div className="w-full h-full bg-gray-100 rounded-full flex items-center justify-center">
                                                    <i className="fas fa-camera text-gray-400 text-lg"></i>
                                                </div>
                                            )}
                                        </div> */}
                                    </div>
                                </div>
                            ))
                        ) : (
                            <p className="text-center text-gray-500 col-span-5">Không có dữ liệu</p>
                        )}
                    </div>
                </div>
            </div>

            {isModalOpen && (
                <Modal isOpen={isModalOpen} setOpen={closeModal}>
                    <div className="bg-white p-6 rounded-xl  max-w-md w-full">
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center space-x-3">
                                <span className="flex items-center justify-center w-10 h-10  rounded-md border shadow-md">
                                    <i className="fas fa-plus text-green-600 text-xl"></i>
                                </span>
                                <h2 className="text-lg  font-bold text-gray-900">Thêm quy cách mới</h2>
                            </div>
                           
                        </div>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Tên quy cách</label>
                                <div className="flex items-center space-x-3 bg-gray-50 p-3 rounded-lg">
                                    <span className="text-gray-500">
                                        <i className="fas fa-tag"></i>
                                    </span>
                                    <input
                                        type="text"
                                        name="ten"
                                        value={formData.ten}
                                        onChange={handleInputChange}
                                        className="w-full bg-transparent text-gray-900 focus:outline-none"
                                        placeholder="Nhập tên quy cách"
                                        required
                                        aria-label="Nhập tên quy cách"
                                    />
                                </div>
                            </div>
                            {/* <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Hình ảnh</label>
                                <div className="flex items-center space-x-4">
                                    <input
                                        type="file"
                                        name="anhMoTa"
                                        id="anhMoTa"
                                        onChange={handleInputChange}
                                        className="hidden"
                                        accept="image/*"
                                        required
                                    />
                                    <label htmlFor="anhMoTa" className="cursor-pointer flex flex-col items-center">
                                        {formData.anhMoTa ? (
                                            <img
                                                src={URL.createObjectURL(formData.anhMoTa)}
                                                alt="Hình ảnh quy cách"
                                                className="w-28 h-28 object-cover rounded-xl border-2 border-gray-200 hover:opacity-90 transition-opacity"
                                            />
                                        ) : (
                                            <div className="w-28 h-28 bg-gray-100 rounded-xl flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-400 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Chọn ảnh</span>
                                    </label>
                                </div>
                            </div> */}
                            <div className="flex justify-end space-x-4">
                                <button
                                    type="button"
                                    onClick={closeModal}
                                    className="px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300 transition-colors"
                                    aria-label="Hủy bỏ"
                                >
                                    Hủy
                                </button>
                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className="px-4 py-2 text-white bg-green-900 rounded-lg hover:bg-green-900 transition-colors disabled:bg-green-400"
                                    aria-label="Thêm quy cách mới"
                                >
                                    {isLoading ? (
                                        <span className="flex items-center">
                                            <svg
                                                className="animate-spin h-5 w-5 mr-2 text-white"
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
                                                />
                                                <path
                                                    className="opacity-75"
                                                    fill="currentColor"
                                                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                                                />
                                            </svg>
                                            Đang xử lý...
                                        </span>
                                    ) : (
                                        "Thêm"
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                </Modal>
            )}

            {isEditModalOpen && selectedItem && (
                <Modal isOpen={isEditModalOpen} setOpen={closeEditModal}>
                    <div className="bg-white p-6 rounded-xl  max-w-md w-full">
                        <div className="flex items-center justify-between mb-6">
                            <div className="flex items-center space-x-3">
                                <span className="flex items-center justify-center w-10 h-10  rounded-md border shadow-md">
                                    <i className="fas fa-edit text-green-900 text-xl"></i>
                                </span>
                                <h2 className="text-lg font-bold text-gray-900">Chỉnh sửa quy cách</h2>
                            </div>
                        </div>
                        <form onSubmit={handleUpdate} className="space-y-6">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Tên quy cách</label>
                                <div className="flex items-center space-x-3 bg-gray-50 p-3 rounded-lg">
                                    <span className="text-gray-500">
                                        <i className="fas fa-tag"></i>
                                    </span>
                                    {selectedItem.canUpdate ? (
                                        <input
                                            type="text"
                                            name="ten"
                                            value={editData.ten}
                                            onChange={handleEditInputChange}
                                            className="w-full bg-transparent text-gray-900 focus:outline-none border-none"
                                            placeholder="Nhập tên quy cách"
                                            required
                                            aria-label="Nhập tên quy cách"
                                        />
                                    ) : (
                                        <p className="w-full text-gray-900 font-medium">{editData.ten}</p>
                                    )}
                                </div>
                            </div>
                            {/* <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Hình ảnh</label>
                                <div className="flex items-center space-x-4">
                                    <input
                                        type="file"
                                        name="anhMoTa"
                                        id="editAnhMoTa"
                                        onChange={handleEditInputChange}
                                        className="hidden"
                                        accept="image/*"
                                    />
                                    <label htmlFor="editAnhMoTa" className="cursor-pointer flex flex-col items-center">
                                        {editData.anhMoTa ? (
                                            <img
                                                src={URL.createObjectURL(editData.anhMoTa)}
                                                alt="Hình ảnh quy cách"
                                                className="w-28 h-28 object-cover rounded-xl border-2 border-gray-200 hover:opacity-90 transition-opacity"
                                            />
                                        ) : selectedItem.duongDan ? (
                                            <img
                                                src={selectedItem.duongDan}
                                                alt="Hình ảnh quy cách"
                                                className="w-28 h-28 object-cover rounded-xl border-2 border-gray-200 hover:opacity-90 transition-opacity"
                                            />
                                        ) : (
                                            <div className="w-28 h-28 bg-gray-100 rounded-xl flex items-center justify-center">
                                                <i className="fas fa-camera text-gray-400 text-3xl"></i>
                                            </div>
                                        )}
                                        <span className="mt-2 text-sm text-gray-600">Chọn ảnh</span>
                                    </label>
                                </div>
                            </div> */}
                            <div className="flex justify-end space-x-4">
                                <button
                                    type="button"
                                    onClick={closeEditModal}
                                    className="px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300 transition-colors"
                                    aria-label="Hủy bỏ"
                                >
                                    Hủy
                                </button>
                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className="px-4 py-2 text-white bg-green-900 rounded-lg  transition-colors "
                                    aria-label="Cập nhật quy cách"
                                >
                                    {isLoading ? (
                                        <span className="flex items-center">
                                            <svg
                                                className="animate-spin h-5 w-5 mr-2 text-white"
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
                                                />
                                                <path
                                                    className="opacity-75"
                                                    fill="currentColor"
                                                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                                                />
                                            </svg>
                                            Đang xử lý...
                                        </span>
                                    ) : (
                                        "Cập nhật"
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                </Modal>
            )}
        </div>
    );
}

export { QuyCachNhapHang };
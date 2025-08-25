import { useEffect, useState } from "react";
import Modal from "../../../components/commons/modal";
import PickCategory from "../../../components/commons/PickCategory";
import { getAll } from "../../../services/ThongSoService";
import { toast } from "react-toastify";
import { deleteDanhMuc, getUpate, save, test, updateca } from "../../../services/CategoryService";
import { useNavigate, useParams } from "react-router-dom";

function isSameCategoryList(list1, list2) {
    if (!Array.isArray(list1) || !Array.isArray(list2)) return false;
    if (list1.length !== list2.length) return false;

    const ids1 = list1.map(item => item.id).sort();
    const ids2 = list2.map(item => item.id).sort();

    return ids1.every((id, index) => id === ids2[index]);
}

function UpdateCategory() {
    const { id } = useParams();
    const navigate=useNavigate()
    const [danhMucCreate, setDanhMucCreate] = useState({
        id: 0,
        ten: "",
        pickCategory: [],
        thongSoChon: [],
        trunggian: {
            id: 0,
            ten: ""
        }
    });

    const [picCategory, setPickCategory] = useState([]);
    const [chuoi, setChuoi] = useState("");
    const [danhsachthongso, setDanhsachthongso] = useState([]);
    const [coVanDe, setCoVanDe] = useState(false);
    const [tg, setTg] = useState(true);
    const [open, setOpen] = useState(false);
    const [open2, setOpen2] = useState(false);
    const [openDelete, setOpenDelete] = useState(false); // Thêm state để mở modal vô hiệu hóa
    const [lan, setLan] = useState(0);
    const [picCategory2, setPickCategory2] = useState([]);
    const [chuoi2, setChuoi2] = useState("");

    useEffect(() => {
        if (
            picCategory?.length !== 0 &&
            picCategory?.[picCategory?.length - 1]?.id === danhMucCreate?.id &&
            open === false
        ) {
            toast.error("Vui lòng chọn danh mục khác chính nó");
            picCategory.pop();
        } else {
            if (open == false) {
                let thongSoMoi = [];
                if (picCategory?.length != 0) {
                    const last = picCategory?.[picCategory?.length - 1];
                    console.log("có đi vô đây");
                    thongSoMoi = last?.thongSo || [];
                    setDanhsachthongso(thongSoMoi);
                }

                const thongSoMoiIds = thongSoMoi?.map(ts => ts.id);
                if (picCategory?.length != 0) {
                    danhMucCreate.thongSoChon = danhMucCreate.thongSoChon?.filter(ts => thongSoMoiIds.includes(ts)) || [];
                }
            }
        }
        const chuoiDanhMuc = picCategory.map(d => d.ten).join(" > ");
        setChuoi(chuoiDanhMuc);
    }, [picCategory, open]);

    useEffect(() => {
        if (open == true) {
            setLan(lan + 1);
        }
    }, [open]);

    useEffect(() => {
        const categoryChain = picCategory?.map(category => category?.ten)?.join(" -> ");
        setChuoi(categoryChain || "");
    }, [picCategory]);

    // Load danh mục khi có id
    useEffect(() => {
        if (id != null) {
            getUpate(id).then((data) => {
                setDanhMucCreate(data);
                setPickCategory(data.pickCategory);
                if (data?.pickCategory?.length == 0) {
                    getAll()
                        .then(d => setDanhsachthongso(d))
                        .catch(e => toast.error("Lấy dữ liệu thất bại"));
                } else {
                    setDanhsachthongso(data?.pickCategory?.[data?.pickCategory?.length - 1].thongSo);
                }
                setDanhMucCreate(prev => ({
                    ...prev,
                    thongSoChon: data.thongSoChon || []
                }));
            });
        }
    }, [id]);

    useEffect(() => {
        const last = picCategory?.[picCategory.length - 1];
        if (last == null) {
            setCoVanDe(false);
        } else {
            test(last.id).then((data) => {
                setCoVanDe(data);
            });
        }
    }, [picCategory]);

    const handleToggleThongSo = (id) => {
        setDanhMucCreate(prev => ({
            ...prev,
            thongSoChon: prev.thongSoChon.includes(id)
                ? prev.thongSoChon.filter(item => item !== id)
                : [...prev.thongSoChon, id]
        }));
    };

    return (
        <div className="mt-6 w-full mx-auto bg-white shadow-lg rounded-xl space-y-6 p-3">
            <h3 className="text-lg font-bold mt-2">Thông tin danh mục</h3>

            <div className="space-y-3">
                <input
                    value={danhMucCreate.ten}
                    onChange={(e) => {
                        setDanhMucCreate(prev => ({ ...prev, ten: e.target.value }));
                    }}
                    type="text"
                    placeholder="Nhập tên danh mục"
                    className="
                        w-[50%]
                        px-4 py-3
                        rounded-lg
                        border
                        border-gray-300
                        transition
                        duration-300
                        ease-in-out
                        shadow-sm
                        placeholder-gray-400
                        text-gray-700
                        outline-none
                    "
                />

                <strong className="block">
                    <i className="fa-solid fa-arrows-turn-right p-2 text-green-900 border shadow-md rounded-md"></i> {chuoi}
                </strong>
                <button
                    onClick={() => setOpen(true)}
                    className=" px-4 py-2 rounded text-green-900 border shadow-md rounded-md"
                >
                    Chọn danh mục cha
                </button>
                {setPickCategory?.length > 0 ? <button
                    onClick={() => {
                        setPickCategory([]);
                        getAll()
                            .then(d => setDanhsachthongso(d))
                            .catch(e => toast.error("Lấy dữ liệu thất bại"));
                    }}
                    className=" px-4 py-2 rounded  ml-2 text-green-900 border shadow-md rounded-md"
                >
                    Tạo danh mục gốc
                </button> : null
                }
            </div>

            {open && (
                <Modal setOpen={setOpen} b={true}>
                    <PickCategory
                        empty={true}
                        setOpen={setOpen}
                        categoryPick={picCategory}
                        setChuoi={setChuoi}
                        setcategoryPick={setPickCategory}
                        color="text-green-900"
                    />
                </Modal>
            )}

            {coVanDe && (
                <div className=" border-l-4 border-yellow-500 p-4 rounded">
                    <div className="space-y-3">
                        <p className="font-semibold">Tạo danh mục trung gian</p>
                        <input
                            type="text"
                            value={danhMucCreate.donDanhMuc.ten}
                            onChange={(e) => {
                                setDanhMucCreate({
                                    ...danhMucCreate,
                                    donDanhMuc: {
                                        ...danhMucCreate.donDanhMuc,
                                        ten: e.target.value,
                                    },
                                });
                            }}
                            placeholder="Nhập tên trung gian"
                            className="
                                w-[50%]
                                px-4 py-3
                                rounded-lg
                                border border-gray-300
                                outline-none
                                shadow-sm 
                                placeholder-gray-400
                                text-gray-700
                                transition duration-300 ease-in-out
                            "
                        />
                    </div>
                </div>
            )}
            {
                picCategory?.length !== 0 &&
                    danhMucCreate.danhMucCon.includes(picCategory[picCategory?.length - 1]?.id) ? (
                    <div className="space-y-3">
                        <p className="font-semibold">Tạo danh mục thay thế</p>
                        <input
                            type="text"
                            value={danhMucCreate.trunggian.ten}
                            onChange={(e) => {
                                setDanhMucCreate({
                                    ...danhMucCreate,
                                    trunggian: {
                                        ...danhMucCreate.trunggian,
                                        ten: e.target.value,
                                    },
                                });
                            }}
                            placeholder="Nhập tên danh mục thay thế"
                            className="
                                    w-[50%]
                                    px-4 py-3
                                    rounded-lg
                                    border border-gray-300
                                    outline-none
                                    shadow-sm 
                                    placeholder-gray-400
                                    text-gray-700
                                    transition duration-300 ease-in-out
                                    "
                        />
                    </div>
                ) : null
            }

            {danhsachthongso?.length > 0 && (
                <div className="space-y-3">
                    <p className="font-semibold">Chọn thông số phân cấp:</p>
                    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
                        {danhsachthongso.map((data) => {
                            const isSelected = danhMucCreate.thongSoChon.includes(data.id);
                            return (
                                <div
                                    key={data.id}
                                    onClick={() => handleToggleThongSo(data.id)}
                                    className={`cursor-pointer shadow-md px-2 py-2 rounded-md text-sm text-center border transition-all duration-300
                                        ${isSelected
                                            ? "bg-green-50 text-green-900 "
                                            : "text-gray-800 hover:bg-gray-200 border-gray-300"
                                        }`}
                                >
                                    {data.ten}
                                </div>
                            );
                        })}
                    </div>
                    <p className="text-sm text-gray-500">
                        Đã chọn: {danhMucCreate.thongSoChon?.length} thông số
                    </p>
                </div>
            )}
            <div className="flex">
                <button
                    onClick={() => {
                        danhMucCreate.pickCategory = picCategory;
                        updateca(danhMucCreate)
                            .then(() => {
                                toast.success("Cập nhật danh mục thành công");
                            })
                            .catch((err) => {
                                toast.error(err.response.data.message);
                            });
                    }}
                    className="bg-green-900 px-4 py-2 rounded text-white mr-2"
                >
                    Cập nhật
                </button>
                <button
                    onClick={() => setOpenDelete(true)} // Mở modal khi click
                    className="flex items-center px-4 py-2 rounded text-sm font-medium text-red-500 border rounded-md shadow-md transition-colors ml-2"
                >
                    <span className="flex items-center justify-center w-6 h-6 mr-2  rounded-full">
                        <i className="fas fa-ban text-red-500"></i>
                    </span>
                    Vô hiệu hóa
                </button>
            </div>
            {openDelete && (
                <Modal setOpen={setOpenDelete} b={true}>
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-md mx-auto">
                        <div className="flex items-center mb-4">
                            <span className="flex items-center justify-center w-8 h-8 mr-3 bg-red-200 rounded-full">
                                <i className="fas fa-ban text-red-700"></i>
                            </span>
                            <h2 className="text-lg font-semibold text-gray-900">Vô hiệu hóa danh mục</h2>
                        </div>
                        <p className="text-gray-700 mb-4">
                            Bạn có chắc chắn muốn vô hiệu hóa danh mục này?
                        </p>
                        {danhMucCreate?.coSanPhamCon ? (
                            <div className="mb-4">
                                <button
                                    onClick={() => setOpen2(true)}
                                    className="flex items-center px-4 py-2 text-sm font-medium text-violet-700 bg-violet-100 rounded-lg shadow-md hover:bg-violet-200 transition-colors"
                                    aria-label="Chọn danh mục thay thế"
                                >
                                    <span className="flex items-center justify-center w-6 h-6 mr-2 bg-violet-200 rounded-full">
                                        <i className="fas fa-folder-open text-violet-700"></i>
                                    </span>
                                    Chọn danh mục thay thế cho sản phẩm
                                </button>
                                <p className="mt-2 text-gray-700">
                                    Danh mục chọn: <span className="font-medium">{chuoi2 || "Chưa chọn"}</span>
                                </p>
                            </div>
                        ) : (
                            <p className="text-gray-700 mb-4">Không có sản phẩm con.</p>
                        )}
                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setOpenDelete(false)}
                                className="flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg shadow-md hover:bg-gray-200 transition-colors"
                                aria-label="Hủy bỏ"
                            >
                                <span className="flex items-center justify-center w-6 h-6 mr-2 bg-gray-200 rounded-full">
                                    <i className="fas fa-times text-gray-700"></i>
                                </span>
                                Hủy
                            </button>
                            <button
                                className={`flex items-center px-4 py-2 text-sm font-medium rounded-lg shadow-md transition-colors ${
                                    danhMucCreate?.coSanPhamCon && picCategory2?.length === 0
                                        ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                                        : "bg-green-100 text-green-700 hover:bg-green-200"
                                }`}
                                onClick={() => {
                                    const replacementCategoryId = picCategory2.length > 0 ? picCategory2[picCategory2.length - 1].id : 0;
                                    deleteDanhMuc(danhMucCreate?.id, replacementCategoryId)
                                        .then(() => {
                                            toast.success("Xóa thành công");
                                            navigate(-1)
                                            setOpenDelete(false);
                                        })
                                        .catch((e) => {
                                            toast.error(e?.response?.data?.message || "Lỗi không xác định");
                                        });
                                }}
                                disabled={danhMucCreate?.coSanPhamCon && picCategory2?.length === 0}
                                aria-label="Xác nhận vô hiệu hóa danh mục"
                            >
                                <span className="flex items-center justify-center w-6 h-6 mr-2 bg-green-200 rounded-full">
                                    <i className="fas fa-check text-green-700"></i>
                                </span>
                                Xác nhận
                            </button>
                        </div>
                    </div>
                </Modal>
            )}
            {open2 ? (
                <Modal setOpen={setOpen2} b={true}>
                    <PickCategory
                        empty={false}
                        setOpen={setOpen2}
                        categoryPick={picCategory2}
                        setChuoi={setChuoi2}
                        setcategoryPick={setPickCategory2}
                    />
                </Modal>
            ) : null}
        </div>
    );
}

export { UpdateCategory };
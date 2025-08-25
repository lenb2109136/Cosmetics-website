import { useEffect, useState } from "react";
import Modal from "../../../components/commons/modal";
import PickCategory from "../../../components/commons/PickCategory";
import { getAll } from "../../../services/ThongSoService";
import { toast } from "react-toastify";
import { getUpate, save, test } from "../../../services/CategoryService";
import { useSearchParams } from "react-router-dom";

function AddCategory() {
    const [search] = useSearchParams();
    const id = search.get("id");

    const [danhMucCreate, setdanhmuccreate] = useState({
        id: 0,
        ten: "",
        pickCategory: [],
        thongSoChon: [],
        trunggian: {
            id: 0,
            ten: ""
        }
    })

    const [picCategory, setPickCategory] = useState([]);
    useEffect(() => {
        if (id != null) {
            getUpate(id).then((data) => {
                setdanhmuccreate(data)
                setPickCategory(data.pickCategory)
            })
        }
    }, [])
    const [open, setOpen] = useState(false);
    const [chuoi, setChuoi] = useState("");
    const [danhsachthongso, setDanhsachthongso] = useState([]);
    const [coVanDe, setCoVanDe] = useState(false);
    const [tg, setTg] = useState(true);

    useEffect(() => {
        const last = picCategory?.[picCategory.length - 1];
        setDanhsachthongso(last?.thongSo || []);
    }, [picCategory]);
    useEffect(() => {
        setdanhmuccreate(prev => ({ ...prev, thongSoChon: [] }));
    }, [picCategory])

    useEffect(() => {
        const last = picCategory?.[picCategory.length - 1];
        if (last == null) {
            setCoVanDe(false)
        }
        else {
            test(last.id).then((data) => {
                setCoVanDe(data)
            })
        }
    }, [picCategory]);
    useEffect(() => {
        getAll().then(d => setDanhsachthongso(d)).catch(e => toast.error("Lấy dữ liệu thất bại"))
    }, [])
    const handleToggleThongSo = (id) => {
        setdanhmuccreate(prev => ({
            ...prev,
            thongSoChon: prev.thongSoChon.includes(id)
                ? prev.thongSoChon.filter(item => item !== id)
                : [...prev.thongSoChon, id]
        }));
    };

    return (
        <div className="mt-6 w-full mx-auto  bg-white shadow-lg rounded-xl space-y-6 p-3">
            <h3 className="text-lg font-bold mt-2">Thông tin danh mục</h3>

            <div className="space-y-3">
                <input
                    value={danhMucCreate.ten}
                    onChange={(e) => {
                        setdanhmuccreate(prev => ({ ...prev, ten: e.target.value }));
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

                <strong className="block "><i class="fa-solid fa-layer-group"></i> {chuoi}</strong>
                <button
                    onClick={() => setOpen(true)}
                    className="text-green-900 px-4 py-2 rounded-md border shadow-md "
                >
                    <i class="fa-solid fa-sitemap"></i> Chọn danh mục cha
                </button>
            </div>

            {open && (
                <Modal setOpen={setOpen} b={false}>
                    <PickCategory
                        empty={true}
                        setOpen={setOpen}
                        categoryPick={picCategory}
                        setChuoi={setChuoi}
                        setcategoryPick={setPickCategory}
                        color={"text-green-900"}
                    />
                </Modal>
            )}

            {coVanDe && (
                <div className=" border-l-4 border-yellow-500 p-4 rounded">
                    <p className="font-semibold mb-2"><i class="fa-solid fa-triangle-exclamation p-2 rounded-sm text-yellow-500 bg-yellow-100"></i> Hành động cần thiết:</p>
                    <label className="flex items-center mb-2 cursor-pointer">
                        <input
                            type="radio"
                            name="chon"
                            checked={tg}
                            onChange={() => setTg(true)}
                            className="peer hidden"
                        />
                        <div className="w-4 h-4 mr-2 rounded-full border-2 border-yellow-400 peer-checked:bg-yellow-400"></div>
                        <span className="peer-checked:text-yellow-500">Thêm danh mục trung gian</span>
                    </label>

                    <label className="flex items-center cursor-pointer peer-checked:bg-green-600">
                        <input
                            type="radio"
                            name="chon"
                            checked={!tg}
                            onChange={() => setTg(false)}
                            className="peer hidden"
                        />
                        <div className="w-4 h-4 mr-2 rounded-full border-2 border-gray-400  peer-checked:border-green-500 peer-checked:bg-green-500"></div>
                        <span className="peer-checked:text-green-600">Chuyển hết sản phẩm qua danh mục này</span>
                    </label>

                </div>
            )}

            {tg && coVanDe && (
                <div className="space-y-3">
                    <p className="font-semibold">Tạo danh mục trung gian</p>
                    <input
                        type="text"
                        value={danhMucCreate.trunggian.ten}
                        onChange={(e) => {
                            setdanhmuccreate({
                                ...danhMucCreate,
                                trunggian: {
                                    ...danhMucCreate.trunggian,
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
            )}

            {danhsachthongso.length > 0 && (
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
                                            ? "bg-green-50 text-green-900"
                                            : " text-gray-800 hover:bg-gray-200 border-gray-300"
                                        }`}
                                >
                                    {data.ten}
                                </div>

                            );
                        })}
                    </div>
                    <p className="text-sm text-gray-500">
                        Đã chọn: {danhMucCreate.thongSoChon.length} thông số
                    </p>
                </div>
            )}
            <button
                onClick={() => {
                    danhMucCreate.pickCategory=picCategory
                    save(danhMucCreate).then(() => {
                        toast.success("Thêm danh mục thành công")
                    }).catch((err) => {
                        toast.error(err.response.data.message)
                    })
                }}
                className="bg-green-900 px-4 py-2 rounded text-white"
            >
                Lưu thông tin
            </button>

        </div>
    );
}

export { AddCategory };

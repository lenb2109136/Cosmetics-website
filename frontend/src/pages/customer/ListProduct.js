import { useContext, useEffect, useRef, useState } from "react";
import { Context } from "./HomeCustommer";
import { getByCondition } from "../../services/ThuongHieuService";
import { toast } from "react-toastify";
import { getThongSoForFilter } from "../../services/ThongSoService";
import { getViewProduct } from "../../services/sanPhamService";
import { ProductItem } from "../../components/commons/ItemListProduct";
import { Pagination } from "../../components/commons/Pagination";
import { Link, useNavigate } from "react-router-dom";
import gsap from "gsap";

function ViewProduct() {
    const { categoryId, tenSanPham, chuoi } = useContext(Context);
    const [danhSachThuongHieu, setDanhSachThuongHieu] = useState([]);
    const [danhSachThongSo, setDanhSachThongSo] = useState([]);
    const [listProduct, setListProduct] = useState([]);
    const [trang, setTrang] = useState(0)
    const [tongPhanTu, setTongPhanTu] = useState(0)
    const [tongSoTrang, setTongSoTrang] = useState(0)
    const navigate = useNavigate()
    const productRefs = useRef([]);
    useEffect(() => {
    productRefs.current = []; // Clear lại ref tránh dư thừa
}, [listProduct]);

   useEffect(() => {
    if (listProduct.length === 0) return;

    requestAnimationFrame(() => {
        gsap.fromTo(
            productRefs.current,
            { opacity: 0, scale: 0.8 },
            {
                opacity: 1,
                scale: 1,
                duration: 0.4,
                stagger: 0.1,
                ease: "back.out(1.7)",
            }
        );
    });
}, [listProduct]);

    const [filter, setFilter] = useState({
        category: 0,
        tenSanPham: "",
        danhSachThongSo: [],
        giaBatDau: 0,
        giaKetThuc: 300000000,
        thuongHieuId: [],
        orderBy: "thapDenCao",
    });

    useEffect(() => {
        getByCondition(tenSanPham, categoryId)
            .then((data) => setDanhSachThuongHieu(data))
            .catch(() => toast.error("Lấy dữ liệu thất bại"));
        getThongSoForFilter(tenSanPham, categoryId)
            .then((data) => setDanhSachThongSo(data))
            .catch(() => toast.error("Lấy dữ liệu thất bại"));
    }, [tenSanPham, categoryId, trang]);

    const handleSortChange = (sortType) => {
        setFilter({ ...filter, orderBy: sortType });
    };

    const handleThongSoChange = (id) => {
        const currentIds = filter.danhSachThongSo || [];
        let updatedIds;

        if (currentIds.includes(id)) {
            updatedIds = currentIds.filter((v) => v !== id);
        } else {
            updatedIds = [...currentIds, id];
        }

        setFilter({
            ...filter,
            danhSachThongSo: updatedIds,
        });
    };

    const handleThuongHieuChange = (id) => {
        const currentIds = filter.thuongHieuId || [];
        let updatedIds;

        if (currentIds.includes(id)) {
            updatedIds = currentIds.filter((v) => v !== id);
        } else {
            updatedIds = [...currentIds, id];
        }

        setFilter({
            ...filter,
            thuongHieuId: updatedIds,
        });
    };
    useEffect(() => {
        getViewProduct({ ...filter, category: categoryId, tenSanPham: tenSanPham },trang).then(data => {
            setListProduct(data.data)
            setTongPhanTu(data.totalElement)
            setTongSoTrang(data.totalPage)
        })
    }, [filter, tenSanPham, categoryId, trang])
    return (
        <div>
            <style>
                {`
          .filter-section::-webkit-scrollbar {
            width: 4px; /* Thinner scrollbar */
          }
          .filter-section::-webkit-scrollbar-track {
            background: #14532d; /* Tailwind green-900 */
            border-radius: 2px;
          }
          .filter-section::-webkit-scrollbar-thumb {
            background: #14532d; /* Tailwind green-900 */
            border-radius: 2px;
          }
          .filter-section::-webkit-scrollbar-thumb:hover {
            background: #0f3d22; /* Slightly darker green for hover (custom, close to green-950) */
          }
          .custom-checkbox {
            cursor: pointer;
            width: 16px;
            height: 16px;
            appearance: none;
            border: 1px solid #d1d5db;
            border-radius: 3px;
            background-color: #fff;
            position: relative;
            transition: all 0.2s;
          }
          .custom-checkbox:checked {
            background-color: #f97316; /* Orange background */
            border-color: #f97316;
          }
          .custom-checkbox:checked::after {
            content: "✔";
            color: #fff; /* White checkmark */
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-size: 12px;
          }
          .custom-checkbox:hover {
            border-color: #f97316;
          }
        `}
            </style>

            <div className="flex mt-2 ml-3">
                {danhSachThuongHieu?.map((item) => (
                    <div key={item.id}>
                        <img
                            onClick={() => handleThuongHieuChange(item.id)}
                            className={`cursor-pointer ${filter.thuongHieuId.includes(item.id)
                                ? "border-green-900 border"
                                : ""
                                } rounded-md mr-2`}
                            src={item.anhDaiDien}
                            alt={item.tenThuongHieu}
                        />
                    </div>
                ))}
            </div>

            <div className="flex flex-row mt-5">
                <div className="basis-1/5 p-4 border-r border-gray-300">
                    <div className="mb-4">
                        <h4 className="font-semibold text-orange-500">KHOẢNG GIÁ </h4>
                        <div className="flex flex-row mt-2">
                            <input type="number" value={filter.giaBatDau} onChange={(e) => {
                                setFilter({
                                    ...filter,
                                    giaBatDau: parseInt(e.target.value)
                                })
                            }} className="outline-none border border-gray-200 mr-2 pl-3 w-[60%] text-gray-400"></input>
                            <input type="number" value={filter.giaKetThuc} onChange={(e) => {
                                setFilter({
                                    ...filter,
                                    giaKetThuc: parseInt(e.target.value)
                                })
                            }} className="outline-none border border-gray-200 pl-3 w-[60%] text-gray-400"></input>
                        </div>
                    </div>
                    {danhSachThongSo?.map((section) => (
                        <div className="mb-4" key={section.ten}>
                            <h4 className="font-semibold text-orange-500 mb-3">{section.ten}</h4>
                            <div className="filter-section max-h-[200px] overflow-y-auto pr-1">
                                {section.danhSachCon?.map((item) => (
                                    <label key={item.id} className="flex items-center mb-2">
                                        <input
                                            type="checkbox"
                                            checked={filter.danhSachThongSo.includes(item.id)}
                                            onChange={() => handleThongSoChange(item.id)}
                                            className="mr-2 custom-checkbox"
                                        />
                                        {item.ten}
                                    </label>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>

                <div className="basis-4/5">
                    <div className="flex mb-4">
                        {/* {chuoi!=""? <><h4 className="ml-5 font-bold mr-1 text-green-800">{chuoi.toUpperCase()} </h4> <span className="text-gray-600">({tongPhanTu} sản phẩm)</span></>:null} */}
                    </div>

                    <div className="flex justify-start items-center">

                        <p className="ml-3 pl-2 pr-2">Sắp xếp theo:</p>
                        <button
                            onClick={() => handleSortChange("thapDenCao")}
                            className={`ml-3 transition-all duration-150 pl-2 pr-2 ${filter.orderBy === "thapDenCao"
                                ? "text-green-900 border-b-2 border-green-900"
                                : "text-gray-700 hover:text-green-900"
                                }`}
                        >
                            Tăng dần
                        </button>
                        <button
                            onClick={() => handleSortChange("caoDenThap")}
                            className={`ml-3 transition-all duration-150 pl-2 pr-2 ${filter.orderBy === "caoDenThap"
                                ? "text-green-900 border-b-2 border-green-900"
                                : "text-gray-700 hover:text-green-900"
                                }`}
                        >
                            Giảm dần
                        </button>
                        
                    </div>
                    <div className="flex flex-wrap gap-5 px-5 mt-4">
                        {listProduct?.map((h, index) => (
                            <div
                                key={index}
                                className="w-[23%]"
                                ref={(el) => (productRefs.current[index] = el)}
                            >
                                <ProductItem product={h} />
                            </div>
                        ))}
                    </div>


                    <div className="mr-14 " ><Pagination color={"bg-green-900"} textColor="text-green-200" trangHienTai={trang} setTrangHienTai={setTrang} soLuongTrang={tongSoTrang}></Pagination></div>
                </div>
            </div>


        </div>
    );
}

export { ViewProduct };
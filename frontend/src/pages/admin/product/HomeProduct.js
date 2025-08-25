import { useEffect, useState } from "react";
import Modal from "../../../components/commons/modal";
import PickCategory from "../../../components/commons/PickCategory";
import { getManager, status } from "../../../services/sanPhamService";
import { Pagination } from "../../../components/commons/Pagination";
import { formatToVND } from "../../../utils/Format";
import { useNavigate } from "react-router-dom";

function HomeProduct() {
    const [openPickCategory, setOpenPickCategory] = useState(false);
    const [category, setcategoryPick] = useState([]);
    const [chuoi, setChuoi] = useState("");
    const [ten, setTen] = useState("");
    const [conSuDung, setConSuDung] = useState(true);
    const [hetHang, setHetHang] = useState(0);
    const [data, setData] = useState([]);
    const [trang, setTrang] = useState(0);
    const [tong, setTong] = useState(0)
    const [tongTrang, setTongTrang] = useState(0)
    const [load,setLoad]=useState(false)
    useEffect(() => {
        getManager(category?.[category?.length - 1]?.id || 0, trang, ten, conSuDung, hetHang).then(data => {
            setData(data.data)
            setTong(data.totalPages)
            setTongTrang(data.totalPages)
        });
    }, [hetHang, category, ten, trang, conSuDung]);
    const navigate = useNavigate()

    return (
        <div className="bg-gray-50">
            <p className="text-left   mt-4 mb-3 bg-white p-2 rounded-md">
                <strong className="text-lg">Quản lý sản phẩm</strong>
                <p className="">Quản lý danh sách sản phẩm bán tại cửa hàng</p>
            </p>

            <div className="bg-white p-2 rounded-md">
                <div className="mb-3">
                    <i class="fa-solid fa-filter mr-1 "></i>
                    <span className="font-bold">Bộ lọc:</span>
                </div>
                <div className="flex flex-wrap justify-between gap-4 mb-4">

                    <div className="flex-1 min-w-[200px]">
                        <div className=" mb-2">
                            <div className="mb-1">
                                <i class="fa-solid fa-list mr-1 text-green-500 bg-green-100 p-1 mr-2"></i>
                                <span >Ngành hàng: <i class="fa-solid fa-arrows-rotate text-blue-500 ml-1" onClick={()=>{
                                    setcategoryPick([])
                                }}></i></span>
                            </div>
                            
                        </div>
                        <input
                            readOnly
                            className="w-full outline-none border px-2 py-1 rounded cursor-pointer"
                            onClick={() => setOpenPickCategory(true)}
                            value={chuoi || "Tất cả ngành hàng"}
                        />
                    </div>

                    <div className="flex-1 min-w-[200px]">
                        <div className="mb-2">
                            <i class="fa-solid fa-boxes-stacked mr-1 text-pink-500 bg-pink-100 p-1 mr-2"></i>
                            <span >Tên sản phẩm:</span>
                        </div>
                        <input
                            value={ten}
                            onChange={(e) => setTen(e.target.value)}
                            className="w-full outline-none border px-2 py-1 rounded"
                            placeholder="Nhập tên sản phẩm..."
                        />
                    </div>

                    <div className="flex-1 min-w-[180px]">
                        <div className="mb-2">
                            <i class="fa-regular fa-circle mr-1 text-violet-400 bg-violet-100 p-1 mr-2"></i>
                            <span >Tình trạng hoạt động:</span>
                        </div>
                        <p
                            onClick={() => setConSuDung(!conSuDung)}
                            className={`p-1 ${conSuDung ? "bg-green-100 text-green-500" : "bg-gray-100 text-gray-400"}  rounded-md text-center cursor-pointer`}
                        >
                            <strong>Hoạt động</strong>
                        </p>
                    </div>

                    <div className="flex-1 min-w-[180px]">
                        <div className="mb-2 flex items-center">
                            <i className="fa-regular fa-circle mr-1 text-yellow-400 bg-yellow-100 p-1 mr-2"></i>
                            <label id="listbox-label" className="block text-sm font-medium text-gray-900">Tình trạng hàng</label>
                        </div>
                        <div className="relative">
                            <button
                                type="button"
                                className="grid w-full cursor-default grid-cols-1 rounded-md bg-white py-1.5 pr-2 pl-3 text-left text-gray-900 outline-none ring-1 ring-gray-300 focus:ring-0 sm:text-sm"
                                aria-haspopup="listbox"
                                aria-expanded="true"
                                aria-labelledby="listbox-label"
                                onClick={(e) => {
                                    const ul = e.currentTarget.nextElementSibling;
                                    ul.classList.toggle('hidden');
                                }}
                            >
                                <span className="col-start-1 row-start-1 block truncate pr-6">
                                    {[{ value: 0, label: 'Tất cả' }, { value: 2, label: 'Hết hàng' }, { value: 1, label: 'Sắp hết hàng' }].find(opt => opt.value === hetHang)?.label}
                                </span>
                                <svg
                                    className="col-start-1 row-start-1 size-5 self-center justify-self-end text-gray-500 sm:size-4"
                                    viewBox="0 0 16 16"
                                    fill="currentColor"
                                    aria-hidden="true"
                                    data-slot="icon"
                                >
                                    <path
                                        fillRule="evenodd"
                                        d="M5.22 10.22a.75.75 0 0 1 1.06 0L8 11.94l1.72-1.72a.75.75 0 1 1 1.06 1.06l-2.25 2.25a.75.75 0 0 1-1.06 0l-2.25-2.25a.75.75 0 0 1 0-1.06ZM10.78 5.78a.75.75 0 0 1-1.06 0L8 4.06 6.28 5.78a.75.75 0 0 1-1.06-1.06l2.25-2.25a.75.75 0 0 1 1.06 0l2.25 2.25a.75.75 0 0 1 0 1.06Z"
                                        clipRule="evenodd"
                                    />
                                </svg>
                            </button>
                            <ul
                                className="absolute z-10 mt-1 max-h-56 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black/5 hidden sm:text-sm"
                                tabIndex="-1"
                                role="listbox"
                                aria-labelledby="listbox-label"
                            >
                                {[
                                    { value: 0, label: 'Tất cả' },
                                    { value: 2, label: 'Hết hàng' },
                                    { value: 1, label: 'Sắp hết hàng' },
                                ].map((option, index) => (
                                    <li
                                        key={option.value}
                                        className={`relative cursor-pointer py-2 pr-9 pl-3 text-gray-900 select-none hover:bg-indigo-100 hover:text-indigo-600 ${hetHang === option.value ? 'bg-indigo-100 text-indigo-600' : ''}`}
                                        id={`listbox-option-${index}`}
                                        role="option"
                                        onClick={() => {
                                            setHetHang(option.value);
                                            document.querySelector('ul[role="listbox"]').classList.add('hidden');
                                        }}
                                    >
                                        <span className={`block truncate ${hetHang === option.value ? 'font-semibold' : 'font-normal'}`}>
                                            {option.label}

                                        </span>
                                        {hetHang === option.value && (
                                            <span className="absolute inset-y-0 right-0 flex items-center pr-4 text-indigo-600">
                                                <svg
                                                    className="size-5"
                                                    viewBox="0 0 20 20"
                                                    fill="currentColor"
                                                    aria-hidden="true"
                                                    data-slot="icon"
                                                >
                                                    <path
                                                        fillRule="evenodd"
                                                        d="M16.704 4.153a.75.75 0 0 1 .143 1.052l-8 10.5a.75.75 0 0 1-1.127.075l-4.5-4.5a.75.75 0 0 1 1.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 0 1 1.05-.143Z"
                                                        clipRule="evenodd"
                                                    />
                                                </svg>
                                            </span>
                                        )}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                </div>


            </div>
            <div className=" bg-white mt-3 rounded-md">
                <strong><p className="p-2">Danh sách sản phẩm: ({tong})</p></strong>
                <table className="w-full border-collapse table-auto">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="p-2 border w-7/25">Sản Phẩm</th>
                            <th className="p-2 border">Phân loại</th>
                            <th className="p-2 border">Giá</th>
                            <th className="p-2 border">Số lượng tại cửa hàng</th>
                            <th className="p-2 border">Số lượng đã bán (đặt)</th>
                            <th className="p-2 border">Lượng hao dự kiến</th>
                            
                        </tr>
                    </thead>
                    <tbody>
                        {data?.map((d, i) => {
                            const first = d?.bienThe?.[0];
                            const rest = d?.bienThe?.slice(1) || [];

                            return (
                                <>
                                    <tr key={`sp-${i}-0`} className="border">
                                        <td className="p-2 border" rowSpan={d?.bienThe?.length}>
                                            <div className="flex items-center gap-2">
                                                <img
                                                    onClick={() => navigate("update?id=" + d.id)}
                                                    className="w-[40px] h-[40px] object-cover cursor-pointer rounded"
                                                    src={d.anhGioiThieu}
                                                    alt=""
                                                />

                                                <div>
                                                    {
                                                        d.conSuDung==false  ?<p onClick={()=>{
                                                            status(d?.id,1).then(()=>{
                                                                d.conSuDung=true;
                                                                setLoad(!load);
                                                            }).catch(()=>{})
                                                        }} className="text-red-500 w-fit bg-red-100 pl-1 pr-1 rounded-sm cursor-pointer">
                                                        <i class="fa-solid fa-circle-stop mr-1"></i>
                                                        Mở bán
                                                    </p>
                                                    : <p onClick={()=>{
                                                            status(d?.id,0).then(()=>{
                                                                d.conSuDung=false;
                                                                setLoad(!load);
                                                            }).catch(()=>{})
                                                        }} className="text-green-700 w-fit bg-green-100 pl-1 pr-1 rounded-sm cursor-pointer">
                                                        <i class="fa-solid fa-circle-stop mr-1"></i>
                                                        Ngưng bán
                                                    </p>
                                                    }
                                                    
                                                    <p
                                                        className="font-medium hover:text-blue-300 cursor-pointer"
                                                        onClick={() => navigate("thongke?id=" + d.id)}
                                                    >
                                                        {d.ten}
                                                    </p>
                                                </div>

                                            </div>
                                        </td>
                                        <td className="p-2 border">
                                            <div className="relative">
                                                {first.notUpdate ? (
                                                    <div className="border border-red-500 absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[1px] bg-white bg-opacity-60 rounded">
                                                        <span className="text-red-500 font-semibold">Không còn sử dụng</span>
                                                    </div>
                                                ) : null}
                                                <div className="flex items-center gap-2">
                                                    <img
                                                        className="w-[40px] h-[40px] object-cover rounded"
                                                        src={first.anhGioiThieu}
                                                        alt=""
                                                    />
                                                    <p>{first.ten}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="p-2 border">{formatToVND(first.gia)}</td>
                                        <td className="p-2 border">
                                            <div className="flex flex-row">
                                                <span className="basis-1/6 font-bold">{first.soLuongKho}</span>
                                                {first.soLuongKho <= 25 && first.soLuongKho > 0 ? (
                                                    <span className="bg-yellow-100 p-1 pl-2 pr-2 rounded-sm text-yellow-500">
                                                        <strong>Sắp hết hàng</strong>
                                                    </span>
                                                ) : null}
                                                {first.soLuongKho <= 0 ? (
                                                    <span className="bg-red-100 p-1 pl-2 pr-2 rounded-sm text-red-500">
                                                        <strong>Hết hàng</strong>
                                                    </span>
                                                ) : null}
                                                {first.soLuongKho > 25 ? (
                                                    <span className="bg-green-100 p-1 pl-2 pr-2 rounded-sm text-green-500">
                                                        <strong>Còn hàng</strong>
                                                    </span>
                                                ) : null}
                                            </div>
                                        </td>
                                        <td className="justify-center font-bold text-center">{first?.daBan}</td>
                                        <td className="justify-center font-bold text-center">{first?.haoDuKien}</td>
                                        
                                    </tr>

                                    {rest.map((f, index) => (
                                        <tr key={`sp-${i}-${index + 1}`} className="border">
                                            <td className="p-2 border">
                                                <div className="relative">
                                                    {f.notUpdate ? (
                                                        <div className="border border-red-500 absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[1px] bg-white bg-opacity-60 rounded">
                                                            <span className="text-red-500 font-semibold">Không còn sử dụng</span>
                                                        </div>
                                                    ) : null}
                                                    <div className="flex items-center gap-2">
                                                        <img
                                                            className="w-[40px] h-[40px] object-cover rounded"
                                                            src={f.anhGioiThieu}
                                                            alt=""
                                                        />
                                                        <p>{f.ten}</p>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="p-2 border">{formatToVND(f.gia)}</td>
                                            <td className="p-2 border">
                                                <div className="flex flex-row">
                                                    <span className="basis-1/6 font-bold">{f.soLuongKho}</span>
                                                    {f.soLuongKho <= 25 && f.soLuongKho > 0 ? (
                                                        <span className="bg-yellow-100 p-1 pl-2 pr-2 rounded-sm text-yellow-500">
                                                            <strong>Sắp hết hàng</strong>
                                                        </span>
                                                    ) : null}
                                                    {f.soLuongKho <= 0 ? (
                                                        <span className="bg-red-100 p-1 pl-2 pr-2 rounded-sm text-red-500">
                                                            <strong>Hết hàng</strong>
                                                        </span>
                                                    ) : null}
                                                    {f.soLuongKho > 25 ? (
                                                        <span className="bg-green-100 p-1 pl-2 pr-2 rounded-sm text-green-500">
                                                            <strong>Còn hàng</strong>
                                                        </span>
                                                    ) : null}
                                                </div>
                                            </td>
                                            <td className="justify-center font-bold text-center">{f?.daBan}</td>
                                            <td className="justify-center font-bold text-center border-l-1">{f?.haoDuKien}</td>

                                            
                                        </tr>
                                    ))}
                                </>
                            );
                        })}
                        {
                            data?.length==0?<tr className="text-center">
                                <td colSpan={6}>
                                    <div className="w-full flex justify-center">
                                        <img
                                            className="w-44"
                                            src="https://media.istockphoto.com/id/861576608/vi/vec-to/bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-empty-shopping-bag-m%E1%BA%ABu-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-vector-online-business.jpg?s=612x612&w=0&k=20&c=-6p0qVyb_p8VeHVu1vJVaDHa6Wd_mCjMkrFBTlQdehI="
                                            alt="Giỏ hàng trống"
                                        />
                                    </div>
                                </td>
                                
                            </tr>:null
                        }
                    </tbody>
                </table>
                <div className="flex justify-between items-center mt-6">
                    <div></div>
                    <Pagination
                        trangHienTai={trang}
                        setTrangHienTai={setTrang}
                        soLuongTrang={tong}
                    />
                </div>
            </div>
            {openPickCategory && (
                <Modal setOpen={setOpenPickCategory}>
                    <PickCategory
                        categoryPick={category}
                        setcategoryPick={setcategoryPick}
                        setChuoi={setChuoi}
                        setOpen={setOpenPickCategory}
                    />
                </Modal>
            )}
        </div>
    );
}

export { HomeProduct };
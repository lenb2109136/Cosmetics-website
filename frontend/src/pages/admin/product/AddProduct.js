import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import Modal from "../../../components/commons/modal"
import PickCategory from "../../../components/commons/PickCategory"
import { useEffect, useRef, useState } from "react";
import { PickClassify } from '../../../components/admin/PickClassify';
import { checkSubmit, Product } from '../../../models/classify';
import PickDetailCategory from "../../../components/commons/PickDetailCategory"
import { getThuongHieu } from '../../../services/ThuongHieuService';
import { saveSanPham } from '../../../services/sanPhamService';
import { message } from 'antd';
import { toast } from 'react-toastify';
import Loading from '../../../components/commons/Loading';
import { getAllDongGoi } from '../../../services/trangthaiservice';
export default function AddProduct() {
    const [tenPhanLoai, setTenPhanLoai] = useState("")
    const [images, setImages] = useState([]);
    const [coverImage, setCoverImage] = useState(null);
    const [imagevariant, setimagevariant] = useState([])
    const [open, setOpen] = useState(false)
    const [opendetailcategory, setopendetailcategory] = useState(true)
    const [openloadin, setopenloading] = useState(false)
    const [danhSachQuyCachDongGoi, setDanhSachQuyCachDongGoi] = useState([])
    const [variantPick, setVariantPick] = useState({})
    const [openDongGoi, setOpenDongGoi] = useState(false)
    const maxImages = 9;
    //thông tin sản phẩm - mô tả
    const [cachDung, setCachDung] = useState('<p>Cách dùng...</p>');
    //nhóm ngành hàng
    const [danhSachNganhHang, setDanhSachNganhHang] = useState([])
    const [nhomHang, setNhomHang] = useState("");
    //phân loại hàng 
    const [product, setProduct] = useState(new Product())

    //thương hiệu
    const [thuonghieu, setthuonghieu] = useState([])
    if (product?.bienTheKhongLe?.length != 0) {
        product.dongGoiNhap = []
        product.maVach = ""
    }
    //load
    const [load, setLoad] = useState(false)
    useEffect(() => {
        getThuongHieu().then((data) => {
            setthuonghieu(data)
        }).catch(() => {

        })

        getAllDongGoi().then(d => {
            setDanhSachQuyCachDongGoi(d)
        }).catch(() => { })

    }, [])
    const handleImageChange = (e) => {
        const files = Array.from(e.target.files);
        const newImages = files.map(file => ({
            file,
            url: URL.createObjectURL(file)
        }));

        if (images.length + newImages.length > maxImages) {
            toast.warning("Tối đa 9 ảnh cho ảnh giới thiệu")
            return;
        }

        setImages(prev => [...prev, ...newImages]);
    };
    //thông số loại
    useEffect(() => {
        if (danhSachNganhHang?.length == 0) {
            product.thongSo = []
            setopendetailcategory(false)
        }
        else {
            if (product.danhMuc != danhSachNganhHang[danhSachNganhHang.length - 1].id) {
                product.danhMuc = danhSachNganhHang[danhSachNganhHang.length - 1].id;
                product.thongSo = []
                setopendetailcategory(true)
                setLoad(!load)
            }
        }
    }, [danhSachNganhHang])

    const handleRemove = (index) => {
        const updated = [...images];
        updated.splice(index, 1);
        setImages(updated);
    };

    const handleCoverChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const img = new Image();
        const url = URL.createObjectURL(file);

        img.onload = () => {
            setCoverImage({ file, url });

        };

        img.src = url;
    };

    return (
        <div className="p-4 w-[100%]">
            {openloadin ? <Loading></Loading> : null}
            <strong><h3 className="text-2xl">Thông tin cơ bản</h3></strong>

            {/* Ảnh sản phẩm */}
            <div className='flex flex-row w-[100%]'>
                <div className='basis-3/5 bg-white mr-2 p-3'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {
                            images?.length != 0 ? <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                            </div> : <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                            </div>

                        }
                        <strong><p className="mt-0">Hình ảnh sản phẩm </p></strong>
                    </div>

                    <div className="flex flex-wrap gap-4">
                        {images.map((img, index) => (
                            <div key={index} className="relative group w-24 h-24 border rounded overflow-hidden">
                                <img src={img.url} alt={`img-${index}`} className="object-cover w-full h-full" />
                                <button
                                    onClick={() => handleRemove(index)}
                                    className="absolute top-1 right-1 bg-black bg-opacity-50 text-white p-1 rounded-full opacity-0 group-hover:opacity-100 transition"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                        ))}

                        {images.length < maxImages && (
                            <label className="w-24 h-24 border-2 border-dashed border-red-500 bg-red-50 rounded-lg flex flex-col items-center justify-center text-red-500 cursor-pointer hover:shadow hover:shadow-red-300 transition">
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
                                </svg>
                                <p className="text-xs mt-1 text-center">Thêm hình</p>
                                <input type="file" accept="image/*" multiple className="hidden" onChange={handleImageChange} />
                            </label>
                        )}
                    </div>
                    <p className="text-sm text-gray-500 mt-2">Đã chọn: {images.length}/9 ảnh</p>
                </div>

                {/* Ảnh bìa */}
                <div className='basis-2/3 bg-white  p-3'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {
                            coverImage != null ? <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                            </div> : <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                            </div>

                        }
                        <strong><p className="mt-0">Ảnh bìa</p></strong>
                    </div>

                    <div className="flex flex-row items-start gap-4">
                        {coverImage ? (
                            <div className="relative w-24 h-24 border rounded overflow-hidden group">
                                <img src={coverImage.url} alt="cover" className="object-cover w-full h-full" />
                                <button
                                    onClick={() => setCoverImage(null)}
                                    className="absolute top-1 right-1 bg-black bg-opacity-50 text-white p-1 rounded-full opacity-0 group-hover:opacity-100 transition"
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                        ) : (
                            <label className="w-24 h-24 border-2 border-dashed border-blue-500 bg-blue-50 rounded-lg flex flex-col items-center justify-center text-blue-500 cursor-pointer hover:shadow hover:shadow-blue-300 transition">
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
                                </svg>
                                <p className="text-xs mt-1 text-center">Thêm ảnh bìa</p>
                                <input type="file" accept="image/*" className="hidden" onChange={handleCoverChange} />
                            </label>
                        )}

                        <ul className="text-sm text-gray-600 list-disc list-inside max-w-md">
                            <li>Tải lên hình ảnh có tỷ lệ 1:1 (vuông).</li>
                            <li>Ảnh bìa sẽ hiển thị ở trang Kết quả tìm kiếm, Gợi ý hôm nay,...</li>
                            <li>Sử dụng ảnh đẹp để thu hút người xem.</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div className='flex flex-row'>
                <div className='basis-3/5 bg-white mr-2 p-3 rounded-md mt-2'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {
                            product.ten != "" ? <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                            </div> : <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                            </div>

                        }

                        <strong><p className="mt-0">Tên sản phẩm</p> </strong>
                    </div>

                    <input
                        onChange={(event) => {
                            product.ten = event.target.value;
                            setLoad(!load)
                        }}
                        class="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 
                                    hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                        type="text">

                    </input>
                </div>
                
                <div className='basis-1/5 bg-white  p-3 mt-2'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {
                            product.thuongHieu != 0 ? <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                            </div> : <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                            </div>

                        }
                        <strong><p className="mt-0">Thương hiệu</p></strong>
                    </div>
                    <div className="w-full max-w-md mt-5">
                        <select
                            className="cursor-pointer w-full p-3 border border-gray-300 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
                            value={product.thuongHieu || ''}
                            onChange={(e) => {
                                product.thuongHieu = parseInt(e.target.value);
                                setLoad(!load);
                            }}
                        >
                            {
                                thuonghieu.map((data) => (
                                    <option key={data.id} value={data.id}>
                                        {data.ten}
                                    </option>
                                ))
                            }
                        </select>
                    </div>
                </div>
                <div className='basis-1/5 bg-white  p-3 mt-2'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {
                            product.thue > 0 ? <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                            </div> : <div className="relative">
                                <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                            </div>

                        }

                        <strong><p className="mt-0">Thuế VAT</p> </strong>
                    </div>

                    <input
                        onChange={(event) => {
                            product.thue = parseInt(event.target.value);
                            setLoad(!load)
                        }}
                        class="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 
                                        hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                        type="number">

                    </input>
                </div>
            </div>

           <div className='flex flex-row items-end justify-start'>
    {/* Tính số div con sẽ hiển thị */}
    {(() => {
        let count = 1; // mặc định có nhóm sản phẩm
        if (product?.bienTheKhongLe?.length <= 0) count += 2; // có giá mặc định + mã vạch
        else if (product?.bienTheKhongLe?.length === 0) count += 1; // trường hợp khác

        // Chia basis động
        let groupBasis;
        if (count === 3) groupBasis = 'basis-3/5';
        else if (count === 2) groupBasis = 'basis-4/5';
        else groupBasis = 'basis-full';

        return (
            <>
                {/* Nhóm sản phẩm */}
                <div className={`${groupBasis} bg-white p-3 mt-2 mr-2 rounded-md`}>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {
                            product.danhMuc !== 0 ? (
                                <div className="relative">
                                    <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                    <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                                </div>
                            ) : (
                                <div className="relative">
                                    <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                    <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                                </div>
                            )
                        }
                        <strong><p className="mt-0">Nhóm sản phẩm</p></strong>
                    </div>
                    <input
                        value={nhomHang}
                        onClick={() => setOpen(true)}
                        className="cursor-pointer rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 
                        hover:border-blue-300 focus:border-blue-300 focus:outline-none font-bold"
                        type="text"
                    />
                </div>

                {/* Giá mặc định */}
                {product?.bienTheKhongLe?.length <= 0 &&
                    <div className={`${count === 3 ? 'basis-1/5' : count === 2 ? 'basis-1/5' : ''} bg-white p-3 mt-2`}>
                        <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                            {
                                product.gia > 0 ? (
                                    <div className="relative">
                                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                        <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                                    </div>
                                ) : (
                                    <div className="relative">
                                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                        <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                                    </div>
                                )
                            }
                            <strong><p className="mt-0">Giá mặc định</p></strong>
                        </div>
                        <input
                            onChange={(event) => {
                                product.gia = parseInt(event.target.value);
                                setLoad(!load);
                            }}
                            className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 
                            hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                            type="number"
                        />
                    </div>
                }
                {product?.bienTheKhongLe?.length <= 0 &&
                    <div className={`${count === 3 ? 'basis-1/5' : count === 2 ? 'basis-1/5' : ''} bg-white p-3 mt-2`}>
                        <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                            {
                                product.khoiLuong > 0 ? (
                                    <div className="relative">
                                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                        <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                                    </div>
                                ) : (
                                    <div className="relative">
                                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                        <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                                    </div>
                                )
                            }
                            <strong><p className="mt-0">Khối lượng</p></strong>
                        </div>
                        <input
                            onChange={(event) => {
                                product.khoiLuong = parseInt(event.target.value);
                                setLoad(!load);
                            }}
                            className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 
                            hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                            type="number"
                        />
                    </div>
                }

                {/* Mã vạch */}
                {product?.bienTheKhongLe?.length <= 0 &&
                    <div className={`${count === 3 ? 'basis-1/5' : count === 2 ? 'basis-1/5' : ''} bg-white p-3 mt-2`}>
                        <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                            {
                                product.ten !== "" ? (
                                    <div className="relative">
                                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                                        <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                                    </div>
                                ) : (
                                    <div className="relative">
                                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                                        <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                                    </div>
                                )
                            }
                            <strong><p className="mt-0">Mã vạch</p></strong>
                        </div>
                        <input
                            onChange={(event) => {
                                product.maVach = event.target.value;
                                setLoad(!load);
                            }}
                            className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 
                            hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                            type="text"
                        />
                    </div>
                }
            </>
        );
    })()}
</div>

            <div className=" mb-5 bg-white  p-3 mt-2">
                <div className="flex flex-row items-center space-x-2">
                    {product.moTa ? (
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                        </div>
                    ) : (
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                        </div>
                    )}
                    <strong>
                        <p className="mt-0 mb-3">Mô tả sản phẩm</p>
                    </strong>
                </div>

                <div className="max-h-[350px] overflow-y-auto border border-gray-300">
                    <CKEditor
                        editor={ClassicEditor}
                        data={product.moTa}
                        config={{
                            placeholder: "Nhập mô tả sản phẩm...",
                        }}
                        onChange={(event, editor) => {
                            const newData = editor.getData();
                            product.moTa = newData;
                            setLoad(!load);
                        }}
                    />
                </div>
            </div>

            {open ? <Modal setOpen={setOpen} ><PickCategory setOpen={setOpen} categoryPick={danhSachNganhHang} setChuoi={setNhomHang} setcategoryPick={setDanhSachNganhHang}></PickCategory></Modal> : null}
            <div className=" mb-5 bg-white  p-3 mt-2">
                <div className="flex flex-row items-center space-x-2">
                    {product.thanhPhan ? (
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                        </div>
                    ) : (
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                        </div>
                    )}
                    <strong>
                        <p className="mt-0 mb-3">Thành phần sản phẩm</p>
                    </strong>
                </div>

                <div className="max-h-[350px] overflow-y-auto border border-gray-300">
                    <CKEditor
                        editor={ClassicEditor}
                        data={product.thanhPhan}
                        config={{
                            placeholder: "Nhập thành phần sản phẩm...",
                        }}
                        onChange={(event, editor) => {
                            const newData = editor.getData();
                            product.thanhPhan = newData;
                            setLoad(!load);
                        }}
                    />
                </div>
            </div>
            <div className=" mb-5 bg-white  p-3 mt-2">
                <div className="flex flex-row items-center space-x-2">
                    {product.cachDung ? (
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                        </div>
                    ) : (
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-red-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-red-500"></span>
                        </div>
                    )}
                    <strong>
                        <p className="mt-0 mb-3">Cách dùng</p>
                    </strong>
                </div>

                <div className="max-h-[350px] overflow-y-auto border border-gray-300">
                    <CKEditor
                        editor={ClassicEditor}
                        data={product.cachDung}
                        config={{
                            placeholder: "Nhập cách dùng...",
                        }}
                        onChange={(event, editor) => {
                            const newData = editor.getData();
                            product.cachDung = newData;
                            setLoad(!load);
                        }}
                    />
                </div>
            </div>

            {
                product?.bienTheKhongLe?.length <= 0 ? <div className="mt-7 mb-5">
                    <div className="flex flex-row items-center space-x-2 bg-white  p-3 mt-2">
                        <strong>
                            <p onClick={() => {
                                setVariantPick(product)
                                console.log(product)
                                setOpenDongGoi(true)
                            }} className="mt-0 mb-3 cursor-pointer"><i class="fa-solid fa-list-ul p-2 text-yellow-500 bg-yellow-100"></i> Quy cách nhập hàng</p>
                        </strong>
                    </div>
                </div> : null
            }

            <div className='bg-white  p-3 mt-2'>
                <div >
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                        </div>
                        <strong><p className="mt-0">Phân loại hàng</p></strong>
                        <i>( * lưu ý nếu không có phân loại hàng thì phải cung cấp giá gốc cho sản phẩm - phân loại mặc định )</i>
                    </div>
                    <div className='flex flex-row'>
                        <button onClick={() => {
                            let id = product?.classify?.length;
                            let bienThe = {
                                ten: tenPhanLoai,
                                gia: 0,
                                dongGoiNhap: []
                            }
                            const newClassify = [...(product.bienTheKhongLe || []), bienThe];
                            const pr = new Product();
                            Object.assign(pr, product);
                            pr.bienTheKhongLe = newClassify;
                            console.log(pr)
                            setProduct(pr);

                        }} class="mb-4 flex items-center justify-center gap-2 px-4 py-2 border border-dashed border-gray-400 text-red-500 rounded hover:bg-red-100">
                            <span class="text-xl">+</span> Thêm phân loại
                        </button>
                        <div className="basis-3/4 ml-3">
                            <input placeholder='Nhập tên phân loại nếu có' class value={tenPhanLoai} onChange={(e) => {
                                setTenPhanLoai(e.target.value)

                            }} type="text" className="cursor-pointer rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 
             hover:border-blue-300 focus:border-blue-300 focus:outline-none "></input>
                        </div>
                    </div>

                </div>
                <table className="min-w-full border border-gray-400 rounded-lg overflow-hidden text-sm mt-4">
                    <thead className="bg-blue-100 text-gray-700 uppercase tracking-wide">
                        <tr className="border border-gray-300">
                            <th className="px-4 py-2 border-r text-center border border-gray-300">Ảnh phân loại</th>
                            <th className="px-4 py-2 border-r text-center border border-gray-300">Tên phân loại</th>
                            <th className="px-4 py-2 border-r text-center border border-gray-300">Mã vạch</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Giá</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Trọng lượng</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Quy cách nhập</th>
                            <th className="px-4 py-2 text-center border border-gray-300">thao tác</th>
                        </tr>
                    </thead>
                    <tbody className="text-gray-800">
                        {
                            product?.bienTheKhongLe?.map((data, index) => {
                                return <tr
                                    className="group odd:bg-white even:bg-gray-50 hover:bg-gray-100 transition relative"
                                >

                                    <td
                                        className="px-4 py-3 border border-gray-300 border-r text-center align-middle font-medium text-gray-600 relative"
                                    >


                                        {imagevariant[index] && (
                                            <div className="mb-2 relative group">

                                                <button
                                                    type="button"
                                                    className="absolute top-0 right-0 bg-white bg-opacity-80 rounded-full text-red-600 hover:text-red-800 px-1 text-xs"
                                                    onClick={() => {
                                                        const newImages = [...imagevariant];
                                                        newImages[index] = null;
                                                        setimagevariant(newImages);
                                                    }}
                                                >
                                                    ✕
                                                </button>
                                            </div>
                                        )}

                                        <label className=" mx-auto relative w-24 h-24 border-2 border-dashed border-red-500 bg-red-50 rounded-lg flex flex-col items-center justify-center text-red-500 cursor-pointer hover:shadow hover:shadow-red-300 transition overflow-hidden">
                                            {imagevariant[index] ? (
                                                <img
                                                    src={URL.createObjectURL(imagevariant[index])}
                                                    alt="preview"
                                                    className="absolute inset-0 w-full h-full object-cover rounded-lg"
                                                />
                                            ) : (
                                                <>
                                                    <svg xmlns="http://www.w3.org/2000/svg" className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
                                                    </svg>
                                                    <p className="text-xs mt-1 text-center">Thêm hình</p>
                                                </>
                                            )}
                                            <input
                                                type="file"
                                                accept="image/*"
                                                onChange={(e) => {
                                                    const file = e.target.files[0];
                                                    if (file) {
                                                        const newImages = [...imagevariant];
                                                        newImages[index] = file;
                                                        setimagevariant(newImages);
                                                    }
                                                }}
                                                className="hidden"
                                            />
                                        </label>


                                    </td>
                                    <td className='text-center '>
                                        <input type='text' className='pl-3 outline-none border boder-gray-200 rounded-sm h-10 rounded-lg' 
                                        value={data.ten} onChange={(e) => {
                                        data.ten = e.target.value;
                                        setLoad(!load);
                                    }}></input></td>
                                    <td className='text-center '>
                                        <input
                                            type='text'
                                            className='pl-3 outline-none border border-gray-200 rounded-sm h-10 rounded-lg'
                                            value={data.maVach}
                                            onChange={(e) => {
                                                const newBarcode = e.target.value.trim();

                                                if (!newBarcode) {
                                                    data.maVach = "";
                                                    setLoad(prev => !prev);
                                                    return;
                                                }

                                                const isDuplicateInOtherBienThe = product.bienTheKhongLe.some(
                                                    bienThe => bienThe !== data && bienThe.maVach === newBarcode
                                                );

                                                const isDuplicateInDongGoi = product.bienTheKhongLe.some(bienThe =>
                                                    bienThe.dongGoiNhap?.some(dg => dg.maVach === newBarcode)
                                                );

                                                const hasDuplicateBarcode = isDuplicateInOtherBienThe || isDuplicateInDongGoi;

                                                if (hasDuplicateBarcode) {
                                                    data.maVach = newBarcode.slice(0, -1);
                                                    toast.error("Mã vạch này đã tồn tại!");
                                                    setLoad(prev => !prev);
                                                    return;
                                                }

                                                data.maVach = newBarcode;
                                                setLoad(prev => !prev);
                                            }}
                                            placeholder="Nhập mã vạch"
                                        />

                                    </td>

                                    <td className='text-center '><input min={1} className='pl-3 h-10 rounded-lg outline-none border boder-gray-200 rounded-sm' type='number' value={data.gia} onChange={(event) => {
                                        data.gia = parseInt(event.target.value);
                                        setLoad(!load)
                                    }}  ></input></td>
                                     <td className='text-center '><input min={1} className='pl-3 h-10 rounded-lg outline-none border boder-gray-200 rounded-sm' type='number' value={data.khoiLuong} onChange={(event) => {
                                        data.khoiLuong = parseInt(event.target.value);
                                        setLoad(!load)
                                    }}  ></input></td>
                                    <td className='text-center'>
                                        <button className='cursor-pointer p-2 text-yellow-500 b rounded-sm  bg-yellow-100' onClick={() => {
                                            setVariantPick(data)
                                            setOpenDongGoi(true)
                                        }}><i class="fa-solid fa-square-pen"></i> Điều chỉnh</button>
                                    </td>
                                    <td className='text-center'><button className='cursor-pointer p-2 text-red-500 b rounded-sm  bg-red-100' onClick={() => {
                                        product?.bienTheKhongLe?.splice(index, 1)
                                        imagevariant.splice(index, 1)
                                        setLoad(!load)
                                    }}><i class="fa-solid fa-trash"></i> Xóa</button></td>

                                </tr>
                            })

                        }
                        {product?.bienTheKhongLe?.length == 0 ? <td colSpan={6}><p className='text-center mt-3 bg-gray-50'><i>Chưa thêm phân loại nào</i></p></td> : null}
                    </tbody>
                </table>

            </div>
            {opendetailcategory ? <div>
                <div className="flex flex-row items-center space-x-2 mt-10 mb-5">
                    <div className="relative">
                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                        <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                    </div>
                    <strong><p className="mt-0">Thông số bổ sung</p></strong>
                </div>
                <PickDetailCategory l={load} setl={setLoad} product={product} open={open} id={product.danhMuc}></PickDetailCategory>
            </div> : null}


            <button
                onClick={() => {
                    let t = checkSubmit(product, coverImage, images, imagevariant);
                    if (t.test == false) {
                        toast.warning(t.message);
                    } else {
                        setopenloading(true)
                        console.log(product)
                        saveSanPham(product, images, coverImage, imagevariant)
                            .then(() => {
                                setopenloading(false)
                                toast.success("Thêm sản phẩm thành công");
                            })
                            .catch((error) => {
                                setopenloading(false)
                                toast.error(error?.response?.data?.message || "Lưu thông tin thất bại");
                            });
                    }
                }}
                className="mt-3 px-6 py-2 bg-gradient-to-r from-red-500 to-pink-500 text-white font-semibold rounded-xl shadow-md hover:shadow-lg transition-all duration-300 ease-in-out hover:brightness-110 active:scale-95"
            >
                Thêm sản phẩm
            </button>
            {openDongGoi ? (
                <Modal setOpen={setOpenDongGoi} b={true}>
                    <div className="space-y-6 p-4 bg-white rounded-xl ">
                        <p className="text-lg font-semibold text-gray-800">Tạo quy cách đóng gói cho sản phẩm</p>

                        {/* Packaging Options */}
                        <div className="flex flex-wrap gap-4">
                            {danhSachQuyCachDongGoi?.map((d) => (
                                <div
                                    key={d?.id}
                                    onClick={() => {
                                        const existing = variantPick?.dongGoiNhap?.filter(item => item.id === d.id) || [];
                                        let initialSoLuong = 2;

                                        // Tìm số lượng đầu tiên không bị trùng
                                        while (existing.some(item => item.soLuong === initialSoLuong)) {
                                            initialSoLuong++;
                                        }

                                        variantPick?.dongGoiNhap.push({
                                            id: d?.id,
                                            tenQuyCach: d?.tenQuyCach,
                                            soLuong: initialSoLuong,
                                            maVach: "",
                                        });

                                        setLoad(!load);
                                    }}
                                    className="flex flex-col items-center p-3 bg-gray-50 rounded-lg shadow hover:shadow-md transition-shadow"
                                >
                                    <p className="text-sm font-medium text-gray-600">{d?.tenQuyCach}</p>
                                    {/* <img className="w-10 h-10 object-cover rounded mt-2" src={d?.duongDan} alt={d?.tenQuyCach} /> */}
                                </div>
                            ))}
                        </div>

                        {/* Packaging Table */}
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-gray-100 border">
                                    <th className="p-3 text-sm font-semibold text-gray-600">STT</th>
                                    <th className="p-3 text-sm font-semibold text-gray-600">Tên quy cách</th>
                                    <th className="p-3 text-sm font-semibold text-gray-600">Số lượng đóng gói</th>
                                    <th className="p-3 text-sm font-semibold text-gray-600">Mã vạch</th>
                                    <th className="p-3 text-sm font-semibold text-gray-600">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                {variantPick?.dongGoiNhap?.map((m, ind) => (
                                    <tr key={ind} className="border-b hover:bg-gray-50">
                                        <td className="p-3 text-sm text-gray-600">{ind + 1}</td>
                                        <td className="p-3 text-sm text-gray-600">{m?.tenQuyCach}</td>
                                        <td className="p-3">
                                            <input
                                                type="number"
                                                className="w-20 h-20 p-1 border rounded outline-none text-center"
                                                value={m.soLuong}
                                                onChange={(e) => {
                                                    const newValue = parseInt(e.target.value);

                                                    if (isNaN(newValue) || newValue < 2) {
                                                        window.alert("Số lượng phải lớn hơn hoặc bằng 2!");
                                                        setLoad(!load); // render lại để trả về giá trị cũ
                                                        return;
                                                    }

                                                    const hasDuplicate = variantPick.dongGoiNhap.some(
                                                        (item, idx) =>
                                                            idx !== ind &&
                                                            item.id === m.id &&
                                                            item.soLuong === newValue
                                                    );

                                                    if (hasDuplicate) {
                                                        window.alert("Số lượng này đã tồn tại cho quy cách đóng gói này!");
                                                        setLoad(!load); // render lại để trả về giá trị cũ
                                                        return;
                                                    }

                                                    m.soLuong = newValue;
                                                    setLoad(!load); // cập nhật giá trị mới
                                                }}
                                                min="2"
                                            />

                                        </td>
                                        <td className="p-3">
                                            <input
                                                type="text"
                                                className="w-32 p-1 border rounded outline-none"
                                                value={m.maVach}
                                                onChange={(e) => {
                                                    const newBarcode = e.target.value;

                                                    // Nếu đang nhập rỗng thì gán luôn
                                                    if (!newBarcode) {
                                                        m.maVach = "";
                                                        setLoad(!load);
                                                        return;
                                                    }

                                                    // --- 1. Kiểm tra trùng trong các đóng gói hiện tại của variant đang nhập ---
                                                    const isDuplicateInCurrentDongGoi = variantPick.dongGoiNhap.some(
                                                        (item, idx) => idx !== ind && item.maVach === newBarcode
                                                    );

                                                    // --- 2. Kiểm tra trùng trong danh sách biến thể không lẻ ---
                                                    const isDuplicateInBienThe = product.bienTheKhongLe.some((bienThe) => bienThe.maVach === newBarcode);

                                                    // --- 3. Kiểm tra trùng trong dongGoiNhap của từng biến thể không lẻ ---
                                                    const isDuplicateInBienTheDongGoi = product.bienTheKhongLe.some((bienThe) =>
                                                        bienThe.dongGoiNhap?.some((dongGoi) => dongGoi.maVach === newBarcode)
                                                    );

                                                    const hasDuplicateBarcode =
                                                        isDuplicateInCurrentDongGoi || isDuplicateInBienThe || isDuplicateInBienTheDongGoi;

                                                    if (hasDuplicateBarcode) {
                                                        //  window.alert("Mã vạch này đã tồn tại ở một nơi khác!");
                                                        const truncated = newBarcode.slice(0, -1);
                                                        m.maVach = truncated;

                                                        setLoad(!load);
                                                        return;
                                                    }

                                                    // Không trùng thì gán
                                                    m.maVach = newBarcode;
                                                    setLoad(!load);
                                                }}
                                                placeholder="Nhập mã vạch"
                                            />




                                        </td>
                                        <td className='text-center'>
                                            <button
                                                onClick={() => {
                                                    variantPick.dongGoiNhap.splice(ind, 1);
                                                    setLoad(!load);
                                                }}
                                                className="rounded-sm px-2 py-2 bg-red-100 text-red-500"
                                            >
                                                <i class="fa-solid fa-eraser"></i>
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </Modal>
            ) : null}

        </div>

    );
}

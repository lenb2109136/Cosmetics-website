import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import Modal from "../../../components/commons/modal";
import PickCategory from "../../../components/commons/PickCategory";
import { useEffect, useState } from "react";
import { PickClassify } from '../../../components/admin/PickClassify';
import { checkSubmit, Product } from '../../../models/classify';
import PickDetailCategory from "../../../components/commons/PickDetailCategory";
import { getThuongHieu } from '../../../services/ThuongHieuService';
import { getUpdate, saveSanPham, upDateProduct } from '../../../services/sanPhamService';
import { message } from 'antd';
import { toast } from 'react-toastify';
import Loading from '../../../components/commons/Loading';
import { v4 as uuidv4 } from 'uuid';
import { useSearchParams } from 'react-router-dom';
import { getAllDongGoi } from '../../../services/trangthaiservice';

export default function UpdateProduct() {
    const [tenPhanLoai, setTenPhanLoai] = useState("");
    const [images, setImages] = useState([]);
    const [coverImage, setCoverImage] = useState(null);
    const [imagevariant, setImagevariant] = useState([]);
    const [open, setOpen] = useState(false);
    const [opendetailcategory, setOpendetailcategory] = useState(true);
    const [openloadin, setOpenloading] = useState(false);
    const maxImages = 9;
    const [cachDung, setCachDung] = useState('<p>Cách dùng...</p>');
    const [danhSachNganhHang, setDanhSachNganhHang] = useState([]);
    const [nhomHang, setNhomHang] = useState("");
    const [product, setProduct] = useState({});
    const [thuonghieu, setThuonghieu] = useState([]);
    const [load, setLoad] = useState(false);
    const [anhGioiThieuNew, setAnhGioiThieuNew] = useState([]);
    const [anhBiaNew, setAnhBiaNew] = useState(null);
    const [anhGioiThieuXoa, setAnhGioiThieuXoa] = useState([])
    const [anhBiaXoa, setAnhBiaXoa] = useState([]);
    const [anhPhanLoaiNew, setAnhPhanLoaiNew] = useState([]);
    const [searchParams] = useSearchParams();
    const [variantPick, setVariantPick] = useState({})
    const [openDongGoi, setOpenDongGoi] = useState(false)
    const [danhSachQuyCachDongGoi, setDanhSachQuyCachDongGoi] = useState([])
    const id = searchParams.get('id');
    useEffect(() => {
        getUpdate(id).then((data) => {
            data.data.anhGioiThieu = data.anhGioiThieu || [];
            data.data.anhBia = data.anhBia || null;
            setProduct(data.data);
            setDanhSachNganhHang(data.danhMuc || []);
            let chuoi = "";
            data.danhMuc.forEach((d, index) => {
                chuoi += d.ten;
                if (index < data.danhMuc.length - 1) {
                    chuoi += " -> ";
                }
            });
            setNhomHang(chuoi);
            setImagevariant(data.data.bienTheKhongLe.map(variant => variant.duongDanAnh || null));
            // Khởi tạo anhPhanLoaiNew với các null
            setAnhPhanLoaiNew(data.data.bienTheKhongLe.map(() => null));
        }).catch((e) => {
            toast.error(e.response?.data?.message || "Không thể lấy dữ liệu");
        });
        getThuongHieu().then((data) => {
            setThuonghieu(data);
        }).catch(() => {
            toast.error("Lấy thông tin thất bại, vui lòng thử lại");
        });
        getAllDongGoi().then(d => {
            setDanhSachQuyCachDongGoi(d)
        }).catch(() => { })
    }, []);

    useEffect(() => {
        if (danhSachNganhHang?.length === 0) {
            product.thongSo = [];
            setOpendetailcategory(false);
        } else {
            if (product.danhMuc !== danhSachNganhHang[danhSachNganhHang.length - 1].id) {
                product.danhMuc = danhSachNganhHang[danhSachNganhHang.length - 1].id;
                product.thongSo = [];
                setOpendetailcategory(true);
                setLoad(!load);
            }
        }
    }, [danhSachNganhHang]);

    const handleAnhGioiThieuChange = (e) => {
        const files = Array.from(e.target.files);
        if (files.length + (product.anhGioiThieu?.length || 0) + anhGioiThieuNew.length > maxImages) {
            toast.error(`Chỉ được chọn tối đa ${maxImages} ảnh`);
            return;
        }
        setAnhGioiThieuNew([...anhGioiThieuNew, ...files]);
        setImages([...images, ...files.map(file => URL.createObjectURL(file))]);
    };

    const handleRemoveAnhGioiThieu = (id, index) => {
        if (index < (product.anhGioiThieu?.length || 0)) {
            // Ảnh cũ từ server
            const removedImage = product.anhGioiThieu[index];
            setAnhGioiThieuXoa(
                [...anhGioiThieuXoa, id]
            )
            const updatedAnhGioiThieu = product.anhGioiThieu.filter((_, i) => i !== index);
            setProduct({ ...product, anhGioiThieu: updatedAnhGioiThieu });
        } else {
            // Ảnh mới
            const newImageIndex = index - (product.anhGioiThieu?.length || 0);
            const updatedAnhGioiThieuNew = anhGioiThieuNew.filter((_, i) => i !== newImageIndex);
            setAnhGioiThieuNew(updatedAnhGioiThieuNew);
        }
        const updatedImages = images.filter((_, i) => i !== index);
        setImages(updatedImages);
    };

    const handleAnhBiaChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            if (product.anhBiaId) {
                setAnhBiaXoa([...anhBiaXoa, product.anhBiaId]);
            }
            setAnhBiaNew(file);
            setCoverImage(URL.createObjectURL(file));
            setProduct({ ...product, anhBia: URL.createObjectURL(file), anhBiaId: null });
        }
    };

    const handleRemoveAnhBia = () => {
        if (product.anhBiaId) {
            setAnhBiaXoa([...anhBiaXoa, product.anhBiaId]);
        }
        setAnhBiaNew(null);
        setCoverImage(null);
        setProduct({ ...product, anhBia: null, anhBiaId: null });
    };

    const handleAddVariant = () => {
        const newVariant = {
            id: uuidv4(),
            ten: tenPhanLoai,
            gia: 0,
            soLuong: 0,
            consuDung: true,
            duongDanAnh: null,
            dongGoiNhap:[],
            old: false
        };
        const newClassify = [...(product.bienTheKhongLe || []), newVariant];
        const pr = new Product();
        Object.assign(pr, product);
        pr.bienTheKhongLe = newClassify;
        setProduct(pr);
        setImagevariant([...imagevariant, null]);
        setAnhPhanLoaiNew([...anhPhanLoaiNew, null]);
        setTenPhanLoai("");
    };

    const handleVariantImageChange = (index, e) => {
        const file = e.target.files[0];
        if (file) {
            const newImagevariant = [...imagevariant];
            newImagevariant[index] = URL.createObjectURL(file);
            setImagevariant(newImagevariant);

            const newAnhPhanLoaiNew = [...anhPhanLoaiNew];
            newAnhPhanLoaiNew[index] = file; // Thay null bằng file mới
            setAnhPhanLoaiNew(newAnhPhanLoaiNew);

            const updatedVariants = [...product.bienTheKhongLe];
            updatedVariants[index].duongDanAnh = URL.createObjectURL(file);
            setProduct({ ...product, bienTheKhongLe: updatedVariants });
        }
    };

    const handleRemoveVariantImage = (index) => {
        const newImagevariant = [...imagevariant];
        newImagevariant[index] = null;
        setImagevariant(newImagevariant);

        const newAnhPhanLoaiNew = [...anhPhanLoaiNew];
        newAnhPhanLoaiNew[index] = null; // Đặt lại thành null
        setAnhPhanLoaiNew(newAnhPhanLoaiNew);

        const updatedVariants = [...product.bienTheKhongLe];
        updatedVariants[index].duongDanAnh = null;
        setProduct({ ...product, bienTheKhongLe: updatedVariants });
    };

    const handleRemoveVariant = (index) => {
        const updatedVariants = [...product.bienTheKhongLe];
        updatedVariants.splice(index, 1);

        const newImagevariant = [...imagevariant];
        newImagevariant.splice(index, 1);

        const newAnhPhanLoaiNew = [...anhPhanLoaiNew];
        newAnhPhanLoaiNew.splice(index, 1);

        setProduct({ ...product, bienTheKhongLe: updatedVariants });
        setImagevariant(newImagevariant);
        setAnhPhanLoaiNew(newAnhPhanLoaiNew);
        setLoad(!load);
    };
   function checkConsuDung(data) {
    const danhSach = data.bienTheKhongLe || [];

    const coConsuDung = danhSach.some(item => item.consuDung === true);
    const khongCoOldFalse = !danhSach.some(item => item.old === false); 

    return coConsuDung || khongCoOldFalse==false;
}


    const handleToggleVariantStatus = (index) => {
        const updatedVariants = [...product.bienTheKhongLe];
        updatedVariants[index].consuDung = !updatedVariants[index].consuDung;
        setProduct({ ...product, bienTheKhongLe: updatedVariants });
        setLoad(!load);
    };

    const handleSubmit = async () => {
        setOpenloading(true);
        product.danhMuc = danhSachNganhHang?.[danhSachNganhHang?.length - 1]?.id;
        upDateProduct(
            product,
            anhPhanLoaiNew,
            anhBiaNew,
            anhGioiThieuNew,
            anhGioiThieuXoa,
            id
        ).then(() => { toast("Cập nhật sản phẩm thành công"); setOpenloading(false) })
            .catch((error) => { toast.error(error.response?.data?.message || "Cập nhật sản phẩm thất bại!"); setOpenloading(false) })
    };

    return (
        <div className="p-4 w-[100%]">
            {openloadin ? <Loading /> : null}
            <strong><h3 className="text-2xl">Thông tin cơ bản</h3></strong>

            {/* Ảnh sản phẩm */}
            <div className='flex flex-row w-[100%]'>
                <div className='basis-3/5'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {images.length > 0 || product.anhGioiThieu?.length > 0 ? (
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
                        <strong><p className="mt-0">Hình ảnh sản phẩm</p></strong>
                    </div>
                    <div className="flex flex-wrap gap-4">
                        {product?.anhGioiThieu?.map((img, index) => (
                            <div key={index} className="relative group w-24 h-24 border rounded overflow-hidden">
                                <img src={img.duongDan} alt={`img-${index}`} className="object-cover w-full h-full" />
                                <button
                                    className="absolute top-1 right-1 bg-black bg-opacity-50 text-white p-1 rounded-full opacity-0 group-hover:opacity-100 transition"
                                    onClick={() => {
                                        handleRemoveAnhGioiThieu(img.id, index);
                                    }}
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                        ))}
                        {images.map((img, index) => (
                            <div key={`new-${index}`} className="relative group w-24 h-24 border rounded overflow-hidden">
                                <img src={img} alt={`new-img-${index}`} className="object-cover w-full h-full" />
                                <button
                                    className="absolute top-1 right-1 bg-black bg-opacity-50 text-white p-1 rounded-full opacity-0 group-hover:opacity-100 transition"
                                    onClick={() => handleRemoveAnhGioiThieu(index + (product.anhGioiThieu?.length || 0))}
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                        ))}
                        {(product?.anhGioiThieu?.length || 0) + images.length < maxImages && (
                            <label className="w-24 h-24 border-2 border-dashed border-red-500 bg-red-50 rounded-lg flex flex-col items-center justify-center text-red-500 cursor-pointer hover:shadow hover:shadow-red-300 transition">
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
                                </svg>
                                <p className="text-xs mt-1 text-center">Thêm hình</p>
                                <input
                                    type="file"
                                    accept="image/*"
                                    multiple
                                    className="hidden"
                                    onChange={handleAnhGioiThieuChange}
                                />
                            </label>
                        )}
                    </div>
                    <p className="text-sm text-gray-500 mt-2">Đã chọn: {(product.anhGioiThieu?.length || 0) + images.length}/{maxImages} ảnh</p>
                </div>
                {/* Ảnh bìa */}
                <div className='basis-2/3'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {product?.anhBia || anhBiaNew ? (
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
                        <strong><p className="mt-0">Ảnh bìa</p></strong>
                    </div>
                    <div className="flex flex-row items-start gap-4">
                        {(product?.anhBia || coverImage) ? (
                            <div className="relative w-24 h-24 border rounded overflow-hidden group">
                                <img src={coverImage || product.anhBia} alt="cover" className="object-cover w-full h-full" />
                                <button
                                    className="absolute top-1 right-1 bg-black bg-opacity-50 text-white p-1 rounded-full opacity-0 group-hover:opacity-100 transition"
                                    onClick={handleRemoveAnhBia}
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
                                <input
                                    type="file"
                                    accept="image/*"
                                    className="hidden"
                                    onChange={handleAnhBiaChange}
                                />
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
                <div className='basis-3/5'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {product.ten ? (
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
                        <strong><p className="mt-0">Tên sản phẩm</p></strong>
                    </div>
                    <input
                        onChange={(event) => {
                            product.ten = event.target.value;
                            setLoad(!load);
                        }}
                        value={product.ten || ''}
                        className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                        type="text"
                    />
                </div>
                <div className='basis-1/5 mr-2'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {product.thuongHieu !== 0 ? (
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
                        <strong><p className="mt-0">Thương hiệu</p></strong>
                    </div>
                    <div className="w-full max-w-md mt-5">
                        <select
                            className="cursor-pointer w-full p-3 border border-gray-300 rounded-xl shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
                            value={product.thuongHieu || ''}
                            onChange={(e) => {
                                product.thuongHieu = parseInt(e.target.value) || 0;
                                setLoad(!load);
                            }}
                        >
                            <option value="">Chọn thương hiệu</option>
                            {thuonghieu.map((data) => (
                                <option key={data.id} value={data.id}>
                                    {data.ten}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
                
                <div className='basis-1/5'>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        {product.thue > 0 ? (
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
                        <strong><p className="mt-0">Thuế VAT</p></strong><i>* Đơn vị tính %</i>
                    </div>
                    <input
                        onChange={(event) => {
                            product.thue = parseInt(event.target.value) || 0;
                            setLoad(!load);
                        }}
                        value={product.thue || ''}
                        className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                        type="number"
                    />
                </div>
            </div>
            <div className="flex flex-row items-end justify-start">
  <div
    className={`${
      checkConsuDung(product) ? "w-full" : "basis-3/5"
    }`}
  >
    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
      {product.danhMuc !== 0 ? (
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
      <strong><p className="mt-0">Nhóm sản phẩm</p></strong>
    </div>
    <input
      value={nhomHang}
      onClick={() => setOpen(true)}
      className="cursor-pointer rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 hover:border-blue-300 focus:border-blue-300 focus:outline-none font-bold"
      type="text"
      readOnly
    />
  </div>
  {!checkConsuDung(product) && (
    <>
      <div className="basis-1/5">
        <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
          {product.gia > 0 ? (
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
          <strong><p className="mt-0">Giá mặc định</p></strong>
        </div>
        <input
          onChange={(event) => {
            product.gia = parseInt(event.target.value) || 0;
            setLoad(!load);
          }}
          value={product.gia || ""}
          className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 hover:border-blue-300 focus:border-blue-300 focus:outline-none"
          type="number"
        />
      </div>
      <div className="basis-1/5">
        <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
          {product.khoiLuong > 0 ? (
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
          <strong><p className="mt-0">Khối lượng</p></strong>
        </div>
        <input
          onChange={(event) => {
            product.khoiLuong = parseInt(event.target.value) || 0;
            setLoad(!load);
          }}
          value={product.khoiLuong || ""}
          className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 hover:border-blue-300 focus:border-blue-300 focus:outline-none"
          type="number"
        />
      </div>
      <div className="basis-1/5 pl-2">
        <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
          {product.ten !== "" ? (
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
          <strong><p className="mt-0">Mã vạch</p></strong>
        </div>
        <input
          onChange={(event) => {
            product.maVach = event.target.value;
            setLoad(!load);
          }}
          defaultValue={product?.maVach}
          className="rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 hover:border-blue-300 focus:border-blue-300 focus:outline-none"
          type="text"
        />
      </div>
    </>
  )}
</div>
            <div className="mt-7 mb-5">
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
                    <strong><p className="mt-0 mb-3">Mô tả sản phẩm</p></strong>
                </div>
                <div className="max-h-[350px] overflow-y-auto border border-gray-300">
                    <CKEditor
                        editor={ClassicEditor}
                        data={product.moTa || ''}
                        config={{ placeholder: "Nhập mô tả sản phẩm..." }}
                        onChange={(event, editor) => {
                            const newData = editor.getData();
                            product.moTa = newData;
                            setLoad(!load);
                        }}
                    />
                </div>
            </div>
            {open ? (
                <Modal setOpen={setOpen}>
                    <PickCategory
                        setOpen={setOpen}
                        categoryPick={danhSachNganhHang}
                        setChuoi={setNhomHang}
                        setcategoryPick={setDanhSachNganhHang}
                    />
                </Modal>
            ) : null}
            <div className="mt-7 mb-5">
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
                    <strong><p className="mt-0 mb-3">Thành phần sản phẩm</p></strong>
                </div>
                <div className="max-h-[350px] overflow-y-auto border border-gray-300">
                    <CKEditor
                        editor={ClassicEditor}
                        data={product.thanhPhan || ''}
                        config={{ placeholder: "Nhập thành phần sản phẩm..." }}
                        onChange={(event, editor) => {
                            const newData = editor.getData();
                            product.thanhPhan = newData;
                            setLoad(!load);
                        }}
                    />
                </div>
            </div>
            <div className="mt-7 mb-5">
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
                    <strong><p className="mt-0 mb-3">Cách dùng</p></strong>
                </div>
                <div className="max-h-[350px] overflow-y-auto border border-gray-300">
                    <CKEditor
                        editor={ClassicEditor}
                        data={product.cachDung || ''}
                        config={{ placeholder: "Nhập cách dùng..." }}
                        onChange={(event, editor) => {
                            const newData = editor.getData();
                            product.cachDung = newData;
                            setLoad(!load);
                        }}
                    />
                </div>
            </div>
             {
                checkConsuDung(product)===false ? <div className="mt-7 mb-5">
                    <div className="flex flex-row items-center space-x-2">
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
            <div>
                <div>
                    <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
                        <div className="relative">
                            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                            <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                        </div>
                        <strong><p className="mt-0">Phân loại hàng</p></strong>
                        <i>(* lưu ý nếu không có phân loại hàng thì phải cung cấp giá gốc cho sản phẩm - phân loại mặc định)</i>
                    </div>
                    <div className='flex flex-row'>
                        <button
                            onClick={handleAddVariant}
                            className="mb-4 flex items-center justify-center gap-2 px-4 py-2 border border-dashed border-gray-400 text-red-500 rounded hover:bg-red-100"
                        >
                            <span className="text-xl">+</span> Thêm phân loại
                        </button>
                        <div className="basis-3/4 ml-3">
                            <input
                                placeholder='Nhập tên phân loại nếu có'
                                value={tenPhanLoai}
                                onChange={(e) => setTenPhanLoai(e.target.value)}
                                type="text"
                                className="cursor-pointer rounded-lg w-[80%] h-12 border border-gray-200 pl-4 transition-colors duration-300 hover:border-blue-300 focus:border-blue-300 focus:outline-none"
                            />
                        </div>
                    </div>
                </div>
                <table className="min-w-full border border-gray-400 rounded-lg overflow-hidden text-sm mt-4">
                    <thead className="bg-blue-100 text-gray-700 uppercase tracking-wide">
                        <tr className="border border-gray-300">
                            <th className="px-4 py-2 border-r text-center border border-gray-300">Ảnh phân loại</th>
                            <th className="px-4 py-2 border-r text-center border border-gray-300">Tên phân loại</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Mã vạch</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Giá</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Số lượng còn lại</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Trọng lượng</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Quy cách nhập</th>
                            <th className="px-4 py-2 text-center border border-gray-300">Trạng thái</th>

                        </tr>
                    </thead>
                    <tbody className="text-gray-800">
                        {product?.bienTheKhongLe?.map((data, index) => (
                            <tr
                                className="group odd:bg-white even:bg-gray-50 hover:bg-gray-100 transition relative"
                                key={data.id || index}
                            >
                                <td className="px-4 py-3 border border-gray-300 border-r text-center align-middle font-medium text-gray-600 relative">
                                    {imagevariant[index] && (
                                        <div className="mb-2 relative group">
                                            <button
                                                type="button"
                                                className="absolute top-0 right-0 bg-white bg-opacity-80 rounded-full text-red-600 hover:text-red-800 px-1 text-xs"
                                                onClick={() => handleRemoveVariantImage(index)}
                                            >
                                                ✕
                                            </button>
                                        </div>
                                    )}
                                    <label className="mx-auto relative w-24 h-24 border-2 border-dashed border-red-500 bg-red-50 rounded-lg flex flex-col items-center justify-center text-red-500 cursor-pointer hover:shadow hover:shadow-red-300 transition overflow-hidden">
                                        {imagevariant[index] && (
                                            <img
                                                src={imagevariant[index]}
                                                alt="preview"
                                                className="absolute inset-0 w-full h-full object-cover rounded-lg"
                                            />
                                        )}
                                        <input
                                            type="file"
                                            accept="image/*"
                                            className="hidden"
                                            onChange={(e) => handleVariantImageChange(index, e)}
                                        />
                                    </label>
                                </td>
                                <td className='text-center'>
                                    <input
                                        type='text'
                                        className='pl-3 outline-none border border-gray-200 rounded-sm'
                                        value={data.ten || ''}
                                        onChange={(e) => {
                                            const updatedVariants = [...product.bienTheKhongLe];
                                            updatedVariants[index].ten = e.target.value;
                                            setProduct({ ...product, bienTheKhongLe: updatedVariants });
                                            setLoad(!load);
                                        }}
                                    />
                                </td>
                                <td className='text-center '>
                                    <input
                                        type='text'
                                        className='pl-3 outline-none border border-gray-200 rounded-sm'
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
                                <td className='text-center'>
                                    <input
                                        min={1}
                                        className='pl-3 outline-none border border-gray-200 rounded-sm'
                                        type='number'
                                        value={data.gia || ''}
                                        onChange={(event) => {
                                            const updatedVariants = [...product.bienTheKhongLe];
                                            updatedVariants[index].gia = parseInt(event.target.value) || 0;
                                            setProduct({ ...product, bienTheKhongLe: updatedVariants });
                                            setLoad(!load);
                                        }}
                                    />
                                </td>
                                <td className='text-center'>
                                    {data?.soLuong}
                                </td>
                                 <td className='text-center'>
                                    <input
                                        min={1}
                                        className='pl-3 outline-none border border-gray-200 rounded-sm'
                                        type='number'
                                        value={data.khoiLuong || ''}
                                        onChange={(event) => {
                                            const updatedVariants = [...product.bienTheKhongLe];
                                            updatedVariants[index].khoiLuong = parseInt(event.target.value) || 0;
                                            setProduct({ ...product, bienTheKhongLe: updatedVariants });
                                            setLoad(!load);
                                        }}
                                    />
                                </td>
                                <td className='text-center'>
                                    <button className='cursor-pointer p-2 text-yellow-500 b rounded-sm  bg-yellow-100' onClick={() => {
                                        setVariantPick(data)
                                        setOpenDongGoi(true)
                                    }}><i class="fa-solid fa-square-pen"></i> Điều chỉnh</button>
                                </td>

                                <td className='text-center'>
                                    {data.old ? (
                                        <div>
                                            {data.consuDung ? (
                                                <button
                                                    className='cursor-pointer p-2 text-green-500 rounded-sm bg-green-100'
                                                    onClick={() => handleToggleVariantStatus(index)}
                                                >
                                                    Hoạt động
                                                </button>
                                            ) : (
                                                <button
                                                    className='cursor-pointer p-2 text-white rounded-sm bg-gray-200'
                                                    onClick={() => handleToggleVariantStatus(index)}
                                                >
                                                    Ngừng hoạt động
                                                </button>
                                            )}
                                        </div>
                                    ) : <button onClick={()=>{
                                        product?.bienTheKhongLe?.splice(index,1)
                                        setLoad(!load)
                                    }} className='p-2 rounded-sm text-red-600 bg-red-100'><i class="fa-solid fa-trash"></i> Xóa</button>}
                                </td>

                            </tr>
                        ))}
                        {product?.bienTheKhongLe?.length === 0 && (
                            <tr>
                                <td colSpan={6}>
                                    <p className='text-center mt-3 bg-gray-50'>
                                        <i>Chưa thêm phân loại nào</i>
                                    </p>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            <div>
                <div className="flex flex-row items-center space-x-2 mt-10 mb-5">
                    <div className="relative">
                        <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
                        <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
                    </div>
                    <strong><p className="mt-0">Thông số bổ sung</p></strong>
                </div>
                <PickDetailCategory
                    l={load}
                    setl={setLoad}
                    product={product}
                    open={open}
                    id={product.danhMuc}
                />
            </div>
            <button
                onClick={handleSubmit}
                className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
                Cập nhật sản phẩm
            </button>
            {openDongGoi ? (
                <Modal setOpen={setOpenDongGoi} b={true}>
                    <div className="space-y-6 p-4 bg-white rounded-xl shadow-lg">
                        <p className="text-lg font-semibold text-gray-800">Tạo quy cách đóng gói cho sản phẩm</p>

                        {/* Packaging Options */}
                        <div className="flex flex-wrap gap-4">
                            {danhSachQuyCachDongGoi?.map((d) => (
                                <div
                                    key={d?.id}
                                    onClick={() => {
                                        // alert(variantPick?.old)
                                        const existing =variantPick?.dongGoiNhap?.filter(item => item.id === d.id) || [];
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
                                <tr className="bg-gray-100">
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
                                                className="w-20 p-1 border rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
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
                                                className="w-32 p-1 border rounded focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
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
                                                <i className="fa-solid fa-trash"></i>
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
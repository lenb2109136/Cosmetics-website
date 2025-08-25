import React, { useEffect, useRef, useState } from "react";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import gsap from "gsap";
import { getProduct } from "../../services/SupplierService";
import Modal from "../commons/modal";
import { ChiTietPhieuNhap } from "./DetailimportSlip";
import { getproductCungCap } from "../../services/sanPhamService";
import { Pagination } from "../commons/Pagination";

function demSoLanNhapHang(thongTinSanPham) {
    const bienTheNhap = thongTinSanPham.danhSachBienTheNhap || [];
    const tatCaLanNhap = bienTheNhap.flatMap(bienThe =>
        (bienThe.danhSachNhap || []).map(nhap => new Date(nhap.ngayNhap).getTime())
    );

    if (tatCaLanNhap.length === 0) return 0;

    tatCaLanNhap.sort((a, b) => a - b);

    let soLanNhap = 1;
    for (let i = 1; i < tatCaLanNhap.length; i++) {
        if (tatCaLanNhap[i] - tatCaLanNhap[i - 1] > 90000) {
            soLanNhap++;
        }
    }

    return soLanNhap;
}

function DetailProduct({ sl=0, bd, kt, id, dvcc = "", sp = "", bienDoi = false }) {
    const [selectedId, setSelectedId] = useState(null);
    const [sanPhamChon, setSanPhamCHon] = useState({})
    const [data, setdata] = useState([])
    const detailProductRef = useRef(null);
    const [idPhieu, setidPhieu] = useState(0);
    const [open, setOpen] = useState(false)
    const [tong, setTong] = useState(0)
    const [tongSL, setTongSL] = useState(0)
    const [trangHienTai,setTrangHienTai]=useState(0)
    useEffect(() => {
        if (detailProductRef.current) {
            gsap.fromTo(detailProductRef.current,
                { opacity: 0, y: 20, scale: 0.98 },
                {
                    opacity: 1,
                    y: 0,
                    scale: 1,
                    duration: 0.7,
                    delay: 0.2,
                    ease: "power2.out"
                }
            );
        }
    }, []);

    useEffect(() => {
        if(bienDoi==false){
            getProduct(bd, kt, id, trangHienTai).then((dat) => {
            setdata(dat.content)
            setTongSL(dat?.totalElements)
            setTong(dat?.totalPages)
        })
        }
        else{
            getproductCungCap(bd, kt,dvcc,sp,trangHienTai).then((dat) => {
            setdata(dat.content)
            setTongSL(dat?.totalElements)
            setTong(dat?.totalPages)
        })
        }
        
        // getproductCungCap()
    }, [])
    useEffect(() => {
        if(bienDoi==false){
            getProduct(bd, kt, id, trangHienTai).then((dat) => {
            setdata(dat.content)
            setTongSL(dat?.totalElements)
            setTong(dat?.totalPages)
        })
        }
        else{
            getproductCungCap(bd, kt,dvcc,sp,trangHienTai).then((dat) => {
            setdata(dat.content)
            setTongSL(dat?.totalElements)
            setTong(dat?.totalPages)
        })
        }
        
        // getproductCungCap()
    }, [bd,kt,dvcc,sp,id])
    const chiTiet = data.find(d => d.idSanPham === selectedId);

    return (<>
        <div ref={detailProductRef} className="p-4 bg-white">
            <p className="mb-2 font-bold"><i class="fa-solid fa-list p-2 rounded-sm text-green-500 bg-green-100 mr-2"></i> Danh sách sản phẩm cung cấp</p>
                    
            <div className="flex transition-all duration-300 gap-4">
                <div className={`${selectedId ? 'w-2/5' : 'w-full'} transition-all duration-300`}>
                    <table className="w-full table-auto border border-gray-300 text-sm">
                        <thead>
                            <tr className="bg-gray-100 text-left">
                                <th className="p-3 border text-center w-[60px]">STT</th>
                                <th className="p-3 border max-w-[180px] truncate">Tên sản phẩm</th>
                                {!selectedId && (
                                    <>
                                        <th className="p-3 border text-center w-[100px]">Ảnh</th>
                                        <th className="p-3 border text-center w-[120px]">Số lần nhập</th>
                                        <th className="p-3 border text-center w-[120px]">Thao tác</th>
                                    </>
                                )}
                            </tr>
                        </thead>
                        <tbody>
                            {data?.length == 0 ? <tr>
                                <td colSpan="6" className="text-center py-4 text-md text-gray-500">
                                    Không có sản phẩm cung cấp trong khoảng thời gian này.
                                </td>
                            </tr> : null}
                            {data.map((d, index) => (
                                <tr
                                    key={d.idSanPham}
                                    className="border hover:bg-gray-50 transition-colors duration-200"
                                >
                                    <td className="p-3 border text-center font-medium">{index + 1}</td>
                                    <td className="p-3 border max-w-[180px] truncate" title={d.tenSanPham}>
                                        {d.tenSanPham}
                                    </td>
                                    {!selectedId && (
                                        <>
                                            <td className="p-3 border text-center">
                                                {d.anhSanPham ? (
                                                    <img
                                                        src={d.anhSanPham}
                                                        alt="Ảnh"
                                                        className="w-16 h-16 object-cover mx-auto rounded-md"
                                                    />
                                                ) : (
                                                    <span className="italic text-gray-400">Không có</span>
                                                )}
                                            </td>
                                            <td className="p-3 border text-center">{demSoLanNhapHang(d)}</td>
                                            <td className="p-3 border text-center whitespace-nowrap">
                                                <button
                                                    onClick={() => {
                                                        setSelectedId(d.idSanPham);
                                                        setSanPhamCHon(d);
                                                    }}
                                                    className="text-green-500 bg-green-100 pl-2 pr-2 pb-1 pt-1 rounded-md"
                                                >
                                                    Xem chi tiết
                                                </button>
                                            </td>
                                        </>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </table>

                </div>

                {/* Panel chi tiết - nhỏ gọn bên phải */}
                {selectedId && (
                    <div className="w-3/5 border p-4 rounded-md shadow transition-transform duration-300 animate-slide-in">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="font-semibold text-xl">Chi tiết sản phẩm</h3>
                            <button
                                onClick={() => setSelectedId(null)}
                                className="bg-blue-100 text-blue-600 font-medium px-4 py-1 rounded-md shadow-sm transition-all duration-200 hover:bg-blue-500 hover:text-white hover:shadow-md active:scale-95"
                            >
                                Đóng
                            </button>

                        </div>

                        <div className="overflow-x-auto">
                            <table className="min-w-full border border-gray-300 text-sm text-left">
                                <thead className="bg-blue-100 text-gray-700">
                                    <tr>
                                        <th className="p-2 border">STT</th>
                                        <th className="p-2 border">Ảnh phân loại</th>
                                        <th className="p-2 border">Phân loại</th>
                                        {bienDoi==true? <th className="p-2 border">Đơn vị cung cấp</th>:null}
                                        <th className="p-2 border">Ngày nhập</th>
                                        <th className="p-2 border">Số lượng</th>
                                        <th className="p-2 border">Đơn giá</th>
                                    </tr>
                                </thead>
                                <tbody>

                                    {sanPhamChon?.danhSachBienTheNhap?.map((bienThe, index) => {
                                        return (
                                            <React.Fragment key={index}>
                                                {bienThe.danhSachNhap?.map((nhap, i) => (
                                                    <tr key={i} className="hover:bg-gray-50">
                                                        {i === 0 && (
                                                            <>
                                                                <td className="p-2 border" rowSpan={bienThe.danhSachNhap.length}>{index + 1}</td>
                                                                <td className="p-2 border text-center" rowSpan={bienThe.danhSachNhap.length}>
                                                                    {bienThe.hinhanh ? (
                                                                        <img src={bienThe.hinhanh} alt="Ảnh" className="w-16 h-16 object-cover rounded-md mx-auto" />
                                                                    ) : 'Không có'}
                                                                </td>
                                                                <td onClick={() => {
                                                                    setidPhieu(nhap.id)
                                                                    setOpen(true)
                                                                }} className="p-2 border hover:text-blue-300 cursor-pointer" rowSpan={bienThe.danhSachNhap.length}>
                                                                    {bienThe.ten}
                                                                </td>
                                                            </>
                                                        )}
                                                        {bienDoi==true? <th className="p-2 border text-center">{nhap.donViCungCap}</th>:null}
                                                        <td className="p-2 border text-center ">{dinhDangNgay(nhap.ngayNhap)}</td>
                                                        <td className="p-2 border text-center">{nhap.soLuong}</td>
                                                        <td className="p-2 border text-right">{formatToVND(nhap.gia)}</td>
                                                    </tr>
                                                ))}
                                            </React.Fragment>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>

                    </div>

                )}
            </div>

        </div>
        {open && (
            <Modal setOpen={setOpen}>
                <ChiTietPhieuNhap id={idPhieu} />
            </Modal>
        )}
        {tong > 0 ? <Pagination
            trangHienTai={trangHienTai}
            setTrangHienTai={setTrangHienTai}
            soLuongTrang={tong}
        /> : null}
    </>
    );
}

export { DetailProduct };

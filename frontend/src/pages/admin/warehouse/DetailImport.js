import React, { useEffect, useRef, useState } from 'react';
import { getproductCungCap } from '../../../services/sanPhamService';
import { getProduct } from '../../../services/SupplierService';
import gsap from 'gsap';
import { Pagination } from '../../../components/commons/Pagination';
import { ChiTietPhieuNhap } from '../../../components/admin/DetailimportSlip';
import Modal from '../../../components/commons/modal';
import IconExcel from "../../../assets/IconExcel.png"

const ProductTable = ({ sl = 0, bd, kt, id, dvcc = "", sp = "", bienDoi = false }) => {
    const [selectedId, setSelectedId] = useState(null);
    const [sanPhamChon, setSanPhamCHon] = useState({})
    const [data, setdata] = useState([])
    const detailProductRef = useRef(null);
    const [idPhieu, setidPhieu] = useState(0);
    const [open, setOpen] = useState(false)
    const [tong, setTong] = useState(0)
    const [tongSL, setTongSL] = useState(0)
    const [trangHienTai, setTrangHienTai] = useState(0)
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
        if (bienDoi == false) {
            getProduct(bd, kt, id, trangHienTai).then((dat) => {
                setdata(dat.content)
                setTongSL(dat?.totalElements)
                setTong(dat?.totalPages)
            })
        }
        else {
            getproductCungCap(bd, kt, dvcc, sp, trangHienTai).then((dat) => {
                setdata(dat.content)
                setTongSL(dat?.totalElements)
                setTong(dat?.totalPages)
            })
        }

        // getproductCungCap()
    }, [])
    useEffect(() => {
        if (bienDoi == false) {
            getProduct(bd, kt, id, trangHienTai).then((dat) => {
                setdata(dat.content)
                setTongSL(dat?.totalElements)
                setTong(dat?.totalPages)
            })
        }
        else {
            getproductCungCap(bd, kt, dvcc, sp, trangHienTai).then((dat) => {
                setdata(dat.content)
                setTongSL(dat?.totalElements)
                setTong(dat?.totalPages)
            })
        }

        // getproductCungCap()
    }, [bd, kt, dvcc, sp, id])
    const chiTiet = data.find(d => d.idSanPham === selectedId);
    const formatCurrency = (value) => {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('vi-VN');
    };

    return (
        <div className="p-4">
            <div className='flex flex-row justify-between items-end w-full mt-3 mb-3'>
                <p className="mb-2 font-bold">Danh sách nhập kho chi tiết:</p>
                <a
                    download
                    href={`http://localhost:8080/api/sanpham/export/lannhapkho?bd=${bd}&kt=${kt}&trang=${trangHienTai}&dvcc=${dvcc}&sp=${sp}`}
                >
                    <div className='flex items-end'>
                        <p className='hover:text-blue-400'><strong>Xuất thống kê</strong></p>
                    <img src={IconExcel} className='w-10' alt='Export Excel' />
                    </div>
                </a>
            </div>
            <p className='pb-2'><i>Tổng số lượng sản phẩm: </i>{tongSL}</p>
            <table className="w-full border-collapse border border-gray-300">
                <thead>
                    <tr className="bg-gray-100">
                        <th className="border border-gray-300 p-2">Tên sản phẩm</th>
                        <th className="border border-gray-300 p-2">Phân loại</th>
                        <th className="border border-gray-300 p-2">Lần nhập</th>
                        <th className="border border-gray-300 p-2">Số lượng</th>
                        <th className="border border-gray-300 p-2">Đơn giá</th>
                        <th className="border border-gray-300 p-2">Tổng tiền</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((product, productIndex) => {
                        const totalRows = product.danhSachBienTheNhap.reduce((acc, variant) => acc + variant.danhSachNhap.length, 0);

                        return product.danhSachBienTheNhap.flatMap((variant, variantIndex) => {
                            return variant.danhSachNhap.map((nhapItem, nhapIndex) => {
                                const isFirstRow = variantIndex === 0 && nhapIndex === 0;
                                const isFirstRowOfVariant = nhapIndex === 0;

                                return (
                                    <tr key={`${product.idSanPham}-${variant.id}-${nhapItem.id}`} className="hover:bg-gray-50">
                                        {isFirstRow && (
                                            <td
                                                rowSpan={totalRows}
                                                className="border border-gray-300 p-2"
                                            >
                                                {product.tenSanPham}
                                            </td>
                                        )}
                                        {isFirstRowOfVariant && (
                                            <td
                                                rowSpan={variant.danhSachNhap.length}
                                                className="border border-gray-300 p-2"
                                            >
                                                {variant.ten}
                                            </td>
                                        )}
                                        <td className="border border-gray-300 p-2 text-center">
                                            {formatDate(nhapItem.ngayNhap)}
                                        </td>
                                        <td className="border border-gray-300 p-2 text-center">
                                            {nhapItem.soLuong}
                                        </td>
                                        <td className="border border-gray-300 p-2 text-right">
                                            {formatCurrency(nhapItem.gia)}
                                        </td>
                                        <td className="border border-gray-300 p-2 text-right">
                                            {formatCurrency(nhapItem.soLuong * nhapItem.gia)}
                                        </td>
                                    </tr>
                                );
                            });
                        });
                    })}
                </tbody>
            </table>
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
        </div>
    );
};

export { ProductTable };






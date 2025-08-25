import React, { useEffect, useState } from 'react';
import { getxuatTonKho } from '../../../services/sanPhamService';
import { Pagination } from '../../../components/commons/Pagination';
import IconExcel from "../../../assets/IconExcel.png"
const XuatTonKho = ({bd, kt,sp,status}) => {
    const [data, setdata] = useState([])
    const [tong, setTong] = useState(0)
    const [tongSL, setTongSL] = useState(0)
    const [trangHienTai, setTrangHienTai] = useState(0)
    let stt = 0;
    useEffect(() => {
                getxuatTonKho(bd, kt, 0,sp, trangHienTai,status).then((dat) => {
                    setdata(dat.content)
                    setTongSL(dat?.totalElements)
                    setTong(dat?.totalPages)
                })
           
        }, [])
        useEffect(() => {
                getxuatTonKho(bd, kt, 0,sp, trangHienTai,status).then((dat) => {
                    setdata(dat.content)
                    setTongSL(dat?.totalElements)
                    setTong(dat?.totalPages)
                })
           
        }, [bd,kt,sp,trangHienTai,status])

    return (
        <div className="p-4">
            <i>( * Lưu ý số lượng xuất bao gồm các hóa đơn ở trạng thái thành công và trạng thái đã xác nhận  không bao gồm các trạng thái đơn ở dạng  <i className='text-red-500'>chờ xác nhận đơn hủy và đơn hoàn hàng </i>)</i>
            <div className='flex flex-row justify-between items-end w-full mt-3 mb-3'>
                
                <p className="mb-2 font-bold">Danh sách nhập kho chi tiết:</p>
                <a
                    download
                    href={`http://localhost:8080/api/sanpham/export/xuatTonKho?bd=${bd}&kt=${kt}&trang=${trangHienTai}&dm=${0}&sp=${sp}&status=${status}`}
                >
                    <div className='flex items-end'>
                        <p className='hover:text-blue-400'><strong>Xuất thống kê</strong></p>
                    <img src={IconExcel} className='w-10' alt='Export Excel' />
                    </div>
                </a>
            </div>
            <table className="min-w-full border-collapse border border-gray-300">
                <thead>
                    <tr className="bg-gray-100">
                        <th className="border border-gray-300 p-2 text-center">STT</th>
                        <th className="border border-gray-300 p-2 text-left">Tên sản phẩm</th>
                        <th className="border border-gray-300 p-2 text-left">Phân loại</th>
                        <th className="border border-gray-300 p-2 text-center">Tồn kỳ trước</th>
                        <th className="border border-gray-300 p-2 text-center">Nhập trong kỳ</th>
                        <th className="border border-gray-300 p-2 text-center">Bán trong kỳ</th>
                        <th className="border border-gray-300 p-2 text-center">Hao hụt</th>
                        <th className="border border-gray-300 p-2 text-center">Tồn kỳ này</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((product) => {
                        const rowspan = product.xuatTonKho.length;
                        return product.xuatTonKho.map((variant, variantIndex) => (
                            <tr key={`${product.id}-${variant.id}`} className="hover:bg-gray-50">
                                {variantIndex === 0 && (
                                    <>
                                        <td className="border border-gray-300 p-2 text-center" rowSpan={rowspan}>
                                            {(stt += 1)}
                                        </td>
                                        <td className="border border-gray-300 p-2" rowSpan={rowspan}>
                                            {product.ten}
                                        </td>
                                    </>
                                )}
                                <td className="border border-gray-300 p-2">{variant.id}</td>
                                <td className="border border-gray-300 p-2 text-right">{variant.tonKyTruoc}</td>
                                <td className="border border-gray-300 p-2 text-right">{variant.soLuongNhapKyNay}</td>
                                <td className="border border-gray-300 p-2 text-right">{variant.soLuongXuatTrongKy}</td>
                                <td className="border border-gray-300 p-2 text-right">{variant.soLuongHuHao}</td>
                                <td className="border border-gray-300 p-2 text-right">{variant.tonKynay}</td>
                            </tr>
                        ));
                    })}
                </tbody>
            </table>
            {tong > 0 ? <Pagination
                trangHienTai={trangHienTai}
                setTrangHienTai={setTrangHienTai}
                soLuongTrang={tong}
            /> : null}
        </div>
    );
};

export { XuatTonKho };
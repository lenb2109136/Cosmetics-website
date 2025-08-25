import { useEffect, useState } from "react";
import { getPhanBoLoiNhuanOfChiTietHoaDon } from "../../services/sanPhamService";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import Cost from "../../assets/cost.png"
import TotalPrice from "../../assets/totalprice.png"
import RateReturn from "../../assets/rateOfReturn.png"
import ToatalSale from "../../assets/totalsales.png"
import NextTo from "../../assets/nextTo.png"
import PreV from "../../assets/preV.png"
import PrePage from "../../assets/prePage.png"
import Modal from "../commons/modal";
import { ChiTietPhieuNhap } from "./DetailimportSlip";
function ChiTietDoanhThuOfHoaDon({ bd, kt, select, next, pre, setPick ,status}) {
    const [dataPick, setDataPick] = useState({});
    let totalQuantity = 0;
    let totalRevenue = 0;
    let totalProfit = 0;
    let totalSales = 0;
    let profitCount = 0;
    const [open, setOpen] = useState(false)
    const [idPhieu, setidPhieu] = useState(0)

    useEffect(() => {
        if (select) {
            getPhanBoLoiNhuanOfChiTietHoaDon(bd, kt, select,0,status).then((d) => {
                setDataPick(d);
            });
        }
    }, [bd, kt, select,status]);
    dataPick?.thonTinDoanhThu?.forEach(item => {
        item.lanBan.forEach(ban => {
            totalSales += 1;
            ban.danhSachPhanNho.forEach(phanNho => {
                totalQuantity += phanNho.soLuong;
                const doanhThu = phanNho.soLuong * phanNho.gia;
                totalRevenue += doanhThu;
                const loiNhuan = doanhThu > 0
                    ? ((doanhThu - ban.giaVon * phanNho.soLuong) / doanhThu) * 100
                    : 0;
                totalProfit += loiNhuan;
                profitCount += 1;
            });
        });
    });



    const averageProfit = profitCount > 0 ? totalProfit / profitCount : 0;
    return <div className="container mx-auto p-6 max-w-7xl">
        <div className="flex items-end">
            <img
                src={PrePage}
                onClick={() => {
                    setPick(false)
                }}
                alt="Ảnh sản phẩm"
                className="w-10 h-10 object-cover cursor-pointer mr-4 "
            />
            <i>* Hệ thống đang tính vốn - giá tồn kho dựa trên phương pháp tính FIFO</i>
        </div>
        <div className="flex flex-row items-end justify-between w-full mt-2">

            <img
                src={PreV}
                onClick={() => {
                    pre()
                }}
                alt="Ảnh sản phẩm"
                className="w-10 h-10 object-cover cursor-pointer mr-4 "
            />
            <img
                src={NextTo}
                onClick={() => {
                    next()
                }}
                alt="Ảnh sản phẩm"
                className="w-10 h-10 object-cover cursor-pointer"
            />
        </div>



        <div className="flex items-center space-x-4 p-4 bg-white shadow-md rounded-xl mb-3 mt-2">
            <img
                src={dataPick?.anhBia}
                alt="Ảnh sản phẩm"
                className="w-24 h-24 object-cover rounded-lg border"
            />
            <h1 className="text-2xl font-semibold text-gray-900">
                {dataPick?.tenSanPham}
            </h1>
        </div>

        <div className="mb-8 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200">
                <div className='flex flex-row items-end'>
                    <img className='w-10 h-10 mr-2' src={Cost}></img>
                    <h3 className="text-lg font-semibold text-gray-700">Tổng giá vốn</h3>
                </div>
                <p className="text-2xl font-bold text-gray-800">
                    {formatToVND(
                        dataPick?.thonTinDoanhThu?.reduce((total, product) =>
                            total + product.lanBan.reduce((sum, sale) =>
                                sum + sale.danhSachPhanNho.reduce((s, item) => s + item.soLuong * item.gia, 0), 0), 0)
                    )}
                </p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200">
                <div className='flex flex-row items-end'>
                    <img className='w-10 h-10 mr-2' src={TotalPrice}></img>
                    <h3 className="text-lg font-semibold text-gray-700">Tổng giá trị bán</h3>
                </div>

                <p className="text-2xl font-bold text-gray-800 ">
                    {formatToVND(
                        dataPick?.thonTinDoanhThu?.reduce((total, product) =>
                            total + product.lanBan.reduce((sum, sale) => sum + sale.tonGiaBan, 0), 0)
                    )}
                </p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200">
                <div className='flex flex-row items-end'>
                    <img className='w-10 h-10 mr-2' src={ToatalSale}></img>
                    <h3 className="text-lg font-semibold text-gray-700">Số lượng bán</h3>
                </div>

                <p className="text-2xl font-bold text-gray-800 text-center">
                    {dataPick?.thonTinDoanhThu?.reduce((total, product) =>
                        total + product.lanBan.reduce((sum, sale) =>
                            sum + sale.danhSachPhanNho.reduce((s, item) => s + item.soLuong, 0), 0), 0)}
                </p>
            </div>
            <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200">
                <div className='flex flex-row items-center'>
                    <img className='w-10 h-10 mr-2' src={RateReturn} />
                    <h3 className="text-lg font-semibold text-gray-700">Lợi nhuận trung bình</h3>
                </div>


                <p className="text-2xl font-bold text-gray-800 text-center">
                    {(() => {
                        const totalCost = dataPick?.thonTinDoanhThu?.reduce((total, product) =>
                            total + product.lanBan.reduce((sum, sale) =>
                                sum + sale.danhSachPhanNho.reduce((s, item) => s + item.soLuong * item.gia, 0), 0), 0) || 0;
                        const totalRevenue = dataPick?.thonTinDoanhThu?.reduce((total, product) =>
                            total + product.lanBan.reduce((sum, sale) => sum + sale.tonGiaBan, 0), 0) || 0;
                        return totalCost > 0 ? ((totalRevenue - totalCost) / totalCost * 100).toFixed(2) + '%' : '0%';
                    })()}
                </p>
            </div>
        </div>



        {dataPick?.thonTinDoanhThu?.map((product) => (
           <div key={product.id} className="mb-8">
    <h2 className="text-xl font-semibold text-gray-700 mb-3">{product.ten}</h2>
    {product.lanBan.length > 0 ? (
        <table className="min-w-full bg-white border border-gray-200 rounded-lg shadow-sm">
            <thead>
                <tr className="bg-gray-100 text-gray-600 text-sm font-medium border-b">
                    <th className="py-3 px-4 border-b text-left">Ngày bán</th>
                    <th className="py-3 px-4 border-b text-left">Số lượng</th>
                    <th className="py-3 px-4 border-b text-left">Giá nhập</th>
                    <th className="py-3 px-4 border-b text-left">Đơn giá bán</th>
                    <th className="py-3 px-4 border-b text-left">Tổng giá trị</th>
                    <th className="py-3 px-4 border-b text-left">Tỷ lệ lợi nhuận</th>
                </tr>
            </thead>
            <tbody>
                {product.lanBan.map((sale, saleIndex) => {
                    const rowSpan = sale.danhSachPhanNho.length;
                    const totalCost = sale.danhSachPhanNho.reduce((sum, item) => sum + item.soLuong * item.gia, 0);
                    const profitMargin = totalCost > 0 ? ((sale.tonGiaBan - totalCost) / totalCost * 100).toFixed(2) + '%' : '0%';

                    return sale.danhSachPhanNho.map((item, itemIndex) => (
                        <tr key={`${sale.e.hdId}-${itemIndex}`} className="text-gray-600 text-sm hover:bg-gray-50 border-b">
                            {itemIndex === 0 && (
                                <>
                                    <td className="py-2 px-4" rowSpan={rowSpan}>{dinhDangNgay(sale.ngayBan)}</td>
                                    <td className="py-2 px-4">{item.soLuong}</td>
                                    <td className="py-2 px-4">{formatToVND(item.gia)}</td>
                                    <td className="py-2 px-4" rowSpan={rowSpan}> {
                                        sale?.cartItemLasts?.map(m => {
                                            return <div>
                                                <p className="mb-2 flex">{formatToVND(m?.giaGiam)} {" "}

                                                            {m?.phanTramDealGiam!=0?<span className="ml-1 mr-1 text-pink-700"> {m?.phanTramDealGiam}%<img
                                                                className="w-5"
                                                                src="https://cdn-icons-png.flaticon.com/128/4772/4772843.png"
                                                                alt="deal icon"
                                                            /></span>:null}
                                                            {m?.phanTramFlashsale!=0?<span className="text-pink-700 ml-1 mr-1">{m?.phanTramFlashsale}% ( {m?.soLuong} ) <img
                                                                className="w-5"
                                                                src="data:image/png;base64,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"
                                                                alt="flashsale icon"
                                                            /></span>:null}

                                                </p>
                                            </div>
                                        })
                                    }</td>
                                    <td className="py-2 px-4" rowSpan={rowSpan}>{formatToVND(sale.tonGiaBan)}</td>
                                    <td className="py-2 px-4" rowSpan={rowSpan}>{profitMargin}</td>
                                </>
                            )}
                            {itemIndex > 0 && (
                                <>
                                    <td className="py-2 px-4">{item.soLuong}</td>
                                    <td className="py-2 px-4">{formatToVND(item.gia)}</td>
                                </>
                            )}
                        </tr>
                    ));
                })}
                {/* Total Row */}
                <tr className="bg-gray-100 font-semibold border-t">
                    <td className="py-2 px-4" colSpan="2">Tổng cộng 🏷️</td>
                    <td className="py-2 px-4">
                        {formatToVND(product.lanBan.reduce((sum, sale) =>
                            sum + sale.danhSachPhanNho.reduce((s, item) => s + item.soLuong * item.gia, 0), 0))}
                    </td>
                    <td className="py-2 px-4"></td>
                    <td className="py-2 px-4">
                        {formatToVND(product.lanBan.reduce((sum, sale) => sum + sale.tonGiaBan, 0))}
                    </td>
                    <td className="py-2 px-4">
                        {(() => {
                            const totalCost = product.lanBan.reduce((sum, sale) =>
                                sum + sale.danhSachPhanNho.reduce((s, item) => s + item.soLuong * item.gia, 0), 0) || 0;
                            const totalRevenue = product.lanBan.reduce((sum, sale) => sum + sale.tonGiaBan, 0) || 0;
                            return totalCost > 0 ? ((totalRevenue - totalCost) / totalCost * 100).toFixed(2) + '%' : '0%';
                        })()}
                    </td>
                </tr>
            </tbody>
        </table>
    ) : (
        <p className="text-gray-500 italic">Chưa có dữ liệu doanh thu cho phân loại này.</p>
    )}
</div>
        ))}
        {open && (
            <Modal setOpen={setOpen}>
                <ChiTietPhieuNhap id={idPhieu} />
            </Modal>
        )}
    </div>
}
export { ChiTietDoanhThuOfHoaDon }
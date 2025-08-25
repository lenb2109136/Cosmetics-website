import React, { useEffect, useState } from 'react';
import { dinhDangNgay, formatToVND } from '../../../utils/Format';
import { getPhanBoLoiNhuanOfChiTietHoaDon, getxuatgetxuatLoiNhuan } from '../../../services/sanPhamService';
import { Pagination } from '../../../components/commons/Pagination';
import IconExcel from "../../../assets/IconExcel.png"
import { ChiTietDoanhThuOfHoaDon } from '../../../components/admin/ChiTietDoanhThuOfHoaDon';

const XuatLoiNhuan = ({ bd, kt, sp,status }) => {

  const [data, setData] = useState([]);
  const [tong, setTong] = useState(0);
  const [tongSL, setTongSL] = useState(0);
  const [pick, setPick] = useState(false);
  const [select, setSelect] = useState(0);
  const [trangHienTai, setTrangHienTai] = useState(0);

  const totalVon = data.reduce((sum, product) =>
    sum + product.bienThe.reduce((subSum, variant) => subSum + variant.tonGiaBan, 0), 0);
  const totalLoiNhuan = data.reduce((sum, product) =>
    sum + product.bienThe.reduce((subSum, variant) => subSum + variant.loiNhuan, 0), 0);
  const totalBanRa =
    data.reduce((sum, product) =>
      sum + product.bienThe.reduce((subSum, variant) => subSum + variant.doanhThu, 0), 0);
  useEffect(() => {
    getxuatgetxuatLoiNhuan(bd, kt, 0, sp, trangHienTai,status).then((dat) => {
      setData(dat.content);
      setTongSL(dat?.totalElements);
      setTong(dat?.totalPages);
    });
  }, [bd, kt, sp, trangHienTai,status]);

  const nextProduct = () => {
    const currentIndex = data.findIndex((product) => product.id === select);
    if (currentIndex < data.length - 1) {
      setSelect(data[currentIndex + 1].id);
    } else {
      setSelect(data[0].id);
    }
  };
const prevProduct = () => {
    const currentIndex = data.findIndex((product) => product.id === select);
    if (currentIndex > 0) {
      setSelect(data[currentIndex - 1].id);
    } else {
      setSelect(data[data.length - 1].id);
    }
  };
  return (
    <div className="p-4 max-w-7xl mx-auto">
      <h1 className="text-2xl font-bold mb-4 text-center">Báo Cáo Lợi Nhuận</h1>
      <i>(* Lưu ý số lượng xuất bao gồm các hóa đơn ở trạng thái thành công, chờ xác nhận và trạng thái đã xác nhận không bao gồm các trạng thái đơn ở dạng <i className='text-red-500'>đơn hủy và đơn hoàn hàng</i>)</i>
      {
        !pick ? <>
          <div className='flex flex-row justify-between items-end w-full mt-3 mb-3'>
            <p className="mb-2 font-bold">Danh sách nhập kho chi tiết:</p>
            <a
              download
              href={`http://localhost:8080/api/sanpham/export/xuatloinhuan?bd=${bd}&kt=${kt}&trang=${trangHienTai}&dm=${0}&sp=${sp}&status=${status}`}
            >
              <div className='flex items-end'>
                <p className='hover:text-blue-400'><strong>Xuất thống kê</strong></p>
                <img src={IconExcel} className='w-10' alt='Export Excel' />
              </div>
            </a>
          </div>
          <div className="overflow-x-auto">
            <table className="min-w-full border border-gray-300">
              <thead className="bg-gray-100">
                <tr>
                  <th className="border border-gray-300 px-4 py-2 text-left text-sm font-medium text-gray-700">Tên Sản Phẩm</th>
                  <th className="border border-gray-300 px-4 py-2 text-left text-sm font-medium text-gray-700">Tên Biến Thể</th>
                  <th className="border border-gray-300 px-4 py-2 text-left text-sm font-medium text-gray-700">Vốn</th>
                  <th className="border border-gray-300 px-4 py-2 text-left text-sm font-medium text-gray-700">Số Lượng Bán</th>
                  <th className="border border-gray-300 px-4 py-2 text-left text-sm font-medium text-gray-700">Doanh Thu</th>
                  <th className="border border-gray-300 px-4 py-2 text-left text-sm font-medium text-gray-700">Lợi Nhuận</th>
                </tr>
              </thead>
              <tbody>
                {data?.map((product) =>
                  product.bienThe.map((variant, index) => (
                    <tr
                      key={`${product.id}-${index}`}
                      className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}
                    >
                      {index === 0 && (
                        <td
                          rowSpan={product.bienThe.length}
                          className="border border-gray-300 px-4 py-2 text-sm text-gray-900"
                        >
                          <p
                            className="cursor-pointer hover:text-blue-500"
                            onClick={() => {
                              setPick(true);
                              setSelect(product.id);
                            }}
                          >
                            {product.ten}
                          </p>
                        </td>
                      )}
                      <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{variant.ten}</td>
                      <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{formatToVND(variant.vonBan)}</td>
                      <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{variant.soLuongBan}</td>
                      <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{formatToVND(variant.doanhThu)}</td>
                      <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{formatToVND(variant.loiNhuan)}</td>
                    </tr>
                  ))
                )}
                <tr className="bg-gray-200 font-bold">
                  <td colSpan="2" className="border border-gray-300 px-4 py-2 text-sm text-gray-900">Tổng</td>
                  <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{formatToVND(totalVon)}</td>
                  <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">-</td>
                  <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{formatToVND(totalBanRa)}</td>
                  <td className="border border-gray-300 px-4 py-2 text-sm text-gray-900">{formatToVND(totalLoiNhuan)}</td>
                </tr>
              </tbody>
            </table>
            {tong > 0 && (
              <Pagination
                trangHienTai={trangHienTai}
                setTrangHienTai={setTrangHienTai}
                soLuongTrang={tong}
              />
            )}
          </div>
        </> : null
      }
      {pick ? <ChiTietDoanhThuOfHoaDon bd={bd} kt={kt} status={status} select={select} next={nextProduct} pre={prevProduct} setPick={setPick}></ChiTietDoanhThuOfHoaDon> : null}
    </div>
  );
};

export { XuatLoiNhuan };
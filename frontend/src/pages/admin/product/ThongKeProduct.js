import React, { useEffect, useState } from 'react';

import DateTimePicker from "../../../components/commons/PickDateTime"
import { formatDateToString, formatToVND } from '../../../utils/Format';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { getThongKeCoBan } from '../../../services/sanPhamService';
import { toast } from 'react-toastify';
import { TongQuanDoanhThu } from './TongQuanDoanhThu';
import { ChiTietDoanhThu, chiTietDoanhThu } from './ChiTietThongKeDoanhThu';
import { XuHuongTruyCap } from './XuHuongTruyCap';

const CupComponent = () => {
    const [batDau, setBatDau] = useState(() => {
        const d = new Date();
        d.setDate(d.getDate() - 30);
        return d;
    });
    const [pick, setPick] = useState(0)
    const [searchParams] = useSearchParams();
    const navigate = useNavigate()
    const [thongTinCoBan, setThongTinCoBan] = useState({})
    const id = searchParams.get('id');
    if (id == null) {
        navigate(-1)
    }

    const [ketThuc, setKetThuc] = useState(() => new Date());
    return (
        <>
            <div className='bg-white p-4 rounded-md mt-4'>
                <p className="text-left   mt-4 mb-2 ">
                    <i class="fa-solid fa-chart-simple text-blue-500 bg-blue-100 p-1"></i>
                    <strong className="text-lg mb-3"> Thống kê sản phẩm</strong>
                </p>
                <p >Thống kê tình trạng doanh thu sản phẩm</p>
            </div>
            <div className="flex items-center gap-4 mb-4 mt-4  p-4 border-b-1 border-blue-600 shadow-sm bg-white">
                <div className=''>

                    <p className="text-md font-bold  whitespace-nowrap mb-3">
                        <i class="fa-solid fa-timeline mr-1 text-yellow-500 bg-yellow-100 p-1"></i>
                        Chọn khung thời gian
                    </p>

                    <DateTimePicker
                        startDate={batDau}
                        endDate={ketThuc}
                        setStartDate={setBatDau}
                        setEndDate={setKetThuc}
                    />
                </div>

                <div className="flex space-x-4 mt-8">
  {[
    { label: "Tổng quan doanh thu", icon: "fa-house" },
    { label: "Chi tiết doanh thu", icon: "fa-clipboard-list" },
  ].map((item, idx) => (
    <div
      key={idx}
      onClick={() => setPick(idx)}
      className={`
        cursor-pointer px-4 py-3 rounded-lg transition-all duration-300
        flex items-center space-x-2
        ${pick === idx ? "bg-blue-100 text-blue-700 font-semibold" : "text-gray-600 hover:bg-gray-100 hover:text-blue-600"}
      `}
    >
      {item.icon && (
        <i className={`fa-solid ${item.icon} text-[18px]`}></i>
      )}
      <span className="text-sm">{item.label}</span>
    </div>
  ))}
</div>

            </div>

            {pick == 0 ? <TongQuanDoanhThu setcb={setThongTinCoBan} batDau={batDau} ketThuc={ketThuc} id={id}></TongQuanDoanhThu> : null}
            {pick == 1 ? <ChiTietDoanhThu setcb={setThongTinCoBan} batDau={batDau} ketThuc={ketThuc} id={id} bienThe={thongTinCoBan.bienThe}></ChiTietDoanhThu> : null}
            
        </>

    );
};

export { CupComponent };

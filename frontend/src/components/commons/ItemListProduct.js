import React from 'react';
import { useNavigate } from 'react-router-dom';

const ProductItem = ({ product }) => {
    const navigate = useNavigate()
    const {
        phanTramGiam,
        soLuotDanhGia,
        ten,
        tenThuonHieu,
        giaMax,
        giaMin,
        anhGioiThieu,
        soSao,
        id
    } = product;

    return (
        <div className="w-full p-4 h-47 ml-4 bg-white shadow-md rounded-lg flex flex-col items-center">
            <img
                src={anhGioiThieu}
                alt={ten}
                className="w-full object-cover rounded-md cursor-pointer"
                onClick={()=>{
                    navigate(`/customer/detailproduct/${id}`)
                }}

            />
            <div className="mt-2 w-full">
               <div className="flex justify-between items-center">
  <p className="font-bold text-orange-600">
    {giaMin === giaMax
      ? `${giaMin.toLocaleString()} đ`
      : `${giaMin.toLocaleString()} - ${giaMax.toLocaleString()} đ`}
  </p>
  <p className="bg-pink-600 text-white px-2 py-1 rounded-sm">
    {phanTramGiam}%
  </p>
</div>


                <p className="text-sm text-green-900 font-bold text-start cursor-pointer">
                    {tenThuonHieu}
                </p>

                <h3 
  className="text-md text-start overflow-hidden whitespace-nowrap text-ellipsis" 
  style={{ textOverflow: 'ellipsis' }}
>
  {ten}
</h3>

                <div className="flex items-center">
                    <div className="text-yellow-500 flex">
                        {'★'.repeat(Math.floor(soSao)) + '☆'.repeat(5 - Math.floor(soSao))}
                    </div>
                    <span className="ml-1 text-sm text-gray-600">({soLuotDanhGia})</span>
                </div>
            </div>
        </div>
    );
};

export { ProductItem };
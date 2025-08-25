import React, { useRef, useState } from "react";
import { changeClassifyTenByIndex, CheckClassify, Classify, ClassifyItem, Product, removeClassifyById, removeDuplicatesById, Variant } from "../../models/classify"
import gsap from "gsap";

function PickClassify({ product, setProduct, imagevariant, setimagevariant }) {
  const [create, setcreate] = useState(false)
  const [tenPhanLoai, setTenPhanLoai] = useState("")
  const vitrixoa = useRef([])
  if (vitrixoa.current.length > 0) {
    let newImageVariant = imagevariant.filter((_, index) => !vitrixoa.current.includes(index))
    vitrixoa.current = []
    newImageVariant = newImageVariant.filter(item => item && item !== '' && item !== null && item !== undefined)

    setimagevariant(newImageVariant)
  }
  console.log(vitrixoa.current)
  const [variantcreate, setvariantcreate] = useState([])
  return <div>
    <div>
      

      <div>
        <div className="flex flex-row items-center space-x-2 mt-7 mb-5">
          <div className="relative">
            <span className="absolute inline-flex h-4 w-4 animate-ping rounded-full bg-sky-400 opacity-75"></span>
            <span className="inline-flex h-4 w-4 rounded-full bg-sky-500"></span>
          </div>
          <strong><p className="mt-0">Phân Loại</p></strong>
        </div>
      </div>
      <table className="min-w-full border border-gray-400 rounded-lg overflow-hidden text-sm mt-4">
        <thead className="bg-blue-100 text-gray-700 uppercase tracking-wide">
          <tr className="border border-gray-300">
            <th className="px-4 py-2 border-r text-center border border-gray-300">Ảnh phân loại</th>
            <th className="px-4 py-2 border-r text-center border border-gray-300">Tên phân loại</th>
            <th className="px-4 py-2 text-center border border-gray-300">Giá</th>
            <th className="px-4 py-2 border-r text-center border border-gray-300">Bán lẻ</th>
            <th className="px-4 py-2 border-r text-center border border-gray-300">Tỉ Lệ bán lẻ</th>
            <th className="px-4 py-2 border-r text-center border border-gray-300">Phân loại bán lẻ</th>
          </tr>
        </thead>
        <tbody className="text-gray-800">
          {
            product?.map.bienTheKhongLe?.map((data, index) => {
              <tr
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
                
                <td className="px-4 py-3 border-r text-center relative border border-gray-300">
                  <span className="block">{data.ten}</span>
                  
                </td>

              </tr>
            })
          }
        </tbody>
      </table>
      {product?.variant?.length == 0 ? <i><p className="text-center mt-3">Chưa có dữ liệu phân loại</p></i> : null}





    </div>
  </div>
}
export { PickClassify }
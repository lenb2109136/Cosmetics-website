import { useState } from "react"
import { getThongSoById, saveThongSo, update } from "../../../services/ThongSoService"
import { toast } from "react-toastify"
import { useNavigate, useSearchParams } from "react-router-dom"

function AddParameter({id}) {
    
    const [thongSo, setThongSo] = useState({
        ten: "",
        thongSoCuThe: []
    })
    const navigate =useNavigate()
    const [selectedIndex, setSelectedIndex] = useState(-1)
    if(id!=null&&thongSo?.ten==""){
        getThongSoById(id).then((data)=>{
            setThongSo(data)
        }).catch(()=>{
            navigate(-1)
        })
    }
    const handleAdd = () => {
        const input = document.getElementById("giatri")
        const value = input?.value?.trim()
        if (!value) return

        if (selectedIndex === -1) {
            setThongSo(prev => ({
                ...prev,
                thongSoCuThe: [...prev.thongSoCuThe, { ten: value }]
            }))
        } else {
            setThongSo(prev => {
                const updated = [...prev.thongSoCuThe]
                updated[selectedIndex] = { ten: value }
                return { ...prev, thongSoCuThe: updated }
            })
        }

        input.value = ""
        setSelectedIndex(-1)
    }

    const handleRemove = (i) => {
        setThongSo(prev => ({
            ...prev,
            thongSoCuThe: prev.thongSoCuThe.filter((_, idx) => idx !== i)
        }))
        if (selectedIndex === i) {
            setSelectedIndex(-1)
        }
    }

    const toggleSelect = (idx) => {
        const input = document.getElementById("giatri")
        if (selectedIndex === idx) {
            setSelectedIndex(-1)
            input.value = ""
        } else {
            setSelectedIndex(idx)
            input.value = thongSo.thongSoCuThe[idx]?.ten
        }
    }

    const handleSubmit = () => {
       if(id==null){
         saveThongSo(thongSo)
            .then(() => toast.success("Lưu thông tin thành công"))
            .catch((e) => {
                const message =
                    e?.response?.data?.message || "Lỗi khi lưu thông tin"
                toast.error(message)
            })
       }
       else{
        update(thongSo).then(()=>{
            toast.success("Cập nhật thông tin thành công")
        }).catch((err)=>{
            toast.error(err.response.data.message)
        })
       }
    }

    return (
        <div className="p-3 max-w-4xl mx-auto">
            <h3 className="text-xl font-bold mb-6">Thông tin Thông số</h3>

            <div className="mb-6">
                <p className="ml-1 mr-2 text-green-900 rounded-sm pl-2 pr-2 w-fit mb-2 pt-1 pb-1"><i class="fa-solid fa-indent "></i> Tên thông số</p>
                <input
                    value={thongSo?.ten}
                    onChange={(e) =>
                        setThongSo(prev => ({ ...prev, ten: e.target.value }))
                    }
                    type="text"
                    className="rounded-lg outline-none w-full h-12 border border-gray-300 pl-4 transition-all duration-300 
                        hover:border-green-900  focus:outline-none shadow-sm"
                />
            </div>

            <div className="mb-8 flex flex-row items-center space-x-2">
                <input
                    id="giatri"
                    type="text"
                    placeholder="Giá trị thông số"
                    className="rounded-lg outline-none flex-grow h-12 border border-gray-300 pl-4 transition-all duration-300 
                        hover:border-green-900  focus:outline-none shadow-sm"
                />
                <button
                    onClick={handleAdd}
                    className="  text-green-900  font-semibold py-2 px-3 rounded-lg shadow transition-all duration-300"
                >
                    {selectedIndex !== -1 ? "Cập nhật" : "+ Thêm"}
                </button>
            </div>

            <div className="flex flex-wrap w-[500px] mb-8">
                {thongSo.thongSoCuThe.map((item, idx) => {
                    const isSelected = selectedIndex === idx
                    return (
                        <div
                            key={idx}
                            className={`relative group px-4 py-2 rounded-xl shadow transition-all mr-2 mb-3 duration-300 cursor-pointer ${
                                isSelected
                                    ? " border border-green-900"
                                    : "bg-gray-100 hover:shadow-md"
                            }`}
                            onClick={() => toggleSelect(idx)}
                        >
                            <p className="text-gray-800">{item?.ten}</p>
                            <button
                                onClick={(e) => {
                                    e.stopPropagation()
                                    handleRemove(idx)
                                }}
                                className="absolute top-[-8px] right-[-8px] bg-green-900 text-white  rounded-full w-6 h-6 text-sm font-bold 
                                    flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 shadow-md hover:bg-blue-100"
                                title="Xóa"
                            >
                                ×
                            </button>
                        </div>
                    )
                })}
            </div>

            <button
                onClick={handleSubmit}
                className="bg-green-900  text-white font-semibold py-2 px-3 rounded-lg shadow transition-all duration-300"
            >
                {id!=null?"Cập nhật":"Lưu thông số"}
            </button>
        </div>
    )
}

export { AddParameter }

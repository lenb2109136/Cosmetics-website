import { useEffect, useState } from "react"
import { CustomCalendar } from "../../../components/commons/PickDateTime"
import Modal from "../../../components/commons/modal"
import { PickProduct } from "../../../components/admin/PickProduct"
import { dinhDangNgay, formatToVND } from "../../../utils/Format"
import { getDeal, saveDeal, updateDeal } from "../../../services/DealService"
import { toast } from "react-toastify"
import { useSearchParams } from "react-router-dom"
import { getBonus, updateBonus } from "../../../services/BonusService"
function isPastTime(timeString) {
    const targetTime = new Date(timeString);
    const now = new Date();
    return now > targetTime;
}
function UpdateBonus() {

    const [bd, setbd] = useState("")
    const [danhSachChon, setDanhSachChon] = useState([])
    const [danhSachChonPhu, setDanhSachChonPhu] = useState([])
    const [open, setOpen] = useState(false)
    const [openDealPhu, setOpenDealPhu] = useState(false)
    const [opentrung, setopentrung] = useState(false)
    const [searchParams] = useSearchParams();

    const id = searchParams.get("id");
    const [load, setLoad] = useState(false)
    const [dealtrung, setdealtrung] = useState
        ({ data: [], dataPhu: [] })
    const [result, setResult] = useState({
        tenChuongTrinh: "",
        ngayBatDau: "",
        ngayKetThuc: "",
        thoiGianBatDau: "",
        thoiGianKetThuc: "",
        data: [],
        soLuongGioiHan: 0
    });
    useEffect(() => {
        getBonus(id).then((d) => {
            setResult(d)
            setDanhSachChon(d.data)
            setDanhSachChonPhu(d.dataPhu)
        })
    }, [])
    return <div>
        <h3 className="text-2xl ml-3 mt-2 text-blue-500">Thông tin khuyến mãi</h3>
        <div>
            <button onClick={() => {
                result.data = danhSachChon
                result.dataPhu = danhSachChonPhu
                updateBonus(result,id).then(() => toast.success("Cập nhật thông tin khuyến mãi thành công")).catch((e) => {
                    toast.error(e?.response?.data?.message || "Cập nhật khuyến mãi thất bại")
                })
            }} className={`${isPastTime(result.ngayKetThuc)?"bg-gray-300 pointer-events-none":"bg-gradient-to-r from-blue-500 to-blue-700"} ml-4 mt-2  text-white px-4 py-1 rounded hover:opacity-90 transition-all duration-300`}>
                Cập nhật
            </button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-6 bg-white rounded-lg shadow-sm w-full mb-4">
           

            <div className="flex flex-col">
                <label className="mb-2 font-semibold text-gray-700">Số lượt giới hạn: ( đã dùng {result?.soLuongDaDung} lượt )</label>
                <input
                    min={result?.soLuongDaDung}
                    value={result.soLuongGioiHan}
                    onChange={(e) => {
                        setResult((prev) => ({
                            ...prev,
                            soLuongGioiHan: e.target.value
                        }));
                    }}
                    type="number"
                    className="border border-gray-300 rounded-md px-4 py-2 outline-none focus:ring-2 focus:ring-blue-400"
                />
            </div>

            <div className="flex flex-col">
                <label className="mb-2 font-semibold text-gray-700">Thời gian bắt đầu:</label>
                <input
                    value={result.ngayBatDau}
                    onChange={(e) => {
                        setResult((prev) => ({
                            ...prev,
                            ngayBatDau: e.target.value
                        }));
                    }}
                    type="datetime-local"
                    className="focus:border-[0.5px] border-gray-300 rounded-md px-4 py-2 outline-none focus:ring-2 focus:ring-blue-400"
                />
            </div>

            <div className="flex flex-col">
                <label className="mb-2 font-semibold text-gray-700">Thời gian kết thúc:</label>
                <input
                    value={result.ngayKetThuc}
                    onChange={(e) => {
                        setResult((prev) => ({
                            ...prev,
                            ngayKetThuc: e.target.value
                        }));
                    }}
                    type="datetime-local"
                    className="border border-gray-300 rounded-md px-4 py-2 outline-none focus:ring-2 focus:ring-blue-400"
                />
            </div>
        </div>


        <div>
            <div className="w-full ml-3">
                <strong><p>Sản phẩm chính: </p></strong>
                <p className="mt-4 bg-gradient-to-r from-blue-500 to-blue-700 text-white px-4 py-1 rounded hover:opacity-90 transition-all duration-300 cursor-pointer max-w-[170px] mb-4 mt-4" onClick={() => {
                    setOpen(true)
                }}>+ Thêm sản phẩm</p>

            </div>
            <table className="w-full max-h-[300px]">
                <tr className="bg-blue-100 text-center">
                    <td><strong>Sản phẩm</strong></td>
                    <td ><strong>Giá</strong></td>
                    <td><strong>Số lượng tối được giảm</strong></td>
                    <td ><strong>Số lượng kho</strong></td>
                    <td ><strong>Hoạt động</strong></td>
                </tr>
                {danhSachChon?.length == 0 ? <tr><td colSpan={4}><i><p className="text-center mt-3">Chưa thêm dữ liệu sản phẩm chính </p></i></td></tr> : null}
                {
                    danhSachChon.map((d, index) => {
                        return <>
                            <tr className="bg-gray-100 text-center">
                                <td colSpan={5}>
                                    <div className="flex fex-row items-center">
                                        <img src={d.hinhAnh} className=" m-2 w-[70px] h-[70px] object-cover rounded"></img>

                                        <p className="pl-3 ">{d.ten}</p>

                                    </div>

                                </td>
                            </tr>

                            {
                                d.bienThe?.map((dd, btIndex) => {
                                    return <tr className="text-center items-center hover:bg-gray-200 duration-200 relative">
                                        <td >
                                            {
                                                dd.notUpdate?<div className="absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[2px]">
                                                <div className="bg-white bg-opacity-80 border-2 border-red-500 text-red-500 font-semibold px-4 py-2 rounded">
                                                    Không còn sử dụng
                                                </div>
                                            </div>:null
                                            }
                                            <div className="flex fex-row items-center">
                                                <img src={dd.hinhAnh} className="ml-4 m-2 w-[70px] h-[70px] object-cover rounded"></img>
                                                <div>
                                                    <p className="pl-3 text-start">{dd.ten}</p>
                                                    {
                                                        dd?.dealPhu?.length != 0 || dd.dealChinh?.length != 0 ? <svg
                                                            className="ml-2 pt-3 duration-500 group-hover:rotate-[360deg] group-hover:scale-110 w-[5%] cursor-pointer"
                                                            viewBox="0 0 24 24"
                                                            fill="red"
                                                            xmlns="http://www.w3.org/2000/svg"
                                                            onClick={() => {
                                                                setdealtrung({
                                                                    data: dd.dealChinh,
                                                                    dataPhu: dd.dealPhu
                                                                })
                                                                setopentrung(true)
                                                            }}
                                                        >
                                                            <path
                                                                d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z"
                                                            ></path>
                                                        </svg> : null
                                                    }
                                                </div>
                                            </div>
                                        </td>
                                        <td >{formatToVND(dd.gia)}</td>
                                        <td><input onChange={(e) => {
                                            const newSoLuongKhuyenMai = parseFloat(e.target.value);
                                            if (newSoLuongKhuyenMai <= 99) {
                                                let a = [...danhSachChon];
                                                a[index] = { ...a[index] };
                                                a[index].bienThe = [...a[index].bienThe];
                                                a[index].bienThe[btIndex] = {
                                                    ...a[index].bienThe[btIndex],
                                                    soLuongKhuyenMai: newSoLuongKhuyenMai,
                                                    conSuDung:e.target.value==0?false:true
                                                };
                                                setDanhSachChon(a);
                                            }
                                        }}
                                            defaultValue={dd?.soLuongKhuyenMai || 0} placeholder="Số lượng giảm tối đa trên đơn" min={0} className="outline-none border border-gray-200 pl-2" type="number"></input></td>
                                        <td >{dd.soLuongKho}</td>
                                        <td>{dd?.ready ? <button onClick={() => {
                                            dd.conSuDung = !dd.conSuDung;
                                            setLoad(!load)
                                        }} className={`${dd.conSuDung ? "bg-green-500" : "bg-gray-300"} pl-2 pr-2 pt-1 pb-1 rounded-sm
                                                ${dd.soLuongKhuyenMai > 0 ? null : "blur-[1px] pointer-events-none"}
                                                text-white`
                                        }><strong>Hoạt động</strong></button> : null}</td>
                                    </tr>
                                })
                            }
                        </>
                    })
                }
            </table>
        </div>
        <div>
            <strong><p className="mt-5">Sản phẩm tặng kèm</p></strong>
            <p className="bg-gradient-to-r from-blue-500 to-blue-700 text-white px-4 py-1 rounded hover:opacity-90 transition-all duration-300 cursor-pointer max-w-[170px] mb-4 mt-4" onClick={() => {
                setOpenDealPhu(true)
            }}>+ Thêm sản phẩm</p>
            <table className="w-full">
                <tr className="bg-blue-100 text-center">
                    <td><strong>Sản phẩm</strong></td>
                    <td><strong>Giá bán hiện tại</strong></td>
                    <td><strong>Số lượng tặng</strong></td>
                    <td><strong>Số lượng kho</strong></td>
                    <td><strong>Hoạt động</strong></td>
                </tr>
                {danhSachChonPhu?.length == 0 ? <tr><td colSpan={5}><i><p className="text-center mt-3">Chưa thêm dữ liệu sản phẩm đi kèm </p></i></td></tr> : null}
                {
                    danhSachChonPhu.map((d, index) => {
                        return <>
                            <tr className="bg-gray-100">
                                <td colSpan={7}>
                                    <div className="flex fex-row items-center">
                                        <img src={d.hinhAnh} className="m-2 w-[70px] h-[70px] object-cover rounded"></img>

                                        <p className="pl-3 ">{d.ten}</p>

                                    </div>

                                </td>
                            </tr>
                            {
                                d.bienThe?.map((dd, btIndex) => {
                                    return <tr className="text-center hover:bg-gray-200 duration-200 relative">
                                        <td>
                                            {
                                                dd.notUpdate?<div className="absolute inset-0 flex items-center justify-center z-10 backdrop-blur-[2px]">
                                                <div className="bg-white bg-opacity-80 border-2 border-red-500 text-red-500 font-semibold px-4 py-2 rounded">
                                                    Không còn sử dụng
                                                </div>
                                            </div>:null
                                            }
                                            <div className="flex fex-row items-center">
                                                <img src={dd.hinhAnh} className="ml-4 m-2 w-[70px] h-[70px] object-cover rounded"></img>
                                                <div>
                                                    <p className="pl-3 text-start">{dd.ten}</p>
                                                    {
                                                        dd?.dealPhu?.length != 0 || dd.dealChinh?.length != 0 ? <svg
                                                            className="ml-2 pt-3 duration-500 group-hover:rotate-[360deg] group-hover:scale-110 w-[5%] cursor-pointer"
                                                            viewBox="0 0 24 24"
                                                            fill="red"
                                                            xmlns="http://www.w3.org/2000/svg"
                                                            onClick={() => {
                                                                setdealtrung({
                                                                    data: dd.dealChinh,
                                                                    dataPhu: dd.dealPhu
                                                                })
                                                                setopentrung(true)
                                                            }}
                                                        >
                                                            <path
                                                                d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm0 22c-5.518 0-10-4.482-10-10s4.482-10 10-10 10 4.482 10 10-4.482 10-10 10zm-1-16h2v6h-2zm0 8h2v2h-2z"
                                                            ></path>
                                                        </svg> : null
                                                    }
                                                </div>
                                            </div>
                                        </td>
                                        <td>{formatToVND(dd.gia)}</td>
                                        {/* <td><input
                                            min={0} max={100} defaultValue={dd?.giaGiam || 0}
                                            onChange={(e) => {
                                                const newGiaGiam = parseFloat(e.target.value);
                                                if (newGiaGiam <= 99) {
                                                    let a = [...danhSachChonPhu];
                                                    a[index] = { ...a[index] };
                                                    a[index].bienThe = [...a[index].bienThe];
                                                    a[index].bienThe[btIndex] = {
                                                        ...a[index].bienThe[btIndex],
                                                        giaGiam: newGiaGiam,
                                                        conSuDung:(newGiaGiam==0||dd.soLuongKhuyenMai==0)?false:true
                                                    };
                                                    setDanhSachChonPhu(a);
                                                }
                                            }} placeholder="Tỉ lệ giảm khi mua kèm" className="outline-none border border-gray-200 pl-2" type="number"></input></td> */}
                                        <td><input
                                            min={0}
                                            onChange={(e) => {
                                                const newSoLuongKhuyenMai = parseFloat(e.target.value);
                                                if (newSoLuongKhuyenMai <= 99) {
                                                    let a = [...danhSachChonPhu];
                                                    a[index] = { ...a[index] };
                                                    a[index].bienThe = [...a[index].bienThe];
                                                    a[index].bienThe[btIndex] = {
                                                        ...a[index].bienThe[btIndex],
                                                        soLuongKhuyenMai: newSoLuongKhuyenMai,
                                                        conSuDung:(newSoLuongKhuyenMai==0)?false:true
                                                    };
                                                    setDanhSachChonPhu(a);
                                                }
                                            }}
                                            defaultValue={dd?.soLuongKhuyenMai || 0} placeholder="Số lượng giảm tối đa trên đơn" className="outline-none border border-gray-200 pl-2" type="number"></input></td>
                                        <td>{dd.soLuongKho}</td>
                                        <td>{dd?.ready ? <button onClick={() => {
                                            dd.conSuDung = !dd.conSuDung;
                                            setLoad(!load)
                                        }} className={`${dd.conSuDung ? "bg-green-500" : "bg-gray-300"} pl-2 pr-2 pt-1 pb-1 rounded-sm
                                                ${(dd.soLuongKhuyenMai>0) ? null : "blur-[1px] pointer-events-none"}
                                                text-white`
                                        }><strong>Hoạt động</strong></button> : null}</td>
                                    </tr>
                                })
                            }
                        </>
                    })
                }

            </table>
        </div>
        {open ? <Modal setOpen={setOpen}>
            <PickProduct danhSachChon={danhSachChon} filter={danhSachChonPhu || []} setDanhSachChon={setDanhSachChon} task={"deal"} />
        </Modal> : null}
        {openDealPhu ? <Modal setOpen={setOpenDealPhu}>
            <PickProduct danhSachChon={danhSachChonPhu} filter={danhSachChon} setDanhSachChon={setDanhSachChonPhu} task={"deal"} />
        </Modal> : null}
        {
            opentrung ? <Modal setOpen={setopentrung}>
                <div className="w-[800px] rounded-sm">
                    <strong><p className="text-xl text-blue-700 text-center">Thông tin Lưu ý</p></strong>
                    <section className="mb-8 w-full">
                        <p className="text-lg font-semibold mb-3 text-blue-700">Sản phẩm chính</p>
                        <div className="overflow-x-auto">
                            <table className="min-w-full text-sm text-left text-gray-800 border">
                                <thead className="bg-gray-200 font-semibold">
                                    <tr>
                                        <th className="py-2 px-3 border">STT</th>
                                        <th className="py-2 px-3 border">Thời gian chạy deal</th>
                                        <th className="py-2 px-3 border">Thời gian ngưng deal</th>
                                        <th className="py-2 px-3 border">Số lượt giới hạn</th>
                                        <th className="py-2 px-3 border">Số lượt đã dùng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {dealtrung?.data?.length === 0 ? (
                                        <tr>
                                            <td colSpan={5} className="text-center italic text-gray-500 py-4">Không có dữ liệu</td>
                                        </tr>
                                    ) : (
                                        dealtrung?.data?.map((data, index) => (
                                            <tr key={index} className="hover:bg-gray-100">
                                                <td className="py-2 px-3 border">{index + 1}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianChay)}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianNgung)}</td>
                                                <td className="py-2 px-3 border">{data.soLuotGioiHan}</td>
                                                <td className="py-2 px-3 border">{data.soLuongDaDung}</td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </section>
                    <section>
                        <p className="text-lg font-semibold mb-3 text-blue-700">Sản phẩm tặng kèm</p>
                        <div className="overflow-x-auto">
                            <table className="min-w-full text-sm text-left text-gray-800 border">
                                <thead className="bg-gray-200 font-semibold">
                                    <tr>
                                        <th className="py-2 px-3 border">STT</th>
                                        <th className="py-2 px-3 border">Thời gian chạy deal</th>
                                        <th className="py-2 px-3 border">Thời gian ngưng deal</th>
                                        <th className="py-2 px-3 border">Số lượt giới hạn</th>
                                        <th className="py-2 px-3 border">Số lượt đã dùng</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {dealtrung?.dataPhu?.length === 0 ? (
                                        <tr>
                                            <td colSpan={5} className="text-center italic text-gray-500 py-4">Không có dữ liệu</td>
                                        </tr>
                                    ) : (
                                        dealtrung?.dataPhu?.map((data, index) => (
                                            <tr key={index} className="hover:bg-gray-100">
                                                <td className="py-2 px-3 border">{index + 1}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianChay)}</td>
                                                <td className="py-2 px-3 border">{dinhDangNgay(data.thoiGianNgung)}</td>
                                                <td className="py-2 px-3 border">{data.soLuotGioiHan}</td>
                                                <td className="py-2 px-3 border">{data.soLuongDaDung}</td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
            </Modal> : null
        }
    </div>
}
export { UpdateBonus }
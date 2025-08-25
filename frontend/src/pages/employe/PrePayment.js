import { useEffect, useState } from "react"
import { getPrevPayment } from "../../services/OrderService"
import { toast } from "react-toastify"
import { QuetMaThanhToan } from "./quetMaThanhToan"

function PrevPayment({id,setOpenp}){
    const [open,setopen]=useState(false)
    const [datacheck,setDataCheck]=useState({})
    useEffect(()=>{
        getPrevPayment(id).then(data=>{setDataCheck(data); setopen(true)})
        .catch(e=>{toast.error(e?.response?.data?.message || "Tạo thanh toán thất bại vui lòng thử lại")})
    },[])
    return <div>

        {datacheck && open ?<QuetMaThanhToan  da={datacheck} setOpen={setopen}></QuetMaThanhToan>:null}
    </div>
}
export {PrevPayment}
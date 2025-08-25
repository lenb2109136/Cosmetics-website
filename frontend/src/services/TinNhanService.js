
import { APIPrivate, APIPublic } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function getInitEmployee(){
    return APIPrivate.get(API_ROUTES.BUSSINESS_TINNHAN+"/getinit").then((d)=>d.data.data)
}

function getTinNhanOfKhacHang(id,idTinNhan){
    return APIPrivate.get(API_ROUTES.BUSSINESS_TINNHAN+`/getby-khachhang?id=${id}&idtinnhan=${idTinNhan}`).then((d)=>d.data.data)
}
export {getInitEmployee,getTinNhanOfKhacHang}
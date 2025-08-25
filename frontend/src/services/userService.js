import { toast } from "react-toastify";
import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function getInfo(){
    return APIPrivate.get(API_ROUTES.BUSSINESS_NGUOIDUNG).then((d)=>d.data.data).catch((e)=>{toast.error("Lấy thông tin người dùng thất bại")});
}
 export {getInfo}
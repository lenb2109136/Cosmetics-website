import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function save(noiDungPhanHoi,soSao,sanPham){
   return  APIPrivate.post(API_ROUTES.BUSSINESS_COMMENT+`/save`,{
    noiDung:noiDungPhanHoi,
    soSao:soSao,
    sanPham:sanPham
   }).then()
}
export {save}


import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function getKhachHang(sdt) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_NGUOIDUNG+`/getkhbysdt?sdt=`+sdt).then((e)=>e.data.data)
}
function update(data) {
    return APIPrivate.put(API_ROUTES.BUSSINESS_NGUOIDUNG,data).then()
}
export {getKhachHang,update}
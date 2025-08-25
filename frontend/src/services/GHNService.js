import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";


function tinhPhiGiaoHang(diachi, data) {
    const encodedAddress = encodeURIComponent(diachi);
    return APIPrivate.post(
        `${API_ROUTES.BUSSINESS_ORDER}/tinhPhiGiaoHang?addr=${encodedAddress}`,
        data
    ).then(d=>d?.data?.data);
}
export {tinhPhiGiaoHang}
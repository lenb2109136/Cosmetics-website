
import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function saveDeal(data) {
    return APIPrivate.post(API_ROUTES.BUSSINESS_DEAL,data)
}
function getDeal(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_DEAL+`/getdeal/${id}`).then(d=>d.data.data)
}
function updateDeal(data,id) {
    return APIPrivate.post(API_ROUTES.BUSSINESS_DEAL+`/update/${id}`,data)
}
function getByStatusDeal(trang,bd,kt,status){
    return APIPrivate.get(API_ROUTES.BUSSINESS_DEAL+`/getbystatus/${status}?bd=${bd}&kt=${kt}&trang=${trang}`).then(data=>  data.data.data);
}
function getThongKe(id){
     return APIPrivate.get(API_ROUTES.BUSSINESS_DEAL+`/thongke/${id}`).then(data=>  data.data.data);
}
function setActiveDeal(id,active){
    return APIPrivate.get(API_ROUTES.BUSSINESS_DEAL+`/active/${id}?active=${active}`);
}
export {saveDeal,getDeal,updateDeal,getByStatusDeal,setActiveDeal,getThongKe}

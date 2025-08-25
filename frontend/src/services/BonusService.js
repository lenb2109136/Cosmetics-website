
import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function saveBonus(data) {
    return APIPrivate.post(API_ROUTES.BUSSINESS_BONUS,data)
}
function getBonus(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_BONUS+`/getbonus/${id}`).then(d=>d.data.data)
}
function updateBonus(data,id) {
    return APIPrivate.post(API_ROUTES.BUSSINESS_BONUS+`/update/${id}`,data)
}
function getByStatusBonus(trang,bd,kt,status){
    return APIPrivate.get(API_ROUTES.BUSSINESS_BONUS+`/getbystatus/${status}?bd=${bd}&kt=${kt}&trang=${trang}`).then(data=>  data.data.data);
}
function setActiveBonus(id,active){
    return APIPrivate.get(API_ROUTES.BUSSINESS_BONUS+`/active/${id}?active=${active}`);
}
export {saveBonus,getBonus,updateBonus,getByStatusBonus,setActiveBonus}
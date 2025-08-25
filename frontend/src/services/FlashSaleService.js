
import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function saveFlashSale(data) {
    return APIPrivate.post(API_ROUTES.BUSSINESS_FLASHSALE,data)
}
function getFlash(id){
 return APIPrivate.get(API_ROUTES.BUSSINESS_FLASHSALE+`/getflashbyid/${id}`).then(data=>  data.data.data);
}

function getIDBienThe(id){
 return APIPrivate.get(API_ROUTES.BUSSINESS_FLASHSALE+`/getbientheofflash/${id}`).then(data=>  data.data.data);
}
function updateFlash(dto,id){
 return APIPrivate.post(API_ROUTES.BUSSINESS_FLASHSALE+`/update/${id}`,dto);
}

function getThongKe(id){
     return APIPrivate.get(API_ROUTES.BUSSINESS_FLASHSALE+`/thongke/${id}`).then(data=>  data.data.data);
}

function getByStatus(trang,bd,kt,status){
    return APIPrivate.get(API_ROUTES.BUSSINESS_FLASHSALE+`/getbystatus/${status}?bd=${bd}&kt=${kt}&trang=${trang}`).then(data=>  data.data.data);
}
function setActive(id,active){
    return APIPrivate.get(API_ROUTES.BUSSINESS_FLASHSALE+`/active/${id}?active=${active}`);
}

export {saveFlashSale,getFlash,updateFlash,getIDBienThe,getByStatus,setActive,getThongKe}
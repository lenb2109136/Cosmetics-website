import { APIPrivate, APIPublic } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";
function getThuongHieu(){
    return APIPrivate.get(API_ROUTES.BUSSINESS_THUONGHIEU+"/get").then((response)=>{
        return response.data.data;
    })
}
function getByCondition(ten,id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_THUONGHIEU+`/getCondition?ten=${ten}&id=${id}`).then((response)=>{
        return response.data.data;
    })
    
}
function getInfo(id){
     return APIPrivate.get(API_ROUTES.BUSSINESS_THUONGHIEU+`/getinfo/${id}`).then((response)=>{
        return response.data.data;
    })
    
}
function getAll(ten){
     return APIPrivate.get(API_ROUTES.BUSSINESS_THUONGHIEU+`/getall?ten=`+ten).then((response)=>{
        return response.data.data;
    })
    
}
function getManager(ten,trang){
     return APIPrivate.get(API_ROUTES.BUSSINESS_THUONGHIEU+`/getmanager?ten=`+ten+"&trang="+trang).then((response)=>{
        return response.data.data;
    })
    
}

function update(form){
     return APIPrivate.put(API_ROUTES.BUSSINESS_THUONGHIEU+`/update`,form).then((response)=>{
        return response.data.data;
    })
    
}
function saveT(form){
     return APIPrivate.post(API_ROUTES.BUSSINESS_THUONGHIEU+`/save`,form).then((response)=>{
        return response.data.data;
    })
    
}

export {getThuongHieu,getByCondition,getInfo,getAll,getManager,update,saveT}
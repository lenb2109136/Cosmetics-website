import { APIPublic } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";
function getStatus(){
    return APIPublic.get(API_ROUTES.BUSSINESS_STATUS).then((response)=>{
        return response.data.data;
    })
}
function getallNenDa(){
    return APIPublic.get(API_ROUTES.BUSSINESS_TRUYCAP+"/getall").then((response)=>{
        return response.data;
    })
}
function getallstep(){
    return APIPublic.get(API_ROUTES.BUSSINESS_TRUYCAP+"/getallstep").then((response)=>{
        return response.data;
    })
}
function getAllDongGoi(){
    return APIPublic.get(API_ROUTES.BUSSINESS_TRUYCAP+"/getall-donggoi").then((response)=>{
        return response.data;
    })
}
function getAllDongHop(){
    return APIPublic.get(API_ROUTES.BUSSINESS_TRUYCAP+"/getall-dong-hop").then((response)=>{
        return response.data;
    })
}







export {getStatus,getallNenDa,getallstep,getAllDongGoi,getAllDongHop}
import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function getAll(){
    return APIPrivate.get(API_ROUTES.BUSSINESS_QUYCACHNHAPHANG+"/getall").then(d=>d.data.data)
}
function them(f){
return APIPrivate.post(API_ROUTES.BUSSINESS_QUYCACHNHAPHANG+"/save",f).then(d=>d)
}

function put(data){
    return APIPrivate.put(API_ROUTES.BUSSINESS_QUYCACHNHAPHANG,data).then(d=>d)
}
export {getAll,them,put}

import { API_ROUTES } from "../utils/constants";
import { APIPrivate } from "../config/axiosconfig";
function removeThongSoCuThe(id){
   return  APIPrivate.delete(API_ROUTES.BUSSINESS_THONGSOCUTHE+`/removethongsocuthe/${id}`)
}
export {removeThongSoCuThe}
import { Routes } from "react-router-dom";
import { APIPrivate, APIPublic } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";

function saveSupplier(data){
    return APIPrivate.post(API_ROUTES.BUSSINESS_DONVICUNGCAP,data)
}
function getbaseSupplierManager(bd, kt, trang, ten) {
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_DONVICUNGCAP}/getManager?bd=${bd}&kt=${kt}&trang=${trang}&ten=${ten}`)
        .then(data => data.data.data);
}
function getProduct(bd, kt,id, trang) {
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_DONVICUNGCAP}/getproduct/${id}?bd=${bd}&kt=${kt}&trang=${trang}`)
        .then(data => data.data.data);
}
function getAll() {
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_DONVICUNGCAP}/getall`)
        .then(data => data.data.data);
}
function getUpdate(id) {
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_DONVICUNGCAP}/getupdate/${id}`)
        .then(data => data.data.data);
}



export {saveSupplier,getbaseSupplierManager,getProduct,getAll,getUpdate}
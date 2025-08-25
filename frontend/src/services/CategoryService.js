import PickCategory from "../components/commons/PickCategory";
import { APIPrivate, APIPublic } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";
function getCategory() {
    return APIPublic.get(API_ROUTES.BUSSINESS_CATEGORY)
    .then(response=> response.data)  
}
function save(data){
    return APIPrivate.post(API_ROUTES.BUSSINESS_CATEGORY,data)
}
function updateca(data){
    return APIPrivate.put(API_ROUTES.BUSSINESS_CATEGORY,data)
}
function getManagerDanhMuc(trang,chuoi){
    return APIPrivate.get(API_ROUTES.BUSSINESS_CATEGORY+`/getmanager/${trang}?thamso=${chuoi}`,)
}
function getThongSoCuTheOfDanhMuc(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_CATEGORY+`/getthonsoofdanhmuc/${id}`).then((data)=>data.data.data)
    
}
function getUpate(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_CATEGORY+`/getupdate/${id}`).then((data)=>data.data.data)
    
}
function test(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_CATEGORY+`/test/${id}`).then((data)=>data.data.data)
    
}
function getSanPhamOfDanhMuc(id) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_CATEGORY + `/getsanphambase/${id}`)
        .then((res) => {
            const mapData = res.data.data;
            const result = Object.entries(mapData).map(([id, ten]) => ({ id, ten }));
            return result;
        });
}

function deleteDanhMuc(id , idt){
 return APIPrivate.delete(API_ROUTES.BUSSINESS_CATEGORY+`?id=${id}&idt=${idt}`).then((data)=>data)
    
}

export { getCategory,deleteDanhMuc,save,getManagerDanhMuc,getSanPhamOfDanhMuc,getThongSoCuTheOfDanhMuc,getUpate,test,updateca}
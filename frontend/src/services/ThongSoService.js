import { ThongSo, ThongSoCuThe } from "../models/Category";
import { API_ROUTES } from "../utils/constants";
import { APIPrivate, APIPublic } from "../config/axiosconfig";
function layThongSo(id) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_THONGSO + `/getthongso/${id}`)
        .then(data => {
            return data.data.data;
        })

}
function saveThongSo(data){
   return  APIPrivate.post(API_ROUTES.BUSSINESS_THONGSO+"/save",data);
}


function update(data){
    return APIPrivate.put(API_ROUTES.BUSSINESS_THONGSO+`/updatethongso`,data);
}
function getAll(){
    return APIPrivate.get(API_ROUTES.BUSSINESS_THONGSO+`/getall`).then((data)=>data.data.data);
}

function getManagerThongSo(trang,chuoi){
    return APIPrivate.get(API_ROUTES.BUSSINESS_THONGSO+`/thongsophantrang/${trang}?thamso=${chuoi}`,)
}


function getDanhMucOfThongSo(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_THONGSO+`/getmucthongso/${id}`).then((data)=>data.data.data)
}
function getThongSoCuTheOfThongSo(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_THONGSO+`/getthongsocuthe/${id}`).then((data)=>data.data.data)
    
}


function deleteDanhMuc(id, idts) {
  return APIPrivate.delete(API_ROUTES.BUSSINESS_THONGSO + `/deletedanhmuc/${id}/${idts}`);
}
function getThongSoById(id) {
  return APIPrivate.get(API_ROUTES.BUSSINESS_THONGSO + `/getthongsobyid/${id}`).then((d)=>d.data.data);
}

function getThongSoForFilter(ten,id) {
  return APIPrivate.get(API_ROUTES.BUSSINESS_THONGSO +`/getCondition?ten=${ten}&id=${id}`).then((response)=>{
        return response.data.data;
    })
}

export { layThongSo,saveThongSo,update,getThongSoForFilter,
    
    getManagerThongSo,getAll,getDanhMucOfThongSo,getThongSoCuTheOfThongSo,deleteDanhMuc,getThongSoById }
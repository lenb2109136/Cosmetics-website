import { APIPublic } from "../config/axiosconfig";

function getXa(id){
    return APIPublic.get(`http://localhost:8080/address/xa-GHN/${id}`).then(data=>data.data.data)
}
function getTinh(){
    return APIPublic.get(`http://localhost:8080/address/tinh-GHN`).then(data=>data.data.data)
}
function getHuyen(id){
    return APIPublic.get(`http://localhost:8080/address/huyen-GHN/${id}`).then(data=>data.data.data)
}
export {getHuyen,getTinh,getXa}
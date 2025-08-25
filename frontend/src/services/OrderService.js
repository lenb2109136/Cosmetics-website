import { APIPrivate } from "../config/axiosconfig";
import { API_ROUTES } from "../utils/constants";
function getviewOrder(data){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/getview`,data).then(data=>data.data.data)

}
function getviewOrderAlready(data){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/getUpdate/${data}`,).then(data=>data.data.data)

}
function getviewOrderAlreadyE(data){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/getUpdateE/${data}`,).then(data=>data.data.data)

}


function getviewOrderAlreadyTaiQuay(data){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/getUpdate/${data}`,).then(data=>data.data.data)

}



function getHinhThuc(data){
    return APIPrivate.get(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/gethinhthuc/${data}`,).then(data=>data.data.data)

}
function getPrevPayment(data){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/getprevpayment/${data}`,).then(data=>data.data.data)

}


function getHinhThucThanhToan(){
     return APIPrivate.get(API_ROUTES.BUSSINESS_HINHTHUCTHANHTOAN+`/getall`).then(data=>data.data.data)
}
function getOrderByStatus(status,filter,trang){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/getbystatus/${status}?trang=${trang}`,filter).then(data=>data.data.data)

}
function remove(id,st,d){
    return APIPrivate.put(API_ROUTES.BUSSINESS_ORDER+`/remove/${id}?status=${st}&res=${d}`)
}
function removeEmployee(id,s,yes){
    return APIPrivate.put(API_ROUTES.BUSSINESS_ORDER+`/remove-employee/${id}?yes=${yes}`)
}


function hoanDonOnline(id,yes,lydo,thanhcong){
    return APIPrivate.put(API_ROUTES.BUSSINESS_ORDER+`/hoanDonOnline/${id}?yes=${yes}&lyDo=${lydo}&thanhCong=${thanhcong}`)
}

function xacNhanHoangHang(id){
    return APIPrivate.put(API_ROUTES.BUSSINESS_ORDER+`/xacnhanhoanhang/${id}`)
}


function createOrder(data){
    if(data?.length==0){
        alert("Vui lòng thêm sản phẩm trước khi đặt hàng")
    }
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/createOrder`,data).then()

}
function getOrderForUpdate(data,id){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/getviewUpdate/${id}`,data).then(data=>data.data.data)
   
}

function getOrderForUpdateEmployee(data,id){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/getview-update-employee/${id}`,data).then(data=>data.data.data)
   
}
function hoanDonAfter(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_ORDER+`/hoantien-after/${id}`).then()
   
}



function hoanDonV2(param) {
    return APIPrivate.get(
        `${API_ROUTES.BUSSINESS_ORDER}/hoanDonV2`,
        { params: param }
    );
}

function changeDongHop(idHoaDon, idDongHop, khoiLuong) {
    return APIPrivate.get(
        `${API_ROUTES.BUSSINESS_ORDER}/chang-donghop`,
        {
            params: {
                idHoaDon: idHoaDon,
                idDongHop: idDongHop,
                KhoiLuong: khoiLuong
            }
        }
    );
}






function update(data,id){
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/update/${id}`,data)
   
}

function createOrderTaiQuay(hinhThucThanhToan, soDienThoai, tenKhachHang, danhSachMatHang) {
    const orderData = {
        hinhThucThanhToan: hinhThucThanhToan,
        soDienThoai: soDienThoai,
        tenKhachHang: tenKhachHang,
        danhSachMatHang: danhSachMatHang
    };
    return APIPrivate.post(API_ROUTES.BUSSINESS_ORDERTAIQUAY+"/create", orderData).then(data=>data.data.data);


}
function xacNhanThanhToan(id){
     return APIPrivate.put(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/xacnhanthanhtoan/${id}`);
    
}
function getTrangThai(id,lan){
     return APIPrivate.get(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/gettrangthai/${id}?lan=${lan}`).then(data=>data.data.data);;
    
}
function chuyenDoiHinhThuc(id){
     return APIPrivate.get(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/chuyendoihinhthuc/${id}`).then(data=>data.data.data);;
    
}
function getByStatusTaiQuay(data){
     return APIPrivate.post(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/getbystatus`,data).then(data=>data.data.data);;
    
}
function getByStatusOnline(data){
     return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/getbystatuss`,data).then(data=>data.data.data);;
    
}
function huyDonTaiQuay(id){
     return APIPrivate.put(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/huydon/${id}`);
    
}

function hoanDon(id,lan,re){
     return APIPrivate.put(API_ROUTES.BUSSINESS_ORDERTAIQUAY+`/hoanDon/${id}?lan=${lan}&re=${re}`);
    
}


// hóa đơn online
function getTrangThaiOnline(id,lan){
     return APIPrivate.get(API_ROUTES.BUSSINESS_ORDER+`/gettrangthai/${id}?lan=${lan}`).then(data=>data.data.data);;
    
}
function duyet(data){
     return APIPrivate.post(API_ROUTES.BUSSINESS_ORDER+`/duyet`,data).then(data=>data.data.data);;
    
}

function getThonTinThanhToanOnline(id){
     return APIPrivate.get(API_ROUTES.BUSSINESS_ORDER+`/getinfothanhtoan/${id}`).then(data=>data.data.data);;
    
}
function getURLprintOrder(id){
     return APIPrivate.get(API_ROUTES.BUSSINESS_ORDER+`/get-print-ghn-order/${id}`).then(data=>data.data.data);;
    
}
function reCreateGHN(id){
     return APIPrivate.get(API_ROUTES.BUSSINESS_ORDER+`/reCreateGHN/${id}`);
    
}


export {removeEmployee,hoanDonV2,reCreateGHN,getURLprintOrder,changeDongHop,hoanDonOnline,xacNhanHoangHang,hoanDonAfter,getviewOrder,getOrderForUpdateEmployee,getviewOrderAlreadyE,duyet,getTrangThaiOnline,getThonTinThanhToanOnline,getByStatusOnline,hoanDon,getHinhThuc,getPrevPayment,getviewOrderAlreadyTaiQuay,getByStatusTaiQuay,huyDonTaiQuay,chuyenDoiHinhThuc,xacNhanThanhToan,getTrangThai,getHinhThucThanhToan,createOrderTaiQuay,createOrder,getOrderByStatus,getviewOrderAlready,remove,getOrderForUpdate,update}
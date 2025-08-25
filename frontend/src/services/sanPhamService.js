import { API_ROUTES } from "../utils/constants";
import { APIPrivate } from "../config/axiosconfig";
import axios from "axios";
function saveSanPham(product, images, coverImage, imagevariant) {
    let form = new FormData()
    form.append("sanpham", JSON.stringify(product))
    images.forEach((image, index) => {
        form.append('anhphu', image.file);
    });
    if (coverImage) {
        form.append('anhbia', coverImage.file);
    }
    imagevariant.forEach((imgVar, index) => {
        form.append('anhbienthe', imgVar);
    });
    return APIPrivate.post(API_ROUTES.BUSSINESS_SANPHAM, form)
}
function getCreateFlashSale(id = 0, ten = "", trang = 0, task) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getcreateflashsale?id=${id}&ten=${ten}&trang=${trang}&task=${task}`).then((data) => data.data.data);
}
function getThongKeCoBan(id, bd, kt) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/thongkedoanhthucoban?id=${id}&bd=${bd}&kt=${kt}`).then((data) => data.data.data);
}
function getChiTietDoanhThu(id, bd, kt, kb, plc) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/chitietdoanhthu?id=${id}&bd=${bd}&kt=${kt}&kb=${kb}&plc=${plc}`).then((data) => data.data.data);
}
function getLuLuongTruyCap(id, bd, kt, time, kb) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/luluongtruycap?id=${id}&bd=${bd}&kt=${kt}&timedurate=${time}&kb=${kb}`).then((data) => data.data.data);
}
function getUpdate(id) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getupdate/${id}`).then(d => d.data.data)
}
function getproductCungCap( bd, kt, dvcc, sp,trang) {
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getproduct?bd=${bd}&kt=${kt}&trang=${trang}&dvcc=${dvcc}&sp=${sp}`).then((data) => data.data.data);
}
function getManager(idDanhMuc, trang, ten, conSuDung, hetHang) {
    const params = new URLSearchParams({
        id: idDanhMuc,
        trang: trang,
        ten: ten || '',
        consudung: conSuDung,
        hetHang: hetHang
    });

    return APIPrivate.get(`${API_ROUTES.BUSSINESS_SANPHAM}/getmanager?${params}`)
        .then((data) => data.data.data);
}
function getxuatTonKho(bd, kt, dm=0, sp,trang,status){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/xuatTonKho?bd=${bd}&kt=${kt}&trang=${trang}&dm=${0}&sp=${sp}&status=${status}`).then((data) => data.data.data);
}
function getxuatgetxuatLoiNhuan(bd, kt, dm=0, sp,trang,status){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/xuatloinhuan?bd=${bd}&kt=${kt}&trang=${trang}&dm=${0}&sp=${sp}&status=${status}`).then((data) => data.data.data);
}
function getPhanBoLoiNhuanOfChiTietHoaDon(bd, kt,sp,trang,status){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getloinhuansanpham/${sp}?bd=${bd}&kt=${kt}&trang=${trang}&&sp=${sp}&status=${status}`).then((data) => data.data.data);
}
function getDetail(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getdetail/${id}`).then((data) => data.data.data);
}

function getByMaVach(ten, l=false){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getByMaVach?ten=${ten}&lonhonkhong=${l}`).then((data) => data.data.data);
}
function getByMaVachForPhieuNhap(ten, l=false){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getByMaVachForPhieuNhap?ten=${ten}&lonhonkhong=${l}`).then((data) => data.data.data);
}
function getByMaVachForPhieuKiem(ten, l=false){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getByMaVachForPhieuKiem?ten=${ten}&lonhonkhong=${l}`).then((data) => data.data.data);
}
function getByMaVachForPhieuKiemV2(ten, l=false, a=1){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getByMaVachForPhieuKiem/v2?ten=${ten}&lonhonkhong=${l}&a=${a}`).then((data) => data.data.data);
}



function getByMaVachForCreateHoaDon(ten, l=false){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getByMaVachForCreateHoaDon?ten=${ten}&lonhonkhong=${l}`).then((data) => data.data.data);
}



function getByMaVachCorect(ten){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getByMaVachCorect?ten=${ten}`).then((data) => data.data.data);
}


function getByMaVachCorectv2(ten){
    return APIPrivate.get(API_ROUTES.BUSSINESS_SANPHAM + `/getByMaVachCorectv2?ten=${ten}`).then((data) => data.data.data);
}


function upDateProduct(product, anhPhanLoaiNew, anhBiaNew, anhGioiThieuNew, anhGioiThieuXoa,id) {
    let productCopy = { ...product };
    productCopy.bienTheKhongLe = productCopy.bienTheKhongLe.map(bt => {
    if (typeof bt.id === 'string') {
        return { ...bt, id: 0 };
    }
    return bt;
});
    productCopy.anhGioiThieuXoa=anhGioiThieuXoa;
    delete productCopy.anhBia;
    delete productCopy.anhGioiThieu;

    let form = new FormData();
    form.append("sanpham", JSON.stringify(productCopy));
   
    anhPhanLoaiNew.forEach((image, index) => {
    if (image != null) {
        form.append(`anhphanloainew`, image);
    } else {
        form.append(`anhphanloainew`, new Blob([], { type: 'text/plain' }), 'empty.txt');
    }
});

    if (anhBiaNew) {
        form.append('anhbianew', anhBiaNew);
    }
    anhGioiThieuNew.forEach((image, index) => {
        form.append('anhGioiThieuNew', image);
    });
        
    
    return APIPrivate.post(`${API_ROUTES.BUSSINESS_SANPHAM}/update/${id}`, form, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });

  
   
}
 function getViewProduct(filter,trang){
    return APIPrivate.post(API_ROUTES.BUSSINESS_SANPHAM + `/getviewproduct/${trang}`,filter).then((data) => data.data.data);
}
function truycap(id){
    return APIPrivate.get(API_ROUTES.BUSSINESS_TRUYCAP + `/truycap/${id}`,).catch(()=>{})
}

function status(id,st){
    return APIPrivate.put(API_ROUTES.BUSSINESS_SANPHAM + `/status/${id}?st=`+st,).catch(()=>{})
}


function getSoLuongDatGioiHan(danhSach)
{
   return APIPrivate.post(API_ROUTES.BUSSINESS_SANPHAM + `/getSoLuongDatGioiHan`,danhSach).then((data) => data.data.data);
}




export {getSoLuongDatGioiHan,status,getByMaVachCorectv2,getByMaVachForPhieuNhap, saveSanPham,getByMaVachForCreateHoaDon,getByMaVach,getByMaVachForPhieuKiem,truycap,getxuatTonKho,getByMaVachCorect,getxuatgetxuatLoiNhuan, getCreateFlashSale, getManager,getPhanBoLoiNhuanOfChiTietHoaDon,
     getThongKeCoBan, getChiTietDoanhThu,getByMaVachForPhieuKiemV2, getLuLuongTruyCap, getUpdate, upDateProduct,getproductCungCap,getViewProduct,getDetail }
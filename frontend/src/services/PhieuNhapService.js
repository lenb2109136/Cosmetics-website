import { APIPrivate } from "../config/axiosconfig";
import { KhauHao } from "../pages/employe/KhauHao";
import { API_ROUTES } from "../utils/constants";
function mapToPhieuNhapDTO(danhSachNhap, selectedSupplier, vat) {
    const phieuNhapDTO = {
        thueVAT: parseFloat(vat) || 0,
        idDonViCungCap: selectedSupplier ? selectedSupplier.id : 0,
        sanPham: danhSachNhap.map(item => ({
            idSanPham: item.id,
            soLuong: item.soLuong || 0,
            donGia: item.donGia || 0,
            maVach:item.maVach
        }))
    };
    return phieuNhapDTO;
}

function mapToKhauHaoDto(danhSachNhap) {
    console.log("đi vào")
    return danhSachNhap.map(item => ({
        idSanPham: item.id,
        soLuong: item.soLuong || 0,
        ghiChu: item.ghiChu || ""
    }));
}

function getAllBase(bd, kt, trang, id) {
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/getphieunhap/${id}?nbd=${bd}&nkt=${kt}&trang=${trang}`)
        .then(data => data.data.data);
}
function getAll(bd, kt,dvcc,sp, trang) {
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/getphieunhapbyten?nbd=${bd}&nkt=${kt}&trang=${trang}&dvcc=${dvcc}&sp=${sp}`)
        .then(data => data.data.data);
}
function getChiTiet(id){
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/getChitiet/${id}`)
        .then(data => data.data.data);
    

}
function getUpdate(id){
    return APIPrivate.get(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/get-update?id=${id}`)
        .then(data => data.data.data);
    

}




function save(danhSachNhap,selectedSupplier,vat){
    let d=mapToPhieuNhapDTO(danhSachNhap,selectedSupplier,vat)
    return APIPrivate.post(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/save`,d)
}

function savev2(danhSachNhap,selectedSupplier,vat){
    let d=mapToPhieuNhapDTO(danhSachNhap,selectedSupplier,vat)
    console.log(d)
    return APIPrivate.post(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/savev2`,d)
}
function update(danhSachNhap,selectedSupplier,vat,id){
    let d=mapToPhieuNhapDTO(danhSachNhap,selectedSupplier,vat)
    console.log(d)
    return APIPrivate.put(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/update/${id}`,d)
}

function duyet(id){
    return APIPrivate.put(`${API_ROUTES.BUSSINESS_PHIEUNHAP}/duyet/${id}`)
}



function saveKhauHao(danhSachNhap){
    
    return APIPrivate.post(API_ROUTES.BUSSINESS_PHIEUKIEM,danhSachNhap)
}

function getKhauHaoForEmpl(ten, bd, kt, trang,active,mv) {
  const params = {
    ten: ten,
    bd: bd,
    kt: kt,
    trang: trang,
    active:active,
    maVach: mv
  };

  return APIPrivate.get(API_ROUTES.BUSSINESS_PHIEUKIEM+"/get-all-page", { params });
}
function getInfoHaoHut(ten, bd, kt, trang,active,mv) {
  const params = {
    ten: ten,
    bd: bd,
    kt: kt,
    trang: trang,
    active:active,
    maVach: mv
  };

  return APIPrivate.get(API_ROUTES.BUSSINESS_PHIEUKIEM+"/get-info-haohut", { params }).then(d=>d.data.data);
}
function getUpdatePhieuKiem(id) {
  return APIPrivate.get(API_ROUTES.BUSSINESS_PHIEUKIEM+`/getupdate/${id}`).then(d=>d.data.data);
}
function getThongKePhieuKiem(ten, bd, kt,active,mv) {
     const params = {
    ten: ten,
    bd: bd,
    kt: kt,
    active:active,
    maVach: mv
  };
  return APIPrivate.get(API_ROUTES.BUSSINESS_PHIEUKIEM+`/get-thongke`, { params }).then(d=>{return d.data.data})
}
function updatePhieuKiem(id,data) {
  return APIPrivate.post(API_ROUTES.BUSSINESS_PHIEUKIEM+`/update/${id}`,data).then(d=>d.data.data);
}
function duyetPhieukiem(id) {
  return APIPrivate.put(API_ROUTES.BUSSINESS_PHIEUKIEM+`/duyet/${id}`).then(d=>d);
}


function deletePhieuNhap(id){
  return APIPrivate.delete(API_ROUTES.BUSSINESS_PHIEUNHAP+`/${id}`)
}

function deletePhieueletePhieuKiem(id){
  return APIPrivate.delete(API_ROUTES.BUSSINESS_PHIEUKIEM+`/${id}`)
}



export {getAllBase,deletePhieuNhap,deletePhieueletePhieuKiem,getInfoHaoHut,duyetPhieukiem,getThongKePhieuKiem,getUpdate,getChiTiet,getAll,save,saveKhauHao,savev2,update,duyet,getKhauHaoForEmpl,getUpdatePhieuKiem,updatePhieuKiem}
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProtectedRoutes from "./ProtectedRoutes";
import LoginPage from "../pages/auth/LoginPage";
import HomePageAdmin from "../pages/admin/HomePage";
import AddProduct from "../pages/admin/product/AddProduct";
import { AddParameter } from "../pages/admin/parameter/AddParameter";
import { ThongSo } from "../pages/admin/parameter/parameterManager";
import { AddCategory } from "../pages/admin/category/Addcategory";
import { DanhMucManger } from "../pages/admin/category/Managercategory";
import { AddSupplier } from "../pages/admin/supplier/AddSupplier";
import { ManagerSupplier } from "../pages/admin/supplier/ManagerSupplier";
import { DetailSupllierManager } from "../pages/admin/supplier/DetailSupplierManager";
import { HomeFlashSale } from "../pages/admin/flashsale/HomeFlashsale";
import { AddFlash } from "../pages/admin/flashsale/AddFlash";
import { UpdateFlash } from "../pages/admin/flashsale/Update";
import { AddDeal } from "../pages/admin/deal/AddDeal";
import { UpdateDeal } from "../pages/admin/deal/UpdateDeal";
import { AddBonus } from "../pages/admin/bonus/AddBonus";
import { UpdateBonus } from "../pages/admin/bonus/UpdateBonus";
import { HomeBonus } from "../pages/admin/bonus/HomeBonus";
import { HomeDeal } from "../pages/admin/deal/HomeDeal";
import { HomeProduct } from "../pages/admin/product/HomeProduct";
import { CupComponent } from "../pages/admin/product/ThongKeProduct";
import UpdateProduct from "../pages/admin/product/UpdateProduct";
import { NhapKho } from "../pages/admin/warehouse/import";
import { ThongKeFlashSale } from "../pages/admin/flashsale/ThongKe";
import ThongKeDeal from "../pages/admin/deal/ThongKeHieuQua"; // Sửa thành default import
import { Homecustomer } from "../pages/customer/HomeCustommer";
import { ViewProduct } from "../pages/customer/ListProduct";
import { DetailProduct } from "../pages/customer/detailProduct";
import { OrderManager } from "../pages/customer/odermanager";
import { Order } from "../pages/customer/Order";
import { UpdateOrder } from "../pages/customer/updateorder";
import { LandingPage } from "../pages/customer/LandingPage";
import { ThuongHieu } from "../pages/customer/ThuongHieu";
import { ListThuongHieu } from "../pages/customer/ListThuongHieu";
import { HomePage } from "../pages/employe/HomePage";
import { HomeNhapHang } from "../pages/employe/HomeNhapHang";
import { ManagerOrder } from "../pages/employe/ManagerOrder";
import { QuanLyHoaDon, QuanLyHoaDonTaiQuay } from "../pages/employe/QuanLyHoaDon";
import { ViewChiTietTaiQuay } from "../pages/employe/viewChiTietTaiQuay";
import { ViewChiTietOnline } from "../pages/employe/viewChiTietOnline";
import { WebSocketContext, WebSocketProvider } from "../components/WebSocketContext";
import { Chat } from "../pages/employe/chatEmployee";
import { ChatCustomer } from "../pages/customer/chat";
import { KhauHao } from "../pages/employe/KhauHao";
import { QuanLySanPham } from "../pages/employe/QuanLySanPham";
import { CreateRoutine } from "../pages/customer/createRoutine";
import { GeneralManager } from "../pages/admin/ManagerGeneral";
import { UpdateCategory } from "../pages/admin/category/UpdateCategory";
import { ManagerThuongHieu } from "../pages/admin/ThuongHieu.js/ManagerThuongHieu";
import { QuyCachNhapHang } from "../pages/admin/QuyCachNhapHang";
import { UpdatePhieuNhap } from "../pages/employe/UpdatePhieuNhap";
import { NhapKhoE } from "../pages/employe/NhapKhoE";
import { HomePhieuKiemHang } from "../pages/employe/HomeKhauHao";
import { UpdateKiemHang } from "../pages/employe/UpdateKhauHao";
import { HomeHaoHut } from "../pages/admin/HomeHaoHut";
import { ViewPhieuKiem } from "../pages/admin/viewPhieuKiem";

export default function AppRouter() {
  return (
    <BrowserRouter>
      <WebSocketProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/admin"
            element={
              <ProtectedRoutes allowedRoles={["ADMIN"]}>
                <HomePageAdmin />
              </ProtectedRoutes>
            }
          >
            <Route path="product/add" element={<AddProduct />} />
            <Route path="product/update" element={<UpdateProduct />} />
            <Route path="product" element={<HomeProduct />} />
            <Route path="product/thongke" element={<CupComponent />} />

            <Route path="supplier" element={<ManagerSupplier />} />
            <Route path="haohut" element={<HomeHaoHut />} />
            <Route path="haohut/:id" element={<ViewPhieuKiem />} />
            <Route path="supplier/addsupplier/:id" element={<AddSupplier />} />
            <Route path="supplier/detail/:id" element={<DetailSupllierManager />} />
            <Route path="marketing/flash" element={<HomeFlashSale />} />
            <Route path="marketing/flash/add" element={<AddFlash />} />
            <Route path="marketing/flash/thongke" element={<ThongKeFlashSale />} />
            <Route path="marketing/flash/update" element={<UpdateFlash />} />
            <Route path="marketing/deal" element={<HomeDeal />} />
            <Route path="marketing/deal/thongke" element={<ThongKeDeal />} />
            <Route path="marketing/deal/add" element={<AddDeal />} />
            <Route path="marketing/deal/update" element={<UpdateDeal />} />
            <Route path="marketing/bonus" element={<HomeBonus />} />
            <Route path="marketing/bonus/add" element={<AddBonus />} />
            <Route path="marketing/bonus/update" element={<UpdateBonus />} />
            <Route path="warehouse/import" element={<NhapKho />} />
            
            <Route path="general" element={<GeneralManager></GeneralManager>}>
              <Route path="category" element={<DanhMucManger />}></Route>
              <Route path="addcategory" element={<AddCategory />} />
              <Route path="update/:id" element={<UpdateCategory />} />
              <Route path="parameter" element={<ThongSo/>}></Route>
              <Route path="brand" element={<ManagerThuongHieu/>}></Route>
              <Route path="quycachdonggoi" element={<QuyCachNhapHang/>}></Route>
            </Route>
          </Route>
          <Route
            path="/customer"
            element={
              <ProtectedRoutes allowedRoles={["CUSTOMER"]}>
                <Homecustomer>

                </Homecustomer>
              </ProtectedRoutes>
            }
          >
            <Route index element={<LandingPage></LandingPage>} />
            <Route path="viewproduct" element={<ViewProduct></ViewProduct>} />
            <Route path="detailproduct/:id" element={<DetailProduct></DetailProduct>} />
            <Route path="order" element={<Order></Order>} />
            <Route path="ordermanager" element={<OrderManager></OrderManager>} />
            <Route path="updateorder" element={<UpdateOrder></UpdateOrder>} />
            <Route path="thuonghieu" element={<ThuongHieu></ThuongHieu>} />
            <Route path="viewthuonghieu" element={<ListThuongHieu></ListThuongHieu>} />
            <Route path="chat" element={<ChatCustomer></ChatCustomer>} />
            <Route path="routine" element={<CreateRoutine></CreateRoutine>} />

          </Route>
          <Route
            path="/employee"
            element={
              <ProtectedRoutes allowedRoles={["EMPLOYEE"]}>
                <HomePage>

                </HomePage>
              </ProtectedRoutes>
            }
          >
            
            <Route path="nhaphang" element={<NhapKhoE></NhapKhoE>} ></Route>
            <Route path="nhaphang/add" element={<HomeNhapHang></HomeNhapHang>} >
              
            </Route>
            <Route path="nhaphang/update/:id" element={<UpdatePhieuNhap></UpdatePhieuNhap>} />
            <Route path="themhoadon" element={<ManagerOrder></ManagerOrder>} />
            <Route path="quanlydon" element={<QuanLyHoaDon></QuanLyHoaDon>} />
            <Route path="detailtaiquay" element={<ViewChiTietTaiQuay></ViewChiTietTaiQuay>} />
            <Route path="detailonline" element={<ViewChiTietOnline></ViewChiTietOnline>} />
            <Route path="chat" element={<Chat></Chat>} />
            <Route path="khauhao" element={<HomePhieuKiemHang></HomePhieuKiemHang>} />
            <Route path="khauhao/:id" element={<UpdateKiemHang></UpdateKiemHang>} />
            <Route path="khauhao/add" element={<KhauHao></KhauHao>} />
            <Route path="sanPham" element={<QuanLySanPham></QuanLySanPham>} />
          </Route>
        </Routes>
      </WebSocketProvider>

    </BrowserRouter>
  );
}
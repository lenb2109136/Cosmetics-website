import { Outlet, useNavigate } from "react-router-dom";
import { createContext, useState, useEffect, useRef } from "react";
import HEADER_IMG from "../../assets/header.jpg";
import Logo from "../../assets/logo.png";
import Modal from "../../components/commons/modal";
import PickCategory from "../../components/commons/PickCategory";
import { ChatCustomer } from "./chat";
import {UpdateProfile} from "../../components/commons/UpdateProfile"
import { subscribeUser } from "../../pushNotification";
export const Context = createContext();

function Homecustomer() {

  useEffect(()=>{
    subscribeUser()
  },[])
  const suggestedProducts = [
  ];
  const [cartUpdate, setCartUpdate] = useState({
    id: 0,
    data: [],
  });
  const [ten,setTen] = useState(localStorage.getItem("name"))
  const [cart, setCart] = useState([]);
  const navigate = useNavigate();
  const [tenSanPham, setTenSanPham] = useState("");
  const [chuoi, setChuoi] = useState();
  const [danhSachDanhMuc, setDanhSachDanhMuc] = useState([]);
  const [openPickCategory, setOpenPickCategory] = useState(false);
  const [inn, setInn] = useState(true);
  const [change, setChange] = useState(false);
  const [isChatOpen, setIsChatOpen] = useState(false); 
  const chatContainerRef = useRef(null);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const profileContainerRef = useRef(null);
  useEffect(()=>{
    setTen(localStorage.getItem("name"))
  },[isProfileOpen])
  useEffect(() => {
    const item = JSON.parse(localStorage.getItem("cart") || "[]");
    setCart(item);
  }, []);


  // PUSH NOTIFY TRỜI ƠI
// useEffect(()=>{
//   subscribeUser()
// })

  useEffect(() => {
    if (danhSachDanhMuc.length !== 0 && !openPickCategory && inn) {
      setInn(false);
      navigate("viewproduct");
    }
  }, [danhSachDanhMuc, openPickCategory, navigate, inn]);
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        isChatOpen &&
        chatContainerRef.current &&
        !chatContainerRef.current.contains(event.target)
      ) {
        setIsChatOpen(false); 
      }
      if (
        isProfileOpen &&
        profileContainerRef.current &&
        !profileContainerRef.current.contains(event.target)
      ) {
        setIsProfileOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isChatOpen, isProfileOpen]);

  return (
    <>
      <div className="w-full">
        <img src={HEADER_IMG} className="w-full" alt="Header Banner" />
        <div className="bg-green-900 px-6 py-3 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <img src={Logo} alt="Logo" className="w-10 h-10" />
            <div>
              <p className="text-white font-bold text-lg">SKINLY.vn</p>
              <p className="text-[10px] text-white">Chất lượng thật - Giá trị thật</p>
            </div>
          </div>
          <div className="flex flex-col items-center w-[30%] relative">
            <input
              type="text"
              placeholder="Tìm kiếm sản phẩm..."
              value={tenSanPham}
              onChange={(e) => setTenSanPham(e.target.value)}
              className="rounded-md w-full px-3 py-1 text-sm outline-none"
            />
            <div className="absolute top-full mt-1 w-full bg-white rounded-md shadow-lg max-h-64 overflow-y-auto z-10">
              {suggestedProducts?.map((product) => (
                <div
                  key={product.id}
                  className="flex items-center gap-3 px-3 py-2 hover:bg-gray-100 cursor-pointer"
                >
                  <img src={product.image} alt={product.name} className="w-10 h-10 object-cover rounded" />
                  <div>
                    <p className="text-sm font-medium">{product.name}</p>
                    <p className="text-xs text-gray-500">{product.price}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
          <div 
            className="text-white flex items-end cursor-pointer"
            onClick={() => setIsProfileOpen(true)}
          >
            <svg width="30" height="30" viewBox="0 0 30 30" fill="none" xmlns="http://www.w3.org/2000/svg">
              <g clipPath="url(#clip0_24246_27855)">
                <circle cx="15" cy="15" r="14.25" stroke="currentColor" strokeWidth="1.5" />
                <path
                  fillRule="evenodd"
                  clipRule="evenodd"
                  d="M21.512 22.83H8.488a.472.472 0 01-.44-.44c0-3.366 2.415-6.146 5.635-6.805C12 15 10.756 13.463 10.756 11.561c0-2.341 1.903-4.244 4.317-4.244 2.342 0 4.317 1.903 4.317 4.244 0 1.902-1.244 3.44-2.927 4.024 3.22.659 5.635 3.44 5.635 6.805-.147.22-.293.44-.586.44z"
                  fill="currentColor"
                />
              </g>
              <defs>
                <clipPath id="clip0_24246_27855">
                  <path fill="currentColor" d="M0 0H30V30H0z" />
                </clipPath>
              </defs>
            </svg>
            <p className="pl-1">{ten}</p>
          </div>
          <div className="relative text-white cursor-pointer">
            <svg
              onClick={() => {
                navigate("/customer/order");
              }}
              width="30"
              height="30"
              viewBox="0 0 30 30"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g clipPath="url(#clip0_24246_27881)">
                <circle cx="15" cy="15" r="14.25" stroke="currentColor" strokeWidth="1.5" />
                <path
                  d="M22.5 10.5h-12l-1.5-4.5H6a.5.5 0 00-.5.5v.5h2.5l3 12h8a.5.5 0 00.5-.5v-.5h-6m-5 3a1 1 0 100-2 1 1 0 000 2zm10 0a1 1 0 100-2 1 1 0 000 2z"
                  fill="currentColor"
                />
              </g>
              <defs>
                <clipPath id="clip0_24246_27881">
                  <path fill="currentColor" d="M0 0H30V30H0z" />
                </clipPath>
              </defs>
            </svg>
            {cart.length > 0 && (
              <span className="absolute -top-2 -right-2 bg-red-600 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                {cart.length}
              </span>
            )}
          </div>
        </div>
      </div>
      <div className="bg-green-900 p-2.5 flex items-center gap-5 justify-center">
        <span
          className="text-green-200 text-xl cursor-pointer"
          onClick={() => setOpenPickCategory(true)}
        >
          ☰
          
        </span>
        <p onClick={()=>{
            setDanhSachDanhMuc([])
          }}><i class="fa-solid fa-arrows-rotate text-white cursor-pointer"></i></p>
        <a
          onClick={() => {
            navigate("/customer/viewthuonghieu");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          THƯƠNG HIỆU
        </a>
        <a
          onClick={() => {
            navigate("/customer/viewproduct");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          SẢN PHẨM
        </a>
        <a
          onClick={() => {
            navigate("/customer/ordermanager");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          QUẢN LÝ ĐƠN
        </a>
        {cartUpdate?.data?.length !== 0 ? (
          <a
            onClick={() => {
              navigate("/customer/updateorder?id=" + cartUpdate?.id);
            }}
            className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
          >
            ĐẾN ĐƠN CẬP NHẬT
          </a>
        ) : null}
      </div>
       {isChatOpen && (
          <ChatCustomer setOpen={setIsChatOpen} />
      )}
      <Context.Provider
        value={{
          categoryId:
            danhSachDanhMuc.length > 0
              ? danhSachDanhMuc[danhSachDanhMuc.length - 1]?.id
              : 0,
          tenSanPham: tenSanPham,
          chuoi: chuoi,
          cart: cart,
          ten:ten,
          setCart: setCart,
          cartUpdate: cartUpdate,
          setCartUpdate: setCartUpdate,
          change: change,
          setChange: setChange,
        }}
      >
        <Outlet />
      </Context.Provider>
      {!isChatOpen && (
        <div
          className="fixed bottom-5 right-5 bg-green-900 text-white p-3 rounded-full cursor-pointer shadow-lg hover:bg-green-700 z-50"
          onClick={() => setIsChatOpen(true)}
        >
          <svg
            className="w-6 h-6"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"
            />
          </svg>
        </div>
      )}
     
      <div className="bg-gray-200 text-center py-4 mt-8 text-sm">
        © 2025 SKINLY.vn – Tất cả các quyền được bảo lưu.
      </div>
      {openPickCategory && (
        <Modal setOpen={setOpenPickCategory}>
          <PickCategory
            color="text-green-900"
            categoryPick={danhSachDanhMuc}
            setcategoryPick={setDanhSachDanhMuc}
            setChuoi={setChuoi}
            setOpen={setOpenPickCategory}
          />
        </Modal>
      )}
      {isProfileOpen && (
      <>
      <div 
          ref={profileContainerRef}
          className="absolute top-0 right-0 h-full w-196 bg-white shadow-2xl z-50 transform translate-x-0 transition-transform duration-300 ease-in-out overflow-y-auto"
        >
          <div className="p-4 bg-green-900 text-white flex justify-between items-center">
            <h2 className="text-xl font-bold">Cập nhật hồ sơ</h2>
            <button 
              onClick={() => setIsProfileOpen(false)}
              className="text-white hover:text-gray-300"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
            <UpdateProfile />
        </div>
        {/* <div className="p-4"> */}
          
          {/* </div> */}
      </>
      )}
      {isProfileOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-40"
          onClick={() => setIsProfileOpen(false)}
        ></div>
      )}
    </>
  );
}

export { Homecustomer };
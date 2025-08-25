import { Outlet, useNavigate } from "react-router-dom";
import { createContext, useState, useEffect, useRef } from "react";
import HEADER_IMG from "../../assets/header.jpg";
import Logo from "../../assets/logo.png";
import Modal from "../../components/commons/modal";
import PickCategory from "../../components/commons/PickCategory";
import { ChatCustomer } from "../customer/chat";
import { UpdateProfile } from "../../components/commons/UpdateProfile";

export const Context = createContext();

function HomePage() {
  const suggestedProducts = [];
  const [cartUpdate, setCartUpdate] = useState({
    id: 0,
    data: [],
  });
  const [ten, setTen] = useState(localStorage.getItem("name"));
  const navigate = useNavigate();
  const [chuoi, setChuoi] = useState();
  const [danhSachDanhMuc, setDanhSachDanhMuc] = useState([]);
  const [openPickCategory, setOpenPickCategory] = useState(false);
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const profileContainerRef = useRef(null);

  // Update name when profile is updated
  useEffect(() => {
    setTen(localStorage.getItem("name"));
  }, [isProfileOpen]);

  // Handle click outside to close profile panel
  useEffect(() => {
    const handleClickOutside = (event) => {
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
  }, [isProfileOpen]);

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
            {/* Search input (kept as is, assuming it might be completed later) */}
          </div>
          <div
            className="text-white flex items-end cursor-pointer"
            onClick={() => setIsProfileOpen(true)}
          >
            <svg
              width="30"
              height="30"
              viewBox="0 0 30 30"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
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
            {/* Cart icon (kept as is, assuming it might be completed later) */}
          </div>
        </div>
      </div>
      <div className="bg-green-900 p-2.5 flex items-center gap-5 justify-center">
       
        <a
          onClick={() => {
            navigate("nhaphang");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          NHẬP HÀNG
        </a>
        <a
          onClick={() => {
            navigate("themhoadon");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          THÊM HÓA ĐƠN
        </a>
        <a
          onClick={() => {
            navigate("quanlydon");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          QUẢN LÝ ĐƠN HÀNG
        </a>
        <a
          onClick={() => {
            navigate("/employee/chat");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          CHAT
        </a>
        <a
          onClick={() => {
            navigate("/employee/khauhao");
          }}
          className="text-green-200 hover:bg-green-300 cursor-pointer hover:text-green-900 px-2.5 py-1 rounded text-sm"
        >
          KHẤU HAO SẢN PHẨM
        </a>
      </div>
      <div className="h-full">
        <Context.Provider
          value={{
            cartUpdate,
            setCartUpdate,
            chuoi,
            setChuoi,
            danhSachDanhMuc,
            setDanhSachDanhMuc,
          }}
        >
          <Outlet />
        </Context.Provider>
      </div>
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
            className="absolute top-0 right-0 h-full w-200 bg-white shadow-2xl z-50 transform translate-x-0 transition-transform duration-300 ease-in-out overflow-y-auto"
          >
            <div className="p-4 bg-green-900 text-white flex justify-between items-center">
              <h2 className="text-xl font-bold">Cập nhật hồ sơ</h2>
              <button
                onClick={() => setIsProfileOpen(false)}
                className="text-white hover:text-gray-300"
              >
                <svg
                  className="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>
            <UpdateProfile />
          </div>
          <div
            className="fixed inset-0 bg-black bg-opacity-50 z-40"
            onClick={() => setIsProfileOpen(false)}
          ></div>
        </>
      )}
    </>
  );
}

export { HomePage };
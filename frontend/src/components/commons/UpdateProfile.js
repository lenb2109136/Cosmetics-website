import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { getInfo } from "../../services/userService";
import Modal from "../../components/commons/modal";
import { ModalAddress } from "../../components/commons/ModalAddress";
import { update } from "../../services/nguoidungservice";

function UpdateProfile() {
    const [userInfo, setUserInfo] = useState({
        ten: "",
        email:"",
        sodienthoai:"",
        matkhau: "",
        diaChi: "",
        code: []
    });
    const [openAddressModal, setOpenAddressModal] = useState(false);

    useEffect(() => {
        getInfo()
            .then((data) => {
                setUserInfo({
                    ten: data.ten || "",
                    email: data.email || "",
                    sodienthoai:data.soDienThoai || "",
                    matkhau: data.matKhau || "",
                    diaChi: data.diaChi || "",
                    code: data.code || []
                });
                 
            })
            .catch((err) => {
               
                toast.error("Không thể tải thông tin người dùng: " + err.message);
            });
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setUserInfo((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    // Function to parse and package the address
    const parseAddress = (addressString) => {
        const [addressPart, codePart] = addressString.split(".");
        const addressComponents = addressPart.split(", ").map(item => item.trim());
        const codes = codePart ? codePart.split(" ").map(code => code.trim()) : [];

        // Ensure the address has at least city, district, ward
        const city = addressComponents[0] || "";
        const district = addressComponents[1] || "";
        const ward = addressComponents[2] || "";
        const additionalInfo = addressComponents.slice(3).join(", ") || "";

        // Format the address string without codes
        const formattedAddress = [city, district, ward, additionalInfo]
            .filter(part => part)
            .join(", ");

        return {
            diaChi: formattedAddress,
            code: codes
        };
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Parse the address string
            const { diaChi, code } = parseAddress("Hà Nội, Quận Nam Từ Liêm, Phường Trung Văn, sdsdsdsd.201 3440 13009");

            // Update userInfo with parsed address and codes
            const updatedUserInfo = {
                ten: userInfo.ten,
                email: userInfo.email,
                sodienthoai:userInfo.sodienthoai,
                matkhau: userInfo.matkhau || undefined,
                diachi: userInfo.diaChi +"."+code[0] +" "+code[1]+" "+code[2]
            };

            // Call the updateUserInfo service
            console.log(updatedUserInfo)
            update(updatedUserInfo).then(()=>{
                toast.success("Cập nhật thông tin thành công")
                localStorage.setItem("name",userInfo.ten)
            })
            .catch((e)=>{
                toast.error(e?.response?.data?.message)
            })
            // await updateUserInfo(updatedUserInfo);
            // toast.success("Cập nhật thông tin thành công!");

            // // Update local state
            // setUserInfo((prev) => ({
            //     ...prev,
            //     diaChi: diaChi,
            //     code: code
            // }));
        } catch (err) {
            toast.error("Cập nhật thất bại: " + err.message);
        }
    };

    const handleAddressUpdate = (newAddress, newCode) => {
        setUserInfo((prev) => ({
            ...prev,
            diaChi: newAddress,
            code: newCode
        }));
        setOpenAddressModal(false);
    };

    return (
        <div className="mx-auto w-full p-10">
            <h2 className="text-2xl font-bold mb-4 text-gray-800">Cập nhật thông tin</h2>
            <form onSubmit={handleSubmit} className="bg-white w-full p-6 rounded-lg shadow-md border border-gray-200">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="flex flex-col">
                        <label className="text-sm text-gray-500 mb-1">Họ và tên</label>
                        <input
                            type="text"
                            name="ten"
                            value={userInfo.ten}
                            onChange={handleInputChange}
                            className="p-2 border border-gray-200 rounded focus:outline-none focus:ring-2 focus:ring-green-900"
                            placeholder="Nhập họ và tên"
                        />
                    </div>
                    <div className="flex flex-col">
                        <label className="text-sm text-gray-500 mb-1">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={userInfo.email}
                            onChange={handleInputChange}
                            className="p-2 border border-gray-200 rounded focus:outline-none focus:ring-2 focus:ring-green-900"
                            placeholder="Nhập email"
                        />
                    </div>
                    <div className="flex flex-col">
                        <label className="text-sm text-gray-500 mb-1">Số điện thoại</label>
                        <input
                            type="text"
                            name="sodienthoai"
                            value={userInfo.sodienthoai}
                            onChange={handleInputChange}
                            className="p-2 border border-gray-200 rounded focus:outline-none focus:ring-2 focus:ring-green-900"
                            placeholder="Nhập Số điện thoại"
                        />
                    </div>
                    <div className="flex flex-col">
                        <label className="text-sm text-gray-500 mb-1">Mật khẩu</label>
                        <input
                            type="password"
                            name="matkhau"
                            value={userInfo.matkhau}
                            onChange={handleInputChange}
                            className="p-2 border border-gray-200 rounded focus:outline-none focus:ring-2 focus:ring-green-900"
                            placeholder="Nhập mật khẩu mới (nếu muốn thay đổi)"
                        />
                    </div>
                </div>
                <div className="w-full mt-4">
                    <label className="text-sm mt-2 text-gray-500 mb-3">Địa chỉ</label>
                    <div className="w-full flex items-center gap-2">
                        <input
                            type="text"
                            value={userInfo.diaChi || "Chưa cung cấp"}
                            readOnly
                            className="p-2 w-8/12 border border-gray-200 rounded bg-gray-100 w-full"
                        />
                        <button
                            type="button"
                            onClick={() => setOpenAddressModal(true)}
                            className="font-bold text-yellow-500 bg-yellow-100 px-3 py-1 rounded"
                        >
                            <i className="fa-solid fa-square-pen mr-2"></i>
                            Chỉnh sửa
                        </button>
                    </div>
                </div>
                <button
                    type="submit"
                    className="mt-6 w-full py-3 bg-green-900 text-white rounded-lg hover:bg-green-700 transition duration-300 font-semibold"
                >
                    Lưu thay đổi
                </button>
            </form>

            {openAddressModal && (
                <Modal setOpen={setOpenAddressModal}>
                    <ModalAddress
                        address={userInfo.diaChi}
                        setAddress={(newAddress) =>
                            setUserInfo((prev) => ({ ...prev, diaChi: newAddress }))
                        }
                        addressIds={userInfo.code}
                        setAddressIds={(newCode) =>
                            setUserInfo((prev) => ({ ...prev, code: newCode }))
                        }
                        isOpen={openAddressModal}
                        setOpen={setOpenAddressModal}
                        onClose={() => setOpenAddressModal(false)}
                    />
                </Modal>
            )}
        </div>
    );
}

export { UpdateProfile };
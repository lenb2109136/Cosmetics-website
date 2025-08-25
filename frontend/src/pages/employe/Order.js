import { formatToVND } from "../../utils/Format";
import BackGroundTopLeft from "../../assets/background.png";
import BackGroundBotRight from "../../assets/backgoundleft.png";
import { useEffect, useState } from "react";
import { createOrderTaiQuay, getHinhThucThanhToan, getviewOrder } from "../../services/OrderService";
import { toast } from "react-toastify";
import { getKhachHang } from "../../services/nguoidungservice";
import Modal from "../../components/commons/modal";
import { useNavigate } from "react-router-dom";
import { QuetMaThanhToan } from "./quetMaThanhToan";
import { getInfo } from "../../services/userService";

function Order({ dsMatHang = [], taiXuong, setTaiXuong, setDanhSachMatHang, hinhThucThanhToan, setHinhThucThanhToan, nguoiDung, setNguoiDung, setRe, luuTru, setLuuTru }) {
    const [dshinhThucThanhToan, setDanhSachHinh] = useState([]);
    const [load, setLoad] = useState(false);
    const [data, setData] = useState({});
    const [open, setOpen] = useState(false);
    const [header, setHeader] = useState("");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();
    const [orderre, setorrderre] = useState({});
    const [openpayment, setopenpayment] = useState(false);
    const [ii, setii] = useState(0);

    useEffect(() => {
        if (dsMatHang?.length == 0) {
            setData({})
            setNguoiDung({
                ten: "",
                sdt: ""
            })
            setHinhThucThanhToan(0)
        }
    }, [dsMatHang])

    // Hàm xử lý reset state
    const handleResetState = () => {
        setDanhSachMatHang([]);
        setHinhThucThanhToan(0);
        setNguoiDung({
            sdt: "",
            ten: ""
        });
        setData({});
        setorrderre({});
        setOpen(false);
        setopenpayment(false);
    };

    useEffect(() => {
        if (dsMatHang.length > 0) {
            getviewOrder(dsMatHang)
                .then(data => {
                    setData(data);
                })
                .catch((e) => {
                    toast.error(e?.response?.data?.message || "Lấy dữ liệu thất bại");
                });
        }
    }, [load, dsMatHang]);

    useEffect(() => {
        getHinhThucThanhToan()
            .then((d) => {
                setDanhSachHinh(d);
            })
            .catch(() => {
                toast.error("Lấy hình thức thanh toán thất bại");
            });
    }, []);
    useEffect(() => {
        if (luuTru === true) {
            const l = {
                thongTinMatHang: data?.data,
                hinhThucThanhToan: hinhThucThanhToan,
                nguoiDung: nguoiDung,
            };
            let DanhSachDonLuuTru = JSON.parse(localStorage.getItem("danhsachluutru") || "[]");

            DanhSachDonLuuTru.push(l);

            localStorage.setItem("danhsachluutru", JSON.stringify(DanhSachDonLuuTru));
            setData({})
            setNguoiDung({
                ten: "",
                sdt: ""
            })
            setLuuTru(false);
        }
    }, [luuTru]);


    const handleCreateOrder = () => {
        createOrderTaiQuay(
            hinhThucThanhToan,
            nguoiDung?.sdt || "",
            nguoiDung?.ten || "",
            dsMatHang
        )
            .then(d => {
                setorrderre(d);
                setRe(d.idCall)
                setopenpayment(true);
                setOpen(false);
            })
            .catch((e) => {
                toast.error(e?.response?.data?.message || "Tạo đơn thất bại, thử lại lần nữa.");
            });
    };

    const handlePhoneChange = async (e) => {
        const phoneValue = e.target.value;
        setNguoiDung(prev => ({ ...prev, sdt: phoneValue }));

        try {
            const response = await getKhachHang(phoneValue);
            if (response) {
                setNguoiDung({
                    id: response.id,
                    sdt: phoneValue,
                    ten: response.ten || ""
                });
            } else {
                setNguoiDung(prev => ({
                    ...prev,
                    id: 0,
                    ten: ""
                }));
            }
        } catch (error) {
            console.error("Lỗi khi gọi getKhachHang:", error);
            setNguoiDung(prev => ({
                ...prev,
                id: 0,
                ten: ""
            }));
            toast.error("Không thể lấy thông tin khách hàng");
        }
    };



    return (
        <div className="w-full relative z-0">
            {/* <img
                src={BackGroundTopLeft}
                alt="bg"
                className="absolute top-0 right-0 w-24 z-30"
            />
            <img
                src={BackGroundBotRight}
                alt="bg"
                className="absolute bottom-0 w-24 z-30"
            /> */}
            <div className="container mx-auto w-full relative bg-white p-4 rounded">
                <div className="flex flex-col md:flex-row gap-4">
                    <div className="flex-1 z-40 basis-[50%]">
                        <div className="max-h-[500px] overflow-y-auto scrollbar-thin scrollbar-thumb-green-900 scrollbar-track-gray-100">
                            <table className="min-w-full text-sm text-center text-gray-600 border">
                                <thead className="bg-gray-50">
                                    <tr>
                                        <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Sản phẩm</th>
                                        <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Phân loại</th>
                                        <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Giá tiền</th>
                                        <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Số lượng</th>
                                        <th className="px-4 py-2.5 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Thành tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {data?.data?.map((product, pIdx) => {
                                        const productRowSpan = product.danhSachCon.reduce(
                                            (acc, variant) => acc + variant.cartItemLasts.length,
                                            0
                                        );

                                        return product.danhSachCon.flatMap((variant, vIdx) => {
                                            const variantRowSpan = variant.cartItemLasts.length;
                                            const totalQuantity = variant.cartItemLasts.reduce((sum, ct) => sum + ct.soLuong, 0);

                                            return variant.cartItemLasts.map((item, iIdx) => {
                                                const isFirstProductRow = vIdx === 0 && iIdx === 0;
                                                const isFirstVariantRow = iIdx === 0;
                                                const thanhTien = item.soLuong * item.giaGiam;

                                                return (
                                                    <tr key={`${pIdx}-${vIdx}-${iIdx}`} className="border-b bg-white hover:bg-gray-50">
                                                        {isFirstProductRow && (
                                                            <td className="px-4 py-2" rowSpan={productRowSpan}>
                                                                <div className="flex items-center justify-center gap-3">
                                                                    <img
                                                                        src={variant.anhGioiThieu || product.anhBia}
                                                                        alt={product.tenSanPham}
                                                                        className="w-16 h-16 object-cover rounded"
                                                                    />
                                                                    <p className="font-medium">{product.tenSanPham}</p>
                                                                </div>
                                                            </td>
                                                        )}

                                                        {isFirstVariantRow && (
                                                            <td className="px-4 py-2 font-semibold" rowSpan={variantRowSpan}>
                                                                {variant.tenBienThe}
                                                            </td>
                                                        )}

                                                        <td className="px-4 py-2">
                                                            {item.giaGiam < variant.giaGoc ? (
                                                                <div>
                                                                    <div className="font-bold text-black">
                                                                        {item.giaGiam.toLocaleString("vi-VN")} đ
                                                                    </div>
                                                                    <div className="line-through text-gray-400">
                                                                        {variant.giaGoc.toLocaleString("vi-VN")} đ
                                                                    </div>
                                                                    <div>({item.soLuong})</div>
                                                                </div>
                                                            ) : (
                                                                <div className="font-bold text-black">
                                                                    {variant.giaGoc.toLocaleString("vi-VN")} đ
                                                                </div>
                                                            )}
                                                        </td>

                                                        {isFirstVariantRow && (
                                                            <td className="px-4 py-2" rowSpan={variantRowSpan}>
                                                                <div className="flex items-center gap-2">
                                                                    <input
                                                                        onChange={(e) => {
                                                                            const value = parseInt(e.target.value);
                                                                            const idBienThe = variant.idBienThe;

                                                                            let cart = [...dsMatHang];
                                                                            const index = cart.findIndex((item) => item.idBienThe === idBienThe);

                                                                            if (index !== -1) {
                                                                                if (value === 0) {
                                                                                    cart.splice(index, 1);
                                                                                } else {
                                                                                    cart[index].soLuong = value;
                                                                                }
                                                                                setDanhSachMatHang(cart);
                                                                            }
                                                                            setLoad(!load);
                                                                        }}
                                                                        type="number"
                                                                        value={totalQuantity}
                                                                        className="w-10 pl-2 outline-none border border-gray-400"
                                                                    />
                                                                </div>
                                                            </td>
                                                        )}

                                                        <td className="px-4 py-2 font-medium">
                                                            {thanhTien.toLocaleString("vi-VN")} đ
                                                        </td>
                                                    </tr>
                                                );
                                            });
                                        });
                                    })}
                                    {
                                        !data?.data || data?.data.length === 0 ? (
                                            <tr className="text-center">
                                                <td colSpan={5}>
                                                    <div className="w-full flex justify-center">
                                                        <img
                                                            className="w-44"
                                                            src="https://media.istockphoto.com/id/861576608/vi/vec-to/bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-empty-shopping-bag-m%E1%BA%ABu-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-vector-online-business.jpg?s=612x612&w=0&k=20&c=-6p0qVyb_p8VeHVu1vJVaDHa6Wd_mCjMkrFBTlQdehI="
                                                        />
                                                    </div>
                                                </td>
                                            </tr>
                                        ) : null
                                    }

                                </tbody>

                            </table>
                        </div>
                    </div>

                    <div className="basis-[30%] md:w-1/4 w-full p-4 relative  bg-gray-50 shadow-inner rounded-md z-40 norder shadow-md">
                        <h3 className="text-lg font-semibold mb-2">Thông tin thanh toán</h3>
                        <hr className="mb-2" />
                        <div className="mb-2">
                            <p className="mb-2">Hình thức thanh toán:</p>
                            {dshinhThucThanhToan?.map((d) => (
                                <div className="ml-2" key={d.id}>
                                    <input
                                        onClick={() => {
                                            setHinhThucThanhToan(d.id);
                                        }}
                                        checked={d?.id == hinhThucThanhToan}
                                        type="radio"
                                        name="hinhThucThanhToan"
                                        className="accent-green-900 mr-2 cursor-pointer"
                                    />
                                    <span>{d.ten}</span>
                                </div>
                            ))}
                        </div>
                        <p className="mb-2">
                            Tạm tính: <span className="float-right">{formatToVND(data?.tongTien)}</span>
                        </p>
                        <p className="mb-2">
                            Tiết kiệm: <span className="float-right">{formatToVND(data?.giamGia)}</span>
                        </p>
                        <hr className="my-2" />
                        <p className="font-bold">
                            Tổng cộng: <span className="float-right">{formatToVND(data?.tongTien)}</span>
                        </p>
                        <p className="text-gray-500 text-sm mt-1">(Đã bao gồm VAT)</p>
                        <button
                            onClick={() => {
                                setOpen(true);
                                setHeader("Xác nhận lập hóa đơn");
                                setMessage("Thực hiện xác nhận với khách hàng trước khi lập đơn");
                            }}
                            className="mt-4 w-full py-2 text-green-900  rounded border shadow-md font-bold"
                        >
                            Lập hóa đơn
                        </button>
                    </div>
                    <div className="basis-[20%] md:w-1/4 w-full p-4 relative  bg-gray-50 shadow-inner rounded-md z-40 border shadow-md rounded-md">
                        <h3 className="text-lg font-semibold mb-2">Thông tin khách hàng</h3>
                        <hr className="mb-2" />
                        <div className="mb-4 w-full">
                            <label htmlFor="phone-input" className="text-sm text-gray-800 font-semibold mb-1 block">
                                Số điện thoại:
                            </label>
                            <div className="relative">
                                <i
                                    className="fa-solid fa-phone absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500"
                                    aria-hidden="true"
                                ></i>
                                <input
                                    id="phone-input"
                                    type="tel"
                                    placeholder="+84 123 456 789"
                                    value={nguoiDung.sdt || ""}
                                    onChange={handlePhoneChange}
                                    className="w-full border border-gray-300 rounded-md pl-10 pr-3 py-2 text-sm outline-none focus:ring-2 focus:ring-green-900 focus:border-green-900 transition-colors"
                                    aria-describedby="phone-error"
                                    pattern="[0-9+]*"
                                />
                            </div>
                        </div>
                        <div className="mb-4 w-full">

                            <label htmlFor="customer-name-input" className="text-sm text-gray-800 font-semibold mb-1 block">
                                Tên khách hàng:
                            </label>
                            <div className="relative">
                                <i
                                    className="fa-regular fa-user absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500"
                                    aria-hidden="true"
                                ></i>
                                <input
                                    id="customer-name-input"
                                    type="text"
                                    placeholder="Nhập tên khách hàng"
                                    value={nguoiDung.ten || ""}
                                    onChange={(e) => {
                                        setNguoiDung({
                                            ...nguoiDung,
                                            ten: e.target.value
                                        });
                                    }}
                                    className="w-full border border-gray-300 rounded-md pl-10 pr-3 py-2 text-sm outline-none focus:ring-2 focus:ring-green-900 focus:border-green-900 transition-colors"
                                    aria-describedby="customer-name-error"
                                />
                            </div>
                        </div>
                        <hr className="my-2" />
                    </div>
                </div>
            </div>

            {open ? (
                <Modal setOpen={setOpen}>
                    <div className="z-50 w-96 bg-white rounded-xl p-6">
                        <h2 className="text-lg font-semibold text-green-700 mb-2">{header}</h2>
                        <p className="text-md text-gray-700 mb-5">{message}?</p>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-800 transition"
                                onClick={() => {
                                    handleCreateOrder();
                                }}
                            >
                                Đồng ý
                            </button>
                            <button
                                className="px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition"
                                onClick={() => setOpen(false)}
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </Modal>

            ) : null}
            {taiXuong ? (
                <Modal setOpen={setTaiXuong}>
                    <div className="min-w-[400px]">
                        {/* Tiêu đề */}
                        <div className="flex items-center gap-2 text-lg font-semibold mb-3 text-gray-800 bg-green-900 text-white">
                            <i className="mr-2"></i>
                            Danh sách hóa đơn lưu tạm
                        </div>

                        {/* Bảng danh sách */}
                        <div className="overflow-x-auto r shadow-md border border-gray-200">
                            <table className="w-full table-auto text-sm text-left">
                                <thead className="bg-gray-100 text-gray-700">
                                    <tr>
                                        <th className="py-2 px-4 border-b border-gray-200 text-center">STT</th>
                                        <th className="py-2 px-4 border-b border-gray-200 text-center">Tên khách hàng</th>
                                        <th className="py-2 px-4 border-b border-gray-200 text-center">Số điện thoại</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {
                                        (() => {
                                            const list = JSON.parse(localStorage.getItem("danhsachluutru") || "[]");
                                            if (list.length === 0) {
                                                return (
                                                    <tr>
                                                        <td colSpan="3" className="text-center py-4 text-gray-500 italic">
                                                            Không có phần tử được chọn
                                                        </td>
                                                    </tr>
                                                );
                                            }

                                            return list.map((d, index) => (
                                                <tr
                                                    key={index}
                                                    onClick={() => {
                                                        let yu = { ...data };
                                                        yu.data = d?.thongTinMatHang;
                                                        setData(yu);
                                                        setNguoiDung(d?.nguoiDung);
                                                        setHinhThucThanhToan(d?.hinhThucThanhToan);

                                                        const danhSach = JSON.parse(localStorage.getItem("danhsachluutru") || "[]");
                                                        danhSach.splice(index, 1);
                                                        localStorage.setItem("danhsachluutru", JSON.stringify(danhSach));

                                                        setTaiXuong(false);
                                                    }}
                                                    className="text-center cursor-pointer hover:bg-gray-50 border-b border-gray-200"
                                                >
                                                    <td className="py-2 px-4">{index + 1}</td>
                                                    <td className="py-2 px-4">{d?.nguoiDung?.ten || "Chưa có thông tin"}</td>
                                                    <td className="py-2 px-4">{d?.nguoiDung?.sdt || "Chưa có thông tin"}</td>
                                                </tr>
                                            ));
                                        })()
                                    }
                                </tbody>
                            </table>
                        </div>
                    </div>
                </Modal>
            ) : null}

            {openpayment ? (
                <QuetMaThanhToan
                    da={orderre}
                    setOpen={setopenpayment}
                    setht={setHinhThucThanhToan}
                    setdata={setDanhSachMatHang}
                    setnguoidung={setNguoiDung}
                    handleResetState={handleResetState}
                />
            ) : null}
        </div>
    );
}

export { Order };
import { useContext, useEffect, useRef, useState } from "react";
import gsap from "gsap";
import Deal from "../../assets/deal.png";
import Classify from "../../assets/classify.png";
import DoiTra from "../../assets/doitra.webp";
import GiaoNhanh from "../../assets/giahangnhanh.webp";
import BaoHanh from "../../assets/hangchinhhang.png";
import { getDetail, truycap } from "../../services/sanPhamService";
import { toast } from "react-toastify";
import "../../styles/DetaolProduct.css";
import { ProductItem } from "../../components/commons/ItemListProduct";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { save } from "../../services/phanhoiservice";
import { Context } from "./HomeCustommer";

function DetailProduct() {
    const [data, setData] = useState({});
    const [imagePick, setImagePick] = useState("");
    const { cartUpdate, setCartUpdate, change, setChange } = useContext(Context);
    const { setCart } = useContext(Context);
    const [prevImage, setPrevImage] = useState("");
    const [variantPick, setVariantPick] = useState({});
    const [noiDungPhanHoi, setNoiDungPhanHoi] = useState("");
    const [soSao, setSoSao] = useState(0);
    const imageNextRef = useRef(null);
    
    const countdownRef = useRef(null);
    const navigate = useNavigate()
     const { id } = useParams();
    useEffect(() => {
        getDetail(id)
            .then((data) => {
                setData(data);
                if (data?.thonTinBienThe?.length > 0) {
                    let maxGiam = -1;
                    let bienTheGiamMax = null;

                    data.thonTinBienThe.forEach((v) => {
                        const giam = v?.thonTinFlashsale?.giaTriGiam || 0;
                        if (giam > maxGiam) {
                            maxGiam = giam;
                            bienTheGiamMax = v;
                        }
                    });

                    setVariantPick(maxGiam > 0 ? bienTheGiamMax : data.thonTinBienThe.reduce((min, v) => v.gia < min.gia ? v : min));
                }
            })
            .catch(() => toast.error("Lấy dữ liệu thất bại"));
            
        // gsap.from(".denbu",{
        //     opacity:0,
        //     width:0,
        //     duration:1
        // })
    }, []);
    useEffect(() => {
        getDetail(id)
            .then((data) => {
                setData(data);
                if (data?.thonTinBienThe?.length > 0) {
                    let maxGiam = -1;
                    let bienTheGiamMax = null;

                    data.thonTinBienThe.forEach((v) => {
                        const giam = v?.thonTinFlashsale?.giaTriGiam || 0;
                        if (giam > maxGiam) {
                            maxGiam = giam;
                            bienTheGiamMax = v;
                        }
                    });

                    setVariantPick(maxGiam > 0 ? bienTheGiamMax : data.thonTinBienThe.reduce((min, v) => v.gia < min.gia ? v : min));
                }
            })
            .catch(() => toast.error("Lấy dữ liệu thất bại"));
   
    }, [id]);
   const hasTracked = useRef(false);

useEffect(() => {
    // if (!hasTracked.current) {
    //     console.log("Gọi truycap(id):", id);
        truycap(id)
            .then(() => {
                hasTracked.current = true; 
            })
            .catch(() => toast.error("Ghi nhận truy cập thất bại"));
    // }
}, [id]);

    useEffect(() => {
        if (data.anhBia) {
            setImagePick(data.anhBia);
            setPrevImage(data.anhBia);
        }
    }, [data]);

    const handleImageClick = (url) => {
        if (url === imagePick) return;
        setPrevImage(imagePick);
        setImagePick(url);
    };

    useEffect(() => {
        if (!imageNextRef.current) return;

        const tl = gsap.timeline();
        tl.set(imageNextRef.current, {
            y: "100%",
            zIndex: 2,
            opacity: 1,
            position: "absolute",
        });
        tl.to(imageNextRef.current, {
            y: "0%",
            duration: 0.8,
            ease: "power3.out",
        });
    }, [imagePick]);

    useEffect(() => {
        const flashsale = variantPick?.thonTinFlashsale;
        if (!flashsale || flashsale?.giaTriGiam <= 0 || !countdownRef.current) return;

        const [sh, sm, ss] = flashsale.thoiGianBatDau.split(":").map(Number);
        const [eh, em, es] = flashsale.thoiGianKetThuc.split(":").map(Number);

        const startTime = new Date();
        startTime.setHours(sh, sm, ss, 0);
        const endTime = new Date();
        endTime.setHours(eh, em, es, 0);

        const updateCountdown = () => {
            const now = new Date();
            const formatTime = (n) => String(n).padStart(2, "0");

            if (now < startTime) {
                const diff = startTime - now;
                const h = Math.floor(diff / 3600000);
                const m = Math.floor((diff % 3600000) / 60000);
                const s = Math.floor((diff % 60000) / 1000);
                countdownRef.current.textContent = `SẮP ĐẾN ${formatTime(h)}:${formatTime(m)}:${formatTime(s)}`;
            } else if (now < endTime) {
                const diff = endTime - now;
                const h = Math.floor(diff / 3600000);
                const m = Math.floor((diff % 3600000) / 60000);
                const s = Math.floor((diff % 60000) / 1000);
                countdownRef.current.textContent = `KẾT THÚC TRONG ${formatTime(h)}:${formatTime(m)}:${formatTime(s)}`;
            } else {
                countdownRef.current.textContent = "ĐÃ KẾT THÚC";
            }
        };

        const interval = setInterval(updateCountdown, 1000);
        updateCountdown();
        return () => clearInterval(interval);
    }, [variantPick]);
    const [soLuongDat, setSoLuongDat] = useState(1);
    const handleStarClick = (rating) => {
        setSoSao(rating);
    };
    let trungBinh5 = data?.soLuotDanhGia > 0 ? Math.round((data?.so5Sao / data?.soLuotDanhGia) * 100) : 0;
    let trungBinh4 = data?.soLuotDanhGia > 0 ? Math.round((data?.so4Sao / data?.soLuotDanhGia) * 100) : 0;
    let trungBinh3 = data?.soLuotDanhGia > 0 ? Math.round((data?.so3Sao / data?.soLuotDanhGia) * 100) : 0;
    let trungBinh2 = data?.soLuotDanhGia > 0 ? Math.round((data?.so2Sao / data?.soLuotDanhGia) * 100) : 0;
    let trungBinh1 = data?.soLuotDanhGia > 0 ? Math.round((data?.so1Sao / data?.soLuotDanhGia) * 100) : 0;
    return (
        <>
            <div className="flex bg-gray-50 mb-2">
                {/* Ảnh chính và ảnh phụ */}
                <div className="basis-[40%] flex items-start bg-white pr-8 pt-8 pl-8 ">
                    <div className="basis-1/6 p-1">
                        {data?.anhGioiThieu?.map((d) => (
                            <div className="border-gray-400 border mt-2" key={d?.id}>
                                <img
                                    onClick={() => handleImageClick(d?.duongDan)}
                                    src={d?.duongDan}
                                    className="w-full h-auto mb-2 cursor-pointer"
                                    alt="Thumbnail"
                                />
                            </div>
                        ))}
                    </div>
                    <div className="basis-5/6 flex items-center justify-center p-2">
                        <div className="w-full h-0 pb-[100%] relative overflow-hidden">
                            <img
                                src={prevImage}
                                className="absolute top-0 left-0 w-full h-full object-cover z-[1]"
                                alt="Previous"
                            />
                            <img
                                ref={imageNextRef}
                                src={imagePick}
                                className="absolute top-0 left-0 w-full h-full object-cover z-[2]"
                                alt="Next"
                            />
                        </div>
                    </div>
                </div>

                <div className="basis-[37%] p-2 bg-white pr-8 pt-8 pb-8">
                    <div className="flex items-center mb-2">
                        <img className="h-5" src="https://hasaki.vn/icon/icon_nowfree.png" alt="icon" />
                        <p className="ml-1 font-bold text-green-900">{data?.tenThuongHieu}</p>
                    </div>
                    <h4 className="text-xl font-bold">{data?.ten}</h4>
                    <div className="flex items-center mt-1">
                        {"★★★★★".split("").map((s, i) => (
                            <span key={i} className="text-yellow-500">{s}</span>
                        ))}
                        <p className="ml-2">{data?.soLuotDanhGia} lượt đánh giá</p>
                    </div>

                    {variantPick?.thonTinFlashsale?.giaTriGiam > 0 && (
                        <div className="bg-orange-500 text-white flex justify-between items-center p-2 mt-2 text-sm">
                            <img className="h-5" src="https://hasaki.vn/icons/flash_deal_title.svg" alt="Flash Deal" />
                            <span ref={countdownRef} className="font-bold"></span>
                        </div>
                    )}

                    <div className="flex items-end mt-2">
                        <p className="font-bold text-orange-500 text-xl pr-4">
                            {variantPick?.giaGiam?.toLocaleString()}đ
                        </p>
                        <p>(Chưa bao gồm VAT: <span className="font-bold">{data?.thueVAT} %</span>)</p>
                    </div>
                    <span className="text-sm">Giá thị trường: {variantPick?.giaThiTruong?.toLocaleString()}đ</span>

                    {variantPick?.thonTinFlashsale?.giaTriGiam > 0 && (
                        <div className="text-sm mt-1">
                            - Tiết kiệm {(variantPick?.giaThiTruong - variantPick?.giaGiam)?.toLocaleString()}đ (
                            <span className="text-orange-500 font-bold">{variantPick?.thonTinFlashsale?.giaTriGiam} %</span>)
                        </div>
                    )}

                    <div className="flex items-center mt-3">
                        <img className="w-6 mr-2" src={Classify} alt="Phân loại" />
                        <p className="font-bold">Phân loại:</p>
                    </div>
                    <div className="flex flex-wrap gap-2 mt-2">
                        {data?.thonTinBienThe?.map((g, idx) => (
                            <div key={idx} className="flex items-center gap-2 cursor-pointer" onClick={() => {
                                setVariantPick(g);
                                handleImageClick(g?.anhBia);
                            }}>
                                <img className="h-12 border border-gray-300" src={g?.anhBia} alt={g?.ten} />
                                <p className="font-medium">{g?.ten}</p>
                            </div>
                        ))}
                    </div>

                    <p className="mt-3">Số lượng hàng: {variantPick?.soLuong} sản phẩm</p>
                    <div className="mt-2">
                        <span>Đặt hàng: </span>
                        <input value={soLuongDat} onChange={(e) => { setSoLuongDat(parseInt(e.target.value)) }} defaultValue={1} min={1} max={variantPick.soLuong} type="number" className="outline-none border border-gray-400 w-12 h-6 rounded-sm text-gray-500 pl-1" />
                    </div>

                    <div
                        onClick={() => {
                            const item = {
                                idBienThe: variantPick.id,
                                soLuong: soLuongDat
                            };
                            if (cartUpdate?.data?.length != 0) {
                                setCartUpdate({
                                    ...cartUpdate,
                                    data: [...cartUpdate.data, item]
                                })
                                setChange(true)
                                toast.success("Đã cập nhật vào đơn")
                            }
                            else {
                                let cart = JSON.parse(localStorage.getItem("cart") || "[]");
                                cart.push(item);
                                setCart(cart);
                                localStorage.setItem("cart", JSON.stringify(cart));
                                toast.success("Thêm vào giỏ hàng thành công");

                            }


                        }}
                        className={`${(JSON.parse(localStorage.getItem("cart") || "[]")).some(item => item.idBienThe === variantPick.id) ||
                            cartUpdate?.data?.some(item => item.idBienThe === variantPick.id)
                            ? "bg-gray-300 text-white pointer-events-none"
                            : "bg-green-900 text-green-200"
                            } flex font-bold w-fit p-1 px-2 rounded-sm mt-4 cursor-pointer`}
                    >
                        <p>🛒 Thêm vào giỏ hàng</p>
                    </div>

                </div>

                <div className="basis-[23%] flex flex-col gap-2 ml-2 bg-white pt-8 pr-3">
                    <div className="flex items-start gap-2 items-center denbu">
                        <img src={BaoHanh} className="w-[25%]" alt="Bảo hành" />
                        <p>SKINLY đền bù 100% hãng đền bù 100% nếu phát hiện hàng giả</p>
                    </div>
                    <div className="flex items-start gap-2 items-center">
                        <img src={GiaoNhanh} className="w-[25%]" alt="Giao nhanh" />
                        <p>Giao hàng cực nhanh nội thành.</p>
                    </div>
                    <div className="flex items-start gap-2 items-center">
                        <img src={DoiTra} className="w-[25%]" alt="Đổi trả" />
                        <p>Đổi trả miễn phí sau khi kiểm hàng.</p>
                    </div>
                    <div className="justify-center flex">
                        <img src={data?.anhGioiThieuThuongHieu} className="w-[75%] cursor-pointer" onClick={() => {
                            navigate("/customer/thuonghieu?id=" + data?.idthuongHieu)
                        }}></img>
                    </div>
                </div>
            </div>
            {variantPick?.giaGiamKem?.length != 0 ? <div className="bg-gray-50 p-1">
                <div className=" bg-white mt-2">
                    <div className="pr-8 pt-6 pl-8 pb-3">
                        <div className="flex items-end text-orange-400 mt-b">
                            <img src={Deal} className="w-10" alt="Deal" />
                            <strong><p>DEAL GIẢM GIÁ CỰC SỐC</p></strong>
                        </div>
                        <div className="flex mt-4 flex-wrap">
                            {variantPick?.giaGiamKem?.map((df, index) => (
                                <div onClick={()=>{navigate(`/customer/detailproduct/${df.id}`)}} key={index} className="flex cursor-pointer p-2 border shadow mr-3 mb-3 rounded-md w-full sm:w-auto sm:max-w-xs">
                                    <img className="h-12 mr-3" src={df.anhDaiDien} alt="" />
                                    <div className="text-sm max-w-[150px]">
                                        <p className="line-clamp-1 break-words" title={df.ten}>
                                            {df.ten.length > 25 ? df.ten.slice(0, 25) + '...' : df.ten}
                                        </p>
                                        <span>Giảm: ({df.tiLeGiam}%)</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div> : null}
            {variantPick?.muaTrenGiamDuoi?.length != 0 ? <div className="bg-gray-50 p-1">
                <div className=" bg-white mt-2">
                    <div className="pr-8 pt-6 pl-8 pb-3">
                        <div className="flex items-end text-orange-400 mt-b">
                            <img src={Deal} className="w-10" alt="Deal" />
                            <p className="font-bold">DEAL - MUA THÊM ĐƯỢC GIẢM</p>
                        </div>
                        <div className="flex mt-4 flex-wrap">
                            {variantPick?.muaTrenGiamDuoi?.map((df, index) => (
                                <div onClick={()=>{navigate(`/customer/detailproduct/${df.id}`)}} key={index} className="flex cursor-pointer p-2 border shadow-md mr-3 mb-3 rounded-md w-full sm:w-auto sm:max-w-xs">
                                    <img className="h-12 mr-3" src={df.anhDaiDien} alt="" />
                                    <div className="text-sm max-w-[150px]">
                                        <p className="line-clamp-1 break-words" title={df.ten}>
                                            {df.ten.length > 25 ? df.ten.slice(0, 25) + '...' : df.ten}
                                        </p>
                                        <span>Mua từ : ({df?.soLuongMuaDuocGiam} sản phẩm)</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div> : null}

            <div className="flex  bg-gray-50 p-2">
                <div className="basis-[75%] mb-2 ">
                    <div className="bg-white p-6">
                        <p className="font-bold text-green-800 mb-1">MÔ TẢ</p>
                        <div className=" prose max-w-none mo-ta-html " dangerouslySetInnerHTML={{ __html: data.moTa }} />
                    </div>
                    <div className="bg-white p-6 mb-2">
                        <p className="font-bold text-green-800 mb-1">THÀNH PHẦN</p>
                        <div className=" prose max-w-none mo-ta-html " dangerouslySetInnerHTML={{ __html: data.thanhPhan }} />
                    </div>
                    <div className="bg-white p-6">
                        <p className="font-bold text-green-800 mb-1">CÁCH DÙNG</p>
                        <div className=" prose max-w-none mo-ta-html " dangerouslySetInnerHTML={{ __html: data.cachDung }} />
                    </div>
                    <div className="mt-2 bg-white p-6">
                        <p className="text-xl font-bold">Đánh giá</p>
                        <div className="flex mt-2">
                            <div className="basis-2/6">
                                <p className="text-center">Đánh giá trung bình</p>
                                <p className="font-bold text-7xl text-center text-orange-500">{data?.soSao / data?.soLuotDanhGia != 0 ? data?.soLuotDanhGia : 1}</p>
                                <div className="text-center">
                                    {"★★★★★".split("").map((s, i) => (
                                        <span key={i} className="text-orange-500 pr-2 ">{s}</span>
                                    ))}
                                </div>
                                <p className="text-center">{data?.soLuotDanhGia} lượt nhận xét</p>
                            </div>
                            <div className="basis-2/6">
                                <div className="flex mb-2 items-center">
                                    <p>5 Sao</p>
                                    <div className="bg-gray-200 w-[45%] ml-2 h-5 mr-2">
                                        <div style={{ width: `${trungBinh5}%` }} className="bg-orange-500 h-full"></div>
                                    </div>
                                    <p>Rất hài lòng</p>
                                </div>
                                <div className="flex mb-2 items-center">
                                    <p>4 Sao</p>
                                    <div className="bg-gray-200 w-[45%] ml-2 h-5 mr-2">
                                        <div style={{ width: `${trungBinh4}%` }} className="bg-orange-500 h-full"></div>
                                    </div>
                                    <p>Hài lòng</p>
                                </div>
                                <div className="flex mb-2 items-center">
                                    <p>3 Sao</p>
                                    <div className="bg-gray-200 w-[45%] ml-2 h-5 mr-2">
                                        <div style={{ width: `${trungBinh3}%` }} className="bg-orange-500 h-full"></div>
                                    </div>
                                    <p>Bình thường</p>
                                </div>
                                <div className="flex mb-2 items-center">
                                    <p>2 Sao</p>
                                    <div className="bg-gray-200 w-[45%] ml-2 h-5 mr-2">
                                        <div style={{ width: `${trungBinh2}%` }} className="bg-orange-500 h-full"></div>
                                    </div>
                                    <p>Không hài lòng</p>
                                </div>
                                <div className="flex mb-2 items-center">
                                    <p>1 Sao</p>
                                    <div className="bg-gray-200 w-[45%] ml-2 h-5 mr-2">
                                        <div style={{ width: `${trungBinh1}%` }} className="bg-orange-500 h-full"></div>
                                    </div>
                                    <p>Rất tệ</p>
                                </div>
                            </div>
                            <div className="basis-2/6 items-center">
                                <p className="text-center ">Chia sẻ của bạn về Sản phẩm này</p>
                                {data?.duocPhanHoi && (
                                    <div>
                                        <div className="flex justify-center mb-2">
                                            {[1, 2, 3, 4, 5].map((star) => (
                                                <span
                                                    key={star}
                                                    className={`text-2xl cursor-pointer ${soSao >= star ? 'text-orange-500' : 'text-gray-300'}`}
                                                    onClick={() => handleStarClick(star)}
                                                >
                                                    ★
                                                </span>
                                            ))}
                                        </div>
                                        <textarea
                                            className="w-full border border-gray-500 p-2 m-2 outline-none"
                                            value={noiDungPhanHoi}
                                            onChange={(e) => setNoiDungPhanHoi(e.target.value)}
                                            placeholder="Nhập bình luận của bạn..."
                                        />
                                    </div>
                                )}
                                <p className="text-center">
                                    <button
                                        onClick={() => {
                                            if (soSao === 0) {
                                                toast.error("Vui lòng cung cấp số sao đánh giá");
                                                return;
                                            }
                                            if (noiDungPhanHoi === "") {
                                                toast.error("Vui lòng cung cấp nội dung phản hồi");
                                                return;
                                            }

                                            const userName = localStorage.getItem("name") || "Khách hàng ẩn danh";
                                            const newComment = {
                                                noiDungPhanHoi,
                                                soSao,
                                                tenKhachHang: userName,
                                                ngayGioiPhanHoi: new Date().toISOString(),
                                            };

                                            save(noiDungPhanHoi, soSao, parseInt(id))
                                                .then(() => {
                                                    toast.success("Thêm phản hồi thành công");
                                                    
                                                    // Update local state with new comment
                                                    setData((prevData) => {
                                                        const updatedPhanHoi = [...(prevData.thongTinPhanHoi || []), newComment];
                                                        const updatedSoSao = {
                                                            so1Sao: prevData.so1Sao || 0,
                                                            so2Sao: prevData.so2Sao || 0,
                                                            so3Sao: prevData.so3Sao || 0,
                                                            so4Sao: prevData.so4Sao || 0,
                                                            so5Sao: prevData.so5Sao || 0,
                                                        };
                                                        updatedSoSao[`so${soSao}Sao`] += 1;

                                                        return {
                                                            ...prevData,
                                                            thongTinPhanHoi: updatedPhanHoi,
                                                            duocPhanHoi:false,
                                                            soLuotDanhGia: (prevData.soLuotDanhGia || 0) + 1,
                                                            soSao: (prevData.soSao || 0) + soSao,
                                                            ...updatedSoSao,
                                                        };
                                                    });

                                                    // Reset input fields
                                                    setNoiDungPhanHoi("");
                                                    setSoSao(0);
                                                })
                                                .catch(() => toast.error("Phản hồi không thành công"));
                                        }}
                                        className={`${data?.duocPhanHoi ? "bg-orange-500" : "bg-gray-400 pointer-events-none"} mt-2 text-white p-1 pl-2 pr-2 font-bold`}
                                    >
                                        Thêm bình luận
                                    </button>
                                </p>
                            </div>
                        </div>
                        <div className="mt-2 ">
                            {data?.thongTinPhanHoi?.map((item, index) => {
                                const date = new Date(item.ngayGioiPhanHoi);
                                const formattedDate = date.toLocaleTimeString('vi-VN', {
                                    hour: '2-digit',
                                    minute: '2-digit'
                                }) + ' | ' + date.toLocaleDateString('vi-VN');

                                return (
                                    <div key={index} className="mb-4">
                                        <div className="flex items-center text-sm">
                                            <span className="text-orange-500 mr-1">{'★'.repeat(item.soSao)}</span>
                                            <span className="font-bold text-green-600 mr-2">{item.tenKhachHang}</span>
                                            <span className="text-gray-500">{formattedDate}</span>
                                        </div>
                                        <p className="mt-1 text-sm mb-2">{item.noiDungPhanHoi}</p>
                                        <hr></hr>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                </div>
                <div className="basis-[25%] mr-3">
                    <h2 className="  font-bold text-xl pl-4 mb-3">Sản phẩm liên quan</h2>
                    {
                        data?.sanPhamLienQuan?.data?.map(m => {
                            if (m.id != data.id) {
                                return <div className="mb-4 w-full pr-2"><ProductItem product={m}></ProductItem></div>
                            }
                        })
                    }
                </div>
            </div>
        </>
    );
}

export { DetailProduct };
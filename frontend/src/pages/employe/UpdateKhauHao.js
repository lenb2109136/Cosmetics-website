import { useEffect, useRef, useState } from "react";
import { getByMaVach, getByMaVachForPhieuKiem, getByMaVachForPhieuKiemV2 } from "../../services/sanPhamService";
import Modal from "../../components/commons/modal";
import { deletePhieueletePhieuKiem, getUpdatePhieuKiem, saveKhauHao, updatePhieuKiem } from "../../services/PhieuNhapService";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import downAnimate from "../../assets/downAnimate.gif";
import { useNavigate, useParams } from "react-router-dom";

function kiemTraSoLuong(result, danhSachHoaDonBiHuy, soLuongThucTe, soLuongDaDat, soLuong) {
    const tongSoLuongTrongDon = result
        .filter(item => danhSachHoaDonBiHuy.includes(item.idHoaDon))
        .reduce((tong, item) => tong + item.tongSoLuongTrongDon, 0);
    const remainingQuantity = soLuongThucTe - (soLuong - tongSoLuongTrongDon);
    return remainingQuantity >= 0;
}

function UpdateKiemHang() {
    const { id } = useParams();
    const [open, setOpen] = useState(false);
    const [maVach, setMaVach] = useState("");
    const [danhSachGoiY, setDanhSachGoiY] = useState([]);
    const [danhSachNhap, setDanhSachNhap] = useState([]);
    const [openGhiChu, setOpenGhiChu] = useState(false);
    const [openGhiChu2, setOpenGhiChu2] = useState(false);
    const [openXacNhanTaoPhieu, setOpenXacNhanTaoPhieu] = useState(false);
    const [openConflictModal, setOpenConflictModal] = useState(false);
    const [openCancelConfirmModal, setOpenCancelConfirmModal] = useState(false); // New state for cancel confirmation
    const [conflictPhieuKiemId, setConflictPhieuKiemId] = useState(null);
    const pick = useRef(null);
    const inputRef = useRef(null);
    const [loai, setLoai] = useState([]);
    const [pickLoai, setPickLoai] = useState([]);
    const pickk = useRef({});
    const nhanVien = localStorage.getItem("name") || "Chưa xác định";
    const [dataUpdate, setDataUpdate] = useState({});
    const [dung, setDung] = useState(false);
    const [load, setLoad] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        getUpdatePhieuKiem(id)
            .then(d => setDataUpdate(d))
            .catch(() => toast.error("Lấy phiếu nhập thất bại"));
    }, [id]);

    useEffect(() => {
        inputRef.current?.focus();
    }, []);

    useEffect(() => {
        if (maVach.trim()) {
            getByMaVachForPhieuKiemV2(maVach)
                .then((d) => setDanhSachGoiY(d.slice(0, 5)))
                .catch(() => setDanhSachGoiY([]));
        } else {
            setDanhSachGoiY([]);
        }
    }, [maVach]);

    const handleAddItem = (item) => {
        if (item.phieuKiemDaCo !== 0 && item.phieuKiemDaCo !== parseInt(id)) {
            setConflictPhieuKiemId(item.phieuKiemDaCo);
            setOpenConflictModal(true);
            return;
        }

        const exists = dataUpdate.danhSachChiTiet?.some(d => d.idSanPham === item.idSanPham);
        if (exists) {
            toast.error("Phân loại này đã được chọn, vui lòng kiểm tra lại");
            return;
        }
        dataUpdate.danhSachChiTiet.push(item);
        setLoad(!load);
    };

    const handleChangeSoLuong = (index, value) => {
        const newSoLuong = parseInt(value) || 0;
        if (newSoLuong <= 0) {
            setDanhSachNhap(prev => prev.filter((_, i) => i !== index));
        } else {
            setDanhSachNhap(prev => {
                const newList = [...prev];
                newList[index] = { ...newList[index], soLuong: newSoLuong };
                return newList;
            });
        }
    };

    const handleChangeSoLuongInput = (index, value) => {
        const newSoLuong = parseInt(value) || 0;
        if (newSoLuong > (dataUpdate.danhSachChiTiet[index].soLuongThucTe + dataUpdate.danhSachChiTiet[index].soLuongDaDat)) {
            toast.error(`Số lượng không được vượt quá: ${dataUpdate.danhSachChiTiet[index].soLuongThucTe + dataUpdate.danhSachChiTiet[index].soLuongDaDat}`);
            return;
        }
        setDataUpdate(prev => {
            const updatedDanhSachChiTiet = [...prev.danhSachChiTiet];
            updatedDanhSachChiTiet[index] = {
                ...updatedDanhSachChiTiet[index],
                soLuong: newSoLuong,
            };
            return {
                ...prev,
                danhSachChiTiet: updatedDanhSachChiTiet,
            };
        });
    };

    const handleUpdateGhiChu = (newGhiChu) => {
        if (pick.current >= 0 && pick.current < dataUpdate.danhSachChiTiet.length) {
            setDataUpdate(prev => {
                const updatedDanhSachChiTiet = [...prev.danhSachChiTiet];
                updatedDanhSachChiTiet[pick.current] = {
                    ...updatedDanhSachChiTiet[pick.current],
                    ghiChu: newGhiChu,
                };
                return {
                    ...prev,
                    danhSachChiTiet: updatedDanhSachChiTiet,
                };
            });
        }
        setOpenGhiChu(false);
    };

    const handleUpdateDanhSachHoaDonBiHuy = (newPickLoai) => {
        let ui = Array.isArray(newPickLoai) ? newPickLoai.map(m => m?.idHoaDon) : [];
        pickk.current.danhSachHoaDonBiHuy = ui;
        const isEnough = kiemTraSoLuong(
            pickk.current.result,
            pickk.current.danhSachHoaDonBiHuy,
            pickk.current.soLuongThucTe,
            pickk.current.soLuongDaDat,
            pickk.current.soLuong
        );
        setDung(isEnough);
        let s = Array.isArray(pickk.current.result)
            ? pickk.current.result.reduce((t, d) => ui.includes(d.idHoaDon) ? t + d.tongSoLuongTrongDon : t, 0)
            : 0;
        console.log("soLuong:", pickk?.current?.soLuong);
        console.log("Remaining:", pickk.current.soLuongThucTe - (pickk.current.soLuong - s));
        console.log("Is Enough:", isEnough);
    };

    const createKhauHao = () => {
        if (danhSachNhap.length === 0) {
            toast.warning("Danh sách sản phẩm trống");
            return;
        }
        saveKhauHao(danhSachNhap, pickLoai)
            .then(() => {
                toast.success("Cập nhật thành công");
                setDanhSachNhap([]);
            })
            .catch((e) => {
                const status = e?.response?.status;
                const message = e?.response?.data?.message || "Tạo phiếu thất bại";
                if (status === 400) {
                    toast.error("Lỗi dữ liệu kiểm kê: " + message);
                } else if (status === 500) {
                    setLoai(e?.response?.data?.data);
                }
            });
    };

    
    const handleCancelInvoice = () => {
        deletePhieueletePhieuKiem(id).then(()=>{
            toast.success("Hủy phiếu thành công");
            navigate("/employee/khauhao");
        }).catch(()=>{
            toast.error("Hủy phiếu thành công");
        })
        
    };

    return (
        <div className="p-4 space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-xl font-bold w-fit p-2 rounded-md shadow-md border">
                    <i className="fa-solid fa-file-pen mr-1 text-green-900"></i> Cập nhật thông tin
                </h2>
                <div className="flex items-center gap-6 text-sm text-gray-700">
                    <span className="p-2 shadow-md rounded-md border text-green-900">Nhân viên: <strong>{dataUpdate?.nhanVienLap}</strong></span>
                    <span className="p-2 shadow-md rounded-md border text-green-900">Ngày: {dinhDangNgay(dataUpdate?.ngayLapPhieu)}</span>
                </div>
            </div>

            <div className="flex flex-wrap gap-4 items-start justify-between w-full">
                <div className="relative w-full max-w-xl flex-1">
                    <input
                        ref={inputRef}
                        className="outline-none border border-gray-400 w-full rounded-md pl-4 py-2 text-sm"
                        placeholder="Nhập tên hoặc mã hàng hóa"
                        value={maVach}
                        onFocus={() => setOpen(true)}
                        onBlur={() => setTimeout(() => setOpen(false), 200)}
                        onChange={(e) => setMaVach(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter' && danhSachGoiY.length > 0) {
                                handleAddItem(danhSachGoiY[0]);
                            }
                        }}
                    />
                    {open && (
                        <div className="absolute bg-white border border-green-900 rounded shadow-md w-full mt-2 z-10 max-h-[250px] overflow-y-auto custom-scrollbar">
                            {danhSachGoiY.length > 0 ? (
                                danhSachGoiY.map((item) => (
                                    <div
                                        key={item.id}
                                        className="px-4 py-2 hover:bg-green-900 hover:text-white cursor-pointer text-sm"
                                        onClick={() => handleAddItem(item)}
                                    >
                                        <div className="font-semibold">{item.sanPham} - {item.tenBienThe}</div>
                                        <div className="text-xs text-gray-500">Mã: {item.maVach}</div>
                                    </div>
                                ))
                            ) : (
                                <div className="px-4 py-2 text-gray-500 text-sm">Không tìm thấy sản phẩm</div>
                            )}
                        </div>
                    )}
                </div>
                {!dataUpdate?.daXacNhan && (
                    <div>
                        <button
                            onClick={() => setOpenXacNhanTaoPhieu(true)}
                            className="text-green-800 px-4 py-2 rounded transition whitespace-nowrap rounded-md shadow-md border"
                        >
                            Cập nhật thông tin
                        </button>
                        <button
                            onClick={() => setOpenCancelConfirmModal(true)}
                            className="text-red-800 px-4 py-2 ml-2 rounded transition whitespace-nowrap rounded-md shadow-md border"
                        >
                            Hủy phiếu
                        </button>
                    </div>
                )}
            </div>

            <div className="overflow-auto shadow-md border">
                <table className="w-full border-collapse shadow-md rounded-md overflow-hidden">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="p-2">STT</th>
                            <th className="p-2">Mã hàng</th>
                            <th className="p-2">Tên hàng - phân loại</th>
                            <th className="p-2">Số lượng thực tại cửa hàng</th>
                            <th className="p-2">Số lượng hao hụt</th>
                            <th className="p-2 w-[200px]">Ghi chú</th>
                            {dataUpdate?.daXacNhan === false && <th className="p-2">Xoá</th>}
                        </tr>
                    </thead>
                    <tbody>
                        {dataUpdate?.danhSachChiTiet?.length === 0 ? (
                            <tr className="text-center">
                                <td colSpan={7}>
                                    <div className="w-full flex justify-center">
                                        <img
                                            className="w-44"
                                            src="https://media.istockphoto.com/id/861576608/vi/vec-to/bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-empty-shopping-bag-m%E1%BA%ABu-bi%E1%BB%83u-t%C6%B0%E1%BB%A3ng-vector-online-business.jpg?s=612x612&w=0&k=20&c=-6p0qVyb_p8VeHVu1vJVaDHa6Wd_mCjMkrFBTlQdehI="
                                            alt="empty"
                                        />
                                    </div>
                                </td>
                            </tr>
                        ) : (
                            dataUpdate?.danhSachChiTiet?.map((item, index) => (
                                <tr key={item.id} className={`text-center ${loai?.some(d => d?.id === item.id) ? "bg-gray-100" : ""}`}>
                                    <td className="border p-2">{index + 1}</td>
                                    <td className="border p-2">{item.maVach}</td>
                                    <td className="border p-2 cursor-pointer">
                                        {item.sanPham + " - " + item.tenBienThe}
                                    </td>
                                    <td className={`border p-2 flex ${dataUpdate?.daXacNhan ? "justify-center" : "justify-between"} items-center`}>
                                        {dataUpdate?.daXacNhan === false && (
                                            item.soLuongThucTe + item.soLuongDaDat + ' [' + item.soLuongDaDat + ' đã được đặt]'
                                        )}
                                        <span
                                            onClick={() => {
                                                pickk.current = item;
                                                setPickLoai(item.result.filter(r => item.danhSachHoaDonBiHuy.includes(r.idHoaDon)));
                                                const isEnough = kiemTraSoLuong(
                                                    item.result,
                                                    item.danhSachHoaDonBiHuy,
                                                    item.soLuongThucTe,
                                                    item.soLuongDaDat,
                                                    item.soLuong
                                                );
                                                setDung(isEnough);
                                                setOpenGhiChu2(true);
                                            }}
                                            className="relative ml-2 mr-2 cursor-pointer flex items-center justify-center size-6"
                                        >
                                            {kiemTraSoLuong(
                                                item.result,
                                                item.danhSachHoaDonBiHuy,
                                                item.soLuongThucTe,
                                                item.soLuongDaDat,
                                                item.soLuong
                                            ) ||
                                            (item?.danhSachHoaDonBiHuy?.length > 0 && item?.soLuongThucTe >= item?.soLuong) ? (
                                                <span className="absolute inline-flex h-full w-full rounded-full bg-green-400"></span>
                                            ) : (
                                                <>
                                                    <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-yellow-400 opacity-75"></span>
                                                    <span className="absolute inline-flex h-full w-full rounded-full bg-yellow-300"></span>
                                                </>
                                            )}
                                            <i className="fa-solid fa-bug text-green-900 rounded-md border shadow-md bg-white p-2 text-[10px] z-10"></i>
                                        </span>
                                    </td>
                                    <td className="border p-2">
                                        {dataUpdate?.daXacNhan === false ? (
                                            <input
                                                type="number"
                                                className="outline-none border border-gray-300 text-center w-[60px]"
                                                value={item.soLuong}
                                                max={item.soLuongThucTe + item?.soLuongDaDat}
                                                onChange={(e) => handleChangeSoLuongInput(index, e.target.value)}
                                            />
                                        ) : (
                                            item.soLuong
                                        )}
                                    </td>
                                    <td
                                        className="border p-2 text-left cursor-pointer"
                                        onClick={() => {
                                            pick.current = index;
                                            setOpenGhiChu(true);
                                        }}
                                    >
                                        {item.ghiChu
                                            ? item.ghiChu.length > 12
                                                ? item.ghiChu.slice(0, 12) + "..."
                                                : item.ghiChu
                                            : (
                                                <img
                                                    className="w-5 mx-auto"
                                                    src="https://cdn-icons-png.flaticon.com/128/1159/1159633.png"
                                                    alt="ghi chú"
                                                />
                                            )}
                                    </td>
                                    {dataUpdate?.daXacNhan === false && (
                                        <td className="border p-2">
                                            <button
                                                className=""
                                                onClick={() => {
                                                    dataUpdate.danhSachChiTiet.splice(index, 1);
                                                    setLoad(!load);
                                                }}
                                            >
                                                <i className="fa-solid fa-eraser text-green-900 p-2 rounded-md shadow-md border"></i>
                                            </button>
                                        </td>
                                    )}
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Modal ghi chú */}
            {openGhiChu && (
                <Modal setOpen={setOpenGhiChu}>
                    <p className="font-semibold mb-2">
                        <i className="fa-solid fa-notes-medical p-2 bg-blue-100 text-blue-500 rounded-md shadow-md"></i> Ghi chú hao hụt hàng hóa:
                    </p>
                    <textarea
                        defaultValue={dataUpdate.danhSachChiTiet[pick.current]?.ghiChu || ""}
                        className={`w-[400px] h-[200px] mt-3 border border-gray-300 outline-none pl-2 pt-1 rounded ${dataUpdate?.daXacNhan ? "pointer-events-none" : ""}`}
                        placeholder="Ghi chú lý do hao hụt..."
                        onBlur={(e) => handleUpdateGhiChu(e.target.value)}
                    />
                    <p className="text-sm mt-2 text-gray-500 italic">
                        (* Ghi chú sẽ lưu khi bạn rời khỏi ô nhập)
                    </p>
                </Modal>
            )}

            {/* Modal danh sách hóa đơn bị hủy */}
            {openGhiChu2 && (
                <Modal setOpen={setOpenGhiChu2}>
                    <div className="mb-1">
                        <i className="ml-4 fa-solid fa-triangle-exclamation bg-yellow-100 text-yellow-500 p-2 rounded-md"></i>
                        <strong><span className="ml-2">{dataUpdate?.daXacNhan === false ? "Thông tin lưu ý" : "Thông tin hóa đơn điều chỉnh"}</span></strong>
                    </div>
                    <div className="flex">
                        {dataUpdate?.daXacNhan === false && (
                            <>
                                <img className="h-5 pl-4 pr-2" src={downAnimate} alt="down animate" />
                                <span className="font-bold">
                                    Giảm số lượng hóa đơn đến mức phù hợp:
                                    {dung ? (
                                        <span className="font-bold text-green-600"> <i className="fa-solid fa-face-smile-beam"></i> Đủ rồi !!!</span>
                                    ) : (
                                        <span className="font-bold text-yellow-400"> <i className="fa-solid fa-face-sad-cry"></i> Chưa đủ !!!</span>
                                    )}
                                </span>
                            </>
                        )}
                    </div>
                    <div className="overflow-x-auto p-4">
                        <table className="min-w-full table-auto border-collapse border border-gray-300 rounded shadow-md">
                            <thead>
                                <tr className="bg-green-800 text-white text-center">
                                    <th className="px-4 py-2 border">STT</th>
                                    <th className="px-4 py-2 border">Tổng giá trị hóa đơn</th>
                                    <th className="px-4 py-2 border">Ngày lập</th>
                                    <th className="px-4 py-2 border">Số lượng sản phẩm</th>
                                    <th className="px-4 py-2 border">Trạng Thái</th>
                                    <th className="px-4 py-2 border">Chọn</th>
                                </tr>
                            </thead>
                            <tbody>
                                {pickk.current?.result?.map((j, index) => (
                                    <tr key={index} className="text-center hover:bg-green-50 transition duration-150">
                                        <td className="px-4 py-2 border">{index + 1}</td>
                                        <td className="px-4 py-2 border">{formatToVND(j.tongSoTien)}</td>
                                        <td className="px-4 py-2 border">{dinhDangNgay(j.ngayLap)}</td>
                                        <td className="px-4 py-2 border">{j.tongSoLuongTrongDon}</td>
                                        <td className="px-4 py-2 border">{j.tenTrangThai}</td>
                                        <td className={`px-4 py-2 border ${dataUpdate?.daXacNhan ? "pointer-events-none" : null}`}>
                                            <input
                                                type="checkbox"
                                                className="w-5 h-5 accent-green-600 cursor-pointer"
                                                checked={pickLoai.some(p => p.idHoaDon === j.idHoaDon)}
                                                onChange={(e) => {
                                                    let newPickLoai;
                                                    if (e.target.checked) {
                                                        newPickLoai = [...pickLoai, j];
                                                    } else {
                                                        newPickLoai = pickLoai.filter(p => p.idHoaDon !== j.idHoaDon);
                                                    }
                                                    setPickLoai(newPickLoai);
                                                    handleUpdateDanhSachHoaDonBiHuy(newPickLoai);
                                                }}
                                            />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </Modal>
            )}

            {/* Modal xác nhận cập nhật */}
            {openXacNhanTaoPhieu && (
                <Modal setOpen={setOpenXacNhanTaoPhieu}>
                    <h2 className="text-lg font-semibold mb-3">Xác nhận cập nhật phiếu hao hụt?</h2>
                    <p className="mb-4">Bạn có chắc chắn muốn cập nhật phiếu với <strong>{dataUpdate?.danhSachChiTiet.length}</strong> sản phẩm?</p>
                    <div className="flex justify-end gap-3">
                        <button
                            className="px-4 py-1 border rounded hover:bg-gray-100"
                            onClick={() => setOpenXacNhanTaoPhieu(false)}
                        >
                            Huỷ
                        </button>
                        <button
                            className="px-4 py-1 bg-green-800 text-white rounded hover:bg-green-700"
                            onClick={() => {
                                updatePhieuKiem(dataUpdate.id, dataUpdate?.danhSachChiTiet)
                                    .then((d) => {
                                        toast.success("Cập nhật thông tin thành công");
                                        setOpenXacNhanTaoPhieu(false);
                                        navigate(`/employee/khauhao/${d}`);
                                    })
                                    .catch((d) => {
                                        toast.error(d?.response?.data?.message || "Có lỗi xảy ra, vui lòng thử lại");
                                    });
                            }}
                        >
                            Đồng ý
                        </button>
                    </div>
                </Modal>
            )}

            {/* Modal xác nhận hủy phiếu */}
            {openCancelConfirmModal && (
                <Modal setOpen={setOpenCancelConfirmModal}>
                    <h2 className="text-lg font-semibold mb-3 text-red-700">Xác nhận hủy phiếu hao hụt?</h2>
                    <p className="mb-4">Bạn có chắc chắn muốn hủy phiếu hao hụt này?</p>
                    <div className="flex justify-end gap-3">
                        <button
                            className="px-4 py-1 border rounded hover:bg-gray-100"
                            onClick={() => setOpenCancelConfirmModal(false)}
                        >
                            Hủy
                        </button>
                        <button
                            className="px-4 py-1 bg-red-700 text-white rounded hover:bg-red-800"
                            onClick={handleCancelInvoice}
                        >
                            Đồng ý
                        </button>
                    </div>
                </Modal>
            )}

            {/* Modal for conflicting phieuKiemDaCo */}
            {openConflictModal && (
                <Modal setOpen={setOpenConflictModal}>
                    <div className="w-[260px]">
                        <h2 className="text-lg font-semibold mb-3 ml-4">Cảnh báo</h2>
                        <div className="mb-1">
                            <i className="ml-4 fa-solid fa-triangle-exclamation bg-yellow-100 text-yellow-500 p-2 rounded-md"></i>
                            <strong><span className="ml-2">Thông tin lưu ý</span></strong>
                        </div>
                        <p className="mb-4 ml-4">
                            Phân loại này đang trong quá trình xét hao hụt ở phiếu khác.
                            Bạn có muốn chỉnh sửa phiếu đó không?
                        </p>
                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-1 border rounded hover:bg-gray-100"
                                onClick={() => setOpenConflictModal(false)}
                            >
                                Không
                            </button>
                            <button
                                className="px-4 py-1 bg-green-800 text-white rounded hover:bg-green-700"
                                onClick={() => {
                                    window.open(`/employee/khauhao/${conflictPhieuKiemId}`, '_blank');
                                    setOpenConflictModal(false);
                                }}
                            >
                                Đồng ý
                            </button>
                        </div>
                    </div>
                </Modal>
            )}
        </div>
    );
}

export { UpdateKiemHang };
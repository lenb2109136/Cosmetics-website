import { toast } from "react-toastify";

function parseDateTimeFromParts(date, time) {
  if (!date || !time) return null;
  return new Date(`${date}T${time}`);
}

function getDatePart(dateTimeStr) {
  if (!dateTimeStr) return null;
  return dateTimeStr.split("T")[0];
}

function isTimeOverlap(start1, end1, start2, end2) {
  return start1 < end2 && start2 < end1;
}

function validateDuLieu(data) {
  const {
    ngayBatDau,
    ngayKetThuc,
    thoiGianBatDau,
    thoiGianKetThuc,
    data: sanPhamList
  } = data;

  if (!Array.isArray(sanPhamList) || sanPhamList.length === 0) {
    return { dung: false, message: "Cung cấp ít nhất một sản phẩm" };
  }

  if (!ngayBatDau || !ngayKetThuc || !thoiGianBatDau || !thoiGianKetThuc) {
    return { dung: false, message: "Thời gian bắt đầu/kết thúc không được để trống" };
  }

  const flashStart = parseDateTimeFromParts(getDatePart(ngayBatDau), thoiGianBatDau);
  const flashEnd = parseDateTimeFromParts(getDatePart(ngayKetThuc), thoiGianKetThuc);

  if (!flashStart || !flashEnd || isNaN(flashStart.getTime()) || isNaN(flashEnd.getTime())) {
    return { dung: false, message: "Thời gian không hợp lệ" };
  }

  if (flashStart >= flashEnd) {
    return { dung: false, message: "Thời điểm bắt đầu Flash Sale phải trước thời điểm kết thúc" };
  }

  for (const sp of sanPhamList) {
    const tenSP = sp.ten || "Không rõ tên sản phẩm";

    const bienTheHopLe = (sp.bienThe || []).filter(bt => {
      if (bt.giaGiam <= 0 || bt.soLuongKhuyenMai <= 0) return false;

      if (bt.soLuongKhuyenMai > bt.soLuongKho) {
        throw {
          dung: false,
          message: `Biến thể của sản phẩm "${tenSP}" có số lượng khuyến mãi (${bt.soLuongKhuyenMai}) vượt quá số lượng kho (${bt.soLuongKho})`
        };
      }
      

      return true;
    });

    if (bienTheHopLe.length === 0) {
      return {
        dung: false,
        message: `Sản phẩm "${tenSP}" không có biến thể hợp lệ để áp dụng Flash Sale`
      };
    }

    for (const bt of bienTheHopLe) {
      const danhSachFlash = bt.danhSachFlasSale || [];

      for (const fs of danhSachFlash) {
        if (!fs.thoiGianChay || !fs.thoiGianNgung) continue;

        const fsStart = new Date(fs.thoiGianChay);
        const fsEnd = new Date(fs.thoiGianNgung);

        if (isTimeOverlap(flashStart.getTime(), flashEnd.getTime(), fsStart.getTime(), fsEnd.getTime())) {
          return {
            dung: false,
            message: `Khung giờ Flash Sale mới bị trùng với khung giờ cũ của sản phẩm "${tenSP}" từ ${fs.thoiGianChay} đến ${fs.thoiGianNgung}`
          };
        }
      }
    }
  }

  return { dung: true, message: "Dữ liệu hợp lệ" };
}

export { validateDuLieu };

import { useEffect, useState, useRef } from "react";
import { getChiTiet } from "../../services/PhieuNhapService";
import { toast } from "react-toastify";
import { dinhDangNgay, formatToVND } from "../../utils/Format";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";

function tinhTongGiaTriPhieuNhap(phieuNhap) {
  let tongGiaTri = 0;
  phieuNhap?.sanPham?.forEach((sanPham) => {
    sanPham?.bienTheNhap?.forEach((bienThe) => {
      tongGiaTri += bienThe.soLuong * bienThe.donGia;
    });
  });
  return tongGiaTri;
}

function ChiTietPhieuNhap({ id }) {
  const [data, setData] = useState({});
  const componentRef = useRef(null); 

  useEffect(() => {
    getChiTiet(id)
      .then((data) => setData(data))
      .catch(() => {
        toast.error("Lấy dữ liệu thất bại");
      });
  }, [id]);

  const handleExportPDF = () => {
    const input = componentRef.current;
    if (!input) return;

    // Use html2canvas to capture the component
    html2canvas(input, {
      scale: 2, // Increase scale for better quality
      useCORS: true, // Enable CORS if external images are involved
    }).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF({
        orientation: "portrait",
        unit: "mm",
        format: "a4",
      });

      const imgWidth = 190; // Width of the image in PDF (in mm)
      const pageHeight = pdf.internal.pageSize.height;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;
      let position = 10; // Top margin

      pdf.addImage(imgData, "PNG", 10, position, imgWidth, imgHeight);
      heightLeft -= pageHeight - 20; 

      while (heightLeft > 0) {
        position = heightLeft - imgHeight + 10; // Adjust position for next page
        pdf.addPage();
        pdf.addImage(imgData, "PNG", 10, position, imgWidth, imgHeight);
        heightLeft -= pageHeight - 20;
      }

      // Save the PDF
      pdf.save(`PhieuNhap_${id}.pdf`);
    }).catch((error) => {
      console.error("Error generating PDF:", error);
      toast.error("Xuất PDF thất bại");
    });
  };

  return (
    <div>
      {/* Export Button */}
      <div className="mb-4 text-right">
        <button
          onClick={handleExportPDF}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Xuất PDF
        </button>
      </div>

      {/* Component Content */}
      <div
        ref={componentRef}
        className="max-w-5xl mx-auto p-6 border border-gray-300 rounded-md shadow-lg bg-white text-sm"
      >
        {/* Top info: Supplier & Receiver */}
        <div className="flex justify-between mb-4">
          <div className="w-1/2 space-y-1">
            <h2 className="text-lg font-bold">Thông tin nhà cung cấp</h2>
            <p>Đơn vị: <strong>{data?.tenCongTy}</strong></p>
            <p>Mã số thuế: {data?.maSoThueCongTy}</p>
            <p>SĐT: {data?.soDienThoaiCongTy}</p>
            <p>Địa chỉ: {data?.diaChiCongTy}</p>
          </div>
          <div className="w-1/2 flex justify-end">
            <div className="text-left space-y-1">
              <h2 className="text-lg font-bold">Nhân viên kiểm kê</h2>
              <p>Họ tên: <strong>{data?.tenNhanVien}</strong></p>
              <p>SĐT: {data?.soDienThoaiNhanVien}</p>
              <p>Địa chỉ: {data?.diaChiNhanVien}</p>
            </div>
          </div>
        </div>

        {/* Centered Title */}
        <div className="text-center my-6">
          <h1 className="text-xl font-bold uppercase">PHIẾU NHẬP HÀNG</h1>
          <p className="text-sm mt-1">Ngày nhập: {dinhDangNgay(data?.ngayNhap)}</p>
        </div>

        {/* Product Table */}
        <div>
          <h2 className="text-lg font-bold mb-2">Danh sách sản phẩm</h2>
          <table className="w-full border border-black text-center text-sm">
            <thead className="bg-gray-200">
              <tr>
                <th className="border border-black p-2">STT</th>
                <th className="border border-black p-2">Tên hàng</th>
                <th className="border border-black p-2">Phân loại</th>
                <th className="border border-black p-2">Số lượng</th>
                <th className="border border-black p-2">Đơn giá</th>
                <th className="border border-black p-2">Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              {data?.sanPham?.map((dat, index) => (
                <>
                  <tr key={`main-${index}`} className="border-t border-gray-300">
                    <td
                      className="border-t border-b border-l border-r border-black p-2 align-top align-middle text-center"
                      rowSpan={dat?.bienTheNhap?.length}
                    >
                      {index + 1}
                    </td>
                    <td
                      className="border-t border-b border-l border-r border-black p-2 align-top align-middle text-center"
                      rowSpan={dat?.bienTheNhap?.length}
                    >
                      {dat.tenSanPham}
                    </td>
                    <td className="border border-black p-2">
                      {dat?.bienTheNhap?.[0]?.bienThe?.ten}
                    </td>
                    <td className="border border-black p-2">
                      {dat?.bienTheNhap?.[0]?.soLuong}
                    </td>
                    <td className="border border-black p-2">
                      {formatToVND(dat?.bienTheNhap?.[0]?.donGia)}
                    </td>
                    <td className="border border-black p-2">
                      {formatToVND(
                        dat?.bienTheNhap?.[0]?.donGia * dat?.bienTheNhap?.[0]?.soLuong
                      )}
                    </td>
                  </tr>
                  {dat?.bienTheNhap?.map((f, inde) => {
                    if (inde === 0) return null;
                    return (
                      <tr  className="">
                        <td className="border border-black p-2">
                           <p> {f.bienThe.ten}</p>
                        </td>
                        <td className="border border-black p-2">{f.soLuong}</td>
                        <td className="border border-black p-2">{formatToVND(f.donGia)}</td>
                        <td className="border border-black p-2">
                          {formatToVND(f.donGia * f.soLuong)}
                        </td>
                      </tr>
                    );
                  })}
                </>
              ))}
              <tr className="border-t border-gray-300">
                <td className="border border-black p-2" colSpan={3}></td>
                <td className="border border-black p-2" colSpan={2}>
                  <strong>
                    <p className="pt-2 text-start">Tổng tiền</p>
                  </strong>
                </td>
                <td className="border border-black p-2">
                  <p className="pt-2">{formatToVND(tinhTongGiaTriPhieuNhap(data))}</p>
                </td>
              </tr>
              {/* <tr>
                <td className="border border-black p-2" colSpan={3}></td>
                <td className="border border-black p-2" colSpan={2}>
                  <strong>
                    <p className="text-start pt-2">
                      Tổng thuế GTGT <i>VAT</i>
                    </p>
                  </strong>
                </td>
                <td className="border border-black p-2">
                  <p className="pt-2">{formatToVND(data?.thueVAT)}</p>
                </td>
              </tr> */}
              {/* <tr>
                <td className="border border-black p-2" colSpan={3}></td>
                <td className="border border-black p-2" colSpan={2}>
                  <strong>
                    <p className="text-start pt-2 pb-2">Tổng tiền thanh toán:</p>
                  </strong>
                </td>
                <td className="border border-black p-2">
                  <p className="pt-2 pb-2">
                    {formatToVND(tinhTongGiaTriPhieuNhap(data) - data?.thueVAT)}
                  </p>
                </td>
              </tr> */}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export { ChiTietPhieuNhap };
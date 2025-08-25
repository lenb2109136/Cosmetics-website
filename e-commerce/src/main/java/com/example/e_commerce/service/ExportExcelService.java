package com.example.e_commerce.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;

import com.example.e_commerce.DTO.response.BienTheNhap;
import com.example.e_commerce.DTO.response.LanNhapKho;
import com.example.e_commerce.DTO.response.SanPhamPhieuNhap;

public class ExportExcelService {

	public static ByteArrayInputStream exportToExcel(List<SanPhamPhieuNhap> sanPhamList, LocalDate startDate, LocalDate endDate) throws IOException {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    try (Workbook workbook = new XSSFWorkbook()) {
	        Sheet sheet = workbook.createSheet("SanPhamPhieuNhap");

	        // Create cell style for title
	        CellStyle titleStyle = workbook.createCellStyle();
	        Font titleFont = workbook.createFont();
	        titleFont.setFontHeightInPoints((short) 18);
	        titleFont.setBold(true);
	        titleStyle.setFont(titleFont);
	        titleStyle.setAlignment(HorizontalAlignment.CENTER);
	        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Ensure vertical centering

	        // Create title row
	        Row titleRow = sheet.createRow(0);
	        Cell titleCell = titleRow.createCell(0);
	        titleCell.setCellValue("Thống kê nhập hàng từ ngày " + startDate.format(formatter) + " đến ngày " + endDate.format(formatter));
	        titleCell.setCellStyle(titleStyle);
	        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
	        titleRow.setHeightInPoints(40); // Increased height for better vertical centering

	        // Create cell style for headers
	     // Create cell style for headers
	        CellStyle headerStyle = workbook.createCellStyle();
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerStyle.setFont(headerFont);
	        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerStyle.setBorderTop(BorderStyle.MEDIUM);
	        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
	        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
	        headerStyle.setBorderRight(BorderStyle.MEDIUM);
	        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
	        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
	        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
	        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
	        headerStyle.setAlignment(HorizontalAlignment.CENTER); // Thêm dòng này để canh giữa
	        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Canh giữa theo chiều dọc (tùy chọn)

	        // Create cell style for data cells
	        CellStyle dataStyle = workbook.createCellStyle();
	        dataStyle.setBorderTop(BorderStyle.THIN);
	        dataStyle.setBorderBottom(BorderStyle.THIN);
	        dataStyle.setBorderLeft(BorderStyle.THIN);
	        dataStyle.setBorderRight(BorderStyle.THIN);
	        dataStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
	        dataStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
	        dataStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
	        dataStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
	        dataStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
	        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        dataStyle.setWrapText(true); // Enable text wrapping

	        // Create header row
	        Row headerRow = sheet.createRow(1);
	        String[] headers = {"Tên sản phẩm", "Phân loại", "Lần nhập", "Số lượng", "Đơn giá", "Tổng tiền"};
	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }
	        headerRow.setHeightInPoints(25);

	        int rowNum = 2;

	        for (SanPhamPhieuNhap sp : sanPhamList) {
	            List<BienTheNhap> danhSachBienThe = sp.getDanhSachBienTheNhap();
	            int startRowSanPham = rowNum;

	            for (BienTheNhap bienThe : danhSachBienThe) {
	                List<LanNhapKho> danhSachLanNhap = bienThe.getDanhSachNhap();
	                int startRowBienThe = rowNum;

	                for (LanNhapKho lanNhap : danhSachLanNhap) {
	                    Row row = sheet.createRow(rowNum++);
	                    row.setHeightInPoints(30); // Increased row height for wrapping
	                    Cell cell2 = row.createCell(2);
	                    cell2.setCellValue(lanNhap.getNgayNhap().format(formatter));
	                    cell2.setCellStyle(dataStyle);
	                    Cell cell3 = row.createCell(3);
	                    cell3.setCellValue(lanNhap.getSoLuong());
	                    cell3.setCellStyle(dataStyle);
	                    Cell cell4 = row.createCell(4);
	                    cell4.setCellValue(lanNhap.getGia());
	                    cell4.setCellStyle(dataStyle);
	                    Cell cell5 = row.createCell(5);
	                    cell5.setCellValue(lanNhap.getSoLuong() * lanNhap.getGia());
	                    cell5.setCellStyle(dataStyle);
	                }

	                // Merge cells for "Phân loại"
	                if (danhSachLanNhap.size() > 1) {
	                    sheet.addMergedRegion(new CellRangeAddress(
	                        startRowBienThe, rowNum - 1, 1, 1
	                    ));
	                }
	                Cell bienTheCell = sheet.getRow(startRowBienThe).createCell(1);
	                bienTheCell.setCellValue(bienThe.getTen());
	                bienTheCell.setCellStyle(dataStyle);

	                // Set column width for "Phân loại"
	                sheet.setColumnWidth(1, 5000);
	            }

	            // Merge cells for "Tên sản phẩm" and fix border/colspan
	            if (rowNum - startRowSanPham > 1) {
	                sheet.addMergedRegion(new CellRangeAddress(
	                    startRowSanPham, rowNum - 1, 0, 0
	                ));
	                for (int i = startRowSanPham; i < rowNum; i++) {
	                    Row row = sheet.getRow(i);
	                    if (row != null) {
	                        Cell cell = row.getCell(0);
	                        if (cell == null) cell = row.createCell(0);
	                        cell.setCellStyle(dataStyle);
	                    }
	                }
	            }
	            Cell sanPhamCell = sheet.getRow(startRowSanPham).createCell(0);
	            sanPhamCell.setCellValue(sp.getTenSanPham());
	            sanPhamCell.setCellStyle(dataStyle);
	            sanPhamCell.getCellStyle().setVerticalAlignment(VerticalAlignment.CENTER);

	            // Set increased column width for "Tên sản phẩm" with wrapping
	            sheet.setColumnWidth(0, 10000);
	        }

	        // Set column widths for other columns
	        sheet.setColumnWidth(2, 3000); // Lần nhập
	        sheet.setColumnWidth(3, 3000); // Số lượng
	        sheet.setColumnWidth(4, 4000); // Đơn giá
	        sheet.setColumnWidth(5, 5000); // Tổng tiền

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        workbook.write(out);
	        return new ByteArrayInputStream(out.toByteArray());
	    } catch (IOException e) {
	        throw new IOException("Failed to generate Excel file: " + e.getMessage(), e);
	    }
	}
	   public static ByteArrayInputStream exportToExcelLoiNhuan(Page<Map<String, Object>> sanPhamPage, LocalDateTime startDate, LocalDateTime endDate) throws IOException {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	        try (Workbook workbook = new XSSFWorkbook()) {
	            Sheet sheet = workbook.createSheet("BaoCaoLoiNhuan");

	            // Create cell style for title
	            CellStyle titleStyle = workbook.createCellStyle();
	            Font titleFont = workbook.createFont();
	            titleFont.setFontHeightInPoints((short) 18);
	            titleFont.setBold(true);
	            titleStyle.setFont(titleFont);
	            titleStyle.setAlignment(HorizontalAlignment.CENTER);
	            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

	            // Create cell style for header
	            CellStyle headerStyle = workbook.createCellStyle();
	            Font headerFont = workbook.createFont();
	            headerFont.setBold(true);
	            headerFont.setFontHeightInPoints((short) 12);
	            headerStyle.setFont(headerFont);
	            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            headerStyle.setBorderTop(BorderStyle.THIN);
	            headerStyle.setBorderBottom(BorderStyle.THIN);
	            headerStyle.setBorderLeft(BorderStyle.THIN);
	            headerStyle.setBorderRight(BorderStyle.THIN);
	            headerStyle.setAlignment(HorizontalAlignment.CENTER);
	            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

	            // Create cell style for data (text)
	            CellStyle textStyle = workbook.createCellStyle();
	            textStyle.setBorderTop(BorderStyle.THIN);
	            textStyle.setBorderBottom(BorderStyle.THIN);
	            textStyle.setBorderLeft(BorderStyle.THIN);
	            textStyle.setBorderRight(BorderStyle.THIN);
	            textStyle.setAlignment(HorizontalAlignment.LEFT);
	            textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	            textStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
	            textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            textStyle.setWrapText(true);

	            // Create cell style for data (numbers)
	            CellStyle currencyStyle = workbook.createCellStyle();
	            DataFormat format = workbook.createDataFormat();
	            currencyStyle.setBorderTop(BorderStyle.THIN);
	            currencyStyle.setBorderBottom(BorderStyle.THIN);
	            currencyStyle.setBorderLeft(BorderStyle.THIN);
	            currencyStyle.setBorderRight(BorderStyle.THIN);
	            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
	            currencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	            currencyStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
	            currencyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            currencyStyle.setDataFormat(format.getFormat("#,##0"));

	            // Create cell style for total row
	            CellStyle totalStyle = workbook.createCellStyle();
	            Font totalFont = workbook.createFont();
	            totalFont.setBold(true);
	            totalFont.setFontHeightInPoints((short) 12);
	            totalStyle.setFont(totalFont);
	            totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            totalStyle.setAlignment(HorizontalAlignment.CENTER);
	            totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	            totalStyle.setBorderTop(BorderStyle.THIN);
	            totalStyle.setBorderBottom(BorderStyle.THIN);
	            totalStyle.setBorderLeft(BorderStyle.THIN);
	            totalStyle.setBorderRight(BorderStyle.THIN);

	            // Create title row
	            Row titleRow = sheet.createRow(0);
	            Cell titleCell = titleRow.createCell(0);
	            titleCell.setCellValue("Thống kê lợi nhuận từ ngày " + startDate.format(formatter) + " đến ngày " + endDate.format(formatter));
	            titleCell.setCellStyle(titleStyle);
	            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
	            titleRow.setHeightInPoints(50);

	            // Create header row
	            Row headerRow = sheet.createRow(1);
	            String[] headers = {"Tên Sản Phẩm", "Tên Biến Thể", "Vốn", "Số Lượng Bán", "Doanh Thu", "Lợi Nhuận"};
	            for (int i = 0; i < headers.length; i++) {
	                Cell cell = headerRow.createCell(i);
	                cell.setCellValue(headers[i]);
	                cell.setCellStyle(headerStyle);
	            }
	            headerRow.setHeightInPoints(35);

	            // Set column widths
	            sheet.setColumnWidth(0, 10000); // Tên Sản Phẩm
	            sheet.setColumnWidth(1, 5000);  // Tên Biến Thể
	            sheet.setColumnWidth(2, 3000);  // Vốn
	            sheet.setColumnWidth(3, 3000);  // Số Lượng Bán
	            sheet.setColumnWidth(4, 3000);  // Doanh Thu
	            sheet.setColumnWidth(5, 3000);  // Lợi Nhuận

	            // Data rows
	            int rowNum = 2;
	            double totalVon = 0;
	            long totalSoLuongBan = 0;
	            double totalDoanhThu = 0;
	            double totalLoiNhuan = 0;

	            for (Map<String, Object> product : sanPhamPage.getContent()) {
	                @SuppressWarnings("unchecked")
	                List<Map<String, Object>> bienTheList = (List<Map<String, Object>>) product.get("bienThe");
	                if (bienTheList == null || bienTheList.isEmpty()) {
	                    continue;
	                }

	                int startRow = rowNum;
	                for (int i = 0; i < bienTheList.size(); i++) {
	                    Map<String, Object> variant = bienTheList.get(i);
	                    Row row = sheet.createRow(rowNum++);
	                    row.setHeightInPoints(30);

	                    // Product name with rowspan
	                    if (i == 0) {
	                        Cell productCell = row.createCell(0);
	                        productCell.setCellValue((String) product.get("ten"));
	                        productCell.setCellStyle(textStyle);
	                        if (bienTheList.size() > 1) {
	                            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + bienTheList.size() - 1, 0, 0));
	                        }
	                    }

	                    // Variant data
	                    Cell variantCell = row.createCell(1);
	                    variantCell.setCellValue((String) variant.get("ten"));
	                    variantCell.setCellStyle(textStyle);

	                    // Handle von
	                    Cell vonCell = row.createCell(2);
	                    double von = variant.get("vonBan") != null ? ((Number) variant.get("vonBan")).doubleValue() : 0.0;
	                    vonCell.setCellValue(von);
	                    vonCell.setCellStyle(currencyStyle);

	                    // Handle soLuongBan
	                    Cell soLuongBanCell = row.createCell(3);
	                    long soLuongBan = variant.get("soLuongBan") != null ? ((Number) variant.get("soLuongBan")).longValue() : 0L;
	                    soLuongBanCell.setCellValue(soLuongBan);
	                    soLuongBanCell.setCellStyle(currencyStyle);

	                    // Handle doanhThu
	                    Cell doanhThuCell = row.createCell(4);
	                    double doanhThu = variant.get("doanhThu") != null ? ((Number) variant.get("doanhThu")).doubleValue() : 0.0;
	                    doanhThuCell.setCellValue(doanhThu);
	                    doanhThuCell.setCellStyle(currencyStyle);

	                    // Handle loiNhuan
	                    Cell loiNhuanCell = row.createCell(5);
	                    double loiNhuan = variant.get("loiNhuan") != null ? ((Number) variant.get("loiNhuan")).doubleValue() : 0.0;
	                    loiNhuanCell.setCellValue(loiNhuan);
	                    loiNhuanCell.setCellStyle(currencyStyle);

	                    // Accumulate totals
	                    totalVon += von;
	                    totalSoLuongBan += soLuongBan;
	                    totalDoanhThu += doanhThu;
	                    totalLoiNhuan += loiNhuan;
	                }

	                // Ensure borders for merged cells
	                if (bienTheList.size() > 1) {
	                    for (int j = startRow; j < startRow + bienTheList.size(); j++) {
	                        Row mergedRow = sheet.getRow(j);
	                        if (mergedRow != null) {
	                            Cell mergedTenCell = mergedRow.getCell(0);
	                            if (mergedTenCell == null) mergedTenCell = mergedRow.createCell(0);
	                            mergedTenCell.setCellStyle(textStyle);
	                        }
	                    }
	                }

	                // Set vertical alignment for product name
	                Cell sanPhamCell = sheet.getRow(startRow).getCell(0);
	                if (sanPhamCell != null) {
	                    sanPhamCell.getCellStyle().setVerticalAlignment(VerticalAlignment.CENTER);
	                }
	            }

	            // Total row
	         // Total row
	            if (rowNum > 2) {
	                Row totalRow = sheet.createRow(rowNum);
	                for (int i = 0; i < headers.length; i++) {
	                    Cell cell = totalRow.createCell(i);
	                    cell.setCellStyle(totalStyle);
	                }
	                Cell totalLabelCell = totalRow.createCell(0);
	                totalLabelCell.setCellValue("Tổng");
	                sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));

	                Cell totalVonCell = totalRow.createCell(2);
	                totalVonCell.setCellValue(totalVon);
	                totalVonCell.setCellStyle(currencyStyle);

	                Cell totalSoLuongBanCell = totalRow.createCell(3);
	                totalSoLuongBanCell.setCellValue(totalSoLuongBan);
	                totalSoLuongBanCell.setCellStyle(currencyStyle);

	                Cell totalDoanhThuCell = totalRow.createCell(4);
	                totalDoanhThuCell.setCellValue(totalDoanhThu);
	                totalDoanhThuCell.setCellStyle(currencyStyle);

	                Cell totalLoiNhuanCell = totalRow.createCell(5);
	                totalLoiNhuanCell.setCellValue(totalLoiNhuan);
	                totalLoiNhuanCell.setCellStyle(currencyStyle);

	                // Đảm bảo border cho tất cả các ô trong hàng tổng
	                for (int i = 0; i < headers.length; i++) {
	                    Cell cell = totalRow.getCell(i);
	                    if (cell == null) {
	                        cell = totalRow.createCell(i);
	                    }
	                    cell.setCellStyle(totalStyle); // Áp dụng style có border
	                }
	                totalRow.setHeightInPoints(35);
	            }

	            // Write to ByteArrayInputStream
	            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	                workbook.write(out);
	                return new ByteArrayInputStream(out.toByteArray());
	            }
	        } catch (IOException e) {
	            throw new IOException("Failed to generate Excel file: " + e.getMessage(), e);
	        }
	    }
	public static ByteArrayInputStream exportToExcel(Page<Map<String, Object>> sanPhamPage, LocalDateTime bd, LocalDateTime kt) throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Xuất Nhập Kho");

        // Create cell style for title
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create title row
        Row headerRow = sheet.createRow(0);
        Cell titleCell = headerRow.createCell(0);
        titleCell.setCellValue("Xuất Nhập Kho từ ngày " + bd.format(formatter) + " đến " + kt.format(formatter));
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        headerRow.setHeightInPoints(50);

        // Create cell style for headers
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Đổi thành xám nhạt
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create cell style for data (text)
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setBorderTop(BorderStyle.THIN);
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);
        textStyle.setAlignment(HorizontalAlignment.LEFT);
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        textStyle.setWrapText(true);

        // Create cell style for data (numbers)
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.RIGHT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataStyle.setWrapText(true);

        // Create header for columns
        Row columnHeaderRow = sheet.createRow(1);
        String[] headers = {"Tên sản phẩm", "Phân loại", "Tồn kỳ trước", "Nhập trong kỳ", "Bán trong kỳ", "Hao hụt", "Tồn kỳ này"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = columnHeaderRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        columnHeaderRow.setHeightInPoints(35);

        // Set column widths
        sheet.setColumnWidth(0, 10000); // Tên sản phẩm
        sheet.setColumnWidth(1, 5000);  // Phân loại
        sheet.setColumnWidth(2, 3000);  // Tồn kỳ trước
        sheet.setColumnWidth(3, 3000);  // Nhập trong kỳ
        sheet.setColumnWidth(4, 3000);  // Bán trong kỳ
        sheet.setColumnWidth(5, 3000);  // Hao hụt
        sheet.setColumnWidth(6, 3000);  // Tồn kỳ này

        // Fill data
        int rowNum = 2;

        for (Map<String, Object> product : sanPhamPage.getContent()) {
            List<Map<String, Object>> variants = (List<Map<String, Object>>) product.get("xuatTonKho");
            int rowspan = variants.size();

            // Skip products with no variants
            if (rowspan == 0) {
                continue;
            }

            int startRowSanPham = rowNum;

            for (int i = 0; i < variants.size(); i++) {
                Map<String, Object> variant = variants.get(i);
                Row row = sheet.createRow(rowNum++);
                row.setHeightInPoints(30);

                // Tên sản phẩm (only write in the first row of each product)
                if (i == 0) {
                    Cell tenCell = row.createCell(0);
                    tenCell.setCellValue((String) product.get("ten"));
                    tenCell.setCellStyle(textStyle);
                    if (rowspan > 1) {
                        sheet.addMergedRegion(new CellRangeAddress(startRowSanPham, startRowSanPham + rowspan - 1, 0, 0));
                    }
                }

                // Phân loại
                Cell phanLoaiCell = row.createCell(1);
                phanLoaiCell.setCellValue((String) variant.get("id"));
                phanLoaiCell.setCellStyle(textStyle);

                // Tồn kỳ trước
                Cell tonKyTruocCell = row.createCell(2);
                tonKyTruocCell.setCellValue(((Number) variant.get("tonKyTruoc")).doubleValue());
                tonKyTruocCell.setCellStyle(dataStyle);

                // Nhập trong kỳ
                Cell nhapTrongKyCell = row.createCell(3);
                nhapTrongKyCell.setCellValue(((Number) variant.get("soLuongNhapKyNay")).doubleValue());
                nhapTrongKyCell.setCellStyle(dataStyle);

                // Bán trong kỳ
                Cell banTrongKyCell = row.createCell(4);
                banTrongKyCell.setCellValue(((Number) variant.get("soLuongXuatTrongKy")).doubleValue());
                banTrongKyCell.setCellStyle(dataStyle);

                // Hao hụt
                Cell haoHutCell = row.createCell(5);
                haoHutCell.setCellValue(((Number) variant.get("soLuongHuHao")).doubleValue());
                haoHutCell.setCellStyle(dataStyle);

                // Tồn kỳ này
                Cell tonKyNayCell = row.createCell(6);
                tonKyNayCell.setCellValue(((Number) variant.get("tonKynay")).doubleValue());
                tonKyNayCell.setCellStyle(dataStyle);
            }

            // Ensure borders for merged cells
            if (rowspan > 1) {
                for (int j = startRowSanPham; j < startRowSanPham + rowspan; j++) {
                    Row mergedRow = sheet.getRow(j);
                    if (mergedRow != null) {
                        Cell mergedTenCell = mergedRow.getCell(0);
                        if (mergedTenCell == null) mergedTenCell = mergedRow.createCell(0);
                        mergedTenCell.setCellStyle(textStyle);
                    }
                }
            }

            // Set vertical alignment for product name
            Cell sanPhamCell = sheet.getRow(startRowSanPham).getCell(0);
            if (sanPhamCell != null) {
                sanPhamCell.getCellStyle().setVerticalAlignment(VerticalAlignment.CENTER);
            }
        }

        // Write workbook to ByteArrayOutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
        throw new IOException("Failed to generate Excel file: " + e.getMessage(), e);
    }
}

}

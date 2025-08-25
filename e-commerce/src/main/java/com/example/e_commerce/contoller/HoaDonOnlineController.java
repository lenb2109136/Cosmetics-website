package com.example.e_commerce.contoller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.api.exceptions.GeneralError;
import com.example.e_commerce.DTO.request.CartItem;
import com.example.e_commerce.DTO.request.FilterOrderEmployee;
import com.example.e_commerce.DTO.request.GHNOrder;
import com.example.e_commerce.DTO.response.APIResponse;
import com.example.e_commerce.Exception.Custom.GeneralException;
import com.example.e_commerce.constants.Routes;
import com.example.e_commerce.model.HoaDon;
import com.example.e_commerce.model.TrangThai;
import com.example.e_commerce.model.TrangThaiHoaDon;
import com.example.e_commerce.service.HoaDonOnlineService;
import com.example.e_commerce.service.HoaDonSerVice;
import com.example.e_commerce.service.ServiceLocator;
import com.example.e_commerce.service.TrangThaiHoaDonService;
import com.example.e_commerce.service.TrangThaiService;

@RestController
@RequestMapping(Routes.API+"/hoadononline")
public class HoaDonOnlineController {
	@Autowired
	private HoaDonOnlineService hoaDonOnlineService;
	
	@Autowired
	private HoaDonSerVice hoaDonSerVice;
	
	@PostMapping("/getview")
	public ResponseEntity<APIResponse> viewHoaDonOnline(@RequestBody List<CartItem> danhSachBienThe){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.viewHoaDon(danhSachBienThe)),HttpStatus.OK);
	}

	@PostMapping("/getviewUpdate/{id}")
	public ResponseEntity<APIResponse> viewHoaDonOnlineForUpdate(
			@PathVariable("id") long id,
			@RequestBody List<CartItem> danhSachBienThe){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.getUpdateViewLast(danhSachBienThe,id)),HttpStatus.OK);
	}
	@PostMapping("/getview-update-employee/{id}")
	public ResponseEntity<APIResponse> viewHoaDonOnlineForUpdateEmployee(
			@PathVariable("id") long id,
			@RequestBody List<CartItem> danhSachBienThe){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.getUpdateViewLastEmployee(danhSachBienThe,id)),HttpStatus.OK);
	}
	
	@PutMapping("/changestatus/{id}")
	public ResponseEntity<APIResponse> viewHoaDonOnlineForUpdateEmployee(
			@PathVariable("id") long st,
			@RequestParam("st") int id){
		HoaDon hd= hoaDonSerVice.getById(id);
		TrangThaiService tt= ServiceLocator.getBean(TrangThaiService.class);
		TrangThaiHoaDonService ttt= ServiceLocator.getBean(TrangThaiHoaDonService.class);
	if(st==1) {
		TrangThai t= tt.getById(1);
		ttt.Save(hd, t, null);
	}
	if(st==12) {
		TrangThai t= tt.getById(12);
		ttt.Save(hd, t, null);
	}
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	
	@GetMapping("/hoantien-after/{id}")
	public ResponseEntity<APIResponse> hoanTienAfter(
			@PathVariable("id") long id){
		hoaDonOnlineService.hoanDon(id);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	@PostMapping("/update/{id}")
	public ResponseEntity<APIResponse> update(
			@PathVariable("id") long id,
			@RequestBody List<CartItem> danhSachBienThe){
		hoaDonOnlineService.UPdateOnLineCanChange(danhSachBienThe, id);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	@PostMapping("/getUpdate/{id}")
	public ResponseEntity<APIResponse> getUpDate(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.viewHoaDonAlReady(id)),HttpStatus.OK);
	}
	@PostMapping("/getUpdateE/{id}")
	public ResponseEntity<APIResponse> getUpDateEmployee(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.viewHoaDonAlReadyEmployee(id)),HttpStatus.OK);
	}
	
	@PostMapping("/getbystatuss")
    public ResponseEntity<APIResponse> getByStatus(
    		@RequestBody FilterOrderEmployee f
    		){
    	return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.filterHoaDonOnline
    			(f.getMaHoaDon(), f.getKhachHang(), f.getTrangThai(), f.getNgayLap(),
    					f.getSort(), f.getTrang())),HttpStatus.OK);
    }
	@PutMapping("/xacnhanhoanhang/{id}")
	public ResponseEntity<APIResponse> xacNhanHoangHang(@PathVariable("id") long id){
		hoaDonOnlineService.xacNhanHoanDon(id);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	
	
	@PostMapping("/createOrder")
	public ResponseEntity<APIResponse> createOrder(@RequestBody List<CartItem> danhSachBienThe){
		hoaDonOnlineService.DatHangOnline(danhSachBienThe);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	
	
	@PostMapping("/getbystatus/{st}")
	public ResponseEntity<APIResponse> getByStatus(
			@RequestParam("trang") int trang,
			@PathVariable("st") long status, 
			
			@RequestBody FilterOrderEmployee f){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonSerVice.getHoaDonOfTrangThai(status,f, trang)),HttpStatus.OK);
	}
	
	
	@GetMapping("gettrangthai/{id}")
	public ResponseEntity<APIResponse> getTrangThai(
			@PathVariable("id") long id,
			@RequestParam("lan") int lan
			) {
		boolean a=hoaDonOnlineService.getTrangThaiHoaDon(id,lan);
		return  new ResponseEntity<APIResponse>(new APIResponse("", a),HttpStatus.OK);
	}
	
	@GetMapping("/getinfothanhtoan/{id}")
	public ResponseEntity<APIResponse> getThongTinThanhToan(@PathVariable("id") long id){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.getThongTinThanhToan(id)),HttpStatus.OK);
	}
	
	@PostMapping("/duyet")
	public ResponseEntity<APIResponse> duyetHoaDon(@RequestBody List<Long> id){
		hoaDonOnlineService.duyetDonHang(id);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	@PutMapping("remove/{id}")
	public ResponseEntity<APIResponse> remove(@PathVariable("id") long id,@RequestParam("status") int status,@RequestParam("res") String resson){
		
		if(resson==null ||resson.length()==0) {
			throw new GeneralException("Vui lòng nêu lý do hoàn hàng", HttpStatus.BAD_REQUEST);
		}
		hoaDonOnlineService.removeHoaDonByKhachHang(id, status,resson);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
		
	}
	@PutMapping("remove-employee/{id}")
	public ResponseEntity<APIResponse> removeEmployee(@PathVariable("id") long id, @RequestParam(value = "yes", required = false, defaultValue = "false") boolean yes){
		Map<String, Object> api = hoaDonOnlineService.removeHoaDonByNhanVien(id, yes);

	    String message = (String) api.get("message");
	    HttpStatus status = (HttpStatus) api.get("status");
		return new ResponseEntity<APIResponse>(new APIResponse(message,null),status);
		
	}
	
	
	@PutMapping("hoanDonOnline/{id}")
	public ResponseEntity<APIResponse> xacNhanHoanDonOnline(@PathVariable("id") long id, @RequestParam(value = "yes", required = false, defaultValue = "false") boolean yes,
			@RequestParam(value = "lyDo", required = false, defaultValue = "") String lyDo,
			@RequestParam(value = "thanhCong", required = false, defaultValue = "false") boolean thanhCong
			){
		hoaDonOnlineService.XacNhanHoanDonByNhanVien(id, yes, lyDo, thanhCong);
		return new ResponseEntity<APIResponse>(new APIResponse("",null),HttpStatus.OK);
	}
	
	@PostMapping("/tinhPhiGiaoHang")
	public ResponseEntity<APIResponse> tinhPhiGiaoHang(@RequestBody List<CartItem> danhSachBienThe,
			@RequestParam(value = "addr", required = false) String address){
		return new ResponseEntity<APIResponse>(new APIResponse("",hoaDonOnlineService.tinhPhi(danhSachBienThe, address)),HttpStatus.OK);
	}
	
	
	@GetMapping("/get-print-ghn-order/{id}")
	public ResponseEntity<APIResponse> getPrintOrder(@PathVariable("id") long id){
		String s=hoaDonOnlineService.getPrintGHNOrder(id);
		System.out.println(s);
		return new ResponseEntity<APIResponse>(new APIResponse("",s),HttpStatus.OK);
	}
	@GetMapping("/callinfo")
    public ResponseEntity<String> getOrderInfo(@RequestParam String orderCode) {
        hoaDonOnlineService.CallBackDonHang(orderCode);
        return ResponseEntity.ok("Callback thành công đơn hàng");
    }
	
	@GetMapping("/chang-donghop")
    public ResponseEntity<Float> changeDongHop(@RequestParam Long idHoaDon,
    		@RequestParam int idDongHop,
    		@RequestParam int KhoiLuong) {
        return ResponseEntity.ok(hoaDonOnlineService.changeDongHop(KhoiLuong, idDongHop, idHoaDon));
    }
	@GetMapping("/reCreateGHN/{id}")
    public ResponseEntity<Float> changeDongHop(@PathVariable long id) {
		hoaDonOnlineService.reCreateGHNOrder(id);
        return ResponseEntity.ok(null);
    }
	
	@GetMapping("/hoanDonV2")
    public ResponseEntity<APIResponse> hoanDon(@RequestParam Long donIdToXacNhan,
    		@RequestParam int hoanChiTra,
    		@RequestParam int traTien) {
       String result= hoaDonOnlineService.hoanDonXacNhan(donIdToXacNhan,hoanChiTra,traTien);
       if(result==null) {
    	   return new ResponseEntity<APIResponse>( new APIResponse("success", null), HttpStatus.OK);
    	   
       }
       else {
    	   return new ResponseEntity<APIResponse>(new APIResponse(result,HttpStatus.INTERNAL_SERVER_ERROR),HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
	
	

}

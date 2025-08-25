package com.example.e_commerce.Validator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TimeValidator {
	 private static LocalDateTime truncateToMinute(LocalDateTime time) {
	        return time.truncatedTo(ChronoUnit.MINUTES);
	    }

	    private static LocalTime truncateToMinute(LocalTime time) {
	        return time.truncatedTo(ChronoUnit.MINUTES);
	    }

	    public static boolean isOverlap(LocalDateTime start1, LocalDateTime end1,
	                                    LocalDateTime start2, LocalDateTime end2) {
	        start1 = truncateToMinute(start1);
	        end1 = truncateToMinute(end1);
	        start2 = truncateToMinute(start2);
	        end2 = truncateToMinute(end2);
	        return !end1.isBefore(start2) && !end2.isBefore(start1);
	    }

	    public static boolean isOverlap(LocalTime start1, LocalTime end1,
	                                    LocalTime start2, LocalTime end2) {
	        start1 = truncateToMinute(start1);
	        end1 = truncateToMinute(end1);
	        start2 = truncateToMinute(start2);
	        end2 = truncateToMinute(end2);
	        return !end1.isBefore(start2) && !end2.isBefore(start1);
	    }
	    public static boolean isTrongKhungGio(LocalTime bd, LocalTime kt) {
	        LocalTime now = LocalTime.now();
	        if (!bd.isAfter(kt)) {
	            return !now.isBefore(bd) && !now.isAfter(kt);
	        }
	        return (!now.isBefore(bd) && now.isBefore(LocalTime.MAX)) || (!now.isAfter(kt) && now.isAfter(LocalTime.MIN));
	    }
	    
	    public static String getThoiGianTuNgay(LocalDateTime dateTime) {
	        LocalDateTime now = LocalDateTime.now();

	        if (dateTime.isAfter(now)) {
	            return "vừa xong";
	        }

	        long giay = ChronoUnit.SECONDS.between(dateTime, now);
	        if (giay < 60) return giay + " giây trước";

	        long phut = ChronoUnit.MINUTES.between(dateTime, now);
	        if (phut < 60) return phut + " phút trước";

	        long gio = ChronoUnit.HOURS.between(dateTime, now);
	        if (gio < 24) return gio + " giờ trước";

	        long ngay = ChronoUnit.DAYS.between(dateTime, now);
	        if (ngay < 30) return ngay + " ngày trước";

	        long thang = ChronoUnit.MONTHS.between(dateTime, now);
	        if (thang < 12) return thang + " tháng trước";

	        long nam = ChronoUnit.YEARS.between(dateTime, now);
	        return nam + " năm trước";
	    }


}

package th.co.infinitait.goodluck;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@RunWith(MockitoJUnitRunner.class)
class CabsatApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test() throws ParseException {
		Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));//GMT+7
		System.out.println(cal1.getTime());//11

		checkTime(LocalDateTime.now());

	}

	boolean checkTime(LocalDateTime localDateTime) throws ParseException {
		int h = localDateTime.getHour();
		System.out.println(h);
		if(h > 0 && h < 6){
			return false;
		}
		return true;
	}

	@Test
	void test_get_last_character() throws ParseException {
		String s = "VID202.10412175918.mp4";
		s = s.substring(s.lastIndexOf('.') + 1);
		System.out.println(s);
	}

	@Test
	void test_rex() throws Exception {
//		String text = "ห้อง211";
		String text = "ณฐพล  มีทรัยพ์ทอง ห้อง 304 ชั้น 3 431 ซอยชานเมือง  รัชดาซอย7แยก7  เขตดินแดง จ.กรุงเทพฯ";
//		Pattern p = Pattern.compile("[ห้อง]*");
		String regex = ".*ห้อง.*";
		boolean matches = Pattern.matches(regex, text);
		if(matches){
			System.out.println(text);
			String ss[] = text.split("ห้อง");
			if(ss.length > 0){
				System.out.println(ss[1]);
			}

		}else{
			System.out.println("ELSE");
		}
	}

	@Test
	void test_isNumeric() throws ParseException {
		if(isNumeric("22")){
			System.out.println("isNumeric");
		}else{
			System.out.println("notNumeric");
		}
		if(isNumeric("")){
			System.out.println("isNumeric");
		}else{
			System.out.println("notNumeric");
		}
		if(isNumeric("null")){
			System.out.println("isNumeric");
		}else{
			System.out.println("notNumeric");
		}
	}

	@Test
	void test_split() throws ParseException {
//		String s = "ณฐพล  มีทรัยพ์ทอง ห้อง 304 ชั้น 3 431 ซอยชานเมือง  รัชดาซอย7แยก7  เขตดินแดง จ.กรุงเทพฯ";
		String s = "ปฐมพงษ์  มณีสาย ตึก2 ชั้น2 ห้อง211 ซ.บ้านสวนกลาง ลาดกระบัง จ.กรุงเทพฯ";
//		String s = "นพคุณ  ห้อง 305 ชั้น 3431 ซอยชานเมือง  รัชดาซอย7แยก7  เขตดินแดง จ.กรุงเทพฯ";
		String[] addressArray = s.split("\\s+");
//		System.out.println(addressArray[0]);

		int i = 0;
		String firstName = addressArray[0];
		String lastName = "";
		String room = "0";
		String floor = "0";
		if(!"ห้อง".equals(addressArray[1]) && !"ชั้น".equals(addressArray[1])){
			lastName = addressArray[1];
		}

		for(String add:addressArray){
			System.out.println(add);
			String regexRoom = "ห้อง.*";
			boolean matchesRoom = Pattern.matches(regexRoom, add);
			if(matchesRoom){
				String[] rooms = add.split("ห้อง");
				if(rooms.length > 0){
					room = rooms[1];
				}else{
					if("ห้อง".equals(add)) {
						room = addressArray[i+1];
					}
				}
			}
			String regexFloor = "ชั้น.*";
			boolean matchesFloor = Pattern.matches(regexFloor, add);
			if(matchesFloor){
				String[] floors = add.split("ชั้น");
				if(floors.length > 0){
					floor = floors[1];
				}else{
					if("ชั้น".equals(add)) {
						floor = addressArray[i+1];
					}
				}
			}
			i++;
		}
		System.out.println("======================");
		System.out.println("ห้อง : "+room);
		System.out.println("ชั้น : "+floor);
		System.out.println("ชื่อ : "+firstName);
		System.out.println("นามสกุล : "+lastName);
	}

	@Test
	void test_divide() throws Exception {
		BigDecimal privateVatBigDecimal = new BigDecimal("3999");
		privateVatBigDecimal = privateVatBigDecimal.divide(new BigDecimal("30"), RoundingMode.CEILING);
		System.out.println("privateVatBigDecimal : "+privateVatBigDecimal);
		privateVatBigDecimal = new BigDecimal("3999");
		privateVatBigDecimal = privateVatBigDecimal.divide(new BigDecimal("30"), RoundingMode.HALF_UP);
		System.out.println("privateVatBigDecimal : "+privateVatBigDecimal);
	}


}

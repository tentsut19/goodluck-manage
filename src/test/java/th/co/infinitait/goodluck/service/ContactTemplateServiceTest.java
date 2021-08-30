package th.co.infinitait.goodluck.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import th.co.infinitait.goodluck.util.DateUtil;
import th.co.infinitait.goodluck.util.DoubleUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static th.co.infinitait.goodluck.util.DateUtil.YYYYMMDD_DATEH24_PATTERNBY_MM_SS_SSS;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class ContactTemplateServiceTest {

    @Test
    public void test0(){
        String title = "aaa : &lt;&lt;customerName&gt;&gt; bbb";
        title = title.replaceAll("&lt;&lt;customerName&gt;&gt;","test");
        assertEquals("aaa : test bbb", title);
    }

    @Test
    public void test00(){
        String title = "aaa : ${customerName} bbb";
        title = title.replaceAll("\\$\\{(.+?)\\}","test");
        assertEquals("aaa : test bbb", title);
    }

    @Test
    public void test000(){
        String title = "aaa : ${customerName} bbb : ${customerName}";
        title = title.replaceAll("\\$\\{customerName}","test");
        System.out.println(title);
        assertEquals("aaa : test bbb : test", title);
    }

    @Test
    public void test1(){
        DecimalFormat df2 = new DecimalFormat( "#,##0.00" );
        String result = df2.format(0);
        System.out.println(result);
        assertEquals("0.00", result);
        result = df2.format(100);
        System.out.println(result);
        assertEquals("100.00", result);
        result = df2.format(1000);
        System.out.println(result);
        assertEquals("1,000.00", result);
        result = df2.format(1000.25);
        System.out.println(result);
        assertEquals("1,000.25", result);
        result = df2.format(100000.75);
        System.out.println(result);
        assertEquals("100,000.75", result);
        result = df2.format(1000000);
        System.out.println(result);
        assertEquals("1,000,000.00", result);
    }

    @Test
    public void test2(){
        String name = String.format("STOCK-%04d", 0);
        assertEquals("STOCK-0000", name);
        name = String.format("STOCK-%04d", 2);
        assertEquals("STOCK-0002", name);
        name = String.format("STOCK-%04d", 23);
        assertEquals("STOCK-0023", name);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE ที่ dd เดือน MMMM พ.ศ. yyyy", new Locale("th", "TH"));

    }

    @Test
    public void test3(){
        SimpleDateFormat formatter1 = new SimpleDateFormat("EEEE ที่ dd เดือน MMMM พ.ศ. yyyy", new Locale("th", "TH"));
        System.out.println(formatter1.format(new Date()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE ที่ dd เดือน MMMM พ.ศ. yyyy", new Locale("th","TH"));
        String formattedDateThaiNow = LocalDateTime.now().format(formatter);
        String title = "aaa : &lt;&lt;dateThaiNow&gt;&gt; : bbb";
        title = title.replaceAll("&lt;&lt;dateThaiNow&gt;&gt;",formattedDateThaiNow);
        System.out.println(title);
    }

    @Test
    public void testBCryptPasswordEncoder(){
        String password = "1234";
        System.out.println(password);
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        System.out.println(encodedPassword);
    }

    @Test
    public void test_divide(){
        BigDecimal unitPerPrice = new BigDecimal("60");
        BigDecimal amount =  new BigDecimal("6000");
        unitPerPrice = amount.divide(unitPerPrice, 2,RoundingMode.CEILING);
        System.out.println(unitPerPrice);
    }

    @Test
    public void test_convert_number_to_2_decimal(){
        double angle = 20.3034;

        String angleFormated = DoubleUtil.toString2DecimalFormat(angle);
        assertEquals(angleFormated, "20.30");

        angle = 20;
        angleFormated = DoubleUtil.toString2DecimalFormat(angle);
        assertEquals(angleFormated, "20.00");

        angle = 20.435;
        angleFormated = DoubleUtil.toString2DecimalFormat(angle);
        assertEquals(angleFormated, "20.43");

        angle = 20.436;
        angleFormated = DoubleUtil.toString2DecimalFormat(angle);
        assertEquals(angleFormated, "20.44");

        angle = 0;
        angleFormated = DoubleUtil.toString2DecimalFormat(angle);
        assertEquals(angleFormated, "0.00");

        angle = 1234.25;
        angleFormated = DoubleUtil.toString2DecimalFormat(angle);
        assertEquals(angleFormated, "1,234.25");

        BigDecimal bd = new BigDecimal("2.22222");
        System.out.println(bd.setScale(2,BigDecimal.ROUND_UP));

        bd = new BigDecimal("2.22222");
        System.out.println(bd.setScale(2,BigDecimal.ROUND_HALF_UP));

        bd = new BigDecimal("2.22522");
        System.out.println(bd.setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void test_BigDecimal(){
        BigDecimal totalAmount = new BigDecimal("426.93");
        BigDecimal vatAmount = (totalAmount.multiply(new BigDecimal("7"))).divide(new BigDecimal("107"));
        System.out.println(vatAmount);
        BigDecimal baseAmount = totalAmount.subtract(vatAmount);
        System.out.println(baseAmount);

        BigDecimal a = new BigDecimal("1.6");
        BigDecimal b = new BigDecimal("9.2");
        BigDecimal c = a.divide(b, 2, RoundingMode.HALF_UP);
        System.out.println(c);
    }

    @Test
    public void test_decimalFormat(){
        float number = 12345678.1465f;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String numberAsString = decimalFormat.format(number);
        System.out.println(numberAsString);
    }

    @Test
    public void test_abc(){
        String text = "aaa bbb  ccc";
        text = text.replaceAll(" ","&nbsp;");
        System.out.println(text);
        assertEquals("aaa&nbsp;bbb&nbsp;&nbsp;ccc", text);
    }

    @Test
    public void test_abcd(){
        String colD = "เชียงราย";
        List<String> colDList = Arrays.asList("Easymanage", "Easynet", "Freelance", "เชียงราย");
        assertTrue(colDList.contains(colD));
    }

    @Test
    public void test_abcd1(){
        String customerName1 = "เพ็ญศรี";
        String customerName2 = "กรรธวัช แสงรุ่งอรุณ";
        String customerName3 = "สีวิดา อร่ามศรี (ตุ๊ก)";
        String[] customer1Names = customerName1.split(" ");
        String[] customer2Names = customerName2.split(" ");
        String[] customer3Names = customerName3.split(" ");
        System.out.println(customer1Names.length);
        System.out.println(customer2Names.length);
        System.out.println(customer3Names.length);

        System.out.println(customer1Names[0]);
        System.out.println(customer2Names[0]);
        System.out.println(customer3Names[0]);

        String s = DateUtil.convertToString(new Date(), YYYYMMDD_DATEH24_PATTERNBY_MM_SS_SSS);
        System.out.println(s);
        String s1 = DateUtil.convertToString(new Date(), YYYYMMDD_DATEH24_PATTERNBY_MM_SS_SSS);
        System.out.println(s1);
    }

    @Test
    public void test_abcd2(){
        String customerName1 = "1/2/3";
        String customerName2 = "1-2-3";
        String customerName3 = "1/2";
        String s = customerName1.replaceAll("[^0-9]+","");
        System.out.println(s);
        String s2 = customerName2.replaceAll("[^0-9]+","");
        System.out.println(s2);

        String[] ss = customerName3.split("\\.");
        String s3 = ss[0].replaceAll("[^0-9]+","");
        System.out.println(s3);
        if(StringUtils.isEmpty(s3)){
            System.out.println("0");
        }

        String customerName4 = "";
        String[] s4 = customerName4.split("/");
        s4 = s4[0].split("-");
        System.out.println(s4[0]);
    }

    @Test
    public void test_regex(){
        String text = "ทดสอบดูที่อยู่123!@\\๒\\๑\\๒\\๑acb\\ABC";
        System.out.println(text);
        String str = text.replaceAll("[-~ก-ูเ-๙-|\\\\]", "#");
        System.out.println(str);
    }

    @Test
    public void test_sss(){
        int dayOfMonth = 10;
        int monthOfYear = 2;
        int year = 2020;
        String date = String.format("%02d/%02d/%s",dayOfMonth,(monthOfYear + 1),year);
        System.out.println(date);
    }


}

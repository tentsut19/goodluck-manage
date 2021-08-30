package th.co.infinitait.goodluck.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtil {

    /** The Constant DEFAULT_TIMEZOME. */
    public static final TimeZone DEFAULT_TIMEZOME = TimeZone.getDefault();

    /** The Constant DEFAULT_LOCALE. */
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();

    /** The Constant THAI_LOCALE. */
    public static final Locale ENG_LOCALE = new Locale("en", "US");
    public static final Locale THAI_LOCALE = new Locale("th", "TH");

    public static final String STANDARD_DATE_PATTERN = "dd/MM/yyyy";
    public static final String STANDARD_TIME_PATTERN = "HH:mm:ss";

    public static final String STANDARD_DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    public static final String STANDARD_DATE_TIME_PATTERN_NO_SECONDS = "dd/MM/yyyy HH:mm";
    public static final String STANDARD_DATE_TIME_PATTERN_DASH = "yyyy-MM-dd HH:mm:ss";
    public static final String STANDARD_DATE_TIME_PATTERN_RQ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String STANDARD_DATE_TIME_PATTERN_RQ1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_CONVERT_DATE_PATTERN = "ddMMyyyy";

    public static final String YYYYMMDD_DATE_PATTERN = "yyyyMMdd";
    public static final String YYYYMMDD_DATEH24_PATTERN = "yyyyMMddHH:mm:ss";
    public static final String YYYYMMDD_DATEH24_PATTERNBY = "yyyyMMddHHmm";
    public static final String YYYYMMDD_DATEH24_PATTERNBY_MM = "yyyyMMddHHmm";
    public static final String YYYYMMDD_DATEH24_PATTERNBY_MM_SS = "yyyyMMddHHmmss";
    public static final String YYYYMMDD_DATEH24_PATTERNBY_MM_SS_SSS = "yyyyMMddHHmmssSSS";

    public static final String YYMMDD_DATE_PATTERN 	= "yyMMdd";
    public static final String HHMMSS_TIME_PATTERN = "HHmmss";

    public static final String YYMM_DATE_PATTERN = "yyMM";

    public static final String DD_MMM_YY_DATE_PATTERN = "dd MMM yy";

    public static final String YYYY_MM_DD_DATE_PATTERN = "yyyy-MM-dd";

    public static final String YYYYMM_DATE_PATTERN = "yyyyMM";

    //January 1, 1970, 00:00:00 GMT
    public static final Date FIRST_DATE = new Date(0);

    public static final String DATE_FORMAT_ENG = "dd MMM yyyy";
    public static final String DATE_FORMAT_THAI = "dd MM yyyy";
    public static final String DATE_FORMAT_THAI1 = "MMMM yyyy";
    public static final String DATE_FORMAT_THAI2 = "dd MMMM yyyy";
    public static final String DATE_FORMAT_THAI3 = "dd.MM.yyyy";
    public static final String DATE_FORMAT_THAI4 = "dd-MM-yyyy";

    /**Convert January 1, 1970, 00:00:00 GMT to null
     *
     * @param date
     * @return
     */
    public static Date trimFirstDateToNull(Date date){
        return FIRST_DATE.equals(date) ? null : date;
    }


    public static Date getDate(Calendar calendar){
        return calendar == null ? null : calendar.getTime();
    }

    public static String getDisplay(Date date, String pattern, Locale locale) {
        String display = "";
        if(date != null) {
            Calendar calendar = Calendar.getInstance(locale);
            calendar.setTime(date);
            display = FastDateFormat.getInstance(pattern, locale).format(calendar);
        }
        return display;
    }

    public static boolean isAfter(Date date1, Date date2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        removeCalendarTime(cal1);
        removeCalendarTime(cal2);
        return cal1.after(cal2);
    }

    public static boolean isBefore(Date date1, Date date2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        removeCalendarTime(cal1);
        removeCalendarTime(cal2);
        return cal1.before(cal2);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        return isSameDay(date1 == null ? null: getCalendar(date1), date2 == null ? null: getCalendar(date2));
    }

    public static boolean isBefore(Calendar date1, Calendar date2){
        Calendar cal1 = copyCalendar(date1);
        Calendar cal2 = copyCalendar(date2);
        removeCalendarTime(cal1);
        removeCalendarTime(cal2);
        return cal1.before(cal2);
    }

    public static boolean isAfter(Calendar date1, Calendar date2){
        Calendar cal1 = copyCalendar(date1);
        Calendar cal2 = copyCalendar(date2);
        removeCalendarTime(cal1);
        removeCalendarTime(cal2);
        return cal1.after(cal2);
    }

    public static void removeCalendarTime(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        calendar.clear(Calendar.MILLISECOND);
    }

    public static Date getRemovedDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = getCalendar(date);
        removeCalendarTime(cal);
        return cal.getTime();
    }

    public static Calendar copyCalendar(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        Calendar result = Calendar.getInstance(calendar.getTimeZone());
        result.setTimeInMillis(calendar.getTimeInMillis());
        return result;
    }

    public static int getDateDiff(Calendar date1, Calendar date2) {
        Calendar compareDate1 = copyCalendar(date1);
        Calendar compareDate2 = copyCalendar(date2);
        removeCalendarTime(compareDate1);
        removeCalendarTime(compareDate2);
        //86400000 = 1000 * 60 * 60 * 24 come from millis to day
        int dateDiff = (int)((compareDate2.getTimeInMillis() - compareDate1.getTimeInMillis()) / 86400000);
        if (dateDiff < 0) {
            dateDiff = -1 * dateDiff;
        }
        return dateDiff;
    }

    public static long getDateDiff(Date currentDate, Date nextCollectionDate) {
        //diff in msec
        long diff = currentDate.getTime() - nextCollectionDate.getTime();

        //diff in days
        return diff / (24 * 60 * 60 * 1000);
    }

    public static boolean isSameDay(Calendar date1, Calendar date2) {
        boolean isSameDay = false;
        if (date1 != null && date2 != null) {
            isSameDay = (date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)) && (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR));
        }
        return isSameDay;
    }

    /**
     * Gets the calendar.
     *
     * @param dateStr the date str
     * @param formatter the formatter
     * @return the calendar
     */
    public static Calendar getCalendar(String dateStr, FastDateFormat formatter) {
        return getCalendar(dateStr, formatter, DEFAULT_TIMEZOME, DEFAULT_LOCALE);
    }

    public static Calendar getCalendar(Date date){
        Calendar calendar = getCurrentCalendar();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Gets the calendar.
     *
     * @param dateStr the date str
     * @param formatter the formatter
     * @param zone the zone
     * @param locale the locale
     * @return the calendar
     */
    public static Calendar getCalendar(String dateStr, FastDateFormat formatter, TimeZone zone, Locale locale) {
        Date date;
        Calendar calendar;
        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }
        try {
            SimpleDateFormat tmp = new SimpleDateFormat(formatter.getPattern(), formatter.getLocale());
            date = tmp.parse(dateStr);
            calendar = getCurrentCalendar(zone, locale);
            calendar.setTime(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return calendar;
    }

    /**
     * Gets the current calendar.
     *
     * @return the current calendar
     */
    public static Calendar getCurrentCalendar() {
        return getCurrentCalendar(DEFAULT_TIMEZOME, DEFAULT_LOCALE);
    }

    /**
     * Gets the current calendar.
     *
     * @param zone the zone
     * @param aLocale the a locale
     * @return the current calendar
     */
    public static Calendar getCurrentCalendar(TimeZone zone, Locale aLocale) {
        Calendar calendar;
        if (zone != null && aLocale != null) {
            calendar = Calendar.getInstance(zone, aLocale);
        } else if (zone == null && aLocale != null) {
            calendar = Calendar.getInstance(aLocale);
        } else if (zone != null && aLocale == null) {
            calendar = Calendar.getInstance(zone);
        } else {
            calendar = Calendar.getInstance();
        }
        return calendar;
    }

    /**
     * Gets the {@link Calendar} set to the last minute of yesterday.
     *
     * @return
     */
    public static Calendar getYesterday() {
        Calendar calendar = getCurrentCalendar();
        calendar.add(Calendar.DATE, -1);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 59, 59);
        return calendar;

    }

    /**
     * Convert Date to String with default pattern
     * @param date
     * @return
     */
    public static String convertToString(Date date) {
        return date != null ? convertToString(date, DEFAULT_CONVERT_DATE_PATTERN) : null;
    }

    public static String convertToString(Date date, Locale locale) {
        return date != null ? convertToString(date, DEFAULT_CONVERT_DATE_PATTERN, locale) : null;
    }

    /**
     * Convert Date To String with pattern
     * @param date
     * @param pattern
     * @return
     */
    public static String convertToString(Date date, String pattern) {
        return convertToString(date, pattern, null);
    }

    public static String convertToString(Date date, String pattern, Locale locale) {
        String convertResult = null;
        if(date != null && StringUtils.isNotBlank(pattern)) {
            Calendar calendar = locale == null ? Calendar.getInstance() : Calendar.getInstance(locale);
            calendar.setTime(date);
            convertResult = convertToString(calendar, pattern);
        }
        return convertResult;
    }

    /**
     * Convert Calendar to String with default pattern
     * @param calendar
     * @return
     */
    public static String convertToString(Calendar calendar) {
        return calendar != null ? convertToString(calendar, DEFAULT_CONVERT_DATE_PATTERN) : null;
    }

    /**
     * Convert Calendar to String with pattern
     * @param calendar
     * @param pattern
     * @return
     */
    public static String convertToString(Calendar calendar, String pattern) {
        return calendar != null && StringUtils.isNotBlank(pattern) ? FastDateFormat.getInstance(pattern, DEFAULT_LOCALE).format(calendar) : null;
    }


    public static Date convertToDate(Calendar calendar) {
        return calendar != null ? getDate(calendar) : null;
    }

    /**
     * Convert String to Date with default pattern
     * @param dateStr
     * @return
     */
    public static Date convertToDate(String dateStr) {
        return StringUtils.isNotBlank(dateStr) ? convertToDate(dateStr, DEFAULT_CONVERT_DATE_PATTERN) : null;
    }

    /**
     * Convert Number to Date with default pattern
     * @param dateNumber
     * @return
     */
    public static Date convertToDate(Number dateNumber) {
        return dateNumber != null ? convertToDate(String.valueOf(dateNumber), DEFAULT_CONVERT_DATE_PATTERN) : null;
    }

    /**
     * Convert String to Date with pattern
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date convertToDate(String dateStr, String pattern) {
        return StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(pattern) ? convertToCalendar(dateStr, pattern).getTime() : null;
    }

    /**
     * Convert String to Date with pattern and locale
     * @param dateStr
     * @param pattern
     * @param locale
     * @return
     */
    public static Date convertToDate(String dateStr, String pattern, Locale locale) {
        return StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(pattern) ? convertToCalendar(dateStr, pattern, locale).getTime() : null;
    }

    /**
     * Convert Date to Calendar
     * @param date
     * @return
     */
    public static Calendar convertToCalendar(Date date) {
        Calendar convertCalendar = null;
        if(date != null){
            convertCalendar = Calendar.getInstance(DEFAULT_TIMEZOME, DEFAULT_LOCALE);
            convertCalendar.setTime(date);
        }
        return convertCalendar;
    }

    /**
     * Convert String to Calendar with default pattern
     * @param dateStr
     * @return
     */
    public static Calendar convertToCalendar(String dateStr) {
        return StringUtils.isNotBlank(dateStr) ? convertToCalendar(dateStr, DEFAULT_CONVERT_DATE_PATTERN) : null;
    }

    /**
     * Convert Number to String with default pattern
     * @param dateNumber
     * @return
     */
    public static Calendar convertToCalendar(Number dateNumber) {
        return dateNumber != null ? convertToCalendar(String.valueOf(dateNumber), DEFAULT_CONVERT_DATE_PATTERN) : null;
    }

    /**
     * Convert String to Calendar with pattern
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Calendar convertToCalendar(String dateStr, String pattern) {
        return StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(pattern) ? getCalendar(dateStr, FastDateFormat.getInstance(pattern, DEFAULT_LOCALE)) : null;
    }

    /**
     * Convert String to Calendar with pattern and locale
     * @param dateStr
     * @param pattern
     * @param locale
     * @return
     */
    public static Calendar convertToCalendar(String dateStr, String pattern, Locale locale) {
        return StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(pattern) ? getCalendar(dateStr, FastDateFormat.getInstance(pattern, locale)) : null;
    }

    /**
     * Convert String Date To String Date with pattern
     *
     * @param dateStr
     * @param srcPattern
     * @param outPattern
     * @return
     */
    public static String convertToString(String dateStr, String srcPattern, String outPattern) {
        return convertToString(dateStr, srcPattern, outPattern, null);
    }

    public static String convertToString(String dateStr, String srcPattern, String outPattern, Locale locale) {
        String convertResult = null;
        if(StringUtils.isNotBlank(dateStr) && StringUtils.isNotBlank(srcPattern)) {
            convertResult = convertToString(convertToDate(dateStr, srcPattern), outPattern, locale);
        }
        return convertResult;
    }

    /**
     * Remove Special Character By Regular Expression<br>
     * <b>Example.<br></b>
     * Source: <b>99:99:99</b><br>
     * Replace with: <b>"" (Blank)</b><br>
     * RegexSpecialChar: <b>\\:</b><br>
     * To Result <b>999999</b>
     *
     * @param sourceStr
     * @param replaceStr
     * @param regexSpecialChar
     * @return
     */
    public static String removeSpecialCharByRegex(String sourceStr, String replaceStr, String regexSpecialChar) {
        String removedResult = null;
        if (StringUtils.isNotBlank(sourceStr) && (null != replaceStr) && StringUtils.isNotBlank(regexSpecialChar)) {
            StringBuilder sourceBuilder = new StringBuilder(sourceStr);
            Pattern p = Pattern.compile(regexSpecialChar);
            Matcher m = p.matcher(sourceBuilder);
            removedResult = m.replaceAll(replaceStr);
        }
        return removedResult;
    }

    public static int getYearDiff(Calendar date1, Calendar date2) {
        Calendar compareDate1 = copyCalendar(date1);
        Calendar compareDate2 = copyCalendar(date2);
        int yearDiff = compareDate1.get(Calendar.YEAR) - compareDate2.get(Calendar.YEAR);
        int month1 = compareDate1.get(Calendar.MONTH);
        int month2 = compareDate2.get(Calendar.MONTH);
        if (yearDiff > 0) {
            if (month1 < month2 || (month1 == month2 && compareDate1.get(Calendar.DATE) < compareDate2.get(Calendar.DATE))) {
                yearDiff -= 1;
            }
        } else if (yearDiff < 0) {
            if (month1 > month2 || (month1 == month2 && compareDate1.get(Calendar.DATE) > compareDate2.get(Calendar.DATE))) {
                yearDiff += 1;
            }
            yearDiff *= -1;
        }
        return yearDiff;
    }

    public static boolean isMoreThanLimitYear(Date startDate, int limitYear) {
        Calendar startDateIncludeLimitYear = getCalendar(startDate);
        startDateIncludeLimitYear.add(Calendar.YEAR, limitYear);
        Calendar currentDate = getCurrentCalendar();
        removeCalendarTime(startDateIncludeLimitYear);
        removeCalendarTime(currentDate);
        return currentDate.after(startDateIncludeLimitYear);
    }

    public static boolean isLessThanLimitYear(Date startDate, int limitYear) {
        Calendar startDateIncludeLimitYear = getCalendar(startDate);
        startDateIncludeLimitYear.add(Calendar.YEAR, limitYear);
        Calendar currentDate = getCurrentCalendar();
        removeCalendarTime(startDateIncludeLimitYear);
        removeCalendarTime(currentDate);
        return currentDate.before(startDateIncludeLimitYear);
    }

    public static Date getBirthDateFromAge(Integer age) {
        if (age == null) {
            return null;
        }
        Calendar calendar = getCurrentCalendar();
        removeCalendarTime(calendar);
        calendar.set(calendar.get(Calendar.YEAR) - age, 0, 1);
        return calendar.getTime();
    }

    public static String getFormatedDateDisplay(Date date, String lang) {
        String display = "";
        if(date != null) {
            Calendar calendar = Calendar.getInstance(("th".equals(lang))?THAI_LOCALE: ENG_LOCALE);
            calendar.setTime(date);
            display = FastDateFormat.getInstance(("th".equals(lang))?DATE_FORMAT_THAI: DATE_FORMAT_ENG, ("th".equals(lang))?THAI_LOCALE: ENG_LOCALE).format(calendar);
        }
        return display;
    }

    public static String getLastDateOfMonth(int year, int month, String pattern, Locale locale) throws Exception {
        Calendar calendar = new GregorianCalendar(year, month, 0);
        String lastDayOfMonth = FastDateFormat.getInstance(pattern, locale).format(calendar);
        return lastDayOfMonth;
    }

    /* 2010-06-22T00:00:00 */
    public static Calendar convertDateToCalendar(String dateTime) throws Exception {
        if (StringUtils.isEmpty(dateTime)) {
            return null;
        }
        String[] dateTimeStr = StringUtils.split(dateTime, "T");
        String[] timeStr = StringUtils.split(dateTimeStr[1], "\\:");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = format.parse(dateTimeStr[0]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), Integer.parseInt(timeStr[0]), Integer.parseInt(timeStr[1]), Integer.parseInt(timeStr[2]));
        return calendar;
    }

    public static Date beforeDate() throws ParseException {
        SimpleDateFormat dateFM = new SimpleDateFormat(STANDARD_DATE_TIME_PATTERN_DASH);
        Date now = new Date();
        String date = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        date = dateFM.format(calendar.getTime());
        now = dateFM.parse(date);
        return now;
    }

    public static Date afterDate() throws ParseException {
        SimpleDateFormat dateFM = new SimpleDateFormat(STANDARD_DATE_TIME_PATTERN_DASH);
        Date now = new Date();
        String date = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        date = dateFM.format(calendar.getTime());
        now = dateFM.parse(date);
        return now;
    }
    public static boolean chkBranchFormat(String numStr) {
        boolean result =false;
        if(!StringUtils.equals("", numStr) && numStr != null) {
            String numFind = "0";
            Pattern word = Pattern.compile(numFind);
            Matcher match = word.matcher(numStr);

            int coutIndex = 0;
            while (match.find()) {
                ++coutIndex;
            }
            if(coutIndex == numStr.length()) {
                result = true;
            }
        }
        return result;
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date setMonth(Date date,int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, amount);
        return c.getTime();
    }

    public static Date setTime(Date date,Integer hours,Integer minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY,hours);
        c.set(Calendar.MINUTE,minute);
        return c.getTime();
    }

    public static Date setDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Boolean compareDate(Date date) {
        if(date == null){
            return false;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            date = calendar.getTime();

            Calendar calendarNow = Calendar.getInstance();
            calendarNow.set(Calendar.HOUR, 0);
            calendarNow.set(Calendar.MINUTE, 0);
            calendarNow.set(Calendar.SECOND, 0);
            Date dateNow = calendarNow.getTime();
            return dateNow.equals(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

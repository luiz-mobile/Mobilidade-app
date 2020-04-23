package estapar.mobilidade.mobilidadepassageiro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Geison on 19/08/2015.
 */
public final class Dates {

    public static final String FORMAT_PT_BR_DATE_HOUR = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_PT_BR_DATE = "dd/MM/yyyy";
    public static final String FORMAT_PT_BR_HOUR = "HH:mm";

    public static String format(Date date, String format) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static String format(String stringDate, String format) {

        Date date = parse(stringDate, format);

        return format(date, format);
    }

    public static Date parse(String date, String format) {
        if (date == null) {
            return null;
        }

        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Produz uma data em string formatada conforme o formato indicado
     * @param dia
     * @param mes
     * @param ano
     * @param format formato usado na construção da data
     * @return data formatada
     */
    public static String format(int dia, int mes, int ano, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dia);
        calendar.set(Calendar.MONTH, mes);
        calendar.set(Calendar.YEAR, ano);
        return new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
    }

    public static Date parse(int dia, int mes, int ano) {
        Calendar c = Calendar.getInstance();
        c.set(ano, mes, dia);
        return c.getTime();
    }

    public static Date parse(int hora, int minutos) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minutos);
        return c.getTime();
    }

    public static String getCurrentDate(String format) {
        Calendar c = Calendar.getInstance();
        return format(c.getTime(), format);
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        return format(c.getTime(), FORMAT_PT_BR_DATE);
    }
}

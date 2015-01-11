/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    private static final SimpleDateFormat SimpleTimeFormat
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------


    // --------------------- Methods public ----------------------

    public static String formatTimeSimple(long time){
        if(time < 1){
            return "";
        }

        Date data = new Date(time);
        return SimpleTimeFormat.format(data);
    }

    // --------------------- Methods private ---------------------


    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------


    // --------------------- logical fragments -------------------

}
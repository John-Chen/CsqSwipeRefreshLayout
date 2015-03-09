/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-12-1
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    private static Handler handler = new Handler(Looper.getMainLooper());

    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------


    // --------------------- Methods public ----------------------

    public static void showInfo(final Context context, final String msg, final boolean isLong)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(context, msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showInfo(final Context context, final int resId, final boolean isLong)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(context, resId, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------------------- Methods private ---------------------


    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------


    // --------------------- logical fragments -------------------

}
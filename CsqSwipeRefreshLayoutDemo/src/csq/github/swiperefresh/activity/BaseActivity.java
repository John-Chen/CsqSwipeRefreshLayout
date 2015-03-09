/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-12-1
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.activity;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    protected Activity context;
    protected int resumedNum = 0;


    // ----------------------- Constructors ----------------------


    // -------- Methods for/from SuperClass/Interfaces -----------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        resumedNum++;
    }


    // --------------------- Methods public ----------------------

    public boolean isHaveResumed(){
        return resumedNum > 0;
    }

    public boolean isFirstResume(){
        return resumedNum == 1;
    }

    // --------------------- Methods private ---------------------


    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------


    // --------------------- logical fragments -------------------

}
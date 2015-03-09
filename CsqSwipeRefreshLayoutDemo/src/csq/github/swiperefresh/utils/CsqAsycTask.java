/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.lang.ref.WeakReference;

public abstract class CsqAsycTask<T> extends AsyncTask <Void, Integer, T> {


    // ------------------------ Constants ------------------------

    private WeakReference<Object> context;

    // ------------------------- Fields --------------------------


    // ----------------------- Constructors ----------------------

    public CsqAsycTask(Context context){
        this.context = new WeakReference<Object>(context);
    }

    // -------- Methods for/from SuperClass/Interfaces -----------

    @Override
    protected T doInBackground(Void... voids) {
        T result = null;
        try{
            result = run();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);

        if(canContine()){
            onResult(t);
        }
    }

    // --------------------- Methods public ----------------------


    // --------------------- Methods private ---------------------

    private boolean canContine(){
        Object h = context.get();
        if(h != null){
            if(h instanceof Activity){
                return h != null && ((Activity)h).isFinishing() == false;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if(h instanceof android.support.v4.app.Fragment){
                    return h != null && ((android.support.v4.app.Fragment)h).isRemoving() == false;
                }
            }
        }

        return true;
    }

    protected abstract T run() throws Exception;
    protected abstract void onResult(T result);

    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------


    // --------------------- logical fragments -------------------

}
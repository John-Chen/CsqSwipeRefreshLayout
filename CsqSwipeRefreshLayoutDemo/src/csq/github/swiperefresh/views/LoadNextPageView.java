/**
 * description : 列表底部加载下一页界面
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 14-11-23
 * Created with IntelliJ IDEA
 */

package csq.github.swiperefresh.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import csq.github.swiperefresh.R;
import csq.github.swiperefresh.models.PageLoadStatus;

public class LoadNextPageView extends RelativeLayout {

    // ------------------------ Constants ------------------------


    // ------------------------- Fields --------------------------

    private ProgressBar pbLoading;
    private Button btnLoadNext;
    private TextView tvFinished;

    private PageLoadStatus status;

    private LoadNextListener loadNextListener;

    // ----------------------- Constructors ----------------------

    public LoadNextPageView(Context context, LoadNextListener loadNextListener) {
        super(context);
        initView(context);
        this.loadNextListener = loadNextListener;
    }

    // -------- Methods for/from SuperClass/Interfaces -----------


    // --------------------- Methods public ----------------------

    public void setStatus(PageLoadStatus status) {
        this.status = status;

        if(status == PageLoadStatus.Loading){
            pbLoading.setVisibility(View.VISIBLE);
            btnLoadNext.setVisibility(View.INVISIBLE);
            tvFinished.setVisibility(View.INVISIBLE);

        }else if(status == PageLoadStatus.Finished){
            pbLoading.setVisibility(View.INVISIBLE);
            btnLoadNext.setVisibility(View.VISIBLE);
            tvFinished.setVisibility(View.INVISIBLE);

        }else{
            pbLoading.setVisibility(View.INVISIBLE);
            btnLoadNext.setVisibility(View.INVISIBLE);
            tvFinished.setVisibility(View.VISIBLE);
        }
    }

    // --------------------- Methods private ---------------------

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_load_next_page, this, true);

        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        btnLoadNext = (Button) findViewById(R.id.btnLoadNext);
        tvFinished = (TextView) findViewById(R.id.tvFinished);

        btnLoadNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loadNextListener != null){
                    loadNextListener.loadNext();
                }
            }
        });

        setStatus(PageLoadStatus.Loading);
    }

    // --------------------- Getter & Setter ---------------------


    // --------------- Inner and Anonymous Classes ---------------

    public static interface LoadNextListener{
        public void loadNext();
    }

    // --------------------- logical fragments -------------------

}
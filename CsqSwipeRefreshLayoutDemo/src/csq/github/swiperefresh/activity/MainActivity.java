package csq.github.swiperefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import csq.github.swiperefresh.R;
import csq.github.swiperefresh.activity.ListViewTestActivity;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnListView:
                ListViewTestActivity.launchActivity(this);
                break;

            default:

                break;
        }
    }


}

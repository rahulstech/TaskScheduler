package rahulstech.android.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import rahulstech.android.ui.R;
import rahulstech.android.ui.fragments.ViewTaskFragment;

@SuppressWarnings("unused")
public class ViewTask extends AppCompatActivity {

    private static final String TAG = "ViewTask";

    public static final String EXTRA_TASK_ID = "extra_task_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        ViewTaskFragment fragment = new ViewTaskFragment();
        fragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
    }
}
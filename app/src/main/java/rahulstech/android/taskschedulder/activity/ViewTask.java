package rahulstech.android.taskschedulder.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import rahulstech.android.taskschedulder.R;

@SuppressWarnings("unused")
public class ViewTask extends AppCompatActivity {

    private static final String TAG = "ViewTask";

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        FragmentContainerView mContainer = findViewById(R.id.container);
        NavHostFragment mFragment = mContainer.getFragment();
        mNavController = mFragment.getNavController();
        mNavController.setGraph(R.navigation.task_view_navigation,getIntent().getExtras());
    }

    @Override
    public void onBackPressed() {
        if (!mNavController.popBackStack()) {
            super.onBackPressed();
        }
    }
}
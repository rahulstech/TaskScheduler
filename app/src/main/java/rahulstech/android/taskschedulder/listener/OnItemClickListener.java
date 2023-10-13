package rahulstech.android.taskschedulder.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface OnItemClickListener {

    void onItemClick(@NonNull RecyclerView rv, int position);
}

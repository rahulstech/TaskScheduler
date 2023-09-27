package rahulstech.android.ui.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface OnItemClickListener {

    void onItemClick(@NonNull RecyclerView rv, int position);
}

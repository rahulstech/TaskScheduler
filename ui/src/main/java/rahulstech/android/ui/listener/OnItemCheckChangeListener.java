package rahulstech.android.ui.listener;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public interface OnItemCheckChangeListener {

    void onCheckChange(@NonNull RecyclerView.Adapter<?> adapter, int position, boolean checked, @NonNull View which);
}

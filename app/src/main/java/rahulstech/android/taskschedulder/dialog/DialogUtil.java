package rahulstech.android.taskschedulder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class DialogUtil {

    @NonNull
    public static Dialog confirmDialog(@NonNull Context context, @NonNull CharSequence message,
                                       @NonNull CharSequence btnYes, @Nullable DialogInterface.OnClickListener yesListener,
                                       @NonNull CharSequence btnNo, @Nullable DialogInterface.OnClickListener noListener) {
        return new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(btnYes,yesListener)
                .setNegativeButton(btnNo,noListener)
                .create();
    }

    public static Dialog showSingleChoiceDialog(@NonNull Context context, @Nullable CharSequence title,
                                                @NonNull CharSequence[] items,
                                                @Nullable DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setItems(items,listener);
        return builder.create();
    }
}

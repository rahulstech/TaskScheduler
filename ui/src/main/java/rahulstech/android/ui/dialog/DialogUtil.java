package rahulstech.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class DialogUtil {

    public static Dialog confirmDialog(@NonNull Context context, @NonNull CharSequence message,
                                       @NonNull CharSequence btnYes, @Nullable DialogInterface.OnClickListener yesListener,
                                       @NonNull CharSequence btnNo, @Nullable DialogInterface.OnClickListener noListener) {
        return new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(btnYes,yesListener)
                .setNegativeButton(btnNo,noListener)
                .create();
    }
}

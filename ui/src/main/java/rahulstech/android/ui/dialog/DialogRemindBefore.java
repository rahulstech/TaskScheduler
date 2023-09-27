package rahulstech.android.ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import rahulstech.android.database.datatype.DurationUnit;
import rahulstech.android.ui.R;

public class DialogRemindBefore extends AlertDialog {

    RadioGroup mRemindOptions;
    RadioButton btnRemindOnTime;
    RadioButton btnRemindBefore;
    EditText iRemindBefore;
    Spinner iBeforeUnit;

    boolean mIsRemindBefore;
    int mRemindBefore;
    DurationUnit mBeforeUnit;

    OnClickSaveListener mSaveListener;

    public DialogRemindBefore(@NonNull Context context) {
        super(context);
        View view = getLayoutInflater().inflate(R.layout.dialog_task_time_reminder,null);
        setView(view);
        mRemindOptions = view.findViewById(R.id.reminder_options);
        btnRemindBefore = view.findViewById(R.id.btn_remind_before);
        btnRemindOnTime = view.findViewById(R.id.btn_remind_on_time);
        iRemindBefore = view.findViewById(R.id.duration);
        iBeforeUnit = view.findViewById(R.id.duration_unit);
        mRemindOptions.setOnCheckedChangeListener((group,id)-> setDurationInputEnabled(id == R.id.btn_remind_before));
        setTitle(R.string.label_remind_before);
        setButton(AlertDialog.BUTTON_POSITIVE, getContext().getText(R.string.label_save),(di,which)->onClickSave(true));
        setButton(AlertDialog.BUTTON_NEGATIVE,getContext().getText(R.string.label_save_without_reminder),(di,which)-> onClickSave(false));
        setButton(AlertDialog.BUTTON_NEUTRAL,getContext().getText(R.string.label_cancel), (OnClickListener) null);
    }

    public void setOnClickSaveListener(OnClickSaveListener listener) {
        mSaveListener = listener;
    }

    public DurationUnit getSelectedDurationUnit() {
        switch (iBeforeUnit.getSelectedItemPosition()) {
            case 1: return DurationUnit.HOUR;
            case 2: return DurationUnit.DAY;
            default: return DurationUnit.MINUTE;
        }
    }

    public int getSelectionForDurationUnit(DurationUnit unit) {
        if (unit == DurationUnit.HOUR) {
            return 1;
        }
        else if (unit == DurationUnit.DAY) {
            return 2;
        }
        else {
            return 0;
        }
    }

    public void setIsRemindBefore(boolean remindBefore ){
        mIsRemindBefore = remindBefore;
        update();
    }

    public void setRemindBefore(int before, @NonNull DurationUnit unit) {
        mRemindBefore = before;
        mBeforeUnit = unit;
        update();
    }

    private void update() {
        if (mIsRemindBefore) {
            mRemindOptions.check(R.id.btn_remind_before);
            iRemindBefore.setText(String.valueOf(mRemindBefore));
            iBeforeUnit.setSelection(getSelectionForDurationUnit(mBeforeUnit));
        }
        else {
            mRemindOptions.check(R.id.btn_remind_on_time);
            iRemindBefore.setText(String.valueOf(mRemindBefore));
            iBeforeUnit.setSelection(getSelectionForDurationUnit(DurationUnit.MINUTE));
        }
    }

    private void setDurationInputEnabled(boolean enable) {
        if (enable) {
            iRemindBefore.setVisibility(View.VISIBLE);
            iBeforeUnit.setVisibility(View.VISIBLE);
            iBeforeUnit.setSelection(getSelectionForDurationUnit(mBeforeUnit));
        }
        else {
            iRemindBefore.setVisibility(View.GONE);
            iBeforeUnit.setVisibility(View.GONE);
        }
    }

    private void onClickSave(boolean addReminder) {
        if (null == mSaveListener) return;
        if (!addReminder) {
            mSaveListener.onClickSave(false,0,null);
        }
        else {
            int before;
            DurationUnit unit;
            if (mRemindOptions.getCheckedRadioButtonId() == R.id.btn_remind_on_time) {
                before = 0;
                unit = DurationUnit.MINUTE;
            }
            else {
                CharSequence txtBefore = iRemindBefore.getText();
                if (TextUtils.isEmpty(txtBefore)) {
                    Toast.makeText(getContext(), R.string.message_error_remind_before_invalid, Toast.LENGTH_SHORT).show();
                    return;
                }
                before = Integer.parseInt(txtBefore.toString());
                unit = getSelectedDurationUnit();
                if (before <= 0) {
                    Toast.makeText(getContext(), R.string.message_error_remind_before_invalid, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mSaveListener.onClickSave(true,before,unit);
        }
    }

    public interface OnClickSaveListener {

        void onClickSave(boolean addReminder, int before, DurationUnit unit);
    }
}

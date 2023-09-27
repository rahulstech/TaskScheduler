package rahulstech.android.ui.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class TextUtil {


    @Nullable
    public static CharSequence strikeThrough(CharSequence text) {
        if (TextUtils.isEmpty(text)) return text;
        return strikeThrough(text,0, text.length());
    }

    @Nullable
    public static CharSequence strikeThrough(CharSequence text, int start, int end) {
        if (TextUtils.isEmpty(text)) return text;
        SpannableString s = new SpannableString(text);
        s.setSpan(new StrikethroughSpan(),start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return s;
    }

    @Nullable
    public static CharSequence italic(CharSequence text) {
        if (TextUtils.isEmpty(text)) return text;
        return italic(text,0, text.length());
    }

    @Nullable
    public static CharSequence italic(CharSequence text, int start, int end) {
        if (TextUtils.isEmpty(text)) return text;
        SpannableString s = new SpannableString(text);
        s.setSpan(new StyleSpan(Typeface.ITALIC),start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return s;
    }

    @Nullable
    public static CharSequence relativeSize(CharSequence text, float size) {
        if (TextUtils.isEmpty(text)) return text;
        return relativeSize(text,size,0, text.length());
    }

    @Nullable
    public static CharSequence relativeSize(CharSequence text, float size, int start, int end) {
        if (TextUtils.isEmpty(text)) return text;
        SpannableString s = new SpannableString(text);
        s.setSpan(new RelativeSizeSpan(size),start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return s;
    }
}

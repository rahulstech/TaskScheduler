package rahulstech.android.util.text;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class SpannableTextBuilder {

    private boolean mNonEmpty;
    private CharSequence mOrgText;
    private SpannableString mText;
    private int length;

    private SpannableTextBuilder(CharSequence text) {
        mOrgText = text;
        mNonEmpty = !TextUtils.isEmpty(text);
        length = mNonEmpty ? text.length() : 0;
        mText = mNonEmpty ? new SpannableString(text) : new SpannableString("");
    }

    public static SpannableTextBuilder text(CharSequence text) {
        return new SpannableTextBuilder(text);
    }

    public SpannableTextBuilder italic() {
        return italic(0,length);
    }

    public SpannableTextBuilder italic(int start, int end) {
        typefaceSpannable(Typeface.ITALIC,start,end);
        return this;
    }

    public SpannableTextBuilder bold() {
        return bold(0,length);
    }

    public SpannableTextBuilder bold(int start, int end) {
        typefaceSpannable(Typeface.BOLD,start,end);
        return this;
    }

    public SpannableTextBuilder relativeSize(float size) {
        return relativeSize(size,0,length);
    }

    public SpannableTextBuilder relativeSize(float size, int start, int end) {
        if (mNonEmpty) {
            SpannableString s = new SpannableString(mText);
            s.setSpan(new RelativeSizeSpan(size), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            mText = s;
        }
        return this;
    }

    public SpannableTextBuilder strikeThrough() {
        return strikeThrough(0,length);
    }

    public SpannableTextBuilder strikeThrough(int start, int end) {
        if (mNonEmpty) {
            SpannableString s = new SpannableString(mText);
            s.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            mText = s;
        }
        return this;
    }

    @NonNull
    public CharSequence build() {
        return mText;
    }

    private void typefaceSpannable(int style, int start, int end) {
        SpannableString s = new SpannableString(mText);
        s.setSpan(new StyleSpan(style),start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mText = s;
    }
}

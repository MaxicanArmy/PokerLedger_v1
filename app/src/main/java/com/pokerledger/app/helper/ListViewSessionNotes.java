package com.pokerledger.app.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ListViewSessionNotes extends ListView {

    public ListViewSessionNotes (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewSessionNotes (Context context) {
        super(context);
    }

    public ListViewSessionNotes (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

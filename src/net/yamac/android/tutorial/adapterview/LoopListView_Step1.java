package net.yamac.android.tutorial.adapterview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class LoopListView_Step1 extends AdapterView<ListAdapter> {
    private ListAdapter mAdapter;

    public LoopListView_Step1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopListView_Step1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSelection(int position) {
        throw new UnsupportedOperationException();
    }

    private int mWidthMeasureSpec;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // MeasureSpecを保持しておく
        mWidthMeasureSpec = widthMeasureSpec;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Adapterがセットされていなければ何もしない
        if (mAdapter == null) {
            return;
        }

        // 全ての子Viewをレイアウトする
        layoutChildren();
    }

    /** 全ての子Viewをレイアウトする */
    private void layoutChildren() {
        // いったん全ての子Viewを削除する。
        removeAllViewsInLayout();

        // ループで子Viewをすべて生成・配置する。
        int childrenTop = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View child = makeAndAddView(i, childrenTop);
            childrenTop += child.getMeasuredHeight() + 1;
        }
    }

    /** 子Viewを生成・配置する */
    private View makeAndAddView(int position, int top) {
        View child = obtainView(position);
        setupChild(child, position, top);
        return child;
    }

    /** Adapterから子Viewを受け取る */
    private View obtainView(int position) {
        View view = mAdapter.getView(position, null, this);
        return view;
    }

    /** 子Viewの位置とサイズを確定してレイアウトに追加する */
    protected void setupChild(View child, int position, int edge) {
        measureChild(child, position, edge);
        addViewInLayout(child, -1, child.getLayoutParams(), false);
    }

    /** 子Viewの位置とサイズを確定する */
    protected void measureChild(View child, int position, int edge) {
        LayoutParams lp = (LayoutParams)child.getLayoutParams();
        if (lp == null) {
            lp = (LayoutParams)generateDefaultLayoutParams();
            child.setLayoutParams(lp);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, 0, lp.width);
        int childHeightSpec;
        if (lp.height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);

        int childLeft = 0;
        int childTop = edge;
        int childRight = childLeft + child.getMeasuredWidth();
        int childBottom = childTop + child.getMeasuredHeight();
        child.layout(childLeft, childTop, childRight, childBottom);
    }

}

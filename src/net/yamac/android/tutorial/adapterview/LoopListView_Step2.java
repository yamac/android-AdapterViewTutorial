package net.yamac.android.tutorial.adapterview;

import java.util.LinkedList;
import java.util.Queue;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class LoopListView_Step2 extends AdapterView<ListAdapter> {
    private ListAdapter mAdapter;

    public LoopListView_Step2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopListView_Step2(Context context, AttributeSet attrs, int defStyle) {
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

    private int mFirstVisiblePosition = INVALID_POSITION;
    private int mLastVisiblePosition = INVALID_POSITION;

    /** 全ての子Viewをレイアウトする */
    private void layoutChildren() {
        // スクロールによって画面から見えなくなった子Viewだけを削除する
        removeInvisibleChildren();

        if (getChildCount() == 0) {
            // 初回ならFirstVisiblePositionを0として子Viewを配置
            mFirstVisiblePosition = 0;
            mLastVisiblePosition = INVALID_POSITION;
            fillBefore(0);
            fillAfter(0);
        } else {
            // そうでないならスクロールによって出来た上か下の余白を子Viewでうめる
            View firstView = getChildAt(0);
            fillBefore(firstView.getTop());

            View lastView = getChildAt(getChildCount() - 1);
            fillAfter(lastView.getBottom());
        }
    }

    /** スクロールによって画面から見えなくなった子Viewだけを削除する */
    private void removeInvisibleChildren() {
        int count = getChildCount();
        Rect r = new Rect();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view != null && (!view.getLocalVisibleRect(r))) {
                removeViewInLayout(view);
                mRecycleQueue.offer(view);
                mFirstVisiblePosition = toRealPosition(mFirstVisiblePosition + 1);
            } else {
                break;
            }
        }
        for (int i = count - 1; i >= 0; i--) {
            View view = getChildAt(i);
            if (view != null && (!view.getLocalVisibleRect(r))) {
                removeViewInLayout(view);
                mRecycleQueue.offer(view);
                mLastVisiblePosition = toRealPosition(mLastVisiblePosition - 1);
            } else {
                break;
            }
        }
    }

    /** 起点座標より前の余白エリアを子Viewでうめる */
    private void fillBefore(int edge) {
        int position = mFirstVisiblePosition;
        while (edge >= getScrollY()) {
            position--;
            int realPosition = toRealPosition(position);
            View child = obtainView(realPosition);
            setupChild(child, realPosition, edge, false);
            edge -= child.getMeasuredHeight();
        }
        mFirstVisiblePosition = toRealPosition(position);
    }

    /** 起点座標より後の余白エリアを子Viewでうめる */
    private void fillAfter(int edge) {
        int position = mLastVisiblePosition;
        while (edge < getScrollY() + getBottom()) {
            position++;
            int realPosition = toRealPosition(position);
            View child = obtainView(realPosition);
            setupChild(child, realPosition, edge, true);
            edge += child.getMeasuredHeight();
        }
        mLastVisiblePosition = toRealPosition(position);
    }

    /** positionを正しい位置に修正する */
    private int toRealPosition(int position) {
        int count = mAdapter.getCount();
        if (position < 0) {
            return Math.abs(count + position) % count;
        }
        return position % count;
    }

    /** 子Viewの再利用キュー */
    private Queue<View> mRecycleQueue = new LinkedList<View>();

    /** Adapterから子Viewを受け取る */
    private View obtainView(int position) {
        View convertView = mRecycleQueue.poll();
        View view = mAdapter.getView(position, convertView, this);
        return view;
    }

    /** 子Viewの位置とサイズを確定してレイアウトに追加する */
    protected void setupChild(View child, int position, int edge, boolean forward) {
        measureChild(child, position, edge, forward);
        addViewInLayout(child, forward ? -1 : 0, child.getLayoutParams(), false);
    }

    /** 子Viewの位置とサイズを確定する */
    protected void measureChild(View child, int position, int edge, boolean forward) {
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
        int childTop = forward ? edge : edge - child.getMeasuredHeight();
        int childRight = childLeft + child.getMeasuredWidth();
        int childBottom = childTop + child.getMeasuredHeight();
        child.layout(childLeft, childTop, childRight, childBottom);
    }

}

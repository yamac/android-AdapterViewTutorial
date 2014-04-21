package net.yamac.android.tutorial.adapterview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class LoopListView_Step4_TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loop_list_view_step4_test_activity);

        String[] items = new String[] {
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.loop_list_view_item_default, R.id.text, items);

        LoopListView_Step4 listView = (LoopListView_Step4)findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}

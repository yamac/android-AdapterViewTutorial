package net.yamac.android.tutorial.adapterview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class LoopListView_Step2_TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loop_list_view_step2_test_activity);

        String[] items = new String[] {
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.loop_list_view_item_default, R.id.text, items);

        LoopListView_Step2 listView = (LoopListView_Step2)findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}

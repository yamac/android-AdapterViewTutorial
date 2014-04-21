package net.yamac.android.tutorial.adapterview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class LoopListView_Step6_TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loop_list_view_step6_test_activity);

        String[] items = new String[] {
            "Item 1",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.loop_list_view_item_large, R.id.text,
            items);

        LoopListView_Step6 listView = (LoopListView_Step6)findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getAdapter().getItem(position);
                Toast.makeText(LoopListView_Step6_TestActivity.this, item, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package net.yamac.android.tutorial.adapterview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RootActivity extends ListActivity {
    private static final String TAG = RootActivity.class.getSimpleName();
    public static final String EXTRA_PATH = TAG + ".path";
    private static final String PATH_SEPARATOR = "/";
    private static final String KEY_PATH = "path";
    private static final String KEY_TYPE = "type";
    private static final String KEY_COMPONENT_NAME = "componentName";
    private static final String TYPE_PATH = "path";
    private static final String TYPE_DEMO = "demo";

    private String mBasePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // パラメタを取得
        Intent intent = getIntent();
        mBasePath = null;
        if (intent.hasExtra(EXTRA_PATH)) {
            mBasePath = intent.getStringExtra(EXTRA_PATH);
        }
        if (mBasePath != null) {
            setTitle(mBasePath);
        }

        // INTENT_ACTION_DEMOSの全アクティビティリストを取得
        PackageManager pm = getPackageManager();
        Intent appIntent = new Intent();
        appIntent.setAction(Const.INTENT_ACTION_APP);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(appIntent, 0);
        String componentBase = getClass().getName();
        componentBase = componentBase.substring(0, componentBase.lastIndexOf('.'));

        // 取得したアクティビティリストからリストアイテムを生成
        List<HashMap<String, String>> demoItems = new ArrayList<HashMap<String, String>>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            // アクティビティ情報を取得
            String componentName = resolveInfo.activityInfo.name;
            String label = resolveInfo.loadLabel(pm).toString();
            if (!componentName.startsWith(componentBase + ".")) {
                continue;
            }

            // パスを解析
            if (mBasePath != null) {
                if (!label.startsWith(mBasePath + PATH_SEPARATOR)) {
                    continue;
                }
                label = label.substring(mBasePath.length() + 1);
            }
            String[] paths = label.split(PATH_SEPARATOR);
            String path = paths[0];
            String type = paths.length == 1 ? TYPE_DEMO : TYPE_PATH;

            // リストアイテムを追加
            HashMap<String, String> demoItem = new HashMap<String, String>();
            demoItem.put(KEY_PATH, path);
            demoItem.put(KEY_TYPE, type);
            demoItem.put(KEY_COMPONENT_NAME, componentName);
            boolean isExists = false;
            for (HashMap<String, String> tmpDemoItem : demoItems) {
                String tmpPath = tmpDemoItem.get(KEY_PATH);
                if (path.equals(tmpPath)) {
                    isExists = true;
                    break;
                }
            }
            if (!isExists) {
                demoItems.add(demoItem);
            }
        }

        // リストを構成
        String[] from = new String[] {
            KEY_PATH
        };
        int[] to = new int[] {
            android.R.id.text1
        };
        ListAdapter listAdapter = new SimpleAdapter(this, demoItems, android.R.layout.simple_list_item_1, from, to);
        setListAdapter(listAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // 選択されたリストアイテムを元に次のアクティビティに遷移
        @SuppressWarnings("unchecked")
        HashMap<String, String> demoItem = (HashMap<String, String>)getListAdapter().getItem(position);
        String path = demoItem.get(KEY_PATH);
        String type = demoItem.get(KEY_TYPE);
        String componentName = demoItem.get(KEY_COMPONENT_NAME);
        if (TYPE_PATH.equals(type)) {
            String nextPath = mBasePath == null ? path : mBasePath + PATH_SEPARATOR + path;
            Intent intent = new Intent(getApplicationContext(), RootActivity.class);
            intent.putExtra(EXTRA_PATH, nextPath);
            startActivity(intent);
        } else if (TYPE_DEMO.equals(type)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), componentName);
            startActivity(intent);
        }
    }
}

package com.example.huanyingxiangji1.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.utils.LogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupList extends ListActivity implements OnItemClickListener {
    private static final String TAG = "GroupList";

    private static final int CREATE_GROUP = 1;
    private static final int ADD_NEW_PICTURE = 2;

    List<Map<String, Object>> list;
    MyApplication application;
    FileProcessor fileProcessor;
    PicProcessor picProcessor;

    private String mCurrentGroupName;

    /*
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list);
        application = (MyApplication) getApplication();

        list = getData();
        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.group_list_item, new String[]{"groupName",
                "preview0", "preview1", "preview2", "preview3"},
                new int[]{R.id.groupName, R.id.preview0, R.id.preview1,
                        R.id.preview2, R.id.preview3});
        adapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if (view instanceof ImageView) {
                    final ImageView iv = (ImageView) view;
                    iv.setImageBitmap((Bitmap) data);
                    iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            adjustImageViewHeight(iv, 1080, 1920);
                            iv.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
        registerForContextMenu(getListView());
        getListView().setOnItemClickListener(this);

    }

    private void adjustImageViewHeight(ImageView view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int bitmapWidth = width;
        int bitmapHeight = height;
        int viewWidth = view.getWidth();
        params.height = (int) (bitmapHeight * (float) viewWidth / bitmapWidth);
        view.setLayoutParams(params);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list;
        Map<String, Object> map;

        FileProcessor.checkDirs();
        fileProcessor = new FileProcessor();

        list = new ArrayList<Map<String, Object>>();
        List<String> groupNames = fileProcessor.getAllGroupName();
        for (Iterator<String> iterator = groupNames.iterator(); iterator
                .hasNext(); ) {
            String groupName = iterator.next();
            map = new HashMap<String, Object>();
            map.put("groupName", groupName);
            List<String> filePaths = fileProcessor.getGroup(groupName);
            for (int i = 0; i < filePaths.size(); i++) {
                String picPath = filePaths.get(i);
                try {
                    map.put("preview" + i, PicProcessor.getBitmapFromUri(getBaseContext(), Uri.fromFile(new File(picPath)), PicProcessor.SCALE_SMALL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (i == 3) {
                    break;
                }
            }
            list.add(map);
        }

        return list;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        int id = (int) info.id;
        if (-1 == id) {
            super.onContextItemSelected(item);
        }

        String destPic = "";
        Map<String, Object> map = list.get(id);
        mCurrentGroupName = (String) map.get("groupName");
        switch (item.getItemId()) {
            case R.id.newGroup:
                Intent i = new Intent(this, CreateNewGroup.class);
                startActivityForResult(i, CREATE_GROUP);
                return true;
            case R.id.deleteGroup:
                fileProcessor.removeGroup(mCurrentGroupName, false);
                list.remove(id);
                ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
                return true;
            case R.id.generateGif:
                destPic = MyApplication.out_path + mCurrentGroupName + ".gif";
                try {
                    PicProcessor.generateGif(fileProcessor.getGroup(mCurrentGroupName), destPic, 2000);
                    Toast.makeText(this, "ok " + MyApplication.out_path,
                            Toast.LENGTH_LONG).show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                return true;
            case R.id.combinate_h:
                picProcessor = new PicProcessor();
                try {
                    destPic = MyApplication.out_path + mCurrentGroupName + "_h.jpg";
                    picProcessor.combinate(fileProcessor.getGroup(mCurrentGroupName),
                            destPic, 0);
                    Toast.makeText(this, "ok " + MyApplication.out_path,
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.combinate_v:
                picProcessor = new PicProcessor();
                try {
                    destPic = MyApplication.out_path + mCurrentGroupName + "_v.jpg";
                    picProcessor.combinate(fileProcessor.getGroup(mCurrentGroupName),
                            destPic, 1);
                    Toast.makeText(this, "ok " + MyApplication.out_path,
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.add_new_pic:
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.setType("image/*");
//                Log.e(TAG, "groupName = " + mCurrentGroupName);
//
//                startActivityForResult(intent, ADD_NEW_PICTURE);
                i = new Intent(this, PreviewAndPicture.class);
                i.putExtra(PreviewAndPicture.KEY_FROM, GroupList.class.getSimpleName());
                i.putExtra(PreviewAndPicture.KEY_MENG_PATH, (fileProcessor.getGroup(mCurrentGroupName).get(0)));
                startActivityForResult(i, ADD_NEW_PICTURE);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        Intent i = new Intent(this, ViewPicture.class);
        i.putExtra("groupName", list.get(pos).get("groupName").toString());
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString("groupName", mCurrentGroupName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(state);
        mCurrentGroupName = state.getString("groupName");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (requestCode == CREATE_GROUP && resultCode == RESULT_OK) {
            Map<String, Object> map = new HashMap<String, Object>();
            String groupName = data.getExtras().getString("groupName");
            map.put("groupName", groupName);
            List<String> filePaths = fileProcessor.getGroup(groupName);
            for (int i = 0; i < filePaths.size(); i++) {
                String picPath = filePaths.get(i);
                map.put("preview" + i, BitmapFactory.decodeFile(picPath));
                if (i == 3) {
                    break;
                }
            }
            list.add(map);
            ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
        } else if (requestCode == ADD_NEW_PICTURE && resultCode == RESULT_OK) {
            Log.e(TAG, "groupName = " + mCurrentGroupName);
            fileProcessor.addToGroup(mCurrentGroupName, fileProcessor.getInputStreamFrom(data.getData(), this));
            //TODO
        }
    }

}

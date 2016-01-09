package com.example.huanyingxiangji1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.huanyingxiangji1.MyApplication;
import com.example.huanyingxiangji1.R;
import com.example.huanyingxiangji1.handler.PictureProcessHandler;
import com.example.huanyingxiangji1.processor.FileProcessor;
import com.example.huanyingxiangji1.processor.PicProcessor;
import com.example.huanyingxiangji1.utils.LogHelper;
import com.example.huanyingxiangji1.utils.ShareUtils;
import com.example.huanyingxiangji1.utils.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupList extends Activity {
    private static final String TAG = "GroupList";

    private static final int CREATE_GROUP = 1;
    private static final int ADD_NEW_PICTURE = 2;

    List<List<Object>> mData;
    MyApplication application;
    FileProcessor fileProcessor;

    private String mCurrentGroupName;
    private RecyclerView mGroupList;
    private MAdapter mAdapter;

    /*
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list);
        application = (MyApplication) getApplication();

        mGroupList = (RecyclerView) findViewById(R.id.group_list);
        mGroupList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        View empty = findViewById(R.id.group_list_empty);
        registerForContextMenu(mGroupList);
        loadData();
    }

    private void loadData() {
        AsyncTask<Object, Integer, List<List<Object>>> task = new AsyncTask<Object, Integer, List<List<Object>>>() {
            @Override
            protected List<List<Object>> doInBackground(Object... params) {
                return getData();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(List<List<Object>> lists) {
                super.onPostExecute(lists);
                mData = lists;
                mAdapter = new MAdapter(GroupList.this, mData, fileProcessor, new MAdapter.OnGroupSelecter() {
                    @Override
                    public void onGroupSelect(String groupName) {
                        mCurrentGroupName = groupName;
                    }
                });
                mGroupList.setAdapter(mAdapter);
            }
        };
        task.execute();
    }

    static class MAdapter extends RecyclerView.Adapter {

        private final List<List<Object>> mData;
        private final Activity mContext;
        private final FileProcessor mFileProcessor;
        private final OnGroupSelecter mOnGroupSelector;

        public MAdapter(Activity c, List<List<Object>> data, FileProcessor fileProcessor, OnGroupSelecter onGroupSelecter) {
            this.mData = data;
            this.mContext = c;
            this.mFileProcessor = fileProcessor;
            this.mOnGroupSelector = onGroupSelecter;
        }

        class Holder extends RecyclerView.ViewHolder {
            public final View mView;
            private final LinearLayout mItemPreviews;
            private final TextView mItemName;
            public View mOpt;

            public Holder(View itemView) {
                super(itemView);
                mView = itemView;
                mItemPreviews = (LinearLayout) itemView.findViewById(R.id.list_item);
                mItemName = (TextView) itemView.findViewById(R.id.list_item_name);
                mOpt = itemView.findViewById(R.id.list_item_opt);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_group_list_item, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            List<Object> item = mData.get(position);
            Holder h = (Holder) holder;
            h.mItemName.setText((String) item.get(0));
            LogHelper.d(TAG, "the item size = " + item.size());
            for (int i = 1; i < item.size(); i++) {
                h.mItemPreviews.addView(createImageView(h.mView.getContext(), (Bitmap) item.get(i)));
            }
            new ClickListener(h, (String) item.get(0), mOnGroupSelector);
        }

        interface OnGroupSelecter {
            void onGroupSelect(String groupName);
        }

        // ===========================
        // 选项单击监听
        class ClickListener implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
            private final OnGroupSelecter mOnGroupSelector;
            Holder holder;
            String groupName;

            public ClickListener(Holder holder, String groupName, OnGroupSelecter onGroupSelecter) {
                this.holder = holder;
                this.groupName = groupName;
                holder.mItemPreviews.setOnClickListener(this);
                holder.mOpt.setOnClickListener(this);
                this.mOnGroupSelector = onGroupSelecter;
            }

            @Override
            public void onClick(View v) {
                if (v == holder.mOpt) {

                    PopupMenu menu = new PopupMenu(v.getContext(), holder.mOpt);
                    menu.inflate(R.menu.group_list_context_menu);
                    menu.setOnMenuItemClickListener(this);
                    menu.show();
                } else if (v == holder.mView || v == holder.mItemPreviews) {
                    Intent i = new Intent(mContext, ViewPicture.class);
                    i.putExtra("groupName", mData.get(holder.getAdapterPosition()).get(0).toString());
                    mContext.startActivity(i);
                }
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PictureProcessHandler handler = PictureProcessHandler.getIntance();
                mOnGroupSelector.onGroupSelect(groupName);
                int id = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.newGroup:
                        createNewGroup();
                        return true;
                    case R.id.deleteGroup:
                        deleteGroup(id, groupName);
                        return true;
                    case R.id.generateGif:
                        handler.generateGif(groupName);
                        return true;
                    case R.id.combinate_h:
                        handler.combineHorizonal(groupName);
                        return true;
                    case R.id.combinate_v:
                        handler.combineVertical(groupName);
                        return true;
                    case R.id.add_new_pic:
                        addNewPic(groupName);
                        return true;
                    case R.id.share:
                        share(groupName);
                        return true;
                    default:
                        return false;
                }
            }
        }

        private void share(String groupName) {
            Uri uri = Uri.parse("file://" + new FileProcessor().getGroup(groupName).get(0));
            ShareUtils.share(mContext, groupName, uri);
        }

        private void deleteGroup(int id, String groupName) {
            mFileProcessor.removeGroup(groupName, false);
            mData.remove(id);
        }

        private void createNewGroup() {
            Intent i = new Intent(mContext, CreateNewGroup.class);
            mContext.startActivityForResult(i, CREATE_GROUP);
        }

        private void addNewPic(String groupName) {
            Intent i = new Intent(mContext, PreviewAndPicture.class);
            i.putExtra(PreviewAndPicture.KEY_FROM, GroupList.class.getSimpleName());
            List<String> group = mFileProcessor.getGroup(groupName);
            i.putExtra(PreviewAndPicture.KEY_MENG_PATH, group.get(group.size() - 1));
            mContext.startActivityForResult(i, ADD_NEW_PICTURE);
        }

        private View createImageView(Context c, Bitmap b) {
            final ImageView iv = new ImageView(c);
            iv.setImageBitmap(b);
            iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    adjustImageViewHeight(iv, 1080, 1920);
                    iv.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            iv.setClickable(false);
            return iv;
        }

        private void adjustImageViewHeight(ImageView view, int width, int height) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int viewWidth = view.getWidth();
            params.height = (int) (height * (float) viewWidth / width);
            view.setLayoutParams(params);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private List<List<Object>> getData() {
        List<List<Object>> list;
        List<Object> item;

        FileProcessor.checkDirs();
        fileProcessor = new FileProcessor();

        list = new ArrayList<List<Object>>();
        List<String> groupNames = fileProcessor.getAllGroupName();
        for (String groupName : groupNames) {
            item = new ArrayList<Object>();
            // item 第一个字符串为组名
            item.add(groupName);
            List<String> filePaths = fileProcessor.getGroup(groupName);
            for (int i = 0; i < filePaths.size(); i++) {
                String picPath = filePaths.get(i);
                try {
                    item.add(PicProcessor.getBitmapFromUri(getBaseContext(), Uri.fromFile(new File(picPath)), PicProcessor.SCALE_SMALL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (i == 3) {
                    break;
                }
            }
            list.add(item);
        }

        return list;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("groupName", mCurrentGroupName);
        LogHelper.d(TAG, "groupName = " + mCurrentGroupName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(state);
        mCurrentGroupName = state.getString("groupName");
        LogHelper.d(TAG, "restore groupName = " + mCurrentGroupName);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (requestCode == CREATE_GROUP && resultCode == RESULT_OK) {
            List<Object> map = new ArrayList<Object>();
            String groupName = data.getExtras().getString("groupName");
            map.add(groupName);
            List<String> filePaths = fileProcessor.getGroup(groupName);
            for (int i = 0; i < filePaths.size(); i++) {
                String picPath = filePaths.get(i);
                map.add(BitmapFactory.decodeFile(picPath));
                if (i == 3) {
                    break;
                }
            }
            mData.add(map);
            mAdapter.notifyDataSetChanged();
        } else if (requestCode == ADD_NEW_PICTURE && resultCode == RESULT_OK) {
            try {
                fileProcessor.addToGroup(this, mCurrentGroupName, data.getData());
            } catch (IOException e) {
                e.printStackTrace();
                ViewUtils.showToast(this, getString(R.string.group_add_new_error));
            }
            //TODO
        }
    }

}

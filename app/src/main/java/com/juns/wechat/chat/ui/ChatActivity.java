package com.juns.wechat.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.juns.wechat.Constants;
import com.juns.wechat.R;
import com.juns.wechat.bean.FriendBean;
import com.juns.wechat.chat.bean.MessageBean;
import com.juns.wechat.bean.UserBean;
import com.juns.wechat.database.dao.DbDataEvent;
import com.juns.wechat.database.dao.FriendDao;
import com.juns.wechat.database.ChatTable;
import com.juns.wechat.exception.UserNotFoundException;
import com.juns.wechat.manager.AccountManager;
import com.juns.wechat.util.LogUtil;
import com.style.base.BaseToolbarActivity;
import com.style.constant.Skip;

import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

//聊天页面
public class ChatActivity extends BaseToolbarActivity {
    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;

    public static final String COPY_IMAGE = "EASEMOBIMG";

    private static final int SIZE = 10;
    @Bind(R.id.list)
    LRecyclerView mRecyclerView;

    private ClipboardManager clipboard;
    private int chatType;

    public File cameraFile;

    public String playMsgId;

    private int contactId;
    private String contactName;

    private ChatInputManager chatInputManager;
    private ChatActivityHelper chatActivityHelper;
    private ChatAdapter mDataAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private UserBean account = AccountManager.getInstance().getUser();
    private FriendBean friendBean;
    private UserBean contactUser;
    private boolean mFirstLoad = true; //是否第一次加载数据

    private List<MessageBean> msgViewModels;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayoutResID = R.layout.activity_chat;
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_right_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.avatar_select:
                //goonNext();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * initData
     */
    public void initData() {
        contactId = getIntent().getIntExtra(Skip.KEY_USER_ID, 0);
        friendBean = FriendDao.getInstance().findByOwnerAndContactName(account.getUserId(), contactId);
        try {
            contactUser = friendBean.getContactUser();
            contactName = contactUser.getUserName();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            finish();
            return;
        }

        setToolbarTitle(friendBean.getShowName());

        chatInputManager = new ChatInputManager(this);
        chatInputManager.onCreate();
        chatActivityHelper = new ChatActivityHelper(this);
        chatActivityHelper.onCreate();

        msgViewModels = new ArrayList<>();
        mDataAdapter = new ChatAdapter(this, msgViewModels);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setLoadMoreEnabled(false);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallPulse);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        //mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallPulse);
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.white, android.R.color.white, R.color.colorAccent);

        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                int queryIndex = chatActivityHelper.getQueryIndex();
                if (queryIndex < 0) {
                    mRecyclerView.refreshComplete(5);
                } else {
                    chatActivityHelper.loadMessagesFromDb();
                }
            }

            @Override
            public void onLoadMore() {

            }
        });

        setUpView();
        chatActivityHelper.loadMessagesFromDb();

    }

    private void setUpView() {
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // 判断单聊还是群聊
        chatType = getIntent().getIntExtra(Constants.TYPE, CHATTYPE_SINGLE);
        if (chatType == CHATTYPE_SINGLE) { // 单聊

        } else {
            // 群聊
            findViewById(R.id.view_location_video).setVisibility(View.GONE);
        }

        String forward_msg_id = getIntent().getStringExtra("forward_msg_id");
        if (forward_msg_id != null) {
            // 显示发送要转发的消息
        }

    }

    public String getContactName() {
        return contactName;
    }

    public UserBean getContactUser() {
        return contactUser;
    }

    /**
     * {@link ChatActivityHelper#loadMessagesFromDb()}方法完成之后调用
     */
    public void loadDataComplete(boolean hasNewData) {
        mRecyclerView.refreshComplete(5);
        if (hasNewData) {
            mDataAdapter.notifyDataSetChanged();  //ChatActivityHelper已经更新数据源
        }
        //第一次加载数据，listView要滚动到底部，下拉刷新加载出来的数据，不用滚动到底部
        if (mFirstLoad) {
            scrollListViewToBottom();
            mFirstLoad = false;
        }
    }

    public void refreshOneData(boolean scrollListView) {
        mDataAdapter.notifyDataSetChanged();
        if (scrollListView) {
            scrollListViewToBottom();
        }
    }

    public List<MessageBean> getMsgViewModels() {
        return msgViewModels;
    }

    private void scrollListViewToBottom() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.smoothScrollToPosition(mDataAdapter.getItemCount());
            }
        });
    }

    /***
     * 监听数据库中消息表数据的变化
     *
     * @param event
     * @see ChatActivityHelper#processOneMessage(List, MessageBean, int)
     */
    @Subscriber(tag = ChatTable.TABLE_NAME)
    private void onDdDataChanged(DbDataEvent<MessageBean> event) {
        if (event.data == null || event.data.isEmpty()) return;
        LogUtil.i("data: " + event.data.toString() + "action: " + event.action);
        MessageBean messageBean = event.data.get(0);
        chatActivityHelper.processOneMessage(msgViewModels, messageBean, event.action);
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        findViewById(R.id.ll_more_function_container).setVisibility(View.GONE);
        mRecyclerView.smoothScrollToPosition(mRecyclerView.getChildCount());
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        /*if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息
				EMMessage copyMsg = ((EMMessage) adapter.getItem(data
						.getIntExtra("position", -1)));
				// clipboard.setText(SmileUtils.getSmiledText(ChatActivity.this,
				// ((TextMessageBody) copyMsg.getBody()).getMessage()));
				clipboard.setText(((TextMessageBody) copyMsg.getBody())
						.getMessage());
				break;
			case RESULT_CODE_DELETE: // 删除消息
				EMMessage deleteMsg = (EMMessage) adapter.getItem(data
						.getIntExtra("position", -1));
				conversation.removeMessage(deleteMsg.getMsgId());
				adapter.refresh();
				listView.setSelection(data.getIntExtra("position",
						adapter.getCount()) - 1);
				break;

			case RESULT_CODE_FORWARD: // 转发消息
				// EMMessage forwardMsg = (EMMessage) adapter.getItem(data
				// .getIntExtra("position", 0));
				// Intent intent = new Intent(this,
				// ForwardMessageActivity.class);
				// intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
				// startActivity(intent);

				break;

			default:
				break;
			}
		}*/
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Skip.CODE_EMPTY_HISTORY:// 清空会话
                    break;
                case Skip.CODE_TAKE_CAMERA: // 发送照片
                    if (cameraFile != null && cameraFile.exists())
                        sendPicture(cameraFile.getAbsolutePath());
                    break;
                case Skip.CODE_TAKE_ALBUM://发送本地图片
                    ArrayList<String> selectedPhotos = data.getStringArrayListExtra("paths");
                    sendPicture(selectedPhotos);
                    break;
                case Skip.CODE_RECORD_VIDEO:
                    if (data!=null){
                        String path = data.getStringExtra("path");
                        sendOfflineVideo(path);
                    }
                    break;
                case Skip.CODE_SELECT_FILE:// 发送选择的文件
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFile(uri);
                        }
                    }
                    break;
                case Skip.CODE_MAP: // 地图
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    if (locationAddress != null && !locationAddress.equals("")) {

                        sendLocationMsg(latitude, longitude, "", locationAddress);
                    } else {
                        String st = getResources().getString(R.string.unable_to_get_loaction);
                    }
                    break;
                // 重发消息
                case Skip.CODE_COPY_AND_PASTE:
                    // 粘贴
                    if (!TextUtils.isEmpty(clipboard.getText())) {
                        String pasteText = clipboard.getText().toString();
                        if (pasteText.startsWith(COPY_IMAGE)) {
                            // 把图片前缀去掉，还原成正常的path
                            //sendPicture(pasteText.replace(COPY_IMAGE, ""));
                        }

                    }
                    break;
            }
        }
    }

    private void sendPicture(String filePath) {
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        sendPicture(filePaths);
    }

    /**
     * 发送图片
     *
     * @param filePaths
     */
    private void sendPicture(ArrayList<String> filePaths) {
        chatInputManager.sendPicture(contactName, filePaths);
    }

    /**
     * 发送视频消息
     */
    private void sendOfflineVideo(String filePath) {
        chatInputManager.sendOfflineVideo(contactName, filePath);
    }

    /**
     * 发送位置信息
     *
     * @param latitude
     * @param longitude
     * @param imagePath
     * @param locationAddress
     */
    private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
    }

    /**
     * 发送文件
     *
     * @param uri
     */
    private void sendFile(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, projection, null,
                        null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
		/*if (file == null || !file.exists()) {
			String st7 = getResources().getString(R.string.File_does_not_exist);
			Toast.makeText(getApplicationContext(), st7, 0).show();
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			String st6 = getResources().getString(
					R.string.The_file_is_not_greater_than_10_m);
			Toast.makeText(getApplicationContext(), st6, 0).show();
			return;
		}*/

	/*	// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP)
			message.setChatType(ChatType.GroupChat);

		message.setReceipt(toChatUsername);
		// insertOrUpdate message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(
				filePath));
		message.addBody(body);
		conversation.addMessage(message);*/
        //listView.setAdapter(adapter);
        //listView.setSelection(listView.getCount() - 1);
        setResult(RESULT_OK);
    }


    /**
     * 点击清空聊天记录
     *
     * @param view
     */
    public void emptyHistory(View view) {
        String st5 = getResources().getString(R.string.Whether_to_empty_all_chats);

    }

    /**
     * 点击进入群组详情
     *
     * @param view
     */
    public void toGroupDetails(View view) {
        // startActivityForResult(
        // (new Intent(this, GroupDeatilActivity.class).putExtra(
        // "groupId", toChatUsername)), REQUEST_CODE_GROUP_DETAIL);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        chatInputManager.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatMediaPlayer.getInstance().release();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");

    }


}

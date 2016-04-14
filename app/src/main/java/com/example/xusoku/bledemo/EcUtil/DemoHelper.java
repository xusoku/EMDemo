package com.example.xusoku.bledemo.EcUtil;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DemoHelper {
    /**
     * 数据同步listener
     */
    static public interface DataSyncListener {
        /**
         * 同步完毕
         * @param success true：成功同步到数据，false失败
         */
        public void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";
    

    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;

	private Map<String, EaseUser> contactList;

	private Map<String, RobotUser> robotList;


	private static DemoHelper instance = null;
	
	private DemoModel demoModel = null;
	
	/**
     * HuanXin sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;
    /**
     * HuanXin sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;
    /**
     * HuanXin sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    
    private boolean alreadyNotified = false;
	
	public boolean isVoiceCalling;
    public boolean isVideoCalling;

	private String username;

    private Context appContext;


    private EMConnectionListener connectionListener;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;

	private DemoHelper() {
	}

	public synchronized static DemoHelper getInstance() {
		if (instance == null) {
			instance = new DemoHelper();
		}
		return instance;
	}

	/**
	 * init helper
	 * 
	 * @param context
	 *            application context
	 */
	public void init(Context context) {
	    demoModel = new DemoModel(context);
	    EMOptions options = initChatOptions();
	    //options传null则使用默认的
		    appContext = context;
           EMClient.getInstance().init(appContext, options);
		    //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
		    EMClient.getInstance().setDebugMode(true);
		    //get easeui instance
			//初始化PreferenceManager
			PreferenceManager.init(context);
			broadcastManager = LocalBroadcastManager.getInstance(appContext);
	        initDbDao();
	}

	
	private EMOptions initChatOptions(){
        Log.d(TAG, "init HuanXin Options");
        
        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        
        //使用gcm和mipush时，把里面的参数替换成自己app申请的
        //设置google推送，需要的GCM的app可以设置此参数
        options.setGCMNumber("324169311137");
        //在小米手机上当app被kill时使用小米推送进行消息提示，同GCM一样不是必须的
        options.setMipushConfig("2882303761517426801", "5381742660801");
        
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());
        
        return options;
//        notifier.setNotificationInfoProvider(getNotificationListener());
    }


    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }
    
    /***
     * 好友变化listener
     * 
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            // 保存增加的联系人
            Map<String, EaseUser> localUsers = getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);

           //发送好友变动广播
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(String username) {
            // 被删除
            Map<String, EaseUser> localUsers = DemoHelper.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            //发送好友变动广播
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(String username, String reason) {
        }

        @Override
        public void onContactAgreed(String username) {
        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }
    }
    

	/**
	 * 是否登录成功过
	 * 
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMClient.getInstance().isLoggedInBefore();
	}

	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken
	 *            是否解绑设备token(使用GCM才有)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		Log.d(TAG, "logout: " + unbindDeviceToken);
		EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "logout: onSuccess");
			    reset();
				if (callback != null) {
					callback.onSuccess();
				}

			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				Log.d(TAG, "logout: onSuccess");
                reset();
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}
	

	public DemoModel getModel(){
        return (DemoModel) demoModel;
    }
	
	/**
	 * 设置好友user list到内存中
	 * 
	 */
	public void setContactList(Map<String, EaseUser> aContactList) {
		if(aContactList == null){
		    if (contactList != null) {
		        contactList.clear();
		    }
			return;
		}
		
		contactList = aContactList;
	}
	
	/**
     * 保存单个user 
     */
    public void saveContact(EaseUser user){
    	contactList.put(user.getUsername(), user);
    	demoModel.saveContact(user);
    }
    
    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = demoModel.getContactList();
        }
        
        // return a empty non-null object to avoid app crash
        if(contactList == null){
        	return new Hashtable<String, EaseUser>();
        }
        
        return contactList;
    }
    
    /**
     * 设置当前用户的环信id
     * @param username
     */
    public void setCurrentUserName(String username){
    	this.username = username;
    	demoModel.setCurrentUserName(username);
    }
    
    /**
     * 获取当前用户的环信id
     */
    public String getCurrentUsernName(){
    	if(username == null){
    		username = demoModel.getCurrentUsernName();
    	}
    	return username;
    }

	public void setRobotList(Map<String, RobotUser> robotList) {
		this.robotList = robotList;
	}
	public Map<String, RobotUser> getRobotList() {
		if (isLoggedIn() && robotList == null) {
			robotList = demoModel.getRobotList();
		}
		return robotList;
	}

	 /**
     * update user list to cach And db
     *
     */
    public void updateContactList(List<EaseUser> contactInfoList) {
         for (EaseUser u : contactInfoList) {
            contactList.put(u.getUsername(), u);
         }
         ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
         mList.addAll(contactList.values());
         demoModel.saveContactList(mList);
    }



	  public void addSyncGroupListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncGroupsListeners.contains(listener)) {
	            syncGroupsListeners.add(listener);
	        }
	    }

	    public void removeSyncGroupListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncGroupsListeners.contains(listener)) {
	            syncGroupsListeners.remove(listener);
	        }
	    }

	    public void addSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncContactsListeners.contains(listener)) {
	            syncContactsListeners.add(listener);
	        }
	    }

	    public void removeSyncContactListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncContactsListeners.contains(listener)) {
	            syncContactsListeners.remove(listener);
	        }
	    }

	    public void addSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (!syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.add(listener);
	        }
	    }

	    public void removeSyncBlackListListener(DataSyncListener listener) {
	        if (listener == null) {
	            return;
	        }
	        if (syncBlackListListeners.contains(listener)) {
	            syncBlackListListeners.remove(listener);
	        }
	    }
	
	/**
    * 同步操作，从服务器获取群组列表
    * 该方法会记录更新状态，可以通过isSyncingGroupsFromServer获取是否正在更新
    * 和isGroupsSyncedWithServer获取是否更新已经完成
    */
   public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback){
       if(isSyncingGroupsWithServer){
           return;
       }
       
       isSyncingGroupsWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               try {
                   EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                   
                   // in case that logout already before server returns, we should return immediately
                   if(!isLoggedIn()){
                       isGroupsSyncedWithServer = false;
                       isSyncingGroupsWithServer = false;
                       //通知listener同步群组完毕
                       noitifyGroupSyncListeners(false);
                       return;
                   }
                   
                   demoModel.setGroupsSynced(true);
                   
                   isGroupsSyncedWithServer = true;
                   isSyncingGroupsWithServer = false;
                   
                   //通知listener同步群组完毕
                   noitifyGroupSyncListeners(true);
                   if(isContactsSyncedWithServer()){
                       notifyForRecevingEvents();
                   }
                   if(callback != null){
                       callback.onSuccess();
                   }
               } catch (HyphenateException e) {
                   demoModel.setGroupsSynced(false);
                   isGroupsSyncedWithServer = false;
                   isSyncingGroupsWithServer = false;
                   noitifyGroupSyncListeners(false);
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
           
           }
       }.start();
   }

   public void noitifyGroupSyncListeners(boolean success){
       for (DataSyncListener listener : syncGroupsListeners) {
           listener.onSyncComplete(success);
       }
   }
   
   public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback){
       if(isSyncingContactsWithServer){
           return;
       }
       
       isSyncingContactsWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               List<String> usernames = null;
               try {
                   usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                   // in case that logout already before server returns, we should return immediately
                   if(!isLoggedIn()){
                       isContactsSyncedWithServer = false;
                       isSyncingContactsWithServer = false;
                       //通知listeners联系人同步完毕
                       notifyContactsSyncListener(false);
                       return;
                   }
                  
                   Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                   for (String username : usernames) {
                       EaseUser user = new EaseUser(username);
                       userlist.put(username, user);
                   }
                   // 存入内存
                   getContactList().clear();
                   getContactList().putAll(userlist);
                    // 存入db
                   UserDao dao = new UserDao(appContext);
                   List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                   dao.saveContactList(users);

                   demoModel.setContactSynced(true);
                   EMLog.d(TAG, "set contact syn status to true");
                   
                   isContactsSyncedWithServer = true;
                   isSyncingContactsWithServer = false;
                   
                   //通知listeners联系人同步完毕
                   notifyContactsSyncListener(true);
                   if(isGroupsSyncedWithServer()){
                       notifyForRecevingEvents();
                   }
                   
                   
                   if(callback != null){
                       callback.onSuccess(usernames);
                   }
               } catch (HyphenateException e) {
                   demoModel.setContactSynced(false);
                   isContactsSyncedWithServer = false;
                   isSyncingContactsWithServer = false;
                   notifyContactsSyncListener(false);
                   e.printStackTrace();
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
               
           }
       }.start();
   }

   public void notifyContactsSyncListener(boolean success){
       for (DataSyncListener listener : syncContactsListeners) {
           listener.onSyncComplete(success);
       }
   }
   
   public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback){
       
       if(isSyncingBlackListWithServer){
           return;
       }
       
       isSyncingBlackListWithServer = true;
       
       new Thread(){
           @Override
           public void run(){
               try {
                   List<String> usernames = EMClient.getInstance().contactManager().getBlackListFromServer();
                   
                   // in case that logout already before server returns, we should return immediately
                   if(!isLoggedIn()){
                       isBlackListSyncedWithServer = false;
                       isSyncingBlackListWithServer = false;
                       notifyBlackListSyncListener(false);
                       return;
                   }
                   
                   demoModel.setBlacklistSynced(true);
                   
                   isBlackListSyncedWithServer = true;
                   isSyncingBlackListWithServer = false;
                   
                   notifyBlackListSyncListener(true);
                   if(callback != null){
                       callback.onSuccess(usernames);
                   }
               } catch (HyphenateException e) {
                   demoModel.setBlacklistSynced(false);
                   
                   isBlackListSyncedWithServer = false;
                   isSyncingBlackListWithServer = true;
                   e.printStackTrace();
                   
                   if(callback != null){
                       callback.onError(e.getErrorCode(), e.toString());
                   }
               }
               
           }
       }.start();
   }
	
	public void notifyBlackListSyncListener(boolean success){
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }
    
    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }
    
    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }
	
	public synchronized void notifyForRecevingEvents(){
        if(alreadyNotified){
            return;
        }
        
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        alreadyNotified = true;
    }
	
    synchronized void reset(){
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;
        
        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);
        
        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;
        
        alreadyNotified = false;
        isGroupAndContactListenerRegisted = false;
        
        setContactList(null);
        setRobotList(null);
        DemoDBManager.getInstance().closeDB();
    }


}

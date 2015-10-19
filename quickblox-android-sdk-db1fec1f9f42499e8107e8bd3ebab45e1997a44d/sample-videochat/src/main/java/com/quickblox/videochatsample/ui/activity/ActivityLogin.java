package com.quickblox.videochatsample.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.core.QBVideoChatController;
import com.quickblox.videochatsample.R;
import com.quickblox.videochatsample.VideoChatApplication;
import com.quickblox.videochatsample.definitions.Consts;

import org.jivesoftware.smack.XMPPException;

import helper.DataHolder;

public class ActivityLogin extends Activity {

    private ProgressDialog progressDialog;
    private EditText mUserIdText, mPasswordText;
    private SharedPreferences mSharedPreferences;
    private int mOtherUserId;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initChatService();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ActivityLogin.this);

        // setup UI
        //

        Bundle extras = getIntent().getExtras();

        mOtherUserId = extras.getInt(Consts.USER_ID);
        setContentView(R.layout.login_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        mUserIdText = (EditText) findViewById(R.id.userId);
        mPasswordText = (EditText) findViewById(R.id.password);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                mSharedPreferences.edit().putString("userName", mUserIdText.getText().toString()).commit();

                createSession(mUserIdText.getText().toString(), mPasswordText.getText().toString());
            }
        });

        if(mSharedPreferences.getInt("userId",0) != 0 ){
            mUserIdText.setVisibility(View.GONE);
            mPasswordText.setVisibility(View.GONE);
            findViewById(R.id.login).setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            createSession(mSharedPreferences.getString("userName", ""), mSharedPreferences.getString("password", ""));
        }

    }

    private void createSession(String login, final String password) {
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

                // Save current user
                //
                VideoChatApplication app = (VideoChatApplication)getApplication();
                app.setCurrentUser(qbSession.getUserId(), password);
                mSharedPreferences.edit().putInt("userId", qbSession.getUserId()).commit();
                mSharedPreferences.edit().putString("password", password).commit();

                // Login to Chat
                //
                QBChatService.getInstance().login(app.getCurrentUser(), new QBEntityCallbackImpl() {
                    @Override
                    public void onSuccess() {
                        try {
                            QBVideoChatController.getInstance().initQBVideoChatMessageListener();
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                        // show next activity
                        showCallUserActivity();
                    }

                    @Override
                    public void onError(List errors) {
                        Toast.makeText(ActivityLogin.this, "Error when login", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onError(List<String> errors) {
                progressDialog.dismiss();
                Toast.makeText(ActivityLogin.this, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllUser() {

        QBUsers.getUsers(null, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                DataHolder.getDataHolder().setQbUsersList(qbUsers);
                startGetAllUsersActivity();
            }

            @Override
            public void onError(List<String> errors) {
            }
        });
    }

    private void startGetAllUsersActivity() {
        Intent intent = new Intent(this, UsersListActivity.class);
        startActivity(intent);
        finish();
    }

    private void initChatService(){
        QBChatService.setDebugEnabled(true);

        if (!QBChatService.isInitialized()) {
            Log.d("ActivityLogin", "InitChat");
            QBChatService.init(this);
        }else{
            Log.d("ActivityLogin", "InitChat not needed");
        }
    }

    private void showCallUserActivity() {
        progressDialog.dismiss();
        
        Intent intent = new Intent(this, ActivityVideoChat.class);
        intent.putExtra(Consts.USER_ID, mOtherUserId);
        startActivity(intent);
        finish();
    }
}
package com.quickblox.videochatsample.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.quickblox.videochatsample.R;
import com.quickblox.videochatsample.adapter.UserListAdapter;
import com.quickblox.videochatsample.definitions.Consts;


import helper.DataHolder;

public class UsersListActivity extends Activity implements AdapterView.OnItemClickListener {

    private UserListAdapter usersListAdapter;
    private ListView usersList;
    private Button logOutButton;
    private Button signInButton;
    private Button selfEditButton;
    private Button singUpButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        initUI();
        initUsersList();
    }

    private void initUI() {
        logOutButton = (Button) findViewById(R.id.logout_button);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        selfEditButton = (Button) findViewById(R.id.self_edit_button);
        singUpButton = (Button) findViewById(R.id.sign_up_button);
        usersList = (ListView) findViewById(R.id.users_listview);
    }

    private void initUsersList() {
        usersListAdapter = new UserListAdapter(this);
        usersList.setAdapter(usersListAdapter);
        usersList.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataHolder.getDataHolder().getSignInQbUser() != null) {
            signInButton.setVisibility(View.GONE);
            singUpButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
            selfEditButton.setVisibility(View.VISIBLE);
        }
        usersListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // destroy session after app close
        DataHolder.getDataHolder().setSignInQbUser(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            signInButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.sign_in_button:
                intent = new Intent(this, ActivityLogin.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startShowUserActivity(position);
    }

    private void startShowUserActivity(int position) {
        Intent intent = new Intent(this, ActivityLogin.class);
        intent.putExtra(Consts.USER_ID, DataHolder.getDataHolder().getQBUser(position).getId());
        startActivity(intent);
    }
}
package com.sumang.chatdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sumang.bajaj on 6/1/2017.
 */

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_user, parent, false);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat = new Intent(getContext(), ChatActivity.class);
                chat.putExtra("selectedUser", getItem(position));//selectedUser
                getContext().startActivity(chat);
            }
        });
        TextView userTextView = (TextView) convertView.findViewById(R.id.tv_UserName);

        User user = getItem(position);
        userTextView.setText(user.getDisplayName());
        return convertView;
    }
}

package com.example.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> MessageList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout leftLayout;
        RelativeLayout rightLayout;
        //LinearLayout leftMessageLayout;
        //LinearLayout rightMessageLayout;
        TextView leftMessage;
        TextView rightMessage;
        //ImageView headRight;
        TextView ipRightText;
        TextView ipLeftText;
        TextView leftTime;
        TextView rightTime;

        public ViewHolder(View view) {
            super(view);
            leftLayout = (RelativeLayout) view.findViewById(R.id.left_layout);
            rightLayout = (RelativeLayout) view.findViewById(R.id.right_layout);
            //获取消息布局与控件实例
            //leftLayout = (LinearLayout) view.findViewById(R.id.leftMessage_layout);
            //rightLayout = (LinearLayout) view.findViewById(R.id.rightMessage_layout);
            leftMessage = (TextView) view.findViewById(R.id.left_Message);
            rightMessage = (TextView) view.findViewById(R.id.right_Message);
            //headRight = (ImageView) view.findViewById(R.id.head_right);//获取头像
            ipRightText = (TextView) view.findViewById(R.id.ip_right_of_head);//获取IP控件实例
            ipLeftText = (TextView) view.findViewById(R.id.ip_left_of_head);
            leftTime = (TextView) view.findViewById(R.id.left_time);//获取时间
            rightTime = (TextView) view.findViewById(R.id.right_time);
        }
    }

    public MessageAdapter(List<Message> messageList) {
        MessageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.message_item, parent, false);//加载子项布局
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = MessageList.get(position);
        //判断是收到消息还是发送消息
        if (msg.getType() == Message.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);

            holder.leftMessage.setText(msg.getContent());
            holder.ipLeftText.setText(msg.getIp());
            holder.leftTime.setText(msg.getTime());
        } else if (msg.getType() == Message.TYPE_SENT){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);

            holder.rightMessage.setText(msg.getContent());
            holder.ipRightText.setText(msg.getIp());
            holder.rightTime.setText(msg.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

}

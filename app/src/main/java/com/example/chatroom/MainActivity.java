package com.example.chatroom;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.exceptions.DataSupportException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public String ip = "192.168.10.4";//指定服务器IP(与自己电脑的IP相同)
    public int port = 12345;//端口号
    private boolean isConnect = false;//判断是否连接到服务器
    private Socket socket;
    private DataInputStream dis;//流，与服务器交互
    private DataOutputStream dos;
    private BufferedReader br;
    private PrintStream ps;
    private String time;

    private List<Message> msgList = new ArrayList<>();//数据源

    private EditText inputText;

    private Button send;
    private Button record;
    private Button delete;

    private EditText ipText;

    private RecyclerView MsgRecyclerView;

    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //连接服务器
        startClient();
        //获取发送按钮和输入框实例
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        record = (Button) findViewById(R.id.record);
        delete = (Button) findViewById(R.id.delete);
        ipText = (EditText) findViewById(R.id.ip_text);
        //获取RecyclerView的实例
        MsgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        Manager();
        //按钮监听
        send.setOnClickListener(this);
        record.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    private void startClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    if (socket != null) {
                        isConnect = true;
                        //dis = new DataInputStream(socket.getInputStream());
                        //dos = new DataOutputStream(socket.getOutputStream());
                        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        ps = new PrintStream(socket.getOutputStream());
                        //开启线程接收服务器的消息
                        new Thread(new Revised()).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void Manager() {
        //设置RecyclerView的布局方式为线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        MsgRecyclerView.setLayoutManager(layoutManager);
        //配置适配器
        adapter = new MessageAdapter(msgList);//将内容传入
        MsgRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                String content = inputText.getText().toString();//获取内容
                String ip = ipText.getText().toString();//获取接收方IP地址
                if (!"".equals(content)) {
                    ip = ip.length() == 0 ? "255.255.255.255" : ip;//如果没有给IP则是最大的
                    if (isConnect == true) {//如果连接服务器成功就发送
                        time = getCurrentTime();
                        send(content, ip, time);//发送消息，进入send方法
                        Message message;
                        if(ip.equals("255.255.255.255")) {
                            message = new Message(content, Message.TYPE_SENT,"群消息", time);
                            msgList.add(message);
                            litePalAdd(content,Message.TYPE_SENT,"群消息", time);//存消息
                        } else {
                            message = new Message(content, Message.TYPE_SENT,"私发消息", time);
                            msgList.add(message);
                            litePalAdd(content,Message.TYPE_SENT,"私发消息", time);//存消息
                        }
                        //刷新操作
                        adapter.notifyDataSetChanged();
                        MsgRecyclerView.scrollToPosition(msgList.size() - 1);
                        //upData();
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //刷新操作
                                adapter.notifyDataSetChanged();
                                MsgRecyclerView.scrollToPosition(msgList.size() - 1);
                            }
                        });*/
                    } else {
                        Toast.makeText(this, "发送失败！！！", Toast.LENGTH_SHORT).show();
                    }
                    inputText.setText("");//将输出框设为空
                }
                break;
            case R.id.record:
                record();
                break;
            case R.id.delete:
                delete();
                break;
            default:
        }
    }

    private void delete() {
        msgList.clear();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //刷新操作
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void record() {
        msgList.clear();
        List<Message> allMsg = LitePal.findAll(Message.class);
        for (Message message : allMsg) {
            msgList.add(message);
            adapter.notifyDataSetChanged();
            MsgRecyclerView.scrollToPosition(msgList.size() - 1);//将消息放于最后一行
        }
    }

    public void send(String content, String ip, String time) {//客户端发送消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ps.println(content);
                    ps.println(ip);
                    ps.println(time);
                    ps.flush();
                    /*dos.writeChars(content);
                    dos.writeChar('\t');
                    dos.writeChars(ip);
                    dos.writeChar('\t');
                    dos.writeChars(time);
                    dos.writeChar('\t');
                    dos.flush();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private String getCurrentTime() {//获取当前时间
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        return sdf.format(d);
    }

    public void litePalAdd(String content,int type, String ip,String time) {//新建表用于存聊天内容
        Message message = new Message();
        message.setContent(content);
        message.setType(type);
        message.setIp(ip);
        message.setTime(time);
        message.save();
    }

    class Revised implements Runnable {
        @Override
        public void run() {
            //无限循环，不然只能接收一条消息
            //StringBuilder sb = new StringBuilder();
            while (true) {
                char c;
                String content = "";
                String ip = "";
                String time = "";

                try {
                    content = br.readLine();
                    ip = br.readLine();
                    time = br.readLine();
                    /*//获取内容
                    while ((c = dis.readChar()) != '\t') {//用'\t'分割
                        sb.append(c);
                    }
                    content = sb.toString();
                    sb.replace(0,sb.length(),"");
                    //获取IP
                    while ((c = dis.readChar()) != '\t') {
                        sb.append(c);
                    }
                    ip = sb.toString();
                    sb.replace(0,sb.length(),"");
                    //获取时间
                    while ((c = dis.readChar()) != '\t') {
                        sb.append(c);
                    }
                    time = sb.toString();
                    sb.replace(0,sb.length(),"");*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (content.equals("")) {
                } else {
                    Message message = new Message(content, Message.TYPE_RECEIVED, ip, time);
                    msgList.add(message);
                    litePalAdd(content,Message.TYPE_RECEIVED,ip,time);//存数据
                    //刷新操作
                    //upData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新
                            adapter.notifyDataSetChanged();
                            MsgRecyclerView.scrollToPosition(msgList.size() - 1);//将消息放于最后一行
                        }
                    });
                }
            }
        }
    }
}



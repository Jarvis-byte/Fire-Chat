package com.example.chatappwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappwithfirebase.Adapter.MessageAdapter;
import com.example.chatappwithfirebase.Notification.APIService;
import com.example.chatappwithfirebase.Model.Chat;
import com.example.chatappwithfirebase.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {
CircleImageView profile_image;
TextView username;
FirebaseUser fuser;
DatabaseReference reference;
Intent intent;
ImageButton btn_send;
EditText txt_send;
MessageAdapter messageAdapter;
List<Chat>mchat;
RecyclerView recyclerView;
     String userid;
     ValueEventListener seenListener;
     APIService apiService;
     boolean notify = false;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
     //   apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
linearLayoutManager.setStackFromEnd(true);
recyclerView.setLayoutManager(linearLayoutManager);


        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        btn_send=findViewById(R.id.btn_send);
        txt_send=findViewById(R.id.text_send);

        intent=getIntent();

        userid=intent.getStringExtra("userid");

        fuser= FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      //  notify=true;
        String msg=txt_send.getText().toString();
            if (!msg.equals(""))
            {
            sendMessage(fuser.getUid(),userid,msg);
            }
            else
            {
                Toast.makeText(MessageActivity.this, "Please Write Something To Send", Toast.LENGTH_SHORT).show();
            }
        txt_send.setText("");

         }
    });

reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
     User user=dataSnapshot.getValue(User.class);
     username.setText(user.getUsername());
     if(user.getImageUrl().equals("default"))
     {
         profile_image.setImageResource(R.mipmap.ic_launcher);
     }
     else
     {
         if (!MessageActivity.this.isFinishing())
         Glide.with(MessageActivity.this).load(user.getImageUrl()).into(profile_image);
     }

            readMessages(fuser.getUid(),userid,user.getImageUrl());
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
seenMessages(userid);

    }
    private void seenMessages(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid))
                    {
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
           hashMap.put("sender",sender);
           hashMap.put("receiver",receiver);
           hashMap.put("message",message);
        hashMap.put("isseen",false);
           reference.child("Chats").push().setValue(hashMap);
//Adding User to chat fragment:

final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid()).child(userid);
chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(!dataSnapshot.exists())
        {
            chatRef.child("id").setValue(userid);
        }
    }
    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
//        final String msg=message;
//        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user=dataSnapshot.getValue(User.class);
//                if (notify) {
//                    sendNotification(receiver, user.getUsername(), msg);
//                }
//                notify=false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
  }

//    private void sendNotification(String receiver, final String username, final String message) {
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query =tokens.orderByKey().equalTo(receiver);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Token token =snapshot.getValue(Token.class);
//                    Data data =new Data(fuser.getUid(),R.mipmap.ic_launcher,username+":"+message,"New Message",
//                            userid);
//                    Sender sender=new Sender(data,token.getToken());
//                    apiService.sendNotification(sender)
//                            .enqueue(new Callback<Response>() {
//                                @Override
//                                public void onResponse(Call<Response> call, Response<Response> response) {
//                                        if(response.code()==200)
//                                        {
//                                            if(response.body().success!=1)
//                                            {
//                                                Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                }
//
//                                @Override
//                                public void onFailure(Call<Response> call, Throwable t) {
//
//                                }
//                            });
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void readMessages(final String myid, final String userid, final String imageurl)
    {
        mchat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
mchat.clear();
for (DataSnapshot snapshot:dataSnapshot.getChildren())
{
    Chat chat=snapshot.getValue(Chat.class);
    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid))
    {
        mchat.add(chat);
    }
messageAdapter=new MessageAdapter(MessageActivity.this,mchat,imageurl);
    recyclerView.setAdapter(messageAdapter);
}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void status(String status)
    {
        reference=FirebaseDatabase.getInstance().
                getReference("Users").
                child(fuser.getUid());
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("Offline");
    }

}
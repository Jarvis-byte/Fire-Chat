package com.example.chatappwithfirebase.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappwithfirebase.Model.Chat;
import com.example.chatappwithfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }

        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
//holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {
//    @Override
//    public boolean onLongClick(View v) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
//        builder.setTitle("DELETE Only From You")
//                .setMessage("Are You Sure")
//                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mChat.remove(position);
//                        notifyItemRemoved(position);
//                    }
//                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).create().show();
//        return false;
//    }
//});

        holder.show_message.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = mChat.get(position).getMessage();

                if (msg.equals("This message was deleted...")) {
                    Toast.makeText(mContext, "Message Already deleted", Toast.LENGTH_SHORT).show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("DELETE From Receiver Side")
                            .setMessage("Are you Sure")

                            .setNeutralButton("DELETE SECRETLY",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            deleteMessage(position);
                                        }
                                    }).setPositiveButton("DELETE PUBLICLY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteMessagePublic(position);
                        }
                    })

                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                }


            }
        });
    }

//        private void deleteFromSelfOnly(final int position)
//        {
//
//            final String myUID=FirebaseAuth.getInstance().getCurrentUser().getUid();
//            String msg=mChat.get(position).getMessage();
//            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
//            Query query=dbRef.orderByChild("message").equalTo(msg);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot ds:dataSnapshot.getChildren())
//                    {
//                        if(ds.child("sender").getValue().equals(myUID))
//                        {
//
//                            mChat.remove(position);
//
//                            notifyItemRemoved(position);
//                            Toast.makeText(mContext, "Message Deleted", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(mContext, "Hey!You can Delete Only your Message", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        }


    private void deleteMessage(final int position) {


        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msg = mChat.get(position).getMessage();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("message").equalTo(msg);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("sender").getValue().equals(myUID)) {

                        ds.getRef().removeValue();
                        Toast.makeText(mContext, "Message Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Hey!You can Delete Only your Message", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteMessagePublic(int position) {
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        String msg = mChat.get(position).getMessage();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("message").equalTo(msg);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("sender").getValue().equals(myUID)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "This message was deleted...");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(mContext, "Message Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Hey!You can Delete Only your Message", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {

        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.text_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }

}

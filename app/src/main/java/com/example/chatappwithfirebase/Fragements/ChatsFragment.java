package com.example.chatappwithfirebase.Fragements;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatappwithfirebase.Adapter.UserAdapter1;
import com.example.chatappwithfirebase.Model.Chat;
import com.example.chatappwithfirebase.Model.ChatList;
import com.example.chatappwithfirebase.Model.User;
import com.example.chatappwithfirebase.Notification.Token;
import com.example.chatappwithfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {
private RecyclerView recyclerView;
private UserAdapter1 userAdapter;
private List<User>mUsers;
FirebaseUser fuser;
DatabaseReference reference;
private List<ChatList> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
fuser=FirebaseAuth.getInstance().getCurrentUser();
usersList =new ArrayList<>();
reference=FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());

reference.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        usersList.clear();
        //Loop for all user:
        for (DataSnapshot snapshot:dataSnapshot.getChildren())
        {
            ChatList chatList =snapshot.getValue(ChatList.class);
            usersList.add(chatList);
        }
        chatList();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
      //  updateToken(FirebaseInstanceId.getInstance().getToken());


return view;

    }
//    private void updateToken(String token)
//    {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1=new Token(token);
//        reference.child(fuser.getUid()).setValue(token1);
//
//    }




    private void chatList() {
//Getting all recent chats:
        mUsers=new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    User user= snapshot.getValue(User.class);
                    for (ChatList chatList:usersList)
                    {
                        if(user.getId().equals(chatList.getId()))
                        {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter=new UserAdapter1(getContext(),mUsers,true);
recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
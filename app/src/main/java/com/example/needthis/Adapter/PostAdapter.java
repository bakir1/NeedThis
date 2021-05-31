package com.example.needthis.Adapter;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.needthis.Model.Post;
import com.example.needthis.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> mList;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public PostAdapter(Activity context, List<Post> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_post, parent, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mList.get(position);
        holder.setPostPic(post.getImage());
        holder.setPostCaption(post.getCaption());

        long milliseconds = post.getTime().getTime();
        String date = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setPostDate(date);

        String userId = post.getUser();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String username = task.getResult().getString("name");
                    String image = task.getResult().getString("image");

                    holder.setProfilePic(image);
                    holder.setPostUsername(username);
                }else {
                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        String postId = post.PostId;
        String currentUserId = auth.getCurrentUser().getUid();
        holder.likePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp" , FieldValue.serverTimestamp());
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).set(likesMap);
                        }else {
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        //like change color
        firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value.exists()){
                        holder.likePic.setImageDrawable(context.getDrawable(R.drawable.after_liked));
                    }else {
                        holder.likePic.setImageDrawable(context.getDrawable(R.drawable.before_liked));
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        ImageView postPic, commentsPic, likePic;
        CircleImageView profilePic;
        TextView postUsername, postDate, postCaption, postLikes;
        View mView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            likePic = mView.findViewById(R.id.like_btn);
        }
        public void setPostLikes(int count){
            postLikes = mView.findViewById(R.id.like_count_tv);
            postLikes.setText(count);
        }

        public void setPostPic(String urlPost){
            postPic = mView.findViewById(R.id.user_post);
            Glide.with(context).load(urlPost).into(postPic);
        }

        public void setProfilePic(String urlProfile){
            profilePic = mView.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profilePic);
        }

        public void setPostUsername(String username){
            postUsername = mView.findViewById(R.id.username_tv);
            postUsername.setText(username);
        }

        public void setPostDate(String date){
            postDate = mView.findViewById(R.id.date_tv);
            postDate.setText(date);
        }

        public void setPostCaption(String caption){
            postCaption = mView.findViewById(R.id.caption_tv);
            postCaption.setText(caption);
        }
    }
}

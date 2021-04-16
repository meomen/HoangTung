package firebase.gopool.Chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import firebase.gopool.R;

public class ChatTextHolder extends RecyclerView.ViewHolder {

    public TextView tv_time,tv_email,tv_chat_message;
    public CircleImageView profile_image;

    public ChatTextHolder(@NonNull View itemView) {
        super(itemView);
        init(itemView);
    }

    private void init(View view) {
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_email = (TextView) view.findViewById(R.id.tv_email);
        tv_chat_message = (TextView) view.findViewById(R.id.tv_chat_message);
        profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
    }
}
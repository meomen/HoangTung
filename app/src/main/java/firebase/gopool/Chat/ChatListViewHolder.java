package firebase.gopool.Chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import firebase.gopool.R;


public class ChatListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView tv_email;
    public TextView tv_chat_message;

    IRecyclerClickListener listener;

    public void setListener(IRecyclerClickListener listener) {
        this.listener = listener;
    }

    public ChatListViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        init(itemView);
    }

    private void init(View view) {
        tv_email = (TextView) view.findViewById(R.id.tv_email);
        tv_chat_message = (TextView) view.findViewById(R.id.tv_chat_message);
    }

    @Override
    public void onClick(View v) {
        listener.onItemClickListener(v,getAdapterPosition());
    }
}

package com.example.msengage.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msengage.R;
import com.example.msengage.models.Messages;
import com.example.msengage.utilities.Constants;
import com.example.msengage.utilities.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {


        if(viewType==ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return  new SenderViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout_item,parent,false);
            return  new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesArrayList.get(position);
        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.txtMessage.setText(messages.getMessage());
        }
        else
        {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.txtMessage.setText(messages.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        PreferenceManager preferenceManager = new PreferenceManager(context.getApplicationContext());
        if(preferenceManager.getString(Constants.KEY_USER_ID).equals(messages.getSenderId()))
        {
            return ITEM_SEND;
        }
        else
        {
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        public SenderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.textMessages);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView txtMessage;
        public ReceiverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.textMessages);
        }
    }
}

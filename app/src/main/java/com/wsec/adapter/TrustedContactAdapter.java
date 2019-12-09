package com.wsec.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.wsec.R;
import com.wsec.RVOnlickListener;
import com.wsec.model.TrustedContact;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrustedContactAdapter extends RecyclerView.Adapter<TrustedContactAdapter.ViewHolder> {


    private List<TrustedContact> trustedContacts;
    private Context context;
    private RVOnlickListener listener;

    public TrustedContactAdapter(Context context, List<TrustedContact> trustedContacts) {
        this.trustedContacts = trustedContacts;
        this.context = context;
        this.listener = (RVOnlickListener) context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trusted_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.contactName.setText(trustedContacts.get(position).getName());
        holder.contactNo.setText(trustedContacts.get(position).getContactNo());
        holder.btnDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position,trustedContacts.get(position).getContactNo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return trustedContacts == null ? 0 : trustedContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contact_name)
        AppCompatTextView contactName;
        @BindView(R.id.contact_no)
        AppCompatTextView contactNo;
        @BindView(R.id.btn_delete)
        AppCompatImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

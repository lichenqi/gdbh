package com.guodongbaohe.app.custom_view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.guodongbaohe.app.R;

public class XiaoHaiActivity extends AppCompatActivity {

    private String[] titles = {"鼻导管", "鼻肠管", "PTCD管", "气切套管", "气管插管", "胃管"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.xiaohaiactivity );
        RecyclerView recyclerview = findViewById( R.id.recyclerview );
        recyclerview.setHasFixedSize( true );
        recyclerview.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        MyAdapter myAdapter = new MyAdapter( titles );
        recyclerview.setAdapter( myAdapter );
        myAdapter.setOnClickListener( new OnItem() {
            @Override
            public void OnItemClick(View view, int position) {
                Toast.makeText( getApplicationContext(), titles[position], Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    public interface OnItem {
        void OnItemClick(View view, int position);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private String[] titles;
        private OnItem onItem;

        public MyAdapter(String[] titles) {
            this.titles = titles;
        }

        public void setOnClickListener(OnItem onItem) {
            this.onItem = onItem;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.xiaohai_item, viewGroup, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            myViewHolder.name.setText( titles[position] );
            if (onItem != null) {
                myViewHolder.name.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItem.OnItemClick( myViewHolder.itemView, myViewHolder.getAdapterPosition() );
                    }
                } );
            }
        }

        @Override
        public int getItemCount() {
            return titles == null ? 0 : titles.length;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            name = itemView.findViewById( R.id.name );
        }
    }

}

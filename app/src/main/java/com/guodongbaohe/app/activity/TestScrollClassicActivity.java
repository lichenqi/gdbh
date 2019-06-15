package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestScrollClassicActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.view_scroll)
    View viewScroll;

    @Override
    public int getContainerView() {
        return R.layout.testscrollclassicactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setMiddleTitle( "滑动分类效果" );
        ButterKnife.bind( this );
        recyclerview.setHasFixedSize( true );
        GridLayoutManager manager = new GridLayoutManager( getApplicationContext(), 5 );
        recyclerview.setLayoutManager( manager );
        recyclerview.setAdapter( new MyAdapter() );
        recyclerview.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                Log.i( "看看滑动位移", dx + "" );
            }
        } );
        Log.i( "任务栈id", getTaskId() + "" );
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.scroll_classic_item, viewGroup, false );
            return new MyHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        public MyHolder(@NonNull View itemView) {
            super( itemView );
        }
    }

}

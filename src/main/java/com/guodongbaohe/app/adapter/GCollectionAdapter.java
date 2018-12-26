//package com.guodongbaohe.app.adapter;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.guodongbaohe.app.R;
//import com.guodongbaohe.app.bean.AllNetBean;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
//import java.util.List;
//
//public class GCollectionAdapter extends BaseAdapter {
//    private boolean isShow = true;//是否显示编辑/完成
//    private List<AllNetBean.AllNetData> shoppingCartBeanList;
//    private CheckInterface checkInterface;
//    private ModifyCountInterface modifyCountInterface;
//    private Context context;
//
//    public GCollectionAdapter(Context context) {
//        this.context = context;
//    }
//
//    public List<AllNetBean.AllNetData> getShoppingCartBeanList() {
//        return shoppingCartBeanList;
//    }
//
//    public void setShoppingCartBeanList(List<AllNetBean.AllNetData> shoppingCartBeanList) {
//        this.shoppingCartBeanList = shoppingCartBeanList;
//        notifyDataSetChanged();
//    }
//
//    /**
//     * 单选接口
//     *
//     * @param checkInterface
//     */
//    public void setCheckInterface(CheckInterface checkInterface) {
//        this.checkInterface = checkInterface;
//    }
//
//    /**
//     * 改变商品数量接口
//     *
//     * @param modifyCountInterface
//     */
//    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
//        this.modifyCountInterface = modifyCountInterface;
//    }
//
//    @Override
//    public int getCount() {
//        return shoppingCartBeanList == null ? 0 : shoppingCartBeanList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return shoppingCartBeanList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//
//    /**
//     * 是否显示可编辑
//     *
//     * @param flag
//     */
//    public void isShow(boolean flag) {
//        isShow = flag;
//        notifyDataSetChanged();
//    }
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        final ViewHolder holder;
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.gcollection_list_item, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        final AllNetBean.AllNetData shoppingCartBean = shoppingCartBeanList.get(position);
//        boolean choosed = shoppingCartBean.isChoosed();
//        if (choosed){
//            holder.ckOneChose.setChecked(true);
//        }else{
//            holder.ckOneChose.setChecked(false);
//        }
//        String attribute = shoppingCartBean.getSeller_shop();
//        if (!TextUtils.isEmpty(attribute)){
//            holder.tvCommodityAttr.setText(attribute);
//        }else{
////            holder.tvCommodityAttr.setText(shoppingCartBean.getDressSize()+"");
//        }
//        holder.tvCommodityName.setText(shoppingCartBean.getGoods_name());
//        holder.tvCommodityPrice.setText(shoppingCartBean.getAttr_price());
////        holder.tvCommodityNum.setText(shoppingCartBean.get);
//        holder.tvCommodityShowNum.setText(shoppingCartBean.getSon_count());
//        ImageLoader.getInstance().displayImage(shoppingCartBean.getGoods_gallery(),holder.ivShowPic);
//        //单选框按钮
//        holder.ckOneChose.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        shoppingCartBean.setChoosed(((CheckBox) v).isChecked());
//                        checkInterface.checkGroup(position, ((CheckBox) v).isChecked());//向外暴露接口
//                    }
//                }
//        );
//
//        //删除弹窗
//        holder.tvCommodityDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog alert = new AlertDialog.Builder(context).create();
//                alert.setTitle("操作提示");
//                alert.setMessage("您确定要将这些商品从购物车中移除吗？");
//                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                return;
//                            }
//                        });
//                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                modifyCountInterface.childDelete(position);//删除 目前只是从item中移除
//
//                            }
//                        });
//                alert.show();
//            }
//        });
//        //判断是否在编辑状态下
//        if (isShow) {
////            holder.tvCommodityName.setVisibility(View.VISIBLE);
//            holder.ckOneChose.setVisibility(View.VISIBLE);
////            holder.rlEdit.setVisibility(View.GONE);
////            holder.tvCommodityNum.setVisibility(View.VISIBLE);
////            holder.tvCommodityDelete.setVisibility(View.GONE);
//        } else {
//            holder.ckOneChose.setVisibility(View.GONE);
////            holder.tvCommodityName.setVisibility(View.VISIBLE);
////            holder.rlEdit.setVisibility(View.VISIBLE);
////            holder.tvCommodityNum.setVisibility(View.GONE);
////            holder.tvCommodityDelete.setVisibility(View.VISIBLE);
//        }
//
//        return convertView;
//    }
//    //初始化控件
//    class ViewHolder {
//        ImageView ivShowPic;
//
//        TextView tvCommodityName, tvCommodityAttr, tvCommodityPrice, tvCommodityNum, tvCommodityShowNum,tvCommodityDelete;
//        CheckBox ckOneChose;
//        LinearLayout rlEdit;
//        public ViewHolder(View itemView) {
//            ckOneChose = (CheckBox) itemView.findViewById(R.id.ck_chose);
//            ivShowPic = (ImageView) itemView.findViewById(R.id.iv);
////            ivSub = (TextView) itemView.findViewById(R.id.iv_sub);
////            ivAdd = (TextView) itemView.findViewById(R.id.iv_add);
//            tvCommodityName = (TextView) itemView.findViewById(R.id.title);
//            tvCommodityAttr = (TextView) itemView.findViewById(R.id.dianpu_name);
//            tvCommodityPrice = (TextView) itemView.findViewById(R.id.tv_price);
//            tvCommodityNum = (TextView) itemView.findViewById(R.id.tv_sale_num);
//            tvCommodityShowNum = (TextView) itemView.findViewById(R.id.ninengzhuan);
//            tvCommodityDelete = (TextView) itemView.findViewById(R.id.sjizhuan);
////            rlEdit = (LinearLayout) itemView.findViewById(R.id.rl_edit);
//
//
//        }
//    }
////    /**
////     * 复选框接口
////     */
////    public interface CheckInterface {
////        /**
////         * 组选框状态改变触发的事件
////         *
////         * @param position  元素位置
////         * @param isChecked 元素选中与否
////         */
////        void checkGroup(int position, boolean isChecked);
////    }
//
//
//    /**
//     * 改变数量的接口
//     */
//    public interface ModifyCountInterface {
//        /**
//         * 增加操作
//         *
//         * @param position      元素位置
//         * @param showCountView 用于展示变化后数量的View
//         * @param isChecked     子元素选中与否
//         */
//        void doIncrease(int position, View showCountView, boolean isChecked);
//
//        /**
//         * 删减操作
//         *
//         * @param position      元素位置
//         * @param showCountView 用于展示变化后数量的View
//         * @param isChecked     子元素选中与否
//         */
//        void doDecrease(int position, View showCountView, boolean isChecked);
//
//        /**
//         * 删除子item
//         *
//         * @param position
//         */
//        void childDelete(int position);
//    }
//}

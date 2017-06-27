package com.guanghua.ln.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guanghua.ln.activitys.R;
import com.guanghua.ln.interfaces.MyDialogEnterListener;


/**
 * 自定义Dialog 工具类
 */
public class HiFiDialogTools {

    private MyDialogEnterListener listener;


    /**
     * @param abstruct 弹出简介提示信息
     */
    public Dialog showAbstructView(Context context, String abstruct) {

        final Dialog abstructViewDialog = new Dialog(context, R.style.MyDialog);
        abstructViewDialog.setContentView(R.layout.dialog_special_abstruct);

        LinearLayout warp = (LinearLayout) abstructViewDialog.findViewById(R.id.parentWarp);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Tools.getDisplayWidth((Activity) context),
                context.getResources().getDimensionPixelOffset(R.dimen.dialog_with));
        warp.setLayoutParams(lp);
        abstructViewDialog.setCanceledOnTouchOutside(false);
        abstructViewDialog.setCancelable(true);

        TextView txtAbstruct = (TextView) abstructViewDialog.findViewById(R.id.txtAbstruct);
        if (txtAbstruct != null && !TextUtils.isEmpty(abstruct))
            txtAbstruct.setText("\u3000\u3000" + abstruct);//首行缩进

        safeShowDialog(context, abstructViewDialog);
        return abstructViewDialog;
    }

    public Dialog showsingeTip(Context context, String content, final MyDialogEnterListener listener) {

        final Dialog singeTipDialog = new Dialog(context, R.style.MyDialog);
        singeTipDialog.setContentView(R.layout.dialog_sing_tip);

        singeTipDialog.findViewById(R.id.dialog_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singeTipDialog.dismiss();
                if (listener != null) {
                    listener.onClickEnter(singeTipDialog, null);
                }
            }
        });

        TextView tv_content = (TextView) singeTipDialog.findViewById(R.id.dialog_content);

        if (!Tools.isNullOrEmpty(content) && tv_content != null)
            tv_content.setText(content);

        safeShowDialog(context, singeTipDialog);
        return singeTipDialog;
    }

//    /**
//     * @param listener 弹出订购提示
//     */
//    public Dialog showPlaceOrder(Context context, final MyDialogEnterListener listener) {
//        final Dialog orderDialog = new Dialog(context, R.style.MyDialog);
//        orderDialog.setContentView(R.layout.dialog_show_placeorder_first);
//        orderDialog.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (orderDialog != null)
//                    orderDialog.dismiss();
//                if (listener != null)
//                    listener.onClickEnter(orderDialog, null);
//            }
//        });
//        orderDialog.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (orderDialog != null)
//                    orderDialog.dismiss();
//                if (listener != null)
//                    listener.onClickEnter(orderDialog, "cancel");
//            }
//        });
//        TextView textView = (TextView) orderDialog.findViewById(R.id.txtorign);
//        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中间划线
//        LinearLayout warp = (LinearLayout) orderDialog.findViewById(R.id.parentWarp);
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Tools.getDisplayWidth((Activity) context), context.getResources().getDimensionPixelOffset(R.dimen.dp450));
//        warp.setLayoutParams(lp);
//        orderDialog.setCanceledOnTouchOutside(false);
//        orderDialog.setCancelable(false);
//
//        safeShowDialog(context, orderDialog);
//        return orderDialog;
//    }


    /**
     * @param context  弹出订购提示,二次确认
     * @param listener
     */
    public Dialog showLeftRightTip(Context context, String title, String message, String leftBtn, String rightBtn, final MyDialogEnterListener listener) {

        final Dialog singeLeftRightTipDialog = new Dialog(context, R.style.MyDialog);
        singeLeftRightTipDialog.setContentView(R.layout.dialog_show_placeorder_second_confirm);

        TextView txtTltie = (TextView) singeLeftRightTipDialog.findViewById(R.id.txtTltie);
        txtTltie.setText(title);
        TextView txtAbstruct = (TextView) singeLeftRightTipDialog.findViewById(R.id.txtAbstruct);
        txtAbstruct.setText(message);

        Button left = (Button) singeLeftRightTipDialog.findViewById(R.id.left);
        left.setText(leftBtn);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (singeLeftRightTipDialog != null) {
                    singeLeftRightTipDialog.dismiss();
                }
                if (listener != null) {
                    listener.onClickEnter(singeLeftRightTipDialog, 0);
                }
            }
        });

        Button right = (Button) singeLeftRightTipDialog.findViewById(R.id.right);
        right.setText(rightBtn);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (singeLeftRightTipDialog != null) {
                    singeLeftRightTipDialog.dismiss();
                }
                if (listener != null) {
                    listener.onClickEnter(singeLeftRightTipDialog, 1);
                }
            }
        });


        if (TextUtils.isEmpty(leftBtn)) {
            left.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(rightBtn)) {
            right.setVisibility(View.GONE);
        }

        LinearLayout warp = (LinearLayout) singeLeftRightTipDialog.findViewById(R.id.parentWarp);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Tools.getDisplayWidth((Activity) context), context.getResources().getDimensionPixelOffset(R.dimen.dialog_with));
        warp.setLayoutParams(lp);
        singeLeftRightTipDialog.setCanceledOnTouchOutside(false);
        singeLeftRightTipDialog.setCancelable(false);

        left.requestFocus();

        safeShowDialog(context, singeLeftRightTipDialog);
        return singeLeftRightTipDialog;
    }

    Dialog orderOptionDialog;

//    /**
//     * @param balance  弹出订购提示余额不足,支付宝充值,二期使用
//     * @param listener
//     * @return
//     */
//    public Dialog showPlaceOrderOption(Context context, Double balance, final MyDialogEnterListener listener) {
//
//        if (orderOptionDialog == null) {
//            orderOptionDialog = new Dialog(context, R.style.MyDialog);
//            orderOptionDialog.setContentView(R.layout.dialog_show_placeorder_lackofbalance);
//            orderOptionDialog.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (orderOptionDialog != null)
//                        orderOptionDialog.dismiss();
//                    if (listener != null)
//                        listener.onClickEnter(orderOptionDialog, null);//支付宝充值
//                }
//            });
//            orderOptionDialog.findViewById(R.id.btn_leftleft).setOnClickListener(new View.OnClickListener() {//营业厅充值
//                @Override
//                public void onClick(View view) {
//                    if (orderOptionDialog != null)
//                        orderOptionDialog.dismiss();
//                }
//            });
//            orderOptionDialog.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (orderOptionDialog != null)
//                        orderOptionDialog.dismiss();
//                }
//            });
//
//            LinearLayout warp = (LinearLayout) orderOptionDialog.findViewById(R.id.parentWarp);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Tools.getDisplayWidth((Activity) context), context.getResources().getDimensionPixelOffset(R.dimen.dp450));
//            warp.setLayoutParams(lp);
//            orderOptionDialog.setCanceledOnTouchOutside(false);
//            orderOptionDialog.setCancelable(false);
//        }
//        TextView textView = (TextView) orderOptionDialog.findViewById(R.id.myBalance);
//        if (textView != null)
//            textView.setText("我的余额:" + Tools.FormatTwoDecimal(balance) + "元");
//
//        safeShowDialog(context, orderOptionDialog);
//        return orderOptionDialog;
//
//    }
//
    /**
     * @param stringRes 弹出简单提示语
     * @param listener
     * @return
     */
    public Dialog showtips(Context context, int stringRes, final MyDialogEnterListener listener) {
        String tip = context.getResources().getString(stringRes);
        return showtips(context, tip, listener);
    }

    /**
     * @param strTip   弹出简单提示语
     * @param listener
     * @return
     */
    public Dialog showtips(Context context, String strTip, final MyDialogEnterListener listener) {

        final Dialog tipsDialog = new Dialog(context, R.style.MyDialog);
        tipsDialog.setContentView(R.layout.dialog_show_singer_tip);
        tipsDialog.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipsDialog != null)
                    tipsDialog.dismiss();
                if (listener != null)
                    listener.onClickEnter(tipsDialog, null);

            }
        });
        LinearLayout warp = (LinearLayout) tipsDialog.findViewById(R.id.parentWarp);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Tools.getDisplayWidth((Activity) context),
                context.getResources().getDimensionPixelOffset(R.dimen.dialog_with));
        warp.setLayoutParams(lp);
        tipsDialog.setCanceledOnTouchOutside(true);
        tipsDialog.setCancelable(true);

        ((TextView) tipsDialog.findViewById(R.id.txtTip)).setText(strTip);

        safeShowDialog(context, tipsDialog);

        return tipsDialog;
    }

    Dialog mWebViewDialog;



    /**
     * @param dialog 安全方式showDialog
     */
    private void safeShowDialog(Context context, Dialog dialog) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (context != null && !((Activity) context).isFinishing() && dialog != null && !dialog.isShowing() && !(((Activity) context).isDestroyed()))
                    dialog.show();
            } else {
                if (context != null && !((Activity) context).isFinishing() && dialog != null && !dialog.isShowing())
                    dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

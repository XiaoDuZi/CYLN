package com.guanghua.ln.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.guanghua.ln.R;


/**
 * Created by Administrator on 2017/7/12 0012.
 */

public class CyDialogFragment extends DialogFragment {

    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static CyDialogFragment newInstance(int num) {
        CyDialogFragment f = new CyDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.ActionSheetDialogStyle);
//        setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

//        // Pick a style based on the num.
//        int style = DialogFragment.STYLE_NORMAL, theme = 0;
//        switch ((mNum-1)%6) {
//            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
//            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
//            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
//            case 4: style = DialogFragment.STYLE_NORMAL; break;
//            case 5: style = DialogFragment.STYLE_NORMAL; break;
//            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
//            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
//            case 8: style = DialogFragment.STYLE_NORMAL; break;
//        }
//        switch ((mNum-1)%6) {
//            case 4: theme = android.R.style.Theme_Holo; break;
//            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
//            case 6: theme = android.R.style.Theme_Holo_Light; break;
//            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
//            case 8: theme = android.R.style.Theme_Holo_Light; break;
//        }
//        setStyle(style, theme);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
        View view=inflater.inflate(R.layout.dialog_exit,container,false);
        Button btExit= (Button) view.findViewById(R.id.bt_app_exit);
        Button btContinue= (Button) view.findViewById(R.id.bt_app_continue);

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
//                https://whitelaning.github.io/2015/05/07/Clear_the_WebView_Cache.html
            }
        });

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        return view;
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//        builder.setMessage("ddddd")
//                .setNegativeButton("aa", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        return builder.create();
//        Dialog dialog=super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        return dialog;

//        Dialog singeLeftRightTipDialog = new Dialog(getActivity(),R.style.MyDialog);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        builder.setView(inflater.inflate(R.layout.dialog_exit, null))
//                .setPositiveButton("再看看", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                })
//                .setNegativeButton("去意已决", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        CyDialogFragment.this.getDialog().cancel();
//                    }
//                });
//        return builder.create();
//    }




}

package com.example.asadabbas.iptvdemo.activities;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.adapter.StreamAdapter;
import com.example.asadabbas.iptvdemo.model.Favourites;
import com.example.asadabbas.iptvdemo.model.IPTV;
import com.example.asadabbas.iptvdemo.util.TinyDB;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class StreamFragment extends Fragment implements StreamAdapter.onItemClick {

    FloatingActionButton addButtonUp;
    RecyclerView stream_list;

    TextView placeHolder;

    public StreamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stream, container, false);
        addButtonUp = view.findViewById(R.id.fab);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert(false, null);
            }
        });
        stream_list = view.findViewById(R.id.stream_list);
        placeHolder = view.findViewById(R.id.place_holder);

        init();

        return view;
    }

    void alert(boolean isUpdate, IPTV iptv) {
        addButtonUp.hide();
        Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("IPTV");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialogue);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                addButtonUp.show();
            }
        });

        TextInputLayout til_name = dialog.findViewById(R.id.name);
        TextInputLayout til_url = dialog.findViewById(R.id.urlName);

        TextInputEditText tidnName = dialog.findViewById(R.id.tid_name);
        TextInputEditText tidUrl = dialog.findViewById(R.id.tid_url);

        if (isUpdate) {
            if (iptv != null) {
                tidnName.setText(iptv.getName());
                tidUrl.setText(iptv.getPath());
            }
        }

        FloatingActionButton addButton = dialog.findViewById(R.id.fab_add);
        if (isUpdate)
            addButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.database_check));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtWatcher(til_name, tidnName);
                txtWatcher(til_url, tidUrl);

                String name = tidnName.getText().toString();
                String url = tidUrl.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    til_name.setError(" ");
                    return;

                } else if (TextUtils.isEmpty(url)) {
                    til_url.setError(" ");
                    return;
                }

                if (isUpdate) {
                    update(iptv.getId(), name, url);
                } else {
                    IPTV iptv = new IPTV(name, url, false, "stream");
                    iptv.save();
                }

                showIPTV();

                addButtonUp.show();

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    void showIPTV() {
        List<IPTV> dbIptv = getData();
        if (dbIptv.size() != 0) {
            StreamAdapter streamAdapter = new StreamAdapter(getActivity(), dbIptv);
            stream_list.setAdapter(streamAdapter);
            streamAdapter.setOnItemClick(this);
            placeHolder.setVisibility(View.INVISIBLE);
        } else {
            stream_list.setAdapter(null);
            placeHolder.setVisibility(View.VISIBLE);
        }
    }

    public List<IPTV> getData() {
        return new Select().from(IPTV.class).execute();
    }

    void txtWatcher(TextInputLayout til, TextInputEditText edt) {

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                til.setError(null);
            }
        });


    }

    private void init() {
        stream_list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        showIPTV();
    }

    @Override
    public void click(boolean isDelete, IPTV iptvData) {

        if (isDelete) {
            deleteDialo(iptvData.getPath());
            return;
        }

        alert(true, iptvData);
    }

    void update(long id, String name, String path) {
        IPTV iptv = IPTV.load(IPTV.class, id);
        iptv.setName(name);
        iptv.setPath(path);
        iptv.save();
    }

    void delete(String str) {
        new Delete().from(IPTV.class).where("Path=?", str).execute();
        new Delete().from(Favourites.class).where("Path=?", str).execute();
    }

    void deleteDialo(String str) {

        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setCancelText("No")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        delete(str);
                        showIPTV();
                    }
                })
                .show();

    }

}

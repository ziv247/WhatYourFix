package com.exmp.ziv24.mapsproject.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.exmp.ziv24.mapsproject.R;
import com.exmp.ziv24.mapsproject.db.Constants;
import com.exmp.ziv24.mapsproject.db.DBHandler;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingFragment extends Fragment {

    private Context mContext;
    private MainActivity mActivity;
    private int langPos;
    private String distancePos;
    private boolean infoFlag;
    private String version;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @BindView(R.id.language_spinner)
    Spinner language_spinner;
    @BindView(R.id.distance_spinner)
    Spinner distance_spinner;
    @BindView(R.id.tv_restore)
    TextView tv_restore;
    @BindView(R.id.tv_restore_message)
    TextView tv_restore_message;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;
    @BindView(R.id.btn_restore)
    Button btn_restore;
    @BindView(R.id.imageButtonRestore)
    ImageButton imageButtonRestore;
    @BindView(R.id.imageButtonInfo)
    ImageButton imageButtonInfo;
    @BindView(R.id.cardViewInfo)
    CardView cardViewInfo;
    @BindView(R.id.cardViewRestore)
    CardView cardViewRestore;
    @BindView(R.id.textView_auth_name)
    TextView textView_auth_name;
    @BindView(R.id.textView_ver)
    TextView textView_ver;
    @BindView(R.id.textView_ver_edit)
    TextView textView_ver_edit;
    @BindView(R.id.textView_author)
    TextView textView_author;
    @BindView(R.id.btn_linkedin)
    ImageButton btn_linkedin;
    @BindView(R.id.btn_email)
    ImageButton btn_email;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        preferences = mContext.getSharedPreferences(Constants.PREF_SETTING, Context.MODE_PRIVATE);
        editor = preferences.edit();
        langPos = preferences.getInt(Constants.LANG_POS, 0);
        distancePos = preferences.getString(Constants.DISTANCE_POS, Constants.KM);
        infoFlag = false;

        initLanguageSpinner();
        initDistanceSpinner();

        cardViewRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreClick();
            }
        });
        cardViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoClick();
            }
        });
        return view;
    }

    private void infoClick() {
        if (!infoFlag) {
            textView_auth_name.setVisibility(View.VISIBLE);
            textView_author.setVisibility(View.VISIBLE);
            textView_ver.setVisibility(View.VISIBLE);
            textView_ver_edit.setVisibility(View.VISIBLE);
            textView_ver_edit.setText(getVer());
            btn_email.setVisibility(View.VISIBLE);
            btn_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail();
                }
            });
            btn_linkedin.setVisibility(View.VISIBLE);
            btn_linkedin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/ziv-abramovich-36b5b6163"));
                    startActivity(openWeb);
                }
            });
            imageButtonInfo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_up));
            infoFlag = true;
        } else {
            textView_auth_name.setVisibility(View.GONE);
            textView_author.setVisibility(View.GONE);
            textView_ver.setVisibility(View.GONE);
            textView_ver_edit.setVisibility(View.GONE);
            btn_email.setVisibility(View.GONE);
            btn_linkedin.setVisibility(View.GONE);
            imageButtonInfo.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_down));
            infoFlag = false;
        }
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "ziv2471993@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void restoreClick() {
        if (imageButtonRestore.getVisibility() != View.GONE) {
            tv_restore.setTextSize(12.0f);
            imageButtonRestore.setVisibility(View.GONE);
            tv_restore_message.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restoreClick();
                }
            });
            btn_restore.setVisibility(View.VISIBLE);
            btn_restore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBHandler handler = new DBHandler(mContext);
                    if (handler.deleteAllPlaces()) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.deleted_all), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.deleted_all_failed), Toast.LENGTH_SHORT).show();
                    }
                    restoreClick();
                }
            });
        } else {
            tv_restore.setTextSize(18.0f);
            imageButtonRestore.setVisibility(View.VISIBLE);
            tv_restore_message.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);
            btn_restore.setVisibility(View.GONE);
        }
    }

    private void initDistanceSpinner() {
        List<String> disList = new ArrayList<>();
        disList.add(mContext.getResources().getString(R.string.km));
        disList.add(mContext.getResources().getString(R.string.miles));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, disList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distance_spinner.setAdapter(adapter);
        if (distancePos.equals(Constants.KM)) {
            distance_spinner.setSelection(0);
        } else if (distancePos.equals(Constants.MILE)) {
            distance_spinner.setSelection(1);
        }
        distance_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editor.putString(Constants.DISTANCE_POS, Constants.KM);
                    editor.apply();
                } else if (position == 1) {
                    editor.putString(Constants.DISTANCE_POS, Constants.MILE);
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initLanguageSpinner() {
        List<String> langList = new ArrayList<>();
        langList.add(mContext.getResources().getString(R.string.english));
        langList.add(mContext.getResources().getString(R.string.hebrew));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, langList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_spinner.setAdapter(adapter);
        language_spinner.setSelection(langPos);
        language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    mActivity.setLocale("en");
                    if (langPos != 0) {
                        editor.putInt(Constants.LANG_POS, position);
                        editor.apply();
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.finish();
                        mContext.startActivity(intent);
                    }

                } else if (position == 1) {
                    mActivity.setLocale("iw");
                    if (langPos != 1) {
                        editor.putInt(Constants.LANG_POS, position);
                        editor.apply();
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.recreate();
                        mContext.startActivity(intent);
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (MainActivity) context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String getVer() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}

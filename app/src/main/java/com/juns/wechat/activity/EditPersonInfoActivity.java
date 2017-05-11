package com.juns.wechat.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.same.city.love.R;
import com.style.base.BaseToolbarActivity;
import com.style.dialog.EditAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * Created by xiajun on 2017/5/9.
 */

public class EditPersonInfoActivity extends BaseToolbarActivity {


    @Bind(R.id.layout_base_info)
    LinearLayout layoutBaseInfo;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_age)
    TextView tvAge;
    @Bind(R.id.tv_constellation)
    TextView tvConstellation;
    @Bind(R.id.tv_industry)
    TextView tvIndustry;
    @Bind(R.id.layout_industry)
    LinearLayout layoutIndustry;
    @Bind(R.id.tv_work_area)
    TextView tvWorkArea;
    @Bind(R.id.layout_work_area)
    LinearLayout layoutWorkArea;
    @Bind(R.id.tv_company_info)
    TextView tvCompanyInfo;
    @Bind(R.id.layout_company_info)
    LinearLayout layoutCompanyInfo;
    @Bind(R.id.tv_hometown_info)
    TextView tvHometownInfo;
    @Bind(R.id.layout_hometown_info)
    LinearLayout layoutHometownInfo;
    @Bind(R.id.tv_my_heart)
    TextView tvMyHeart;
    @Bind(R.id.layout_my_heart)
    LinearLayout layoutMyHeart;
    @Bind(R.id.tv_my_label)
    TextView tvMyLabel;
    @Bind(R.id.layout_content_my_label)
    RelativeLayout layoutContentMyLabel;
    @Bind(R.id.layout_my_label)
    LinearLayout layoutMyLabel;
    @Bind(R.id.tv_interest_sport)
    TextView tvInterestSport;
    @Bind(R.id.layout_content_interest_sport)
    RelativeLayout layoutContentInterestSport;
    @Bind(R.id.layout_interest_sport)
    LinearLayout layoutInterestSport;
    @Bind(R.id.tv_interest_music)
    TextView tvInterestMusic;
    @Bind(R.id.layout_content_interest_music)
    RelativeLayout layoutContentInterestMusic;
    @Bind(R.id.layout_interest_music)
    LinearLayout layoutInterestMusic;
    @Bind(R.id.tv_interest_food)
    TextView tvInterestFood;
    @Bind(R.id.layout_content_interest_food)
    RelativeLayout layoutContentInterestFood;
    @Bind(R.id.layout_interest_food)
    LinearLayout layoutInterestFood;
    @Bind(R.id.tv_interest_movie)
    TextView tvInterestMovie;
    @Bind(R.id.layout_content_interest_movie)
    RelativeLayout layoutContentInterestMovie;
    @Bind(R.id.layout_interest_movie)
    LinearLayout layoutInterestMovie;
    @Bind(R.id.tv_question)
    TextView tvQuestion;
    @Bind(R.id.layout_content_question)
    RelativeLayout layoutContentQuestion;
    @Bind(R.id.layout_question)
    LinearLayout layoutQuestion;
    @Bind(R.id.tv_emotion)
    TextView tvEmotion;
    @Bind(R.id.layout_emotion)
    LinearLayout layoutEmotion;
    @Bind(R.id.tv_education)
    TextView tvEducation;
    @Bind(R.id.layout_education)
    LinearLayout layoutEducation;

    public int p = 0;
    @Bind(R.id.tag_view_my_label)
    TagView tagViewMyLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayoutResID = R.layout.activity_edit_userinfo;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        setToolbarTitle("编辑个人资料");

    }

    @OnClick(R.id.layout_base_info)
    public void modifyBaseInfo() {
        skip(EditBaseInfoActivity.class);
    }

    @OnClick(R.id.layout_emotion)
    public void modifyInfo3() {
        openSingleSelect(getResources().getStringArray(R.array.emotion), tvEmotion);
    }

    @OnClick(R.id.layout_education)
    public void modifyInfo4() {
        openSingleSelect(getResources().getStringArray(R.array.education), tvEducation);
    }

    @OnClick(R.id.layout_industry)
    public void modifyInfo() {
        openSingleSelect(getResources().getStringArray(R.array.industry), tvIndustry);
    }

    @OnClick(R.id.layout_work_area)
    public void modifyInfo2() {
        openSingleSelect(getResources().getStringArray(R.array.occupations), tvWorkArea);
    }

    private void openSingleSelect(final String[] strings, final TextView textView) {
        int checkedItem = 0;
        String value = textView.getText().toString();
        if (!TextUtils.isEmpty(value))
            for (int i = 0; i < strings.length; i++) {
                if (value.endsWith(strings[i])) {
                    checkedItem = i;
                    break;
                }
            }
        AlertDialog singleDialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(strings, checkedItem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        p = which;
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textView.setText(strings[p]);
                    }
                })
                .setNeutralButton("清空", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textView.setText("");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        singleDialog.show();
    }

    @OnClick(R.id.layout_company_info)
    public void modifyInfo5() {
        openEdit(tvCompanyInfo);
    }

    @OnClick(R.id.layout_hometown_info)
    public void modifyInfo6() {
        openEdit(tvHometownInfo);
    }

    @OnClick(R.id.layout_my_heart)
    public void modifyInfo7() {
        openEdit(tvMyHeart);
    }

    private void openEdit(final TextView textView) {
        String name = textView.getText().toString();
        final EditAlertDialog editAlertDialog = new EditAlertDialog(this);
        editAlertDialog.setData(name, textView.getHint().toString());
        editAlertDialog.show(new EditAlertDialog.OnPromptListener() {
            @Override
            public void onPositiveButton(String s) {
                setText(textView, s);
                editAlertDialog.dismiss();
            }

            @Override
            public void onNegativeButton() {
                editAlertDialog.dismiss();
            }
        });

    }

    @OnClick(R.id.layout_my_label)
    public void modifyInfo11() {
        openMulti("标签", getResources().getStringArray(R.array.my_label), tagViewMyLabel, R.color.tag_my_label, R.color.tag_my_label_bg, tvMyLabel);
    }
    @OnClick(R.id.layout_interest_sport)
    public void modifyInfo12() {
        openMulti("运动", getResources().getStringArray(R.array.sport), tagViewMyLabel, R.color.tag_sport, R.color.tag_sport_bg, tvInterestSport);
    }
    @OnClick(R.id.layout_interest_music)
    public void modifyInfo13() {
        openMulti("音乐", getResources().getStringArray(R.array.music), tagViewMyLabel, R.color.tag_music, R.color.tag_music_bg, tvInterestMusic);
    }
    @OnClick(R.id.layout_interest_food)
    public void modifyInfo14() {
        openMulti("美食", getResources().getStringArray(R.array.food), tagViewMyLabel, R.color.tag_food, R.color.tag_food_bg, tvInterestFood);
    }
    @OnClick(R.id.layout_interest_movie)
    public void modifyInfo15() {
        openMulti("电影", getResources().getStringArray(R.array.movie), tagViewMyLabel, R.color.tag_movie, R.color.tag_movie_bg, tvInterestMovie);
    }
    private void openMulti(final String title, final String[] allData, final TagView tagView, final int tagColor, final int tagColorBg, final TextView textView) {
        final boolean[] checkedItems = new boolean[allData.length];
        List<Tag> oldData = tagView.getTags();

        for (int i = 0; i < allData.length; i++) {
            for (Tag tag : oldData) {
                if (allData[i].equals(tag.text)) {
                    checkedItems[i] = true;
                    break;
                }
            }
        }

        AlertDialog multiDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMultiChoiceItems(allData, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<Tag> newData = new ArrayList<Tag>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                Tag tag = new Tag(allData[i]);
                                tag.radius = 10f;
                                tag.tagTextColor = tagColor;
                                tag.layoutColor = tagColorBg;
                                tag.layoutColorPress = tagColorBg;
                                newData.add(tag);
                            }
                        }
                        tagView.removeAllTags();
                        if (newData.size()>0) {
                            textView.setVisibility(View.GONE);
                            tagView.setVisibility(View.VISIBLE);
                            tagView.addTags(newData);
                        } else{
                            tagView.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                        }
                        tagView.setFocusable(false);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        multiDialog.show();
    }
}

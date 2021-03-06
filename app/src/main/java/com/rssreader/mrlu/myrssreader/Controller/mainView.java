package com.rssreader.mrlu.myrssreader.Controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.MAppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.rssreader.mrlu.myrssreader.Controller.CustomView.NoScrollViewPager;
import com.rssreader.mrlu.myrssreader.Controller.fragment.starredFragment;
import com.rssreader.mrlu.myrssreader.R;
import com.rssreader.mrlu.myrssreader.Controller.fragment.unReadFragment;

import java.util.ArrayList;
import java.util.List;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;

import static com.rssreader.mrlu.myrssreader.R.color.appBaseColor;
import static com.rssreader.mrlu.myrssreader.R.color.md_teal_a700_color_code;

public class mainView extends MAppCompatActivity implements View.OnClickListener {

    //声明ViewPager
    private ViewPager mNoScrollViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;
    //四个Tab对应的布局
    private LinearLayout mTabUnread;
    private LinearLayout mTabStarred;

    String rssUrl;

    //0为day，1为night
    public static int Swith_Mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //取消actionBar
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);

        super.onCreate(savedInstanceState);

        StatusBarUtil.setColor(this, getResources().getColor(appBaseColor), 0);

        setContentView(R.layout.main);

        mNoScrollViewPager = (NoScrollViewPager) findViewById(R.id.id_noviewpager);
        ImageView nightSwitch = (ImageView) findViewById(R.id.iv_night_swith);
        ImageView ivUpdate = (ImageView) findViewById(R.id.iv_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_feedList);


        //设置自定义的view为ActionBar
        setSupportActionBar(toolbar);

        //主页刷新按钮的点击事件
        ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        nightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //点击切换日间/夜间图标
                    switch (Swith_Mode) {
                        case 0:
                            Swith_Mode = 1;
                            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            recreate();
                            Toast.makeText(mainView.this, "已切换为夜间模式", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Swith_Mode = 0;
                            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            recreate();

                            Toast.makeText(mainView.this, "已切换为日间模式", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception e) {
                    Log.i("切换日夜间部分", e.getMessage());
                }
            }
        });


        //处理toolbar的menu的点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent();
                switch (item.getOrder()) {
                    case 1:
                        intent.setClass(mainView.this, SettingsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                        break;
                    case 2:
                        intent.setClass(mainView.this, AboutActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                        break;
                }

                return true;
            }
        });

        initView();//初始化控件
        initEvent();//初始化事件
        initData();//初始化数据
        selectTab(0);
    }

    private void initData() {
        mFragments = new ArrayList<>();
        //将两个Fragment加入集合中
        mFragments.add(new unReadFragment());
        mFragments.add(new starredFragment());

        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }

        };

        //不要忘记设置ViewPager的适配器
        mNoScrollViewPager.setAdapter(mAdapter);
        //设置ViewPager的切换监听
        mNoScrollViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                //设置position对应的集合中的Fragment
                mNoScrollViewPager.setCurrentItem(position);
                resetImgs();
                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEvent() {
        //设置两个Tab的点击事件
        mTabUnread.setOnClickListener(this);
        mTabStarred.setOnClickListener(this);
    }

    //初始化控件
    private void initView() {
        mNoScrollViewPager = (ViewPager) findViewById(R.id.id_noviewpager);

        mTabUnread = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabStarred = (LinearLayout) findViewById(R.id.id_tab_frd);
    }

    @Override
    public void onClick(View v) {
        //先将两个ImageButton置为灰色
        resetImgs();

        //根据点击的object不同来处理点击事件
        switch (v.getId()) {

            //两个tab
            case R.id.id_tab_weixin:
                selectTab(0);
                break;
            case R.id.id_tab_frd:
                selectTab(1);
                break;
        }
    }

    private void selectTab(int i) {
        //根据点击的Tab设置对应的ImageButton为绿色
        switch (i) {
            case 0:
                //mImgWeixin.setImageResource(R.drawable.feed_read);
                mTabStarred.setBackgroundColor(Color.parseColor("#393a3f"));
                mTabUnread.setBackgroundColor(getResources().getColor(md_teal_a700_color_code));
                break;
            case 1:
                //EventBus发送消息
//                EventBus.getDefault().post(rssUrl);
                //mImgFrd.setImageResource(R.drawable.long_press_starred);
                mTabUnread.setBackgroundColor(Color.parseColor("#393a3f"));
                mTabStarred.setBackgroundColor(getResources().getColor(md_teal_a700_color_code));
                break;
        }
        //设置当前点击的Tab所对应的页面
        mNoScrollViewPager.setCurrentItem(i);
    }

    //将两个ImageButton设置为灰色
    private void resetImgs() {
        mTabStarred.setBackgroundColor(Color.parseColor("#393a3f"));
        mTabStarred.setBackgroundColor(Color.parseColor("#393a3f"));

    }

    //创建Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rsslist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    //双击back健退出
    private int sum = 0;
    long startTime = 0;
    long endTime = 0;

    @Override
    public void onBackPressed() {

        sum++;
        Log.i("onBackPressed", "sum = " + sum);
        switch (sum) {
            case 1:
                Toast.makeText(mainView.this, "再按一次退出Reer", Toast.LENGTH_SHORT).show();
                startTime = System.currentTimeMillis();// 当前时间对应的毫秒数
                break;
            case 2:
                endTime = System.currentTimeMillis();// 当前时间对应的毫秒数
                if (endTime - startTime < 1500) {
                    finish();

                } else {
                    Toast.makeText(mainView.this, "再按一次退出Reer", Toast.LENGTH_SHORT).show();
                    startTime = System.currentTimeMillis();// 当前时间对应的毫秒数
                    sum = 1;
                }
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        JAnalyticsInterface.onPageStart(this, this.getClass().getCanonicalName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JAnalyticsInterface.onPageEnd(this, this.getClass().getCanonicalName());
    }
}



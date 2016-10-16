package com.itheima.leon.qqdemo.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.leon.qqdemo.model.User;
import com.itheima.leon.qqdemo.presenter.RegisterPresenter;
import com.itheima.leon.qqdemo.utils.StringUtils;
import com.itheima.leon.qqdemo.utils.ThreadUtils;
import com.itheima.leon.qqdemo.view.RegisterView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 创建者:   Leon
 * 创建时间:  2016/10/16 22:21
 * 描述：    TODO
 */
public class RegisterPresenterImpl implements RegisterPresenter {
    public static final String TAG = "RegisterPresenterImpl";

    public RegisterView mRegisterView;

    public RegisterPresenterImpl(RegisterView registerView) {
        mRegisterView = registerView;
    }

    @Override
    public void register(String userName, String pwd, String pwdConfirm) {
        if (StringUtils.checkUserName(userName)) {
            if (StringUtils.checkPassword(pwd)) {
                if (pwd.equals(pwdConfirm)) {
                    mRegisterView.onStartRegister();
                    startRegister(userName, pwd);
                } else {
                    mRegisterView.onConfirmPasswordError();
                }
            } else {
                mRegisterView.onPasswordError();
            }
        } else {
            mRegisterView.onUserNameError();
        }

    }

    private void startRegister(final String userName, final String pwd) {
        User user = new User(userName, pwd);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    mRegisterView.onRegisterError();
                } else {
                    ThreadUtils.runOnBackgroundThread(new RegisterEMTask(userName, pwd));
                }
            }
        });
    }

    private class RegisterEMTask implements Runnable {

        private String mUserName;
        private String mPassword;

        public RegisterEMTask(String userName, String pwd) {
            mUserName = userName;
            mPassword = pwd;
        }

        @Override
        public void run() {
            try {
                EMClient.getInstance().createAccount(mUserName, mPassword);
                mRegisterView.onRegisterSuccess();
            } catch (HyphenateException e) {
                e.printStackTrace();
                mRegisterView.onRegisterError();
            }
        }
    }
}
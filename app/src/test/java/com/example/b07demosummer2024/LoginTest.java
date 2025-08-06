package com.example.b07demosummer2024;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import android.os.Build;

import com.google.firebase.auth.FirebaseUser;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)

public class LoginTest {

    @Mock
    LoginActivity view;
    @Mock
    LoginModel model;
    // note i need roboelectric since the login model uses an email checker that is from an sdk
    @Before
    public void SetupMock(){
        MockitoAnnotations.openMocks(this);
    }
    //pass in a specific format of email and password that fails a check and verify that the specific error message is ran
    //for most of the test methods below
    @Test
    public void TestNoEmail() {

        doNothing().when(view).showError("Please enter email");
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin("", "asdf");
        verify(view, times(1)).showError("Please enter email");
    }
    @Test
    public void TestNullEmail() {
        doNothing().when(view).showError("Please enter email");
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin(null, "asdf");
        verify(view, times(1)).showError("Please enter email");
    }
    @Test
    public void TestBadEmail() {
        doNothing().when(view).showError("Please enter a valid email");
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin("silly goose", "asdf");
        verify(view, times(1)).showError("Please enter a valid email");
    }
    @Test
    public void TestEmptyPassword() {
        doNothing().when(view).showError("Please enter password");
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin("goose@gmail.com", "");
        verify(view, times(1)).showError("Please enter password");
    }
    @Test
    public void TestNullPassword() {
        doNothing().when(view).showError("Please enter password");
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin("goose@gmail.com", null);
        verify(view, times(1)).showError("Please enter password");
    }
    @Test
    public void TestShortPassword() {
        doNothing().when(view).showError("Password must be 6 or more characters");
        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin("goose@gmail.com", "123");
        verify(view, times(1)).showError("Password must be 6 or more characters");
    }
    //pass a password that and email that works, then mock the model saying the login was successful
    @Test
    public void TestGoodLogin() {
        doNothing().when(view).onLoginSuccess();
        doNothing().when(view).hideError();
        doAnswer( invocation -> {
        FirebaseUser goose = mock(FirebaseUser.class);
            LoginModel.LoginCallback callback = invocation.getArgument(2);
            callback.onLoginSuccess(goose);

            return null;
                }).when(model).loginUser(eq("goose@gmail.com"), eq("123567"),any(LoginModel.LoginCallback.class) );


        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin("goose@gmail.com", "123567");
        verify(view, times(1)).hideError();
       verify(view, times(1)).onLoginSuccess();

    }
    // same as above but call back calls failed
    @Test
    public void TestBadLogin() {
        doNothing().when(view).onLoginSuccess();
        doNothing().when(view).hideError();
        doAnswer( invocation -> {
            LoginModel.LoginCallback callback = invocation.getArgument(2);
            callback.onLoginFailure("Login failed :( :( :(");

            return null;
        }).when(model).loginUser(eq("goose@gmail.com"), eq("123567"),any(LoginModel.LoginCallback.class) );


        LoginPresenter presenter = new LoginPresenter(view, model);
        presenter.validateAndLogin("goose@gmail.com", "123567");
        verify(view, times(1)).hideError();
        verify(view, times(1)).onLoginFailure("Login failed :( :( :(");

    }

}
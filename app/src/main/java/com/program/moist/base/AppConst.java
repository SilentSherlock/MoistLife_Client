package com.program.moist.base;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: App中用到的常量
 */
public class AppConst {

    public static final String divide = "/";
    public static final String before = "before";
    public static final String browse = "browse";

    public interface Server {
        String server_address = "http://192.168.31.38:8080" + divide;
        String file_address = "";
    }

    public interface Base {
        String login_token = "login_token";

        String number_format = "0?(13|14|15|18)[0-9]{9}";
        String email_format = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        String identify_number_format = "\\d{17}[\\d|x]|\\d{15}";

        String format_wrong = "格式错误";
        String number = "手机号";
        String email = "Email";
        String identify_number = "身份证号";
    }

    public interface Info {

    }

    public interface User {
        String user = "user";
        String pref = Server.server_address + user + divide + before + divide;
        String pref_l = Server.server_address + user + divide;

        String check = pref + "check";
        String login = pref + "login";
        String register = pref + "register";
        String getUserById = pref + "getUserById";
        String accountValidate = pref + "accountValidateCode";

        String addFollow = pref_l + "addFollow";
        String deleteFollow = pref_l + "deleteFollow";
        String getFollowers = pref_l + "getFollowers";
        String getFollowing = pref_l + "getFollowing";
        String addAvatar = pref_l + "addAvatar";
        String getUserInfo = pref_l + "getUserInfo";
    }
}

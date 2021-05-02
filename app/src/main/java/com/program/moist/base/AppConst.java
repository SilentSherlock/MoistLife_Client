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
    public static final String TAG = "MoistLife";

    public interface Server {
        String server_address = "http://192.168.31.38:8080" + divide;
        String FTP_IP = "139.196.243.30";
        int FTP_PORT = 21;
        String FTP_USER = "sherlock";
        String FTP_PASS = "FTP_sherlock";
        String FTP_DIR = "ftp://" + FTP_IP + "/data/";
        String FTP_TEST = "data/5/avatar/5.png";
    }

    public interface Base {
        String login_token = "login_token";
        String category = "category";
        Integer category_num = 15;

        String number_format = "0?(13|14|15|18)[0-9]{9}";
        String email_format = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        String identify_number_format = "\\d{17}[\\d|x]|\\d{15}";

        String format_wrong = "格式错误";
        String number = "手机号";
        String email = "Email";
        String identify_number = "身份证号";
        String recommend = "推荐";
        String follow = "关注";
        Integer type_number = 1;
        Integer type_email = 0;

        String image_path = "image_path";
        String image_type = "image_type";
        Integer background = 1;
        Integer avatar = 0;
    }

    public interface Info {
        String info = "info";
        String pref = Server.server_address + info + divide + browse + divide;
        String pref_l = Server.server_address + info + divide;

        String getDefaultInfo = pref + "getDefaultInfo";
        String getAllCate = pref + "getAllCate";
        String getInfoByCate = pref + "getInfoByCate";
        String getInfoByArea = pref + "getInfoByArea";

        String getInfoByPage = pref_l + "getInfoByPage";
        String addInfo = pref_l + "addInfo";
        String updateInfo = pref_l + "updateInfo";
        String deleteInfo = pref_l + "deleteInfo";
        String addUserFavInfo = pref_l + "addUserFavInfo";
        String deleteUserFavInfo = pref_l + "deleteUserFavInfo";
        String getUserFavInfo = pref_l + "getUserFavInfo";
        String uploadInfoImage = pref_l + "uploadInfoImage";

    }

    public interface User {
        String user = "user";
        String user_avatar = "user_avatar";
        String user_background = "user_background";
        String pref = Server.server_address + user + divide + before + divide;
        String pref_l = Server.server_address + user + divide;
        String user_avatar_path = App.context.getFilesDir().getPath() + divide + "imagePath/avatar/";
        String user_background_path = App.context.getFilesDir().getPath() + divide + "imagePath/background/";

        String check = pref + "check";
        String login = pref + "login";
        String register = pref + "register";
        String getUserById = pref + "getUserById";
        String accountValidate = pref + "accountValidateCode";

        String addFollow = pref_l + "addFollow";
        String deleteFollow = pref_l + "deleteFollow";
        String getFollowers = pref_l + "getFollowers";
        String getFollowing = pref_l + "getFollowing";
        String addImage = pref_l + "addImage";
        String getUserInfo = pref_l + "getUserInfo";
    }
}

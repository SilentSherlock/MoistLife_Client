package com.program.moist.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Author: SilentSherlock
 * Date: 2021/4/18
 * Description: App中用到的常量
 */
public class AppConst {

    /**
     * 获得格式化的日期字符串
     * @param date 参数为null 返回当前日期的格式化
     * @return
     */
    public static String getFormatDate(Date date) {
        if (date == null) date = new Date();
        return new SimpleDateFormat(date_format, Locale.CHINA).format(date);
    }
    //以字符串返回指定范围的整数
    public static String getRandomNumber(Integer bound) {
        if (bound == null) bound = 32;
        return String.valueOf(new Random().nextInt(bound));
    }
    public static final String divide = "/";
    public static final String divide_2 = "Ψ";
    public static final String before = "before";
    public static final String browse = "browse";
    public static final String TAG = "MoistLife";
    public static final Integer port = 27149;
    public static final String date_format = "yyyy-MM-dd HH:mm:ss";

    public interface Server {
        String server_address = "http://192.168.31.38:8080" + divide;
        String oss_address = "https://moist-life.oss-cn-shanghai.aliyuncs.com" + divide;
        String FTP_IP = "139.196.243.30";
        int FTP_PORT = 21;
        String FTP_USER = "sherlock";
        String FTP_PASS = "FTP_sherlock";
        String FTP_DIR = "ftp://" + FTP_IP + "/data/";
        String FTP_TEST = "data/5/avatar/5.png";
    }

    public interface Base {
        String login_token = "login_token";
        String sts_token = "sts_token";
        String category = "category";
        String default_info = "default_info";
        String infos = "INFOS";
        Integer category_num = 15;
        String chat_dir = App.context.getFilesDir().getPath() + divide + "chatLogs/";

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
        String key_word = "今晚打老虎";
        String not_key_word = "今晚不打老虎";
        String kind = "kind";
        Integer kind_info = 0;
        Integer kind_fav = 1;
        Integer kind_post = 2;
        Integer kind_follow = 3;
    }

    public interface Info {
        String info = "info";
        String category = "category";
        String pref = Server.server_address + info + divide + browse + divide;
        String pref_l = Server.server_address + info + divide;
        String ongoing = "ongoing";
        String finish = "finish";

        String getDefaultInfo = pref + "getDefaultInfo";
        String getAllCate = pref + "getAllCate";
        String getInfoByCate = pref + "getInfoByCate";
        String getInfoByArea = pref + "getInfoByArea";
        String getInfoByUserId = pref + "getInfoByUserId";
        String getCateById = pref + "getCateById";
        String getInfoById = pref + "getInfoById";
        String getChildCate = pref + "getChildCate";
        String getStatusCountByCateId = pref + "getStatusCountByCateId";

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
        String users = "users";
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
        String getUserByPage = pref + "getUserByPage";

        String addFollow = pref_l + "addFollow";
        String deleteFollow = pref_l + "deleteFollow";
        String getFollowers = pref_l + "getFollowers";
        String getFollowing = pref_l + "getFollowing";
        String addImage = pref_l + "addImage";
        String getUserInfo = pref_l + "getUserInfo";
        String getStsToken = pref_l + "getStsToken";
        String updateUser = pref_l + "updateUser";
        String updateUserColumn = pref_l + "updateUserColumn";
    }

    public interface Post {
        String post = "post";
        String posts = "posts";
        String comments = "comments";
        String community = "community";
        String DEFAULT_POST = "default_post";
        String pref = Server.server_address + community + divide + browse + divide;
        String pref_l = Server.server_address + community + divide;

        String defaultPost = pref + "defaultPost";
        String getPostByUserId = pref + "getPostByUserId";
        String getCommentByPostId = pref + "getCommentByPostId";

        String getPost = pref_l + "getPost";//登录之后的推荐
        String getFollowPost = pref_l + "getFollowPost";
        String addPost = pref_l + "addPost";
        String deletePost = pref_l + "deletePost";
        String addThumbUpPost = pref_l + "addThumbUpPost";
        String deleteThumbUpPost = pref_l + "deleteThumbUpPost";
        String addComment = pref_l + "addComment";
        String deleteComment = pref_l + "deleteComment";
        String addThumbUpComment = pref_l + "addThumbUpComment";
        String addTopicSub = pref_l + "addTopicSub";
        String deleteTopicSub = pref_l + "deleteTopicSub";

    }
    public interface Search {
        String search = "search";
        String pref = Server.server_address + search + divide;

        String searchInfoPage = pref + "searchInfoPage";
    }
}

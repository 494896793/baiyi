package www.qisu666.com.bean;

/**
 * 717219917@qq.com 2018/7/15 17:12.
 */
public class EventMsg {


    private String msg;
    //1 退出 2 登陆
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

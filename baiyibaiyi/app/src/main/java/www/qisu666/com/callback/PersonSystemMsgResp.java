package www.qisu666.com.callback;

import java.util.List;

/**
 * Created by wujiancheng on 2017/7/30.
 * 个人系统消息
 */

public class PersonSystemMsgResp {
    private List<Datas> datas;

    public List<Datas> getDatas() {
        return datas;
    }

    public void setDatas(List<Datas> datas) {
        this.datas = datas;
    }

    public static class Datas {
        private String id;
        private int customerId;//":11
        private String content;//":"您实名认证审核不通过，请确认正确填写信息。如有疑问，请联系客服0775-2661393。",
        private String title;//":"审核不通过",
        private String category;//":"member"
        private String createTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCustomerId() {
            return customerId;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}

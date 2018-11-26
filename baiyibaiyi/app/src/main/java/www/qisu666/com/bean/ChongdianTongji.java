package www.qisu666.com.bean;

import java.util.List;

/**
 * 717219917@qq.com 2018/7/14 17:19.
 */
public class ChongdianTongji {


    /**
     * msg : 查询成功
     * code : 1025
     * data : {"myOrderList":[{"chargeElectricity":0,"CT":3,"ROWNO":1,"chargeTimes":3,"chargeTotal":"0.00","chargeMonth":"201807","SB":"201807"},{"chargeElectricity":0,"CT":2,"ROWNO":2,"chargeTimes":2,"chargeTotal":"0.00","chargeMonth":"201805","SB":"201805"},{"chargeElectricity":5,"CT":2,"ROWNO":3,"chargeTimes":2,"chargeTotal":"0.05","chargeMonth":"201804","SB":"201804"}]}
     */



        private List<MyOrderList> myOrderList;

        public List<MyOrderList> getMyOrderList() {
            return myOrderList;
        }

        public void setMyOrderList(List<MyOrderList> myOrderList) {
            this.myOrderList = myOrderList;
        }

        public static class MyOrderList {
            /**
             * chargeElectricity : 0
             * CT : 3
             * ROWNO : 1
             * chargeTimes : 3
             * chargeTotal : 0.00
             * chargeMonth : 201807
             * SB : 201807
             */

            private int chargeElectricity;
            private int CT;
            private int ROWNO;
            private int chargeTimes;
            private String chargeTotal;
            private String chargeMonth;
            private String SB;

            public int getChargeElectricity() {
                return chargeElectricity;
            }

            public void setChargeElectricity(int chargeElectricity) {
                this.chargeElectricity = chargeElectricity;
            }

            public int getCT() {
                return CT;
            }

            public void setCT(int CT) {
                this.CT = CT;
            }

            public int getROWNO() {
                return ROWNO;
            }

            public void setROWNO(int ROWNO) {
                this.ROWNO = ROWNO;
            }

            public int getChargeTimes() {
                return chargeTimes;
            }

            public void setChargeTimes(int chargeTimes) {
                this.chargeTimes = chargeTimes;
            }

            public String getChargeTotal() {
                return chargeTotal;
            }

            public void setChargeTotal(String chargeTotal) {
                this.chargeTotal = chargeTotal;
            }

            public String getChargeMonth() {
                return chargeMonth;
            }

            public void setChargeMonth(String chargeMonth) {
                this.chargeMonth = chargeMonth;
            }

            public String getSB() {
                return SB;
            }

            public void setSB(String SB) {
                this.SB = SB;
            }
        }
    }


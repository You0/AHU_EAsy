package krelve.app.Easy.bean;

/**
 * Created by 11092 on 2016/2/18.
 */
public class ConsumptionBean {
    private String JnDateTime;//交易时间
    private String TranName;//交易名称
    private String TranAmt;//交易金额
    private String AccAmt;//交易后余额
    private String MercName;//交易名称

    public void setJnDateTime(String jnDateTime) {
        JnDateTime = jnDateTime;
    }

    public void setTranName(String tranName) {
        TranName = tranName;
    }

    public void setMercName(String mercName) {
        MercName = mercName;
    }

    public String getMercName() {
        return MercName;
    }

    public void setTranAmt(String tranAmt) {
        TranAmt = tranAmt;
    }

    public void setAccAmt(String accAmt) {
        AccAmt = accAmt;
    }

    public String getJnDateTime() {
        return JnDateTime;
    }

    public String getTranName() {
        return TranName;
    }

    public String getTranAmt() {
        return TranAmt;
    }

    public String getAccAmt() {
        return AccAmt;
    }
}

package www.qisu666.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import www.qisu666.com.utils.SBUtils;

public class PReceiver extends BroadcastReceiver {

    private IDoReceiver mReceiver;
    private String action;

    public PReceiver(String action) {
        this.action = action;
        SBUtils.register(this);
    }

    public PReceiver(Context c, String action) {
        this.action = action;
        SBUtils.register(c, this);
    }

    public String getAction() {
        return action;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(action)) {
            if (mReceiver != null) {
                mReceiver.doReceiver(intent.getIntExtra(SBUtils.MAPKEY, 0));
            }
        }
    }

    public void setReceive(IDoReceiver mReceiver) {
        this.mReceiver = mReceiver;
    }

    public interface IDoReceiver {
        void doReceiver(int type);
    }

}

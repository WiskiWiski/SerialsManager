package by.wiskiw.serialsmanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by WiskiW on 23.01.2017.
 */
class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

    private final int NATIVE_EXPRESS_AD_HEIGHT = 132;
    private final String TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1072772517";

    private NativeExpressAdView adView;
    private View view;

    NativeExpressAdViewHolder(final View view) {
        super(view);
        this.view = view;
    }

    void setAdView(NativeExpressAdView adView) {
        this.adView = adView;
    }

    void showAd(){
        view.post(new Runnable() {
            @Override
            public void run() {
                final int adWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
                final float scale = view.getContext().getResources().getDisplayMetrics().density;
                AdSize adSize = new AdSize((int) (adWidth / scale), NATIVE_EXPRESS_AD_HEIGHT);
                adView.setAdSize(adSize);
                adView.setAdUnitId(TEST_AD_UNIT_ID);
                adView.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}

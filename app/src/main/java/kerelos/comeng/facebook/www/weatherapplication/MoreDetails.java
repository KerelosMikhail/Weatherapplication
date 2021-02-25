package kerelos.comeng.facebook.www.weatherapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MoreDetails extends AppCompatActivity {
    private WebView wvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);

        wvWeather = (WebView) findViewById(R.id.wvWeather);
    }
}

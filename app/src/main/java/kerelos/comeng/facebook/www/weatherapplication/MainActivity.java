package kerelos.comeng.facebook.www.weatherapplication;
// degree symbol   °
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public String city_name, country, region_name, direction, speed, sunrise, sunset, link, pubDate, temp,
            conditionText, highTemp, lowTemp, humidity, pressure; // 15 var. 14 will be used
    public String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22" +
            city_name + "%2C%20%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    private TextView tvTemp, tvWindSpeed,tvWindDirection, tvDate, tvCondition, tvCountry, tvMax,tvMin,
    tvRegion,tvSunRise,tvSunSet,tvPressure,tvHumidlty,tvtet;
    private EditText etCity;
    private double tempF, tempC, tempMaxF, tempMaxC, tempMinF, tempMinC;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvWindSpeed = (TextView) findViewById(R.id.tvWindSpeed);
        tvWindDirection = (TextView) findViewById(R.id.tvWindDirection);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvCondition = (TextView) findViewById(R.id.tvCondition);
        tvCountry = (TextView) findViewById(R.id.tvCountry);
        tvMax = (TextView) findViewById(R.id.tvMax);
        tvMin = (TextView) findViewById(R.id.tvMin);
        tvWindDirection = (TextView) findViewById(R.id.tvWindDirection);
        tvRegion = (TextView) findViewById(R.id.tvRegion);
        tvSunRise = (TextView) findViewById(R.id.tvSunRise);
        tvSunSet = (TextView) findViewById(R.id.tvSunSet);
        tvPressure = (TextView) findViewById(R.id.tvPressure);
        tvHumidlty = (TextView) findViewById(R.id.tvHumidity);

        tvtet = (TextView) findViewById(R.id.tvtest);

        etCity = (EditText) findViewById(R.id.etCity);

               // Initialize the city name
        etCity.setText("giza");
        city_name = etCity.getText().toString().trim();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("About");
                ab.setMessage("This program is made by: Kerelos.Adel@gmail.com \n" +
                        "I used Yahoo weather to get this data");
                ab.setIcon(R.drawable.blueinfo);
                ab.setPositiveButton("OK", null);
                ab.show();
                break;

            case R.id.action_refresh:

                if (isInternetConnection()) {
                    // GetDtat from internet
 //                   Toast.makeText(this, "Internet OK", Toast.LENGTH_SHORT).show();
//          clearTextFromAll();
       /*             if ((etCity.getText().toString().trim())!= null){
                        city_name = etCity.getText().toString().trim();
                        Toast.makeText(this, "after iNternet connection ", Toast.LENGTH_SHORT).show();
                    }*/
                    getWeatherDataFromInternet();
                    break;
                } else
                    // Get Data from Shared pref
                    DataFromSharedDB();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isInternetConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    private void DataFromSharedDB() {
        SharedPreferences preferences = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor weather = preferences.edit();
        if (preferences.contains("country")) {
   //         Toast.makeText(this, "Last Saved data: \n" + pubDate, Toast.LENGTH_SHORT).show();
            //Set the data into shared preferences
            weather.putString("city_name", city_name);
            weather.putString("country", country);
            weather.putString("region", region_name);
            weather.putString("direction", direction);
            weather.putString("speed", speed);
            weather.putString("sunrise", sunrise);
            weather.putString("sunset", sunset);
            weather.putString("link", link);
            weather.putString("pubDate", pubDate);
            weather.putString("temp", temp);
            weather.putString("conditionText", conditionText);
            weather.putString("highTemp", highTemp);
            weather.putString("lowTemp", lowTemp);
            weather.putString("humidity", humidity);
            weather.putString("pressure", pressure);

            weather.commit();
        } else {
            Toast.makeText(this, "DB   Please check your internet connection", Toast.LENGTH_LONG).show();

        }

    }

    private void clearTextFromAll() {
        tvTemp.setText("" );
        tvMax.setText("");
        tvMin.setText("");
        tvCountry.setText("");
        tvRegion.setText("");
        tvWindSpeed.setText("");
        tvWindDirection.setText("");
        tvSunRise.setText("");
        tvSunSet.setText("");
        tvPressure.setText("");
        tvHumidlty.setText("");
        tvCondition.setText("");
        tvDate.setText("");


    }

    private void getWeatherDataFromInternet() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
          //                  Toast.makeText(MainActivity.this,"In try" +url, Toast.LENGTH_LONG).show();
                            JSONObject query = response.getJSONObject("query");
                            String resultsString = query.getString("results");
                            // check for null "results" = null .....i.e. Dialog
                            if (resultsString != null) {
                                JSONObject results = query.getJSONObject("results");
                                JSONObject channel = results.getJSONObject("channel");
                                JSONObject loc = channel.getJSONObject("location");
                                //"city":"Cairo","country":"Egypt", "region":" Cairo"
                                // city_name = etCity.getText().toString().trim();
                                //city_name = loc.getString("city");
                                country = loc.getString("country");
                                region_name = loc.getString("region");
                                tvtet.setText(region_name);

                                JSONObject wind = channel.getJSONObject("wind");
                                //"chill":"57", "direction":"45","speed":"22"
                                speed = wind.getString("speed");
                                direction = wind.getString("direction");
                                JSONObject atmosphere = channel.getJSONObject("atmosphere");
                                //     "humidity":"77" "pressure":"1005.0",
                                humidity = atmosphere.getString("humidity");
                                pressure = atmosphere.getString("pressure");

                                JSONObject astronomy = channel.getJSONObject("astronomy");
                                // "sunrise":"5:32 am"'"sunset":"6:20 pm"
                                sunrise = astronomy.getString("sunrise");
                                sunset = astronomy.getString("sunset");

                                JSONObject item = channel.getJSONObject("item");
                                // "link",  pubDate,
                                link = item.getString("link");
                                pubDate = item.getString("pubDate");

                                JSONObject condition = item.getJSONObject("condition");
                                // temp , text :"cloudy ....
                                temp = condition.getString("temp");
                                conditionText = condition.getString("text");

                                JSONArray forecast = item.getJSONArray("forecast");
                                JSONObject firstObject = forecast.getJSONObject(0);
                                // high , low ...firstObject
                                highTemp = firstObject.getString("high");
                                lowTemp = firstObject.getString("low");


                                // From F to C
                                // (°F  -  32)  x  5/9 = °C
                                tempF = Double.parseDouble(temp);
                                tempC = (tempF - 32) / 1.8;
                                tempMinF = Double.parseDouble(lowTemp);
                                tempMinC = (tempMinF - 32) / 1.8;
                                tempMaxF = Double.parseDouble(highTemp);
                                tempMaxC = (tempMaxF - 32) / 1.8;

                                // to round the double values
                                java.text.DecimalFormat df = new java.text.DecimalFormat("##");


                                // setting the interface
                                tvTemp.setText("     " + df.format(tempC) + "°C");
                                tvMax.setText(df.format(tempMaxC) + " °C");
                                tvMin.setText(df.format(tempMinC) + " °C");

                                tvCountry.setText("Country: " + country);
                                tvRegion.setText("Region: " + region_name);
                                tvWindSpeed.setText("Wind speed: " + speed + " mph");
                                tvWindDirection.setText("Wind direction: " + direction);
                                tvSunRise.setText("Sunrise at: " + sunrise);
                                tvSunSet.setText("Sunset at: " + sunset);
                                tvPressure.setText("Pressure: " + pressure + " in");
                                tvHumidlty.setText("Humidlty: " + humidity);
                                tvCondition.setText("Today is: " + conditionText);
                                tvDate.setText("Last update: " + pubDate);

                                tvtet.setText(""+ link.substring(63,116));
                                DataFromSharedDB();
                            }else {
                                AlertDialog.Builder rnull = new AlertDialog.Builder(MainActivity.this);
                                rnull.setTitle("Not found");
                                rnull.setMessage("Please enter the city name correctly even by your native language");
                                rnull.setIcon(android.R.drawable.stat_sys_warning);
                                rnull.setPositiveButton("OK", null);
                                rnull.show();
                                //can try break here;
                            }

                        } catch (JSONException e) {
                            // e.printStackTrace();
                            Toast.makeText(MainActivity.this, "JSONException    Please check your internet connection \n", Toast.LENGTH_SHORT).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "VolleyError    Please check the internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    // need check
    @Override
    protected void onRestart() {
        DataFromSharedDB();
        super.onRestart();
    }
}
    /*         if (resultsString == null) {
                                AlertDialog.Builder rnull = new AlertDialog.Builder(MainActivity.this);
                                rnull.setTitle("Not found");
                                rnull.setMessage("Please enter the city name correctly even by your native language");
                                rnull.setIcon(android.R.drawable.stat_sys_warning);
                                rnull.setPositiveButton("OK", null);
                                rnull.show();
                                //can try break here;
                            } else {
                            */


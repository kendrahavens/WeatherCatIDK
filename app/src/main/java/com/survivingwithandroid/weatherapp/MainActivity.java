package com.survivingwithandroid.weatherapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.survivingwithandroid.weatherapp.model.Weather;

import org.json.JSONException;

//I'm pushing this to the repository, FOOLS

/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class MainActivity extends Activity {

	
	private TextView cityText;
	private TextView condDescr;
	private TextView temp;
	private TextView press;
	private TextView windSpeed;
	private TextView windDeg;
	
	private TextView hum;
	private ImageView imgView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String city = "Norman,US"; //have to change this for testing
		
		cityText = (TextView) findViewById(R.id.cityText);
		condDescr = (TextView) findViewById(R.id.condDescr);
		temp = (TextView) findViewById(R.id.temp);
		hum = (TextView) findViewById(R.id.hum);
		press = (TextView) findViewById(R.id.press);
		windSpeed = (TextView) findViewById(R.id.windSpeed);
		windDeg = (TextView) findViewById(R.id.windDeg);
		imgView = (ImageView) findViewById(R.id.condIcon);

        imgView.getLayoutParams().height = 150;
        imgView.getLayoutParams().width = 150;
        imgView.requestLayout();
		
		JSONWeatherTask task = new JSONWeatherTask();
		task.execute(new String[]{city});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = new WeatherHttpClient().getImage(weather.currentCondition.getIcon() + ".png");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }


        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            //test condIcon: android:background="@drawable/cloudicon2"

            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition());
            temp.setText("\n" + (Math.round(((weather.temperature.getTemp() - 273.15) * (9)) / 5) + 32) + " degrees F" + "\n");
            hum.setText("\n" + weather.currentCondition.getHumidity() + "%");
            press.setText("\n" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("\n" + weather.wind.getSpeed() + " mps");
            windDeg.setText(" " + weather.currentCondition.getDescr() + "\n");

            RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.relLayout);
            Resources res = getResources();

            //Default background is few clouds or scattered clouds

            if (condDescr.getText().equals("Clear Sky") || condDescr.getText().equals("Clear sky")) {
                Drawable clearsky = res.getDrawable(R.drawable.clearsky);
                rLayout.setBackground(clearsky);
            }

            if (condDescr.getText().equals("Rain") || condDescr.getText().equals("Shower Rain") || condDescr.getText().equals("Shower rain") || condDescr.getText().equals("Thunderstorm")) {
               Drawable rain = res.getDrawable(R.drawable.rain2);
               rLayout.setBackground(rain);
            }

            if (condDescr.getText().equals("Broken Clouds") || condDescr.getText().equals("Broken clouds"))
            {
                Drawable brokenclouds = res.getDrawable(R.drawable.brokenclouds);
                rLayout.setBackground(brokenclouds);
            }

            if (condDescr.getText().equals("Mist")) {
                Drawable mist = res.getDrawable(R.drawable.mist);
                rLayout.setBackground(mist);
            }

            if (condDescr.getText().equals("Snow")) {
                Drawable snow = res.getDrawable(R.drawable.snow);
                rLayout.setBackground(snow);
            }

        }
    }
}


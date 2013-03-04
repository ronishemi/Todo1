package il.ac.shenkar.todos;

import java.io.IOException;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ArrowKeyMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Menu;

public class Wallpaper extends Activity implements View.OnClickListener {
	// Properties
	ImageView display;
	int toPhone;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// buttons configuration
		setContentView(R.layout.wallpaper);
		display = (ImageView) findViewById(R.id.imageViewDisplay);
		ImageView image1 = (ImageView) findViewById(R.id.Vimage1);
		ImageView image2 = (ImageView) findViewById(R.id.Vimage2);
		ImageView image3 = (ImageView) findViewById(R.id.Vimage3);
		ImageView image4 = (ImageView) findViewById(R.id.Vimage4);
		ImageView image5 = (ImageView) findViewById(R.id.Vimage5);
		ImageView image6 = (ImageView) findViewById(R.id.Vimage6);
		ImageView image7 = (ImageView) findViewById(R.id.Vimage7);
		ImageView image8 = (ImageView) findViewById(R.id.Vimage8);
		Button setWall = (Button) findViewById(R.id.bSetWallpaper);
		toPhone = R.drawable.colored_smoke;
		// set action listener to buttons
		image1.setOnClickListener((OnClickListener) this);
		image2.setOnClickListener((OnClickListener) this);
		image3.setOnClickListener((OnClickListener) this);
		image4.setOnClickListener((OnClickListener) this);
		image5.setOnClickListener((OnClickListener) this);
		image6.setOnClickListener((OnClickListener) this);
		image7.setOnClickListener((OnClickListener) this);
		setWall.setOnClickListener((OnClickListener) this);
	
	}

	// Change wallpaper image method
	@SuppressWarnings("deprecation")
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.Vimage1:
			display.setImageResource(R.drawable.awesome_night);
			toPhone = R.drawable.awesome_night;
			break;

		case R.id.Vimage2:
			display.setImageResource(R.drawable.dark_design);
			toPhone = R.drawable.dark_design;
			break;

		case R.id.Vimage3:
			display.setImageResource(R.drawable.droid_broken_glass);
			toPhone = R.drawable.droid_broken_glass;
			break;

		case R.id.Vimage4:
			display.setImageResource(R.drawable.rain_on_the_window);
			toPhone = R.drawable.rain_on_the_window;
			break;

		case R.id.Vimage5:
			display.setImageResource(R.drawable.raindrops);
			toPhone = R.drawable.raindrops;
			break;

		case R.id.Vimage6:
			display.setImageResource(R.drawable.regenerated_dna);
			toPhone = R.drawable.regenerated_dna;
			break;

		case R.id.Vimage7:
			display.setImageResource(R.drawable.water_droplets);
			toPhone = R.drawable.water_droplets;
			break;

		case R.id.Vimage8:
			display.setImageResource(R.drawable.lightbulb);
			toPhone = R.drawable.lightbulb;
			break;

		case R.id.bSetWallpaper:

			Intent intent = new Intent(Wallpaper.this, TaskListActivity.class);
			intent.putExtra("toPhone", String.valueOf(toPhone));
			if (toPhone == 0)
				setResult(RESULT_CANCELED, intent);
			else
				setResult(RESULT_OK, intent);
			finish();
		}

	}

}

package com.cooking.mycuisine;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SortedRecipesActivity extends Activity {

	GridView gridView;
	ImageAdapter imageAdapter;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sorted_recipes);

		// changes Here
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// till here
		
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new ImageAdapter(this));

		Spinner spinner = (Spinner) findViewById(R.id.sort_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.sort_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

	}

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
			inflater = LayoutInflater.from(c);

		}

		public int getCount() {
			return 30;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView foric_launcher each item referenced by the
		// Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			final RecipeGridHolder holder;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				convertView = inflater.inflate(R.layout.grid_recipe_holder,
						null);
				holder = new RecipeGridHolder();

				holder.recipeImage = (ImageView) convertView
						.findViewById(R.id.recipe_image);
				holder.recipeName = (TextView) convertView
						.findViewById(R.id.recipe_name);

				convertView.setTag(holder);
			} else {
				holder = (RecipeGridHolder) convertView.getTag();
			}

			holder.recipeImage.setAdjustViewBounds(true);
			holder.recipeImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			holder.recipeImage.setPadding(1, 1, 1, 1);
			holder.recipeImage.setImageResource(R.drawable.cream_roll_2);
			// UrlImageViewHelper.setUrlDrawable(holder.recipeImage,
			// recipeList.get(position).getImage());

			return convertView;
		}

		class RecipeGridHolder {
			ImageView recipeImage;
			TextView recipeName;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent add = new Intent(this, AddRecipesActivity.class);
			add.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(add);
			break;
		case R.id.action_sort:
			Intent sort = new Intent(this, SortedRecipesActivity.class);
			sort.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(sort);
			break;

		case R.id.action_refresh:
			Toast.makeText(this, "refresh not implemented", Toast.LENGTH_SHORT)
					.show();
			break;
		// <Changes from here>
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		// hereeeeeee
		default:
			break;
		}

		return true;
	}

}
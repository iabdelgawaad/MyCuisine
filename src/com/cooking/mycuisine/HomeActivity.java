package com.cooking.mycuisine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import android.view.View.OnClickListener;
import com.cooking.mycuisine.model.Recipe;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.cooking.mycuisine.util.*;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	GridView gridView;
	ImageAdapter imageAdapter;
	String recipeDataDownloaded;
	public static ArrayList<Recipe> recipesList = new ArrayList<Recipe>();
	ProgressBar dialog_progressBar;
	RelativeLayout ConnectionLayout, noRecipesLayout , noUserAccount;
	LinearLayout gridLayout;
	boolean userHaveRecipes = false , errorInDownloadingRecipes = false;
	Button noUserAccount_bt;

	//
	private static Drive service;
	static final int CAPTURE_IMAGE = 3;
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	private GoogleAccountCredential credential;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_recipes);

		gridView = (GridView) findViewById(R.id.gridview);
		
		dialog_progressBar = (ProgressBar) findViewById(R.id.preloader);
		
		ConnectionLayout = (RelativeLayout) findViewById(R.id.internetConnectionLayout);
		ConnectionLayout.setVisibility(View.INVISIBLE);
		
		noRecipesLayout = (RelativeLayout) findViewById(R.id.noRecipesLayout);
		noRecipesLayout.setVisibility(View.INVISIBLE);
		
		noUserAccount = (RelativeLayout) findViewById(R.id.noUserAccounts);
		noUserAccount.setVisibility(View.INVISIBLE);
		
		
		gridLayout = (LinearLayout) findViewById(R.id.gridLayout);
		gridLayout.setVisibility(View.VISIBLE);
		
		noUserAccount_bt = (Button) findViewById(R.id.account_button);
		noUserAccount_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getUserAccount();
			}
		});
		
		getUserAccount();
	}
     
	public void getUserAccount() 
    {
		if (ConnectionUtil.isConnectionAvailable(getApplicationContext()))
		{
			credential = GoogleAccountCredential.usingOAuth2(this,
					Arrays.asList(DriveScopes.DRIVE_FILE));
			
			startActivityForResult(credential.newChooseAccountIntent(),
					REQUEST_ACCOUNT_PICKER);
		}
		else
		{
			ConnectionLayout.setVisibility(View.VISIBLE);
		}
		
	}
	public  void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), toast,
						Toast.LENGTH_LONG).show();
			}
		});
	}
	//Authentication 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
	    case REQUEST_ACCOUNT_PICKER:
	      if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
	        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	        if (accountName != null) {
	          credential.setSelectedAccountName(accountName);
	          service = getDriveService(credential);
               new getRecipes().execute("");
	        }
	      }
	      else if (resultCode == RESULT_CANCELED) 
	      {
	    	  //show message to alert user to choose google account.
	    	  noUserAccount.setVisibility(View.VISIBLE);
	      }
	      break;
	    case REQUEST_AUTHORIZATION:
	      if (resultCode == Activity.RESULT_OK) {
              new getRecipes().execute("");
           } else {
	          startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	      }
	      break;
	   
	    }
	}

	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(),
				new GsonFactory(), credential).build();
	}

	private  class ImageAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
			inflater = LayoutInflater.from(c);

		}

		public int getCount() {
			return recipesList.size();
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

			Bitmap mybitmap = AddRecipesActivity.decodeToImage(recipesList.get(
					pos).getImage_string());
			holder.recipeImage.setImageBitmap(mybitmap);
			
			String recipename = recipesList.get(pos).getName();
			holder.recipeName.setText(recipename);
			
			// holder.recipeImage.setImageResource(R.drawable.cream_roll);
			// UrlImageViewHelper.setUrlDrawable(holder.recipeImage,
			// recipeList.get(position).getImage());
			

			return convertView;
		}

		class RecipeGridHolder {
			ImageView recipeImage;
			TextView recipeName;
		}
	}
	// from here
	public Recipe deserializeRecipeObject(String recipeJsonStr, int index) {
		Recipe recipe = null;
		if (!recipeJsonStr.equals("") && recipeJsonStr != null) {
			Gson gson = new Gson();
			try{
			recipe = gson.fromJson(recipeJsonStr, Recipe.class);
			}catch (JsonParseException e)
			{
				Log.d("File Not Recipes", index+"");
			}
		}
		return recipe;
	}
	private void downloadFile()
	{
	    try 
	            {
	                //com.google.api.services.drive.model.File file = service.files().get(fileID).execute();
	                FileList file = service.files().list().setQ("title contains 'recipe'").execute();
	                List<com.google.api.services.drive.model.File> fileList = file.getItems();
	                Iterator<com.google.api.services.drive.model.File> iterator = fileList.iterator();

                    Log.d("List Numbers IS :: ", fileList.size()+" ");	                
	                for(int i=0 ; i<fileList.size() ; i++)
	                {
	                	com.google.api.services.drive.model.File fileItem = fileList.get(i);
		                Log.d("FileID" , fileItem.getId());
		                Log.d("FileExtension",fileItem.getFileExtension());
		                Log.d("FIleTitle" , fileItem.getTitle());
		                
		                if (fileItem.getDownloadUrl() != null && fileItem.getDownloadUrl().length() > 0)
		                {
		                    HttpResponse resp = service.getRequestFactory().buildGetRequest(new GenericUrl(fileItem.getDownloadUrl())).execute();
		                    InputStream inputStream = resp.getContent();
		                    
		                    String line;
		                  //reading   
		                    try{
		                        InputStreamReader ipsr=new InputStreamReader(inputStream);
		                        BufferedReader br=new BufferedReader(ipsr);
		                        while ((line = br.readLine()) != null){
		                            recipeDataDownloaded =line;
		                        }
		                        br.close(); 
		                    }       
		                    catch (Exception e){
		                        System.out.println(e.toString());
		                    }
		                    System.out.println("Downloaded file content : "+ recipeDataDownloaded);
		                    
		                    if (!recipeDataDownloaded.equals("") && recipeDataDownloaded != null)
		                    {
		                    	Recipe recipeObjFromJson = new Recipe ();
			                    recipeObjFromJson = deserializeRecipeObject(recipeDataDownloaded, i);
			                    recipesList.add(i,recipeObjFromJson);
			                    Log.d("Recipe Nameee--> ", recipeObjFromJson.getName()) ;
		                        Log.d("nRecipe ImageString--->", recipeObjFromJson.getImage_string()) ;
		                    }
		                    else
		                    {
		                    	//User dosn't have any recipes !
		                    	userHaveRecipes = true;
		                        
		            		}
		                    
		                }
		                	            	
	                }
	               
	            } 
	            catch (IOException e)
	            {
	                Log.e("WriteToFile", e.toString());
	                e.printStackTrace();
	                errorInDownloadingRecipes = true;
	            }
	        }
	   
	
	
	public class getRecipes extends AsyncTask<String, Integer, String>
	{

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			dialog_progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... params) {
			publishProgress();
	        downloadFile();
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (userHaveRecipes)
			{
				//user dosn't have recipes..
				noRecipesLayout.setVisibility(View.VISIBLE);
			}
			else if (errorInDownloadingRecipes)
			{
				ConnectionLayout.setVisibility(View.VISIBLE);
			}
			else
			{
			   dialog_progressBar.setVisibility(View.INVISIBLE);
			   gridView.setAdapter(new ImageAdapter(getApplicationContext()));
	       	}
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

		default:
			break;
		}

		return true;
	}

}
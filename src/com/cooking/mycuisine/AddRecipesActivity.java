package com.cooking.mycuisine;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;


import com.cooking.mycuisine.model.Category;
import com.cooking.mycuisine.model.Direction;
import com.cooking.mycuisine.model.Ingredient;
import com.cooking.mycuisine.model.Recipe;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddRecipesActivity extends Activity {
	Context context = this;
	Button requiredIngredient;
	Button directionStep;
	Button category;
	Button camera_button;
	AlertDialog.Builder builder;
	AlertDialog alertDialog;
	String Imagebase64String = "";
	private static Uri fileUri;
	private static Drive service;
	static final int CAPTURE_IMAGE = 3;
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	private GoogleAccountCredential credential;

	EditText newDirEditText;
	EditText amount;
	EditText component;
	Spinner fraction_spinner;
	Spinner unit_spinner;
	Spinner use_deafult;
	int numOfmins;
	int numOfServings;
	boolean isUnfinished;
	Category cat;
	List<Ingredient> ingredients;
	List<Direction> directions;
	Gson recipeGson = new Gson();
	String recipeDataUploaded;
	String recipeDataDownloaded;
	boolean recipeImageCaptured = true;
	boolean recipeNameTaken = true;
	Recipe recipe;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_recipes);

		credential = GoogleAccountCredential.usingOAuth2(this,
				Arrays.asList(DriveScopes.DRIVE_FILE));
		// Changes here
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// till here

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		final ArrayAdapter<CharSequence> unit_adapter = ArrayAdapter
				.createFromResource(this, R.array.unit_array,
						android.R.layout.simple_spinner_item);

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		final ArrayAdapter<CharSequence> fraction_adapter = ArrayAdapter
				.createFromResource(this, R.array.fraction_array,
						android.R.layout.simple_spinner_item);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		final ArrayAdapter<CharSequence> use_deafult_adapter = ArrayAdapter
				.createFromResource(this, R.array.use_deafult_array,
						android.R.layout.simple_spinner_item);
		// Image Button
		camera_button = (Button) findViewById(R.id.camera_button);
		camera_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// GoogleAccountCredential
				startCameraIntent();
				
			}
		});

		// Implement required Ingredient button..
		requiredIngredient = (Button) findViewById(R.id.button_1);
		ingredients = new ArrayList<Ingredient>();
		// add button listener
		requiredIngredient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(
						R.layout.required_ingredient_dialog, null);

				amount = (EditText) layout.findViewById(R.id.mount);
				component = (EditText) layout.findViewById(R.id.onion);
				fraction_spinner = (Spinner) layout
						.findViewById(R.id.fraction_spinner);

				// Specify the layout to use when the list of choices appears
				fraction_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				fraction_spinner.setAdapter(fraction_adapter);

				unit_spinner = (Spinner) layout.findViewById(R.id.unit_spinner);

				// Specify the layout to use when the list of choices appears
				unit_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				unit_spinner.setAdapter(unit_adapter);

				use_deafult = (Spinner) layout
						.findViewById(R.id.use_default_spinner);
				// Specify the layout to use when the list of choices appears
				use_deafult_adapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				use_deafult.setAdapter(use_deafult_adapter);

				builder = new AlertDialog.Builder(context);
				builder.setView(layout);
				// Add the buttons
				builder.setNegativeButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
				builder.setNeutralButton("Add+1",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
				builder.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								fillAndValidateIngredient();
								dialog.dismiss();
							}
						});
				System.out.println("Ingredients ArrayList Size--> "
						+ ingredients.size());
				alertDialog = builder.create();
				alertDialog.show();
				alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {

								fillAndValidateIngredient();
								fraction_spinner.setSelection(0);
								use_deafult.setSelection(0);
								unit_spinner.setSelection(0);
							}
						});
			}
		});

		// Implement direction step button..
		directionStep = (Button) findViewById(R.id.button_2);
		directions = new ArrayList<Direction>();
		// add button listener
		directionStep.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.direction_step_dialog,
						null);

				builder = new AlertDialog.Builder(context);
				builder.setView(layout);
				newDirEditText = (EditText) layout
						.findViewById(R.id.edit_example_dialog_2);

				// Add the buttons
				builder.setNegativeButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
				builder.setNeutralButton("Add+1",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
				builder.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								fillAndValidateDirection();
								dialog.dismiss();

							}
						});
				System.out.println("Directions ArrayList Size--> "
						+ directions.size());
				alertDialog = builder.create();
				alertDialog.show();
				alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								fillAndValidateDirection();
							}
						});
			}
		});

		// Implement category button...
		category = (Button) findViewById(R.id.button_3);
		category.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(
						AddRecipesActivity.this);
				builderSingle.setIcon(R.drawable.ic_launcher);
				builderSingle.setTitle("Select One Category");
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						AddRecipesActivity.this,
						android.R.layout.select_dialog_singlechoice);
				arrayAdapter.add("Main Dishes");
				arrayAdapter.add("Appetizers & Beverages");
				arrayAdapter.add("Breads & Rolls");
				arrayAdapter.add("Soups & Salads");
				arrayAdapter.add("Desserts");
				arrayAdapter.add("Vegetables & Side Dishes");
				arrayAdapter.add("Cookies & Candy");
				arrayAdapter.add("This & That");

				builderSingle.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				builderSingle.setAdapter(arrayAdapter,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String strName = arrayAdapter.getItem(which);
								AlertDialog.Builder builderInner = new AlertDialog.Builder(
										AddRecipesActivity.this);
								builderInner.setMessage(strName);
								builderInner
										.setTitle("Your Selected Category is");
								cat = new Category();
								cat.setCategory(strName);
								builderInner.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												dialog.dismiss();
											}
										});
								builderInner.show();
							}
						});
				builderSingle.show();
			}
		});

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
	    case REQUEST_ACCOUNT_PICKER:
	      if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
	        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	        if (accountName != null) {
	          credential.setSelectedAccountName(accountName);
	            service = getDriveService(credential);
				saveFileToDrive();	
	        }
	      }
	      break;
	    case REQUEST_AUTHORIZATION:
	      if (resultCode == Activity.RESULT_OK) {
                  saveFileToDrive();
           } else {
	          startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	      }
	      break;
	    case CAPTURE_IMAGE:
	      if (resultCode == Activity.RESULT_OK) {
	    	  Bitmap photo = (Bitmap) data.getExtras().get("data");
				Imagebase64String = incodeImageToString(photo);	
				recipeImageCaptured = true;
	      }
	    }
	}


	private void fillAndValidateIngredient() {

		Ingredient ingredient = new Ingredient();
		if (!amount.getText().toString().equals("")
				&& amount.getText().toString() != null) {

			ingredient.setAmount(amount.getText().toString());

		}

		if (!component.getText().toString().equals("")
				&& component.getText().toString() != null) {

			ingredient.setComponent(component.getText().toString());
		}

		if (fraction_spinner.getSelectedItem() != null) {

			ingredient.setFraction(fraction_spinner.getSelectedItem()
					.toString());
		}

		if (use_deafult.getSelectedItem() != null) {

			ingredient.setGroup(use_deafult.getSelectedItem().toString());
		}

		if (unit_spinner.getSelectedItem() != null) {

			ingredient.setUnit(unit_spinner.getSelectedItem().toString());

		}

		ingredients.add(ingredient);
		amount.setText("");
		component.setText("");
	}

	private void fillAndValidateDirection() {

		Direction direction = new Direction();
		if (!newDirEditText.getText().toString().equals("")
				&& newDirEditText.getText().toString() != null) {

			direction.setDirectionStep(newDirEditText.getText().toString());
			directions.add(direction);
			newDirEditText.setText("");
		} else {
			showToast("you should enter a text");
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

	private void startCameraIntent() {
		    Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    startActivityForResult(cameraIntent, CAPTURE_IMAGE);
	    }
	
	public static String incodeImageToString(Bitmap recipe_Image) {
		/*
		 * recipe_Image.buildDrawingCache(); Bitmap bmap =
		 * recipe_Image.getDrawingCache();
		 */
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		recipe_Image.compress(Bitmap.CompressFormat.PNG, 90, stream);
		byte[] image = stream.toByteArray();
		String img_str = Base64.encodeToString(image, 0);

		return img_str;
	}
	
	//ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	//bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object   
	//byte[] b = baos.toByteArray();

	//to encode base64 from byte array use following method

	//String encoded = Base64.encodeToString(b, Base64.DEFAULT);
	//from search about how to convert bitmap image from string to base 64 ok? ok 
	//Don't mention.
	//byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
	//Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
    //tmam wla? fola :D :D  ok lets complete 
	
	
	 public static Bitmap decodeToImage(String imageString) 
	 {
		 byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
		 Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
	 	
		 return decodedByte;
	    }


	public void SerializeRecipeObject() {

		EditText recipeName = (EditText) findViewById(R.id.editText_1);
		EditText minutes = (EditText) findViewById(R.id.editText_2);
		EditText servings = (EditText) findViewById(R.id.editText_3);
		CheckBox isDraft = (CheckBox) findViewById(R.id.checkbox);

		if (!minutes.getText().toString().equals("")
				&& minutes.getText().toString() != null) {

			numOfmins = Integer.parseInt(minutes.getText().toString());

		} else {

			numOfmins = 0;
		}
		if (!servings.getText().toString().equals("")
				&& servings.getText().toString() != null) {

			numOfServings = Integer.parseInt(servings.getText().toString());

		} else {

			numOfServings = 0;
		}

		if (isDraft.isChecked()) {

			isUnfinished = true;
		} else {

			isUnfinished = false;
		}

		recipe = new Recipe();
		// fill the Recipe object with data
		if (!recipeName.getText().toString().equals("")
				&& recipeName.getText().toString() != null) {

			recipe.setName(recipeName.getText().toString());
			recipeNameTaken = true;

		} else {

			showToast("Please enter the Recipe name !");
			recipeNameTaken = false;

		}
		if (!Imagebase64String.equals("") && Imagebase64String != null) {

			recipe.setImage_string(Imagebase64String);
			recipeImageCaptured = true;

		} else {
			showToast("Please take image for your Recipe !");
			recipeImageCaptured = false;
		}

		recipe.setNumOfMinutes(numOfmins);
		recipe.setNumofServings(numOfServings);
		recipe.setDraft(isUnfinished);
		if (cat != null) {

			recipe.setCategory(cat);
		}
		if (directions != null) {

			recipe.setDirections(directions);
		}
		if (ingredients != null) {

			recipe.setIngredients(ingredients);
		}

		if(recipeNameTaken && recipeImageCaptured != false)
		{
			// convert java object to Gson String
			recipeDataUploaded = recipeGson.toJson(recipe);
			System.out.println("Recipe jsonnnn \n" + recipeDataUploaded);
			Log.d("Recipe jsonnnn", recipeGson+"");
			
			writeStrToFile(recipeDataUploaded);
			
			startActivityForResult(credential.newChooseAccountIntent(),
					REQUEST_ACCOUNT_PICKER);
		}
/*
		// Test Deserialization will used in allrecipes layouts.
		Recipe recipeObjFromJson = deserializeRecipeObject(recipeData);
		if (recipeObjFromJson != null) {
			System.out.println("Recipe Nameee--> "
					+ recipeObjFromJson.getName() + "\nRecipe ImageString---> "
					+ recipeObjFromJson.getImage_string());
		}
		*/

	}
	
	// write a file to internal storage
		public void writeStrToFile(String str) {
			
			String filename = "myfile";
			String string = str;
			FileOutputStream outputStream;
			try {
				outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
				outputStream.write(string.getBytes());
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//read a file data as a string
		public String readStrFromFile() {
			String str = "";
			String filename = "myfile";
			byte buffer[] = new byte[10000];
			FileInputStream inputStream;
			try {
				inputStream = openFileInput(filename);
				try {
					inputStream.read(buffer);
					str = new String(buffer);
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return str;
		}
	// till here
	
	private void saveFileToDrive() {
	    Thread t = new Thread(new Runnable() {
	      @Override
	      public void run() {
	        try {
	        	File newFile = new File();
	        	newFile.setTitle(recipe.getName()+". recipe");
	        	newFile.setMimeType("text/plain");
	        	String contentStr = recipeDataUploaded;   

	        	File file = service.files().insert(
	        	         newFile, ByteArrayContent.fromString("text/plain", contentStr)).execute();
	       if (file != null) {
	              showToast("New recipe uploaded: " + file.getTitle() + "File ID is:"+ file.getId());
	          }
	        } catch (UserRecoverableAuthIOException e) {
	          startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	      }
	    });
	    t.start();
	  }
	
	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(),
				new GsonFactory(), credential).build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_recipe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save:
			SerializeRecipeObject();
			break;

		// <Changes from here>
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		// to here
		}

		return true;
	}

}
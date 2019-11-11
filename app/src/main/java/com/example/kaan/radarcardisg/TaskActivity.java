package com.example.kaan.radarcardisg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity  implements CompoundButton.OnCheckedChangeListener   {

    String table_URL = "http://gknsatinalma.midsoft.com.tr:81/MIDSOFT_JSON_WCF/JSON.svc/get_hse_table/";

    RelativeLayout relativeLayout;
    ArrayList<String> images ;
    File imagesFolder;
    File outputFolder;
    String imagePath;
    String outputPath;
    String listId;
    String tkId;
    String id = "0";
    Integer REQUESTCAMERA = 1,SELECT_FILE = 0;
    int index = 1;
    ArrayList<StoredData> datasToStore = new ArrayList<>();
    ArrayList<String> taskData = new ArrayList<>();
    int[] photoCount = new int[35];
    GridView gridView;
    ImageAdapterGridView imageAdapterGridView;
    int taskNumber = -1;
    String upLoadServerUri = "http://gknsatinalma.midsoft.com.tr:81/gknSatinalma/upload.php";  //"http://192.168.0.12/evoLibDemo/upload.php";
    int serverResponseCode = 0;

    private class StoredData implements Parcelable {

        int id;
        boolean switchSituation;
        String explanation;

        protected StoredData(Parcel in) {
            id = in.readInt();
            switchSituation = in.readByte() != 0;
            explanation = in.readString();
        }
        public final Creator<StoredData> CREATOR = new Creator<StoredData>() {
            @Override
            public StoredData createFromParcel(Parcel in) {
                return new StoredData(in);
            }

            @Override
            public StoredData[] newArray(int size) {
                return new StoredData[size];
            }
        };
        public StoredData(int id, boolean switchSituation, String explanation) {
            this.id = id;
            this.switchSituation = switchSituation;
            this.explanation = explanation;
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeByte((byte) (switchSituation ? 1 : 0));
            dest.writeString(explanation);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        getTable();
        ignoreZeros();
        createLayout();
        taskNumber = getIntent().getExtras().getInt("taskNumber");
        listId = getIntent().getExtras().getString("listId");
        tkId = getIntent().getExtras().getString("tkId");
        setSavedInstanceState(savedInstanceState);
        setActionBar();
    }

    private void ignoreZeros(){
        datasToStore.add(new StoredData(0,true,""));
     //   storedJsons.add(new JSONObject());
    }

    private void getTable(){
        Intent intent = getIntent();
        String listId = intent.getStringExtra("listId");
        RequestHandler.getRequestHandler().getTableElements(table_URL+ listId,taskData);
    }
    //-----------------------------------------------------------------     Layout Design
    private void createLayout(){
        relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        relativeLayout.setId(R.id.testRelative);
//-----------------------------------------------------------------------------------
        ScrollView scrollView = new ScrollView(this);
        ScrollView.LayoutParams sclp = new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(sclp);
//-----------------------------------------------------------------------------------
        LinearLayout baseLayout = new LinearLayout(this);
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams baseLayoutParams = new LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.MATCH_PARENT ,
                LinearLayout.LayoutParams.MATCH_PARENT
                        );
        baseLayout.setLayoutParams(baseLayoutParams);
//-----------------------------------------------------------------------------------
        LinearLayout nestedLayout = new LinearLayout(this);
        nestedLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams nestedLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        nestedLayoutParams.leftMargin = 10;
        nestedLayoutParams.gravity = Gravity.LEFT;
        nestedLayout.setLayoutParams(nestedLayoutParams);
//-----------------------------------------------------------------------------------
        index = 1;
        for(int i= 1;i<taskData.size();i++){
            if(taskData.get(i).startsWith("|")) {
                nestedLayout.addView(addAllContents(taskData.get(i),i));
            }
        }

        baseLayout.addView(nestedLayout);
        baseLayout.addView(setStoreButton());
        scrollView.addView(baseLayout);
        relativeLayout.setLayoutParams(rlp);
        relativeLayout.addView(scrollView);
        setContentView(relativeLayout);
    }
    private TextView addTitle(String title){
        TextView titleText = new TextView(this);
        title = title.replace("|","");
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(Color.parseColor("#FFFFFF"));
        titleText.setBackgroundColor(Color.parseColor("#3B7ADB"));
        return titleText;
    }
    private LinearLayout addNestedContents(String content,String id){
        LinearLayout nestedContentLayout = new LinearLayout(this);
        nestedContentLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams nestedContentLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        nestedContentLayoutParams.topMargin = 5;
        nestedContentLayout.setLayoutParams(nestedContentLayoutParams);

        nestedContentLayout.addView(addItems(content,id));


        return nestedContentLayout;
    }
    private LinearLayout addAllContents(String title,int k){
        LinearLayout allContentsLayout = new LinearLayout(this);
        allContentsLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams allContentLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        allContentLayoutParams.topMargin = 5;
        allContentsLayout.setLayoutParams(allContentLayoutParams);
        allContentsLayout.addView(addTitle(title));

        k++;
        while (k < taskData.size() &&!taskData.get(k).startsWith("|")){
            allContentsLayout.addView(addNestedContents(taskData.get(k),Integer.toString(index)));
            index++;
            k++;
        }
        return allContentsLayout;
    }
    private TextView addTextContent(String content, final String txtId){
        TextView itemText = new TextView(this);
        itemText.setText(content);
        itemText.setTextSize(16);
        itemText.setTextColor(Color.parseColor("#269DF1"));
        itemText.setTag("textView"+txtId);
        itemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    buildPhotoTakeDialog(txtId);
            }
        });

        final float inPixels= getApplication().getResources().getDimension(R.dimen.width);


        LinearLayout.LayoutParams itemTextParams =new LinearLayout.LayoutParams((int)inPixels,LinearLayout.LayoutParams.WRAP_CONTENT);
        itemText.setLayoutParams(itemTextParams);

        return itemText;
    }
    private SwitchCompat addSwitch(String switchId){
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.parseColor("#C50016"),//
                Color.parseColor("#3DD84C"),//
        };

        SwitchCompat itemSwitch = new SwitchCompat(this);
        LinearLayout.LayoutParams asd1 =new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        asd1.rightMargin = 15;
        asd1.gravity = Gravity.CENTER;
        DrawableCompat.setTintList(DrawableCompat.wrap(itemSwitch.getThumbDrawable()),new ColorStateList(states,thumbColors));
        itemSwitch.setChecked(true);
        itemSwitch.setLayoutParams(asd1);
        itemSwitch.setTag("switch"+switchId );
        itemSwitch.setOnCheckedChangeListener(this);
        datasToStore.add(new StoredData(Integer.parseInt(switchId),true,""));
        return itemSwitch;
    }
    private LinearLayout addItems(String content,String id){
        LinearLayout singleItemLayout = new LinearLayout(this);
        singleItemLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams singleItemLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        singleItemLayoutParams.leftMargin = 23;
        singleItemLayout.setLayoutParams(singleItemLayoutParams);

        View view = new View(this);
        LinearLayout.LayoutParams viewParams =new LinearLayout.LayoutParams(0,0);
        viewParams.weight = 1;
        view.setLayoutParams(viewParams);

        singleItemLayout.addView(addTextContent(content,id));
        singleItemLayout.addView(view);
        singleItemLayout.addView(addSwitch(id));
        return singleItemLayout;
    }
    //-----------------------------------------------------------------    Layout Design
    private void setSavedInstanceState(Bundle savedInstanceState){
        if(savedInstanceState == null){
            try {
                generateDirectories(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            savedInstanceState.getBoolean("reload",false);
            id = savedInstanceState.getString("id");
            photoCount = savedInstanceState.getIntArray("photoCount");
            buildPhotoTakeDialog(id);
            //dataToStore =(StoredData[]) savedInstanceState.getParcelableArray("dataToStore");
            datasToStore = savedInstanceState.getParcelableArrayList("dataToStore");
            try {
                generateDirectories(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Button setStoreButton(){
       Button store = new Button(this);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    createXML();

                if(imagesFolder.exists() && outputFolder.exists())
                    FileHelper.zip(imagePath,outputPath,"zippedImages.zip",false);

                File myImagesToDelete = new File(Environment.getExternalStorageDirectory(), "MyImages");

                RequestHandler.getRequestHandler().uploadFile(outputPath+"/zippedImages.zip",upLoadServerUri,serverResponseCode,
                        getApplicationContext(),TaskActivity.this);

                deleteFiles(myImagesToDelete);
                deleteFolder(imagesFolder);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = getIntent();
                intent.putExtra("task",taskNumber);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        store.setText("KAYDET");

        return store;
    }
    private void createXML() throws IOException {

        File xmlFile = new File(imagePath, "XML_Output.xml");
        FileWriter fileWriter = new FileWriter(xmlFile);
        fileWriter.append("<record>"+"\r\n");
        for (StoredData data:datasToStore) {
            if(data.id == 0)
                continue;
            fileWriter.append("<data id='"+Integer.toString(data.id)+"'>"+"\r\n");
            fileWriter.append("<listId>"+listId+"</listId>"+"\r\n");
            fileWriter.append("<tkId>"+tkId+"</tkId>"+"\r\n");
            fileWriter.append("<explanation>"+data.explanation+"</explanation>"+"\r\n");
            fileWriter.append("<switch>"+data.switchSituation+"</switch>"+"\r\n");
            fileWriter.append("</data>"+"\r\n");
        }

        fileWriter.append("</record>"+"\r\n");
        fileWriter.flush();
        fileWriter.close();
    }
    private void setActionBar(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.trans)));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
    }
    private void generateDirectories(boolean reload) throws IOException {
        imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        imagesFolder.mkdirs();
        imagesFolder.deleteOnExit();
        outputFolder = new File(Environment.getExternalStorageDirectory(), "Output");
        outputFolder.mkdirs();
        imagePath = imagesFolder.getPath();
        outputPath = outputFolder.getPath();
        if(!reload){
            deleteFiles(imagesFolder);
            deleteFiles(outputFolder);
        }

    }
    private void buildPhotoTakeDialog( final String expId){
        final AlertDialog.Builder photoBuilder = new AlertDialog.Builder(TaskActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.photo_layout,null);
        photoBuilder.setView(mView);
        final AlertDialog alertPhoto = photoBuilder.create();
        setAlertBackground(alertPhoto,Gravity.BOTTOM);
        setPhotoTakeButtons(expId,mView);
        id= expId;
        alertPhoto.show();
    }
    private void setPhotoTakeButtons(final String expId, View mView){
        ImageButton btnCam = (ImageButton) mView.findViewById(R.id.imgBtnCam);
        ImageButton btnDir = (ImageButton) mView.findViewById(R.id.imgBtnDir);
        ImageButton btnGallery = (ImageButton) mView.findViewById(R.id.imgBtnGallery);

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoCount[Integer.parseInt(expId)]++;
                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File image = new File(imagesFolder,expId+"_"+ Integer.toString( photoCount[Integer.parseInt(expId)]) + ".png");
                Uri uriSavedImage = Uri.fromFile(image);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT,uriSavedImage);
                if(imageIntent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(imageIntent,REQUESTCAMERA);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                id = expId;
                startActivityForResult(Intent.createChooser(intent,"Select File"),SELECT_FILE);
            }
        });

        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getPaths();
                getSpecPaths(expId);
                buildPhotoGalleryDialog();
                // Intent myIntent = new Intent(getApplicationContext(),asd.class);
                //startActivityForResult(myIntent,0);
                //alert11.dismiss();
            }
        });
    }
    private void setAlertBackground(AlertDialog alertDialog, int gravity){
        Window win = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = win.getAttributes();
        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        wlp.gravity = gravity ;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        win.setAttributes(wlp);
    }
    private void getSpecPaths(String expId){
        images = new ArrayList<String>();
        for (int i =1;i<photoCount[Integer.parseInt(expId)]+1;i++){
            File imageFile = new File(Environment.getExternalStorageDirectory(), "MyImages/"+expId+"_"+i+".png");
            if(imageFile.exists()){
                if(containCheck(images,imageFile.getAbsolutePath().toString())){
                    images.add(imageFile.getAbsolutePath());
                }
            }
        }
    }
    private boolean containCheck(ArrayList<String> arr, String targetValue) {
        if(arr.isEmpty())
            return true;

        for (String t:arr
                ) {
            if(t.equals(targetValue))
                return false;
        }
        return true;
    }
    private void buildPhotoGalleryDialog(){
        final AlertDialog.Builder photoGalleryBuilder = new AlertDialog.Builder(TaskActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.grid_photo_layout,null);
        mView.setBackgroundResource(R.color.transparent);
        photoGalleryBuilder.setView(mView);

        final AlertDialog photoGalleryAlert = photoGalleryBuilder.create();
        setAlertBackground(photoGalleryAlert,Gravity.CENTER_HORIZONTAL);

        gridView = (GridView) mView.findViewById(R.id.gridView1);
        gridView.setBackgroundResource(R.color.transparent);
        imageAdapterGridView = new ImageAdapterGridView(this);
        gridView.setAdapter(imageAdapterGridView);

        photoGalleryAlert.show();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("id", id);
        outState.putBoolean("reload",true);
        outState.putIntArray("photoCount",photoCount);
       // outState.putParcelableArray("dataToStore", (Parcelable[]) dataToStore);
        outState.putParcelableArrayList("dataToStore",datasToStore);
        super.onSaveInstanceState(outState);
    }
    private void buildExplanationDialog(final String switchId, final CompoundButton buttonView){
        String textId = "textView" + switchId;
        //int resId = getResources().getIdentifier(textId, "id", getPackageName());
        TextView txt = (TextView) relativeLayout.findViewWithTag(textId); //findViewById(resId);
        String asd = txt.getText().toString();
        AlertDialog.Builder explanationBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_box_layout,null);
        explanationBuilder.setCancelable(false);

        final EditText txtExplanation = (EditText) mView.findViewById(R.id.edtTxtExplanation);
        TextView txtStatus = (TextView) mView.findViewById(R.id.txtStatus);
        ImageButton btnOk = (ImageButton) mView.findViewById(R.id.imgBtnOk);
        ImageButton btnCancel = (ImageButton) mView.findViewById(R.id.imgBtnCancel);

        explanationBuilder.setView(mView);
        final AlertDialog explanationAlert = explanationBuilder.create();
        txtStatus.setText(txt.getText().toString());

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtExplanation.getText().toString().equals("")){
                    //dataToStore[Integer.parseInt(switchId)].switchSituation = false;
                   // dataToStore[Integer.parseInt(switchId)].explanation = txtExplanation.getText().toString();
                    datasToStore.get(Integer.parseInt(switchId)).switchSituation = false;
                    datasToStore.get(Integer.parseInt(switchId)).explanation = txtExplanation.getText().toString();
                    explanationAlert.cancel();
                }
                else {
                    Toast toast= Toast.makeText(TaskActivity.this,
                            "Açıklama alanı boş bırakılamaz!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonView.setChecked(true);
                explanationAlert.dismiss();
            }
        });
        explanationAlert.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == SELECT_FILE){
                Uri selectedImageUri = data.getData();
                File selectedImage = new File(getRealPathFromURI(selectedImageUri));
                photoCount[Integer.parseInt(id)]++;
                File newImage = new File(Environment.getExternalStorageDirectory(), "MyImages/"+id+
                        "_"+Integer.toString( photoCount[Integer.parseInt(id)])+".png");
                try
                {
                    newImage.createNewFile();
                    FileUtils.copyFile(selectedImage, newImage);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    void deleteFiles(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                FileUtils.forceDelete(c);
            }
        }
    }
    void deleteFolder(File f) throws IOException {
        FileUtils.forceDelete(f);
    }
    @Override
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        String realId = (String) buttonView.getTag(); //getResources().getResourceEntryName(buttonView.getId());

        final String switchId = realId.replaceAll("[^0-9]", "");
        id = switchId;
        if(!isChecked){
            buildExplanationDialog(switchId,buttonView);
        }
        else {
            datasToStore.get(Integer.parseInt(switchId)).switchSituation = true;
            datasToStore.get(Integer.parseInt(switchId)).explanation = "";

            // dataToStore[Integer.parseInt(switchId)].switchSituation = true;
            //dataToStore[Integer.parseInt(switchId)].explanation = "";
        }
    }
    public void textOnClick(View view){
        String realId =  getResources().getResourceEntryName(view.getId());
        final String expId = realId.replaceAll("[^0-9]", "");
        buildPhotoTakeDialog(expId);
    }
    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return images.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView mImageView;
            View v =  getLayoutInflater().inflate(R.layout.taken_photo_layout, null);
            mImageView =(ImageView) v.findViewById(R.id.tView);
            ImageButton b = (ImageButton) v.findViewById(R.id.imgBtnDelete);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    File imageToDelete = new File(images.get(position));
                    try {
                        FileUtils.forceDelete(imageToDelete);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    images.remove(position);
                    imageAdapterGridView.notifyDataSetChanged(); // deferNotifyDataSetChanged();
                }
            });

            File imgFile = new File(images.get(position));
            Picasso.with(mContext)
                    .load(imgFile)
                    .tag(mContext)
                    .resize(300,300)
                    .centerCrop()
                    .into(mImageView);
            return v;
        }
    }
}
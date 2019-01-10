package moezbenselem.translator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {

    public static String APPKEY = "trnsl.1.1.20180911T043059Z.6fd429cae7f9e78a.95ee7525d88583a2cead7c468507bfd6879b6992";
    CardView cardInput,cardOutput;
    EditText etTxt;
    TextView tvResult;
    Button btTranslate,btCopy,btHint,btDetect,btSpeach;
    ArrayList<String> listNames,listCodes;
    Spinner spIn,spOut;
    ArrayAdapter<String> spin_adapter;
    ArrayList<String> listLocales;
    String selectedIn="null",selectedOut="null",detectedLang="";
    Boolean fromSpIn=false;
    Boolean fromSpOut=false;
    TextToSpeech textToSpeech;
    private ArabicTTS tts;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        tts = new ArabicTTS();
        // Preparing the language
        tts.prepare(this);

        listLocales = new ArrayList<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            //Log.d("LOCALES", locale.getLanguage() + "_" + locale.getCountry() + " [" + locale.getDisplayName() + "]");
            listLocales.add(locale.getLanguage());
        }
        System.out.println("list locales ====");
        for (String l : listLocales) {
            //Log.d("LOCALES", locale.getLanguage() + "_" + locale.getCountry() + " [" + locale.getDisplayName() + "]");

            System.out.println(l);
        }

        //int badgeCount = 5;
        //ShortcutBadger.applyCount(this, badgeCount); //for 1.1.4+

        spIn = (Spinner)findViewById(R.id.spIn);
        spOut = (Spinner)findViewById(R.id.spOut);

        cardInput = (CardView) findViewById(R.id.card_input);
        cardOutput = (CardView) findViewById(R.id.card_output);
        tvResult = (TextView) cardOutput.findViewById(R.id.item_output);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                    System.out.println("status ==== "+status);

            }
        });

        btSpeach = (Button) cardOutput.findViewById(R.id.btSpeach);

        btSpeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String toSpeak = tvResult.getText().toString();
                if(toSpeak.length()>0) {


                    textToSpeech.setLanguage(new Locale(selectedOut));

                    if(selectedOut.equals("ar")){
                        tts.talk(toSpeak);

                    }
                    else
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }


                }

            }
        });

        btDetect =(Button) findViewById(R.id.btdetect);
        btHint =(Button) cardInput.findViewById(R.id.btHint);
        btHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spIn.setSelection(findLang(detectedLang));
                btHint.setText("");
                btHint.setVisibility(View.GONE);

            }
        });

        btTranslate = (Button)cardInput.findViewById(R.id.btTranslate);
        etTxt = (EditText) cardInput.findViewById(R.id.item_input);

        spIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedIn = listCodes.get(position);
                String txt = etTxt.getText().toString();
                fromSpIn = true;
                System.out.println("select in === "+selectedIn);
                if(selectedIn!="null" && selectedOut!="null" && txt.length()>0)
                    translate(txt,selectedIn,selectedOut);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedOut = listCodes.get(position);
                String txt = etTxt.getText().toString();
                System.out.println("select out === "+selectedOut);
                if(selectedIn!="null" && selectedOut!="null" && txt.length()>0)
                    translate(txt,selectedIn,selectedOut);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getLangueges();

        btDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etTxt.getText().toString().length()>0) {
                    detectLanguage(etTxt.getText().toString());
                    spIn.setSelection(findLang(detectedLang));
                    btHint.setText("");
                    btHint.setVisibility(View.GONE);
                }else
                    Toast.makeText(getApplicationContext(),"Write Something First !",Toast.LENGTH_LONG).show();

            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(spIn.getSelectedItemPosition()==0 && s.toString().length()>0){
                    detectLanguage(s.toString());
                }
                else
                    btHint.setVisibility(View.GONE);

            }
        };
        etTxt.addTextChangedListener(textWatcher);


        btTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt = etTxt.getText().toString();
                if(selectedIn=="null")
                {
                    Toast.makeText(getApplicationContext(),"Select an Input Language !",Toast.LENGTH_LONG).show();

                }else if(selectedOut=="null")
                    Toast.makeText(getApplicationContext(),"Select an Output Language !",Toast.LENGTH_LONG).show();
                else
                    translate(txt,selectedIn,selectedOut);
            }
        });

        btCopy = (Button) findViewById(R.id.btCopy);
        btCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(getApplication().CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", tvResult.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"Copied to Clipboard !",Toast.LENGTH_SHORT).show();

            }
        });


        MobileAds.initialize(this, "ca-app-pub-7087198421941611~8282399375");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this,ActivityABout.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getLocale(String lang){

        for (int i =0;i<listLocales.size();i++)
        {
            if(listLocales.get(i).equalsIgnoreCase(lang))
                return listLocales.get(i);
        }
        return null;
    }

    public int findLang(String lang){

        for(int i =0;i<listCodes.size();i++)
        {
            if(lang.equalsIgnoreCase(listCodes.get(i)))
                return i;

        }
        return -1;
    }

    public void detectLanguage(final String text){

        try {

            String url = "https://translate.yandex.net/api/v1.5/tr.json/detect";
            final StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            System.out.println("result === \n" + response);
                            ArrayList<Language> listLang = new ArrayList<>();
                            try {
                                //JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                detectedLang = jsonObject.getString("lang");
                                btHint.setText(listNames.get(findLang(detectedLang)));
                                btHint.setVisibility(View.VISIBLE);



                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                            error.printStackTrace();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new Hashtable<>();
                    params.put("key",APPKEY);
                    params.put("text",text);

                    return params;
                }
            };

            {
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }


    }

    public void translate(final String txt, String from, String to){

        try {
            final String lang = from+"-"+to;
            String url = "https://translate.yandex.net/api/v1.5/tr.json/translate";
            final StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            System.out.println("result === \n" + response);
                            ArrayList<Language> listLang = new ArrayList<>();
                            try {
                                //JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                String result2 = jsonObject.get("text").toString()
                                        .replaceAll("\"","").replaceAll("\\[","").replaceAll("\\]","").replaceAll("\\\\n","\n");
                                String result = jsonObject.get("text").toString()
                                        .replaceAll("\"","").replaceAll("\\[","").replaceAll("\\]","");
                                System.out.println("translation == " + result2);
                                tvResult.setText(result);
                                tvResult.setText(tvResult.getText().toString().replaceAll("\\\\n","\n"));
                                cardOutput.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                            error.printStackTrace();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new Hashtable<>();
                    params.put("key",APPKEY);
                    params.put("text",txt);
                    params.put("lang",lang);

                    return params;
                }
            };

            {
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    public void getLangueges(){


        try {

            String url = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20180911T043059Z.6fd429cae7f9e78a.95ee7525d88583a2cead7c468507bfd6879b6992&ui=ar";
            final StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            //System.out.println("result === \n" + response);
                            ArrayList<Language> listLang = new ArrayList<>();
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                String langs = jsonObject.get("langs").toString()
                                        .replaceAll("\"","").replaceAll("\\{","").replaceAll("\\}","");
                                System.out.println("list langs == " + jsonObject.get("langs").toString());
                                ArrayList<String> splitLangs = new ArrayList<>(Arrays.asList(langs.split(",")));
                                for (int i=0;i<splitLangs.size();i++){
                                    //System.out.println(splitLangs.get(i));
                                    Language l = new Language(splitLangs.get(i).split(":")[0],splitLangs.get(i).split(":")[1]);
                                    listLang.add(l);

                                }

                                Collections.sort(listLang,
                                        new Comparator<Language>() {
                                            @Override
                                            public int compare(Language s1, Language s2) {
                                                return s1.name.compareToIgnoreCase(s2.name);
                                            }
                                        });
                                listCodes = new ArrayList<>();
                                listNames = new ArrayList<>();
                                listCodes.add("null");
                                listNames.add("CHOOSE LANGUAGE");

                                for (int i=0;i<listLang.size();i++){
                                    System.out.println("code == "+listLang.get(i).code);
                                    System.out.println("name == "+listLang.get(i).name);
                                    listCodes.add(listLang.get(i).code);
                                    listNames.add(listLang.get(i).name);
                                }

                                spin_adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout, listNames);
                                spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spIn.setAdapter(spin_adapter);
                                spOut.setAdapter(spin_adapter);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                            error.printStackTrace();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new Hashtable<>();

                    return params;
                }
            };

            {
                int socketTimeout = 30000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                System.out.println("status ==== "+status);

            }
        });
    }

    @Override
    protected void onPause() {
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
}
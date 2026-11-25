package com.fls.servicedesk;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Toast;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final int SAVE_XLSX = 71;
    private static final int NOTIFICATION_PERMISSION_REQUEST = 72;
    private static final String NOTIFICATION_CHANNEL = "fls_assignments";
    private static final String FLS_LAN_HOST = "192.168.1.33";
    private static final String DEFAULT_SERVER = "http://192.168.1.33:8788";
    private WebView web;
    private EditText address;
    private LinearLayout errorPanel;
    private ScrollView errorScroll;
    private TextView errorMessage;
    private String pendingBase64;
    private boolean pageLoaded;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable loadTimeout = () -> showLoadError("The FLS server did not respond in time. Check the server address and make sure your phone can reach it.");

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        createNotificationChannel();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        String saved = getPreferences(MODE_PRIVATE).getString("server", "");
        Uri savedUri = Uri.parse(saved);
        if (FLS_LAN_HOST.equals(savedUri.getHost()) && (savedUri.getPort() == 8787 || savedUri.getPort() == 8789 || (savedUri.getPort() == 8788 && "https".equalsIgnoreCase(savedUri.getScheme())))) {
            saved = savedUri.buildUpon().scheme("http").authority(FLS_LAN_HOST+":8788").build().toString().replaceAll("/+$", "");
            getPreferences(MODE_PRIVATE).edit().putString("server", saved).apply();
        }
        if (saved.isEmpty()) {
            saved = DEFAULT_SERVER;
            getPreferences(MODE_PRIVATE).edit().putString("server", saved).apply();
        }
        showApp(saved);
    }

    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density); }

    private void showServerForm(String current) {
        handler.removeCallbacks(loadTimeout);
        if(web!=null)web.stopLoading();
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(Color.rgb(244,247,244));
        LinearLayout stage = new LinearLayout(this); stage.setGravity(Gravity.CENTER); stage.setPadding(dp(16),dp(16),dp(16),dp(16));
        stage.setOnApplyWindowInsetsListener((view,insets)->{view.setPadding(dp(16),dp(16)+insets.getSystemWindowInsetTop(),dp(16),dp(16)+insets.getSystemWindowInsetBottom());return insets.consumeSystemWindowInsets();});
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(22),dp(24),dp(22),dp(20));
        android.graphics.drawable.GradientDrawable cardBg=new android.graphics.drawable.GradientDrawable();cardBg.setColor(Color.WHITE);cardBg.setCornerRadius(dp(18));card.setBackground(cardBg);
        TextView title = new TextView(this); title.setText("FLS Service Desk"); title.setTextSize(24); title.setTextColor(Color.rgb(16,37,31)); card.addView(title);
        TextView info = new TextView(this); info.setText("Enter the FLS server address. On the same Wi-Fi, use your server computer's LAN address, for example http://192.168.1.33:8788. Use the HTTPS address after hosting."); info.setTextSize(14); info.setTextColor(Color.rgb(75,91,82)); info.setPadding(0,dp(10),0,dp(12)); card.addView(info);
        address = new EditText(this); address.setSingleLine(true); address.setTextSize(16); address.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_URI); address.setHint("https://your-fls-server.example.com"); address.setText(current); card.addView(address);
        Button connect = new Button(this); connect.setText("Save and connect"); connect.setAllCaps(false); connect.setOnClickListener(v -> saveServer()); card.addView(connect);
        int maxWidth=Math.min(Math.max(dp(280),getResources().getDisplayMetrics().widthPixels-dp(32)),dp(560));
        scroll.addView(stage,new ScrollView.LayoutParams(-1,-1));stage.addView(card,new LinearLayout.LayoutParams(maxWidth,-2));setContentView(scroll);
    }
    private void saveServer() {
        String value = address.getText().toString().trim();
        if (!value.startsWith("http://") && !value.startsWith("https://")) value = "https://" + value;
        Uri parsed = Uri.parse(value);
        if (parsed.getHost() == null) { address.setError("Enter a valid server address"); return; }
        value = value.replaceAll("/+$", ""); getPreferences(MODE_PRIVATE).edit().putString("server", value).apply(); showApp(value);
    }

    private void showApp(String base) {
        final int dark = Color.rgb(16,37,31);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(dark);
        root.setOnApplyWindowInsetsListener((view,insets) -> {
            view.setPadding(0,insets.getSystemWindowInsetTop(),0,insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });
        LinearLayout bar = new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(dp(8),0,dp(8),0); bar.setBackgroundColor(dark);
        ImageView logo = new ImageView(this); logo.setImageResource(R.drawable.fls_logo); logo.setScaleType(ImageView.ScaleType.FIT_CENTER); bar.addView(logo,new LinearLayout.LayoutParams(dp(38),dp(42))); TextView brand = new TextView(this); brand.setText("FLS SERVICE"); brand.setTextColor(Color.WHITE); brand.setTextSize(14); brand.setPadding(dp(8),0,0,0); bar.addView(brand,new LinearLayout.LayoutParams(0,dp(50),1));
        Button reload = new Button(this); reload.setText("↻"); reload.setAllCaps(false); reload.setTextSize(18); reload.setMinWidth(0); reload.setMinimumWidth(0); reload.setPadding(dp(10),0,dp(10),0); reload.setBackgroundTintList(ColorStateList.valueOf(dark)); reload.setTextColor(Color.WHITE); reload.setContentDescription("Reload FLS service"); reload.setOnClickListener(v -> { if(web!=null){showConnecting();web.reload();} }); bar.addView(reload);
        root.addView(bar);

        errorScroll = new ScrollView(this); errorScroll.setFillViewport(true); errorScroll.setBackgroundColor(Color.rgb(244,247,244));
        errorPanel = new LinearLayout(this); errorPanel.setOrientation(LinearLayout.VERTICAL); errorPanel.setGravity(Gravity.CENTER); errorPanel.setPadding(dp(16),dp(20),dp(16),dp(20)); errorPanel.setBackgroundColor(Color.rgb(244,247,244));
        LinearLayout errorCard=new LinearLayout(this);errorCard.setOrientation(LinearLayout.VERTICAL);errorCard.setGravity(Gravity.CENTER_HORIZONTAL);errorCard.setPadding(dp(22),dp(24),dp(22),dp(20));android.graphics.drawable.GradientDrawable errorBg=new android.graphics.drawable.GradientDrawable();errorBg.setColor(Color.WHITE);errorBg.setCornerRadius(dp(18));errorCard.setBackground(errorBg);
        TextView heading = new TextView(this); heading.setText("Connecting to FLS Service Desk"); heading.setTextSize(21); heading.setGravity(Gravity.CENTER); heading.setTextColor(Color.rgb(16,37,31)); errorCard.addView(heading,new LinearLayout.LayoutParams(-1,-2));
        errorMessage = new TextView(this); errorMessage.setText("Loading your service workspace…"); errorMessage.setTextSize(14); errorMessage.setTextColor(Color.rgb(75,91,82)); errorMessage.setGravity(Gravity.CENTER); errorMessage.setPadding(0,dp(12),0,dp(12)); errorCard.addView(errorMessage,new LinearLayout.LayoutParams(-1,-2));
        Button retry = new Button(this); retry.setText("Try again"); retry.setAllCaps(false); retry.setOnClickListener(v -> { showConnecting(); if(web!=null)web.reload(); }); errorCard.addView(retry,new LinearLayout.LayoutParams(-1,-2));
        Button change = new Button(this); change.setText("Change server address"); change.setAllCaps(false); change.setOnClickListener(v -> showServerForm(getPreferences(MODE_PRIVATE).getString("server", ""))); errorCard.addView(change,new LinearLayout.LayoutParams(-1,-2));
        int maxErrorWidth=Math.min(Math.max(dp(280),getResources().getDisplayMetrics().widthPixels-dp(32)),dp(560));
        errorPanel.addView(errorCard,new LinearLayout.LayoutParams(maxErrorWidth,-2));errorScroll.addView(errorPanel,new ScrollView.LayoutParams(-1,-1));
        root.addView(errorScroll,new LinearLayout.LayoutParams(-1,0,1));
        web = new WebView(this); web.setBackgroundColor(Color.rgb(244,247,244)); web.setVisibility(WebView.GONE);
        WebSettings settings = web.getSettings(); settings.setJavaScriptEnabled(true); settings.setDomStorageEnabled(true); settings.setSupportMultipleWindows(false); settings.setAllowFileAccess(false); settings.setAllowContentAccess(false); settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        CookieManager.getInstance().setAcceptCookie(true); CookieManager.getInstance().setAcceptThirdPartyCookies(web,false);
        web.addJavascriptInterface(new NativeBridge(),"FlsAndroid");
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request) {
                Uri target=request.getUrl(),origin=Uri.parse(getPreferences(MODE_PRIVATE).getString("server",""));
                if (("http".equals(target.getScheme())||"https".equals(target.getScheme()))&&target.getHost()!=null&&target.getHost().equalsIgnoreCase(origin.getHost())) return false;
                if ("http".equals(target.getScheme())||"https".equals(target.getScheme())) { try { startActivity(new Intent(Intent.ACTION_VIEW,target)); } catch(Exception ignored){} }
                return true;
            }
            @Override public void onPageStarted(WebView view,String url,android.graphics.Bitmap favicon) { super.onPageStarted(view,url,favicon); pageLoaded=false; showConnecting(); handler.removeCallbacks(loadTimeout); handler.postDelayed(loadTimeout,15000); }
            @Override public void onPageCommitVisible(WebView view,String url) { super.onPageCommitVisible(view,url); pageLoaded=true; handler.removeCallbacks(loadTimeout); rootErrorVisibility(false); web.setVisibility(WebView.VISIBLE); }
            @Override public void onPageFinished(WebView view,String url) {
                super.onPageFinished(view,url); pageLoaded=true; handler.removeCallbacks(loadTimeout); rootErrorVisibility(false); web.setVisibility(WebView.VISIBLE);
                view.evaluateJavascript("(function(){function hook(){if(window.XLSX&&!XLSX.__fls){var old=XLSX.writeFile;XLSX.writeFile=function(w,n,o){try{FlsAndroid.saveExcel(n||'FLS-Service-Activity.xlsx',XLSX.write(w,{bookType:'xlsx',type:'base64'}));}catch(e){if(old)old.call(XLSX,w,n,o);}};XLSX.__fls=true;return true;}return false;}if(!hook()){var tries=0,t=setInterval(function(){if(hook()||++tries>100)clearInterval(t);},200);}})();",null);
            }
            @Override public void onReceivedHttpError(WebView view,WebResourceRequest request,android.webkit.WebResourceResponse response) { super.onReceivedHttpError(view,request,response); if(request.isForMainFrame()&&response.getStatusCode()>=400){pageLoaded=false;handler.removeCallbacks(loadTimeout);showLoadError("The FLS server returned HTTP "+response.getStatusCode()+". Check the server address.");} }
            @Override public void onReceivedError(WebView view,WebResourceRequest request,WebResourceError error) {
                if(request.isForMainFrame()) { pageLoaded=false; handler.removeCallbacks(loadTimeout); showLoadError("Cannot reach "+getPreferences(MODE_PRIVATE).getString("server","the FLS server")+". Check that the phone and server are on the same Wi-Fi, the server is running, and the address includes the correct port. ("+error.getDescription()+")"); }
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onConsoleMessage(ConsoleMessage message) { if(message.messageLevel()==ConsoleMessage.MessageLevel.ERROR)Log.e("FLS-WebView",message.message()+" at "+message.sourceId()+":"+message.lineNumber()); return true; }
            @Override public boolean onJsAlert(WebView v,String u,String msg,JsResult result) { new AlertDialog.Builder(MainActivity.this).setMessage(msg).setPositiveButton("OK",(d,w)->result.confirm()).setOnCancelListener(d->result.cancel()).show(); return true; }
            @Override public boolean onJsPrompt(WebView v,String u,String msg,String defaultValue,JsPromptResult result) {
                EditText link=new EditText(MainActivity.this); link.setText(defaultValue==null?"":defaultValue); link.setSelectAllOnFocus(true);
                new AlertDialog.Builder(MainActivity.this).setTitle(msg).setView(link)
                    .setPositiveButton("Copy link",(d,w)->{String text=link.getText().toString();copy(text);result.confirm(text);})
                    .setNeutralButton("Share",(d,w)->{String text=link.getText().toString();copy(text);Intent send=new Intent(Intent.ACTION_SEND);send.setType("text/plain");send.putExtra(Intent.EXTRA_TEXT,text);startActivity(Intent.createChooser(send,"Share assistant link"));result.confirm(text);})
                    .setNegativeButton("Cancel",(d,w)->result.cancel()).setOnCancelListener(d->result.cancel()).show(); return true;
            }
        });
        root.addView(web,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root); showConnecting(); web.loadUrl(base+"/");
    }

    private void rootErrorVisibility(boolean show) { if(errorScroll!=null)errorScroll.setVisibility(show?ScrollView.VISIBLE:ScrollView.GONE); }
    private void showConnecting() { if(errorPanel!=null&&errorMessage!=null){rootErrorVisibility(true);errorMessage.setText("Loading your service workspace…");if(web!=null)web.setVisibility(WebView.GONE);} }
    private void showLoadError(String text) { if(errorPanel!=null&&errorMessage!=null){rootErrorVisibility(true);errorMessage.setText(text);if(web!=null)web.setVisibility(WebView.GONE);} }
    private void createNotificationChannel() { if(Build.VERSION.SDK_INT>=26){NotificationManager manager=getSystemService(NotificationManager.class);if(manager!=null)manager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL,"FLS service assignments",NotificationManager.IMPORTANCE_DEFAULT));} }
    private boolean notificationsAllowed() { return Build.VERSION.SDK_INT<33||checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED; }
    private void requestNotificationPermission() { if(Build.VERSION.SDK_INT>=33&&!notificationsAllowed())requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},NOTIFICATION_PERMISSION_REQUEST);else Toast.makeText(this,"FLS notifications are enabled",Toast.LENGTH_SHORT).show(); }
    private void postAssignmentNotification(String title,String message) { if(!notificationsAllowed())return;Notification notification=new Notification.Builder(this,NOTIFICATION_CHANNEL).setSmallIcon(android.R.drawable.stat_notify_more).setContentTitle(title).setContentText(message).setStyle(new Notification.BigTextStyle().bigText(message)).setAutoCancel(true).build();NotificationManager manager=getSystemService(NotificationManager.class);if(manager!=null)manager.notify((title+message).hashCode(),notification); }
    private void copy(String value) { ClipboardManager c=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);c.setPrimaryClip(ClipData.newPlainText("FLS assistant link",value));Toast.makeText(this,"Link copied",Toast.LENGTH_SHORT).show(); }

    private class NativeBridge {
        @JavascriptInterface public void saveExcel(String name,String contents) {
            runOnUiThread(()->{pendingBase64=contents;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");i.putExtra(Intent.EXTRA_TITLE,name==null||name.isEmpty()?"FLS-Service-Activity.xlsx":name);try{startActivityForResult(i,SAVE_XLSX);}catch(Exception e){Toast.makeText(MainActivity.this,"Could not open Save dialog",Toast.LENGTH_LONG).show();}});
        }
        @JavascriptInterface public void requestNotifications() { runOnUiThread(()->requestNotificationPermission()); }
        @JavascriptInterface public boolean notificationsAllowed() { return MainActivity.this.notificationsAllowed(); }
        @JavascriptInterface public void showNotification(String title,String message) { runOnUiThread(()->postAssignmentNotification(title==null?"FLS service update":title,message==null?"A vehicle has been assigned to you.":message)); }
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==NOTIFICATION_PERMISSION_REQUEST&&web!=null){String message=notificationsAllowed()?"Notifications enabled. Keep the app open to receive assignment alerts.":"Notifications are off. Assigned vehicles still appear in the app.";String js="var n=document.getElementById('assistantNotificationStatus');if(n)n.textContent="+org.json.JSONObject.quote(message)+";";web.evaluateJavascript(js,null);}
    }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==SAVE_XLSX&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) {
            try(OutputStream out=getContentResolver().openOutputStream(data.getData())) { if(out==null)throw new Exception("No output stream");out.write(Base64.decode(pendingBase64,Base64.DEFAULT));Toast.makeText(this,"Excel file saved",Toast.LENGTH_LONG).show(); }
            catch(Exception e){Toast.makeText(this,"Could not save Excel file",Toast.LENGTH_LONG).show();}
        }
        pendingBase64=null;
    }

    @Override protected void onResume() { super.onResume(); if(web!=null)web.evaluateJavascript("if(typeof refresh==='function')refresh()",null); }
    @Override public void onBackPressed() { if(web!=null&&web.getUrl()!=null&&web.getUrl().contains("service=")){web.evaluateJavascript("if(typeof backToAssistantJobs==='function')backToAssistantJobs()",null);return;} if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed(); }
    @Override protected void onDestroy() { handler.removeCallbacks(loadTimeout); if(web!=null){web.stopLoading();web.destroy();web=null;} super.onDestroy(); }
}

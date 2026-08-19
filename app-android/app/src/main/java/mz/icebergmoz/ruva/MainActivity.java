package mz.icebergmoz.ruva;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_REQUEST = 1001;
    private static final String HOME = "https://donicebergmoz-lgtm.github.io/estudos-online-iceberg-moz/";

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        showSplash();
        new Handler(Looper.getMainLooper()).postDelayed(this::showWeb, 1800);
    }

    private void showSplash() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(Color.rgb(247,250,255));

        ImageView logo = new ImageView(this);
        logo.setImageResource(getResources().getIdentifier("splash_books_pen", "drawable", getPackageName()));
        logo.setContentDescription("Livros e caneta");
        root.addView(logo, new LinearLayout.LayoutParams(180, 180));

        TextView title = new TextView(this);
        title.setText("ICEBERG MOZ RUVA");
        title.setTextColor(Color.rgb(8,38,78));
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 12, 0, 4);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView subtitle = new TextView(this);
        subtitle.setText("Estudos UCM");
        subtitle.setTextColor(Color.rgb(18,101,216));
        subtitle.setTextSize(18);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, -2));

        TextView slogan = new TextView(this);
        slogan.setText("Materiais de estudo, exames e conhecimento");
        slogan.setTextColor(Color.rgb(70,90,115));
        slogan.setTextSize(14);
        slogan.setGravity(Gravity.CENTER);
        slogan.setPadding(0, 16, 0, 0);
        root.addView(slogan, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
    }

    private void showWeb() {
        webView = new WebView(this);
        setContentView(webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true); s.setAllowContentAccess(true); s.setSupportZoom(false);
        s.setUserAgentString(s.getUserAgentString()+" IcebergMozRuva/1.1");
        webView.setBackgroundColor(Color.rgb(247,250,255));
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){ return openExternalIfNeeded(r.getUrl().toString()); }
            @Override public boolean shouldOverrideUrlLoading(WebView v, String url){ return openExternalIfNeeded(url); }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p){
                if(fileCallback!=null) fileCallback.onReceiveValue(null); fileCallback=cb;
                try { startActivityForResult(p.createIntent(), FILE_REQUEST); } catch(Exception e){ fileCallback=null; return false; }
                return true;
            }
        });
        webView.setDownloadListener((url,userAgent,contentDisposition,mimeType,contentLength)->{
            try { startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))); } catch(Exception ignored) {}
        });
        webView.loadUrl(HOME);
    }

    private boolean openExternalIfNeeded(String url){
        Uri u=Uri.parse(url); String host=u.getHost();
        if(host==null || host.endsWith("github.io")) return false;
        try { startActivity(new Intent(Intent.ACTION_VIEW,u)); } catch(Exception ignored) {}
        return true;
    }

    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(request==FILE_REQUEST && fileCallback!=null){
            Uri[] r=null;
            if(result==RESULT_OK && data!=null){
                if(data.getClipData()!=null){ int n=data.getClipData().getItemCount(); r=new Uri[n]; for(int i=0;i<n;i++) r[i]=data.getClipData().getItemAt(i).getUri(); }
                else if(data.getData()!=null) r=new Uri[]{data.getData()};
            }
            fileCallback.onReceiveValue(r); fileCallback=null;
        }
    }

    @Override public void onBackPressed(){ if(webView!=null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
}

package mz.icebergmoz.ruva;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
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
        webView = new WebView(this);
        setContentView(webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true); s.setAllowContentAccess(true); s.setSupportZoom(false);
        s.setUserAgentString(s.getUserAgentString()+" IcebergMozRuva/1.0");
        webView.setBackgroundColor(Color.rgb(247,250,255));
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){
                return openExternalIfNeeded(r.getUrl().toString());
            }
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
            Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse(url)); startActivity(i);
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
            Uri[] r=null; if(result==RESULT_OK && data!=null){ if(data.getClipData()!=null){ int n=data.getClipData().getItemCount(); r=new Uri[n]; for(int i=0;i<n;i++) r[i]=data.getClipData().getItemAt(i).getUri(); } else if(data.getData()!=null) r=new Uri[]{data.getData()}; }
            fileCallback.onReceiveValue(r); fileCallback=null;
        }
    }
    @Override public void onBackPressed(){ if(webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
}

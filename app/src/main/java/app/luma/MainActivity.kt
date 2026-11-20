package app.luma
import android.app.Activity
import android.os.Bundle
import android.webkit.*
import android.view.View
import android.content.Intent
import android.net.Uri

class MainActivity : Activity() {
 private var uploadCallback: ValueCallback<Array<Uri>>? = null
 private val pickCode = 4102
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  window.statusBarColor = android.graphics.Color.rgb(7,7,11)
  window.navigationBarColor = android.graphics.Color.rgb(7,7,11)
  window.decorView.systemUiVisibility = 0
  val web = WebView(this)
  web.setBackgroundColor(android.graphics.Color.rgb(7,7,11))
  web.settings.javaScriptEnabled = true
  web.settings.domStorageEnabled = true
  web.settings.allowFileAccess = true
  web.settings.allowContentAccess = true
  web.settings.mediaPlaybackRequiresUserGesture = false
  web.settings.setSupportZoom(false)
  web.webViewClient = object: WebViewClient() {
   override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
  }
  web.webChromeClient = object: WebChromeClient() {
   override fun onShowFileChooser(w: WebView?, cb: ValueCallback<Array<Uri>>?, params: FileChooserParams?): Boolean {
    uploadCallback?.onReceiveValue(null); uploadCallback = cb
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE); type = "*/*"; putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*","video/*","audio/*")); putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true) }
    startActivityForResult(intent,pickCode); return true
   }
  }
  setContentView(web)
  web.loadUrl("file:///android_asset/index.html")
 }
 override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) {
  super.onActivityResult(requestCode,resultCode,data)
  if(requestCode==pickCode) { val result = if(resultCode==RESULT_OK && data!=null) { val clip=data.clipData; if(clip!=null) Array(clip.itemCount){clip.getItemAt(it).uri} else data.data?.let{arrayOf(it)} } else null; uploadCallback?.onReceiveValue(result); uploadCallback=null }
 }
 override fun onBackPressed() { val v=findViewById<View>(android.R.id.content); val web=(v as? android.view.ViewGroup)?.getChildAt(0) as? WebView; if(web?.canGoBack()==true) web.goBack() else super.onBackPressed() }
}
package app.luma
import android.os.Bundle
import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import java.util.Date

private val accent=Color(0xFFB52BFF); private val card=Color(0xFF17171D)
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{LumaApp()}}}
data class Project(val title:String,val media:String="")
@Composable fun LumaApp(){
 val context=LocalContext.current;val pref=remember{context.getSharedPreferences("luma",Context.MODE_PRIVATE)}
 var screen by remember{mutableStateOf("Projects")};var current by remember{mutableStateOf(Project("Untitled"))};var list by remember{mutableStateOf(pref.getString("projects","")!!.split("\n").filter{it.isNotBlank()})};var dialog by remember{mutableStateOf(false)};var name by remember{mutableStateOf("")};var light by remember{mutableStateOf(pref.getBoolean("light",false))};var pro by remember{mutableStateOf(pref.getBoolean("pro",false))};var snap by remember{mutableStateOf(pref.getBoolean("snap",true))};var auto by remember{mutableStateOf(pref.getBoolean("auto",true))};var vertical by remember{mutableStateOf(pref.getBoolean("vertical",false))};var media by remember{mutableStateOf("")};var quality by remember{mutableStateOf("Auto")};var fps by remember{mutableIntStateOf(30)}
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)){u->if(u.isNotEmpty()){media=u.first().toString();current=Project("Imported edit",media);list=list+current.title;pref.edit().putString("projects",list.joinToString("\n")).apply();screen="Editor"}}
 fun bool(key:String,value:Boolean){pref.edit().putBoolean(key,value).apply()}
 MaterialTheme(colorScheme=if(light)lightColorScheme(primary=Color(0xFF8123B8)) else darkColorScheme(primary=accent,background=Color(0xFF09090C),surface=card)){
 Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(14.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Row(verticalAlignment=Alignment.CenterVertically){Box(Modifier.width(5.dp).height(20.dp).background(accent));Spacer(Modifier.width(3.dp));Box(Modifier.width(5.dp).height(26.dp).background(Color(0xFF8300C3));Spacer(Modifier.width(3.dp));Box(Modifier.width(5.dp).height(20.dp).background(Color(0xFFD000A8)))};Spacer(Modifier.width(8.dp));Text("Luma",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Spacer(Modifier.weight(1f));IconButton(onClick={screen="Settings"}){Icon(Icons.Outlined.Settings,"Settings")}}
 when(screen){
 "Projects"->Column(Modifier.weight(1f)){Text("Projects",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold);Text("Local editing",color=Color.Gray);Row{Button(onClick={dialog=true},colors=ButtonDefaults.buttonColors(containerColor=accent)){Text("+ New project")};OutlinedButton(onClick={picker.launch(ActivityResultContracts.PickVisualMedia.ImageAndVideo)}){Text("Import media")}};Spacer(Modifier.height(12.dp));if(list.isEmpty())Box(Modifier.fillMaxSize(),Alignment.Center){Text("No projects yet",color=Color.Gray)}else LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(list){p->Row(Modifier.fillMaxWidth().background(card,RoundedCornerShape(10.dp)).clickable{current=Project(p);screen="Editor"}.padding(12.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(70.dp,48.dp).background(Color(0xFF28232E),RoundedCornerShape(6.dp)),Alignment.Center){Icon(Icons.Outlined.PlayArrow,"",tint=accent)};Spacer(Modifier.width(10.dp));Column{Text(p,fontWeight=FontWeight.SemiBold);Text("Timeline project",style=MaterialTheme.typography.labelSmall,color=Color.Gray)}}}}}
 "Editor"->Column(Modifier.weight(1f)){Row(verticalAlignment=Alignment.CenterVertically){IconButton(onClick={screen="Projects"}){Icon(Icons.Outlined.ArrowBack,"Back")};Text(current.title,Modifier.weight(1f),fontWeight=FontWeight.Bold);TextButton(onClick={exportFile(context)}){Text("Export",color=accent)}};Box(Modifier.fillMaxWidth().weight(.48f).background(Color.Black,RoundedCornerShape(9.dp)),Alignment.Center){Text(if(media.isEmpty())"Preview" else "Media preview placeholder",color=Color.Gray)};Row(Modifier.fillMaxWidth().padding(8.dp),horizontalArrangement=Arrangement.Center){Icon(Icons.Outlined.SkipPrevious,"");Icon(Icons.Outlined.PlayArrow,"",Modifier.padding(horizontal=18.dp),tint=accent);Icon(Icons.Outlined.SkipNext,"");Text(" 00:00:00")};Text("Timeline",fontWeight=FontWeight.Bold);Column(Modifier.weight(.32f).fillMaxWidth().background(card,RoundedCornerShape(8.dp)).padding(8.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){listOf("Video","Overlay","Audio","Text").forEach{Row(verticalAlignment=Alignment.CenterVertically){Text(it,Modifier.width(60.dp),color=Color.Gray);Box(Modifier.weight(1f).height(30.dp).background(Color(0xFF28232E),RoundedCornerShape(4.dp)))}}};Row(Modifier.horizontalScroll(rememberScrollState())){listOf("Edit","Effects","Text","Audio","Color","Speed","Masks","Transitions","Motion","Captions").forEach{FilterChip(selected=false,onClick={},label={Text(it)})}}}
 "Templates"->Column(Modifier.weight(1f)){Text("Templates",style=MaterialTheme.typography.headlineLarge);listOf("Vertical 9:16","Cinematic 16:9","Square 1:1","Slideshow","Gaming montage").forEach{Text(it,Modifier.fillMaxWidth().padding(vertical=6.dp).background(card,RoundedCornerShape(9.dp)).clickable{dialog=true}.padding(15.dp))}}
 else->Column(Modifier.weight(1f)){Text("Settings",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold);Setting("Light theme",light){light=it;bool("light",it)};Setting("Pro mode",pro){pro=it;bool("pro",it)};Setting("Vertical timeline",vertical){vertical=it;bool("vertical",it)};Setting("Autosave",auto){auto=it;bool("auto",it)};Setting("Magnetic snapping",snap){snap=it;bool("snap",it)};Text("Preview quality");Row{listOf("Auto","480p","720p","1080p").forEach{FilterChip(quality==it,{quality=it},label={Text(it)})}};Text("Export FPS: $fps");Slider(fps.toFloat(),{fps=it.toInt()},valueRange=24f..60f,steps=5);Text("Offline · no AI · no ads · no subscriptions",color=Color.Gray)}}
 NavigationBar{listOf("Projects","Editor","Templates","Settings").forEach{n->NavigationBarItem(screen==n,{screen=n},icon={Icon(when(n){"Projects"->Icons.Outlined.VideoLibrary;"Editor"->Icons.Outlined.Movie;"Templates"->Icons.Outlined.AutoAwesome;else->Icons.Outlined.Settings},n)},label={Text(n)})}}
 }
 if(dialog)AlertDialog(onDismissRequest={dialog=false},title={Text("New project")},text={OutlinedTextField(name,{name=it},label={Text("Project name")})},confirmButton={TextButton(onClick={current=Project(name.ifBlank{"Untitled"});list=list+current.title;pref.edit().putString("projects",list.joinToString("\n")).apply();name="";dialog=false;screen="Editor"}){Text("Create")}},dismissButton={TextButton(onClick={dialog=false}){Text("Cancel")}})
}
@Composable private fun Setting(t:String,v:Boolean,on:(Boolean)->Unit){Row(Modifier.fillMaxWidth().background(card,RoundedCornerShape(8.dp)).padding(12.dp),verticalAlignment=Alignment.CenterVertically){Text(t,Modifier.weight(1f));Switch(v,on)}}
private fun exportFile(ctx:Context){val dir=ctx.getExternalFilesDir("exports")?:ctx.filesDir;dir.mkdirs();File(dir,"Luma_export_placeholder.txt").writeText("Stage 1: export placeholder, render pipeline not yet implemented. "+Date())}

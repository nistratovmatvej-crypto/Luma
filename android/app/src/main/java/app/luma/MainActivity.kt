package app.luma

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import java.util.Date

private val LumaAccent = Color(0xFFB52BFF)
private val LumaPanel = Color(0xFF17171D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LumaApp() }
    }
}

@Composable
fun LumaApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("luma", Context.MODE_PRIVATE) }
    var page by remember { mutableStateOf("Projects") }
    var projectName by remember { mutableStateOf("Untitled") }
    var mediaUri by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf(prefs.getString("projects", "")!!.split("\\n").filter { it.isNotBlank() }) }
    var newProjectDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    var light by remember { mutableStateOf(prefs.getBoolean("light", false)) }
    var pro by remember { mutableStateOf(prefs.getBoolean("pro", false)) }
    var vertical by remember { mutableStateOf(prefs.getBoolean("vertical", false)) }
    var autosave by remember { mutableStateOf(prefs.getBoolean("autosave", true)) }
    var snapping by remember { mutableStateOf(prefs.getBoolean("snapping", true)) }
    var previewQuality by remember { mutableStateOf(prefs.getString("preview", "Auto") ?: "Auto") }
    var fps by remember { mutableIntStateOf(prefs.getInt("fps", 30)) }

    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
        if (uris.isNotEmpty()) {
            mediaUri = uris.first().toString()
            projectName = "Imported edit"
            projects = projects + projectName
            prefs.edit().putString("projects", projects.joinToString("\\n")).apply()
            page = "Editor"
        }
    }
    fun saveBool(key: String, value: Boolean) { prefs.edit().putBoolean(key, value).apply() }

    MaterialTheme(colorScheme = if (light) lightColorScheme(primary = Color(0xFF8123B8)) else darkColorScheme(primary = LumaAccent, background = Color(0xFF09090C), surface = LumaPanel)) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(14.dp)) {
            LumaHeader { page = "Settings" }
            when (page) {
                "Projects" -> ProjectsPage(projects, onCreate = { newProjectDialog = true }, onImport = { mediaPicker.launch(ActivityResultContracts.PickVisualMedia.ImageAndVideo) }, onOpen = { projectName = it; page = "Editor" })
                "Editor" -> EditorPage(projectName, mediaUri, pro, onBack = { page = "Projects" }, onExport = { exportPlaceholder(context) }, onImport = { mediaPicker.launch(ActivityResultContracts.PickVisualMedia.ImageAndVideo) })
                "Templates" -> TemplatesPage { newProjectDialog = true }
                else -> SettingsPage(light, { light = it; saveBool("light", it) }, pro, { pro = it; saveBool("pro", it) }, vertical, { vertical = it; saveBool("vertical", it) }, autosave, { autosave = it; saveBool("autosave", it) }, snapping, { snapping = it; saveBool("snapping", it) }, previewQuality, { previewQuality = it; prefs.edit().putString("preview", it).apply() }, fps, { fps = it; prefs.edit().putInt("fps", it).apply() })
            }
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                listOf("Projects", "Editor", "Templates", "Settings").forEach { item ->
                    NavigationBarItem(selected = page == item, onClick = { page = item }, icon = { Icon(navIcon(item)), item }, label = { Text(item) })
                }
            }
        }
        if (newProjectDialog) {
            AlertDialog(onDismissRequest = { newProjectDialog = false }, title = { Text("New project") }, text = { OutlinedTextField(nameInput, { nameInput = it }, label = { Text("Project name") }, singleLine = true) }, confirmButton = {
                TextButton(onClick = { projectName = nameInput.ifBlank { "Untitled" }; projects = projects + projectName; prefs.edit().putString("projects", projects.joinToString("\\n")).apply(); nameInput = ""; newProjectDialog = false; page = "Editor" }) { Text("Create") }
            }, dismissButton = { TextButton(onClick = { newProjectDialog = false }) { Text("Cancel") } })
        }
    }
}

@Composable private fun LumaHeader(onSettings: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(Modifier.width(5.dp).height(20.dp).background(Color(0xFFB000E8)))
            Box(Modifier.width(5.dp).height(26.dp).background(Color(0xFF8100C5)))
            Box(Modifier.width(5.dp).height(20.dp).background(Color(0xFFD000A8)))
        }
        Spacer(Modifier.width(9.dp)); Text("Luma", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f)); IconButton(onClick = onSettings) { Icon(Icons.Outlined.Settings, "Settings") }
    }
}
private fun navIcon(name: String) = when(name) {
    "Projects" -> Icons.Outlined.VideoLibrary
    "Editor" -> Icons.Outlined.Movie
    "Templates" -> Icons.Outlined.AutoAwesome
    else -> Icons.Outlined.Settings
}

@Composable private fun ProjectsPage(projects: List<String>, onCreate: () -> Unit, onImport: () -> Unit, onOpen: (String) -> Unit) {
    Column(Modifier.weight(1f)) {
        Text("Projects", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Local editing workspace", color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 12.dp)) {
            Button(onCreate, colors = ButtonDefaults.buttonColors(containerColor = LumaAccent)) { Text("+ New project") }
            OutlinedButton(onImport) { Text("Import media") }
        }
        if (projects.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No projects yet", color = Color.Gray) }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(projects) { title ->
                Row(Modifier.fillMaxWidth().background(LumaPanel, RoundedCornerShape(10.dp)).clickable { onOpen(title) }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp, 50.dp).background(Color(0xFF28232E), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.PlayArrow, "Open", tint = LumaAccent) }
                    Spacer(Modifier.width(10.dp)); Column { Text(title, fontWeight = FontWeight.SemiBold); Text("Timeline project", style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
                }
            }
        }
    }
}

@Composable private fun EditorPage(title: String, uri: String, pro: Boolean, onBack: () -> Unit, onExport: () -> Unit, onImport: () -> Unit) {
    var tool by remember { mutableStateOf("Edit") }
    Column(Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onBack) { Icon(Icons.Outlined.ArrowBack, "Back") }
            Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text(if (pro) "PRO" else "SIMPLE", color = LumaAccent, style = MaterialTheme.typography.labelSmall)
            TextButton(onExport) { Text("Export") }
        }
        Box(Modifier.fillMaxWidth().weight(.48f).background(Color.Black, RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) { Text(if (uri.isEmpty()) "Preview" else "Imported preview placeholder", color = Color.Gray) }
        Row(Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.Center) { Icon(Icons.Outlined.SkipPrevious, "Previous"); Icon(Icons.Outlined.PlayArrow, "Play", Modifier.padding(horizontal = 18.dp), tint = LumaAccent); Icon(Icons.Outlined.SkipNext, "Next"); Text("  00:00:00") }
        Text("Timeline", fontWeight = FontWeight.Bold)
        Column(Modifier.fillMaxWidth().weight(.32f).background(LumaPanel, RoundedCornerShape(8.dp)).padding(8.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("Video", "Overlay", "Audio", "Text").forEach { track ->
                Row(verticalAlignment = Alignment.CenterVertically) { Text(track, Modifier.width(60.dp), color = Color.Gray); Box(Modifier.weight(1f).height(30.dp).background(Color(0xFF28232E), RoundedCornerShape(4.dp))) }
            }
        }
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            listOf("Edit", "Effects", "Text", "Audio", "Color", "Speed", "Masks", "Transitions", "Motion", "Captions", "AI").forEach { name ->
                FilterChip(selected = tool == name, onClick = { if (name != "AI") tool = name }, enabled = name != "AI", label = { Text(name) })
            }
        }
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            val actions = when(tool) { "Effects" -> listOf("Blur", "Glow", "Glitch", "RGB", "Grain"); "Text" -> listOf("Title", "Subtitles"); "Audio" -> listOf("Music", "Voice", "Volume", "Fade"); "Color" -> listOf("Exposure", "Contrast", "Curves"); else -> listOf("Import", "Split", "Trim", "Delete", "Duplicate") }
            actions.forEach { OutlinedButton(onClick = { if (it == "Import") onImport() }) { Text(it) } }
        }
    }
}
@Composable private fun TemplatesPage(onNew: () -> Unit) {
    Column(Modifier.weight(1f)) { Text("Templates", style = MaterialTheme.typography.headlineLarge); listOf("Vertical 9:16", "Cinematic 16:9", "Square 1:1", "Slideshow", "Gaming montage").forEach { Text(it, Modifier.fillMaxWidth().padding(vertical = 6.dp).background(LumaPanel, RoundedCornerShape(9.dp)).clickable { onNew() }.padding(15.dp)) } }
}
@Composable private fun SettingsPage(light: Boolean, onLight: (Boolean) -> Unit, pro: Boolean, onPro: (Boolean) -> Unit, vertical: Boolean, onVertical: (Boolean) -> Unit, autosave: Boolean, onAutosave: (Boolean) -> Unit, snapping: Boolean, onSnap: (Boolean) -> Unit, quality: String, onQuality: (String) -> Unit, fps: Int, onFps: (Int) -> Unit) {
    Column(Modifier.weight(1f)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Setting("Light theme", light, onLight); Setting("Pro mode", pro, onPro); Setting("Vertical timeline", vertical, onVertical); Setting("Autosave", autosave, onAutosave); Setting("Magnetic snapping", snapping, onSnap)
        Text("Preview quality", Modifier.padding(top = 12.dp))
        Row { listOf("Auto", "480p", "720p", "1080p").forEach { FilterChip(selected = quality == it, onClick = { onQuality(it) }, label = { Text(it) }) } }
        Text("Export FPS: $fps", Modifier.padding(top = 12.dp))
        Slider(value = fps.toFloat(), onValueChange = { onFps(it.toInt()) }, valueRange = 24f..60f, steps = 5)
        Text("Offline · No AI · No ads · No subscription", color = Color.Gray)
    }
}
@Composable private fun Setting(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().background(LumaPanel, RoundedCornerShape(8.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text(title, Modifier.weight(1f)); Switch(checked, onChange) }
}
private fun exportPlaceholder(context: Context) {
    val folder = context.getExternalFilesDir("exports") ?: context.filesDir
    folder.mkdirs()
    File(folder, "Luma_export_placeholder.txt").writeText("Stage 1 placeholder: video rendering is not implemented yet. " + Date())
}

package app.luma.core.ui
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val Dark = darkColorScheme(primary=Color(0xFFB64CFF), onPrimary=Color.White, background=Color(0xFF09090C), surface=Color(0xFF141419), surfaceVariant=Color(0xFF222229), secondary=Color(0xFFC15AFF))
private val Light = lightColorScheme(primary=Color(0xFF8535C8), background=Color(0xFFF5F5F7), surface=Color.White, surfaceVariant=Color(0xFFE9E9EF))
@Composable fun LumaTheme(dark:Boolean, content:@Composable ()->Unit) { MaterialTheme(colorScheme=if(dark) Dark else Light, content=content) }

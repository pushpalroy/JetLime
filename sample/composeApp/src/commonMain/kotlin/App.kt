import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import theme.JetLimeTheme

@Composable
@Preview
fun App() {
  var darkTheme by remember { mutableStateOf(true) }
  JetLimeTheme(darkTheme = darkTheme) {
    HomeScreen(
      isDarkTheme = darkTheme,
      onThemeChange = {
        darkTheme = it
      },
    )
  }
}

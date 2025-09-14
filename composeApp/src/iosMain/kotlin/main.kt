import androidx.compose.ui.window.ComposeUIViewController
import org.notesapp.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import composefordesktop.resources.Res
import composefordesktop.resources.circle_x
import deskit.dialogs.file.filechooser.FileChooserDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.imaging.ImageFormats
import org.apache.commons.imaging.Imaging
import org.apache.commons.imaging.formats.tiff.TiffImagingParameters
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants
import org.jetbrains.compose.resources.painterResource
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppIconAttachmentSection(
    modifier: Modifier,
){
    var showFileChooser by remember { mutableStateOf(false) }
    var selectedIconPath by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // /home/zahid/Downloads/compose-icon-nobg.png

    OutlinedCard(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Select App Icon",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedIconPath.isNotEmpty() && File(selectedIconPath).exists()) {
                        val image = ImageIO.read(File(selectedIconPath))
                        val imageBitmap = image.toComposeImageBitmap()
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "App Icon",
                        )
                    } else {
                        Image(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "Default App Icon",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                OutlinedTextField(
                    value = selectedIconPath,
                    onValueChange = {selectedIconPath = it},
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                )

                AnimatedVisibility(
                    visible = selectedIconPath.isNotEmpty(),
                    modifier = Modifier.animateContentSize(),
                ){
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text("Clear input")
                            }
                        },
                        state = rememberTooltipState()
                    ){
                        IconButton(
                            onClick = {selectedIconPath = ""},
                            modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                        ){
                            Icon(
                                painter = painterResource(Res.drawable.circle_x),
                                contentDescription = "Clear"
                            )
                        }
                    }
                }


                OutlinedButton(
                    onClick = { showFileChooser = true },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Text("Browse")
                }
            }
        }
    }

    if (showFileChooser) {
        FileChooserDialog(
            title = "Select Icon File",
            allowedExtensions = listOf("png"),
            onFileSelected = {
                scope.launch(Dispatchers.IO) {
                    selectedIconPath = it.absolutePath
                    showFileChooser = false
                }
            },
            onCancel = {
                showFileChooser = false
            }
        )
    }

}

fun convertPngToIcnsWithTwelveMonkeys(scope: CoroutineScope) {
    scope.launch {
        val input = File("icons/compose.png")
        val output = File("test.icns")

        try {
            val image = ImageIO.read(input)
            val success = ImageIO.write(image, "ICNS", output)
            if (success) {
                println("ICNS saved via TwelveMonkeys")
            } else {
                println("TwelveMonkeys ICNS writer not found")
            }
        } catch (e: Exception) {
            println("TwelveMonkeys ICNS error: ${e.message}")
        }
    }
}

fun convertPngToIcoWithApacheImaging(scope: CoroutineScope){
    scope.launch {
        val file = File("icons/compose.png")
        val image = Imaging.getBufferedImage(file)

        val params = TiffImagingParameters()
        params.compression = TiffConstants.COMPRESSION_UNCOMPRESSED
        val format = ImageFormats.ICO

        try{
            Imaging.writeImage(image, File("test.ico"), format)
            println("ico file created")
        }catch(e: IOException){
            println("error creating ico file")
            e.printStackTrace()
        }
    }
}
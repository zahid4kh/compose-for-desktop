package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.awtTransferable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import composefordesktop.resources.Res
import composefordesktop.resources.circle_x
import deskit.dialogs.file.filechooser.FileChooserDialog
import deskit.dialogs.info.InfoDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.awt.datatransfer.DataFlavor
import java.io.File
import javax.imageio.ImageIO

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppIconAttachmentSection(
    modifier: Modifier,
){
    val scope = rememberCoroutineScope()

    var showFileChooser by remember { mutableStateOf(false) }
    var selectedIconPath by rememberSaveable { mutableStateOf("") }
    var isDragging by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ){
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.Center) {
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
                    if (selectedIconPath.isNotEmpty()
                        && File(selectedIconPath).exists()
                        && File(selectedIconPath).extension == "png") {
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
                        .height(IntrinsicSize.Max)
                        .animateContentSize()
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = {true},
                            target = remember{
                                object: DragAndDropTarget{
                                    override fun onStarted(event: DragAndDropEvent) {
                                        isDragging = true
                                    }
                                    override fun onEnded(event: DragAndDropEvent) {
                                        isDragging = false
                                    }
                                    override fun onDrop(event: DragAndDropEvent): Boolean{
                                        val files = try{
                                            if(event.awtTransferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                                                @Suppress("UNCHECKED CAST")
                                                println("file selected: ${event.awtTransferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>}")
                                                event.awtTransferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                                            }else{
                                                println("unsupported file type")
                                                emptyList()
                                            }
                                        }catch(e: Exception){
                                            e.printStackTrace()
                                            null
                                        }

                                        if(files != null && files.last().exists() && files.last().extension == "png"){
                                            selectedIconPath = files.last().absolutePath
                                            return true
                                        }
                                        return false
                                    }
                                }
                            }
                        ),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = if(isDragging) Color.LightGray else Color.Transparent,
                        focusedContainerColor = if(isDragging) Color.LightGray else Color.Transparent,
                    ),
                    trailingIcon = {
                        IconButton(onClick = {showInfoDialog = true}, modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)){
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                            )
                        }
                    }
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
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.primary
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

    if(showInfoDialog){
        InfoDialog(
            height = 320.dp,
            title = "Info",
            onClose = {
                showInfoDialog = false
            },
            resizable = true
        ){
            Column(modifier  = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(20.dp)
            ) {
                Text("You can either:", modifier = Modifier.padding(bottom = 8.dp))
                Text(" - Drag and drop your '.png' icon into the input field")
                Text(" - Use the file chooser")
                Text(" - Manually type the full path to the file")
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
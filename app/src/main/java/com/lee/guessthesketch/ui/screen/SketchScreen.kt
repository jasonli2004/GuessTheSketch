package com.lee.guessthesketch.ui.screen

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import com.lee.guessthesketch.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asAndroidPath

import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.io.ByteArrayOutputStream

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SketchScreen(
    modifier: Modifier,
    viewModel: SketchViewModel = viewModel(),
    difficulty : Int
) {
    val paths = viewModel.paths
    val strokeScale = 40
    val time = viewModel.time
    val showDialog = viewModel.showDialog
    val winState = viewModel.winState
    val lastGuess = viewModel.lastGuess
    val word = viewModel.word
    val curBase64Image = viewModel.curBase64Image
    val apiLaunched = viewModel.apiLaunched
    val sliderValue = viewModel.sliderValue
    val strokeWidth = viewModel.strokeWidth
    val strokeColor = viewModel.strokeColor
    val gameStart = viewModel.gameStart
    val isLoading = viewModel.isLoading

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6A11CB),
                        Color(0xFF2575FC)
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Word: ${word.value}",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color.White
        )
        CountdownTimer(totalTime = time, onTimerFinish = {
            isLoading.value = true
            val bitmap = captureCanvasContentAsBitmap(
                width = 800, // Desired width
                height = 800, // Desired height
                paths = paths // Drawn paths
            )

            // Convert the bitmap to Base64 and update the ViewModel
            val base64Image = bitmapToBase64(bitmap)
            viewModel.updateBase64Image(base64Image)
            viewModel.viewModelScope.launch {
                try {
                    val response = viewModel.chatCompletionWithBase64Image(curBase64Image.value)
                    Log.d("OpenAI Response", response)
                } catch (e: Exception) {
                    Log.e("OpenAI Error", "Exception occurred: ${e.message}", e)
                }

                try {
                    val response = viewModel.checkWin(word.value)
                    Log.d("OpenAI Response", winState.value.toString())
                } catch (e: Exception) {
                    Log.e("OpenAI Error", "Exception occurred: ${e.message}", e)
                }
                isLoading.value = false
                showDialog.value = true
                gameStart.value = false
            }
        }, launched = apiLaunched
        )


        SketchCanvas(
            paths = paths,
            addPath = { path: ColoredPath -> viewModel.addPath(path) },
            color = strokeColor,
            strokeWidth = strokeWidth
        )
        if (!gameStart.value && !viewModel.firstGame.value){
            Button(
                onClick = {viewModel.saveBitmapToLocal(
                    captureCanvasContentAsBitmap(
                        width = 800, // Desired width
                        height = 800, // Desired height
                        paths = paths // Drawn paths
                    ), context = context, filename = "sketch_${word.value}.png")
                    Toast.makeText(context, viewModel.toastMessage.value , Toast.LENGTH_LONG).show()
                },
                colors = Color(0xFF8BC34A).let { ButtonDefaults.buttonColors(containerColor = it) },
                modifier = Modifier.padding(16.dp)
            ){
                Text("Save Sketch")
            }
        }
        if (viewModel.gameStart.value) {
            Row(

                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(text = "Width: ", style = TextStyle(fontWeight = FontWeight.Bold), color = Color.White)
                Slider(
                    value = sliderValue.value,
                    onValueChange = { newValue ->
                        viewModel.updateSliderValue(newValue)
                        viewModel.updateStrokeWidth(newValue * strokeScale)
                    },
                    valueRange = 0.2f..1.2f,
                    steps = 15,
                    modifier = Modifier.padding(horizontal = 3.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color(0xFF6dd5ed),
                        inactiveTrackColor = Color.White,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )


            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 65.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ColoredButton(
                    color = Color.Black,
                    onClick = {
                        viewModel.updateColor(Color.Black)
                        viewModel.updateButtonSelected(0)
                    },
                    viewModel.buttonSelected,
                    buttonId = 0,
                    modifier = Modifier.weight(1f)
                )
                ColoredButton(
                    color = Color.Red,
                    onClick = {
                        viewModel.updateColor(Color.Red)
                        viewModel.updateButtonSelected(1)
                    },
                    viewModel.buttonSelected,
                    buttonId = 1,
                    modifier = Modifier.weight(1f)
                )
                ColoredButton(
                    color = Color(0xFFFFA500),
                    onClick = {
                        viewModel.updateColor(Color(0xFFFFA500))
                        viewModel.updateButtonSelected(2)
                    },
                    viewModel.buttonSelected,
                    buttonId = 2,
                    modifier = Modifier.weight(1f)
                )
                ColoredButton(
                    color = Color.Yellow,
                    onClick = {
                        viewModel.updateColor(Color.Yellow)
                        viewModel.updateButtonSelected(3)
                    },
                    viewModel.buttonSelected,
                    buttonId = 3,
                    modifier = Modifier.weight(1f)
                )
                ColoredButton(
                    color = Color.Green,
                    onClick = {
                        viewModel.updateColor(Color.Green)
                        viewModel.updateButtonSelected(4)
                    },
                    viewModel.buttonSelected,
                    buttonId = 4,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 65.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ColoredButton(
                    color = Color.Cyan,
                    onClick = {
                        viewModel.updateColor(Color.Cyan)
                        viewModel.updateButtonSelected(5)
                    },
                    viewModel.buttonSelected,
                    buttonId = 5,
                    modifier = Modifier.weight(1f)
                )
                ColoredButton(
                    color = Color.Blue,
                    onClick = {
                        viewModel.updateColor(Color.Blue)
                        viewModel.updateButtonSelected(6)
                    },
                    viewModel.buttonSelected,
                    buttonId = 6,
                    modifier = Modifier.weight(1f)
                )
                ColoredButton(
                    color = Color.Magenta,
                    onClick = {
                        viewModel.updateColor(Color.Magenta)
                        viewModel.updateButtonSelected(7)
                    },
                    viewModel.buttonSelected,
                    buttonId = 7,
                    modifier = Modifier.weight(1f)
                )
                ColoredButton(
                    color = Color.Gray,
                    onClick = {
                        viewModel.updateColor(Color.Gray)
                        viewModel.updateButtonSelected(8)
                    },
                    viewModel.buttonSelected,
                    buttonId = 8,
                    modifier = Modifier.weight(1f)
                )
                ImageButton(
                    color = Color.White,
                    onClick = {
                        viewModel.updateColor(Color.White)
                        viewModel.updateButtonSelected(9)
                    },
                    viewModel.buttonSelected,
                    buttonId = 9,
                    modifier = Modifier.weight(1f),
                    image = R.drawable.erasers
                )
            }
            Button(onClick = { viewModel.deleteAllPath() },
                modifier = Modifier.padding(5.dp).height(50.dp).wrapContentWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clear),
                        contentDescription = "Clear",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Clear",
                        color = Color.Black
                    )
                }
            }
        }

        if (gameStart.value) {
            Button(
                onClick = {
                    time.value = 1
                },
                modifier = Modifier.padding(16.dp),
                colors = Color(0xFFFFD700).let { ButtonDefaults.buttonColors(containerColor = it) }
            ) {
                Text(
                    text = "Submit",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
        else{
            Button(
                onClick = {
                    viewModel.start(difficulty)
                },
                modifier = Modifier.padding(16.dp),
                colors = Color(0xFFF9A825).let { ButtonDefaults.buttonColors(containerColor = it) }

            ){
                Text("New Game")
            }
        }


        if (showDialog.value) {
            finishDialog(
                onDismiss = { showDialog.value = false; },
                onConfirm = { showDialog.value = false; viewModel.start(difficulty) },
                winState = winState,
                lastGuess = lastGuess
            )
        }
    }
    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.Blue,
                strokeWidth = 4.dp
            )
        }
    }
}


@Composable
fun SketchCanvas(
    paths: List<ColoredPath>,
    addPath: (ColoredPath) -> Unit,
    color: MutableState<Color>,
    strokeWidth: MutableState<Float>
) {
    var activePath by remember { mutableStateOf(Path()) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1.0f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        activePath.moveTo(offset.x, offset.y) // Start a new path
                    },
                    onDrag = { change, _ ->
                        activePath.lineTo(change.position.x, change.position.y)
                        activePath = Path().apply { this.addPath(activePath) } // Create a new path for each drag
                    },
                    onDragEnd = {
                        if (!activePath.isEmpty) {
                            addPath(ColoredPath(Path().apply { this.addPath(activePath) }, color.value, strokeWidth.value)) // Save completed path
                        }
                        activePath.reset()
                    }
                )
            }
    ) {
        clipRect {
            drawRect(color = Color.White, size = size)


            paths.forEach { savedPath ->
                drawPath(
                    path = savedPath.path,
                    color = savedPath.color,
                    style = Stroke(width = savedPath.strokeWidth)
                )
            }

            drawPath(
                path = activePath,
                color = color.value,
                style = Stroke(width = strokeWidth.value)
            )

        }

    }
}


@Composable
fun ColoredButton(color : Color, onClick: (newColor: Color) -> Unit, buttonSelected : MutableState<Int>, buttonId : Int, modifier: Modifier = Modifier){
    Button(
        shape = CircleShape,
        onClick = {
            onClick(color)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = color,),
        border = if (buttonSelected.value == buttonId) {
            if (buttonId == 0){
                BorderStroke(2.dp, Color.White)
            }
            else{
                BorderStroke(2.dp, Color.White)
            }
        } else null,
        modifier = modifier.aspectRatio(1f).padding(5.dp),
    ){
    }
}

@Composable
fun ImageButton(color: Color, onClick: (newColor: Color) -> Unit, buttonSelected : MutableState<Int>, buttonId : Int, modifier: Modifier = Modifier, image: Int
) {
    Button(
        onClick = {onClick(color)},
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        contentPadding = PaddingValues(1.dp),
        border = if (buttonSelected.value == buttonId) {
            if (buttonId == 0){
                BorderStroke(2.dp, Color.White)
            }
            else{
                BorderStroke(2.dp, Color.Black)
            }
        } else null,
        modifier = modifier.aspectRatio(1f).padding(5.dp),
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "eraser",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}



fun saveBitmapToGallery(bitmap: Bitmap, context: Context) {
    val filename = "sketch_${System.currentTimeMillis()}.png"
    val fos: OutputStream?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = resolver.openOutputStream(imageUri!!)
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }
    if (fos != null) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    }
    fos?.close()
}

fun captureCanvasContentAsBitmap(
    width: Int,
    height: Int,
    paths: List<ColoredPath>
): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Set background color to white
    canvas.drawColor(android.graphics.Color.WHITE)

    // Paint for drawing paths
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        isDither = true
        style = android.graphics.Paint.Style.STROKE
    }

    // Draw each path on the canvas
    paths.forEach { coloredPath ->
        paint.color = coloredPath.color.toArgb()
        paint.strokeWidth = coloredPath.strokeWidth
        val androidPath = coloredPath.path.asAndroidPath()
        canvas.drawPath(androidPath, paint)
    }

    return bitmap
}


fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

@Composable
fun CountdownTimer(totalTime: MutableState<Int>, onTimerFinish: () -> Unit, launched : MutableState<Boolean>) {

    var timeLeft = totalTime.value
    // Display the timer
    if (timeLeft == 1 && !launched.value ) {
        launched.value = true;
        onTimerFinish()
    }
    Text(
        text = "Time left: $timeLeft seconds",
        modifier = Modifier.padding(16.dp),
        color = Color.White
    )
}

@Composable
fun finishDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    winState: MutableState<Boolean>,
    lastGuess: MutableState<String>,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .wrapContentSize()
                .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (winState.value) {
                Text(text = "You have won!")
            } else {
                Text(text = "You have lost!")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The guesses from AI were:",
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = lastGuess.value,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
                Button(onClick = onConfirm) {
                    Text("New Game")
                }
            }
        }
    }
}




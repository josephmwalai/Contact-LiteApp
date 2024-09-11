package com.example.contactslite

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.contactslite.repository.ContactRepository
import com.example.contactslite.room.ContactDatabase
import com.example.contactslite.ui.theme.ContactsLiteTheme
import com.example.contactslite.ui.theme.GreenBk
import com.example.contactslite.view.ContactViewModel
import com.example.contactslite.view.ContactViewModelFactory
import okio.Path.Companion.toPath
import java.io.File
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(applicationContext, ContactDatabase::class.java,
            "contact_database").build()

        val repository = ContactRepository(database.ContactDao())
        val viewModel : ContactViewModel by viewModels {
            ContactViewModelFactory(repository)
        }

        setContent {
            ContactsLiteTheme {

            }
        }
    }
    
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(viewModel: ContactViewModel, navController: NavController) {
    val context = LocalContext.current.applicationContext

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var name by remember {
        mutableStateOf("")
    }
    var phoneNumber by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier.height(48.dp),
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {
                    Text(text = "Add Contact", fontSize = 18.sp)
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    Toast.makeText(context, "Add Contact", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.contactadd),
                        contentDescription = "add"
                    )

                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GreenBk,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White)
        )
    }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            imageUri?.let { uri: Uri ->
                Image(painter = rememberAsyncImagePainter(uri),
                    contentDescription = null, modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape), contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                launcher.launch("image/*")
            }, colors = ButtonDefaults.buttonColors(GreenBk)) {
                Text(text = "Select Image")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = name, onValueChange = {name = it },
                label = { Text(text = "name")},
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.Blue,
                    unfocusedContainerColor = Color.Blue,
                ))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = phoneNumber, onValueChange = {phoneNumber = it},
                label = { Text(text = "Phone Number")},
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Blue,
                    unfocusedContainerColor = Color.Blue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ))
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = email, onValueChange = {email = it},
                label = { Text(text = "Email")}, modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(10.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Blue,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.Blue
                ))
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                imageUri?.let {
                    val internalPath = copyUriToInternalStorage(context, it, "$name.jpg")
                    internalPath?.let { path ->
                        viewModel.addContact(path, name, phoneNumber, email)
                        navController.navigate("contactPage") {
                            popUpTo(0)
                        }
                    }
                }
            }, colors = ButtonDefaults.buttonColors(GreenBk)) {
                Text(text = "Add Contact")
            }
        }

    }
}

    fun copyUriToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
        val file = File(context.filesDir, fileName)
        return try {
            context.contentResolver.openInputStream(uri)?.use {  inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

package com.example.courtreservationapp.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.User
import com.example.courtreservationapp.data.Utils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowProfile(context: Context, viewModel: ProfileViewModel) {
    val (editMode, setEditMode) = remember { mutableStateOf(false) }
    val (user, setUser) = remember { mutableStateOf(Utils().recoverLocalStorageUser(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Profile",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        setEditMode(!editMode)
                        if(editMode)
                            viewModel.saveUser(context, user)
                    }) {
                        Icon(
                            imageVector = if (editMode) Icons.Filled.Check else Icons.Filled.Edit,
                            contentDescription = if (editMode) "Save" else "Edit"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                val customTextStyle = TextStyle(fontSize = 20.sp)
                val defaultTypography = MaterialTheme.typography
                MaterialTheme(
                    typography = defaultTypography.copy(
                        labelLarge = defaultTypography.labelLarge.copy(fontSize = 20.sp)
                    )
                ) {
                    ProvideTextStyle(customTextStyle) {

                        UserInfoContent(isEditMode = editMode, user, viewModel, setUser)

                    }
                }

            }
        }
    )
}

@Composable
fun UserInfoContent(
    isEditMode: Boolean,
    user: User,
    viewModel: ProfileViewModel,
    setUser: (User) -> Unit
) {

    val skillLevels = listOf("Beginner", "Intermediate", "Advanced")
    val sports = viewModel.sports.value!!.map { it.name.replaceFirstChar { it.uppercase() } }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        ImageUploadScreen(isEditMode, viewModel)
        Column(modifier = Modifier.fillMaxWidth().padding(start = 25.dp, end = 25.dp).border(2.dp,Color.Black,shape).padding(25.dp)) {
            Text(
                text = "Full Name",
                style = MaterialTheme.typography.labelLarge
            )
            TextForm(user.fullName, isEditMode, propertyKey = "fullName") {
                setUser(user.copy(fullName = it))
            }
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Nickname",
                style = MaterialTheme.typography.labelLarge
            )
            TextForm(user.nickName, isEditMode,propertyKey = "nickName") {
                setUser(user.copy(nickName = it))
            }
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Age",
                style = MaterialTheme.typography.labelLarge
            )
            NumericForm(user.age, isEditMode) { newUserAge ->
                if (newUserAge.isBlank() || newUserAge.toIntOrNull() != null) {
                    setUser(user.copy(age = newUserAge.toIntOrNull() ?: 0))
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Objective",
                style = MaterialTheme.typography.labelLarge
            )
            TextForm(user.objective, isEditMode,propertyKey = "objective") {
                setUser(user.copy(objective = it))
            }
            Spacer(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Description",
                style = MaterialTheme.typography.labelLarge
            )
            TextForm(user.description, isEditMode,propertyKey = "description") {
                setUser(user.copy(description = it))
            }
        }


        Text(
            text = "Favorite Sport",
            style = MaterialTheme.typography.labelLarge
        )
        DropdownField(user.sport, sports, isEditMode) {
            setUser(user.copy(sport = it))
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))

        Text(
            text = "Skill Level",
            style = MaterialTheme.typography.labelLarge
        )
        DropdownField(user.skillLevel, skillLevels, isEditMode) {
            setUser(user.copy(skillLevel = it))
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))

        Text(
            text = "Achievements",
            style = MaterialTheme.typography.labelLarge
        )
        TextForm(user.achievements, isEditMode,propertyKey = "achievements") {
            setUser(user.copy(achievements = it))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextForm(text: String, isEditMode: Boolean, propertyKey : String,onValueChange: (String) -> Unit) {
    if (isEditMode) {
        // Show an editable text field if in edit mode
        OutlinedTextField(
            value = if(!text.equals(User().getPropertiesUserFromKey(propertyKey))) text else "",
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = User().getPropertiesUserFromKey(propertyKey)) }
        )
    } else {
        // Show the user's full name if not in edit mode
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumericForm(text: Int, isEditMode: Boolean, onValueChange: (String) -> Unit) {
    if (isEditMode) {
        OutlinedTextField(
            value = text.toString(),
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )
    } else {
        // Show the user's full name if not in edit mode
        Text(text = text.toString())
    }

}

@Composable
fun DropdownField(
    skillLevel: String,
    skillLevels: List<String>,
    isEditMode: Boolean,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    if (isEditMode) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .then(Modifier.shadow(2.dp))
                .clickable { expanded = !expanded }

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(text = skillLevel)
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        // Show a drop-down menu with fixed options if in edit mode
        Box {
            DropdownMenu(expanded, { expanded = !expanded }, offset = DpOffset(0.dp, 48.dp)) {
                skillLevels.forEach { level ->
                    DropdownMenuItem(
                        {
                            Text(
                                level
                            )
                        },
                        {
                            expanded = false
                            onValueChange(level)
                        },

                        )
                }
            }
        }
    } else {
        // Show the user's selected skill level if not in edit mode
        Text(text = skillLevel)
    }
}

@Composable
fun ImageUploadScreen(isEditMode: Boolean, viewModel: ProfileViewModel) {
    val selectedImage = viewModel.selectedImage.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the selected image
        Box(
            modifier = Modifier
                .fillMaxWidth(.5f)
                .aspectRatio(1f)
                .background(Color.Gray)
        ) {
            if (selectedImage.value != null)
                Image(
                    bitmap = viewModel.toBitmap(selectedImage.value)!!.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            else {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            if (isEditMode) {
                // Show the image selection dialog on button click
                Button(
                    onClick = { viewModel.showImageSelectionDialog() },
                    modifier = Modifier.align(alignment = Alignment.BottomEnd)
                ) {
                    Text(text = "Change Image")
                }
            }
        }


        // Dialog to select image from camera or gallery
        ImageSelectionDialog(
            showDialog = viewModel.showDialog,
            onDismiss = { viewModel.dismissImageSelectionDialog() },
            //TODO
            onCameraSelected = {
                viewModel.openCamera()
                viewModel.dismissImageSelectionDialog()
            },
            onGallerySelected = {
                viewModel.openGallery()
                viewModel.dismissImageSelectionDialog()
            }
        )


    }


}

@Composable
fun ImageSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier.background(Color.White, Shapes().medium),
                content = {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Select Image From:")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onCameraSelected,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Camera")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onGallerySelected,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Gallery")
                        }
                    }
                }
            )
        }
    }
}



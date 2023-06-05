package com.example.courtreservationapp.profile

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.courtreservationapp.R
import com.example.courtreservationapp.data.Skill
import com.example.courtreservationapp.data.Sport
import com.example.courtreservationapp.data.User
import com.example.courtreservationapp.signIn.AuthViewModel
import com.example.courtreservationapp.signIn.GoogleAuthClient
import com.example.courtreservationapp.signIn.SignOutButton
import com.google.android.gms.auth.api.identity.Identity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel = viewModel(ProfileViewModel::class.java)

    val googleAuthClient by lazy {
        GoogleAuthClient(
            context = context.applicationContext,
            oneTapClient = Identity.getSignInClient(context.applicationContext),
            auth = authViewModel.auth
        )
    }

    val name = viewModel.name.observeAsState()
    val nick = viewModel.nick.observeAsState()
    val age = viewModel.age.observeAsState()
    val sport = viewModel.sport.observeAsState()
    val skill = viewModel.skill.observeAsState()
    val objective = viewModel.objective.observeAsState()
    val achievement = viewModel.achievement.observeAsState()
    val description = viewModel.description.observeAsState()
    val image = viewModel.getImage(context).observeAsState()

    val (edit, setEdit) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_title),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                actions = {
                    SignOutButton(
                        googleAuthClient = googleAuthClient,
                        authViewModel = authViewModel,
                        navController = navController
                    )

                    IconButton(onClick = {
                        if (edit)
                            viewModel.saveUser(context, User(
                                name.value?: "",
                                nick.value?: "",
                                age.value?: ("0").toInt(),
                                sport.value?: "",
                                skill.value?: mapOf(),
                                achievement.value?: "",
                                objective.value?: "",
                                description.value?: ""
                            ), image.value, context)

                        setEdit(!edit)
                    }) {
                        Icon(
                            imageVector = if (edit) Icons.Filled.Check else Icons.Filled.Edit,
                            contentDescription = if (edit) "Save" else "Edit"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HeaderCard(
                Modifier.weight(1.2f),
                image.value,
                name.value?: "",
                nick.value?: "",
                edit,
                { viewModel.setImage(it) },
                { viewModel.setName(it) },
                { viewModel.setNick(it) }
            )

            BodyCard(
                Modifier.weight(2f),
                age.value?.toString()?: "",
                sport.value?: "",
                skill.value?: mapOf(),
                objective.value?: "",
                achievement.value?: "",
                description.value?: "",
                edit,
                { if(it == "") viewModel.setAge(0) else viewModel.setAge(it.toInt()) },
                { viewModel.setSport(it) },
                { viewModel.setSkill(it) },
                { viewModel.setObjective(it) },
                { viewModel.setAchievement(it) },
                { viewModel.setDescription(it) }
            )
        }
    }
}

@Composable
fun HeaderCard(
    modifier: Modifier,
    image: Bitmap?,
    name: String,
    nickname: String,
    edit: Boolean,
    setImage: (Bitmap?) -> Unit,
    setName: (String) -> Unit,
    setNick: (String) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Row(
            Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxSize()
        ) {
            ImageForm(image, edit) {
                setImage(it)
            }

            Column(
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(10.dp)
            ) {
                TextForm(
                    Modifier,
                    R.string.fullName,
                    name, edit,
                    Arrangement.Start,
                    true
                ) {
                    setName(it)
                }

                Spacer(Modifier.height(10.dp))

                TextForm(
                    Modifier,
                    R.string.nickName,
                    nickname,
                    edit,
                    Arrangement.Start,
                    true
                ) {
                    setNick(it)
                }
            }
        }
    }
}

@Composable
fun ImageForm(image: Bitmap?, edit: Boolean, setImage: (Bitmap?) -> Unit) {
    val context = LocalContext.current
    val viewModel = viewModel(ProfileViewModel::class.java)

    val placeholder = painterResource(id = R.drawable.ic_launcher_foreground)

    val (painter, setPainter) = remember {
        mutableStateOf(image?.let {
            BitmapPainter(it.asImageBitmap())
        }?: placeholder)
    }
    val (show, setShow) = remember {
        mutableStateOf(false)
    }

    val uri = remember { ProfileImageProvider.getImageUri(context) }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val bitmap = viewModel.toBitmap(context, uri)

            setImage(bitmap)
            setPainter(bitmap?.asImageBitmap()?.let { BitmapPainter(it) }?: placeholder)
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { new ->
        if (new != null) {
            val bitmap = viewModel.toBitmap(context, new)

            setImage(bitmap)
            setPainter(bitmap?.asImageBitmap()?.let { BitmapPainter(it) }?: placeholder)
        }
    }

    Box(Modifier.aspectRatio(1f)) {
        Image(
            painter,
            "Selected Image",
            Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (edit) {
            // Show the image selection dialog on button click
            Button(
                { setShow(true) },
                Modifier.align(alignment = Alignment.BottomEnd)
            ) {
                Text(stringResource(R.string.change_image))
            }
        }
    }

    if (show)
        ImageSelectionDialog(
            { setShow(false) },
            {
                cameraLauncher.launch(uri)
                setShow(false)
            },
            {
                galleryLauncher.launch("image/*")
                setShow(false)
            }
        )
}

@Composable
fun ImageSelectionDialog(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    Dialog(onDismiss) {
        Box(Modifier.background(MaterialTheme.colorScheme.background, Shapes().medium)) {
            Column(Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.select_image_from))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onCameraSelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.camera))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onGallerySelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.gallery))
                }
            }
        }
    }
}

@Composable
fun BodyCard(
    modifier: Modifier,
    age: String,
    sportID: String,
    skill: Map<String, String>,
    objective: String,
    achievements: String,
    description: String,
    edit: Boolean,
    setAge: (String) -> Unit,
    setSport: (String) -> Unit,
    setSkill: (Map<String, String>) -> Unit,
    setObjective: (String) -> Unit,
    setAchievement: (String) -> Unit,
    setDescription: (String) -> Unit
) {
    val lang = Locale.current.language

    val viewModel = viewModel(ProfileViewModel::class.java)

    val sports = viewModel.sports.observeAsState()
    val allSkills = viewModel.skills.observeAsState()

    val skillLevel = buildMap {
        sports.value?.forEach { s ->
            put(s, allSkills.value?.find { it.id == skill[s.id] })
        }
    }
    val sport = sports.value?.find { it.id == sportID }?: Sport()

    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        NumberForm(Modifier.padding(20.dp), R.string.age, age, edit) {
            setAge(it)
        }

        DropDownForm(
            Modifier.padding(20.dp),
            stringResource(R.string.favorite_sport),
            Pair(sport.id, sport.name[lang]?: sport.name["en"]?: ""),
            sports.value?.map { Pair(it.id, it.name[lang]?: it.name["en"]!!) }?: listOf(),
            edit
        ) { newId ->
            val new = sports.value?.find {
                it.id == newId
            }

            if (new != null)
                setSport(new.id)
        }

        SkillLevel(
            Modifier.padding(20.dp),
            allSkills.value?: listOf(),
            skillLevel,
            edit
        ) { map ->

            setSkill(map.mapKeys { (key, _) ->
                key.id
            }.mapValues { (_, value) ->
                value?.id?: ""
            })
        }

        TextForm(Modifier.padding(20.dp), R.string.objective, objective, edit) {
            setObjective(it)
        }

        TextForm(Modifier.padding(20.dp), R.string.achievements, achievements, edit) {
            setAchievement(it)
        }

        TextForm(Modifier.padding(20.dp), R.string.description, description, edit) {
            setDescription(it)
        }
    }
}

@Composable
fun TextForm(
    modifier: Modifier = Modifier,
    field: Int,
    text: String,
    edit: Boolean,
    alignment: Arrangement.Horizontal = Arrangement.End,
    singleLine: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column(modifier.fillMaxWidth()) {
        Row {
            Text(
                stringResource(id = field) + ":",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = alignment
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                if (edit) {
                    // Show an editable text field if in edit mode
                    TextField(text, onValueChange, singleLine = singleLine)
                }
                else {
                    // Show the user's full name if not in edit mode
                    Text(text)
                }
            }
        }
    }
}

@Composable
fun NumberForm(
    modifier: Modifier,
    field: Int,
    value: String,
    edit: Boolean,
    onValueChange: (String) -> Unit
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(id = field) + ":",
            Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge
        )

        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
            if (edit) {
                // Show an editable value field if in edit mode
                TextField(
                    value,
                    onValueChange,
                    Modifier.weight(.2f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )
            }
            else {
                // Show the user's full name if not in edit mode
                Text(if (value == "0") "" else value)
            }
        }
    }
}

@Composable
fun DropDownForm(
    modifier: Modifier,
    field: String,
    value: Pair<String, String>,
    list: List<Pair<String, String>>,
    edit: Boolean,
    isList: Boolean = false,
    setValue: (String) -> Unit
) {
    val (expanded, setExpanded) = remember {
        mutableStateOf(false)
    }

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$field:",
            Modifier.weight(1f),
            style = if (isList) MaterialTheme.typography.bodyLarge
            else MaterialTheme.typography.titleLarge
        )

        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
            Text(value.second)

            if (edit)
                Box {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        "Dropdown Icon",
                        Modifier.clickable { setExpanded(!expanded) }
                    )

                    DropdownMenu(
                        expanded,
                        { setExpanded(!expanded) }
                    ) {
                        list.forEach {
                            DropdownMenuItem(
                                { Text(it.second) },
                                {
                                    setExpanded(false)
                                    setValue(it.first)
                                }
                            )
                        }
                    }
                }
        }
    }
}

@Composable
fun SkillLevel(
    modifier: Modifier,
    skills: List<Skill>,
    map: Map<Sport, Skill?>,
    edit: Boolean,
    setValue: (Map<Sport, Skill?>) -> Unit
) {
    val lang = Locale.current.language

    Column(modifier.fillMaxWidth()) {
        Row {
            Text(
                stringResource(id = R.string.skillLevel) + ":",
                style = MaterialTheme.typography.titleLarge
            )
        }

        map.map { (key, value) ->
            Row(Modifier.fillMaxWidth()) {
                if (edit || value != null)
                    DropDownForm(
                        Modifier.padding(start = 10.dp, bottom = 5.dp),
                        key.name[lang]?: key.name["en"]!!,
                        Pair(value?.id?: "", value?.text?.get(lang)?: value?.text?.get("en")?: ""),
                        skills.map {
                            if (it.id != value?.id)
                                Pair(it.id, it.text[lang]?: value?.text?.get("en")?: "")
                            else
                                Pair("", stringResource(R.string.none))
                        },
                        edit,
                        true
                    ) { new ->
                        setValue(map.mapValues {
                            if (it.key.id == key.id)
                                skills.find { skill ->
                                    skill.id == new
                                }
                            else
                                it.value
                        })
                    }
            }
        }
    }
}
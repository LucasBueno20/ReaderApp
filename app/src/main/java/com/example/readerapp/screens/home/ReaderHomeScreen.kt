package com.example.readerapp.screens.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readerapp.components.*
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

//@Preview
@Composable
fun HomeScreen(
    navController: NavController = NavController(LocalContext.current),
    viewModel: HomeScreenViewModel = hiltViewModel() //viewModel
) {
    Scaffold(topBar = {
                      ReaderAppBar(title = "A. Reader", navController = navController)
    }, floatingActionButton = {
        FABContent{
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }
    ) {
        //content
        Surface(modifier = Modifier.fillMaxSize()) {
            //home content
            HomeContent(navController, viewModel)

        }
    }
}


@Composable
fun HomeContent(navController: NavController, viewModel: HomeScreenViewModel) {
    val scrollState = rememberScrollState()
    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!viewModel.data.value.data.isNullOrEmpty()) {
        listOfBooks = viewModel.data.value.data!!.toList().filter { mBook ->
            mBook.userId == currentUser?.uid.toString()
        }

        Log.d("Books", "HomeContent: ${listOfBooks.toString()}")
    }

//    val listOfBooks = listOf(
//        MBook(id = "dmrfri", title = "Hello Again", authors = "All of us", notes = null),
//        MBook(id = "dmrfri", title = "Hello Again", authors = "All of us", notes = null),
//        MBook(id = "dmrfri", title = "Hello Again", authors = "All of us", notes = null),
//        MBook(id = "dmrfri", title = "Hello Again", authors = "All of us", notes = null),
//        MBook(id = "dmrfri", title = "Hello Again", authors = "All of us", notes = null),
//    )

    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if (!email.isNullOrEmpty()) FirebaseAuth.getInstance().currentUser?.email
            ?.split("@")?.get(0) else
                "N/A"
    Column(
        modifier = Modifier
            .padding(2.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top
    ) {

        Row(modifier = Modifier.align(Alignment.Start)) {
            TitleSection(label = "Your Reading \n " + " activity right now...")
            
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            
            Column {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                        }
                        .size(45.dp),
                    tint = MaterialTheme.colors.onSecondary)
                Text(
                    text = currentUserName!!,
                    modifier = Modifier.padding(2.dp),
                    style = MaterialTheme.typography.overline,
                    color = Color.Red,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip)
                Divider(modifier = Modifier.padding(end = 5.dp))
            }
        }
        ReadingRightNowArea(listOfBooks = listOfBooks, navController = navController)

        TitleSection(label = "Reading List")

        BoolListArea(listOfBooks = listOfBooks, navController = navController)

    }
}

@Composable
fun ReadingRightNowArea(
    listOfBooks: List<MBook>,
    navController: NavController) {
    //Filter books by reading now
    val readingNowList = listOfBooks.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(readingNowList){
        Log.d("TAG", "BoolListArea: $it")
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun BoolListArea(listOfBooks: List<MBook>, navController: NavController) {
    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }
    HorizontalScrollableComponent(addedBooks){
        navController.navigate(ReaderScreens.UpdateScreen.name +"/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>, onCardPressed: (String) -> Unit) {
    val scrollState = rememberScrollState()
    
    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(280.dp)
        .horizontalScroll(scrollState)) {

        for (book in listOfBooks) {
            ListCard(book) {
                onCardPressed(book.googleBookId.toString())
            }
        }
    }
}






























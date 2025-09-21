package com.example.yu_gi_db.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.example.yu_gi_db.R
import com.example.yu_gi_db.music.InitMusicView
import com.example.yu_gi_db.music.MusicView
import com.example.yu_gi_db.ui.theme.YuGiDBTheme


@Composable
fun InitInformationView(
    modifier: Modifier = Modifier,
) {
    InformationView(modifier = modifier,
        musicControlSlot={
            InitMusicView(modifier =modifier,musicViewModel = hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner) )
        }
    )
}

@Composable
fun InformationView(
    modifier: Modifier = Modifier,
    musicControlSlot: @Composable () -> Unit={} // New parameter for the music controls
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        InfoSectionView( // Assicurati che sia definita e importata
            title = stringResource(R.string.info_section_about_title)
        ) {
            Text(
                text = stringResource(R.string.info_section_about_content),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        InfoSectionView(
            title = stringResource(R.string.info_section_version_title)
        ) {
            Text(
                text = stringResource(R.string.version),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        InfoSectionView(
            title = stringResource(R.string.info_section_developer_title)
        ) {
            Text(
                text = stringResource(R.string.name_and_company),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        InfoSectionView(
            title = stringResource(R.string.info_section_credits_title)
        )
        {
            Text(
                text = stringResource(R.string.info_section_credits_content),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center
            )
        }
        //InitMusicView(musicViewModel = hiltViewModel(viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner) )//LocalContext.current as ComponentActivity
        musicControlSlot()
    }
}

@Composable
fun InfoSectionView(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(content = content)
    }
}

// --- Preview ---

@Preview(name = "InfoSectionView - Esempio", showBackground = true)
@Composable
fun InfoSectionViewPreview() {
    YuGiDBTheme {
        InfoSectionView(title = "Titolo della Sezione") {
            Text("Questo è il contenuto della sezione. Può essere un testo lungo o altri componenti Composable.")
            Text("Altra riga di contenuto.")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InformationViewPreview(modifier: Modifier = Modifier) {
    YuGiDBTheme {
        InformationView(modifier,musicControlSlot = {MusicView() })
    }
}


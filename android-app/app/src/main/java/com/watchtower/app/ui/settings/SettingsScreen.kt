package com.watchtower.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.watchtower.app.WatchtowerApplication
import com.watchtower.app.ui.theme.TextMuted
import com.watchtower.app.ui.theme.TextPrimary

@Composable
fun SettingsScreen(onSaved: () -> Unit) {
    val app = LocalContext.current.applicationContext as WatchtowerApplication
    val factory = viewModelFactory {
        initializer { SettingsViewModel(app.settingsRepository) }
    }
    val viewModel: SettingsViewModel = viewModel(factory = factory)
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val saved by viewModel.saved.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Connect to your API", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Text(
            "Enter the base URL and API key for your watchtower-api instance.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
        )

        OutlinedTextField(
            value = draft.baseUrl,
            onValueChange = viewModel::updateBaseUrl,
            label = { Text("Base URL") },
            placeholder = { Text("http://127.0.0.1:8080") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = draft.apiKey,
            onValueChange = viewModel::updateApiKey,
            label = { Text("API key") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                viewModel.save()
                onSaved()
            },
            enabled = draft.baseUrl.isNotBlank() && draft.apiKey.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Save")
        }

        if (saved) {
            Text("Saved.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
    }
}

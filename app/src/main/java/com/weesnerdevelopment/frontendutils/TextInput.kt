package com.weesnerdevelopment.frontendutils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class TextInputType { Outlined, Normal }

@Composable
fun TextInput(
	modifier: Modifier = Modifier,
	@StringRes label: Int? = null,
	oldValue: String? = null,
	@StringRes helperText: Int? = null,
	keyboardType: KeyboardType = KeyboardType.Text,
	textInputType: TextInputType = TextInputType.Normal,
	spaceMedium: Dp = 8.dp,
	textChange: (String) -> Unit
) {
	val (value, setValue) = remember(oldValue) {
		mutableStateOf(
			TextFieldValue(oldValue ?: "", TextRange(oldValue?.length ?: 0))
		)
	}

    val visualTransformation = remember {
        if (keyboardType == KeyboardType.Password) PasswordVisualTransformation()
        else VisualTransformation.None
    }

    Column(modifier = modifier) {
        when (textInputType) {
			TextInputType.Outlined -> OutlinedTextField(
				value = value,
				onValueChange = {
					setValue(it)
					textChange(it.text)
				},
				visualTransformation = visualTransformation,
				keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
				modifier = Modifier.fillMaxWidth(),
				label = { if (label != null) Text(text = stringResource(label)) }
			)
			TextInputType.Normal -> TextField(
				value = value,
				onValueChange = {
					setValue(it)
					textChange(it.text)
				},
				visualTransformation = visualTransformation,
				keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
				modifier = Modifier.fillMaxWidth(),
				backgroundColor = MaterialTheme.colors.background,
				label = { if (label != null) Text(text = stringResource(label)) }
			)
        }
        if (helperText != null)
            Text(
				text = stringResource(helperText),
				modifier = Modifier.padding(start = spaceMedium),
				style = MaterialTheme.typography.caption
			)
    }
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun previewTextInputNoValue() {
    TextInput(label = R.string.name) {}
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun previewTextInput() {
    TextInput(label = R.string.name, oldValue = "Adam") {}
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun previewTextInputHelperText() {
    TextInput(label = R.string.name, oldValue = "Adam", helperText = R.string.optional) {}
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun previewTextInputPassword() {
    TextInput(label = R.string.name, oldValue = "Adam", keyboardType = KeyboardType.Password) {}
}
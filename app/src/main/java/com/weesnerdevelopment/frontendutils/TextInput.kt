package com.weesnerdevelopment.frontendutils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview

@Composable
fun TextInput(
	@StringRes label: Int? = null,
	oldValue: String? = null,
	@StringRes helperText: Int? = null,
	keyboardType: KeyboardType = KeyboardType.Text,
	modifier: Modifier = Modifier,
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
        TextField(
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
    TextInput(R.string.name) {}
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun previewTextInput() {
    TextInput(R.string.name, "Adam") {}
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun previewTextInputHelperText() {
    TextInput(R.string.name, "Adam", R.string.optional) {}
}

@Preview(showBackground = true, widthDp = 440)
@Composable
private fun previewTextInputPassword() {
    TextInput(R.string.name, "Adam", keyboardType = KeyboardType.Password) {}
}
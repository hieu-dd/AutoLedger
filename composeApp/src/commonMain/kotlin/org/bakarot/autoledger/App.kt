package org.bakarot.autoledger

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bakarot.autoledger.ui.screen.auth.LoginScreen
import org.bakarot.autoledger.ui.theme.AutoLedgerTheme

@Composable
@Preview
fun App() {
    AutoLedgerTheme {
        LoginScreen()
    }
}

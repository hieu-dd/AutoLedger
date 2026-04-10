package org.bakarot.autoledger

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bakarot.autoledger.ui.screen.main.MainScreen
import org.bakarot.autoledger.ui.theme.AutoLedgerTheme

@Composable
@Preview
fun App() {
    AutoLedgerTheme {
        MainScreen()
    }
}

package com.slh.studentlabhub.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slh.studentlabhub.ui.components.DotMatrixText
import com.slh.studentlabhub.ui.theme.SlhBlack
import com.slh.studentlabhub.ui.theme.SlhMono
import com.slh.studentlabhub.ui.theme.SlhOutline
import com.slh.studentlabhub.ui.theme.SlhRed
import com.slh.studentlabhub.ui.theme.SlhSans
import com.slh.studentlabhub.ui.theme.SlhSurface
import com.slh.studentlabhub.ui.theme.SlhTextDisabled
import com.slh.studentlabhub.ui.theme.SlhTextMuted
import com.slh.studentlabhub.ui.theme.SlhTextSecondary
import com.slh.studentlabhub.ui.theme.SlhWhite
import com.slh.studentlabhub.ui.theme.StudentLabHubTheme

data class LoginUiState(
    val accountEmail: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@Composable
fun LoginScreen(
    state: LoginUiState,
    onSignInClick: () -> Unit,
    onChooseAnotherAccountClick: () -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlhBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(104.dp))
            SlhLogo()
            Spacer(modifier = Modifier.height(48.dp))
            AccountField(email = state.accountEmail)
            Spacer(modifier = Modifier.height(15.dp))
            GoogleSignInButton(
                isLoading = state.isLoading,
                onClick = onSignInClick,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = SlhOutline)
            Spacer(modifier = Modifier.height(16.dp))
            SecondaryOutlinedButton(
                text = "Выбрать другой аккаунт",
                enabled = !state.isLoading,
                onClick = onChooseAnotherAccountClick,
            )
            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.errorMessage,
                    color = SlhRed,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Принимаю условия использования",
                color = SlhTextDisabled,
                fontFamily = SlhSans,
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(role = Role.Button, onClick = onTermsClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SlhLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(SlhRed),
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        DotMatrixText(
            text = "SLH",
            color = SlhWhite,
            dotSize = 7.dp,
            dotGap = 2.dp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "STUDENTLABHUB",
            color = SlhWhite,
            fontFamily = SlhMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 6.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(2.dp)
                .background(SlhRed),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Управление учебным процессом",
            color = SlhTextMuted,
            fontFamily = SlhSans,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
        )
    }
}

@Composable
private fun AccountField(email: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SlhSurface)
            .border(1.dp, SlhOutline, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (email == null) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SlhTextMuted),
                    )
                }
            }
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = "(Google account)",
                color = SlhTextMuted,
                style = MaterialTheme.typography.labelMedium,
            )
        } else {
            Text(
                text = email,
                color = SlhTextSecondary,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(
        topStart = 2.dp,
        bottomStart = 2.dp,
        topEnd = 8.dp,
        bottomEnd = 8.dp,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(shape)
            .background(SlhWhite)
            .clickable(
                enabled = !isLoading,
                role = Role.Button,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(SlhRed),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = SlhBlack,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "ВОЙТИ ЧЕРЕЗ GOOGLE",
                    color = SlhBlack,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
    }
}

@Composable
private fun SecondaryOutlinedButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, SlhOutline, RoundedCornerShape(8.dp))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) SlhTextMuted else SlhTextDisabled,
            fontFamily = SlhSans,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 844)
@Composable
private fun LoginScreenPreview() {
    StudentLabHubTheme {
        LoginScreen(
            state = LoginUiState(),
            onSignInClick = {},
            onChooseAnotherAccountClick = {},
            onTermsClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, widthDp = 390, heightDp = 844)
@Composable
private fun LoginScreenLoadingPreview() {
    StudentLabHubTheme {
        LoginScreen(
            state = LoginUiState(
                accountEmail = "student@gmail.com",
                isLoading = true,
                errorMessage = "Не удалось подключиться к Google",
            ),
            onSignInClick = {},
            onChooseAnotherAccountClick = {},
            onTermsClick = {},
        )
    }
}
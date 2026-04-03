package com.example.diplom.feature.profile.ui

import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val ProfileBg = Color(0xFF101114)
private val CardDark = Color(0xFF181A1F)
private val CardDarkSecondary = Color(0xFF1F2228)
private val DividerDark = Color(0xFF2C3138)
private val TextPrimary = Color(0xFFF3F4F6)
private val TextSecondary = Color(0xFFADB3BC)
private val AccentOrange = Color(0xFFFF9F0A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onManageProductsClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    isAuthorized: Boolean = false,
    displayName: String? = null,
    email: String? = null,
    roleLabel: String? = null
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = ProfileBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Профиль",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = "Уведомления",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ProfileBg,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileBg)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            if (isAuthorized) {
                AuthorizedProfileCard(
                    name = displayName ?: "Пользователь",
                    email = email,
                    roleLabel = roleLabel,
                    onLogoutClick = onLogoutClick
                )
            } else {
                GuestProfileCard(
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick
                )
            }

            Spacer(Modifier.height(14.dp))

            if (isAuthorized) {
                RoleActionsBlock(
                    roleLabel = roleLabel,
                    onManageProductsClick = onManageProductsClick,
                    onAdminClick = onAdminClick
                )

                if (!roleLabel.isNullOrBlank() && roleLabel != "Пользователь") {
                    Spacer(Modifier.height(14.dp))
                }
            }

            Spacer(Modifier.height(14.dp))

            PromoCard()

            Spacer(Modifier.height(14.dp))

            SettingsBlock {
                SettingsRow(
                    icon = Icons.Outlined.LocationOn,
                    title = "Москва",
                    subtitle = null,
                    onClick = { }
                )
            }

            Spacer(Modifier.height(14.dp))

            SettingsBlock {
                SettingsRow(
                    icon = Icons.Outlined.FavoriteBorder,
                    title = "Избранное",
                    subtitle = null,
                    onClick = { }
                )
                DarkDivider()
                SettingsRow(
                    icon = Icons.Outlined.CompareArrows,
                    title = "Сравнение",
                    subtitle = null,
                    onClick = { }
                )
                DarkDivider()
                SettingsRow(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    title = "Обратная связь",
                    subtitle = null,
                    onClick = { }
                )
            }

            Spacer(Modifier.height(14.dp))

            SettingsBlock {
                SettingsRow(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Помощь",
                    subtitle = null,
                    onClick = { }
                )
                DarkDivider()
                SettingsRow(
                    icon = Icons.Outlined.Info,
                    title = "О приложении",
                    subtitle = "Демонстрационная версия для диплома",
                    onClick = { }
                )
                DarkDivider()
                SettingsRow(
                    icon = Icons.Outlined.Settings,
                    title = "Настройки",
                    subtitle = null,
                    onClick = { }
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun GuestProfileCard(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Войдите или зарегистрируйтесь",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Войти")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    android.util.Log.d("AuthFlow", "Register button clicked")
                    onRegisterClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Регистрация")
            }
        }
    }
}

@Composable
private fun AuthorizedProfileCard(
    name: String,
    email: String?,
    roleLabel: String?,
    onLogoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardDarkSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!email.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    if (!roleLabel.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentOrange)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = roleLabel,
                                color = Color.Black,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Text(
                    text = "›",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(14.dp))
            Divider(color = DividerDark)
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Вы вошли в аккаунт",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(Modifier.height(14.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Выйти")
            }
        }
    }
}

@Composable
private fun PromoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDarkSecondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "QR",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text = "QR-код для получения заказов",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Покажите код сотруднику магазина или используйте его на кассе самообслуживания.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SettingsBlock(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = TextSecondary
        )

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.titleLarge,
            color = TextSecondary
        )
    }
}

@Composable
private fun RoleActionsBlock(
    roleLabel: String?,
    onManageProductsClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    val isManager = roleLabel == "Менеджер" || roleLabel == "Администратор"
    val isAdmin = roleLabel == "Администратор"

    if (!isManager && !isAdmin) return

    SettingsBlock {
        if (isManager) {
            SettingsRow(
                icon = Icons.Outlined.Settings,
                title = "Управление товарами",
                subtitle = "Доступно менеджеру и администратору",
                onClick = onManageProductsClick
            )
        }

        if (isAdmin) {
            if (isManager) {
                DarkDivider()
            }

            SettingsRow(
                icon = Icons.Outlined.Info,
                title = "Администрирование",
                subtitle = "Функции администратора",
                onClick = onAdminClick
            )
        }
    }
}

@Composable
private fun DarkDivider() {
    Divider(
        modifier = Modifier.padding(start = 52.dp),
        color = DividerDark
    )
}
package com.moviebox.app.ui.membership

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviebox.app.auth.models.MembershipTier
import com.moviebox.app.auth.models.User
import com.moviebox.app.ui.theme.*

@Composable
fun MembershipPlanScreen(
    user: User?,
    onSelectPlan: (MembershipTier) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTier by remember { mutableStateOf(user?.plan ?: MembershipTier.VIP) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Text(
                text = "ShowTime+ VIP Plans",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Unlock Ultra HD 4K, high-speed chunk streaming, and zero buffering.",
            color = TextMuted,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Plan 1: Free Tier
        PlanCard(
            title = "Free Member",
            price = "₹0 / Free",
            features = listOf("720p HD Streaming", "Standard Segment Buffering", "Single Device"),
            isCurrent = user?.plan == MembershipTier.FREE,
            isSelected = selectedTier == MembershipTier.FREE,
            highlightColor = TextMuted,
            onSelect = { selectedTier = MembershipTier.FREE }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Plan 2: VIP Gold
        PlanCard(
            title = "VIP Gold",
            price = "₹99 / Month",
            features = listOf(
                "1080p Full HD Streaming",
                "High Speed Multi-Segment Chunks",
                "Background PiP Playback",
                "Multi-language Subtitles"
            ),
            isCurrent = user?.plan == MembershipTier.VIP,
            isSelected = selectedTier == MembershipTier.VIP,
            highlightColor = GoldVIP,
            onSelect = { selectedTier = MembershipTier.VIP }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Plan 3: Ultra 4K VIP (Best Value)
        PlanCard(
            title = "Ultra 4K VIP",
            price = "₹199 / Month",
            features = listOf(
                "4K Ultra HD + HDR Streaming",
                "Lightning Turbo Chunk Preloading",
                "Unlimited Offline Downloads",
                "VIP Discord & Stremio Addon Access",
                "Zero Buffering Guarantee"
            ),
            isCurrent = user?.plan == MembershipTier.PREMIUM_4K,
            isSelected = selectedTier == MembershipTier.PREMIUM_4K,
            highlightColor = AccentRed,
            onSelect = { selectedTier = MembershipTier.PREMIUM_4K }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onSelectPlan(selectedTier) },
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = TextWhite)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Activate ${selectedTier.displayName}",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    features: List<String>,
    isCurrent: Boolean,
    isSelected: Boolean,
    highlightColor: androidx.compose.ui.graphics.Color,
    onSelect: () -> Unit
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) highlightColor else BorderDark,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = highlightColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                if (isCurrent) {
                    Surface(
                        color = highlightColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "CURRENT PLAN",
                            color = highlightColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = price,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            features.forEach { feat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = highlightColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feat,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

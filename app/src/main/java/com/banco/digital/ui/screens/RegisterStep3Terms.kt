package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Step3TerminosYCondiciones(
    aceptoTerminos: Boolean,
    aceptoTratamientoDatos: Boolean,
    onTerminosChange: (aceptoTerminos: Boolean, aceptoTratamiento: Boolean) -> Unit,
    onContinue: () -> Unit
) {
    val mintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
    )
    val primaryDarkText = Color(0xFF042F2C)
    val isStep3Valid = aceptoTerminos && aceptoTratamientoDatos

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Términos y Privacidad",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Revisa y acepta los consentimientos legales requeridos",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta informativa de Seguridad y KYC
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF0FDF4),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Cumplimiento y Protección Bancaria",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF064E3B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tus datos biométricos y fotos de documento serán procesados de forma encriptada bajo estándares bancarios internacionales para prevenir fraudes e identificar tu titularidad.",
                        fontSize = 13.sp,
                        color = Color(0xFF047857),
                        lineHeight = 19.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Checkbox 1: Términos y Condiciones
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTerminosChange(!aceptoTerminos, aceptoTratamientoDatos) }
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = aceptoTerminos,
                    onCheckedChange = { onTerminosChange(it, aceptoTratamientoDatos) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF064E3B),
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Acepto los Términos y Condiciones",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Reglamento de cuentas de ahorro y transferencias digitales.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Checkbox 2: Tratamiento de Datos Personales
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTerminosChange(aceptoTerminos, !aceptoTratamientoDatos) }
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = aceptoTratamientoDatos,
                    onCheckedChange = { onTerminosChange(aceptoTerminos, it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF064E3B),
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Autorizo el Tratamiento de Datos y Biometría",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Consentimiento para validación KYC y escaneo facial.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Botón Continuar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = if (isStep3Valid) mintGradient else Brush.linearGradient(
                        listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                    )
                )
                .clickable(enabled = isStep3Valid) {
                    if (isStep3Valid) onContinue()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continuar a Captura de DNI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep3Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }
    }
}

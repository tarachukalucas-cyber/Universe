package com.universe.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Void = Color(0xFF090D18)
private val Panel = Color(0xFF151D2D)
private val Cyan = Color(0xFF68E1E8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UniverseApp() }
    }
}

@Composable
private fun UniverseApp() {
    var page by remember { mutableStateOf("Início") }
    MaterialTheme(colorScheme = darkColorScheme(primary = Cyan, background = Void, surface = Panel)) {
        Row(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF17243A), Void)))) {
            NavigationRail(containerColor = Color(0x99090D18)) {
                Spacer(Modifier.height(20.dp))
                listOf("Início" to Icons.Default.Home, "Versões" to Icons.Default.ViewList, "Mods" to Icons.Default.Extension, "Servidores" to Icons.Default.Public, "Ajustes" to Icons.Default.Settings).forEach { (name, icon) ->
                    NavigationRailItem(selected = page == name, onClick = { page = name }, icon = { Icon(icon, null) }, label = { Text(name, fontSize = 10.sp) })
                }
            }
            Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("UNIVERSE", color = Cyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                Text(page, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                when (page) {
                    "Início" -> {
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("MINECRAFT JAVA", color = Cyan, fontSize = 12.sp, letterSpacing = 2.sp)
                                Text("Seu universo começa aqui.", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                                Text("O ambiente do launcher está pronto para receber o runtime Java.", color = Color.LightGray)
                                Button(onClick = { page = "Versões" }, modifier = Modifier.fillMaxWidth()) { Text("CONFIGURAR JOGO") }
                            }
                        }
                        StatusTile("Runtime do Minecraft", "Aguardando integração", Icons.Default.Terminal)
                        StatusTile("Gerenciador de mods", "Estrutura planejada", Icons.Default.Extension)
                        StatusTile("Multiplayer", "Lista de servidores em desenvolvimento", Icons.Default.Public)
                    }
                    "Versões" -> InfoPage("Versões instaladas", "O instalador de versões e o runtime Java ainda serão integrados.")
                    "Mods" -> InfoPage("Central de mods", "A integração precisa validar loader, versão do jogo e dependências antes de instalar mods.")
                    "Servidores" -> InfoPage("Multiplayer", "A lista local de servidores será implementada antes de qualquer serviço online.")
                    else -> InfoPage("Configurações", "As preferências de desempenho e controles serão adicionadas junto ao runtime.")
                }
            }
        }
    }
}

@Composable
private fun StatusTile(title: String, detail: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Cyan, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(14.dp))
            Column { Text(title, color = Color.White, fontWeight = FontWeight.SemiBold); Text(detail, color = Color.LightGray, fontSize = 12.sp) }
        }
    }
}

@Composable
private fun InfoPage(title: String, description: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(description, color = Color.LightGray)
        }
    }
}

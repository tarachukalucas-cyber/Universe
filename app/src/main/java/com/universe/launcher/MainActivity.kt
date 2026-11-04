package com.universe.launcher

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URI

private val Void = Color(0xFF090D18)
private val Panel = Color(0xFF151D2D)
private val Cyan = Color(0xFF68E1E8)
private val Muted = Color(0xFFAAB6C8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UniverseApp(this) }
    }
}

private data class ServerEntry(val name: String, val address: String)

@Composable
private fun UniverseApp(context: Context) {
    var page by remember { mutableStateOf("Início") }
    var servers by remember { mutableStateOf(loadServers(context)) }
    var showDialog by remember { mutableStateOf(false) }
    var serverName by remember { mutableStateOf("") }
    var serverAddress by remember { mutableStateOf("") }
    var mods by remember { mutableStateOf(listMods(context)) }
    var notice by remember { mutableStateOf("Universe iniciado") }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching {
            val name = context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                val i = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (i >= 0 && c.moveToFirst()) c.getString(i) else null
            } ?: "mod.jar"
            require(name.endsWith(".jar", true)) { "Selecione um arquivo .jar." }
            val safe = name.replace(Regex("[^A-Za-z0-9._-]"), "_")
            val dir = File(context.filesDir, "mods").apply { mkdirs() }
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "Não foi possível abrir o arquivo." }
                File(dir, safe).outputStream().use { output -> input.copyTo(output) }
            }
            mods = listMods(context)
            notice = "Mod importado: $safe"
        }.onFailure { notice = it.message ?: "Falha ao importar mod." }
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Cyan, background = Void, surface = Panel)) {
        Row(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF17243A), Void)))) {
            NavigationRail(containerColor = Color(0x99090D18)) {
                Spacer(Modifier.height(12.dp))
                listOf("Início" to Icons.Default.Home, "Versões" to Icons.Default.ViewList, "Mods" to Icons.Default.Extension, "Servidores" to Icons.Default.Public, "Ajustes" to Icons.Default.Settings).forEach { (label, icon) ->
                    NavigationRailItem(selected = page == label, onClick = { page = label }, icon = { Icon(icon, null) }, label = { Text(label, fontSize = 10.sp) })
                }
            }
            Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("UNIVERSE", color = Cyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                Text(page, color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                when (page) {
                    "Início" -> {
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("MINECRAFT JAVA • ANDROID", color = Cyan, fontSize = 11.sp, letterSpacing = 2.sp)
                                Text("Explore sem limites.", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                                Text("Interface original Universe. O jogo ainda depende da integração de um runtime Android compatível.", color = Muted)
                                Button(onClick = { page = "Versões" }, modifier = Modifier.fillMaxWidth()) { Text("CONFIGURAR JOGO") }
                            }
                        }
                        Tile("Runtime Java", "Ainda não integrado", Icons.Default.Terminal)
                        Tile("Mods importados", "${mods.size} arquivo(s) .jar", Icons.Default.Extension)
                        Tile("Servidores salvos", "${servers.size} servidor(es)", Icons.Default.Public)
                        Text(notice, color = Muted, fontSize = 11.sp)
                    }
                    "Versões" -> Info("Gerenciador de versões", "A instalação e inicialização do Minecraft ainda não estão implementadas. A integração exige um runtime Android compatível e autenticação oficial.")
                    "Mods" -> {
                        Text("Importe arquivos .jar para o armazenamento privado do launcher.", color = Muted)
                        Button(onClick = { picker.launch(arrayOf("application/java-archive", "application/octet-stream")) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("IMPORTAR MOD .JAR")
                        }
                        if (mods.isEmpty()) Info("Nenhum mod importado", "Escolha um arquivo .jar no dispositivo.")
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(mods, key = { it }) { name ->
                                Tile(name, "${File(context.filesDir, "mods/$name").length() / 1024} KB • importado", Icons.Default.Extension)
                            }
                        }
                        Text("Importar não ativa o mod. Compatibilidade com Minecraft e modloader ainda não é validada.", color = Muted, fontSize = 11.sp)
                    }
                    "Servidores" -> {
                        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("ADICIONAR SERVIDOR")
                        }
                        if (servers.isEmpty()) Info("Sua lista está vazia", "Adicione o endereço de um servidor Minecraft Java.")
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(servers, key = { it.address }) { server ->
                                Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Dns, null, tint = Cyan)
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(server.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                                            Text(server.address, color = Muted, fontSize = 12.sp)
                                        }
                                        IconButton(onClick = {
                                            servers = servers.filterNot { it.address == server.address }
                                            saveServers(context, servers)
                                        }) { Icon(Icons.Default.Delete, "Remover", tint = Muted) }
                                    }
                                }
                            }
                        }
                        Text("Lista local salva no aparelho. Conectar ainda depende do runtime.", color = Muted, fontSize = 11.sp)
                    }
                    else -> Info("Configurações", "Controles, memória e resolução serão integrados depois do runtime.")
                }
            }
        }
        if (showDialog) AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Novo servidor") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(serverName, { serverName = it }, label = { Text("Nome") }, singleLine = true)
                    OutlinedTextField(serverAddress, { serverAddress = it }, label = { Text("Host ou host:porta") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val address = serverAddress.trim()
                    val valid = address.isNotBlank() && !address.contains(" ") && runCatching { URI("tcp://$address").host != null }.getOrDefault(false)
                    if (valid && servers.none { it.address.equals(address, true) }) {
                        servers = servers + ServerEntry(serverName.trim().ifBlank { address }, address)
                        saveServers(context, servers)
                        showDialog = false; serverName = ""; serverAddress = ""
                    } else notice = "Endereço inválido ou já cadastrado."
                }) { Text("SALVAR") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("CANCELAR") } }
        )
    }
}

private fun loadServers(context: Context): List<ServerEntry> {
    val raw = context.getSharedPreferences("universe", Context.MODE_PRIVATE).getString("servers", "[]") ?: "[]"
    return runCatching {
        val a = JSONArray(raw)
        (0 until a.length()).map { i -> a.getJSONObject(i).let { ServerEntry(it.getString("name"), it.getString("address")) } }
    }.getOrDefault(emptyList())
}

private fun saveServers(context: Context, servers: List<ServerEntry>) {
    val a = JSONArray()
    servers.forEach { a.put(JSONObject().put("name", it.name).put("address", it.address)) }
    context.getSharedPreferences("universe", Context.MODE_PRIVATE).edit().putString("servers", a.toString()).apply()
}

private fun listMods(context: Context): List<String> =
    File(context.filesDir, "mods").listFiles()?.filter { it.isFile && it.extension.equals("jar", true) }?.map { it.name }?.sorted() ?: emptyList()

@Composable
private fun Tile(title: String, detail: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Cyan, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(12.dp))
            Column { Text(title, color = Color.White, fontWeight = FontWeight.SemiBold); Text(detail, color = Muted, fontSize = 11.sp) }
        }
    }
}

@Composable
private fun Info(title: String, description: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(description, color = Muted)
        }
    }
}

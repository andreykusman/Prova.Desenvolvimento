package com.example.prova

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prova.ui.theme.ProvaTheme



// Tela de Cadastro de Produto
@Composable
fun TelaCadastroProduto(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome do Produto") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = preco,
            onValueChange = { preco = it },
            label = { Text("Preço") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = quantidade,
            onValueChange = { quantidade = it },
            label = { Text("Quantidade em Estoque") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Validação dos campos
            if (nome.isEmpty() || categoria.isEmpty() || preco.isEmpty() || quantidade.isEmpty()) {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_LONG).show()
            } else {
                val precoDouble = preco.toDoubleOrNull()
                val quantidadeInt = quantidade.toIntOrNull()

                if (precoDouble == null || quantidadeInt == null || precoDouble <= 0 || quantidadeInt < 1) {
                    Toast.makeText(context, "Preço deve ser maior que 0 e quantidade maior que 0", Toast.LENGTH_LONG).show()
                } else {
                    val produto = Produto(nome, categoria, precoDouble, quantidadeInt)
                    Estoque.adicionarProduto(produto)
                    navController.navigate("listaProdutos")
                }
            }
        }) {
            Text("Cadastrar")
        }
    }
}

// Tela de Lista de Produtos
@Composable
fun TelaListaProdutos(navController: NavController) {
    val produtos = Estoque.listaProdutos

    Column(modifier = Modifier.padding(16.dp)) {
        LazyColumn {
            items(produtos.size) { index ->
                val produto = produtos[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${produto.nome} (${produto.quantidade} unidades)")
                    Button(onClick = {
                        navController.navigate("detalhesProduto/${produto.nome}")
                    }) {
                        Text("Detalhes")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("estatisticas") }) {
            Text("Exibir Estatísticas")
        }
    }
}

// Tela de Estatísticas
@Composable
fun TelaEstatisticas(navController: NavController) {
    val valorTotalEstoque = Estoque.calcularValorTotalEstoque()
    val quantidadeTotalProdutos = Estoque.quantidadeTotalProdutos()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Valor Total do Estoque: R$$valorTotalEstoque")
        Text("Quantidade Total de Produtos: $quantidadeTotalProdutos")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

// Tela de Navegação
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "cadastroProduto") {
        composable("cadastroProduto") { TelaCadastroProduto(navController) }
        composable("listaProdutos") { TelaListaProdutos(navController) }
        composable("detalhesProduto/{nomeProduto}") { backStackEntry ->
            TelaDetalhesProduto(backStackEntry.arguments?.getString("nomeProduto"), navController)
        }
        composable("estatisticas") { TelaEstatisticas(navController) }
    }
}

// Tela de Detalhes do Produto
@Composable
fun TelaDetalhesProduto(nomeProduto: String?, navController: NavController) {
    val produto = Estoque.listaProdutos.find { it.nome == nomeProduto }

    Column(modifier = Modifier.padding(16.dp)) {
        produto?.let {
            Text("Nome: ${produto.nome}")
            Text("Categoria: ${produto.categoria}")
            Text("Preço: R$${produto.preco}")
            Text("Quantidade em Estoque: ${produto.quantidade}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Voltar")
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvaTheme {
                Navigation()
            }
        }
    }
}

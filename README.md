# Hidratapp 💧

Aplicativo Android desenvolvido em **Kotlin** para monitoramento, controle e incentivo à hidratação diária, permitindo o registro de diferentes tipos de bebidas, acompanhamento de metas personalizadas e alertas de consumo consciente.

---

## 📱 Funcionalidades

- **Cálculo de Meta Diária:** Determina a quantidade ideal de ingestão líquida (em ml) baseando-se no perfil individual (peso, idade e sexo).
- **Cálculo de IMC:** Apresenta o Índice de Massa Corporal a partir do peso e altura informados.
- **Registro de Consumo:** Cadastro rápido de bebidas com opções de 200 ml, 300 ml, 500 ml ou volume personalizado, data e observações.
- **Diversidade de Bebidas:** Suporte a água, suco, refrigerante, café, energético, cerveja, vinho e destilados.
- **Alerta de Moderação:** Sistema de segurança que notifica o usuário caso registre 2 ou mais bebidas alcoólicas consecutivas, sugerindo a ingestão de água.
- **Histórico e Detalhes:** Listagem completa das bebidas cadastradas em `RecyclerView`, com visualização individual, opção de favoritar, editar e excluir.
- **Persistência de Dados Local:** Armazenamento seguro de registros em SQLite e dados de perfil em SharedPreferences.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Interface:** XML com Material Components (Material 3)
- **Design:** Suporte a Edge-to-Edge e Window Insets
- **Banco de Dados Local:** SQLite via `SQLiteOpenHelper`
- **Armazenamento de Preferências:** `SharedPreferences`
- **Componentes Android:** `RecyclerView`, `Spinner`, `ProgressBar`, `AlertDialog`
- **Gradle:** Versão 9.x com Kotlin DSL (`build.gradle.kts`)

---

## 📂 Estrutura do Projeto

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/hidratapp/
│   ├── BancoHelper.kt            # Criação e operações CRUD no banco SQLite (hidrata.db)
│   ├── Bebida.kt                 # Modelo de dados da bebida e listas de tipos
│   ├── BebidaAdapter.kt          # Adaptador para exibição na RecyclerView
│   ├── Calculos.kt               # Lógica de cálculo de meta de água, IMC e alertas
│   ├── MainActivity.kt           # Tela principal com progresso diário e atalhos
│   ├── CadastroBebidaActivity.kt # Tela de registro e edição de bebidas
│   ├── DetalheBebidaActivity.kt  # Tela de detalhes, exclusão e favoritos
│   ├── ListaActivity.kt          # Tela com histórico das bebidas cadastradas
│   ├── PerfilActivity.kt         # Tela de configuração do perfil do usuário
│   └── SobreActivity.kt          # Tela informativa sobre o aplicativo
└── res/
    ├── layout/                   # Layouts das telas e itens da lista
    └── values/                   # Cores, temas e textos do aplicativo
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Android Studio** (versão Ladybug / Iguana ou superior recomendada)
- **JDK 11** ou superior
- Dispositivo físico Android ou Emulador (API 24+)

### Passos
1. Clone ou abra a pasta do projeto no Android Studio:
   ```bash
   cd UPCampusPlus
   ```
2. Sincronize as dependências do Gradle (**Sync Project with Gradle Files**).
3. Compile e execute o projeto diretamente pelo Android Studio ou via terminal:
   ```bash
   ./gradlew assembleDebug
   ```
4. O APK gerado estará disponível em:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

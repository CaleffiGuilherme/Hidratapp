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
│   ├── Bebida.kt                 # Modelo de dados da bebida
│   ├── TipoBebida.kt             # Enum com nomes, ícones e regras das categorias
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
- **JDK 17** (usado pelo Gradle; o código é compilado com compatibilidade Java 11)
- Dispositivo físico Android ou Emulador (API 24+)

### Passos
1. Clone ou abra a pasta do projeto no Android Studio:
   ```bash
   cd Hidratapp
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


## Melhorias para a apresentação

- Lembretes locais configuráveis (30, 60, 90 ou 120 minutos), início/fim, permissões, teste de notificação e reagendamento após reiniciar o aparelho.
- Gráfico de sete dias com volume por dia, média incluindo dias sem registros e dias que atingiram a meta **atual**. Os filtros afetam o histórico, não o resumo semanal.
- Meta única entre perfil, início, estatísticas e dicas, incluindo nível de atividade física.
- Registro rápido de água (+200, +300 e +500 ml), com Desfazer por seis segundos.
- Histórico filtrável por período, tipo e favoritos. Horário editável e ordenação pela data/hora de consumo.
- Banco versão 3 com migração sem apagar bebidas/sintomas; horários antigos permanecem desconhecidos.
- Validação de quantidade/data futura, mensagens de falha na gravação e preservação da etapa do perfil ao recriar a tela.
- Tela “Regras, limites e fontes”, acessível pelo início e Sobre. A fórmula é uma regra estimativa do projeto, sem validação clínica individual. As dicas são educativas e não diagnosticam sintomas.

### Verificação

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
# Com um emulador/dispositivo de teste conectado:
.\gradlew.bat :app:connectedDebugAndroidTest
```

Os testes cobrem cálculos, sequência de álcool, agendamento, migrações das versões 1 e 2, CRUD, filtros, registro/desfazer, recriação do perfil e publicação de notificação.

### Roteiro de demonstração

1. Cadastre o perfil e confira a meta; registre água pelos atalhos e demonstre Desfazer.
2. Cadastre outra bebida com data/hora, edite, favorite e use os filtros no histórico.
3. Mostre o gráfico e explique que a comparação usa a meta atual e dias sem registro contam como zero.
4. Configure e salve os lembretes; autorize notificações e use “Enviar notificação de teste”.
5. Abra as dicas, registre um sintoma e mostre as regras e fontes na tela Sobre.

Lembretes usam alarmes aproximados: o Android pode atrasá-los por economia de bateria. O período deve começar e terminar no mesmo dia. Notificações exigem autorização e canal habilitado. Forçar a parada do aplicativo suspende o trabalho até reabri-lo.


## Identidade visual

A interface mantém a identidade da água: azul, branco e detalhes em verde-água. A organização usa um foco principal por tela, bastante espaço, cartões arredondados e ações claras.

- Início com progresso circular e preenchimento de água animado, volume em destaque e mensagens conforme o progresso.
- Navegação inferior com Hoje, Evolução, Lembretes e Perfil, mantendo o registro de bebida sempre acessível.
- Atalhos rápidos, cartões de cuidado, respostas visuais ao toque e confirmação com Desfazer.
- Mesma paleta, hierarquia e superfícies no perfil, cadastro, histórico, sintomas, dicas e lembretes.
- Aparência clara consistente, textos escuros legíveis e conteúdo rolável.


### Abertura, configuração e telas internas

A abertura e as boas-vindas compartilham uma ilustração vetorial de água. A configuração inicial tem quatro etapas com opções em cartões, progresso e resumo final com acesso ao dia. Cadastro, detalhes, evolução, sintomas, dicas, lembretes, fontes e Sobre seguem os mesmos cabeçalhos, cores e superfícies da tela inicial. Dicas podem ser expandidas e recolhidas.

O teste de primeiro acesso percorre as quatro etapas, salva o perfil e verifica a meta no resumo. A transição automática da abertura é cancelada quando a tela é fechada.


### Consumo por bebida na tela inicial

Deslize horizontalmente no círculo de consumo ou use as setas para alternar entre Água, os outros tipos registrados hoje e o Total do dia. Registros do mesmo tipo são somados. Água permanece como primeira página; as demais aparecem quando há registros. O total segue a regra existente de excluir álcool da meta. A seleção é preservada ao recriar a tela, e os atalhos rápidos continuam registrando água. A rolagem vertical permanece disponível.

### Tipos de bebida centralizados

`TipoBebida` reúne o valor permanente do banco (`valorBanco`), o recurso do nome exibido (`nomeRes`), o ícone, o ID do chip e as classificações de álcool e cafeína. O cadastro percorre o enum e infla `item_tipo_bebida.xml`; as cores continuam nos seletores `drink_chip_*`. Não existem listas paralelas de nomes e IDs.

Para renomear uma opção, altere o texto `tipo_*` correspondente em `res/values/strings.xml` e preserve `valorBanco`. Os registros atuais continuam válidos no SQLite versão 3, sem migração. Filtros, carrossel, detalhes e regras consultam o enum; categorias desconhecidas continuam visíveis pelo texto original. Para uma nova categoria, inclua sua entrada e declare um ID em `res/values/ids.xml`.

## Melhorias de confiabilidade e manutenção

- **Desfazer exclusão:** após confirmar a exclusão, os detalhes oferecem dez segundos para desfazer. A restauração preserva identificador, favorito, data, horário e observação, inclusive após recriar a tela. Uma colisão de identificador não sobrescreve outro registro.
- **Comemoração da meta:** um pulso discreto aparece quando o total válido atinge a meta, uma vez por data. O marcador fica nas preferências e a animação respeita a desativação de animações do sistema. A mensagem não substitui o botão Desfazer do registro rápido.
- **Textos centralizados:** rótulos, mensagens, nomes das bebidas, opções de sintomas, notificações e descrições acessíveis ficam em `res/values/strings.xml`. Os valores permanentes do SQLite continuam separados dos rótulos exibidos.
- **Virada do dia:** o painel agenda a próxima meia-noite conforme o fuso local, atualiza a data e consulta os registros novamente. Também reage a mudanças de hora, data e fuso e atualiza ao voltar ao primeiro plano. O agendamento da tela é cancelado quando ela deixa de estar visível.
- **Persistência fora da interface:** `BebidaRepository` concentra as operações SQLite, e `BancoAsync` as executa numa fila em segundo plano. `DadosActivity` devolve resultados à thread principal e ignora callbacks de telas encerradas. Cadastro, edição, detalhes, filtros, dicas, sintomas e atalhos usam esse fluxo. Botões de gravação ficam desabilitados durante a operação.

O banco permanece na versão 3. Estas cinco melhorias não incluem exportação ou restauração de backup em arquivo.

Validação desta atualização: 21 testes unitários e 18 instrumentados aprovados em emulador Android 17, compilação concluída e lint sem erros. Os cenários novos verificam restauração após recriação, preservação dos campos, execução do repositório fora da thread principal, virada de ano e comemoração diária. Há teste unitário de mudança de horário de verão.

### Pontos rápidos de alteração

`AjustesDoApp.kt` reúne volumes dos atalhos, limite por registro, tempos de Desfazer, duração das animações e coeficientes da meta. Os textos dos atalhos e a mensagem do limite acompanham esses valores automaticamente. A ordem e as faixas de idade da fórmula continuam em `Calculos.metaAgua`.

`strings.xml` reúne nomes e mensagens; `colors.xml` reúne a paleta e as cores dos chips selecionados. Aliases como `teal_primary` apontam para a cor principal. As tonalidades específicas dos desenhos em Canvas continuam em `HydrationProgressView.kt` e `WaterIllustrationView.kt`; as dos cartões educativos, em `DicasActivity.kt`.

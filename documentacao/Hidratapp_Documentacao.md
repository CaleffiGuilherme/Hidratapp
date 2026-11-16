# HIDRATAPP

UNIVERSIDADE POSITIVO

CIÊNCIA DA COMPUTAÇÃO

DESENVOLVIMENTO DE APLICATIVOS MÓVEIS

GUILHERME CALEFFI PELLENS PEREIRA

GABRIEL CARDOSO GONÇALVES

HIDRATAPP

Documentação Hidratapp
Relatório Técnico

Curitiba

2026

Universidade Positivo

Ciência da computação

Desenvolvimento de aplicativos móveis

GUILHERME CALEFFI PELLENS PEREIRA

GABRIEL CARDOSO GONÇALVES

HIDRATAPP

Documentação Hidratapp
Relatório Técnico

Relatório técnico apresentado à disciplina de
Desenvolvimento de Aplicativos Móveis
do curso de Ciência da Computação,
da Universidade Positivo,
como registro da proposta, implementação
e avaliação técnica do Hidratapp.

Professor: Armando Augusto Piva

16/11/2026

Curitiba

2026

RESUMO

Este trabalho propõe o Hidratapp, um aplicativo para Android que registra o que o usuário bebeu e estima sua meta de líquidos diárias. Nesse trabalho, é descrita a proposta, a modelagem dos dados, as telas, os testes realizados. O app foi desenvolvido em Kotlin e utiliza Layouts em XML e elementos visuais de Material Components. Os dados de registro são persistidos em um banco SQLite no dispositivo. O perfil e as configurações são armazenados em SharedPreferences. O perfil é utilizado para calcular a meta. Em seguida, o consumo é exibido agrupado por bebida, o histórico dos registros e um resumo do consumo semanal. As funcionalidades incluídas foram: editar registro, favoritar, desfazer exclusão, anotar sintomas, ler dicas, configurar lembretes. O código foi dividido em partes para facilitar a manutenção e o banco foi consultado em segundo plano para que a tela não fique travada. O painel é atualizado com a mudança do dia e celebra a meta, uma única vez por dia. A verificação inclui geração APK, análise automática sem erros, 21 testes unitários e 18 testes no emulador do Android 17. Os testes demonstram que as situações testadas funcionam, mas não garantem que funcionará em todos os aparelhos. A meta estimada é apenas uma estimativa do projeto. O app não realiza diagnóstico e não substitui orientação profissional.

Palavras-chave: aplicativo android, kotlin, hidratação, sqlite, testes de software.

LISTA DE ILUSTRAÇÕES

FIGURA 1 – CATÁLOGO DE BEBIDAS	11

FIGURA 2 – META DIÁRIA COMPARTILHADA	11

FIGURA 3 – MIGRAÇÃO DE ESQUEMA	12

FIGURA 4 – VALIDAÇÃO VOLUME CADASTRADO	12

FIGURA 5 – AGRUPAMENTO POR CATEGORIA	13

FIGURA 6 – AGENDAR PRÓXIMO AVISO LOCAL	13

FIGURA 7 – EXECUTAR TRABALHO E ENTREGAR PARA INTERFACE	14

FIGURA 8 – ABRIR E MOSTRAR AO USUÁRIO PELO PRIMEIRO VEZ	15

FIGURA 9 – MEDIDAS E ATIVIDADE FÍSICA	16

FIGURA 10 – CADASTRO DE BEBIDA E CONSULTA DE CONSUMO POR CATEGORIA	17

FIGURA 11 – CONSULTA SEMANAL E CONFIGURAÇÃO DE AVISOS	18

FIGURA 12 – DETALHE DE BEBIDA E EXCLUSÃO COM DESCARTAR	19

SUMÁRIO

1 INTRODUÇÃO	5

2 PROPOSTA E OBJETIVOS	6

3 PROCESSO DE DESENVOLVIMENTO E AMBIENTE	7

4 REQUISITOS DO SISTEMA	8

5 ARQUITETURA E ORGANIZAÇÃO DO PROJETO	9

6 MODELO DE DADOS E PERSISTÊNCIA	10

7 IMPLEMENTAÇÃO DOS FLUXOS	11

7.1 Modelos catálogo e cálculo da meta	11

7.2 Validação gravação e manutenção do esquema	12

7.3 Acompanhamento diário e lembretes	13

7.4 Execução assíncrona e continuidade do uso	14

8 INTERFACE E JORNADA DE USO	15

8.1 Configuração do perfil	16

8.2 Registro e acompanhamento	17

8.3 Histórico estatísticas e lembretes	18

8.4 Detalhes e recuperação de registros	19

9 DECISÕES TÉCNICAS E LIMITAÇÕES	20

10 VERIFICAÇÃO E RESULTADOS	21

11 PROCEDIMENTOS DE EXECUÇÃO E USO	22

11.1 Consulta edição e informações complementares	23

12 AVALIAÇÃO TÉCNICA	24

13 CONSIDERAÇÕES FINAIS	25

## 1 INTRODUÇÃO

O Hidratapp foi desenvolvido na intenção de reunir em um só lugar o que o usuário bebeu, quanto bebeu e em que momento. Sem que isso esteja registrado, o usuário não consegue ter fácil acesso a seu consumo ao longo do dia. O objetivo do aplicativo é ajudar o usuário a acompanhar no seu celular seus consumos de líquido e estima uma meta diárias.

O objetivo deste trabalho é aplicar os conceitos aprendidos na disciplina. Criar as telas, as funcionalidades, armazenar os dados e testar o funcionamento. O relatório descreve o que o aplicativo deve realizar, a divisão do código e como o aplicativo funciona.

O usuário começa preenchendo seu perfil. Em seguida, pode cadastrar suas bebidas, visualizando-as na tela principal, histórico e estatísticas. Também pode marcar favoritos, anotar sintomas, ler dicas, configurar lembretes. O painel organiza seu consumo por tipo de bebida, mostrando o total de consumo para a meta.

O aplicativo visa ajudar a organizar-se. A meta é calculada por uma regra do projeto e não foi validada para cada indivíduo. Além disso, a anotação de sintomas não permite diagnosticar suas causas.

Em seguida, a proposta do aplicativo e suas funcionalidades são apresentadas. Depois, o processo de desenvolvimento, a divisão do código e funcionamento das telas. Por fim, como executar o projeto, os resultados dos testes e o que ainda deve ser avaliado.

O trabalho foi escrito em papel A4, em fonte Times New Roman 12, espaçamento 1,5.

## 2 PROPOSTA E OBJETIVOS

O objetivo proposto é criar um aplicativo para Android que reúna o perfil, cadastro de bebidas e acompanhamento de consumo. Como os dados são armazenados localmente no celular, as principais funcionalidades não necessitam de conta, nem de conexão com servidor do projeto. Além disso, permite explorar o armazenamento de dados em um aplicativo Android.

O objetivo geral é desenvolver e testar um aplicativo para Android que permita ao usuário organizar seu consumo de bebidas e visualizar uma meta estimada. Em particular, o aplicativo deve permitir:

- Configuração do perfil;

- Estimar meta;

- Cadastrar bebida;

- Consultar bebida;

- Editar bebida;

- Excluir bebida;

- Descartar exclusão;

- Favoritar;

- Consultar estatísticas;

- Anotar sintomas;

- Ler dicas;

- Configurar lembretes.

O aplicativo trabalha com oito tipos de bebida. Os dados incluem data e horário. Na tela inicial, o usuário pode navegar entre os tipos de bebida do dia. É possível filtrar registros no histórico. O gráfico não é influenciado por filtros e mostra o último sete dias.

O aplicativo é verde e azul, com detalhes em verde-água. As ações são facilmente entendidas por meio de cartões, títulos e botões. Esse é o estilo visual proposto para o projeto. No entanto, não foi realizado estudo comparativo com outros estilos em pesquisa ou avaliação com usuários.

Nesta versão, não há login, sincronização de celulares, integração com relógios, exportação de dados. Além disso, não define quanto de líquido cada indivíduo deveria consumir clinicamente.

## 3 PROCESSO DE DESENVOLVIMENTO E AMBIENTE

Para descrever o processo de desenvolvimento, divide-se o trabalho em cinco etapas: definição de funcionalidades, modelagem de dados, criação das telas, implementação das ações e verificação de resultados. Com isso, é possível descrever melhor como uma necessidade de usuário converge em uma ação do aplicativo e em uma situação de teste.

As funcionalidades incluem o perfil, o registro de bebida, o histórico e as informações complementares. Separar os dados que devem ser armazenados dos dados que são manipulados para mostrar os dados foi uma preocupação. As Activities (classes que controlam as telas) recebem os cliques do usuário e convergem no cálculo de funcionalidades e armazenamento de dados.

No que se refere à linguagem, o aplicativo foi desenvolvido em Kotlin. Os layouts foram organizados em XML. Os elementos visuais foram implementados com Material Components. O projeto está configurado com compileSdk, targetSdk 37 e minSdk 24. Ou seja, o código é compilado para versão 37 do Android e requer no mínimo a versão 24. O Gradle do projeto utiliza Scripts em Kotlin DSL, JDK 17 e compatibilidade Java 11.

Isso não significa que o código do aplicativo foi escrito em Java. Os testes foram realizados com JUnit, AndroidJUnitRunner e Espresso.

Para facilitar o trabalho com o banco de dados, o SQLiteOpenHelper foi utilizado. No projeto, o BancoHelper implementa as consultas e converte os resultados em objetos utilizáveis pelo código. As operações de consulta são disponibilizadas para as telas por meio de BebidaRepository. As configurações simples, como dados do perfil, são armazenados em SharedPreferences. O SQLite é utilizado seguindo a documentação de armazenamento local do Android.

A verificação incluiu o Gradle Wrapper e o emulador Medium_Phone com Android 17. As imagens de tela utilizam dados de teste. Os resultados da geração do app, análise automática e testes estão descritos na Seção 10.

Não houve estudo com usuários, avaliação clínica, monitoramento prolongido da bateria, ou certificação de acessibilidade. Os resultados referem-se às situações testadas e ao ambiente registrado.

## 4 REQUISITOS DO SISTEMA

Perfil
Sexo utilizado na fórmula, peso, altura, idade, nível de atividade física. Alguns dados serão preenchidos opcionalmente. É necessário verificar os valores inseridos, permitir a edição e garantir que a meta será disponibilizada em todas as telas que a utilizam.

Cadastro
Permite o cadastro de água, suco, refrigerante, café, energético, cerveja, vinho e destilado. A quantidade deve ser um número inteiro, entre 1 e 5000 ml. A data e horário devem ser válidos e não futuros. As edições devem manter o código do registro e marcador de favorito.

Histórico
Permite filtar por período, tipo de bebida e favorito. Antes de excluir, deve pedir confirmação e dar oportunidade de desfazer a ação, recuperando as informações originais. As estatísticas devem mostrar os últimos sete dias, incluindo dias sem registros (zero). Devem comparar os valores com a meta atual.

Painel
Deve iniciar sempre na categoria Água. Deve possibilitar navegação entre categorias, deslizando o dedo ou tocando as setas. Somente deve exibir outros tipos se eles tiverem sido registrados no dia. Bebidas alcoólicas devem ser exibidas, mas não contabilizadas na meta. Deve acompanhar a mudança da data, celebrando a meta uma única vez por dia.

Lembretes
Permite escolher início, fim e intervalo no mesmo dia. Permitir ativar, testar e acessar permissões. As dicas e registros de sintomas devem ser claros, indicando que são para fins informativos e acompanhamento pessoal. Não deve ser capaz de realizar diagnósticos e não deve afirmar que beber água neutraliza o álcool.

O aplicativo deve verificar a persistência de dados, manter os registros se o banco for atualizado, recuperar informações importantes se a tela for recriada pelo sistema Android. O banco deve ser consultado em segundo plano. Textos, cores e valores utilizados em mais de uma tela deve ser organizado para facilitar futuras alterações.

## 5 ARQUITETURA E ORGANIZAÇÃO DO PROJETO

O código foi dividido em partes de acordo com a função que cada parte desempenha. As Activities lidam com as telas e o clique do usuário. Os modelos representam os registros. Classes auxiliares lidam com cálculos, aparência e armazenamento. Foram criadas onze Activities: tela de abertura, tela de início, tela de perfil, tela de cadastro, tela de detalhes, tela de histórico, tela de estatísticas, tela de sintomas, tela de dicas, tela de Sobre, tela de lembretes e tela de regras e fontes.

Os modelos Bebida e Sintoma representam as informações armazenadas. TipoBebeida é uma lista central de tipos de bebidas. Armazena o nome, ícone, código de opção de seleção e informações sobre álcool e cafeína. O cadastro, os filtros, os detalhes e o painel utilizam esta mesma lista. Com isso, não é necessário definir as categorias em várias partes do código.

BebidaRepository implementa as operações para consultar, salvar, editar e excluir registros. Ele utiliza BancoHelper para acessar o SQLite. DadosActivity conecta as telas ao processo. BancoAsync executa tarefas em uma fila de segundo plano. Quando o trabalho é completo, o resultado é retornado à parte do Android que atualiza a tela.

Calculos implementa diversas funções, incluindo o cálculo de meta. PerfilRepository consulta a meta utilizando o perfil armazenado. AjustesDoApp armazena valores utilizados nas fórmulas, atalhos, limite de cadastro e tempos de interação. O texto do botão e a quantidade que ele registra utilizam a mesma configuração.

Outras classes realizam tarefas mais específicas. ConsumoDoDia soma a quantidade de bebidas. SwipeConsumoLayout detecta o gesto de deslizar. HydrationProgressView desenha o progresso. Lembretes e LembreteReceiver lidam com os avisos. DicasEngine constrói as mensagens educativas. Visual, PageComponents, PaginaSimples e WaterIllustrationView incluem partes do visual utilizadas nas telas.

A divisão do código facilita a organização do projeto. As Activities ainda lidam com parte das ações e construção de visual. O app não utiliza MVVM completo, em que a lógica de tela é separada em outras classes. Não utiliza ViewModel, Room ou DataStore. A principal divisão é nas regras compartilhadas e no acesso ao banco.

## 6 MODELO DE DADOS E PERSISTÊNCIA

O registro de bebida armazena: id, tipo, ml, data, hora, observacao, favorito. O id é um número gerado pelo banco que identifica o registro. A quantidade é um número inteiro. O favorito é 0 ou 1 (não ou sim). A data e hora seguem o formato dd/MM/yyyy e HH:mm, respectivamente. O código salvo para o tipo de bebida é independente do nome que é exibido na tela. Dessa forma, não é necessário renomear os registros quando o texto exibido é modificado.

A tabela de sintomas armazena: id, data, sintomas, nivel_sede, observacao. Os sintomas selecionados são concatenados em um texto. As tabelas de bebidas e sintomas não estão diretamente conectadas por um identificador. Quando houver necessidade de consultar informações do mesmo dia, a data é utilizada como filtro.

O banco de dados é chamado hidrata.db e está na versão 3. Quando instalado, o código verifica se uma versão anterior foi atualizada. Se a versão antiga for menor que 2, a tabela de sintomas é criada. Se a versão antiga for menor que 3, o horário é adicionado às bebidas. Assim, os registros antigos sem horário continuarão sem horário.

O histórico agrupa os registros por ano, mês, dia e horário. Em seguida, pelo id. A data foi armazenada como texto. Portanto, para que o ordenamento seja correto, a data precisa ser separada em partes para que possa ser ordenada. O id permite que o registro correto seja encontrado, mesmo que a ordem na lista tenha mudado.

SharedPreferences armazena o perfil, configurações de lembrete e data da última celebração da meta. Ele armazena as informações no celular e segue as regras de backup do Android.

O banco retorna o id do registro criado quando uma bebida é salva. Quando uma bebida é editada ou excluída, retorna a quantidade de registros afetados. O app verifica se essas respostas estão corretas antes de exibir que o trabalho foi concluído. Para descartar uma exclusão, uma cópia temporária da bebida é mantida. Em seguida, ela é tentada a ser salva com o mesmo id. Se esse número já estiver sendo utilizado, não deve substituir outro registro. Os testes verificam essas operações e a atualização do banco sem perder dados.

## 7 IMPLEMENTAÇÃO DOS FLUXOS

### 7.1 Modelos catálogo e cálculo da meta

FIGURA 1 – CATÁLOGO DE BEBIDAS

Fonte: Código-fonte do Hidratapp, TipoBebida.kt, Linhas 8 a 16.

TipoBebida é um enum. Ou seja, é composto de opções definidas no código. Cada item armazena valorBanco, que é o identificador no banco, e nomeRes, que é o texto em strings.xml. O catálogo inclui ícone, identificação do botão, classificações. A consulta reconhece os tipos conhecidos. Textos fora do catálogo permanecerão legíveis. No modelo Bebida, os campos são imutáveis (val), e a função copy permite criar uma versão com alterações, mantendo as demais informações.

FIGURA 2 – META DIÁRIA COMPARTILHADA

Fonte: Código-fonte do Hidratapp, PerfilRepository.kt, Linhas 5 a 12.

A conta base é 35ml/kg de peso. Se for menor de 18 anos, 40ml/kg. Acima de 55 anos, 30ml/kg. A atividade física acrescenta 0ml/kg, 5ml/kg, 10ml/kg, 15ml/kg. A opção masculina multiplica o resultado por 1,05. Esses valores são armazenados em AjustesDoApp. O cálculo é realizado em Calculos. Para um indivíduo de 70kg, 30 anos, opção feminina e atividade moderada, a meta é 3150ml. Com opção masculina, o resultado é 3307ml. A conversão para número inteiro descarta a parte decimal. A meta estimada é apenas uma estimativa do projeto. Não é recomendada como prescrição de saúde.

### 7.2 Validação gravação e manutenção do esquema

FIGURA 3 – MIGRAÇÃO DE ESQUEMA

Fonte: Código-fonte do Hidratapp, BancoHelper.kt, Linhas 36 a 45.

Quando o banco é atualizado, a versão antiga é verificada para que o que precisa ser migrado seja identificado. O comando ALTER TABLE adiciona o novo campo de horário sem que as bebidas antigas sejam apagadas. Os testes utilizam versões 1 e 2 do banco. Verificam se os dados permanecem disponíveis após a atualização. Testes de cadastro, consulta, edição e exclusão também são realizados.

FIGURA 4 – VALIDAÇÃO VOLUME CADASTRADO

Fonte: Código-fonte do Hidratapp, CadastroBebidaActivity.kt, Linhas 146 a 149.

O cadastro identifica qual opção de bebida foi selecionada. Salva o valorBanco. Os atalhos adicionam a quantidade ao volume até o limite de AjustesDoApp. A data e horário também são validados antes de salvar. Valores inválidos ou no futuro são rejeitados.

Após as validações, a tela cria o objeto Bebida. Solicita ao repositório para que ele grave o registro. A função dados é utilizada. Na edição, a função copy mantém o id e o favorito. Altera os campos escolhidos. Durante a gravação, o botão é desativado para que o usuário não clique novamente. O app só confirma o sucesso após a resposta do banco. Caso o Android recrie a tela, os campos e seleção são recuperados.

### 7.3 Acompanhamento diário e lembretes

FIGURA 5 – AGRUPAMENTO POR CATEGORIA

Fonte: Código-fonte do Hidratapp, ConsumoDoDia.kt, Linhas 9 a 16.

ConsumoDoDia soma os registros do dia. Separa o consumo por tipo de bebida. A categoria Água é sempre a primeira. Quando existir outra categoria, o painel exibe uma página com o total de consumo para a meta (sem álcool). Deslizar para os lados ou tocar nas setas apenas altera o que é exibido na tela. Não altera os registros. A rolagem vertical continua disponível.

FIGURA 6 – AGENDAR PRÓXIMO AVISO LOCAL

Fonte: Código-fonte do Hidratapp, Lembretes.kt, Linhas 22 a 29.

Lembretes calcula o próximo aviso. Utiliza o serviço de alarmes do Android, AlarmManager, para agendar o aviso. Quando o momento chega, LembreteReceiver exibe a notificação (se houver permissão). Agenda o próximo aviso. O código também trata a reinicialização do celular, atualizações do app e mudanças de data ou fuso. Os horários são aproximados e dependem do Android. As notificações precisam estar autorizadas e com o canal de avisos habilitado.

### 7.4 Execução assíncrona e continuidade do uso

FIGURA 7 – EXECUTAR TRABALHO E ENTREGAR PARA INTERFACE

Fonte: Código-fonte do Hidratapp, DadosActivity.kt, Linhas 17 a 26.

BancoAsync faz as operações do banco em segundo plano para deixar a execução da tela disponível. runCatching guarda se a operação deu certo ou falhou, e Handler entrega a resposta à parte que atualiza a interface. Antes de mostrar o resultado, DadosActivity verifica se a tela ainda está aberta. Esse caminho é usado no cadastro, nos detalhes, no histórico, nas dicas, nos sintomas e nos atalhos.

RotinaDiaria calcula quando será a próxima meia-noite no horário local. Nesse momento, MainActivity atualiza a data e busca os registros do novo dia. A atualização também acontece quando a tela volta a aparecer e quando o Android informa uma mudança de horário. Quando a tela deixa de ficar visível, seu agendamento é cancelado.

Quando o total que conta para a meta atinge o valor esperado, o painel mostra uma mensagem e uma pequena animação. O app salva a data dessa comemoração para não repeti-la no mesmo dia. Se as animações estiverem desligadas no sistema, essa escolha é respeitada. A mensagem também não remove a opção de desfazer um registro rápido.

Ao excluir uma bebida, DetalheBebidaActivity guarda uma cópia do registro por dez segundos. Nesse tempo, os dados continuam visíveis e o usuário pode tocar em Desfazer. A cópia e o prazo são recuperados se o Android recriar a tela. Ao restaurar, o repositório impede que um registro já existente com o mesmo id seja substituído.

## 8 INTERFACE E JORNADA DE USO

FIGURA 8 – ABRIR E MOSTRAR AO USUÁRIO PELO PRIMEIRO VEZ



Fonte: execução do Hidratapp em emulador Android 17, com dados de teste (2026).

O app começa com uma tela ilustrada e depois apresenta a configuração do perfil. As cores azuis, a água e os fundos claros fazem parte da identidade visual. Se a tela de abertura for encerrada, o código cancela a passagem automática para a próxima tela, evitando abrir outra página depois que o usuário saiu.

### 8.1 Configuração do perfil

FIGURA 9 – MEDIDAS E ATIVIDADE FÍSICA



Fonte: execução do Hidratapp em emulador Android 17, com dados de teste (2026).

O perfil é preenchido em quatro etapas, com a opção de sexo usada na fórmula, as medidas, a atividade física e os cuidados opcionais. No resumo, o usuário confere a meta e segue para o acompanhamento do dia. Se a tela for recriada pelo Android, o app recupera a etapa e os campos. O perfil também pode ser editado pelo menu inferior.

### 8.2 Registro e acompanhamento

FIGURA 10 – CADASTRO DE BEBIDA E CONSULTA DE CONSUMO POR CATEGORIA



Fonte: execução do Hidratapp em emulador Android 17, com dados de teste (2026).

Na tela de cadastro, a quantidade fica em destaque, as bebidas aparecem agrupadas e o botão de salvar fica na parte inferior. As opções de seleção usam um mesmo layout XML e mudam de aparência quando escolhidas. Os nomes e as mensagens ficam em strings.xml, e as cores também têm arquivos próprios. No painel, alternar entre os tipos ajuda a entender de qual bebida vem cada quantidade.

### 8.3 Histórico estatísticas e lembretes

FIGURA 11 – CONSULTA SEMANAL E CONFIGURAÇÃO DE AVISOS



Fonte: execução do Hidratapp em emulador Android 17, com dados de teste (2026).

No histórico, o usuário pode combinar filtros para encontrar os registros. O gráfico continua mostrando o resumo da semana. Na tela de lembretes, ficam as opções de ativar, definir horários e intervalo, testar o aviso e conferir permissões. Cabeçalhos e cartões seguem o mesmo estilo entre as telas. Nas dicas, é possível abrir o texto completo e depois recolhê-lo.

### 8.4 Detalhes e recuperação de registros

FIGURA 12 – DETALHE DE BEBIDA E EXCLUSÃO COM DESCARTAR



Fonte: execução do Hidratapp em emulador Android 17, com dados de teste (2026).

Nos detalhes de uma bebida, é possível consultar o horário e a observação, marcar como favorita e editar. Ao confirmar a exclusão, o usuário tem dez segundos para desfazer. O cartão continua visível nesse período, mesmo se a tela for recriada. Ao desfazer, o app recupera as informações do registro.

## 9 DECISÕES TÉCNICAS E LIMITAÇÕES

Escolhemos usar componentes do Android e salvar os dados no celular para aplicar os conteúdos da disciplina. Com SQLiteOpenHelper, o projeto mostra diretamente como criar tabelas, fazer consultas e atualizar o banco. Por outro lado, tivemos que cuidar manualmente dessas mudanças no código. O repositório e a fila ajudam a separar o acesso aos dados da atualização das telas.

As operações do SQLite são feitas em segundo plano, mas isso não significa que o desempenho foi medido. Ainda não foram realizados testes com muitos registros nem medições organizadas de memória, tempo de resposta ou bateria. A fila executa uma tarefa por vez, e as Activities ainda coordenam parte das ações.

Guardar datas e horários como texto exige cuidado na leitura e na ordenação. O app também não guarda separadamente o momento do cadastro e o momento do consumo. No gráfico, os sete dias são comparados com a meta atual, sem guardar metas antigas. Já a sequência de bebidas alcoólicas não considera dose, teor alcoólico ou intervalo. Mesmo ao cadastrar uma bebida de uma data passada, o aviso de inclusão consulta o dia atual.

Para testar a mudança de dia, foi usado um relógio controlado pelo teste e acionada a função que o agendamento utiliza. Também há testes para a próxima meia-noite na mudança de ano e no horário de verão. Essas verificações não substituem deixar o app funcionando durante várias viradas reais em aparelhos diferentes.

Os testes no Android 17 ainda precisam ser complementados com aparelhos físicos e celulares de outros fabricantes. Também falta avaliar o uso com leitor de tela e fonte ampliada. A recuperação do backup do Android e possíveis atrasos longos nas notificações precisam de mais testes. O app não permite exportar ou importar um arquivo de backup.

A fórmula da meta e as mensagens não foram validadas para as necessidades de cada pessoa. Pela regra do app, todo o volume de bebidas sem álcool entra no total, mas isso não significa que elas tenham o mesmo valor nutricional. As referências ajudam a explicar o tema da hidratação, sem comprovar os valores escolhidos para a fórmula.

## 10 VERIFICAÇÃO E RESULTADOS

Na nossa verificação, procuramos conferir as funções em situações que podem ser repetidas. O comando assembleDebug gerou a versão de teste do aplicativo com a mensagem BUILD SUCCESSFUL. O lintDebug, ferramenta que procura problemas no código, não encontrou erros nem avisos de HardcodedText nos layouts, que indicariam textos escritos diretamente nesses arquivos. Ainda existem outros avisos, então o resultado não significa que todos os possíveis problemas foram eliminados.

Na execução registrada, passaram 21 testes unitários, voltados a funções isoladas, e 18 testes instrumentados, que rodam no ambiente Android. Os instrumentados foram executados no emulador Medium_Phone com Android 17. O quadro abaixo mostra quantos testes há em cada grupo.

Os testes unitários conferem os cálculos, os tipos de bebida, as somas do dia e as regras de data. Os instrumentados verificam a atualização do banco, o cadastro, a consulta, a edição e a exclusão, além do perfil, dos filtros, dos gestos e da recuperação das telas. Também conferem o desfazer e se o banco trabalha em segundo plano. O teste de mudança de dia verifica a nova consulta e o controle da comemoração da meta.

NotificacaoTest confere se o Android publica o aviso. Ele não comprova que a notificação chega pontualmente depois de um período longo com o celular em repouso. Também não foram medidos o percentual de código coberto pelos testes, o desempenho ou a eficácia clínica. Os dados criados pelos testes são removidos ao final de cada situação.

## 11 PROCEDIMENTOS DE EXECUÇÃO E USO

Para abrir o projeto, selecione no Android Studio a pasta que contém settings.gradle.kts. Aguarde a sincronização do Gradle e tenha JDK 17 e SDK 37 disponíveis. Para rodar, use um emulador ou celular com API 24 ou superior. O computador pode precisar de internet para baixar as dependências, que são as bibliotecas usadas pelo projeto.

No Windows, execute .\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain. O APK fica em app/build/outputs/apk/debug/app-debug.apk. Os relatórios unitários e estáticos ficam nos diretórios app/build/reports/tests e app/build/reports.

Com um emulador de teste conectado, execute .\gradlew.bat :app:connectedDebugAndroidTest para rodar os testes no Android. Na execução documentada, o app e o pacote de testes foram instalados e os testes foram iniciados pelo comando am instrument, usando AndroidJUnitRunner. O registro dessa execução está em app/build/qa/guia_ajustes_instrumentados.txt.

No primeiro acesso, toque em Vamos começar e preencha as quatro etapas. Informe as medidas, escolha a atividade física e confira o resumo. Para conferir o exemplo de meta de 3150 ml, use 70 kg, 30 anos, opção feminina e atividade moderada.

Na tela principal, os atalhos de 200, 300 e 500 ml registram água e permitem desfazer por seis segundos. O botão principal abre o cadastro completo, em que você escolhe a bebida e informa quantidade, data e horário. A observação é opcional. Os volumes dos atalhos e o limite do cadastro ficam definidos em AjustesDoApp.

### 11.1 Consulta edição e informações complementares

Deslize sobre o círculo ou toque nas setas para consultar o consumo por tipo de bebida. Água aparece primeiro, e as outras categorias dependem do que foi registrado no dia. O total da meta não inclui álcool. Trocar a página só muda a visualização. A data é atualizada na virada do dia e ao voltar à tela, e a comemoração usa a soma válida e a meta atual.

Em Evolução, consulte o gráfico da semana e use os filtros do histórico. Toque em um registro para ver detalhes, favoritar, editar ou excluir. Depois de confirmar uma exclusão, a opção Desfazer fica disponível por dez segundos. Ao recuperar a bebida, o app mantém o id, horário, favorito e observação.

Em Lembretes, escolha início, fim e intervalo e salve. Permita as notificações quando o Android solicitar e use o botão de teste para conferir se o aviso aparece. Os horários são aproximados. Se você forçar a parada do app nas configurações do celular, os lembretes ficam suspensos até ele ser aberto novamente.

Na tela de sintomas, marque o que percebeu e informe o nível de sede. Se quiser, acrescente uma observação antes de salvar. Os relatos podem ser excluídos pelo histórico. Nas dicas, Ler dica abre o texto completo e Recolher volta à versão resumida. Essas mensagens não descobrem a causa dos sintomas.

A área de regras e fontes explica os valores da fórmula e os limites do app. O uso não exige conta, e os dados não são sincronizados pelo projeto. Uma possível recuperação pelo backup do Android depende das configurações do celular e ainda não foi verificada nos testes apresentados.

## 12 AVALIAÇÃO TÉCNICA

A avaliação observa se as funções previstas aparecem no código e funcionam nas situações testadas. No registro de uma bebida, por exemplo, o usuário escolhe o tipo, o app confere os campos, monta o objeto e salva no banco para consultar depois. Os testes de edição e de desfazer verificam se os dados são mantidos. Já os testes de recriação conferem se a tela recupera as informações necessárias para continuar o uso.

A lista central TipoBebida permite usar os mesmos tipos no cadastro, nos filtros, no histórico e nas regras. Os nomes exibidos ficam separados dos valores salvos no banco. Os ajustes compartilhados ajudam a manter os botões, mensagens e cálculos com os mesmos valores. Os arquivos de aparência também deixam as mudanças visuais mais organizadas.

O repositório e a fila definem um caminho comum para acessar o SQLite. O teste confirma que a operação acontece em segundo plano na situação verificada, mas não mede quanto isso melhora o desempenho. O acompanhamento diário reúne a consulta por data, o agendamento e a data da comemoração. Para conferir essas regras, os testes usam um relógio controlado.

A geração do app e os 39 testes aprovados mostram que as situações descritas funcionaram no ambiente registrado. As imagens e os trechos de código ajudam a ligar cada função às classes responsáveis. A análise automática complementa essa conferência, mas ainda são necessárias avaliações de segurança, acessibilidade e uso por mais tempo.

Nossa avaliação está ligada ao desenvolvimento acadêmico e ao funcionamento do aplicativo. Como próximos passos, é necessário testar aparelhos físicos, recursos de acessibilidade, bancos com mais registros, recuperação de backup e proteção dos dados. Esses pontos ainda precisam ser avaliados e não fazem parte dos resultados já demonstrados.

## 13 CONSIDERAÇÕES FINAIS

Com o Hidratapp, conseguimos reunir no nosso projeto os principais conteúdos usados no desenvolvimento de um aplicativo Android. O app permite configurar o perfil, registrar bebidas, acompanhar o consumo e consultar o histórico. Também inclui sintomas, dicas e lembretes. Para isso, o projeto combina telas XML, regras em Kotlin e dados salvos no próprio celular.

Ao dividir o código, tivemos o cuidado de definir o papel de cada parte: TipoBebida organiza as categorias, Calculos faz as contas, as Activities cuidam da interação e o repositório encaminha as operações ao banco. As tarefas em segundo plano e a recuperação dos campos das telas ajudam o usuário a continuar o cadastro, a consulta, a edição e o desfazer sem perder informações importantes.

Na verificação registrada, o aplicativo foi gerado, a análise automática não encontrou erros e os testes unitários e instrumentados passaram. Com esses resultados, a gente consegue mostrar o funcionamento das situações avaliadas, dentro dos limites do ambiente de teste. A meta continua sendo uma estimativa, sem validação individual de saúde. O projeto pode ser ampliado com avaliações de uso, acessibilidade, desempenho e proteção dos dados.

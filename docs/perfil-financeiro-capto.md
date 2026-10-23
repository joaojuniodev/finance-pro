# Perfil Financeiro do Usuário — Especificação Conceitual do Capto

## 1. Objetivo e status metodológico

O questionário financeiro do Capto é um instrumento próprio para produzir um retrato inicial da vida financeira do usuário e personalizar a experiência do aplicativo.

Ele foi elaborado com base em conceitos presentes em referências reconhecidas de saúde, bem-estar e comportamento financeiro:

- **Índice de Saúde Financeira do Brasileiro (I-SFB):** desenvolvido pela FEBRABAN em cooperação técnica com o Banco Central do Brasil;
- **OECD/INFE Toolkit:** publicado pela Organização para a Cooperação e Desenvolvimento Econômico e sua rede de educação financeira;
- **Financial Well-Being Scale:** desenvolvida pelo Consumer Financial Protection Bureau (CFPB);
- **Financial Management Behavior Scale (FMBS):** desenvolvida por Jeffrey Dew e Jing Jian Xiao.

As 17 perguntas, a pontuação e a classificação de perfis são próprias do Capto. O instrumento não reproduz integralmente nenhuma dessas escalas e, até passar por validação estatística própria, não deve ser apresentado como questionário cientificamente validado.

O resultado do Capto não é o I-SFB e não deve receber esse nome. Se o produto futuramente utilizar o I-SFB oficial, deverá aplicar suas perguntas e metodologia sem alterações e apresentar o resultado separadamente.

Referências:

- [I-SFB/FEBRABAN](https://indice.febraban.org.br/calcule-)
- [FAQ oficial do I-SFB](https://indice.febraban.org.br/faq)
- [Estudo do Banco Central](https://www.bcb.gov.br/content/cidadaniafinanceira/documentos_cidadania/serie_cidadania/serie_cidadania_financeira_8_endividamento_risco_2ed.pdf)
- [OECD/INFE Toolkit](https://www.oecd.org/en/publications/oecd-infe-toolkit-for-measuring-financial-literacy-and-financial-inclusion-2022_cbc4114f-en.html)
- [CFPB Financial Well-Being Scale](https://www.consumerfinance.gov/data-research/research-reports/financial-well-being-scale/)
- [Financial Management Behavior Scale](https://digitalcommons.uri.edu/hdf_facpubs/2/)

## 2. Índice geral e perfil financeiro

O Capto produz dois resultados complementares.

### Índice Financeiro Geral do Capto

É uma nota de 0 a 100 obtida pela média das seis dimensões. Resume o momento financeiro, mas não explica sozinho como o resultado foi formado.

### Perfil financeiro do usuário

É definido pelo padrão formado pelas seis dimensões, por respostas críticas e pela relação entre pontos fortes e vulnerabilidades.

Duas pessoas podem possuir o mesmo índice geral e perfis diferentes. Uma pode ter boa organização e pouca resiliência; outra pode ter boa reserva e dificuldade para regular decisões impulsivas.

Uma dimensão isolada não define a pessoa. Uma nota baixa em organização representa uma oportunidade atual de organização financeira, e não autoriza classificar a pessoa simplesmente como “desorganizada”.

```text
Respostas
   ↓
Pontuações das seis dimensões (0 a 100)
   ↓
├── Média das dimensões → Índice Financeiro Geral do Capto
└── Combinação das dimensões + regras críticas → Perfil financeiro
```

## 3. As seis dimensões

### 3.1 Segurança financeira

Avalia a capacidade atual de sustentar compromissos cotidianos:

- relação entre renda e gastos;
- sobra ou insuficiência mensal;
- pontualidade no pagamento de contas;
- dependência de crédito para concluir o mês;
- compromissos não pagos.

Uma nota baixa pode decorrer de renda insuficiente, desemprego, dependentes ou despesas extraordinárias. Ela não representa falta de caráter ou responsabilidade.

### 3.2 Resiliência financeira

Avalia a capacidade de enfrentar imprevistos e interrupções de renda:

- capacidade de absorver uma despesa inesperada;
- existência de reserva;
- dependência de empréstimos ou parcelamentos;
- tempo de manutenção das despesas essenciais sem renda;
- capacidade de reorganizar o orçamento.

### 3.3 Organização e controle

Avalia a visibilidade e o domínio sobre a vida financeira:

- acompanhamento do orçamento;
- conhecimento das contas a vencer;
- planejamento e cumprimento de metas;
- evitação de saldo, fatura ou contas;
- percepção de perda de controle.

### 3.4 Regulação de impulsos

Avalia quanto decisões imediatas, emocionais ou sociais interferem no comportamento:

- compras não planejadas;
- arrependimento após compras;
- influência de ansiedade, estresse ou tédio;
- influência ou pressão social;
- reflexão antes da compra;
- impacto do impulso sobre o planejamento.

### 3.5 Orientação para o futuro

Avalia a capacidade de incorporar objetivos e consequências futuras às decisões atuais:

- preparação para objetivos futuros;
- capacidade de adiar recompensas quando necessário;
- uso consciente de receitas inesperadas;
- disposição para poupar;
- consistência entre intenção e comportamento.

Valorizar o presente não é uma falha. O problema surge quando o presente compromete obrigações ou objetivos importantes para o próprio usuário.

### 3.6 Equilíbrio e liberdade

Avalia se a relação com o dinheiro permite segurança e qualidade de vida:

- capacidade de aproveitar o presente sem prejudicar obrigações;
- excesso de restrição financeira;
- sensação de liberdade;
- sacrifício de necessidades essenciais;
- equilíbrio entre lazer, segurança e objetivos;
- autopercepção.

Poupar ou gastar menos nem sempre significa maior equilíbrio. Uma restrição excessiva também pode reduzir liberdade e bem-estar.

## 4. Questionário inicial de 17 perguntas

As perguntas devem aparecer uma por vez ou em blocos curtos. O usuário deve poder avançar, voltar e corrigir respostas.

Quando aplicável, devem existir as opções “Não sei dizer”, “Não se aplica” ou “Prefiro não responder”. Elas não recebem zero; são respostas ausentes.

### Bloco 1 — Como estão as coisas por aí?

#### 1. Pensando nos últimos três meses, como ficaram seus gastos em relação ao dinheiro que entrou?

- Gastaram bem menos do que entrou
- Gastaram um pouco menos
- Ficaram praticamente iguais
- Gastaram um pouco mais
- Gastaram bem mais
- Não sei dizer

Dimensão: Segurança financeira.

#### 2. Nesse mesmo período, como foi o pagamento das suas contas?

- Todas foram pagas antes ou no prazo
- Quase todas foram pagas no prazo
- Algumas atrasaram
- Várias atrasaram
- Precisei deixar alguma conta sem pagar

Dimensão: Segurança financeira.

#### 3. Depois dos gastos essenciais e compromissos do mês, normalmente…

- Sobra uma quantia confortável
- Sobra um pouco
- Fico praticamente no zero
- Falta um pouco
- Preciso recorrer a crédito, empréstimo ou atraso de contas

Dimensão: Segurança financeira.

#### 4. Se aparecesse hoje uma despesa inesperada equivalente a cerca de 20% da sua renda mensal, o que provavelmente aconteceria?

- Eu pagaria usando uma reserva
- Eu conseguiria pagar reorganizando o mês
- Eu parcelaria, mas conseguiria manter as outras contas
- Eu precisaria adiar alguma conta ou pedir dinheiro emprestado
- Eu não saberia o que fazer

Dimensão: Resiliência financeira.

#### 5. Se sua principal fonte de renda parasse hoje, por quanto tempo conseguiria manter os gastos essenciais?

- Menos de uma semana
- Algumas semanas
- De um a três meses
- De três a seis meses
- Mais de seis meses
- Não sei dizer

Dimensão: Resiliência financeira.

### Bloco 2 — Como você cuida do dinheiro?

As perguntas 6 a 9 usam: Nunca · Raramente · Às vezes · Frequentemente · Quase sempre.

#### 6. Eu acompanho quanto ainda posso gastar antes do fim do mês.

Dimensão: Organização e controle.

#### 7. Eu sei quais contas e compromissos ainda estão por vencer.

Dimensão: Organização e controle.

#### 8. Quando defino uma meta financeira, separo algum valor para ela.

Dimensão: Organização e controle.

#### 9. Evito olhar o saldo, a fatura ou as contas quando acho que não vou gostar do que verei.

Dimensão: Organização e controle. Pontuação invertida.

### Bloco 3 — Quando aparece algo que você quer

#### 10. Antes de uma compra não essencial de valor relevante para você, o que mais costuma acontecer?

- Espero um pouco e penso se ainda quero comprar
- Comparo opções e verifico se cabe no orçamento
- Compro se parecer que consigo pagar
- Compro e reorganizo as contas depois
- Geralmente compro sem verificar o impacto

Dimensão: Regulação de impulsos.

#### 11. Com que frequência você faz uma compra não planejada e depois se arrepende?

- Nunca
- Raramente
- Às vezes
- Frequentemente
- Quase sempre

Dimensão: Regulação de impulsos. Pontuação invertida.

#### 12. Estresse, ansiedade, tédio ou pressão de outras pessoas fazem você gastar mais do que pretendia?

- Nunca
- Raramente
- Às vezes
- Frequentemente
- Quase sempre

Dimensão: Regulação de impulsos. Pontuação invertida.

#### 13. Com que frequência você mantém um gasto de lazer mesmo sabendo que existe uma conta importante pendente?

- Nunca
- Raramente
- Às vezes
- Frequentemente
- Quase sempre
- Não se aplica

Dimensão: Equilíbrio e liberdade. Pontuação invertida e possível sinal crítico.

### Bloco 4 — Presente ou futuro?

#### 14. Quando entra um dinheiro que você não estava esperando, qual opção mais se parece com você?

- Guardo ou uso para reduzir dívidas
- Divido entre objetivos futuros e algo que quero aproveitar agora
- Planejo alguma compra ou experiência
- Costumo gastar livremente enquanto o dinheiro está disponível
- Depende muito da situação

Dimensão: Orientação para o futuro. “Depende muito” deve gerar uma pergunta complementar futura ou ser tratado como ausente.

#### 15. Qual frase mais combina com você atualmente?

- Prefiro abrir mão de algumas coisas agora para ter mais segurança no futuro
- Tento equilibrar o que quero hoje com meus planos futuros
- Prefiro aproveitar o presente, desde que minhas contas estejam cobertas
- Costumo priorizar o presente e resolver o restante depois

Dimensão: Orientação para o futuro. A resposta equilibrada recebe a maior pontuação; valorizar o presente com as contas cobertas não é uma falha.

### Bloco 5 — Dinheiro também precisa permitir viver

#### 16. Com que frequência você deixa de comprar algo essencial para manter gastos com lazer, compras ou experiências?

- Nunca
- Raramente
- Às vezes
- Frequentemente
- Quase sempre

Dimensão: Equilíbrio e liberdade. Pontuação invertida e possível sinal crítico.

#### 17. Qual destas situações mais representa sua vida hoje?

- Cuido tanto do futuro que às vezes tenho dificuldade de aproveitar o presente
- Consigo aproveitar o presente sem comprometer meus planos
- Às vezes exagero, mas normalmente consigo me reorganizar
- Frequentemente gasto mais do que pretendia
- Minhas decisões de gasto estão prejudicando compromissos importantes

Dimensão: Equilíbrio e liberdade e autopercepção.

## 5. Perguntas contextuais não pontuadas

Estas respostas ajudam a interpretar o resultado, mas não alteram diretamente o índice:

- A renda costuma ser estável, variar um pouco ou variar bastante?
- O usuário cuida apenas das próprias despesas ou sustenta outras pessoas?
- As decisões financeiras da casa são individuais, compartilhadas ou tomadas principalmente por outra pessoa?
- O usuário usa cartão de crédito e, se usa, paga integralmente, parcela de forma planejada, entra no rotativo ou atrasa?

Renda, idade, gênero, estado civil e quantidade de dependentes não determinam o perfil.

## 6. Pontuação

### 6.1 Escala padrão

Perguntas com cinco respostas ordenáveis usam pontos de 0 a 4:

| Interpretação | Pontos |
|---|---:|
| Comportamento mais protetivo ou equilibrado | 4 |
| Predominantemente protetivo | 3 |
| Intermediário | 2 |
| Sinal de vulnerabilidade | 1 |
| Vulnerabilidade mais intensa | 0 |

Perguntas negativas usam pontuação invertida:

| Frequência | Pontos |
|---|---:|
| Nunca | 4 |
| Raramente | 3 |
| Às vezes | 2 |
| Frequentemente | 1 |
| Quase sempre | 0 |

Respostas ausentes não valem zero e são excluídas do denominador. Se ultrapassarem 25% do questionário ou deixarem uma dimensão sem itens suficientes, o resultado não deve ser conclusivo.

### 6.2 Respostas não lineares

Nem todas as opções representam uma sequência simples:

- Pergunta 10: “esperar e refletir” e “comparar e verificar o orçamento” podem receber 4 pontos.
- Pergunta 15: “equilibrar presente e futuro” recebe 4; “priorizar a segurança futura” e “aproveitar com contas cobertas” podem receber 3; “resolver o restante depois” recebe 0.
- Pergunta 17: “equilíbrio” recebe 4; “às vezes exagero e me reorganizo” recebe 3; “restrição excessiva” recebe 2; “gasto frequentemente além do planejado” recebe 1; “prejudico compromissos” recebe 0.

Esses valores são hipóteses iniciais e devem ser revisados após validação.

### 6.3 Cálculo por dimensão

```text
Pontuação da dimensão =
    pontos obtidos nos itens respondidos
    ÷ pontos máximos possíveis desses itens
    × 100
```

Exemplo:

```text
Pontos obtidos = 3 + 2 + 4 = 9
Pontos possíveis = 3 × 4 = 12
Pontuação = 9 ÷ 12 × 100 = 75
```

Mapeamento:

| Dimensão | Perguntas |
|---|---|
| Segurança financeira | 1, 2 e 3 |
| Resiliência financeira | 4 e 5 |
| Organização e controle | 6, 7, 8 e 9 |
| Regulação de impulsos | 10, 11 e 12 |
| Orientação para o futuro | 14 e 15 |
| Equilíbrio e liberdade | 13, 16 e 17 |

Uma resposta pode servir como sinal secundário para outra dimensão, mas a pontuação inicial deve evitar contá-la duas vezes sem justificativa validada.

### 6.4 Índice Financeiro Geral do Capto

Enquanto não houver pesos validados, as dimensões têm o mesmo peso:

```text
Índice Financeiro Geral do Capto =
    (Segurança
    + Resiliência
    + Organização e controle
    + Regulação de impulsos
    + Orientação para o futuro
    + Equilíbrio e liberdade)
    ÷ 6
```

Não atribuir pesos diferentes apenas por intuição. Perguntas críticas ativam regras de interpretação em vez de simplesmente valerem mais pontos.

## 7. Faixas provisórias

| Pontuação | Interpretação |
|---:|---|
| 0–24 | Atenção prioritária |
| 25–44 | Baixa |
| 45–59 | Em desenvolvimento |
| 60–74 | Boa |
| 75–89 | Muito boa |
| 90–100 | Destaque |

As faixas descrevem uma dimensão atual, não a identidade da pessoa, e precisam ser recalibradas com dados reais.

## 8. Regras críticas

O índice geral não pode esconder vulnerabilidades severas. São sinalizadores:

- várias contas atrasadas ou alguma conta não paga;
- necessidade recorrente de crédito, empréstimo ou atraso para concluir o mês;
- necessidade de adiar contas ou pedir dinheiro diante de um imprevisto;
- lazer mantido frequentemente apesar de conta importante pendente;
- sacrifício frequente de necessidade essencial para manter lazer ou compras;
- evitação frequente de saldo, fatura ou contas.

Regras iniciais:

- resposta crítica de pagamento impede classificar o momento como “confortável”;
- impacto frequente sobre necessidades ou compromissos impede classificar o perfil como “equilibrado”;
- evitação frequente pode acrescentar “com dificuldade de acompanhamento”;
- baixa resiliência deve aparecer mesmo quando a média geral for alta.

As regras definitivas precisam ser calibradas e versionadas.

## 9. Construção do perfil

O perfil possui três componentes.

### 9.1 Tendência principal

Taxonomia inicial:

- **Guardião cauteloso:** controle e foco futuro elevados, com possível restrição excessiva;
- **Planejador equilibrado:** organização, controle de impulsos e equilíbrio elevados;
- **Flexível consciente:** valoriza o presente, mas normalmente preserva limites e compromissos;
- **Espontâneo:** decide bastante pelo momento e planeja com menos consistência, sem impacto crítico recorrente;
- **Planejador em construção:** demonstra intenção e visão de futuro, mas precisa transformá-las em organização recorrente;
- **Impulso no comando:** decisões impulsivas ou emocionais interferem com frequência em compromissos ou objetivos.

### 9.2 Momento financeiro

- Sob pressão;
- Em reorganização;
- Estável;
- Confortável;
- Resiliente.

### 9.3 Característica principal

O resultado destaca a dimensão mais forte, a principal oportunidade, respostas críticas e o nível de confiança.

Exemplo:

> **Planejador em construção — em reorganização**  
> Você pensa no futuro e demonstra bom controle sobre compras impulsivas, mas ainda encontra dificuldade para acompanhar seus compromissos e criar uma reserva para imprevistos.

### 9.4 Síntese interpretativa

A síntese interpretativa não cria uma nova pontuação ou classificação. Ela transforma os resultados já calculados em uma mensagem única e compreensível para o usuário.

O texto combina, nesta ordem:

1. o nível de confiança, que define quão afirmativa pode ser a mensagem;
2. a tendência financeira;
3. o momento financeiro atual;
4. a força principal;
5. o ponto de atenção principal, quando existir;
6. a oportunidade principal, quando existir.

Cada resultado seleciona um trecho previamente definido. Isso mantém a linguagem consistente, explicável e não determinista sem exigir uma resposta diferente para cada combinação possível.

Quando a confiança for inconclusiva, a síntese não deve afirmar uma tendência, momento, força ou vulnerabilidade. Deve apenas informar que ainda não existem dados suficientes e convidar o usuário a revisar ou completar as respostas.

## 10. Confiança e divergência do usuário

Registrar separadamente:

- perfil calculado pelo Capto;
- perfil escolhido ou confirmado pelo usuário;
- seis pontuações dimensionais;
- Índice Financeiro Geral do Capto;
- respostas críticas;
- respostas ausentes;
- nível de confiança;
- data da avaliação;
- versão do questionário e do algoritmo.

A confiança diminui com respostas ausentes, contradições relevantes ou poucos itens válidos em uma dimensão.

### 10.1 Cálculo provisório da confiança

A confiança começa em 100 pontos e recebe os seguintes descontos:

- 5 pontos por resposta ausente;
- 15 pontos por grupo de contradição identificado.

Faixas provisórias:

| Pontuação | Confiança |
|---:|---|
| 90–100 | Alta |
| 70–89 | Moderada |
| 0–69 | Baixa |

O resultado é inconclusivo quando mais de quatro das 17 respostas estão ausentes ou quando uma dimensão não possui nenhuma resposta válida. Nesse caso, a classificação inconclusiva prevalece sobre a faixa numérica e a pontuação fica limitada a 49.

As verificações iniciais de contradição são:

1. relação renda–gastos protetiva junto de falta recorrente de recursos, ou o inverso;
2. baixa capacidade declarada para uma despesa inesperada junto de reserva declarada para vários meses;
3. acompanhamento frequente de gastos ou compromissos junto de evitação frequente de saldo, fatura ou contas;
4. separação frequente de dinheiro para metas junto de priorização do presente e postergação do restante;
5. reflexão consistente antes de comprar junto de arrependimento frequente após compras não planejadas;
6. reflexão consistente antes de comprar junto de gasto emocional frequente;
7. uso protetivo de dinheiro inesperado junto de priorização declarada do presente, ou gasto livre desse dinheiro junto de restrição declarada em favor do futuro;
8. declaração de equilíbrio ou de presente com contas cobertas junto de lazer frequente com obrigações pendentes ou sacrifício frequente de necessidades essenciais;
9. autopercepção de restrição excessiva junto de declaração de equilíbrio ou de priorização do presente.

Diferenças que podem ser explicadas por contexto, renda, período analisado ou natureza da despesa não devem ser tratadas automaticamente como contradição. Essas regras também precisam ser calibradas e versionadas com dados reais.

O usuário pode revisar respostas ou escolher outro perfil. O perfil calculado e o escolhido devem ser preservados separadamente.

Texto recomendado:

> **Este retrato combina com você?**  
> O resultado foi construído a partir das suas respostas, mas sua percepção também é importante. Se ele não representar bem seu momento, você pode rever suas respostas ou escolher o perfil com que mais se identifica.

## 11. Linguagem e experiência

Sempre:

- tratar o resultado como retrato atual, não identidade permanente;
- destacar ponto forte e oportunidade de melhoria;
- explicar por que o sistema chegou ao resultado;
- permitir voltar e corrigir respostas;
- informar que o perfil pode mudar;
- usar linguagem acolhedora, direta e não moralista;
- considerar o contexto antes de emitir alertas.

Evitar:

- “gastão”, “irresponsável”, “mão de vaca” ou “desorganizado” como identidade;
- afirmar que renda baixa significa mau comportamento;
- considerar todo gasto de lazer negativo;
- presumir que poupar o máximo possível é sempre saudável;
- apresentar o questionário como diagnóstico clínico;
- chamar o índice do Capto de I-SFB;
- alterar silenciosamente o perfil após uma única transação.

Adequado: “Sua organização financeira está baixa neste momento.”

Inadequado: “Você é uma pessoa desorganizada.”

## 12. Uso em alertas e personalização

O perfil personaliza o tom e a sugestão, mas não determina sozinho se uma transação é arriscada.

Antes de alertar, considerar quando disponíveis:

- valor e proporção da compra;
- orçamento disponível;
- contas e faturas próximas;
- compromissos atrasados;
- recorrências pendentes;
- reserva financeira;
- padrão histórico do usuário;
- categoria e essencialidade declarada;
- perfil e momento financeiro.

A categoria “lazer” isoladamente não caracteriza irresponsabilidade.

## 13. O que o questionário não avalia

O questionário inicial não mede suficientemente:

- conhecimento técnico de juros, inflação ou investimentos;
- perfil de risco de investimento;
- capacidade matemática;
- compulsão por compras como diagnóstico clínico;
- caráter ou responsabilidade moral;
- adequação absoluta da renda;
- elegibilidade de crédito.

## 14. Validação futura

Para validar o instrumento, o Capto deverá:

1. realizar entrevistas cognitivas;
2. executar piloto com públicos diversos;
3. utilizar amostra de algumas centenas de usuários;
4. avaliar consistência interna de cada dimensão;
5. realizar teste e reteste;
6. comparar respostas com comportamentos observados, mediante consentimento;
7. verificar vieses por renda, idade, gênero, região, renda variável e dependentes;
8. recalibrar pesos, faixas e regras;
9. versionar alterações metodológicas;
10. respeitar privacidade, transparência e os princípios aplicáveis da LGPD.

Até essa validação, pontuações, faixas e perfis são hipóteses transparentes de produto, não conclusões científicas definitivas.

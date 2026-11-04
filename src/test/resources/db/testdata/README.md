# Dados de fatura para testes

Ao executar com os perfis `dev` ou `test`, o Flyway aplica as migrations de
`db/migration` e os dados de demonstração versionados em `db/testdata`, em ordem
de versão. Depois, aplica `R__card_recurrences.sql`. O script usa a conta `demo` e a fatura aberta do
cartão Nubank dos dados de exemplo para criar recorrências mensal, quinzenal e
anual, incluindo duas ocorrências quinzenais no ciclo.

Execute o perfil apenas com um banco PostgreSQL isolado de testes. A configuração
de conexão continua vindo do ambiente; o perfil não cria nem limpa o banco.
O script evita repetir as ocorrências já inseridas e ajusta o limite apenas pelos
lançamentos novos.

O perfil `dev` carrega esses dados ao iniciar a aplicação. Para testes
automatizados, configure a conexão e execute
`mvn.cmd '-Dspring.profiles.active=test' test` no PowerShell. O perfil de
produção não carrega esses dados de demonstração.

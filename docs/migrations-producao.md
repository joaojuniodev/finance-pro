# Migrations e deploy

O perfil `prod` carrega apenas `classpath:db/migration`. Em um banco novo,
essas migrations criam o esquema e os catálogos de papéis, permissões, bancos
e categorias. Elas não criam usuários de demonstração nem dados financeiros
fictícios. Os planos e preços dos dados de exemplo também não são carregados;
o catálogo comercial precisa ser configurado com valores aprovados antes de
habilitar o checkout.

Os perfis `dev` e `test` também carregam `classpath:db/testdata`, que contém
usuários com senhas públicas, planos fictícios e outros exemplos. Use esses
perfis apenas com bancos isolados. Nunca os aponte para dados de produção.

As migrations V26–V40 e V42 foram movidas para `db/testdata` sem alteração de
conteúdo. Assim, bancos de desenvolvimento que já aplicaram esses arquivos
continuam encontrando os mesmos arquivos e checksums quando usam o perfil
`dev` ou `test`. Em produção, as lacunas na numeração são intencionais.

O histórico atual começa com uma base destinada a um esquema vazio. Se o banco
de destino já tiver um histórico Flyway anterior ou tiver aplicado os dados de
exemplo, não use `repair`, `baseline` ou exclusões para forçar o deploy. Faça
backup e avalie o histórico e os dados antes de preparar uma migração específica
para esse banco. Um banco com migrations de exemplo já aplicadas não é
automaticamente convertido em um banco de produção por essa separação.

Para o deploy, configure `SPRING_PROFILES_ACTIVE=prod` e as variáveis de
conexão do PostgreSQL (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`
e `SPRING_DATASOURCE_PASSWORD`). Defina também `JWT_SECRET` com um segredo
aleatório e exclusivo do ambiente: o perfil de produção exige essa variável,
em vez de usar a chave fixa de desenvolvimento. O perfil de produção mantém a
validação do esquema pelo Hibernate após a execução do Flyway.

Gere o artefato a partir de um checkout limpo ou com `mvn clean package`.
Uma compilação incremental pode manter em `target/classes` recursos antigos
que já foram movidos; não publique um artefato com esses arquivos residuais.

O teste `MigrationIsolationIntegrationTests` é opcional e exige três bancos
PostgreSQL **vazios e exclusivos para teste**. Configure `CAPTO_MIGRATION_TEST=true`,
`CAPTO_MIGRATION_PROD_URL`, `CAPTO_MIGRATION_DEV_URL`,
`CAPTO_MIGRATION_UPGRADE_URL`, `CAPTO_MIGRATION_USER` e, se necessário,
`CAPTO_MIGRATION_PASSWORD`. Depois execute
`mvn -Dtest=MigrationIsolationIntegrationTests test`. O teste recusa bancos que
já tenham tabelas no esquema público; ele não apaga dados existentes.

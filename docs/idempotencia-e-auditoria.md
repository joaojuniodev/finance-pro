# Idempotência e auditoria de operações — planejamento do Capto

Status: desenho acordado para implementação futura. Começar pela criação de transações. Este documento não implementa o mecanismo.

## Identificação e conteúdo

O frontend gera um UUID por intenção do usuário e envia no header `Idempotency-Key`. Reenvios da mesma intenção, inclusive após timeout, reutilizam a chave. Uma nova intenção recebe uma nova chave.

O backend identifica a operação por `user_id + operation_type + request_id`. Uma restrição UNIQUE no PostgreSQL garante esse escopo inclusive sob concorrência; consultar antes de inserir, isoladamente, não garante idempotência.

O backend normaliza os campos relevantes do payload (corpo da requisição) e calcula seu hash SHA-256, com ordenação consistente de campos, valores monetários e datas. O hash não inclui chave de idempotência, timestamps do servidor ou estado de processamento.

| Chave no mesmo escopo | Hash | Resultado |
|---|---|---|
| Nova | Qualquer | Registrar e processar |
| Existente | Igual | Reutilizar o resultado ou informar o processamento atual |
| Existente | Diferente | Rejeitar a nova chamada com 409, preservando a operação original |

Payloads iguais com chaves diferentes representam operações independentes. O hash não deve ter restrição de unicidade.

## operation_requests

Uma linha por operação lógica, atualizada durante sua execução. Guarda request_id, usuário, tipo, payload_hash, estado atual, código HTTP, recurso criado, código de erro, mensagem sanitizada, tentativas, possibilidade de retry e horários de recebimento, início e conclusão.

Estados previstos: WAITING → PROCESSING → COMPLETED ou FAILED. WAITING significa requisição recebida, sem implicar existência de fila. Cada entrega HTTP pode ter um identificador de correlação separado da chave de idempotência.

## operation_events

Vários eventos por operation_request, relacionados por chave estrangeira. Cada evento registra horário, tipo/estado, mensagem segura e identificação da tentativa quando aplicável. Exemplos: recebimento, início, conclusão, falha e resultado repetido retornado. Registrar transições e acontecimentos úteis, não cada instrução do serviço.

## Concorrência, atomicidade e respostas

- A aquisição da operação para processamento precisa ser atômica, usando restrição única e inserção/trava adequada.
- A transação financeira, seus efeitos associados e o resultado COMPLETED precisam ser confirmados atomicamente. Não marcar sucesso antes do commit.
- A auditoria de falha precisa sobreviver ao rollback da operação financeira, com transação separada controlada pela camada coordenadora.
- Reenvio de COMPLETED retorna o resultado original, sem executar efeitos novamente.
- WAITING/PROCESSING pode retornar 202 apenas quando houver processamento efetivamente em andamento e um caminho de consulta/reenvio documentado.
- O conflito de chave com payload diferente retorna 409 à chamada conflitante; não muda uma operação original COMPLETED para FAILED.
- Definir política explícita para FAILED: falhas determinísticas repetem o erro; falhas transitórias só podem ser reprocessadas após confirmar rollback e adquirir a operação atomicamente.
- Prever recuperação de PROCESSING abandonado por queda do processo e comportamento de resultado desconhecido; um timeout não prova falha da operação.
- Nunca depender apenas de comparação em memória ou de `find` seguido de `save` sem proteção do banco.

## Auditoria e observabilidade

O painel de auditoria é acessível somente a administradores com autorização validada no backend. Dados técnicos detalhados seguem em logs próprios, relacionados por request/correlation ID. Não armazenar tokens, senhas, dados de cartão ou stack traces extensos nas tabelas de negócio.

Manter registros compactos e índices focados no escopo único, usuário, estado e horário. Definir retenção e arquivamento após medir volume. Nunca apagar chaves de idempotência sem definir a janela contratual de deduplicação, pois isso pode permitir executar novamente uma requisição antiga. Mensagens de auditoria são acrescentadas como eventos; devem ter proteção contra alteração indevida.

Referências já consultadas: [AWS Builders’ Library](https://aws.amazon.com/builders-library/making-retries-safe-with-idempotent-APIs/), [PostgreSQL INSERT / ON CONFLICT](https://www.postgresql.org/docs/current/sql-insert.html).

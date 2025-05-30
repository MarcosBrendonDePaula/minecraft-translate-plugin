# README - TranslateChat Plugin

## Descrição
TranslateChat é um plugin para servidores Minecraft 1.20.1 que permite aos jogadores escolherem seu idioma preferido e receberem todas as mensagens do chat traduzidas automaticamente para esse idioma. O plugin também oferece diferentes modos de chat (global, local e privado) para melhorar a experiência de comunicação no servidor. O plugin utiliza a API de tradução fornecida por `translate.marcosbrendon.com` para realizar as traduções em tempo real.

## Funcionalidades
- Tradução automática de mensagens do chat para o idioma escolhido pelo jogador
- Interface gráfica para seleção de idioma
- Múltiplos modos de chat: global, local e privado
- Sistema de cache para otimizar traduções repetidas
- Comandos para gerenciar idiomas e modos de chat
- Configuração flexível de idiomas disponíveis
- Persistência das escolhas de idioma dos jogadores

## Comandos de Idioma
- `/language <idioma>` - Define o idioma para tradução das mensagens
- `/language` - Mostra o idioma atual e abre a interface de seleção
- `/languages` - Mostra os idiomas disponíveis para tradução

## Comandos de Chat
- `/g <mensagem>` - Envia uma mensagem no chat global (para todos os jogadores)
- `/g` - Alterna o modo de chat entre global travado e destravado
- `/l <mensagem>` - Envia uma mensagem no chat local (apenas para jogadores próximos)
- `/l` - Alterna o modo de chat entre local travado e destravado
- `/msg <jogador> <mensagem>` - Envia uma mensagem privada para um jogador específico
- `/r <mensagem>` - Responde à última mensagem privada recebida
- `/broadcast <mensagem>` ou `/bc <mensagem>` - Envia um aviso do sistema para todos os jogadores (requer permissão)

## Modos de Chat
### Chat Global
- Mensagens enviadas para todos os jogadores do servidor, independentemente da distância ou mundo
- Prefixo padrão: `[G]` em amarelo
- Use `/g` sem argumentos para travar/destravar o modo global

### Chat Local
- Mensagens enviadas apenas para jogadores dentro de um raio configurável
- Prefixo padrão: `[L]` em verde
- Use `/l` sem argumentos para travar/destravar o modo local
- Raio padrão: 100 blocos (configurável)

### Mensagens Privadas
- Comunicação direta entre dois jogadores
- Use `/msg <jogador> <mensagem>` para enviar
- Use `/r <mensagem>` para responder rapidamente à última mensagem recebida

### Avisos do Sistema
- Mensagens destacadas enviadas para todos os jogadores
- Prefixo padrão: `[SISTEMA]` em vermelho
- Requer permissão: `translatechat.system`

## Configuração
O arquivo `config.yml` permite configurar:
- Lista de idiomas disponíveis
- Idioma padrão para novos jogadores
- Raio do chat local
- Prefixos dos diferentes tipos de mensagem
- Configurações da API de tradução e cache
- Mensagens personalizadas
- Configurações da interface de seleção de idioma

## Instalação
1. Coloque o arquivo `TranslateChat.jar` na pasta `plugins` do seu servidor
2. Reinicie o servidor ou use um gerenciador de plugins para carregar o plugin
3. Configure o arquivo `config.yml` conforme necessário

## Requisitos
- Servidor Minecraft 1.20.1 (Spigot, Paper ou derivados)
- Java 17 ou superior

## Permissões
- `translatechat.system` - Permite usar o comando `/broadcast` para enviar avisos do sistema
- Não são necessárias permissões especiais para usar os outros comandos

## Códigos de Idioma
Exemplos de códigos de idioma suportados:
- `pt-br` - Português (Brasil)
- `en` - Inglês
- `es` - Espanhol
- `fr` - Francês
- `de` - Alemão
- `it` - Italiano
- `ja` - Japonês
- `ko` - Coreano
- `ru` - Russo
- `zh-cn` - Chinês (Simplificado)

## Suporte
Para suporte ou dúvidas, entre em contato com o desenvolvedor.

# README - TranslateChat Plugin

## Descrição
TranslateChat é um plugin para servidores Minecraft 1.20.1 que permite aos jogadores escolherem seu idioma preferido e receberem todas as mensagens do chat traduzidas automaticamente para esse idioma. O plugin utiliza a API de tradução fornecida por `translate.marcosbrendon.com` para realizar as traduções em tempo real.

## Funcionalidades
- Tradução automática de mensagens do chat para o idioma escolhido pelo jogador
- Interface gráfica para seleção de idioma
- Comandos para gerenciar idiomas
- Configuração flexível de idiomas disponíveis
- Persistência das escolhas de idioma dos jogadores

## Comandos
- `/language <idioma>` - Define o idioma para tradução das mensagens
- `/language` - Mostra o idioma atual e abre a interface de seleção
- `/languages` - Mostra os idiomas disponíveis para tradução

## Configuração
O arquivo `config.yml` permite configurar:
- Lista de idiomas disponíveis
- Idioma padrão para novos jogadores
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
- Não são necessárias permissões especiais para usar os comandos básicos

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

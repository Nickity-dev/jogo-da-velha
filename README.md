# Jogo da Velha em Java

Jogo da velha para terminal com partidas entre duas pessoas ou contra o computador.

## Requisitos

- Java Development Kit (JDK) instalado.

Para confirmar a instalação:

```powershell
java -version
```

## Compilar e executar

No PowerShell, dentro da pasta do projeto:

```powershell
javac src\Main.java
java -cp src Main
```

## Uso

No menu inicial, escolha:

- `1` para jogar contra outra pessoa. Informe os dois nomes; o primeiro escolhe `X` ou `O`. O jogador com `X` começa.
- `2` para jogar contra o computador. Informe seu nome e escolha seu símbolo. Ao escolher `O`, o computador inicia com `X`.
- `0` para sair.

Em cada turno, informe linha e coluna de `0` a `2`:

```
  0   1   2
0   |   |
 ---+---+---
1   |   |
 ---+---+---
2   |   |
```

O jogo impede coordenadas inválidas e casas preenchidas. Após a partida, escolha jogar novamente, voltar ao menu ou sair.

O computador busca vitória e bloqueio imediatos e usa minimax para não perder. Entre opções igualmente seguras, prefere o centro, depois os cantos e, por fim, escolhe aleatoriamente.

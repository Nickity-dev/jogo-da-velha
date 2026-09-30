import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final char VAZIO = ' ';
    private static final char[][] tabuleiro = new char[3][3];
    private static final Random ALEATORIO = new Random();

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        System.out.println("=== JOGO DA VELHA ===");

        boolean sair = false;
        while (!sair) {
            int opcao = lerOpcao(entrada,
                    "\n1 - Jogar contra outro jogador\n2 - Jogar contra o computador\n0 - Sair\nEscolha uma opção: ", 0, 2);
            if (opcao == 0) sair = true;
            else if (opcao == 1) sair = jogarDoisJogadores(entrada);
            else sair = jogarContraComputador(entrada);
        }

        System.out.println("Obrigado por jogar!");
        entrada.close();
    }

    private static boolean jogarDoisJogadores(Scanner entrada) {
        String primeiro = lerNome(entrada, "Nome do primeiro jogador: ");
        String segundo = lerNome(entrada, "Nome do segundo jogador: ");
        char simboloPrimeiro = lerSimbolo(entrada, primeiro + ", escolha X ou O: ");
        String jogadorX = simboloPrimeiro == 'X' ? primeiro : segundo;
        String jogadorO = simboloPrimeiro == 'O' ? primeiro : segundo;

        while (true) {
            jogarPartida(entrada, jogadorX, jogadorO, false, VAZIO);
            int acao = lerAcaoFinal(entrada);
            if (acao == 1) continue;
            return acao == 0;
        }
    }

    private static boolean jogarContraComputador(Scanner entrada) {
        String jogador = lerNome(entrada, "Seu nome: ");
        char simboloJogador = lerSimbolo(entrada, jogador + ", escolha X ou O: ");
        char simboloComputador = outroSimbolo(simboloJogador);
        String jogadorX = simboloJogador == 'X' ? jogador : "Computador";
        String jogadorO = simboloJogador == 'O' ? jogador : "Computador";

        while (true) {
            jogarPartida(entrada, jogadorX, jogadorO, true, simboloComputador);
            int acao = lerAcaoFinal(entrada);
            if (acao == 1) continue;
            return acao == 0;
        }
    }

    private static int lerAcaoFinal(Scanner entrada) {
        return lerOpcao(entrada, "\n1 - Jogar novamente\n2 - Voltar ao menu\n0 - Sair\nEscolha uma opção: ", 0, 2);
    }

    private static void jogarPartida(Scanner entrada, String jogadorX, String jogadorO,
                                     boolean contraComputador, char simboloComputador) {
        limparTabuleiro();
        char atual = 'X';

        while (true) {
            mostrarTabuleiro();
            String nomeAtual = atual == 'X' ? jogadorX : jogadorO;
            System.out.println("Vez de " + nomeAtual + " (" + atual + ").");

            if (contraComputador && atual == simboloComputador) {
                int[] jogada = escolherJogadaComputador(simboloComputador);
                tabuleiro[jogada[0]][jogada[1]] = simboloComputador;
                System.out.println("Computador escolheu linha " + jogada[0] + ", coluna " + jogada[1] + ".");
            } else {
                fazerJogadaHumana(entrada, atual);
            }

            if (venceu(atual)) {
                mostrarTabuleiro();
                System.out.println(nomeAtual + " venceu!");
                return;
            }
            if (tabuleiroCheio()) {
                mostrarTabuleiro();
                System.out.println("Deu velha! A partida terminou empatada.");
                return;
            }
            atual = outroSimbolo(atual);
        }
    }

    private static void fazerJogadaHumana(Scanner entrada, char simbolo) {
        while (true) {
            int linha = lerOpcao(entrada, "Linha (0 a 2): ", 0, 2);
            int coluna = lerOpcao(entrada, "Coluna (0 a 2): ", 0, 2);
            if (tabuleiro[linha][coluna] == VAZIO) {
                tabuleiro[linha][coluna] = simbolo;
                return;
            }
            System.out.println("Essa posição já está ocupada. Escolha outra.");
        }
    }

    private static int[] escolherJogadaComputador(char computador) {
        char adversario = outroSimbolo(computador);
        List<int[]> livres = posicoesLivres();
        List<int[]> vencedoras = jogadasQueVencem(livres, computador);
        if (!vencedoras.isEmpty()) return escolherPorPrioridade(vencedoras);

        List<int[]> bloqueios = jogadasQueVencem(livres, adversario);
        if (!bloqueios.isEmpty()) return escolherMelhorJogada(bloqueios, computador);

        return escolherMelhorJogada(livres, computador);
    }

    private static List<int[]> jogadasQueVencem(List<int[]> jogadas, char simbolo) {
        List<int[]> resultado = new ArrayList<>();
        for (int[] jogada : jogadas) {
            tabuleiro[jogada[0]][jogada[1]] = simbolo;
            if (venceu(simbolo)) resultado.add(jogada);
            tabuleiro[jogada[0]][jogada[1]] = VAZIO;
        }
        return resultado;
    }

    private static int[] escolherMelhorJogada(List<int[]> candidatas, char computador) {
        int melhorPontuacao = Integer.MIN_VALUE;
        List<int[]> melhores = new ArrayList<>();
        for (int[] jogada : candidatas) {
            tabuleiro[jogada[0]][jogada[1]] = computador;
            int pontuacao = minimax(outroSimbolo(computador), computador, 0);
            tabuleiro[jogada[0]][jogada[1]] = VAZIO;
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhores.clear();
                melhores.add(jogada);
            } else if (pontuacao == melhorPontuacao) {
                melhores.add(jogada);
            }
        }
        return escolherPorPrioridade(melhores);
    }

    private static int minimax(char vez, char computador, int profundidade) {
        char adversario = outroSimbolo(computador);
        if (venceu(computador)) return 10 - profundidade;
        if (venceu(adversario)) return profundidade - 10;
        if (tabuleiroCheio()) return 0;

        int melhor = vez == computador ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int[] jogada : posicoesLivres()) {
            tabuleiro[jogada[0]][jogada[1]] = vez;
            int pontuacao = minimax(outroSimbolo(vez), computador, profundidade + 1);
            tabuleiro[jogada[0]][jogada[1]] = VAZIO;
            melhor = vez == computador ? Math.max(melhor, pontuacao) : Math.min(melhor, pontuacao);
        }
        return melhor;
    }

    private static int[] escolherPorPrioridade(List<int[]> jogadas) {
        for (int[] jogada : jogadas) {
            if (jogada[0] == 1 && jogada[1] == 1) return jogada;
        }
        List<int[]> cantos = new ArrayList<>();
        for (int[] jogada : jogadas) {
            if ((jogada[0] == 0 || jogada[0] == 2) && (jogada[1] == 0 || jogada[1] == 2)) cantos.add(jogada);
        }
        if (!cantos.isEmpty()) return cantos.get(ALEATORIO.nextInt(cantos.size()));
        return jogadas.get(ALEATORIO.nextInt(jogadas.size()));
    }

    private static List<int[]> posicoesLivres() {
        List<int[]> livres = new ArrayList<>();
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 3; coluna++) {
                if (tabuleiro[linha][coluna] == VAZIO) livres.add(new int[] {linha, coluna});
            }
        }
        Collections.shuffle(livres, ALEATORIO);
        return livres;
    }

    private static String lerNome(Scanner entrada, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String nome = entrada.nextLine().trim();
            if (!nome.isEmpty()) return nome;
            System.out.println("O nome não pode ficar vazio.");
        }
    }

    private static char lerSimbolo(Scanner entrada, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String simbolo = entrada.nextLine().trim().toUpperCase();
            if (simbolo.equals("X") || simbolo.equals("O")) return simbolo.charAt(0);
            System.out.println("Digite apenas X ou O.");
        }
    }

    private static int lerOpcao(Scanner entrada, String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            try {
                int opcao = Integer.parseInt(entrada.nextLine().trim());
                if (opcao >= minimo && opcao <= maximo) return opcao;
            } catch (NumberFormatException e) {
                // A partida continua após uma entrada inválida.
            }
            System.out.println("Digite um número entre " + minimo + " e " + maximo + ".");
        }
    }

    private static void limparTabuleiro() {
        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 3; coluna++) tabuleiro[linha][coluna] = VAZIO;
        }
    }

    private static boolean tabuleiroCheio() {
        return posicoesLivres().isEmpty();
    }

    private static char outroSimbolo(char simbolo) {
        return simbolo == 'X' ? 'O' : 'X';
    }

    private static void mostrarTabuleiro() {
        System.out.println("\n  0   1   2");
        for (int linha = 0; linha < 3; linha++) {
            System.out.println(linha + " " + tabuleiro[linha][0] + " | " + tabuleiro[linha][1] + " | " + tabuleiro[linha][2]);
            if (linha < 2) System.out.println(" ---+---+---");
        }
        System.out.println();
    }

    private static boolean venceu(char jogador) {
        for (int i = 0; i < 3; i++) {
            if ((tabuleiro[i][0] == jogador && tabuleiro[i][1] == jogador && tabuleiro[i][2] == jogador)
                    || (tabuleiro[0][i] == jogador && tabuleiro[1][i] == jogador && tabuleiro[2][i] == jogador)) return true;
        }
        return (tabuleiro[0][0] == jogador && tabuleiro[1][1] == jogador && tabuleiro[2][2] == jogador)
                || (tabuleiro[0][2] == jogador && tabuleiro[1][1] == jogador && tabuleiro[2][0] == jogador);
    }
}

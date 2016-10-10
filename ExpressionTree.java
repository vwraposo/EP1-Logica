import java.util.Scanner;

/** Classe responsável por representar e manipular uma expressão lógica
 * no formato de árvore.
 * Todas as fórmulas, com exceção das atômicas, devem ser parentizadas e bem formadas.
 * Átomos são definidos por letras minúsculas.
 * Operações são as seguintes:
 *     (F1 .O. F2) (DISJUNÇÃO)
 *     (F1 .A. F2) (CONJUNÇÃO)
 *     (F1 .I. F2) (IMPLICAÇÃO)
 *     (.N. F1) (NEGAÇÃO) 
 * onde F1 e F2 são fórmulas bem formadas.
 *
 * @author Gabriel Russo
 * @author Matheus Oliveira
 * @author Victor Colombo
 * @author Victor Raposo
 */
public class ExpressionTree {
    private Node root;
    private String input;
    private int count;
    
    private class Node {
        private Node left, right;
        private char value;
        private int size;
        
        public Node (char value) {
            this.left = null;
            this.right = null;
            this.value = value;
            this.size = 1;
        }
    }

    /**
     * Inicializa uma nova ExpressionTree a partir da string input.
     * @params input fórmula bem formada e parentizada
     */
    public ExpressionTree (String input) {
        this.input = input;
        this.root = build ();
        this.count = 0;
    }

    /**
     * Inicializa uma nova ExpressionTree com raiz root.
     * @params root nó de uma ExpressionTree
     */
    private ExpressionTree (Node root) {
        this.root = root;
    }

    /**
     * Devolve o tamanho de uma ExpressionTree
     * @return o tamanho da ExpressionTree (quantidade de nós)
     */
    public int getSize () {
        return root.size;
    }

    /**
     * Devolve se uma ExpressionTree é um átomo
     * @return true se a ExpressionTree é uma fórmula atômica
     */
    public boolean isAtomic () {
        return this.getSize () == 1;
    }

    /**
     * Devolve uma nova ExpressionTree induzida pela subárvore esquerda
     * @return a subárvore esquerda
     */
    public ExpressionTree left () {
        return new ExpressionTree (root.left);
    }

    /**
     * Devolve uma nova ExpressionTree induzida pela subárvore direita
     * @return a subárvore direita
     */
    public ExpressionTree right () {
        return new ExpressionTree (root.right);
    }

    /**
     * Devolve o valor da raiz
     * @return o caractere da raiz
     */
    public char getRoot () {
        return this.root.value;
    }

    /**
     * Devolve true se c é um átomo (letra minúscula)
     * @param c caractere
     * @return true se for um átomo (minúscula)
     */
    private static boolean isAtom (char c) {
        return c >= 'a' && c <= 'z';
    }

    /**
     * Devolve true se c é uma operação (A, I, O, N)
     * @param c caractere
     * @return true se é uma operação (A, I, O, N)
     */
    private static boolean isOp (char c) {
        return c == 'A' || c == 'I' || c == 'O' || c == 'N';
    }

    /**
     * Devolve o proximo caractere relevate de this.input
     * @return o proximo caractere relevate de this.input
     */
    private char next () {
        char c = input.charAt (count++);
        while (c != '(' && c != ')' && !isAtom (c) && !isOp (c))
            c = input.charAt (count++);
        return c;
    }

    /**
     * Constroi a ExpressionTree recursivamente a partir de this.input
     * @return a ExpressionTree da expressão em this.input
     */
    private Node build () {
        char c = next ();
        if (isAtom (c)) return new Node (c);
        if (c == 'N') return null;
        Node loc = new Node ('$');
        loc.left = build ();
        if (loc.left == null) loc.value = 'N';
        else loc.value = next ();
        loc.right = build ();
        c = next ();
        if (loc.left != null) loc.size += loc.left.size;
        if (loc.right != null) loc.size += loc.right.size;
        return loc;
    }

    /**
     * Imprime na saída padrão this em ordem (e-r-d)
     */
    private void show () {
        showR (this.root);
    }

    /**
     * Imprime na saída padrão em e-r-d a subárvore que tem raiz r
     * @param r raiz de uma ExpressionTree
     */
    private void showR (Node r) {
        if (r == null) return;
        showR (r.left);
        System.out.println (r.value);
        showR (r.right);
    }

    /**
     * Cliente de teste
     * @param args argumentos da linha de comando
     */
    public static void main (String[] args) {
        Scanner scanner = new Scanner (System.in);
        ExpressionTree t = new ExpressionTree (scanner.next ());
        t.show ();
        System.out.println (t.getSize ());
        t.right ().show ();
    }
}


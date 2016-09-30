import java.io.*;

/* Classe responsável por representar e manipular uma expressão lógica
 * no formato de árvore.
 * Todas as fórmulas, com exceção das atômicas, devem ser parentizadas e bem formadas.
 * Átomos são definidos por letras minúsculas.
 * Operações são as seguintes:
 *     (F1 .O. F2) (DISJUNÇÃO)
 *     (F1 .A. F2) (CONJUNÇÃO)
 *     (F1 .I. F2) (IMPLICAÇÃO)
 *     (.N. F1) (NEGAÇÃO) 
 * onde F1 e F2 são fórmulas bem formadas. */

public class ExpressionTree {
    
    private Node root;
    private InputStream in;
    
    private class Node {
        private Node left, right;
        private boolean atomic;
        private char value;
        
        public Node (boolean atomic, char value) {
            this.left = null;
            this.right = null;
            this.atomic = atomic;
            this.value = value;
        }
    }

    /* Devolve uma ExpressionTree lida de in. Todas as formulas, com exceção
     * das atômicas devem ser parentizadas. */
    public ExpressionTree (InputStream in) {
        this.in = in;
        this.root = build ();
    }

    /* Verifica se c é um átomo (letra minúscula). */
    private boolean isAtom (char c) {
        return c >= 'a' && c <= 'z';
    }

    /* Verifica se c é uma operação (A, I, O ou N) */
    private boolean isOp (char c) {
        return c == 'A' || c == 'I' || c == 'O' || c == 'N';
    }

    /* Devolve o próximo caractere relevante de in */
    private char next () {
        char c = '$';
        while (c != '(' && c != ')' && !isAtom (c) && !isOp (c)) {
            try {
                c = (char) in.read ();
            } catch (IOException e) {
            }
        }
        return c;
    }
    
    /* Constroi a ExpressionTree recursivamente */
    private Node build () {
        char c = next ();
        if (isAtom (c)) return new Node (true, c);
        if (c == 'N') return null;
        Node loc = new Node (false, '$');
        loc.left = build ();
        if (loc.left == null) loc.value = 'N';
        else loc.value = next ();
        loc.right = build ();
        c = next ();
        return loc;
    }

    /* Imprime a árvore em e-r-d */
    private void show () {
        showR (this.root);
    }

    /* Método recursivo auxiliar para show */
    private void showR (Node r) {
        if (r == null) return;
        showR (r.left);
        System.out.println (r.value);
        showR (r.right);
    }

    /* Cliente de testes */
    public static void main (String[] args) {
        ExpressionTree t = new ExpressionTree (System.in);
        t.show ();
    }
}


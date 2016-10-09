import java.util.Scanner;

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
    private String input;
    private int count;
    
    private class Node {
        private Node left, right;
        private boolean atomic;
        private char value;
        private int size;
        
        public Node (boolean atomic, char value) {
            this.left = null;
            this.right = null;
            this.atomic = atomic;
            this.value = value;
            this.size = 1;
        }
    }

    /* Devolve a ExpressionTree de input. Todas as formulas, com exceção
     * das atômicas devem ser parentizadas. */
    public ExpressionTree (String input) {
        this.input = input;
        this.root = build ();
        this.count = 0;
    }

    /* Instancia uma ExpressionTree a partir de outra */
    private ExpressionTree (Node root) {
        this.root = root;
    }

    /* Devolve o tamanho da ExpresionTree */
    public int getSize () {
        return root.size;
    }

    public boolean isAtomic () {
        return this.root.atomic;
    }

    /* Devolve a subarvore da esquerda como uma nova ExpressionTree */
    public ExpressionTree left () {
        return new ExpressionTree (root.left);
    } 
    
    /* Devolve a subarvore da direita como uma nova ExpressionTree */
    public ExpressionTree right () {
        return new ExpressionTree (root.right);
    } 

    public char getRoot () {
        return this.root.value;
    }

    /* Verifica se c é um átomo (letra minúscula). */
    private boolean isAtom (char c) {
        return c >= 'a' && c <= 'z';
    }

    /* Verifica se c é uma operação (A, I, O ou N) */
    private boolean isOp (char c) {
        return c == 'A' || c == 'I' || c == 'O' || c == 'N';
    }

    /* Devolve o próximo caractere relevante de input */
    private char next () {
        char c = input.charAt (count++);
        while (c != '(' && c != ')' && !isAtom (c) && !isOp (c))
            c = input.charAt (count++);
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
        if (loc.left != null) loc.size += loc.left.size;
        if (loc.right != null) loc.size += loc.right.size;
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
        Scanner scanner = new Scanner (System.in);
        ExpressionTree t = new ExpressionTree (scanner.next ());
        t.show ();
        System.out.println (t.getSize ());
        t.right ().show ();
    }
}


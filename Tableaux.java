import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;

/**
 * Classe responsável por montar e trabalhar um Tableaux semântico.
 * Representa as fórmulas com auxílio da classe ExpressionTree.
 *
 * A implementação utiliza uma pilha para guardar o estado anterior a uma
 * expansão beta e um vetor para armazenar o ramo atual. A estratégia utilizada
 * foi a da busca em profundidade, executando sempre todas as expansões alfa e depois
 * a menor expansão beta, em relação ao tamanho da fórmula. Quando um ramo se contradiz,
 * voltamos para o estado antes da última transformação beta. Quando não há tal possibilidade,
 * temos um sequente válido.
 *
 * @author Gabriel Russo
 * @author Matheus Oliveira
 * @author Victor Colombo
 * @author Victor Raposo
 */
public class Tableaux { 
    private int open;
    private int size;
    private Formula[] branch; 
    private boolean[] betas;
    private Stack<Triple> stack;

    private class Formula {
        private boolean value;
        private ExpressionTree tree;

        public Formula (boolean value, ExpressionTree tree) {
            this.value = value;
            this.tree = tree;
        }
    }

    private class Triple {
        private Formula formula;
        private boolean[] betas;
        private int size;

        public Triple (Formula formula, boolean[] betas, int size) {
            this.formula = formula;
            this.betas = new boolean[betas.length];
            for (int i = 0; i < betas.length; i++)
                this.betas[i] = betas[i];
            this.size = size;
        }
    }

    /**
     * Inicializa um Tableaux da forma A1,..., An |- B1,...,Bn
     * @see ExpressionTree
     * @param A premissas (fórmulas bem formadas)
     * @param B consequencias lógicas (fórmulas bem formadas)
     */
    public Tableaux (String[] A, String[] B) {
        int count = 0;
        this.open = 1;
        ArrayList<Formula> tmp = new ArrayList<Formula> ();
        for (String f : A) tmp.add (new Formula (true, new ExpressionTree (f))); 
        for (String f : B) tmp.add (new Formula (false, new ExpressionTree (f))); 
        for (Formula f : tmp) count += f.tree.getSize ();
        this.betas = new boolean[count];
        this.branch = new Formula[count];
        this.size = tmp.size ();
        for (int i = 0; i < size; i++) branch[i] = tmp.get (i); 
        stack = new Stack<Triple> (); 
    }

    /**
     * Verifica se o ramo atual tem uma contradição
     * @return true se o ramo atual tem uma contradição
     */
    private boolean hasContradiction () {
        boolean value[] = new boolean[26];
        boolean set[] = new boolean[26];
        for (int i = 0; i < 26; i++) set[i] = false;
        for (int i = 0; i < size; i++) {
            Formula f = branch[i];
            if (f.tree.isAtomic ()) {
                int k = f.tree.getRoot () - 'a';
                if (set[k]) if (value[k] != f.value) return true;
                set[k] = true;
                value[k] = f.value;
            }
        }
        return false;
    }

    /**
     * Aplica todas expansões alfa no ramo atual a partir da posição start.
     * Verifica e marca a presença de expansões do tipo beta.
     * @param start posição para começar aplicar as expansões (branch[start..size-1])
     * @return true se existe uma expansão beta a ser aplicada.
     */
    private boolean applyAlpha (int start) {
        boolean saturated = false;
        for (int i = start; i < size; i++) {
            Formula f = branch[i];
            char c = f.tree.getRoot ();
            boolean b = f.value, bL, bR, found;
            found = bL = bR = false;

            /* Determina qual expansão alfa deve ser feita */
            if (b && c == 'A') {
                found = true;
                bL = true;
                bR = true;
            }
            else if (!b && c == 'O') {
                found = true;
                bL = false;
                bR = false;
            }
            else if (!b && c == 'I') {
                found = true;
                bL = true;
                bR = false;
            }
            else if (b && c == 'N') {
                found = true;
                bR = false;
            }
            else if (!b && c == 'N') {
                found = true;
                bR = true;
            }
            
            /* Adiciona ao ramo */
            if (found) {
                if (c != 'N') branch[size++] = new Formula (bL, f.tree.left ());
                branch[size++] = new Formula (bR, f.tree.right ());
            }
            /* Marca a expansão beta */
            else if (!found && !f.tree.isAtomic ())
                betas[i] = true;
        }
        /* Verifica se existe uma expansão beta no ramo */
        for (int i = 0; i < size; i++) if (betas[i]) return true;
        return false;
    }

    /**
     * Aplica a expansão beta no ramo atual de menor tamanho, se existir.
     */
    private void applyBeta () {
        int min, mini = -1;

        /* Encontra a menor expansão beta no ramo */
        min = branch.length + 1;
        for (int i = 0; i < size; i++) {
            if (betas[i] && branch[i].tree.getSize () < min) {
                min = branch[i].tree.getSize ();
                mini = i;
            }
        }
        if (mini == -1) return;
        Formula f = branch[mini];
        char c = f.tree.getRoot ();
        boolean b = f.value;
        boolean bL, bR;
        bL = bR = false;

        /* Determina qual expansão beta deve ser feita */
        if (!b && c == 'A') {
            bL = false;
            bR = false;
        }
        else if (b && c == 'O') {
            bL = true;
            bR = true;
        }
        else if (b && c == 'I') {
            bL = false;
            bR = true;
        }

        betas[mini] = false;
        open++;
        /* Salva na pilha a maior subfórmula e adiciona ao ramo a menor */
        if (f.tree.right ().getSize () > f.tree.left ().getSize ()) {
            stack.push (new Triple (new Formula (bR, f.tree.right ()), betas, size));
            branch[size++] = new Formula (bL, f.tree.left ());
        } else {
            stack.push (new Triple (new Formula (bL, f.tree.left ()), betas, size));
            branch[size++] = new Formula (bR, f.tree.right ());
        }
    }

    /**
     * Imprime um contra-exemplo para a fórmula.
     * O método supõe que o ramo está saturado e aberto.
     */
    private void contraExample () {
        boolean[] seen = new boolean[26];
        for (int i = 0; i < 26; i++) seen[i] = false;
        for (int i = 0; i < size; i++) {
            Formula f = branch[i];
            char c = f.tree.getRoot ();
            if (f.tree.isAtomic () && !seen[c - 'a']) {
                System.out.println (c + " = " + f.value);
                seen[c - 'a'] = true;
            }
        }
    }

    /**
     * Resolve o Tableaux.
     * Imprime na saída padrão se representa um sequente válido ou dá um contra-exemplo.
     */
    public void solve () {
        boolean valid, hasBeta;
        int start = 0;
        valid = true;
        while (open > 0) {
            hasBeta = applyAlpha (start);

            /* Ramo fechado */
            if (hasContradiction ()) {
                if (!stack.isEmpty ()) {
                    Triple last = stack.pop ();
                    size = last.size;
                    betas = last.betas;
                    branch[size++] = last.formula;
                }
                open--;
                if (open == 0) break;
            }
            /* Ramo saturado */
            else if (!hasBeta) {
                valid = false;
                System.out.println ("O sequente não é válido. Contra-exemplo:");
                contraExample ();
                break;
            }
            else applyBeta ();
            start = size - 1;
        }
        if (valid) 
            System.out.println ("O sequente é válido!");
    }

    /**
     * Lê as premissas da entrada padrão e uma consequência lógica B.
     * Constrói um Tableaux e resolve-o.
     * @param args argumentos da linha de comando
     */
    public static void main (String[] args) {
        int n, m;
        String[] A, B;
        Scanner in = new Scanner (System.in);

        System.out.print ("Insira o tamanho do conjunto de premissas: ");    
        n = Integer.parseInt (in.nextLine ());
        if (n > 0) System.out.println ("Insira cada premissa, devidamente formatada");
        A = new String[n];
        for (int i = 0; i < n; i++) A[i] = in.nextLine ();

        System.out.println ("Insira a consequência lógica B");    
        m = 1;
        B = new String[m];
        for (int i = 0; i < m; i++) B[i] = in.nextLine ();

        Tableaux tb = new Tableaux (A, B);
        tb.solve ();
    }
}

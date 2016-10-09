import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;

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

    private boolean applyAlpha (int start) {
        boolean saturated = false;
        for (int i = start; i < size; i++) {
            Formula f = branch[i];
            char c = f.tree.getRoot ();
            boolean b = f.value, bL, bR, found;
            found = bL = bR = false;

            /* Determina que operacao alpha deve ser feita */
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
            
            if (found) { 
                /* Adiciona ao ramo */ 
                if (c != 'N') branch[size++] = new Formula (bL, f.tree.left ());
                branch[size++] = new Formula (bR, f.tree.right ());
            }
            else if (!found && !f.tree.isAtomic ())
                betas[i] = true;
        }
        for (int i = 0; i < size; i++) if (betas[i]) return true;
        return false;
    }

    private void applyBeta () {
        for (int i = 0; i < size; i++) {
            Formula f = branch[i];
            if (betas[i]) {
                char c = f.tree.getRoot ();
                boolean b = f.value;
                boolean bL, bR;
                bL = bR = false;

                /* Determina que operacao beta deve ser feita */
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

                betas[i] = false;
                stack.push (new Triple (new Formula (bR, f.tree.right ()), betas, size));
                open++;
                branch[size++] = new Formula (bL, f.tree.left ());
                return;
            }
        }
    }

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
    /*while apply_alpha -> apply beta 
      dai verifica se e valida se nao for valida e staurado
      mas nao veremos contradicoes no meio do role, sempre saturamos o ramo
    Implementar alguma forma de estrategia? nem que seja pegar o beta de tamanho menor
    so pra ter algo pra escrever no relatoro
    como vamos mandar o ep se o paca nao funciona, ver com a galera
    fazer ponteiro do has beta ser statico */
    public void solve () {
        boolean valid, hasBeta;
        int start = 0;
        valid = true;
        while (open > 0) {
            hasBeta = applyAlpha (start);
            
            // Ramo fechado
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
            // Ramo saturado
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

    public static void main (String[] args) {
        int n, m;
        String[] A, B;
        Scanner in = new Scanner (System.in);

        System.out.print ("Insira o tamanho do conjunto de premissas: ");    
        n = Integer.parseInt (in.nextLine ());
        System.out.println ("Insira cada premissa, devidamente formatada");
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


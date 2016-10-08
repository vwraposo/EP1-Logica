import java.util.Scanner;
import java.util.Stack;
import java.io.InputStream;

public class Tableaux { 
    private int open;
    private int size;
    private Pair[] branch; 
    private boolean[] betas;
    private Stack<Triple> stack;

    private class Pair {
        boolean value;
        ExpressionTree tree;

        public Pair (boolean value, ExpressionTree tree) {
            this.value = value;
            this.tree = tree;
        }
    }

    private class Triple {
        Pair pair;
        boolean[] betas;
        int size;

        public Triple (Pair pair, boolean[] betas, int size) {
            this.pair = pair;
            this.betas = betas;
            this.size = size;
        }
    }


    public Tableaux () {
        open = 1;
        size = 0;
        branch = (Pair[]) new Object[30];
        betas = new boolean[30];
        stack = new Stack<Triple> (); 
    }


    private void readInput (Scanner in) {
        int n = in.nextInt ();
        while (size < n)
            branch[size++] = new Pair (true, new ExpressionTree (in));
        int m = in.nextInt ();
        while (size < n+m)
            branch[size++] = new Pair (false, new ExpressionTree (in));
    }

    private boolean contradiction () {
        boolean value[] = new boolean[26];
        boolean set[] = new boolean [26];
        for (int i = 0; i < 26; i++) set[i] = false;
        for (int i = 0; i < size; i++) {
            if (branch[i].tree.size () == 1) {
                int k = branch[i].tree.getRoot () - 'a';
                if (set[k]) if (value[k] != branch[i].value) return true;
                set[k] = true;
                value[k] = branch[i].value;
            }
        }
        return false;
    }

    private boolean applyAlpha () {
        boolean returnValue = false;
        boolean saturated = false;
        for (int i = 0; i < size; i++) {
            char c = branch[i].tree.getRoot ();
            boolean b = branch[i].value;
            boolean boolL, boolR, hasAlpha;
            boolL = boolR = hasAlpha = false;

            /* Determina que operacao alpha deve ser feita */
            if (b && c == 'A') {
                hasAlpha = true;
                boolL = true;
                boolR = true;
            }
            else if (!b && c == 'O') {
                hasAlpha = true;
                boolL = false;
                boolR = false;
            }
            else if (!b && c == 'I') {
                hasAlpha = true;
                boolL = true;
                boolR = false;
            }
            else if (b && c == 'N') {
                hasAlpha = true;
                boolR = false;
            }
            else if (!b && c == 'N') {
                hasAlpha = true;
                boolR = true;
            }

            
            if (hasAlpha) { 
                returnValue = true;
                /* Adiciona ao ramo */ 
                if (c != 'N') 
                    branch[size++] = new Pair (boolL, branch[size].tree.left ());
                branch[size++] = new Pair (boolR, branch[size].tree.right ());
            }
            else if (!hasAlpha && branch[i].tree.size () > 1) {
                betas[i] = true;
            }
        }

        return returnValue;
    }

    private boolean applyBeta () {
        boolean returnValue = false;
        for (int i = 0; i < size; i++)
            if (betas[i]) {
                returnValue = true;
                char c = branch[i].tree.getRoot ();
                boolean b = branch[i].value;
                boolean boolL, boolR, hasAlpha;
                boolL = boolR = false;

                /* Determina que operacao alpha deve ser feita */
                if (!b && c == 'A') {
                    boolL = false;
                    boolR = false;
                }
                else if (b && c == 'O') {
                    boolL = true;
                    boolR = true;
                }
                else if (b && c == 'I') {
                    boolL = false;
                    boolR = true;
                }

                stack.push (new Triple (new Pair (boolR, branch[i].tree.right ()), betas, size));
                betas[i] = false;
                open++;
                branch[size++] = new Pair (boolL, branch[size].tree.left ());

                break;
            }
            
            return returnValue;
    }
    /*while apply_alpha -> apply beta 
      dai verifica se e valida se nao for valida e staurado
      mas nao veremos contradicoes no meio do role, sempre saturamos o ramo
    Implementar alguma forma de estrategia? nem que seja pegar o beta de tamanho menor
    so pra ter algo pra escrever no relatoro
    como vamos mandar o ep se o paca nao funciona, ver com a galera
    fazer ponteiro do has beta ser statico */
    
    
    public void solve (InputStream input) {
        Scanner in = new Scanner (input);
        boolean valid, hasAlpha, hasBeta;
        readInput (in);
        valid = hasAlpha = hasBeta = true;

        while (open > 0) {
            if (!hasAlpha && !hasBeta) { 
                //imprime e break
                valid = false;
                for (int i = 0; i < size; i++) 
                    if (branch[i].tree.size () == 1) 
                        System.out.println (branch[i].tree.getRoot () + " = " + branch[i].value);
                break;
            }
            // Aplica todos os alphas e marca os betas
            hasAlpha = applyAlpha ();
            // Aplica um beta e empilha o estado open++;
            hasBeta = applyBeta ();
            if (contradiction ()) {
                // Desempilha o estado open--;
                Triple last = stack.pop ();
                size = last.size;
                betas = last.betas;
                branch[++size] = last.pair;
                open--;
            }
        }
        if (valid) 
            System.out.println ("Valido");
    }
}


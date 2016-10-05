import java.util.Stack;

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
        open= 1;
        size = 0;
        branch = (Pair[]) new Object[30];
        betas = new boolean[30];
        stack = new Stack<Triple> (); 


    private void readInput (InputStream in) {
        int n = in.nextInt;
        while (size < n)
            branch[size++] = new Pair (true, new ExpressionTree (in));
        int m = in.nextInt;
        while (size < n+m)
            branch[size++] = new Pair (false, new ExpressionTree (in));
    }

    private void applyAlpha () {
        for (int i = 0; i < size; i++) {
            char c = branch[i].tree.getRoot ();
            boolean b = branch[i].value;
            boolean boolL, boolR, hasAlpha;
            boolL = boolR = false;

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
                /* Adiciona ao ramo */ 
                if (c != 'N') 
                    branch[size++] = new Pair (boolL, branch[size].tree.left ());
                branch[size++] = new Pair (boolR, branch[size].tree.right ());
            }
            else if (!hasAlpha && branch[i].size == 1) {
                betas[i] = true;
            }
        }
    }

    public void Solve (Input Stream in) {
        readInput (in);

        while (open) {
            if (isSaturated ()) { 
                //imprime e break
                for (int i = 0; i < size; i++) 
                    if (branch[i].tree.size () == 1) 
                        System.out.println (branch[i].getRoot () + " = " + branch[i].value);

                break;
            }
            // Aplica todos os alphas e marca os betas
            applyAlpha ();
            // Aplica um beta e empilha o estado open++;
            applyBeta ();
            if (verify ()) {
                // Desempilha o estado open--;
                Triple last = stack.pop ();
                size = last.size;
                betas = last.betas;
                branch[++size] = last.pair;
                open--;
            }
        }
    }
}

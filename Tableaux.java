import java.util.Stack;

public class Tableaux { 
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

    int open = 1;
    int size;
    Pair[] branch = (Pair[]) new Object[30];
    boolean[] betas = new boolean[10];
    Stack<Triple> stack = new Stack<Triple> (); 

    public void Solve () {
        read_input ();
        
        while (open) {
            if (saturated ()) { 
                //imprime e break
                for (int i = 0; i < size; i++) 
                    if (branch[i].tree.size () == 1) 
                        System.out.println (branch[i].getAtom () + " = " + branch[i].value);

                break;
            }
            // Aplica todos os alphas e marca os betas
            apply_alpha ();
            // Aplica um beta e empilha o estado open++;
            apply_beta ();
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

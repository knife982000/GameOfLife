package life;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();
        //System.out.println(scanner.nextInt());
        //long seed = new Random().nextLong();
        int steps = 11; //scanner.nextInt();
        boolean[][] mat = getInitialState(size);

        for(int i=0; i<steps; i++){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clearScreen();
            evolve(mat);
            System.out.println("Generation # "+(i+1));
            System.out.println("Alive: " + count(mat));
            printState(mat);
        }
    }

    public static int count(boolean[][] mat) {
        int i = 0;
        for (boolean[] b: mat) {
            for (boolean v : b) {
                if (v) {
                    i++;
                }
            }
        }
        return i;
    }

    public static void evolve(boolean[][] mat){
        int counter[][] = new int[mat.length][mat[0].length];
        for (int i=0; i < mat.length; i++) {
            for (int j=0; j < mat[0].length; j++) {
                int count = 0;
                for(int i1=-1; i1 <= 1; i1++) {
                    for(int j1=-1; j1 <= 1; j1++) {
                        if (mat[(mat.length+i+i1)%mat.length][(mat[0].length + j+j1)%mat.length] && !(i1==0 && j1==0))
                            count++;
                    }
                }
                counter[i][j] = count;
            }
        }
        for (int i=0; i < mat.length; i++) {
            for (int j=0; j < mat[0].length; j++) {
                int count = counter[i][j];
                if (mat[i][j] && (count<2 || count>3))
                    mat[i][j] = false;
                else if (!mat[i][j] && count==3)
                    mat[i][j] = true;
            }
        }
    }

    public static void printState(boolean[][] mat) {
        for (int i=0; i < mat.length; i++) {
            for (int j=0; j < mat[0].length; j++) {
                System.out.print(mat[i][j]? 'o':' ');
            }
            System.out.println();
        }
    }

    public static boolean[][] getInitialState(int size, long seed) {
        Random random = new Random(seed);
        boolean[][] mat = new boolean[size][size];
        for (int i=0; i < size; i++) {
            for (int j=0; j < size; j++) {
                mat[i][j] = random.nextBoolean();
            }
        }
        return mat;
    }

    public static boolean[][] getInitialState(int size) {
        Random random = new Random();
        boolean[][] mat = new boolean[size][size];
        for (int i=0; i < size; i++) {
            for (int j=0; j < size; j++) {
                mat[i][j] = random.nextBoolean();
            }
        }
        return mat;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        /*try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        }
        catch (IOException | InterruptedException e) {}*/
    }
}

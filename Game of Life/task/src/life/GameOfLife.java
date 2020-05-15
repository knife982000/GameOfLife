package life;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GameOfLife extends JFrame {
    JLabel generations;
    JLabel alive;
    JSlider slider;
    JPanel data;
    Thread runner;
    AtomicBoolean play = new AtomicBoolean(false);
    boolean[][] mat;
    JToggleButton[][] bmat;
    AtomicInteger gen = new AtomicInteger(0);

    JToggleButton bplay;
    JButton reset;

    public GameOfLife() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);
        getContentPane().setLayout(new BorderLayout());

        data = new JPanel();
        getContentPane().add(data, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));
        getContentPane().add(panel, BorderLayout.WEST);
        generations = new JLabel();
        generations.setName("GenerationLabel");
        generations.setText("Generation #1");
        panel.add(generations);


        alive = new JLabel();
        alive.setName("AliveLabel");
        alive.setText("Alive: 82");
        panel.add(alive);

        slider = new JSlider(5, 100);
        panel.add(slider);
        slider.setValue(10);
        slider.addChangeListener(changeEvent -> restart());

        bplay = new JToggleButton();
        bplay.setName("PlayToggleButton");
        bplay.setText("Play");
        bplay.addActionListener(l->playPause());
        panel.add(bplay);

        reset = new JButton("Reset");
        reset.setName("ResetButton");
        reset.addActionListener(l->restart());
        panel.add(reset);

        restart();
    }

    private void playPause() {
        synchronized (runner) {
            if(play.get()) {
                bplay.setText("Play");
                play.set(false);
            } else {
                bplay.setText("Pause");
                play.set(true);
                runner.notify();
            }
        }
    }

    AtomicBoolean ended = new AtomicBoolean(true);
    private void restart() {
        getContentPane().remove(data);
        data = new JPanel();
        getContentPane().add(data, BorderLayout.CENTER);
        mat = Main.getInitialState(slider.getValue());
        data.setLayout(new GridLayout(slider.getValue(), slider.getValue()));
        bmat = new JToggleButton[slider.getValue()][slider.getValue()];
        for(int i=0; i<slider.getValue(); i++) {
            for(int j=0; j<slider.getValue(); j++) {
                bmat[i][j] = new JToggleButton();
                data.add(bmat[i][j]);
            }
        }
        if(runner!=null){
            runner.interrupt();
        }
        while (!ended.get()) {
            Thread.yield();
        }
        ended.set(false);
        gen.set(0);
        updateShowMat();
        play.set(false);
        bplay.setSelected(false);
        bplay.setText("Play");
        runner = new Thread(()->{
           while (true) {
               synchronized (runner){
                   while (!play.get()){
                       try {
                           runner.wait();
                       } catch (InterruptedException e) {
                           ended.set(true);
                           return;
                       }
                   }
               }
               Main.evolve(mat);
               gen.incrementAndGet();
               SwingUtilities.invokeLater(() -> updateShowMat());
               try {
                   Thread.sleep(100);
               } catch (InterruptedException e) {
                   ended.set(true);
                   return;
               }
           }
        });
        runner.start();
    }

    private void updateShowMat() {
        generations.setText("Generation #" + gen.get());
        alive.setText("Alive: "+Main.count(mat));
        for(int i=0; i<mat.length; i++) {
            for(int j=0; j<mat.length; j++) {
                bmat[i][j].setSelected(mat[i][j]);
            }
        }
    }

    public static void main(String[] args) {
        new GameOfLife();
    }
}

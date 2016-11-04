
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.sound.sampled.*;
import pong2.GameState;
import pong2.HighValues;
import pong2.NameSave;
import pong2.Score;
import pong2.ScoreWindow;

public class Pong2 extends JFrame {
//Line 369, added to save button, save as object

    private static class GamePanel extends JPanel {

        private final int BALL_SIZE = 50;
        private final int PADDLE_WIDTH = 20;
        private final int PADDLE_HEIGHT = 145;

        private int MIN_Y;
        private int MAX_Y;
        private int MIN_X;
        private int MAX_X;

        private int score;
        private String name;

        private int dx = 4, dy = 4;

        private int paddleDY = 26;

        Point paddle = new Point();
        Point ball = new Point();

        private Timer timer;
        private int timerDelay = 5;

        private Rectangle paddleRectangle = new Rectangle();
        private AudioInputStream hitInputSteam, missInputStream;
        private Clip hitClip, missedClip;

        private GameState state;
        private Score gameScore;
        private HighValues scoreList;

        GamePanel() {
            state = new GameState();
            scoreList = new HighValues(10);

            try {
                setupHitAudio();
                setupMissAudio();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(Pong2.class.getName()).log(Level.SEVERE, null, ex);
            }

            MIN_Y = 0;
            MIN_X = 0;
            MAX_Y = 600; //Height
            MAX_X = 797; //Width

            setBackground(Color.black);
            setFocusable(true);

            addMouseWheelListener(new MouseWheelListener() {

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.getWheelRotation() < 0) {
                        paddle.translate(0, -paddleDY);
                    } else if (e.getWheelRotation() > 0) {
                        paddle.translate(0, paddleDY);
                    }
                    if (paddleHitBottom()) {
                        paddle.translate(0, -paddleDY);

                        repaint();
                    }
                    if (paddleHitTop()) {
                        paddle.translate(0, paddleDY);
                    }
                    repaint();

                }
            });

            addKeyListener(new KeyAdapter() {
                //Controls the timer on the space bar
                @Override
                public void keyPressed(KeyEvent e) {

                    if (e.getKeyCode() == 32) {

                        if (timer.isRunning()) {
                            timer.stop();
                        } else {
                            timer.start();

                        }
                    }
                }
            });

            timer = new Timer(timerDelay, new ActionListener() {

                @Override
                public synchronized void actionPerformed(ActionEvent e) {
  
                    ball.translate(dx, dy);

                    if (timer.getDelay() > 2) {  //Reset the timer to normal pace
                        timer.setDelay(timerDelay);//Default is 2
                    }

                    if (ball.y <= MIN_Y || ball.y + BALL_SIZE >= MAX_Y - BALL_SIZE) {
                        dy = -dy;
                    } else if (paddleHitCheck() || ball.x <= MIN_X) {
                        dx = -dx;
                    } else if (ball.x + BALL_SIZE >= MAX_X) {
                        playClip(missedClip);
                        ressetClip(missedClip);
                        ball.move(MIN_X + 5, MIN_Y + 100);
                        timer.setDelay(1000); //Move the ball to left side of window,
                        //Increase the delay(only way to schedule the swing timer)
                    }

                    repaint();
                }
            });
        }

        /**
         * Checks if the ball has collided with the paddle, Updates the SCORE if
         * true. Also plays the audio
         */
        private boolean paddleHitCheck() {
            if (paddleRectangle.intersects(ball.x + 2, ball.y, BALL_SIZE, BALL_SIZE)) {
                ball.x = paddleRectangle.x - BALL_SIZE; // Temporary bug fix for intersection
                playClip(hitClip);
                ressetClip(hitClip);
                updateScore();
                return true;
            }
            return false;
        }

        /**
         * Ensure that the paddles y never goes above the window
         */
        private boolean paddleHitBottom() {
            return paddleRectangle.intersects(MAX_X - paddleRectangle.width,
                    MAX_Y - 70,
                    paddleRectangle.width, 1);
        }

        /**
         * Ensure the paddles bottom location doesnt go below the window
         */
        private boolean paddleHitTop() {
            return paddleRectangle.intersects(MAX_X - paddleRectangle.width,
                    MIN_Y + 16,
                    paddleRectangle.width, 1);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            paddleRectangle.setRect(this.getWidth() - PADDLE_WIDTH - 10,
                    (this.getHeight() - PADDLE_HEIGHT) / 2 + paddle.y,
                    PADDLE_WIDTH, PADDLE_HEIGHT);

            g2.setColor(Color.white);
            g2.fill(paddleRectangle);

            g2.fillOval(ball.x, ball.y, BALL_SIZE, BALL_SIZE);

            g2.setColor(Color.LIGHT_GRAY);

            if (name != null) {//Fix this logic
                g2.scale(5, 5);
                g2.drawString(name, MIN_X + 4, MIN_Y + 20);
            } else {
                g2.scale(10, 10);
            }

            g2.scale(2, 2);

            g2.drawString(getScore(), MIN_X + 20, MIN_Y + 20);
        }

        /**
         * When player has hit the paddle his SCORE is updated
         */
        private void updateScore() {
            score++;
        }

        /**
         * Get the String representation of the SCORE
         */
        private String getScore() {
            Integer i = score;
            return i.toString();
        }

        /**
         * Pause the game timer, stops the ball from moving
         */
        public void pause() {
            timer.stop();
        }

        /**
         * Starts the timer, the ball resumes moving from current position
         */
        public void resume() {
            timer.start();
        }

        private void saveGameObject(String pongObjectFile, String name) throws FileNotFoundException, IOException {

            scoreList.add(score); //When a game is saved, the score is saved to the High Values class.

            state.setBall(ball);
            state.setPaddle(paddle);
            state.setDx(dx);
            state.setDy(dy);

            state.SCORE.setName(name);
            state.SCORE.setScore(score);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pongObjectFile))) {
                oos.writeObject(state);
            }
        }

        private void openGameObject(String pongObjectFile) throws FileNotFoundException, IOException, ClassNotFoundException {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pongObjectFile))) {

                state = (GameState) ois.readObject();
            }
            ball = state.getBall();
            paddle = state.getPaddle();
            name = state.getSCORE().getName();
            score = state.getSCORE().getScore();
            dx = state.getDx();
            dy = state.getDy();
        }

        /**
         * Resets the values to 0, and 4 for the deltas
         */
        public void openResetGame() {
            name = null;
            score = 0;
            ball.x = 0;
            ball.y = 0;
            dx = 4;
            dy = 4;
        }

        /**
         * Set up the audio Clip for when the ball hits paddle
         */
        private void setupHitAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            hitInputSteam = AudioSystem.getAudioInputStream(new File("hitPaddle.wav"));
            hitClip = AudioSystem.getClip();
            hitClip.open(hitInputSteam);
        }

        /**
         * Sets up the audio Clip for when the ball misses the paddle
         */
        private void setupMissAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            missInputStream = AudioSystem.getAudioInputStream(new File("missed.wav"));
            missedClip = AudioSystem.getClip();
            missedClip.open(missInputStream);
        }
//

        private void playClip(Clip c) {
            c.start();
        }

        private void ressetClip(Clip c) {
            c.setFramePosition(0);
        }
    }

    private final GamePanel game;

    private final String pongFile = "pong.txt";
    private final String pongObjectFile = "pongObject.bin";

    private final JButton save;
    private final JButton start;
    private final JButton loadSavedGame;
    private final JButton exit;
    private final JButton restart;
    private final JButton highScores;

    private JMenuBar menuBar;
    private JMenu menu;

    private NameSave saveName;
    private ScoreWindow scoreWindow;
    private Timer timer;

    public Pong2() {
        //scoreWindow = new ScoreWindow();
        saveName = new NameSave();
        highScores = new JButton("Show High Scores");

        restart = new JButton("Restart");
        loadSavedGame = new JButton("Load saved Game");
        save = new JButton("Save");
        start = new JButton("Start");
        exit = new JButton("Exit");

        game = new GamePanel();

        setupListeners();
        setUpMenu();

        setTitle("PONG");
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(game, BorderLayout.CENTER);
        setJMenuBar(menuBar);
        setVisible(true);
    }

    public void setupListeners() {
        highScores.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                scoreWindow = new ScoreWindow();
                String val = "";
                game.scoreList.sortDescending();

                for (Object element : game.scoreList) {
                    val += element.toString() + " ";
                    System.out.println("Values in sorted List " + element.toString());

                }
                scoreWindow.draw(val);

            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                saveName.setVisible(true);
                timer = new Timer(1000, new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String nameRetrieved = saveName.getNameOf();

                        if (nameRetrieved != null) {
                            try {
                                game.saveGameObject(pongObjectFile, nameRetrieved);
                                timer.stop();

                                System.out.println("name retrived");
                            } catch (IOException ex) {
                                System.out.println("Io exception");
                                Logger.getLogger(Pong2.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
                timer.start();
            }
        });

        //Load a game that was saved
        loadSavedGame.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    //  game.loadSavedGame(pongFile);

                    game.openGameObject(pongObjectFile);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Pong2.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(Pong2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        start.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Start")) {
                    game.resume();
                    start.setText("Pause");

                } else {
                    game.pause();
                    start.setText("Start");
                }
            }
        });
        restart.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game.openResetGame();
            }
        });
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowIconified(WindowEvent e) {
                game.pause();
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                start.setText("Start");
            }
        });

        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Exit"));
                System.exit(0);
            }
        });
    }

    private void setUpMenu() {
        menuBar = new JMenuBar();
        menu = new JMenu("Game");

        menu.add(highScores);
        menu.add(loadSavedGame);
        menu.add(restart);
        menu.add(start);
        menu.add(save);

        menu.add(exit);
        menuBar.add(menu);
    }

//    public static void main(String[] args) {
//        new Pong2();
//    }

}

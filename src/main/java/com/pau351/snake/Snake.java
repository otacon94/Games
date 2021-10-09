package com.pau351.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class Snake extends JFrame {

    static class SnakePanel extends JPanel implements ActionListener {
        public static final int TILE_SIZE = 10;
        public static final int SLOW_MODE = 100;
        public static final int SPEED_MODE = 60;
        public static final int INITIAL_PIECES = 5;
        public static final int APPLE_SCORE = 20;
        public static final String TITLE = "SNAKE GAME - SCORE %S";

        private enum Direction {UP, DOWN, LEFT, RIGHT}

        private int dimx = 85, dimy = 159;
        private LinkedList<Point> snakeBody;
        private Direction currentDirection;
        private boolean inGame;
        private Timer gameTimer;
        private int score = 0;
        private Point apple;

        private JFrame parent;

        public SnakePanel(JFrame parent) {
            this.parent = parent;
            initializeSnake();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_LEFT && currentDirection != Direction.RIGHT) {
                        currentDirection = Direction.LEFT;
                    } else if (key == KeyEvent.VK_RIGHT && currentDirection != Direction.LEFT) {
                        currentDirection = Direction.RIGHT;
                    } else if (key == KeyEvent.VK_UP && currentDirection != Direction.DOWN) {
                        currentDirection = Direction.UP;
                    } else if (key == KeyEvent.VK_DOWN && currentDirection != Direction.UP) {
                        currentDirection = Direction.DOWN;
                    } else if (key == KeyEvent.VK_SPACE) {
                        if (inGame)
                            gameTimer.setDelay(SPEED_MODE);
                        else {
                            initializeSnake();
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_SPACE) {
                        gameTimer.setDelay(SLOW_MODE);
                    }
                }
            });
        }

        private void initializeSnake() {
            snakeBody = new LinkedList<>();
            currentDirection = Direction.DOWN;
            inGame = true;
            score = 0;
            for (int i = 0; i < INITIAL_PIECES; i++) {
                snakeBody.add(new Point(100, 100 - (TILE_SIZE * i)));
            }
            gameTimer = new Timer(SLOW_MODE, this);
            gameTimer.start();
            setBackground(Color.black);
            parent.setTitle(String.format(TITLE, score));
            placeApple();
        }

        private void placeApple() {
            apple = new Point((int) (Math.random() * (parent.getWidth())) + TILE_SIZE,
                    (int) (Math.random() * (parent.getHeight())) + TILE_SIZE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (inGame) {
                checkCollision();
                move();
            }
            repaint();
        }

        private void move() {
            for (int i = snakeBody.size() - 1; i > 0; i--) {
                snakeBody.get(i).setLocation(snakeBody.get(i - 1));
            }
            Point head = snakeBody.getFirst();
            switch (currentDirection) {
                case UP:
                    head.y = head.y - TILE_SIZE;
                    if (head.y < 0) {
                        head.y = getHeight();
                    }
                    break;
                case DOWN:
                    head.y = head.y + TILE_SIZE;
                    if (head.y > getHeight()) {
                        head.y = 0;
                    }
                    break;
                case LEFT:
                    head.x = head.x - TILE_SIZE;
                    if (head.x < 0) {
                        head.x = getWidth();
                    }
                    break;
                default:
                    head.x = head.x + TILE_SIZE;
                    if (head.x > getWidth()) {
                        head.x = 0;
                    }
                    break;
            }
        }//muovi

        public boolean isInGame() { return inGame; }

        public void paint(Graphics g) {
            super.paint(g);
            if (inGame) {
                paintApple(g, Color.red);
                for (int z = 0; z < snakeBody.size(); z++) {
                    if (z == 0) {
                        paintSnakeBody(g, z, Color.orange);
                    } else {
                        paintSnakeBody(g, z, Color.white);
                    }
                }
                g.dispose();
            } else {
                Font f = new Font("Helvetica", Font.BOLD, 25);
                g.setFont(f);
                g.setColor(Color.white);
                g.drawString("GAMEOVER", dimx, dimy);
                g.dispose();
            }
        }

        private void paintApple(Graphics g, Color color) {
            g.setColor(color);
            g.drawRect(apple.x, apple.y, TILE_SIZE, TILE_SIZE);
            g.fillRect(apple.x, apple.y, TILE_SIZE, TILE_SIZE);
        }

        private void paintSnakeBody(Graphics g, int index, Color color) {
            g.setColor(color);
            g.drawRect(snakeBody.get(index).x, snakeBody.get(index).y, TILE_SIZE, TILE_SIZE);
            g.fillRect(snakeBody.get(index).x, snakeBody.get(index).y, TILE_SIZE, TILE_SIZE);
        }

        private void checkCollision() {
            Point head = snakeBody.peek();
            for (int i = snakeBody.size() - 1; i >= 0; i--) {
                Point currentPiece = snakeBody.get(i);
                if (i > 3 && head.x == currentPiece.x && head.y == currentPiece.y) {
                    inGame = false;
                    gameTimer.stop();
                }
            }
            Rectangle headRect = new Rectangle(head.x, head.y, TILE_SIZE, TILE_SIZE);
            Rectangle appleRect = new Rectangle(apple.x, apple.y, TILE_SIZE, TILE_SIZE);
            if (headRect.intersects(appleRect)) {
                score += APPLE_SCORE;
                parent.setTitle(String.format(TITLE, score));
                snakeBody.addLast(computeNewPart());
                placeApple();
            }
        }

        private Point computeNewPart() {
            Point last = snakeBody.getLast();
            Point secondLast = snakeBody.get(snakeBody.size() - 1);
            Point newBodyPart = new Point(last);
            if (last.x > secondLast.x)
                newBodyPart.x += TILE_SIZE;
            if (last.x < secondLast.x)
                newBodyPart.x -= TILE_SIZE;
            if (last.y < secondLast.y)
                newBodyPart.y += TILE_SIZE;
            if (last.y < secondLast.y)
                newBodyPart.y -= TILE_SIZE;
            return newBodyPart;
        }
    }

    private SnakePanel snakePanel;

    public Snake() {
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        snakePanel = new SnakePanel(this);
        setContentPane(snakePanel);
        snakePanel.setFocusable(true);
        snakePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Snake();
            }
        });
    }// main

}

package com.pau351.snake;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Snake extends JFrame {

    static class SnakePanel extends JPanel implements ActionListener {
        public static final int TILE_SIZE = 10;

        public static final int RAT_SIZE = 8;
        public static final int RAT_SPEED = 120;
        public static final int RAT_MIN_DIRECTION_CHANGE_TIME = 1000;
        public static final int RAT_MAX_DIRECTION_CHANGE_TIME = 5000;

        public static final int SLOW_MODE = 100;
        public static final int RAT_TIMER = 10000;
        public static final int SPEED_MODE = 60;

        public static final int INITIAL_PIECES = 5;

        public static final int APPLE_SCORE = 20;
        public static final int RAT_SCORE = 80;

        public static final String GAMEOVER = "GAMEOVER";
        public static final String TITLE = "SNAKE GAME - SCORE %S";
        public static final String DAEMON_COMMAND = "daemon";
        public static final String RAT_COMMAND = "rat";
        public static final String RAT_MOVER_COMMAND = "ratMover";

        private enum Direction {UP("UP"), DOWN("DOWN"), LEFT("LEFT"), RIGHT("RIGHT");
            private List<String> possibleMoves;

            Direction(String direction) {
                possibleMoves = new LinkedList<>();
                switch (direction) {
                    case "UP" :
                    case "DOWN":
                        possibleMoves.addAll(Arrays.asList("LEFT", "RIGHT"));
                        break;
                    default:
                        possibleMoves.addAll(Arrays.asList("UP", "DOWN"));
                        break;
                }
            }

            public List<Direction> getPossibleMoves() { return Arrays.stream(Direction.values())
                    .filter(e -> possibleMoves.contains(e.name()))
                    .collect(Collectors.toList());
            }

        }

        private LinkedList<Point> snakeBody;
        private Direction snakeDirection;
        private boolean inGame;
        private Timer gameTimer;
        private Timer ratTimer;
        private Timer ratMoverTimer;
        private int score = 0;
        private Point apple;

        private List<Point> ratBody;
        private Direction ratDirection;
        private boolean ratAlive;

        private Random random;

        private JFrame parent;

        public SnakePanel(JFrame parent) {
            this.parent = parent;
            random = new Random();
            initializeSnake();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_LEFT && snakeDirection != Direction.RIGHT) {
                        snakeDirection = Direction.LEFT;
                    } else if (key == KeyEvent.VK_RIGHT && snakeDirection != Direction.LEFT) {
                        snakeDirection = Direction.RIGHT;
                    } else if (key == KeyEvent.VK_UP && snakeDirection != Direction.DOWN) {
                        snakeDirection = Direction.UP;
                    } else if (key == KeyEvent.VK_DOWN && snakeDirection != Direction.UP) {
                        snakeDirection = Direction.DOWN;
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
            snakeDirection = Direction.DOWN;
            inGame = true;
            score = 0;
            for (int i = 0; i < INITIAL_PIECES; i++) {
                snakeBody.add(new Point(100, 100 - (TILE_SIZE * i)));
            }
            gameTimer = new Timer(SLOW_MODE, this);
            gameTimer.setActionCommand(DAEMON_COMMAND);
            gameTimer.start();
            initializeRatTimer();
            setBackground(Color.black);
            parent.setTitle(String.format(TITLE, score));
            placeApple();
        }

        private void initializeRatTimer() {
            ratTimer = new Timer(RAT_TIMER, this);
            ratTimer.setActionCommand(RAT_COMMAND);
            ratTimer.start();
        }

        private void placeApple() {
            apple = new Point(random.nextInt(parent.getWidth() - (TILE_SIZE*2)) + TILE_SIZE,
                    random.nextInt(parent.getHeight() - (TILE_SIZE*2)) + TILE_SIZE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (inGame) {
                if (DAEMON_COMMAND.equals(e.getActionCommand())) {
                    checkCollision();
                    move(snakeBody, TILE_SIZE, snakeDirection);
                    if (ratAlive) {
                        move(ratBody, RAT_SIZE, ratDirection);
                    }
                } else if (RAT_COMMAND.equals(e.getActionCommand())) {
                    if (!ratAlive) {
                        ratBody = new LinkedList<>();
                        ratAlive = true;
                        ratMoverTimer = new Timer(RAT_MAX_DIRECTION_CHANGE_TIME, this);
                        ratMoverTimer.setActionCommand(RAT_MOVER_COMMAND);
                        ratMoverTimer.start();
                        placeRat();
                    }
                } else if (RAT_MOVER_COMMAND.equals(e.getActionCommand())) {
                    ratMoverTimer.setDelay(random.nextInt(RAT_MAX_DIRECTION_CHANGE_TIME - RAT_MIN_DIRECTION_CHANGE_TIME)
                            + RAT_MIN_DIRECTION_CHANGE_TIME);
                    boolean changeDirection = random.nextBoolean();
                    if (changeDirection) {
                        Collections.shuffle(ratDirection.possibleMoves);
                        ratDirection = ratDirection.getPossibleMoves().get(0);
                    }
                }
            }
            repaint();
        }

        private void placeRat() {
            ratDirection = Direction.values()[random.nextInt(Direction.values().length)];
            Point ratHead, ratTail;
            boolean bodyOverlap = true;
            do {
                bodyOverlap = true;
                ratHead = new Point(random.nextInt(parent.getWidth() - (RAT_SIZE * 2)) + (RAT_SIZE * 2),
                        random.nextInt(parent.getHeight() - (RAT_SIZE * 2)) + (RAT_SIZE * 2));
                ratTail = new Point();
                for (Direction d : Direction.values()) {
                    if (d == Direction.UP) {
                        ratTail = new Point(ratHead.x, ratHead.y + RAT_SIZE);
                        if (!snakeOverlap(ratTail)) {
                            bodyOverlap = false;
                            break;
                        }
                    }
                    if (d == Direction.DOWN) {
                        ratTail = new Point(ratHead.x, ratHead.y - RAT_SIZE);
                        if (!snakeOverlap(ratTail)) {
                            bodyOverlap = false;
                            break;
                        }
                    }
                    if (d == Direction.LEFT) {
                        ratTail = new Point(ratHead.x + RAT_SIZE, ratHead.y);
                        if (!snakeOverlap(ratTail)) {
                            bodyOverlap = false;
                            break;
                        }
                    }
                    if (d == Direction.RIGHT) {
                        ratTail = new Point(ratHead.x - RAT_SIZE, ratHead.y);
                        if (!snakeOverlap(ratTail)) {
                            bodyOverlap = false;
                            break;
                        }
                    }
                }
            } while (!snakeOverlap(ratHead) || bodyOverlap);
            ratBody.add(ratHead);
            ratBody.add(ratTail);
        }

        private boolean snakeOverlap(Point point) {
            Rectangle rat = new Rectangle(point.x, point.y, RAT_SIZE, RAT_SIZE);
            for (Point snake : snakeBody) {
                if (rat.intersects(new Rectangle(snake.x, snake.y, TILE_SIZE, TILE_SIZE))) {
                    return true;
                }
            }
            return false;
        }

        private void move(List<Point> body, int bodySize, Direction direction) {
            for (int i = body.size() - 1; i > 0; i--) {
                body.get(i).setLocation(body.get(i - 1));
            }
            Point head = body.get(0);
            switch (direction) {
                case UP:
                    head.y = head.y - bodySize;
                    if (head.y < 0) {
                        head.y = getHeight() - bodySize;
                    }
                    break;
                case DOWN:
                    head.y = head.y + bodySize;
                    if (head.y > getHeight()) {
                        head.y = bodySize;
                    }
                    break;
                case LEFT:
                    head.x = head.x - bodySize;
                    if (head.x < 0) {
                        head.x = getWidth() - bodySize;
                    }
                    break;
                default:
                    head.x = head.x + bodySize;
                    if (head.x > getWidth()) {
                        head.x = bodySize;
                    }
                    break;
            }
        }//muovi

        public boolean isInGame() { return inGame; }

        public void paint(Graphics g) {
            super.paint(g);
            if (inGame) {
                paintRect(g, apple, Color.red);
                for (int i = 0; i < snakeBody.size(); i++) {
                    Color color = Color.white;
                    if (i == 0) {
                        color = Color.orange;
                    }
                    paintRect(g, snakeBody.get(i), color);
                }
                if (ratAlive) {
                    for (int i = 0; i < ratBody.size(); i++) {
                        paintCircle(g, ratBody.get(i), Color.PINK);
                    }
                }
            } else {
                Font f = new Font("Helvetica", Font.BOLD, 25);
                g.setFont(f);
                g.setColor(Color.white);
                g.drawString(GAMEOVER,
                        (getWidth() / 2) - (g.getFontMetrics().stringWidth(GAMEOVER) / 2),
                        getHeight() / 2);
            }
            g.dispose();
        }

        private void paintRect(Graphics g, Point point, Color color) {
            g.setColor(color);
            g.drawRect(point.x, point.y, TILE_SIZE, TILE_SIZE);
            g.fillRect(point.x, point.y, TILE_SIZE, TILE_SIZE);
        }

        private void paintCircle(Graphics g, Point point, Color color) {
            g.setColor(color);
            g.drawOval(point.x, point.y, RAT_SIZE, RAT_SIZE);
            g.fillOval(point.x, point.y, RAT_SIZE, RAT_SIZE);
        }

        private void checkCollision() {
            Point head = snakeBody.peek();
            for (int i = snakeBody.size() - 1; i >= 0; i--) {
                Point currentPiece = snakeBody.get(i);
                if (i > 3 && head.x == currentPiece.x && head.y == currentPiece.y) {
                    gameOver();
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
            if (ratAlive) {
                boolean intersectAny = false;
                for (Point p : ratBody) {
                    if (headRect.intersects(new Rectangle(p.x, p.y, RAT_SIZE, RAT_SIZE))) {
                        intersectAny = true;
                        break;
                    }
                }
                if (intersectAny) {
                    ratAlive = false;
                    ratTimer.stop();
                    initializeRatTimer();
                    score += RAT_SCORE;
                    parent.setTitle(String.format(TITLE, score));
                    ratBody.clear();
                    snakeBody.addLast(computeNewPart());
                }
            }
        }

        private void gameOver() {
            inGame = false;
            gameTimer.stop();
            ratTimer.stop();
            ratAlive = false;
            ratBody.clear();
            ratMoverTimer.stop();
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
        setSize(600, 500);
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

package com.letflo.tictactoe;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TicTacToeGUI extends JFrame {

    public static final Border GRAY_BORDER = BorderFactory.createLineBorder(Color.DARK_GRAY);
    public static final Border NORMAL_BORDER = new JPanel().getBorder();
    public static final String CURRENT_PLAYER = "TicTacToe - Current Player: %s";
    public static final String WINNER_STRING = "WINNER: %s";

    public static final int OFFSET_X = 54;
    public static final int OFFSET_Y = 40;
    public static final int PANEL_WIDTH = 130;
    public static final int PANEL_HEIGHT = 130;

    private JPanel mainPanel;

    private TicTacToe game;
    private PlayPanel board[][];
    private Player.Type currentPlayer;

    public TicTacToeGUI() {
        initializeFrame();
        setVisible(true);
        setFocusable(true);
        setEnabled(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 500);
    }

    void initializeFrame() {
        mainPanel = new CustomPanel();
        game = new TicTacToe();
        board = new PlayPanel[3][3];
        currentPlayer = Player.Type.X;
        setTitle(String.format(CURRENT_PLAYER, currentPlayer));

        mainPanel.setLayout(new GridBagLayout());
        mainPanel.addMouseListener(new MouseAdapter() {});
        mainPanel.setEnabled(true);
        mainPanel.setBorder(GRAY_BORDER);
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.fill = GridBagConstraints.BOTH;
        layoutConstraints.ipadx = 120;
        layoutConstraints.ipady = 120;
        for (int i = 0; i < 3; i++) {
            layoutConstraints.gridx = i;
            for (int j = 0; j < 3; j++) {
                final int pos = (i * 3) + j;
                layoutConstraints.gridy = j;
                PlayPanel panel = new PlayPanel();
                panel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {
                        play(e);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        JPanel parent = (JPanel) e.getSource();
                        parent.setBorder(GRAY_BORDER);
                        mainPanel.revalidate();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        JPanel parent = (JPanel) e.getSource();
                        parent.setBorder(NORMAL_BORDER);
                        mainPanel.revalidate();
                    }

                    void play(MouseEvent e) {
                        if (!game.isEndGame()) {
                            PlayPanel parent = (PlayPanel) e.getSource();
                            System.out.println(String.format("Width: %s", panel.getWidth()));
                            System.out.println(String.format("Height: %s", panel.getHeight()));
                            System.out.println(String.format("X: %s", panel.getX()));
                            System.out.println(String.format("Y: %s", panel.getY()));
                            parent.setType(currentPlayer);
                            game.place(currentPlayer, pos);
                            checkWin();
                            if (!game.isEndGame()) {
                                nextPlayer();
                                setTitle(String.format(CURRENT_PLAYER, currentPlayer));
                            }
                            mainPanel.repaint();
                        } else {
                            clearBoard();
                        }
                    }

                    void checkWin() {
                        game.checkWin(currentPlayer);
                        if (game.isEndGame()) {
                            setTitle(String.format(WINNER_STRING, game.getWinner()));
                        }
                    }

                });
                board[i][j] = panel;
                mainPanel.add(panel, layoutConstraints);
            }
        }
        setContentPane(mainPanel);
        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();
    }

    void clearBoard() {
        game.clear();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j].setType(null);
                board[i][j].repaint();
            }
        }
        mainPanel.repaint();
        setTitle(String.format(CURRENT_PLAYER, currentPlayer));
    }

    class PlayPanel extends JPanel {
        private com.letflo.tictactoe.Player.Type type;

        public PlayPanel() {}

        public com.letflo.tictactoe.Player.Type getType() { return type; }

        public void setType(com.letflo.tictactoe.Player.Type type) { this.type = type; }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (type != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(10));
                if (type == Player.Type.X) {
                    g.setColor(Color.ORANGE);
                    g.drawLine(20, 20, 110, 110);
                    g.drawLine(20, 110, 110, 20);
                } else {
                    g.setColor(Color.RED);
                    g2.drawOval(20, 20, 90, 90);
                }
            }
        }
    }

    void nextPlayer() {
        if (currentPlayer == Player.Type.X) {
            currentPlayer = Player.Type.O;
        } else {
            currentPlayer = Player.Type.X;
        }
    }


    class CustomPanel extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (!game.isEndGame()) {
                g.setColor(Color.blue);
                //draw horizontal
                g.drawLine(OFFSET_X, OFFSET_Y + PANEL_HEIGHT,
                        getWidth() - OFFSET_X, OFFSET_Y + PANEL_HEIGHT);
                g.drawLine(OFFSET_X, OFFSET_Y + (PANEL_HEIGHT * 2),
                        getWidth() - OFFSET_X, OFFSET_Y + (PANEL_HEIGHT * 2));
                //draw vertical
                g.drawLine(PANEL_WIDTH + OFFSET_X, OFFSET_Y,
                        PANEL_WIDTH + OFFSET_X, (PANEL_WIDTH * 3) + OFFSET_Y);
                g.drawLine((PANEL_WIDTH * 2) + OFFSET_X, OFFSET_Y,
                        (PANEL_WIDTH * 2) + OFFSET_X, (PANEL_WIDTH * 3) + OFFSET_Y);
            } else {
                Font f = new Font("Helvetica", Font.BOLD, 25);
                g.setFont(f);
                g.setColor(Color.black);
                String text = String.format(WINNER_STRING, currentPlayer);
                g.drawRect(OFFSET_X, OFFSET_Y,
                        (PANEL_WIDTH * 3), (PANEL_HEIGHT * 3));
                g.fillRect(OFFSET_X, OFFSET_Y,
                        (PANEL_WIDTH * 3), (PANEL_HEIGHT * 3));
                g.setColor(Color.white);
                g.drawString(String.format(WINNER_STRING, currentPlayer),
                        (getWidth() / 2) - (g.getFontMetrics().stringWidth(text) / 2),
                        (getHeight() / 2));

            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TicTacToeGUI gui = new TicTacToeGUI();
            }
        });
    }

}

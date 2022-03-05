import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Random;
public class PingPong extends JPanel implements KeyListener, ActionListener {
    public static PingPong myPanel;
    public static int width = 600, height = 400;
    Player player1, player2;
    Ball ball;
    public boolean bot = false, selectingDifficulty;
    public boolean w, s, up, down;
    public int gameStatus = 0, scoreLimit = 3, playerWon; //0 = Menu, 1 = Paused, 2 = Playing
    public int botDifficulty, botMoves, botControl = 0;
    public Random random;
    public PingPong() {
        Timer timer = new Timer(20, this);
        timer.start();
    }
    public static void main(String[] args) {
        myPanel = new PingPong();
        JFrame window = new JFrame("Ping Pong");
        window.setSize(width + 15, height + 35);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.addKeyListener(myPanel);
        window.add(myPanel);
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        this.draw2D((Graphics2D) g);
    }
    public void start() {
        gameStatus = 2;
        player1 = new Player(this, 1);
        player2 = new Player(this, 2);
        ball = new Ball(this);
    }
    public void update() {
        if (player1.score >= scoreLimit) {
            playerWon = 1;
            gameStatus = 3;
        }
        if (player2.score >= scoreLimit) {
            gameStatus = 3;
            playerWon = 2;
        }
        if (w)
            player1.move(true);
        if (s)
            player1.move(false);
        if (!bot) {
            if (up)
                player2.move(true);
            if (down)
                player2.move(false);
        }
        else {
            if (botControl > 0) {
                botControl--;
                if (botControl == 0)
                    botMoves = 0;
            }
            if (botMoves < 10) {
                if (player2.y + player2.height / 2 < ball.y) {
                    player2.move(false);
                    botMoves++;
                }
                if (player2.y + player2.height / 2 > ball.y) {
                    player2.move(true);
                    botMoves++;
                }
                if (botDifficulty == 0)
                    botControl = 20;
                if (botDifficulty == 1)
                    botControl = 15;
                if (botDifficulty == 2)
                    botControl = 10;
            }
        }
        ball.move(player1, player2);
    }
    public void draw2D(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (gameStatus == 0) {
            g.setColor(Color.BLUE);
            g.setFont(new Font("Calibri", 1, 40));
            g.drawString("PING PONG", width/2 - 110, 50);
            if (!selectingDifficulty) {
                g.setColor(Color.RED);
                g.setFont(new Font("Calibri", 1, 25));
                g.drawString("Press Space to Play", width/2 - 110, height/2 - 25);
                g.drawString("Press Shift to Play with Bot", width/2 - 160, height/2 + 25);
                g.drawString("<< Score Limit: " + scoreLimit + " >>", width/2 - 110, height/2 + 75);
            }
        }
        if (selectingDifficulty) {
            String string = botDifficulty == 0 ? "Easy" : (botDifficulty == 1 ? "Medium" : "Hard");
            g.setColor(Color.RED);
            g.setFont(new Font("Calibri", 1, 25));
            g.drawString("<< Bot Difficulty: " + string + " >>", width/2 - 149, height/2 - 25);
            g.drawString("Press Space to Play", width/2 - 118, height/2 + 25);
        }
        if (gameStatus == 1) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Calibri", 1, 25));
            g.drawString("PAUSED", width/2 - 42, height/2 - 25);
        }
        if (gameStatus == 1 || gameStatus == 2) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2f));
            g.drawLine(width/2, 0, width/2, height);
            g.setStroke(new BasicStroke(2f));
            g.drawOval(width/2 - 75, height/2 - 75, 150, 150);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Calibri", 1, 18));
            g.drawString("Player(1) ", width/2 - 115, 50);
            g.drawString("Player(2) ", width/2 + 40, 50);
            g.setColor(Color.GREEN);
            g.setFont(new Font("Calibri", 1, 18));
            g.drawString(String.valueOf(player1.score), width/2 - 87, 80);
            g.drawString(String.valueOf(player2.score), width/2 + 70, 80);
            player1.draw(g, 8, 85);
            player2.draw(g, 8, 85);
            ball.draw(g, 13, 13);
        }
        if (gameStatus == 3) {
            g.setColor(Color.BLUE);
            g.setFont(new Font("Calibri", 1, 40));
            g.drawString("PING PONG", width/2 - 100, 50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Calibri", 1, 32));
            if (bot)
                if (player2.score > player1.score)
                    g.drawString("The Bot Wins!", width/2 - 100, 172);
                else
                    g.drawString("You Win!", width/2 - 58, 172);
            else
                g.drawString("Player " + playerWon + " Wins!", width/2 - 95, 172);
            g.setFont(new Font("Calibri", 1, 25));
            g.drawString("Press Space to Play Again", width/2 - 140, height/2 + 30);
            g.drawString("Press ESC for Menu", width/2 - 101, height/2 + 75);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStatus == 2)
            update();
        this.repaint();
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int id = e.getKeyCode();
        if (id == KeyEvent.VK_W)
            w = true;
        else if (id == KeyEvent.VK_S)
            s = true;
        else if (id == KeyEvent.VK_UP)
            up = true;
        else if (id == KeyEvent.VK_DOWN)
            down = true;
        else if (id == KeyEvent.VK_RIGHT) {
            if (selectingDifficulty) {
                if (botDifficulty < 2)
                    botDifficulty++;
                else
                    botDifficulty = 0;
            }
            else if (gameStatus == 0)
                scoreLimit++;
        }
        else if (id == KeyEvent.VK_LEFT) {
            if (selectingDifficulty) {
                if (botDifficulty > 0)
                    botDifficulty--;
                else
                    botDifficulty = 2;
            }
            else if (gameStatus == 0 && scoreLimit > 1)
                scoreLimit--;
        }
        else if (id == KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3))
            gameStatus = 0;
        else if (id == KeyEvent.VK_SHIFT && gameStatus == 0) {
            bot = true;
            selectingDifficulty = true;
        }
        else if (id == KeyEvent.VK_SPACE) {
            if (gameStatus == 0 || gameStatus == 3) {
                if (!selectingDifficulty)
                    bot = false;
                else
                    selectingDifficulty = false;
                start();
            }
            else if (gameStatus == 1)
                gameStatus = 2;
            else if (gameStatus == 2)
                gameStatus = 1;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int id = e.getKeyCode();
        if (id == KeyEvent.VK_W)
            w = false;
        else if (id == KeyEvent.VK_S)
            s = false;
        else if (id == KeyEvent.VK_UP)
            up = false;
        else if (id == KeyEvent.VK_DOWN)
            down = false;
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
}
class Drawable {
    public int x, y;
    public void draw(Graphics g, int width, int height) {
        g.fillRect(this.x, this.y, width, height);
    }
    public int checkCollision (Player player, int x, int y, int width, int height) {
        if (x < player.x + player.width && x + width > player.x && y < player.y + player.height && y + height > player.y)
            return 1; //bounce
        else if ((player.x > x && player.playerNumber == 1) || (player.x < x - width && player.playerNumber == 2))
            return 2; //score
        return 0; //nothing
    }
}
class Player extends Drawable {
    public int x, y, width = 8, height = 85;
    public int playerNumber, speed = 15;
    public int score;
    public Player(PingPong myPanel, int playerNumber) {
        this.playerNumber = playerNumber;
        if (playerNumber == 1) {
            this.x = 7;
        }
        if (playerNumber == 2) {
            this.x = PingPong.width - width - 9;
        }
        this.y = PingPong.height / 2 - this.height / 2;
    }
    @Override
    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.BLUE);
        g.fillRect(this.x, this.y, width, height);
    }
    public void move(boolean up) {
        if (up) {
            if (y - speed > 0)
                y -= speed;
            else
                y = 0;
        }
        else {
            if (y + height + speed < PingPong.height)
                y += speed;
            else
                y = PingPong.height - height;
        }
    }
}
class Ball extends Drawable {
    int x, y, width, height, speedX, speedY, amountOfHits;
    private Random random;
    private PingPong myPanel;
    public Ball(PingPong myPanel) {
        this.myPanel = myPanel;
        this.random = new Random();
        kickOff();
    }
    public void move(Player player1, Player player2) {
        int speed = 5;
        this.x += speedX * speed;
        this.y += speedY * speed;
        if (this.y + height - speedY > PingPong.height || this.y + speedY < 0) {
            if (this.speedY < 0) {
                this.y = 0;
                this.speedY = random.nextInt(4);
            }
            else {
                this.speedY = -random.nextInt(4);
                this.y = PingPong.height - height;
                if (speedY == 0)
                    speedY = -1;
            }
        }
        if (checkCollision(player1, x, y, width, height) == 1) {
            this.speedX = 1 + (amountOfHits/5);
            this.speedY = -2 + random.nextInt(4);
            if (speedY == 0)
                speedY = 1;
            amountOfHits++;
        }
        else if (checkCollision(player2, x, y, width, height) == 1) {
            this.speedX = -1 - (amountOfHits/5);
            this.speedY = -2 + random.nextInt(4);
            if (speedY == 0)
                speedY = 1;
            amountOfHits++;
        }
        if (checkCollision(player1, x, y, width, height) == 2) {
            player2.score++;
            kickOff();
        }
        else if (checkCollision(player2, x, y, width, height) == 2) {
            player1.score++;
            kickOff();
        }
    }
    private void kickOff() {
        this.amountOfHits = 0;
        this.x = PingPong.width / 2 - this.width / 2;
        this.y = PingPong.height / 2 - this.height / 2;
        this.speedY = -2 + random.nextInt(4);
        if (speedY == 0)
            speedY = 1;
        if (random.nextBoolean())
            speedX = 1;
        else
            speedX = -1;
    }
    @Override
    public void draw(Graphics g, int width, int height) {
        g.setColor(Color.YELLOW);
        g.fillOval(this.x, this.y, width, height);
        this.width = width;
        this.height = height;
    }
}

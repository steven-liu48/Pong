import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

public class LobPongGame extends JFrame implements KeyListener, MouseMotionListener, ActionListener{
	private static final long serialVersionUID = 1L;
	
	//Run the game
	public static void main(String[] args) {
		LobPongGame a = new LobPongGame();
		a.setVisible(true);
	}
	
	//Panels and Window
	private JPanel subpanel1 = new JPanel();
	private JPanel subpanel2 = new JPanel();
	private JLabel scoreLabel = new JLabel();
	private JLabel livesLabel = new JLabel();
	private JLabel timerLabel = new JLabel();
	private JLabel toNextLevelLabel = new JLabel();
	private JLabel levelLabel = new JLabel();
	private Canvas canvas = new Canvas();
	private int WindowWidth = 800;
	private int WindowHeight = 800;

	//Game status
	private boolean lose = false;
	private int level = 1;
	private double time = 0;
	private int score = 0;
	private int lives = 3;
	
	//Paddle settings
	private final int PaddleHeight = 25;
	private int PaddleWidth = 200;
	private int PaddleX = WindowWidth/2 - PaddleWidth/2;
	private int PaddleY = WindowHeight - 200;
	int PaddleDirection = 0;
	double previous = 0;//Previous direction used in MouseMotionListener
	
	//Ball settings
	private int diameter = 22;
	private final double g = 9.8;
	private double v0 = 30;//Initial velocity
	private int angle = (int)(Math.random() * 120 + 30); //random initial angle
	private double vx = (v0 * Math.cos(Math.toRadians(angle)));;//horizontal speed
	private double vy = (v0 * Math.sin(Math.toRadians(angle)) - g * time);;//vertical speed
	private double x = WindowWidth/2 - diameter/2, y = WindowHeight - 300;//x and y coordinate
	private double x0 = WindowWidth/2 - diameter/2, y0 = WindowHeight - 300;//starting points
	private double dx, dy;//DeltaX and DeltaY

	//Bonus Box settings
	private int BoxX = (int) ((WindowWidth - 40) * Math.random());
	private int BoxY = (int) ((WindowHeight - PaddleY) * Math.random());;
	private int BoxHeight = 45;
	private int BoxWidth = 45;
	
	//Timer and time settings
	Timer ballTimer;
	Timer countDownTimer;
	double timeLeft = 10000;//10s for each level
	SimpleDateFormat date;

	//Constructor
	public LobPongGame() {

		//Window and Canvas
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		add(subpanel1, BorderLayout.NORTH);
		add(subpanel2, BorderLayout.SOUTH);
		canvas.setPreferredSize(new Dimension(WindowWidth, WindowHeight));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);//Not resizable
		setTitle("Lob Pong Game");

		//Labels
		scoreLabel.setText("Press SPACE to start the game");
		scoreLabel.setForeground(Color.WHITE);
		livesLabel.setForeground(Color.WHITE);
		subpanel1.setBackground(Color.GRAY);
		subpanel1.setPreferredSize(new Dimension(WindowWidth, 30));
		subpanel1.setLayout(new FlowLayout());
		subpanel1.add(scoreLabel);
		subpanel1.add(livesLabel);

		//Timer ActionListener
		countDownTimer = new Timer(100, this);
		subpanel2.setLayout(new FlowLayout());
		subpanel2.setBackground(Color.BLACK);
		subpanel2.add(levelLabel);
		levelLabel.setForeground(Color.WHITE);
		subpanel2.add(toNextLevelLabel);
		toNextLevelLabel.setForeground(Color.WHITE);
		subpanel2.add(timerLabel);
		timerLabel.setForeground(Color.WHITE);
		toNextLevelLabel.setText("Time left to next level: ");
		
		//Add timer
		ballTimer = new Timer(10, this);

		this.addKeyListener(this);
		
		this.addMouseMotionListener(this);
		
		//Pack everything
		pack();

	}
	
	//Paint canvas
	protected class Canvas extends JComponent {
		private static final long serialVersionUID = 1L;

		public void paint(Graphics g) {
			//Background Color
			getContentPane().setBackground(new Color(36, 54, 123));
			//Losing
			if (lose) {
				ballTimer.stop();
				countDownTimer.stop();
				g.setColor(Color.WHITE);
				g.drawString("Good Game!", 300, 200);
				g.drawString("Your Total Score: " + score, 300, 250);
				g.drawString("Difficulty Level " + level, 300, 300);
			}
			//Draws everything
			else {
				g.setColor(Color.YELLOW);
				g.fillOval((int) x, (int) y, diameter, diameter);
				g.setColor(Color.RED);
				g.fillRect(PaddleX, PaddleY, PaddleWidth, PaddleHeight);
				g.setColor(Color.BLACK);
				g.fillRect(BoxX, BoxY, BoxHeight, BoxWidth);
				g.setColor(Color.YELLOW);
				g.fillRect(BoxX + 6, BoxY + 6, BoxHeight - 12, BoxWidth - 12);
				g.setColor(Color.BLACK);
				g.setFont(new Font("TimesRoman", Font.BOLD, 25));
				g.drawString("?", BoxX + 16, BoxY + 30);
			}
		}
	}
	


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//ActionListener for timer
		if(e.getSource() == countDownTimer) {
			//Label for time countdown
			timeLeft -= 100;
			date = new SimpleDateFormat("mm:ss:S");
			timerLabel.setText(date.format(timeLeft));
			levelLabel.setText("Current Level: " + level + "  ");

			//When time is up, continue to next level
			if (timeLeft <= 0) {
				countDownTimer.stop();
				//Level number increase
				level++;
				//Reset time left to 10 seconds
				timeLeft = 10000;
				//Shorten width to increase difficulty
				PaddleWidth = PaddleWidth * 19/20;
				//Increase velocity to increase difficulty
				v0 = v0 * 1.1;
				livesLabel.setForeground(Color.WHITE);
				livesLabel.setText("      Press SPACE to continue to next Level");
				ballTimer.stop();
				//Random launch angle
				angle = (int)(Math.random() * 120 + 30);
				//Reset everything
				vx = (v0 * Math.cos(Math.toRadians(angle)));
				vy = (v0 * Math.sin(Math.toRadians(angle)) - g * time);
				dx = (vx * time);
				dy = ((vy + g * time) * time - 0.5 * g * Math.pow(time, 2));
				time = 0;
				x = WindowWidth / 2 - diameter/2;
				y = WindowHeight - 300;
				x0 = WindowWidth / 2 - diameter/2;
				y0 = WindowHeight - 300;
				PaddleX = WindowWidth/2 - PaddleWidth/2;
				PaddleY = WindowHeight - 200;
				repaint();
			}
		}
		//ActionListener for ball
		else if(e.getSource() == ballTimer) {
			//Score and life when ball is moving
			if (ballTimer.isRunning()) {
				scoreLabel.setText("Score: " + score);
				livesLabel.setForeground(Color.WHITE);
				if (lives == 0) {
					livesLabel.setText("       Lives left: " + lives + "   Last Chance!");
				} else {
					livesLabel.setText("       Lives left: " + lives);
				}
			}
			
			//Parabola calculation
			time += 0.01;
			vy = vy - g * time;
			dx = vx * 0.01;
			dy = ((vy + g * 0.01) * 0.01 - 0.5 * g * Math.pow(0.01, 2));
			x += dx;
			y -= dy;

			//Hit left/right boundary
			if (x <= 0 || x >= WindowWidth - diameter) {
				x0 = x;
				y0 = y;
				vx = -vx;
			}

			//Hit top boundary
			if (y <= 0) {
				x0 = x;
				y0 = y;
				vy = - vy;
			}
			
			//Hit the paddle
			if ((y >= PaddleY - diameter && y <= PaddleY + diameter) && x >= PaddleX - diameter/2
					&& x <= PaddleX + PaddleWidth - diameter/2) {
				x0 = x;
				y0 = y;		
				/* EXTRA CREDIT - Every time you hit the ball, there is an increase of random upward velocity 
				 *to simulate the real Lob Pong. Also, when you move the paddle to left/right, 
				 *the ball will bounce according to that direction to simulate the real Lob Pong.
				 */
				vy = Math.random() * 1000 + 100;
				if(PaddleDirection == -1) {
					vx += Math.random() * 200;
				}
				else if(PaddleDirection == 1) {
					vx -= Math.random() * 200;
				}
				time = 0;
				score++;
			}
			
			//EXTRA CREDIT - When you hit the bonus box, you get a random number of bonus points.
			if(x >= BoxX - diameter / 2 - 3 && x <= BoxX + BoxWidth - diameter / 2 + 3 
					&& y <= BoxY + BoxHeight - diameter / 2 + 3 && y >= BoxY - diameter / 2 - 3) {
				BoxX = (int) (WindowWidth * Math.random());
				BoxY = (int) ((WindowHeight - PaddleY) * Math.random());;
				BoxHeight = 45;
				BoxWidth = 45;
				score = (int) (score + Math.random() * 10 + 3);
			}
			
			//Lose life
			if (y >= WindowHeight) {
				livesLabel.setText("   Life lost! Press SPACE to continue.");
				ballTimer.stop();
				countDownTimer.stop();
				//Lost
				if (lives == 0) {
					lose = true;
					livesLabel.setText("");
				} 
				//Not lost, relaunch
				else if (lives > 0) {
					lives--;
					//Reset everything
					v0 = Math.abs(v0);
					angle = (int)(Math.random() * 120 + 30);
					vx = (v0 * Math.cos(Math.toRadians(angle)));
					vy = (v0 * Math.sin(Math.toRadians(angle)) - g * time);
					dx = (vx * time);
					dy = ((vy + g * time) * time - 0.5 * g * Math.pow(time, 2));
					time = 0;
					x = WindowWidth / 2;
					y = WindowHeight - 300;
					x0 = WindowWidth / 2;
					y0 = WindowHeight - 300;
					PaddleX = WindowWidth/2 - PaddleWidth/2;
					PaddleY = WindowHeight - 200;
					repaint();
				}
			}
			
			//Repaint again
			repaint();
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		PaddleX = e.getX() - PaddleWidth/2;
		/* EXTRA CREDIT - When you move the paddle to left/right, 
		 *the ball will bounce according to that direction to simulate the real Lob Pong.
		 */
		if (PaddleX > previous) {
			PaddleDirection = -1;
			previous = PaddleX;
		}
		else if (PaddleX < previous){
			PaddleDirection = 1;
			previous = PaddleX;
		}
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		PaddleX = e.getX() - PaddleWidth/2;
		/* EXTRA CREDIT - When you move the paddle to left/right, 
		 *the ball will bounce according to that direction to simulate the real Lob Pong.
		 */
		if (PaddleX > previous) {
			PaddleDirection = -1;
			previous = PaddleX;
		}
		else if (PaddleX < previous){
			PaddleDirection = 1;
			previous = PaddleX;
		}
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//Moving left
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (PaddleX > 0)
				PaddleX -= 40;
				PaddleDirection = 1;
		} 
		//Moving right
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (PaddleX < WindowWidth - PaddleWidth)
				PaddleX += 40;
				PaddleDirection = -1;
		}
		//Start game by pressing SPACE
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			ballTimer.start();
			countDownTimer.start();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
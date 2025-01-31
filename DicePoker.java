
                       		//FARIDAH OLAMIDE AGBAJE

                               import javax.imageio.ImageIO;
                               import java.util.Scanner;
                               import java.util.*;
                               import java.util.List;
                               import java.io.*;
                               import java.awt.*;
                               import java.awt.image.BufferedImage;
                               import java.awt.event.ActionEvent;
                               import java.awt.event.ActionListener;
                               import javax.swing.*;
                               import javax.swing.Timer;
                               import javax.swing.table.DefaultTableModel;
                               import java.util.Collections;
                               
                               public class DicePoker extends JFrame implements Serializable {
                                   private static final long serialVersionUID = 1L;
                                   // Game variables
                                   private int accountbalance; // Current balance of the player
                                   private JTextField betSpace; // Text field for entering the bet amount
                                   private int attempts; // Number of attempts left
                                   private JLabel balanceLabel; // Label to display the account balance
                                   private JLabel diceLabel1; // Label to display the first dice image
                                   private JLabel diceLabel2; // Label to display the second dice image
                                   private JLabel resultLabel; // Label to display the result of the dice roll
                                   private JLabel instructionLabel; // Label to display game instructions
                                   private JButton diceButton; // Button to roll the dice
                                   private JButton instructionButton; // Button to show/hide instructions
                                   private String playerName; // Current player's name
                                   private List<String> result; // List to store the results of each roll
                                   private List<Score> highScore; // List to store high scores
                                   private Map<Integer, ImageIcon> diceImages; // Map to store dice images
                                   private static final String HIGH_SCORE_FILES = "high_scores.txt"; // File to store high scores
                               
                                   public DicePoker() {
                                       highScore = new ArrayList<>(); // Initialize the high score list
                                       loadHighScore(); // Load high scores from file
                                       initializeGame(); // Initialize game variables
                                       setupGUI(); // Set up the graphical user interface
                                       playerSelection(); // Prompt player selection or new player creation
                                   }
                               
                                   private void playerSelection() {
                                       // Prepare list of existing player names and an option for a new player
                                       List<String> playerNames = new ArrayList<>();
                                       for (Score score : highScore) {
                                           playerNames.add(score.playerName);
                                       }
                                       playerNames.add("New Player");
                               
                                       String[] options = playerNames.toArray(new String[0]);
                                       String selectedPlayer = (String) JOptionPane.showInputDialog(
                                           this,
                                           "Select a player or create a new one:",
                                           "Select Player",
                                           JOptionPane.QUESTION_MESSAGE,
                                           null,
                                           options,
                                           options[0]
                                       );
                               
                                       if (selectedPlayer != null) {
                                           if (selectedPlayer.equals("New Player")) {
                                               // Prompt for a new player's name and reset the game
                                               playerName = JOptionPane.showInputDialog(this, "Enter player name:");
                                               if (playerName != null && !playerName.trim().isEmpty()) {
                                                   resetGame();
                                               } else {
                                                   playerSelection();// Retry if the input is invalid
                                               }
                                           } else {
                                               // Load existing player's data
                                               playerName = selectedPlayer;
                                               loadPlayerData();
                                           }
                                       } else {
                                           System.exit(0);// Exit the application if no player is selected
                                       }
                                   }
                               
                                   private void initializeGame() {
                                       // Initialize game settings
                                       accountbalance = 6; // Starting balance
                                       attempts = 5; // Number of attempts
                                       result = new ArrayList<>(); // Initialize result list
                                       ImageGenerator.generateDiceImages(); // Generate dice images
                                       loadDiceImages(); // Load dice images
                                   }
                               
                                   public static void main(String[] args) {
                                       //Run game
                                       SwingUtilities.invokeLater(new Runnable() {
                                           public void run() {
                                               new DicePoker();
                                           }
                                       });
                                   }
                               
                                   private void setupGUI() {
                                       // Set up the graphical user interface
                                       setTitle("DICE POKER");
                                       setSize(600, 600);
                                       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                       getContentPane().setBackground(new Color(30, 144, 255)); // Set background color
                                       setLayout(new GridLayout(7, 1, 10, 10)); // Set layout
                               
                                       // Initialize and add components
                                       balanceLabel = new JLabel("Account Balance: £" + accountbalance, SwingConstants.CENTER);
                                       balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
                                       balanceLabel.setForeground(Color.WHITE);
                                       add(balanceLabel);
                               
                                       resultLabel = new JLabel("Input your bet amount and roll the dice!", SwingConstants.CENTER);
                                       resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
                                       resultLabel.setForeground(Color.GREEN);
                                       add(resultLabel);
                               
                                       betSpace = new JTextField();
                                       betSpace.setFont(new Font("Arial", Font.BOLD, 16));
                                       betSpace.setHorizontalAlignment(JTextField.CENTER);
                                       add(betSpace);
                               
                                       setupInstructions(); // Set up instruction button and label
                               
                                       JPanel dicePanel = new JPanel();
                                       dicePanel.setOpaque(false);
                                       diceLabel1 = new JLabel();
                                       diceLabel2 = new JLabel();
                                       dicePanel.add(diceLabel1);
                                       dicePanel.add(diceLabel2);
                                       add(dicePanel);
                               
                                       diceButton = new JButton("Roll Dice");
                                       diceButton.setBackground(new Color(0, 128, 0));
                                       diceButton.setForeground(Color.WHITE);
                                       diceButton.setFont(new Font("Arial", Font.BOLD, 18));
                                       diceButton.setFocusPainted(false);
                                       diceButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                                       diceButton.addActionListener(new diceButtonListener()); // Add action listener
                                       add(diceButton);
                               
                                       setVisible(true); // Make the frame visible
                                   }
                               
                                   private void loadPlayerData() {
                                       // Load data for the selected player
                                       for (Score score : highScore) {
                                           if (score.playerName.equals(playerName)) {
                                               accountbalance = score.bankBalance; // Update balance
                                               break;
                                           }
                                       }
                                       updateBalanceLabel(); // Update balance display
                                       resultLabel.setText("Welcome back, " + playerName + "! Place your bet and roll the dice!");
                                       diceButton.setEnabled(true); // Enable the dice button
                                   }
                               
                                   private class diceButtonListener implements ActionListener {
                                       public void actionPerformed(ActionEvent e) {
                                           rollDice(); // Roll dice when button is pressed
                                       }
                                   }
                               
                               
                                   private void loadDiceImages() {
                                       // Load dice images from files
                                       diceImages = new HashMap<>();
                                       try {
                                           for (int x = 1; x <= 6; x++) {
                                               File file = new File("dice" + x + ".png");
                                               diceImages.put(x, new ImageIcon(ImageIO.read(file)));
                                           }
                                       } catch (IOException e) {
                                           e.printStackTrace(); // Handle exceptions
                                       }
                                   }
                               
                                   private void rollDice() {
                                       // Roll the dice and update the game state
                                       int betAmount = getBetAmount(); // Get bet amount
                                       if (betAmount == -1) return; // If invalid, return
                               
                                       accountbalance -= betAmount; // Deduct bet amount
                               
                                       int[] dice = rollTwoDice(); // Roll two dice
                                       int die1 = dice[0];
                                       int die2 = dice[1];
                               
                                       animateDiceRoll(die1, die2); // Animate the dice roll
                               
                                       Timer timer = new Timer(1000, new ActionListener() {
                                           public void actionPerformed(ActionEvent evt) {
                                               String outcome = determineOutcome(die1, die2, betAmount); // Determine outcome
                                               updateBalanceLabel(); // Update balance display
                                               resultLabel.setText(outcome); // Update result label
                                               storeResult(betAmount, die1, die2, outcome); // Store result
                               
                                               attempts--; // Decrease attempts
                                               if (attempts <= 0 || accountbalance <= 0) {
                                                   endGame(); // End the game if attempts or balance run out
                                               }
                                           }
                                       });
                                       timer.setRepeats(false); // Timer should run only once
                                       timer.start();
                                   }
                               
                                   private void setupInstructions() {
                                       //Instruction button and Label setup
                                       instructionButton = new JButton("Instructions");
                                       instructionButton.setBackground(new Color(255, 140, 0));
                                       instructionButton.setForeground(Color.WHITE);
                                       instructionButton.setFont(new Font("Arial", Font.BOLD, 16));
                                       instructionButton.setFocusPainted(false);
                                       instructionButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                               
                                       instructionLabel = new JLabel("<html>1. Enter your bet amount (between £1 and £4).<br>" +
                                               "2. Click 'Roll Dice' to roll the dice.<br>" +
                                               "3. You win or lose based on the dice outcomes.<br>" +
                                               "4. The game ends when you run out of money or attempts.<br>" +
                                               "5. Your final results and high scores will be displayed.</html>", SwingConstants.CENTER);
                                       instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                                       instructionLabel.setForeground(Color.WHITE);
                                       instructionLabel.setVisible(false);
                               
                                       instructionButton.addActionListener(new ActionListener() {
                                           @Override
                                           public void actionPerformed(ActionEvent e) {
                                               instructionLabel.setVisible(!instructionLabel.isVisible());//Toggle visibility
                                           }
                                       });
                               
                                       add(instructionButton);
                                       add(instructionLabel);
                                   }
                               
                                   private int getBetAmount() {
                                       //Validate and get bet amount
                                       int betAmount;
                                       try {
                                           betAmount = Integer.parseInt(betSpace.getText());
                                           if (betAmount < 1 || betAmount > 4 || betAmount > accountbalance) {
                                               resultLabel.setText("Invalid bet amount.");
                                               return -1;
                                           }
                                       } catch (NumberFormatException e) {
                                           resultLabel.setText("Please enter a valid bet amount.");
                                           return -1;
                                       }
                                       return betAmount;
                                   }
                               
                                   private void animateDiceRoll(int die1, int die2) {
                                       new Thread(() -> {
                                           Random random = new Random();
                                           for (int x = 0; x < 10; x++) {
                                               int tempDie1 = random.nextInt(6) + 1;
                                               int tempDie2 = random.nextInt(6) + 1;
                                               diceLabel1.setIcon(diceImages.get(tempDie1));
                                               diceLabel2.setIcon(diceImages.get(tempDie2));
                                               try {
                                                   Thread.sleep(100);
                                               } catch (InterruptedException e) {
                                               }
                                           }
                                           diceLabel1.setIcon(diceImages.get(die1));
                                           diceLabel2.setIcon(diceImages.get(die2));
                                       }).start();
                                   }
                               
                                   private void storeResult(int betAmount, int die1, int die2, String outcome) {
                                       String resultEntry = "Bet: £" + betAmount + " | Dice: " + die1 + " & " + die2 + " | Outcome: " + outcome;
                                       result.add(resultEntry);
                                   }
                               
                                   private int[] rollTwoDice() {
                                       //Generate random numbers for two dice
                                       Random dice = new Random();
                                       int die1 = dice.nextInt(6) + 1;
                                       int die2 = dice.nextInt(6) + 1;
                                       return new int[]{die1, die2};
                                   }
                               
                                   private String determineOutcome(int die1, int die2, int betAmount) {
                                       //determine the outcome
                                       String outcome;
                                       if (die1 == die2) {
                                           accountbalance += betAmount * 4;
                                           outcome = "You rolled a pair! You won £" + (betAmount * 4) + "!";
                                       } else if (die1 + 1 == die2 || die2 + 1 == die1) {
                                           accountbalance += betAmount * 2;
                                           outcome = "You rolled a sequence! You won £" + (betAmount * 2) + "!";
                                       } else {
                                           outcome = "You lost £" + betAmount + ". Better luck next time!";
                                       }
                                       return outcome;
                                   }
                               
                                // Method to end the game and show results
                                   private void endGame() {
                                       diceButton.setEnabled(false);
                                       String message = "<html>Final results:<br><br>";
                                       for (String res : result) {
                                           message += res + "<br>";
                                       }
                                       message += "<br>Final Balance: £" + accountbalance + "</html>";
                               
                                       // Show message with the game results and final balance
                                       JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                               
                                       // Save the high score and show the high scores
                                       saveHighScore(playerName, accountbalance);
                                       showHighScores(); // Show the high scores after the game ends
                               
                                       // Prompt to play again
                                       int rollAgain = JOptionPane.showConfirmDialog(this, "Do you want to roll the dice again?", "Roll Again", JOptionPane.YES_NO_OPTION);
                                       if (rollAgain == JOptionPane.YES_OPTION) {
                                           initializeGame();
                                           setupGUI(); // Restart the game
                                       } else {
                                           System.exit(0); // Exit the game
                                       }
                                   }
                               
                                // Save high score to the list
                                   private void saveHighScore(String playerName, int bankBalance) {
                                       for (Score score : highScore) {
                                           if (score.playerName.equals(playerName)) {
                                               if (bankBalance > score.bankBalance) {
                                                   score.bankBalance = bankBalance; // Update if current balance is higher
                                               }
                                               saveHighScoresToFile();
                                               return;
                                           }
                                       }
                                       highScore.add(new Score(playerName, bankBalance)); // Add new score
                                       saveHighScoresToFile(); // Save high scores to file
                               
                                       // Show high scores after adding a new high score
                                       showHighScores();
                                   }
                               
                                   private void loadHighScore() {
                                       Scanner scanner = null;
                                       try {
                                           scanner = new Scanner(new File(HIGH_SCORE_FILES));
                                           while (scanner.hasNextLine()) {
                                               String line = scanner.nextLine();
                                               String[] parts = line.split(",");
                                               if (parts.length == 2) {
                                                   String name = parts[0];
                                                   int balance = Integer.parseInt(parts[1]);
                                                   highScore.add(new Score(name, balance));
                                               }
                                           }
                                       } catch (IOException e) {
                                           System.err.println("Unable to Load High Scores. Check the file and try again.");
                                       } finally {
                                           if (scanner != null) {
                                               scanner.close();
                                           }
                                       }
                                   }
                               
                                   
                                // Save high scores to a file
                                   private void saveHighScoresToFile() {
                               
                                       try (final PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORE_FILES))) {
                               
                                       for (Score score : highScore) {
                               
                                       writer. println(score. playerName + "," + score. bankBalance);
                               
                                       }
                               
                                       } catch (IOException e) {
                               
                                       e.printStackTrace();
                               
                                       }
                                       }
                                   
                                   private static class Score implements Comparable<Score> {
                                       String playerName; // Player's name
                                       int bankBalance; // Player's balance
                               
                                       Score(String playerName, int bankBalance) {
                                           this.playerName = playerName;
                                           this.bankBalance = bankBalance;
                                       }
                               
                                       @Override
                                       public int compareTo(Score other) {
                                           return Integer.compare(this.bankBalance, other.bankBalance); // Compare based on balance
                                       }
                                   }
                                   
                                   private void showHighScores() {
                                       // Ensure the list is sorted in descending order
                                       Collections.sort(highScore);
                               
                                       // Create table model
                                       DefaultTableModel model = new DefaultTableModel();
                                       model.addColumn("Rank");
                                       model.addColumn("Name");
                                       model.addColumn("Balance");
                               
                                       // Add data to the table model
                                       int rank = 1;
                                       for (Score score : highScore) {
                                           model.addRow(new Object[]{rank++, score.playerName, score.bankBalance});
                                       }
                               
                                       // Create and display the table in a JOptionPane
                                       JTable table = new JTable(model);
                                       table.setFillsViewportHeight(true);
                                       JScrollPane scrollPane = new JScrollPane(table);
                                       JOptionPane.showMessageDialog(this, scrollPane, "High Scores", JOptionPane.INFORMATION_MESSAGE);
                                   }
                               
                                   private void resetGame() {
                                       // Reset the game for a new player
                                       accountbalance = 8; // Reset balance
                                       attempts = 4; // Reset attempts
                                       updateBalanceLabel(); // Update balance display
                                       resultLabel.setText("Welcome, " + playerName + "! Place your bet and roll the dice!");
                                       diceButton.setEnabled(true); // Enable the dice button
                                   }
                               
                                   private void updateBalanceLabel() {
                                       balanceLabel.setText("Account Balance: £" + accountbalance);
                                   }
                               
                                   static class ImageGenerator {
                                       //generate dice image
                                       static void generateDiceImages() {
                                           int imageSize = 100;
                                           int dotSize = 20;
                                           Color[] dotColors = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN};
                                           Random rand = new Random();
                               
                                           for (int i = 1; i <= 6; i++) {
                                               BufferedImage diceImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
                                               Graphics2D g = diceImage.createGraphics();
                               
                                               g.setColor(Color.WHITE);
                                               g.fillRect(0, 0, imageSize, imageSize);
                               
                                               g.setColor(dotColors[rand.nextInt(dotColors.length)]);
                                               switch (i) {
                                                   case 1:
                                                       drawDot(g, imageSize / 2, imageSize / 2, dotSize);
                                                       break;
                                                   case 2:
                                                       drawDot(g, imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       break;
                                                   case 3:
                                                       drawDot(g, imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, imageSize / 2, imageSize / 2, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       break;
                                                   case 4:
                                                       drawDot(g, imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       break;
                                                   case 5:
                                                       drawDot(g, imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       drawDot(g, imageSize / 2, imageSize / 2, dotSize);
                                                       break;
                                                   case 6:
                                                       drawDot(g, imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, imageSize / 4, imageSize / 2, dotSize);
                                                       drawDot(g, imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, imageSize / 4, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, imageSize / 2, dotSize);
                                                       drawDot(g, 3 * imageSize / 4, 3 * imageSize / 4, dotSize);
                                                       break;
                                               }
                                               g.dispose();
                                               try {
                                                   ImageIO.write(diceImage, "png", new File("dice" + i + ".png"));
                                               } catch (IOException e) {
                                                   e.printStackTrace();
                                               }
                                           }
                                       }
                               
                                       private static void drawDot(Graphics2D g, int x, int y, int size) {
                                           g.fillOval(x - size / 2, y - size / 2, size, size);
                                       }
                                   }
                               }
                               
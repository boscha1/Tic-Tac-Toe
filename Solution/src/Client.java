import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Anthony Bosch
 * Tic Tac Toe Client Server
 * Client
 */

public class Client {
    private static Socket toServerSocket;
    private static char[][] board;
    private static int row, col;
    private static DataInputStream inStream;
    private static DataOutputStream outStream;

    public static void main(String[] args) throws IOException {
        System.out.println("CLIENT is attempting connection....");
        try {
            // attempt to connect to localhost at port 7788
            toServerSocket = new Socket("localhost", 7788);
            System.out.println("\nCONNECTION HAS BEEN MADE");
            inStream = new DataInputStream(toServerSocket.getInputStream());
            outStream = new DataOutputStream(toServerSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(outStream, true);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

        // initialize a board of 3 by 3 empty chars
        board = new char[3][3];

        for (int rowIndex = 0; rowIndex <= 2; ++rowIndex) {
            for (int columnIndex = 0; columnIndex <= 2; ++columnIndex) {
                board[rowIndex][columnIndex] = ' ';
            }
        }

        row = -1;
        col = -1;
        startGame(br, pw);
    }

    public static void startGame(BufferedReader bufferedReader, PrintWriter printWriter) throws IOException {
        Scanner input = new Scanner(System.in);
        boolean gameOver = false;
        boolean turn = false;
        String response;

        do {
            if (!turn) {
                // get response from server
                response = bufferedReader.readLine();
                if (!response.equals("NONE")) {
                    String[] data = response.split("\\s+");
                    if (data.length > 3) {
                        row = Integer.parseInt(data[1]);
                        col = Integer.parseInt(data[2]);

                        if (!data[3].equals("WIN") && row != -1)
                            board[row][col] = 'X';

                        String status = data[3];

                        if (!(status.isEmpty())) {
                            if (status == "WIN") System.out.println("You Won!");
                            else if (status == "TIE") System.out.println("You Tied!");
                            else System.out.println("You Lost!");
                        }
                        gameOver = true;
                    }
                    else {
                        System.out.println("\n ******SERVER MOVE******");
                        row = Integer.parseInt(data[1]);
                        col = Integer.parseInt(data[2]);
                        board[row][col] = 'X';
                    }
                }
            }
            // player's turn
            else {
                do {
                    System.out.print("\nEnter your move (row a column)  : ");
                    row = input.nextInt();
                    col = input.nextInt();

                    try {
                        if (board[row][col] != ' ') {
                            System.out.println("Invalid Selection");
                            continue;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.out.println("Please enter a value between 0 and 2");
                        continue;
                    }

                    if (row <= 2 && col <= 2 && board[row][col] == ' ') {
                        board[row][col] = 'O';
                        // send move position to server
                        printWriter.println("MOVE " + row + " " + col);
                        break;
                    }
                } while (true);
            }
            printBoard();
            // signal next turn
            turn = !turn;
        } while (!gameOver);
    }

    public static void printBoard() {
        System.out.println("\n-------------");

        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }
}

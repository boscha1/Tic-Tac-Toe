import java.io.*;
import java.net.Socket;
import java.util.Random;

/**
 * Anthony Bosch
 * Tic Tac Toe Client Server
 * ServerThread
 */

public class ServerThread extends Thread {

    private Socket toClientSocket;      // Socket used to communicate with the client
    private DataInputStream inStream;   // stores input stream of the Socket
    private DataOutputStream outStream; // stores output stream of the Socket
    private PrintWriter out;            // allow us to use print() and println()
    private BufferedReader in;          // allow us to use readLine()
    private Random randomGen;           // used to select random moves
    private char[][] board;             // matrix of char to represent game board
    private int row, col;               // hold current row/column values

    public ServerThread(Socket socket) throws IOException {
        this.toClientSocket = socket;   // Assign socket to toClientSocket
        randomGen = new Random();       // Instantiate random num generator
        inStream = new DataInputStream(toClientSocket.getInputStream());    // Obtain DataInputStream from the socket
        outStream = new DataOutputStream(toClientSocket.getOutputStream()); // Obtain DataOutputStream from the socket
        out = new PrintWriter(outStream, true);     // autoFlush = true turns on autoFlash
        in = new BufferedReader(new InputStreamReader(inStream));   // use DataInputStream to instantiate BufferReader

        row = -1;
        col = -1;
        board = new char[3][3];
        // instantiate and initialize the board char matrix
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    @Override
    public void run() {
        int counter = 0;
        String response = "";
        boolean gameOver = false;
        boolean turn = false;
        int randNum;

        randNum = randomGen.nextInt();

        // flip coin to see who goes first
        if (randNum % 2 == 0)
            // player goes first
            turn = true;

        if (turn == true) {
            // player is making first move
            // send message to player via socket
            out.println("NONE");
        }
        do {
            if (turn) {
                System.out.println();
                try {
                    // read player's move
                    response = in.readLine();
                } catch (IOException e) {
                    System.out.println("Some sort of read error on socket in server thread");
                }
                // break up string into words
                String[] data = response.split("\\s+"); // breaks up string on any whitespace
                row = Integer.parseInt(data[1]);
                col = Integer.parseInt(data[2]);

                // put an 'O' into selected cell of the matrix
                counter++;
                board[row][col] = 'O';
                printBoard();

                // check to see if the game is over - win or tie
                if (checkWin() || counter == 9) {
                    gameOver = true;
                    if (checkWin())
                        // send win message to client
                        out.println("MOVE -1 -1 WIN");
                    else
                        // send tie message to client
                        out.println("MOVE -1 -1 TIE");
                }
            }
            else {
                //makeMove();
                smartMove();
                counter++;
                board[row][col] = 'X';
                printBoard();
                if (checkWin() || counter == 9) {
                    if (checkWin())
                        // send loss message to client
                        out.println("MOVE " + row + " " + col + " LOSS");
                    else
                        // send tie message to client
                        out.println("MOVE " + row + " " + col + " TIE");
                }
                else
                    out.println("MOVE " + row + " " + col);
            }
            // signal next turn
            turn = !turn;
        } while (!gameOver);
    }


    private void makeMove() {
        do {
            row = randomGen.nextInt(3);
            col = randomGen.nextInt(3);
        } while (board[row][col] != ' ');
    }

    private void smartMove() {
        do {
            row = randomGen.nextInt(3);
            col = randomGen.nextInt(3);

            // go for horizontal win or block
            for (int x = 0; x <= 2; x++) {
                if (board[x][0] == board[x][1] && board[x][0] != ' ' && board[x][2] == ' ') {
                    row = x;
                    col = 2;
                    break;
                }
                else if (board[x][1] == board[x][2] && board[x][1] != ' ' && board[x][0] == ' ') {
                    row = x;
                    col = 0;
                    break;
                }
                else if (board[x][0] == board[x][2] && board[x][0] != ' ' && board[x][1] == ' ') {
                    row = x;
                    col = 1;
                }
            }

            // go for vertical win or block
            for (int x = 0; x <= 2; x++) {
                if (board[0][x] == board[1][x] && board[0][x] != ' ' && board[2][x] == ' ') {
                    row = 2;
                    col = x;
                    break;
                }
                else if (board[1][x] == board[2][x] && board[1][x] != ' ' && board[0][x] == ' ') {
                    row = 0;
                    col = x;
                    break;
                }
                else if (board[0][x] == board[2][x] && board[0][x] != ' ' && board[1][x] == ' ') {
                    row = 1;
                    col = x;
                }
            }

            // go for diagonal win or block
            if (board[0][0] == board[1][1] && board[0][0] != ' ' && board[2][2] == ' ') {
                row = 2;
                col = 2;
            }
            else if (board[0][0] == board[2][2] && board[0][0] != ' ' && board[1][1] == ' ') {
                row = 1;
                col = 1;
            }
            else if (board[1][1] == board[2][2] && board[1][1] != ' ' && board[0][0] == ' ') {
                row = 0;
                col = 0;
            }
            else if (board[0][2] == board[1][1] && board[0][2] != ' ' && board[2][0] == ' ') {
                row = 2;
                col = 0;
            }
            else if (board[2][0] == board[1][1] && board[2][0] != ' ' && board[0][2] == ' ') {
                row = 0;
                col = 2;
            }
            else if (board[2][0] == board[0][2] && board[2][0] != ' ' && board[1][1] == ' ') {
                row = 1;
                col = 1;
            }
        } while (board[row][col] != ' ');
    }

    private boolean checkWin() {
        // horizontal win check
        for (int x = 0; x <= 2; x++)
            if (board[x][0] == board[x][1] && board[x][1] == board[x][2] && board[x][0] != ' ')
                return true;

        // vertical win check
        for (int x = 0; x <= 2; x++)
            if (board[0][x] == board[1][x] && board[1][x] == board[2][x] && board[0][x] != ' ')
                return true;

        // diagonal win check
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != ' ')
            return true;
        else if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != ' ')
            return true;

        return false;
    }

    public void printBoard() {
        System.out.println("-------------");

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

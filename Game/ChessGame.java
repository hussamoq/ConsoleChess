package Game;
import Board.*;
import java.io.*;
import java.util.Scanner;
import Observer.CheckMateObserver;
import Utility.Pair;
import ChessPiece.ChessPiece;

public class ChessGame extends Game {
    static final int numberOfPlayers = 2;
    private boolean checkMate = false;
    private BufferedReader input = null;
    Player player = Player.PLAYER1;
    Scanner scanner = new Scanner(System.in);
    public ChessGame(){
        super(new ChessBoard(), numberOfPlayers);
        board.registerObserver(new CheckMateObserver(this));

        System.out.println("Would you like to take the input from the test file? (y/n) Default is no.");
        Scanner yesOrNo = new Scanner(System.in);
        if(yesOrNo.nextLine().equalsIgnoreCase("y")){
            openTestFile();
            setFileInput();
        }
    }

    @Override
    public void start(){
        int currentNumberOfMoves = 0;

        do{
            board.printGrid();

            switch (player) {
                case PLAYER1 : {
                    System.out.println("Enter your move player 1: ");
                    ProcessMessage message = processTurn(Player.PLAYER1);
                    if(message == ProcessMessage.BAD)
                        continue;
                    player = Player.PLAYER2;
                    break;
                }
                case PLAYER2 : {
                    System.out.println("Enter your move player 2: ");
                    ProcessMessage message = processTurn(Player.PLAYER2);
                    if(message == ProcessMessage.BAD)
                        continue;
                    player = Player.PLAYER1;
                    break;
                }
            }

            ++currentNumberOfMoves;
        }while(isNotCheckMate() && currentNumberOfMoves < maxNumberOfMoves);

        board.printGrid();
        scanner.close();
        try{
            if(fileInput)
                input.close();
        }catch(Exception e){
            System.out.println("Could not close test file");
        }

        if(isCheckMate()){
            System.out.println("Checkmate!");
            return;
        }

        System.out.println("DRAW");
    }

    @Override
    public boolean isCheckMate(){
        return checkMate;
    }

    @Override
    public boolean isNotCheckMate() {
        return !checkMate;
    }

    public void setCheckMate(){
        checkMate = true;
    }

    @Override
    ProcessMessage processTurn(Player player){
        String move;
        if(fileInput)
            move = attemptToGetNextLineInFile();
        else
            move = scanner.nextLine();

        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> pair = getMovesFromString(move);
        int currentHeight = pair.first.first;
        int currentWidth = pair.first.second;
        int destinationHeight = pair.second.first;
        int destinationWidth = pair.second.second;

        if(notInBound(currentHeight, currentWidth) || notInBound(destinationHeight, destinationWidth) ||
            getBoard().getGrid()[currentHeight][currentWidth] == null)
            return ProcessMessage.BAD;

        if(checkTurnCorrectness(currentHeight, currentWidth) == ProcessMessage.BAD)
            return ProcessMessage.BAD;

        if(checkDestinationCorrectness(destinationHeight, destinationWidth, currentHeight, currentWidth) == ProcessMessage.BAD)
            return ProcessMessage.BAD;

        board.updateMoves();
        return ProcessMessage.GOOD;
    }

    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getMovesFromString(String str){
        if(str.length() != 10){
            str = "1234567890";
        }

        str = str.substring(0, 10);

        String num1 = str.substring(5, 7);
        String num2 = str.substring(8, 10);

        int currentHeight = 7 - (num1.charAt(1) - '0' - 1);
        int currentWidth = num1.charAt(0) - 'a';
        int destinationHeight = 7 - (num2.charAt(1) - '0' - 1);
        int destinationWidth = num2.charAt(0) - 'a';

        return new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(new Pair<Integer, Integer>(currentHeight, currentWidth),
                new Pair<Integer, Integer>(destinationHeight, destinationWidth));
    }

    ProcessMessage checkTurnCorrectness(int currentHeight, int currentWidth){
        if(board.getGrid()[currentHeight][currentWidth].getColor() == ChessPiece.Color.WHITE && player == Player.PLAYER2){
            System.out.println("try again");
            return ProcessMessage.BAD;
        }
        else if(board.getGrid()[currentHeight][currentWidth].getColor() == ChessPiece.Color.BLACK && player == Player.PLAYER1){
            System.out.println("try again");
            return ProcessMessage.BAD;
        }

        return ProcessMessage.GOOD;
    }

    ProcessMessage checkDestinationCorrectness(int destinationHeight, int destinationWidth,
                                               int currentHeight, int currentWidth){
        try{
            board.getGrid()[currentHeight][currentWidth].move(destinationHeight, destinationWidth);
        }catch(IllegalAccessException e){
            System.out.println("try again");
            return ProcessMessage.BAD;
        }catch(Exception e){
            System.out.println("Unknown error occurred");
            System.exit(1);
        }

        return ProcessMessage.GOOD;
    }

    void openTestFile(){
        try{
            input = new BufferedReader(new FileReader("put test cases here.txt"));
        }catch(IOException e){
            System.out.println("could not open file");
            System.exit(1);
        }
    }

    boolean inBound(int height, int width){
        return height >= 0 && height < 8 && width >= 0 && width < 8;
    }

    boolean notInBound(int height, int width){
        return !inBound(height, width);
    }

    String attemptToGetNextLineInFile(){
        String move = null;
        try{
            move = input.readLine();

            if(move == null){
                input.close();
                System.out.println("Test complete");
                System.exit(0);
            }

        }catch(IOException e){
            System.out.println("Could not read from test file");
            System.exit(1);
        }

        return move;
    }
}

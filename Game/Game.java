package Game;
import Board.Board;

public abstract class Game {
    protected static enum Player { PLAYER1, PLAYER2, PLAYER3, PLAYER4, PLAYER5, PLAYER6, PLAYER7,
        PLAYER8, PLAYER9, PLAYER10 };
    protected static enum ProcessMessage {GOOD, BAD};
    protected final Board board;
    protected final int numberOfPlayers;
    protected final int maxNumberOfPlayers = 10;
    protected final int maxNumberOfMoves = 50;
    protected boolean fileInput = false;
    public Game(Board board, int numberOfPlayers) throws IllegalArgumentException{
        if(numberOfPlayers > maxNumberOfPlayers)
            throw new IllegalArgumentException("Max player number exceeded. Max is :" + maxNumberOfPlayers);

        this.board = board;
        this.numberOfPlayers = numberOfPlayers;
    }

    public Board getBoard(){ return board; }
    public int getNumberOfPlayers() { return maxNumberOfPlayers; }
    public int getMaxNumberOfPlayers() { return maxNumberOfPlayers; }
    public int getMaxNumberOfMoves() { return maxNumberOfMoves; }

    public abstract void start();
    public abstract boolean isCheckMate();
    public abstract boolean isNotCheckMate();

    void setFileInput(){
        fileInput = true;
    }
    abstract ProcessMessage processTurn(Player player) throws IllegalAccessException;

}

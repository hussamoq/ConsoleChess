package ChessPiece;
import Mobility.Moves;
import java.util.*;
import Utility.Pair;
import Board.*;

public abstract class ChessPiece {
    public static enum Color { BLACK, WHITE, BLUE, YELLOW, RED, ORANGE, GREEN, PURPLE, PINK };
    protected final Color color;
    protected final Moves moves;
    protected int currentWidth;
    protected int currentHeight;
    //Every chess piece should have a reference to the target board
    protected Board board;

    //Current legal moves relative to the chess piece location
    protected Set<Pair<Integer, Integer>> legalMoves;

    public ChessPiece(Color color, Moves moves, int width, int height, Board board){
        this.color = color;
        this.moves = moves;
        currentWidth = width;
        currentHeight = height;
        legalMoves = new HashSet<>();
        this.board = board;
    }

    public Color getColor(){
        return color;
    }
    public Moves getMoves(){
        return moves;
    }
    public int getCurrentWidth() { return currentWidth; }
    public int getCurrentHeight() { return currentHeight; }

    public void move(int height, int width) throws IllegalAccessException{
        if(!legalMoves.contains(new Pair<Integer, Integer>(width, height))){
            throw new IllegalAccessException("Illegal move, try again");
        }
    }

    public void recalculateLegalMoves(){
        clearLegalMoves();
    }

    public void attemptBlockAttackOnKing(Color beingAttacked, ArrayList<Pair<Integer, Integer>> pathToKing){
        if(color == beingAttacked){
            if(pathToKing == null){
                clearLegalMoves();
                return;
            }

            ArrayList<Pair<Integer, Integer>> keptMoves = new ArrayList<>();
            for(Pair<Integer, Integer> position : pathToKing){
                if(legalMoves.contains(position)){
                    keptMoves.add(position);
                }
            }

            clearLegalMoves();
            for(Pair<Integer, Integer> move : keptMoves){
                legalMoves.add(move);
            }

            addToAllMoves((ChessBoard)board);
        }
    }

    public void keepIntersectedPositions(ArrayList<Pair<Integer, Integer>> path){
        ArrayList<Pair<Integer, Integer>> keptMoves = new ArrayList<>();
        for(Pair<Integer, Integer> move : path){
            if(legalMoves.contains(move)){
                keptMoves.add(move);
            }
        }

        legalMoves.clear();
        for(Pair<Integer, Integer> move : keptMoves){
            legalMoves.add(move);
        }

        addToAllMoves((ChessBoard)board);
    }

    public Set<Pair<Integer, Integer>> getLegalMoves(){
        return legalMoves;
    }
    public abstract String getName();
    public boolean inBound(int height, int width){
        return height < board.getGrid().length && height >= 0 && width < board.getGrid()[0].length && width >= 0;
    }
    public boolean notInBound(int height, int width){
        return !inBound(height, width);
    }
    public boolean isEnemy(int height, int width){
        return this.color != board.getGrid()[height][width].color;
    }
    public boolean isNotEnemy(int height, int width) { return !isEnemy(height, width); }

    public boolean isEmptySpace(int height, int width) { return board.getGrid()[height][width] == null; }
    public boolean isNotEmptySpace(int height, int width){ return !isEmptySpace(height, width); }

    public Pair<Integer, Integer> getCurrentLocation(){
        return new Pair<Integer, Integer>(getCurrentWidth(), getCurrentHeight());
    }

    public boolean doesIntersectWithOppositeTeam(int height, int width){
        ChessBoard chessBoard = (ChessBoard)board;
        if(color == Color.WHITE){
            return chessBoard.getAllBlackLegalMoves().contains(new Pair<Integer, Integer>(width, height));
        }

        return chessBoard.getAllWhiteLegalMoves().contains(new Pair<Integer, Integer>(width, height));
    }

    public boolean doesNotIntersectWithOppositeTeam(int height, int width){
        return !doesIntersectWithOppositeTeam(height, width);
    }

    public void addToLegalMoves(int height, int width){
        legalMoves.add(new Pair<Integer, Integer>(width, height));
    }
    void clearLegalMoves() { legalMoves.clear(); }
    void addToAllMoves(ChessBoard board){
        for(Pair<Integer, Integer> pair : legalMoves){
            if(color == Color.WHITE)
                board.addToAllWhiteLegalMoves(pair);
            else
                board.addToAllBlackLegalMoves(pair);
        }
    }

    void addToAllPotentialMoves(Pair<Integer, Integer> move){
        ChessBoard chessBoard = (ChessBoard)board;
        if(color == Color.WHITE){
            chessBoard.addToAllWhitePotentialLegalMoves(move);
        }else{
            chessBoard.addToAllBlackPotentialLegalMoves(move);
        }
    }

    void addPotentialAttack(int position){
        int[] verticalMoves = moves.getVerticalMoves();
        int[] horizontalMoves = moves.getHorizontalMoves();
        int verticalDisplacement = verticalMoves[position] + currentHeight;
        int horizontalDisplacement = horizontalMoves[position] + currentWidth;

        addToAllPotentialMoves(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));

    }
}

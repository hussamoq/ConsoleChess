package ChessPiece;
import Mobility.Moves;
import Board.*;
import Utility.Pair;
public final class King extends ChessPiece {
    private final ChessBoard chessBoard;
    public King(Color color, Moves moves, int width, int height, Board board){
        super(color, moves, width, height, board);
        chessBoard = (ChessBoard)board;
    }

    @Override
    public String getName(){
        return "King";
    }

    @Override
    public void move(int height, int width) throws IllegalAccessException{
        try{
            super.move(height, width);
        }catch(IllegalAccessException e){
            throw e;
        }catch(Exception e){
            System.out.println("Unknown error occurred");
            System.exit(1);
        }

        board.getGrid()[height][width] = this;
        board.getGrid()[currentHeight][currentWidth] = null;

        currentHeight = height;
        currentWidth = width;
    }

    @Override
    public void recalculateLegalMoves(){
        super.recalculateLegalMoves();

        int[] tempVerticalMoves = getMoves().getVerticalMoves();
        int[] tempHorizontalMoves = getMoves().getHorizontalMoves();
        for(int i = 0; i < tempVerticalMoves.length; ++i){
            int verticalDisplacement = currentHeight + tempVerticalMoves[i];
            int horizontalDisplacement = currentWidth + tempHorizontalMoves[i];
            if(inBound(verticalDisplacement, horizontalDisplacement)){
                if(isEmptySpace(verticalDisplacement, horizontalDisplacement) ||
                        isEnemy(verticalDisplacement, horizontalDisplacement)){
                    if(color == Color.WHITE){
                        if(chessBoard.getAllBlackLegalMoves().contains(new Pair<Integer, Integer>
                                (horizontalDisplacement, verticalDisplacement))){
                            continue;
                        }
                    }

                    if(color == Color.BLACK){
                        if(chessBoard.getAllWhitePotentialLegalMoves().contains(new Pair<Integer, Integer>
                                (horizontalDisplacement, verticalDisplacement)))
                            continue;
                    }

                    addToLegalMoves(verticalDisplacement, horizontalDisplacement);
                }
            }
        }

        //Sorts it to the universal moves containers
        addToAllMoves(chessBoard);
    }
}

package Board;
import ChessPiece.*;
import Mobility.*;
import Utility.Pair;
import java.util.*;

public class ChessBoard extends Board {
    private final Set<Pair<Integer, Integer>> allWhiteLegalMoves = new HashSet<>();
    private final Set<Pair<Integer, Integer>> allBlackLegalMoves = new HashSet<>();
    private final Set<Pair<Integer, Integer>> allWhitePotentialLegalMoves = new HashSet<>();
    private final Set<Pair<Integer, Integer>> allBlackPotentialLegalMoves = new HashSet<>();
    private final ChessPiece whiteKing;
    private final ChessPiece blackKing;
    private ChessPiece checkingPiece = null;
    private boolean kingIsCheckedByTwoEnemies = false;

    public ChessBoard(){
        //A normal chess board is always 8x8
        height = 8;
        width = 8;
        //Initialize the board to null ChessPiece objects
        grid = new ChessPiece[height][width];

        //Arrange chess pieces in the correct initial positions
        arrangePawns();
        arrangeRooks();
        arrangeKnights();
        arrangeBishops();
        arrangeQueens();
        arrangeKings();

        //Keep a reference to both kings to find checkmate later
        whiteKing = grid[7][4];
        blackKing = grid[0][4];
    }

    //Everytime any piece moves, they all need to recalculate their options
    @Override
    public void updateMoves(){
        clearWhiteLegalMoves();
        clearBlackLegalMoves();
        clearAllWhitePotentialLegalMoves();
        clearAllBlackPotentialLegalMoves();

        HashMap<ChessPiece, ArrayList<Pair<Integer, Integer>>> kingDefendersToPath =
                checkForIndirectAttacks();

        //invoke each chess piece class and recalculate moves since a move was taken
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                if(grid[i][j] != null){
                    grid[i][j].recalculateLegalMoves();
                }
            }
        }

        removeKingIntersectionsWithEnemy();
        testForCheckMate();
        stopPiecesFromMovingInCertainDirections(kingDefendersToPath);
    }

    @Override
    public void printGrid() {
        //This prints the top letters
        System.out.print("  ");
        for(int i = 0; i < width; ++i){
            System.out.print((char)(i + 'a') + " ");
        }
        System.out.println();

        //This prints the grid with an extra number at the beginning of each line
        for(int i = 0; i < width; ++i){
            System.out.print((width - i) + " ");
            for(int j = 0; j < 8; ++j){
                if(grid[i][j] != null)
                    System.out.print((grid[i][j].getColor() == ChessPiece.Color.WHITE ?
                                    grid[i][j].getName().charAt(0) :
                            grid[i][j].getName().toLowerCase().charAt(0)) + " ");
                else
                    System.out.print("- ");
            }
            System.out.println();
        }
    }

    public void addToAllWhiteLegalMoves(Pair<Integer, Integer> whiteLegalMove){
        allWhiteLegalMoves.add(whiteLegalMove);
    }
    public void addToAllBlackLegalMoves(Pair<Integer, Integer> blackLegalMove){
        allBlackLegalMoves.add(blackLegalMove);
    }

    public void addToAllWhitePotentialLegalMoves(Pair<Integer, Integer> move){
        allWhitePotentialLegalMoves.add(move);
    }

     public void addToAllBlackPotentialLegalMoves(Pair<Integer, Integer> move){
        allBlackPotentialLegalMoves.add(move);
    }

    void clearWhiteLegalMoves(){
        allWhiteLegalMoves.clear();
    }
    void clearBlackLegalMoves(){
        allBlackLegalMoves.clear();
    }

    void clearAllWhitePotentialLegalMoves(){
        allWhitePotentialLegalMoves.clear();
    }

    void clearAllBlackPotentialLegalMoves(){
        allBlackPotentialLegalMoves.clear();
    }

    void checkIfWhiteKingCanEscape(){
        for(Pair<Integer, Integer> move :  allBlackLegalMoves){
            whiteKing.getLegalMoves().remove(move);
        }

        for(Pair<Integer, Integer> move : allBlackPotentialLegalMoves){
            whiteKing.getLegalMoves().remove(move);
        }
    }

    void checkIfBlackKingCanEscape(){
        for(Pair<Integer, Integer> move : allWhiteLegalMoves){
            blackKing.getLegalMoves().remove(move);
        }

        for(Pair<Integer, Integer> move : allWhitePotentialLegalMoves){
            blackKing.getLegalMoves().remove(move);
        }
    }

    public Set<Pair<Integer, Integer>> getAllWhiteLegalMoves() { return allWhiteLegalMoves; }
    public Set<Pair<Integer, Integer>> getAllBlackLegalMoves() { return allBlackLegalMoves; }
    public Set<Pair<Integer, Integer>> getAllWhitePotentialLegalMoves() { return allWhitePotentialLegalMoves; }
    public Set<Pair<Integer, Integer>> getAllBlackPotentialLegalMoves() { return allBlackPotentialLegalMoves; }
    public void setCheckingPiece(ChessPiece piece) { checkingPiece = piece; }

    HashMap<ChessPiece, ArrayList<Pair<Integer, Integer>>> checkForIndirectAttacks(){
        //If a piece is attacking another king indirectly, ie(there exists exactly one enemy piece between it
        // and the king) then this enemy piece has to be forbidden from being moved to avoid exposing the king.
        //(Unless it can attack that piece without further exposure)
        //the ones with indirect attacks can only be a queen, bishop or rook.
        HashMap<ChessPiece, ArrayList<Pair<Integer, Integer>>> kingDefendersToPath = new HashMap<>();
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                if(grid[i][j] != null){
                    if(isQueen(grid[i][j]) || isBishop(grid[i][j]) || isRook(grid[i][j])) {
                        Pair<ChessPiece, ArrayList<Pair<Integer, Integer>>> result = searchForEnemyKing(grid[i][j]);
                        ArrayList<Pair<Integer, Integer>> potentialPathToKing = result.second;
                        if(potentialPathToKing.size() == 1 || result.first == null)
                            continue;

                        kingDefendersToPath.put(result.first, result.second);
                    }
                }
            }
        }

        return kingDefendersToPath;
    }

    Pair<ChessPiece, ArrayList<Pair<Integer, Integer>>> searchForEnemyKing(ChessPiece piece){
        int cycles = 4;
        if(piece instanceof Queen)
            cycles = 8;
        int[] verticalMoves = piece.getMoves().getVerticalMoves();
        int[] horizontalMoves = piece.getMoves().getHorizontalMoves();

        boolean pieceFound = false;
        boolean kingFound = false;
        ChessPiece foundPiece = null;
        ArrayList<Pair<Integer, Integer>> potentialPathToKing = new ArrayList<>();
        //The current location could be somewhere the other side piece can move to
        potentialPathToKing.add(piece.getCurrentLocation());
        for(int i = 0; i < cycles; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = verticalMoves[j] + piece.getCurrentHeight();
                int horizontalDisplacement = horizontalMoves[j] + piece.getCurrentWidth();
                if(piece.notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(piece.isNotEmptySpace(verticalDisplacement, horizontalDisplacement) &&
                    piece.isNotEnemy(verticalDisplacement, horizontalDisplacement))
                    break;
                if(piece.isNotEmptySpace(verticalDisplacement, horizontalDisplacement) &&
                        piece.isEnemy(verticalDisplacement, horizontalDisplacement) &&
                        !(grid[verticalDisplacement][horizontalDisplacement] instanceof King)){
                    if(pieceFound) {
                        pieceFound = false;
                        foundPiece = null;
                        break;
                    }

                    foundPiece = grid[verticalDisplacement][horizontalDisplacement];
                    pieceFound = true;
                }
                else if(piece.isNotEmptySpace(verticalDisplacement, horizontalDisplacement) &&
                        piece.isEnemy(verticalDisplacement, horizontalDisplacement) &&
                        (grid[verticalDisplacement][horizontalDisplacement] instanceof King)){
                    kingFound = true;
                    break;
                }

                potentialPathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
            }

            if(pieceFound && kingFound)
                break;

            potentialPathToKing.clear();
            potentialPathToKing.add(piece.getCurrentLocation());
        }

        return new Pair<ChessPiece, ArrayList<Pair<Integer, Integer>>>(foundPiece, potentialPathToKing);
    }

    boolean isQueen(ChessPiece piece){
        return piece instanceof Queen;
    }

    boolean isBishop(ChessPiece piece){
        return piece instanceof Bishop;
    }

    boolean isRook(ChessPiece piece){
        return piece instanceof Rook;
    }

    //Checks if the White king is currently being attacked
    boolean isCheckForWhite(){
       return allBlackLegalMoves.contains(new Pair<Integer, Integer>(whiteKing.getCurrentWidth(),
                whiteKing.getCurrentHeight()));
    }

    //Checks if the Black king is currently being attacked
    boolean isCheckForBlack(){
        return allWhiteLegalMoves.contains(new Pair<Integer, Integer>(blackKing.getCurrentWidth(),
                blackKing.getCurrentHeight()));
    }

    void testForCheckMate(){
        if(isCheckForWhite()){
            if(isCheckFromTwoEnemies(ChessPiece.Color.WHITE)){
                kingIsCheckedByTwoEnemies = true;
            }

            attemptToRemoveCheckForWhite();
        }
        else if(isCheckForBlack()){
            if(isCheckFromTwoEnemies(ChessPiece.Color.BLACK)){
                kingIsCheckedByTwoEnemies = true;
            }

            attemptToRemoveCheckForBlack();
        }
    }

    boolean isCheckFromTwoEnemies(ChessPiece.Color color){
        King king;
        if(color == ChessPiece.Color.WHITE){
            king = (King)whiteKing;
        }else{
            king = (King)blackKing;
        }

        boolean foundCheck = false;
        boolean kingIsCheckedByTwoEnemies = false;
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                if(grid[i][j] != null && grid[i][j].getColor() != color){
                    if(grid[i][j].getLegalMoves().contains(king.getCurrentLocation())){
                        if(!foundCheck){
                            foundCheck = true;
                        }else{
                            kingIsCheckedByTwoEnemies = true;
                            break;
                        }
                    }
                }
            }
            if(kingIsCheckedByTwoEnemies)
                break;
        }

        return kingIsCheckedByTwoEnemies;
    }

    void stopPiecesFromMovingInCertainDirections(HashMap<ChessPiece, ArrayList<Pair<Integer, Integer>>> kingDefendersToPath){
        for(HashMap.Entry<ChessPiece, ArrayList<Pair<Integer, Integer>>> set : kingDefendersToPath.entrySet()){
            set.getKey().keepIntersectedPositions(set.getValue());
        }
    }

    void attemptToRemoveCheckForWhite(){
        if(!kingIsCheckedByTwoEnemies){
            //check if the attacking piece is killable
            HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> whiteToAttacker
                    = storePossibleAttackForWhite(checkingPiece);

            checkIfWhiteKingCanEscape();
            clearWhiteLegalMoves();

            //Check what kind of piece has a check on the king and
            //Check the direction where the king is being attacked from
            ArrayList<Pair<Integer, Integer>> pathToKing = null;
            if(isQueen(checkingPiece)){
                pathToKing = attemptToAvoidBlackQueen();
            }else if(isBishop(checkingPiece)){
                pathToKing = attemptToAvoidBlackBishop();
            }else if(isRook(checkingPiece)){
                pathToKing = attemptToAvoidBlackRook();
            }

            //Now that we have the path that attacks the king, we need to update our moves in a way to block
            //this path, by going through all the white chess pieces and only keeping the moves that help
            //block this attack.

            attemptToBlockAttackForWhiteKing(pathToKing);

            //let us also iterate the map and add the possible attacks on the black piece
            addPossibleAttacksToWhitePieces(whiteToAttacker);
        }

        //We removed all possible moves that cannot block the King of being checked
        //we also removed the king's moves that intersect with other opposite piece's moves
        //If the moves are empty simply means a checkmate
        if(allWhiteLegalMoves.isEmpty()){
            notifyObservers();
        }
        kingIsCheckedByTwoEnemies = false;
    }

    void attemptToRemoveCheckForBlack(){
        if(!kingIsCheckedByTwoEnemies){
            HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> blackToAttacker
                    = storePossibleAttackForBlack(checkingPiece);

            checkIfBlackKingCanEscape();
            clearBlackLegalMoves();

            //Check what kind of piece has a check on the king and
            //Check the direction where the king is being attacked from
            ArrayList<Pair<Integer, Integer>> pathToKing = null;
            if(isQueen(checkingPiece)){
                pathToKing = attemptToAvoidWhiteQueen();
            }else if(isBishop(checkingPiece)){
                pathToKing = attemptToAvoidWhiteBishop();
            }else if(isRook(checkingPiece)){
                pathToKing = attemptToAvoidWhiteRook();
            }

            //Now that we have the path that attacks the king, we need to update our moves in a way to block
            //this path, by going through all the white chess pieces and only keeping the moves that help
            //block this attack.

            attemptToBlockPathToBlackKing(pathToKing);
            addPossibleAttacksToBlackPieces(blackToAttacker);
        }

        //We removed all possible moves that cannot block the King of being checked
        //we also removed the king's moves that intersect with other opposite piece's moves
        //If the moves are empty simply means a checkmate
        if(allBlackLegalMoves.isEmpty()){
            notifyObservers();
        }
        kingIsCheckedByTwoEnemies = false;
    }

    ArrayList<Pair<Integer, Integer>> attemptToAvoidWhiteQueen(){
        boolean kingFound = false;
        ArrayList<Pair<Integer, Integer>> pathToKing = new ArrayList<>();
        Queen queen = (Queen)(checkingPiece);
        int[] queenVerticalMoves = queen.getMoves().getVerticalMoves();
        int[] queenHorizontalMoves = queen.getMoves().getHorizontalMoves();
        for(int i = 0; i < 8; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = queen.getCurrentHeight() + queenVerticalMoves[j];
                int horizontalDisplacement = queen.getCurrentWidth() + queenHorizontalMoves[j];
                if(queen.notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(queen.isEmptySpace(verticalDisplacement, horizontalDisplacement)){
                    pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
                }

                if(grid[verticalDisplacement][horizontalDisplacement] == blackKing){
                    kingFound = true;
                    break;
                }
            }

            if(kingFound)
                break;

            pathToKing.clear();
        }

        return pathToKing;
    }

    HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> storePossibleAttackForWhite(ChessPiece lastMovedPiece){
        HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> whiteToAttacker =
                new HashMap<>();
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                if(grid[i][j] != null){
                    Pair<Integer, Integer> attackerLocation = lastMovedPiece.getCurrentLocation();
                    if(grid[i][j].getColor() == ChessPiece.Color.WHITE &&
                            grid[i][j].getLegalMoves().contains(attackerLocation)){

                        if(grid[i][j] instanceof King)
                            if(allBlackPotentialLegalMoves.contains(attackerLocation))
                                continue;

                        whiteToAttacker.put(grid[i][j].getCurrentLocation(), attackerLocation);
                    }
                }
            }
        }

        return whiteToAttacker;
    }

    HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> storePossibleAttackForBlack(ChessPiece lastMovedPiece){
        HashMap<Pair<Integer, Integer>, Pair<Integer, Integer>> blackToAttacker =
                new HashMap<>();
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                if(grid[i][j] != null){
                    Pair<Integer, Integer> attackerLocation = lastMovedPiece.getCurrentLocation();
                    if(grid[i][j].getColor() == ChessPiece.Color.BLACK &&
                            grid[i][j].getLegalMoves().contains(attackerLocation)){

                        if(grid[i][j] instanceof King)
                            if(allWhitePotentialLegalMoves.contains(attackerLocation))
                                continue;

                        blackToAttacker.put(grid[i][j].getCurrentLocation(), attackerLocation);
                    }
                }
            }
        }

        return blackToAttacker;
    }

    void removeKingIntersectionsWithEnemy(){
        //make sure the new king moves do not intersect with opposite team moves
        for(Pair<Integer, Integer> move :  allBlackLegalMoves){
            whiteKing.getLegalMoves().remove(move);
        }

        for(Pair<Integer, Integer> move : allWhiteLegalMoves){
            blackKing.getLegalMoves().remove(move);
        }
    }

    void attemptToBlockAttackForWhiteKing(ArrayList<Pair<Integer, Integer>> pathToKing){
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                if(grid[i][j] != null && !(grid[i][j] instanceof King))
                    grid[i][j].attemptBlockAttackOnKing(ChessPiece.Color.WHITE, pathToKing);
            }
        }
    }

    void attemptToBlockPathToBlackKing(ArrayList<Pair<Integer, Integer>> pathToKing){
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                if(grid[i][j] != null && !(grid[i][j] instanceof King))
                    grid[i][j].attemptBlockAttackOnKing(ChessPiece.Color.BLACK, pathToKing);
            }
        }
    }

    void addPossibleAttacksToWhitePieces(Map<Pair<Integer, Integer>, Pair<Integer, Integer>> whiteToAttacker){
        for(HashMap.Entry<Pair<Integer, Integer>, Pair<Integer, Integer>> entry : whiteToAttacker.entrySet()){
            int width = entry.getKey().first;
            int height = entry.getKey().second;
            allWhiteLegalMoves.add(entry.getValue());
            grid[height][width].addToLegalMoves(entry.getValue().second, entry.getValue().first);
        }
    }

    void addPossibleAttacksToBlackPieces(Map<Pair<Integer, Integer>, Pair<Integer, Integer>> blackToAttacker){
        for(HashMap.Entry<Pair<Integer, Integer>, Pair<Integer, Integer>> entry : blackToAttacker.entrySet()){
            int width = entry.getKey().first;
            int height = entry.getKey().second;
            allBlackLegalMoves.add(entry.getValue());
            grid[height][width].addToLegalMoves(entry.getValue().second, entry.getValue().first);
        }
    }


    ArrayList<Pair<Integer, Integer>> attemptToAvoidWhiteBishop(){
        boolean kingFound = false;
        ArrayList<Pair<Integer, Integer>> pathToKing = new ArrayList<>();
        Bishop bishop = (Bishop)(checkingPiece);
        int[] bishopVerticalMoves = bishop.getMoves().getVerticalMoves();
        int[] bishopHorizontalMoves = bishop.getMoves().getHorizontalMoves();
        for(int i = 0; i < 4; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = bishop.getCurrentHeight() + bishopVerticalMoves[j];
                int horizontalDisplacement = bishop.getCurrentWidth() + bishopHorizontalMoves[j];
                if(bishop.notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(bishop.isEmptySpace(verticalDisplacement, horizontalDisplacement)){
                    pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
                }

                if(grid[verticalDisplacement][horizontalDisplacement] == blackKing){
                    kingFound = true;
                    break;
                }

                pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
            }

            if(kingFound)
                break;

            pathToKing.clear();
        }

        return pathToKing;
    }

    ArrayList<Pair<Integer, Integer>> attemptToAvoidWhiteRook(){
        boolean kingFound = false;
        ArrayList<Pair<Integer, Integer>> pathToKing = new ArrayList<>();
        Rook rook = (Rook)(checkingPiece);
        int[] rookVerticalMoves = rook.getMoves().getVerticalMoves();
        int[] rookHorizontalMoves = rook.getMoves().getHorizontalMoves();
        for(int i = 0; i < 4; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = rook.getCurrentHeight() + rookVerticalMoves[j];
                int horizontalDisplacement = rook.getCurrentWidth() + rookHorizontalMoves[j];
                if(rook.notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(rook.isEmptySpace(verticalDisplacement, horizontalDisplacement)){
                    pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
                }

                if(grid[verticalDisplacement][horizontalDisplacement] == blackKing){
                    kingFound = true;
                    break;
                }

                pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
            }

            if(kingFound)
                break;

            pathToKing.clear();
        }

        return pathToKing;
    }

    ArrayList<Pair<Integer, Integer>> attemptToAvoidBlackQueen(){
        boolean kingFound = false;
        ArrayList<Pair<Integer, Integer>> pathToKing = new ArrayList<>();
        Queen queen = (Queen)(checkingPiece);
        int[] queenVerticalMoves = queen.getMoves().getVerticalMoves();
        int[] queenHorizontalMoves = queen.getMoves().getHorizontalMoves();
        for(int i = 0; i < 8; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = checkingPiece.getCurrentHeight() + queenVerticalMoves[j];
                int horizontalDisplacement = checkingPiece.getCurrentWidth() + queenHorizontalMoves[j];
                if(queen.notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(queen.isEmptySpace(verticalDisplacement, horizontalDisplacement)){
                    pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
                }

                if(grid[verticalDisplacement][horizontalDisplacement] == whiteKing){
                    kingFound = true;
                    break;
                }

                pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
            }

            if(kingFound)
                break;

            pathToKing.clear();
        }

        return pathToKing;
    }

    ArrayList<Pair<Integer, Integer>> attemptToAvoidBlackBishop(){
        boolean kingFound = false;
        ArrayList<Pair<Integer, Integer>> pathToKing = new ArrayList<>();
        Bishop bishop = (Bishop)(checkingPiece);
        int[] bishopVerticalMoves = bishop.getMoves().getVerticalMoves();
        int[] bishopHorizontalMoves = bishop.getMoves().getHorizontalMoves();
        for(int i = 0; i < 4; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = bishop.getCurrentHeight() + bishopVerticalMoves[j];
                int horizontalDisplacement = bishop.getCurrentWidth() + bishopHorizontalMoves[j];
                if(bishop.notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(bishop.isEmptySpace(verticalDisplacement, horizontalDisplacement)){
                    pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
                }

                if(grid[verticalDisplacement][horizontalDisplacement] == whiteKing){
                    kingFound = true;
                    break;
                }

                pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
            }

            if(kingFound)
                break;

            pathToKing.clear();
        }

        return pathToKing;
    }

    ArrayList<Pair<Integer, Integer>> attemptToAvoidBlackRook(){
        boolean kingFound = false;
        ArrayList<Pair<Integer, Integer>> pathToKing = new ArrayList<>();
        Rook rook = (Rook)(checkingPiece);
        int[] rookVerticalMoves = rook.getMoves().getVerticalMoves();
        int[] rookHorizontalMoves = rook.getMoves().getHorizontalMoves();
        for(int i = 0; i < 4; ++i){
            for(int j = i * 7; j < (i + 1) * 7; ++j){
                int verticalDisplacement = rook.getCurrentHeight() + rookVerticalMoves[j];
                int horizontalDisplacement = rook.getCurrentWidth() + rookHorizontalMoves[j];
                if(rook.notInBound(verticalDisplacement, horizontalDisplacement))
                    break;
                if(rook.isEmptySpace(verticalDisplacement, horizontalDisplacement)){
                    pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
                }

                if(grid[verticalDisplacement][horizontalDisplacement] == whiteKing){
                    kingFound = true;
                    break;
                }

                pathToKing.add(new Pair<Integer, Integer>(horizontalDisplacement, verticalDisplacement));
            }

            if(kingFound)
                break;

            pathToKing.clear();
        }

        return pathToKing;
    }

    void arrangePawns(){
        for(int i = 0; i < width; ++i){
            grid[1][i] = new Pawn(ChessPiece.Color.BLACK, new PawnMoves(), i, 1, this);
            grid[6][i] = new Pawn(ChessPiece.Color.WHITE, new PawnMoves(), i, 6, this);
        }
    }

    void arrangeRooks(){
        grid[0][0] = new Rook(ChessPiece.Color.BLACK, new RookMoves(), 0, 0, this);
        grid[0][7] = new Rook(ChessPiece.Color.BLACK, new RookMoves(), 7, 0, this);
        grid[7][0] = new Rook(ChessPiece.Color.WHITE, new RookMoves(), 0, 7, this);
        grid[7][7] = new Rook(ChessPiece.Color.WHITE, new RookMoves(), 7, 7, this);
    }

    void arrangeKnights(){
        grid[0][1] = new Knight(ChessPiece.Color.BLACK, new KnightMoves(), 1, 0, this);
        grid[0][6] = new Knight(ChessPiece.Color.BLACK, new KnightMoves(), 6, 0, this);
        grid[7][1] = new Knight(ChessPiece.Color.WHITE, new KnightMoves(), 1, 7, this);
        grid[7][6] = new Knight(ChessPiece.Color.WHITE, new KnightMoves(), 6, 7, this);
    }

    void arrangeBishops(){
        grid[0][2] = new Bishop(ChessPiece.Color.BLACK, new BishopMoves(), 2, 0, this);
        grid[0][5] = new Bishop(ChessPiece.Color.BLACK, new BishopMoves(), 5, 0, this);
        grid[7][2] = new Bishop(ChessPiece.Color.WHITE, new BishopMoves(), 2, 7, this);
        grid[7][5] = new Bishop(ChessPiece.Color.WHITE, new BishopMoves(), 5, 7, this);
    }

    void arrangeQueens(){
        grid[0][3] = new Queen(ChessPiece.Color.BLACK, new QueenMoves(), 3, 0, this);
        grid[7][3] = new Queen(ChessPiece.Color.WHITE, new QueenMoves(), 3, 7, this);
    }

    void arrangeKings(){
        grid[0][4] = new King(ChessPiece.Color.BLACK, new KingMoves(), 4, 0, this);
        grid[7][4] = new King(ChessPiece.Color.WHITE, new KingMoves(), 4, 7, this);
    }
}

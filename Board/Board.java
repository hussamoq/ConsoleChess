package Board;
import ChessPiece.ChessPiece;
import Observer.*;
import java.util.ArrayList;

public abstract class Board {
    protected int width;
    protected int height;
    protected ArrayList<Observer> observerList = new ArrayList<>();

    //Every chess game has a 2D board of chessPieces
    ChessPiece[][] grid;

    public ChessPiece[][] getGrid(){
        return grid;
    }
    public int getHeight() { return height; }
    public int getWidth() { return width; }
    public void registerObserver(Observer observer) { observerList.add(observer); }
    public void unregisterObserver(Observer observer) { observerList.remove(observer); }
    public abstract void updateMoves();
    public abstract void printGrid();

    void notifyObservers(){
        for(Observer observer : observerList){
            observer.update();
        }
    }
}

package Utility;

public class Pair<Type1, Type2>{
    public Type1 first;
    public Type2 second;

    public Pair(Type1 first, Type2 second){
        try{
            this.first = first;
            this.second = second;
        }catch(Exception e){
               System.out.println("Error initializing pair class");
        }
    }

    @Override
    public boolean equals(Object object){
        if(object == this)
            return true;
        if(!(object instanceof Pair))
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) object;

        return pair.first == this.first && pair.second == this.second;
    }

    @Override
    public int hashCode(){
        int hash = 5;
        return (int)this.first * (int)this.second * hash;
    }
}

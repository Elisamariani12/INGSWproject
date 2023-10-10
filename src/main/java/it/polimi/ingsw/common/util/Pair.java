package it.polimi.ingsw.common.util;

/**
 * The mutable type Tuple.
 *
 * @param <U> the type parameter
 * @param <V> the type parameter
 */
public class Pair<U, V> {
    private U first;
    private V second;

    /**
     * Instantiates a new Pair.
     *
     * @param first  the first
     * @param second the second
     */
    public Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Pair tuple = (Pair) o;

        return first.equals(tuple.first) &&
                second.equals(tuple.second);
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Gets first element of the tuple.
     *
     * @return the first element of the tuple
     */
    public U getFirst() {
        return first;
    }

    /**
     * Sets first element of the tuple.
     *
     * @param first the first element of the tuple
     */
    public void setFirst(U first) {
        this.first = first;
    }

    /**
     * Gets second element of the tuple.
     *
     * @return the second element of the tuple
     */
    public V getSecond() {
        return second;
    }

    /**
     * Sets second element of the tuple.
     *
     * @param second the second element of the tuple
     */
    public void setSecond(V second) {
        this.second = second;
    }

    /**
     * Copy pair.
     *
     * @return the pair
     */
    public Pair<U, V> copy(){
        return new Pair<>(this.first, this.second);
    }

}

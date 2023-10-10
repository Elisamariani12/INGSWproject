package it.polimi.ingsw.common.util;

/**
 * The mutable type Triplet.
 *
 * @param <U> the type parameter
 * @param <V> the type parameter
 * @param <T> the type parameter
 */
public class Triplet<U, V, T> {

    private U first;
    private V second;
    private T third;

    /**
     * Instantiates a new Triplet.
     *
     * @param first  the first
     * @param second the second
     * @param third  the third
     */
    public Triplet(U first, V second, T third)
    {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Triplet<U,V,T> triplet = (Triplet<U, V, T>) o;

        return first.equals(triplet.first) &&
                second.equals(triplet.second) &&
                third.equals(triplet.third);
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ", " + third + ")";
    }

    /**
     * Gets first element of the triplet.
     *
     * @return the first element of the triplet
     */
    public U getFirst() {
        return first;
    }

    /**
     * Sets first element of the triplet.
     *
     * @param first the first element of the triplet
     */
    public void setFirst(U first) {
        this.first = first;
    }

    /**
     * Gets second element of the triplet.
     *
     * @return the second element of the triplet
     */
    public V getSecond() {
        return second;
    }

    /**
     * Sets second element of the triplet.
     *
     * @param second the second element of the triplet
     */
    public void setSecond(V second) {
        this.second = second;
    }

    /**
     * Gets third element of the triplet.
     *
     * @return the third element of the triplet
     */
    public T getThird() {
        return third;
    }

    /**
     * Sets third element of the triplet.
     *
     * @param third the third element of the triplet
     */
    public void setThird(T third) {
        this.third = third;
    }

    /**
     * Copy triplet.
     *
     * @return the triplet
     */
    public Triplet<U, V, T> copy(){
        return new Triplet<>(this.first, this.second, this.third);
    }

    /**
     * Remove the second element of a triplet, creating a pair
     * @return  the pair obtained removing the second element
     */
    public Pair<U,T> eliminate2element(){
        Pair<U,T> p=new Pair<>(this.first,this.third);
        return p;
    }
}

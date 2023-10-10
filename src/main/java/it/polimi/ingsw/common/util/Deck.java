package it.polimi.ingsw.common.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import java.util.stream.Stream;

/** Represents a Deck of cards, action tokens or any other data type specified */
public class Deck<E> implements Iterable<E>
{
    //Internal representation
    private Stack<E> deckRep;

    /**
     * Instantiates an empty deck
     */
    public Deck()
    {
        deckRep = new Stack<E>();
    }

    /**
     * Inserts an element on top of the deck
     * @param el The element to be added to the deck
     * */
    public void addElement(E el)
    {
        deckRep.push(el);
    }

    /**
     * Removes the topmost element in the deck and returns it
     * @return Topmost element in the deck
     * */
    public E removeElement()
    {
        return deckRep.isEmpty() ? null : deckRep.pop();
    }

    /**
     * Returns the topmost element from the deck without removing it
     * @return Topmost element in the deck
     * */
    public E peekElement()
    {
        return deckRep.isEmpty() ? null : deckRep.peek();
    }

    /**
     * Randomly rearranges elements of the deck using PRNG
     */
    public void shuffle()
    {
        Collections.shuffle(deckRep);
    }

    /**
     * Returns the nth element from the deck without removing it
     * @param index Index of the element to be returned
     * @return Requested element
     * */
    public E getElement(int index)
    {
        return deckRep.get(index);
    }

    /**
     * Returns the size of the deck
     * @return Number of elements in the deck
     */
    public int getSize()
    {
        return deckRep.size();
    }

    /**
     * Returns a copy of the deck (elements are not reinstantiated)
     * @return New deck object with the same elements
     */
    public Deck<E> copy()
    {
        Deck<E> buffer = new Deck<E>();
        for(E el : this)
        {
            buffer.addElement(el);
        }
        return buffer;
    }

    /**
     * Standard iterator pattern
     * @return Iterator for the Deck object
    */
    @Override
    public Iterator<E> iterator() {
        Iterator<E> it = new Iterator<E>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < getSize();
            }

            @Override
            public E next() {
                return getElement(i++);
            }

        };
        return it;
    }

    /**
     * Returns whether or not an element is contained in the deck
     * @param element The element that needs to be searched
     * @return Is the element present?
     */
    public boolean contains(E element)
    {
        return deckRep.contains(element);
    }

    /**
     * Returns a sequential Stream with this collection as its source. (Directly returned from the rep, a stack)
     * @return a sequential Stream over the elements in this collection
     */
    public Stream<E> stream()
    {
        return deckRep.stream();
    }

    /**
     * Picks an element and reinserts it at the end of the deck
     * @return Topmost element
     */
    public E peekAndReinsert()
    {
        E element = removeElement();
        deckRep.add(0, element);
        return element;
    }

    /**
     * Returns a deck state as a Stack to be easily serialized
     * @return Equivalent stack of elements
     */
    public Stack<E> getSerializableState()
    {
        return (Stack<E>)deckRep.clone();
    }
}

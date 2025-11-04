//COLLABORATOR - GPT5


package linkedlist;

import java.util.*;

/**
 * A minimal, singly-linked queue that also exposes a couple of list-y helpers (get/remove by index).
 * Implements just enough of Queue<T> for this assignment.
 *
 * Core operations are O(1): offer/add at tail, poll/remove at head, peek at head.
 * Random access (get/remove by index) is O(n) via traversal from head.
 */
public class CustomLinkedList<T> implements Queue<T> {

    // --- Node chain state ---
    private Node<T> head;   // first node (front of queue)
    private Node<T> tail;   // last node (back of queue)
    private int size;       // cached size for O(1) size()

    public CustomLinkedList() { }

    
    
    /* =========================
       Basic queue operations
       ========================= */

    /** Enqueue at tail. Returns true (like java.util.LinkedList). */
    
    @Override
    public boolean add(T val) {
        return offer(val);
    }

    /** Enqueue at tail; returns true. */
    
    @Override
    public boolean offer(T val) {
        Node<T> n = new Node<>(val);
        if (tail == null) {          // empty list: head==null, tail==null
            head = tail = n;
        } else {
            tail.next = n;           // link old tail -> new node
            tail = n;                // move tail forward
        }
        size++;
        return true;
    }

    /** Front-of-queue without removing; null if empty. */
    @Override
    public T peek() {
        return (head == null) ? null : head.val;
    }

    /** Remove & return front; null if empty. */
    @Override
    public T poll() {
        if (head == null) return null;
        T v = head.val;
        head = head.next;            // advance head
        if (head == null) tail = null; // became empty: clear tail too
        size--;
        return v;
    }

    
    
    
    /** Like poll() but throws if empty (Queue contract). */
    @Override
    public T remove() {
        T v = poll();
        if (v == null) throw new NoSuchElementException("Queue is empty");
        return v;
    }
    
    

    /** Like peek() but throws if empty (Queue contract). */
    @Override
    public T element() {
        T v = peek();
        if (v == null) throw new NoSuchElementException("Queue is empty");
        return v;
    }

    
    
    /* =========================
       Helpers for the exercise
       ========================= */

    /** O(n) random access: walk from head to the index. */
    public T get(int index) {
        checkIndex(index);
        Node<T> cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur.val;
    }

    
    /** O(n) removal by index. Returns the removed value. */
    public T remove(int index) {
        checkIndex(index);
        if (index == 0) return remove(); // reuse queue remove (throws if empty)
        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) prev = prev.next;
        Node<T> target = prev.next;
        prev.next = target.next;
        if (target == tail) tail = prev; // if we removed tail, fix tail
        size--;
        return target.val;
    }

    private void checkIndex(int idx) {
        if (idx < 0 || idx >= size) throw new IndexOutOfBoundsException("index=" + idx + ", size=" + size);
    }

    
    
    
    
    /* =========================
       Minimal Collection bits
       ========================= */

    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }

    @Override
    public void clear() {
        // help GC by breaking links
        Node<T> cur = head;
        while (cur != null) {
            Node<T> nxt = cur.next;
            cur.next = null;
            cur = nxt;
        }
        head = tail = null;
        size = 0;
    }

    @Override
    public boolean contains(Object v) {
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            if (Objects.equals(cur.val, v)) return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object obj) {
        if (head == null) return false;
        if (Objects.equals(head.val, obj)) {
            remove();
            return true;
        }
        Node<T> prev = head, cur = head.next;
        while (cur != null) {
            if (Objects.equals(cur.val, obj)) {
                prev.next = cur.next;
                if (cur == tail) tail = prev;
                size--;
                return true;
            }
            prev = cur; cur = cur.next;
        }
        return false;
    }

    
    
    
    
    
    // --- Ignored / simple implementations for interface completeness ---

    @Override public boolean addAll(Collection<? extends T> c) { boolean changed = false; for (T t: c){ offer(t); changed = true; } return changed; }
    @Override public boolean containsAll(Collection<?> c) { for (Object o: c) if (!contains(o)) return false; return true; }
    @Override public boolean removeAll(Collection<?> c) { boolean changed = false; for (Object o: c) changed |= remove(o); return changed; }
    @Override public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        Node<T> prev = null, cur = head;
        while (cur != null) {
            if (!c.contains(cur.val)) {
                changed = true;
                if (cur == head) { head = head.next; cur.next = null; cur = head; if (head == null) tail = null; size--; prev = null; continue; }
                else { prev.next = cur.next; if (cur == tail) tail = prev; size--; cur = prev.next; continue; }
            }
            prev = cur; cur = cur.next;
        }
        return changed;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> cur = head;
            public boolean hasNext() { return cur != null; }
            public T next() {
                if (cur == null) throw new NoSuchElementException();
                T v = cur.val; cur = cur.next; return v;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] a = new Object[size];
        int i = 0;
        for (Node<T> cur = head; cur != null; cur = cur.next) a[i++] = cur.val;
        return a;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E[] toArray(E[] a) {
        if (a.length < size) a = (E[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        for (Node<T> cur = head; cur != null; cur = cur.next) a[i++] = (E) cur.val;
        if (a.length > size) a[size] = null;
        return a;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /* =========================
       Micro-benchmark in main
       ========================= */

    public static void main(String[] args) {
        final int COUNT = 100_000;             
        final long seed = 42L;                 // deterministic data
        int[] data = new int[COUNT];
        Random rnd = new Random(seed);
        for (int i = 0; i < COUNT; i++) data[i] = rnd.nextInt();

        // --- Time java.util.LinkedList add ---
        LinkedList<Integer> jdk = new LinkedList<>();
        long t0 = System.nanoTime();
        for (int x : data) jdk.add(x);
        long t1 = System.nanoTime();

        // --- Time CustomLinkedList offer/add ---
        CustomLinkedList<Integer> mine = new CustomLinkedList<>();
        long t2 = System.nanoTime();
        for (int x : data) mine.add(x);        // same semantics as offer()
        long t3 = System.nanoTime();

        System.out.printf("time for library LL : %.3f ms%n", (t1 - t0) / 1_000_000.0);
        System.out.printf("time for custom  LL : %.3f ms%n", (t3 - t2) / 1_000_000.0);

    }
}


// FINALRESULTS AND INFERENCE --> JDK LinkedList is highly optimized, but a lean singly-linked
// implementation is VERYYY competitive for pure tail-appends --> and hence sometimes it also beats the library.



package effective;

import java.util.*;

/**
 * Created by wenjusun on 8/11/2015.
 */
public class JavaEffective {

    public void basic_WeakHashMap() {
        WeakHashMap weakHashMap = new WeakHashMap();


        Map myMap = new HashMap<String, String>();
        Map<String, String> myMap2 = new HashMap<String, String>();
        for (String k : myMap2.keySet()) {

        }

        Map<?, ?> myMap3 = new HashMap<Object, Object>();

        List mylist = new ArrayList();

    }

    interface Function<T> {
        T apply(T arg1, T arg2);
    }

/*
    static <E> E reduce(List<E> list, Function<E> f,E initVal){
        E[] snapshots = list.toArray(); //This will report compliation error

        E result = initVal;
        for (E e : snapshots){
            result = f.apply(result,e);
        }

        return result;
    }
*/


    public void autobox() {

        Comparator<Integer> naturalComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 < o2 ? -1 : (o1 == o2 ? 0 : 1);
            }
        };

        System.out.println(naturalComparator.compare(42, 42));
        System.out.println(naturalComparator.compare(Integer.valueOf(42), Integer.valueOf(42)));
        System.out.println(naturalComparator.compare(new Integer(42), new Integer(42)));

    }

    public static void main(String args[]) {
        JavaEffective je = new JavaEffective();
        je.autobox();
    }

}
